package fk.retail.ip.manager;

import com.codahale.metrics.JmxReporter;
import com.fasterxml.jackson.datatype.jdk7.Jdk7Module;
import com.google.common.collect.Sets;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.hubspot.dropwizard.guice.GuiceBundle;
import fk.retail.ip.email.internal.module.EmailModule;
import fk.retail.ip.manager.config.ManagerConfiguration;
import fk.retail.ip.manager.config.ManagerModule;
import fk.retail.ip.requirement.config.RequirementModule;
import fk.retail.ip.segmentation.config.SegmentationModule;
import fk.retail.ip.segmentation.job.GroupSegmentationAlertJob;
import fk.retail.ip.segmentation.job.GroupSegmentationJob;
import fk.retail.ip.ssl.config.SslClientModule;
import fk.retail.ip.zulu.config.ZuluModule;
import fk.retail.ip.fdp.config.FdpModule;
import fk.retail.ip.d42.config.D42ClientModule;
import fk.sp.common.extensions.RequestContextFilter;
import fk.sp.common.extensions.config.CustomEnumModule;
import fk.sp.common.extensions.dropwizard.jersey.JerseyClientModule;
import fk.sp.common.extensions.dropwizard.jersey.LoggingFilter;
import fk.sp.common.extensions.guice.jpa.spring.JpaWithSpringModule;
import fk.sp.common.restbus.sender.config.RestbusSenderModule;
import flipkart.retail.server.admin.bundle.RotationManagementBundle;
import flipkart.retail.server.admin.config.RotationManagementConfig;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.forms.MultiPartBundle;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import java.util.EnumSet;
import java.util.Properties;
import java.util.TimeZone;
import javax.servlet.DispatcherType;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

public class ManagerApplication extends Application<ManagerConfiguration> {

    private GuiceBundle<ManagerConfiguration> guiceBundle;

    @Override
    public void initialize(Bootstrap<ManagerConfiguration> bootstrap) {
        super.initialize(bootstrap);
        Properties jpaProperties = new Properties();
        jpaProperties.put(JpaWithSpringModule.HIBERNATE_EJB_NAMING_STRATEGY,
                "fk.retail.ip.manager.config.AnnotationRespectfulNamingStrategy");
        jpaProperties.put("hibernate.jdbc.batch_size",150);
        jpaProperties.put("hibernate.order_inserts", "true");
        jpaProperties.put("hibernate.order_updates", "true");
        jpaProperties.put("hibernate.jdbc.batch_versioned_data", "true");
        this.guiceBundle = GuiceBundle.<ManagerConfiguration>newBuilder()
                .setConfigClass(ManagerConfiguration.class)
                .addModule(new ManagerModule())
                .addModule(new JerseyClientModule())
                .addModule(new RequirementModule())
                .addModule(new ZuluModule())
                .addModule(new SslClientModule())
                .addModule(new FdpModule())
                .addModule(new RestbusSenderModule())
                .addModule(new D42ClientModule())
                .addModule(new EmailModule())
                .addModule(new SegmentationModule())
                .addModule(new JpaWithSpringModule(
                        Sets.newHashSet(
                                "fk.retail.ip",
                                "com.restbus.client.plugin.hibernate.entity"
                        ), jpaProperties))
                .enableAutoConfig(
                        "fk.sp.common.extensions.exception",
                        "fk.sp.common.extensions.dropwizard.hystrix",
                        "fk.sp.common.extensions.dropwizard.logging",
                        "fk.sp.common.extensions.dropwizard.jmx",
                        "fk.sp.common.extensions.dropwizard.jpa",
                        "fk.sp.common.extensions.filter",
                        "fk.retail.ip"
                )
                .build(Stage.DEVELOPMENT);
        bootstrap.addBundle(guiceBundle);
        bootstrap.addBundle(new MultiPartBundle());

        //For oor and bir task and healthcheck integration
        bootstrap.addBundle(new RotationManagementBundle<ManagerConfiguration>() {
            @Override
            public RotationManagementConfig getRotationManagementConfig(
                    ManagerConfiguration managerConfiguration) {
                return managerConfiguration.getRotationManagementConfig();
            }
        });

        //For dbMigrations support
        bootstrap.addBundle(new MigrationsBundle<ManagerConfiguration>() {
            public DataSourceFactory getDataSourceFactory(
                    ManagerConfiguration configuration) {
                return configuration.getDatabaseConfiguration();
            }
        });

    }

    @Override
    public void run(ManagerConfiguration configuration, Environment environment) throws Exception {

        //For swagger UI
        AssetsBundle assetsBundle = new AssetsBundle("/apidocs", "/apidocs", "index.html", "/apidocs");
        assetsBundle.run(environment);
        environment.jersey().register(LoggingFilter.class);

        environment.getObjectMapper().registerModule(new Jdk7Module());
        environment.getObjectMapper().setTimeZone(TimeZone.getTimeZone("UTC"));
        environment.getObjectMapper().registerModule(new CustomEnumModule());

        environment.servlets().addFilter("request context filter",
                new RequestContextFilter())
                .addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");

        JmxReporter reporter = JmxReporter.forRegistry(environment.metrics()).build();
        reporter.start();

        scheduleJobs();
    }

    public static void main(String[] args) throws Exception {
        ManagerApplication managerApplication = new ManagerApplication();
        managerApplication.run(args);

    }

    private void scheduleJobs() throws SchedulerException {
        Injector injector = this.guiceBundle.getInjector();
        SchedulerFactory schedFact = new StdSchedulerFactory();
        Scheduler sched = schedFact.getScheduler();
        sched.setJobFactory(injector.getInstance(GuiceJobFactory.class));
        sched.start();

        JobDetail segmentationJob = newJob(GroupSegmentationJob.class).withIdentity("groupSegmentationJob","segmentationService").build();
        Trigger segmentationTrigger = newTrigger().withIdentity("groupSegmentationTrigger","segmentationService").startNow().withSchedule(cronSchedule("0 33 12 * * ?")).build();

        JobDetail segmentationAlertJob = newJob(GroupSegmentationAlertJob.class).withIdentity("groupSegmentationAlertJob","segmentationService").build();
        Trigger segmentationAlertTrigger = newTrigger().withIdentity("groupSegmentationAlertTrigger","segmentationService").startNow().withSchedule(cronSchedule("0 57 11 * * ?")).build();


       // sched.scheduleJob(segmentationJob, segmentationTrigger);
       // sched.scheduleJob(segmentationAlertJob, segmentationAlertTrigger);
    }
}
