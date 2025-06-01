// File: IssueServiceImpl.java
package rmi;

import Controller.IssueController;
import Controller.UserController;
import model.Issue;
import model.Comment;
import model.Like;
import model.User;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

/**
 * RMI Implementation of IssueService
 * Wraps IssueController to work with RMI
 */
public class IssueServiceImpl extends UnicastRemoteObject implements IssueService {
    
    private final IssueController issueController;
    private final UserController userController; // For admin checks
    
    public IssueServiceImpl() throws RemoteException {
        super();
        this.issueController = new IssueController();
        this.userController = new UserController();
        System.out.println("✅ IssueServiceImpl initialized for RMI");
    }
    
    @Override
    public boolean createIssue(Issue issue, int userUserId) throws RemoteException {
        try {
            return issueController.postIssue(issue, userUserId);
        } catch (Exception e) {
            System.err.println("⚠️ RMI Error in createIssue: " + e.getMessage());
            throw new RemoteException("Create issue failed", e);
        }
    }
    
    @Override
    public boolean updateIssue(int issueId, Issue issue, int userId) throws RemoteException {
        try {
            User currentUser = userController.getUserById(userId);
            Issue existingIssue = issueController.getIssueById(issueId);
            if (currentUser == null || existingIssue == null) {
                throw new RemoteException("User or issue not found");
            }
            // Allow update if user is the issue owner or an admin
            if (existingIssue.getUserId() != userId && !currentUser.isAdmin()) {
                throw new RemoteException("Only the issue owner or admin can update this issue");
            }
            issue.setId(issueId); // Ensure the correct ID is set
            return issueController.postIssue(issue, userId); // Reuse postIssue for update
        } catch (Exception e) {
            System.err.println("⚠️ RMI Error in updateIssue: " + e.getMessage());
            throw new RemoteException("Update issue failed", e);
        }
    }
    
    @Override
    public boolean deleteIssue(int issueId, int userId) throws RemoteException {
        try {
            User currentUser = userController.getUserById(userId);
            Issue existingIssue = issueController.getIssueById(issueId);
            if (currentUser == null || existingIssue == null) {
                throw new RemoteException("User or issue not found");
            }
            // Allow deletion if user is the issue owner or an admin
            if (existingIssue.getUserId() != userId && !currentUser.isAdmin()) {
                throw new RemoteException("Only the issue owner or admin can delete this issue");
            }
            return issueController.deleteIssue(issueId);
        } catch (Exception e) {
            System.err.println("⚠️ RMI Error in deleteIssue: " + e.getMessage());
            throw new RemoteException("Delete issue failed", e);
        }
    }
    
    @Override
    public Issue getIssueById(int issueId) throws RemoteException {
        try {
            return issueController.getIssueById(issueId);
        } catch (Exception e) {
            System.err.println("⚠️ RMI Error in getIssueById: " + e.getMessage());
            throw new RemoteException("Get issue by ID failed", e);
        }
    }
    
    @Override
    public List<Issue> getIssuesByUserId(int userId) throws RemoteException {
        try {
            List<Issue> allIssues = issueController.getAllIssues();
            List<Issue> userIssues = new ArrayList<>();
            for (Issue issue : allIssues) {
                if (issue.getUserId() != null && issue.getUserId() == userId) {
                    userIssues.add(issue);
                }
            }
            return userIssues;
        } catch (Exception e) {
            System.err.println("⚠️ RMI Error in getIssuesByUserId: " + e.getMessage());
            throw new RemoteException("Get issues by user ID failed", e);
        }
    }
    
    @Override
    public List<Issue> getAllIssues() throws RemoteException {
        try {
            return issueController.getAllIssues();
        } catch (Exception e) {
            System.err.println("⚠️ RMI Error in getAllIssues: " + e.getMessage());
            throw new RemoteException("Get all issues failed", e);
        }
    }
    
    @Override
    public boolean addComment(Comment comment, int userId) throws RemoteException {
        try {
            return issueController.addComment(comment, userId);
        } catch (Exception e) {
            System.err.println("⚠️ RMI Error in addComment: " + e.getMessage());
            throw new RemoteException("Add comment failed", e);
        }
    }
    
    @Override
    public boolean deleteComment(int commentId, int userId) throws RemoteException {
        try {
            User currentUser = userController.getUserById(userId);
            Comment comment = issueController.getCommentsForIssue(commentId).stream()
                    .filter(c -> c.getId() == commentId)
                    .findFirst()
                    .orElse(null);
            if (currentUser == null || comment == null) {
                throw new RemoteException("User or comment not found");
            }
            // Allow deletion if user is the comment owner or an admin
            if (comment.getUser().getId() != userId && !currentUser.isAdmin()) {
                throw new RemoteException("Only the comment owner or admin can delete this comment");
            }
            return issueController.deleteComment(commentId);
        } catch (Exception e) {
            System.err.println("⚠️ RMI Error in deleteComment: " + e.getMessage());
            throw new RemoteException("Delete comment failed", e);
        }
    }
    
    @Override
    public List<Comment> getCommentsForIssue(int issueId) throws RemoteException {
        try {
            return issueController.getCommentsForIssue(issueId);
        } catch (Exception e) {
            System.err.println("⚠️ RMI Error in getCommentsForIssue: " + e.getMessage());
            throw new RemoteException("Get comments failed", e);
        }
    }
    
    @Override
    public boolean likeIssue(int userId, int issueId, Like.ReactionType reactionType) throws RemoteException {
        try {
            return issueController.likeIssue(userId, issueId, reactionType);
        } catch (Exception e) {
            System.err.println("⚠️ RMI Error in likeIssue: " + e.getMessage());
            throw new RemoteException("Like issue failed", e);
        }
    }
    
    @Override
    public boolean hasUserLikedIssue(int userId, int issueId) throws RemoteException {
        try {
            return issueController.hasUserLikedIssue(userId, issueId);
        } catch (Exception e) {
            System.err.println("⚠️ RMI Error in hasUserLikedIssue: " + e.getMessage());
            throw new RemoteException("Check like status failed", e);
        }
    }
    
    @Override
    public boolean hasUserDislikedIssue(int userId, int issueId) throws RemoteException {
        try {
            return issueController.hasUserDislikedIssue(userId, issueId);
        } catch (Exception e) {
            System.err.println("⚠️ RMI Error in hasUserDislikedIssue: " + e.getMessage());
            throw new RemoteException("Check dislike status failed", e);
        }
    }
    
    @Override
    public int getLikeCount(int issueId) throws RemoteException {
        try {
            return issueController.getLikeCount(issueId);
        } catch (Exception e) {
            System.err.println("⚠️ RMI Error in getLikeCount: " + e.getMessage());
            throw new RemoteException("Get like count failed", e);
        }
    }
    
    @Override
    public int getDislikeCount(int issueId) throws RemoteException {
        try {
            return issueController.getDislikeCount(issueId);
        } catch (Exception e) {
            System.err.println("⚠️ RMI Error in getDislikeCount: " + e.getMessage());
            throw new RemoteException("Get dislike count failed", e);
        }
    }
    
    @Override
    public ArrayList<Issue> searchIssues(String searchQuery) throws RemoteException {
        try {
            return issueController.searchIssues(searchQuery);
        } catch (Exception e) {
            System.err.println("⚠️ RMI Error in searchIssues: " + e.getMessage());
            throw new RemoteException("Search issues failed", e);
        }
    }
    
    @Override
    public int countIssuesByUserId(int userId) throws RemoteException {
        try {
            return issueController.countIssuesByUserId(userId);
        } catch (Exception e) {
            System.err.println("⚠️ RMI Error in countIssuesByUserId: " + e.getMessage());
            throw new RemoteException("Count issues failed", e);
        }
    }
    
    @Override
    public int countLikesReceivedByUserId(int userId) throws RemoteException {
        try {
            return issueController.countLikesReceivedByUserId(userId);
        } catch (Exception e) {
            System.err.println("⚠️ RMI Error in countLikesReceivedByUserId: " + e.getMessage());
            throw new RemoteException("Count likes received failed", e);
        }
    }
    
    @Override
    public int countCommentsReceivedByUserId(int userId) throws RemoteException {
        try {
            return issueController.countCommentsReceivedByUserId(userId);
        } catch (Exception e) {
            System.err.println("⚠️ RMI Error in countCommentsReceivedByUserId: " + e.getMessage());
            throw new RemoteException("Count comments received failed", e);
        }
    }
    
    @Override
    public int countCommentsMadeByUserId(int userId) throws RemoteException {
        try {
            return issueController.countCommentsMadeByUserId(userId);
        } catch (Exception e) {
            System.err.println("⚠️ RMI Error in countCommentsMadeByUserId: " + e.getMessage());
            throw new RemoteException("Count comments made failed", e);
        }
    }
    
    @Override
    public long getIssueCount() throws RemoteException {
        try {
            return issueController.getIssueCount();
        } catch (Exception e) {
            System.err.println("⚠️ RMI Error in getIssueCount: " + e.getMessage());
            throw new RemoteException("Get issue count failed", e);
        }
    }
    
    @Override
    public long getOpenIssueCount() throws RemoteException {
        try {
            return issueController.getOpenIssueCount();
        } catch (Exception e) {
            System.err.println("⚠️ RMI Error in getOpenIssueCount: " + e.getMessage());
            throw new RemoteException("Get open issue count failed", e);
        }
    }
    
    @Override
    public long getCommentCount() throws RemoteException {
        try {
            return issueController.getCommentCount();
        } catch (Exception e) {
            System.err.println("⚠️ RMI Error in getCommentCount: " + e.getMessage());
            throw new RemoteException("Get comment count failed", e);
        }
    }
}