package rmi;

import model.User;
import model.AuditLog;
import model.Issue;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

/**
 * Remote interface for User operations
 * This defines all the methods that can be called remotely
 */
public interface UserService extends Remote {
    
    // Authentication methods
    User loginAndGetUser(String username, String password) throws RemoteException;
    boolean loginUser(String username, String password) throws RemoteException;
    boolean registerUser(User user) throws RemoteException;
    void logout() throws RemoteException;
    User authenticate(String username, String password) throws RemoteException;
    
    // Password reset methods
    String sendPasswordResetOTP(String email) throws RemoteException;
  
    public boolean resetPassword(String email, String otp, String newPassword) throws RemoteException;
    String generateOTP() throws RemoteException;
    
    // User management methods
    User getUserByUsername(String username) throws RemoteException;
    User getUserById(Integer userId) throws RemoteException;
    boolean addUser(User user) throws RemoteException;
    boolean updateUser(int userId, String username, String email, String password) throws RemoteException;
    
    // Search methods
    ArrayList<User> searchUsers(String searchQuery) throws RemoteException;
    ArrayList<Issue> searchIssues(String searchQuery) throws RemoteException;
    
    // Statistics methods
    int countIssuesByUserId(int userId) throws RemoteException;
    int countLikesReceivedByUserId(int userId) throws RemoteException;
    int countCommentsReceivedByUserId(int userId) throws RemoteException;
    int countCommentsMadeByUserId(int userId) throws RemoteException;
    
    // Admin methods
    boolean isMaintenanceMode() throws RemoteException;
    boolean setMaintenanceMode(boolean enabled) throws RemoteException;
    boolean setUserStatus(int userId, String status) throws RemoteException;
    List<AuditLog> getRecentAuditLogs(int limit) throws RemoteException;
    
    // Session management
    int getCurrentLoggedInUserId() throws RemoteException;
    void setCurrentLoggedInUserId(int userId) throws RemoteException;
    
    // Debug methods
    void debugSampleData() throws RemoteException;
    void debugGetUserById(int userId) throws RemoteException;
    void testMultipleSearchTerms() throws RemoteException;
}