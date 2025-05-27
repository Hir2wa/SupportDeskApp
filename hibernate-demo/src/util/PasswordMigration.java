package util;

import model.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mindrot.jbcrypt.BCrypt;
import util.HibernateUtil;
import java.util.List;

public class PasswordMigration {
    public static void main(String[] args) {
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();
            List<User> users = session.createQuery("FROM User", User.class).list();
            for (User user : users) {
                String password = user.getPassword();
                // Check if password is not already a BCrypt hash
                if (password != null && !password.startsWith("$2a$") && !password.startsWith("$2b$") && !password.startsWith("$2y$")) {
                    user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
                    session.update(user);
                    System.out.println("✅ Hashed password for user: " + user.getUsername());
                } else {
                    System.out.println("ℹ️ Password already hashed or null for user: " + user.getUsername());
                }
            }
            tx.commit();
            System.out.println("✅ Password migration completed");
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            System.err.println("⚠️ Migration failed: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
}