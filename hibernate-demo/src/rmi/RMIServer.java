// File: RMIServer.java
package rmi;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * RMI Server to host UserService, IssueService, NotificationService, ReportService, SystemSettingsService, and NoticeService
 */
public class RMIServer {
    
    public static final String SERVER_HOST = "localhost";
    public static final int SERVER_PORT = 1099;
    public static final String USER_SERVICE_NAME = "UserService";
    public static final String ISSUE_SERVICE_NAME = "IssueService";
    public static final String NOTIFICATION_SERVICE_NAME = "NotificationService";
    public static final String REPORT_SERVICE_NAME = "ReportService";
    public static final String SETTINGS_SERVICE_NAME = "SystemSettingsService";
    public static final String NOTICE_SERVICE_NAME = "NoticeService";
    
    public static void main(String[] args) {
        try {
            System.out.println("🚀 Starting RMI Server...");
            
            // Create and start RMI registry
            Registry registry = LocateRegistry.createRegistry(SERVER_PORT);
            System.out.println("✅ RMI Registry created on port " + SERVER_PORT);
            
            // Create and bind UserService
            UserServiceImpl userService = new UserServiceImpl();
            String userServiceUrl = "rmi://" + SERVER_HOST + ":" + SERVER_PORT + "/" + USER_SERVICE_NAME;
            Naming.rebind(userServiceUrl, userService);
            System.out.println("✅ UserService bound to: " + userServiceUrl);
            
            // Create and bind IssueService
            IssueServiceImpl issueService = new IssueServiceImpl();
            String issueServiceUrl = "rmi://" + SERVER_HOST + ":" + SERVER_PORT + "/" + ISSUE_SERVICE_NAME;
            Naming.rebind(issueServiceUrl, issueService);
            System.out.println("✅ IssueService bound to: " + issueServiceUrl);
            
            
            // Create and bind ReportService
            ReportServiceImpl reportService = new ReportServiceImpl();
            String reportServiceUrl = "rmi://" + SERVER_HOST + ":" + SERVER_PORT + "/" + REPORT_SERVICE_NAME;
            Naming.rebind(reportServiceUrl, reportService);
            System.out.println("✅ ReportService bound to: " + reportServiceUrl);
            
         
            
            // Create and bind NoticeService
            NoticeServiceImpl noticeService = new NoticeServiceImpl();
            String noticeServiceUrl = "rmi://" + SERVER_HOST + ":" + SERVER_PORT + "/" + NOTICE_SERVICE_NAME;
            Naming.rebind(noticeServiceUrl, noticeService);
            System.out.println("✅ NoticeService bound to: " + noticeServiceUrl);
            
            System.out.println("🎉 RMI Server is running and ready to accept connections!");
            System.out.println("📡 Server Details:");
            System.out.println("   - Host: " + SERVER_HOST);
            System.out.println("   - Port: " + SERVER_PORT);
            System.out.println("   - Services: " + USER_SERVICE_NAME + ", " + ISSUE_SERVICE_NAME + ", " +
                    NOTIFICATION_SERVICE_NAME + ", " + REPORT_SERVICE_NAME + ", " +
                    SETTINGS_SERVICE_NAME + ", " + NOTICE_SERVICE_NAME);
            System.out.println("\n⏳ Press Ctrl+C to stop the server");
            
            // Keep the server running
            Thread.currentThread().join();
            
        } catch (Exception e) {
            System.err.println("❌ RMI Server failed to start: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    public static void shutdown() {
        try {
            String userServiceUrl = "rmi://" + SERVER_HOST + ":" + SERVER_PORT + "/" + USER_SERVICE_NAME;
            String issueServiceUrl = "rmi://" + SERVER_HOST + ":" + SERVER_PORT + "/" + ISSUE_SERVICE_NAME;
           
            String reportServiceUrl = "rmi://" + SERVER_HOST + ":" + SERVER_PORT + "/" + REPORT_SERVICE_NAME;
           
            String noticeServiceUrl = "rmi://" + SERVER_HOST + ":" + SERVER_PORT + "/" + NOTICE_SERVICE_NAME;
            Naming.unbind(userServiceUrl);
            Naming.unbind(issueServiceUrl);
          
            Naming.unbind(reportServiceUrl);
    
            Naming.unbind(noticeServiceUrl);
            System.out.println("✅ Services unbound successfully");
            System.out.println("🛑 RMI Server shutdown complete");
        } catch (Exception e) {
            System.err.println("⚠️ Error during server shutdown: " + e.getMessage());
        }
    }
}