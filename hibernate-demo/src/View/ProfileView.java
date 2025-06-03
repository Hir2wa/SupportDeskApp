package View;

import Controller.UserController;
import model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ProfileView {
    private JFrame profileFrame;
    private static final Color AUCA_BLUE = new Color(0, 51, 153);
    private static final Color AUCA_BLUE_HOVER = new Color(0, 71, 173);
    private static final Color BACKGROUND = new Color(245, 245, 245); // Light gray for depth

    public ProfileView(String username, String email, ImageIcon profilePic,
                       int issuesSubmitted, int likesReceived,
                       int commentsReceived, int commentsMade, boolean allowEditing) {
        // ==== Frame Setup ====
        profileFrame = new JFrame("Profile - " + username);
        profileFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        profileFrame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Full-screen
        profileFrame.setLocationRelativeTo(null);
        profileFrame.setLayout(new BorderLayout());
        profileFrame.getContentPane().setBackground(BACKGROUND);

        // ==== Main Panel ====
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(BACKGROUND);
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // ==== Top Panel for Back Button ====
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(BACKGROUND);
        topPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setBackground(AUCA_BLUE);
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setPreferredSize(new Dimension(120, 40));
        backButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 1, AUCA_BLUE),
                new EmptyBorder(5, 15, 5, 15)
        ));

        // Hover effect
        backButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                backButton.setBackground(AUCA_BLUE_HOVER);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                backButton.setBackground(AUCA_BLUE);
            }
        });

        // Back button action
        backButton.addActionListener(e -> {
            profileFrame.dispose(); // Close ProfileView
            new HomePageView(username, null); // Open HomePageView
        });

        topPanel.add(backButton);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // ==== Profile Panel ====
        JPanel profilePanel = new JPanel(new GridBagLayout());
        profilePanel.setBackground(Color.WHITE);
        profilePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 1, AUCA_BLUE),
                new EmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(20, 0, 20, 0);
        gbc.anchor = GridBagConstraints.CENTER;

        // ==== Username ====
        gbc.gridy = 0;
        JLabel usernameLabel = new JLabel("@" + username);
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 24));
        usernameLabel.setForeground(AUCA_BLUE);
        profilePanel.add(usernameLabel, gbc);

        // ==== Email ====
        gbc.gridy++;
        JLabel emailLabel = new JLabel(email);
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        emailLabel.setForeground(Color.DARK_GRAY);
        profilePanel.add(emailLabel, gbc);

        // ==== Stats Panel ====
        gbc.gridy++;
        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        statsPanel.setBackground(Color.WHITE);
        statsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createMatteBorder(1, 1, 1, 1, AUCA_BLUE),
                        "Activity Summary",
                        javax.swing.border.TitledBorder.LEFT,
                        javax.swing.border.TitledBorder.TOP,
                        new Font("Arial", Font.BOLD, 16),
                        AUCA_BLUE
                ),
                new EmptyBorder(15, 15, 15, 15)
        ));

        Font statFont = new Font("Arial", Font.PLAIN, 14);
        statsPanel.add(createStatLabel("Issues Submitted: " + issuesSubmitted, statFont));
        statsPanel.add(createStatLabel("Likes Received: " + likesReceived, statFont));
        statsPanel.add(createStatLabel("Comments Received: " + commentsReceived, statFont));
        statsPanel.add(createStatLabel("Comments Made: " + commentsMade, statFont));

        gbc.insets = new Insets(30, 0, 30, 0); // Extra spacing for stats
        profilePanel.add(statsPanel, gbc);

        // ==== Edit Profile Button or View-Only Label ====
        gbc.gridy++;
        gbc.insets = new Insets(20, 0, 20, 0);
        if (allowEditing) {
            JButton editProfileButton = new JButton("Edit Profile");
            editProfileButton.setFont(new Font("Arial", Font.BOLD, 18));
            editProfileButton.setBackground(AUCA_BLUE);
            editProfileButton.setForeground(Color.WHITE);
            editProfileButton.setFocusPainted(false);
            editProfileButton.setPreferredSize(new Dimension(250, 60));
            editProfileButton.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(1, 1, 1, 1, AUCA_BLUE),
                    new EmptyBorder(15, 30, 15, 30)
            ));

            // Hover effect
            editProfileButton.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    editProfileButton.setBackground(AUCA_BLUE_HOVER);
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    editProfileButton.setBackground(AUCA_BLUE);
                }
            });

            // Edit button action
            editProfileButton.addActionListener(e -> {
                UserController controller = new UserController();
                User user = controller.getUserByUsername(username);
                if (user != null) {
                    profileFrame.dispose(); // Close ProfileView
                    new EditProfilePageView(user, controller);
                } else {
                    JOptionPane.showMessageDialog(profileFrame,
                            "Error: Could not retrieve user data from database",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            });
            profilePanel.add(editProfileButton, gbc);
        } else {
            JLabel viewOnlyLabel = new JLabel("Viewing " + username + "'s Profile");
            viewOnlyLabel.setFont(new Font("Arial", Font.ITALIC, 16));
            viewOnlyLabel.setForeground(new Color(100, 100, 100));
            profilePanel.add(viewOnlyLabel, gbc);
        }

        // ==== Center Profile Panel ====
        mainPanel.add(profilePanel, BorderLayout.CENTER);

        // ==== Scroll Pane for Responsiveness ====
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        profileFrame.add(scrollPane, BorderLayout.CENTER);
        profileFrame.setVisible(true);
    }

    private JLabel createStatLabel(String text, Font font) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(Color.DARK_GRAY);
        return label;
    }

    public void setVisible(boolean visible) {
        profileFrame.setVisible(visible);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ProfileView profileView = new ProfileView("..", "...", null, 10, 20, 30, 40, true);
            profileView.setVisible(true);
        });
    }
}