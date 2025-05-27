package View;

import Controller.NoticeController;
import Controller.UserController;
import model.Notice;
import model.User;
import javax.swing.*;
import java.awt.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class NoticesView {
    private final NoticeController noticeController;
    private final UserController userController;
    private final JFrame parentFrame;
    private Timestamp lastNoticeTime;
    private final Color primaryColor = new Color(0, 102, 204);
    private final Color lightGray = new Color(245, 245, 245);

    public NoticesView(JFrame parentFrame, UserController userController) {
        this.parentFrame = parentFrame;
        this.noticeController = new NoticeController();
        this.userController = userController;
        this.lastNoticeTime = new Timestamp(System.currentTimeMillis());
        startNoticePolling();
    }

    public JPanel createNoticesPanel() {
        JPanel noticesPanel = new JPanel(new GridBagLayout());
        noticesPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        try {
            List<Notice> notices = noticeController.getAllNotices();
            if (notices.isEmpty()) {
                JLabel noNoticesLabel = new JLabel("No active notices available");
                noNoticesLabel.setHorizontalAlignment(SwingConstants.CENTER);
                noNoticesLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                noNoticesLabel.setForeground(new Color(100, 100, 100));
                gbc.gridx = 0; gbc.gridy = 0;
                noticesPanel.add(noNoticesLabel, gbc);
            } else {
                int row = 0;
                for (Notice notice : notices) {
                    if (!"active".equals(notice.getStatus())) continue;
                    JPanel singleNoticePanel = new JPanel(new BorderLayout(5, 5));
                    singleNoticePanel.setBackground(lightGray);
                    singleNoticePanel.setBorder(BorderFactory.createTitledBorder(
                            BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                            "Notice #" + notice.getId()));

                    JLabel titleLabel = new JLabel(notice.getTitle());
                    titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
                    singleNoticePanel.add(titleLabel, BorderLayout.NORTH);

                    JTextArea contentArea = new JTextArea(notice.getContent());
                    contentArea.setLineWrap(true);
                    contentArea.setWrapStyleWord(true);
                    contentArea.setEditable(false);
                    contentArea.setBackground(lightGray);
                    contentArea.setFont(new Font("Arial", Font.PLAIN, 14));
                    singleNoticePanel.add(new JScrollPane(contentArea), BorderLayout.CENTER);

                    User postedBy = notice.getPostedBy();
                    String postedByName = (postedBy != null) ? postedBy.getUsername() : "Unknown";
                    JLabel postedLabel = new JLabel("Posted by " + postedByName + " on " + notice.getCreatedAt());
                    postedLabel.setFont(new Font("Arial", Font.ITALIC, 12));
                    singleNoticePanel.add(postedLabel, BorderLayout.SOUTH);

                    gbc.gridx = 0; gbc.gridy = row++;
                    noticesPanel.add(singleNoticePanel, gbc);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(parentFrame, "Error loading notices: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

        return noticesPanel;
    }

    private void startNoticePolling() {
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    List<Notice> notices = noticeController.getAllNotices();
                    for (Notice notice : notices) {
                        if ("active".equals(notice.getStatus()) && 
                            notice.getCreatedAt().isAfter(lastNoticeTime.toLocalDateTime())) {
                            SwingUtilities.invokeLater(() -> showNoticePopup(notice));
                            lastNoticeTime = Timestamp.valueOf(notice.getCreatedAt());
                            break; // Show only the latest notice
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, 30000); // Check every 30 seconds
    }

    private void showNoticePopup(Notice notice) {
        JDialog dialog = new JDialog(parentFrame, "New Notice", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(parentFrame);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(Color.WHITE);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(lightGray);
        JLabel titleLabel = new JLabel(notice.getTitle());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        contentPanel.add(titleLabel, BorderLayout.NORTH);

        JTextArea contentArea = new JTextArea(notice.getContent());
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setEditable(false);
        contentArea.setBackground(lightGray);
        contentArea.setFont(new Font("Arial", Font.PLAIN, 14));
        contentPanel.add(new JScrollPane(contentArea), BorderLayout.CENTER);

        User postedBy = notice.getPostedBy();
        String postedByName = (postedBy != null) ? postedBy.getUsername() : "Unknown";
        JLabel postedLabel = new JLabel("Posted by " + postedByName + " on " + notice.getCreatedAt());
        postedLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        contentPanel.add(postedLabel, BorderLayout.SOUTH);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBackground(primaryColor);
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }
}