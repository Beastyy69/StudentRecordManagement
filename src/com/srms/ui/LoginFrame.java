package com.srms.ui;

import com.srms.dao.DAOFactory;
import com.srms.dao.StudentDAO;
import com.srms.exception.DataAccessException;
import com.srms.service.AuthService;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

/** Admin login screen. Locks the app after 3 failed attempts. */
public class LoginFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final int MAX_ATTEMPTS = 3;

    private final JTextField txtUsername = new JTextField(18);
    private final JPasswordField txtPassword = new JPasswordField(18);
    private final AuthService authService = new AuthService();
    private int failedAttempts = 0;

    public LoginFrame() {
        super("Student Record Management System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        buildUI();
        pack();
        setLocationRelativeTo(null);
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(0, 10));
        root.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel title = new JLabel("Admin Login", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(new Color(33, 64, 120));
        root.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 6, 6, 6);
        gc.anchor = GridBagConstraints.WEST;

        gc.gridx = 0; gc.gridy = 0;
        form.add(new JLabel("Username:"), gc);
        gc.gridx = 1;
        form.add(txtUsername, gc);

        gc.gridx = 0; gc.gridy = 1;
        form.add(new JLabel("Password:"), gc);
        gc.gridx = 1;
        form.add(txtPassword, gc);
        root.add(form, BorderLayout.CENTER);

        JButton btnLogin = new JButton("Login");
        JButton btnExit = new JButton("Exit");
        JPanel buttons = new JPanel();
        buttons.add(btnLogin);
        buttons.add(btnExit);
        root.add(buttons, BorderLayout.SOUTH);

        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doLogin();
            }
        });
        btnExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        getRootPane().setDefaultButton(btnLogin); // Enter key = Login
        setContentPane(root);
    }

    private void doLogin() {
        String user = txtUsername.getText().trim();
        char[] pass = txtPassword.getPassword();

        if (user.isEmpty() || pass.length == 0) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password.",
                    "Missing Information", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean ok = authService.login(user, pass);
        Arrays.fill(pass, '\0'); // wipe password from memory

        if (!ok) {
            failedAttempts++;
            int left = MAX_ATTEMPTS - failedAttempts;
            if (left <= 0) {
                JOptionPane.showMessageDialog(this, "Too many failed attempts. The application will close.",
                        "Access Denied", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
            JOptionPane.showMessageDialog(this, "Invalid username or password.\nAttempts left: " + left,
                    "Login Failed", JOptionPane.ERROR_MESSAGE);
            txtPassword.setText("");
            txtPassword.requestFocusInWindow();
            return;
        }

        try {
            StudentDAO dao = DAOFactory.create();
            new MainFrame(dao, user).setVisible(true);
            dispose();
        } catch (DataAccessException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Storage Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
