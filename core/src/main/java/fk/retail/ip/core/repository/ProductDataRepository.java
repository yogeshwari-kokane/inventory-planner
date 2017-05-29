package fk.retail.ip.core.repository;

import java.util.List;

import fk.retail.ip.core.entities.ProductData;
import fk.sp.common.extensions.jpa.JpaGenericRepository;

/**
 * Created by nidhigupta.m on 16/05/17.
 */
public interface ProductDataRepository  extends JpaGenericRepository<ProductData, Long> {
    List<String> getDistinctVerticals();

    List<String> getFsns(String vertical);
}
