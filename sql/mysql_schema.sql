-- =====================================================================
--  MySQL schema for Student Record Management System
--  Run in MySQL Workbench or:  mysql -u root -p < sql/mysql_schema.sql
--  (SQLite needs nothing - the app creates the table automatically.)
-- =====================================================================

CREATE DATABASE IF NOT EXISTS student_db;
USE student_db;

CREATE TABLE IF NOT EXISTS students (
    roll_no    VARCHAR(15)  PRIMARY KEY,
    name       VARCHAR(50)  NOT NULL,
    course     VARCHAR(50)  NOT NULL,
    study_year INT          NOT NULL,
    email      VARCHAR(100) NOT NULL UNIQUE,
    phone      VARCHAR(10)  NOT NULL,
    gender     VARCHAR(10)  NOT NULL
);

-- Optional sample data
INSERT IGNORE INTO students VALUES
('CS2025-001', 'Aarav Sharma', 'B.Tech CSE', 1, 'aarav.sharma@college.edu', '9876543210', 'Male'),
('CS2025-002', 'Priya Verma',  'B.Tech CSE', 1, 'priya.verma@college.edu',  '9123456780', 'Female'),
('BCA2024-010', 'Rohan Gupta', 'BCA',        2, 'rohan.gupta@college.edu',  '8765432109', 'Male'),
('MBA2023-005', 'Sneha Iyer',  'MBA',        2, 'sneha.iyer@college.edu',   '7012345678', 'Female');
