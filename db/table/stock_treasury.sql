CREATE TABLE `stock_treasury` (
  `accountUserId` varchar(12) NOT NULL,
  `accountId` varchar(4) NOT NULL,
  `assetType` char(1) NOT NULL,
  `stockId` varchar(5) NOT NULL,
  `quantity` decimal(4,0) DEFAULT NULL,
  `amount` decimal(6,0) DEFAULT NULL,
  PRIMARY KEY (`accountUserId`,`accountId`,`assetType`,`stockId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
