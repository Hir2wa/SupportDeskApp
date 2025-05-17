package model;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "likes")
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "liked_at")
    private Timestamp likedAt;

    @ManyToOne
    @JoinColumn(name = "liked_user_id")
    private User likedUser;

    @ManyToOne
    @JoinColumn(name = "issue_id")
    private Issue issue;

    @Enumerated(EnumType.STRING)
    @Column(name = "reaction_type", nullable = false)
    private ReactionType reactionType;

    public enum ReactionType {
        LIKE, DISLIKE
    }

    // Constructors
    public Like() {
        this.reactionType = ReactionType.LIKE;
    }

    public Like(User user, Issue issue) {
        this.user = user;
        this.issue = issue;
        this.reactionType = ReactionType.LIKE;
    }

    public Like(User user, Issue issue, ReactionType reactionType) {
        this.user = user;
        this.issue = issue;
        this.reactionType = reactionType != null ? reactionType : ReactionType.LIKE;
    }

    public Like(User user, User likedUser, ReactionType reactionType) {
        this.user = user;
        this.likedUser = likedUser;
        this.reactionType = reactionType != null ? reactionType : ReactionType.LIKE;
    }

    // Legacy constructor for HomePageView
    public Like(int issueId, String username) {
        this.issue = new Issue();
        this.issue.setId(issueId);
        this.user = new User();
        this.user.setUsername(username);
        this.reactionType = ReactionType.LIKE;
    }

    // Lifecycle callback
    @PrePersist
    protected void onCreate() {
        likedAt = new Timestamp(System.currentTimeMillis());
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Timestamp getLikedAt() { return likedAt; }
    public void setLikedAt(Timestamp likedAt) { this.likedAt = likedAt; }

    public User getLikedUser() { return likedUser; }
    public void setLikedUser(User likedUser) { this.likedUser = likedUser; }

    public Issue getIssue() { return issue; }
    public void setIssue(Issue issue) { this.issue = issue; }

    public ReactionType getReactionType() { return reactionType; }
    public void setReactionType(ReactionType reactionType) {
        this.reactionType = reactionType != null ? reactionType : ReactionType.LIKE;
    }

    // Legacy setter for string reactionType
    public void setReactionType(String reactionType) {
        this.reactionType = reactionType != null && reactionType.equalsIgnoreCase("dislike") ?
            ReactionType.DISLIKE : ReactionType.LIKE;
    }

    // Convenience methods
    public Integer getUserId() { return user != null ? user.getId() : null; }
    public Integer getIssueId() { return issue != null ? issue.getId() : null; }
    public Integer getLikedUserId() { return likedUser != null ? likedUser.getId() : null; }
}