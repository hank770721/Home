CREATE TABLE `bank_authority` (
  `userId` varchar(20) NOT NULL,
  `accountUserId` varchar(20) NOT NULL,
  `accountId` varchar(4) NOT NULL,
  `orderNumber` varchar(3) DEFAULT NULL,
  PRIMARY KEY (`userId`,`accountUserId`,`accountId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
