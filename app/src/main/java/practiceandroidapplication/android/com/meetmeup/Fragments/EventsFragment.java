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
import practiceandroidapplication.android.com.meetmeup.ViewEventsActivity;
import practiceandroidapplication.android.com.meetmeup.ViewMeetupsActivity;
import practiceandroidapplication.android.com.meetmeup.ViewProfileActivity;


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
            final LinearLayout.LayoutParams linearEvents = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            linearEvents.setMargins(0, 0, 0, 15);

            LinearLayout recordOfEvents = new LinearLayout(getActivity());
            recordOfEvents.setLayoutParams(linearEvents);
            recordOfEvents.setOrientation(LinearLayout.VERTICAL);
            recordOfEvents.setBackgroundResource(R.drawable.main_background);
            recordOfEvents.setPadding(20, 20, 20, 5);


            final LinearLayout.LayoutParams linearPostedBy = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            linearPostedBy.setMargins(0, 0, 0, 30);

            LinearLayout postedByLayout = new LinearLayout(getActivity());
            postedByLayout.setLayoutParams(linearPostedBy);
            postedByLayout.setOrientation(LinearLayout.HORIZONTAL);
            postedByLayout.setTag(event.getPostedBy());
            postedByLayout.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    startActivity(new Intent(getActivity(), ViewProfileActivity.class).putExtra("USER_ID", view.getTag() + "" + ""));
                    Log.d("USER_ID", view.getTag() + "");
                }
            });

            final LinearLayout.LayoutParams linearDate = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            linearDate.setMargins(0, 0, 0, 30);

            LinearLayout postedByDate = new LinearLayout(getActivity());
            postedByDate.setLayoutParams(linearDate);
            postedByDate.setOrientation(LinearLayout.VERTICAL);


            Log.d("Event", event.getEventName());
            recordOfEvents.setTag(event.getId());

            final LinearLayout.LayoutParams imageEventParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            imageEventParams.weight = 1.0f;
            imageEventParams.height = 80;
            imageEventParams.width = 80;
            imageEventParams.setMargins(0, 0, 10, 0);
            imageEventParams.gravity = Gravity.LEFT;

            final ImageView eventPostedByImage = new ImageView(getActivity());
            eventPostedByImage.setBackgroundColor(Color.parseColor("#E6E9ED"));

            final TextView eventPostedBy = new TextView(getActivity());
            eventPostedBy.setText(event.getPostedByName());
            eventPostedBy.setTextSize(17);
            eventPostedBy.setTextColor(Color.BLACK);

            if (!event.getPostedUserImage().equals("null") && !event.getPostedUserImage().equals("")) {

                class DownloadEventImage extends AsyncTask<Void, Void, Bitmap> {

                    String filename;

                    public DownloadEventImage(String filename) {
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

                new DownloadEventImage(event.getPostedUserImage() + ".JPG").execute();
                eventPostedByImage.setLayoutParams(imageEventParams);
                eventPostedByImage.setScaleType(ImageView.ScaleType.FIT_XY);
            }


            final TextView eventPostedDate = new TextView(getActivity());
            eventPostedDate.setText(timeDiff(event.getPostedDate()) + "");
            eventPostedDate.setTextSize(15);

            final TextView eventName = new TextView(getActivity());
            eventName.setText(event.getEventName());
            eventName.setTextSize(25);
            eventName.setTextColor(Color.BLACK);
            eventName.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    final LinearLayout parent = (LinearLayout) v.getParent().getParent();

                    Intent meetups = new Intent(getActivity(), ViewEventsActivity.class);
                    meetups.putExtra("EVENTS_ID", parent.getTag() + "");

                    Log.d("EVENTS_ID", parent.getTag() + "");

                    startActivity(meetups);
                }
            });

            final TextView eventDetails = new TextView(getActivity());
            eventDetails.setText(event.getDetails());
            eventDetails.setTextSize(15);

            final TextView eventLocation = new TextView(getActivity());
            eventLocation.setText("Location: " + event.getLocation());
            eventLocation.setTextSize(15);

            final LinearLayout.LayoutParams optionLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            optionLayout.weight = 1.0f;
            optionLayout.gravity = Gravity.CENTER;

            LinearLayout options = new LinearLayout(getActivity());
            options.setOrientation(LinearLayout.HORIZONTAL);
            options.setLayoutParams(optionLayout);

            final LinearLayout.LayoutParams btnLayout = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
            btnLayout.weight = 1.0f;
            btnLayout.setMargins(10, 10, 10, 10);

            final Button view = new Button(getActivity());
            view.setLayoutParams(btnLayout);
            view.setText("View");
            view.setTextSize(15);
            view.setAllCaps(false);
            view.setBackgroundColor(Color.parseColor("#00000000"));
            //view.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_info_black_24dp,0,0,0);
            view.setGravity(Gravity.CENTER);
            view.setTextColor(Color.BLACK);

            view.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    final LinearLayout parent = (LinearLayout) v.getParent().getParent();

                    Intent events = new Intent(getActivity(), ViewEventsActivity.class);
                    events.putExtra("EVENTS_ID", parent.getTag() + "");

                    Log.d("EVENTS_ID", parent.getTag() + "");

                    startActivity(events);
                }
            });

            /*final Button join = new Button(getActivity());
            join.setLayoutParams(btnLayout);
            join.setText("Join");
            join.setTextSize(15);
            join.setAllCaps(false);
            join.setBackgroundColor(Color.parseColor("#00000000"));
            join.setGravity(Gravity.CENTER);
            join.setTextColor(Color.BLACK);

            join.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    final LinearLayout parent = (LinearLayout) v.getParent();

                    Intent meetups = new Intent(getActivity(), ViewEventsActivity.class);
                    meetups.putExtra("EVENTS_ID", parent.getTag() + "");

                    Log.d("EVENTS_ID", parent.getTag() + "");

                    startActivity(meetups);
                    getActivity().finish();
                }
            });*/

            final Button map = new Button(getActivity());
            map.setLayoutParams(btnLayout);
            map.setText("Map");
            map.setTextSize(15);
            map.setAllCaps(false);
            map.setBackgroundColor(Color.parseColor("#00000000"));
            map.setGravity(Gravity.CENTER);
            map.setTextColor(Color.BLACK);

            map.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    final LinearLayout parent = (LinearLayout) v.getParent();

                    Intent events = new Intent(getActivity(), ViewEventsActivity.class);
                    events.putExtra("EVENTS_ID", parent.getTag() + "");

                    Log.d("EVENTS_ID", parent.getTag() + "");

                    startActivity(events);
                }
            });

            options.addView(view);
            //options.addView(join);
            options.addView(map);


            recordOfEvents.addView(postedByLayout);
            postedByLayout.addView(eventPostedByImage);

            postedByLayout.addView(postedByDate);
            postedByDate.addView(eventPostedBy);
            postedByDate.addView(eventPostedDate);


            recordOfEvents.addView(eventName);
            recordOfEvents.addView(eventDetails);
            recordOfEvents.addView(eventLocation);
            recordOfEvents.addView(options);

            listOfEvents.addView(recordOfEvents);
        }
        currentEvents.clear();

        Button btnSeeMore = new Button(getActivity());
        btnSeeMore.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        btnSeeMore.setText("see more");
        btnSeeMore.setTextSize(15);
        btnSeeMore.setAllCaps(false);
        btnSeeMore.setGravity(Gravity.CENTER);
        btnSeeMore.setBackgroundColor(Color.parseColor("#00000000"));
        listOfEvents.addView(btnSeeMore);

    }

    public String timeDiff(String timeDiff) {

        Log.d("Time diff", timeDiff);

        String Str = new String(timeDiff);
        String date[] = Str.split(":", 3);
        String time = "";

        if(Integer.parseInt(date[0]) > 24) {
            time = Integer.parseInt(date[0]) / 24 + " days";
        } else if (Integer.parseInt(date[0]) < 24 && Integer.parseInt(date[0]) > 0) {
            time = Integer.parseInt(date[0]) + " hrs";
        } else if (Integer.parseInt(date[0]) == 0 && Integer.parseInt(date[1]) != 0) {
            time = Integer.parseInt(date[1]) + " mins";
        } else if (Integer.parseInt(date[0]) == 0 && Integer.parseInt(date[1]) == 0) {
            time = Integer.parseInt(date[2]) + " secs";
        }

        return time;
    }

    /*
        thread
     */

class RetrieveEvents extends AsyncTask<String, String, String> {

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
            List<NameValuePair> params = new ArrayList<>();

            Log.d("USER_ID (user)", currentUser.getId() + "");
            params.add(new BasicNameValuePair("id", currentUser.getId() + ""));
            params.add(new BasicNameValuePair("filter", "newsfeed"));
            params.add(new BasicNameValuePair("user_id", currentUser.getId() + ""));

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
                    event.setPostedDate(jUserObject.getString("time_diff"));

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
