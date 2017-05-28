CREATE TABLE `requirement_snapshot` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `forecast` varchar(200) DEFAULT NULL,
  `inventory_qty` int(11) DEFAULT NULL,
  `qoh` int(11) DEFAULT NULL,
  `pending_po_qty` int(11) DEFAULT NULL,
  `open_req_qty` int(11) DEFAULT NULL,
  `iwit_intransit_qty` int(11) DEFAULT NULL,
  `policy` text,
  `group_id` bigint(20) unsigned DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `group_id` (`group_id`),
  CONSTRAINT `requirement_snapshot_ibfk_2` FOREIGN KEY (`group_id`) REFERENCES `ip_groups` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `last_app_supplier` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `fsn` varchar(11) DEFAULT NULL,
  `warehouse` varchar(11) DEFAULT NULL,
  `last_supplier` varchar(11) DEFAULT NULL,
  `last_app` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `warehouse` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `code` varchar(45) NOT NULL DEFAULT '',
  `name` varchar(45) NOT NULL DEFAULT '',
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `key` (`code`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


alter table projection_states add column fsn varchar(20);
update projection_states as ps set fsn = (select fsn from projections where id = ps.projection_id ) where fsn = null;

//populating existing fsn from projections to projection_states
update projection_states as ps set fsn = (select fsn from projections where id = ps.projection_id ) where fsn is NULL

alter table projection_states add column is_current TINYINT(1);
update projection_states ps join projections p on ps.projection_id=p.id and p.enabled=1 and p.current_state=ps.state set ps.is_current=1;
update projection_states as ps set enabled = 1 where projection_id in (select id from projections where enabled = 1 );

alter table projection_states add column requirement_snapshot_id BIGINT(20) unsigned, add key(requirement_snapshot_id);

ALTER TABLE `projection_states` ADD CONSTRAINT `projection_states_req_snapshot_id` FOREIGN KEY (`requirement_snapshot_id`) REFERENCES `requirement_snapshot` (`id`)
update projection_states as ps set enabled = 1 where projection_id in (select id from projections where enabled = 1 );



alter table projection_states add column proc_type varchar(20);
update  projection_states as ps set proc_type= (select proc_type from projections where id = ps.projection_id );

alter table ip_groups add column is_enabled TINYINT(1) NOT NULL DEFAULT 0;
update ip_groups set is_enabled= 1 where tag = "rp_planning";

alter table `INTRANSIT` add column `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP;



CREATE TABLE `requirement_approval_state_transition` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `group_id` int(11) NOT NULL,
  `from_state` varchar(20) NOT NULL DEFAULT '',
  `to_state` varchar(20) NOT NULL DEFAULT '',
  `action` varchar(20) NOT NULL DEFAULT '',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `group_fromstate_action` (`group_id`,`from_state`,`action`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



//changes for Segmentation service

alter table ip_groups add column rule VARCHAR(1000);
alter table ip_groups add column segmentation_enabled boolean;


alter table product_detail add column business_unit VARCHAR(100);
alter table product_detail add column pv_band int(2);
alter table product_detail add column sales_band int(2);
alter table product_detail add column atp int(11);
alter table product_detail add column qoh int(11);
alter table product_detail add column last_po_date timestamp  NULL;
alter table product_detail add column publisher varchar(20);



CREATE TABLE `fsn_sales_data` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `fsn` varchar(20) DEFAULT NULL,
  `sales_time` int(11) DEFAULT NULL,
  `sales_quantity` int(11) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `warehouse` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `fsn_time` (`fsn`,`sales_time`),
  KEY `composite` (`sales_quantity`,`sales_time`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
