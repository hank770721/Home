CREATE TABLE `asset_goal` (
  `accountUserId` varchar(40) NOT NULL,
  `accountId` varchar(4) NOT NULL,
  `amount` decimal(7,0) DEFAULT NULL,
  `memo` varchar(10) DEFAULT NULL,
  `enterUserId` varchar(12) DEFAULT NULL,
  `enterDatetime` datetime DEFAULT NULL,
  `updateUserId` varchar(12) DEFAULT NULL,
  `updateDatetime` datetime DEFAULT NULL,
  PRIMARY KEY (`accountUserId`,`accountId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
