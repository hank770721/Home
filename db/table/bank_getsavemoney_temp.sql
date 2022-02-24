CREATE TABLE `bank_getsavemoney_temp` (
  `accountUserId` varchar(20) NOT NULL,
  `accountId` varchar(4) NOT NULL,
  `month` varchar(6) NOT NULL,
  `amount` decimal(8,0) DEFAULT NULL,
  `enterDatetime` datetime DEFAULT NULL,
  PRIMARY KEY (`accountUserId`,`month`,`accountId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
