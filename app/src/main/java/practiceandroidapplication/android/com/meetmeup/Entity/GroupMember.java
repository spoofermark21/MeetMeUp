package practiceandroidapplication.android.com.meetmeup.Entity;

/**
 * Created by sibimark on 19/12/2015.
 */
public class GroupMember {

    private int id;

    private int groupId;

    private int userId;

    private String userName;

    private String userImage;

    private char collaborationStatus;

    private String requestDate;

    private String acceptedDate;

    public GroupMember(){

    }

    public GroupMember(int userId, String userName, String userImage) {
        this.userId = userId;
        this.userName = userName;
        this.userImage = userImage;
    }

    public GroupMember(int groupId, int userId) {
        this.groupId = groupId;
        this.userId = userId;
    }

    public GroupMember(int id, int groupId, int userId){
        this.id = id;
        this.groupId = groupId;
        this.userId = userId;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
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
        return "GroupMember{" +
                "id=" + id +
                ", groupId=" + groupId +
                ", userId=" + userId +
                ", collaborationStatus=" + collaborationStatus +
                ", requestDate='" + requestDate + '\'' +
                ", acceptedDate='" + acceptedDate + '\'' +
                '}';
    }
}
