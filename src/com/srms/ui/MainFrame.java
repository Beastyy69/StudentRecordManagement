package com.srms.ui;

import com.srms.dao.StudentDAO;
import com.srms.exception.DataAccessException;
import com.srms.exception.DuplicateRecordException;
import com.srms.exception.ValidationException;
import com.srms.model.Student;
import com.srms.util.Validator;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableRowSorter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

/** Main dashboard: form on the left, searchable/sortable table on the right. */
public class MainFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private final StudentDAO dao;
    private final StudentFormPanel form = new StudentFormPanel();
    private final StudentTableModel tableModel = new StudentTableModel();
    private final JTable table = new JTable(tableModel);
    private final JTextField txtSearch = new JTextField(20);
    private final JComboBox<String> cmbSearchField = new JComboBox<>(new String[]{
            StudentDAO.FIELD_ALL, StudentDAO.FIELD_ROLL, StudentDAO.FIELD_NAME, StudentDAO.FIELD_COURSE});
    private final JLabel lblStatus = new JLabel(" ");

    /** Roll no of the row currently loaded in the form, or null when adding a new student. */
    private String selectedRollNo;

    public MainFrame(StudentDAO dao, String username) {
        super("Student Record Management System");
        this.dao = dao;

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitApplication();
            }
        });

        setJMenuBar(buildMenuBar());
        buildUI(username);
        refreshTable();

        setMinimumSize(new Dimension(1000, 560));
        setSize(1150, 620);
        setLocationRelativeTo(null);
    }

    // ===================== UI construction =====================

    private void buildUI(String username) {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ---- Header ----
        JLabel header = new JLabel("Student Record Management System");
        header.setFont(new Font("SansSerif", Font.BOLD, 20));
        header.setForeground(new Color(33, 64, 120));
        JLabel welcome = new JLabel("Logged in as: " + username);
        JPanel north = new JPanel(new BorderLayout());
        north.add(header, BorderLayout.WEST);
        north.add(welcome, BorderLayout.EAST);
        root.add(north, BorderLayout.NORTH);

        // ---- Left: form + buttons ----
        JButton btnAdd = new JButton("Add");
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete");
        JButton btnClear = new JButton("Clear");
        JButton btnExit = new JButton("Exit");

        btnAdd.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) { addStudent(); }
        });
        btnUpdate.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) { updateStudent(); }
        });
        btnDelete.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) { deleteStudent(); }
        });
        btnClear.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) { clearForm(); }
        });
        btnExit.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) { exitApplication(); }
        });

        JPanel buttons = new JPanel(new GridLayout(1, 5, 6, 6));
        buttons.add(btnAdd);
        buttons.add(btnUpdate);
        buttons.add(btnDelete);
        buttons.add(btnClear);
        buttons.add(btnExit);

        JPanel west = new JPanel(new BorderLayout(0, 10));
        west.add(form, BorderLayout.NORTH);
        west.add(buttons, BorderLayout.CENTER);
        west.setPreferredSize(new Dimension(420, 0));
        root.add(west, BorderLayout.WEST);

        // ---- Right: search bar + table ----
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        searchBar.add(new JLabel("Search by:"));
        searchBar.add(cmbSearchField);
        searchBar.add(txtSearch);
        JButton btnShowAll = new JButton("Show All");
        searchBar.add(btnShowAll);

        // Live search: filter as the user types.
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { refreshTable(); }
            @Override public void removeUpdate(DocumentEvent e) { refreshTable(); }
            @Override public void changedUpdate(DocumentEvent e) { refreshTable(); }
        });
        cmbSearchField.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) { refreshTable(); }
        });
        btnShowAll.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                cmbSearchField.setSelectedIndex(0);
                txtSearch.setText("");
                refreshTable();
            }
        });

        table.setRowSorter(new TableRowSorter<>(tableModel)); // click headers to sort
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(24);
        table.getTableHeader().setReorderingAllowed(false);
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    loadSelectedRow();
                }
            }
        });

        JPanel center = new JPanel(new BorderLayout(0, 8));
        center.setBorder(BorderFactory.createTitledBorder("All Students (click a row to edit, click a header to sort)"));
        center.add(searchBar, BorderLayout.NORTH);
        center.add(new JScrollPane(table), BorderLayout.CENTER);
        root.add(center, BorderLayout.CENTER);

        // ---- Status bar ----
        lblStatus.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
        root.add(lblStatus, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private JMenuBar buildMenuBar() {
        JMenuBar bar = new JMenuBar();

        JMenu file = new JMenu("File");
        file.setMnemonic(KeyEvent.VK_F);
        JMenuItem miLogout = new JMenuItem("Logout");
        JMenuItem miExit = new JMenuItem("Exit");
        miExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK));
        miLogout.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) { logout(); }
        });
        miExit.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) { exitApplication(); }
        });
        file.add(miLogout);
        file.addSeparator();
        file.add(miExit);

        JMenu records = new JMenu("Records");
        records.setMnemonic(KeyEvent.VK_R);
        JMenuItem miNew = new JMenuItem("New / Clear Form");
        miNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
        JMenuItem miRefresh = new JMenuItem("Refresh");
        miRefresh.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
        JMenuItem miSearch = new JMenuItem("Search");
        miSearch.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK));
        miNew.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) { clearForm(); }
        });
        miRefresh.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) { refreshTable(); }
        });
        miSearch.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) { txtSearch.requestFocusInWindow(); }
        });
        records.add(miNew);
        records.add(miRefresh);
        records.add(miSearch);

        JMenu help = new JMenu("Help");
        help.setMnemonic(KeyEvent.VK_H);
        JMenuItem miAbout = new JMenuItem("About");
        miAbout.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(MainFrame.this,
                        "Student Record Management System\nVersion 1.0\n\n"
                                + "Built with Java Swing + JDBC\nStorage: " + dao.getStorageName(),
                        "About", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        help.add(miAbout);

        bar.add(file);
        bar.add(records);
        bar.add(help);
        return bar;
    }

    // ===================== CRUD actions =====================

    private void addStudent() {
        if (selectedRollNo != null) {
            showWarning("You are editing an existing record. Click 'Clear' first to add a new student.");
            return;
        }
        Student s = form.getStudent();
        try {
            Validator.validate(s);
            dao.add(s);
            showInfo("Student '" + s.getName() + "' added successfully.");
            clearForm();
            refreshTable();
        } catch (ValidationException e) {
            showWarning(e.getMessage());
        } catch (DuplicateRecordException e) {
            showError("Duplicate Entry", e.getMessage());
        } catch (DataAccessException e) {
            showError("Storage Error", e.getMessage());
        }
    }

    private void updateStudent() {
        if (selectedRollNo == null) {
            showWarning("Please select a student from the table to update.");
            return;
        }
        Student s = form.getStudent();
        s.setRollNo(selectedRollNo); // primary key cannot change
        try {
            Validator.validate(s);
            dao.update(s);
            showInfo("Student '" + s.getRollNo() + "' updated successfully.");
            clearForm();
            refreshTable();
        } catch (ValidationException e) {
            showWarning(e.getMessage());
        } catch (DuplicateRecordException e) {
            showError("Duplicate Entry", e.getMessage());
        } catch (DataAccessException e) {
            showError("Storage Error", e.getMessage());
        }
    }

    private void deleteStudent() {
        if (selectedRollNo == null) {
            showWarning("Please select a student from the table to delete.");
            return;
        }
        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete student '" + selectedRollNo + "'?\nThis cannot be undone.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (choice != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            if (dao.delete(selectedRollNo)) {
                showInfo("Student '" + selectedRollNo + "' deleted.");
            } else {
                showWarning("Record was not found - it may already have been deleted.");
            }
            clearForm();
            refreshTable();
        } catch (DataAccessException e) {
            showError("Storage Error", e.getMessage());
        }
    }

    private void clearForm() {
        selectedRollNo = null;
        table.clearSelection();
        form.clear();
    }

    // ===================== helpers =====================

    /** Reloads the table using the current search text and field. */
    private void refreshTable() {
        try {
            String field = (String) cmbSearchField.getSelectedItem();
            List<Student> list = dao.search(field, txtSearch.getText());
            tableModel.setStudents(list);
            lblStatus.setText("Records shown: " + list.size() + "   |   Storage: " + dao.getStorageName());
        } catch (DataAccessException e) {
            showError("Storage Error", e.getMessage());
        }
    }

    private void loadSelectedRow() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            return;
        }
        Student s = tableModel.getStudentAt(table.convertRowIndexToModel(viewRow));
        selectedRollNo = s.getRollNo();
        form.setStudent(s);
        form.setEditMode(true);
    }

    private void logout() {
        int choice = JOptionPane.showConfirmDialog(this, "Do you want to logout?",
                "Logout", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame().setVisible(true);
        }
    }

    private void exitApplication() {
        int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to exit?",
                "Exit", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            dispose();
            System.exit(0);
        }
    }

    private void showInfo(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showWarning(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Invalid Input", JOptionPane.WARNING_MESSAGE);
    }

    private void showError(String title, String msg) {
        JOptionPane.showMessageDialog(this, msg, title, JOptionPane.ERROR_MESSAGE);
    }
}
