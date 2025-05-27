package Controller;

import model.Notice;
import model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import util.HibernateUtil;
import java.util.ArrayList;
import java.util.List;

public class NoticeController {
    private final SessionFactory sessionFactory;

    public NoticeController() {
        this.sessionFactory = HibernateUtil.getSessionFactory();
        System.out.println("✅ Notice Hibernate SessionFactory initialized!");
    }

    public boolean createNotice(Notice notice, int userId) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            User user = session.get(User.class, userId);
            if (user == null) {
                System.out.println("❌ User not found: ID=" + userId);
                return false;
            }
            // Validate notice fields
            if (notice.getContent() == null || notice.getContent().trim().isEmpty()) {
                System.out.println("❌ Invalid notice: content is null or empty");
                return false;
            }
            if (notice.getTitle() == null || notice.getTitle().trim().isEmpty()) {
                System.out.println("❌ Invalid notice: title is null or empty");
                return false;
            }
            notice.setPostedBy(user);
            System.out.println("✅ Saving notice: " + notice.toString());
            session.persist(notice);
            tx.commit();
            System.out.println("✅ Notice created: " + notice.getTitle() + " by User ID: " + userId);
            return true;
        } catch (Exception e) {
          
            System.out.println("⚠️ Error creating notice: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<Notice> getAllNotices() {
        List<Notice> notices = new ArrayList<>();
        try (Session session = sessionFactory.openSession()) {
            Query<Notice> query = session.createQuery("FROM Notice ORDER BY createdAt DESC", Notice.class);
            notices = query.getResultList();
            System.out.println("✅ Fetched " + notices.size() + " notices");
        } catch (Exception e) {
            System.out.println("⚠️ Error fetching all notices");
            e.printStackTrace();
        }
        return notices;
    }

    public Notice getNoticeById(int noticeId) {
        try (Session session = sessionFactory.openSession()) {
            Notice notice = session.get(Notice.class, noticeId);
            if (notice != null) {
                System.out.println("✅ Found notice: ID=" + noticeId);
            } else {
                System.out.println("❌ No notice found with ID: " + noticeId);
            }
            return notice;
        } catch (Exception e) {
            System.out.println("⚠️ Error fetching notice by ID");
            e.printStackTrace();
            return null;
        }
    }

    public boolean updateNotice(Notice notice) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            Notice existing = session.get(Notice.class, notice.getId());
            if (existing == null) {
                System.out.println("❌ Notice not found: ID=" + notice.getId());
                return false;
            }
            // Validate fields
            if (notice.getContent() == null || notice.getContent().trim().isEmpty()) {
                System.out.println("❌ Invalid notice: content is null or empty");
                return false;
            }
            if (notice.getTitle() == null || notice.getTitle().trim().isEmpty()) {
                System.out.println("❌ Invalid notice: title is null or empty");
                return false;
            }
            existing.setTitle(notice.getTitle());
            existing.setContent(notice.getContent());
            session.merge(existing);
            tx.commit();
            System.out.println("✅ Notice updated: ID=" + notice.getId());
            return true;
        } catch (Exception e) {
            System.out.println("⚠️ Error updating notice: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteNotice(int noticeId) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            Notice notice = session.get(Notice.class, noticeId);
            if (notice == null) {
                System.out.println("❌ Notice not found: ID=" + noticeId);
                return false;
            }
            session.remove(notice);
            tx.commit();
            System.out.println("✅ Notice deleted: ID=" + noticeId);
            return true;
        } catch (Exception e) {
            System.out.println("⚠️ Error deleting notice");
            e.printStackTrace();
            return false;
        }
    }

    public List<Notice> getNoticesByUserId(int userId) {
        List<Notice> notices = new ArrayList<>();
        try (Session session = sessionFactory.openSession()) {
            Query<Notice> query = session.createQuery(
                "FROM Notice WHERE postedBy.id = :userId ORDER BY createdAt DESC", Notice.class);
            query.setParameter("userId", userId);
            notices = query.getResultList();
            System.out.println("✅ Fetched " + notices.size() + " notices for User ID: " + userId);
        } catch (Exception e) {
            System.out.println("⚠️ Error fetching notices by user");
            e.printStackTrace();
        }
        return notices;
    }
}