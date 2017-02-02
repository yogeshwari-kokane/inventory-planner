package fk.retail.ip.projection;

import com.google.inject.Inject;
import com.google.inject.Provider;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.collections4.map.MultiKeyMap;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import fk.retail.ip.core.poi.SpreadSheetWriter;
import fk.retail.ip.projection.internal.command.UploadProjectionCommand;
import fk.retail.ip.projection.internal.dao.FsnBandDAO;
import fk.retail.ip.projection.internal.dao.ProjectionItemDAO;
import fk.retail.ip.projection.internal.dao.WarehouseForecastDAO;
import fk.retail.ip.projection.internal.dao.WeeklySaleDAO;
import fk.retail.ip.projection.internal.entities.FsnBand;
import fk.retail.ip.projection.internal.entities.WarehouseForecast;
import fk.retail.ip.projection.internal.entities.WeeklySale;
import fk.retail.ip.projection.internal.exception.ProjectionOverrideException;
import fk.retail.ip.projection.models.ProjectionLineItem;
import io.dropwizard.hibernate.UnitOfWork;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

/**
 * Created by nidhigupta.m on 06/01/17.
 */

@Path("/projection")
public class ProjectionResource {

    private Provider<UploadProjectionCommand> overrideProjectionCommandProvider;
    private ProjectionItemDAO projectionItemDAO;
    private ObjectMapper mapper = new ObjectMapper();
    private InputStream template;
    private static final String TEMPLATE_NAME = "/test.xlsx";
    private final FsnBandDAO fsnBandDAO;
    private final WarehouseForecastDAO warehouseForecastDAO;
    private final WeeklySaleDAO weeklySaleDAO;


    @Inject
    public ProjectionResource(
            ProjectionItemDAO projectionItemDAO,
            FsnBandDAO fsnBandDAO,
            WarehouseForecastDAO warehouseForecastDAO,
            WeeklySaleDAO weeklySaleDAO,
            Provider<UploadProjectionCommand> overrideProjectionCommandProvider) {
        this.projectionItemDAO = projectionItemDAO;
        this.fsnBandDAO = fsnBandDAO;
        this.warehouseForecastDAO = warehouseForecastDAO;
        template = getClass().getResourceAsStream(TEMPLATE_NAME);
        this.weeklySaleDAO = weeklySaleDAO;
        this.overrideProjectionCommandProvider = overrideProjectionCommandProvider;
    }

    @GET
    @Path("/all")
    @Timed
    @UnitOfWork
    public Response download() {
        StreamingOutput stream = new StreamingOutput() {
            @Override
            public void write(OutputStream output) throws IOException, WebApplicationException {
                try {
                    SpreadSheetWriter spreadsheet = new SpreadSheetWriter();
                    // collect projections
                    List<ProjectionLineItem> projectionLineItems = projectionItemDAO.findAll().stream().map(ProjectionLineItem::new).collect(toList());
                    Map<String, List<ProjectionLineItem>> fsnToProjection = projectionLineItems.stream().collect(groupingBy(ProjectionLineItem::getFsn));
                    Set<String> fsns = Collections.unmodifiableSet(fsnToProjection.keySet());

                    // collect band info
                    List<FsnBand> bands = fsnBandDAO.find(fsns);
                    bands.stream().forEach(b -> {
                        List<ProjectionLineItem> items = fsnToProjection.get(b.getFsn());
                        items.forEach(i -> {
                            i.setPvBand(b.getPvBand());
                            i.setSalesBand(b.getSalesBand());
                        });
                    });

                    // collect forecast
                    List<WarehouseForecast> forecasts = warehouseForecastDAO.find(fsns);
                    MultiKeyMap<String, String> fsnWhForecastMap = new MultiKeyMap();
                    forecasts.forEach(f -> fsnWhForecastMap.put(f.getFsn(), f.getWarehouse(), f.getForecast()));
                    projectionLineItems.forEach(pi -> pi.setForecast(fsnWhForecastMap.get(pi.getFsn(), pi.getWarehouse())));

                    // collect weekly sales
                    List<WeeklySale> sales = weeklySaleDAO.find(fsns);

                    projectionLineItems.forEach(pi
                            -> populateSalesData(sales, pi, pi::setWeek0Sale, pi::setWeek1Sale, pi::setWeek2Sale, pi::setWeek3Sale, pi::setWeek4Sale, pi::setWeek5Sale, pi::setWeek6Sale, pi::setWeek7Sale)
                    );

                    spreadsheet.populateTemplate(template, output, (List<Map<String, Object>>) mapper.convertValue(projectionLineItems, new TypeReference<List<Map>>() {
                    }));
                } catch (Exception e) {
                    throw new WebApplicationException(e);
                }
            }
        };

        return Response.ok(stream)
                .header(HttpHeaders.CONTENT_TYPE, "application/octet-stream")
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename = projection.xlsx")
                .build();
    }

    private void populateSalesData(List<WeeklySale> sales, ProjectionLineItem pi, Consumer<Integer>... setters) {
        MultiKeyMap<String, Integer> fsnWhWeekSalesMap = new MultiKeyMap();
        sales.forEach(s -> fsnWhWeekSalesMap.put(s.getFsn(), s.getWarehouse(), String.valueOf(s.getWeek()), s.getSaleUnit()));
        LocalDate date = LocalDate.now();
        TemporalField weekOfYear = WeekFields.of(DayOfWeek.MONDAY, 1).weekOfWeekBasedYear();
        int currentWeek = date.get(weekOfYear); // 1-52

        for (Consumer<Integer> setter : setters) {
            setter.accept(fsnWhWeekSalesMap.get(pi.getFsn(), pi.getWarehouse(), String.valueOf(currentWeek)));
            currentWeek = (currentWeek - 2 + 52) % 52 + 1;
        }
    }

    @GET
    public Response sayHello() {
        String name = "Nidhi";
        return Response.ok(name).build();
    }


    @POST
    @Path("/upload")
    public Response uploadProjectionOverride(@FormDataParam("file")InputStream inputStream,
                                             @FormDataParam("file")FormDataContentDisposition fileDetails,
                                             Map<String, Object> params) throws IOException, InvalidFormatException, ProjectionOverrideException {

        overrideProjectionCommandProvider.get().uploadProjectionOverride(inputStream, params);
        return Response.ok().build();

    }


}
