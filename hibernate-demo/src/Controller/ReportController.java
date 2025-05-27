package Controller;

import model.Report;
import model.User;
import model.AuditLog;
import model.Comment;
import model.Issue;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import util.HibernateUtil;
import util.PdfGenerator;

import java.util.ArrayList;
import java.util.List;

public class ReportController {
    private final SessionFactory sessionFactory;

    public ReportController() {
        this.sessionFactory = HibernateUtil.getSessionFactory();
        System.out.println("✅ Report Hibernate SessionFactory initialized!");
    }

    public boolean createReport(Report report) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            if (report.getReportedBy() == null) {
                System.out.println("❌ ReportedBy user is null");
                return false;
            }
            // Ensure entities are managed
            User reportedBy = session.get(User.class, report.getReportedBy().getId());
            if (reportedBy == null) {
                System.out.println("❌ User not found: ID=" + report.getReportedBy().getId());
                return false;
            }
            report.setReportedBy(reportedBy);
            if (report.getComment() != null) {
                Comment comment = session.get(Comment.class, report.getComment().getId());
                if (comment == null) {
                    System.out.println("❌ Comment not found: ID=" + report.getComment().getId());
                    return false;
                }
                report.setComment(comment);
            }
            if (report.getIssue() != null) {
                Issue issue = session.get(Issue.class, report.getIssue().getId());
                if (issue == null) {
                    System.out.println("❌ Issue not found: ID=" + report.getIssue().getId());
                    return false;
                }
                report.setIssue(issue);
            }
            session.persist(report);
            tx.commit();
            System.out.println("✅ Report created: ID=" + report.getId());
            return true;
        } catch (Exception e) {
            System.out.println("⚠️ Error creating report: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean reportIssue(int reportedBy, int issueId, String reason) {
        try (Session session = sessionFactory.openSession()) {
            User user = session.get(User.class, reportedBy);
            Issue issue = session.get(Issue.class, issueId);
            if (user == null || issue == null) {
                System.out.println("❌ User or Issue not found: UserID=" + reportedBy + ", IssueID=" + issueId);
                return false;
            }
            Report report = new Report(user, null, issue, reason);
            return createReport(report);
        } catch (Exception e) {
            System.out.println("⚠️ Error reporting issue: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean reportComment(int userId, Integer commentId, String reason) {
        if (commentId == null) {
            System.out.println("❌ Comment ID cannot be null");
            throw new IllegalArgumentException("commentId cannot be null");
        }
        try (Session session = sessionFactory.openSession()) {
            User user = session.get(User.class, userId);
            Comment comment = session.get(Comment.class, commentId);
            if (user == null || comment == null) {
                System.out.println("❌ User or Comment not found: UserID=" + userId + ", CommentID=" + commentId);
                return false;
            }
            Report report = new Report(user, comment, null, reason);
            return createReport(report);
        } catch (Exception e) {
            System.out.println("⚠️ Error reporting comment: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<Report> getAllReports() {
        List<Report> reports = new ArrayList<>();
        try (Session session = sessionFactory.openSession()) {
            Query<Report> query = session.createQuery("FROM Report ORDER BY createdAt DESC", Report.class);
            reports = query.getResultList();
            System.out.println("✅ Fetched " + reports.size() + " reports");
        } catch (Exception e) {
            System.out.println("⚠️ Error fetching all reports");
            e.printStackTrace();
        }
        return reports;
    }

    public List<Report> fetchReportsBy(Integer issueId, Integer commentId, Integer reportedBy) {
        List<Report> reports = new ArrayList<>();
        try (Session session = sessionFactory.openSession()) {
            StringBuilder hql = new StringBuilder("FROM Report WHERE 1=1");
            List<String> params = new ArrayList<>();
            List<Object> values = new ArrayList<>();
            if (issueId != null) {
                hql.append(" AND issue.id = :issueId");
                params.add("issueId");
                values.add(issueId);
            }
            if (commentId != null) {
                hql.append(" AND comment.id = :commentId");
                params.add("commentId");
                values.add(commentId);
            }
            if (reportedBy != null) {
                hql.append(" AND reportedBy.id = :reportedBy");
                params.add("reportedBy");
                values.add(reportedBy);
            }
            hql.append(" ORDER BY createdAt DESC");
            Query<Report> query = session.createQuery(hql.toString(), Report.class);
            for (int i = 0; i < params.size(); i++) {
                query.setParameter(params.get(i), values.get(i));
            }
            reports = query.getResultList();
            System.out.println("✅ Fetched " + reports.size() + " reports for criteria");
        } catch (Exception e) {
            System.out.println("⚠️ Error fetching reports by criteria");
            e.printStackTrace();
        }
        return reports;
    }

    public boolean deleteReport(int reportId) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            Report report = session.get(Report.class, reportId);
            if (report == null) {
                System.out.println("❌ Report not found: ID=" + reportId);
                return false;
            }
            session.remove(report);
            tx.commit();
            System.out.println("✅ Report deleted: ID=" + reportId);
            return true;
        }
        catch (Exception e) {
            System.out.println("⚠️ Error deleting report");
            e.printStackTrace();
            return false;
        }
    }

    public boolean hasUserReportedIssue(int userId, int issueId) {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery(
                "SELECT COUNT(*) FROM Report WHERE reportedBy.id = :userId AND issue.id = :issueId", Long.class);
            query.setParameter("userId", userId);
            query.setParameter("issueId", issueId);
            boolean result = query.uniqueResult() > 0;
            System.out.println("✅ User " + (result ? "has" : "has not") + " reported Issue ID: " + issueId);
            return result;
        } catch (Exception e) {
            System.out.println("⚠️ Error checking if user reported issue");
            e.printStackTrace();
            return false;
        }
    }

    public boolean hasUserReportedComment(int userId, int commentId) {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery(
                "SELECT COUNT(*) FROM Report WHERE reportedBy.id = :userId AND comment.id = :commentId", Long.class);
            query.setParameter("userId", userId);
            query.setParameter("commentId", commentId);
            boolean result = query.uniqueResult() > 0;
            System.out.println("✅ User " + (result ? "has" : "has not") + " reported Comment ID: " + commentId);
            return result;
        } catch (Exception e) {
            System.out.println("⚠️ Error checking if user reported comment");
            e.printStackTrace();
            return false;
        }
    }

    public boolean generateReportPdf(String dest, Integer issueId, Integer commentId, Integer reportedBy) {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            Transaction tx = session.beginTransaction();
    
            // Fetch reports based on the provided filters using the existing fetchReportsBy method
            List<Report> reports = fetchReportsBy(issueId, commentId, reportedBy);
            if (reports.isEmpty()) {
                System.out.println("⚠️ No reports found for the given criteria: issueId=" + issueId + ", commentId=" + commentId + ", reportedBy=" + reportedBy);
                tx.commit();
                return false;
            }
    
            // Generate the PDF using PdfGenerator (user selects save location via JFileChooser)
            PdfGenerator pdfGenerator = new PdfGenerator();
            pdfGenerator.generateReportPdf(reports); // No dest parameter needed
    
            // Audit logging
            AuditLog auditLog = new AuditLog();
            auditLog.setAdmin(session.get(User.class, 1L)); // Replace 1 with actual admin ID
            auditLog.setAction("Generated PDF report with filters: issueId=" + issueId + ", commentId=" + commentId + ", reportedBy=" + reportedBy);
            auditLog.setTargetType("Report");
            session.persist(auditLog);
    
            tx.commit();
            System.out.println("✅ PDF generated and audit logged");
            return true;
        } catch (Exception e) {
            if (session != null && session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
            System.err.println("⚠️ Error generating PDF: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
  

    
}