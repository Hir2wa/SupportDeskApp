package View;

import Controller.UserController;
import model.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.logging.Logger;

public class LoginView {
    private static final Logger LOGGER = Logger.getLogger(LoginView.class.getName());

    private JFrame loginFrame;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton, registerButton, forgotPasswordButton;
    private UserController userController;

    public LoginView() {
        userController = new UserController();
        Color aucaBlue = new Color(0, 51, 153);
        loginFrame = new JFrame("Login - Support Desk");
        loginFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        mainPanel.setBackground(Color.WHITE);

        // Add Logo
        JLabel logoLabel = new JLabel();
        URL logoUrl = getClass().getResource("/Assets/LogoSupportDesk.png");
        System.out.println("Looking for logo at: " + (logoUrl != null ? logoUrl.toString() : "null")); // Debug
        ImageIcon logoIcon = (logoUrl != null) ? new ImageIcon(logoUrl) : null;
        if (logoIcon != null && logoIcon.getImage() != null && logoIcon.getImageLoadStatus() == MediaTracker.COMPLETE) {
            Image img = logoIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
            logoIcon = new ImageIcon(img);
            logoLabel.setIcon(logoIcon);
        } else {
            logoLabel.setText("Logo not found - Check /Assets/LogoSupportDesk.png in src");
            logoLabel.setForeground(Color.RED);
            System.err.println("Failed to load logo. Ensure Assets/LogoSupportDesk.png is in src and copied to bin.");
        }
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(logoLabel);
        mainPanel.add(Box.createVerticalStrut(10));

        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BorderLayout());
        titlePanel.setBorder(BorderFactory.createLineBorder(aucaBlue, 2));
        titlePanel.setBackground(new Color(240, 248, 255)); // Light blue background
        JLabel titleLabel = new JLabel("SUPPORT DESK");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(aucaBlue);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        mainPanel.add(titlePanel);
        mainPanel.add(Box.createVerticalStrut(20));

        // Welcome Label
        JLabel loginLabel = new JLabel("<html><center>Welcome Back!<br>Login to your account</center></html>");
        loginLabel.setHorizontalAlignment(SwingConstants.CENTER);
        loginLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginLabel.setFont(new Font("Arial", Font.BOLD, 20)); // Larger and bolder
        mainPanel.add(loginLabel);
        mainPanel.add(Box.createVerticalStrut(30));

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        usernameField = new JTextField(15);
        addFormRow(formPanel, gbc, 0, "Username:", usernameField);

        passwordField = new JPasswordField(15);
        addFormRow(formPanel, gbc, 1, "Password:", passwordField);

        loginButton = new JButton("Login");
        loginButton.setBackground(aucaBlue);
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(loginButton, gbc);

        registerButton = new JButton("Register");
        registerButton.setForeground(aucaBlue);
        registerButton.setBorder(BorderFactory.createLineBorder(aucaBlue));
        registerButton.setBackground(Color.WHITE);

        gbc.gridy = 3;
        formPanel.add(registerButton, gbc);

        forgotPasswordButton = new JButton("Forgot Password?");
        forgotPasswordButton.setForeground(aucaBlue);
        forgotPasswordButton.setBorderPainted(false);
        forgotPasswordButton.setContentAreaFilled(false);
        forgotPasswordButton.setFocusPainted(false);

        gbc.gridy = 4;
        formPanel.add(forgotPasswordButton, gbc);

        mainPanel.add(formPanel);

        loginFrame.getContentPane().add(mainPanel);
        loginFrame.setVisible(true);

        // Login Button Action (unchanged)
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword()).trim();

                if (username.isEmpty() || password.isEmpty()) {
                    LOGGER.warning("Login attempted with empty username or password");
                    JOptionPane.showMessageDialog(loginFrame,
                            "Please enter both username and password.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Check maintenance mode (unchanged)
                try {
                    if (userController.isMaintenanceMode()) {
                        LOGGER.info("Maintenance mode active, checking user: " + username);
                        User user = userController.getUserByUsername(username);
                        if (user == null || !user.isAdmin()) {
                            LOGGER.info("Non-admin user or invalid username attempted login during maintenance: " + username);
                            JOptionPane.showMessageDialog(loginFrame,
                                    "System under maintenance. Please try again later.",
                                    "Maintenance", JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                    }
                } catch (Exception ex) {
                    LOGGER.severe("Error checking maintenance mode: " + ex.getMessage());
                    JOptionPane.showMessageDialog(loginFrame,
                            "Error checking system status. Please try again.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Attempt login (unchanged)
                try {
                    User loggedInUser = userController.loginAndGetUser(username, password);
                    if (loggedInUser != null) {
                        LOGGER.info("Successful login for user: " + username);
                        loginFrame.dispose();
                        if (loggedInUser.isAdmin()) {
                            new AdminDashboardView(loggedInUser);
                        } else {
                            new HomePageView(username, null);
                        }
                    } else {
                        User tempUser = userController.getUserByUsername(username);
                        if (tempUser != null && tempUser.isBlocked()) {
                            LOGGER.info("Blocked user attempted login: " + username);
                            JOptionPane.showMessageDialog(loginFrame,
                                    "🚫 Your account is blocked.\nPlease contact support to unblock your account.\n\n📞 Call or WhatsApp us at: +250 78 014 6863",
                                    "Account Blocked", JOptionPane.ERROR_MESSAGE);
                        } else {
                            LOGGER.info("Invalid credentials for user: " + username);
                            JOptionPane.showMessageDialog(loginFrame,
                                    "❌ Invalid credentials.\nPlease try again or contact support if the issue continues.",
                                    "Login Failed", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } catch (Exception ex) {
                    LOGGER.severe("Error during login attempt for user " + username + ": " + ex.getMessage());
                    JOptionPane.showMessageDialog(loginFrame,
                            "An error occurred during login. Please try again.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Register Button Action (unchanged)
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LOGGER.info("Opening RegisterView");
                loginFrame.dispose();
                new RegisterView();
            }
        });

        // Forgot Password Action (unchanged)
        forgotPasswordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = JOptionPane.showInputDialog(loginFrame, "Enter your email address:");
                if (email == null || email.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(loginFrame, "Email is required.", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try {
                    String otp = userController.sendPasswordResetOTP(email);
                    if (otp == null) {
                        JOptionPane.showMessageDialog(loginFrame, 
                            "Error sending OTP. Check email or try again.", 
                            "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    String inputOtp = JOptionPane.showInputDialog(loginFrame, 
                        "Enter the OTP sent to your email:");
                    if (inputOtp == null || inputOtp.trim().isEmpty()) {
                        JOptionPane.showMessageDialog(loginFrame, 
                            "OTP is required.", 
                            "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    String newPassword = JOptionPane.showInputDialog(loginFrame, 
                        "Enter new password:");
                    if (newPassword == null || newPassword.trim().isEmpty()) {
                        JOptionPane.showMessageDialog(loginFrame, 
                            "New password is required.", 
                            "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    boolean success = userController.resetPassword(email, inputOtp, newPassword, otp);
                    JOptionPane.showMessageDialog(loginFrame, 
                        success ? "Password reset successfully! Please log in." 
                                : "Error resetting password. Invalid OTP or user not found.", 
                        success ? "Success" : "Error", 
                        success ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    LOGGER.severe("Error during password reset for email " + email + ": " + ex.getMessage());
                    JOptionPane.showMessageDialog(loginFrame,
                        "An error occurred during password reset. Please try again.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int y, String label, JTextField field) {
        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        panel.add(field, gbc);
    }
}