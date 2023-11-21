-- MySQL Administrator dump 1.4
--
-- ------------------------------------------------------
-- Server version	5.1.43-community


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


--
-- Create schema profiling
--

CREATE DATABASE IF NOT EXISTS profiling;
USE profiling;

--
-- Definition of table `analysis_content`
--

DROP TABLE IF EXISTS `analysis_content`;
CREATE TABLE `analysis_content` (
  `AC_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `AC_ANALYSIS_ID` int(10) unsigned NOT NULL,
  `AC_TABLE_NAME` mediumtext,
  `AC_COLUMN_NAME` mediumtext,
  PRIMARY KEY (`AC_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `analysis_content`
--

/*!40000 ALTER TABLE `analysis_content` DISABLE KEYS */;
/*!40000 ALTER TABLE `analysis_content` ENABLE KEYS */;


--
-- Definition of table `analysis_info`
--

DROP TABLE IF EXISTS `analysis_info`;
CREATE TABLE `analysis_info` (
  `A_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `A_NAME` text,
  `A_DESC` text,
  `A_CREATION` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `A_UPDATE` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `A_CREATOR` varchar(45) DEFAULT NULL,
  `A_CONNECTION_ID` int(10) unsigned NOT NULL,
  PRIMARY KEY (`A_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `analysis_info`
--

/*!40000 ALTER TABLE `analysis_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `analysis_info` ENABLE KEYS */;


--
-- Definition of table `analysis_results`
--

DROP TABLE IF EXISTS `analysis_results`;
CREATE TABLE `analysis_results` (
  `AR_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `AR_ANALYSIS_CONTENT_ID` int(10) unsigned DEFAULT '0',
  `AR_DATE` datetime DEFAULT '0000-00-00 00:00:00',
  `AR_DATA_TYPE` varchar(45) DEFAULT NULL,
  `AR_LOW_VALUE` mediumtext,
  `AR_HIGHT_VALUE` mediumtext,
  `AR_LOW_VALUE_COUNT` int(10) unsigned DEFAULT NULL,
  `AR_HIGHT_VALUE_COUNT` int(10) unsigned DEFAULT NULL,
  `AR_AVG_VALUE` double DEFAULT NULL,
  `AR_DISTINCT_COUNT` int(10) unsigned DEFAULT NULL,
  `AR_NULL_COUNT` int(10) unsigned DEFAULT NULL,
  `AR_NULL_PERC` double DEFAULT NULL,
  `AR_ZERO_COUNT` int(10) unsigned DEFAULT NULL,
  `AR_ZERO_PERC` double DEFAULT NULL,
  `AR_BLANK_COUNT` int(10) unsigned DEFAULT NULL,
  `AR_BLANK_PERC` double DEFAULT NULL,
  PRIMARY KEY (`AR_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `analysis_results`
--

/*!40000 ALTER TABLE `analysis_results` DISABLE KEYS */;
/*!40000 ALTER TABLE `analysis_results` ENABLE KEYS */;


--
-- Definition of table `condition_results`
--

DROP TABLE IF EXISTS `condition_results`;
CREATE TABLE `condition_results` (
  `CNR_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `CNR_VALID_COUNT` int(10) unsigned DEFAULT NULL,
  `CNR_VALID_PERCENT` double DEFAULT NULL,
  `CNR_DISTINCT_VALID` int(10) unsigned DEFAULT NULL,
  `CNR_DISTINCT_PERCENT` double DEFAULT NULL,
  `CNR_CONDITION_ID` int(11) DEFAULT NULL,
  `CNR_DATE` datetime NOT NULL,
  `CNR_RULE_SET_ID` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`CNR_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `condition_results`
--

/*!40000 ALTER TABLE `condition_results` DISABLE KEYS */;
/*!40000 ALTER TABLE `condition_results` ENABLE KEYS */;


--
-- Definition of table `conditions`
--

DROP TABLE IF EXISTS `conditions`;
CREATE TABLE `conditions` (
  `CND_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `CND_RULE_SET_ID` varchar(45) NOT NULL,
  `CND_VALUE1` varchar(45) NOT NULL,
  `CND_VALUE2` varchar(45) NOT NULL,
  `CND_OPERATOR` varchar(45) NOT NULL,
  PRIMARY KEY (`CND_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `conditions`
--

/*!40000 ALTER TABLE `conditions` DISABLE KEYS */;
/*!40000 ALTER TABLE `conditions` ENABLE KEYS */;


--
-- Definition of table `connections`
--

DROP TABLE IF EXISTS `connections`;
CREATE TABLE `connections` (
  `C_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `C_DATABASE` varchar(100) DEFAULT NULL,
  `C_DRIVER` varchar(100) DEFAULT NULL,
  `C_HOST` varchar(100) NOT NULL DEFAULT '',
  `C_PORT` varchar(100) DEFAULT NULL,
  `C_LOGIN` varchar(100) DEFAULT NULL,
  `C_PASSWORD` varchar(100) DEFAULT NULL,
  `C_NAME` varchar(100) DEFAULT NULL,
  `C_SCHEMA` varchar(100) DEFAULT NULL,
  `C_IS_FROM_REPOSITORY` tinyint(1) NOT NULL DEFAULT '0',
  `C_DIRECTORY_ITEM_ID` int(10) unsigned DEFAULT NULL,
  `C_FMDT_DATASOURCE_NAME` varchar(100) DEFAULT NULL,
  `C_FMDT_CONNECTION_NAME` varchar(100) DEFAULT NULL,
  `C_VANILLA_GROUP_ID` int(11) DEFAULT NULL,
  PRIMARY KEY (`C_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `connections`
--

/*!40000 ALTER TABLE `connections` DISABLE KEYS */;
/*!40000 ALTER TABLE `connections` ENABLE KEYS */;


--
-- Definition of table `rules_set`
--

DROP TABLE IF EXISTS `rules_set`;
CREATE TABLE `rules_set` (
  `RS_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `RS_ANALYSIS_CONTENT_ID` int(10) unsigned DEFAULT NULL,
  `RS_NAME` varchar(100) DEFAULT NULL,
  `RS_DESC` mediumtext,
  `RS_OPERATOR` int(10) unsigned NOT NULL,
  PRIMARY KEY (`RS_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `rules_set`
--

/*!40000 ALTER TABLE `rules_set` DISABLE KEYS */;
/*!40000 ALTER TABLE `rules_set` ENABLE KEYS */;


--
-- Definition of table `tags`
--

DROP TABLE IF EXISTS `tags`;
CREATE TABLE `tags` (
  `T_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `T_CREATOR` varchar(45) DEFAULT NULL,
  `T_CREATION_DATE` datetime DEFAULT NULL,
  `T_UPDATE_DATE` datetime DEFAULT NULL,
  `T_COMMENT` mediumtext,
  `T_RESULT_ID` int(10) unsigned DEFAULT NULL,
  `T_RESULT_CONDITION_ID` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`T_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `tags`
--

/*!40000 ALTER TABLE `tags` DISABLE KEYS */;
/*!40000 ALTER TABLE `tags` ENABLE KEYS */;




/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
