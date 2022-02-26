CREATE TABLE `life_expense` (
  `recordId` varchar(14) NOT NULL,
  `recordDate` varchar(8) DEFAULT NULL,
  `transMode` char(1) DEFAULT NULL,
  `accountUserId` varchar(20) DEFAULT NULL,
  `accountId` varchar(4) DEFAULT NULL,
  `memo` varchar(20) DEFAULT NULL,
  `amount` decimal(9,0) DEFAULT NULL,
  `isConsolidation` char(1) DEFAULT NULL,
  `enterUserId` varchar(12) DEFAULT NULL,
  `enterDatetime` datetime DEFAULT NULL,
  `updateUserId` varchar(12) DEFAULT NULL,
  `updateDatetime` datetime DEFAULT NULL,
  PRIMARY KEY (`recordId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
