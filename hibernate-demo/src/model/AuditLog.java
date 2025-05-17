package model;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "audit_logs")
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private User admin;

    @Column(name = "action", length = 100)
    private String action;

    @Column(name = "target_id")
    private Integer targetId;

    @Column(name = "target_type", length = 50)
    private String targetType;

    @Column(name = "created_at")
    private Timestamp createdAt;

    // Constructors
    public AuditLog() {}

    public AuditLog(User admin, String action, Integer targetId, String targetType) {
        this.admin = admin;
        this.action = action;
        this.targetId = targetId;
        this.targetType = targetType;
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

    public User getAdmin() {
        return admin;
    }

    public void setAdmin(User admin) {
        this.admin = admin;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Integer getTargetId() {
        return targetId;
    }

    public void setTargetId(Integer targetId) {
        this.targetId = targetId;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}