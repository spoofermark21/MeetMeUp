package practiceandroidapplication.android.com.meetmeup;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
import practiceandroidapplication.android.com.meetmeup.Entity.Network;
import practiceandroidapplication.android.com.meetmeup.Entity.Sessions;
import practiceandroidapplication.android.com.meetmeup.Entity.User;
import practiceandroidapplication.android.com.meetmeup.Handles.JSONParser;

public class EventsActivity extends AppCompatActivity {

    private static final String RETRIEVE_EVENTS_URL = Network.forDeploymentIp + "events_retrieve.php";
    private static final String UPDATE_EVENT_URL = Network.forDeploymentIp + "event_update.php";
    private static final String LEAVE_URL = Network.forDeploymentIp + "leave.php";

    private static final String TAG_STATUS = "status";
    private static final String TAG_RESPONSE = "response";

    JSONParser jsonParser = new JSONParser();
    ProgressDialog pDialog;

    Toolbar toolbar;
    LinearLayout listOfEvents;
    TextView lblMessage;

    Sessions sessions = Sessions.getSessionsInstance();
    List<Events> currentEvents = new ArrayList<>();

    User currentUser = Sessions.getSessionsInstance().currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //listEvents = (ListView) findViewById(R.id.list_events);
        listOfEvents = (LinearLayout) findViewById(R.id.linear_events);
        listOfEvents.setVisibility(View.INVISIBLE);
        lblMessage = (TextView) findViewById(R.id.lbl_message);
        lblMessage.setVisibility(View.INVISIBLE);

        new RetrieveEvents().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.group_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_create) {
            startActivity(new Intent(EventsActivity.this, CreateEventActivity.class));
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void displayEvents() {

        for (Events event : currentEvents) {

            LinearLayout recordOfEvents = new LinearLayout(this);
            recordOfEvents.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            recordOfEvents.setOrientation(LinearLayout.VERTICAL);
            recordOfEvents.setPadding(10, 10, 10, 10);
            //recordOfEvents.setBackgroundColor(getResources().getColor(R.color.colorMainBackground));
            //recordOfEvents.setBackgroundResource(R.drawable.edit_text);

            Log.d("Event name", event.getEventName());
            recordOfEvents.setTag(event.getId());

            final TextView eventName = new TextView(this);
            eventName.setText(event.getEventName());
            eventName.setTextSize(20);
            eventName.setTextColor(Color.BLACK);
            eventName.setTag(event.getLattitude());

            final TextView eventDetails = new TextView(this);
            eventDetails.setText("Details: " + event.getDetails());
            eventDetails.setTextSize(15);
            eventDetails.setTextColor(Color.BLACK);
            eventDetails.setTag(event.getLongtitude());

            final TextView eventKey = new TextView(this);
            eventKey.setText("Key: " + event.getKey());
            eventKey.setTextSize(15);
            eventKey.setTextColor(Color.BLACK);

            final TextView eventLocation = new TextView(this);
            eventLocation.setText("Location: " + event.getLocation());
            eventLocation.setTextSize(15);
            eventLocation.setTextColor(Color.BLACK);

            final TextView startDate = new TextView(this);
            startDate.setText("Start Date: " + event.getEndDate());
            startDate.setTextSize(15);
            startDate.setTextColor(Color.BLACK);

            final TextView endDate = new TextView(this);
            endDate.setText("End date: " + event.getEndDate());
            endDate.setTextSize(15);
            endDate.setTextColor(Color.BLACK);

            final TextView eventPostedBy = new TextView(this);
            eventPostedBy.setText("Posted by: " + event.getPostedByName());
            eventPostedBy.setTextSize(15);
            eventPostedBy.setTextColor(Color.BLACK);

            final LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
            params2.weight = 1.0f;
            params2.gravity = Gravity.RIGHT;
            params2.leftMargin = 50;

            LinearLayout options = new LinearLayout(this);
            options.setOrientation(LinearLayout.HORIZONTAL);
            options.setPadding(10, 10, 10, 10);
            options.setLayoutParams(params2);

            final LinearLayout.LayoutParams btnLayout = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
            btnLayout.weight = 1.0f;
            btnLayout.setMargins(10, 10, 10, 10);

            final TextView view = new TextView(this);
            view.setLayoutParams(btnLayout);
            view.setPadding(10, 10, 0, 10);
            view.setText("view");
            view.setTextSize(15);
            view.setBackgroundColor(Color.TRANSPARENT);

            final TextView edit = new TextView(this);
            edit.setLayoutParams(btnLayout);
            edit.setPadding(10, 10, 0, 10);
            edit.setText("edit");
            edit.setTextSize(15);
            edit.setBackgroundColor(Color.TRANSPARENT);


            final TextView delete = new TextView(this);
            delete.setLayoutParams(btnLayout);
            delete.setPadding(10, 10, 0, 10);
            delete.setText("delete");
            delete.setTextSize(15);
            delete.setBackgroundColor(Color.TRANSPARENT);
            delete.setTextColor(Color.parseColor("#D46A6A"));

            final TextView leave = new TextView(this);
            leave.setLayoutParams(btnLayout);
            leave.setPadding(10, 10, 0, 10);
            leave.setText("leave");
            leave.setTextSize(15);
            leave.setGravity(Gravity.CENTER);
            leave.setBackgroundColor(Color.TRANSPARENT);
            leave.setTextColor(Color.parseColor("#D46A6A"));


            final TextView map = new TextView(this);
            map.setLayoutParams(btnLayout);
            map.setPadding(10, 10, 0, 10);
            map.setText("map");
            map.setTextSize(15);
            map.setGravity(Gravity.CENTER);
            map.setBackgroundColor(Color.TRANSPARENT);

            map.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    final LinearLayout parent = (LinearLayout) v.getParent().getParent();

                    Log.d("Location", parent.getChildAt(0) + " " + parent.getChildAt(1));

                    Intent map = new Intent(EventsActivity.this, ViewMapsActivity.class);
                    map.putExtra("LATTITUDE", parent.getChildAt(0).getTag() + "");
                    map.putExtra("LONGTITUDE", parent.getChildAt(1).getTag() + "");

                    startActivity(map);
                    /*Toast.makeText(EventsActivity.this, parent.getTag() + "! "
                            , Toast.LENGTH_SHORT).show();*/
                }
            });


            view.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    final LinearLayout parent = (LinearLayout) v.getParent().getParent();

                    Intent event = new Intent(EventsActivity.this, ViewEventsActivity.class);
                    event.putExtra("EVENTS_ID", parent.getTag() + "");
                    startActivity(event);
                }
            });

            edit.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    final LinearLayout parent = (LinearLayout) v.getParent().getParent();

                    final TextView name = (TextView) parent.getChildAt(0);
                    //final TextView details = (TextView) parent.getChildAt(1);
                    final TextView key = (TextView) parent.getChildAt(2);

                    String eventId = parent.getTag() + "";

                    Intent event = new Intent(EventsActivity.this, EditEventsActivity.class);
                    event.putExtra("EVENT_ID", eventId);
                    startActivity(event);

                    Toast.makeText(EventsActivity.this, name.getText().toString() + "! "
                            + key.getText().toString(), Toast.LENGTH_SHORT).show();

                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {

                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(EventsActivity.this);
                    dlgAlert.setMessage("Are you sure to delete this event?");
                    dlgAlert.setTitle("Warning!");
                    dlgAlert.setCancelable(true);

                    dlgAlert.setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(EventsActivity.this, "Successful!", Toast.LENGTH_SHORT).show();

                                    final LinearLayout parent = (LinearLayout) v.getParent().getParent();
                                    //final TextView key = (TextView) parent.getChildAt(2);
                                    String removeId = parent.getTag() + "";

                                    listOfEvents.removeView(parent);
                                    new DisableEvent().execute(removeId);

                                    if (listOfEvents.getChildCount() == 1)
                                        lblMessage.setVisibility(View.VISIBLE);
                                    else
                                        lblMessage.setVisibility(View.GONE);

                                }
                            });

                    dlgAlert.setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //do nothing
                                }
                            });

                    dlgAlert.create().show();

                }
            });

            leave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {

                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(EventsActivity.this);
                    dlgAlert.setMessage("Are you sure to leave this Meetup?");
                    dlgAlert.setTitle("Warning!");
                    dlgAlert.setCancelable(true);
                    dlgAlert.setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(EventsActivity.this, "Successful!", Toast.LENGTH_SHORT).show();

                                    final LinearLayout parent = (LinearLayout) v.getParent().getParent();

                                    String eventId = parent.getTag() + "";

                                    listOfEvents.removeView(parent);

                                    new LeaveEvents(eventId, currentUser.getId() + "").execute();

                                    if (listOfEvents.getChildCount() == 1)
                                        lblMessage.setVisibility(View.VISIBLE);
                                    else
                                        lblMessage.setVisibility(View.GONE);
                                }
                            });

                    dlgAlert.setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });

                    dlgAlert.create().show();

                }
            });

            options.addView(map);
            options.addView(view);

            recordOfEvents.addView(eventName);
            recordOfEvents.addView(eventDetails);
            recordOfEvents.addView(eventKey);
            recordOfEvents.addView(eventLocation);
            recordOfEvents.addView(startDate);
            recordOfEvents.addView(endDate);

            if(event.getPostedBy() == currentUser.getId()) {
                options.addView(edit);
                options.addView(delete);
            } else {
                options.addView(leave);
                recordOfEvents.addView(eventPostedBy);
            }

            recordOfEvents.addView(options);

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
            pDialog = new ProgressDialog(EventsActivity.this, R.style.progress);
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


                    currentEvents.clear();

                    for (int i = 0; i < jUserArray.length(); i++) {
                        jUserObject = jUserArray.getJSONObject(i);

                        Events addEvent = new Events(jUserObject.getInt("id"), jUserObject.getString("event_name"),
                                jUserObject.getString("details"), jUserObject.getString("location"), jUserObject.getString("key"),
                                jUserObject.getString("start_date"), jUserObject.getString("end_date"), jUserObject.getInt("posted_by"),
                                jUserObject.getString("posted_by_user"));

                        addEvent.setLattitude(jUserObject.getDouble("lattitude"));
                        addEvent.setLongtitude(jUserObject.getDouble("longtitude"));

                        currentEvents.add(addEvent);


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
                listOfEvents.setVisibility(View.VISIBLE);
                if (message.equals("Successful")) {
                    Toast.makeText(EventsActivity.this, message + "!", Toast.LENGTH_SHORT).show();
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

    class DisableEvent extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EventsActivity.this, R.style.progress);
            pDialog.setCancelable(true);
            pDialog.setProgressStyle(android.R.style.Widget_Material_ProgressBar_Large);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... eventInfo) {
            // TODO Auto-generated method stub

            int success;

            try {
                // Building Parameters
                Log.d("KEY", eventInfo[0]);
                List<NameValuePair> params = new ArrayList<NameValuePair>();

                params.add(new BasicNameValuePair("id", eventInfo[0]));
                params.add(new BasicNameValuePair("query_type", "disable"));

                Log.d("request!", "starting");

                JSONObject json = jsonParser.makeHttpRequest(
                        UPDATE_EVENT_URL, "POST", params);

                Log.d("Updating...", json.toString());

                success = json.getInt(TAG_STATUS);

                if (success == 1) {
                    Log.d("Success!", json.toString());

                    return json.getString(TAG_RESPONSE);
                } else {
                    Log.d("Update failed...", json.getString(TAG_RESPONSE));
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
                if (message.equals("Successful")) {
                    Toast.makeText(EventsActivity.this, message + "!", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    class LeaveEvents extends AsyncTask<String, String, String> {

        String eventId;
        String userId;

        public LeaveEvents(String eventId, String userId) {
            this.eventId = eventId;
            this.userId = userId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EventsActivity.this, R.style.progress);
            pDialog.setCancelable(true);
            pDialog.setProgressStyle(android.R.style.Widget_Material_ProgressBar_Large);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... meetupInfo) {
            // TODO Auto-generated method stub

            int success;

            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<>();

                params.add(new BasicNameValuePair("id", eventId));
                params.add(new BasicNameValuePair("user_id", userId));
                params.add(new BasicNameValuePair("query_type", "attendees"));
                params.add(new BasicNameValuePair("type", "E"));

                Log.d("Leave details", "Meetup id " + eventId + " User id " + userId);

                Log.d("request!", "starting");

                JSONObject json = jsonParser.makeHttpRequest(
                        LEAVE_URL, "POST", params);

                Log.d("Updating...", json.toString());

                success = json.getInt(TAG_STATUS);

                if (success == 1) {
                    Log.d("Success!", json.toString());

                    return json.getString(TAG_RESPONSE);
                } else {
                    Log.d("Update failed...", json.getString(TAG_RESPONSE));
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
                if (message.equals("Successful")) {
                    Toast.makeText(EventsActivity.this, message + "!", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

}


