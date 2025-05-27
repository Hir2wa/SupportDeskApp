package View;

import Controller.UserController;
import model.User;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class EditProfilePageView {
    private JFrame editFrame;
    private UserController userController;
    private User currentUser;
    private static final Color AUCA_BLUE = new Color(0, 51, 153);
    private static final Color AUCA_BLUE_HOVER = new Color(0, 71, 173);
    private static final Color BACKGROUND = new Color(245, 245, 245);

    public EditProfilePageView(User user, UserController controller) {
        this.currentUser = user;
        this.userController = controller;
        String username = user.getUsername();
        String email = user.getEmail();

        BufferedImage fallbackImg = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = fallbackImg.createGraphics();
        g2d.setColor(new Color(0, 102, 204));
        g2d.fillRect(0, 0, 100, 100);
        g2d.dispose();
        ImageIcon defaultPic = new ImageIcon(fallbackImg);
        
        try {
            File imageFile = new File("Assets/LogoSupportDesk.png");
            if (imageFile.exists()) {
                defaultPic = new ImageIcon(imageFile.getAbsolutePath());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        ImageIcon finalProfilePic = defaultPic;

        editFrame = new JFrame("Edit Profile - " + username);
        editFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        editFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        editFrame.setLocationRelativeTo(null);
        editFrame.setLayout(new BorderLayout());
        editFrame.getContentPane().setBackground(BACKGROUND);

        // ==== Main Panel ====
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(BACKGROUND);
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // ==== Top Panel for Back Button ====
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(BACKGROUND);
        topPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JButton backButton = new JButton("← Back to Profile");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setBackground(AUCA_BLUE);
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setPreferredSize(new Dimension(180, 45));
        backButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 1, AUCA_BLUE),
                new EmptyBorder(8, 15, 8, 15)
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

        backButton.addActionListener(e -> {
            editFrame.dispose();
            new ProfileView(currentUser.getUsername(), currentUser.getEmail(), finalProfilePic,
                          0, 0, 0, 0, true); // You'll need to get actual stats
        });

        topPanel.add(backButton);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // ==== Edit Profile Panel ====
        JPanel editPanel = new JPanel(new GridBagLayout());
        editPanel.setBackground(Color.WHITE);
        editPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 1, AUCA_BLUE),
                new EmptyBorder(40, 40, 40, 40)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);

        // ==== Title ====
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JLabel titleLabel = new JLabel("Edit Your Profile");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(AUCA_BLUE);
        editPanel.add(titleLabel, gbc);

        // ==== Profile Picture ====
        gbc.gridy++;
        gbc.insets = new Insets(20, 15, 30, 15);
        JLabel profileLabel = new JLabel();
        profileLabel.setIcon(resizeAndRoundIcon(finalProfilePic, 120, 120));
        profileLabel.setHorizontalAlignment(SwingConstants.CENTER);
        editPanel.add(profileLabel, gbc);

        // ==== Form Fields ====
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 15, 15, 15);

        // Username Field
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = 0.3;
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        usernameLabel.setForeground(AUCA_BLUE);
        editPanel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JTextField usernameField = new JTextField(username, 25);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameField.setPreferredSize(new Dimension(300, 35));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 1, Color.LIGHT_GRAY),
                new EmptyBorder(5, 10, 5, 10)
        ));
        editPanel.add(usernameField, gbc);

        // Email Field
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = 0.3;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Arial", Font.BOLD, 16));
        emailLabel.setForeground(AUCA_BLUE);
        editPanel.add(emailLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JTextField emailField = new JTextField(email, 25);
        emailField.setFont(new Font("Arial", Font.PLAIN, 14));
        emailField.setPreferredSize(new Dimension(300, 35));
        emailField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 1, Color.LIGHT_GRAY),
                new EmptyBorder(5, 10, 5, 10)
        ));
        editPanel.add(emailField, gbc);

        // Password Field
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = 0.3;
        JLabel passwordLabel = new JLabel("Password (optional):");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 16));
        passwordLabel.setForeground(AUCA_BLUE);
        editPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JPasswordField passwordField = new JPasswordField(25);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setPreferredSize(new Dimension(300, 35));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 1, Color.LIGHT_GRAY),
                new EmptyBorder(5, 10, 5, 10)
        ));
        editPanel.add(passwordField, gbc);

        // Save Button
        JButton saveButton = new JButton("💾 Save Changes");
        saveButton.setFont(new Font("Arial", Font.BOLD, 16));
        saveButton.setBackground(AUCA_BLUE);
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.setPreferredSize(new Dimension(200, 50));
        saveButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 1, AUCA_BLUE),
                new EmptyBorder(10, 20, 10, 20)
        ));

        // Hover effect
        saveButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                saveButton.setBackground(AUCA_BLUE_HOVER);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                saveButton.setBackground(AUCA_BLUE);
            }
        });

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(30, 15, 15, 15);
        editPanel.add(saveButton, gbc);

        // ==== Center Edit Panel ====
        mainPanel.add(editPanel, BorderLayout.CENTER);

        // ==== Scroll Pane for Responsiveness ====
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        editFrame.add(scrollPane, BorderLayout.CENTER);
        editFrame.setVisible(true);

        saveButton.addActionListener(e -> {
            System.out.println("Save button clicked");
            
            // First, check if controller is null
            if (userController == null) {
                System.out.println("ERROR: UserController is null!");
                JOptionPane.showMessageDialog(editFrame, 
                    "Internal error: Controller not found.", 
                    "System Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Check if user is null or has invalid ID
            if (currentUser == null) {
                System.out.println("ERROR: Current user is null!");
                JOptionPane.showMessageDialog(editFrame, 
                    "Internal error: User data not found.", 
                    "System Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            System.out.println("Current user ID: " + currentUser.getId());
            
            // Check if the user ID is valid
            if (currentUser.getId() <= 0) {
                System.out.println("ERROR: Invalid user ID: " + currentUser.getId());
                JOptionPane.showMessageDialog(editFrame, 
                    "Cannot update profile: Invalid user ID", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String newUsername = usernameField.getText().trim();
            String newEmail = emailField.getText().trim();
            String newPassword = new String(passwordField.getPassword()).trim();
            
            if (newUsername.isEmpty() || newEmail.isEmpty()) {
                JOptionPane.showMessageDialog(editFrame, 
                    "Username and email cannot be empty!", 
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            System.out.println("Attempting to update user with ID: " + currentUser.getId());
            System.out.println("New username: " + newUsername);
            System.out.println("New email: " + newEmail);
            System.out.println("Password change requested: " + (!newPassword.isEmpty()));
            
            boolean updated = userController.updateUser(
                currentUser.getId(), 
                newUsername, 
                newEmail, 
                newPassword);
            
            System.out.println("Update result: " + (updated ? "SUCCESS" : "FAILED"));
                
            if (updated) {
                currentUser.setUsername(newUsername);
                currentUser.setEmail(newEmail);
                if (!newPassword.isEmpty()) {
                    currentUser.setPassword(newPassword);
                }
                JOptionPane.showMessageDialog(editFrame, 
                    "Profile updated successfully!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                editFrame.dispose();
            } else {
                JOptionPane.showMessageDialog(editFrame, 
                    "Failed to update profile. Please try again.", 
                    "Update Failed", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private ImageIcon resizeAndRoundIcon(ImageIcon originalIcon, int width, int height) {
        Image img = originalIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage roundedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = roundedImage.createGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.fillOval(0, 0, width, height);
        g2d.setComposite(AlphaComposite.SrcIn);
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();

        return new ImageIcon(roundedImage);
    }

    private boolean isValidEmail(String email) {
        return email.contains("@") && email.contains(".") && email.length() > 5;
    }

    private void showSuccessDialog(String message) {
        JOptionPane.showMessageDialog(editFrame, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(editFrame, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}