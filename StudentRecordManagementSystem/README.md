# Student Record Management System (Java Swing + JDBC)

A desktop application for administrative staff to **add, view, update, delete and search**
student records through a simple graphical interface. Data is stored persistently in
**SQLite / MySQL (via JDBC)**, or in a **file** when no database driver is available.

## Features

| Module        | Description                                                            |
|---------------|------------------------------------------------------------------------|
| Login         | Admin authentication (SHA-256 hashed password, 3-attempt lockout)      |
| Add Student   | Roll No, Name, Course, Year, Email, Phone, Gender                       |
| View Records  | All students in a sortable `JTable` (click any column header)           |
| Update/Delete | Click a row to load it into the form, then Update or Delete             |
| Search        | Live search by Roll No, Name, Course, or All fields                    |
| Clear/Exit    | Reset form; exit/logout with confirmation                              |
| Validation    | Required fields, email/phone format, duplicate Roll No and Email checks |

**Default login:** `admin` / `admin123` (change it in `config/app.properties`).

## Project Structure

```
StudentRecordManagementSystem/
├── README.md
├── build.bat / build.sh            # compile sources into out/
├── run.bat   / run.sh              # build if needed + run
├── config/
│   └── app.properties              # storage type, DB URL, admin credentials
├── lib/
│   └── README.txt                  # put sqlite-jdbc / mysql-connector jar here
├── sql/
│   ├── mysql_schema.sql            # MySQL database + table + sample data
│   └── sqlite_schema.sql           # reference (auto-created by the app)
└── src/com/srms/
    ├── Main.java                   # entry point, sets Look & Feel
    ├── model/
    │   └── Student.java            # POJO (Serializable)
    ├── dao/
    │   ├── StudentDAO.java         # interface (CRUD + search)
    │   ├── JdbcStudentDAO.java     # SQLite/MySQL implementation (PreparedStatement)
    │   ├── FileStudentDAO.java     # file implementation (serialization + LinkedHashMap)
    │   └── DAOFactory.java         # picks JDBC or file storage
    ├── db/
    │   └── DBConnection.java       # JDBC connection + table creation
    ├── service/
    │   └── AuthService.java        # admin login check
    ├── exception/
    │   ├── ValidationException.java
    │   ├── DuplicateRecordException.java
    │   └── DataAccessException.java
    ├── util/
    │   ├── AppConfig.java          # loads config/app.properties
    │   ├── PasswordUtil.java       # SHA-256 hashing (+ hash generator)
    │   └── Validator.java          # input validation rules (regex)
    └── ui/
        ├── LoginFrame.java         # login window
        ├── MainFrame.java          # dashboard: form, buttons, table, menu, search
        ├── StudentFormPanel.java   # registration form (GridBagLayout)
        └── StudentTableModel.java  # JTable model
```

## Requirements

- JDK 8 or higher
- (Optional) SQLite JDBC driver or MySQL Connector/J jar in `lib/`
- Any IDE (NetBeans / Eclipse / IntelliJ IDEA) or just the command line

## How to Run

### Option 1 – Command line
Windows: double-click `run.bat` (or run `build.bat` then `run.bat`).
Linux/macOS: `chmod +x *.sh && ./run.sh`

### Option 2 – IDE (Eclipse / IntelliJ / NetBeans)
1. Create a new Java project and point its source folder at `src/`
   (or copy the `src/com` folder into your project's `src`).
2. Add the jar(s) from `lib/` to the project's build path / libraries.
3. Set the **working directory** to the project root (so `config/` and `data/` are found).
4. Run `com.srms.Main`.

## Storage Options

Configured in `config/app.properties`:

- **SQLite (default):** download `sqlite-jdbc-x.x.x.jar` (and `slf4j-api-x.x.x.jar` for
  newer versions) into `lib/`. The database `data/students.db` and the table are
  created automatically.
- **MySQL:** run `sql/mysql_schema.sql`, put `mysql-connector-j-x.x.x.jar` in `lib/`,
  and switch the `db.*` lines in `app.properties` to the MySQL ones.
- **File:** set `storage.type=file` (or simply have no driver jar with `storage.type=auto`).
  Records are saved to `data/students.dat`.

The current storage is shown in the status bar at the bottom of the main window.

## Validation Rules

| Field   | Rule                                                         |
|---------|--------------------------------------------------------------|
| Roll No | Required, letters/digits/`-`, max 15, unique (cannot edit)   |
| Name    | Required, letters/spaces/`.`/`'`, 2–50 characters            |
| Course  | Must be selected                                             |
| Year    | Must be selected (1–5)                                       |
| Email   | Required, valid format, unique                               |
| Phone   | Required, 10 digits starting with 6–9                        |
| Gender  | Must be selected                                             |

## Changing the Admin Password

```
java -cp out com.srms.util.PasswordUtil MyNewPassword
```
Copy the printed hash into `admin.passwordHash` in `config/app.properties`.

## Java Concepts Demonstrated

- **OOP:** encapsulation (`Student`), abstraction & polymorphism (`StudentDAO` with two
  implementations), factory pattern (`DAOFactory`), inheritance (`JFrame`, `JPanel`,
  `AbstractTableModel`, custom exceptions).
- **Event handling:** `ActionListener`, `DocumentListener` (live search),
  `ListSelectionListener` (row selection), `WindowAdapter` (safe exit).
- **Collections:** `List`, `ArrayList`, `LinkedHashMap`, `Comparator`.
- **Exception handling:** custom checked exceptions, try-with-resources.
- **JDBC:** `DriverManager`, `PreparedStatement`, `ResultSet`, DDL auto-creation.
- **Swing:** JFrame, JPanel, JLabel, JTextField, JPasswordField, JComboBox, JRadioButton,
  ButtonGroup, JButton, JTable, TableRowSorter, JScrollPane, JOptionPane, JMenuBar,
  BorderLayout, GridLayout, GridBagLayout, FlowLayout.
