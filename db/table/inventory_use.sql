CREATE TABLE `inventory_use` (
  `recordId` varchar(12) NOT NULL,
  `userId` varchar(12) DEFAULT NULL,
  `recordDate` varchar(8) DEFAULT NULL,
  `purchaseId` varchar(12) DEFAULT NULL,
  `beginDate` varchar(8) DEFAULT NULL,
  `endDate` varchar(8) DEFAULT NULL,
  `isRunOut` char(1) DEFAULT NULL,
  `enterUserId` varchar(20) DEFAULT NULL,
  `enterDatetime` datetime DEFAULT NULL,
  `updateUserId` varchar(20) DEFAULT NULL,
  `updateDatetime` datetime DEFAULT NULL,
  PRIMARY KEY (`recordId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
