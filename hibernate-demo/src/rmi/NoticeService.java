// File: NoticeService.java
package rmi;

import model.Notice;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Remote interface for Notice operations
 */
public interface NoticeService extends Remote {
    boolean createNotice(Notice notice, int adminId) throws RemoteException;
    boolean updateNotice(int noticeId, Notice notice, int adminId) throws RemoteException;
    boolean deleteNotice(int noticeId, int adminId) throws RemoteException;
    Notice getNoticeById(int noticeId) throws RemoteException;
    List<Notice> getActiveNotices() throws RemoteException;
    List<Notice> getNoticesByUserId(int userId) throws RemoteException;
}