package com.srms.ui;

import com.srms.model.Student;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/** Adapts a List&lt;Student&gt; to a JTable. Read-only: edits go through the form. */
public class StudentTableModel extends AbstractTableModel {

    private static final long serialVersionUID = 1L;

    private static final String[] COLUMNS =
            {"Roll No", "Name", "Course", "Year", "Email", "Phone", "Gender"};

    private List<Student> students = new ArrayList<>();

    public void setStudents(List<Student> students) {
        this.students = new ArrayList<>(students);
        fireTableDataChanged();
    }

    public Student getStudentAt(int modelRow) {
        return students.get(modelRow);
    }

    @Override
    public int getRowCount() {
        return students.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMNS.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMNS[column];
    }

    /** Year is an Integer so the column sorts numerically. */
    @Override
    public Class<?> getColumnClass(int column) {
        return column == 3 ? Integer.class : String.class;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    @Override
    public Object getValueAt(int row, int column) {
        Student s = students.get(row);
        switch (column) {
            case 0: return s.getRollNo();
            case 1: return s.getName();
            case 2: return s.getCourse();
            case 3: return s.getYear();
            case 4: return s.getEmail();
            case 5: return s.getPhone();
            case 6: return s.getGender();
            default: return "";
        }
    }
}
