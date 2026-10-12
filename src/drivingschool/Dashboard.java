package drivingschool;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class Dashboard extends JFrame {

    private JPanel contentPanel;
    private CardLayout cardLayout;

    private String userRole;

    private JButton dashboardButton;
    private JButton studentsButton;
    private JButton instructorsButton;
    private JButton vehiclesButton;
    private JButton lessonsButton;
    private JButton examsButton;
    private JButton paymentsButton;
    private JButton logoutButton;

    public Dashboard() {
        this("Administrator");
    }

    public Dashboard(String role) {

        userRole = role;

        setTitle(
                "Driving School - Dashboard"
        );

        setSize(1200, 700);

        setLocationRelativeTo(null);

        setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
        );
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

setExtendedState(JFrame.MAXIMIZED_BOTH);

createUI();

        createUI();
    }

    private void createUI() {

        JPanel mainPanel =
                new JPanel(new BorderLayout());

        mainPanel.setBackground(
                UI.LIGHT_GRAY
        );

        // SIDEBAR

        JPanel sidebar =
                new JPanel();

        sidebar.setLayout(
                new FlowLayout(
                        FlowLayout.CENTER,
                        0,
                        10
                )
        );

        sidebar.setPreferredSize(
                new Dimension(210, 0)
        );

        UI.styleSidebar(sidebar);

        JLabel logoLabel =
                new JLabel("DRIVING SCHOOL");

        logoLabel.setFont(
                UI.BUTTON_FONT
        );

        logoLabel.setForeground(
                UI.WHITE
        );

        sidebar.add(logoLabel);

        dashboardButton =
                createSidebarButton(
                        "Dashboard"
                );

        studentsButton =
                createSidebarButton(
                        "Students"
                );

        instructorsButton =
                createSidebarButton(
                        "Instructors"
                );

        vehiclesButton =
                createSidebarButton(
                        "Vehicles"
                );

        lessonsButton =
                createSidebarButton(
                        "Lessons"
                );

        examsButton =
                createSidebarButton(
                        "Exams"
                );

        paymentsButton =
                createSidebarButton(
                        "Payments"
                );

        logoutButton =
                createSidebarButton(
                        "Logout"
                );

        sidebar.add(dashboardButton);
        sidebar.add(studentsButton);

        // ADMIN ONLY

        if (isAdministrator()) {

            sidebar.add(
                    instructorsButton
            );

            sidebar.add(
                    vehiclesButton
            );
        }

        sidebar.add(lessonsButton);
        sidebar.add(examsButton);
        sidebar.add(paymentsButton);

        sidebar.add(logoutButton);

        // CONTENT

        cardLayout =
                new CardLayout();

        contentPanel =
                new JPanel(cardLayout);

        contentPanel.setBackground(
                UI.LIGHT_GRAY
        );

        createDashboardPage();

        contentPanel.add(
                new Students(),
                "Students"
        );

        if (isAdministrator()) {

            contentPanel.add(
                    new Instructors(),
                    "Instructors"
            );

            contentPanel.add(
                    new Vehicles(),
                    "Vehicles"
            );
        }

        contentPanel.add(
                new Lessons(),
                "Lessons"
        );

        contentPanel.add(
                new Exams(),
                "Exams"
        );

        contentPanel.add(
                new Payments(),
                "Payments"
        );

        // BUTTON ACTIONS

        dashboardButton.addActionListener(
                e -> showPage("Dashboard")
        );

        studentsButton.addActionListener(
                e -> showPage("Students")
        );

        if (isAdministrator()) {

            instructorsButton.addActionListener(
                    e -> showPage("Instructors")
            );

            vehiclesButton.addActionListener(
                    e -> showPage("Vehicles")
            );
        }

        lessonsButton.addActionListener(
                e -> showPage("Lessons")
        );

        examsButton.addActionListener(
                e -> showPage("Exams")
        );

        paymentsButton.addActionListener(
                e -> showPage("Payments")
        );

        logoutButton.addActionListener(
                e -> logout()
        );

        mainPanel.add(
                sidebar,
                BorderLayout.WEST
        );

        mainPanel.add(
                contentPanel,
                BorderLayout.CENTER
        );

        add(mainPanel);
    }

    private JButton createSidebarButton(
            String text
    ) {

        JButton button =
                new JButton(text);

        button.setPreferredSize(
                new Dimension(190, 45)
        );

        UI.styleSidebarButton(button);

        return button;
    }

    private boolean isAdministrator() {

        return userRole != null
                && userRole.equalsIgnoreCase(
                        "Administrator"
                );
    }

    private void showPage(
            String page
    ) {

        cardLayout.show(
                contentPanel,
                page
        );
    }

    private void createDashboardPage() {

        JPanel dashboardPanel =
                new JPanel(
                        new BorderLayout()
                );

        dashboardPanel.setBackground(
                UI.LIGHT_GRAY
        );

        JPanel headerPanel =
                new JPanel(
                        new BorderLayout()
                );

        headerPanel.setBackground(
                UI.LIGHT_GRAY
        );

        headerPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        25,
                        30,
                        15,
                        30
                )
        );

        JLabel titleLabel =
                new JLabel("Dashboard");

        UI.styleTitle(titleLabel);

        JLabel subtitleLabel =
                new JLabel(
                        "Driving School Management Overview"
                );

        UI.styleSubtitle(
                subtitleLabel
        );

        JPanel titlePanel =
                new JPanel();

        titlePanel.setLayout(
                new GridLayout(
                        2,
                        1
                )
        );

        titlePanel.setOpaque(false);

        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);

        JLabel roleLabel =
                new JLabel(
                        "Role: "
                                + userRole
                );

        roleLabel.setFont(
                UI.BUTTON_FONT
        );

        roleLabel.setForeground(
                UI.BLUE
        );

        headerPanel.add(
                titlePanel,
                BorderLayout.WEST
        );

        headerPanel.add(
                roleLabel,
                BorderLayout.EAST
        );

        dashboardPanel.add(
                headerPanel,
                BorderLayout.NORTH
        );

        JPanel cardsPanel =
                new JPanel(
                        new GridLayout(
                                2,
                                4,
                                20,
                                20
                        )
                );

        cardsPanel.setBackground(
                UI.LIGHT_GRAY
        );

        cardsPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        20,
                        30,
                        30,
                        30
                )
        );

        cardsPanel.add(
                createCard(
                        "Students",
                        getCount(
                                "SELECT COUNT(*) FROM Student"
                        ),
                        UI.BLUE
                )
        );

        cardsPanel.add(
                createCard(
                        "Instructors",
                        getCount(
                                "SELECT COUNT(*) FROM Instructor"
                        ),
                        UI.NAVY
                )
        );

        cardsPanel.add(
                createCard(
                        "Vehicles",
                        getCount(
                                "SELECT COUNT(*) FROM Vehicle"
                        ),
                        new Color(52, 152, 219)
                )
        );

        cardsPanel.add(
                createCard(
                        "Lessons",
                        getCount(
                                "SELECT COUNT(*) FROM Lesson"
                        ),
                        new Color(46, 125, 50)
                )
        );

        cardsPanel.add(
                createCard(
                        "Exams",
                        getCount(
                                "SELECT COUNT(*) FROM Exam"
                        ),
                        new Color(123, 31, 162)
                )
        );

        cardsPanel.add(
                createCard(
                        "Payments",
                        getCount(
                                "SELECT COUNT(*) FROM Payment"
                        ),
                        new Color(230, 126, 34)
                )
        );

        cardsPanel.add(
                createCard(
                        "Passed Exams",
                        getCount(
                                "SELECT COUNT(*) "
                                + "FROM Exam "
                                + "WHERE Result = 'Passed'"
                        ),
                        new Color(39, 174, 96)
                )
        );

        cardsPanel.add(
                createCard(
                        "Available Vehicles",
                        getCount(
                                "SELECT COUNT(*) "
                                + "FROM Vehicle "
                                + "WHERE Status = 'Available'"
                        ),
                        new Color(41, 128, 185)
                )
        );

        dashboardPanel.add(
                cardsPanel,
                BorderLayout.CENTER
        );

        contentPanel.add(
                dashboardPanel,
                "Dashboard"
        );
    }

    private JPanel createCard(
            String title,
            int value,
            Color color
    ) {

        JPanel card =
                new JPanel(
                        new BorderLayout()
                );

        card.setBackground(
                UI.WHITE
        );

        card.setBorder(
                BorderFactory.createLineBorder(
                        UI.BORDER
                )
        );

        JLabel titleLabel =
                new JLabel(title);

        titleLabel.setFont(
                UI.NORMAL_FONT
        );

        titleLabel.setForeground(
                UI.GRAY_TEXT
        );

        titleLabel.setBorder(
                BorderFactory.createEmptyBorder(
                        15,
                        15,
                        5,
                        15
                )
        );

        JLabel valueLabel =
                new JLabel(
                        String.valueOf(value)
                );

        valueLabel.setFont(
                new java.awt.Font(
                        "Segoe UI",
                        java.awt.Font.BOLD,
                        30
                )
        );

        valueLabel.setForeground(
                color
        );

        valueLabel.setBorder(
                BorderFactory.createEmptyBorder(
                        5,
                        15,
                        15,
                        15
                )
        );

        card.add(
                titleLabel,
                BorderLayout.NORTH
        );

        card.add(
                valueLabel,
                BorderLayout.CENTER
        );

        return card;
    }

    private int getCount(
            String sql
    ) {

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet result =
                        statement.executeQuery()
        ) {

            if (result.next()) {

                return result.getInt(1);
            }

        } catch (Exception e) {

            System.out.println(
                    "Dashboard error: "
                            + e.getMessage()
            );
        }

        return 0;
    }

    private void logout() {

        int answer =
                JOptionPane.showConfirmDialog(
                        this,
                        "Are you sure you want to logout?",
                        "Logout",
                        JOptionPane.YES_NO_OPTION
                );

        if (answer ==
                JOptionPane.YES_OPTION) {

            Login login =
                    new Login();

            login.setVisible(true);

            dispose();
        }
    }

    public static void main(
            String[] args
    ) {

        java.awt.EventQueue.invokeLater(
                () -> {

                    Dashboard dashboard =
                            new Dashboard(
                                    "Administrator"
                            );

                    dashboard.setVisible(true);
                }
        );
    }
}