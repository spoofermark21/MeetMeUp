package practiceandroidapplication.android.com.meetmeup.Entity;

import android.util.Log;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

import practiceandroidapplication.android.com.meetmeup.R;

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

    final CharSequence[] items = {" Easy "," Medium "," Hard "," Very Hard "};

    public static List<String> loadNationalities() {
        ListNationalities listNationalities = ListNationalities.getInstanceListNationalities();
        List<String> list = new ArrayList<>();

        for (Nationality natiolity : listNationalities.nationalities) {
            Log.d(natiolity.getId() + "", natiolity.getNationality());
            list.add(natiolity.getNationality());
        }

        return list;
    }

    public static CharSequence[] loadNationalitesSequence() {
        ListNationalities listNationalities = ListNationalities.getInstanceListNationalities();
        List<String> list = new ArrayList<>();

        for (Nationality natiolity : listNationalities.nationalities) {
            Log.d(natiolity.getId() + "", natiolity.getNationality());
            list.add(natiolity.getNationality());
        }

        return list.toArray(new CharSequence[list.size()]);
    }


}
