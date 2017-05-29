INSERT OVERWRITE TABLE alpha_fsn_monthly_sales_bucket_fact
select fsn, sale_period, count(DISTINCT (unit_id)) FROM  (
select
 ff.fulfill_item_product_id as fsn,
 case when DATEDIFF(CURRENT_DATE,from_unixtime(unix_timestamp(CONCAT(ff.fulfill_item_unit_reserve_actual_date_key ,'') ,'yyyyMMdd') , 'yyyy-MM-dd')) <= 30 then 30
 when DATEDIFF(CURRENT_DATE,from_unixtime(unix_timestamp(CONCAT(ff.fulfill_item_unit_reserve_actual_date_key ,'') ,'yyyyMMdd') , 'yyyy-MM-dd')) > 30 and  DATEDIFF(CURRENT_DATE,from_unixtime(unix_timestamp(CONCAT(ff.fulfill_item_unit_reserve_actual_date_key ,'') ,'yyyyMMdd') , 'yyyy-MM-dd')) <= 60 then 60
   when DATEDIFF(CURRENT_DATE,from_unixtime(unix_timestamp(CONCAT(ff.fulfill_item_unit_reserve_actual_date_key ,'') ,'yyyyMMdd') , 'yyyy-MM-dd')) > 60 and  DATEDIFF(CURRENT_DATE,from_unixtime(unix_timestamp(CONCAT(ff.fulfill_item_unit_reserve_actual_date_key ,'') ,'yyyyMMdd') , 'yyyy-MM-dd')) <= 90 then 90
 when DATEDIFF(CURRENT_DATE,from_unixtime(unix_timestamp(CONCAT(ff.fulfill_item_unit_reserve_actual_date_key ,'') ,'yyyyMMdd') , 'yyyy-MM-dd')) > 90 and  DATEDIFF(CURRENT_DATE,from_unixtime(unix_timestamp(CONCAT(ff.fulfill_item_unit_reserve_actual_date_key ,'') ,'yyyyMMdd') , 'yyyy-MM-dd')) <= 120 then 120
 when DATEDIFF(CURRENT_DATE,from_unixtime(unix_timestamp(CONCAT(ff.fulfill_item_unit_reserve_actual_date_key ,'') ,'yyyyMMdd') , 'yyyy-MM-dd')) > 120 and  DATEDIFF(CURRENT_DATE,from_unixtime(unix_timestamp(CONCAT(ff.fulfill_item_unit_reserve_actual_date_key ,'') ,'yyyyMMdd') , 'yyyy-MM-dd')) <= 150 then 150
   when DATEDIFF(CURRENT_DATE,from_unixtime(unix_timestamp(CONCAT(ff.fulfill_item_unit_reserve_actual_date_key ,'') ,'yyyyMMdd') , 'yyyy-MM-dd')) > 150 and  DATEDIFF(CURRENT_DATE,from_unixtime(unix_timestamp(CONCAT(ff.fulfill_item_unit_reserve_actual_date_key ,'') ,'yyyyMMdd') , 'yyyy-MM-dd')) <= 180 then 180
 end as sale_period,
 ff.fulfill_item_unit_id as unit_id
FROM bigfoot_external_neo.scp_fulfillment__fulfillment_unit_hive_fact ff join (alpha_fsn_fact)
WHERE ff.fulfill_item_unit_reserve_actual_date_key >= YEAR(DATE_SUB(current_date(), 180))*10000 +  MONTH(DATE_SUB(current_date(), 180))*100 + DAY(DATE_SUB(current_date(), 180))
and ff.fulfill_item_product_id = (alpha_fsn_fact).fsn
) sales_table group by fsn, sale_period

--
-- // At fsn-fc level


INSERT OVERWRITE TABLE alpha_fsn_fc_monthly_sales_bucket_fact
select fsn, warehouse, sale_period, count(DISTINCT (unit_id)) FROM  (
select
 ff.fulfill_item_product_id as fsn,ff.fulfill_item_unit_region As warehouse,
 case when DATEDIFF(CURRENT_DATE,from_unixtime(unix_timestamp(CONCAT(ff.fulfill_item_unit_reserve_actual_date_key ,'') ,'yyyyMMdd') , 'yyyy-MM-dd')) <= 30 then 30
 when DATEDIFF(CURRENT_DATE,from_unixtime(unix_timestamp(CONCAT(ff.fulfill_item_unit_reserve_actual_date_key ,'') ,'yyyyMMdd') , 'yyyy-MM-dd')) > 30 and  DATEDIFF(CURRENT_DATE,from_unixtime(unix_timestamp(CONCAT(ff.fulfill_item_unit_reserve_actual_date_key ,'') ,'yyyyMMdd') , 'yyyy-MM-dd')) <= 60 then 60
   when DATEDIFF(CURRENT_DATE,from_unixtime(unix_timestamp(CONCAT(ff.fulfill_item_unit_reserve_actual_date_key ,'') ,'yyyyMMdd') , 'yyyy-MM-dd')) > 60 and  DATEDIFF(CURRENT_DATE,from_unixtime(unix_timestamp(CONCAT(ff.fulfill_item_unit_reserve_actual_date_key ,'') ,'yyyyMMdd') , 'yyyy-MM-dd')) <= 90 then 90
 when DATEDIFF(CURRENT_DATE,from_unixtime(unix_timestamp(CONCAT(ff.fulfill_item_unit_reserve_actual_date_key ,'') ,'yyyyMMdd') , 'yyyy-MM-dd')) > 90 and  DATEDIFF(CURRENT_DATE,from_unixtime(unix_timestamp(CONCAT(ff.fulfill_item_unit_reserve_actual_date_key ,'') ,'yyyyMMdd') , 'yyyy-MM-dd')) <= 120 then 120
 when DATEDIFF(CURRENT_DATE,from_unixtime(unix_timestamp(CONCAT(ff.fulfill_item_unit_reserve_actual_date_key ,'') ,'yyyyMMdd') , 'yyyy-MM-dd')) > 120 and  DATEDIFF(CURRENT_DATE,from_unixtime(unix_timestamp(CONCAT(ff.fulfill_item_unit_reserve_actual_date_key ,'') ,'yyyyMMdd') , 'yyyy-MM-dd')) <= 150 then 150
   when DATEDIFF(CURRENT_DATE,from_unixtime(unix_timestamp(CONCAT(ff.fulfill_item_unit_reserve_actual_date_key ,'') ,'yyyyMMdd') , 'yyyy-MM-dd')) > 150 and  DATEDIFF(CURRENT_DATE,from_unixtime(unix_timestamp(CONCAT(ff.fulfill_item_unit_reserve_actual_date_key ,'') ,'yyyyMMdd') , 'yyyy-MM-dd')) <= 180 then 180
 end as sale_period,
 ff.fulfill_item_unit_id as unit_id
FROM bigfoot_external_neo.scp_fulfillment__fulfillment_unit_hive_fact ff
LEFT OUTER JOIN bigfoot_external_neo.sp_seller__seller_hive_dim sel
                ON ff.seller_id_key=sel.seller_hive_dim_key
left outer join (alpha_fsn_fact)
WHERE ff.fulfill_item_unit_reserve_actual_date_key >= YEAR(DATE_SUB(current_date(), 180))*10000 +  MONTH(DATE_SUB(current_date(), 180))*100 + DAY(DATE_SUB(current_date(), 180))
and ff.fulfill_item_product_id = "ACCCRRQZ2BHPWWEH" and sel.is_first_party_seller = true
) sales_table group by fsn,warehouse, sale_period






