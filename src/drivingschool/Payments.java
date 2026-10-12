package drivingschool;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

public class Payments extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private JTextField searchField;

    public Payments() {

        setLayout(new BorderLayout(15, 15));
        setBackground(UI.LIGHT_GRAY);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        createTopPanel();
        createTable();

        loadPayments();
    }

    // =========================================================
    // TOP PANEL
    // =========================================================

    private void createTopPanel() {

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(UI.LIGHT_GRAY);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new javax.swing.BoxLayout(
                titlePanel,
                javax.swing.BoxLayout.Y_AXIS
        ));
        titlePanel.setBackground(UI.LIGHT_GRAY);

        JLabel title = new JLabel("Payments");
        UI.styleTitle(title);

        JLabel subtitle = new JLabel(
                "Manage student payments"
        );
        UI.styleSubtitle(subtitle);

        titlePanel.add(title);
        titlePanel.add(subtitle);

        topPanel.add(titlePanel, BorderLayout.WEST);

        JPanel buttonPanel = new JPanel(new FlowLayout(
                FlowLayout.RIGHT,
                10,
                0
        ));
        buttonPanel.setBackground(UI.LIGHT_GRAY);

        JButton addButton = new JButton("Add Payment");
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");
        JButton refreshButton = new JButton("Refresh");

        UI.styleButton(addButton);
        UI.styleButton(editButton);
        UI.styleButton(deleteButton);
        UI.styleButton(refreshButton);

        addButton.addActionListener(e -> addPayment());
        editButton.addActionListener(e -> editPayment());
        deleteButton.addActionListener(e -> deletePayment());
        refreshButton.addActionListener(e -> loadPayments());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        topPanel.add(buttonPanel, BorderLayout.EAST);

        JPanel searchPanel = new JPanel(new FlowLayout(
                FlowLayout.LEFT,
                0,
                10
        ));
        searchPanel.setBackground(UI.LIGHT_GRAY);

        searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(300, 35));

        UI.styleTextField(searchField);

        JButton searchButton = new JButton("Search");
        UI.styleButton(searchButton);

        searchButton.addActionListener(e -> searchPayments());

        searchField.addActionListener(e -> searchPayments());

        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.setBackground(UI.LIGHT_GRAY);

        northPanel.add(topPanel, BorderLayout.NORTH);
        northPanel.add(searchPanel, BorderLayout.CENTER);

        add(northPanel, BorderLayout.NORTH);
    }

    // =========================================================
    // TABLE
    // =========================================================

    private void createTable() {

        model = new DefaultTableModel(
                new Object[]{
                    "Payment ID",
                    "Student",
                    "Amount",
                    "Payment Date",
                    "Payment Method",
                    "Description"
                },
                0
        ) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);

        table.setRowHeight(32);
        table.setFont(UI.NORMAL_FONT);
        table.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        table.getTableHeader().setFont(UI.BUTTON_FONT);
        table.getTableHeader().setBackground(UI.NAVY);
        table.getTableHeader().setForeground(UI.WHITE);

        JScrollPane scrollPane = new JScrollPane(table);

        scrollPane.setBorder(
                BorderFactory.createLineBorder(UI.BORDER)
        );

        add(scrollPane, BorderLayout.CENTER);
    }

    // =========================================================
    // LOAD PAYMENTS
    // =========================================================

    private void loadPayments() {

        model.setRowCount(0);

        String sql =
                "SELECT p.PaymentID, " +
                "per.FirstName + ' ' + per.LastName AS StudentName, " +
                "p.Amount, " +
                "p.PaymentDate, " +
                "p.PaymentMethod, " +
                "p.Description " +
                "FROM Payment p " +
                "INNER JOIN Student s " +
                "ON p.StudentID = s.StudentID " +
                "INNER JOIN Person per " +
                "ON s.PersonID = per.PersonID " +
                "ORDER BY p.PaymentID DESC";

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet result =
                        statement.executeQuery()
        ) {

            while (result.next()) {

                model.addRow(new Object[]{
                    result.getInt("PaymentID"),
                    result.getString("StudentName"),
                    result.getBigDecimal("Amount"),
                    result.getDate("PaymentDate"),
                    result.getString("PaymentMethod"),
                    result.getString("Description")
                });
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error loading payments:\n" +
                    e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================================================
    // SEARCH
    // =========================================================

    private void searchPayments() {

        String search = searchField.getText().trim();

        if (search.isEmpty()) {

            loadPayments();
            return;
        }

        model.setRowCount(0);

        String sql =
                "SELECT p.PaymentID, " +
                "per.FirstName + ' ' + per.LastName AS StudentName, " +
                "p.Amount, " +
                "p.PaymentDate, " +
                "p.PaymentMethod, " +
                "p.Description " +
                "FROM Payment p " +
                "INNER JOIN Student s " +
                "ON p.StudentID = s.StudentID " +
                "INNER JOIN Person per " +
                "ON s.PersonID = per.PersonID " +
                "WHERE per.FirstName LIKE ? " +
                "OR per.LastName LIKE ? " +
                "OR p.PaymentMethod LIKE ? " +
                "OR p.Description LIKE ? " +
                "ORDER BY p.PaymentID DESC";

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            String value = "%" + search + "%";

            statement.setString(1, value);
            statement.setString(2, value);
            statement.setString(3, value);
            statement.setString(4, value);

            ResultSet result = statement.executeQuery();

            while (result.next()) {

                model.addRow(new Object[]{
                    result.getInt("PaymentID"),
                    result.getString("StudentName"),
                    result.getBigDecimal("Amount"),
                    result.getDate("PaymentDate"),
                    result.getString("PaymentMethod"),
                    result.getString("Description")
                });
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error searching payments:\n" +
                    e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================================================
    // ADD PAYMENT
    // =========================================================

    private void addPayment() {

        JDialog dialog = new JDialog();
        dialog.setTitle("Add Payment");
        dialog.setModal(true);
        dialog.setSize(500, 500);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(
                new BorderLayout(10, 10)
        );

        mainPanel.setBackground(UI.WHITE);
        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        25,
                        30,
                        25,
                        30
                )
        );

        JLabel title = new JLabel("Add Payment");
        UI.styleSectionTitle(title);

        mainPanel.add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(
                new GridBagLayout()
        );

        formPanel.setBackground(UI.WHITE);

        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        JComboBox<String> studentCombo =
                new JComboBox<>();

        loadStudents(studentCombo);

        JTextField amountField = new JTextField();
        JTextField dateField = new JTextField();
        JTextField descriptionField = new JTextField();

        JComboBox<String> methodCombo =
                new JComboBox<>(
                        new String[]{
                            "Cash",
                            "Card",
                            "Bank Transfer"
                        }
                );

        UI.styleTextField(amountField);
        UI.styleTextField(dateField);
        UI.styleTextField(descriptionField);

        dateField.setText(
                new java.text.SimpleDateFormat(
                        "yyyy-MM-dd"
                ).format(
                        new java.util.Date()
                )
        );

        addFormRow(
                formPanel,
                gbc,
                0,
                "Student",
                studentCombo
        );

        addFormRow(
                formPanel,
                gbc,
                1,
                "Amount",
                amountField
        );

        addFormRow(
                formPanel,
                gbc,
                2,
                "Payment Date",
                dateField
        );

        addFormRow(
                formPanel,
                gbc,
                3,
                "Payment Method",
                methodCombo
        );

        addFormRow(
                formPanel,
                gbc,
                4,
                "Description",
                descriptionField
        );

        mainPanel.add(
                formPanel,
                BorderLayout.CENTER
        );

        JPanel buttonPanel = new JPanel(
                new FlowLayout(FlowLayout.RIGHT)
        );

        buttonPanel.setBackground(UI.WHITE);

        JButton saveButton =
                new JButton("Save");

        JButton cancelButton =
                new JButton("Cancel");

        UI.styleButton(saveButton);
        UI.styleButton(cancelButton);

        cancelButton.addActionListener(
                e -> dialog.dispose()
        );

        saveButton.addActionListener(e -> {

            if (studentCombo.getSelectedItem() == null) {

                JOptionPane.showMessageDialog(
                        dialog,
                        "Please select a student."
                );

                return;
            }

            String amountText =
                    amountField.getText().trim();

            if (amountText.isEmpty()) {

                JOptionPane.showMessageDialog(
                        dialog,
                        "Please enter the amount."
                );

                return;
            }

            BigDecimal amount;

            try {

                amount = new BigDecimal(amountText);

                if (amount.compareTo(BigDecimal.ZERO) <= 0) {

                    JOptionPane.showMessageDialog(
                            dialog,
                            "Amount must be greater than zero."
                    );

                    return;
                }

            } catch (NumberFormatException ex) {

                JOptionPane.showMessageDialog(
                        dialog,
                        "Please enter a valid amount."
                );

                return;
            }

            Date paymentDate;

            try {

                paymentDate = Date.valueOf(
                        dateField.getText().trim()
                );

            } catch (IllegalArgumentException ex) {

                JOptionPane.showMessageDialog(
                        dialog,
                        "Date must be in YYYY-MM-DD format."
                );

                return;
            }

            int studentID =
                    getIDFromCombo(
                            studentCombo
                    );

            String sql =
                    "INSERT INTO Payment " +
                    "(StudentID, Amount, PaymentDate, " +
                    "PaymentMethod, Description) " +
                    "VALUES (?, ?, ?, ?, ?)";

            try (
                    Connection connection =
                            DBConnection.getConnection();

                    PreparedStatement statement =
                            connection.prepareStatement(sql)
            ) {

                statement.setInt(1, studentID);
                statement.setBigDecimal(2, amount);
                statement.setDate(3, paymentDate);
                statement.setString(
                        4,
                        methodCombo.getSelectedItem()
                                .toString()
                );
                statement.setString(
                        5,
                        descriptionField.getText().trim()
                );

                statement.executeUpdate();

                JOptionPane.showMessageDialog(
                        dialog,
                        "Payment added successfully."
                );

                dialog.dispose();
                loadPayments();

            } catch (SQLException ex) {

                JOptionPane.showMessageDialog(
                        dialog,
                        "Error adding payment:\n" +
                        ex.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        mainPanel.add(
                buttonPanel,
                BorderLayout.SOUTH
        );

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    // =========================================================
    // EDIT PAYMENT
    // =========================================================

    private void editPayment() {

        int selectedRow =
                table.getSelectedRow();

        if (selectedRow == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a payment first."
            );

            return;
        }

        int paymentID =
                Integer.parseInt(
                        table.getValueAt(
                                selectedRow,
                                0
                        ).toString()
                );

        String sql =
                "SELECT StudentID, Amount, PaymentDate, " +
                "PaymentMethod, Description " +
                "FROM Payment " +
                "WHERE PaymentID = ?";

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, paymentID);

            ResultSet result =
                    statement.executeQuery();

            if (result.next()) {

                showEditDialog(
                        paymentID,
                        result.getInt("StudentID"),
                        result.getBigDecimal("Amount"),
                        result.getDate("PaymentDate"),
                        result.getString("PaymentMethod"),
                        result.getString("Description")
                );
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error loading payment:\n" +
                    e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================================================
    // EDIT DIALOG
    // =========================================================

    private void showEditDialog(
            int paymentID,
            int currentStudentID,
            BigDecimal currentAmount,
            Date currentDate,
            String currentMethod,
            String currentDescription
    ) {

        JDialog dialog = new JDialog();
        dialog.setTitle("Edit Payment");
        dialog.setModal(true);
        dialog.setSize(500, 500);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(
                new BorderLayout(10, 10)
        );

        mainPanel.setBackground(UI.WHITE);
        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        25,
                        30,
                        25,
                        30
                )
        );

        JLabel title =
                new JLabel("Edit Payment");

        UI.styleSectionTitle(title);

        mainPanel.add(
                title,
                BorderLayout.NORTH
        );

        JPanel formPanel = new JPanel(
                new GridBagLayout()
        );

        formPanel.setBackground(UI.WHITE);

        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        JComboBox<String> studentCombo =
                new JComboBox<>();

        loadStudentsForEdit(
                studentCombo,
                currentStudentID
        );

        JTextField amountField =
                new JTextField(
                        currentAmount.toString()
                );

        JTextField dateField =
                new JTextField(
                        currentDate.toString()
                );

        JComboBox<String> methodCombo =
                new JComboBox<>(
                        new String[]{
                            "Cash",
                            "Card",
                            "Bank Transfer"
                        }
                );

        methodCombo.setSelectedItem(
                currentMethod
        );

        JTextField descriptionField =
                new JTextField(
                        currentDescription == null
                                ? ""
                                : currentDescription
                );

        UI.styleTextField(amountField);
        UI.styleTextField(dateField);
        UI.styleTextField(descriptionField);

        addFormRow(
                formPanel,
                gbc,
                0,
                "Student",
                studentCombo
        );

        addFormRow(
                formPanel,
                gbc,
                1,
                "Amount",
                amountField
        );

        addFormRow(
                formPanel,
                gbc,
                2,
                "Payment Date",
                dateField
        );

        addFormRow(
                formPanel,
                gbc,
                3,
                "Payment Method",
                methodCombo
        );

        addFormRow(
                formPanel,
                gbc,
                4,
                "Description",
                descriptionField
        );

        mainPanel.add(
                formPanel,
                BorderLayout.CENTER
        );

        JPanel buttonPanel = new JPanel(
                new FlowLayout(FlowLayout.RIGHT)
        );

        buttonPanel.setBackground(UI.WHITE);

        JButton updateButton =
                new JButton("Update");

        JButton cancelButton =
                new JButton("Cancel");

        UI.styleButton(updateButton);
        UI.styleButton(cancelButton);

        cancelButton.addActionListener(
                e -> dialog.dispose()
        );

        updateButton.addActionListener(e -> {

            String amountText =
                    amountField.getText().trim();

            if (amountText.isEmpty()) {

                JOptionPane.showMessageDialog(
                        dialog,
                        "Please enter the amount."
                );

                return;
            }

            BigDecimal amount;

            try {

                amount = new BigDecimal(amountText);

                if (amount.compareTo(BigDecimal.ZERO) <= 0) {

                    JOptionPane.showMessageDialog(
                            dialog,
                            "Amount must be greater than zero."
                    );

                    return;
                }

            } catch (NumberFormatException ex) {

                JOptionPane.showMessageDialog(
                        dialog,
                        "Please enter a valid amount."
                );

                return;
            }

            Date paymentDate;

            try {

                paymentDate = Date.valueOf(
                        dateField.getText().trim()
                );

            } catch (IllegalArgumentException ex) {

                JOptionPane.showMessageDialog(
                        dialog,
                        "Date must be in YYYY-MM-DD format."
                );

                return;
            }

            int studentID =
                    getIDFromCombo(
                            studentCombo
                    );

            String updateSQL =
                    "UPDATE Payment SET " +
                    "StudentID = ?, " +
                    "Amount = ?, " +
                    "PaymentDate = ?, " +
                    "PaymentMethod = ?, " +
                    "Description = ? " +
                    "WHERE PaymentID = ?";

            try (
                    Connection connection =
                            DBConnection.getConnection();

                    PreparedStatement statement =
                            connection.prepareStatement(updateSQL)
            ) {

                statement.setInt(1, studentID);
                statement.setBigDecimal(2, amount);
                statement.setDate(3, paymentDate);
                statement.setString(
                        4,
                        methodCombo.getSelectedItem()
                                .toString()
                );
                statement.setString(
                        5,
                        descriptionField.getText().trim()
                );
                statement.setInt(6, paymentID);

                statement.executeUpdate();

                JOptionPane.showMessageDialog(
                        dialog,
                        "Payment updated successfully."
                );

                dialog.dispose();
                loadPayments();

            } catch (SQLException ex) {

                JOptionPane.showMessageDialog(
                        dialog,
                        "Error updating payment:\n" +
                        ex.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        buttonPanel.add(updateButton);
        buttonPanel.add(cancelButton);

        mainPanel.add(
                buttonPanel,
                BorderLayout.SOUTH
        );

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    // =========================================================
    // DELETE PAYMENT
    // =========================================================

    private void deletePayment() {

        int selectedRow =
                table.getSelectedRow();

        if (selectedRow == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a payment first."
            );

            return;
        }

        int paymentID =
                Integer.parseInt(
                        table.getValueAt(
                                selectedRow,
                                0
                        ).toString()
                );

        int choice =
                JOptionPane.showConfirmDialog(
                        this,
                        "Are you sure you want to delete " +
                        "this payment?",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION
                );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        String sql =
                "DELETE FROM Payment " +
                "WHERE PaymentID = ?";

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, paymentID);

            statement.executeUpdate();

            JOptionPane.showMessageDialog(
                    this,
                    "Payment deleted successfully."
            );

            loadPayments();

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error deleting payment:\n" +
                    e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================================================
    // LOAD STUDENTS
    // =========================================================

    private void loadStudents(
            JComboBox<String> combo
    ) {

        String sql =
                "SELECT s.StudentID, " +
                "p.FirstName, p.LastName " +
                "FROM Student s " +
                "INNER JOIN Person p " +
                "ON s.PersonID = p.PersonID " +
                "WHERE s.Status = 'Active' " +
                "ORDER BY p.FirstName, p.LastName";

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet result =
                        statement.executeQuery()
        ) {

            while (result.next()) {

                combo.addItem(
                        result.getInt("StudentID")
                        + " - "
                        + result.getString("FirstName")
                        + " "
                        + result.getString("LastName")
                );
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error loading students:\n" +
                    e.getMessage()
            );
        }
    }

    // =========================================================
    // LOAD STUDENTS FOR EDIT
    // =========================================================

    private void loadStudentsForEdit(
            JComboBox<String> combo,
            int currentStudentID
    ) {

        String sql =
                "SELECT s.StudentID, " +
                "p.FirstName, p.LastName " +
                "FROM Student s " +
                "INNER JOIN Person p " +
                "ON s.PersonID = p.PersonID " +
                "WHERE s.Status = 'Active' " +
                "OR s.StudentID = ? " +
                "ORDER BY p.FirstName, p.LastName";

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, currentStudentID);

            ResultSet result =
                    statement.executeQuery();

            while (result.next()) {

                int id =
                        result.getInt("StudentID");

                combo.addItem(
                        id
                        + " - "
                        + result.getString("FirstName")
                        + " "
                        + result.getString("LastName")
                );
            }

            selectComboByID(
                    combo,
                    currentStudentID
            );

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error loading students:\n" +
                    e.getMessage()
            );
        }
    }

    // =========================================================
    // GET ID FROM COMBO
    // =========================================================

    private int getIDFromCombo(
            JComboBox<String> combo
    ) {

        String selected =
                combo.getSelectedItem()
                        .toString();

        return Integer.parseInt(
                selected.split(" - ")[0]
        );
    }

    // =========================================================
    // SELECT COMBO ITEM
    // =========================================================

    private void selectComboByID(
            JComboBox<String> combo,
            int id
    ) {

        for (int i = 0;
             i < combo.getItemCount();
             i++) {

            String item =
                    combo.getItemAt(i);

            if (item.startsWith(id + " - ")) {

                combo.setSelectedIndex(i);
                break;
            }
        }
    }

    // =========================================================
    // FORM ROW
    // =========================================================

    private void addFormRow(
            JPanel panel,
            GridBagConstraints gbc,
            int row,
            String labelText,
            java.awt.Component component
    ) {

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;

        JLabel label =
                new JLabel(labelText);

        UI.styleLabel(label);

        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;

        panel.add(component, gbc);
    }
}