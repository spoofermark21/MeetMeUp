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

import practiceandroidapplication.android.com.meetmeup.Entity.Meetups;
import practiceandroidapplication.android.com.meetmeup.Entity.Network;
import practiceandroidapplication.android.com.meetmeup.Entity.Sessions;
import practiceandroidapplication.android.com.meetmeup.Entity.User;
import practiceandroidapplication.android.com.meetmeup.Handles.JSONParser;
import practiceandroidapplication.android.com.meetmeup.MeetupsActivity;
import practiceandroidapplication.android.com.meetmeup.NewsfeedActivity;
import practiceandroidapplication.android.com.meetmeup.R;
import practiceandroidapplication.android.com.meetmeup.ViewMeetupActivity;


public class MeetupsFragment extends Fragment {

    private static final String RETRIEVE_MEETUPS_URL = Network.forDeploymentIp + "meetups_retrieve.php";
    private static final String TAG_STATUS = "status";
    private static final String TAG_RESPONSE = "response";

    JSONParser jsonParser = new JSONParser();
    ProgressDialog pDialog;

    Toolbar toolbar;
    //ListView listMeetups;
    LinearLayout listOfMeetups;
    TextView lblMessage;

    LinearLayout listOfFeeds;

    Sessions sessions = Sessions.getSessionsInstance();
    List<Meetups> currentMeetups = new ArrayList<>();
    User currentUser = Sessions.getSessionsInstance().currentUser;


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_meetups, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listOfFeeds = (LinearLayout) getActivity().findViewById(R.id.linear_feeds);

        listOfMeetups = (LinearLayout) getActivity().findViewById(R.id.linear_meetups);
        lblMessage = (TextView) getActivity().findViewById(R.id.lbl_message);

        listOfMeetups.setVisibility(View.INVISIBLE);
        lblMessage.setVisibility(View.INVISIBLE);

        new RetrieveMeetups().execute();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    public void displayMeetups() {
        for (Meetups meetups : currentMeetups) {

            LinearLayout recordOfMeetups = new LinearLayout(getActivity());
            recordOfMeetups.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            recordOfMeetups.setOrientation(LinearLayout.VERTICAL);
            recordOfMeetups.setPadding(10, 10, 10, 10);

            Log.d("Meetup", meetups.getDetails());
            recordOfMeetups.setTag(meetups.getId());

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

            final ImageView meetupPostedByImage = new ImageView(getActivity());
            meetupPostedByImage.setImageResource(R.drawable.meetmeup);
            meetupPostedByImage.setLayoutParams(imageGroupParams);

            final TextView meetupPostedBy = new TextView(getActivity());
            meetupPostedBy.setText("Mark Sibi");
            meetupPostedBy.setTextSize(20);
            meetupPostedBy.setTextColor(Color.BLACK);

            user.addView(meetupPostedByImage);
            user.addView(meetupPostedBy);

            final TextView meetupSubject = new TextView(getActivity());
            meetupSubject.setText(meetups.getSubject());
            meetupSubject.setTextSize(15);
            meetupSubject.setTextColor(Color.BLACK);

            final TextView meetupDetails = new TextView(getActivity());
            meetupDetails.setText("Details: " + meetups.getDetails());
            meetupDetails.setTextSize(15);
            meetupDetails.setTextColor(Color.BLACK);

            final TextView meetupLocation = new TextView(getActivity());
            meetupLocation.setText("Location: " + meetups.getLocation());
            meetupLocation.setTextSize(15);
            meetupLocation.setTextColor(Color.BLACK);

            final TextView meetupKey = new TextView(getActivity());
            meetupKey.setText("Key: " + meetups.getKey());
            meetupKey.setTextSize(15);
            meetupKey.setTextColor(Color.BLACK);


            final LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
            params2.weight = 1.0f;
            params2.gravity = Gravity.RIGHT;
            params2.leftMargin = 50;

            LinearLayout options = new LinearLayout(getActivity());
            options.setOrientation(LinearLayout.HORIZONTAL);
            options.setPadding(10, 10, 10, 10);
            options.setLayoutParams(params2);

            //final ImageButton edit = new ImageButton(this);
            //edit.setImageResource(R.drawable.ic_mode_edit_black_24dp);
            final TextView view = new TextView(getActivity());
            view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            view.setPadding(10, 10, 0, 10);
            view.setText("View");
            view.setTextSize(15);
            view.setBackgroundColor(Color.TRANSPARENT);

            options.addView(view);

            recordOfMeetups.addView(user);
            recordOfMeetups.addView(meetupSubject);
            recordOfMeetups.addView(meetupDetails);
            recordOfMeetups.addView(meetupLocation);
            recordOfMeetups.addView(meetupKey);
            recordOfMeetups.addView(options);

            listOfMeetups.addView(recordOfMeetups);
        }

    }

    /* thread */

    class RetrieveMeetups extends AsyncTask<String, String, String> {

        String[] groups = new String[9999];
        ArrayAdapter<String> meetupAdapter;

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
                        RETRIEVE_MEETUPS_URL, "POST", params);

                Log.d("Fetching...", json.toString());

                success = json.getInt(TAG_STATUS);

                if (success == 1) {
                    Log.d("Success!", json.toString());

                    JSONArray jUserArray = json.getJSONArray("meetups");
                    JSONObject jUserObject;

                    //sessions.removeGroups();

                    for (int i = 0; i < jUserArray.length(); i++) {
                        jUserObject = jUserArray.getJSONObject(i);

                        currentMeetups.add(new Meetups(jUserObject.getInt("id"), jUserObject.getString("subject"),
                                jUserObject.getString("details"), jUserObject.getString("location"),
                                jUserObject.getString("posted_date"), jUserObject.getString("key")));

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
                listOfMeetups.setVisibility(View.VISIBLE);
                if (message.equals("Successful")) {
                    Toast.makeText(getActivity(), message + "!", Toast.LENGTH_SHORT).show();
                    displayMeetups();
                } else if (message.equals("No meetups")) {
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
