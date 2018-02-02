CREATE TABLE `achievements` (
  `ID` int(11) NOT NULL,
  `UUID` varchar(36) NOT NULL,
  `achievement` varchar(100) NOT NULL,
  `date` bigint(20) NOT NULL,
  `messageSent` tinyint(1) NOT NULL DEFAULT '1'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `blocks` (
  `ID` int(11) NOT NULL,
  `type` varchar(30) NOT NULL,
  `data` smallint(6) NOT NULL,
  `displayname` varchar(200) DEFAULT NULL,
  `price` double NOT NULL,
  `old_price` double NOT NULL DEFAULT '0',
  `order_i` int(11) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `bot_coins` (
  `ID` int(11) NOT NULL,
  `UUID` varchar(36) NOT NULL,
  `coins` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `economy` (
  `ID` int(11) NOT NULL,
  `UUID` varchar(36) NOT NULL,
  `amount` double NOT NULL DEFAULT '40'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `games` (
  `ID` int(11) NOT NULL,
  `game_id` int(11) NOT NULL,
  `map_id` int(11) NOT NULL,
  `word_id` int(11) NOT NULL,
  `plot_id` varchar(10) NOT NULL,
  `request_bot` tinyint(1) NOT NULL DEFAULT '0',
  `players_count` int(11) NOT NULL,
  `game_type` smallint(5) NOT NULL,
  `date` bigint(20) NOT NULL,
  `know_count` int(11) NOT NULL,
  `plot_type` int(11) NOT NULL,
  `game_length` int(11) NOT NULL,
  `openTime` bigint(20) NOT NULL DEFAULT '-1'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `game_players` (
  `ID` int(11) NOT NULL,
  `game_id` int(11) NOT NULL,
  `UUID` varchar(36) NOT NULL,
  `player_type` int(11) NOT NULL,
  `npc_id` int(11) NOT NULL,
  `know_time` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `playerblocks` (
  `ID` int(11) NOT NULL,
  `UUID` varchar(36) NOT NULL,
  `block_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `playerbonuses` (
  `ID` int(11) NOT NULL,
  `UUID` varchar(36) NOT NULL,
  `bonuseid` int(50) NOT NULL,
  `amount` int(11) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `playereffects` (
  `ID` int(11) NOT NULL,
  `UUID` varchar(36) NOT NULL,
  `effect_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `playereffectschoose` (
  `ID` int(11) NOT NULL,
  `UUID` varchar(36) NOT NULL,
  `view_id` int(11) NOT NULL,
  `effect_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `playereffectviews` (
  `ID` int(11) NOT NULL,
  `UUID` varchar(36) NOT NULL,
  `view_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `players` (
  `ID` int(20) NOT NULL,
  `UUID` varchar(36) NOT NULL,
  `Name` varchar(16) NOT NULL,
  `hotbar_items` varchar(200) DEFAULT NULL,
  `firstLogin` bigint(20) NOT NULL DEFAULT '-1',
  `lastLogin` bigint(20) NOT NULL DEFAULT '-1',
  `playTime` bigint(20) NOT NULL DEFAULT '-1',
  `LastIp` varchar(15) DEFAULT NULL,
  `loginTimes` int(11) NOT NULL DEFAULT '-1',
  `hebrewWords` tinyint(1) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `playerstats` (
  `UUID` varchar(36) NOT NULL,
  `gameType` int(11) NOT NULL,
  `totalGames` int(11) NOT NULL,
  `builder` int(11) NOT NULL,
  `normal` int(11) NOT NULL,
  `know` int(11) NOT NULL,
  `knowFirst` int(11) NOT NULL,
  `allKnow` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `reports` (
  `ID` int(11) NOT NULL,
  `reporterUUID` varchar(36) NOT NULL,
  `gameId` int(11) NOT NULL,
  `reason` text NOT NULL,
  `plotId` varchar(10) NOT NULL,
  `date` bigint(20) NOT NULL,
  `checked` tinyint(1) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `words` (
  `ID` int(11) NOT NULL,
  `word_english` varchar(300) NOT NULL,
  `word_hebrew` varchar(300) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


ALTER TABLE `achievements`
  ADD PRIMARY KEY (`ID`),
  ADD KEY `UUID` (`UUID`);

ALTER TABLE `blocks`
  ADD PRIMARY KEY (`ID`);

ALTER TABLE `bot_coins`
  ADD PRIMARY KEY (`ID`),
  ADD KEY `UUID` (`UUID`);

ALTER TABLE `economy`
  ADD PRIMARY KEY (`ID`),
  ADD KEY `UUID` (`UUID`);

ALTER TABLE `games`
  ADD PRIMARY KEY (`ID`);

ALTER TABLE `game_players`
  ADD PRIMARY KEY (`ID`);

ALTER TABLE `playerblocks`
  ADD PRIMARY KEY (`ID`),
  ADD KEY `UUID` (`UUID`);

ALTER TABLE `playerbonuses`
  ADD PRIMARY KEY (`UUID`,`bonuseid`),
  ADD UNIQUE KEY `ID` (`ID`),
  ADD KEY `UUID` (`UUID`);

ALTER TABLE `playereffects`
  ADD PRIMARY KEY (`ID`),
  ADD KEY `UUID` (`UUID`);

ALTER TABLE `playereffectschoose`
  ADD PRIMARY KEY (`UUID`,`view_id`),
  ADD UNIQUE KEY `ID` (`ID`),
  ADD KEY `UUID` (`UUID`);

ALTER TABLE `playereffectviews`
  ADD PRIMARY KEY (`ID`),
  ADD KEY `UUID` (`UUID`);

ALTER TABLE `players`
  ADD PRIMARY KEY (`ID`),
  ADD UNIQUE KEY `UUID_2` (`UUID`),
  ADD KEY `UUID` (`UUID`);

ALTER TABLE `playerstats`
  ADD PRIMARY KEY (`UUID`,`gameType`);

ALTER TABLE `reports`
  ADD PRIMARY KEY (`ID`);

ALTER TABLE `words`
  ADD PRIMARY KEY (`ID`);


ALTER TABLE `blocks`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT;
ALTER TABLE `economy`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT;
ALTER TABLE `games`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT;
ALTER TABLE `game_players`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT;
ALTER TABLE `playerblocks`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT;
ALTER TABLE `playerbonuses`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT;
ALTER TABLE `playereffects`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT;
ALTER TABLE `playereffectschoose`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT;
ALTER TABLE `playereffectviews`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT;
ALTER TABLE `players`
  MODIFY `ID` int(20) NOT NULL AUTO_INCREMENT;
ALTER TABLE `words`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT;
