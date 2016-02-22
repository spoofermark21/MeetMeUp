package practiceandroidapplication.android.com.meetmeup.Entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sibimark on 26/12/2015.
 */
public class Events {

    private int id;

    private String eventName;

    private char eventType;

    private String key;

    private String details;

    private String location;

    private double lattitude;

    private double longtitude;

    private String startDate;

    private String endDate;

    private String postedDate;

    private int postedBy;

    private int postedByUser;

    private String postedByName;

    private char postedByType;

    private char availStatus;

    private char activeFlag;

    private String postedUserImage;

    private static List<Comments> comments;

    private int attendees;

    private String[] eventTypeString = {"Traditional","Personal","Blah"};

    public Events(){}

    public Events(int id, String eventName, String details, String location,
                  String key, String startDate, String endDate, int postedBy, String postedByName) {
        this.id = id;
        this.eventName = eventName;
        this.details = details;
        this.location = location;
        this.key = key;
        this.startDate = startDate;
        this.endDate = endDate;
        this.postedBy = postedBy;
        this.postedByName = postedByName;
    }

    public Events(int id, String eventName, String details, String location,
                  String key, String startDate, String endDate) {
        this.id = id;
        this.eventName = eventName;
        this.details = details;
        this.location = location;
        this.key = key;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public int getPostedByUser() {
        return postedByUser;
    }

    public void setPostedByUser(int postedByUser) {
        this.postedByUser = postedByUser;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public char getEventType() {
        return eventType;
    }

    public void setEventType(char eventType) {
        this.eventType = eventType;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getPostedDate() {
        return postedDate;
    }

    public void setPostedDate(String postedDate) {
        this.postedDate = postedDate;
    }

    public int getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(int postedBy) {
        this.postedBy = postedBy;
    }

    public char getPostedByType() {
        return postedByType;
    }

    public void setPostedByType(char postedByType) {
        this.postedByType = postedByType;
    }

    public char getAvailStatus() {
        return availStatus;
    }

    public void setAvailStatus(char availStatus) {
        this.availStatus = availStatus;
    }

    public char getActiveFlag() {
        return activeFlag;
    }

    public void setActiveFlag(char activeFlag) {
        this.activeFlag = activeFlag;
    }

    public String getPostedByName() {
        return postedByName;
    }

    public void setPostedByName(String postedByName) {
        this.postedByName = postedByName;
    }

    public String getPostedUserImage() {
        return postedUserImage;
    }

    public void setPostedUserImage(String postedUserImage) {
        this.postedUserImage = postedUserImage;
    }

    public void setEventTypeString(String[] eventTypeString) {
        this.eventTypeString = eventTypeString;
    }

    public int getAttendees() {
        return attendees;
    }

    public void setAttendees(int attendees) {
        this.attendees = attendees;
    }

    public String[] getEventTypeString() {
        return eventTypeString;
    }

    public static List<Comments> getComments() {
        return comments;
    }

    public static void setComments(List<Comments> comments) {
        Events.comments = comments;
    }

    public List<String> listOfComments() {

        List<String> listOfComments = new ArrayList<>();

        for (Comments comments : getComments()) {
            listOfComments.add(comments.getComment() + " -" + comments.getUserName());
        }

        return listOfComments;
    }

    public double getLattitude() {
        return lattitude;
    }

    public void setLattitude(double lattitude) {
        this.lattitude = lattitude;
    }

    public double getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(double longtitude) {
        this.longtitude = longtitude;
    }
}
