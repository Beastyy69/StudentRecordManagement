package com.srms.dao;

import com.srms.exception.DataAccessException;
import com.srms.exception.DuplicateRecordException;
import com.srms.model.Student;

import java.util.List;

/**
 * Data Access Object contract. The UI only talks to this interface,
 * so storage can be switched between JDBC (SQLite/MySQL) and a file
 * without changing any UI code (abstraction + polymorphism).
 */
public interface StudentDAO {

    /** Field names accepted by {@link #search(String, String)}. */
    String FIELD_ALL = "All";
    String FIELD_ROLL = "Roll No";
    String FIELD_NAME = "Name";
    String FIELD_COURSE = "Course";

    void add(Student s) throws DataAccessException, DuplicateRecordException;

    void update(Student s) throws DataAccessException, DuplicateRecordException;

    boolean delete(String rollNo) throws DataAccessException;

    Student findByRollNo(String rollNo) throws DataAccessException;

    List<Student> findAll() throws DataAccessException;

    /** Case-insensitive "contains" search on the given field. */
    List<Student> search(String field, String keyword) throws DataAccessException;

    /** Short label shown in the status bar, e.g. "SQLite (JDBC)". */
    String getStorageName();
}
