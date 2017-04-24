package fk.retail.ip.requirement.internal.command;

import com.google.inject.Inject;
import fk.retail.ip.requirement.internal.command.download.GenerateExcelCommand;
import fk.retail.ip.requirement.internal.entities.Requirement;
import fk.retail.ip.requirement.internal.repository.*;
import fk.retail.ip.requirement.model.RequirementSearchLineItem;
import fk.retail.ip.zulu.client.ZuluClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.map.MultiKeyMap;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Slf4j
public class SearchCommand extends RequirementSearchDataAggregator {

    @Inject
    public SearchCommand(FsnBandRepository fsnBandRepository, WeeklySaleRepository weeklySaleRepository, GenerateExcelCommand generateExcelCommand, LastAppSupplierRepository lastAppSupplierRepository,
                         ProductInfoRepository productInfoRepository, ZuluClient zuluClient, RequirementRepository requirementRepository, WarehouseRepository warehouseRepository) {
        super(fsnBandRepository, weeklySaleRepository,lastAppSupplierRepository, productInfoRepository, zuluClient, requirementRepository, warehouseRepository);
    }

    public Map<String, List<RequirementSearchLineItem>> execute(List<Requirement> requirements, List<Requirement> cdoRequirements) {
        log.info("Search Request for {} number of requirements", requirements.size());
        List<RequirementSearchLineItem> requirementSearchLineItems = requirements.stream().map(RequirementSearchLineItem::new).collect(toList());
        if(!cdoRequirements.isEmpty()) {
            MultiKeyMap<String, Integer> fsnWhQuantity = new MultiKeyMap();
            cdoRequirements.forEach(r -> {
                fsnWhQuantity.put(r.getFsn(), r.getWarehouse(), (int) r.getQuantity());
            });
            requirementSearchLineItems.forEach(reqItem
                    -> {
                reqItem.setQty(fsnWhQuantity.get(reqItem.getFsn(),reqItem.getWarehouse()));
            });
        }
        Map<String, List<RequirementSearchLineItem>> fsnToRequirement = requirementSearchLineItems.stream().collect(Collectors.groupingBy(RequirementSearchLineItem::getFsn));
        if (requirements.isEmpty()) {
            log.info("No requirements found for search.");
            return fsnToRequirement;
        }
        Set<String> fsns = fsnToRequirement.keySet();
        fetchProductData(fsnToRequirement);
        fetchFsnBandData(fsnToRequirement);
        fetchSalesBucketData(fsns, requirementSearchLineItems);
        return fsnToRequirement;
    }

}
