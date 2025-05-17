    package Controller;

import model.Issue;
import model.Comment;
import model.Like;
import model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import util.HibernateUtil;
import java.util.ArrayList;
import java.util.List;

public class IssueController {
    private final SessionFactory sessionFactory;

    public IssueController() {
        this.sessionFactory = HibernateUtil.getSessionFactory();
        System.out.println("✅ Issue Hibernate SessionFactory initialized!");
    }

    // Post new issue
    public boolean postIssue(Issue issue, int userId) {
                try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            User user = session.get(User.class, userId);
            if (user == null) {
                System.out.println("❌ User not found: ID=" + userId);
                return false;
            }
            issue.setUser(user);
            session.persist(issue);
            tx.commit();
            System.out.println("✅ Issue posted: " + issue.getTitle() + " by User ID: " + userId);
            return true;
        } catch (Exception e) {
            System.out.println("⚠️ Error posting issue: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Get all issues
    public List<Issue> getAllIssues() {
        List<Issue> issues = new ArrayList<>();
        try (Session session = sessionFactory.openSession()) {
            Query<Issue> query = session.createQuery("FROM Issue ORDER BY id DESC", Issue.class);
            issues = query.getResultList();
            // Set like counts for GUI compatibility
            for (Issue issue : issues) {
                Query<Like> likeQuery = session.createQuery(
                    "FROM Like WHERE issue.id = :issueId AND reactionType = 'like'", Like.class);
                likeQuery.setParameter("issueId", issue.getId());
                issue.setLikes(likeQuery.getResultList());
            }
        } catch (Exception e) {
            System.out.println("⚠️ Error fetching all issues");
            e.printStackTrace();
        }
        return issues;
    }

    // Get issue by ID
    public Issue getIssueById(int issueId) {
        
        try (Session session = sessionFactory.openSession()) {
            Issue issue = session.get(Issue.class, issueId);
            if (issue != null) {
                // Set like count for GUI compatibility
                Query<Like> likeQuery = session.createQuery(
                    "FROM Like WHERE issue.id = :issueId AND reactionType = 'like'", Like.class);
                likeQuery.setParameter("issueId", issueId);
                issue.setLikes(likeQuery.getResultList());
                System.out.println("✅ Found issue: ID=" + issueId);
            } else {
                System.out.println("❌ No issue found with ID: " + issueId);
            }
            return issue;
        } catch (Exception e) {
            System.out.println("⚠️ Error fetching issue by ID");
            e.printStackTrace();
            return null;
        }
    }

    // Get all comments for an issue
    public List<Comment> getCommentsForIssue(int issueId) {
        List<Comment> comments = new ArrayList<>();
        try (Session session = sessionFactory.openSession()) {
            Query<Comment> query = session.createQuery(
                "FROM Comment WHERE issue.id = :issueId ORDER BY createdAt ASC", Comment.class);
            query.setParameter("issueId", issueId);
            comments = query.getResultList();
        } catch (Exception e) {
            System.out.println("⚠️ Error fetching comments for issue: " + issueId);
            e.printStackTrace();
        }
        return comments;
    }

    // Comment on an issue
    public boolean addComment(Comment comment, int userId) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            User user = session.get(User.class, userId);
            Issue issue = session.get(Issue.class, comment.getId());
            if (user == null || issue == null) {
                System.out.println("❌ User or Issue not found: UserID=" + userId + ", IssueID=" + comment.getId());
                return false;
            }
            comment.setUser(user);
            comment.setIssue(issue);
            session.persist(comment);
            tx.commit();
            System.out.println("✅ Comment added to Issue ID: " + comment.getId());
            return true;
        } catch (Exception e) {
            System.out.println("⚠️ Error adding comment");
            e.printStackTrace();
            return false;
        }
    }

    // Delete a comment
    public boolean deleteComment(int commentId) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            Comment comment = session.get(Comment.class, commentId);
            if (comment == null) {
                System.out.println("❌ Comment not found: ID=" + commentId);
                return false;
            }
            session.remove(comment);
            tx.commit();
            System.out.println("✅ Comment deleted: ID=" + commentId);
            return true;
        } catch (Exception e) {
            System.out.println("⚠️ Error deleting comment");
            e.printStackTrace();
            return false;
        }
    }

    // Delete an issue and its associated comments and likes
    public boolean deleteIssue(int issueId) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            Issue issue = session.get(Issue.class, issueId);
            if (issue == null) {
                System.out.println("❌ Issue not found: ID=" + issueId);
                return false;
            }
            // Hibernate cascades deletes for comments and likes (ON DELETE CASCADE in schema)
            session.remove(issue);
            tx.commit();
            System.out.println("✅ Issue deleted: ID=" + issueId);
            return true;
        } catch (Exception e) {
            System.out.println("⚠️ Error deleting issue");
            e.printStackTrace();
            return false;
        }
    }

    // Check if a user has liked an issue
    public boolean hasUserLikedIssue(int userId, int issueId) {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery(
                "SELECT COUNT(*) FROM Like WHERE user.id = :userId AND issue.id = :issueId AND reactionType = 'like'", Long.class);
            query.setParameter("userId", userId);
            query.setParameter("issueId", issueId);
            return query.uniqueResult() > 0;
        } catch (Exception e) {
            System.out.println("⚠️ Error checking like status");
            e.printStackTrace();
            return false;
        }
    }

    // Check if a user has disliked an issue
    public boolean hasUserDislikedIssue(int userId, int issueId) {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery(
                "SELECT COUNT(*) FROM Like WHERE user.id = :userId AND issue.id = :issueId AND reactionType = 'dislike'", Long.class);
            query.setParameter("userId", userId);
            query.setParameter("issueId", issueId);
            return query.uniqueResult() > 0;
        } catch (Exception e) {
            System.out.println("⚠️ Error checking dislike status");
            e.printStackTrace();
            return false;
        }
    }

    // Get the number of likes for an issue
    public int getLikeCount(int issueId) {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery(
                "SELECT COUNT(*) FROM Like WHERE issue.id = :issueId AND reactionType = 'like'", Long.class);
            query.setParameter("issueId", issueId);
            return query.uniqueResult().intValue();
        } catch (Exception e) {
            System.out.println("⚠️ Error counting likes");
            e.printStackTrace();
            return 0;
        }
    }

    // Get the number of dislikes for an issue
    public int getDislikeCount(int issueId) {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery(
                "SELECT COUNT(*) FROM Like WHERE issue.id = :issueId AND reactionType = 'dislike'", Long.class);
            query.setParameter("issueId", issueId);
            return query.uniqueResult().intValue();
        } catch (Exception e) {
            System.out.println("⚠️ Error counting dislikes");
            e.printStackTrace();
            return 0;
        }
    }

    // Add a like to an issue if not already liked
    public boolean likeIssue(Like like, int userId) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            if (hasUserLikedIssue(userId, like.getId())) {
                System.out.println("❌ User already liked Issue ID: " + like.getId());
                return false;
            }
            // Remove existing dislike if present
            Query<?> deleteQuery = session.createQuery(
                "DELETE FROM Like WHERE user.id = :userId AND issue.id = :issueId");
            deleteQuery.setParameter("userId", userId);
            deleteQuery.setParameter("issueId", like.getId());
            deleteQuery.executeUpdate();
            // Add like
            User user = session.get(User.class, userId);
            Issue issue = session.get(Issue.class, like.getId());
            if (user == null || issue == null) {
                System.out.println("❌ User or Issue not found: UserID=" + userId + ", IssueID=" + issue.getId());
                return false;
            }
            Like newLike = new Like(user, issue);
            newLike.setReactionType("like");
            session.persist(newLike);
            tx.commit();
            System.out.println("✅ Like added to Issue ID: " + like.getId());
            return true;
        } catch (Exception e) {
            System.out.println("⚠️ Error adding like");
            e.printStackTrace();
            return false;
        }
    }

    // Add a dislike to an issue if not already disliked
    public boolean dislikeIssue(Like like, int userId) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            if (hasUserDislikedIssue(userId, like.getId())) {
                System.out.println("❌ User already disliked Issue ID: " + like.getId());
                return false;
            }
            // Remove existing like if present
            Query<?> deleteQuery = session.createQuery(
                "DELETE FROM Like WHERE user.id = :userId AND issue.id = :issueId");
            deleteQuery.setParameter("userId", userId);
            deleteQuery.setParameter("issueId", like.getId());
            deleteQuery.executeUpdate();
            // Add dislike
            User user = session.get(User.class, userId);
            Issue issue = session.get(Issue.class, like.getId());
            if (user == null || issue == null) {
                System.out.println("❌ User or Issue not found: UserID=" + userId + ", IssueID=" + like.getId());
                return false;
            }
            Like newLike = new Like(user, issue);
            newLike.setReactionType("dislike");
            session.persist(newLike);
            tx.commit();
            System.out.println("✅ Dislike added to Issue ID: " + like.getId());
            return true;
        } catch (Exception e) {
            System.out.println("⚠️ Error adding dislike");
            e.printStackTrace();
            return false;
        }
    }

    // Remove a dislike from an issue
    public boolean undislikeIssue(int userId, int issueId) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            Query<?> query = session.createQuery(
                "DELETE FROM Like WHERE user.id = :userId AND issue.id = :issueId AND reactionType = 'dislike'");
            query.setParameter("userId", userId);
            query.setParameter("issueId", issueId);
            int rowsAffected = query.executeUpdate();
            tx.commit();
            if (rowsAffected > 0) {
                System.out.println("✅ Dislike removed from Issue ID: " + issueId);
            } else {
                System.out.println("❌ No dislike found to remove for Issue ID: " + issueId);
            }
            return rowsAffected > 0;
        } catch (Exception e) {
            System.out.println("⚠️ Error removing dislike");
            e.printStackTrace();
            return false;
        }
    }

    // Remove a like from an issue
    public boolean unlikeIssue(int userId, int issueId) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            Query<?> query = session.createQuery(
                "DELETE FROM Like WHERE user.id = :userId AND issue.id = :issueId AND reactionType = 'like'");
            query.setParameter("userId", userId);
            query.setParameter("issueId", issueId);
            int rowsAffected = query.executeUpdate();
            tx.commit();
            if (rowsAffected > 0) {
                System.out.println("✅ Like removed from Issue ID: " + issueId);
            } else {
                System.out.println("❌ No like found to remove for Issue ID: " + issueId);
            }
            return rowsAffected > 0;
        } catch (Exception e) {
            System.out.println("⚠️ Error removing like");
            e.printStackTrace();
            return false;
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

    // 🔍 Search for issues by title, description, or ID
    public ArrayList<Issue> searchIssues(String searchQuery) {
        System.out.println("Searching for issues with query: " + searchQuery);
        ArrayList<Issue> results = new ArrayList<>();
        try (Session session = sessionFactory.openSession()) {
            String pattern = "%" + searchQuery + "%";
            Query<Object[]> query = session.createQuery(
                "SELECT i, l FROM Issue i LEFT JOIN Like l ON i.id = l.issue.id AND l.reactionType = 'like' " +
                "WHERE i.title ILIKE :pattern OR i.description ILIKE :pattern OR CAST(i.id AS string) = :searchQuery ", Object[].class);
            query.setParameter("pattern", pattern);
            query.setParameter("searchQuery", searchQuery);
            List<Object[]> resultList = query.getResultList();
            for (Object[] row : resultList) {
                Issue issue = (Issue) row[0];
                List<Like> likes = (List<Like>) row[1];
                issue.setLikes(likes);
                results.add(issue);
                System.out.println("Found issue: " + issue.getTitle() + " with " + issue.getLikes().size() + " likes");
            }
            System.out.println("Total issues found: " + results.size());
        } catch (Exception e) {
            System.out.println("⚠️ Search issues failed: " + e.getMessage());
            e.printStackTrace();
        }
        return results;
    }
}