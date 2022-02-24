CREATE TABLE `life_temperature` (
  `recordId` varchar(12) NOT NULL,
  `recordDate` varchar(8) DEFAULT NULL,
  `userId` varchar(20) DEFAULT NULL,
  `temperature` decimal(4,2) DEFAULT NULL,
  `enterUserId` varchar(12) DEFAULT NULL,
  `enterDatetime` datetime DEFAULT NULL,
  `updateUserId` varchar(12) DEFAULT NULL,
  `updateDatetime` datetime DEFAULT NULL,
  PRIMARY KEY (`recordId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
