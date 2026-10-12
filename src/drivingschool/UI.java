package drivingschool;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class UI {

    // ========================================
    // COLORS
    // ========================================

    // Dark navy - sidebar
    public static final Color NAVY =
            new Color(24, 40, 59);

    // Main blue - buttons and selected items
    public static final Color BLUE =
            new Color(43, 99, 230);

    // White
    public static final Color WHITE =
            Color.WHITE;

    // Main light background
    public static final Color LIGHT_GRAY =
            new Color(244, 246, 248);

    // Main text
    public static final Color DARK_TEXT =
            new Color(40, 40, 40);

    // Secondary text
    public static final Color GRAY_TEXT =
            new Color(100, 110, 120);

    // Border color
    public static final Color BORDER =
            new Color(220, 224, 230);


    // ========================================
    // FONTS
    // ========================================

    // Main page title
    public static final Font TITLE_FONT =
            new Font("Segoe UI", Font.BOLD, 28);

    // Section title
    public static final Font SECTION_FONT =
            new Font("Segoe UI", Font.BOLD, 20);

    // Subtitle
    public static final Font SUBTITLE_FONT =
            new Font("Segoe UI", Font.PLAIN, 14);

    // Button text
    public static final Font BUTTON_FONT =
            new Font("Segoe UI", Font.BOLD, 14);

    // Normal text
    public static final Font NORMAL_FONT =
            new Font("Segoe UI", Font.PLAIN, 14);

    // Small text
    public static final Font SMALL_FONT =
            new Font("Segoe UI", Font.PLAIN, 12);


    // ========================================
    // BUTTON STYLE
    // ========================================

    public static void styleButton(JButton button) {

        button.setBackground(BLUE);
        button.setForeground(WHITE);
        button.setFont(BUTTON_FONT);

        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);

        button.setCursor(
                new java.awt.Cursor(
                        java.awt.Cursor.HAND_CURSOR
                )
        );
    }


    // ========================================
    // SIDEBAR STYLE
    // ========================================

    public static void styleSidebar(JPanel panel) {

        panel.setBackground(NAVY);
    }


    // ========================================
    // SIDEBAR BUTTON STYLE
    // ========================================

    public static void styleSidebarButton(JButton button) {

        button.setBackground(NAVY);
        button.setForeground(WHITE);
        button.setFont(NORMAL_FONT);

        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);

        button.setHorizontalAlignment(
                JButton.LEFT
        );

        button.setCursor(
                new java.awt.Cursor(
                        java.awt.Cursor.HAND_CURSOR
                )
        );
    }


    // ========================================
    // SELECTED SIDEBAR BUTTON
    // ========================================

    public static void styleSelectedSidebarButton(
            JButton button) {

        button.setBackground(BLUE);
        button.setForeground(WHITE);
        button.setFont(BUTTON_FONT);

        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);

        button.setHorizontalAlignment(
                JButton.LEFT
        );
    }


    // ========================================
    // PANEL STYLE
    // ========================================

    public static void stylePanel(JPanel panel) {

        panel.setBackground(WHITE);

        panel.setBorder(
                BorderFactory.createLineBorder(
                        BORDER
                )
        );
    }


    // ========================================
    // SIMPLE PANEL STYLE
    // ========================================

    public static void styleSimplePanel(JPanel panel) {

        panel.setBackground(WHITE);

        panel.setBorder(null);
    }


    // ========================================
    // TITLE STYLE
    // ========================================

    public static void styleTitle(JLabel label) {

        label.setFont(TITLE_FONT);
        label.setForeground(DARK_TEXT);
    }


    // ========================================
    // SECTION TITLE STYLE
    // ========================================

    public static void styleSectionTitle(
            JLabel label) {

        label.setFont(SECTION_FONT);
        label.setForeground(DARK_TEXT);
    }


    // ========================================
    // SUBTITLE STYLE
    // ========================================

    public static void styleSubtitle(
            JLabel label) {

        label.setFont(SUBTITLE_FONT);
        label.setForeground(GRAY_TEXT);
    }


    // ========================================
    // NORMAL LABEL STYLE
    // ========================================

    public static void styleLabel(
            JLabel label) {

        label.setFont(NORMAL_FONT);
        label.setForeground(DARK_TEXT);
    }


    // ========================================
    // SMALL LABEL STYLE
    // ========================================

    public static void styleSmallLabel(
            JLabel label) {

        label.setFont(SMALL_FONT);
        label.setForeground(GRAY_TEXT);
    }


    // ========================================
    // TEXT FIELD STYLE
    // ========================================

    public static void styleTextField(
            JTextField field) {

        field.setFont(NORMAL_FONT);
        field.setForeground(DARK_TEXT);
        field.setBackground(WHITE);

        field.setBorder(
                BorderFactory.createLineBorder(
                        BORDER
                )
        );

        field.setPreferredSize(
                new Dimension(200, 35)
        );
    }
}