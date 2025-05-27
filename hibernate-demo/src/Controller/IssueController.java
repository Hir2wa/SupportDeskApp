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

import java.security.Timestamp;
import java.time.LocalDateTime;
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

            // Validate non-nullable fields
            if (issue.getTitle() == null || issue.getTitle().isEmpty()) {
                System.out.println("❌ Invalid issue: title is required");
                return false;
            }
            if (issue.getDescription() == null || issue.getDescription().isEmpty()) {
                System.out.println("❌ Invalid issue: description is required");
                return false;
            }
            if (issue.getStatus() == null || issue.getStatus().isEmpty()) {
                issue.setStatus("OPEN");
                System.out.println("⚠️ Status was null; set to default: OPEN");
            }

            // Log Issue state
            System.out.println("Issue ID: " + issue.getId() + 
                ", Title: " + issue.getTitle() + 
                ", Status: " + issue.getStatus() + 
                ", User ID: " + userId);

            // Set timestamps
            if (issue.getCreatedAt() == null) {
                issue.setCreatedAt(new java.sql.Timestamp(System.currentTimeMillis()));
            }
            issue.setUpdatedAt(new java.sql.Timestamp(System.currentTimeMillis()));

            try {
                if (issue.getId() == null) {
                    session.persist(issue);
                    System.out.println("✅ New issue persisted: " + issue.getTitle());
                } else {
                    Issue mergedIssue = (Issue) session.merge(issue);
                    issue.setId(mergedIssue.getId());
                    System.out.println("✅ Issue merged: " + issue.getTitle());
                }
                tx.commit();
                System.out.println("✅ Issue posted: " + issue.getTitle() + " by User ID: " + userId);
                return true;
            } catch (Exception e) {
                tx.rollback();
                System.out.println("⚠️ Error persisting/merging issue: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        } catch (Exception e) {
            System.out.println("⚠️ Error opening session: " + e.getMessage());
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
                    "FROM Like WHERE issue.id = :issueId AND reactionType = :reactionType", Like.class);
                likeQuery.setParameter("issueId", issue.getId());
                likeQuery.setParameter("reactionType", Like.ReactionType.LIKE);
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
                    "FROM Like WHERE issue.id = :issueId AND reactionType = :reactionType", Like.class);
                likeQuery.setParameter("issueId", issueId);
                likeQuery.setParameter("reactionType", Like.ReactionType.LIKE);
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
            try {
                // Reattach the Issue and User to the current session
                Issue issue = comment.getIssue() != null ? session.merge(comment.getIssue()) : null;
                User user = session.find(User.class, userId);
    
                if (issue == null || user == null) {
                    System.err.println("Issue or User not found: issueId=" + 
                        (comment.getIssue() != null ? comment.getIssue().getId() : "null") + 
                        ", userId=" + userId);
                    tx.rollback();
                    return false;
                }
    
                comment.setIssue(issue);
                comment.setUser(user);
    
                session.persist(comment);
                tx.commit();
                System.out.println("✅ Comment added for Issue ID: " + issue.getId() + " by User ID: " + userId);
                return true;
            } catch (Exception e) {
                tx.rollback();
                System.err.println("Error adding comment: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
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
                "SELECT COUNT(*) FROM Like WHERE user.id = :userId AND issue.id = :issueId AND reactionType = :reactionType", Long.class);
            query.setParameter("userId", userId);
            query.setParameter("issueId", issueId);
            query.setParameter("reactionType", Like.ReactionType.LIKE);
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
                "SELECT COUNT(*) FROM Like WHERE user.id = :userId AND issue.id = :issueId AND reactionType = :reactionType", Long.class);
            query.setParameter("userId", userId);
            query.setParameter("issueId", issueId);
            query.setParameter("reactionType", Like.ReactionType.DISLIKE);
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
                "SELECT COUNT(*) FROM Like WHERE issue.id = :issueId AND reactionType = :reactionType", Long.class);
            query.setParameter("issueId", issueId);
            query.setParameter("reactionType", Like.ReactionType.LIKE);
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
                "SELECT COUNT(*) FROM Like WHERE issue.id = :issueId AND reactionType = :reactionType", Long.class);
            query.setParameter("issueId", issueId);
            query.setParameter("reactionType", Like.ReactionType.DISLIKE);
            return query.uniqueResult().intValue();
        } catch (Exception e) {
            System.out.println("⚠️ Error counting dislikes");
            e.printStackTrace();
            return 0;
        }
    }

    // Add or remove a like/dislike
    public boolean likeIssue(int userId, int issueId, Like.ReactionType reactionType) {
        if (issueId <= 0 || userId <= 0) {
            System.err.println("❌ Invalid userId or issueId: UserID=" + userId + ", IssueID=" + issueId);
            return false;
        }

        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            User user = session.get(User.class, userId);
            Issue issue = session.get(Issue.class, issueId);

            if (user == null || issue == null) {
                System.err.println("❌ User or Issue not found: UserID=" + userId + ", IssueID=" + issueId);
                if (tx.isActive()) tx.rollback();
                return false;
            }

            // Check for existing like/dislike
            Query<Like> existingQuery = session.createQuery(
                "FROM Like l WHERE l.user.id = :userId AND l.issue.id = :issueId",
                Like.class
            );
            existingQuery.setParameter("userId", userId);
            existingQuery.setParameter("issueId", issueId);
            Like existingLike = existingQuery.uniqueResult();

            if (reactionType == null) {
                // Remove existing like/dislike
                if (existingLike != null) {
                    session.delete(existingLike);
                    tx.commit();
                    System.out.println("✅ Removed like/dislike for Issue ID: " + issueId + " by User ID: " + userId);
                    return true;
                }
                if (tx.isActive()) tx.rollback();
                return false; // Nothing to remove
            }

            if (existingLike != null) {
                if (existingLike.getReactionType() == reactionType) {
                    if (tx.isActive()) tx.rollback();
                    System.out.println("❌ User already " + reactionType + "d Issue ID: " + issueId);
                    return false; // Already liked/disliked with same reaction
                }
                // Update existing like to new reaction type
                existingLike.setReactionType(reactionType);
                session.update(existingLike);
            } else {
                // Add new like/dislike
                Like like = new Like(user, issue, reactionType);
                session.save(like);
            }

            tx.commit();
            System.out.println("✅ Added/Updated " + reactionType + " for Issue ID: " + issueId + " by User ID: " + userId);
            return true;
        } catch (Exception e) {
            System.err.println("❌ Error adding/removing " + reactionType + ": " + e.getMessage());
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
                "SELECT COUNT(l) FROM Like l JOIN l.issue i WHERE i.user.id = :userId AND l.reactionType = :reactionType", Long.class);
            query.setParameter("userId", userId);
            query.setParameter("reactionType", Like.ReactionType.LIKE);
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
                "SELECT i, l FROM Issue i LEFT JOIN Like l ON i.id = l.issue.id AND l.reactionType = :reactionType " +
                "WHERE i.title ILIKE :pattern OR i.description ILIKE :pattern OR CAST(i.id AS string) = :searchQuery ", Object[].class);
            query.setParameter("reactionType", Like.ReactionType.LIKE);
            query.setParameter("pattern", pattern);
            query.setParameter("searchQuery", searchQuery);
            List<Object[]> resultList = query.getResultList();
            for (Object[] row : resultList) {
                Issue issue = (Issue) row[0];
                Like like = (Like) row[1];
                List<Like> likes = issue.getLikes() != null ? issue.getLikes() : new ArrayList<>();
                if (like != null && !likes.contains(like)) {
                    likes.add(like);
                }
                issue.setLikes(likes);
                if (!results.contains(issue)) {
                    results.add(issue);
                    System.out.println("Found issue: " + issue.getTitle() + " with " + likes.size() + " likes");
                }
            }
            System.out.println("Total issues found: " + results.size());
        } catch (Exception e) {
            System.out.println("⚠️ Search issues failed: " + e.getMessage());
            e.printStackTrace();
        }
        return results;
    }

    public long getIssueCount() {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery("SELECT COUNT(*) FROM Issue", Long.class);
            Long result = query.uniqueResult();
            System.out.println("Total Issues Count: " + (result != null ? result : 0L));
            return result != null ? result : 0L;
        }
    }

    public long getOpenIssueCount() {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery(
                "SELECT COUNT(*) FROM Issue WHERE status = :status", Long.class);
            query.setParameter("status", "open");
            Long result = query.uniqueResult();
            System.out.println("Open Issues Count: " + (result != null ? result : 0L));
            return result != null ? result : 0L;
        }
    }

    public long getCommentCount() {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery("SELECT COUNT(*) FROM Comment", Long.class);
            Long result = query.uniqueResult();
            System.out.println("Comments Count: " + (result != null ? result : 0L));
            return result != null ? result : 0L;
        }
    }



}