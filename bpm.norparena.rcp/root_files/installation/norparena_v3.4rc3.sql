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
-- Create schema norparena
--

CREATE DATABASE IF NOT EXISTS norparena;
USE norparena;

--
-- Definition of table `address`
--

DROP TABLE IF EXISTS `address`;
CREATE TABLE `address` (
  `AD_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `AD_Label` varchar(45) DEFAULT NULL,
  `AD_Address_Type` varchar(45) DEFAULT NULL,
  `AD_Street1` varchar(32) DEFAULT NULL,
  `AD_Street2` varchar(32) DEFAULT NULL,
  `AD_ZipCode` int(10) unsigned DEFAULT NULL,
  `AD_INSEECode` int(10) unsigned DEFAULT NULL,
  `AD_City` varchar(45) DEFAULT NULL,
  `AD_Country` varchar(45) DEFAULT NULL,
  `AD_Bloc` varchar(45) DEFAULT NULL,
  `AD_Arrondissement` varchar(45) DEFAULT NULL,
  `KE_Label` varchar(255) DEFAULT NULL,
  `KE_Folder` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`AD_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=38 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `address`
--

/*!40000 ALTER TABLE `address` DISABLE KEYS */;
INSERT INTO `address` (`AD_ID`,`AD_Label`,`AD_Address_Type`,`AD_Street1`,`AD_Street2`,`AD_ZipCode`,`AD_INSEECode`,`AD_City`,`AD_Country`,`AD_Bloc`,`AD_Arrondissement`,`KE_Label`,`KE_Folder`) VALUES 
 (1,'All Customers','','','',0,0,'','','','',NULL,NULL),
 (2,'Australia','','','',0,0,'','','','',NULL,NULL),
 (3,'Austria','','','',0,0,'','','','',NULL,NULL),
 (4,'Belgium','','','',0,0,'','','','',NULL,NULL),
 (5,'Canada','','','',0,0,'','','','',NULL,NULL),
 (6,'Denmark','','','',0,0,'','','','',NULL,NULL),
 (7,'Finland','','','',0,0,'','','','',NULL,NULL),
 (8,'France','','','',0,0,'','','','',NULL,NULL),
 (9,'Germany','','','',0,0,'','','','',NULL,NULL),
 (10,'Honk Kong','','','',0,0,'','','','',NULL,NULL),
 (11,'Ireland','','','',0,0,'','','','',NULL,NULL),
 (12,'Israel','','','',0,0,'','','','',NULL,NULL),
 (13,'Italy','','','',0,0,'','','','',NULL,NULL),
 (14,'Japan','','','',0,0,'','','','',NULL,NULL),
 (15,'Netherlands','','','',0,0,'','','','',NULL,NULL),
 (16,'New Zealand','','','',0,0,'','','','',NULL,NULL),
 (17,'Norway','','','',0,0,'','','','',NULL,NULL),
 (18,'Philippines','','','',0,0,'','','','',NULL,NULL),
 (19,'Poland','','','',0,0,'','','','',NULL,NULL),
 (20,'Portugal','','','',0,0,'','','','',NULL,NULL),
 (21,'Russia','','','',0,0,'','','','',NULL,NULL),
 (22,'Singapore','','','',0,0,'','','','',NULL,NULL),
 (23,'South Africa','','','',0,0,'','','','',NULL,NULL),
 (24,'Spain','','','',0,0,'','','','',NULL,NULL),
 (25,'Sweden','','','',0,0,'','','','',NULL,NULL),
 (26,'Switzerland','','','',0,0,'','','','',NULL,NULL),
 (27,'UK','','','',0,0,'','','','',NULL,NULL),
 (28,'USA','','','',0,0,'','','','',NULL,NULL),
 (29,'Lille','','','',0,0,'','','','',NULL,NULL),
 (30,'Lyon','','','',0,0,'','','','',NULL,NULL),
 (31,'Marseille','','','',0,0,'','','','',NULL,NULL),
 (32,'Nantes','','','',0,0,'','','','',NULL,NULL),
 (33,'Paris','','','',0,0,'','','','',NULL,NULL),
 (34,'Reims','','','',0,0,'','','','',NULL,NULL),
 (35,'Strasbourg','','','',0,0,'','','','',NULL,NULL),
 (36,'Toulouse','','','',0,0,'','','','',NULL,NULL),
 (37,'Versailles','','','',0,0,'','','','',NULL,NULL);
/*!40000 ALTER TABLE `address` ENABLE KEYS */;


--
-- Definition of table `address_map_definition_relation`
--

DROP TABLE IF EXISTS `address_map_definition_relation`;
CREATE TABLE `address_map_definition_relation` (
  `AMDR_ID` int(11) NOT NULL,
  `AMDR_Address_ID` int(11) DEFAULT NULL,
  `AMDR_Map_Definition_ID` int(11) DEFAULT NULL,
  PRIMARY KEY (`AMDR_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `address_map_definition_relation`
--

/*!40000 ALTER TABLE `address_map_definition_relation` DISABLE KEYS */;
INSERT INTO `address_map_definition_relation` (`AMDR_ID`,`AMDR_Address_ID`,`AMDR_Map_Definition_ID`) VALUES 
 (1,1,1),
 (2,8,2);
/*!40000 ALTER TABLE `address_map_definition_relation` ENABLE KEYS */;


--
-- Definition of table `address_relation`
--

DROP TABLE IF EXISTS `address_relation`;
CREATE TABLE `address_relation` (
  `AR_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `AR_Daddy_ID` int(10) unsigned NOT NULL,
  `AR_Child_ID` int(10) unsigned NOT NULL,
  PRIMARY KEY (`AR_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `address_relation`
--

/*!40000 ALTER TABLE `address_relation` DISABLE KEYS */;
INSERT INTO `address_relation` (`AR_ID`,`AR_Daddy_ID`,`AR_Child_ID`) VALUES 
 (1,1,2),
 (2,1,3),
 (3,1,4),
 (4,1,5),
 (5,1,6),
 (6,1,7),
 (7,1,8),
 (8,1,9),
 (9,1,10),
 (10,1,11),
 (11,1,12),
 (12,1,13),
 (13,1,14),
 (14,1,15),
 (15,1,16),
 (16,1,17),
 (17,1,18),
 (18,1,19),
 (19,1,20),
 (20,1,21),
 (21,1,22),
 (22,1,23),
 (23,1,24),
 (24,1,25),
 (25,1,26),
 (26,1,27),
 (27,1,28),
 (28,8,29),
 (29,8,30),
 (30,8,31),
 (31,8,32),
 (32,8,33),
 (33,8,34),
 (34,8,35),
 (35,8,36),
 (36,8,37);
/*!40000 ALTER TABLE `address_relation` ENABLE KEYS */;


--
-- Definition of table `address_zone`
--

DROP TABLE IF EXISTS `address_zone`;
CREATE TABLE `address_zone` (
  `AZ_ID` int(10) NOT NULL AUTO_INCREMENT,
  `AZ_Address_ID` int(10) NOT NULL,
  `AZ_Map_Zone_ID` int(10) NOT NULL,
  PRIMARY KEY (`AZ_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `address_zone`
--

/*!40000 ALTER TABLE `address_zone` DISABLE KEYS */;
INSERT INTO `address_zone` (`AZ_ID`,`AZ_Address_ID`,`AZ_Map_Zone_ID`) VALUES 
 (1,2,9),
 (2,3,10),
 (3,4,17),
 (4,5,32),
 (5,6,49),
 (6,7,63),
 (7,8,64),
 (8,9,69),
 (9,10,80),
 (10,11,87),
 (11,12,88),
 (12,13,89),
 (13,14,91),
 (14,15,130),
 (15,16,132),
 (16,17,136),
 (17,18,144),
 (18,19,145),
 (19,20,146),
 (20,21,150),
 (21,22,160),
 (22,22,165),
 (23,23,165),
 (24,24,166),
 (25,25,174),
 (26,26,175),
 (27,27,190),
 (28,29,228),
 (29,30,298),
 (30,31,297),
 (31,32,269),
 (32,33,215),
 (33,34,205),
 (34,35,292),
 (35,36,236),
 (36,37,203);
/*!40000 ALTER TABLE `address_zone` ENABLE KEYS */;


--
-- Definition of table `building`
--

DROP TABLE IF EXISTS `building`;
CREATE TABLE `building` (
  `BU_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `BU_Address_ID` int(10) unsigned DEFAULT NULL,
  `BU_Label` varchar(255) DEFAULT NULL,
  `BU_Type` varchar(20) DEFAULT NULL,
  `BU_Latitude` double DEFAULT NULL,
  `BU_Longitude` double DEFAULT NULL,
  `BU_Altitude` double DEFAULT NULL,
  `BU_Surface` double DEFAULT NULL,
  `BU_SizeX` double DEFAULT NULL,
  `BU_SizeY` double DEFAULT NULL,
  `BU_NbFloors` int(10) unsigned DEFAULT NULL,
  `BU_Image_ID` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`BU_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `building`
--

/*!40000 ALTER TABLE `building` DISABLE KEYS */;
/*!40000 ALTER TABLE `building` ENABLE KEYS */;


--
-- Definition of table `building_floor`
--

DROP TABLE IF EXISTS `building_floor`;
CREATE TABLE `building_floor` (
  `BF_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `BF_Label` varchar(255) DEFAULT NULL,
  `BF_Building_ID` int(10) unsigned DEFAULT NULL,
  `BF_Level` int(10) DEFAULT NULL,
  `BF_Height` int(10) unsigned DEFAULT NULL,
  `BF_Image_ID` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`BF_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `building_floor`
--

/*!40000 ALTER TABLE `building_floor` DISABLE KEYS */;
/*!40000 ALTER TABLE `building_floor` ENABLE KEYS */;


--
-- Definition of table `cell`
--

DROP TABLE IF EXISTS `cell`;
CREATE TABLE `cell` (
  `CE_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `CE_Building_ID` int(10) unsigned DEFAULT NULL,
  `CE_Floor_ID` int(10) unsigned DEFAULT NULL,
  `CE_PositionX` double DEFAULT NULL,
  `CE_PositionY` double DEFAULT NULL,
  `CE_Surface` double DEFAULT NULL,
  `CE_Image_ID` int(10) unsigned DEFAULT NULL,
  `CE_Label` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`CE_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `cell`
--

/*!40000 ALTER TABLE `cell` DISABLE KEYS */;
/*!40000 ALTER TABLE `cell` ENABLE KEYS */;


--
-- Definition of table `fusion_map_object`
--

DROP TABLE IF EXISTS `fusion_map_object`;
CREATE TABLE `fusion_map_object` (
  `FMO_ID` bigint(20) NOT NULL,
  `FMO_DESCRIPTION` varchar(255) DEFAULT NULL,
  `FMO_NAME` varchar(255) DEFAULT NULL,
  `FMO_SWF_FILE_NAME` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`FMO_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `fusion_map_object`
--

/*!40000 ALTER TABLE `fusion_map_object` DISABLE KEYS */;
INSERT INTO `fusion_map_object` (`FMO_ID`,`FMO_DESCRIPTION`,`FMO_NAME`,`FMO_SWF_FILE_NAME`) VALUES 
 (1,'WorldCountries','World with countries','FCMap_WorldwithCountries.swf'),
 (2,'FranceMap','FranceMap','FCMap_FranceDepartment.swf'),
 (3,'Asia','Asia','FCMap_Asia.swf'),
 (4,'Belgium','Belgium','FCMap_Belgium.swf'),
 (5,'Europe','Europe','FCMap_Europe.swf'),
 (6,'IleDeFrance','IleDeFrance','FCMap_IledeFrance.swf'),
 (7,'Lyon','Lyon','FCMap_Lyon.swf'),
 (8,'Malaysia','Malaysia','FCMap_Malaysia.swf'),
 (9,'Marseille','Marseille','FCMap_Marseille.swf'),
 (10,'Paris','Paris','FCMap_Paris.swf'),
 (11,'Philippines','Philippines','FCMap_Philippines.swf'),
 (12,'Singapore','Singapore','FCMap_Singapore.swf'),
 (13,'Switzerland','Switzerland','FCMap_Switzerland.swf'),
 (14,'Thailand','Thailand','FCMap_Thailand.swf'),
 (15,'USA','USA','FCMap_USA.swf');
/*!40000 ALTER TABLE `fusion_map_object` ENABLE KEYS */;


--
-- Definition of table `fusion_map_specification_entities`
--

DROP TABLE IF EXISTS `fusion_map_specification_entities`;
CREATE TABLE `fusion_map_specification_entities` (
  `FMSE_ID` bigint(20) NOT NULL,
  `FMSE_FMO_ID` bigint(20) DEFAULT NULL,
  `FMSE_SWF_INTERNAL_ID` varchar(255) DEFAULT NULL,
  `FMSE_LONG_NAME` varchar(255) DEFAULT NULL,
  `FMSE_SHORT_NAME` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`FMSE_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `fusion_map_specification_entities`
--

/*!40000 ALTER TABLE `fusion_map_specification_entities` DISABLE KEYS */;
INSERT INTO `fusion_map_specification_entities` (`FMSE_ID`,`FMSE_FMO_ID`,`FMSE_SWF_INTERNAL_ID`,`FMSE_LONG_NAME`,`FMSE_SHORT_NAME`) VALUES 
 (1,1,'93','Afghanistan','AF '),
 (2,1,'129','Albania','AL '),
 (3,1,'39','Algeria','DZ '),
 (4,1,'130','Andorra','AD '),
 (5,1,'40','Angola','AO '),
 (6,1,'01','Antigua and Barbuda','AG '),
 (7,1,'25','Argentina','AR '),
 (8,1,'94','Armenia','AM '),
 (9,1,'175','Australia','AU '),
 (10,1,'131','Austria','AT '),
 (11,1,'95','Azerbaijan','AZ '),
 (12,1,'02','Bahamas','BS '),
 (13,1,'190','Bahrain','BA '),
 (14,1,'96','Bangladesh','BD '),
 (15,1,'03','Barbados','BB '),
 (16,1,'132','Belarus','BY '),
 (17,1,'133','Belgium','BE '),
 (18,1,'04','Belize','BZ '),
 (19,1,'41','Benin','BJ '),
 (20,1,'97','Bhutan','BT '),
 (21,1,'26','Bolivia','BO '),
 (22,1,'134','Bosnia and Herzegovina','BH '),
 (23,1,'42','Botswana','BW '),
 (24,1,'27','Brazil','BR '),
 (25,1,'98','Brunei','BN '),
 (26,1,'135','Bulgaria','BG '),
 (27,1,'43','Burkina Faso','BF '),
 (28,1,'99','Burma (Myanmar)','MM '),
 (29,1,'44','Burundi','BI '),
 (30,1,'100','Cambodia','KH '),
 (31,1,'45','Cameroon','CM '),
 (32,1,'05','Canada','CA '),
 (33,1,'46','Cape Verde','CV '),
 (34,1,'203','Cayman Islands ','KY '),
 (35,1,'47','Central African Republic','CP '),
 (36,1,'48','Chad','TD '),
 (37,1,'28','Chile','CL '),
 (38,1,'101','China','CN '),
 (39,1,'29','Colombia','CO '),
 (40,1,'49','Comoros','KM '),
 (41,1,'91','Congo','CG '),
 (42,1,'06','Costa Rica','CR '),
 (43,1,'50','Cote d Ivoire','CI '),
 (44,1,'136','Croatia','HY '),
 (45,1,'07','Cuba','CU '),
 (46,1,'172','Cyprus','CY '),
 (47,1,'137','Czech Republic','CZ '),
 (48,1,'51','Democratic Republic of the Congo','CD '),
 (49,1,'138','Denmark','DK '),
 (50,1,'52','Djibouti','DJ '),
 (51,1,'08','Dominica','DM '),
 (52,1,'09','Dominican Rep.','DO '),
 (53,1,'102','East Timor','TP '),
 (54,1,'30','Ecuador','EC '),
 (55,1,'53','Egypt','EG '),
 (56,1,'10','El Salvador','SV '),
 (57,1,'54','Equatorial Guinea','GQ '),
 (58,1,'55','Eritrea','ER '),
 (59,1,'139','Estonia','EE '),
 (60,1,'56','Ethiopia','ET '),
 (61,1,'31','Falkland Islands','FK '),
 (62,1,'176','Fiji','FJ '),
 (63,1,'140','Finland','FI '),
 (64,1,'141','France','FR '),
 (65,1,'32','French Guiana','GF '),
 (66,1,'57','Gabon','GA '),
 (67,1,'90','Gambia','GM '),
 (68,1,'103','Georgia','GE '),
 (69,1,'142','Germany','DE '),
 (70,1,'58','Ghana','GH '),
 (71,1,'143','Greece','GR '),
 (72,1,'24','Greenland','GL '),
 (73,1,'11','Grenada','GD '),
 (74,1,'12','Guatemala','GT '),
 (75,1,'59','Guinea','GN '),
 (76,1,'60','Guinea-Bissau','GW '),
 (77,1,'33','Guyana','GY '),
 (78,1,'13','Haiti','HT '),
 (79,1,'14','Honduras','HN '),
 (80,1,'127','Hong Kong','HK '),
 (81,1,'144','Hungary','HU '),
 (82,1,'145','Iceland','IS '),
 (83,1,'104','India','IN '),
 (84,1,'105','Indonesia','ID '),
 (85,1,'106','Iran','IA '),
 (86,1,'191','Iraq','IZ '),
 (87,1,'146','Ireland','IR '),
 (88,1,'192','Israel','IE '),
 (89,1,'147','Italy','IT '),
 (90,1,'15','Jamaica','JM '),
 (91,1,'107','Japan','JP '),
 (92,1,'193','Jordan','JO '),
 (93,1,'108','Kazakhstan','KZ '),
 (94,1,'61','Kenya','KE '),
 (95,1,'177','Kiribati','KI '),
 (96,1,'109','Korea (north)','KP '),
 (97,1,'110','Korea (south)','KR '),
 (98,1,'194','Kuwait','KU '),
 (99,1,'111','Kyrgyzstan','KG '),
 (100,1,'112','Laos','LA '),
 (101,1,'148','Latvia','LV '),
 (102,1,'195','Lebanon','LB '),
 (103,1,'62','Lesotho','LS '),
 (104,1,'63','Liberia','LI '),
 (105,1,'64','Libya','LR '),
 (106,1,'149','Liechtenstein','LN '),
 (107,1,'150','Lithuania','LT '),
 (108,1,'151','Luxembourg','LU '),
 (109,1,'128','Macau','MO '),
 (110,1,'152','Macedonia','MK '),
 (111,1,'65','Madagascar','MS '),
 (112,1,'66','Malawi','MW '),
 (113,1,'113','Malaysia','MY '),
 (114,1,'67','Mali','ML '),
 (115,1,'153','Malta','MT '),
 (116,1,'178','Marshall Islands','MH '),
 (117,1,'68','Mauritania','MR '),
 (118,1,'92','Mauritius','MI '),
 (119,1,'16','Mexico','MX '),
 (120,1,'179','Micronesia','FM '),
 (121,1,'154','Moldova','MV '),
 (122,1,'155','Monaco','MC '),
 (123,1,'114','Mongolia','MN '),
 (124,1,'156','Montenegro','MG '),
 (125,1,'69','Morocco','MA '),
 (126,1,'70','Mozambique','MZ '),
 (127,1,'71','Namibia','NA '),
 (128,1,'180','Nauru','NR '),
 (129,1,'115','Nepal','NP '),
 (130,1,'157','Netherlands','NL '),
 (131,1,'189','New Caledonia','NC '),
 (132,1,'181','New Zealand','NZ '),
 (133,1,'17','Nicaragua','NI '),
 (134,1,'72','Niger','NE '),
 (135,1,'73','Nigeria','NG '),
 (136,1,'158','Norway','NO '),
 (137,1,'196','Oman','OM '),
 (138,1,'116','Pakistan','PK '),
 (139,1,'182','Palau','PW '),
 (140,1,'18','Panama','PA '),
 (141,1,'183','Papua New Guinea','PG '),
 (142,1,'34','Paraguay','PY '),
 (143,1,'35','Peru','PE '),
 (144,1,'117','Philippines','PH '),
 (145,1,'159','Poland','PL '),
 (146,1,'160','Portugal','PT '),
 (147,1,'202','Puerto Rico','PR '),
 (148,1,'197','Qatar','QA '),
 (149,1,'161','Romania','RO '),
 (150,1,'118','Russia','RU '),
 (151,1,'74','Rwanda','RW '),
 (152,1,'184','Samoa','WS '),
 (153,1,'162','San Marino','SM '),
 (154,1,'75','Sao Tome and Principe','ST '),
 (155,1,'198','Saudi Arabia','SA '),
 (156,1,'76','Senegal','SN '),
 (157,1,'163','Serbia','CS '),
 (158,1,'77','Seycelles','SC '),
 (159,1,'78','Sierra Leone','SL '),
 (160,1,'119','Singapore','SG '),
 (161,1,'164','Slovakia','SK '),
 (162,1,'165','Slovenia','SI '),
 (163,1,'185','Solomon Islands','SB '),
 (164,1,'79','Somalia','SO '),
 (165,1,'80','South Africa','ZA '),
 (166,1,'166','Spain','ES '),
 (167,1,'120','Sri Lanka','LK '),
 (168,1,'19','St. Kitts & Nevis','KN '),
 (169,1,'20','St. Lucia','LC '),
 (170,1,'21','St. Vincent & the Grenadines','VC '),
 (171,1,'81','Sudan','SD '),
 (172,1,'36','Suriname','SR '),
 (173,1,'82','Swaziland','SZ '),
 (174,1,'167','Sweden','SE '),
 (175,1,'168','Switzerland','CH '),
 (176,1,'199','Syria','SY '),
 (177,1,'126','Taiwan','TW '),
 (178,1,'121','Tajikistan','TJ '),
 (179,1,'83','Tanzania','TZ '),
 (180,1,'122','Thailand','TH '),
 (181,1,'84','Togo','TG '),
 (182,1,'186','Tonga','TO '),
 (183,1,'22','Trinidad & Tobago','TT '),
 (184,1,'85','Tunisia','TN '),
 (185,1,'173','Turkey','TK '),
 (186,1,'123','Turkmenistan','TM '),
 (187,1,'187','Tuvalu','TV '),
 (188,1,'86','Uganda','UG '),
 (189,1,'169','Ukraine','UA '),
 (190,1,'170','United Kingdom','UK '),
 (191,1,'23','United States','US '),
 (192,1,'200','UnitedArabEmirates','AE '),
 (193,1,'37','Uruguay','UY '),
 (194,1,'124','Uzbekistan','UZ '),
 (195,1,'188','Vanuatu','VU '),
 (196,1,'171','Vatican City ','VA '),
 (197,1,'38','Venezuela','VE '),
 (198,1,'125','Vietnam','VN '),
 (199,1,'87','Western Sahara','WA '),
 (200,1,'201','Yemen','YM '),
 (201,1,'88','Zambia','ZM '),
 (202,1,'89','Zimbabwe','ZW '),
 (203,2,'FR.YV','Yvelines','YV'),
 (204,2,'FR.MY','Mayenne','MY'),
 (205,2,'FR.MR','Marne','MR'),
 (206,2,'FR.OR','Orne','OR'),
 (207,2,'FR.LC','Loir et Cher','LC'),
 (208,2,'FR.SS','Seine-Saint-Denis','SS'),
 (209,2,'FR.SE','Seine et Marne','SE'),
 (210,2,'FR.LD','Landes','LD'),
 (211,2,'FR.AB','Aube','AB'),
 (212,2,'FR.IN','Indre ','IN'),
 (213,2,'FR.IL','Indre et Loire','IL'),
 (214,2,'FR.VR','Var','VR'),
 (215,2,'FR.VP','Ville de Paris','VP'),
 (216,2,'FR.SM','Seine Maritime','SM'),
 (217,2,'FR.HS','Haute Savoie','HS'),
 (218,2,'FR.HL','Haute Loire','HL'),
 (219,2,'FR.CS','Corse du Sud','CS'),
 (220,2,'FR.EU','Eure','EU'),
 (221,2,'FR.VC','Vaucluse','VC'),
 (222,2,'FR.CT','Charente','CT'),
 (223,2,'FR.YO','Yonne','YO'),
 (224,2,'FR.AV','Aveyron','AV'),
 (225,2,'FR.DS','Deux Sevres','DS'),
 (226,2,'FR.ML','Maine et Loire','ML'),
 (227,2,'FR.VO','Val d\'Oise','VO'),
 (228,2,'FR.NO','Nord','NO'),
 (229,2,'FR.JU','Jura','JU'),
 (230,2,'FR.CO','Cote d\'Or','CO'),
 (231,2,'FR.LZ','Lozere','LZ'),
 (232,2,'FR.VN','Vienne','VN'),
 (233,2,'FR.SV','Savoie','SV'),
 (234,2,'FR.MB','Morbihan','MB'),
 (235,2,'FR.MM','Meurthe et Moselle','MM'),
 (236,2,'FR.HG','Haute Garonne','HG'),
 (237,2,'FR.LO','Lot','LO'),
 (238,2,'FR.DM','Drome','DM'),
 (239,2,'FR.GA','Gard','GA'),
 (240,2,'FR.TG','Tarn et Garonne','TG'),
 (241,2,'FR.FI','Finistere','FI'),
 (242,2,'FR.HV','Haute Vienne','HV'),
 (243,2,'FR.HA','Hautes Alpes','HA'),
 (244,2,'FR.HD','Hauts-de-Seine','HD'),
 (245,2,'FR.AN','Ardennes','AN'),
 (246,2,'FR.AD','Aude','AD'),
 (247,2,'FR.GI','Gironde','GI'),
 (248,2,'FR.OI','Oise','OI'),
 (249,2,'FR.CV','Calvados','CV'),
 (250,2,'FR.DB','Doubs','DB'),
 (251,2,'FR.LG','Lot et Garonne','LG'),
 (252,2,'FR.GE','Gers','GE'),
 (253,2,'FR.VD','Vendee','VD'),
 (254,2,'FR.PC','Pas de Calais','PC'),
 (255,2,'FR.HE','Herault','HE'),
 (256,2,'FR.ES','Essonne','ES'),
 (257,2,'FR.CH','Cher','CH'),
 (258,2,'FR.DD','Dordogne','DD'),
 (259,2,'FR.IV','Ille et Vilaine','IV'),
 (260,2,'FR.AH','Ardeche','AH'),
 (261,2,'FR.VG','Vosges','VG'),
 (262,2,'FR.HR','Haut Rhin','HR'),
 (263,2,'FR.HC','Haute Corse','HC'),
 (264,2,'FR.TB','Territoire de Belfort','TB'),
 (265,2,'FR.AP','Alpes de Haute Provence','AP'),
 (266,2,'FR.HN','Haute Saone','HN'),
 (267,2,'FR.CA','Cotes d\'Armor','CA'),
 (268,2,'FR.MH','Manche','MH'),
 (269,2,'FR.LA','Loire Atlantique','LA'),
 (270,2,'FR.LT','Loiret','LT'),
 (271,2,'FR.NI','Nievre','NI'),
 (272,2,'FR.HP','Hautes Pyrenees','HP'),
 (273,2,'FR.CR','Creuse','CR'),
 (274,2,'FR.TA','Tarn','TA'),
 (275,2,'FR.SL','Saone et Loire','SL'),
 (276,2,'FR.EL','Eure et Loir','EL'),
 (277,2,'FR.IS','Isere','IS'),
 (278,2,'FR.LR','Loire','LR'),
 (279,2,'FR.CM','Charente Maritime','CM'),
 (280,2,'FR.CL','Cantal','CL'),
 (281,2,'FR.MO','Moselle','MO'),
 (282,2,'FR.AS','Aisne','AS'),
 (283,2,'FR.AL','Allier','AL'),
 (284,2,'FR.PO','Pyrenees Orientales','PO'),
 (285,2,'FR.AM','Alpes Maritimes','AM'),
 (286,2,'FR.SO','Somme','SO'),
 (287,2,'FR.HM','Haute Marne ','HM'),
 (288,2,'FR.ST','Sarthe','ST'),
 (289,2,'FR.PD','Puy de Dome','PD'),
 (290,2,'FR.PA','Pyrenees Atlantiques','PA'),
 (291,2,'FR.MS','Meuse','MS'),
 (292,2,'FR.BR','Bas Rhin','BR'),
 (293,2,'FR.CZ','Correze','CZ'),
 (294,2,'FR.AG','Ariege','AG'),
 (295,2,'FR.AI','Ain','AI'),
 (296,2,'FR.VM','Val-de-Marne','VM'),
 (297,2,'FR.BD','Bouches du Rhone','BD'),
 (298,2,'FR.RH','Rhone','RH'),
 (299,3,'042','Thailand','TH'),
 (300,3,'044','Turkmenistan','TM'),
 (301,3,'015','Indonesia','ID'),
 (302,3,'033','Pakistan','PK'),
 (303,3,'001','Afghanistan','AF'),
 (304,3,'030','Mongolia','MN'),
 (305,3,'047','Vietnam','VN'),
 (306,3,'023','Korea (south)','KR'),
 (307,3,'026','Laos','LA'),
 (308,3,'013','Georgia','GE'),
 (309,3,'022','Korea (north)','KP'),
 (310,3,'012','East Timor','TP'),
 (311,3,'036','Russian Federation','RU'),
 (312,3,'007','Brunei','BN'),
 (313,3,'008','Burma (Myanmar)','MM'),
 (314,3,'041','Tajikistan','TJ'),
 (315,3,'050','Hong Kong','HK'),
 (316,3,'021','Kazakhstan','KZ'),
 (317,3,'031','Nepal','NP'),
 (318,3,'009','Cambodia','KH'),
 (319,3,'014','India','IN'),
 (320,3,'003','Azerbaijan','AZ'),
 (321,3,'051','Macau','MO'),
 (322,3,'046','Uzbekistan','UZ'),
 (323,3,'038','Singapore','SG'),
 (324,3,'019','Japan','JP'),
 (325,3,'039','Sri Lanka','LK'),
 (326,3,'005','Bangladesh','BD'),
 (327,3,'002','Armenia','AM'),
 (328,3,'025','Kyrgyzstan','KG'),
 (329,3,'028','Malaysia','MY'),
 (330,3,'006','Bhutan','BT'),
 (331,3,'049','Taiwan','TW'),
 (332,3,'034','Philippines','PH'),
 (333,3,'016','Iran','IR'),
 (334,3,'010','China','CN'),
 (335,4,'05','Luxembourg','LU'),
 (336,4,'08','West-Vlaanderen','WV'),
 (337,4,'06','Namur','NA'),
 (338,4,'07','Oost-Vlaanderen','OV'),
 (339,4,'03','Liege','LI'),
 (340,4,'04','Limburg','LM'),
 (341,4,'010','Brussels','BR'),
 (342,4,'011','Vlaams-Brabant','VB'),
 (343,4,'01','Antwerpen','AN'),
 (344,4,'09','Brabant Wallon','BW'),
 (345,4,'02','Hainaut','HA'),
 (346,5,'011','Estonia','EE '),
 (347,5,'010','Denmark','DK '),
 (348,5,'038','Spain','ES '),
 (349,5,'015','Greece','GR '),
 (350,5,'020','Latvia','LV '),
 (351,5,'034','San Marino','SM '),
 (352,5,'031','Poland','PL '),
 (353,5,'024','Macedonia','MK '),
 (354,5,'014','Germany','DE '),
 (355,5,'028','Montenegro','MO '),
 (356,5,'003','Austria','AT '),
 (357,5,'004','Belarus','BY '),
 (358,5,'029','Netherlands','NL '),
 (359,5,'023','Luxembourg','LU '),
 (360,5,'013','France','FR '),
 (361,5,'025','Malta','MT '),
 (362,5,'001','Albania','AL '),
 (363,5,'035','Serbia','CS '),
 (364,5,'033','Romania','RO '),
 (365,5,'018','Ireland','IE '),
 (366,5,'044','Cyprus','CY '),
 (367,5,'026','Moldova','MD '),
 (368,5,'042','United Kingdom','UK '),
 (369,5,'007','dotBulgaria','BG '),
 (370,5,'027','Monaco','MC '),
 (371,5,'016','Hungary','HU '),
 (372,5,'039','Sweden','SE '),
 (373,5,'012','Finland','FI '),
 (374,5,'045','Turkey','TK '),
 (375,5,'005','Belgium','BE '),
 (376,5,'041','Ukraine','UA '),
 (377,5,'032','Portugal','PT '),
 (378,5,'017','Iceland','IS '),
 (379,5,'002','Andorra','AD '),
 (380,5,'021','Liechtenstein','LI '),
 (381,5,'008','Croatia','HY '),
 (382,5,'043','Vatican City ','VA '),
 (383,5,'040','Switzerland','CH '),
 (384,5,'022','Lithuania','LT '),
 (385,5,'019','Italy','IT '),
 (386,5,'037','Slovenia','SL '),
 (387,5,'009','Czech Republic','CZ '),
 (388,5,'036','Slovakia','SK '),
 (389,5,'006','Bosnia and Herzegovina','BA '),
 (390,5,'046','Russia','RU '),
 (391,5,'030','Norway','NO '),
 (392,6,'FR.HS','Hauts de Seine','HS'),
 (393,6,'FR.VM','Val de Marne','VM'),
 (394,6,'FR.PA','Paris','PA'),
 (395,6,'FR.SM','Seine et Marne','SM'),
 (396,6,'FR.ES','Essonne','ES'),
 (397,6,'FR.YV','Yvelines','YV'),
 (398,6,'FR.SD','Seine Saint Denis','SD'),
 (399,6,'FR.VO','Val d\'Oise','VO'),
 (400,7,'FR.LY.8e','8th Arrondissement','8e'),
 (401,7,'FR.LY.2e','2nd Arrondissement','2e'),
 (402,7,'FR.LY.9e','9th Arrondissement','9e'),
 (403,7,'FR.LY.1e','1st Arrondissement','1e'),
 (404,7,'FR.LY.7e','7th Arrondissement','7e'),
 (405,7,'FR.LY.3e','3rd Arrondissement','3e'),
 (406,7,'FR.LY.6e','6th Arrondissement','6e'),
 (407,7,'FR.LY.5e','5th Arrondissement','5e'),
 (408,7,'FR.LY.4e','4th Arrondissement','4e'),
 (409,8,'006','Pahang','PA'),
 (410,8,'011','Sarawak','SR'),
 (411,8,'001','Johor','JO'),
 (412,8,'016','Labuan','LA'),
 (413,8,'009','Pulau Pinang','PP'),
 (414,8,'008','Perlis','PR'),
 (415,8,'003','Kelantan','KL'),
 (416,8,'005','Negeri Sembilan','NS'),
 (417,8,'015','Kuala Lumpur','KL'),
 (418,8,'017','Putrajaya.','PU'),
 (419,8,'002','Kedah','KE'),
 (420,8,'007','Perak','PE'),
 (421,8,'013','Terengganu','TE'),
 (422,8,'010','Sabah','SA'),
 (423,8,'012','Selangor','SL'),
 (424,8,'004','Melaka','ME'),
 (425,9,'FR.MS.III','3rd Arrondissement','III'),
 (426,9,'FR.MS.XVI','16th Arrondissement','XVI'),
 (427,9,'FR.MS.IX','9th Arrondissement','IX'),
 (428,9,'FR.MS.VI','6th Arrondissement','VI'),
 (429,9,'FR.MS.IV','4th Arrondissement','IV'),
 (430,9,'FR.MS.V','5th Arrondissement','V'),
 (431,9,'FR.MS.I','1st Arrondissement','I'),
 (432,9,'FR.MS.XI','11th Arrondissement','XI'),
 (433,9,'FR.MS.XV','15th Arrondissement','XV'),
 (434,9,'FR.MS.XIV','14t Arrondissement','XIV'),
 (435,9,'FR.MS.XIII','13th Arrondissement','XIII'),
 (436,9,'FR.MS.II','2nd Arrondissement','II'),
 (437,9,'FR.MS.VIII','8th Arrondissement','VIII'),
 (438,9,'FR.MS.X','10th Arrondissement','X'),
 (439,9,'FR.MS.XII','12th Arrondissement','XII'),
 (440,9,'FR.MS.VII','7th Arrondissement','VII'),
 (441,10,'FR.PR.RU','Reuilly','RU'),
 (442,10,'FR.PR.OB','Observatoire','OB'),
 (443,10,'FR.PR.MT','Menilmontant','MT'),
 (444,10,'FR.PR.LU','Luxembourg','LU'),
 (445,10,'FR.PR.GB','Gobelins','GB'),
 (446,10,'FR.PR.HV','Hotel de ville','HV'),
 (447,10,'FR.PR.PB','Palais Bourbon','PB'),
 (448,10,'FR.PR.PS','Passy','PS'),
 (449,10,'FR.PR.PA','Pantheon','PA'),
 (450,10,'FR.PR.BM','Batignolles Monceau','BM'),
 (451,10,'FR.PR.TM','Temple','TM'),
 (452,10,'FR.PR.BT','Butte Montmartre','BT'),
 (453,10,'FR.PR.EL','Elysee','EL'),
 (454,10,'FR.PR.PC','Popincourt','PC'),
 (455,10,'FR.PR.BC','Buttes Chaumont','BC'),
 (456,10,'FR.PR.VG','Vaugirard','VG'),
 (457,10,'FR.PR.OP','Opera','OP'),
 (458,10,'FR.PR.BO','Bourse','BO'),
 (459,10,'FR.PR.ES','Enclos St Laurent','ES'),
 (460,10,'FR.PR.LV','Louvre','LV'),
 (461,11,'PH.LG','Laguna','LG'),
 (462,11,'PH.SK','Sultan Kudarat','SK'),
 (463,11,'PH.ES','Eastern Samar','ES'),
 (464,11,'PH.AQ','Antique','AQ'),
 (465,11,'PH.QR','Quirino','QR'),
 (466,11,'PH.BK','Bukidnon','BK'),
 (467,11,'PH.CG','Cagayan','CG'),
 (468,11,'PH.ND','Negros Occidental','ND'),
 (469,11,'PH.LS','Lanao del Sur','LS'),
 (470,11,'PH.LE','Leyte','LE'),
 (471,11,'PH.AS','Agusan del Sur','AS'),
 (472,11,'PH.QZ','Quezon','QZ'),
 (473,11,'PH.PL','Palawan','PL'),
 (474,11,'PH.ZM','Zambales','ZM'),
 (475,11,'PH.CB','Cebu','CB'),
 (476,11,'PH.NS','Northern Samar','NS'),
 (477,11,'PH.BO','Bohol','BO'),
 (478,11,'PH.SS','Surigao del Sur','SS'),
 (479,11,'PH.SQ','Siquijor','SQ'),
 (480,11,'PH.SL','Southern Leyte','SL'),
 (481,11,'PH.BA','Bataan','BA'),
 (482,11,'PH.IF','Ifugao','IF'),
 (483,11,'PH.NC','Cotabato','NC'),
 (484,11,'PH.LU','La Union','LU'),
 (485,11,'PH.BS','Basilan','BS'),
 (486,11,'PH.SF','Shariff Kabunsuan','SF'),
 (487,11,'PH.MQ','Marinduque','MQ'),
 (488,11,'PH.MR','Oriental Mindoro','MR'),
 (489,11,'PH.SC','South Cotabato','SC'),
 (490,11,'PH.IN','Ilocos Norte','IN'),
 (491,11,'PH.IS','Ilocos Sur','IS'),
 (492,11,'PH.MA','Maguindanao','MA'),
 (493,11,'PH.NR','Negros Oriental','NR'),
 (494,11,'PH.SG','Sarangani','SG'),
 (495,11,'PH.ZY','Zamboanga Sibugay','ZY'),
 (496,11,'PH.AN','Agusan del Norte','AN'),
 (497,11,'PH.BG','Benguet','BG'),
 (498,11,'PH.KA','Kalinga','KA'),
 (499,11,'PH.AU','Aurora','AU'),
 (500,11,'PH.AK','Aklan','AK'),
 (501,11,'PH.NE','Nueva Ecija','NE'),
 (502,11,'PH.AP','Apayao','AP'),
 (503,11,'PH.ZN','Zamboanga del Norte','ZN'),
 (504,11,'PH.ZS','Zamboanga del Sur','ZS'),
 (505,11,'PH.MC','Occidental Mindoro','MC'),
 (506,11,'PH.RI','Rizal','RI'),
 (507,11,'PH.AL','Albay','AL'),
 (508,11,'PH.BI','Biliran','BI'),
 (509,11,'PH.SM','Samar','SM'),
 (510,11,'PH.IB','Isabela','IB'),
 (511,11,'PH.AB','Abra','AB'),
 (512,11,'PH.RO','Romblon','RO'),
 (513,11,'PH.GU','Guimaras','GU'),
 (514,11,'PH.CL','Compostela Valley','CL'),
 (515,11,'PH.II','Iloilo','II'),
 (516,11,'PH.MD','Misamis Occidental','MD'),
 (517,11,'PH.CV','Cavite','CV'),
 (518,11,'PH.BU','Bulacan','BU'),
 (519,11,'PH.LN','Lanao del Norte','LN'),
 (520,11,'PH.TT','Tawi-Tawi','TT'),
 (521,11,'PH.SU','Sulu','SU'),
 (522,11,'PH.DO','Davao Oriental','DO'),
 (523,11,'PH.DS','Davao del Sur','DS'),
 (524,11,'PH.PM','Pampanga','PM'),
 (525,11,'PH.SR','Sorsogon','SR'),
 (526,11,'PH.CN','Camarines Norte','CN'),
 (527,11,'PH.DV','Davao del Norte','DV'),
 (528,11,'PH.CP','Capiz','CP'),
 (529,11,'PH.TR','Tarlac','TR'),
 (530,11,'PH.MT','Mountain Province','MT'),
 (531,11,'PH.MB','Masbate','MB'),
 (532,11,'PH.BT','Batangas','BT'),
 (533,11,'PH.CT','Catanduanes','CT'),
 (534,11,'PH.NV','Nueva Vizcaya','NV'),
 (535,11,'PH.PN','Pangasinan','PN'),
 (536,11,'PH.CS','Camarines Sur','CS'),
 (537,11,'PH.CM','Camiguin','CM'),
 (538,11,'PH.MN','Misamis Oriental','MN'),
 (539,11,'PH.ST','Surigao del Norte','ST'),
 (540,12,'SG.WE','West Singapore','WE'),
 (541,12,'SG.CS','Central Singapore','CS'),
 (542,12,'SG.EA','East Singapore','EA'),
 (543,12,'SG.NO','North Singapore','NO'),
 (544,12,'SG.SO','South Singapore','SO'),
 (545,13,'16','Schaffhausen','SC'),
 (546,13,'09','Graubünden ','GR'),
 (547,13,'01','Aargau','AA'),
 (548,13,'08','Glarus','GL'),
 (549,13,'13','Nidwalden','NI'),
 (550,13,'15','Sankt Gallen','SG'),
 (551,13,'26','Jura','JU'),
 (552,13,'21','Uri','UR'),
 (553,13,'24','Zug','ZU'),
 (554,13,'06','Fribourg','FR'),
 (555,13,'20','Ticino','TI'),
 (556,13,'18','Solothurn','SO'),
 (557,13,'25','Zurich','ZR'),
 (558,13,'07','Geneve','GE'),
 (559,13,'02','Ausser-Rhoden','AR'),
 (560,13,'14','Obwalden','OB'),
 (561,13,'10','Inner-Rhoden','IR'),
 (562,13,'11','Luzern','LU'),
 (563,13,'12','Neuchâtel','NE'),
 (564,13,'19','Thurgau','TH'),
 (565,13,'22','Valais','VA'),
 (566,13,'05','Bern','BE'),
 (567,13,'03','Basel-Landschaft','BL'),
 (568,13,'23','Vaud','VU'),
 (569,13,'04','Basel-Stadt','BS'),
 (570,13,'17','Schwyz','SH'),
 (571,14,'TH.UN','Udon Thani','UN'),
 (572,14,'TH.NB','Nong Bua Lam Phu','NB'),
 (573,14,'TH.PI','Pattani','PI'),
 (574,14,'TH.NN','Nakhon Nayok','NN'),
 (575,14,'TH.SO','Sukhothai','SO'),
 (576,14,'TH.NO','Nonthaburi','NO'),
 (577,14,'TH.SG','Songkhla','SG'),
 (578,14,'TH.ST','Surat Thani','ST'),
 (579,14,'TH.MD','Mukdahan','MD'),
 (580,14,'TH.AT','Ang Thong','AT'),
 (581,14,'TH.RN','Ranong','RN'),
 (582,14,'TH.PU','Phuket','PU'),
 (583,14,'TH.RT','Ratchaburi','RT'),
 (584,14,'TH.CN','Chai Nat','CN'),
 (585,14,'TH.SI','Si Sa Ket','SI'),
 (586,14,'TH.CC','Chachoengsao','CC'),
 (587,14,'TH.PL','Phatthalung','PL'),
 (588,14,'TH.LB','Lop Buri','LB'),
 (589,14,'TH.NA','Nan','NA'),
 (590,14,'TH.TK','Tak','TK'),
 (591,14,'TH.KP','Kamphaeng Phet','KP'),
 (592,14,'TH.PR','Phrae','PR'),
 (593,14,'TH.TG','Trang','TG'),
 (594,14,'TH.AC','Amnat Charoen','AC'),
 (595,14,'TH.LG','Lampang','LG'),
 (596,14,'TH.NP','Nakhon Pathom','NP'),
 (597,14,'TH.NS','Nakhon Sawan','NS'),
 (598,14,'TH.LN','Lamphun','LN'),
 (599,14,'TH.BR','Buri Ram','BR'),
 (600,14,'TH.MS','Maha Sarakham','MS'),
 (601,14,'TH.KN','Kanchanaburi','KN'),
 (602,14,'TH.LE','Loei','LE'),
 (603,14,'TH.NW','Narathiwat','NW'),
 (604,14,'TH.SR','Saraburi','SR'),
 (605,14,'TH.TT','Trat','TT'),
 (606,14,'TH.SM','Samut Songkhram','SM'),
 (607,14,'TH.MH','Mae Hong Son','MH'),
 (608,14,'TH.CR','Chiang Rai','CR'),
 (609,14,'TH.SK','Sa Kaeo','SK'),
 (610,14,'TH.PK','Prachuap Khiri Khan','PK'),
 (611,14,'TH.UT','Uthai Thani','UT'),
 (612,14,'TH.RY','Rayong','RY'),
 (613,14,'TH.CT','Chanthaburi','CT'),
 (614,14,'TH.NR','Nakhon Ratchasima','NR'),
 (615,14,'TH.CP','Chumphon','CP'),
 (616,14,'TH.KK','Khon Kaen','KK'),
 (617,14,'TH.NK','Nong Khai','NK'),
 (618,14,'TH.PC','Phichit','PC'),
 (619,14,'TH.SH','Suphan Buri','SH'),
 (620,14,'TH.NF','Nakhon Phanom','NF'),
 (621,14,'TH.PY','Phayao','PY'),
 (622,14,'TH.SU','Surin','SU'),
 (623,14,'TH.SP','Samut Prakan','SP'),
 (624,14,'TH.SN','Sakon Nakhon','SN'),
 (625,14,'TH.KL','Kalasin','KL'),
 (626,14,'TH.SB','Sing Buri','SB'),
 (627,14,'TH.SS','Samut Sakhon','SS'),
 (628,14,'TH.CB','Chon Buri','CB'),
 (629,14,'TH.NT','Nakhon Si Thammarat','NT'),
 (630,14,'TH.KR','Krabi','KR'),
 (631,14,'TH.PS','Phitsanulok','PS'),
 (632,14,'TH.YS','Yasothon','YS'),
 (633,14,'TH.UR','Ubon Ratchathani','UR'),
 (634,14,'TH.UD','Uttaradit','UD'),
 (635,14,'TH.SA','Satun','SA'),
 (636,14,'TH.YL','Yala','YL'),
 (637,14,'TH.PT','Pathum Thani','PT'),
 (638,14,'TH.PG','Phangnga','PG'),
 (639,14,'TH.CY','Chaiyaphum','CY'),
 (640,14,'TH.CM','Chiang Mai','CM'),
 (641,14,'TH.PE','Phetchaburi','PE'),
 (642,14,'TH.RE','Roi Et','RE'),
 (643,14,'TH.PH','Phetchabun','PH'),
 (644,15,'AL','Alabama','AL'),
 (645,15,'CT','Connecticut','CT'),
 (646,15,'NH','New Hampshire','NH'),
 (647,15,'MS','Mississippi','MS'),
 (648,15,'MI','Michigan','MI'),
 (649,15,'AR','Arkansas','AR'),
 (650,15,'ME','Maine','ME'),
 (651,15,'KY','Kentucky','KY'),
 (652,15,'HI','Hawaii','HI'),
 (653,15,'WI','Wisconsin','WI'),
 (654,15,'TN','Tennessee','TN'),
 (655,15,'OR','Oregon','OR'),
 (656,15,'KS','Kansas','KS'),
 (657,15,'NM','New Mexico','NM'),
 (658,15,'PA','Pennsylvania','PA'),
 (659,15,'RI','Rhode Island','RI'),
 (660,15,'NJ','New Jersey','NJ'),
 (661,15,'WA','Washington','WA'),
 (662,15,'TX','Texas','TX'),
 (663,15,'OK','Oklahoma','OK'),
 (664,15,'WV','West Virginia','WV'),
 (665,15,'IL','Illinois','IL'),
 (666,15,'MA','Massachusetts','MA'),
 (667,15,'DC','District of Columbia','DC'),
 (668,15,'CA','California','CA'),
 (669,15,'NE','Nebraska','NE'),
 (670,15,'AZ','Arizona','AZ'),
 (671,15,'GA','Georgia','GA'),
 (672,15,'SC','South Carolina','SC'),
 (673,15,'IA','Iowa','IA'),
 (674,15,'NV','Nevada','NV'),
 (675,15,'LA','Louisiana','LA'),
 (676,15,'SD','South Dakota','SD'),
 (677,15,'IN','Indiana','IN'),
 (678,15,'MD','Maryland','MD'),
 (679,15,'MO','Missouri','MO'),
 (680,15,'UT','Utah','UT'),
 (681,15,'OH','Ohio','OH'),
 (682,15,'FL','Florida','FL'),
 (683,15,'AK','Alaska','AK'),
 (684,15,'NC','North Carolina','NC'),
 (685,15,'NY','New York','NY'),
 (686,15,'DE','Delaware','DE'),
 (687,15,'MN','Minnesota','MN'),
 (688,15,'ID','Idaho','ID'),
 (689,15,'MT','Montana','MT'),
 (690,15,'CO','Colorado','CO'),
 (691,15,'VA','Virginia','VA'),
 (692,15,'VT','Vermont','VT'),
 (693,15,'ND','North Dakota','ND'),
 (694,15,'WY','Wyoming','WY');
/*!40000 ALTER TABLE `fusion_map_specification_entities` ENABLE KEYS */;


--
-- Definition of table `images`
--

DROP TABLE IF EXISTS `images`;
CREATE TABLE `images` (
  `IM_ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `IM_Image_Item_ID` int(11) NOT NULL,
  `IM_Image_Repository_ID` int(11) DEFAULT NULL,
  PRIMARY KEY (`IM_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `images`
--

/*!40000 ALTER TABLE `images` DISABLE KEYS */;
/*!40000 ALTER TABLE `images` ENABLE KEYS */;


--
-- Definition of table `kml_object`
--

DROP TABLE IF EXISTS `kml_object`;
CREATE TABLE `kml_object` (
  `KO_ID` int(11) NOT NULL,
  `KO_File_Name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`KO_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `kml_object`
--

/*!40000 ALTER TABLE `kml_object` DISABLE KEYS */;
/*!40000 ALTER TABLE `kml_object` ENABLE KEYS */;


--
-- Definition of table `kml_specification_entities`
--

DROP TABLE IF EXISTS `kml_specification_entities`;
CREATE TABLE `kml_specification_entities` (
  `KSE_ID` int(11) NOT NULL AUTO_INCREMENT,
  `KSE_Kml_Object_ID` int(10) unsigned NOT NULL,
  `KSE_Placemark_ID` varchar(255) NOT NULL,
  `KSE_Placemark_Type` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`KSE_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `kml_specification_entities`
--

/*!40000 ALTER TABLE `kml_specification_entities` DISABLE KEYS */;
/*!40000 ALTER TABLE `kml_specification_entities` ENABLE KEYS */;


--
-- Definition of table `map_definition_relation`
--

DROP TABLE IF EXISTS `map_definition_relation`;
CREATE TABLE `map_definition_relation` (
  `MDR_ID` int(11) NOT NULL,
  `MDR_Daddy_ID` int(11) DEFAULT NULL,
  `MDR_Child_ID` int(11) DEFAULT NULL,
  PRIMARY KEY (`MDR_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `map_definition_relation`
--

/*!40000 ALTER TABLE `map_definition_relation` DISABLE KEYS */;
/*!40000 ALTER TABLE `map_definition_relation` ENABLE KEYS */;


--
-- Definition of table `maps_definition`
--

DROP TABLE IF EXISTS `maps_definition`;
CREATE TABLE `maps_definition` (
  `MD_ID` int(11) NOT NULL,
  `MD_Label` varchar(255) DEFAULT NULL,
  `MD_Description` varchar(255) DEFAULT NULL,
  `MD_Map_Type` varchar(45) DEFAULT NULL,
  `MD_Kml_Object_ID` int(11) DEFAULT NULL,
  `MD_Fusion_Map_Object_ID` mediumtext,
  PRIMARY KEY (`MD_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `maps_definition`
--

/*!40000 ALTER TABLE `maps_definition` DISABLE KEYS */;
INSERT INTO `maps_definition` (`MD_ID`,`MD_Label`,`MD_Description`,`MD_Map_Type`,`MD_Kml_Object_ID`,`MD_Fusion_Map_Object_ID`) VALUES 
 (1,'WorldMap','World Map',NULL,NULL,'1'),
 (2,'France',NULL,NULL,NULL,'2');
/*!40000 ALTER TABLE `maps_definition` ENABLE KEYS */;




/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
