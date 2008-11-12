CREATE TABLE `sample`.`Contact` (
  `id` INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  `lastName` VARCHAR(64),
  `firstName` VARCHAR(64),
  `email` VARCHAR(64),
  `rating` INTEGER UNSIGNED,
  PRIMARY KEY (`id`)
)
ENGINE = InnoDB
CHARACTER SET utf8 COLLATE utf8_general_ci;

ALTER TABLE `sample`.`Contact` ADD INDEX `lastNameIndex`(`lastName`),
 ADD INDEX `firstNameIndex`(`firstName`),
 ADD INDEX `ratingIndex`(`rating`);
