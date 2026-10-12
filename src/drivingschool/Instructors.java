package drivingschool;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

public class Instructors extends JPanel {

    private JTable instructorTable;
    private DefaultTableModel tableModel;

    private JTextField searchField;

    // ========================================
    // CONSTRUCTOR
    // ========================================

    public Instructors() {

        createUI();
        loadInstructors();
    }

    // ========================================
    // CREATE UI
    // ========================================

    private void createUI() {

        setLayout(new BorderLayout());
        setBackground(UI.LIGHT_GRAY);

        setBorder(
                BorderFactory.createEmptyBorder(
                        30, 30, 30, 30
                )
        );

        // ========================================
        // TOP SECTION
        // ========================================

        JPanel topPanel =
                new JPanel(new BorderLayout());

        topPanel.setBackground(UI.LIGHT_GRAY);

        JLabel title =
                new JLabel("Instructors");

        UI.styleTitle(title);

        topPanel.add(
                title,
                BorderLayout.WEST
        );

        // ========================================
        // SEARCH SECTION
        // ========================================

        JPanel searchPanel =
                new JPanel(new FlowLayout(
                        FlowLayout.RIGHT,
                        10,
                        0
                ));

        searchPanel.setBackground(UI.LIGHT_GRAY);

        searchField =
                new JTextField();

        searchField.setPreferredSize(
                new Dimension(220, 35)
        );

        UI.styleTextField(searchField);

        JButton searchButton =
                new JButton("Search");

        UI.styleButton(searchButton);

        JButton refreshButton =
                new JButton("Refresh");

        UI.styleButton(refreshButton);

        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(refreshButton);

        topPanel.add(
                searchPanel,
                BorderLayout.EAST
        );

        add(
                topPanel,
                BorderLayout.NORTH
        );

        // ========================================
        // TABLE
        // ========================================

        String[] columns = {
            "Instructor ID",
            "First Name",
            "Last Name",
            "Phone",
            "Email",
            "Hire Date",
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

        instructorTable =
                new JTable(tableModel);

        instructorTable.setFont(
                UI.NORMAL_FONT
        );

        instructorTable.setRowHeight(32);

        instructorTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        instructorTable.getTableHeader()
                .setFont(
                        new Font(
                                "Segoe UI",
                                Font.BOLD,
                                13
                        )
                );

        instructorTable.getTableHeader()
                .setPreferredSize(
                        new Dimension(0, 35)
                );

        JScrollPane scrollPane =
                new JScrollPane(instructorTable);

        scrollPane.setBorder(
                BorderFactory.createLineBorder(
                        UI.BORDER
                )
        );

        add(
                scrollPane,
                BorderLayout.CENTER
        );

        // ========================================
        // BOTTOM BUTTONS
        // ========================================

        JPanel bottomPanel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT,
                                10,
                                15
                        )
                );

        bottomPanel.setBackground(
                UI.LIGHT_GRAY
        );

        JButton addButton =
                new JButton("Add Instructor");

        JButton editButton =
                new JButton("Edit Instructor");

        JButton deleteButton =
                new JButton("Delete Instructor");

        UI.styleButton(addButton);
        UI.styleButton(editButton);
        UI.styleButton(deleteButton);

        bottomPanel.add(addButton);
        bottomPanel.add(editButton);
        bottomPanel.add(deleteButton);

        add(
                bottomPanel,
                BorderLayout.SOUTH
        );

        // ========================================
        // BUTTON ACTIONS
        // ========================================

        searchButton.addActionListener(
                e -> searchInstructors()
        );

        refreshButton.addActionListener(
                e -> loadInstructors()
        );

        addButton.addActionListener(
                e -> addInstructor()
        );

        editButton.addActionListener(
                e -> editInstructor()
        );

        deleteButton.addActionListener(
                e -> deleteInstructor()
        );

        searchField.addActionListener(
                e -> searchInstructors()
        );
    }

    // ========================================
    // LOAD INSTRUCTORS
    // ========================================

    private void loadInstructors() {

        tableModel.setRowCount(0);

        String sql =
                "SELECT i.InstructorID, " +
                "p.FirstName, " +
                "p.LastName, " +
                "p.Phone, " +
                "p.Email, " +
                "i.HireDate, " +
                "i.Status " +
                "FROM Instructor i " +
                "INNER JOIN Person p " +
                "ON i.PersonID = p.PersonID " +
                "ORDER BY i.InstructorID DESC";

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
                            result.getInt("InstructorID"),
                            result.getString("FirstName"),
                            result.getString("LastName"),
                            result.getString("Phone"),
                            result.getString("Email"),
                            result.getDate("HireDate"),
                            result.getString("Status")
                        }
                );
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error loading instructors:\n"
                    + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // ========================================
    // SEARCH INSTRUCTORS
    // ========================================

    private void searchInstructors() {

        String search =
                searchField.getText().trim();

        if (search.isEmpty()) {

            loadInstructors();
            return;
        }

        tableModel.setRowCount(0);

        String sql =
                "SELECT i.InstructorID, " +
                "p.FirstName, " +
                "p.LastName, " +
                "p.Phone, " +
                "p.Email, " +
                "i.HireDate, " +
                "i.Status " +
                "FROM Instructor i " +
                "INNER JOIN Person p " +
                "ON i.PersonID = p.PersonID " +
                "WHERE p.FirstName LIKE ? " +
                "OR p.LastName LIKE ? " +
                "OR p.Phone LIKE ? " +
                "OR p.Email LIKE ? " +
                "OR i.Status LIKE ? " +
                "ORDER BY i.InstructorID DESC";

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            String value =
                    "%" + search + "%";

            statement.setString(1, value);
            statement.setString(2, value);
            statement.setString(3, value);
            statement.setString(4, value);
            statement.setString(5, value);

            ResultSet result =
                    statement.executeQuery();

            while (result.next()) {

                tableModel.addRow(
                        new Object[]{
                            result.getInt("InstructorID"),
                            result.getString("FirstName"),
                            result.getString("LastName"),
                            result.getString("Phone"),
                            result.getString("Email"),
                            result.getDate("HireDate"),
                            result.getString("Status")
                        }
                );
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error searching instructors:\n"
                    + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // ========================================
    // ADD INSTRUCTOR
    // ========================================

    private void addInstructor() {

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

        String[] statusOptions = {
            "Active",
            "Inactive"
        };

        JComboBox<String> statusCombo =
                new JComboBox<>(
                        statusOptions
                );

        JPanel formPanel =
                new JPanel(
                        new GridLayout(
                                7,
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

        formPanel.add(
                new JLabel("Status:")
        );

        formPanel.add(
                statusCombo
        );

        int result =
                JOptionPane.showConfirmDialog(
                        this,
                        formPanel,
                        "Add Instructor",
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

        String status =
                statusCombo.getSelectedItem()
                        .toString();

        // ========================================
        // VALIDATION
        // ========================================

        if (firstName.isEmpty()
                || lastName.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "First Name and Last Name are required.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        Date birthDate = null;

        if (!dateOfBirth.isEmpty()) {

            try {

                birthDate =
                        Date.valueOf(
                                dateOfBirth
                        );

            } catch (IllegalArgumentException e) {

                JOptionPane.showMessageDialog(
                        this,
                        "Date of Birth must be in this format:\n"
                        + "YYYY-MM-DD",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }
        }

        // ========================================
        // DATABASE TRANSACTION
        // ========================================

        Connection connection = null;

        try {

            connection =
                    DBConnection.getConnection();

            connection.setAutoCommit(false);

            // Insert Person
            String personSql =
                    "INSERT INTO Person "
                    + "(FirstName, LastName, Phone, Email, "
                    + "Address, DateOfBirth) "
                    + "VALUES (?, ?, ?, ?, ?, ?)";

            PreparedStatement personStatement =
                    connection.prepareStatement(
                            personSql,
                            Statement.RETURN_GENERATED_KEYS
                    );

            personStatement.setString(
                    1,
                    firstName
            );

            personStatement.setString(
                    2,
                    lastName
            );

            personStatement.setString(
                    3,
                    phone
            );

            personStatement.setString(
                    4,
                    email
            );

            personStatement.setString(
                    5,
                    address
            );

            if (birthDate == null) {

                personStatement.setNull(
                        6,
                        java.sql.Types.DATE
                );

            } else {

                personStatement.setDate(
                        6,
                        birthDate
                );
            }

            personStatement.executeUpdate();

            // Get generated PersonID
            ResultSet generatedKeys =
                    personStatement.getGeneratedKeys();

            int personID;

            if (generatedKeys.next()) {

                personID =
                        generatedKeys.getInt(1);

            } else {

                throw new Exception(
                        "Could not get PersonID."
                );
            }

            // Insert Instructor
            String instructorSql =
                    "INSERT INTO Instructor "
                    + "(PersonID, HireDate, Status) "
                    + "VALUES (?, GETDATE(), ?)";

            PreparedStatement instructorStatement =
                    connection.prepareStatement(
                            instructorSql
                    );

            instructorStatement.setInt(
                    1,
                    personID
            );

            instructorStatement.setString(
                    2,
                    status
            );

            instructorStatement.executeUpdate();

            // Commit transaction
            connection.commit();

            JOptionPane.showMessageDialog(
                    this,
                    "Instructor added successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            loadInstructors();

        } catch (Exception e) {

            try {

                if (connection != null) {
                    connection.rollback();
                }

            } catch (Exception rollbackError) {
                // Nothing to do here.
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Error adding instructor:\n"
                    + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );

        } finally {

            try {

                if (connection != null) {
                    connection.setAutoCommit(true);
                    connection.close();
                }

            } catch (Exception e) {
                // Nothing to do here.
            }
        }
    }

    // ========================================
    // EDIT INSTRUCTOR
    // ========================================

    private void editInstructor() {

        int selectedRow =
                instructorTable.getSelectedRow();

        if (selectedRow == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select an instructor first.",
                    "Edit Instructor",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        int instructorID =
                (int) tableModel.getValueAt(
                        selectedRow,
                        0
                );

        String currentFirstName =
                tableModel.getValueAt(
                        selectedRow,
                        1
                ).toString();

        String currentLastName =
                tableModel.getValueAt(
                        selectedRow,
                        2
                ).toString();

        String currentPhone =
                tableModel.getValueAt(
                        selectedRow,
                        3
                ) == null
                        ? ""
                        : tableModel.getValueAt(
                                selectedRow,
                                3
                        ).toString();

        String currentEmail =
                tableModel.getValueAt(
                        selectedRow,
                        4
                ) == null
                        ? ""
                        : tableModel.getValueAt(
                                selectedRow,
                                4
                        ).toString();

        String currentStatus =
                tableModel.getValueAt(
                        selectedRow,
                        6
                ).toString();

        JTextField firstNameField =
                new JTextField(
                        currentFirstName
                );

        JTextField lastNameField =
                new JTextField(
                        currentLastName
                );

        JTextField phoneField =
                new JTextField(
                        currentPhone
                );

        JTextField emailField =
                new JTextField(
                        currentEmail
                );

        String[] statusOptions = {
            "Active",
            "Inactive"
        };

        JComboBox<String> statusCombo =
                new JComboBox<>(
                        statusOptions
                );

        statusCombo.setSelectedItem(
                currentStatus
        );

        JPanel formPanel =
                new JPanel(
                        new GridLayout(
                                5,
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
                new JLabel("Status:")
        );

        formPanel.add(
                statusCombo
        );

        int result =
                JOptionPane.showConfirmDialog(
                        this,
                        formPanel,
                        "Edit Instructor",
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

        String status =
                statusCombo.getSelectedItem()
                        .toString();

        if (firstName.isEmpty()
                || lastName.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "First Name and Last Name are required.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        Connection connection = null;

        try {

            connection =
                    DBConnection.getConnection();

            connection.setAutoCommit(false);

            // Update Person
            String personSql =
                    "UPDATE Person "
                    + "SET FirstName = ?, "
                    + "LastName = ?, "
                    + "Phone = ?, "
                    + "Email = ? "
                    + "WHERE PersonID = "
                    + "(SELECT PersonID "
                    + "FROM Instructor "
                    + "WHERE InstructorID = ?)";

            PreparedStatement personStatement =
                    connection.prepareStatement(
                            personSql
                    );

            personStatement.setString(
                    1,
                    firstName
            );

            personStatement.setString(
                    2,
                    lastName
            );

            personStatement.setString(
                    3,
                    phone
            );

            personStatement.setString(
                    4,
                    email
            );

            personStatement.setInt(
                    5,
                    instructorID
            );

            personStatement.executeUpdate();

            // Update Instructor
            String instructorSql =
                    "UPDATE Instructor "
                    + "SET Status = ? "
                    + "WHERE InstructorID = ?";

            PreparedStatement instructorStatement =
                    connection.prepareStatement(
                            instructorSql
                    );

            instructorStatement.setString(
                    1,
                    status
            );

            instructorStatement.setInt(
                    2,
                    instructorID
            );

            instructorStatement.executeUpdate();

            connection.commit();

            JOptionPane.showMessageDialog(
                    this,
                    "Instructor updated successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            loadInstructors();

        } catch (Exception e) {

            try {

                if (connection != null) {
                    connection.rollback();
                }

            } catch (Exception rollbackError) {
                // Nothing to do here.
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Error updating instructor:\n"
                    + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );

        } finally {

            try {

                if (connection != null) {
                    connection.setAutoCommit(true);
                    connection.close();
                }

            } catch (Exception e) {
                // Nothing to do here.
            }
        }
    }

    // ========================================
    // DELETE INSTRUCTOR
    // ========================================

    private void deleteInstructor() {

        int selectedRow =
                instructorTable.getSelectedRow();

        if (selectedRow == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select an instructor first.",
                    "Delete Instructor",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        int instructorID =
                (int) tableModel.getValueAt(
                        selectedRow,
                        0
                );

        String firstName =
                tableModel.getValueAt(
                        selectedRow,
                        1
                ).toString();

        String lastName =
                tableModel.getValueAt(
                        selectedRow,
                        2
                ).toString();

        int confirmation =
                JOptionPane.showConfirmDialog(
                        this,
                        "Are you sure you want to delete\n"
                        + firstName
                        + " "
                        + lastName
                        + "?",
                        "Delete Instructor",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

        if (confirmation
                != JOptionPane.YES_OPTION) {

            return;
        }

        Connection connection = null;

        try {

            connection =
                    DBConnection.getConnection();

            connection.setAutoCommit(false);

            // Get PersonID first
            String personIDSql =
                    "SELECT PersonID "
                    + "FROM Instructor "
                    + "WHERE InstructorID = ?";

            PreparedStatement personIDStatement =
                    connection.prepareStatement(
                            personIDSql
                    );

            personIDStatement.setInt(
                    1,
                    instructorID
            );

            ResultSet result =
                    personIDStatement.executeQuery();

            int personID;

            if (result.next()) {

                personID =
                        result.getInt(
                                "PersonID"
                        );

            } else {

                throw new Exception(
                        "Instructor not found."
                );
            }

            // Delete Instructor
            String deleteInstructorSql =
                    "DELETE FROM Instructor "
                    + "WHERE InstructorID = ?";

            PreparedStatement deleteInstructorStatement =
                    connection.prepareStatement(
                            deleteInstructorSql
                    );

            deleteInstructorStatement.setInt(
                    1,
                    instructorID
            );

            deleteInstructorStatement.executeUpdate();

            // Delete Person
            String deletePersonSql =
                    "DELETE FROM Person "
                    + "WHERE PersonID = ?";

            PreparedStatement deletePersonStatement =
                    connection.prepareStatement(
                            deletePersonSql
                    );

            deletePersonStatement.setInt(
                    1,
                    personID
            );

            deletePersonStatement.executeUpdate();

            connection.commit();

            JOptionPane.showMessageDialog(
                    this,
                    "Instructor deleted successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            loadInstructors();

        } catch (Exception e) {

            try {

                if (connection != null) {
                    connection.rollback();
                }

            } catch (Exception rollbackError) {
                // Nothing to do here.
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Could not delete instructor.\n\n"
                    + "The instructor may already be connected "
                    + "to lessons or exams.\n\n"
                    + e.getMessage(),
                    "Delete Error",
                    JOptionPane.ERROR_MESSAGE
            );

        } finally {

            try {

                if (connection != null) {
                    connection.setAutoCommit(true);
                    connection.close();
                }

            } catch (Exception e) {
                // Nothing to do here.
            }
        }
    }
}