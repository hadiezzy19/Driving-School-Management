package drivingschool;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class Login extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public Login() {

        setTitle("Driving School - Login");

        setSize(900, 550);

        setLocationRelativeTo(null);

        setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
        );

        createUI();
    }

    private void createUI() {

        JPanel mainPanel =
                new JPanel(new BorderLayout());

        mainPanel.setBackground(
                UI.LIGHT_GRAY
        );

        // LEFT PANEL

        JPanel leftPanel =
                new JPanel(new GridBagLayout());

        leftPanel.setBackground(
                UI.NAVY
        );

        leftPanel.setPreferredSize(
                new Dimension(350, 0)
        );

        JLabel schoolLabel =
                new JLabel("DRIVING SCHOOL");

        schoolLabel.setFont(
                UI.TITLE_FONT
        );

        schoolLabel.setForeground(
                UI.WHITE
        );

        JLabel welcomeLabel =
                new JLabel("Management System");

        welcomeLabel.setFont(
                UI.SUBTITLE_FONT
        );

        welcomeLabel.setForeground(
                UI.WHITE
        );

        GridBagConstraints leftGbc =
                new GridBagConstraints();

        leftGbc.gridx = 0;
        leftGbc.gridy = 0;

        leftGbc.insets =
                new Insets(5, 5, 10, 5);

        leftPanel.add(
                schoolLabel,
                leftGbc
        );

        leftGbc.gridy = 1;

        leftPanel.add(
                welcomeLabel,
                leftGbc
        );

        // RIGHT PANEL

        JPanel rightPanel =
                new JPanel(new GridBagLayout());

        rightPanel.setBackground(
                UI.WHITE
        );

        JLabel titleLabel =
                new JLabel("Login");

        UI.styleTitle(
                titleLabel
        );

        JLabel subtitleLabel =
                new JLabel(
                        "Enter your username and password"
                );

        UI.styleSubtitle(
                subtitleLabel
        );

        JLabel usernameLabel =
                new JLabel("Username");

        UI.styleLabel(
                usernameLabel
        );

        usernameField =
                new JTextField();

        UI.styleTextField(
                usernameField
        );

        JLabel passwordLabel =
                new JLabel("Password");

        UI.styleLabel(
                passwordLabel
        );

        passwordField =
                new JPasswordField();

        passwordField.setFont(
                UI.NORMAL_FONT
        );

        passwordField.setForeground(
                UI.DARK_TEXT
        );

        passwordField.setBackground(
                UI.WHITE
        );

        passwordField.setBorder(
                BorderFactory.createLineBorder(
                        UI.BORDER
                )
        );

        passwordField.setPreferredSize(
                new Dimension(200, 35)
        );

        loginButton =
                new JButton("Login");

        UI.styleButton(
                loginButton
        );

        loginButton.setPreferredSize(
                new Dimension(200, 40)
        );

        loginButton.addActionListener(
                e -> login()
        );

        // FORM

        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.gridx = 0;

        gbc.fill =
                GridBagConstraints.HORIZONTAL;

        gbc.insets =
                new Insets(5, 5, 5, 5);

        gbc.gridy = 0;

        rightPanel.add(
                titleLabel,
                gbc
        );

        gbc.gridy = 1;

        rightPanel.add(
                subtitleLabel,
                gbc
        );

        gbc.gridy = 2;

        gbc.insets =
                new Insets(25, 5, 5, 5);

        rightPanel.add(
                usernameLabel,
                gbc
        );

        gbc.gridy = 3;

        gbc.insets =
                new Insets(5, 5, 10, 5);

        rightPanel.add(
                usernameField,
                gbc
        );

        gbc.gridy = 4;

        gbc.insets =
                new Insets(5, 5, 5, 5);

        rightPanel.add(
                passwordLabel,
                gbc
        );

        gbc.gridy = 5;

        gbc.insets =
                new Insets(5, 5, 20, 5);

        rightPanel.add(
                passwordField,
                gbc
        );

        gbc.gridy = 6;

        gbc.insets =
                new Insets(5, 5, 5, 5);

        rightPanel.add(
                loginButton,
                gbc
        );

        mainPanel.add(
                leftPanel,
                BorderLayout.WEST
        );

        mainPanel.add(
                rightPanel,
                BorderLayout.CENTER
        );

        add(mainPanel);
    }

    private void login() {

        String username =
                usernameField.getText().trim();

        String password =
                new String(
                        passwordField.getPassword()
                );

        if (username.isEmpty()
                || password.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter username and password.",
                    "Login",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        String sql =
                "SELECT UserRole, IsActive "
                + "FROM UserAccount "
                + "WHERE Username = ? "
                + "AND Password = ?";

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    username
            );

            statement.setString(
                    2,
                    password
            );

            ResultSet result =
                    statement.executeQuery();

            if (result.next()) {

                boolean active =
                        result.getBoolean(
                                "IsActive"
                        );

                String role =
                        result.getString(
                                "UserRole"
                        );

                if (!active) {

                    JOptionPane.showMessageDialog(
                            this,
                            "This account is inactive.",
                            "Login",
                            JOptionPane.WARNING_MESSAGE
                    );

                    return;
                }

                // SHOW SUCCESS MESSAGE

                JOptionPane.showMessageDialog(
                        this,
                        "Login Successful!\n\n"
                                + "Welcome, "
                                + username
                                + "\n"
                                + "Role: "
                                + role,
                        "Login Successful",
                        JOptionPane.INFORMATION_MESSAGE
                );

                // OPEN DASHBOARD WITH ROLE

                Dashboard dashboard =
                        new Dashboard(role);

                dashboard.setVisible(true);

                // CLOSE LOGIN

                dispose();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Invalid username or password.",
                        "Login Failed",
                        JOptionPane.ERROR_MESSAGE
                );
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Database error:\n"
                            + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );

            System.out.println(
                    "Login error:"
            );

            System.out.println(
                    e.getMessage()
            );
        }
    }

    public static void main(
            String[] args
    ) {

        java.awt.EventQueue.invokeLater(
                () -> {

                    Login login =
                            new Login();

                    login.setVisible(true);
                }
        );
    }
}