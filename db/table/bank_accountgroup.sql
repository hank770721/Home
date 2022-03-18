CREATE TABLE `bank_accountgroup` (
  `userId` varchar(20) NOT NULL,
  `groupId` varchar(4) NOT NULL,
  `groupName` varchar(4) DEFAULT NULL,
  PRIMARY KEY (`userId`,`groupId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
