package practiceandroidapplication.android.com.meetmeup.Entity;

/**
 * Created by sibimark on 28/01/2016.
 */
public class Comments {

    private int id;

    private int postId;

    private char postType;

    private int userId;

    private String userName;

    private String comment;

    private String commentDate;

    public Comments() {

    }

    public Comments(int postId, char postType, int userId, String comment) {
        this.postId = postId;
        this.postType = postType;
        this.userId = userId;
        this.comment = comment;
    }

    public Comments(int id, int postId, char postType, int userId, String comment, String commentDate, String userName) {
        this.id = id;
        this.postId = postId;
        this.postType = postType;
        this.userId = userId;
        this.comment = comment;
        this.commentDate = commentDate;
        this.userName = userName;
    }

    public Comments(int id, int postId, char postType, int userId, String comment, String commentDate) {
        this.id = id;
        this.postId = postId;
        this.postType = postType;
        this.userId = userId;
        this.comment = comment;
        this.commentDate = commentDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public char getPostType() {
        return postType;
    }

    public void setPostType(char postType) {
        this.postType = postType;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCommentDate() {
        return commentDate;
    }

    public void setCommentDate(String commentDate) {
        this.commentDate = commentDate;
    }
}
