// File: ReportService.java
package rmi;

import model.Report;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Remote interface for Report operations
 */
public interface ReportService extends Remote {
    boolean createReport(Report report, int userId) throws RemoteException;  
    boolean deleteReport(int reportId, int adminId) throws RemoteException;
  
}