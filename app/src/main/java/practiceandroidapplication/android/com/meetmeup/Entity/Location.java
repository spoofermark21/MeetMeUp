package practiceandroidapplication.android.com.meetmeup.Entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sibimark on 05/02/2016.
 */
public class Location {

    private int id;

    private String location;

    private String lattitude;

    private String longtitude;

    public Location(int id, String location) {
        this.id = id;
        this.location = location;
    }

    public Location(int id, String location, String lattitude, String longtitude) {
        this.id = id;
        this.location = location;
        this.lattitude = lattitude;
        this.longtitude = longtitude;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLattitude() {
        return lattitude;
    }

    public void setLattitude(String lattitude) {
        this.lattitude = lattitude;
    }

    public String getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(String longtitude) {
        this.longtitude = longtitude;
    }
}
