package practiceandroidapplication.android.com.meetmeup.Entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sibimark on 08/12/2015.
 */

//singleton listNationalities

public class ListNationalities {

    public static ListNationalities listNationalities = null;

    public List<Nationality> nationalities = new ArrayList<>();

    private ListNationalities() {}

    public static ListNationalities getInstanceListNationalities () {
        if (listNationalities == null) {
            listNationalities = new ListNationalities();
        }

        return listNationalities;
    }

}
