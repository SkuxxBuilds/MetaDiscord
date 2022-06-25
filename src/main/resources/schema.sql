CREATE TABLE `discord_players`
(
    `id`         INT NOT NULL AUTO_INCREMENT,
    `uuid`       VARCHAR(36),
    `name`       VARCHAR(32),
    `discord_id` BIGINT,
    PRIMARY KEY (`id`, `uuid`)
);