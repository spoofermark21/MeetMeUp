package practiceandroidapplication.android.com.meetmeup.Entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sibimark on 19/12/2015.
 */
public class Preference {

    private int startAge;

    private int endAge;

    private char gender;

    private String location;

    private List<Nationality> prefNationalities = new ArrayList<>();

    public Preference(){

    }

    public Preference(int startAge, int endAge,
                      char gender, String location) {

        this.startAge = startAge;
        this.endAge = endAge;
        this.gender = gender;
        this.location = location;

    }

    public List<Nationality> getPrefNationalities(){
        return prefNationalities;
    }

    public int getStartAge() {
        return startAge;
    }

    public void setStartAge(int startAge) {
        this.startAge = startAge;
    }

    public int getEndAge() {
        return endAge;
    }

    public void setEndAge(int endAge) {
        this.endAge = endAge;
    }

    public char getGender() {
        return gender;
    }

    public void setGender(char gender) {
        this.gender = gender;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

}
