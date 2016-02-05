package practiceandroidapplication.android.com.meetmeup.Entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sibimark on 05/02/2016.
 */
public class ListLocations {

    public static ListLocations listOfLocations = null;
    public List<Location> locations = new ArrayList<>();

    private ListLocations() {}

    public static ListLocations getInstanceListLocations(){
        if(listOfLocations == null) {
            listOfLocations = new ListLocations();
        }

        return listOfLocations;
    }


}
