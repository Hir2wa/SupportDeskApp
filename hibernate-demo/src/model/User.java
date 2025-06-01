package model;

import java.util.Date;
import java.sql.Timestamp;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "username", length = 50, unique = true, nullable = false)
    private String username;

    @Column(name = "full_name", length = 100, nullable = false)
    private String fullName;

    @Column(name = "email", length = 100, unique = true, nullable = false)
    private String email;

    @Column(name = "password", length = 255, nullable = false)
    private String password;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Column(name = "is_admin")
    private boolean isAdmin;

    @Column(name = "is_blocked")
    private boolean isBlocked;

    @Column(name = "reset_otp")
    private String resetOtp;

    @Column(name = "otp_created_at")
    private Date otpCreatedAt; // java.util.Date
    // Constructors
    public User() {
        this.isAdmin = false; // Match database default
        this.isBlocked = false; // Match database default
    }

    public User(int id, String fullName, String username, String email, String password) {
        this.id = id;
        this.fullName = fullName;
        this.username = username;
        this.email = email;
        this.password = password;
        this.isAdmin = false;
        this.isBlocked = false;
    }

    public User(int id, String fullName, String username, String email, String password, boolean isAdmin, boolean isBlocked) {
        this.id = id;
        this.fullName = fullName;
        this.username = username;
        this.email = email;
        this.password = password;
        this.isAdmin = isAdmin;
        this.isBlocked = isBlocked;
    }

    public User(String fullName, String username, String email, String password) {
        this.fullName = fullName;
        this.username = username;
        this.email = email;
        this.password = password;
        this.isAdmin = false;
        this.isBlocked = false;
    }

    // Lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        createdAt = new Timestamp(System.currentTimeMillis());
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Timestamp(System.currentTimeMillis());
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean isBlocked) {
        this.isBlocked = isBlocked;
    }

    public String getStatus() {
        return isBlocked ? "BLOCKED" : "ACTIVE";
    }
    
    public void setStatus(String status) {
        if ("BLOCKED".equalsIgnoreCase(status)) {
            this.isBlocked = true;
        } else if ("ACTIVE".equalsIgnoreCase(status)) {
            this.isBlocked = false;
        } else {
            throw new IllegalArgumentException("Invalid status: " + status);
        }
    }

    // Getters and setters
    public String getResetOtp() {
        return resetOtp;
    }

    public void setResetOtp(String resetOtp) {
        this.resetOtp = resetOtp;
    }

    public Date getOtpCreatedAt() {
        return otpCreatedAt;
    }

    public void setOtpCreatedAt(Date otpCreatedAt) {
        this.otpCreatedAt = otpCreatedAt;
    }
 
}