package fk.retail.ip.ssl.client;

import fk.retail.ip.ssl.model.SupplierSelectionRequest;
import fk.retail.ip.ssl.model.SupplierSelectionResponse;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by yogeshwari.k on 01/03/17.
 */
public interface SslClient {
    List<SupplierSelectionResponse> getSupplierSelectionResponse(List<SupplierSelectionRequest> requests) throws ExecutionException, InterruptedException;
}
