package fk.retail.ip.ssl.client;

import com.google.common.collect.Lists;
import fk.retail.ip.ssl.internal.command.GetSupplierDetailsCommand;
import fk.retail.ip.ssl.model.SupplierSelectionRequest;

import javax.inject.Provider;
import com.google.inject.Inject;
import fk.retail.ip.ssl.model.SupplierSelectionResponse;

import java.util.List;

/**
 * Created by yogeshwari.k on 01/03/17.
 */
public class HystrixSslClient implements SslClient{

    private final Provider<GetSupplierDetailsCommand> getSupplierDetailsCommandProvider;

    @Inject
    public HystrixSslClient(Provider<GetSupplierDetailsCommand> getSupplierDetailsCommandProvider) {
        this.getSupplierDetailsCommandProvider = getSupplierDetailsCommandProvider;
    }

    @Override
    public List<SupplierSelectionResponse> getSupplierSelectionResponse(List<SupplierSelectionRequest> requests) {
        return getSupplierDetailsCommandProvider.get()
                .withSslRequests(requests)
                .execute();
    }

    @Override
    public List<SupplierSelectionResponse> getBatchedSupplierSelectionResponse(List<SupplierSelectionRequest> requests) {
        List<SupplierSelectionResponse> responses = Lists.newArrayList();
        for(List<SupplierSelectionRequest> requestList : Lists.partition(requests, 10)) {
            GetSupplierDetailsCommand getSupplierDetailsCommand = getSupplierDetailsCommandProvider.get();
            getSupplierDetailsCommand.withSslRequests(requestList).execute();
            getSupplierDetailsCommand.promise().done(res -> responses.addAll(res));
        }
        return responses;
    }

}
