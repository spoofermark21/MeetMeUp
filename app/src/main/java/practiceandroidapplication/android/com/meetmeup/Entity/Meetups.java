package practiceandroidapplication.android.com.meetmeup.Entity;

/**
 * Created by sibimark on 26/12/2015.
 */
public class Meetups {

    private int id;

    private String key;

    private String subject;

    private String details;

    private int postedBy;

    private String location;

    private String postedDate;

    private Preference preference;

    private char availStatus;

    private char activeFlag;

    public Meetups(){}

    public Meetups(int id, String subject, String details, String location, String postedDate){
        this.id = id;
        this.subject = subject;
        this.details = details;
        this.location = location;
        this.postedDate = postedDate;
    }

    public Meetups(int id, String subject, String details, int postedBy, String location,
                   String postedDate, Preference preference) {
        this.id = id;
        this.subject = subject;
        this.details = details;
        this.postedBy = postedBy;
        this.location = location;
        this.preference = preference;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(int postedBy) {
        this.postedBy = postedBy;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPostedDate() {
        return postedDate;
    }

    public void setPostedDate(String postedDate) {
        this.postedDate = postedDate;
    }

    public Preference getPreference() {
        return preference;
    }

    public void setPreference(Preference preference) {
        this.preference = preference;
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
}
