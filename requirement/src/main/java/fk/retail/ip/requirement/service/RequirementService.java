package fk.retail.ip.requirement.service;

import com.google.inject.Inject;
import fk.retail.ip.core.poi.SpreadSheetReader;
import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.retail.ip.requirement.internal.repository.RequirementRepository;
import fk.retail.ip.requirement.model.DownloadRequirementRequest;
import fk.retail.ip.requirement.model.RequirementManager;
import fk.retail.ip.requirement.model.RequirementUploadLineItem;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.StreamingOutput;

/**
 * Created by nidhigupta.m on 26/01/17.
 */
public class RequirementService {

    private final RequirementRepository requirementRepository;
    private final RequirementManager requirementManager;

    @Inject
    public RequirementService(RequirementRepository requirementRepository, RequirementManager requirementManager) {
        this.requirementRepository = requirementRepository;
        this.requirementManager = requirementManager;

    }

    public StreamingOutput downloadRequirement(DownloadRequirementRequest downloadRequirementRequest) {
        List<Long> requirementIds = downloadRequirementRequest.getRequirementIds();
        String requirementState = downloadRequirementRequest.getState();
        boolean isLastAppSupplierRequired = downloadRequirementRequest.isLastAppSupplierRequired();
        List<Requirement> requirements;
        if (!requirementIds.isEmpty()) {
            requirements = requirementRepository.findRequirementByIds(requirementIds);
        } else {
            requirements = requirementRepository.findAllEnabledRequirements(requirementState);
        }

        StreamingOutput output = requirementManager.withRequirements(requirements).download(requirementState, isLastAppSupplierRequired);
        return output;

    }

    public List<RequirementUploadLineItem> uploadRequirement(InputStream inputStream, FormDataContentDisposition fileDetails,
                                                             Map<String, Object> params) throws IOException, InvalidFormatException {
        SpreadSheetReader spreadSheetReader = new SpreadSheetReader();
        List<Map<String, Object>> parsedJson = spreadSheetReader.writeToCsv(inputStream);
        String requirementState = params.get("state").toString();
        List<Requirement> requirements;
        requirements = requirementRepository.findAllEnabledRequirements(requirementState);
        return requirementManager.withRequirements(requirements).upload(requirementState, parsedJson);

    }

}
