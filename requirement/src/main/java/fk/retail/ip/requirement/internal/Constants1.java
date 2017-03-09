package fk.retail.ip.requirement.internal;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by agarwal.vaibhav on 08/03/17.
 */
public class Constants1 {
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

    public static String NO_REQUIREMENT_FOUND = "NO_REQUIREMENT_FOUND";

    public static String QUANTITY_OVERRIDE_COMMENT = "QUANTITY_OVERRIDE_COMMENT";
    public static String APP_OVERRIDE_COMMENT = "APP_OVERRIDE_COMMENT";
    public static String SUPPLIER_OVERRIDE_COMMENT = "SUPPLIER_OVERRIDE_COMMENT";
    public static String STATUS = "STATUS";
    public static String PROPERTIES_FILE_PATH = "/message.properties";

    private static Properties properties;

    static {
        try {
            properties = new Properties();
            properties.load(Constants.class.getResourceAsStream(Constants1.PROPERTIES_FILE_PATH));
        } catch (IOException ioe) {
            System.out.println("Unable to find file");
        }
    }

    private Constants1() {}

    public static String getKey(String key) {
        return properties.getProperty(key);
    }
}
