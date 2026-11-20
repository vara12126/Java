-- Setup script for Task B2: Adversarial Input and SQL Injection Test Database
-- Database: assignment5_testdb
-- NOTE: Local dummy test accounts only; no real user credentials.

CREATE DATABASE IF NOT EXISTS assignment5_testdb;
USE assignment5_testdb;

DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id VARCHAR(50) NOT NULL,
    pwd VARCHAR(100) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role VARCHAR(30) NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Populate dummy test accounts
INSERT INTO users (id, pwd, full_name, role) VALUES
('admin', 'Secret@123', 'Campus Administrator', 'ADMIN'),
('192311094', 'Dhanush@2026', 'Kattiripalli Dhanush', 'STUDENT'),
('faculty01', 'Prof@Pass99', 'Dr. S. Raman', 'FACULTY');
