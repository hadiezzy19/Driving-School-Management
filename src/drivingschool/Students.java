package drivingschool;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class Students extends JPanel {

    private JTable studentTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton;
    private JButton predictionButton;

    public Students() {

        createUI();
        loadStudents();
    }

    // ========================================
    // CREATE UI
    // ========================================

    private void createUI() {

        setLayout(
                new BorderLayout(10, 10)
        );

        setBackground(
                UI.LIGHT_GRAY
        );

        setBorder(
                BorderFactory.createEmptyBorder(
                        20, 20, 20, 20
                )
        );

        // ====================================
        // TOP PANEL
        // ====================================

        JPanel topPanel =
                new JPanel(
                        new BorderLayout()
                );

        topPanel.setBackground(
                UI.LIGHT_GRAY
        );

        JLabel title =
                new JLabel("Students");

        UI.styleTitle(title);

        // ====================================
        // SEARCH
        // ====================================

        JPanel searchPanel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT
                        )
                );

        searchPanel.setBackground(
                UI.LIGHT_GRAY
        );

        searchField =
                new JTextField(15);

        UI.styleTextField(
                searchField
        );

        JButton searchButton =
                new JButton("Search");

        UI.styleButton(
                searchButton
        );

        searchPanel.add(
                searchField
        );

        searchPanel.add(
                searchButton
        );

        topPanel.add(
                title,
                BorderLayout.WEST
        );

        topPanel.add(
                searchPanel,
                BorderLayout.EAST
        );

        // ====================================
        // TABLE
        // ====================================

        String[] columns = {
            "Student ID",
            "First Name",
            "Last Name",
            "Phone",
            "Email",
            "Status"
        };

        tableModel =
                new DefaultTableModel(
                        columns,
                        0
                ) {

                    @Override
                    public boolean isCellEditable(
                            int row,
                            int column
                    ) {

                        return false;
                    }
                };

        studentTable =
                new JTable(tableModel);

        studentTable.setFont(
                UI.NORMAL_FONT
        );

        studentTable.setRowHeight(
                30
        );

        studentTable.getTableHeader()
                .setFont(
                        UI.BUTTON_FONT
                );

        JScrollPane scrollPane =
                new JScrollPane(
                        studentTable
                );

        // ====================================
        // BUTTON PANEL
        // ====================================

        JPanel buttonPanel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT
                        )
                );

        buttonPanel.setBackground(
                UI.LIGHT_GRAY
        );

        addButton =
                new JButton("Add Student");

        editButton =
                new JButton("Edit Student");

        deleteButton =
                new JButton("Delete Student");

        predictionButton =
                new JButton("AI Prediction");

        refreshButton =
                new JButton("Refresh");

        UI.styleButton(
                addButton
        );

        UI.styleButton(
                editButton
        );

        UI.styleButton(
                deleteButton
        );

        UI.styleButton(
                predictionButton
        );

        UI.styleButton(
                refreshButton
        );

        buttonPanel.add(
                addButton
        );

        buttonPanel.add(
                editButton
        );

        buttonPanel.add(
                deleteButton
        );

        buttonPanel.add(
                predictionButton
        );

        buttonPanel.add(
                refreshButton
        );

        // ====================================
        // BUTTON ACTIONS
        // ====================================

        searchButton.addActionListener(
                e -> searchStudents()
        );

        refreshButton.addActionListener(
                e -> loadStudents()
        );

        addButton.addActionListener(
                e -> addStudent()
        );

        editButton.addActionListener(
                e -> editStudent()
        );

        deleteButton.addActionListener(
                e -> deleteStudent()
        );

        predictionButton.addActionListener(
                e -> showPrediction()
        );

        // ====================================
        // ADD TO PANEL
        // ====================================

        add(
                topPanel,
                BorderLayout.NORTH
        );

        add(
                scrollPane,
                BorderLayout.CENTER
        );

        add(
                buttonPanel,
                BorderLayout.SOUTH
        );
    }

    // ========================================
    // LOAD STUDENTS
    // ========================================

    private void loadStudents() {

        tableModel.setRowCount(0);

        String sql =
                "SELECT s.StudentID, "
                + "p.FirstName, "
                + "p.LastName, "
                + "p.Phone, "
                + "p.Email, "
                + "s.Status "
                + "FROM Student s "
                + "INNER JOIN Person p "
                + "ON s.PersonID = p.PersonID "
                + "ORDER BY s.StudentID";

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet result =
                        statement.executeQuery()
        ) {

            while (result.next()) {

                tableModel.addRow(
                        new Object[]{
                            result.getInt(
                                    "StudentID"
                            ),

                            result.getString(
                                    "FirstName"
                            ),

                            result.getString(
                                    "LastName"
                            ),

                            result.getString(
                                    "Phone"
                            ),

                            result.getString(
                                    "Email"
                            ),

                            result.getString(
                                    "Status"
                            )
                        }
                );
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Could not load students:\n"
                    + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // ========================================
    // SEARCH STUDENTS
    // ========================================

    private void searchStudents() {

        String search =
                searchField.getText().trim();

        if (search.isEmpty()) {

            loadStudents();

            return;
        }

        tableModel.setRowCount(0);

        String sql =
                "SELECT s.StudentID, "
                + "p.FirstName, "
                + "p.LastName, "
                + "p.Phone, "
                + "p.Email, "
                + "s.Status "
                + "FROM Student s "
                + "INNER JOIN Person p "
                + "ON s.PersonID = p.PersonID "
                + "WHERE p.FirstName LIKE ? "
                + "OR p.LastName LIKE ? "
                + "OR p.Phone LIKE ? "
                + "OR p.Email LIKE ? "
                + "ORDER BY s.StudentID";

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            String value =
                    "%" + search + "%";

            statement.setString(
                    1,
                    value
            );

            statement.setString(
                    2,
                    value
            );

            statement.setString(
                    3,
                    value
            );

            statement.setString(
                    4,
                    value
            );

            ResultSet result =
                    statement.executeQuery();

            while (result.next()) {

                tableModel.addRow(
                        new Object[]{
                            result.getInt(
                                    "StudentID"
                            ),

                            result.getString(
                                    "FirstName"
                            ),

                            result.getString(
                                    "LastName"
                            ),

                            result.getString(
                                    "Phone"
                            ),

                            result.getString(
                                    "Email"
                            ),

                            result.getString(
                                    "Status"
                            )
                        }
                );
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Search failed:\n"
                    + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // ========================================
    // AI PREDICTION
    // ========================================

    private void showPrediction() {

        int row =
                studentTable.getSelectedRow();

        if (row == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a student first.",
                    "AI Prediction",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        int studentID =
                (int) tableModel.getValueAt(
                        row,
                        0
                );

        String firstName =
                String.valueOf(
                        tableModel.getValueAt(
                                row,
                                1
                        )
                );

        String lastName =
                String.valueOf(
                        tableModel.getValueAt(
                                row,
                                2
                        )
                );

        AIPrediction prediction =
                new AIPrediction(
                        this,
                        studentID,
                        firstName + " " + lastName
                );

        prediction.setVisible(true);
    }

    // ========================================
    // ADD STUDENT
    // ========================================

    private void addStudent() {

        JTextField firstNameField =
                new JTextField();

        JTextField lastNameField =
                new JTextField();

        JTextField phoneField =
                new JTextField();

        JTextField emailField =
                new JTextField();

        JTextField addressField =
                new JTextField();

        JTextField dateOfBirthField =
                new JTextField();

        JPanel formPanel =
                new JPanel(
                        new GridLayout(
                                6,
                                2,
                                10,
                                10
                        )
                );

        formPanel.add(
                new JLabel("First Name:")
        );

        formPanel.add(
                firstNameField
        );

        formPanel.add(
                new JLabel("Last Name:")
        );

        formPanel.add(
                lastNameField
        );

        formPanel.add(
                new JLabel("Phone:")
        );

        formPanel.add(
                phoneField
        );

        formPanel.add(
                new JLabel("Email:")
        );

        formPanel.add(
                emailField
        );

        formPanel.add(
                new JLabel("Address:")
        );

        formPanel.add(
                addressField
        );

        formPanel.add(
                new JLabel(
                        "Date of Birth (YYYY-MM-DD):"
                )
        );

        formPanel.add(
                dateOfBirthField
        );

        int result =
                JOptionPane.showConfirmDialog(
                        this,
                        formPanel,
                        "Add Student",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE
                );

        if (result != JOptionPane.OK_OPTION) {

            return;
        }

        String firstName =
                firstNameField.getText().trim();

        String lastName =
                lastNameField.getText().trim();

        String phone =
                phoneField.getText().trim();

        String email =
                emailField.getText().trim();

        String address =
                addressField.getText().trim();

        String dateOfBirth =
                dateOfBirthField.getText().trim();

        // ====================================
        // VALIDATION
        // ====================================

        if (firstName.isEmpty()
                || lastName.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "First Name and Last Name are required.",
                    "Validation",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        // ====================================
        // SQL
        // ====================================

        String personSql =
                "INSERT INTO Person "
                + "(FirstName, LastName, Phone, "
                + "Email, Address, DateOfBirth) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        String studentSql =
                "INSERT INTO Student "
                + "(PersonID, RegistrationDate, Status) "
                + "VALUES (?, GETDATE(), 'Active')";

        Connection connection = null;

        try {

            connection =
                    DBConnection.getConnection();

            if (connection == null) {

                JOptionPane.showMessageDialog(
                        this,
                        "Could not connect to database.",
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE
                );

                return;
            }

            connection.setAutoCommit(
                    false
            );

            // ====================================
            // INSERT PERSON
            // ====================================

            int personID;

            try (
                    PreparedStatement statement =
                            connection.prepareStatement(
                                    personSql,
                                    Statement.RETURN_GENERATED_KEYS
                            )
            ) {

                statement.setString(
                        1,
                        firstName
                );

                statement.setString(
                        2,
                        lastName
                );

                statement.setString(
                        3,
                        phone
                );

                statement.setString(
                        4,
                        email
                );

                statement.setString(
                        5,
                        address
                );

                if (dateOfBirth.isEmpty()) {

                    statement.setNull(
                            6,
                            java.sql.Types.DATE
                    );

                } else {

                    statement.setDate(
                            6,
                            java.sql.Date.valueOf(
                                    dateOfBirth
                            )
                    );
                }

                statement.executeUpdate();

                ResultSet keys =
                        statement.getGeneratedKeys();

                if (!keys.next()) {

                    throw new Exception(
                            "Could not get PersonID."
                    );
                }

                personID =
                        keys.getInt(1);
            }

            // ====================================
            // INSERT STUDENT
            // ====================================

            try (
                    PreparedStatement statement =
                            connection.prepareStatement(
                                    studentSql
                            )
            ) {

                statement.setInt(
                        1,
                        personID
                );

                statement.executeUpdate();
            }

            connection.commit();

            JOptionPane.showMessageDialog(
                    this,
                    "Student added successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            loadStudents();

        } catch (IllegalArgumentException e) {

            if (connection != null) {

                try {
                    connection.rollback();
                } catch (Exception ignored) {
                }
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Invalid date.\n"
                    + "Please use YYYY-MM-DD.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE
            );

        } catch (Exception e) {

            if (connection != null) {

                try {
                    connection.rollback();
                } catch (Exception ignored) {
                }
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Could not add student:\n"
                    + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );

        } finally {

            if (connection != null) {

                try {

                    connection.setAutoCommit(
                            true
                    );

                    connection.close();

                } catch (Exception ignored) {
                }
            }
        }
    }

    // ========================================
    // EDIT STUDENT
    // ========================================

    private void editStudent() {

        int row =
                studentTable.getSelectedRow();

        if (row == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a student first."
            );

            return;
        }

        int studentID =
                (int) tableModel.getValueAt(
                        row,
                        0
                );

        String firstName =
                String.valueOf(
                        tableModel.getValueAt(
                                row,
                                1
                        )
                );

        String lastName =
                String.valueOf(
                        tableModel.getValueAt(
                                row,
                                2
                        )
                );

        String phone =
                String.valueOf(
                        tableModel.getValueAt(
                                row,
                                3
                        )
                );

        String email =
                String.valueOf(
                        tableModel.getValueAt(
                                row,
                                4
                        )
                );

        JTextField firstNameField =
                new JTextField(
                        firstName
                );

        JTextField lastNameField =
                new JTextField(
                        lastName
                );

        JTextField phoneField =
                new JTextField(
                        phone
                );

        JTextField emailField =
                new JTextField(
                        email
                );

        JPanel formPanel =
                new JPanel(
                        new GridLayout(
                                4,
                                2,
                                10,
                                10
                        )
                );

        formPanel.add(
                new JLabel("First Name:")
        );

        formPanel.add(
                firstNameField
        );

        formPanel.add(
                new JLabel("Last Name:")
        );

        formPanel.add(
                lastNameField
        );

        formPanel.add(
                new JLabel("Phone:")
        );

        formPanel.add(
                phoneField
        );

        formPanel.add(
                new JLabel("Email:")
        );

        formPanel.add(
                emailField
        );

        int result =
                JOptionPane.showConfirmDialog(
                        this,
                        formPanel,
                        "Edit Student",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE
                );

        if (result != JOptionPane.OK_OPTION) {

            return;
        }

        String newFirstName =
                firstNameField.getText().trim();

        String newLastName =
                lastNameField.getText().trim();

        String newPhone =
                phoneField.getText().trim();

        String newEmail =
                emailField.getText().trim();

        if (newFirstName.isEmpty()
                || newLastName.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "First Name and Last Name are required.",
                    "Validation",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        String sql =
                "UPDATE Person "
                + "SET FirstName = ?, "
                + "LastName = ?, "
                + "Phone = ?, "
                + "Email = ? "
                + "WHERE PersonID = "
                + "(SELECT PersonID "
                + "FROM Student "
                + "WHERE StudentID = ?)";

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    newFirstName
            );

            statement.setString(
                    2,
                    newLastName
            );

            statement.setString(
                    3,
                    newPhone
            );

            statement.setString(
                    4,
                    newEmail
            );

            statement.setInt(
                    5,
                    studentID
            );

            int updated =
                    statement.executeUpdate();

            if (updated > 0) {

                JOptionPane.showMessageDialog(
                        this,
                        "Student updated successfully.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );

                loadStudents();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Student could not be updated.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Could not update student:\n"
                    + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // ========================================
    // DELETE STUDENT
    // ========================================

    private void deleteStudent() {

        int row =
                studentTable.getSelectedRow();

        if (row == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a student first."
            );

            return;
        }

        // ====================================
        // GET STUDENT ID
        // ====================================

        int studentID =
                (int) tableModel.getValueAt(
                        row,
                        0
                );

        String firstName =
                String.valueOf(
                        tableModel.getValueAt(
                                row,
                                1
                        )
                );

        String lastName =
                String.valueOf(
                        tableModel.getValueAt(
                                row,
                                2
                        )
                );

        // ====================================
        // CONFIRM DELETE
        // ====================================

        int confirmation =
                JOptionPane.showConfirmDialog(
                        this,
                        "Are you sure you want to delete\n"
                        + firstName
                        + " "
                        + lastName
                        + "?",
                        "Delete Student",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

        if (confirmation
                != JOptionPane.YES_OPTION) {

            return;
        }

        // ====================================
        // SQL
        // ====================================

        String getPersonSql =
                "SELECT PersonID "
                + "FROM Student "
                + "WHERE StudentID = ?";

        String deleteStudentSql =
                "DELETE FROM Student "
                + "WHERE StudentID = ?";

        String deletePersonSql =
                "DELETE FROM Person "
                + "WHERE PersonID = ?";

        Connection connection = null;

        try {

            connection =
                    DBConnection.getConnection();

            if (connection == null) {

                JOptionPane.showMessageDialog(
                        this,
                        "Could not connect to database.",
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE
                );

                return;
            }

            // ====================================
            // START TRANSACTION
            // ====================================

            connection.setAutoCommit(
                    false
            );

            // ====================================
            // GET PERSON ID
            // ====================================

            int personID;

            try (
                    PreparedStatement statement =
                            connection.prepareStatement(
                                    getPersonSql
                            )
            ) {

                statement.setInt(
                        1,
                        studentID
                );

                ResultSet result =
                        statement.executeQuery();

                if (!result.next()) {

                    throw new Exception(
                            "Student was not found."
                    );
                }

                personID =
                        result.getInt(
                                "PersonID"
                        );
            }

            // ====================================
            // DELETE STUDENT
            // ====================================

            try (
                    PreparedStatement statement =
                            connection.prepareStatement(
                                    deleteStudentSql
                            )
            ) {

                statement.setInt(
                        1,
                        studentID
                );

                statement.executeUpdate();
            }

            // ====================================
            // DELETE PERSON
            // ====================================

            try (
                    PreparedStatement statement =
                            connection.prepareStatement(
                                    deletePersonSql
                            )
            ) {

                statement.setInt(
                        1,
                        personID
                );

                statement.executeUpdate();
            }

            // ====================================
            // COMMIT
            // ====================================

            connection.commit();

            JOptionPane.showMessageDialog(
                    this,
                    "Student deleted successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            loadStudents();

        } catch (Exception e) {

            // ====================================
            // ROLLBACK
            // ====================================

            if (connection != null) {

                try {
                    connection.rollback();
                } catch (Exception ignored) {
                }
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Could not delete student:\n"
                    + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );

        } finally {

            if (connection != null) {

                try {

                    connection.setAutoCommit(
                            true
                    );

                    connection.close();

                } catch (Exception ignored) {
                }
            }
        }
    }
}