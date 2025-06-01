package View;

import Controller.UserController;
import model.User;
import java.util.logging.Logger;

public class PasswordResetLoginTest {
    private static final Logger LOGGER = Logger.getLogger(PasswordResetLoginTest.class.getName());
    private static UserController userController;

    public static void main(String[] args) {
        userController = new UserController();
        
        // Test configuration
        String testUsername = "testuser"; // Change to "admin" for user_id=1
        String testEmail = "testuser@example.com"; // Change to admin's email for user_id=1
        String originalPassword = "password123";
        String newPassword = "newPassword456";

        // Step 1: Verify user exists
        LOGGER.info("Step 1: Verifying user exists: " + testUsername);
        User user = userController.getUserByUsername(testUsername);
        if (user == null) {
            LOGGER.severe("User not found: " + testUsername);
            System.out.println("❌ Test failed: User not found. Register the user first.");
            return;
        }
        System.out.println("✅ User found: " + user.getUsername() + ", ID: " + user.getId() + ", Email: " + user.getEmail());

        // Step 2: Send OTP
        LOGGER.info("Step 2: Sending OTP to: " + testEmail);
        String otp = userController.sendPasswordResetOTP(testEmail);
        if (otp == null) {
            LOGGER.severe("Failed to send OTP to: " + testEmail);
            System.out.println("❌ Test failed: OTP sending failed. Check email or UserController.");
            return;
        }
        System.out.println("✅ OTP sent: " + otp + " (In production, this would be emailed)");

        // Step 3: Reset password
        LOGGER.info("Step 3: Resetting password for: " + testEmail);
        boolean resetSuccess = userController.resetPassword(testEmail, otp, newPassword);
        if (!resetSuccess) {
            LOGGER.severe("Password reset failed for: " + testEmail);
            System.out.println("❌ Test failed: Password reset failed. Check OTP, email, or UserController.");
            return;
        }
        System.out.println("✅ Password reset successful for: " + testEmail);

        // Step 4: Test login with original password (should fail)
        LOGGER.info("Step 4: Testing login with original password: " + originalPassword);
        User loginUser = userController.loginAndGetUser(testUsername, originalPassword);
        if (loginUser != null) {
            LOGGER.warning("Login with original password succeeded unexpectedly: " + testUsername);
            System.out.println("❌ Test issue: Login with original password should fail after reset.");
        } else {
            System.out.println("✅ Login with original password failed (expected).");
        }

        // Step 5: Test login with new password
        LOGGER.info("Step 5: Testing login with new password: " + newPassword);
        loginUser = userController.loginAndGetUser(testUsername, newPassword);
        if (loginUser != null) {
            System.out.println("✅ Login successful! User: " + loginUser.getUsername() + ", ID: " + loginUser.getId());
        } else {
            LOGGER.severe("Login failed with new password for: " + testUsername);
            System.out.println("❌ Test failed: Login with new password failed. Check UserController or database.");
        }
    }
}
