# SupportDeskHibernateVision

// Authentication methods
public User loginAndGetUser(String username, String password) {
try {
reconnectIfNeeded();
if (userService != null) {
return userService.loginAndGetUser(username, password);
}
} catch (RemoteException e) {
System.err.println("❌ RMI Error in loginAndGetUser: " + e.getMessage());
userService = null; // Force reconnection next time
}
return null;
}

    public boolean loginUser(String username, String password) {
        try {
            reconnectIfNeeded();
            if (userService != null) {
                return userService.loginUser(username, password);
            }
        } catch (RemoteException e) {
            System.err.println("❌ RMI Error in loginUser: " + e.getMessage());
            userService = null;
        }
        return false;
    }

    public boolean registerUser(User user) {
        try {
            reconnectIfNeeded();
            if (userService != null) {
                return userService.registerUser(user);
            }
        } catch (RemoteException e) {
            System.err.println("❌ RMI Error in registerUser: " + e.getMessage());
            userService = null;
        }
        return false;
    }

    public void logout() {
        try {
            reconnectIfNeeded();
            if (userService != null) {
                userService.logout();
            }
        } catch (RemoteException e) {
            System.err.println("❌ RMI Error in logout: " + e.getMessage());
            userService = null;
        }
    }

    // User management methods
    public User getUserByUsername(String username) {
        try {
            reconnectIfNeeded();
            if (userService != null) {
                return userService.getUserByUsername(username);
            }
        } catch (RemoteException e) {
            System.err.println("❌ RMI Error in getUserByUsername: " + e.getMessage());
            userService = null;
        }
        return null;
    }

    public User getUserById(Integer userId) {
        try {
            reconnectIfNeeded();
            if (userService != null) {
                return userService.getUserById(userId);
            }
        } catch (RemoteException e) {
            System.err.println("❌ RMI Error in getUserById: " + e.getMessage());
            userService = null;
        }
        return null;
    }

    public boolean updateUser(int userId, String username, String email, String password) {
        try {
            reconnectIfNeeded();
            if (userService != null) {
                return userService.updateUser(userId, username, email, password);
            }
        } catch (RemoteException e) {
            System.err.println("❌ RMI Error in updateUser: " + e.getMessage());
            userService = null;
        }
        return false;
    }

    // Search methods
    public ArrayList<User> searchUsers(String searchQuery) {
        try {
            reconnectIfNeeded();
            if (userService != null) {
                return userService.searchUsers(searchQuery);
            }
        } catch (RemoteException e) {
            System.err.println("❌ RMI Error in searchUsers: " + e.getMessage());
            userService = null;
        }
        return new ArrayList<>();
    }

    // Password reset methods
    public String sendPasswordResetOTP(String email) {
        try {
            reconnectIfNeeded();
            if (userService != null) {
                return userService.sendPasswordResetOTP(email);
            }
        } catch (RemoteException e) {
            System.err.println("❌ RMI Error in sendPasswordResetOTP: " + e.getMessage());
            userService = null;
        }
        return null;
    }

    public boolean resetPassword(String email, String otp, String newPassword, String storedOtp) {
        try {
            reconnectIfNeeded();
            if (userService != null) {
                return userService.resetPassword(email, otp, newPassword, storedOtp);
            }
        } catch (RemoteException e) {
            System.err.println("❌ RMI Error in resetPassword: " + e.getMessage());
            userService = null;
        }
        return false;
    }

    // Statistics methods
    public int countIssuesByUserId(int userId) {
        try {
            reconnectIfNeeded();
            if (userService != null) {
                return userService.countIssuesByUserId(userId);
            }
        } catch (RemoteException e) {
            System.err.println("❌ RMI Error in countIssuesByUserId: " + e.getMessage());
            userService = null;
        }
        return 0;
    }

    public int getCurrentLoggedInUserId() {
        try {
            reconnectIfNeeded();
            if (userService != null) {
                return userService.getCurrentLoggedInUserId();
            }
        } catch (RemoteException e) {
            System.err.println("❌ RMI Error in getCurrentLoggedInUserId: " + e.getMessage());
            userService = null;
        }
        return -1;
    }

    // Admin methods
    public boolean isMaintenanceMode() {
        try {
            reconnectIfNeeded();
            if (userService != null) {
                return userService.isMaintenanceMode();
            }
        } catch (RemoteException e) {
            System.err.println("❌ RMI Error in isMaintenanceMode: " + e.getMessage());
            userService = null;
        }
        return false;
    }

    public boolean setUserStatus(int userId, String status) {
        try {
            reconnectIfNeeded();
            if (userService != null) {
                return userService.setUserStatus(userId, status);
            }
        } catch (RemoteException e) {
            System.err.println("❌ RMI Error in setUserStatus: " + e.getMessage());
            userService = null;
        }
        return false;
    }

    /**
     * Test the connection to the server
     */
    public void testConnection() {
        try {
            reconnectIfNeeded();
            if (userService != null) {
                // Try a simple operation to test the connection
                userService.getCurrentLoggedInUserId();
                System.out.println("✅ RMI Connection test successful!");
            } else {
                System.err.println("❌ RMI Connection test failed - no connection");
            }
        } catch (RemoteException e) {
            System.err.println("❌ RMI Connection test failed: " + e.getMessage());
            userService = null;
        }
    }
