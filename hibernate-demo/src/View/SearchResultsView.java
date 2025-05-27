package View;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import Controller.UserController;
import model.User;
import model.Issue;

public class SearchResultsView {
    private JFrame resultsFrame;
    private UserController userController;
    private JTabbedPane tabbedPane;
    private String searchQuery;
    private int currentLoggedInUserId;
    private String loggedInUsername;
    
    // Same color scheme as EditProfilePageView
    private static final Color AUCA_BLUE = new Color(0, 51, 153);
    private static final Color AUCA_BLUE_HOVER = new Color(0, 71, 173);
    private static final Color BACKGROUND = new Color(245, 245, 245);
    private static final Color ACCENT_COLOR = new Color(51, 153, 255);

    public SearchResultsView(String searchQuery, UserController controller, String loggedInUsername) {
        this.searchQuery = searchQuery;
        this.userController = controller;
        this.currentLoggedInUserId = controller.getCurrentLoggedInUserId();
        this.loggedInUsername = loggedInUsername;

        // Enhanced frame setup like EditProfilePageView
        resultsFrame = new JFrame("Search Results - " + searchQuery);
        resultsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        resultsFrame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Full screen like EditProfilePageView
        resultsFrame.setLocationRelativeTo(null);
        resultsFrame.setLayout(new BorderLayout());
        resultsFrame.getContentPane().setBackground(BACKGROUND);

        // ==== Main Panel with proper spacing ====
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(BACKGROUND);
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // ==== Top Panel for Back Button (matching EditProfilePageView style) ====
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(BACKGROUND);
        topPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JButton backButton = new JButton("← Back to Home");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setBackground(AUCA_BLUE);
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setPreferredSize(new Dimension(180, 45));
        backButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 1, AUCA_BLUE),
                new EmptyBorder(8, 15, 8, 15)
        ));

        // Hover effect like EditProfilePageView
        backButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                backButton.setBackground(AUCA_BLUE_HOVER);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                backButton.setBackground(AUCA_BLUE);
            }
        });

        backButton.addActionListener(e -> {
            System.out.println("Back button clicked, returning to HomePageView for username: " + loggedInUsername);
            resultsFrame.dispose();
            new HomePageView(loggedInUsername, null);
        });

        topPanel.add(backButton);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // ==== Title Panel ====
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(BACKGROUND);
        titlePanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("Search Results for: \"" + searchQuery + "\"");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(AUCA_BLUE);
        titlePanel.add(titleLabel);

        // ==== Enhanced Tabbed Pane ====
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 16));
        tabbedPane.setBackground(Color.WHITE);
        tabbedPane.setForeground(AUCA_BLUE);
        tabbedPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 1, AUCA_BLUE),
                new EmptyBorder(15, 15, 15, 15)
        ));

        loadResults();

        // ==== Center Panel for Content ====
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(BACKGROUND);
        centerPanel.add(titlePanel, BorderLayout.NORTH);
        centerPanel.add(tabbedPane, BorderLayout.CENTER);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // ==== Scroll Pane for Responsiveness ====
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        resultsFrame.add(scrollPane, BorderLayout.CENTER);
        resultsFrame.setVisible(true);
    }

    private void loadResults() {
        ArrayList<User> userResults = userController.searchUsers(searchQuery);
        JPanel usersPanel = createUsersPanel(userResults);
        tabbedPane.addTab("👥 Users (" + userResults.size() + ")", usersPanel);

        ArrayList<model.Issue> issueResults = userController.searchIssues(searchQuery);
        JPanel issuesPanel = createIssuesPanel(issueResults);
        tabbedPane.addTab("📋 Issues (" + issueResults.size() + ")", issuesPanel);
    }

    private JPanel createUsersPanel(ArrayList<User> users) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header with better styling
        JLabel headerLabel = new JLabel("Found " + users.size() + " users matching your search");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerLabel.setForeground(AUCA_BLUE);
        headerLabel.setBorder(new EmptyBorder(0, 0, 15, 0));
        panel.add(headerLabel, BorderLayout.NORTH);

        if (users.isEmpty()) {
            JPanel emptyPanel = new JPanel(new GridBagLayout());
            emptyPanel.setBackground(Color.WHITE);
            
            JLabel noResultsLabel = new JLabel("🔍 No users found matching your search");
            noResultsLabel.setFont(new Font("Arial", Font.ITALIC, 16));
            noResultsLabel.setForeground(Color.GRAY);
            noResultsLabel.setHorizontalAlignment(SwingConstants.CENTER);
            
            emptyPanel.add(noResultsLabel);
            panel.add(emptyPanel, BorderLayout.CENTER);
            return panel;
        }

        DefaultListModel<User> listModel = new DefaultListModel<>();
        for (User user : users) {
            listModel.addElement(user);
        }

        JList<User> userList = new JList<>(listModel);
        userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userList.setCellRenderer(new EnhancedUserListCellRenderer());
        userList.setBackground(BACKGROUND);
        userList.setFont(new Font("Arial", Font.PLAIN, 14));

        JButton viewProfileButton = createStyledButton("👤 View Selected Profile");
        viewProfileButton.setEnabled(false);
        viewProfileButton.addActionListener(e -> {
            User selectedUser = userList.getSelectedValue();
            if (selectedUser != null) {
                openUserProfile(selectedUser);
            }
        });

        userList.addListSelectionListener(e -> {
            viewProfileButton.setEnabled(userList.getSelectedIndex() != -1);
        });

        userList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    User selectedUser = userList.getSelectedValue();
                    if (selectedUser != null) {
                        openUserProfile(selectedUser);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(userList);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 1, AUCA_BLUE),
                new EmptyBorder(5, 5, 5, 5)
        ));
        scrollPane.setPreferredSize(new Dimension(0, 400));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Bottom panel with button and instructions
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(viewProfileButton);

        JLabel instructionLabel = new JLabel("💡 Select a user and click 'View Profile' or double-click to view");
        instructionLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        instructionLabel.setForeground(Color.GRAY);
        instructionLabel.setHorizontalAlignment(SwingConstants.CENTER);

        bottomPanel.add(buttonPanel, BorderLayout.NORTH);
        bottomPanel.add(instructionLabel, BorderLayout.SOUTH);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createIssuesPanel(ArrayList<model.Issue> issues) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header with better styling
        JLabel headerLabel = new JLabel("Found " + issues.size() + " issues matching your search");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerLabel.setForeground(AUCA_BLUE);
        headerLabel.setBorder(new EmptyBorder(0, 0, 15, 0));
        panel.add(headerLabel, BorderLayout.NORTH);

        System.out.println("Creating issues panel with " + issues.size() + " issues");
        for (model.Issue issue : issues) {
            System.out.println("Issue in panel: " + issue.getTitle() + " - " + issue.getStatus());
        }

        if (issues.isEmpty()) {
            JPanel emptyPanel = new JPanel(new GridBagLayout());
            emptyPanel.setBackground(Color.WHITE);
            
            JLabel noResultsLabel = new JLabel("🔍 No issues found matching your search");
            noResultsLabel.setFont(new Font("Arial", Font.ITALIC, 16));
            noResultsLabel.setForeground(Color.GRAY);
            noResultsLabel.setHorizontalAlignment(SwingConstants.CENTER);
            
            emptyPanel.add(noResultsLabel);
            panel.add(emptyPanel, BorderLayout.CENTER);
            return panel;
        }

        String[] columnNames = {"📋 Title", "🔄 Status", "👤 Creator", "👍 Likes", "📅 Created", "📝 Description"};
        Object[][] data = new Object[issues.size()][6];

        for (int i = 0; i < issues.size(); i++) {
            model.Issue issue = issues.get(i);
            User creator = userController.getUserById(issue.getUserId());
            String creatorName = (creator != null) ? creator.getUsername() : "Unknown";
            String description = issue.getDescription();
            String shortDesc = (description.length() > 50) ? description.substring(0, 50) + "..." : description;
            data[i][0] = issue.getTitle();
            data[i][1] = issue.getStatus();
            data[i][2] = creatorName;
            data[i][3] = issue.getLikes();
            data[i][4] = issue.getCreatedAt();
            data[i][5] = shortDesc;
        }

        JTable issuesTable = new JTable(data, columnNames);
        issuesTable.setFillsViewportHeight(true);
        issuesTable.setRowHeight(35);
        issuesTable.setFont(new Font("Arial", Font.PLAIN, 14));
        issuesTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        issuesTable.getTableHeader().setBackground(AUCA_BLUE);
        issuesTable.getTableHeader().setForeground(Color.WHITE);
        issuesTable.getTableHeader().setPreferredSize(new Dimension(0, 40));
        issuesTable.setGridColor(AUCA_BLUE);
        issuesTable.setBackground(BACKGROUND);
        issuesTable.setSelectionBackground(ACCENT_COLOR);
        issuesTable.setSelectionForeground(Color.WHITE);
        issuesTable.setShowGrid(true);
        issuesTable.setIntercellSpacing(new Dimension(1, 1));

        // Enhanced column widths
        issuesTable.getColumnModel().getColumn(0).setPreferredWidth(250); // Title
        issuesTable.getColumnModel().getColumn(1).setPreferredWidth(120); // Status
        issuesTable.getColumnModel().getColumn(2).setPreferredWidth(140); // Creator
        issuesTable.getColumnModel().getColumn(3).setPreferredWidth(80);  // Likes
        issuesTable.getColumnModel().getColumn(4).setPreferredWidth(150); // Created At
        issuesTable.getColumnModel().getColumn(5).setPreferredWidth(300); // Description

        issuesTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    int row = issuesTable.rowAtPoint(e.getPoint());
                    if (row >= 0) {
                        openIssueDetails(issues.get(row));
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(issuesTable);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 1, AUCA_BLUE),
                new EmptyBorder(5, 5, 5, 5)
        ));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Instruction label for issues
        JLabel instructionLabel = new JLabel("💡 Click on any issue to view details");
        instructionLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        instructionLabel.setForeground(Color.GRAY);
        instructionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        instructionLabel.setBorder(new EmptyBorder(10, 0, 0, 0));
        panel.add(instructionLabel, BorderLayout.SOUTH);

        return panel;
    }

    private void openUserProfile(User user) {
        int issuesSubmitted = userController.countIssuesByUserId(user.getId());
        int likesReceived = userController.countLikesReceivedByUserId(user.getId());
        int commentsReceived = userController.countCommentsReceivedByUserId(user.getId());
        int commentsMade = userController.countCommentsMadeByUserId(user.getId());

        boolean isCurrentUser = (currentLoggedInUserId == user.getId());
        System.out.println("Opening ProfileView from SearchResultsView for username: " + user.getUsername() + ", allowEditing: " + isCurrentUser);

        new ProfileView(user.getUsername(), user.getEmail(), null,
                       issuesSubmitted, likesReceived, commentsReceived, commentsMade, isCurrentUser);
    }

    private void openIssueDetails(model.Issue issue) {
        User creator = userController.getUserById(issue.getUserId());
        String creatorName = (creator != null) ? creator.getUsername() : "Unknown";

        String message = "<html><body style='width: 500px; font-family: Arial; padding: 20px;'>" +
                        "<h2 style='color: #003399; margin-bottom: 15px;'>📋 " + issue.getTitle() + "</h2>" +
                        "<div style='background: #f5f5f5; padding: 15px; border-radius: 5px; margin-bottom: 15px;'>" +
                        "<p style='margin: 5px 0;'><b>🔄 Status:</b> " + issue.getStatus() + "</p>" +
                        "<p style='margin: 5px 0;'><b>👤 Created by:</b> " + creatorName + "</p>" +
                        "<p style='margin: 5px 0;'><b>📅 Posted on:</b> " + issue.getCreatedAt() + "</p>" +
                        "<p style='margin: 5px 0;'><b>👍 Likes:</b> " + issue.getLikes() + "</p>" +
                        "</div>" +
                        "<div style='margin-top: 20px;'>" +
                        "<h3 style='color: #003399; margin-bottom: 10px;'>📝 Description:</h3>" +
                        "<div style='background: white; padding: 15px; border: 1px solid #003399; border-radius: 5px;'>" +
                        issue.getDescription() + 
                        "</div></div>" +
                        "</body></html>";

        JDialog detailDialog = new JDialog(resultsFrame, "Issue Details", true);
        detailDialog.setLayout(new BorderLayout());
        detailDialog.getContentPane().setBackground(Color.WHITE);

        JEditorPane contentPane = new JEditorPane("text/html", message);
        contentPane.setEditable(false);
        contentPane.setBackground(Color.WHITE);
        contentPane.setBorder(new EmptyBorder(20, 20, 20, 20));

        JScrollPane scrollPane = new JScrollPane(contentPane);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 1, AUCA_BLUE),
                new EmptyBorder(5, 5, 5, 5)
        ));
        detailDialog.add(scrollPane, BorderLayout.CENTER);

        JButton closeButton = createStyledButton("✖ Close");
        closeButton.addActionListener(e -> detailDialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        buttonPanel.add(closeButton);
        detailDialog.add(buttonPanel, BorderLayout.SOUTH);

        detailDialog.setSize(700, 600);
        detailDialog.setLocationRelativeTo(resultsFrame);
        detailDialog.setVisible(true);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(AUCA_BLUE);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(200, 40));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 1, AUCA_BLUE),
                new EmptyBorder(8, 15, 8, 15)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect like EditProfilePageView
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(AUCA_BLUE_HOVER);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(AUCA_BLUE);
            }
        });

        return button;
    }

    class EnhancedUserListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            User user = (User) value;
            
            label.setText("👤 " + user.getUsername() + " (" + user.getEmail() + ")");
            label.setFont(new Font("Arial", Font.PLAIN, 14));
            label.setBorder(new EmptyBorder(12, 15, 12, 15));
            
            if (isSelected) {
                label.setBackground(AUCA_BLUE);
                label.setForeground(Color.WHITE);
            } else {
                if (index % 2 == 0) {
                    label.setBackground(BACKGROUND);
                } else {
                    label.setBackground(Color.WHITE);
                }
                label.setForeground(Color.BLACK);
            }
            
            label.setOpaque(true);
            return label;
        }
    }
}