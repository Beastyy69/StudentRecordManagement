package com.srms.ui;

import com.srms.model.Student;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/** Registration / edit form with all student fields (GridBagLayout). */
public class StudentFormPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final String SELECT = "-- Select --";
    private static final String[] COURSES = {
            SELECT, "B.Tech CSE", "B.Tech ECE", "B.Tech ME", "B.Tech Civil",
            "BCA", "MCA", "B.Sc", "M.Sc", "B.Com", "BBA", "MBA"
    };
    private static final String[] YEARS = {SELECT, "1", "2", "3", "4", "5"};

    private final JTextField txtRollNo = new JTextField(18);
    private final JTextField txtName = new JTextField(18);
    private final JComboBox<String> cmbCourse = new JComboBox<>(new DefaultComboBoxModel<>(COURSES));
    private final JComboBox<String> cmbYear = new JComboBox<>(YEARS);
    private final JTextField txtEmail = new JTextField(18);
    private final JTextField txtPhone = new JTextField(18);
    private final JRadioButton rbMale = new JRadioButton("Male");
    private final JRadioButton rbFemale = new JRadioButton("Female");
    private final JRadioButton rbOther = new JRadioButton("Other");
    private final ButtonGroup genderGroup = new ButtonGroup();

    public StudentFormPanel() {
        super(new GridBagLayout());
        setBorder(BorderFactory.createTitledBorder("Student Details"));

        genderGroup.add(rbMale);
        genderGroup.add(rbFemale);
        genderGroup.add(rbOther);
        JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        genderPanel.add(rbMale);
        genderPanel.add(rbFemale);
        genderPanel.add(rbOther);

        int row = 0;
        addRow(row++, "Roll No *", txtRollNo);
        addRow(row++, "Name *", txtName);
        addRow(row++, "Course *", cmbCourse);
        addRow(row++, "Year *", cmbYear);
        addRow(row++, "Email *", txtEmail);
        addRow(row++, "Phone *", txtPhone);
        addRow(row, "Gender *", genderPanel);

        txtRollNo.setToolTipText("Letters, digits or '-' (max 15), e.g. CS2025-001");
        txtPhone.setToolTipText("10-digit mobile number");
        txtEmail.setToolTipText("e.g. student@college.edu");
    }

    private void addRow(int row, String label, java.awt.Component field) {
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(5, 6, 5, 6);
        gc.gridy = row;

        gc.gridx = 0;
        gc.anchor = GridBagConstraints.WEST;
        add(new JLabel(label), gc);

        gc.gridx = 1;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1.0;
        add(field, gc);
    }

    /** Builds a Student from the current field values (not yet validated). */
    public Student getStudent() {
        Student s = new Student();
        s.setRollNo(txtRollNo.getText().trim());
        s.setName(txtName.getText().trim().replaceAll("\\s+", " "));
        s.setCourse(cmbCourse.getSelectedIndex() <= 0 ? "" : (String) cmbCourse.getSelectedItem());
        s.setYear(cmbYear.getSelectedIndex() <= 0 ? 0 : Integer.parseInt((String) cmbYear.getSelectedItem()));
        s.setEmail(txtEmail.getText().trim());
        s.setPhone(txtPhone.getText().trim());
        if (rbMale.isSelected()) s.setGender("Male");
        else if (rbFemale.isSelected()) s.setGender("Female");
        else if (rbOther.isSelected()) s.setGender("Other");
        else s.setGender("");
        return s;
    }

    /** Fills the form from an existing record (used when a table row is selected). */
    public void setStudent(Student s) {
        txtRollNo.setText(s.getRollNo());
        txtName.setText(s.getName());

        // Course stored in DB may not be in the default list - add it if needed.
        DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) cmbCourse.getModel();
        if (model.getIndexOf(s.getCourse()) < 0) {
            model.addElement(s.getCourse());
        }
        cmbCourse.setSelectedItem(s.getCourse());
        cmbYear.setSelectedItem(String.valueOf(s.getYear()));
        txtEmail.setText(s.getEmail());
        txtPhone.setText(s.getPhone());

        genderGroup.clearSelection();
        if ("Male".equalsIgnoreCase(s.getGender())) rbMale.setSelected(true);
        else if ("Female".equalsIgnoreCase(s.getGender())) rbFemale.setSelected(true);
        else if ("Other".equalsIgnoreCase(s.getGender())) rbOther.setSelected(true);
    }

    public void clear() {
        txtRollNo.setText("");
        txtName.setText("");
        cmbCourse.setSelectedIndex(0);
        cmbYear.setSelectedIndex(0);
        txtEmail.setText("");
        txtPhone.setText("");
        genderGroup.clearSelection();
        setEditMode(false);
        txtRollNo.requestFocusInWindow();
    }

    /** In edit mode the Roll No (primary key) cannot be changed. */
    public void setEditMode(boolean editing) {
        txtRollNo.setEditable(!editing);
    }
}
