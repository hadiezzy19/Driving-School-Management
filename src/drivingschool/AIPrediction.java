package drivingschool;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class AIPrediction extends JDialog {

    private int studentID;
    private String studentName;

    public AIPrediction(
            java.awt.Component parent,
            int studentID,
            String studentName
    ) {

        super(
                javax.swing.SwingUtilities
                        .getWindowAncestor(parent),
                "AI Student Prediction",
                ModalityType.APPLICATION_MODAL
        );

        this.studentID =
                studentID;

        this.studentName =
                studentName;

        createUI();

        loadPrediction();
    }

    // ========================================
    // CREATE UI
    // ========================================

    private void createUI() {

        setSize(
                500,
                600
        );

        setMinimumSize(
                new Dimension(
                        500,
                        600
                )
        );

        setLocationRelativeTo(
                getParent()
        );

        JPanel mainPanel =
                new JPanel(
                        new BorderLayout(
                                15,
                                15
                )
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

        // ====================================
        // TITLE
        // ====================================

        JLabel title =
                new JLabel(
                        "AI Student Prediction",
                        SwingConstants.CENTER
                );

        UI.styleSectionTitle(title);

        mainPanel.add(
                title,
                BorderLayout.NORTH
        );

        // ====================================
        // CENTER
        // ====================================

        JPanel centerPanel =
                new JPanel(
                        new BorderLayout(
                                10,
                                10
                        )
                );

        centerPanel.setBackground(
                UI.WHITE
        );

        centerPanel.setBorder(
                BorderFactory.createLineBorder(
                        UI.BORDER
                )
        );

        predictionLabel =
                new JLabel();

        predictionLabel.setVerticalAlignment(
                SwingConstants.TOP
        );

        predictionLabel.setHorizontalAlignment(
                SwingConstants.LEFT
        );

        predictionLabel.setFont(
                UI.NORMAL_FONT
        );

        predictionLabel.setForeground(
                UI.DARK_TEXT
        );

        centerPanel.add(
                predictionLabel,
                BorderLayout.CENTER
        );

        mainPanel.add(
                centerPanel,
                BorderLayout.CENTER
        );

        // ====================================
        // CLOSE BUTTON
        // ====================================

        JButton closeButton =
                new JButton("Close");

        UI.styleButton(
                closeButton
        );

        closeButton.addActionListener(
                e -> dispose()
        );

        JPanel bottomPanel =
                new JPanel();

        bottomPanel.setBackground(
                UI.WHITE
        );

        bottomPanel.add(
                closeButton
        );

        mainPanel.add(
                bottomPanel,
                BorderLayout.SOUTH
        );

        setContentPane(
                mainPanel
        );
    }

    private JLabel predictionLabel;

    // ========================================
    // LOAD PREDICTION
    // ========================================

    private void loadPrediction() {

        String examSql =
                "SELECT "
                + "COUNT(*) AS ExamCount, "
                + "AVG(Score) AS AverageScore, "
                + "SUM(CASE "
                + "WHEN Result = 'Passed' "
                + "THEN 1 ELSE 0 END) AS PassedExams, "
                + "SUM(CASE "
                + "WHEN Result = 'Failed' "
                + "THEN 1 ELSE 0 END) AS FailedExams "
                + "FROM Exam "
                + "WHERE StudentID = ?";

        String lessonSql =
                "SELECT "
                + "COUNT(*) AS TotalLessons, "
                + "SUM(CASE "
                + "WHEN AttendanceStatus = 'Present' "
                + "THEN 1 ELSE 0 END) AS PresentLessons "
                + "FROM Lesson "
                + "WHERE StudentID = ?";

        int examCount = 0;

        double averageScore = 0;

        int passedExams = 0;

        int failedExams = 0;

        int totalLessons = 0;

        int presentLessons = 0;

        try (
                Connection connection =
                        DBConnection.getConnection()
        ) {

            if (connection == null) {

                showError(
                        "Could not connect to database."
                );

                return;
            }

            // ====================================
            // GET EXAM INFORMATION
            // ====================================

            try (
                    PreparedStatement statement =
                            connection.prepareStatement(
                                    examSql
                            )
            ) {

                statement.setInt(
                        1,
                        studentID
                );

                ResultSet result =
                        statement.executeQuery();

                if (result.next()) {

                    examCount =
                            result.getInt(
                                    "ExamCount"
                            );

                    averageScore =
                            result.getDouble(
                                    "AverageScore"
                            );

                    passedExams =
                            result.getInt(
                                    "PassedExams"
                            );

                    failedExams =
                            result.getInt(
                                    "FailedExams"
                            );
                }
            }

            // ====================================
            // GET LESSON INFORMATION
            // ====================================

            try (
                    PreparedStatement statement =
                            connection.prepareStatement(
                                    lessonSql
                            )
            ) {

                statement.setInt(
                        1,
                        studentID
                );

                ResultSet result =
                        statement.executeQuery();

                if (result.next()) {

                    totalLessons =
                            result.getInt(
                                    "TotalLessons"
                            );

                    presentLessons =
                            result.getInt(
                                    "PresentLessons"
                            );
                }
            }

            // ====================================
            // CALCULATE ATTENDANCE
            // ====================================

            double attendancePercentage =
                    0;

            if (totalLessons > 0) {

                attendancePercentage =
                        (
                                (double) presentLessons
                                / totalLessons
                        ) * 100;
            }

            // ====================================
            // CALCULATE EXAM SCORE
            // ====================================

            double examScorePercentage =
                    averageScore;

            if (examCount == 0) {

                examScorePercentage = 0;
            }

            // ====================================
            // CALCULATE PASS RATE
            // ====================================

            double passRate =
                    0;

            if (examCount > 0) {

                passRate =
                        (
                                (double) passedExams
                                / examCount
                        ) * 100;
            }

            // ====================================
            // CALCULATE LESSON SCORE
            // ====================================

            /*
             * We consider 20 completed lessons
             * as the target for the lesson part
             * of the prediction.
             */

            double lessonScore =
                    Math.min(
                            totalLessons * 5,
                            100
                    );

            // ====================================
            // AI WEIGHTED SCORE
            // ====================================

            /*
             * Exam average       = 40%
             * Attendance         = 25%
             * Passed exams       = 20%
             * Completed lessons  = 15%
             */

            double predictionScore =
                    (
                            examScorePercentage * 0.40
                    )
                    +
                    (
                            attendancePercentage * 0.25
                    )
                    +
                    (
                            passRate * 0.20
                    )
                    +
                    (
                            lessonScore * 0.15
                    );

            // ====================================
            // LIMIT SCORE
            // ====================================

            predictionScore =
                    Math.max(
                            0,
                            Math.min(
                                    predictionScore,
                                    100
                            )
                    );

            int confidence =
                    (int) Math.round(
                            predictionScore
                    );

            // ====================================
            // PREDICTION
            // ====================================

            String prediction;

            if (examCount == 0
                    && totalLessons == 0) {

                prediction =
                        "NOT ENOUGH DATA";

                confidence = 0;

            } else if (predictionScore >= 70) {

                prediction =
                        "LIKELY TO PASS";

            } else if (predictionScore >= 50) {

                prediction =
                        "UNCERTAIN";

            } else {

                prediction =
                        "LIKELY TO FAIL";
            }

            // ====================================
            // DISPLAY
            // ====================================

            String predictionColor;

            if (prediction.equals(
                    "LIKELY TO PASS"
            )) {

                predictionColor =
                        "#2E7D32";

            } else if (prediction.equals(
                    "LIKELY TO FAIL"
            )) {

                predictionColor =
                        "#C62828";

            } else {

                predictionColor =
                        "#EF6C00";
            }

            String html =
                    "<html>"
                    + "<body style='font-family:Segoe UI; "
                    + "font-size:14px;'>"

                    + "<div style='text-align:center;'>"
                    + "<h2>"
                    + studentName
                    + "</h2>"
                    + "</div>"

                    + "<hr>"

                    + "<p><b>Student ID:</b> "
                    + studentID
                    + "</p>"

                    + "<p><b>Previous Exams:</b> "
                    + examCount
                    + "</p>"

                    + "<p><b>Average Score:</b> "
                    + String.format(
                            "%.2f",
                            averageScore
                    )
                    + "</p>"

                    + "<p><b>Passed Exams:</b> "
                    + passedExams
                    + "</p>"

                    + "<p><b>Failed Exams:</b> "
                    + failedExams
                    + "</p>"

                    + "<p><b>Completed Lessons:</b> "
                    + totalLessons
                    + "</p>"

                    + "<p><b>Present Lessons:</b> "
                    + presentLessons
                    + "</p>"

                    + "<p><b>Attendance:</b> "
                    + String.format(
                            "%.2f",
                            attendancePercentage
                    )
                    + "%</p>"

                    + "<br>"

                    + "<div style='text-align:center;'>"

                    + "<h3>"
                    + "AI Prediction"
                    + "</h3>"

                    + "<h2 style='color:"
                    + predictionColor
                    + ";'>"
                    + prediction
                    + "</h2>"

                    + "<p><b>Confidence:</b> "
                    + confidence
                    + "%</p>"

                    + "</div>"

                    + "<br>"

                    + "<hr>"

                    + "<p>"
                    + "<b>Prediction factors:</b>"
                    + "</p>"

                    + "<p>"
                    + "Exam Average: 40%<br>"
                    + "Attendance: 25%<br>"
                    + "Passed Exams: 20%<br>"
                    + "Completed Lessons: 15%"
                    + "</p>"

                    + "</body>"
                    + "</html>";

            predictionLabel.setText(
                    html
            );

        } catch (Exception e) {

            showError(
                    "Could not calculate prediction:\n"
                    + e.getMessage()
            );
        }
    }

    // ========================================
    // SHOW ERROR
    // ========================================

    private void showError(
            String message
    ) {

        predictionLabel.setText(
                "<html><b>Error:</b><br>"
                + message.replace(
                        "\n",
                        "<br>"
                )
                + "</html>"
        );
    }
}