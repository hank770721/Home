CREATE TABLE `stock_getstocktype_temp` (
  `accountUserId` varchar(20) NOT NULL,
  `accountId` varchar(4) NOT NULL,
  `month` varchar(6) NOT NULL,
  `typeId` char(1) NOT NULL,
  `typeName` varchar(2) DEFAULT NULL,
  `stock` decimal(8,0) DEFAULT NULL,
  `cost` decimal(8,0) DEFAULT NULL,
  `dividend` decimal(8,0) DEFAULT NULL,
  `profit` decimal(8,0) DEFAULT NULL,
  `enterDatetime` datetime DEFAULT NULL,
  PRIMARY KEY (`accountUserId`,`accountId`,`month`,`typeId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
