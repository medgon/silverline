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

DROP TABLE IF EXISTS `item` ;
CREATE TABLE IF NOT EXISTS `item` (
  `item_id` bigint(12) NOT NULL AUTO_INCREMENT,
  `item_name` VARCHAR(100) DEFAULT NULL,
  `item_description` VARCHAR(1000) NULL DEFAULT NULL,
  `item_quantity` INT(50) NULL DEFAULT NULL,
  `location` VARCHAR(1000) NULL DEFAULT NULL,
  PRIMARY KEY (`item_id`)
);

LOCK TABLES `item` WRITE;
load data local infile '[csv_files_path]item.csv' into table item fields terminated by ',' enclosed by '"' lines terminated by '\n' ignore 1 lines;
UNLOCK TABLES;

SET unique_checks=1;
SET foreign_key_checks=1;

--
-- Done
--
select "Woohoo! All Done!";

