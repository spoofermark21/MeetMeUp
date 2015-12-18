package practiceandroidapplication.android.com.meetmeup.Entity;

/**
 * Created by sibimark on 18/12/2015.
 */
public class Sessions {

    public static Sessions sessions = null;
    public static User currentUser = new User();

    private Sessions () {}

    public static Sessions getSessionsInstance() {
        if (sessions == null) {
            sessions = new Sessions();
        }
        return sessions;
    }

}
