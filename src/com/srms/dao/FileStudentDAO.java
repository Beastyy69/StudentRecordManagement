package com.srms.dao;

import com.srms.exception.DataAccessException;
import com.srms.exception.DuplicateRecordException;
import com.srms.model.Student;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * File-based implementation using Java serialization.
 * Records are kept in memory in a LinkedHashMap (key = upper-case roll no)
 * and written to disk after every change.
 */
public class FileStudentDAO implements StudentDAO {

    private final File file;
    private final Map<String, Student> students = new LinkedHashMap<>();

    public FileStudentDAO(String path) throws DataAccessException {
        this.file = new File(path);
        load();
    }

    @Override
    public synchronized void add(Student s) throws DataAccessException, DuplicateRecordException {
        String key = key(s.getRollNo());
        if (students.containsKey(key)) {
            throw new DuplicateRecordException("A student with Roll No '" + s.getRollNo() + "' already exists.");
        }
        checkEmailUnique(s.getEmail(), null);
        students.put(key, s);
        save();
    }

    @Override
    public synchronized void update(Student s) throws DataAccessException, DuplicateRecordException {
        String key = key(s.getRollNo());
        if (!students.containsKey(key)) {
            throw new DataAccessException("No student found with Roll No '" + s.getRollNo() + "'.");
        }
        checkEmailUnique(s.getEmail(), s.getRollNo());
        students.put(key, s);
        save();
    }

    @Override
    public synchronized boolean delete(String rollNo) throws DataAccessException {
        boolean removed = students.remove(key(rollNo)) != null;
        if (removed) {
            save();
        }
        return removed;
    }

    @Override
    public synchronized Student findByRollNo(String rollNo) {
        return students.get(key(rollNo));
    }

    @Override
    public synchronized List<Student> findAll() {
        List<Student> list = new ArrayList<>(students.values());
        Collections.sort(list, new Comparator<Student>() {
            @Override
            public int compare(Student a, Student b) {
                return a.getRollNo().compareToIgnoreCase(b.getRollNo());
            }
        });
        return list;
    }

    @Override
    public synchronized List<Student> search(String field, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAll();
        }
        String k = keyword.trim().toLowerCase();
        List<Student> result = new ArrayList<>();
        for (Student s : findAll()) {
            boolean roll = s.getRollNo().toLowerCase().contains(k);
            boolean name = s.getName().toLowerCase().contains(k);
            boolean course = s.getCourse().toLowerCase().contains(k);
            boolean match;
            if (FIELD_ROLL.equals(field)) {
                match = roll;
            } else if (FIELD_NAME.equals(field)) {
                match = name;
            } else if (FIELD_COURSE.equals(field)) {
                match = course;
            } else {
                match = roll || name || course;
            }
            if (match) {
                result.add(s);
            }
        }
        return result;
    }

    @Override
    public String getStorageName() {
        return "File (" + file.getPath() + ")";
    }

    // ---------- helpers ----------

    private String key(String rollNo) {
        return rollNo == null ? "" : rollNo.trim().toUpperCase();
    }

    private void checkEmailUnique(String email, String excludeRollNo) throws DuplicateRecordException {
        for (Student other : students.values()) {
            boolean sameEmail = other.getEmail().equalsIgnoreCase(email);
            boolean sameStudent = excludeRollNo != null && other.getRollNo().equalsIgnoreCase(excludeRollNo);
            if (sameEmail && !sameStudent) {
                throw new DuplicateRecordException(
                        "Email '" + email + "' is already used by Roll No " + other.getRollNo() + ".");
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void load() throws DataAccessException {
        if (!file.exists()) {
            return;
        }
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            List<Student> list = (List<Student>) in.readObject();
            for (Student s : list) {
                students.put(key(s.getRollNo()), s);
            }
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            throw new DataAccessException("Could not read data file " + file.getPath() + ": " + e.getMessage(), e);
        }
    }

    private void save() throws DataAccessException {
        File parent = file.getAbsoluteFile().getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(new ArrayList<>(students.values()));
        } catch (IOException e) {
            throw new DataAccessException("Could not write data file " + file.getPath() + ": " + e.getMessage(), e);
        }
    }
}
