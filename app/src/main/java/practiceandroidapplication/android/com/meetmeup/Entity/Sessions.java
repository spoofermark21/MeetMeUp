package practiceandroidapplication.android.com.meetmeup.Entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sibimark on 18/12/2015.
 */
public class Sessions {

    public static Sessions sessions = null;

    public static User currentUser = new User();
    public static Preference currentPreference = new Preference();


    public static List<Group> currentGroups = new ArrayList<>();
    public static List<GroupMember> currentGroupMembers = new ArrayList<>();

    public static double currentLocationLatitude;
    public static double currentLocationLongtitude;


    private Sessions () {}

    public static Sessions getSessionsInstance() {
        if (sessions == null) {
            sessions = new Sessions();
        }
        return sessions;
    }

    public static List<String> listOfGroups(List<Group> currentGroups){

        List<String> listGroupsNames = new ArrayList<>();

        for(Group group : currentGroups){
            if(group.getGroupName() != null){
                listGroupsNames.add(group.getGroupName());
            }

        }
        return listGroupsNames;
    }

    public static List<String> listOfMeetups(List<Meetups> currentMeetups){

        List<String> listMeetupNames = new ArrayList<>();

        for(Meetups meetups : currentMeetups){
            if(meetups.getDetails() != null){
                listMeetupNames.add(meetups.getDetails());
            }

        }
        return listMeetupNames;
    }

    public static List<String> listOfEvents(List<Events> currentEvents){

        List<String> listEvents = new ArrayList<>();

        for(Events events : currentEvents){
            if(events.getEventName() != null){
                listEvents.add(events.getEventName());
            }

        }
        return listEvents;
    }

    public static int getEventId (List<Events> currentEvents, int position){
        List<String> listEvents = new ArrayList<>();

        int counter = 0;
        for(Events events : currentEvents){
            if(position == counter) {
                position = events.getId();
            }
            counter += 1;
        }
        return position;
    }


    public static void removeGroups(){
        currentGroups = null;
    }

}
