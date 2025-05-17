package Controller;

import model.User;
import model.Report;
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
    public boolean postNotice(String title, String content, int postedBy) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            User user = session.get(User.class, postedBy);
            if (user == null) {
                System.out.println("❌ User not found: ID=" + postedBy);
                return false;
            }
            Notice notice = new Notice(title, content, user);
            session.persist(notice);
            tx.commit();
            System.out.println("✅ Notice posted: " + title + " by User ID: " + postedBy);
            return true;
        } catch (Exception e) {
            System.out.println("❌ Failed to post notice");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get all notices ordered by creation date
     * @return List of notices
     */
    public List<Notice> getAllNotices() {
        List<Notice> notices = new ArrayList<>();
        try (Session session = sessionFactory.openSession()) {
            Query<Notice> query = session.createQuery("FROM Notice ORDER BY createdAt DESC", Notice.class);
            notices = query.getResultList();
            System.out.println("✅ Fetched " + notices.size() + " notices");
        } catch (Exception e) {
            System.out.println("❌ Failed to get notices");
            e.printStackTrace();
        }
        return notices;
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
    public model.AdminStats getSystemStats() {
        model.AdminStats stats = new model.AdminStats();
        try (Session session = sessionFactory.openSession()) {
            // Total users
            Query<Long> userQuery = session.createQuery("SELECT COUNT(*) FROM User", Long.class);
            stats.setTotalUsers(userQuery.uniqueResult().intValue());

            // Total issues
            Query<Long> issueQuery = session.createQuery("SELECT COUNT(*) FROM Issue", Long.class);
            stats.setTotalIssues(issueQuery.uniqueResult().intValue());

            // Total comments
            Query<Long> commentQuery = session.createQuery("SELECT COUNT(*) FROM Comment", Long.class);
            stats.setTotalComments(commentQuery.uniqueResult().intValue());

            // Active reports
            Query<Long> reportQuery = session.createQuery("SELECT COUNT(*) FROM Report", Long.class);
            stats.setActiveReports(reportQuery.uniqueResult().intValue());

            // Recent activity (issues in last 7 days)
            Query<Long> recentQuery = session.createQuery(
                "SELECT COUNT(*) FROM Issue WHERE createdAt >= :date", Long.class);
            recentQuery.setParameter("date", new java.sql.Timestamp(
                System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000));
            stats.setRecentActivity(recentQuery.uniqueResult().intValue());

            System.out.println("✅ Fetched system stats");
        } catch (Exception e) {
            System.out.println("❌ Failed to get system stats");
            e.printStackTrace();
        }
        return stats;
    }

    // Inner class to hold system statistics
    public class AdminStats {
        private int totalUsers;
        private int totalIssues;
        private int totalComments;
        private int activeReports;
        private int recentActivity;

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

        public int getRecentActivity() {
            return recentActivity;
        }

        public void setRecentActivity(int recentActivity) {
            this.recentActivity = recentActivity;
        }
    }
}