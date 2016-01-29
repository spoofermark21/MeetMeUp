package practiceandroidapplication.android.com.meetmeup.Entity;

/**
 * Created by sibimark on 27/01/2016.
 */
public class Notification {

    private int id;

    private int userId;

    private int fromId;

    private String fromUser;

    private int postCommentId;

    private char type;

    private String details;

    private char viewFlag;

    private String dateNotified;



    public Notification() {

    }

    public Notification(int id, int userId, int fromId, int postCommentId, char type,
                        String details, char viewFlag, String dateNotified, String fromUser) {
        this.id = id;
        this.userId = userId;
        this.fromId = fromId;
        this.postCommentId = postCommentId;
        this.type = type;
        this.details = details;
        this.viewFlag = viewFlag;
        this.dateNotified = dateNotified;
        this.fromUser = fromUser;
    }

    public Notification(int userId, int fromId, int postCommentId, char type, String details) {
        this.userId = userId;
        this.fromId = fromId;
        this.postCommentId = postCommentId;
        this.type = type;
        this.details = details;
    }

    public Notification(int id, int userId, int fromId, int postCommentId, char type, String details) {
        this.id = id;
        this.userId = userId;
        this.fromId = fromId;
        this.postCommentId = postCommentId;
        this.type = type;
        this.details = details;
    }


    public String getFromUser() {
        return fromUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getFromId() {
        return fromId;
    }

    public void setFromId(int fromId) {
        this.fromId = fromId;
    }

    public int getPostCommentId() {
        return postCommentId;
    }

    public void setPostCommentId(int postCommentId) {
        this.postCommentId = postCommentId;
    }

    public char getType() {
        return type;
    }

    public void setType(char type) {
        this.type = type;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public char getViewFlag() {
        return viewFlag;
    }

    public void setViewFlag(char viewFlag) {
        this.viewFlag = viewFlag;
    }

    public String getDateNotified() {
        return dateNotified;
    }

    public void setDateNotified(String dateNotified) {
        this.dateNotified = dateNotified;
    }
}
