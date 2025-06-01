package Controller;

import model.User;
import model.Report;
import model.AuditLog;
import model.Issue;
import model.Notice;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import util.HibernateUtil;
import java.util.ArrayList;
import java.util.List;

public class AdminController {
    private final SessionFactory sessionFactory;
    private final UserController userController;
    private final ReportController reportController;
    private final IssueController issueController;

    public AdminController() {
        this.sessionFactory = HibernateUtil.getSessionFactory();
        this.userController = new UserController();
        this.reportController = new ReportController();
        this.issueController = new IssueController();
        System.out.println("✅ Admin Hibernate SessionFactory initialized!");
    }
      NoticeController noticeController = new NoticeController();

    /**
     * Get all reports in the system
     * @return List of reports
     */
    public List<Report> getReports() {
        return reportController.getAllReports();
    }

    /**
     * Block a user by setting their is_blocked flag to true
     * @param userId The user ID to block
     * @return true if successful, false otherwise
     */
    public boolean blockUser(int userId) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            User user = session.get(User.class, userId);
            if (user == null) {
                System.out.println("❌ User not found: ID=" + userId);
                return false;
            }
            user.setBlocked(true);
            user.setUpdatedAt(new java.sql.Timestamp(System.currentTimeMillis()));
            session.merge(user);
            tx.commit();
            System.out.println("✅ User blocked: ID=" + userId);
            return true;
        } catch (Exception e) {
            System.out.println("❌ Failed to block user");
            e.printStackTrace();
            return false;
        }
    }



    public Report getReportById(int reportId) {
        try (Session session = sessionFactory.openSession()) {
            Report report = session.get(Report.class, reportId);
            if (report != null) {
                System.out.println("✅ Fetched report: " + reportId);
            } else {
                System.out.println("❌ Report not found: " + reportId);
            }
            return report;
        } catch (Exception e) {
            System.out.println("❌ Failed to fetch report: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Unblock a user by setting their is_blocked flag to false
     * @param userId The user ID to unblock
     * @return true if successful, false otherwise
     */
    public boolean unblockUser(int userId) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            User user = session.get(User.class, userId);
            if (user == null) {
                System.out.println("❌ User not found: ID=" + userId);
                return false;
            }
            user.setBlocked(false);
            user.setUpdatedAt(new java.sql.Timestamp(System.currentTimeMillis()));
            session.merge(user);
            tx.commit();
            System.out.println("✅ User unblocked: ID=" + userId);
            return true;
        } catch (Exception e) {
            System.out.println("❌ Failed to unblock user");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Delete an issue and all associated comments and likes
     * @param issueId The issue ID to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteIssue(int issueId) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            Issue issue = session.get(Issue.class, issueId);
            if (issue == null) {
                System.out.println("❌ Issue not found: ID=" + issueId);
                return false;
            }
            // Hibernate cascades deletes for comments, likes, reports (ON DELETE CASCADE)
            session.remove(issue);
            tx.commit();
            System.out.println("✅ Issue deleted: ID=" + issueId);
            return true;
        } catch (Exception e) {
            System.out.println("❌ Failed to delete issue");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Post a new notice for all users
     * @param title Notice title
     * @param content Notice content
     * @param postedBy User ID of the admin posting the notice
     * @return true if successful, false otherwise
     */
    // public boolean postNotice(String title, String content, int postedBy) {
    //     try (Session session = sessionFactory.openSession()) {
    //         Transaction tx = session.beginTransaction();
    //         User user = session.get(User.class, postedBy);
    //         if (user == null) {
    //             System.out.println("❌ User not found: ID=" + postedBy);
    //             return false;
    //         }
    //         Notice notice = new Notice(title, content, user);
    //         session.persist(notice);
    //         tx.commit();
    //         System.out.println("✅ Notice posted: " + title + " by User ID: " + postedBy);
    //         return true;
    //     } catch (Exception e) {
    //         System.out.println("❌ Failed to post notice");
    //         e.printStackTrace();
    //         return false;
    //     }
    // }

    /**
     * Get all notices ordered by creation date
     * @return List of notices
     */
public List<Notice> getAllNotices() {
        return noticeController.getAllNotices();
    }


    public boolean generateReportPdf(String dest, Integer issueId, Integer commentId, Integer reportedBy) {
        return reportController.generateReportPdf(dest, issueId, commentId, reportedBy);
    }
    /**
     * Delete a notice
     * @param noticeId The notice ID to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteNotice(int noticeId) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            Notice notice = session.get(Notice.class, noticeId);
            if (notice == null) {
                System.out.println("❌ Notice not found: ID=" + noticeId);
                return false;
            }
            session.remove(notice);
            tx.commit();
            System.out.println("✅ Notice deleted: ID=" + noticeId);
            return true;
        } catch (Exception e) {
            System.out.println("❌ Failed to delete notice");
            e.printStackTrace();
            return false;
        }
    }

 public boolean updateNotice(int noticeId, String title, String content) {
        Notice notice = noticeController.getNoticeById(noticeId);
        if (notice == null) {
            return false;
        }
        notice.setTitle(title);
        notice.setContent(content);
        return noticeController.updateNotice(notice);
    }


    public boolean postNotice(String title, String content, int userId) {
        User user = new UserController().getUserById(userId);
        if (user == null || !user.isAdmin()) {
            System.out.println("❌ Invalid admin ID or user is not an admin: " + userId);
            return false;
        }
        Notice notice = new Notice(title, content, user);
        return noticeController.createNotice(notice, userId);
    }



 public Notice getNoticeById(int noticeId) {
        return noticeController.getNoticeById(noticeId);
    }
    /**
     * Get all users in the system
     * @return List of users
     */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        try (Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery("FROM User ORDER BY id", User.class);
            users = query.getResultList();
            System.out.println("✅ Fetched " + users.size() + " users");
        } catch (Exception e) {
            System.out.println("❌ Failed to get users");
            e.printStackTrace();
        }
        return users;
    }

    /**
     * Get system statistics
     * @return AdminStats object with system metrics
     */




     public boolean toggleAdminStatus(int userId) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            User user = session.get(User.class, userId);
            if (user != null) {
                user.setAdmin(!user.isAdmin());
                session.merge(user);
                session.getTransaction().commit();
                System.out.println("✅ Toggled admin status for user: " + userId);
                return true;
            }
            System.out.println("❌ User not found: " + userId);
            return false;
        } catch (Exception e) {
            System.out.println("❌ Failed to toggle admin status: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
   
    
    
    public List<User> searchUsers(String query) {
        try (Session session = sessionFactory.openSession()) {
            Query<User> q = session.createQuery(
                "FROM User WHERE username LIKE :query OR fullName LIKE :query OR email LIKE :query",
                User.class);
            q.setParameter("query", "%" + query + "%");
            List<User> users = q.getResultList();
            System.out.println("✅ Found " + users.size() + " users for query: " + query);
            return users;
        } catch (Exception e) {
            System.out.println("❌ Failed to search users: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    public AdminStats getSystemStats() {
        AdminStats stats = new AdminStats();
        try (Session session = sessionFactory.openSession()) {
            // Total users
            try {
                Query<Long> userQuery = session.createQuery("SELECT COUNT(*) FROM User", Long.class);
                Long userCount = userQuery.uniqueResult();
                stats.setTotalUsers(userCount != null ? userCount.intValue() : 0);
                System.out.println("✅ Users counted: " + stats.getTotalUsers());
            } catch (Exception e) {
                System.out.println("❌ Failed to count users: " + e.getMessage());
                stats.setTotalUsers(0);
            }
    
            // Total issues
            try {
                Query<Long> issueQuery = session.createQuery("SELECT COUNT(*) FROM Issue", Long.class);
                Long issueCount = issueQuery.uniqueResult();
                stats.setTotalIssues(issueCount != null ? issueCount.intValue() : 0);
                System.out.println("✅ Issues counted: " + stats.getTotalIssues());
            } catch (Exception e) {
                System.out.println("❌ Failed to count issues: " + e.getMessage());
                stats.setTotalIssues(0);
            }
    
            // Open issues
            try {
                Query<Long> openIssueQuery = session.createQuery(
                    "SELECT COUNT(*) FROM Issue i WHERE i.status = :status", Long.class);
                openIssueQuery.setParameter("status", "open");
                Long openIssueCount = openIssueQuery.uniqueResult();
                stats.setOpenIssues(openIssueCount != null ? openIssueCount.intValue() : 0);
                System.out.println("✅ Open issues counted: " + stats.getOpenIssues());
            } catch (Exception e) {
                System.out.println("❌ Failed to count open issues: " + e.getMessage());
                stats.setOpenIssues(0);
            }
    
            // Total comments
            try {
                Query<Long> commentQuery = session.createQuery("SELECT COUNT(*) FROM Comment", Long.class);
                Long commentCount = commentQuery.uniqueResult();
                stats.setTotalComments(commentCount != null ? commentCount.intValue() : 0);
                System.out.println("✅ Comments counted: " + stats.getTotalComments());
            } catch (Exception e) {
                System.out.println("❌ Failed to count comments: " + e.getMessage());
                stats.setTotalComments(0);
            }
    
            // Active reports
            try {
                Query<Long> reportQuery = session.createQuery(
                    "SELECT COUNT(*) FROM Report r WHERE r.status = :status", Long.class);
                reportQuery.setParameter("status", "pending");
                Long reportCount = reportQuery.uniqueResult();
                stats.setActiveReports(reportCount != null ? reportCount.intValue() : 0);
                System.out.println("✅ Reports counted: " + stats.getActiveReports());
            } catch (Exception e) {
                System.out.println("❌ Failed to count reports: " + e.getMessage());
                stats.setActiveReports(0);
            }
    
            // System notices
            try {
                Query<Long> noticeQuery = session.createQuery(
                    "SELECT COUNT(*) FROM Notice n WHERE n.status = :status", Long.class);
                noticeQuery.setParameter("status", "active");
                Long noticeCount = noticeQuery.uniqueResult();
                stats.setSystemNotices(noticeCount != null ? noticeCount.intValue() : 0);
                System.out.println("✅ Notices counted: " + stats.getSystemNotices());
            } catch (Exception e) {
                System.out.println("❌ Failed to count notices: " + e.getMessage());
                stats.setSystemNotices(0);
            }
    
            System.out.println("✅ Fetched system stats: Users=" + stats.getTotalUsers() +
                ", Issues=" + stats.getTotalIssues() +
                ", OpenIssues=" + stats.getOpenIssues() +
                ", Comments=" + stats.getTotalComments() +
                ", Reports=" + stats.getActiveReports() +
                ", Notices=" + stats.getSystemNotices());
        } catch (Exception e) {
            System.out.println("❌ Failed to open session: " + e.getMessage());
            e.printStackTrace();
        }
        return stats;
    }

    /**
     * Update report status
     * @param reportId The report ID to update
     * @param status The new status
     * @return true if successful, false otherwise
     */
    public boolean updateReportStatus(int reportId, String status) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            Report report = session.get(Report.class, reportId);
            if (report == null) {
                System.out.println("❌ Report not found: ID=" + reportId);
                return false;
            }
            report.setStatus(status);
            session.merge(report);
            tx.commit();
            System.out.println("✅ Report status updated: ID=" + reportId + ", Status=" + status);
            return true;
        } catch (Exception e) {
            System.out.println("❌ Failed to update report status: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Inner class to hold system statistics
    public class AdminStats {
        private int totalUsers;
        private int totalIssues;
        private int openIssues;
        private int totalComments;
        private int activeReports;
        private int systemNotices;

        public int getTotalUsers() {
            return totalUsers;
        }

        public void setTotalUsers(int totalUsers) {
            this.totalUsers = totalUsers;
        }

        public int getTotalIssues() {
            return totalIssues;
        }

        public void setTotalIssues(int totalIssues) {
            this.totalIssues = totalIssues;
        }

        public int getOpenIssues() {
            return openIssues;
        }

        public void setOpenIssues(int openIssues) {
            this.openIssues = openIssues;
        }

        public int getTotalComments() {
            return totalComments;
        }

        public void setTotalComments(int totalComments) {
            this.totalComments = totalComments;
        }

        public int getActiveReports() {
            return activeReports;
        }

        public void setActiveReports(int activeReports) {
            this.activeReports = activeReports;
        }

        public int getSystemNotices() {
            return systemNotices;
        }

        public void setSystemNotices(int systemNotices) {
            this.systemNotices = systemNotices;
        }
    }

    
    public boolean deleteUser(int userId) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
    
            try {
                // Step 1: Delete related audit_logs entries
                Query<?> deleteAuditLogsQuery = session.createQuery(
                    "DELETE FROM AuditLog al WHERE al.admin.id = :userId");
                deleteAuditLogsQuery.setParameter("userId", userId);
                int auditLogsDeleted = deleteAuditLogsQuery.executeUpdate();
                System.out.println("✅ Deleted " + auditLogsDeleted + " audit log entries for user ID " + userId);

                // Step 2: Delete related comments (before issues)
                Query<?> deleteCommentsQuery = session.createQuery(
                    "DELETE FROM Comment c WHERE c.issue.id IN (SELECT i.id FROM Issue i WHERE i.user.id = :userId)");
                deleteCommentsQuery.setParameter("userId", userId);
                int commentsDeleted = deleteCommentsQuery.executeUpdate();
                System.out.println("✅ Deleted " + commentsDeleted + " comments for user ID " + userId);

                // Step 3: Delete related issues
                Query<?> deleteIssuesQuery = session.createQuery(
                    "DELETE FROM Issue i WHERE i.user.id = :userId");
                deleteIssuesQuery.setParameter("userId", userId);
                int issuesDeleted = deleteIssuesQuery.executeUpdate();
                System.out.println("✅ Deleted " + issuesDeleted + " issues for user ID " + userId);

                // Step 4: Delete related likes
                Query<?> deleteLikesQuery = session.createQuery(
                    "DELETE FROM Like l WHERE l.user.id = :userId");
                deleteLikesQuery.setParameter("userId", userId);
                int likesDeleted = deleteLikesQuery.executeUpdate();
                System.out.println("✅ Deleted " + likesDeleted + " likes for user ID " + userId);

                // Step 5: Delete the user
                Query<?> deleteUserQuery = session.createQuery(
                    "DELETE FROM User u WHERE u.id = :userId");
                deleteUserQuery.setParameter("userId", userId);
                int usersDeleted = deleteUserQuery.executeUpdate();

                if (usersDeleted > 0) {
                    tx.commit();
                    System.out.println("✅ User with ID " + userId + " deleted successfully.");
                    return true;
                } else {
                    tx.rollback();
                    System.out.println("❌ No user found with ID " + userId);
                    return false;
                }
    
            } catch (Exception e) {
                tx.rollback();
                System.out.println("❌ Failed to delete user: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
    
        } catch (Exception e) {
            System.out.println("❌ Session error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    public boolean deleteReportedContent(int reportId, String type) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteReportedContent'");
    }
}