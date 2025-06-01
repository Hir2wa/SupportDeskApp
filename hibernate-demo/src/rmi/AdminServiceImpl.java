// // File: AdminServiceImpl.java
// package rmi;

// import Controller.AdminController;
// import Controller.UserController;
// import model.User;
// import model.Report;
// import model.Issue;
// import model.Notice;
// import model.AuditLog;
// import Controller.AdminController.AdminStats;
// import java.rmi.RemoteException;
// import java.rmi.server.UnicastRemoteObject;
// import java.util.ArrayList;
// import java.util.List;

// import javax.mail.Session;

// /**
//  * RMI Implementation of AdminService
//  */
// public class AdminServiceImpl extends UnicastRemoteObject implements AdminService {
    
//     private final AdminController adminController;
//     private final UserController userController;
    
//     public AdminServiceImpl() throws RemoteException {
//         super();
//         this.adminController = new AdminController();
//         this.userController = new UserController();
//         System.out.println("✅ AdminServiceImpl initialized for RMI");
//     }
    
//     private void checkAdmin(int adminId) throws RemoteException {
//         User admin = userController.getUserById(adminId);
//         if (admin == null || !admin.isAdmin()) {
//             throw new RemoteException("Only admins can perform this operation");
//         }
//     }
    
//     @Override
//     public boolean blockUser(int userId, int adminId) throws RemoteException {
//         try {
//             checkAdmin(adminId);
//             boolean success = adminController.blockUser(userId);
//             if (success) {
//                 createAuditLog("BLOCK_USER", userId, "USER", adminId);
//             }
//             return success;
//         } catch (Exception e) {
//             System.err.println("⚠️ RMI Error in blockUser: " + e.getMessage());
//             throw new RemoteException("Block user failed", e);
//         }
//     }
    
//     @Override
//     public boolean unblockUser(int userId, int adminId) throws RemoteException {
//         try {
//             checkAdmin(adminId);
//             boolean success = adminController.unblockUser(userId);
//             if (success) {
//                 createAuditLog("UNBLOCK_USER", userId, "USER", adminId);
//             }
//             return success;
//         } catch (Exception e) {
//             System.err.println("⚠️ RMI Error in unblockUser: " + e.getMessage());
//             throw new RemoteException("Unblock user failed", e);
//         }
//     }
    
//     @Override
//     public boolean toggleAdminStatus(int userId, int adminId) throws RemoteException {
//         try {
//             checkAdmin(adminId);
//             boolean success = adminController.toggleAdminStatus(userId);
//             if (success) {
//                 createAuditLog("TOGGLE_ADMIN", userId, "USER", adminId);
//             }
//             return success;
//         } catch (Exception e) {
//             System.err.println("⚠️ RMI Error in toggleAdminStatus: " + e.getMessage());
//             throw new RemoteException("Toggle admin status failed", e);
//         }
//     }
    
//     @Override
//     public boolean deleteUser(int userId, int adminId) throws RemoteException {
//         try {
//             checkAdmin(adminId);
//             boolean success = adminController.deleteUser(userId);
//             if (success) {
//                 createAuditLog("DELETE_USER", userId, "USER", adminId);
//             }
//             return success;
//         } catch (Exception e) {
//             System.err.println("⚠️ RMI Error in deleteUser: " + e.getMessage());
//             throw new RemoteException("Delete user failed", e);
//         }
//     }
    
//     @Override
//     public List<User> getAllUsers(int adminId) throws RemoteException {
//         try {
//             checkAdmin(adminId);
//             return adminController.getAllUsers();
//         } catch (Exception e) {
//             System.err.println("⚠️ RMI Error in getAllUsers: " + e.getMessage());
//             throw new RemoteException("Get all users failed", e);
//         }
//     }
    
//     @Override
//     public List<User> searchUsers(String query, int adminId) throws RemoteException {
//         try {
//             checkAdmin(adminId);
//             return adminController.searchUsers(query);
//         } catch (Exception e) {
//             System.err.println("⚠️ RMI Error in searchUsers: " + e.getMessage());
//             throw new RemoteException("Search users failed", e);
//         }
//     }
    
//     @Override
//     public boolean deleteIssue(int issueId, int adminId) throws RemoteException {
//         try {
//             checkAdmin(adminId);
//             boolean success = adminController.deleteIssue(issueId);
//             if (success) {
//                 createAuditLog("DELETE_ISSUE", issueId, "ISSUE", adminId);
//             }
//             return success;
//         } catch (Exception e) {
//             System.err.println("⚠️ RMI Error in deleteIssue: " + e.getMessage());
//             throw new RemoteException("Delete issue failed", e);
//         }
//     }
    
//     @Override
//     public List<Report> getReports(int adminId) throws RemoteException {
//         try {
//             checkAdmin(adminId);
//             return adminController.getReports();
//         } catch (Exception e) {
//             System.err.println("⚠️ RMI Error in getReports: " + e.getMessage());
//             throw new RemoteException("Get reports failed", e);
//         }
//     }
    
//     @Override
//     public Report getReportById(int reportId, int adminId) throws RemoteException {
//         try {
//             checkAdmin(adminId);
//             return adminController.getReportById(reportId);
//         } catch (Exception e) {
//             System.err.println("⚠️ RMI Error in getReportById: " + e.getMessage());
//             throw new RemoteException("Get report failed", e);
//         }
//     }
    
//     @Override
//     public boolean updateReportStatus(int reportId, String status, int adminId) throws RemoteException {
//         try {
//             checkAdmin(adminId);
//             boolean success = adminController.updateReportStatus(reportId, status);
//             if (success) {
//                 createAuditLog("UPDATE_REPORT_STATUS", reportId, "REPORT", adminId);
//             }
//             return success;
//         } catch (Exception e) {
//             System.err.println("⚠️ RMI Error in updateReportStatus: " + e.getMessage());
//             throw new RemoteException("Update report status failed", e);
//         }
//     }
    
//     @Override
//     public boolean generateReportPdf(String dest, Integer issueId, Integer commentId, Integer reportedBy, int adminId) throws RemoteException {
//         try {
//             checkAdmin(adminId);
//             boolean success = adminController.generateReportPdf(dest, issueId, commentId, reportedBy);
//             if (success) {
//                 createAuditLog("GENERATE_REPORT_PDF", null, "REPORT", adminId);
//             }
//             return success;
//         } catch (Exception e) {
//             System.err.println("⚠️ RMI Error in generateReportPdf: " + e.getMessage());
//             throw new RemoteException("Generate report PDF failed", e);
//         }
//     }
    
//     @Override
//     public boolean postNotice(String title, String content, int adminId) throws RemoteException {
//         try {
//             checkAdmin(adminId);
//             boolean success = adminController.postNotice(title, content, adminId);
//             if (success) {
//                 createAuditLog("POST_NOTICE", null, "NOTICE", adminId);
//             }
//             return success;
//         } catch (Exception e) {
//             System.err.println("⚠️ RMI Error in postNotice: " + e.getMessage());
//             throw new RemoteException("Post notice failed", e);
//         }
//     }
    
//     @Override
//     public boolean updateNotice(int noticeId, String title, String content, int adminId) throws RemoteException {
//         try {
//             checkAdmin(adminId);
//             boolean success = adminController.updateNotice(noticeId, title, content);
//             if (success) {
//                 createAuditLog("UPDATE_NOTICE", noticeId, "NOTICE", adminId);
//             }
//             return success;
//         } catch (Exception e) {
//             System.err.println("⚠️ RMI Error in updateNotice: " + e.getMessage());
//             throw new RemoteException("Update notice failed", e);
//         }
//     }
    
//     @Override
//     public boolean deleteNotice(int noticeId, int adminId) throws RemoteException {
//         try {
//             checkAdmin(adminId);
//             boolean success = adminController.deleteNotice(noticeId);
//             if (success) {
//                 createAuditLog("DELETE_NOTICE", noticeId, "NOTICE", adminId);
//             }
//             return success;
//         } catch (Exception e) {
//             System.err.println("⚠️ RMI Error in deleteNotice: " + e.getMessage());
//             throw new RemoteException("Delete notice failed", e);
//         }
//     }
    
//     @Override
//     public List<Notice> getAllNotices(int adminId) throws RemoteException {
//         try {
//             checkAdmin(adminId);
//             return adminController.getAllNotices();
//         } catch (Exception e) {
//             System.err.println("⚠️ RMI Error in getAllNotices: " + e.getMessage());
//             throw new RemoteException("Get all notices failed", e);
//         }
//     }
    
//     @Override
//     public Notice getNoticeById(int noticeId, int adminId) throws RemoteException {
//         try {
//             checkAdmin(adminId);
//             return adminController.getNoticeById(noticeId);
//         } catch (Exception e) {
//             System.err.println("⚠️ RMI Error in getNoticeById: " + e.getMessage());
//             throw new RemoteException("Get notice failed", e);
//         }
//     }
    
//     @Override
//     public AdminStats getSystemStats(int adminId) throws RemoteException {
//         try {
//             checkAdmin(adminId);
//             return adminController.getSystemStats();
//         } catch (Exception e) {
//             System.err.println("⚠️ RMI Error in getSystemStats: " + e.getMessage());
//             throw new RemoteException("Get system stats failed", e);
//         }
//     }
    
//     @Override
//     public boolean createAuditLog(String action, Integer targetId, String targetType, int adminId) throws RemoteException {
//         try {
//             checkAdmin(adminId);
//             User admin = userController.getUserById(adminId);
//             if (admin == null) {
//                 throw new RemoteException("Admin not found");
//             }
//             AuditLog log = new AuditLog(admin, action, targetId, targetType);
//             try (var session = adminController.getSessionFactory().openSession()) {
//                 var tx = session.beginTransaction();
//                 session.persist(log);
//                 tx.commit();
//                 System.out.println("✅ Audit log created: " + action);
//                 return true;4
//             }
//         } catch (Exception e) {
//             System.err.println("⚠️ RMI Error in createAuditLog: " + e.getMessage());
//             throw new RemoteException("Create audit log failed", e);
//         }
//     }
    
//     @Override
//     public List<AuditLog> getAuditLogs(int adminId) throws RemoteException {
//         try {
//             checkAdmin(adminId);
//             try (Session session = adminController.getSessionFactory().openSession()) {
//                 var query = session.createQuery("FROM AuditLog ORDER BY createdAt DESC", AuditLog.class);
//                 return query.getResultList();
//             }
//         } catch (Exception e) {
//             System.err.println("⚠️ RMI Error in getAuditLogs: " + e.getMessage());
//             throw new RemoteException("Get audit logs failed", e);
//         }
//     }
    
//     @Override
//     public List<AuditLog> getAuditLogsByAdminId(int targetAdminId, int adminId) throws RemoteException {
//         try {
//             checkAdmin(adminId);
//             try (var session = adminController.getSessionFactory().openSession()) {
//                 var query = session.createQuery(
//                     "FROM AuditLog WHERE admin.id = :adminId ORDER BY createdAt DESC", AuditLog.class);
//                 query.setParameter("adminId", targetAdminId);
//                 return query.getResultList();
//             }
//         } catch (Exception e) {
//             System.err.println("⚠️ RMI Error in getAuditLogsByAdminId: " + e.getMessage());
//             throw new RemoteException("Get audit logs by admin failed", e);
//         }
//     }
// }