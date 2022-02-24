CREATE TABLE `bank_accountnotify` (
  `userId` varchar(20) NOT NULL,
  `accountId` varchar(4) NOT NULL,
  `lineId` varchar(40) NOT NULL,
  PRIMARY KEY (`userId`,`accountId`,`lineId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
