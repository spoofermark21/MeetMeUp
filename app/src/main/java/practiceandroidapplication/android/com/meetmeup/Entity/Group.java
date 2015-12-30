package practiceandroidapplication.android.com.meetmeup.Entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sibimark on 19/12/2015.
 */
public class Group {

    private int id;

    private String groupName;

    private String details;

    private int createdBy;

    private String createdDate;

    private char availFlag;

    private char activeFlag;

    private String groupImage;

    private List<GroupMember> members = new ArrayList<>();

    public Group() {}

    public Group(int id, String groupName, String details,int createdBy, String createdDate) {
        this.id = id;
        this.groupName = groupName;
        this.details = details;
        this.createdBy = createdBy;
        this.createdDate = createdDate;
    }

    public Group(String groupName, String details, int createdBy) {
        this.groupName = groupName;
        this.details = details;
        this.createdBy = createdBy;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public char getAvailFlag() {
        return availFlag;
    }

    public void setAvailFlag(char availFlag) {
        this.availFlag = availFlag;
    }

    public char getActiveFlag() {
        return activeFlag;
    }

    public void setActiveFlag(char activeFlag) {
        this.activeFlag = activeFlag;
    }

    public String getGroupImage() {
        return groupImage;
    }

    public void setGroupImage(String groupImage) {
        this.groupImage = groupImage;
    }

    public List<GroupMember> getMembers() {
        return members;
    }

    public void setMembers(List<GroupMember> members) {
        this.members = members;
    }
}