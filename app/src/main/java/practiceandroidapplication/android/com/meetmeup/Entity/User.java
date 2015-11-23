package practiceandroidapplication.android.com.meetmeup.Entity;

import java.util.Date;

/**
 * Created by sibimark on 16/11/2015.
 */
public class User {

    // account
    private String username;
    private String password;

    //bio
    private String firstName;
    private String lastName;
    private Date birthDate;
    private int nationId;
    private String currentLocation;

    //contacts
    private String emailAddress;
    private String contactNumber;
    private char privacyFlag;

    //optional preference
    //private Preferred pref;

    //flags
    private char activeFlag;
    //image
    private String userImage;



    //for login purposes
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    //registration
    public User(String username, String password, String firstName,
                Date birthDate, int nationId, String currentLocation,
                String emailAddress, String contactNumber,
                char privacyFlag, char activeFlag) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.birthDate = birthDate;
        this.currentLocation = currentLocation;
        this.emailAddress = emailAddress;
        this.contactNumber = contactNumber;
        this.privacyFlag = privacyFlag;
        this.activeFlag = activeFlag;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public int getNationId() {
        return nationId;
    }

    public void setNationId(int nationId) {
        this.nationId = nationId;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public char getPrivacyFlag() {
        return privacyFlag;
    }

    public void setPrivacyFlag(char privacyFlag) {
        this.privacyFlag = privacyFlag;
    }

    public char getActiveFlag() {
        return activeFlag;
    }

    public void setActiveFlag(char activeFlag) {
        this.activeFlag = activeFlag;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", birthDate=" + birthDate +
                ", nationId=" + nationId +
                ", currentLocation='" + currentLocation + '\'' +
                ", emailAddress='" + emailAddress + '\'' +
                ", contactNumber='" + contactNumber + '\'' +
                ", privacyFlag=" + privacyFlag +
                ", activeFlag=" + activeFlag +
                ", userImage='" + userImage + '\'' +
                '}';
    }
}
