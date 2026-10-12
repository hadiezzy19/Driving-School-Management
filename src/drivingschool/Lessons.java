package drivingschool;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;

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

public class Lessons extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private JTextField searchField;

    public Lessons() {

        setLayout(new BorderLayout(15, 15));
        setBackground(UI.LIGHT_GRAY);

        setBorder(
                BorderFactory.createEmptyBorder(
                        20, 20, 20, 20
                )
        );

        createTopPanel();
        createTable();

        loadLessons();
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
                new JLabel("Lessons");

        UI.styleTitle(title);

        JLabel subtitle =
                new JLabel(
                        "Manage driving lessons"
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

        buttonPanel.setBackground(
                UI.LIGHT_GRAY
        );

        JButton addButton =
                new JButton("Add Lesson");

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
                e -> addLesson()
        );

        editButton.addActionListener(
                e -> editLesson()
        );

        deleteButton.addActionListener(
                e -> deleteLesson()
        );

        refreshButton.addActionListener(
                e -> loadLessons()
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

        searchPanel.setBackground(
                UI.LIGHT_GRAY
        );

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
                e -> searchLessons()
        );

        searchField.addActionListener(
                e -> searchLessons()
        );

        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        JPanel northPanel =
                new JPanel(new BorderLayout());

        northPanel.setBackground(
                UI.LIGHT_GRAY
        );

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
                            "Lesson ID",
                            "Student",
                            "Instructor",
                            "Vehicle",
                            "Date",
                            "Start Time",
                            "End Time",
                            "Lesson Type",
                            "Attendance"
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

        table.setFont(
                UI.NORMAL_FONT
        );

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
    // LOAD LESSONS
    // =========================================================

    private void loadLessons() {

        model.setRowCount(0);

        String sql =
                "SELECT l.LessonID, " +
                "sp.FirstName + ' ' + sp.LastName AS StudentName, " +
                "ip.FirstName + ' ' + ip.LastName AS InstructorName, " +
                "v.PlateNumber, " +
                "v.Brand, " +
                "v.Model, " +
                "l.LessonDate, " +
                "l.StartTime, " +
                "l.EndTime, " +
                "l.LessonType, " +
                "l.AttendanceStatus " +
                "FROM Lesson l " +
                "INNER JOIN Student s " +
                "ON l.StudentID = s.StudentID " +
                "INNER JOIN Person sp " +
                "ON s.PersonID = sp.PersonID " +
                "INNER JOIN Instructor i " +
                "ON l.InstructorID = i.InstructorID " +
                "INNER JOIN Person ip " +
                "ON i.PersonID = ip.PersonID " +
                "INNER JOIN Vehicle v " +
                "ON l.VehicleID = v.VehicleID " +
                "ORDER BY l.LessonDate DESC, l.StartTime DESC";

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet result =
                        statement.executeQuery()
        ) {

            while (result.next()) {

                String vehicle =
                        result.getString("PlateNumber");

                String brand =
                        result.getString("Brand");

                String vehicleModel =
                        result.getString("Model");

                if (brand != null &&
                        !brand.trim().isEmpty()) {

                    vehicle += " - " + brand;
                }

                if (vehicleModel != null &&
                        !vehicleModel.trim().isEmpty()) {

                    vehicle += " " + vehicleModel;
                }

                model.addRow(
                        new Object[]{
                            result.getInt("LessonID"),
                            result.getString(
                                    "StudentName"
                            ),
                            result.getString(
                                    "InstructorName"
                            ),
                            vehicle,
                            result.getDate(
                                    "LessonDate"
                            ),
                            result.getTime(
                                    "StartTime"
                            ),
                            result.getTime(
                                    "EndTime"
                            ),
                            result.getString(
                                    "LessonType"
                            ),
                            result.getString(
                                    "AttendanceStatus"
                            )
                        }
                );
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error loading lessons:\n"
                    + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================================================
    // SEARCH LESSONS
    // =========================================================

    private void searchLessons() {

        String search =
                searchField.getText().trim();

        if (search.isEmpty()) {

            loadLessons();
            return;
        }

        model.setRowCount(0);

        String sql =
                "SELECT l.LessonID, " +
                "sp.FirstName + ' ' + sp.LastName AS StudentName, " +
                "ip.FirstName + ' ' + ip.LastName AS InstructorName, " +
                "v.PlateNumber, " +
                "v.Brand, " +
                "v.Model, " +
                "l.LessonDate, " +
                "l.StartTime, " +
                "l.EndTime, " +
                "l.LessonType, " +
                "l.AttendanceStatus " +
                "FROM Lesson l " +
                "INNER JOIN Student s " +
                "ON l.StudentID = s.StudentID " +
                "INNER JOIN Person sp " +
                "ON s.PersonID = sp.PersonID " +
                "INNER JOIN Instructor i " +
                "ON l.InstructorID = i.InstructorID " +
                "INNER JOIN Person ip " +
                "ON i.PersonID = ip.PersonID " +
                "INNER JOIN Vehicle v " +
                "ON l.VehicleID = v.VehicleID " +
                "WHERE sp.FirstName LIKE ? " +
                "OR sp.LastName LIKE ? " +
                "OR ip.FirstName LIKE ? " +
                "OR ip.LastName LIKE ? " +
                "OR v.PlateNumber LIKE ? " +
                "OR v.Brand LIKE ? " +
                "OR v.Model LIKE ? " +
                "OR l.LessonType LIKE ? " +
                "OR l.AttendanceStatus LIKE ? " +
                "ORDER BY l.LessonDate DESC";

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            String value =
                    "%" + search + "%";

            for (int i = 1; i <= 9; i++) {

                statement.setString(i, value);
            }

            ResultSet result =
                    statement.executeQuery();

            while (result.next()) {

                String vehicle =
                        result.getString(
                                "PlateNumber"
                        );

                String brand =
                        result.getString("Brand");

                String vehicleModel =
                        result.getString("Model");

                if (brand != null &&
                        !brand.trim().isEmpty()) {

                    vehicle += " - " + brand;
                }

                if (vehicleModel != null &&
                        !vehicleModel.trim().isEmpty()) {

                    vehicle += " " + vehicleModel;
                }

                model.addRow(
                        new Object[]{
                            result.getInt("LessonID"),
                            result.getString(
                                    "StudentName"
                            ),
                            result.getString(
                                    "InstructorName"
                            ),
                            vehicle,
                            result.getDate(
                                    "LessonDate"
                            ),
                            result.getTime(
                                    "StartTime"
                            ),
                            result.getTime(
                                    "EndTime"
                            ),
                            result.getString(
                                    "LessonType"
                            ),
                            result.getString(
                                    "AttendanceStatus"
                            )
                        }
                );
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error searching lessons:\n"
                    + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================================================
    // ADD LESSON
    // =========================================================

    private void addLesson() {

        JDialog dialog =
                new JDialog();

        dialog.setTitle("Add Lesson");
        dialog.setModal(true);
        dialog.setSize(550, 600);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel =
                new JPanel(
                        new BorderLayout(10, 10)
                );

        mainPanel.setBackground(
                UI.WHITE
        );

        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        25,
                        30,
                        25,
                        30
                )
        );

        JLabel title =
                new JLabel("Add Lesson");

        UI.styleSectionTitle(title);

        mainPanel.add(
                title,
                BorderLayout.NORTH
        );

        JPanel formPanel =
                new JPanel(
                        new GridBagLayout()
                );

        formPanel.setBackground(
                UI.WHITE
        );

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

        JComboBox<String> vehicleCombo =
                new JComboBox<>();

        JComboBox<String> typeCombo =
                new JComboBox<>(
                        new String[]{
                            "Practical",
                            "Theoretical"
                        }
                );

        JComboBox<String> attendanceCombo =
                new JComboBox<>(
                        new String[]{
                            "Present",
                            "Absent"
                        }
                );

        JTextField dateField =
                new JTextField();

        JTextField startTimeField =
                new JTextField();

        JTextField endTimeField =
                new JTextField();

        JTextField notesField =
                new JTextField();

        UI.styleTextField(dateField);
        UI.styleTextField(startTimeField);
        UI.styleTextField(endTimeField);
        UI.styleTextField(notesField);

        loadStudents(studentCombo);
        loadInstructors(instructorCombo);
        loadAvailableVehicles(vehicleCombo);

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
                "Vehicle",
                vehicleCombo
        );

        addFormRow(
                formPanel,
                gbc,
                3,
                "Lesson Date",
                dateField
        );

        addFormRow(
                formPanel,
                gbc,
                4,
                "Start Time",
                startTimeField
        );

        addFormRow(
                formPanel,
                gbc,
                5,
                "End Time",
                endTimeField
        );

        addFormRow(
                formPanel,
                gbc,
                6,
                "Lesson Type",
                typeCombo
        );

        addFormRow(
                formPanel,
                gbc,
                7,
                "Attendance",
                attendanceCombo
        );

        addFormRow(
                formPanel,
                gbc,
                8,
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

        buttonPanel.setBackground(
                UI.WHITE
        );

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
                    instructorCombo.getSelectedItem() == null ||
                    vehicleCombo.getSelectedItem() == null) {

                JOptionPane.showMessageDialog(
                        dialog,
                        "Please select student, instructor and vehicle."
                );

                return;
            }

            Date lessonDate;

            try {

                lessonDate =
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

            Time startTime;
            Time endTime;

            try {

                startTime =
                        Time.valueOf(
                                startTimeField
                                        .getText()
                                        .trim()
                                        + ":00"
                        );

                endTime =
                        Time.valueOf(
                                endTimeField
                                        .getText()
                                        .trim()
                                        + ":00"
                        );

            } catch (IllegalArgumentException ex) {

                JOptionPane.showMessageDialog(
                        dialog,
                        "Time must be in HH:MM format."
                );

                return;
            }

            if (!endTime.after(startTime)) {

                JOptionPane.showMessageDialog(
                        dialog,
                        "End time must be after start time."
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

            int vehicleID =
                    getIDFromCombo(
                            vehicleCombo
                    );

            String sql =
                    "INSERT INTO Lesson " +
                    "(StudentID, InstructorID, VehicleID, " +
                    "LessonDate, StartTime, EndTime, " +
                    "LessonType, AttendanceStatus, Notes) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

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

                statement.setInt(
                        3,
                        vehicleID
                );

                statement.setDate(
                        4,
                        lessonDate
                );

                statement.setTime(
                        5,
                        startTime
                );

                statement.setTime(
                        6,
                        endTime
                );

                statement.setString(
                        7,
                        typeCombo
                                .getSelectedItem()
                                .toString()
                );

                statement.setString(
                        8,
                        attendanceCombo
                                .getSelectedItem()
                                .toString()
                );

                statement.setString(
                        9,
                        notesField
                                .getText()
                                .trim()
                );

                statement.executeUpdate();

                JOptionPane.showMessageDialog(
                        dialog,
                        "Lesson added successfully."
                );

                dialog.dispose();

                loadLessons();

            } catch (SQLException ex) {

                JOptionPane.showMessageDialog(
                        dialog,
                        "Error adding lesson:\n"
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
    // EDIT LESSON
    // =========================================================

    private void editLesson() {

        int selectedRow =
                table.getSelectedRow();

        if (selectedRow == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a lesson first."
            );

            return;
        }

        int lessonID =
                Integer.parseInt(
                        table.getValueAt(
                                selectedRow,
                                0
                        ).toString()
                );

        String sql =
                "SELECT StudentID, InstructorID, VehicleID, " +
                "LessonDate, StartTime, EndTime, " +
                "LessonType, AttendanceStatus, Notes " +
                "FROM Lesson " +
                "WHERE LessonID = ?";

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(
                    1,
                    lessonID
            );

            ResultSet result =
                    statement.executeQuery();

            if (result.next()) {

                showEditDialog(
                        lessonID,
                        result.getInt("StudentID"),
                        result.getInt("InstructorID"),
                        result.getInt("VehicleID"),
                        result.getDate("LessonDate"),
                        result.getTime("StartTime"),
                        result.getTime("EndTime"),
                        result.getString("LessonType"),
                        result.getString("AttendanceStatus"),
                        result.getString("Notes")
                );
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error loading lesson:\n"
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
            int lessonID,
            int currentStudentID,
            int currentInstructorID,
            int currentVehicleID,
            Date currentDate,
            Time currentStartTime,
            Time currentEndTime,
            String currentType,
            String currentAttendance,
            String currentNotes
    ) {

        JDialog dialog =
                new JDialog();

        dialog.setTitle("Edit Lesson");
        dialog.setModal(true);
        dialog.setSize(550, 600);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel =
                new JPanel(
                        new BorderLayout(10, 10)
                );

        mainPanel.setBackground(
                UI.WHITE
        );

        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        25,
                        30,
                        25,
                        30
                )
        );

        JLabel title =
                new JLabel("Edit Lesson");

        UI.styleSectionTitle(title);

        mainPanel.add(
                title,
                BorderLayout.NORTH
        );

        JPanel formPanel =
                new JPanel(
                        new GridBagLayout()
                );

        formPanel.setBackground(
                UI.WHITE
        );

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

        JComboBox<String> vehicleCombo =
                new JComboBox<>();

        JComboBox<String> typeCombo =
                new JComboBox<>(
                        new String[]{
                            "Practical",
                            "Theoretical"
                        }
                );

        JComboBox<String> attendanceCombo =
                new JComboBox<>(
                        new String[]{
                            "Present",
                            "Absent"
                        }
                );

        JTextField dateField =
                new JTextField(
                        currentDate.toString()
                );

        JTextField startTimeField =
                new JTextField(
                        currentStartTime
                                .toString()
                                .substring(0, 5)
                );

        JTextField endTimeField =
                new JTextField(
                        currentEndTime
                                .toString()
                                .substring(0, 5)
                );

        JTextField notesField =
                new JTextField(
                        currentNotes == null
                                ? ""
                                : currentNotes
                );

        UI.styleTextField(dateField);
        UI.styleTextField(startTimeField);
        UI.styleTextField(endTimeField);
        UI.styleTextField(notesField);

        loadStudentsForEdit(
                studentCombo,
                currentStudentID
        );

        loadInstructorsForEdit(
                instructorCombo,
                currentInstructorID
        );

        /*
         * IMPORTANT:
         *
         * The current vehicle is included even when
         * its status is not Available.
         *
         * This prevents an existing lesson from
         * losing its current vehicle while editing.
         */
        loadVehiclesForEdit(
                vehicleCombo,
                currentVehicleID
        );

        typeCombo.setSelectedItem(
                currentType
        );

        attendanceCombo.setSelectedItem(
                currentAttendance
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
                "Vehicle",
                vehicleCombo
        );

        addFormRow(
                formPanel,
                gbc,
                3,
                "Lesson Date",
                dateField
        );

        addFormRow(
                formPanel,
                gbc,
                4,
                "Start Time",
                startTimeField
        );

        addFormRow(
                formPanel,
                gbc,
                5,
                "End Time",
                endTimeField
        );

        addFormRow(
                formPanel,
                gbc,
                6,
                "Lesson Type",
                typeCombo
        );

        addFormRow(
                formPanel,
                gbc,
                7,
                "Attendance",
                attendanceCombo
        );

        addFormRow(
                formPanel,
                gbc,
                8,
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

        buttonPanel.setBackground(
                UI.WHITE
        );

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

            Date lessonDate;

            try {

                lessonDate =
                        Date.valueOf(
                                dateField
                                        .getText()
                                        .trim()
                        );

            } catch (IllegalArgumentException ex) {

                JOptionPane.showMessageDialog(
                        dialog,
                        "Date must be in YYYY-MM-DD format."
                );

                return;
            }

            Time startTime;
            Time endTime;

            try {

                startTime =
                        Time.valueOf(
                                startTimeField
                                        .getText()
                                        .trim()
                                        + ":00"
                        );

                endTime =
                        Time.valueOf(
                                endTimeField
                                        .getText()
                                        .trim()
                                        + ":00"
                        );

            } catch (IllegalArgumentException ex) {

                JOptionPane.showMessageDialog(
                        dialog,
                        "Time must be in HH:MM format."
                );

                return;
            }

            if (!endTime.after(startTime)) {

                JOptionPane.showMessageDialog(
                        dialog,
                        "End time must be after start time."
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

            int vehicleID =
                    getIDFromCombo(
                            vehicleCombo
                    );

            String sql =
                    "UPDATE Lesson SET " +
                    "StudentID = ?, " +
                    "InstructorID = ?, " +
                    "VehicleID = ?, " +
                    "LessonDate = ?, " +
                    "StartTime = ?, " +
                    "EndTime = ?, " +
                    "LessonType = ?, " +
                    "AttendanceStatus = ?, " +
                    "Notes = ? " +
                    "WHERE LessonID = ?";

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

                statement.setInt(
                        3,
                        vehicleID
                );

                statement.setDate(
                        4,
                        lessonDate
                );

                statement.setTime(
                        5,
                        startTime
                );

                statement.setTime(
                        6,
                        endTime
                );

                statement.setString(
                        7,
                        typeCombo
                                .getSelectedItem()
                                .toString()
                );

                statement.setString(
                        8,
                        attendanceCombo
                                .getSelectedItem()
                                .toString()
                );

                statement.setString(
                        9,
                        notesField
                                .getText()
                                .trim()
                );

                statement.setInt(
                        10,
                        lessonID
                );

                statement.executeUpdate();

                JOptionPane.showMessageDialog(
                        dialog,
                        "Lesson updated successfully."
                );

                dialog.dispose();

                loadLessons();

            } catch (SQLException ex) {

                JOptionPane.showMessageDialog(
                        dialog,
                        "Error updating lesson:\n"
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
    // DELETE LESSON
    // =========================================================

    private void deleteLesson() {

        int selectedRow =
                table.getSelectedRow();

        if (selectedRow == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a lesson first."
            );

            return;
        }

        int lessonID =
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
                        "this lesson?",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION
                );

        if (choice !=
                JOptionPane.YES_OPTION) {

            return;
        }

        String sql =
                "DELETE FROM Lesson " +
                "WHERE LessonID = ?";

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(
                    1,
                    lessonID
            );

            statement.executeUpdate();

            JOptionPane.showMessageDialog(
                    this,
                    "Lesson deleted successfully."
            );

            loadLessons();

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error deleting lesson:\n"
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
                        result.getInt(
                                "InstructorID"
                        )
                        + " - "
                        + result.getString(
                                "FirstName"
                        )
                        + " "
                        + result.getString(
                                "LastName"
                        )
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
                        result.getInt(
                                "InstructorID"
                        )
                        + " - "
                        + result.getString(
                                "FirstName"
                        )
                        + " "
                        + result.getString(
                                "LastName"
                        )
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
    // LOAD AVAILABLE VEHICLES
    // =========================================================

    private void loadAvailableVehicles(
            JComboBox<String> combo
    ) {

        String sql =
                "SELECT VehicleID, PlateNumber, " +
                "Brand, Model " +
                "FROM Vehicle " +
                "WHERE Status = 'Available' " +
                "ORDER BY PlateNumber";

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
                        createVehicleText(result)
                );
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error loading vehicles:\n"
                    + e.getMessage()
            );
        }
    }

    // =========================================================
    // LOAD VEHICLES FOR EDIT
    // =========================================================

    private void loadVehiclesForEdit(
            JComboBox<String> combo,
            int currentVehicleID
    ) {

        String sql =
                "SELECT VehicleID, PlateNumber, " +
                "Brand, Model " +
                "FROM Vehicle " +
                "WHERE Status = 'Available' " +
                "OR VehicleID = ? " +
                "ORDER BY PlateNumber";

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(
                    1,
                    currentVehicleID
            );

            ResultSet result =
                    statement.executeQuery();

            while (result.next()) {

                combo.addItem(
                        createVehicleText(result)
                );
            }

            selectComboByID(
                    combo,
                    currentVehicleID
            );

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error loading vehicles:\n"
                    + e.getMessage()
            );
        }
    }

    // =========================================================
    // CREATE VEHICLE TEXT
    // =========================================================

    private String createVehicleText(
            ResultSet result
    ) throws SQLException {

        String text =
                result.getInt("VehicleID")
                + " - "
                + result.getString("PlateNumber");

        String brand =
                result.getString("Brand");

        String vehicleModel =
                result.getString("Model");

        if (brand != null &&
                !brand.trim().isEmpty()) {

            text += " - " + brand;
        }

        if (vehicleModel != null &&
                !vehicleModel.trim().isEmpty()) {

            text += " " + vehicleModel;
        }

        return text;
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

            if (item.startsWith(
                    id + " - "
            )) {

                combo.setSelectedIndex(i);

                break;
            }
        }
    }

    // =========================================================
    // ADD FORM ROW
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