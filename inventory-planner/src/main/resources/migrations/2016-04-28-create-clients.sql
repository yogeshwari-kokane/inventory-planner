--liquibase formatted sql

--changeset durga.p:1

CREATE TABLE `client` (
  `name` VARCHAR(256) NOT NULL,
  `email` VARCHAR(256) NOT NULL,
  `description` VARCHAR(256) NULL,
  PRIMARY KEY (`name`));

INSERT INTO `client` (`name`, `email`, `description`)
VALUES ('sp.test','sp-dev@flipkart.com','A test client, not to be used in production');


-- rollback drop table client
