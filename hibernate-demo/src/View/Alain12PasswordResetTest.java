package View;

import Controller.UserController;
import model.User;

public class Alain12PasswordResetTest {
    public static void main(String[] args) {
        UserController controller = new UserController();
        String email = "alainfabricehirwa@gmail.com";
        String newPassword = "alainfa12"; 

        
        System.out.println("Sending OTP to: " + email);
        String otp = controller.sendPasswordResetOTP(email);
        if (otp == null) {
            System.out.println("❌ Failed to send OTP to: " + email);
            return;
        }
        System.out.println("✅ OTP sent: " + otp);

        
        System.out.println("Resetting password for: " + email);
        boolean resetSuccess = controller.resetPassword(email, otp, newPassword);
        if (!resetSuccess) {
            System.out.println("❌ Password reset failed for: " + email);
            return;
        }
        System.out.println("✅ Password reset successful for: " + email);

        // Step 3: Test Login
        System.out.println("Testing login for Alain12 with new password");
        User user = controller.loginAndGetUser("Alain12", newPassword);
        if (user != null) {
            System.out.println("✅ Login successful! User: " + user.getUsername() + ", ID: " + user.getId());
        } else {
            System.out.println("❌ Login failed for Alain12");
        }
    }
}