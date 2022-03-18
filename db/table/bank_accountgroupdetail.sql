CREATE TABLE `bank_accountgroupdetail` (
  `userId` varchar(20) NOT NULL,
  `groupId` varchar(4) NOT NULL,
  `accountUserId` varchar(20) NOT NULL,
  `accountId` varchar(4) NOT NULL,
  PRIMARY KEY (`userId`,`groupId`,`accountUserId`,`accountId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
