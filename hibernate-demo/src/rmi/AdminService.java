// File: AdminService.java
package rmi;

import model.User;
import model.Report;
import model.Issue;
import model.Notice;
import model.AuditLog;
import Controller.AdminController.AdminStats;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Remote interface for Admin operations
 */
public interface AdminService extends Remote {
    // User management
    boolean blockUser(int userId, int adminId) throws RemoteException;
    boolean unblockUser(int userId, int adminId) throws RemoteException;
    boolean toggleAdminStatus(int userId, int adminId) throws RemoteException;
    boolean deleteUser(int userId, int adminId) throws RemoteException;
    List<User> getAllUsers(int adminId) throws RemoteException;
    List<User> searchUsers(String query, int adminId) throws RemoteException;

    // Issue management
    boolean deleteIssue(int issueId, int adminId) throws RemoteException;

    // Report management
    List<Report> getReports(int adminId) throws RemoteException;
    Report getReportById(int reportId, int adminId) throws RemoteException;
    boolean updateReportStatus(int reportId, String status, int adminId) throws RemoteException;
    boolean generateReportPdf(String dest, Integer issueId, Integer commentId, Integer reportedBy, int adminId) throws RemoteException;

    // Notice management
    boolean postNotice(String title, String content, int adminId) throws RemoteException;
    boolean updateNotice(int noticeId, String title, String content, int adminId) throws RemoteException;
    boolean deleteNotice(int noticeId, int adminId) throws RemoteException;
    List<Notice> getAllNotices(int adminId) throws RemoteException;
    Notice getNoticeById(int noticeId, int adminId) throws RemoteException;

    // System stats
    AdminStats getSystemStats(int adminId) throws RemoteException;

    // Audit log management
    boolean createAuditLog(String action, Integer targetId, String targetType, int adminId) throws RemoteException;
    List<AuditLog> getAuditLogs(int adminId) throws RemoteException;
    List<AuditLog> getAuditLogsByAdminId(int targetAdminId, int adminId) throws RemoteException;
}