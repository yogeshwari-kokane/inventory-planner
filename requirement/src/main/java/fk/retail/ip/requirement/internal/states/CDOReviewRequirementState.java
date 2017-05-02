package fk.retail.ip.requirement.internal.states;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Provider;
import fk.retail.ip.requirement.internal.command.CalculateRequirementCommand;
import fk.retail.ip.requirement.internal.command.RequirementHelper;
import fk.retail.ip.requirement.internal.command.download.DownloadCDOReviewCommand;
import fk.retail.ip.requirement.internal.command.upload.CDOReviewUploadCommand;
import fk.retail.ip.requirement.internal.entities.ProductInfo;
import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.retail.ip.requirement.internal.repository.ProductInfoRepository;
import fk.retail.ip.requirement.model.RequirementDownloadLineItem;
import fk.retail.ip.requirement.model.UploadOverrideFailureLineItem;
import fk.retail.ip.ssl.SslClientCallable;
import fk.retail.ip.ssl.client.SslClient;
import fk.retail.ip.ssl.config.SslClientConfiguration;
import fk.retail.ip.ssl.model.SupplierSelectionRequest;
import fk.retail.ip.ssl.model.SupplierSelectionResponse;
import org.apache.commons.collections4.map.MultiKeyMap;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.ws.rs.core.StreamingOutput;

/**
 * Created by nidhigupta.m on 21/02/17.
 */
public class CDOReviewRequirementState implements RequirementState {
    private final Provider<DownloadCDOReviewCommand> downloadCDOReviewCommandProvider;
    private final Provider<CDOReviewUploadCommand> uploadCDOReviewCommandProvider;
    private final ProductInfoRepository productInfoRepository;
    private final SslClient sslClient;
    private final RequirementHelper requirementHelper;
    private final SslClientConfiguration sslClientConfiguration;

    @Inject
    public CDOReviewRequirementState(Provider<DownloadCDOReviewCommand> downloadCDOReviewCommandProvider, Provider<CDOReviewUploadCommand> uploadCDOReviewCommandProvider,
                                     ProductInfoRepository productInfoRepository, SslClient sslClient, RequirementHelper requirementHelper,
                                     SslClientConfiguration sslClientConfiguration) {
        this.downloadCDOReviewCommandProvider = downloadCDOReviewCommandProvider;
        this.uploadCDOReviewCommandProvider = uploadCDOReviewCommandProvider;
        this.productInfoRepository = productInfoRepository;
        this.sslClient = sslClient;
        this.requirementHelper = requirementHelper;
        this.sslClientConfiguration = sslClientConfiguration;
    }

    @Override
    public List<UploadOverrideFailureLineItem> upload(List<Requirement> requirements,
                                                      List<RequirementDownloadLineItem> parsedJson,
                                                      String userId,
                                                      Map<String, String> fsnToVerticalMap,
                                                      MultiKeyMap<String,SupplierSelectionResponse> fsnWhSupplierMap) {
        return uploadCDOReviewCommandProvider.get().execute(parsedJson, requirements, userId, fsnToVerticalMap, fsnWhSupplierMap);
    }

    @Override
    public Map<String, String> createFsnVerticalMap(List<Requirement> requirements) {
        Set<String> fsns = requirements.stream().map(Requirement::getFsn).collect(Collectors.toSet());
        List<ProductInfo> productInfos = productInfoRepository.getProductInfo(fsns);
        Map<String, String> fsnToVerticalMap = productInfos.stream().collect(Collectors.toMap(ProductInfo::getFsn, ProductInfo::getVertical, (k1, k2) -> k1));
        return fsnToVerticalMap;
    }

    @Override
    public MultiKeyMap<String, SupplierSelectionResponse> createFsnWhSupplierMap(
            List<RequirementDownloadLineItem> requirementDownloadLineItems, List<Requirement> requirements) {
        Map<Long, Requirement> requirementMap = requirements.stream().
                collect(Collectors.toMap(Requirement::getId, Function.identity()));
        List<Requirement> supplierOverriddenRequirements = getSupplierOverriddenRequirement(requirementDownloadLineItems, requirementMap);
        MultiKeyMap<String,SupplierSelectionResponse> fsnWhSupplierMap = null;
        try {
            fsnWhSupplierMap = getSSLResponseMap(supplierOverriddenRequirements);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return fsnWhSupplierMap;
    }

    @Override
    public StreamingOutput download(List<Requirement> requirements, boolean isLastAppSupplierRequired) {
        return downloadCDOReviewCommandProvider.get().execute(requirements, isLastAppSupplierRequired);
    }

    private List<Requirement> getSupplierOverriddenRequirement(List<RequirementDownloadLineItem> requirementDownloadLineItems, Map<Long, Requirement> requirementMap) {
        List<Requirement> requirements = Lists.newArrayList();
        for(RequirementDownloadLineItem row : requirementDownloadLineItems) {
            Long requirementId = row.getRequirementId();
            if(requirementMap.containsKey(requirementId)) {
                String overridenSupplier = row.getCdoSupplierOverride();
                String supplierOverrideReason = row.getCdoSupplierOverrideReason();
                String currentSupplier = row.getSupplier();
                if (overridenSupplier != currentSupplier && !isEmptyString(overridenSupplier) && !isEmptyString(supplierOverrideReason)) {
                    requirements.add(requirementMap.get(requirementId));
                }
            }
        }
        return requirements;
    }

    public MultiKeyMap<String,SupplierSelectionResponse> getSSLResponseMap (List<Requirement> requirements) throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(5);
        List<SupplierSelectionRequest> requests = requirementHelper.createSupplierSelectionRequest(requirements);
        List<SupplierSelectionResponse> supplierSelectionResponses = Lists.newArrayList();
        List<Future<List<SupplierSelectionResponse>>> futureList = Lists.newArrayList();
        for(List<SupplierSelectionRequest> requestList : Lists.partition(requests, sslClientConfiguration.getBatchSize())) {
            Future<List<SupplierSelectionResponse>> futureResponse = executor.submit(new SslClientCallable(sslClient, requestList));
            futureList.add(futureResponse);
        }
        for(Future<List<SupplierSelectionResponse>> future : futureList) {
            supplierSelectionResponses.addAll(future.get());
        }
        MultiKeyMap<String, SupplierSelectionResponse> supplierSelectionResponseMap  = new MultiKeyMap<>();
        supplierSelectionResponses.forEach( response -> {
            supplierSelectionResponseMap.put(response.getFsn(),response.getWarehouseId(), response);
        });
        return supplierSelectionResponseMap;
    }

    private boolean isEmptyString(String comment) {
        return comment == null || comment.trim().isEmpty() ? true : false;
    }
}
