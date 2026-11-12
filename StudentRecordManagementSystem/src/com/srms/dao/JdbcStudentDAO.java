package com.srms.dao;

import com.srms.db.DBConnection;
import com.srms.exception.DataAccessException;
import com.srms.exception.DuplicateRecordException;
import com.srms.model.Student;
import com.srms.util.AppConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC implementation (works with SQLite and MySQL).
 * Uses PreparedStatement everywhere to prevent SQL injection.
 */
public class JdbcStudentDAO implements StudentDAO {

    private static final String COLS = "roll_no, name, course, study_year, email, phone, gender";

    @Override
    public void add(Student s) throws DataAccessException, DuplicateRecordException {
        if (findByRollNo(s.getRollNo()) != null) {
            throw new DuplicateRecordException("A student with Roll No '" + s.getRollNo() + "' already exists.");
        }
        checkEmailUnique(s.getEmail(), null);

        String sql = "INSERT INTO students (" + COLS + ") VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, s.getRollNo());
            ps.setString(2, s.getName());
            ps.setString(3, s.getCourse());
            ps.setInt(4, s.getYear());
            ps.setString(5, s.getEmail());
            ps.setString(6, s.getPhone());
            ps.setString(7, s.getGender());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Could not save student: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Student s) throws DataAccessException, DuplicateRecordException {
        checkEmailUnique(s.getEmail(), s.getRollNo());

        String sql = "UPDATE students SET name = ?, course = ?, study_year = ?, email = ?, phone = ?, gender = ?"
                + " WHERE roll_no = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, s.getName());
            ps.setString(2, s.getCourse());
            ps.setInt(3, s.getYear());
            ps.setString(4, s.getEmail());
            ps.setString(5, s.getPhone());
            ps.setString(6, s.getGender());
            ps.setString(7, s.getRollNo());
            if (ps.executeUpdate() == 0) {
                throw new DataAccessException("No student found with Roll No '" + s.getRollNo() + "'.");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not update student: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(String rollNo) throws DataAccessException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM students WHERE roll_no = ?")) {
            ps.setString(1, rollNo);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Could not delete student: " + e.getMessage(), e);
        }
    }

    @Override
    public Student findByRollNo(String rollNo) throws DataAccessException {
        String sql = "SELECT " + COLS + " FROM students WHERE UPPER(roll_no) = UPPER(?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, rollNo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not read student: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Student> findAll() throws DataAccessException {
        return query("SELECT " + COLS + " FROM students ORDER BY roll_no", null, 0);
    }

    @Override
    public List<Student> search(String field, String keyword) throws DataAccessException {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAll();
        }
        String like = "%" + keyword.trim().toUpperCase() + "%";
        String where;
        int params = 1;
        if (FIELD_ROLL.equals(field)) {
            where = "UPPER(roll_no) LIKE ?";
        } else if (FIELD_NAME.equals(field)) {
            where = "UPPER(name) LIKE ?";
        } else if (FIELD_COURSE.equals(field)) {
            where = "UPPER(course) LIKE ?";
        } else {
            where = "UPPER(roll_no) LIKE ? OR UPPER(name) LIKE ? OR UPPER(course) LIKE ?";
            params = 3;
        }
        return query("SELECT " + COLS + " FROM students WHERE " + where + " ORDER BY roll_no", like, params);
    }

    @Override
    public String getStorageName() {
        String url = AppConfig.get("db.url");
        if (url.startsWith("jdbc:sqlite")) return "SQLite (JDBC)";
        if (url.startsWith("jdbc:mysql")) return "MySQL (JDBC)";
        return "Database (JDBC)";
    }

    // ---------- helpers ----------

    private List<Student> query(String sql, String param, int paramCount) throws DataAccessException {
        List<Student> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            for (int i = 1; i <= paramCount; i++) {
                ps.setString(i, param);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not load students: " + e.getMessage(), e);
        }
        return list;
    }

    /** Throws if another student (other than excludeRollNo) already uses this email. */
    private void checkEmailUnique(String email, String excludeRollNo)
            throws DataAccessException, DuplicateRecordException {
        String sql = "SELECT roll_no FROM students WHERE UPPER(email) = UPPER(?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String owner = rs.getString(1);
                    if (excludeRollNo == null || !owner.equalsIgnoreCase(excludeRollNo)) {
                        throw new DuplicateRecordException(
                                "Email '" + email + "' is already used by Roll No " + owner + ".");
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not check email: " + e.getMessage(), e);
        }
    }

    private Student map(ResultSet rs) throws SQLException {
        return new Student(
                rs.getString("roll_no"),
                rs.getString("name"),
                rs.getString("course"),
                rs.getInt("study_year"),
                rs.getString("email"),
                rs.getString("phone"),
                rs.getString("gender"));
    }
}
