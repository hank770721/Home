CREATE TABLE `inventory_authority` (
  `userId` varchar(20) NOT NULL,
  `stockroomUserId` varchar(12) NOT NULL,
  `stockroomId` varchar(3) NOT NULL,
  PRIMARY KEY (`userId`,`stockroomUserId`,`stockroomId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
