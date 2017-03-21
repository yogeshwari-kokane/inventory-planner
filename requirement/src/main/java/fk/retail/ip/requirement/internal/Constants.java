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
public interface Constants {

    double DEFAULT_FORECAST = 0.0;
    int DAYS_IN_WEEK = 7;
    int WEEKS_OF_FORECAST = 15;
    double MIN_CASE_SIZE = 1D;
    String MAX_COVERAGE_KEY = "max_coverage";
    String CASE_SIZE_KEY = "case_size";
    Set<String> INTRANSIT_REQUEST_STATUSES = Sets.newHashSet("in-process", "dispatched", "requested");
    String POLICY_DISPLAY_FORMAT = "{\"%s\":%.2f}";

    //Error messages
    String INVALID_POLICY_TYPE = "Did not find matching policy type for: {}";
    String GROUP_NOT_FOUND = "Group for this fsn is not present";
    String FORECAST_NOT_FOUND = "Forecast for this fsn is not present";
    String VALID_POLICY_NOT_FOUND = "Valid %s policy for this fsn is not present";
    String UNABLE_TO_PARSE = "Unable to parse: {}";
    String NOT_APPLICABLE = "N/A";
    String ERROR_STATE = "error";

    String FSN_OR_WAREHOUSE_IS_MISSING = "FSN or Warehouse is missing";
    String QUANTITY_OVERRIDE_COMMENT_IS_MISSING = "Quantity override reason is missing";
    String SUGGESTED_QUANTITY_IS_NOT_GREATER_THAN_ZERO = "Suggested quantity is not greater than zero";
    String APP_OVERRIDE_COMMENT_IS_MISSING = "Price override reason is missing";
    String SUPPLIER_OVERRIDE_COMMENT_IS_MISSING = "Supplier override reason is missing";
    String SLA_QUANTITY_IS_NOT_GREATER_THAN_ZERO = "Suggested SLA should be greater than zero";
    String APP_QUANTITY_IS_NOT_GREATER_THAN_ZERO = "Suggested price should be greater than zero";
    String REQUIREMENT_NOT_FOUND_FOR_GIVEN_REQUIREMENT_ID = "Requirement not found for given requirement Id";
    String INVALID_QUANTITY_WITHOUT_COMMENT = "Invalid quantity without reason";
    String INVALID_APP_WITHOUT_COMMENT = "Invalid Price without reason";

    String NO_REQUIREMENT_FOUND = "No Requirement found for the uploaded file";
    String UNSUPPORTED_OPERATION = "Unsupported Operation for the given state";

    String QUANTITY_OVERRIDE_COMMENT = "quantityOverrideComment";
    String APP_OVERRIDE_COMMENT = "appOverrideComment";
    String SUPPLIER_OVERRIDE_COMMENT = "supplierOverrideComment";
    String STATUS = "status";

}

