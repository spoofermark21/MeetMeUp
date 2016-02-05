package practiceandroidapplication.android.com.meetmeup.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import practiceandroidapplication.android.com.meetmeup.EditGroupActivity;
import practiceandroidapplication.android.com.meetmeup.Entity.Events;
import practiceandroidapplication.android.com.meetmeup.Entity.Network;
import practiceandroidapplication.android.com.meetmeup.Entity.Sessions;
import practiceandroidapplication.android.com.meetmeup.Entity.User;
import practiceandroidapplication.android.com.meetmeup.Handles.JSONParser;
import practiceandroidapplication.android.com.meetmeup.Helpers.ImageHelper;
import practiceandroidapplication.android.com.meetmeup.R;
import practiceandroidapplication.android.com.meetmeup.ViewMeetupsActivity;


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

    Button btnEvents;

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
        lblMessage.setVisibility(View.GONE);

        listOfEvents.setVisibility(View.INVISIBLE);

        /*btnEvents = (Button) getActivity().findViewById(R.id.btn_events);
        btnEvents.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                new RetrieveEvents().execute();
            }
        });*/

        new RetrieveEvents().execute();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    /* thread

     */

    public void displayEvents() {

        listOfEvents.removeAllViews();
        //removeEvents();

        for (Events event : currentEvents) {

            final LinearLayout.LayoutParams linearGroups = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            linearGroups.setMargins(0,0,0,60);

            LinearLayout recordOfEvents = new LinearLayout(getActivity());
            recordOfEvents.setLayoutParams(linearGroups);
            recordOfEvents.setOrientation(LinearLayout.VERTICAL);
           //recordOfEvents.setPadding(3, 3, 3, 3);

            Log.d("Event name", event.getEventName());
            recordOfEvents.setTag(event.getId());

            final LinearLayout.LayoutParams imageEventParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            imageEventParams.weight = 1.0f;
            imageEventParams.height = 650;
            imageEventParams.width = 550;
            //imageEventParams.setMargins(0,0,0,60);
            imageEventParams.gravity = Gravity.CENTER;

            final ImageView eventPostedByImage = new ImageView(getActivity());
            eventPostedByImage.setVisibility(View.GONE);

            eventPostedByImage.setTag("Posted by" + event.getPostedBy());

            if(!event.getPostedByName().equals("null") && !event.getPostedByName().equals("")) {

                class DownloadEventsImage extends AsyncTask<Void, Void, Bitmap> {

                    String filename;
                    //Bitmap groupImage;

                    public DownloadEventsImage(String filename) {
                        this.filename = filename;
                    }

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                    }

                    @Override
                    protected Bitmap doInBackground(Void... params) {
                        // TODO Auto-generated method stub

                        try {
                            final String USER_IMAGE_URL = Network.forDeploymentIp + "meetmeup/uploads/users/" + this.filename;
                            Log.d("Image", USER_IMAGE_URL);
                            URLConnection connection = new URL(USER_IMAGE_URL).openConnection();
                            connection.setConnectTimeout(1000 * 30);

                            return BitmapFactory.decodeStream((InputStream) connection.getContent(), null, null);
                        } catch (FileNotFoundException ex) {
                            ex.printStackTrace();
                            return null;
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            return null;
                        }
                    }


                    protected void onPostExecute(Bitmap bitmap) {
                        //pDialog.dismiss();
                        try {
                            if (bitmap != null) {
                                Log.d("Image", "Success");
                                eventPostedByImage.setImageBitmap(bitmap);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }

                }

                /*DownloadGroupImage downloadUserImage = new DownloadGroupImage(group.getGroupImage() + ".JPG");
                downloadUserImage.execute();
                groupImage.setImageResource(R.drawable.meetmeup);
                //groupImage.setImageBitmap(downloadUserImage.downloadImage());*/
                new DownloadEventsImage(event.getPostedUserImage() + ".JPG").execute();
                eventPostedByImage.setLayoutParams(imageEventParams);
                eventPostedByImage.setScaleType(ImageView.ScaleType.FIT_XY);
                eventPostedByImage.setVisibility(View.VISIBLE);
            }

            final TextView eventPostedBy = new TextView(getActivity());
            eventPostedBy.setText("Posted by :" + event.getPostedByName());
            eventPostedBy.setTextSize(15);
            eventPostedBy.setTextColor(Color.BLACK);


            final TextView eventName = new TextView(getActivity());
            eventName.setText(event.getEventName());
            eventName.setTextSize(25);
            eventName.setTextColor(Color.BLACK);
            eventName.setTextColor(Color.parseColor("#37BC9B"));

            eventName.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    final LinearLayout parent = (LinearLayout) v.getParent().getParent();

                    Intent meetups = new Intent(getActivity(), ViewMeetupsActivity.class);
                    meetups.putExtra("MEETUPS_ID", parent.getTag() + "");

                    Log.d("MEETUPS_ID", parent.getTag() + "");

                    startActivity(meetups);
                    getActivity().finish();
                }
            });

            final TextView eventDetails = new TextView(getActivity());
            eventDetails.setText("Details: " + event.getDetails());
            eventDetails.setTextSize(15);
            eventDetails.setTextColor(Color.BLACK);

            /*final TextView eventKey = new TextView(getActivity());
            eventKey.setText("Key: " + event.getKey());
            eventKey.setTextSize(15);
            eventKey.setTextColor(Color.BLACK);*/

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
            view.setTextColor(Color.parseColor("#37BC9B"));

            view.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    final LinearLayout parent = (LinearLayout) v.getParent().getParent();

                    Intent events = new Intent(getActivity(), EditGroupActivity.class);
                    events.putExtra("Events ID", parent.getTag() + "");
                    startActivity(events);

                    Toast.makeText(getActivity(), parent.getTag() + "!", Toast.LENGTH_SHORT).show();
                }
            });

            //options.addView(view);

            recordOfEvents.addView(eventName);
            recordOfEvents.addView(eventDetails);
            //recordOfEvents.addView(eventKey);
            recordOfEvents.addView(eventLocation);
            recordOfEvents.addView(startDate);
            recordOfEvents.addView(endDate);
            recordOfEvents.addView(eventPostedBy);
            recordOfEvents.addView(eventPostedByImage);

            //recordOfEvents.addView(options);

            listOfEvents.addView(recordOfEvents);

        }
    }

    /*
        thread
     */

    class RetrieveEvents extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity(), R.style.progress);
            pDialog.setCancelable(true);
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
                params.add(new BasicNameValuePair("filter", "newsfeed"));

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
                    currentEvents.clear();

                    for (int i = 0; i < jUserArray.length(); i++) {
                        jUserObject = jUserArray.getJSONObject(i);

                        Events event = new Events(jUserObject.getInt("id"), jUserObject.getString("event_name"),
                                jUserObject.getString("details"), jUserObject.getString("location"), jUserObject.getString("key"),
                                jUserObject.getString("start_date"), jUserObject.getString("end_date"));

                        event.setPostedBy(jUserObject.getInt("posted_by"));
                        event.setPostedByName(jUserObject.getString("posted_by_user"));
                        event.setPostedUserImage(jUserObject.getString("user_image"));

                        currentEvents.add(event);


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
