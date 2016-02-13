package practiceandroidapplication.android.com.meetmeup.Entity;

/**
 * Created by sibimark on 05/01/2016.
 */
public class Attendees {

    private int id;

    private int postId;

    private char postType;

    private int userId;

    private String userName;

    private String userImage;

    private char collaborationStatus;

    private String requestDate;

    private String acceptedDate;

    public Attendees() {

    }

    public Attendees(int userId, String userName, String userImage){
        this.userId = userId;
        this.userName = userName;
        this.userImage = userImage;
    }

    public Attendees(int postId, char postType, int userId,
                     char collaborationStatus, String requestDate) {
        this.postId = postId;
        this.postType = postType;
        this.userId = userId;
        this.collaborationStatus = collaborationStatus;
        this.requestDate = requestDate;
    }


    public Attendees(int id, int postId, char postType, int userId,
                     char collaborationStatus, String requestDate, String acceptedDate) {
        this.id = id;
        this.postId = postId;
        this.postType = postType;
        this.userId = userId;
        this.collaborationStatus = collaborationStatus;
        this.requestDate = requestDate;
        this.acceptedDate = acceptedDate;
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

    public char getCollaborationStatus() {
        return collaborationStatus;
    }

    public void setCollaborationStatus(char collaborationStatus) {
        this.collaborationStatus = collaborationStatus;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }

    public String getAcceptedDate() {
        return acceptedDate;
    }

    public void setAcceptedDate(String acceptedDate) {
        this.acceptedDate = acceptedDate;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }


    @Override
    public String toString() {
        return "Attendees{" +
                "id=" + id +
                ", postId=" + postId +
                ", postType=" + postType +
                ", userId=" + userId +
                ", collaborationStatus=" + collaborationStatus +
                ", requestDate='" + requestDate + '\'' +
                ", acceptedDate='" + acceptedDate + '\'' +
                '}';
    }
}
