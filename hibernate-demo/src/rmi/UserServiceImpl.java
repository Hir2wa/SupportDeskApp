package rmi;

import Controller.UserController;
import model.User;
import model.AuditLog;
import model.Issue;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

/**
 * RMI Implementation of UserService
 * This class wraps your existing UserController to work with RMI
 */
public class UserServiceImpl extends UnicastRemoteObject implements UserService {
    
    private final UserController userController;
    
    public UserServiceImpl() throws RemoteException {
        super();
        this.userController = new UserController();
        System.out.println("✅ UserServiceImpl initialized for RMI");
    }
    
    // Authentication methods
    @Override
    public User loginAndGetUser(String username, String password) throws RemoteException {
        try {
            return userController.loginAndGetUser(username, password);
        } catch (Exception e) {
            System.err.println("⚠️ RMI Error in loginAndGetUser: " + e.getMessage());
            throw new RemoteException("Login failed", e);
        }
    }
    
    @Override
    public boolean loginUser(String username, String password) throws RemoteException {
        try {
            return userController.loginUser(username, password);
        } catch (Exception e) {
            System.err.println("⚠️ RMI Error in loginUser: " + e.getMessage());
            throw new RemoteException("Login failed", e);
        }
    }
    
    @Override
    public boolean registerUser(User user) throws RemoteException {
        try {
            return userController.registerUser(user);
        } catch (Exception e) {
            System.err.println("⚠️ RMI Error in registerUser: " + e.getMessage());
            throw new RemoteException("Registration failed", e);
        }
    }
    
    @Override
    public void logout() throws RemoteException {
        try {
            userController.logout();
        } catch (Exception e) {
            System.err.println("⚠️ RMI Error in logout: " + e.getMessage());
            throw new RemoteException("Logout failed", e);
        }
    }
    
    @Override
    public User authenticate(String username, String password) throws RemoteException {
        try {
            return userController.authenticate(username, password);
        } catch (Exception e) {
            System.err.println("⚠️ RMI Error in authenticate: " + e.getMessage());
            throw new RemoteException("Authentication failed", e);
        }
    }
    
    // Password reset methods
    @Override
    public String sendPasswordResetOTP(String email) throws RemoteException {
        try {
            return userController.sendPasswordResetOTP(email);
        } catch (Exception e) {
            System.err.println("⚠️ RMI Error in sendPasswordResetOTP: " + e.getMessage());
            throw new RemoteException("OTP sending failed", e);
        }
    }
    
    @Override
    public boolean resetPassword(String email, String otp, String newPassword) throws RemoteException {
        try {
            return userController.resetPassword(email, otp, newPassword);
        } catch (Exception e) {
            System.err.println("⚠️ RMI Error in resetPassword: " + e.getMessage());
            throw new RemoteException("Password reset failed", e);
        }
    }
    @Override
    public String generateOTP() throws RemoteException {
        try {
            return userController.generateOTP();
        } catch (Exception e) {
            System.err.println("⚠️ RMI Error in generateOTP: " + e.getMessage());
            throw new RemoteException("OTP generation failed", e);
        }
    }
    
    // User management methods
    @Override
    public User getUserByUsername(String username) throws RemoteException {
        try {
            return userController.getUserByUsername(username);
        } catch (Exception e) {
            System.err.println("⚠️ RMI Error in getUserByUsername: " + e.getMessage());
            throw new RemoteException("Get user by username failed", e);
        }
    }
    
    @Override
    public User getUserById(Integer userId) throws RemoteException {
        try {
            return userController.getUserById(userId);
        } catch (Exception e) {
            System.err.println("⚠️ RMI Error in getUserById: " + e.getMessage());
            throw new RemoteException("Get user by ID failed", e);
        }
    }
    
    @Override
    public boolean addUser(User user) throws RemoteException {
        try {
            return userController.addUser(user);
        } catch (Exception e) {
            System.err.println("⚠️ RMI Error in addUser: " + e.getMessage());
            throw new RemoteException("Add user failed", e);
        }
    }
    
    @Override
    public boolean updateUser(int userId, String username, String email, String password) throws RemoteException {
        try {
            return userController.updateUser(userId, username, email, password);
        } catch (Exception e) {
            System.err.println("⚠️ RMI Error in updateUser: " + e.getMessage());
            throw new RemoteException("Update user failed", e);
        }
    }
    
    // Search methods
    @Override
    public ArrayList<User> searchUsers(String searchQuery) throws RemoteException {
        try {
            return userController.searchUsers(searchQuery);
        } catch (Exception e) {
            System.err.println("⚠️ RMI Error in searchUsers: " + e.getMessage());
            throw new RemoteException("Search users failed", e);
        }
    }
    
    @Override
    public ArrayList<Issue> searchIssues(String searchQuery) throws RemoteException {
        try {
            return userController.searchIssues(searchQuery);
        } catch (Exception e) {
            System.err.println("⚠️ RMI Error in searchIssues: " + e.getMessage());
            throw new RemoteException("Search issues failed", e);
        }
    }
    
    // Statistics methods
    @Override
    public int countIssuesByUserId(int userId) throws RemoteException {
        try {
            return userController.countIssuesByUserId(userId);
        } catch (Exception e) {
            System.err.println("⚠️ RMI Error in countIssuesByUserId: " + e.getMessage());
            throw new RemoteException("Count issues failed", e);
        }
    }
    
    @Override
    public int countLikesReceivedByUserId(int userId) throws RemoteException {
        try {
            return userController.countLikesReceivedByUserId(userId);
        } catch (Exception e) {
            System.err.println("⚠️ RMI Error in countLikesReceivedByUserId: " + e.getMessage());
            throw new RemoteException("Count likes failed", e);
        }
    }
    
    @Override
    public int countCommentsReceivedByUserId(int userId) throws RemoteException {
        try {
            return userController.countCommentsReceivedByUserId(userId);
        } catch (Exception e) {
            System.err.println("⚠️ RMI Error in countCommentsReceivedByUserId: " + e.getMessage());
            throw new RemoteException("Count comments received failed", e);
        }
    }
    
    @Override
    public int countCommentsMadeByUserId(int userId) throws RemoteException {
        try {
            return userController.countCommentsMadeByUserId(userId);
        } catch (Exception e) {
            System.err.println("⚠️ RMI Error in countCommentsMadeByUserId: " + e.getMessage());
            throw new RemoteException("Count comments made failed", e);
        }
    }
    
    // Admin methods
    @Override
    public boolean isMaintenanceMode() throws RemoteException {
        try {
            return userController.isMaintenanceMode();
        } catch (Exception e) {
            System.err.println("⚠️ RMI Error in isMaintenanceMode: " + e.getMessage());
            throw new RemoteException("Check maintenance mode failed", e);
        }
    }
    
    @Override
    public boolean setMaintenanceMode(boolean enabled) throws RemoteException {
        try {
            return userController.setMaintenanceMode(enabled);
        } catch (Exception e) {
            System.err.println("⚠️ RMI Error in setMaintenanceMode: " + e.getMessage());
            throw new RemoteException("Set maintenance mode failed", e);
        }
    }
    
    @Override
    public boolean setUserStatus(int userId, String status) throws RemoteException {
        try {
            return userController.setUserStatus(userId, status);
        } catch (Exception e) {
            System.err.println("⚠️ RMI Error in setUserStatus: " + e.getMessage());
            throw new RemoteException("Set user status failed", e);
        }
    }
    
    @Override
    public List<AuditLog> getRecentAuditLogs(int limit) throws RemoteException {
        try {
            return userController.getRecentAuditLogs(limit);
        } catch (Exception e) {
            System.err.println("⚠️ RMI Error in getRecentAuditLogs: " + e.getMessage());
            throw new RemoteException("Get audit logs failed", e);
        }
    }
    
    // Session management
    @Override
    public int getCurrentLoggedInUserId() throws RemoteException {
        try {
            return userController.getCurrentLoggedInUserId();
        } catch (Exception e) {
            System.err.println("⚠️ RMI Error in getCurrentLoggedInUserId: " + e.getMessage());
            throw new RemoteException("Get current user ID failed", e);
        }
    }
    
    @Override
    public void setCurrentLoggedInUserId(int userId) throws RemoteException {
        try {
            userController.setCurrentLoggedInUserId(userId);
        } catch (Exception e) {
            System.err.println("⚠️ RMI Error in setCurrentLoggedInUserId: " + e.getMessage());
            throw new RemoteException("Set current user ID failed", e);
        }
    }
    
    // Debug methods
    @Override
    public void debugSampleData() throws RemoteException {
        try {
            userController.debugSampleData();
        } catch (Exception e) {
            System.err.println("⚠️ RMI Error in debugSampleData: " + e.getMessage());
            throw new RemoteException("Debug sample data failed", e);
        }
    }
    
    @Override
    public void debugGetUserById(int userId) throws RemoteException {
        try {
            userController.debugGetUserById(userId);
        } catch (Exception e) {
            System.err.println("⚠️ RMI Error in debugGetUserById: " + e.getMessage());
            throw new RemoteException("Debug get user by ID failed", e);
        }
    }
    
    @Override
    public void testMultipleSearchTerms() throws RemoteException {
        try {
            userController.testMultipleSearchTerms();
        } catch (Exception e) {
            System.err.println("⚠️ RMI Error in testMultipleSearchTerms: " + e.getMessage());
            throw new RemoteException("Test multiple search terms failed", e);
        }
    }

    @Override
    public String sendRegistrationOTP(String email) throws RemoteException {
        try {
            return userController.sendRegistrationOTP(email);
        } catch (Exception e) {
            System.err.println("⚠️ RMI Error in sendRegistrationOTP: " + e.getMessage());
            throw new RemoteException("Registration OTP sending failed", e);
        }
    }

    @Override
    public boolean verifyRegistrationOTP(String email, String otp, User user) throws RemoteException {
        try {
            return userController.verifyRegistrationOTP(email, otp, user);
        } catch (Exception e) {
            System.err.println("⚠️ RMI Error in verifyRegistrationOTP: " + e.getMessage());
            throw new RemoteException("Registration OTP verification failed", e);
        }
    }
    
}