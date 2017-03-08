package fk.retail.ip.requirement.internal;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.Properties;
import com.google.common.collect.Sets;
import java.util.Set;

/**
 * Created by agarwal.vaibhav on 03/03/17.
 */
public class Constants {

    public static final double DEFAULT_FORECAST = 0.0;
    public static final int DAYS_IN_WEEK = 7;
    public static final int WEEKS_OF_FORECAST = 15;
    public static final Set<String> INTRANSIT_REQUEST_STATUSES = Sets.newHashSet("in-process", "dispatched", "requested");
    public static final String POLICY_DISPLAY_FORMAT = "{\"%s\":%.2f}";

    //Error messages
    public static final String INVALID_POLICY_TYPE = "Did not find matching policy type for: {}";
    public static final String GROUP_NOT_FOUND = "Group for this fsn is not present";
    public static final String FORECAST_NOT_FOUND = "Forecast for this fsn is not present";
    public static final String VALID_POLICY_NOT_FOUND = "Valid %s policy for this fsn is not present";
    public static final String UNABLE_TO_PARSE = "Unable to parse: {}";
    public static final String NOT_APPLICABLE = "N/A";

    public static final String ERROR_STATE = "error";

    public static String FSN_OR_WAREHOUSE_IS_MISSING = "FSN_OR_WAREHOUSE_IS_MISSING";
    public static String QUANTITY_OVERRIDE_COMMENT_IS_MISSING = "QUANTITY_OVERRIDE_COMMENT_IS_MISSING";
    public static String SUGGESTED_QUANTITY_IS_NOT_GREATER_THAN_ZERO =
            "SUGGESTED_QUANTITY_IS_NOT_GREATER_THAN_ZERO";
    public static String APP_OVERRIDE_COMMENT_IS_MISSING = "APP_OVERRIDE_COMMENT_IS_MISSING";
    public static String SUPPLIER_OVERRIDE_COMMENT_IS_MISSING = "SUPPLIER_OVERRIDE_COMMENT_IS_MISSING";
    public static String SUPPLIER_OVERRIDE_COMMENT_IS_MISSING_WHEN_UPDATED_FROM_BLANK =
            "SUPPLIER_OVERRIDE_COMMENT_IS_MISSING_WHEN_UPDATED_FROM_BLANK";
    public static String SLA_QUANTITY_IS_NOT_GREATER_THAN_ZERO = "SLA_QUANTITY_IS_NOT_GREATER_THAN_ZERO";
    public static String APP_QUANTITY_IS_NOT_GREATER_THAN_ZERO = "APP_QUANTITY_IS_NOT_GREATER_THAN_ZERO";
    public static String REQUIREMENT_NOT_FOUND_FOR_GIVEN_REQUIREMENT_ID =
            "REQUIREMENT_NOT_FOUND_FOR_GIVEN_REQUIREMENT_ID";
    public static String INVALID_QUANTITY_WITHOUT_COMMENT =
            "INVALID_QUANTITY_WITHOUT_COMMENT";
    public static String INVALID_APP_WITHOUT_COMMENT =
            "INVALID_APP_WITHOUT_COMMENT";

    public static String QUANTITY_OVERRIDE_COMMENT = "QUANTITY_OVERRIDE_COMMENT";
    public static String APP_OVERRIDE_COMMENT = "APP_OVERRIDE_COMMENT";
    public static String SUPPLIER_OVERRIDE_COMMENT = "SUPPLIER_OVERRIDE_COMMENT";
    public static String STATUS = "STATUS";
    public static String PROPERTIES_FILE_PATH = "/message.properties";

    private static Properties properties;

    static {
        try {
            properties = new Properties();
            properties.load(Constants.class.getResourceAsStream(Constants.PROPERTIES_FILE_PATH));
        } catch (IOException ioe) {
            System.out.println("Unable to find file");
        }
    }

    private Constants() {}

    public static String getKey(String key) {
        return properties.getProperty(key);
    }

}

