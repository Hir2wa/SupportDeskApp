package View;

import Controller.UserController;
import model.User;
import org.mindrot.jbcrypt.BCrypt;

public class PasswordCheckTest {
    public static void main(String[] args) {
        UserController controller = new UserController();
        String username = "Alain12";
        String testPassword = "alainfa12"; // Replace with the password you tried

        User user = controller.getUserByUsername(username);
        if (user == null) {
            System.out.println("❌ User not found: " + username);
            return;
        }
        System.out.println("✅ Found user: " + user.getUsername() + ", ID: " + user.getId());
        boolean passwordMatches = BCrypt.checkpw(testPassword, user.getPassword());
        if (passwordMatches) {
            System.out.println("✅ Password is correct for " + username);
        } else {
            System.out.println("❌ Password is incorrect for " + username);
        }
    }
}