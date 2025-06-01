// File: IssueService.java
package rmi;

import model.Issue;
import model.Comment;
import model.Like;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

/**
 * Remote interface for Issue operations
 * Defines methods that can be called remotely for managing issues
 */
public interface IssueService extends Remote {
    
    // Issue management methods
    boolean createIssue(Issue issue, int userId) throws RemoteException;
    boolean updateIssue(int issueId, Issue issue, int userId) throws RemoteException;
    boolean deleteIssue(int issueId, int userId) throws RemoteException;
    Issue getIssueById(int issueId) throws RemoteException;
    List<Issue> getIssuesByUserId(int userId) throws RemoteException;
    List<Issue> getAllIssues() throws RemoteException;
    
    // Comment management methods
    boolean addComment(Comment comment, int userId) throws RemoteException;
    boolean deleteComment(int commentId, int userId) throws RemoteException;
    List<Comment> getCommentsForIssue(int issueId) throws RemoteException;
    
    // Like/dislike methods
    boolean likeIssue(int userId, int issueId, Like.ReactionType reactionType) throws RemoteException;
    boolean hasUserLikedIssue(int userId, int issueId) throws RemoteException;
    boolean hasUserDislikedIssue(int userId, int issueId) throws RemoteException;
    int getLikeCount(int issueId) throws RemoteException;
    int getDislikeCount(int issueId) throws RemoteException;
    
    // Search method
    ArrayList<Issue> searchIssues(String searchQuery) throws RemoteException;
    
    // Statistics methods
    int countIssuesByUserId(int userId) throws RemoteException;
    int countLikesReceivedByUserId(int userId) throws RemoteException;
    int countCommentsReceivedByUserId(int userId) throws RemoteException;
    int countCommentsMadeByUserId(int userId) throws RemoteException;
    
    // Aggregate statistics
    long getIssueCount() throws RemoteException;
    long getOpenIssueCount() throws RemoteException;
    long getCommentCount() throws RemoteException;
}