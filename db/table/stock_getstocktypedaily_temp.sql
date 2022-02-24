CREATE TABLE `stock_getstocktypedaily_temp` (
  `accountUserId` varchar(20) NOT NULL,
  `accountId` varchar(4) NOT NULL,
  `date` varchar(8) NOT NULL,
  `typeId` char(1) NOT NULL,
  `amount` decimal(8,0) DEFAULT NULL,
  `cost` decimal(8,0) DEFAULT NULL,
  `enterDatetime` datetime DEFAULT NULL,
  PRIMARY KEY (`accountUserId`,`accountId`,`date`,`typeId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
