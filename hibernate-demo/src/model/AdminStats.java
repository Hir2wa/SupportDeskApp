package model;

public class AdminStats {
    private long totalUsers;
    private long totalIssues;
    private long openIssues;
    private long totalComments;
    private long activeReports;
    private long systemNotices;

    public AdminStats() {}

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getTotalIssues() {
        return totalIssues;
    }

    public void setTotalIssues(long totalIssues) {
        this.totalIssues = totalIssues;
    }

    public long getOpenIssues() {
        return openIssues;
    }

    public void setOpenIssues(long openIssues) {
        this.openIssues = openIssues;
    }

    public long getTotalComments() {
        return totalComments;
    }

    public void setTotalComments(long totalComments) {
        this.totalComments = totalComments;
    }

    public long getActiveReports() {
        return activeReports;
    }

    public void setActiveReports(long activeReports) {
        this.activeReports = activeReports;
    }

    public long getSystemNotices() {
        return systemNotices;
    }

    public void setSystemNotices(long systemNotices) {
        this.systemNotices = systemNotices;
    }

    public char[] getRecentActivity() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getRecentActivity'");
    }

    public void setRecentActivity(int intValue) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setRecentActivity'");
    }
}