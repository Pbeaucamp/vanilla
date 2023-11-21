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
-- Create schema vanilla_forms
--

CREATE DATABASE IF NOT EXISTS vanilla_forms;
USE vanilla_forms;

--
-- Definition of table `form`
--

DROP TABLE IF EXISTS `form`;
CREATE TABLE `form` (
  `F_ID` bigint(20) NOT NULL,
  `F_CREATION_DATE` datetime DEFAULT NULL,
  `F_CREATOR_ID` int(11) DEFAULT NULL,
  `F_DESCRIPTION` varchar(255) DEFAULT NULL,
  `F_NAME` varchar(255) DEFAULT NULL,
  `F_LIFE_EXPECTANCY` datetime DEFAULT NULL,
  `F_LIFE_EXPECTANCY_HOURS` int(11) DEFAULT '0',
  `F_LIFE_EXPECTANCY_DAYS` int(11) DEFAULT '0',
  `F_LIFE_EXPECTANCY_MONTHS` int(11) DEFAULT '0',
  `F_LIFE_EXPECTANCY_YEARS` int(11) DEFAULT '0',
  PRIMARY KEY (`F_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `form`
--

/*!40000 ALTER TABLE `form` DISABLE KEYS */;
/*!40000 ALTER TABLE `form` ENABLE KEYS */;


--
-- Definition of table `form_definition`
--

DROP TABLE IF EXISTS `form_definition`;
CREATE TABLE `form_definition` (
  `FD_ID` int(11) NOT NULL,
  `FD_FORM_ID` mediumtext,
  `FD_DEFINED` tinyint(4) DEFAULT NULL,
  `FD_CREATION_DATE` datetime DEFAULT NULL,
  `FD_CREATOR_ID` int(11) DEFAULT NULL,
  `FD_DESCRIPTION` text,
  `FD_START_DATE` date DEFAULT NULL,
  `FD_STOP_DATE` date DEFAULT NULL,
  `FD_VERSION` int(11) DEFAULT NULL,
  `FD_ACTIVE` tinyint(4) DEFAULT NULL,
  `FD_NAME` text,
  `FD_F_ID` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`FD_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `form_definition`
--

/*!40000 ALTER TABLE `form_definition` DISABLE KEYS */;
/*!40000 ALTER TABLE `form_definition` ENABLE KEYS */;


--
-- Definition of table `form_field_mapping`
--

DROP TABLE IF EXISTS `form_field_mapping`;
CREATE TABLE `form_field_mapping` (
  `FFM_ID` int(11) NOT NULL,
  `FFM_FD_ID` int(11) NOT NULL,
  `FFM_TT_ID` int(11) DEFAULT NULL,
  `FFM_DESCRIPTION` text,
  `FFM_FORM_FIELD_ID` text,
  `FFM_FORM_FIELD_NAME` text,
  `FFM_TABLE_COLUMN_NAME` text,
  `FFM_MULTIVALUED` tinyint(4) DEFAULT NULL,
  `FFM_TT_LABEL` varchar(255) DEFAULT NULL,
  `FFM_SQL_DATA_TYPE` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`FFM_ID`),
  KEY `fk_FORM_TABLE_MAPPING_FORM_DEFINITION1` (`FFM_FD_ID`),
  CONSTRAINT `fk_FORM_TABLE_MAPPING_FORM_DEFINITION1` FOREIGN KEY (`FFM_FD_ID`) REFERENCES `form_definition` (`FD_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

--
-- Dumping data for table `form_field_mapping`
--

/*!40000 ALTER TABLE `form_field_mapping` DISABLE KEYS */;
/*!40000 ALTER TABLE `form_field_mapping` ENABLE KEYS */;


--
-- Definition of table `form_instance`
--

DROP TABLE IF EXISTS `form_instance`;
CREATE TABLE `form_instance` (
  `FI_ID` int(11) NOT NULL,
  `FI_FD_ID` int(11) NOT NULL,
  `FI_CREATION_DATE` datetime DEFAULT NULL,
  `FI_GROUP_ID` int(11) DEFAULT NULL,
  `FI_SUBMITED` int(11) DEFAULT NULL,
  `FI_VALIDATED` int(11) DEFAULT NULL,
  `FI_LAST_SUBMITION_DATE` datetime DEFAULT NULL,
  `FI_LAST_VALIDATION_DATE` datetime DEFAULT NULL,
  `FI_SUBMITER_IP_ADR` varchar(255) DEFAULT NULL,
  `FI_VALIDATOR_IP_ADR` varchar(255) DEFAULT NULL,
  `FI_VALIDATOR_USER_ID` int(11) DEFAULT NULL,
  `FI_SUBMITER_USER_ID` int(11) DEFAULT NULL,
  `FI_EXPIRATION_DATE` datetime DEFAULT NULL,
  PRIMARY KEY (`FI_ID`),
  KEY `fk_FORM_INSTANCE_FORM_DEFINITION1` (`FI_FD_ID`),
  CONSTRAINT `fk_FORM_INSTANCE_FORM_DEFINITION1` FOREIGN KEY (`FI_FD_ID`) REFERENCES `form_definition` (`FD_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

--
-- Dumping data for table `form_instance`
--

/*!40000 ALTER TABLE `form_instance` DISABLE KEYS */;
/*!40000 ALTER TABLE `form_instance` ENABLE KEYS */;


--
-- Definition of table `form_instance_state`
--

DROP TABLE IF EXISTS `form_instance_state`;
CREATE TABLE `form_instance_state` (
  `FIS_ID` int(11) NOT NULL,
  `FIS_FI_ID` int(11) NOT NULL,
  `FIS_VALUE` text,
  `FIS_FFM_ID` bigint(20) DEFAULT NULL,
  `FIS_VALIDATED` int(11) DEFAULT NULL,
  PRIMARY KEY (`FIS_ID`),
  KEY `fk_FORM_INSTANCE_STATE_FORM_INSTANCE1` (`FIS_FI_ID`),
  CONSTRAINT `fk_FORM_INSTANCE_STATE_FORM_INSTANCE1` FOREIGN KEY (`FIS_FI_ID`) REFERENCES `form_instance` (`FI_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `form_instance_state`
--

/*!40000 ALTER TABLE `form_instance_state` DISABLE KEYS */;
/*!40000 ALTER TABLE `form_instance_state` ENABLE KEYS */;


--
-- Definition of table `form_table_mapping`
--

DROP TABLE IF EXISTS `form_table_mapping`;
CREATE TABLE `form_table_mapping` (
  `FTM_ID` int(11) NOT NULL,
  `FTM_FD_ID` int(11) NOT NULL,
  `FTM_TT_ID` int(11) NOT NULL,
  `FTM__FD_ID` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`FTM_ID`),
  KEY `fk_TARGET_TABLES_FORM_DEFINITION` (`FTM_FD_ID`),
  CONSTRAINT `fk_TARGET_TABLES_FORM_DEFINITION` FOREIGN KEY (`FTM_FD_ID`) REFERENCES `form_definition` (`FD_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `form_table_mapping`
--

/*!40000 ALTER TABLE `form_table_mapping` DISABLE KEYS */;
/*!40000 ALTER TABLE `form_table_mapping` ENABLE KEYS */;


--
-- Definition of table `form_ui`
--

DROP TABLE IF EXISTS `form_ui`;
CREATE TABLE `form_ui` (
  `FU_ID` bigint(20) NOT NULL,
  `FU_FD_ID` bigint(20) DEFAULT NULL,
  `FU_UI_NAME` varchar(255) DEFAULT NULL,
  `FU_PROPERTY_NAME` varchar(255) DEFAULT NULL,
  `FU_PROPERTY_VALUE` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`FU_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `form_ui`
--

/*!40000 ALTER TABLE `form_ui` DISABLE KEYS */;
/*!40000 ALTER TABLE `form_ui` ENABLE KEYS */;


--
-- Definition of table `instance_groups`
--

DROP TABLE IF EXISTS `instance_groups`;
CREATE TABLE `instance_groups` (
  `IG_ID` bigint(20) NOT NULL,
  `IG_FORM_ID` bigint(20) DEFAULT NULL,
  `IG_GROUP_ID` int(11) DEFAULT NULL,
  PRIMARY KEY (`IG_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `instance_groups`
--

/*!40000 ALTER TABLE `instance_groups` DISABLE KEYS */;
/*!40000 ALTER TABLE `instance_groups` ENABLE KEYS */;


--
-- Definition of table `instance_validator`
--

DROP TABLE IF EXISTS `instance_validator`;
CREATE TABLE `instance_validator` (
  `IV_ID` bigint(20) NOT NULL,
  `IV_FORM_ID` bigint(20) DEFAULT NULL,
  `IV_GROUP_ID` int(11) DEFAULT NULL,
  PRIMARY KEY (`IV_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `instance_validator`
--

/*!40000 ALTER TABLE `instance_validator` DISABLE KEYS */;
/*!40000 ALTER TABLE `instance_validator` ENABLE KEYS */;


--
-- Definition of table `instanciation_rules`
--

DROP TABLE IF EXISTS `instanciation_rules`;
CREATE TABLE `instanciation_rules` (
  `IR_ID` bigint(20) NOT NULL,
  `IR_FORM_ID` bigint(20) DEFAULT NULL,
  `IR_MODE` int(11) DEFAULT NULL,
  PRIMARY KEY (`IR_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `instanciation_rules`
--

/*!40000 ALTER TABLE `instanciation_rules` DISABLE KEYS */;
/*!40000 ALTER TABLE `instanciation_rules` ENABLE KEYS */;


--
-- Definition of table `target_table`
--

DROP TABLE IF EXISTS `target_table`;
CREATE TABLE `target_table` (
  `TT_ID` bigint(20) NOT NULL,
  `TT_PHYSICAL_NAME` varchar(255) DEFAULT NULL,
  `TT_DESCRIPTION` varchar(255) DEFAULT NULL,
  `TT_LABEL` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`TT_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `target_table`
--

/*!40000 ALTER TABLE `target_table` DISABLE KEYS */;
/*!40000 ALTER TABLE `target_table` ENABLE KEYS */;




/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
