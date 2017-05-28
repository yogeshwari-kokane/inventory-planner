INSERT OVERWRITE TABLE alpha_fsn_detail_fact
select pd.product_id , pd.analytic_super_category, pd.analytic_category, pd.analytic_vertical, pd.analytic_business_unit, pd.brand,
 pd.product_title, pd.publisher, pv.pv_band, pv.sales_band, inv.atp, inv.qoh, po.last_po_interval
from sp_product__product_attribute_hive_dim pd
left outer join retail_ip__fsn_pv_sales_band_fact pv
on pv.fsn = pd.product_id
left outer join retail_ip__retail_rp_fsn_warehouse_inventory_fact inv
on pd.product_id = inv.fsn
left outer join  fsn_alpha_inventory inv
on pd.product_id = inv.fsn
left outer join  last_po_raised po
on pd.product_id = po.fsn


INSERT OVERWRITE TABLE alpha_fsn_inventory
sELECT fsn, SUM(quantity) as atp, SUM(qoh) as qoh
        FROM retail_ip__retail_rp_fsn_warehouse_inventory_fact
        group by fsn


INSERT OVERWRITE TABLE alpha_fsn_last_po_raised_fact
SELECT m1.fsn, DATEDIFF(CURRENT_DATE,from_unixtime(unix_timestamp(CONCAT(max(m1.poi_initiated_date_key),''),'yyyyMMdd'))) as last_po_interval
FROM Retail_procurement__fki_po_poi_l0_fact m1
group by fsn;

