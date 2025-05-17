package model;


import java.sql.Timestamp;

import jakarta.persistence.*;

@Entity
@Table(name = "reports")
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "reported_by", nullable = false)
    private User reportedBy;

    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @ManyToOne
    @JoinColumn(name = "issue_id")
    private Issue issue;

    @Column(name = "reason", nullable = false)
    private String reason;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "status", columnDefinition = "VARCHAR(50) DEFAULT 'pending'")
    private String status;

    // Constructors
    public Report() {
        this.status = "pending"; // Match database default
    }

    public Report(User reportedBy, Comment comment, Issue issue, String reason) {
        this.reportedBy = reportedBy;
        this.comment = comment;
        this.issue = issue;
        this.reason = reason;
        this.status = "pending";
    }

    // Lifecycle callback
    @PrePersist
    protected void onCreate() {
        createdAt = new Timestamp(System.currentTimeMillis());
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getReportedBy() {
        return reportedBy;
    }

    public void setReportedBy(User reportedBy) {
        this.reportedBy = reportedBy;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public Issue getIssue() {
        return issue;
    }

    public void setIssue(Issue issue) {
        this.issue = issue;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}