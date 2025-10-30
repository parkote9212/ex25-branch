CREATE TABLE student
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    email      VARCHAR(255) NOT NULL,
    age        INT          NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);