package View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import Controller.IssueController;
import Controller.UserController;
import model.Issue;
import model.Comment;
import model.User;

public class IssueDetailView {
    private JFrame detailFrame;
    private Issue issue;
    private IssueController issueController;
    private UserController userController;
    private int userId;
    private JTextArea commentField;
    private JPanel commentsPanel;
    private Color primaryColor = new Color(0, 102, 204); // Match HomePageView

    public IssueDetailView(Issue issue, int currentUserId) {
        this.issue = issue;
        this.userId = currentUserId;
        this.issueController = new IssueController();
        this.userController = new UserController();
        
        // Validate issue
        if (issue == null || issue.getId() <= 0) {
            System.err.println("IssueDetailView: Invalid issue: " + (issue == null ? "null" : "id=" + issue.getId()));
        }
        System.out.println("IssueDetailView: issue=" + (issue != null ? issue.getId() + ", " + issue.getTitle() : "null") + ", userId=" + userId);

        // Create and set up the frame
        detailFrame = new JFrame("Issue: " + (issue != null ? issue.getTitle() : "Invalid Issue"));
        detailFrame.setSize(800, 600);
        detailFrame.setLocationRelativeTo(null);
        detailFrame.setLayout(new BorderLayout());
        
        // Issue details panel at the top
        JPanel detailsPanel = createDetailsPanel();
        
        // Comments section in the middle
        JPanel commentsSection = createCommentsSection();
        
        // Comment input at the bottom
        JPanel inputPanel = createCommentInputPanel();
        
        // Add all components to the frame
        detailFrame.add(detailsPanel, BorderLayout.NORTH);
        detailFrame.add(commentsSection, BorderLayout.CENTER);
        detailFrame.add(inputPanel, BorderLayout.SOUTH);
        
        detailFrame.setVisible(true);
    }
    
    private JPanel createDetailsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // Title and status
        JPanel titlePanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel(issue != null ? issue.getTitle() : "Invalid Issue");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        
        JLabel statusLabel = new JLabel("Status: " + (issue != null ? issue.getStatus() : "Unknown"));
        statusLabel.setForeground(getStatusColor(issue != null ? issue.getStatus() : ""));
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        titlePanel.add(titleLabel, BorderLayout.WEST);
        titlePanel.add(statusLabel, BorderLayout.EAST);
        titlePanel.setOpaque(false);
        
        // Creator info
        User creator = issue != null ? userController.getUserById(issue.getUserId()) : null;
        String creatorName = (creator != null) ? creator.getUsername() : "Unknown";
        String createdAt = issue != null ? issue.getCreatedAt().toString() : "Unknown";
        
        JLabel creatorLabel = new JLabel("Posted by: " + creatorName + " on " + createdAt);
        creatorLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        creatorLabel.setForeground(Color.GRAY);
        
        // Description
        JTextArea descriptionArea = new JTextArea(issue != null ? issue.getDescription() : "No description available");
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setLineWrap(true);
        descriptionArea.setEditable(false);
        descriptionArea.setOpaque(false);
        descriptionArea.setFont(new Font("Arial", Font.PLAIN, 14));
        descriptionArea.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        // Reactions
        JPanel reactionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        reactionsPanel.setOpaque(false);
        
        JButton likeButton = createStyledButton("Like 👍", primaryColor);
        JButton dislikeButton = createStyledButton("Dislike 👎", new Color(150, 150, 150));
        JButton reportButton = createStyledButton("Report ⚠️", new Color(220, 53, 69));

        int initialLikeCount = issue != null ? issueController.getLikeCount(issue.getId()) : 0;
        int initialDislikeCount = issue != null ? issueController.getDislikeCount(issue.getId()) : 0;

        boolean hasLiked = issue != null && issueController.hasUserLikedIssue(userId, issue.getId());
        boolean hasDisliked = issue != null && issueController.hasUserDislikedIssue(userId, issue.getId());

        JLabel likeLabel = new JLabel(initialLikeCount + " Likes");
        likeLabel.setFont(new Font("Arial", Font.BOLD, 12));

        JLabel dislikeLabel = new JLabel(initialDislikeCount + " Dislikes");
        dislikeLabel.setFont(new Font("Arial", Font.BOLD, 12));

        final boolean[] liked = {hasLiked};
        final boolean[] disliked = {hasDisliked};
        final int[] likeCount = {initialLikeCount};
        final int[] dislikeCount = {initialDislikeCount};

        if (liked[0]) {
            likeButton.setText("Unlike 👍");
            likeButton.setBackground(primaryColor);
            likeButton.setForeground(Color.WHITE);
        } else {
            likeButton.setBackground(new Color(240, 240, 240));
            likeButton.setForeground(new Color(60, 60, 60));
        }

        if (disliked[0]) {
            dislikeButton.setText("Undislike 👎");
            dislikeButton.setBackground(primaryColor);
            dislikeButton.setForeground(Color.WHITE);
        } else {
            dislikeButton.setBackground(new Color(240, 240, 240));
            dislikeButton.setForeground(new Color(60, 60, 60));
        }

        reportButton.setBackground(new Color(240, 240, 240));
        reportButton.setForeground(new Color(60, 60, 60));
        reportButton.setFont(new Font("Arial", Font.PLAIN, 12));

        likeButton.addActionListener(e -> {
            try {
                System.out.println("Like button clicked: userId=" + userId + ", issueId=" + (issue != null ? issue.getId() : "null") + ", issueTitle=" + (issue != null ? issue.getTitle() : "null"));
                if (issue == null || issue.getId() <= 0 || userId <= 0) {
                    JOptionPane.showMessageDialog(detailFrame, 
                        "Invalid issue or user.", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!liked[0]) {
                    boolean success = issueController.likeIssue(userId, issue.getId(), model.Like.ReactionType.LIKE);
                    if (success) {
                        likeCount[0]++;
                        liked[0] = true;
                        likeButton.setText("Unlike 👍");
                        likeButton.setBackground(primaryColor);
                        likeButton.setForeground(Color.WHITE);
                        if (disliked[0]) {
                            dislikeCount[0]--;
                            disliked[0] = false;
                            dislikeButton.setText("Dislike 👎");
                            dislikeButton.setBackground(new Color(240, 240, 240));
                            dislikeButton.setForeground(new Color(60, 60, 60));
                            dislikeLabel.setText(dislikeCount[0] + " Dislikes");
                        }
                        likeLabel.setText(likeCount[0] + " Likes");
                    } else {
                        JOptionPane.showMessageDialog(detailFrame, 
                            "Failed to like issue. You may have already liked it.", 
                            "Notice", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    boolean success = issueController.likeIssue(userId, issue.getId(), null); // Remove like
                    if (success) {
                        likeCount[0]--;
                        liked[0] = false;
                        likeButton.setText("Like 👍");
                        likeButton.setBackground(new Color(240, 240, 240));
                        likeButton.setForeground(new Color(60, 60, 60));
                        likeLabel.setText(likeCount[0] + " Likes");
                    } else {
                        JOptionPane.showMessageDialog(detailFrame, 
                            "Failed to unlike issue. Please try again.", 
                            "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(detailFrame, 
                    "Error: " + ex.getMessage(), 
                    "Like Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        dislikeButton.addActionListener(e -> {
            try {
                System.out.println("Dislike button clicked: userId=" + userId + ", issueId=" + (issue != null ? issue.getId() : "null") + ", issueTitle=" + (issue != null ? issue.getTitle() : "null"));
                if (issue == null || issue.getId() <= 0 || userId <= 0) {
                    JOptionPane.showMessageDialog(detailFrame, 
                        "Invalid issue or user.", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!disliked[0]) {
                    boolean success = issueController.likeIssue(userId, issue.getId(), model.Like.ReactionType.DISLIKE);
                    if (success) {
                        dislikeCount[0]++;
                        disliked[0] = true;
                        dislikeButton.setText("Undislike 👎");
                        dislikeButton.setBackground(primaryColor);
                        dislikeButton.setForeground(Color.WHITE);
                        if (liked[0]) {
                            likeCount[0]--;
                            liked[0] = false;
                            likeButton.setText("Like 👍");
                            likeButton.setBackground(new Color(240, 240, 240));
                            likeButton.setForeground(new Color(60, 60, 60));
                            likeLabel.setText(likeCount[0] + " Likes");
                        }
                        dislikeLabel.setText(dislikeCount[0] + " Dislikes");
                    } else {
                        JOptionPane.showMessageDialog(detailFrame, 
                            "Failed to dislike issue. You may have already disliked it.", 
                            "Notice", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    boolean success = issueController.likeIssue(userId, issue.getId(), null); // Remove dislike
                    if (success) {
                        dislikeCount[0]--;
                        disliked[0] = false;
                        dislikeButton.setText("Dislike 👎");
                        dislikeButton.setBackground(new Color(240, 240, 240));
                        dislikeButton.setForeground(new Color(60, 60, 60));
                        dislikeLabel.setText(dislikeCount[0] + " Dislikes");
                    } else {
                        JOptionPane.showMessageDialog(detailFrame, 
                            "Failed to remove dislike. Please try again.", 
                            "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(detailFrame, 
                    "Error: " + ex.getMessage(), 
                    "Dislike Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Note: reportController is not defined in this class. Assuming it's available or handled elsewhere.
        // If reportController is defined, uncomment and adjust:
        /*
        reportButton.addActionListener(e -> {
            System.out.println("Report button clicked: userId=" + userId + ", issueId=" + (issue != null ? issue.getId() : "null") + ", issueTitle=" + (issue != null ? issue.getTitle() : "null"));
            if (issue == null || issue.getId() <= 0 || userId <= 0) {
                JOptionPane.showMessageDialog(detailFrame, 
                    "Invalid issue or user.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (reportController.hasUserReportedIssue(userId, issue.getId())) {
                JOptionPane.showMessageDialog(detailFrame, 
                    "You have already reported this issue.", 
                    "Notice", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            showReportDialog(issue.getId(), null);
        });
        */

        reactionsPanel.add(likeButton);
        reactionsPanel.add(likeLabel);
        reactionsPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        reactionsPanel.add(dislikeButton);
        reactionsPanel.add(dislikeLabel);
        reactionsPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        // reactionsPanel.add(reportButton); // Uncomment if reportController is added
        
        // Assemble the details panel
        panel.add(titlePanel, BorderLayout.NORTH);
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(creatorLabel, BorderLayout.NORTH);
        centerPanel.add(descriptionArea, BorderLayout.CENTER);
        
        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(reactionsPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createCommentsSection() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        JLabel commentsLabel = new JLabel("Comments");
        commentsLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        commentsPanel = new JPanel();
        commentsPanel.setLayout(new BoxLayout(commentsPanel, BoxLayout.Y_AXIS));
        
        // Load comments
        loadComments();
        
        JScrollPane scrollPane = new JScrollPane(commentsPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        panel.add(commentsLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void loadComments() {
        commentsPanel.removeAll();
        
        List<Comment> comments = issue != null ? issueController.getCommentsForIssue(issue.getId()) : List.of();
        
        if (comments.isEmpty()) {
            JLabel noCommentsLabel = new JLabel("No comments yet. Be the first to comment!");
            noCommentsLabel.setForeground(Color.GRAY);
            noCommentsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            commentsPanel.add(Box.createVerticalStrut(20));
            commentsPanel.add(noCommentsLabel);
        } else {
            for (Comment comment : comments) {
                commentsPanel.add(createCommentPanel(comment));
            }
        }
        
        commentsPanel.revalidate();
        commentsPanel.repaint();
    }
    
    private JPanel createCommentPanel(Comment comment) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(10, 0, 10, 0)
        ));
        
        User commenter = comment.getUser();
        String commenterName = (commenter != null) ? commenter.getUsername() : "Unknown User";
        
        JLabel userLabel = new JLabel(commenterName);
        userLabel.setFont(new Font("Arial", Font.BOLD, 13));
        
        JLabel dateLabel = new JLabel(comment.getCreatedAt().toString());
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        dateLabel.setForeground(Color.GRAY);
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.add(userLabel, BorderLayout.WEST);
        headerPanel.add(dateLabel, BorderLayout.EAST);
        
        JTextArea contentArea = new JTextArea(comment.getContent());
        contentArea.setWrapStyleWord(true);
        contentArea.setLineWrap(true);
        contentArea.setEditable(false);
        contentArea.setOpaque(false);
        contentArea.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(contentArea, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createCommentInputPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel addCommentLabel = new JLabel("Add a comment:");
        addCommentLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        commentField = new JTextArea(3, 20);
        commentField.setLineWrap(true);
        commentField.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(commentField);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        
        JButton postButton = new JButton("Post Comment");
        postButton.setBackground(primaryColor);
        postButton.setForeground(Color.WHITE);
        postButton.setFocusPainted(false);
        
        postButton.addActionListener(e -> postComment());
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(postButton);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        panel.add(addCommentLabel, BorderLayout.NORTH);
        panel.add(bottomPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void postComment() {
        String content = commentField.getText().trim();
        if (!content.isEmpty()) {
            Comment comment = new Comment();
            comment.setIssue(issue);
            comment.setContent(content);
            comment.setUser(userController.getUserById(userId));
            
            if (issueController.addComment(comment, userId)) {
                commentField.setText("");
                loadComments();
            } else {
                JOptionPane.showMessageDialog(detailFrame, 
                    "Failed to post comment. Please try again.",
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(detailFrame, 
                "Comment cannot be empty.",
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private Color getStatusColor(String status) {
        if (status == null) return Color.BLACK;
        switch (status.toLowerCase()) {
            case "open":
                return new Color(0, 150, 0);  // Green
            case "in progress":
                return new Color(255, 165, 0);  // Orange
            case "closed":
                return new Color(200, 0, 0);  // Red
            default:
                return Color.BLACK;
        }
    }
    
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.PLAIN, 12));
        button.setFocusPainted(false);
        return button;
    }
}