package fk.retail.ip.requirement.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import fk.retail.ip.core.poi.SpreadSheetReader;
import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.retail.ip.requirement.internal.exception.NoRequirementsSelectedException;
import fk.retail.ip.requirement.internal.factory.RequirementStateFactory;
import fk.retail.ip.requirement.internal.repository.RequirementRepository;
import fk.retail.ip.requirement.internal.states.RequirementState;
import fk.retail.ip.requirement.model.DownloadRequirementRequest;
import fk.retail.ip.requirement.model.RequirementDownloadLineItem;
import fk.retail.ip.requirement.model.RequirementUploadLineItem;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.ws.rs.core.StreamingOutput;

/**
 * Created by nidhigupta.m on 26/01/17.
 */
@Slf4j
public class RequirementService {

    private final RequirementRepository requirementRepository;
    private final RequirementStateFactory requirementStateFactory;

    @Inject
    public RequirementService(RequirementRepository requirementRepository, RequirementStateFactory requirementStateFactory) {
        this.requirementRepository = requirementRepository;
        this.requirementStateFactory = requirementStateFactory;

    }

    public StreamingOutput downloadRequirement(DownloadRequirementRequest downloadRequirementRequest) {
        List<Long> requirementIds = downloadRequirementRequest.getRequirementIds();
        String requirementState = downloadRequirementRequest.getState();
        boolean isLastAppSupplierRequired = downloadRequirementRequest.isLastAppSupplierRequired();
        List<Requirement> requirements;
        if (!requirementIds.isEmpty()) {
            requirements = requirementRepository.findRequirementByIds(requirementIds);
        } else {
            requirements = requirementRepository.findAllCurrentRequirements(requirementState);
        }
        //todo: cleanup remove if 'all' column value for warehouse is removed
        if (requirements.size() == 0) {
            throw new NoRequirementsSelectedException("No requirements were selected in state " + requirementState);
        }
        requirements = requirements.stream().filter(requirement -> !requirement.getWarehouse().equals("all")).collect(Collectors.toList());
        RequirementState state = requirementStateFactory.getRequirementState(requirementState);
        return state.download(requirements, isLastAppSupplierRequired);

    }

    public List<RequirementUploadLineItem> uploadRequirement(InputStream inputStream, String requirementState) throws IOException, InvalidFormatException {
        SpreadSheetReader spreadSheetReader = new SpreadSheetReader();
        List<Map<String, Object>> parsedMappingList = spreadSheetReader.read(inputStream);
        ObjectMapper mapper = new ObjectMapper();
        List<RequirementDownloadLineItem> requirementDownloadLineItems = mapper.convertValue(parsedMappingList, new TypeReference<List<RequirementDownloadLineItem>>() {});
//        System.out.println("the line item is :");
//        System.out.println(requirementDownloadLineItems.get(0));
//        System.out.println("the parsed json is");

        List<Requirement> requirements;
        List<Long> requirementIds = new ArrayList<>();
        requirementDownloadLineItems.forEach(row ->
                requirementIds.add(row.getRequirementId())
        );
//        parsedMappingList.forEach(row ->
//                requirementIds.add((Long)row.get("Requirement Id"))
//        );

        System.out.println("the list of requirement ids is : ");
        System.out.println(requirementIds.get(0));
        requirements = requirementRepository.findRequirementByIds(requirementIds);
        if (requirements.size() == 0) {
            throw new NoRequirementsSelectedException("No requirements were selected in state " + requirementState);
        }
        RequirementState state = requirementStateFactory.getRequirementState(requirementState);
        return state.upload(requirements, requirementDownloadLineItems);
//        return requirementManager.withRequirements(requirements).upload(requirementState, parsedJson);

    }

}
