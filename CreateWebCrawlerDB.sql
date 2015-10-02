DROP DATABASE IF EXISTS `WebCrawler`;

CREATE DATABASE IF NOT EXISTS `WebCrawler`;

use `WebCrawler`;

CREATE TABLE IF NOT EXISTS `sources`(
	`id` INT(10) NOT NULL AUTO_INCREMENT,
	`url` VARCHAR(200) NOT NULL UNIQUE,
	PRIMARY KEY(`id`)
);

CREATE TABLE IF NOT EXISTS `frequency_distribution`(
	`id` INT(10) NOT NULL AUTO_INCREMENT,
	`source_id` INT(10) NOT NULL,
	`word` VARCHAR(25) NOT NULL,
	`count` INT NOT NULL,
	PRIMARY KEY (`id`),
	FOREIGN KEY(source_id) REFERENCES sources(id)
);

ALTER TABLE `frequency_distribution` ADD UNIQUE INDEX (word, source_id);

COMMIT;