package practiceandroidapplication.android.com.meetmeup.Fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import practiceandroidapplication.android.com.meetmeup.Entity.Events;
import practiceandroidapplication.android.com.meetmeup.Entity.Meetups;
import practiceandroidapplication.android.com.meetmeup.Entity.Network;
import practiceandroidapplication.android.com.meetmeup.Entity.Sessions;
import practiceandroidapplication.android.com.meetmeup.Entity.User;
import practiceandroidapplication.android.com.meetmeup.Handles.JSONParser;
import practiceandroidapplication.android.com.meetmeup.R;
import practiceandroidapplication.android.com.meetmeup.ViewEventsActivity;


public class EventsFragment extends Fragment {

    private static final String RETRIEVE_EVENTS_URL = Network.forDeploymentIp + "events_retrieve.php";
    private static final String UPDATE_EVENT_URL = Network.forDeploymentIp + "event_update.php";
    private static final String TAG_STATUS = "status";
    private static final String TAG_RESPONSE = "response";

    JSONParser jsonParser = new JSONParser();
    ProgressDialog pDialog;

    Toolbar toolbar;
    //ListView listEvents;
    LinearLayout listOfEvents;
    TextView lblMessage;

    LinearLayout listOfFeeds;

    Sessions sessions = Sessions.getSessionsInstance();
    List<Events> currentEvents = new ArrayList<>();

    User currentUser = Sessions.getSessionsInstance().currentUser;


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_events, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listOfFeeds = (LinearLayout) getActivity().findViewById(R.id.linear_feeds);

        listOfEvents = (LinearLayout) getActivity().findViewById(R.id.linear_events);
        lblMessage = (TextView) getActivity().findViewById(R.id.lbl_message);

        lblMessage.setVisibility(View.INVISIBLE);
        listOfEvents.setVisibility(View.INVISIBLE);

        new RetrieveEvents().execute();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    public void displayEvents() {

        for (Events event : currentEvents) {

            LinearLayout recordOfEvents = new LinearLayout(getActivity());
            recordOfEvents.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            recordOfEvents.setOrientation(LinearLayout.VERTICAL);
            recordOfEvents.setPadding(10, 10, 10, 10);

            Log.d("Event name", event.getEventName());
            recordOfEvents.setTag(event.getId());

            final LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
            params1.weight = 1.0f;
            params1.gravity = Gravity.LEFT;

            LinearLayout user = new LinearLayout(getActivity());
            user.setOrientation(LinearLayout.HORIZONTAL);
            user.setPadding(10, 10, 10, 10);
            user.setLayoutParams(params1);

            final LinearLayout.LayoutParams imageGroupParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
            imageGroupParams.height = 50;
            imageGroupParams.width = 50;
            imageGroupParams.gravity = Gravity.LEFT;

            final ImageView eventPostedByImage = new ImageView(getActivity());
            eventPostedByImage.setImageResource(R.drawable.meetmeup);
            eventPostedByImage.setLayoutParams(imageGroupParams);

            final TextView eventPostedBy = new TextView(getActivity());
            eventPostedBy.setText("Mark Sibi");
            eventPostedBy.setTextSize(20);
            eventPostedBy.setTextColor(Color.BLACK);

            user.addView(eventPostedByImage);
            user.addView(eventPostedBy);

            final TextView eventName = new TextView(getActivity());
            eventName.setText(event.getEventName());
            eventName.setTextSize(15);
            eventName.setTextColor(Color.BLACK);

            final TextView eventDetails = new TextView(getActivity());
            eventDetails.setText("Details: " + event.getDetails());
            eventDetails.setTextSize(15);
            eventDetails.setTextColor(Color.BLACK);

            final TextView eventKey = new TextView(getActivity());
            eventKey.setText("Key: " + event.getKey());
            eventKey.setTextSize(15);
            eventKey.setTextColor(Color.BLACK);

            final TextView eventLocation = new TextView(getActivity());
            eventLocation.setText("Location: " + event.getLocation());
            eventLocation.setTextSize(15);
            eventLocation.setTextColor(Color.BLACK);

            final TextView startDate = new TextView(getActivity());
            startDate.setText("Start Date: " + event.getEndDate());
            startDate.setTextSize(15);
            startDate.setTextColor(Color.BLACK);

            final TextView endDate = new TextView(getActivity());
            endDate.setText("End date: " + event.getEndDate());
            endDate.setTextSize(15);
            endDate.setTextColor(Color.BLACK);

            final LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
            params2.weight = 1.0f;
            params2.gravity = Gravity.RIGHT;
            params2.leftMargin = 50;

            LinearLayout options = new LinearLayout(getActivity());
            options.setOrientation(LinearLayout.HORIZONTAL);
            options.setPadding(10, 10, 10, 10);
            options.setLayoutParams(params2);

            final TextView view = new TextView(getActivity());
            view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            view.setPadding(10, 10, 0, 10);
            view.setText("view");
            view.setTextSize(15);
            view.setBackgroundColor(Color.TRANSPARENT);

            options.addView(view);

            recordOfEvents.addView(user);
            recordOfEvents.addView(eventName);
            recordOfEvents.addView(eventDetails);
            recordOfEvents.addView(eventKey);
            recordOfEvents.addView(eventLocation);
            recordOfEvents.addView(startDate);
            recordOfEvents.addView(endDate);
            recordOfEvents.addView(options);

            listOfEvents.addView(recordOfEvents);

        }
    }

    /* thread */

    /*
        thread
     */

    class RetrieveEvents extends AsyncTask<String, String, String> {

        String[] groups = new String[9999];
        ArrayAdapter<String> eventAdapter;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity(), R.style.progress);
            pDialog.setCancelable(false);
            pDialog.setProgressStyle(android.R.style.Widget_Material_ProgressBar_Large);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... userInfo) {
            // TODO Auto-generated method stub

            int success;

            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();

                Log.d("USER_ID (user)", currentUser.getId() + "");
                params.add(new BasicNameValuePair("id", currentUser.getId() + ""));
                params.add(new BasicNameValuePair("filter", "all"));

                Log.d("request!", "starting");

                JSONObject json = jsonParser.makeHttpRequest(
                        RETRIEVE_EVENTS_URL, "POST", params);

                Log.d("Fetching...", json.toString());

                success = json.getInt(TAG_STATUS);

                if (success == 1) {
                    Log.d("Success!", json.toString());

                    JSONArray jUserArray = json.getJSONArray("events");
                    JSONObject jUserObject;

                    //sessions.removeGroups();

                    for (int i = 0; i < jUserArray.length(); i++) {
                        jUserObject = jUserArray.getJSONObject(i);

                        currentEvents.add(new Events(jUserObject.getInt("id"), jUserObject.getString("event_name"),
                                jUserObject.getString("details"), jUserObject.getString("location"), jUserObject.getString("key"),
                                jUserObject.getString("start_date"), jUserObject.getString("end_date")));


                        Log.d("ID:", jUserObject.getInt("id") + "");
                    }

                    return json.getString(TAG_RESPONSE);
                } else {
                    Log.d("Fetching failed...", json.getString(TAG_RESPONSE));
                    return json.getString(TAG_RESPONSE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }


        protected void onPostExecute(String message) {
            pDialog.dismiss();
            try {
                listOfFeeds.setVisibility(View.VISIBLE);
                listOfEvents.setVisibility(View.VISIBLE);
                if (message.equals("Successful")) {
                    Toast.makeText(getActivity(), message + "!", Toast.LENGTH_SHORT).show();
                    displayEvents();
                } else if (message.equals("No events")) {
                    lblMessage.setVisibility(View.VISIBLE);
                } else {
                    lblMessage.setVisibility(View.VISIBLE);
                    lblMessage.setText("Please check your internet connection");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    } // end of thread retrieve user
}
