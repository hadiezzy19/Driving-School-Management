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

public class Exams extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private JTextField searchField;

    public Exams() {

        setLayout(new BorderLayout(15, 15));
        setBackground(UI.LIGHT_GRAY);

        setBorder(
                BorderFactory.createEmptyBorder(
                        20,
                        20,
                        20,
                        20
                )
        );

        createTopPanel();
        createTable();

        loadExams();
    }

    // =========================================================
    // TOP PANEL
    // =========================================================

    private void createTopPanel() {

        JPanel topPanel =
                new JPanel(new BorderLayout());

        topPanel.setBackground(UI.LIGHT_GRAY);

        JPanel titlePanel =
                new JPanel();

        titlePanel.setLayout(
                new javax.swing.BoxLayout(
                        titlePanel,
                        javax.swing.BoxLayout.Y_AXIS
                )
        );

        titlePanel.setBackground(UI.LIGHT_GRAY);

        JLabel title =
                new JLabel("Exams");

        UI.styleTitle(title);

        JLabel subtitle =
                new JLabel(
                        "Manage student exams"
                );

        UI.styleSubtitle(subtitle);

        titlePanel.add(title);
        titlePanel.add(subtitle);

        topPanel.add(
                titlePanel,
                BorderLayout.WEST
        );

        JPanel buttonPanel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT,
                                10,
                                0
                        )
                );

        buttonPanel.setBackground(UI.LIGHT_GRAY);

        JButton addButton =
                new JButton("Add Exam");

        JButton editButton =
                new JButton("Edit");

        JButton deleteButton =
                new JButton("Delete");

        JButton refreshButton =
                new JButton("Refresh");

        UI.styleButton(addButton);
        UI.styleButton(editButton);
        UI.styleButton(deleteButton);
        UI.styleButton(refreshButton);

        addButton.addActionListener(
                e -> addExam()
        );

        editButton.addActionListener(
                e -> editExam()
        );

        deleteButton.addActionListener(
                e -> deleteExam()
        );

        refreshButton.addActionListener(
                e -> loadExams()
        );

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        topPanel.add(
                buttonPanel,
                BorderLayout.EAST
        );

        JPanel searchPanel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT,
                                0,
                                10
                        )
                );

        searchPanel.setBackground(UI.LIGHT_GRAY);

        searchField =
                new JTextField();

        searchField.setPreferredSize(
                new Dimension(300, 35)
        );

        UI.styleTextField(searchField);

        JButton searchButton =
                new JButton("Search");

        UI.styleButton(searchButton);

        searchButton.addActionListener(
                e -> searchExams()
        );

        searchField.addActionListener(
                e -> searchExams()
        );

        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        JPanel northPanel =
                new JPanel(new BorderLayout());

        northPanel.setBackground(UI.LIGHT_GRAY);

        northPanel.add(
                topPanel,
                BorderLayout.NORTH
        );

        northPanel.add(
                searchPanel,
                BorderLayout.CENTER
        );

        add(
                northPanel,
                BorderLayout.NORTH
        );
    }

    // =========================================================
    // TABLE
    // =========================================================

    private void createTable() {

        model =
                new DefaultTableModel(
                        new Object[]{
                            "Exam ID",
                            "Student",
                            "Instructor",
                            "Exam Date",
                            "Exam Type",
                            "Score",
                            "Result",
                            "Notes"
                        },
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

        table =
                new JTable(model);

        table.setRowHeight(32);
        table.setFont(UI.NORMAL_FONT);

        table.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        table.getTableHeader()
                .setFont(UI.BUTTON_FONT);

        table.getTableHeader()
                .setBackground(UI.NAVY);

        table.getTableHeader()
                .setForeground(UI.WHITE);

        JScrollPane scrollPane =
                new JScrollPane(table);

        scrollPane.setBorder(
                BorderFactory.createLineBorder(
                        UI.BORDER
                )
        );

        add(
                scrollPane,
                BorderLayout.CENTER
        );
    }

    // =========================================================
    // LOAD EXAMS
    // =========================================================

    private void loadExams() {

        model.setRowCount(0);

        String sql =
                "SELECT e.ExamID, " +
                "sp.FirstName + ' ' + sp.LastName AS StudentName, " +
                "ip.FirstName + ' ' + ip.LastName AS InstructorName, " +
                "e.ExamDate, " +
                "e.ExamType, " +
                "e.Score, " +
                "e.Result, " +
                "e.Notes " +
                "FROM Exam e " +
                "INNER JOIN Student s " +
                "ON e.StudentID = s.StudentID " +
                "INNER JOIN Person sp " +
                "ON s.PersonID = sp.PersonID " +
                "INNER JOIN Instructor i " +
                "ON e.InstructorID = i.InstructorID " +
                "INNER JOIN Person ip " +
                "ON i.PersonID = ip.PersonID " +
                "ORDER BY e.ExamDate DESC, e.ExamID DESC";

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet result =
                        statement.executeQuery()
        ) {

            while (result.next()) {

                model.addRow(
                        new Object[]{
                            result.getInt("ExamID"),
                            result.getString("StudentName"),
                            result.getString("InstructorName"),
                            result.getDate("ExamDate"),
                            result.getString("ExamType"),
                            result.getBigDecimal("Score"),
                            result.getString("Result"),
                            result.getString("Notes")
                        }
                );
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error loading exams:\n"
                    + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================================================
    // SEARCH
    // =========================================================

    private void searchExams() {

        String search =
                searchField.getText().trim();

        if (search.isEmpty()) {

            loadExams();
            return;
        }

        model.setRowCount(0);

        String sql =
                "SELECT e.ExamID, " +
                "sp.FirstName + ' ' + sp.LastName AS StudentName, " +
                "ip.FirstName + ' ' + ip.LastName AS InstructorName, " +
                "e.ExamDate, " +
                "e.ExamType, " +
                "e.Score, " +
                "e.Result, " +
                "e.Notes " +
                "FROM Exam e " +
                "INNER JOIN Student s " +
                "ON e.StudentID = s.StudentID " +
                "INNER JOIN Person sp " +
                "ON s.PersonID = sp.PersonID " +
                "INNER JOIN Instructor i " +
                "ON e.InstructorID = i.InstructorID " +
                "INNER JOIN Person ip " +
                "ON i.PersonID = ip.PersonID " +
                "WHERE sp.FirstName LIKE ? " +
                "OR sp.LastName LIKE ? " +
                "OR ip.FirstName LIKE ? " +
                "OR ip.LastName LIKE ? " +
                "OR e.ExamType LIKE ? " +
                "OR e.Result LIKE ? " +
                "OR e.Notes LIKE ? " +
                "ORDER BY e.ExamDate DESC";

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            String value =
                    "%" + search + "%";

            for (int i = 1; i <= 7; i++) {
                statement.setString(i, value);
            }

            ResultSet result =
                    statement.executeQuery();

            while (result.next()) {

                model.addRow(
                        new Object[]{
                            result.getInt("ExamID"),
                            result.getString("StudentName"),
                            result.getString("InstructorName"),
                            result.getDate("ExamDate"),
                            result.getString("ExamType"),
                            result.getBigDecimal("Score"),
                            result.getString("Result"),
                            result.getString("Notes")
                        }
                );
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error searching exams:\n"
                    + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================================================
    // ADD EXAM
    // =========================================================

    private void addExam() {

        JDialog dialog =
                new JDialog();

        dialog.setTitle("Add Exam");
        dialog.setModal(true);
        dialog.setSize(550, 600);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel =
                new JPanel(
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
                new JLabel("Add Exam");

        UI.styleSectionTitle(title);

        mainPanel.add(
                title,
                BorderLayout.NORTH
        );

        JPanel formPanel =
                new JPanel(
                        new GridBagLayout()
                );

        formPanel.setBackground(UI.WHITE);

        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.insets =
                new Insets(8, 5, 8, 5);

        gbc.fill =
                GridBagConstraints.HORIZONTAL;

        gbc.weightx = 1;

        JComboBox<String> studentCombo =
                new JComboBox<>();

        JComboBox<String> instructorCombo =
                new JComboBox<>();

        JComboBox<String> examTypeCombo =
                new JComboBox<>(
                        new String[]{
                            "Theoretical",
                            "Practical"
                        }
                );

        JComboBox<String> resultCombo =
                new JComboBox<>(
                        new String[]{
                            "Passed",
                            "Failed",
                            "Pending"
                        }
                );

        JTextField dateField =
                new JTextField();

        JTextField scoreField =
                new JTextField();

        JTextField notesField =
                new JTextField();

        UI.styleTextField(dateField);
        UI.styleTextField(scoreField);
        UI.styleTextField(notesField);

        loadStudents(studentCombo);
        loadInstructors(instructorCombo);

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
                "Instructor",
                instructorCombo
        );

        addFormRow(
                formPanel,
                gbc,
                2,
                "Exam Date",
                dateField
        );

        addFormRow(
                formPanel,
                gbc,
                3,
                "Exam Type",
                examTypeCombo
        );

        addFormRow(
                formPanel,
                gbc,
                4,
                "Score",
                scoreField
        );

        addFormRow(
                formPanel,
                gbc,
                5,
                "Result",
                resultCombo
        );

        addFormRow(
                formPanel,
                gbc,
                6,
                "Notes",
                notesField
        );

        mainPanel.add(
                formPanel,
                BorderLayout.CENTER
        );

        JPanel buttonPanel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT
                        )
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

            if (studentCombo.getSelectedItem() == null ||
                    instructorCombo.getSelectedItem() == null) {

                JOptionPane.showMessageDialog(
                        dialog,
                        "Please select a student and instructor."
                );

                return;
            }

            Date examDate;

            try {

                examDate =
                        Date.valueOf(
                                dateField.getText().trim()
                        );

            } catch (IllegalArgumentException ex) {

                JOptionPane.showMessageDialog(
                        dialog,
                        "Date must be in YYYY-MM-DD format."
                );

                return;
            }

            BigDecimal score;

            try {

                score =
                        new BigDecimal(
                                scoreField.getText().trim()
                        );

                if (score.compareTo(
                        BigDecimal.ZERO
                ) < 0 ||
                        score.compareTo(
                                new BigDecimal("100")
                        ) > 0) {

                    JOptionPane.showMessageDialog(
                            dialog,
                            "Score must be between 0 and 100."
                    );

                    return;
                }

            } catch (NumberFormatException ex) {

                JOptionPane.showMessageDialog(
                        dialog,
                        "Please enter a valid score."
                );

                return;
            }

            int studentID =
                    getIDFromCombo(
                            studentCombo
                    );

            int instructorID =
                    getIDFromCombo(
                            instructorCombo
                    );

            String sql =
                    "INSERT INTO Exam " +
                    "(StudentID, InstructorID, ExamDate, " +
                    "ExamType, Score, Result, Notes) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";

            try (
                    Connection connection =
                            DBConnection.getConnection();

                    PreparedStatement statement =
                            connection.prepareStatement(sql)
            ) {

                statement.setInt(
                        1,
                        studentID
                );

                statement.setInt(
                        2,
                        instructorID
                );

                statement.setDate(
                        3,
                        examDate
                );

                statement.setString(
                        4,
                        examTypeCombo
                                .getSelectedItem()
                                .toString()
                );

                statement.setBigDecimal(
                        5,
                        score
                );

                statement.setString(
                        6,
                        resultCombo
                                .getSelectedItem()
                                .toString()
                );

                statement.setString(
                        7,
                        notesField
                                .getText()
                                .trim()
                );

                statement.executeUpdate();

                JOptionPane.showMessageDialog(
                        dialog,
                        "Exam added successfully."
                );

                dialog.dispose();

                loadExams();

            } catch (SQLException ex) {

                JOptionPane.showMessageDialog(
                        dialog,
                        "Error adding exam:\n"
                        + ex.getMessage(),
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
    // EDIT EXAM
    // =========================================================

    private void editExam() {

        int selectedRow =
                table.getSelectedRow();

        if (selectedRow == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select an exam first."
            );

            return;
        }

        int examID =
                Integer.parseInt(
                        table.getValueAt(
                                selectedRow,
                                0
                        ).toString()
                );

        String sql =
                "SELECT StudentID, InstructorID, " +
                "ExamDate, ExamType, Score, Result, Notes " +
                "FROM Exam " +
                "WHERE ExamID = ?";

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(
                    1,
                    examID
            );

            ResultSet result =
                    statement.executeQuery();

            if (result.next()) {

                showEditDialog(
                        examID,
                        result.getInt("StudentID"),
                        result.getInt("InstructorID"),
                        result.getDate("ExamDate"),
                        result.getString("ExamType"),
                        result.getBigDecimal("Score"),
                        result.getString("Result"),
                        result.getString("Notes")
                );
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error loading exam:\n"
                    + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================================================
    // EDIT DIALOG
    // =========================================================

    private void showEditDialog(
            int examID,
            int currentStudentID,
            int currentInstructorID,
            Date currentDate,
            String currentType,
            BigDecimal currentScore,
            String currentResult,
            String currentNotes
    ) {

        JDialog dialog =
                new JDialog();

        dialog.setTitle("Edit Exam");
        dialog.setModal(true);
        dialog.setSize(550, 600);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel =
                new JPanel(
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
                new JLabel("Edit Exam");

        UI.styleSectionTitle(title);

        mainPanel.add(
                title,
                BorderLayout.NORTH
        );

        JPanel formPanel =
                new JPanel(
                        new GridBagLayout()
                );

        formPanel.setBackground(UI.WHITE);

        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.insets =
                new Insets(8, 5, 8, 5);

        gbc.fill =
                GridBagConstraints.HORIZONTAL;

        gbc.weightx = 1;

        JComboBox<String> studentCombo =
                new JComboBox<>();

        JComboBox<String> instructorCombo =
                new JComboBox<>();

        JComboBox<String> examTypeCombo =
                new JComboBox<>(
                        new String[]{
                            "Theoretical",
                            "Practical"
                        }
                );

        JComboBox<String> resultCombo =
                new JComboBox<>(
                        new String[]{
                            "Passed",
                            "Failed",
                            "Pending"
                        }
                );

        JTextField dateField =
                new JTextField(
                        currentDate.toString()
                );

        JTextField scoreField =
                new JTextField(
                        currentScore == null
                                ? ""
                                : currentScore.toString()
                );

        JTextField notesField =
                new JTextField(
                        currentNotes == null
                                ? ""
                                : currentNotes
                );

        UI.styleTextField(dateField);
        UI.styleTextField(scoreField);
        UI.styleTextField(notesField);

        loadStudentsForEdit(
                studentCombo,
                currentStudentID
        );

        loadInstructorsForEdit(
                instructorCombo,
                currentInstructorID
        );

        examTypeCombo.setSelectedItem(
                currentType
        );

        resultCombo.setSelectedItem(
                currentResult
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
                "Instructor",
                instructorCombo
        );

        addFormRow(
                formPanel,
                gbc,
                2,
                "Exam Date",
                dateField
        );

        addFormRow(
                formPanel,
                gbc,
                3,
                "Exam Type",
                examTypeCombo
        );

        addFormRow(
                formPanel,
                gbc,
                4,
                "Score",
                scoreField
        );

        addFormRow(
                formPanel,
                gbc,
                5,
                "Result",
                resultCombo
        );

        addFormRow(
                formPanel,
                gbc,
                6,
                "Notes",
                notesField
        );

        mainPanel.add(
                formPanel,
                BorderLayout.CENTER
        );

        JPanel buttonPanel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT
                        )
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

            Date examDate;

            try {

                examDate =
                        Date.valueOf(
                                dateField.getText().trim()
                        );

            } catch (IllegalArgumentException ex) {

                JOptionPane.showMessageDialog(
                        dialog,
                        "Date must be in YYYY-MM-DD format."
                );

                return;
            }

            BigDecimal score;

            try {

                score =
                        new BigDecimal(
                                scoreField.getText().trim()
                        );

                if (score.compareTo(
                        BigDecimal.ZERO
                ) < 0 ||
                        score.compareTo(
                                new BigDecimal("100")
                        ) > 0) {

                    JOptionPane.showMessageDialog(
                            dialog,
                            "Score must be between 0 and 100."
                    );

                    return;
                }

            } catch (NumberFormatException ex) {

                JOptionPane.showMessageDialog(
                        dialog,
                        "Please enter a valid score."
                );

                return;
            }

            int studentID =
                    getIDFromCombo(
                            studentCombo
                    );

            int instructorID =
                    getIDFromCombo(
                            instructorCombo
                    );

            String sql =
                    "UPDATE Exam SET " +
                    "StudentID = ?, " +
                    "InstructorID = ?, " +
                    "ExamDate = ?, " +
                    "ExamType = ?, " +
                    "Score = ?, " +
                    "Result = ?, " +
                    "Notes = ? " +
                    "WHERE ExamID = ?";

            try (
                    Connection connection =
                            DBConnection.getConnection();

                    PreparedStatement statement =
                            connection.prepareStatement(sql)
            ) {

                statement.setInt(
                        1,
                        studentID
                );

                statement.setInt(
                        2,
                        instructorID
                );

                statement.setDate(
                        3,
                        examDate
                );

                statement.setString(
                        4,
                        examTypeCombo
                                .getSelectedItem()
                                .toString()
                );

                statement.setBigDecimal(
                        5,
                        score
                );

                statement.setString(
                        6,
                        resultCombo
                                .getSelectedItem()
                                .toString()
                );

                statement.setString(
                        7,
                        notesField
                                .getText()
                                .trim()
                );

                statement.setInt(
                        8,
                        examID
                );

                statement.executeUpdate();

                JOptionPane.showMessageDialog(
                        dialog,
                        "Exam updated successfully."
                );

                dialog.dispose();

                loadExams();

            } catch (SQLException ex) {

                JOptionPane.showMessageDialog(
                        dialog,
                        "Error updating exam:\n"
                        + ex.getMessage(),
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
    // DELETE EXAM
    // =========================================================

    private void deleteExam() {

        int selectedRow =
                table.getSelectedRow();

        if (selectedRow == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select an exam first."
            );

            return;
        }

        int examID =
                Integer.parseInt(
                        table.getValueAt(
                                selectedRow,
                                0
                        ).toString()
                );

        int choice =
                JOptionPane.showConfirmDialog(
                        this,
                        "Are you sure you want to delete this exam?",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION
                );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        String sql =
                "DELETE FROM Exam " +
                "WHERE ExamID = ?";

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(
                    1,
                    examID
            );

            statement.executeUpdate();

            JOptionPane.showMessageDialog(
                    this,
                    "Exam deleted successfully."
            );

            loadExams();

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error deleting exam:\n"
                    + e.getMessage(),
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
                    "Error loading students:\n"
                    + e.getMessage()
            );
        }
    }

    // =========================================================
    // LOAD STUDENTS FOR EDIT
    // =========================================================

    private void loadStudentsForEdit(
            JComboBox<String> combo,
            int currentID
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

            statement.setInt(
                    1,
                    currentID
            );

            ResultSet result =
                    statement.executeQuery();

            while (result.next()) {

                combo.addItem(
                        result.getInt("StudentID")
                        + " - "
                        + result.getString("FirstName")
                        + " "
                        + result.getString("LastName")
                );
            }

            selectComboByID(
                    combo,
                    currentID
            );

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error loading students:\n"
                    + e.getMessage()
            );
        }
    }

    // =========================================================
    // LOAD INSTRUCTORS
    // =========================================================

    private void loadInstructors(
            JComboBox<String> combo
    ) {

        String sql =
                "SELECT i.InstructorID, " +
                "p.FirstName, p.LastName " +
                "FROM Instructor i " +
                "INNER JOIN Person p " +
                "ON i.PersonID = p.PersonID " +
                "WHERE i.Status = 'Active' " +
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
                        result.getInt("InstructorID")
                        + " - "
                        + result.getString("FirstName")
                        + " "
                        + result.getString("LastName")
                );
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error loading instructors:\n"
                    + e.getMessage()
            );
        }
    }

    // =========================================================
    // LOAD INSTRUCTORS FOR EDIT
    // =========================================================

    private void loadInstructorsForEdit(
            JComboBox<String> combo,
            int currentID
    ) {

        String sql =
                "SELECT i.InstructorID, " +
                "p.FirstName, p.LastName " +
                "FROM Instructor i " +
                "INNER JOIN Person p " +
                "ON i.PersonID = p.PersonID " +
                "WHERE i.Status = 'Active' " +
                "OR i.InstructorID = ? " +
                "ORDER BY p.FirstName, p.LastName";

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(
                    1,
                    currentID
            );

            ResultSet result =
                    statement.executeQuery();

            while (result.next()) {

                combo.addItem(
                        result.getInt("InstructorID")
                        + " - "
                        + result.getString("FirstName")
                        + " "
                        + result.getString("LastName")
                );
            }

            selectComboByID(
                    combo,
                    currentID
            );

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error loading instructors:\n"
                    + e.getMessage()
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
    // SELECT COMBO BY ID
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

        panel.add(
                label,
                gbc
        );

        gbc.gridx = 1;
        gbc.weightx = 1;

        panel.add(
                component,
                gbc
        );
    }
}