CREATE TABLE `feedserver`.`Contact` (
  `id` INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  `last_name` VARCHAR(64),
  `first_name` VARCHAR(64),
  `email` VARCHAR(64),
  `rating` INTEGER UNSIGNED,
  PRIMARY KEY (`id`)
) ENGINE = InnoDB CHARACTER SET utf8 COLLATE utf8_general_ci;

ALTER TABLE `feedserver`.`Contact` ADD INDEX `lastNameIndex`(`last_name`),
    ADD INDEX `firstNameIndex`(`first_name`), ADD INDEX `ratingIndex`(`rating`);

INSERT INTO `feedserver`.`Contact`
    (`first_name`, `last_name`, `email`, `rating`)
    values ('Jim', 'Simon', 'jsimon@example.com', 5),
    ('John', 'Doe', 'jdoe@example.com', 10); 
