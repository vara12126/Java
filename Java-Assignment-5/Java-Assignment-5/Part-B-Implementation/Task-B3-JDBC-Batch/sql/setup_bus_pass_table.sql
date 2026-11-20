-- Database Setup for Task B3: Bus Pass JDBC Benchmark
CREATE DATABASE IF NOT EXISTS assignment5_testdb;
USE assignment5_testdb;

DROP TABLE IF EXISTS bus_pass;

CREATE TABLE bus_pass (
    pass_id VARCHAR(20) NOT NULL,
    reg_no VARCHAR(20) NOT NULL,
    name VARCHAR(50) NOT NULL,
    route_no INT NOT NULL,
    amount DOUBLE NOT NULL,
    PRIMARY KEY (pass_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
