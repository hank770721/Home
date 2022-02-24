CREATE TABLE `stock_assettype` (
  `userId` varchar(20) NOT NULL,
  `accountId` varchar(4) NOT NULL,
  `id` char(1) NOT NULL,
  `name` varchar(2) DEFAULT NULL,
  PRIMARY KEY (`userId`,`id`,`accountId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
