-- =====================================================================
--  SQLite schema (for reference only - the app creates this automatically
--  in data/students.db on first run).
-- =====================================================================

CREATE TABLE IF NOT EXISTS students (
    roll_no    VARCHAR(15)  PRIMARY KEY,
    name       VARCHAR(50)  NOT NULL,
    course     VARCHAR(50)  NOT NULL,
    study_year INT          NOT NULL,
    email      VARCHAR(100) NOT NULL UNIQUE,
    phone      VARCHAR(10)  NOT NULL,
    gender     VARCHAR(10)  NOT NULL
);
