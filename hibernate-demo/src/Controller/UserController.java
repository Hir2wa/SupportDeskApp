package Controller;

import model.User;
import model.AuditLog;
import model.Issue;
import model.Like;
import model.SystemSettings;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;


import util.EmailService;
import util.HibernateUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.mindrot.jbcrypt.BCrypt;

public class UserController {
    private final SessionFactory sessionFactory;
    private static int currentLoggedInUserId = -1;

    public UserController() {
        this.sessionFactory = HibernateUtil.getSessionFactory();
        System.out.println("✅ Hibernate SessionFactory initialized successfully!");
    }





    public void setCurrentLoggedInUserId(int userId) {
        this.currentLoggedInUserId = userId;
    }


    public String generateOTP() {
        return String.format("%06d", new Random().nextInt(999999));
    }


    // Send OTP for password reset
    public String sendPasswordResetOTP(String email) {
        try (Session session = sessionFactory.openSession()) {
            User user = session.createQuery("FROM User WHERE email = :email", User.class)
                    .setParameter("email", email)
                    .uniqueResult();
            if (user == null) {
                System.out.println("❌ User not found: " + email);
                return null;
            }
            String otp = generateOTP();
            new EmailService().sendOTPEmail(email, otp);

            Transaction tx = session.beginTransaction();
            AuditLog auditLog = new AuditLog(user, "Sent password reset OTP", user.getId(), "User");
            session.persist(auditLog);
            tx.commit();

            System.out.println("✅ OTP sent to " + email);
            return otp; // For simplicity; store in DB for production
        } catch (Exception e) {
            System.out.println("⚠️ Error sending OTP: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }



    public boolean resetPassword(String email, String otp, String newPassword, String storedOtp) {
        try (Session session = sessionFactory.openSession()) {
            if (!otp.equals(storedOtp)) {
                System.out.println("❌ Invalid OTP for " + email);
                return false;
            }
            Transaction tx = session.beginTransaction();
            User user = session.createQuery("FROM User WHERE email = :email", User.class)
                    .setParameter("email", email)
                    .uniqueResult();
            if (user == null) {
                System.out.println("❌ User not found: " + email);
                return false;
            }
            user.setPassword(BCrypt.hashpw(newPassword, BCrypt.gensalt()));
            session.update(user);

            AuditLog auditLog = new AuditLog(user, "Password reset", user.getId(), "User");
            session.persist(auditLog);
            tx.commit();
            System.out.println("✅ Password reset for " + email);
            return true;
        } catch (Exception e) {
            System.out.println("⚠️ Error resetting password: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    // 🔐 Register a new user
    public boolean registerUser(User user) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
            session.persist(user);
            tx.commit();
            System.out.println("✅ User registered: " + user.getUsername());
            return true;
        } catch (Exception e) {
            System.out.println("⚠️ Registration failed");
            e.printStackTrace();
            return false;
        }
    }

    // 🔐 Authenticate user and return User object
    // Replace the existing loginAndGetUser method in UserController.java with this fixed version:

// 🔐 Authenticate user and return User object
public User loginAndGetUser(String username, String password) {
    try (Session session = sessionFactory.openSession()) {
        Query<User> query = session.createQuery(
            "FROM User WHERE username = :username", User.class);
        query.setParameter("username", username);
        User user = query.uniqueResult();
        
        if (user != null && BCrypt.checkpw(password, user.getPassword())) {
            if (user.isBlocked()) {
                System.out.println("❌ Login failed - User is blocked: " + username);
                return null;
            }
            currentLoggedInUserId = user.getId();
            System.out.println("✅ Login successful for user: " + username);
            return user;
        } else {
            System.out.println("❌ Login failed for user: " + username);
            return null;
        }
    } catch (Exception e) {
        System.out.println("⚠️ Login error");
        e.printStackTrace();
        return null;
    }
}

    // Keep for backward compatibility
    public boolean loginUser(String username, String password) {
        return loginAndGetUser(username, password) != null;
    }

    public int getCurrentLoggedInUserId() {
        return currentLoggedInUserId;
    }

    public void logout() {
        currentLoggedInUserId = -1;
        System.out.println("✅ User logged out");
    }

    // 📦 Fetch a user by username
    public User getUserByUsername(String username) {
        try (Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery("FROM User WHERE username = :username", User.class);
            query.setParameter("username", username);
            User user = query.uniqueResult();
            if (user != null) {
                System.out.println("✅ Found user: " + username);
            } else {
                System.out.println("❌ No user found with username: " + username);
            }
            return user;
        } catch (Exception e) {
            System.out.println("⚠️ Error fetching user by username");
            e.printStackTrace();
            return null;
        }
    }

    // 📦 Fetch a user by ID
    public User getUserById(Integer userId) {
        try (Session session = sessionFactory.openSession()) {
            User user = session.get(User.class, userId);
            if (user != null) {
                System.out.println("✅ Found user with ID: " + user.getId());
            } else {
                System.out.println("❌ No user found with ID: " + userId);
            }
            return user; // This line was missing
        } catch (Exception e) { // This catch block was missing
            System.out.println("⚠️ Error fetching user by ID");
            e.printStackTrace();
            return null;
        }
    }

    // 🧮 Count total issues submitted by a user
    public int countIssuesByUserId(int userId) {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery(
                "SELECT COUNT(*) FROM Issue WHERE user.id = :userId", Long.class);
            query.setParameter("userId", userId);
            return query.uniqueResult().intValue();
        } catch (Exception e) {
            System.out.println("⚠️ Error counting issues");
            e.printStackTrace();
            return 0;
        }
    }

    // ❤️ Count likes received on user's issues
    public int countLikesReceivedByUserId(int userId) {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery(
                "SELECT COUNT(l) FROM Like l JOIN l.issue i WHERE i.user.id = :userId AND l.reactionType = 'like'", Long.class);
            query.setParameter("userId", userId);
            return query.uniqueResult().intValue();
        } catch (Exception e) {
            System.out.println("⚠️ Error counting likes");
            e.printStackTrace();
            return 0;
        }
    }

    public boolean addUser(User user) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(user);
            session.getTransaction().commit();
            System.out.println("✅ User added: " + user.getUsername());
            return true;
        } catch (Exception e) {
            System.out.println("❌ Failed to add user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    // 💬 Count comments received on user's issues
    public int countCommentsReceivedByUserId(int userId) {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery(
                "SELECT COUNT(c) FROM Comment c WHERE c.issue.user.id = :userId", Long.class);
            query.setParameter("userId", userId);
            return query.uniqueResult().intValue();
        } catch (Exception e) {
            System.out.println("⚠️ Error counting comments received");
            e.printStackTrace();
            return 0;
        }
    }

    // 💭 Count comments made by the user
    public int countCommentsMadeByUserId(int userId) {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery(
                "SELECT COUNT(c) FROM Comment c WHERE c.user.id = :userId", Long.class);
            query.setParameter("userId", userId);
            return query.uniqueResult().intValue();
        } catch (Exception e) {
            System.out.println("⚠️ Error counting comments made");
            e.printStackTrace();
            return 0;
        }
    }

    // 🔄 Update user information
    public boolean updateUser(int userId, String username, String email, String password) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            User user = session.get(User.class, userId);
            if (user == null) {
                System.out.println("❌ No user found with ID: " + userId);
                return false;
            }
            user.setUsername(username);
            user.setEmail(email);
            if (!password.isEmpty()) {
                user.setPassword(password);
            }
            session.merge(user);
            tx.commit();
            System.out.println("✅ User updated: ID=" + userId + ", Username=" + username);
            return true;
        } catch (Exception e) {
            System.out.println("⚠️ Update failed");
            e.printStackTrace();
            return false;
        }
    }

    // 🔍 Search for users by username, email, full name, or ID
    public ArrayList<User> searchUsers(String searchQuery) {
        System.out.println("Searching for users with query: " + searchQuery);
        ArrayList<User> results = new ArrayList<>();
        try (Session session = sessionFactory.openSession()) {
            String pattern = "%" + searchQuery + "%";
            Query<User> query = session.createQuery(
                "FROM User WHERE username ILIKE :pattern OR email ILIKE :pattern OR fullName ILIKE :pattern OR CAST(id AS string) = :searchQuery", User.class);
            query.setParameter("pattern", pattern);
            query.setParameter("searchQuery", searchQuery);
            results.addAll(query.getResultList());
            System.out.println("Total users found: " + results.size());
            results.forEach(user -> System.out.println("Found user: " + user.getUsername() + " (ID: " + user.getId() + ")"));
        } catch (Exception e) {
            System.out.println("⚠️ Search users failed: " + e.getMessage());
            e.printStackTrace();
        }
        return results;
    }

    // 🔍 Search for issues by title, description, or ID (should move to IssueController)
    public ArrayList<Issue> searchIssues(String searchQuery) {
        Like like ;
        System.out.println("Searching for issues with query: " + searchQuery);
        ArrayList<Issue> results = new ArrayList<>();
        try (Session session = sessionFactory.openSession()) {
            String pattern = "%" + searchQuery + "%";
            Query<Object[]> query = session.createQuery(
                "SELECT i, COUNT(l) FROM Issue i LEFT JOIN Like l ON i.id = l.issue.id AND l.reactionType = 'like' " +
                "WHERE i.title ILIKE :pattern OR i.description ILIKE :pattern OR CAST(i.id AS string) = :searchQuery " +
                "GROUP BY i.id", Object[].class);
            query.setParameter("pattern", pattern);
            query.setParameter("searchQuery", searchQuery);
            List<Object[]> resultList = query.getResultList();
            for (Object[] row : resultList) {
                Issue issue = (Issue) row[0];
                Long likeCount = (Long) row[1];
                List<Like> likesList = new ArrayList<>();
                for (int i = 0; i < likeCount.intValue(); i++) {
                    likesList.add(new Like());
                }
                issue.setLikes(likesList);
                results.add(issue);
                System.out.println("Found issue: " + issue.getTitle() + " with " + issue.getLikes() + " likes");
            }
            System.out.println("Total issues found: " + results.size());
        } catch (Exception e) {
            System.out.println("⚠️ Search issues failed: " + e.getMessage());
            e.printStackTrace();
        }
        return results;
    }

    // Debugging methods
    public void debugSampleData() {
        try (Session session = sessionFactory.openSession()) {
            // Count users
            Query<Long> userCountQuery = session.createQuery("SELECT COUNT(*) FROM User", Long.class);
            long userCount = userCountQuery.uniqueResult();
            System.out.println("Total users in database: " + userCount);

            // Count issues
            Query<Long> issueCountQuery = session.createQuery("SELECT COUNT(*) FROM Issue", Long.class);
            long issueCount = issueCountQuery.uniqueResult();
            System.out.println("Total issues in database: " + issueCount);

            // Sample users
            if (userCount > 0) {
                Query<User> userQuery = session.createQuery("FROM User", User.class).setMaxResults(3);
                List<User> users = userQuery.getResultList();
                System.out.println("Sample users:");
                users.forEach(user -> System.out.println("  - ID: " + user.getId() +
                        ", Username: " + user.getUsername() + ", Email: " + user.getEmail()));
            }

            // Sample issues
            if (issueCount > 0) {
                Query<Issue> issueQuery = session.createQuery("FROM Issue", Issue.class).setMaxResults(3);
                List<Issue> issues = issueQuery.getResultList();
                System.out.println("Sample issues:");
                issues.forEach(issue -> System.out.println("  - ID: " + issue.getId() +
                        ", Title: " + issue.getTitle() + ", User ID: " + issue.getUser().getId()));
            }
        } catch (Exception e) {
            System.out.println("Error running debug sample data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void debugGetUserById(int userId) {
        System.out.println("Looking up user with ID: " + userId);
        User user = getUserById(userId);
        if (user != null) {
            System.out.println("Found user: " + user.getUsername() + " (ID: " + user.getId() + ")");
        } else {
            System.out.println("No user found with ID: " + userId);
            try (Session session = sessionFactory.openSession()) {
                Query<User> query = session.createQuery("FROM User WHERE id = :id", User.class);
                query.setParameter("id", userId);
                User dbUser = query.uniqueResult();
                if (dbUser != null) {
                    System.out.println("Database has user: " + dbUser.getUsername() + " (ID: " + dbUser.getId() + ")");
                } else {
                    System.out.println("Database has no user with ID: " + userId);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void testMultipleSearchTerms() {
        System.out.println("\n===== TESTING MULTIPLE SEARCH TERMS =====");
        String[] testTerms = {"4", "a", "e", "test", "issue", "user"};
        for (String term : testTerms) {
            System.out.println("\nTesting search with term: \"" + term + "\"");
            ArrayList<User> users = searchUsers(term);
            System.out.println("  Found " + users.size() + " users with term \"" + term + "\"");
            ArrayList<Issue> issues = searchIssues(term);
            System.out.println("  Found " + issues.size() + " issues with term \"" + term + "\"");
        }
    }


    public User authenticate(String username, String password) {
        try (Session session = sessionFactory.openSession()) {
            User user = session.createQuery("FROM User WHERE username = :username", User.class)
                    .setParameter("username", username)
                    .uniqueResult();
            if (user != null && BCrypt.checkpw(password, user.getPassword()) && !"BLOCKED".equals(user.getStatus())) {
                return user;
            }
            return null;
        } catch (Exception e) {
            System.err.println("⚠️ Error authenticating user: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // Check if system is in maintenance mode
    public boolean isMaintenanceMode() {
        try (Session session = sessionFactory.openSession()) {
            SystemSettings setting = session.createQuery("FROM SystemSettings WHERE settingKey = :key", SystemSettings.class)
                    .setParameter("key", "maintenance_mode")
                    .uniqueResult();
            return setting != null && "true".equals(setting.getSettingValue());
        } catch (Exception e) {
            System.err.println("⚠️ Error checking maintenance mode: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // Toggle maintenance mode (for admin)
    public boolean setMaintenanceMode(boolean enabled) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            
            // Use settingKey (property name) instead of setting_key (column name)
            SystemSettings setting = session.createQuery("FROM SystemSettings WHERE settingKey = :key", SystemSettings.class)
                    .setParameter("key", "maintenance_mode")
                    .uniqueResult();
                    
            if (setting == null) {
                setting = new SystemSettings();
                setting.setSettingKey("maintenance_mode");
                setting.setSettingValue(enabled ? "true" : "false");
                session.persist(setting);
            } else {
                setting.setSettingValue(enabled ? "true" : "false");
                session.merge(setting); // Use merge instead of update
            }
            
            tx.commit();
            System.out.println("✅ Maintenance mode set to: " + enabled);
            return true;
        } catch (Exception e) {
            System.err.println("⚠️ Error setting maintenance mode: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Block or unblock a user (for admin)
    public boolean setUserStatus(int userId, String status) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            User user = session.get(User.class, userId);
            if (user != null) {
                user.setStatus(status);
                session.update(user);
                tx.commit();
                System.out.println("✅ User ID " + userId + " status set to: " + status);
                return true;
            }
            tx.rollback();
            System.err.println("⚠️ User ID " + userId + " not found");
            return false;
        } catch (Exception e) {
            System.err.println("⚠️ Error setting user status: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }




    public List<AuditLog> getRecentAuditLogs(int limit) {
        try (Session session = sessionFactory.openSession()) {
            Query<AuditLog> query = session.createQuery(
                "FROM AuditLog ORDER BY timestamp DESC", AuditLog.class);
            query.setMaxResults(limit);
            List<AuditLog> logs = query.list();
            System.out.println("✅ Fetched " + logs.size() + " recent audit logs");
            return logs;
        } catch (Exception e) {
            System.err.println("⚠️ Error fetching audit logs: " + e.getMessage());
            e.printStackTrace();
            return List.of(); // Return empty list on error
        }
    }



    
}
