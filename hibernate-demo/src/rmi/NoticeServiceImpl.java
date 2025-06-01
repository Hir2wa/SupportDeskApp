// File: NoticeServiceImpl.java
package rmi;

import Controller.NoticeController;
import Controller.UserController;
import model.Notice;
import model.User;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

/**
 * RMI Implementation of NoticeService
 */
public class NoticeServiceImpl extends UnicastRemoteObject implements NoticeService {
    
    private final NoticeController noticeController;
    private final UserController userController;
    
    public NoticeServiceImpl() throws RemoteException {
        super();
        this.noticeController = new NoticeController();
        this.userController = new UserController();
        System.out.println("✅ NoticeServiceImpl initialized for RMI");
    }
    
    @Override
    public boolean createNotice(Notice notice, int adminId) throws RemoteException {
        try {
            User admin = userController.getUserById(adminId);
            if (admin == null || !admin.isAdmin()) {
                throw new RemoteException("Only admins can create notices");
            }
            return noticeController.createNotice(notice, adminId);
        } catch (Exception e) {
            System.err.println("⚠️⚠️ RMI Error in createNotice: " + e.getMessage());
            throw new RemoteException("Create notice failed", e);
        }
    }
    
    @Override
    public boolean updateNotice(int noticeId, Notice notice, int adminId) throws RemoteException {
        try {
            User admin = userController.getUserById(adminId);
            if (admin == null || !admin.isAdmin()) {
                throw new RemoteException("Only admins can update notices");
            }
            notice.setId(noticeId);
            return noticeController.updateNotice(notice);
        } catch (Exception e) {
            System.err.println("⚠️ RMI Error in updateNotice: " + e.getMessage());
            throw new RemoteException("Update notice failed", e);
        }
    }
    
    @Override
    public boolean deleteNotice(int noticeId, int adminId) throws RemoteException {
        try {
            User admin = userController.getUserById(adminId);
            if (admin == null || !admin.isAdmin()) {
                throw new RemoteException("Only admins can delete notices");
            }
            return noticeController.deleteNotice(noticeId);
        } catch (Exception e) {
            System.err.println("⚠️ RMI Error in deleteNotice: " + e.getMessage());
            throw new RemoteException("Delete notice failed", e);
        }
    }
    
    @Override
    public Notice getNoticeById(int noticeId) throws RemoteException {
        try {
            return noticeController.getNoticeById(noticeId);
        } catch (Exception e) {
            System.err.println("⚠️ RMI Error in getNoticeById: " + e.getMessage());
            throw new RemoteException("Get notice failed", e);
        }
    }
    
    @Override
    public List<Notice> getActiveNotices() throws RemoteException {
        try {
            return noticeController.getAllNotices();
        } catch (Exception e) {
            System.err.println("⚠️ RMI Error in getActiveNotices: " + e.getMessage());
            throw new RemoteException("Get active notices failed", e);
        }
    }
    
    @Override
    public List<Notice> getNoticesByUserId(int userId) throws RemoteException {
        try {
            return noticeController.getNoticesByUserId(userId);
        } catch (Exception e) {
            System.err.println("⚠️ RMI Error in getNoticesByUserId: " + e.getMessage());
            throw new RemoteException("Get user notices failed", e);
        }
    }
}