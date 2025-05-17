package model;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "notices")
public class Notice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "title", length = 255, nullable = false)
    private String title;

    @Column(name = "content", nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "posted_by")
    private User postedBy;

    @Column(name = "created_at")
    private Timestamp createdAt;

    // Constructors
    public Notice() {}

    public Notice(int id, String title, String content, User postedBy, Timestamp createdAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.postedBy = postedBy;
        this.createdAt = createdAt;
    }

    public Notice(String title, String content, User postedBy) {
        this.title = title;
        this.content = content;
        this.postedBy = postedBy;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(User postedBy) {
        this.postedBy = postedBy;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}