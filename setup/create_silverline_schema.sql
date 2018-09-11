--
-- Create DB and User
--

CREATE USER IF NOT EXISTS 'app_silverline'@'localhost' IDENTIFIED BY 'app123';
GRANT SELECT, INSERT, UPDATE, DELETE ON silverline.* TO 'app_silverline'@'localhost';

DROP SCHEMA IF EXISTS `silverline`;
CREATE DATABASE `silverline`  DEFAULT CHARACTER SET utf8;
USE `silverline`;

SET unique_checks=0;
SET foreign_key_checks=0;

--
-- User Management Tool
--
select "Creating User Management Tool tables...";

DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `user_id` bigint(12) NOT NULL AUTO_INCREMENT,
  `is_deleted` tinyint(1) DEFAULT NULL,
  `is_locked` tinyint(1) DEFAULT NULL,
  `locked_reason` varchar(1000) DEFAULT NULL,
  `username` varchar(200) DEFAULT NULL UNIQUE,
  `full_name` varchar(200) DEFAULT NULL,
  `created_by` varchar(200) DEFAULT NULL,
  `email` varchar(200) DEFAULT NULL,
  `last_login` datetime DEFAULT NULL,
  PRIMARY KEY (`user_id`)
);

DROP TABLE IF EXISTS `role` ;
CREATE TABLE IF NOT EXISTS `role` (
  `role_id` bigint(12) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL UNIQUE,
  `description` VARCHAR(1000) NULL DEFAULT NULL,
  `is_deleted` TINYINT(1) NOT NULL,
  PRIMARY KEY (`role_id`)
);

DROP TABLE IF EXISTS `permission` ;
CREATE TABLE IF NOT EXISTS `permission` (
  `permission_id` bigint(12) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NULL DEFAULT NULL,
  `description` VARCHAR(1000) NULL DEFAULT NULL,
  `is_deleted` TINYINT(1) NOT NULL,
  PRIMARY KEY (`permission_id`)
);

DROP TABLE IF EXISTS `user_role` ;
CREATE TABLE IF NOT EXISTS `user_role` (
  `user_id` bigint(12) NOT NULL,
  `role_id` bigint(12) NOT NULL,
  PRIMARY KEY (`user_id`, `role_id`)
);

DROP TABLE IF EXISTS `role_permission` ;
CREATE TABLE IF NOT EXISTS `role_permission` (
  `role_id` bigint(12) NOT NULL,
  `permission_id` bigint(12) NOT NULL,
  PRIMARY KEY (`role_id`, `permission_id`)
);

DROP TABLE IF EXISTS `user_info` ;
CREATE TABLE `user_info` (
  `user_id` bigint(12) NOT NULL AUTO_INCREMENT,
  `user_name` varchar(200) DEFAULT NULL,  
  `dept_id` varchar(100) NOT NULL,
  PRIMARY KEY (`user_id`)
);

DROP TABLE IF EXISTS `user_action` ;
CREATE TABLE IF NOT EXISTS `user_action` (
  `action_id` bigint(12) NOT NULL AUTO_INCREMENT,
  `action_name` VARCHAR(100) NOT NULL UNIQUE,
  `description` VARCHAR(1000) NULL DEFAULT NULL,
  `is_deleted` TINYINT(1) NOT NULL,
  PRIMARY KEY (`action_id`)
);

-- Create Product tables

DROP TABLE IF EXISTS `product` ;
CREATE TABLE `product` (
	`product_id` bigint(12) NOT NULL AUTO_INCREMENT,
	`product_title` varchar(100) NOT NULL,
	`product_description` varchar(1000) NOT NULL,
	`product_price` double DEFAULT 0,
	`product_unit` Integer DEFAULT NULL,
	`created_by` bigint(12) DEFAULT NULL,
	`created_date` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  	`last_modified_by` BIGINT(12) DEFAULT NULL,
  	`last_modified_date` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	`is_deleted` tinyint(1) DEFAULT 0,
  	`last_action` bigint(12) DEFAULT NULL,
  	PRIMARY KEY (`product_id`),
  	FOREIGN KEY (`created_by`) REFERENCES `silverline`.`user` (`user_id`),
  	FOREIGN KEY (`last_modified_by`) REFERENCES `silverline`.`user` (`user_id`),
  	FOREIGN KEY (`last_action`) REFERENCES `silverline`.`user_action` (`action_id`)
);

--
--Additional Monetary Tables
--
--DROP TABLE IF EXISTS `tip` ;
--CREATE TABLE `encumbrance` (
--	`encumbrance_id` bigint(12) NOT NULL AUTO_INCREMENT,
--	`fund_id` bigint(12) NOT NULL,
--	`fiscal_year` INT(4) DEFAULT NULL,
--	`amount` double DEFAULT NULL,
--	`created_by` bigint(12) DEFAULT NULL,
--	`created_date` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--  	`last_modified_by` BIGINT(12) DEFAULT NULL,
--  	`last_modified_date` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--	`is_deleted` tinyint(1) DEFAULT 0,
--  	`delete_reason` varchar(1000) DEFAULT NULL,
--  	`deleted_by` bigint(12) DEFAULT NULL,
--  	`deleted_date` TIMESTAMP NULL DEFAULT NULL,
--  	PRIMARY KEY (`encumbrance_id`),
--  	FOREIGN KEY (`fund_id`) REFERENCES `scholarships`.`fund` (`fund_id`),
--  	FOREIGN KEY (`created_by`) REFERENCES `scholarships`.`user` (`user_id`),
--  	FOREIGN KEY (`last_modified_by`) REFERENCES `scholarships`.`user` (`user_id`),
--  	FOREIGN KEY (`deleted_by`) REFERENCES `scholarships`.`user` (`user_id`)
--);

--DROP TABLE IF EXISTS `encumbrance_AUD` ;
--CREATE TABLE `encumbrance_AUD` (
--	`encumbrance_id` bigint(12) NOT NULL AUTO_INCREMENT,
--	`rev` integer NOT NULL,
--  	`revtype` tinyint NOT NULL,
--	`fund_id` bigint(12) NOT NULL,
--	`fiscal_year` INT(4) DEFAULT NULL,
--	`amount` double DEFAULT NULL,
--	`created_by` bigint(12) DEFAULT NULL,
--	`created_date` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--  	`last_modified_by` BIGINT(12) DEFAULT NULL,
--  	`last_modified_date` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--	`is_deleted` tinyint(1) DEFAULT 0,
--  	`delete_reason` varchar(1000) DEFAULT NULL,
--  	`deleted_by` bigint(12) DEFAULT NULL,
--  	`deleted_date` TIMESTAMP NULL DEFAULT NULL,
--  	PRIMARY KEY (`encumbrance_id`, `rev`)
--);

--
--Appointment Tables
--
--DROP TABLE IF EXISTS `strategy` ;
--CREATE TABLE `strategy` (
--	`strategy_id` bigint(12) NOT NULL AUTO_INCREMENT,
--	`fund_id` bigint(12) NOT NULL,
--	`description` varchar(1000) NOT NULL,
--	`effective_date` date NULL DEFAULT NULL,
--  	`fiscal_year` int(4) DEFAULT NULL,
--  	`academic_year` varchar(200) DEFAULT NULL,
--  	`term` varchar(200) DEFAULT NULL,
--  	`estimated_amount` double DEFAULT 0,
--	`created_by` bigint(12) DEFAULT NULL,
--	`created_date` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--  	`last_modified_by` BIGINT(12) DEFAULT NULL,
--  	`last_modified_date` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--	`is_deleted` tinyint(1) DEFAULT 0,
--  	`delete_reason` varchar(1000) DEFAULT NULL,
--  	`deleted_by` bigint(12) DEFAULT NULL,
--  	`deleted_date` TIMESTAMP NULL DEFAULT NULL,
--  	PRIMARY KEY (`strategy_id`),
--  	FOREIGN KEY (`fund_id`) REFERENCES `scholarships`.`fund` (`fund_id`),
--  	FOREIGN KEY (`created_by`) REFERENCES `scholarships`.`user` (`user_id`),
--  	FOREIGN KEY (`last_modified_by`) REFERENCES `scholarships`.`user` (`user_id`),
--  	FOREIGN KEY (`deleted_by`) REFERENCES `scholarships`.`user` (`user_id`)
--);

--DROP TABLE IF EXISTS `strategy_AUD` ;
--CREATE TABLE `strategy_AUD` (
--	`strategy_id` bigint(12) NOT NULL AUTO_INCREMENT,
--	`rev` integer NOT NULL,
--  	`revtype` tinyint NOT NULL,
--	`fund_id` bigint(12) NOT NULL,
--	`description` varchar(1000) NOT NULL,
--	`effective_date` date NULL DEFAULT NULL,
--  	`fiscal_year` int(4) DEFAULT NULL,
--  	`academic_year` varchar(200) DEFAULT NULL,
--  	`term` varchar(200) DEFAULT NULL,
--  	`estimated_amount` double DEFAULT 0,
--	`created_by` bigint(12) DEFAULT NULL,
--	`created_date` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--  	`last_modified_by` BIGINT(12) DEFAULT NULL,
--  	`last_modified_date` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--	`is_deleted` tinyint(1) DEFAULT 0,
--  	`delete_reason` varchar(1000) DEFAULT NULL,
--  	`deleted_by` bigint(12) DEFAULT NULL,
--  	`deleted_date` TIMESTAMP NULL DEFAULT NULL,
--  	PRIMARY KEY (`strategy_id`, `rev`)
--);

--
--Comments Tables
--

--DROP TABLE IF EXISTS `comment`;
--CREATE TABLE IF NOT EXISTS `comment` (
--	`comment_id` bigint(12) NOT NULL AUTO_INCREMENT,
--	`fund_id` bigint(12) NOT NULL,
--	`comment_text` varchar(1000) NOT NULL,
--	`created_by` bigint(12) DEFAULT NULL,
--	`created_date` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--	`is_deleted` tinyint(1) DEFAULT 0,
--  	PRIMARY KEY (`comment_id`),
--  	FOREIGN KEY (`fund_id`) REFERENCES `scholarships`.`fund` (`fund_id`),
--  	FOREIGN KEY (`created_by`) REFERENCES `scholarships`.`user` (`user_id`)
-- );
--
--DROP TABLE IF EXISTS `comment_notify_users`;
--CREATE TABLE IF NOT EXISTS `comment_notify_users` (
--  `comment_id` bigint(12) DEFAULT NULL,
--  `uuid` varchar(255) DEFAULT NULL,
--   FOREIGN KEY (`comment_id`) REFERENCES `scholarships`.`comment` (`comment_id`)
--);

--
--Document Tables
--
--DROP TABLE IF EXISTS `fund_document`;
--CREATE TABLE IF NOT EXISTS `fund_document` (
--  `document_id` bigint(12) NOT NULL AUTO_INCREMENT,
--  `fund_id` bigint(12) NOT NULL,
--  `file_name` varchar(200) DEFAULT NULL,
--  `description` varchar(1000) DEFAULT NULL,
--  `uploaded_date` timestamp NULL DEFAULT NULL,
--  `uploaded_by` bigint(12) DEFAULT NULL,
--  `deleted_date` timestamp NULL DEFAULT NULL,
--  `is_deleted` tinyint(1) DEFAULT NULL,
--   PRIMARY KEY(`document_id`),
--   FOREIGN KEY (`fund_id`) REFERENCES `scholarships`.`fund` (`fund_id`)
--
-- );
-- 
-- DROP TABLE IF EXISTS `fund_document_aud`;
--CREATE TABLE IF NOT EXISTS `fund_document_aud` (
--  `document_id` bigint(12) NOT NULL AUTO_INCREMENT,
--  `rev` integer NOT NULL,
--  `revtype` tinyint NOT NULL,
--  `fund_id` bigint(12) NOT NULL,
--  `file_name` varchar(200) DEFAULT NULL,
--  `description` varchar(1000) DEFAULT NULL,
--  `uploaded_date` timestamp NULL DEFAULT NULL,
--  `uploaded_by` bigint(12) DEFAULT NULL,
--  `deleted_date` timestamp NULL DEFAULT NULL,
--  `is_deleted` tinyint(1) DEFAULT NULL,
--   PRIMARY KEY (`document_id`, `rev`)
-- );
-- 
--DROP TABLE IF EXISTS `fund_document_content`;
--CREATE TABLE IF NOT EXISTS `fund_document_content`(
--  `document_id` bigint(12) NOT NULL ,
--  `file_content` longblob NOT NULL,
--  PRIMARY KEY (`document_id`),
--  FOREIGN KEY (`document_id`) REFERENCES `scholarships`.`fund_document` (`document_id`)
--);

----

ALTER TABLE `user_role` ADD CONSTRAINT `user_role_to_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE `user_role` ADD CONSTRAINT `user_role_to_role` FOREIGN KEY (`role_id`) REFERENCES `role` (`role_id`) ON DELETE NO ACTION ON UPDATE NO ACTION;

ALTER TABLE `role_permission` ADD CONSTRAINT `role_permission_to_role` FOREIGN KEY (`role_id`) REFERENCES `role` (`role_id`) ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE `role_permission` ADD CONSTRAINT `role_permission_to_permission` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`permission_id`) ON DELETE NO ACTION ON UPDATE NO ACTION;


-- 
-- User Management tool data
--
select "User Management Data...";
LOCK TABLES `user` WRITE;
load data local infile '[csv_files_path]user.csv' into table user fields terminated by ',' enclosed by '"' lines terminated by '\n' ignore 1 lines;
UNLOCK TABLES;

LOCK TABLES `role` WRITE;
load data local infile '[csv_files_path]role.csv' into table role fields terminated by ',' enclosed by '"' lines terminated by '\n' ignore 1 lines;
UNLOCK TABLES;

LOCK TABLES `permission` WRITE;
load data local infile '[csv_files_path]permission.csv' into table permission fields terminated by ',' enclosed by '"' lines terminated by '\n' ignore 1 lines;
UNLOCK TABLES;

LOCK TABLES `user_role` WRITE;
load data local infile '[csv_files_path]user_role.csv' into table user_role fields terminated by ',' enclosed by '"' lines terminated by '\n' ignore 1 lines;
UNLOCK TABLES;

LOCK TABLES `role_permission` WRITE;
load data local infile '[csv_files_path]role_permission.csv' into table role_permission fields terminated by ',' enclosed by '"' lines terminated by '\n' ignore 1 lines;
UNLOCK TABLES;

LOCK TABLES `user_info` WRITE;
load data local infile '[csv_files_path]user_info.csv' into table user_info fields terminated by ',' enclosed by '"' lines terminated by '\n' ignore 1 lines;
UNLOCK TABLES;


-- 
-- Product data
--
select "Prduct Data...";

LOCK TABLES `product` WRITE;
load data local infile '[csv_files_path]product.csv' into table item fields terminated by ',' enclosed by '"' lines terminated by '\n' ignore 1 lines;
UNLOCK TABLES;

SET unique_checks=1;
SET foreign_key_checks=1;

--
-- Done
--
select "Woohoo! All Done!";

