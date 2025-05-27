package View;

import model.User;
import model.Report;
import model.Issue;
import model.Notice;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import Controller.UserController;
import Controller.AdminController;
import Controller.IssueController;
import java.util.logging.Logger;

import java.util.List;

public class AdminDashboardView {
    private JFrame frame;
    private User adminUser;
    private JTabbedPane tabbedPane;
    private JTable usersTable;
    private JTable issuesTable;
    private JTable reportsTable;
    private JTable noticesTable;
    private JTextArea previewArea;
    private AdminController adminController;
    private IssueController issueController;
    private UserController userController;

    public AdminDashboardView(User adminUser) {
        this.adminUser = adminUser;
        
        if (!adminUser.isAdmin()) {
            JOptionPane.showMessageDialog(null, "Access denied. Admin privileges required.");
            new LoginView();
            return;
        }
        
        adminController = new AdminController();
        issueController = new IssueController();
        userController = new UserController();
        
        initialize();
    }
    
    private void initialize() {
        frame = new JFrame("Admin Dashboard - Support Desk");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Users Management", createUsersPanel());
        tabbedPane.addTab("Issues Management", createIssuesPanel());
        tabbedPane.addTab("Reports Management", createReportsPanel());
        tabbedPane.addTab("System Notices", createNoticesPanel());
        tabbedPane.addTab("Statistics", createStatsPanel());
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        JLabel statusBar = new JLabel(" Ready | Logged in as: " + adminUser.getUsername() + " (Admin)");
        statusBar.setBorder(BorderFactory.createLoweredBevelBorder());
        mainPanel.add(statusBar, BorderLayout.SOUTH);
        
        frame.getContentPane().add(mainPanel);
        frame.setVisible(true);
    }
    
    private JPanel createHeaderPanel() {
        Color aucaBlue = new Color(0, 51, 153);
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(aucaBlue);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        JLabel titleLabel = new JLabel("Admin Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
        
        JLabel adminLabel = new JLabel("Admin: " + adminUser.getUsername());
        adminLabel.setForeground(Color.WHITE);
        rightPanel.add(adminLabel);
        
        JButton logoutButton = new JButton("Logout");
        logoutButton.setFocusPainted(false);
        logoutButton.addActionListener(e -> {
            frame.dispose();
            new LoginView();
        });
        rightPanel.add(logoutButton);
        
        headerPanel.add(rightPanel, BorderLayout.EAST);
        return headerPanel;
    }

    private JPanel createUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        JButton refreshBtn = new JButton("Refresh");
        JButton addUserBtn = new JButton("Add User");
        JButton blockUserBtn = new JButton("Block/Unblock");
        JButton makeAdminBtn = new JButton("Toggle Admin");
        JButton deleteUserBtn = new JButton("Delete User");

        toolBar.add(refreshBtn);
        toolBar.add(addUserBtn);
        toolBar.add(blockUserBtn);
        toolBar.add(makeAdminBtn);
        toolBar.add(deleteUserBtn);

        panel.add(toolBar, BorderLayout.NORTH);

        String[] columns = {"ID", "Username", "Full Name", "Email", "Admin", "Blocked", "Created At"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        usersTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(usersTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search: "));
        JTextField searchField = new JTextField(20);
        searchPanel.add(searchField);
        JButton searchBtn = new JButton("Search");
        searchPanel.add(searchBtn);

        panel.add(searchPanel, BorderLayout.SOUTH);

        refreshBtn.addActionListener(e -> loadUsers());

        blockUserBtn.addActionListener(e -> {
            int selectedRow = usersTable.getSelectedRow();
            if (selectedRow != -1) {
                int userId = (int) usersTable.getValueAt(selectedRow, 0);
                boolean isBlocked = (boolean) usersTable.getValueAt(selectedRow, 5);
                boolean success = isBlocked ? adminController.unblockUser(userId) : adminController.blockUser(userId);
                if (success) {
                    JOptionPane.showMessageDialog(frame, "User status updated successfully!");
                    loadUsers();
                } else {
                    JOptionPane.showMessageDialog(frame, "Failed to update user status.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Please select a user first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });

        addUserBtn.addActionListener(e -> showAddUserDialog());

        makeAdminBtn.addActionListener(e -> {
            int selectedRow = usersTable.getSelectedRow();
            if (selectedRow != -1) {
                int userId = (int) usersTable.getValueAt(selectedRow, 0);
                if (adminController.toggleAdminStatus(userId)) {
                    JOptionPane.showMessageDialog(frame, "Admin status toggled successfully!");
                    loadUsers();
                } else {
                    JOptionPane.showMessageDialog(frame, "Failed to toggle admin status.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Please select a user first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });

        deleteUserBtn.addActionListener(e -> {
            int selectedRow = usersTable.getSelectedRow();
            if (selectedRow != -1) {
                int userId = (int) usersTable.getValueAt(selectedRow, 0);
                int confirm = JOptionPane.showConfirmDialog(
                    frame, "Are you sure you want to delete this user?", "Confirm Deletion",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (confirm == JOptionPane.YES_OPTION) {
                    if (adminController.deleteUser(userId)) {
                        JOptionPane.showMessageDialog(frame, "User deleted successfully!");
                        loadUsers();
                    } else {
                        JOptionPane.showMessageDialog(frame, "Failed to delete user.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Please select a user first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });

        searchBtn.addActionListener(e -> {
            String query = searchField.getText().trim();
            DefaultTableModel searchModel = (DefaultTableModel) usersTable.getModel();
            searchModel.setRowCount(0);
            try {
                List<User> users = adminController.searchUsers(query);
                for (User user : users) {
                    searchModel.addRow(new Object[]{
                        user.getId(), user.getUsername(), user.getFullName(),
                        user.getEmail(), user.isAdmin(), user.isBlocked(), user.getCreatedAt()
                    });
                }
                System.out.println("✅ Found " + users.size() + " users for query: " + query);
            } catch (Exception ex) {
                System.err.println("❌ Failed to search users: " + ex.getMessage());
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Error searching users: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        loadUsers();

        return panel;
    }

    private void loadUsers() {
        DefaultTableModel model = (DefaultTableModel) usersTable.getModel();
        model.setRowCount(0);
        try {
            List<User> users = adminController.getAllUsers();
            for (User user : users) {
                model.addRow(new Object[]{
                    user.getId(), user.getUsername(), user.getFullName(),
                    user.getEmail(), user.isAdmin(), user.isBlocked(), user.getCreatedAt()
                });
            }
            System.out.println("✅ Loaded " + users.size() + " users");
        } catch (Exception e) {
            System.err.println("❌ Error loading users: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error loading users: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAddUserDialog() {
        JDialog dialog = new JDialog(frame, "Add New User", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(frame);
    
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
    
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        JTextField usernameField = new JTextField(15);
        formPanel.add(usernameField, gbc);
    
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        JTextField fullNameField = new JTextField(15);
        formPanel.add(fullNameField, gbc);
    
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        JTextField emailField = new JTextField(15);
        formPanel.add(emailField, gbc);
    
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        JPasswordField passwordField = new JPasswordField(15);
        formPanel.add(passwordField, gbc);
    
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        JCheckBox adminCheckbox = new JCheckBox("Admin privileges");
        formPanel.add(adminCheckbox, gbc);
    
        mainPanel.add(formPanel, BorderLayout.CENTER);
    
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Add User");
        JButton cancelButton = new JButton("Cancel");
    
        saveButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String fullName = fullNameField.getText().trim();
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
    
            if (fullName.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
    
            if (!fullName.matches("^[a-zA-Z\\s'-]{2,100}$")) {
                JOptionPane.showMessageDialog(dialog, "Full name must be 2–100 characters, letters, spaces, hyphens, or apostrophes only!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
    
            if (!username.matches("^[a-zA-Z0-9_]{3,20}$")) {
                JOptionPane.showMessageDialog(dialog, "Username must be 3–20 characters, alphanumeric or underscores only!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
    
            if (!email.matches("^[\\w!#$%&’*+/=?`{|}~^-]+(?:\\.[\\w!#$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$") || email.length() > 255) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid email address (max 255 characters)!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
    
            if (!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{8,50}$")) {
                JOptionPane.showMessageDialog(dialog, "Password must be 8–50 characters, with at least one uppercase, lowercase, number, and special character (!@#$%^&*)!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
    
            try {
                User newUser = new User(fullName, username, email, password);
                newUser.setAdmin(adminCheckbox.isSelected());
                boolean result = userController.addUser(newUser);
                if (result) {
                    JOptionPane.showMessageDialog(dialog, "User added successfully!");
                    dialog.dispose();
                    loadUsers();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to add user.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                System.err.println("❌ Error adding user: " + ex.getMessage());
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog, "Error adding user: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    
        cancelButton.addActionListener(e -> dialog.dispose());
    
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
    
        dialog.getContentPane().add(mainPanel);
        dialog.setVisible(true);
    }

    private JPanel createIssuesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        
        JButton refreshBtn = new JButton("Refresh");
        JButton viewIssueBtn = new JButton("View Details");
        JButton deleteIssueBtn = new JButton("Delete Issue");
        
        toolBar.add(refreshBtn);
        toolBar.add(viewIssueBtn);
        toolBar.add(deleteIssueBtn);
        
        panel.add(toolBar, BorderLayout.NORTH);
        
        String[] columns = {"ID", "Title", "Status", "Reported By", "Category", "Created At", "Updated At"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        issuesTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(issuesTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search: "));
        JTextField searchField = new JTextField(20);
        searchPanel.add(searchField);
        
        searchPanel.add(new JLabel("Filter by Status: "));
        String[] statuses = {"All", "Open", "In Progress", "Resolved", "Closed"};
        JComboBox<String> statusFilter = new JComboBox<>(statuses);
        searchPanel.add(statusFilter);
        
        JButton searchBtn = new JButton("Search");
        searchPanel.add(searchBtn);
        
        panel.add(searchPanel, BorderLayout.SOUTH);
        
        refreshBtn.addActionListener(e -> loadIssues());
        
        deleteIssueBtn.addActionListener(e -> {
            int selectedRow = issuesTable.getSelectedRow();
            if (selectedRow != -1) {
                int issueId = (int) issuesTable.getValueAt(selectedRow, 0);
                int confirm = JOptionPane.showConfirmDialog(
                    frame, 
                    "Are you sure you want to delete this issue? This action cannot be undone.",
                    "Confirm Deletion",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
                );
                
                if (confirm == JOptionPane.YES_OPTION) {
                    boolean success = adminController.deleteIssue(issueId);
                    if (success) {
                        JOptionPane.showMessageDialog(frame, "Issue deleted successfully!");
                        loadIssues();
                    } else {
                        JOptionPane.showMessageDialog(frame, "Failed to delete issue.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Please select an issue first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        viewIssueBtn.addActionListener(e -> {
            int selectedRow = issuesTable.getSelectedRow();
            if (selectedRow != -1) {
                int issueId = (int) issuesTable.getValueAt(selectedRow, 0);
                String title = (String) issuesTable.getValueAt(selectedRow, 1);
                showIssueDetails(issueId, title);
            } else {
                JOptionPane.showMessageDialog(frame, "Please select an issue first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        loadIssues();
        
        return panel;
    }
    
    private void loadIssues() {
        DefaultTableModel model = (DefaultTableModel) issuesTable.getModel();
        model.setRowCount(0);
        
        try {
            List<Issue> issues = issueController.getAllIssues();
            for (Issue issue : issues) {
                User user = userController.getUserById(issue.getUserId());
                String reportedBy = (user != null) ? user.getUsername() : "Unknown";
                model.addRow(new Object[]{
                    issue.getId(),
                    issue.getTitle(),
                    issue.getStatus(),
                    reportedBy,
                    "General",
                    issue.getCreatedAt(),
                    issue.getUpdatedAt()
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error loading issues: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showIssueDetails(int issueId, String title) {
        JDialog dialog = new JDialog(frame, "Issue Details: " + title, true);
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(frame);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JTextArea issueContent = new JTextArea(
            "Issue ID: " + issueId + "\n" +
            "Title: " + title + "\n" +
            "Description: This is a sample issue description.\n\n" +
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed commodo " +
            "tincidunt nisl, a aliquam massa vehicula in. Suspendisse potenti."
        );
        issueContent.setEditable(false);
        issueContent.setLineWrap(true);
        issueContent.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(issueContent);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(closeButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.getContentPane().add(mainPanel);
        dialog.setVisible(true);
    }
    
    private JPanel createReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        
        JButton refreshBtn = new JButton("Refresh");
        JButton viewReportBtn = new JButton("View Details");
        JButton resolveBtn = new JButton("Mark as Resolved");
        JButton dismissBtn = new JButton("Dismiss Report");
        JButton deleteContentBtn = new JButton("Delete Reported Content");
        JButton generatePdfBtn = new JButton("Generate PDF"); // New button
        
        toolBar.add(refreshBtn);
        toolBar.add(viewReportBtn);
        toolBar.add(resolveBtn);
        toolBar.add(dismissBtn);
        toolBar.add(deleteContentBtn);
        toolBar.add(generatePdfBtn);
        
        panel.add(toolBar, BorderLayout.NORTH);
        
        String[] columns = {"ID", "Type", "Reported By", "Reason", "Status", "Created At"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return String.class;
            }
        };
        
        reportsTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(reportsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search: "));
        JTextField searchField = new JTextField(20);
        searchPanel.add(searchField);
        
        searchPanel.add(new JLabel("Filter by Status: "));
        String[] statuses = {"All", "Pending", "Resolved", "Dismissed"};
        JComboBox<String> statusFilter = new JComboBox<>(statuses);
        searchPanel.add(statusFilter);
        
        JButton searchBtn = new JButton("Search");
        searchPanel.add(searchBtn);
        
        panel.add(searchPanel, BorderLayout.SOUTH);
        
        refreshBtn.addActionListener(e -> loadReports());
        
        viewReportBtn.addActionListener(e -> {
            int selectedRow = reportsTable.getSelectedRow();
            if (selectedRow != -1) {
                int reportId = Integer.parseInt(reportsTable.getValueAt(selectedRow, 0).toString());
                String type = reportsTable.getValueAt(selectedRow, 1).toString();
                showReportDetails(reportId, type);
            } else {
                JOptionPane.showMessageDialog(frame, "Please select a report first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        resolveBtn.addActionListener(e -> {
            int selectedRow = reportsTable.getSelectedRow();
            if (selectedRow != -1) {
                int reportId = Integer.parseInt(reportsTable.getValueAt(selectedRow, 0).toString());
                updateReportStatus(reportId, "Resolved");
            } else {
                JOptionPane.showMessageDialog(frame, "Please select a report first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        dismissBtn.addActionListener(e -> {
            int selectedRow = reportsTable.getSelectedRow();
            if (selectedRow != -1) {
                int reportId = Integer.parseInt(reportsTable.getValueAt(selectedRow, 0).toString());
                updateReportStatus(reportId, "Dismissed");
            } else {
                JOptionPane.showMessageDialog(frame, "Please select a report first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        deleteContentBtn.addActionListener(e -> {
            int selectedRow = reportsTable.getSelectedRow();
            if (selectedRow != -1) {
                int reportId = Integer.parseInt(reportsTable.getValueAt(selectedRow, 0).toString());
                String type = reportsTable.getValueAt(selectedRow, 1).toString();
                int confirm = JOptionPane.showConfirmDialog(
                    frame, 
                    "Are you sure you want to delete the reported " + type.toLowerCase() + "? This action cannot be undone.",
                    "Confirm Deletion",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
                );
                
                if (confirm == JOptionPane.YES_OPTION) {
                    boolean success = adminController.deleteReportedContent(reportId, type);
                    if (success) {
                        JOptionPane.showMessageDialog(frame, "Reported content deleted successfully!");
                        loadReports();
                    } else {
                        JOptionPane.showMessageDialog(frame, "Failed to delete content.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Please select a report first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        generatePdfBtn.addActionListener(e -> {
            boolean success = adminController.generateReportPdf("reports.pdf", null, null, null);
            JOptionPane.showMessageDialog(frame, 
                success ? "Report generated: reports.pdf" : "Error generating report", 
                success ? "Success" : "Error", 
                success ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
        });
        
        loadReports();
        
        return panel;
    }
    
    private void loadReports() {
        DefaultTableModel model = (DefaultTableModel) reportsTable.getModel();
        model.setRowCount(0);
        
        try {
            List<Report> reports = adminController.getReports();
            for (Report report : reports) {
                String type = (report.getIssue() != null) ? "Issue" : "Comment";
                User user = userController.getUserById(report.getReportedBy().getId());
                String reportedBy = (user != null) ? user.getUsername() : "Unknown";
                model.addRow(new Object[]{
                    report.getId(),
                    type,
                    reportedBy,
                    report.getReason(),
                    report.getStatus(),
                    report.getCreatedAt()
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error loading reports: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showReportDetails(int reportId, String type) {
        JDialog dialog = new JDialog(frame, "Report Details: #" + reportId, true);
        dialog.setSize(700, 500);
        dialog.setLocationRelativeTo(frame);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel detailsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        detailsPanel.add(new JLabel("Report ID:"), gbc);
        gbc.gridx = 1;
        detailsPanel.add(new JLabel("#" + reportId), gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        detailsPanel.add(new JLabel("Report Type:"), gbc);
        gbc.gridx = 1;
        detailsPanel.add(new JLabel(type), gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        detailsPanel.add(new JLabel("Reported By:"), gbc);
        gbc.gridx = 1;
        detailsPanel.add(new JLabel("User #123 (username)"), gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        detailsPanel.add(new JLabel("Created At:"), gbc);
        gbc.gridx = 1;
        detailsPanel.add(new JLabel("2023-01-05 14:30:00"), gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        detailsPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        detailsPanel.add(new JLabel("Pending"), gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 5;
        detailsPanel.add(new JLabel("Reason:"), gbc);
        gbc.gridx = 1;
        detailsPanel.add(new JLabel("Inappropriate content"), gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        detailsPanel.add(new JLabel("Reported Content:"), gbc);
        
        gbc.gridy = 7;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        JTextArea contentArea = new JTextArea("This is the reported content text that violated our guidelines...");
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setEditable(false);
        JScrollPane contentScroll = new JScrollPane(contentArea);
        contentScroll.setPreferredSize(new Dimension(600, 200));
        detailsPanel.add(contentScroll, gbc);
        
        mainPanel.add(new JScrollPane(detailsPanel), BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton resolveButton = new JButton("Resolve");
        JButton dismissButton = new JButton("Dismiss");
        JButton deleteButton = new JButton("Delete Content");
        JButton closeButton = new JButton("Close");
        
        resolveButton.addActionListener(e -> {
            updateReportStatus(reportId, "Resolved");
            dialog.dispose();
        });
        
        dismissButton.addActionListener(e -> {
            updateReportStatus(reportId, "Dismissed");
            dialog.dispose();
        });
        
        deleteButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                dialog, 
                "Are you sure you want to delete the reported content? This action cannot be undone.",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = adminController.deleteReportedContent(reportId, type);
                if (success) {
                    JOptionPane.showMessageDialog(dialog, "Reported content deleted successfully!");
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to delete content.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        closeButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(resolveButton);
        buttonPanel.add(dismissButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(closeButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.getContentPane().add(mainPanel);
        dialog.setVisible(true);
    }
    
    private JPanel createNoticesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JPanel controlPanel = new JPanel(new BorderLayout());
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        JButton refreshBtn = new JButton("Refresh");
        JButton newNoticeBtn = new JButton("New Notice");
        JButton editNoticeBtn = new JButton("Edit Notice");
        JButton deleteNoticeBtn = new JButton("Delete Notice");
        JCheckBox maintenanceModeCheckBox = new JCheckBox("Maintenance Mode");
        maintenanceModeCheckBox.setSelected(userController.isMaintenanceMode());
        toolBar.add(refreshBtn);
        toolBar.add(newNoticeBtn);
        toolBar.add(editNoticeBtn);
        toolBar.add(deleteNoticeBtn);
        toolBar.add(Box.createHorizontalGlue());
        toolBar.add(maintenanceModeCheckBox);
        controlPanel.add(toolBar, BorderLayout.NORTH);
        String[] columns = {"ID", "Title", "Posted By", "Created At"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        noticesTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(noticesTable);
        panel.add(controlPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        JPanel previewPanel = new JPanel(new BorderLayout());
        previewPanel.setBorder(BorderFactory.createTitledBorder("Notice Preview"));
        previewArea = new JTextArea(5, 20);
        previewArea.setEditable(false);
        previewArea.setLineWrap(true);
        previewArea.setWrapStyleWord(true);
        previewPanel.add(new JScrollPane(previewArea), BorderLayout.CENTER);
        panel.add(previewPanel, BorderLayout.SOUTH);

        refreshBtn.addActionListener(e -> loadNotices());
        newNoticeBtn.addActionListener(e -> showCreateNoticeDialog());
        editNoticeBtn.addActionListener(e -> {
            int selectedRow = noticesTable.getSelectedRow();
            if (selectedRow != -1) {
                int noticeId = (int) noticesTable.getValueAt(selectedRow, 0);
                showEditNoticeDialog(noticeId);
            } else {
                JOptionPane.showMessageDialog(frame, "Please select a notice first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });
        deleteNoticeBtn.addActionListener(e -> {
            int selectedRow = noticesTable.getSelectedRow();
            if (selectedRow != -1) {
                int noticeId = (int) noticesTable.getValueAt(selectedRow, 0);
                int confirm = JOptionPane.showConfirmDialog(
                        frame, "Are you sure you want to delete this notice?", "Confirm Deletion",
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (confirm == JOptionPane.YES_OPTION) {
                    if (adminController.deleteNotice(noticeId)) {
                        JOptionPane.showMessageDialog(frame, "Notice deleted successfully!");
                        loadNotices();
                    } else {
                        JOptionPane.showMessageDialog(frame, "Failed to delete notice.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Please select a notice first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });
        maintenanceModeCheckBox.addActionListener(e -> {
            boolean enabled = maintenanceModeCheckBox.isSelected();
            try {
                boolean success = userController.setMaintenanceMode(enabled);
                if (success) {
                    System.out.println("Maintenance mode set to: " + enabled);
                    JOptionPane.showMessageDialog(frame,
                            "Maintenance mode " + (enabled ? "enabled" : "disabled"),
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    System.out.println("Failed to set maintenance mode to: " + enabled);
                    maintenanceModeCheckBox.setSelected(!enabled);
                    JOptionPane.showMessageDialog(frame,
                            "Failed to update maintenance mode.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                System.out.println("Error setting maintenance mode: " + ex.getMessage());
                maintenanceModeCheckBox.setSelected(!enabled);
                JOptionPane.showMessageDialog(frame,
                        "Error updating maintenance mode.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        noticesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = noticesTable.getSelectedRow();
                if (selectedRow != -1) {
                    int noticeId = (int) noticesTable.getValueAt(selectedRow, 0);
                    Notice notice = adminController.getNoticeById(noticeId);
                    if (notice != null) {
                        previewArea.setText("Title: " + notice.getTitle() + "\n\n" + notice.getContent());
                    } else {
                        previewArea.setText("Failed to load notice content.");
                    }
                } else {
                    previewArea.setText("");
                }
            }
        });
        loadNotices();
        return panel;
    }
    
    private void loadNotices() {
        DefaultTableModel model = (DefaultTableModel) noticesTable.getModel();
        model.setRowCount(0);
        try {
            List<Notice> notices = adminController.getAllNotices();
            for (Notice notice : notices) {
                User user = userController.getUserById(notice.getPostedBy().getId());
                String postedBy = user != null ? user.getUsername() : "Unknown";
                model.addRow(new Object[]{
                    notice.getId(), notice.getTitle(), postedBy, notice.getCreatedAt()
                });
            }
            previewArea.setText("");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error loading notices: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showCreateNoticeDialog() {
        JDialog dialog = new JDialog(frame, "Create New Notice", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(frame);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        JTextField titleField = new JTextField(20);
        formPanel.add(titleField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Content:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        JTextArea contentArea = new JTextArea(10, 20);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(contentArea);
        formPanel.add(scrollPane, gbc);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Post Notice");
        JButton cancelButton = new JButton("Cancel");
        
        saveButton.addActionListener(e -> {
            String title = titleField.getText() != null ? titleField.getText().trim() : "";
            String content = contentArea.getText() != null ? contentArea.getText().trim() : "";
            
            if (title.isEmpty() || content.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Title and content are required!", 
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                boolean success = adminController.postNotice(title, content, adminUser.getId());
                if (success) {
                    JOptionPane.showMessageDialog(dialog, "Notice posted successfully!");
                    dialog.dispose();
                    loadNotices();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to post notice.", 
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog, "Error posting notice: " + ex.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.getContentPane().add(mainPanel);
        dialog.setVisible(true);
    }
    
    private void showEditNoticeDialog(int noticeId) {
        JDialog dialog = new JDialog(frame, "Edit Notice", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(frame);
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        Notice notice = adminController.getNoticeById(noticeId);
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        JTextField titleField = new JTextField(notice != null ? notice.getTitle() : "", 20);
        formPanel.add(titleField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Content:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 1.0;
        JTextArea contentArea = new JTextArea(notice != null ? notice.getContent() : "", 10, 20);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(contentArea);
        formPanel.add(scrollPane, gbc);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save Changes");
        JButton cancelButton = new JButton("Cancel");
        saveButton.addActionListener(e -> {
            String title = titleField.getText() != null ? titleField.getText().trim() : "";
            String content = contentArea.getText() != null ? contentArea.getText().trim() : "";
            if (title.isEmpty() || content.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Title and content are required!", 
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                boolean success = adminController.updateNotice(noticeId, title, content);
                if (success) {
                    JOptionPane.showMessageDialog(dialog, "Notice updated successfully!");
                    dialog.dispose();
                    loadNotices();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to update notice.", 
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog, "Error updating notice: " + ex.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        cancelButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        dialog.getContentPane().add(mainPanel);
        dialog.setVisible(true);
    }
    
    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshBtn = new JButton("Refresh Stats");
        topPanel.add(refreshBtn);
        panel.add(topPanel, BorderLayout.NORTH);
        
        JPanel statsPanel = new JPanel(new GridLayout(3, 2, 20, 20));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        loadStats(statsPanel);
        
        JScrollPane scrollPane = new JScrollPane(statsPanel);
        scrollPane.setBorder(null);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        refreshBtn.addActionListener(e -> loadStats(statsPanel));
        
        return panel;
    }
    
    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(color);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 32));
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        
        return card;
    }
    
    private void loadStats(JPanel statsPanel) {
        try {
            System.out.println("Loading admin dashboard stats...");
            if (statsPanel == null) {
                throw new IllegalArgumentException("Stats panel is null");
            }
            if (adminController == null) {
                throw new IllegalStateException("AdminController is not initialized");
            }
            statsPanel.removeAll();
            AdminController.AdminStats stats = adminController.getSystemStats();
            if (stats == null) {
                throw new IllegalStateException("Failed to retrieve system stats");
            }
            statsPanel.add(createStatCard("Total Users", String.format("%,d", stats.getTotalUsers()), new Color(41, 128, 185)));
            statsPanel.add(createStatCard("Total Issues", String.format("%,d", stats.getTotalIssues()), new Color(39, 174, 96)));
            statsPanel.add(createStatCard("Open Issues", String.format("%,d", stats.getOpenIssues()), new Color(211, 84, 0)));
            statsPanel.add(createStatCard("Total Comments", String.format("%,d", stats.getTotalComments()), new Color(142, 68, 173)));
            statsPanel.add(createStatCard("Active Reports", String.format("%,d", stats.getActiveReports()), new Color(231, 76, 60)));
            statsPanel.add(createStatCard("System Notices", String.format("%,d", stats.getSystemNotices()), new Color(52, 152, 219)));
            statsPanel.revalidate();
            statsPanel.repaint();
            System.out.println("Stats loaded: Users=" + stats.getTotalUsers() +
                    ", Issues=" + stats.getTotalIssues() +
                    ", OpenIssues=" + stats.getOpenIssues() +
                    ", Comments=" + stats.getTotalComments() +
                    ", Reports=" + stats.getActiveReports() +
                    ", Notices=" + stats.getSystemNotices());
            JOptionPane.showMessageDialog(frame, "Statistics refreshed successfully!");
        } catch (Exception e) {
            String errorMsg = "Failed to load stats: " + (e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName());
            System.err.println(errorMsg);
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, errorMsg, "Error", JOptionPane.ERROR_MESSAGE);
            statsPanel.removeAll();
            statsPanel.add(createStatCard("Total Users", "N/A", new Color(41, 128, 185)));
            statsPanel.add(createStatCard("Total Issues", "N/A", new Color(39, 174, 96)));
            statsPanel.add(createStatCard("Open Issues", "N/A", new Color(211, 84, 0)));
            statsPanel.add(createStatCard("Total Comments", "N/A", new Color(142, 68, 173)));
            statsPanel.add(createStatCard("Active Reports", "N/A", new Color(231, 76, 60)));
            statsPanel.add(createStatCard("System Notices", "N/A", new Color(52, 152, 219)));
            statsPanel.revalidate();
            statsPanel.repaint();
        }
    }

    private void updateReportStatus(int reportId, String status) {
        try {
            boolean success = adminController.updateReportStatus(reportId, status);
            if (success) {
                JOptionPane.showMessageDialog(frame, "Report status updated successfully!");
                loadReports();
            } else {
                JOptionPane.showMessageDialog(frame, "Failed to update report status.", 
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error updating report status: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}