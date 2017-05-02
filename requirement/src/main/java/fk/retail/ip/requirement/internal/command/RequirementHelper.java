package fk.retail.ip.requirement.internal.command;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.retail.ip.requirement.internal.repository.WarehouseSupplierSlaRepository;
import fk.retail.ip.ssl.model.SupplierSelectionRequest;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Optional;

@Slf4j
public class RequirementHelper {

    private final WarehouseSupplierSlaRepository warehouseSupplierSlaRepository;

    @Inject
    public RequirementHelper (WarehouseSupplierSlaRepository warehouseSupplierSlaRepository) {
        this.warehouseSupplierSlaRepository = warehouseSupplierSlaRepository;
    }

    //TODO: optimize this
    public int getSla(String vertical, String warehouse, String supplier, int apiSla) {
        if (vertical == null || warehouse == null) {
            return apiSla;
        }
        try {
            Optional<Integer> sla = warehouseSupplierSlaRepository.getSla(vertical, warehouse, supplier);
            if (sla.isPresent()) {
                return sla.get();
            } else {
                return apiSla;
            }
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return apiSla;
        }
    }

    public List<SupplierSelectionRequest> createSupplierSelectionRequest(List<Requirement> requirements) {
        List<SupplierSelectionRequest> requests = Lists.newArrayList();
        requirements.forEach(req -> {
            SupplierSelectionRequest request = new SupplierSelectionRequest();
            request.setFsn(req.getFsn());
            request.setSku("SKU0000000000000");
            request.setOrderType(req.getProcType());
            request.setQuantity((int) req.getQuantity());
            request.setEntityType("Requirement");
            request.setWarehouseId(req.getWarehouse());
            request.setTenantId("FKI");
            DateTime date = DateTime.now();
            request.setRequiredByDate(date.toString());
            requests.add(request);
        });
        return requests;
    }

}
