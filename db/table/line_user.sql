CREATE TABLE `line_user` (
  `lineId` varchar(40) NOT NULL,
  `userId` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`lineId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
