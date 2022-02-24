CREATE TABLE `bank_account` (
  `userId` varchar(20) NOT NULL,
  `id` varchar(4) NOT NULL,
  `bankId` varchar(4) DEFAULT NULL,
  `memo` varchar(100) DEFAULT NULL,
  `isBankAccount` varchar(1) DEFAULT NULL,
  `isSecurities` varchar(1) DEFAULT NULL,
  `isCreditCard` varchar(1) DEFAULT NULL,
  PRIMARY KEY (`userId`,`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
