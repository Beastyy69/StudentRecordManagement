package com.srms.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Plain model (POJO) representing one student record.
 * Implements Serializable so the file-based storage can persist it.
 */
public class Student implements Serializable {

    private static final long serialVersionUID = 1L;

    private String rollNo;
    private String name;
    private String course;
    private int year;
    private String email;
    private String phone;
    private String gender;

    public Student() {
    }

    public Student(String rollNo, String name, String course, int year,
                   String email, String phone, String gender) {
        this.rollNo = rollNo;
        this.name = name;
        this.course = course;
        this.year = year;
        this.email = email;
        this.phone = phone;
        this.gender = gender;
    }

    public String getRollNo() { return rollNo; }
    public void setRollNo(String rollNo) { this.rollNo = rollNo; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    /** Two students are the same record if their roll numbers match (case-insensitive). */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student)) return false;
        Student other = (Student) o;
        return rollNo != null && rollNo.equalsIgnoreCase(other.rollNo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rollNo == null ? null : rollNo.toUpperCase());
    }

    @Override
    public String toString() {
        return rollNo + " - " + name + " (" + course + ", Year " + year + ")";
    }
}
