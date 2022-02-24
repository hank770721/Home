CREATE TABLE `inventory_authority` (
  `userId` varchar(20) NOT NULL,
  `dataUserId` varchar(20) NOT NULL,
  PRIMARY KEY (`userId`,`dataUserId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
