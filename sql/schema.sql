CREATE TABLE `access_log` (
  `date` datetime NOT NULL,
  `ip` varchar(15) NOT NULL,
  `request` varchar(20) NOT NULL,
  `status` int(11) NOT NULL,
  `user_agent` varchar(200) NOT NULL,
  KEY `date` (`date`),
  KEY `ip` (`ip`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `blocked` (
  `ip` varchar(15) NOT NULL,
  `comment` longtext
) ENGINE=InnoDB DEFAULT CHARSET=latin1;