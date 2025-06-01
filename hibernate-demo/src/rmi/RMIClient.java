// File: RMIClient.java
package rmi;

import model.User;
import model.Issue;
import model.Comment;
import model.Like;
import model.Report;
import model.Notice;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

/**
 * RMI Client to connect to UserService, IssueService, ReportService, and NoticeService
 */
public class RMIClient {
    
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 1099;
    private static final String USER_SERVICE_NAME = "UserService";
    private static final String ISSUE_SERVICE_NAME = "IssueService";
    private static final String REPORT_SERVICE_NAME = "ReportService";
    private static final String NOTICE_SERVICE_NAME = "NoticeService";
    private static final String USER_SERVICE_URL = "rmi://" + SERVER_HOST + ":" + SERVER_PORT + "/" + USER_SERVICE_NAME;
    private static final String ISSUE_SERVICE_URL = "rmi://" + SERVER_HOST + ":" + SERVER_PORT + "/" + ISSUE_SERVICE_NAME;
    private static final String REPORT_SERVICE_URL = "rmi://" + SERVER_HOST + ":" + SERVER_PORT + "/" + REPORT_SERVICE_NAME;
    private static final String NOTICE_SERVICE_URL = "rmi://" + SERVER_HOST + ":" + SERVER_PORT + "/" + NOTICE_SERVICE_NAME;
    
    private UserService userService;
    private IssueService issueService;
    private ReportService reportService;
    private NoticeService noticeService;
    private static RMIClient instance;
    
    private RMIClient() {
        connect();
    }
    
    public static RMIClient getInstance() {
        if (instance == null) {
            instance = new RMIClient();
        }
        return instance;
    }
    
    private void connect() {
        try {
            System.out.println("🔌 Connecting to RMI Server...");
            userService = (UserService) Naming.lookup(USER_SERVICE_URL);
            issueService = (IssueService) Naming.lookup(ISSUE_SERVICE_URL);
            reportService = (ReportService) Naming.lookup(REPORT_SERVICE_URL);
            noticeService = (NoticeService) Naming.lookup(NOTICE_SERVICE_URL);
            System.out.println("✅ Connected to all services successfully!");
        } catch (Exception e) {
            System.err.println("❌ Failed to connect to RMI Server: " + e.getMessage());
            System.err.println("🔍 Make sure the RMI Server is running!");
            userService = null;
            issueService = null;
            reportService = null;
            noticeService = null;
        }
    }
    
    public boolean isConnected() {
        return userService != null && issueService != null && reportService != null && noticeService != null;
    }
    
    public void reconnectIfNeeded() {
        if (!isConnected()) {
            System.out.println("🔄 Attempting to reconnect to RMI Server...");
            connect();
        }
    }
    
    // Authentication methods
    public User loginAndGetUser(String username, String password) {
        try {
            reconnectIfNeeded();
            if (userService != null) {
                return userService.loginAndGetUser(username, password);
            }
        } catch (RemoteException e) {
            System.err.println("❌ RMI Error in loginAndGetUser: " + e.getMessage());
            userService = null;
        }
        return null;
    }
    
    public boolean loginUser(String username, String password) {
        try {
            reconnectIfNeeded();
            if (userService != null) {
                return userService.loginUser(username, password);
            }
        } catch (RemoteException e) {
            System.err.println("❌ RMI Error in loginUser: " + e.getMessage());
            userService = null;
        }
        return false;
    }
    
    public boolean registerUser(User user) {
        try {
            reconnectIfNeeded();
            if (userService != null) {
                return userService.registerUser(user);
            }
        } catch (RemoteException e) {
            System.err.println("❌ RMI Error in registerUser: " + e.getMessage());
            userService = null;
        }
        return false;
    }
    
    public void logout() {
        try {
            reconnectIfNeeded();
            if (userService != null) {
                userService.logout();
            }
        } catch (RemoteException e) {
            System.err.println("❌ RMI Error in logout: " + e.getMessage());
            userService = null;
        }
    }
    
    // User management methods
    public User getUserByUsername(String username) {
        try {
            reconnectIfNeeded();
            if (userService != null) {
                return userService.getUserByUsername(username);
            }
        } catch (RemoteException e) {
            System.err.println("❌ RMI Error in getUserByUsername: " + e.getMessage());
            userService = null;
        }
        return null;
    }
    
    public User getUserById(Integer userId) {
        try {
            reconnectIfNeeded();
            if (userService != null) {
                return userService.getUserById(userId);
            }
        } catch (RemoteException e) {
            System.err.println("❌ RMI Error in getUserById: " + e.getMessage());
            userService = null;
        }
        return null;
    }
    
    public boolean updateUser(int userId, String username, String email, String password) {
        try {
            reconnectIfNeeded();
            if (userService != null) {
                return userService.updateUser(userId, username, email, password);
            }
        } catch (RemoteException e) {
            System.err.println("❌ RMI Error in updateUser: " + e.getMessage());
            userService = null;
        }
        return false;
    }
    
    // Search methods
    public ArrayList<User> searchUsers(String searchQuery) {
        try {
            reconnectIfNeeded();
            if (userService != null) {
                return userService.searchUsers(searchQuery);
            }
        } catch (RemoteException e) {
            System.err.println("❌ RMI Error in searchUsers: " + e.getMessage());
            userService = null;
        }
        return new ArrayList<>();
    }
    
    // Password reset methods
    public String sendPasswordResetOTP(String email) {
        try {
            reconnectIfNeeded();
            if (userService != null) {
                return userService.sendPasswordResetOTP(email);
            }
        } catch (RemoteException e) {
            System.err.println("❌ RMI Error in sendPasswordResetOTP: " + e.getMessage());
            userService = null;
        }
        return null;
    }
    
    public boolean resetPassword(String email, String otp, String newPassword) {
        try {
            reconnectIfNeeded();
            if (userService != null) {
                return userService.resetPassword(email, otp, newPassword);
            }
        } catch (RemoteException e) {
            System.err.println("❌ RMI Error in resetPassword: " + e.getMessage());
            userService = null;
        }
        return false;
    }
    
    // Statistics methods
    public int countIssuesByUserId(int userId) {
        try {
            reconnectIfNeeded();
            if (userService != null) {
                return userService.countIssuesByUserId(userId);
            }
        } catch (RemoteException e) {
            System.err.println("❌ RMI Error in countIssuesByUserId: " + e.getMessage());
            userService = null;
        }
        return 0;
    }
    
    public int getCurrentLoggedInUserId() {
        try {
            reconnectIfNeeded();
            if (userService != null) {
                return userService.getCurrentLoggedInUserId();
            }
        } catch (RemoteException e) {
            System.err.println("❌ RMI Error in getCurrentLoggedInUserId: " + e.getMessage());
            userService = null;
        }
        return -1;
    }
    
    // Admin methods
    public boolean isMaintenanceMode() {
        try {
            reconnectIfNeeded();
            if (userService != null) {
                return userService.isMaintenanceMode();
            }
        } catch (RemoteException e) {
            System.err.println("❌ RMI Error in isMaintenanceMode: " + e.getMessage());
            userService = null;
        }
        return false;
    }
    
    public boolean setUserStatus(int userId, String status) {
        try {
            reconnectIfNeeded();
            if (userService != null) {
                return userService.setUserStatus(userId, status);
            }
        } catch (RemoteException e) {
            System.err.println("❌ RMI Error in setUserStatus: " + e.getMessage());
            userService = null;
        }
        return false;
    }
    
    /**
     * Test the connection to the server
     */
    public void testConnection() {
        try {
            reconnectIfNeeded();
            if (userService != null) {
                userService.getCurrentLoggedInUserId();
                System.out.println("✅ RMI Connection test successful!");
            } else {
                System.err.println("❌ RMI Connection test failed - no connection");
            }
        } catch (RemoteException e) {
            System.err.println("❌ RMI Connection test failed: " + e.getMessage());
            userService = null;
        }
    }
    
    // IssueService methods
    public boolean createIssue(Issue issue, int userId) {
        try {
            reconnectIfNeeded();
            if (issueService != null) {
                return issueService.createIssue(issue, userId);
            }
        } catch (RemoteException e) {
            System.err.println("❌ RMI Error in createIssue: " + e.getMessage());
            issueService = null;
        }
        return false;
    }
    
    public boolean updateIssue(int issueId, Issue issue, int userId) {
        try {
            reconnectIfNeeded();
            if (issueService != null) {
                return issueService.updateIssue(issueId, issue, userId);
            }
        } catch (RemoteException e) {
            System.err.println("❌ RMI Error in updateIssue: " + e.getMessage());
            issueService = null;
        }
        return false;
    }
    
    public boolean deleteIssue(int issueId, int userId) {
        try {
            reconnectIfNeeded();
            if (issueService != null) {
                return issueService.deleteIssue(issueId, userId);
            }
        } catch (RemoteException e) {
            System.err.println("❌ RMI Error in deleteIssue: " + e.getMessage());
            issueService = null;
        }
        return false;
    }
    
    public Issue getIssueById(int issueId) {
        try {
            reconnectIfNeeded();
            if (issueService != null) {
                return issueService.getIssueById(issueId);
            }
        } catch (RemoteException e) {
            System.err.println("❌ RMI Error in getIssueById: " + e.getMessage());
            issueService = null;
        }
        return null;
    }
    
    public List<Issue> getIssuesByUserId(int userId) {
        try {
            reconnectIfNeeded();
            if (issueService != null) {
                return issueService.getIssuesByUserId(userId);
            }
        } catch (RemoteException e) {
            System.err.println("❌ RMI Error in getIssuesByUserId: " + e.getMessage());
            issueService = null;
        }
        return new ArrayList<>();
    }
    
    public List<Issue> getAllIssues() {
        try {
            reconnectIfNeeded();
            if (issueService != null) {
                return issueService.getAllIssues();
            }
        } catch (RemoteException e) {
            System.err.println("❌ RMI Error in getAllIssues: " + e.getMessage());
            issueService = null;
        }
        return new ArrayList<>();
    }
    
    public boolean addComment(Comment comment, int userId) {
        try {
            reconnectIfNeeded();
            if (issueService != null) {
                return issueService.addComment(comment, userId);
            }
        } catch (RemoteException e) {
            System.err.println("❌ RMI Error in addComment: " + e.getMessage());
            issueService = null;
        }
        return false;
    }
    
    public boolean deleteComment(int commentId, int userId) {
        try {
            reconnectIfNeeded();
            if (issueService != null) {
                return issueService.deleteComment(commentId, userId);
            }
        } catch (RemoteException e) {
            System.err.println("❌ RMI Error in deleteComment: " + e.getMessage());
            issueService = null;
        }
        return false;
    }
    
    public List<Comment> getCommentsForIssue(int issueId) {
        try {
            reconnectIfNeeded();
            if (issueService != null) {
                return issueService.getCommentsForIssue(issueId);
            }
        } catch (RemoteException e) {
            System.err.println("❌ RMI Error in getCommentsForIssue: " + e.getMessage());
            issueService = null;
        }
        return new ArrayList<>();
    }
    
    public boolean likeIssue(int userId, int issueId, Like.ReactionType reactionType) {
        try {
            reconnectIfNeeded();
            if (issueService != null) {
                return issueService.likeIssue(userId, issueId, reactionType);
            }
        } catch (RemoteException e) {
            System.err.println("❌ RMI Error in likeIssue: " + e.getMessage());
            issueService = null;
        }
        return false;
    }
    
    public boolean hasUserLikedIssue(int userId, int issueId) {
        try {
            reconnectIfNeeded();
            if (issueService != null) {
                return issueService.hasUserLikedIssue(userId, issueId);
            }
        } catch (RemoteException e) {
            System.err.println("❌ RMI Error in hasUserLikedIssue: " + e.getMessage());
            issueService = null;
        }
        return false;
    }
    
    public boolean hasUserDislikedIssue(int userId, int issueId) {
        try {
            reconnectIfNeeded();
            if (issueService != null) {
                return issueService.hasUserDislikedIssue(userId, issueId);
            }
        } catch (RemoteException e) {
            System.err.println("❌ RMI Error in hasUserDislikedIssue: " + e.getMessage());
            issueService = null;
        }
        return false;
    }
    
    public int getLikeCount(int issueId) {
        try {
            reconnectIfNeeded();
            if (issueService != null) {
                return issueService.getLikeCount(issueId);
            }
        } catch (RemoteException e) {
            System.err.println("❌ RMI Error in getLikeCount: " + e.getMessage());
            issueService = null;
        }
        return 0;
    }
    
    public int getDislikeCount(int issueId) {
        try {
            reconnectIfNeeded();
            if (issueService != null) {
                return issueService.getDislikeCount(issueId);
            }
        } catch (RemoteException e) {
            System.err.println("❌ RMI Error in getDislikeCount: " + e.getMessage());
            issueService = null;
        }
        return 0;
    }
    
    public ArrayList<Issue> searchIssues(String searchQuery) {
        try {
            reconnectIfNeeded();
            if (issueService != null) {
                return issueService.searchIssues(searchQuery);
            }
        } catch (RemoteException e) {
            System.err.println("❌ RMI Error in searchIssues: " + e.getMessage());
            issueService = null;
        }
        return new ArrayList<>();
    }
    
    public int countLikesReceivedByUserId(int userId) {
        try {
            reconnectIfNeeded();
            if (issueService != null) {
                return issueService.countLikesReceivedByUserId(userId);
            }
        } catch (RemoteException e) {
            System.err.println("❌ RMI Error in countLikesReceivedByUserId: " + e.getMessage());
            issueService = null;
        }
        return 0;
    }
    
    public int countCommentsReceivedByUserId(int userId) {
        try {
            reconnectIfNeeded();
            if (issueService != null) {
                return issueService.countCommentsReceivedByUserId(userId);
            }
        } catch (RemoteException e) {
            System.err.println("❌ RMI Error in countCommentsReceivedByUserId: " + e.getMessage());
            issueService = null;
        }
        return 0;
    }
    
    public int countCommentsMadeByUserId(int userId) {
        try {
            reconnectIfNeeded();
            if (issueService != null) {
                return issueService.countCommentsMadeByUserId(userId);
            }
        } catch (RemoteException e) {
            System.err.println("❌ RMI Error in countCommentsMadeByUserId: " + e.getMessage());
            issueService = null;
        }
        return 0;
    }
    
    public long getIssueCount() {
        try {
            reconnectIfNeeded();
            if (issueService != null) {
                return issueService.getIssueCount();
            }
        } catch (RemoteException e) {
            System.err.println("❌ RMI Error in getIssueCount: " + e.getMessage());
            issueService = null;
        }
        return 0;
    }
    
    public long getOpenIssueCount() {
        try {
            reconnectIfNeeded();
            if (issueService != null) {
                return issueService.getOpenIssueCount();
            }
        } catch (RemoteException e) {
            System.err.println("❌ RMI Error in getOpenIssueCount: " + e.getMessage());
            issueService = null;
        }
        return 0;
    }
    
    public long getCommentCount() {
        try {
            reconnectIfNeeded();
            if (issueService != null) {
                return issueService.getCommentCount();
            }
        } catch (RemoteException e) {
            System.err.println("❌ RMI Error in getCommentCount: " + e.getMessage());
            issueService = null;
        }
        return 0;
    }
    
    // ReportService methods
    public boolean createReport(Report report, int userId) {
        try {
            reconnectIfNeeded();
            if (reportService != null) {
                return reportService.createReport(report, userId);
            }
        } catch (RemoteException e) {
            System.err.println("❌ RMI Error in createReport: " + e.getMessage());
            reportService = null;
        }
        return false;
    }

    public boolean deleteReport(int reportId, int adminId) {
        try {
            reconnectIfNeeded();
            if (reportService != null) {
                return reportService.deleteReport(reportId, adminId);
            }
        } catch (RemoteException e) {
            System.err.println("❌ RMI Error in deleteReport: " + e.getMessage());
            reportService = null;
        }
        return false;
    }
    
    // NoticeService methods
    public boolean createNotice(Notice notice, int adminId) {
        try {
            reconnectIfNeeded();
            if (noticeService != null) {
                return noticeService.createNotice(notice, adminId);
            }
        } catch (RemoteException e) {
            System.err.println("❌ RMI Error in createNotice: " + e.getMessage());
            noticeService = null;
        }
        return false;
    }
    
    public boolean updateNotice(int noticeId, Notice notice, int adminId) {
        try {
            reconnectIfNeeded();
            if (noticeService != null) {
                return noticeService.updateNotice(noticeId, notice, adminId);
            }
        } catch (RemoteException e) {
            System.err.println("❌ RMI Error in updateNotice: " + e.getMessage());
            noticeService = null;
        }
        return false;
    }
    
    public boolean deleteNotice(int noticeId, int adminId) {
        try {
            reconnectIfNeeded();
            if (noticeService != null) {
                return noticeService.deleteNotice(noticeId, adminId);
            }
        } catch (RemoteException e) {
            System.err.println("❌ RMI Error in deleteNotice: " + e.getMessage());
            noticeService = null;
        }
        return false;
    }
    
    public Notice getNoticeById(int noticeId) {
        try {
            reconnectIfNeeded();
            if (noticeService != null) {
                return noticeService.getNoticeById(noticeId);
            }
        } catch (RemoteException e) {
            System.err.println("❌ RMI Error in getNoticeById: " + e.getMessage());
            noticeService = null;
        }
        return null;
    }
    
    public List<Notice> getActiveNotices() {
        try {
            reconnectIfNeeded();
            if (noticeService != null) {
                return noticeService.getActiveNotices();
            }
        } catch (RemoteException e) {
            System.err.println("❌ RMI Error in getActiveNotices: " + e.getMessage());
            noticeService = null;
        }
        return new ArrayList<>();
    }
    
    public List<Notice> getNoticesByUserId(int userId) {
        try {
            reconnectIfNeeded();
            if (noticeService != null) {
                return noticeService.getNoticesByUserId(userId);
            }
        } catch (RemoteException e) {
            System.err.println("❌ RMI Error in getNoticesByUserId: " + e.getMessage());
            noticeService = null;
        }
        return new ArrayList<>();
    }
}