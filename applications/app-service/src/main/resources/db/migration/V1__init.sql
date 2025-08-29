-- Create target database if not exists (uses Flyway placeholder ${db})
CREATE DATABASE IF NOT EXISTS `${db}` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Create roles table
CREATE TABLE IF NOT EXISTS `${db}`.`roles` (
                                               `role_id` BIGINT NOT NULL AUTO_INCREMENT,
                                               `name` VARCHAR(100) NOT NULL,
    `description` VARCHAR(255) NULL,
    PRIMARY KEY (`role_id`)
    ) ENGINE=InnoDB;

-- Create usuarios table
CREATE TABLE IF NOT EXISTS `${db}`.`users` (
                                                  `id_user` BIGINT NOT NULL AUTO_INCREMENT,
                                                  `name` VARCHAR(100) NOT NULL,
    `last_name` VARCHAR(100) NOT NULL,
    `date_of_birth` VARCHAR(20) NULL,
    `address` VARCHAR(200) NULL,
    `telephone` VARCHAR(50) NULL,
    `email` VARCHAR(150) NULL,
    `base_salary` DECIMAL(15,2) NULL,
    `identity_document` VARCHAR(50) NULL,
    `password` VARCHAR(70) NULL,
    `role_id` BIGINT NULL,
    PRIMARY KEY (`id_user`),
    CONSTRAINT `fk_usuarios_roles` FOREIGN KEY (`role_id`) REFERENCES `${db}`.`roles`(`role_id`)
    ON UPDATE CASCADE ON DELETE SET NULL
    ) ENGINE=InnoDB;

-- Helpful index for FK
CREATE INDEX `idx_usuarios_rol_id` ON `${db}`.`users` (`role_id`);
