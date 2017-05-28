package fk.retail.ip.requirement.internal.repository;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import fk.retail.ip.requirement.config.TestDbModule;
import fk.retail.ip.requirement.internal.entities.FsnSales;
import fk.retail.ip.requirement.internal.entities.ProductInfo;
import fk.sp.common.extensions.jpa.TransactionalJpaRepositoryTest;
import java.util.List;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by agarwal.vaibhav on 16/02/17.
 */
@RunWith(JukitoRunner.class)
@UseModules(TestDbModule.class)
public class ProductInfoRepositoryTest extends TransactionalJpaRepositoryTest {

    @Inject
    ProductInfoRepository productInfoRepository;

    @Inject
    FsnSalesRepository fsnSalesRepository;

    @After
    public void resetAutoIncrement() {
        entityManagerProvider.get()
                .createNativeQuery("TRUNCATE SCHEMA PUBLIC RESTART IDENTITY AND COMMIT NO CHECK")
                .executeUpdate();
    }

    @Test
    public void testGetProductInfo() {
        ProductInfo productInfo = getProductInfo(1);
        productInfoRepository.persist(productInfo);
        List<ProductInfo> productInfoList = productInfoRepository.getProductInfo(Sets.newHashSet("fsn1"));
        Assert.assertEquals(1, productInfoList.size());
        Assert.assertEquals(productInfo, productInfoList.get(0));
    }

    @Test
    public void testGetFsns(){
        ProductInfo productInfo = getProductInfo(1);
        ProductInfo productInfo1 = getProductInfo(2);
        productInfoRepository.persist(productInfo);
        productInfoRepository.persist(productInfo1);
        List<String> fsns = productInfoRepository.getFsns("dummy_vertical","dummy_category", "dummy_super_category", null);
        Assert.assertEquals(2, fsns.size());
        Assert.assertEquals("fsn1", fsns.get(0));
        Assert.assertEquals("fsn2", fsns.get(1));

    }

    @Test
    public void testGetSegmentedFsns() {
        FsnSales fsnSales = TestHelper.getFsnSales("fsn",90, 120);
        ProductInfo productInfo = TestHelper.getProductDetail("fsn");
        productInfoRepository.persist(productInfo);
        fsnSalesRepository.persist(fsnSales);
        String query = "vertical = 'dummy_vertical' and last_po_date > CURRENT_DATE - INTERVAL 15 DAY and sales_time = 90 and sales_quantity > 100 ";
        List<String> fsns = productInfoRepository.getFsns(query);
        Assert.assertEquals(1,fsns.size());
        Assert.assertEquals("fsn", fsns.get(0));
    }


    private ProductInfo getProductInfo(int i) {
       ProductInfo productInfo = new ProductInfo();
       productInfo.setFsn("fsn" + String.valueOf(i));
       productInfo.setCategory("dummy_category");
       productInfo.setBrand("dummy_brand");
       productInfo.setFsp(1);
       productInfo.setTitle("dummy_title");
       productInfo.setSuperCategory("dummy_super_category");
       productInfo.setVertical("dummy_vertical");
       return  productInfo;
    }
}
