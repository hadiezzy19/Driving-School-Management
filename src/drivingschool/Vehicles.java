package drivingschool;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
import javax.swing.table.DefaultTableModel;

public class Vehicles extends JPanel {

    private JTable vehicleTable;
    private DefaultTableModel tableModel;

    private JTextField searchField;

    // ========================================
    // CONSTRUCTOR
    // ========================================

    public Vehicles() {

        createUI();
        loadVehicles();
    }

    // ========================================
    // CREATE UI
    // ========================================

    private void createUI() {

        setLayout(new BorderLayout());

        setBackground(
                UI.LIGHT_GRAY
        );

        setBorder(
                BorderFactory.createEmptyBorder(
                        30,
                        30,
                        30,
                        30
                )
        );

        // ========================================
        // TOP SECTION
        // ========================================

        JPanel topPanel =
                new JPanel(
                        new BorderLayout()
                );

        topPanel.setBackground(
                UI.LIGHT_GRAY
        );

        JLabel title =
                new JLabel("Vehicles");

        UI.styleTitle(title);

        topPanel.add(
                title,
                BorderLayout.WEST
        );

        // ========================================
        // SEARCH
        // ========================================

        JPanel searchPanel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT,
                                10,
                                0
                        )
                );

        searchPanel.setBackground(
                UI.LIGHT_GRAY
        );

        searchField =
                new JTextField();

        searchField.setPreferredSize(
                new Dimension(
                        220,
                        35
                )
        );

        UI.styleTextField(
                searchField
        );

        JButton searchButton =
                new JButton("Search");

        UI.styleButton(
                searchButton
        );

        JButton refreshButton =
                new JButton("Refresh");

        UI.styleButton(
                refreshButton
        );

        searchPanel.add(
                searchField
        );

        searchPanel.add(
                searchButton
        );

        searchPanel.add(
                refreshButton
        );

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
            "Vehicle ID",
            "Plate Number",
            "Brand",
            "Model",
            "Year",
            "Vehicle Type",
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

        vehicleTable =
                new JTable(
                        tableModel
                );

        vehicleTable.setFont(
                UI.NORMAL_FONT
        );

        vehicleTable.setRowHeight(
                32
        );

        vehicleTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        vehicleTable.getTableHeader()
                .setFont(
                        new Font(
                                "Segoe UI",
                                Font.BOLD,
                                13
                        )
                );

        vehicleTable.getTableHeader()
                .setPreferredSize(
                        new Dimension(
                                0,
                                35
                        )
                );

        JScrollPane scrollPane =
                new JScrollPane(
                        vehicleTable
                );

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
                new JButton("Add Vehicle");

        JButton editButton =
                new JButton("Edit Vehicle");

        JButton deleteButton =
                new JButton("Delete Vehicle");

        UI.styleButton(
                addButton
        );

        UI.styleButton(
                editButton
        );

        UI.styleButton(
                deleteButton
        );

        bottomPanel.add(
                addButton
        );

        bottomPanel.add(
                editButton
        );

        bottomPanel.add(
                deleteButton
        );

        add(
                bottomPanel,
                BorderLayout.SOUTH
        );

        // ========================================
        // BUTTON ACTIONS
        // ========================================

        searchButton.addActionListener(
                e -> searchVehicles()
        );

        refreshButton.addActionListener(
                e -> loadVehicles()
        );

        addButton.addActionListener(
                e -> addVehicle()
        );

        editButton.addActionListener(
                e -> editVehicle()
        );

        deleteButton.addActionListener(
                e -> deleteVehicle()
        );

        searchField.addActionListener(
                e -> searchVehicles()
        );
    }

    // ========================================
    // LOAD VEHICLES
    // ========================================

    private void loadVehicles() {

        tableModel.setRowCount(0);

        String sql =
                "SELECT VehicleID, "
                + "PlateNumber, "
                + "Brand, "
                + "Model, "
                + "VehicleYear, "
                + "VehicleType, "
                + "Status "
                + "FROM Vehicle "
                + "ORDER BY VehicleID DESC";

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(
                                sql
                        );

                ResultSet result =
                        statement.executeQuery()
        ) {

            while (result.next()) {

                tableModel.addRow(
                        new Object[]{
                            result.getInt(
                                    "VehicleID"
                            ),
                            result.getString(
                                    "PlateNumber"
                            ),
                            result.getString(
                                    "Brand"
                            ),
                            result.getString(
                                    "Model"
                            ),
                            result.getObject(
                                    "VehicleYear"
                            ),
                            result.getString(
                                    "VehicleType"
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
                    "Error loading vehicles:\n"
                    + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // ========================================
    // SEARCH VEHICLES
    // ========================================

    private void searchVehicles() {

        String search =
                searchField.getText().trim();

        if (search.isEmpty()) {

            loadVehicles();

            return;
        }

        tableModel.setRowCount(0);

        String sql =
                "SELECT VehicleID, "
                + "PlateNumber, "
                + "Brand, "
                + "Model, "
                + "VehicleYear, "
                + "VehicleType, "
                + "Status "
                + "FROM Vehicle "
                + "WHERE PlateNumber LIKE ? "
                + "OR Brand LIKE ? "
                + "OR Model LIKE ? "
                + "OR VehicleType LIKE ? "
                + "OR Status LIKE ? "
                + "ORDER BY VehicleID DESC";

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(
                                sql
                        )
        ) {

            String value =
                    "%"
                    + search
                    + "%";

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

            statement.setString(
                    5,
                    value
            );

            ResultSet result =
                    statement.executeQuery();

            while (result.next()) {

                tableModel.addRow(
                        new Object[]{
                            result.getInt(
                                    "VehicleID"
                            ),
                            result.getString(
                                    "PlateNumber"
                            ),
                            result.getString(
                                    "Brand"
                            ),
                            result.getString(
                                    "Model"
                            ),
                            result.getObject(
                                    "VehicleYear"
                            ),
                            result.getString(
                                    "VehicleType"
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
                    "Error searching vehicles:\n"
                    + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // ========================================
    // ADD VEHICLE
    // ========================================

    private void addVehicle() {

        JTextField plateField =
                new JTextField();

        JTextField brandField =
                new JTextField();

        JTextField modelField =
                new JTextField();

        JTextField yearField =
                new JTextField();

        JTextField typeField =
                new JTextField();

        String[] statusOptions = {
            "Available",
            "In Use",
            "Maintenance",
            "Inactive"
        };

        JComboBox<String> statusCombo =
                new JComboBox<>(
                        statusOptions
                );

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
                new JLabel(
                        "Plate Number:"
                )
        );

        formPanel.add(
                plateField
        );

        formPanel.add(
                new JLabel(
                        "Brand:"
                )
        );

        formPanel.add(
                brandField
        );

        formPanel.add(
                new JLabel(
                        "Model:"
                )
        );

        formPanel.add(
                modelField
        );

        formPanel.add(
                new JLabel(
                        "Year:"
                )
        );

        formPanel.add(
                yearField
        );

        formPanel.add(
                new JLabel(
                        "Vehicle Type:"
                )
        );

        formPanel.add(
                typeField
        );

        formPanel.add(
                new JLabel(
                        "Status:"
                )
        );

        formPanel.add(
                statusCombo
        );

        int result =
                JOptionPane.showConfirmDialog(
                        this,
                        formPanel,
                        "Add Vehicle",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE
                );

        if (result
                != JOptionPane.OK_OPTION) {

            return;
        }

        String plate =
                plateField.getText().trim();

        String brand =
                brandField.getText().trim();

        String model =
                modelField.getText().trim();

        String yearText =
                yearField.getText().trim();

        String type =
                typeField.getText().trim();

        String status =
                statusCombo
                        .getSelectedItem()
                        .toString();

        // ========================================
        // VALIDATION
        // ========================================

        if (plate.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Plate Number is required.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        Integer vehicleYear = null;

        if (!yearText.isEmpty()) {

            try {

                vehicleYear =
                        Integer.parseInt(
                                yearText
                        );

            } catch (NumberFormatException e) {

                JOptionPane.showMessageDialog(
                        this,
                        "Year must be a number.",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }
        }

        // ========================================
        // INSERT
        // ========================================

        String sql =
                "INSERT INTO Vehicle "
                + "(PlateNumber, Brand, Model, "
                + "VehicleYear, VehicleType, Status) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(
                                sql
                        )
        ) {

            statement.setString(
                    1,
                    plate
            );

            statement.setString(
                    2,
                    brand
            );

            statement.setString(
                    3,
                    model
            );

            if (vehicleYear == null) {

                statement.setNull(
                        4,
                        java.sql.Types.INTEGER
                );

            } else {

                statement.setInt(
                        4,
                        vehicleYear
                );
            }

            statement.setString(
                    5,
                    type
            );

            statement.setString(
                    6,
                    status
            );

            statement.executeUpdate();

            JOptionPane.showMessageDialog(
                    this,
                    "Vehicle added successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            loadVehicles();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error adding vehicle:\n"
                    + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // ========================================
    // EDIT VEHICLE
    // ========================================

    private void editVehicle() {

        int selectedRow =
                vehicleTable.getSelectedRow();

        if (selectedRow == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a vehicle first.",
                    "Edit Vehicle",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        int vehicleID =
                (int) tableModel.getValueAt(
                        selectedRow,
                        0
                );

        String currentPlate =
                tableModel.getValueAt(
                        selectedRow,
                        1
                ).toString();

        String currentBrand =
                tableModel.getValueAt(
                        selectedRow,
                        2
                ) == null
                        ? ""
                        : tableModel.getValueAt(
                                selectedRow,
                                2
                        ).toString();

        String currentModel =
                tableModel.getValueAt(
                        selectedRow,
                        3
                ) == null
                        ? ""
                        : tableModel.getValueAt(
                                selectedRow,
                                3
                        ).toString();

        String currentYear =
                tableModel.getValueAt(
                        selectedRow,
                        4
                ) == null
                        ? ""
                        : tableModel.getValueAt(
                                selectedRow,
                                4
                        ).toString();

        String currentType =
                tableModel.getValueAt(
                        selectedRow,
                        5
                ) == null
                        ? ""
                        : tableModel.getValueAt(
                                selectedRow,
                                5
                        ).toString();

        String currentStatus =
                tableModel.getValueAt(
                        selectedRow,
                        6
                ).toString();

        JTextField plateField =
                new JTextField(
                        currentPlate
                );

        JTextField brandField =
                new JTextField(
                        currentBrand
                );

        JTextField modelField =
                new JTextField(
                        currentModel
                );

        JTextField yearField =
                new JTextField(
                        currentYear
                );

        JTextField typeField =
                new JTextField(
                        currentType
                );

        String[] statusOptions = {
            "Available",
            "In Use",
            "Maintenance",
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
                                6,
                                2,
                                10,
                                10
                        )
                );

        formPanel.add(
                new JLabel(
                        "Plate Number:"
                )
        );

        formPanel.add(
                plateField
        );

        formPanel.add(
                new JLabel(
                        "Brand:"
                )
        );

        formPanel.add(
                brandField
        );

        formPanel.add(
                new JLabel(
                        "Model:"
                )
        );

        formPanel.add(
                modelField
        );

        formPanel.add(
                new JLabel(
                        "Year:"
                )
        );

        formPanel.add(
                yearField
        );

        formPanel.add(
                new JLabel(
                        "Vehicle Type:"
                )
        );

        formPanel.add(
                typeField
        );

        formPanel.add(
                new JLabel(
                        "Status:"
                )
        );

        formPanel.add(
                statusCombo
        );

        int result =
                JOptionPane.showConfirmDialog(
                        this,
                        formPanel,
                        "Edit Vehicle",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE
                );

        if (result
                != JOptionPane.OK_OPTION) {

            return;
        }

        String plate =
                plateField.getText().trim();

        String brand =
                brandField.getText().trim();

        String model =
                modelField.getText().trim();

        String yearText =
                yearField.getText().trim();

        String type =
                typeField.getText().trim();

        String status =
                statusCombo
                        .getSelectedItem()
                        .toString();

        if (plate.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Plate Number is required.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        Integer vehicleYear = null;

        if (!yearText.isEmpty()) {

            try {

                vehicleYear =
                        Integer.parseInt(
                                yearText
                        );

            } catch (NumberFormatException e) {

                JOptionPane.showMessageDialog(
                        this,
                        "Year must be a number.",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }
        }

        // ========================================
        // UPDATE
        // ========================================

        String sql =
                "UPDATE Vehicle "
                + "SET PlateNumber = ?, "
                + "Brand = ?, "
                + "Model = ?, "
                + "VehicleYear = ?, "
                + "VehicleType = ?, "
                + "Status = ? "
                + "WHERE VehicleID = ?";

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(
                                sql
                        )
        ) {

            statement.setString(
                    1,
                    plate
            );

            statement.setString(
                    2,
                    brand
            );

            statement.setString(
                    3,
                    model
            );

            if (vehicleYear == null) {

                statement.setNull(
                        4,
                        java.sql.Types.INTEGER
                );

            } else {

                statement.setInt(
                        4,
                        vehicleYear
                );
            }

            statement.setString(
                    5,
                    type
            );

            statement.setString(
                    6,
                    status
            );

            statement.setInt(
                    7,
                    vehicleID
            );

            statement.executeUpdate();

            JOptionPane.showMessageDialog(
                    this,
                    "Vehicle updated successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            loadVehicles();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error updating vehicle:\n"
                    + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // ========================================
    // DELETE VEHICLE
    // ========================================

    private void deleteVehicle() {

        int selectedRow =
                vehicleTable.getSelectedRow();

        if (selectedRow == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a vehicle first.",
                    "Delete Vehicle",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        int vehicleID =
                (int) tableModel.getValueAt(
                        selectedRow,
                        0
                );

        String plate =
                tableModel.getValueAt(
                        selectedRow,
                        1
                ).toString();

        int confirmation =
                JOptionPane.showConfirmDialog(
                        this,
                        "Are you sure you want to delete\n"
                        + "vehicle "
                        + plate
                        + "?",
                        "Delete Vehicle",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

        if (confirmation
                != JOptionPane.YES_OPTION) {

            return;
        }

        String sql =
                "DELETE FROM Vehicle "
                + "WHERE VehicleID = ?";

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(
                                sql
                        )
        ) {

            statement.setInt(
                    1,
                    vehicleID
            );

            statement.executeUpdate();

            JOptionPane.showMessageDialog(
                    this,
                    "Vehicle deleted successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            loadVehicles();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Could not delete vehicle.\n\n"
                    + "This vehicle may already be "
                    + "connected to a lesson.\n\n"
                    + e.getMessage(),
                    "Delete Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}