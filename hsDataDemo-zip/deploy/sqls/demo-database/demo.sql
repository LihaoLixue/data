/*
SQLyog Ultimate v12.4.3 (64 bit)
MySQL - 10.2.19-MInnoDBDB : Database - monitor
*********************************************************************
*/
CREATE DATABASE IF NOT EXISTS `rpc_demo` DEFAULT CHARSET utf8 COLLATE utf8_general_ci;

USE `rpc_demo`;

DROP TABLE IF EXISTS `tb_rpc_demo`;

CREATE TABLE `tb_rpc_demo` (
  `app_id` VARCHAR(40) NOT NULL,
  `app_name` VARCHAR(50) DEFAULT NULL,
  `app_desc` VARCHAR(200) DEFAULT NULL,
  `app_host` VARCHAR(2000) DEFAULT NULL,
  `app_port` TEXT DEFAULT NULL,
  `app_group` BIGINT(50) DEFAULT NULL,
  `app_type` VARCHAR(50) DEFAULT NULL,
  PRIMARY KEY (`app_id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8;