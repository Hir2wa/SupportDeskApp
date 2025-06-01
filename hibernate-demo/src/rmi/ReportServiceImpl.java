// File: ReportServiceImpl.java
package rmi;

import Controller.ReportController;
import Controller.UserController;
import model.Report;
import model.User;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

/**
 * RMI Implementation of ReportService
 */
public class ReportServiceImpl extends UnicastRemoteObject implements ReportService {
    
    private final ReportController reportController;
    private final UserController userController;
    
    public ReportServiceImpl() throws RemoteException {
        super();
        this.reportController = new ReportController();
        this.userController = new UserController();
        System.out.println("✅ ReportServiceImpl initialized for RMI");
    }
    
    @Override
    public boolean createReport(Report report, int userId) throws RemoteException {
        try {
            User user = userController.getUserById(userId);
            if (user == null) {
                throw new RemoteException("User not found");
            }
            return reportController.createReport(report);
        } catch (Exception e) {
            System.err.println("⚠️ RMI Error in createReport: " + e.getMessage());
            throw new RemoteException("Create report failed", e);
        }
    }
    
   
    @Override
    public boolean deleteReport(int reportId, int adminId) throws RemoteException {
        try {
            User admin = userController.getUserById(adminId);
            if (admin == null || !admin.isAdmin()) {
                throw new RemoteException("Only admins can delete reports");
            }
            return reportController.deleteReport(reportId);
        } catch (Exception e) {
            System.err.println("⚠️ RMI Error in deleteReport: " + e.getMessage());
            throw new RemoteException("Delete report failed", e);
        }
    }

    
}
