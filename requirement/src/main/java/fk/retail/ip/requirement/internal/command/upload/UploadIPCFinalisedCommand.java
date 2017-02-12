package fk.retail.ip.requirement.internal.command.upload;

import java.util.Map;

/**
 * Created by agarwal.vaibhav on 08/02/17.
 */
public class UploadIPCFinalisedCommand extends UploadCommand {
    @Override
    String validateStateSpecific(Map<String, Object> row) {
        return null;
    }

    @Override
    Map<String, Object> getOverriddenFields(Map<String, Object> row) {
        return null;
    }
}
