package com.srms.util;

import com.srms.exception.ValidationException;
import com.srms.model.Student;

import java.util.regex.Pattern;

/** Central place for all input validation rules. */
public final class Validator {

    private static final Pattern ROLL_NO = Pattern.compile("^[A-Za-z0-9-]{1,15}$");
    private static final Pattern NAME    = Pattern.compile("^[A-Za-z][A-Za-z .']{1,49}$");
    private static final Pattern EMAIL   = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)*\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE   = Pattern.compile("^[6-9][0-9]{9}$");

    private Validator() {
    }

    public static void validate(Student s) throws ValidationException {
        if (isBlank(s.getRollNo())) {
            throw new ValidationException("Roll No is required.");
        }
        if (!ROLL_NO.matcher(s.getRollNo()).matches()) {
            throw new ValidationException("Roll No may contain only letters, digits and '-' (max 15 characters).");
        }
        if (isBlank(s.getName())) {
            throw new ValidationException("Name is required.");
        }
        if (!NAME.matcher(s.getName()).matches()) {
            throw new ValidationException("Name must start with a letter and contain only letters, spaces, '.' or ' (2-50 characters).");
        }
        if (isBlank(s.getCourse())) {
            throw new ValidationException("Please select a Course.");
        }
        if (s.getYear() < 1 || s.getYear() > 5) {
            throw new ValidationException("Please select a valid Year.");
        }
        if (isBlank(s.getEmail())) {
            throw new ValidationException("Email is required.");
        }
        if (!EMAIL.matcher(s.getEmail()).matches()) {
            throw new ValidationException("Please enter a valid email address (e.g. name@example.com).");
        }
        if (isBlank(s.getPhone())) {
            throw new ValidationException("Phone is required.");
        }
        if (!PHONE.matcher(s.getPhone()).matches()) {
            throw new ValidationException("Phone must be a 10-digit mobile number starting with 6-9.");
        }
        if (isBlank(s.getGender())) {
            throw new ValidationException("Please select a Gender.");
        }
    }

    public static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
