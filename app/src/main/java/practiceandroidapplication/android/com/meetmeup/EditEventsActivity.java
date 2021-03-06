package practiceandroidapplication.android.com.meetmeup;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import practiceandroidapplication.android.com.meetmeup.Entity.Events;
import practiceandroidapplication.android.com.meetmeup.Entity.Group;
import practiceandroidapplication.android.com.meetmeup.Entity.ListLocations;
import practiceandroidapplication.android.com.meetmeup.Entity.Location;
import practiceandroidapplication.android.com.meetmeup.Entity.Meetups;
import practiceandroidapplication.android.com.meetmeup.Entity.Network;
import practiceandroidapplication.android.com.meetmeup.Entity.Preference;
import practiceandroidapplication.android.com.meetmeup.Entity.Sessions;
import practiceandroidapplication.android.com.meetmeup.Entity.User;
import practiceandroidapplication.android.com.meetmeup.Handles.Interactions;
import practiceandroidapplication.android.com.meetmeup.Handles.JSONParser;

public class EditEventsActivity extends AppCompatActivity {

    private static final String RETRIEVE_EVENT_URL = Network.forDeploymentIp + "events_retrieve.php";
    private static final String UPDATE_EVENT_URL = Network.forDeploymentIp + "event_update.php";

    private static final String TAG_STATUS = "status";
    private static final String TAG_RESPONSE = "response";

    JSONParser jsonParser = new JSONParser();
    private ProgressDialog pDialog;

    Toolbar toolBar;

    EditText txtEventName, txtLocation, txtDetails;
    Spinner spnEventType, spnLocation;
    DatePicker startDate, endDate;

    Button btnCreate, btnSetMapLocation;

    ScrollView scrollView;

    public static List<Group> listOfGroups = new ArrayList<>();

    Sessions sessions = Sessions.getSessionsInstance();

    List<Group> currentGroups = Sessions.getSessionsInstance().currentGroups;

    User currentUser = Sessions.getSessionsInstance().currentUser;

    Events events = new Events();
    String[] eventType = {"Traditional", "Personal", "Blah"};

    boolean isCreate = false;
    boolean hasSetGroup = false;
    char createdBy = 'A';

    String currentEventId;
    int selectedGroup;

    String location;
    Group group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_events);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        toolBar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EditEventsActivity.this, EventsActivity.class));
                finish();
            }
        });

        initUI();
        loadEventType();
        loadLocations();
        //init
        Intent intent = getIntent();
        currentEventId = intent.getStringExtra("EVENT_ID");

        //fetch
        new RetrieveEvent().execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.only_save_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_save) {
            if (validateForm() ) {
                //events = new Events(txtEventName.getText().toString(), txtDetails.getText().toString(),
                //        txtLocation.getText().toString());
                try {
                    location = txtLocation.getText().toString() + ", " + spnLocation.getSelectedItem().toString();

                    events.setEventName(txtEventName.getText().toString());
                    events.setDetails(txtDetails.getText().toString());
                    events.setLocation(location);

                    events.setStartDate(startDate.getYear() + "-" + (startDate.getMonth() + 1)
                            + "-" + startDate.getDayOfMonth());

                    events.setEndDate(endDate.getYear() + "-" + (endDate.getMonth() + 1)
                            + "-" + endDate.getDayOfMonth());

                    Log.d("Date", events.getStartDate() + " " + events.getEndDate() + 1);

                    events.setEventType(spnEventType.getSelectedItem().toString().charAt(0));

                    new UpdateEvent().execute();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                //new UpdateEvent().execute();
            }

        }

        return super.onOptionsItemSelected(item);
    }

    /*
        functions
     */

    public void initUI() {
        txtEventName = (EditText) findViewById(R.id.txt_event_name);
        txtDetails = (EditText) findViewById(R.id.txt_details);
        txtLocation = (EditText) findViewById(R.id.txt_location);
        spnEventType = (Spinner) findViewById(R.id.spn_event_type);
        startDate = (DatePicker) findViewById(R.id.start_date);
        endDate = (DatePicker) findViewById(R.id.end_date);

        scrollView = (ScrollView) findViewById(R.id.scroll_view);
        scrollView.setVisibility(View.INVISIBLE);

        spnLocation = (Spinner) findViewById(R.id.spn_location);

        /*btnUpdate = (Button) findViewById(R.id.btn_create);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

            }
        });
        btnDisable = (Button) findViewById(R.id.btn_disable);
        btnDisable.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {

                if (events.getEventType() != 'A') {
                    if (Interactions.showQuestion("Are you sure to disable this event?",
                            ViewEventsActivity1.this)) {
                        // update
                        Log.d("QUESTION","true");
                    }
                } else {
                    if (Interactions.showQuestion("Are you sure to enable this event?",
                            ViewEventsActivity1.this)) {
                        // update
                        Log.d("QUESTION","false");
                    }
                }

            }
        });*/

        btnSetMapLocation = (Button) findViewById(R.id.btn_set_map);

        btnSetMapLocation.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {

                        if (!txtLocation.getText().toString().trim().equals("")) {
                            Intent intent = new Intent(EditEventsActivity.this, MapsActivity.class);
                            intent.putExtra("TYPE", "creation");

                            if (!spnLocation.getSelectedItem().toString().contains("Cebu")) {
                                intent.putExtra("LOCATION", txtLocation.getText().toString() + ", "
                                        + spnLocation.getSelectedItem().toString() + ", Cebu");
                            } else {
                                intent.putExtra("LOCATION", txtLocation.getText().toString()
                                        + ", " + spnLocation.getSelectedItem().toString());
                            }
                            startActivity(intent);
                            txtLocation.setError(null);
                        } else {
                            txtLocation.setError("Please set a location first");
                        }


                    }
                }

        );
    }


    public void loadLocations() {
        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<>(getApplicationContext(),
                R.layout.spinner_layout, ListLocations.getInstanceListLocations().loadLocations());
        adapter.setDropDownViewResource(R.layout.spinner_layout);
        spnLocation.setAdapter(adapter);
    }

    public static CharSequence[] loadGroups() {
        List<String> list = new ArrayList<>();

        for (Group group : listOfGroups) {
            Log.d(group.getId() + " ", group.getGroupName());
            list.add(group.getGroupName());
        }
        listOfGroups.clear();

        return list.toArray(new CharSequence[list.size()]);
    }

    public void loadEventType() {
        ArrayAdapter<String> adapter;

        adapter = new ArrayAdapter<>(getApplicationContext(),
                R.layout.spinner_layout, eventType);
        adapter.setDropDownViewResource(R.layout.spinner_layout);
        spnEventType.setAdapter(adapter);
    }

    public boolean validateForm() {
        boolean isReadyToSave = true;

        if (txtEventName.getText().toString().equals("")) {
            txtEventName.setError("Event name is required.");
            isReadyToSave = false;
        } else
            txtEventName.setError(null);

        if (txtLocation.getText().toString().equals("")) {
            txtLocation.setError("Location is required.");
            isReadyToSave = false;
        } else
            txtLocation.setError(null);

        if (txtDetails.getText().toString().equals("")) {
            txtDetails.setError("Details is required.");
            isReadyToSave = false;
        } else
            txtDetails.setError(null);

        return isReadyToSave;
    }

    public int selectEventType() {
        int index = 0;
        for (String eventString : eventType) {
            if (eventString.charAt(0) == events.getEventType()) {
                break;
            }
            index += 1;
        }
        return index;
    }

    public void onBackPressed() {
        startActivity(new Intent(EditEventsActivity.this, EventsActivity.class));
        finish();
    }

    /*
        thread
     */


    class RetrieveEvent extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EditEventsActivity.this, R.style.progress);
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
                List<NameValuePair> params = new ArrayList<>();

                Log.d("EVENT_ID (event)", currentEventId);

                params.add(new BasicNameValuePair("id", currentEventId));
                params.add(new BasicNameValuePair("filter", "individual"));


                Log.d("request!", "starting");

                JSONObject json = jsonParser.makeHttpRequest(
                        RETRIEVE_EVENT_URL, "POST", params);

                Log.d("Fetching...", json.toString());


                success = json.getInt(TAG_STATUS);

                if (success == 1) {
                    Log.d("Success!", json.toString());

                    JSONArray jUserArray = json.getJSONArray("events");
                    JSONObject jUserObject = jUserArray.getJSONObject(0);

                    events.setEventName(jUserObject.getString("event_name"));
                    events.setDetails(jUserObject.getString("details"));

                    String loc = jUserObject.getString("location");

                    for(Location location : ListLocations.getInstanceListLocations().locations) {
                        loc = loc.replaceAll(", " + location.getLocation(), "");
                    }

                    events.setLocation(loc);
                    // set date
                    events.setStartDate(jUserObject.getString("start_date"));
                    events.setEndDate(jUserObject.getString("end_date"));


                    events.setEventType(jUserObject.getString("event_type").charAt(0));
                    events.setKey(jUserObject.getString("key"));

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
                if (message.equals("Successful")) {

                    scrollView.setVisibility(View.VISIBLE);

                    txtEventName.setText(events.getEventName());
                    txtDetails.setText(events.getDetails());

                    txtLocation.setText(events.getLocation());

                    spnEventType.setSelection(selectEventType());

                    String start = new String(events.getStartDate());
                    String dateStart[] = start.split("-", 3);

                    startDate.updateDate(Integer.parseInt(dateStart[0]),
                            Integer.parseInt(dateStart[1]) - 1, Integer.parseInt(dateStart[2]));

                    String end = new String(events.getEndDate());
                    String dateEnd[] = end.split("-", 3);

                    endDate.updateDate(Integer.parseInt(dateEnd[0]),
                            Integer.parseInt(dateEnd[1]) - 1, Integer.parseInt(dateEnd[2]));

                    //String btnDisableText = events.getEventType() == 'A' ? "Disable" : "Enable";
                    //btnDisable.setText(btnDisableText);

                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    class UpdateEvent extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EditEventsActivity.this, R.style.progress);
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
                List<NameValuePair> params = new ArrayList<NameValuePair>();

                Log.d("EVENT ID", currentEventId);

                params.add(new BasicNameValuePair("id", currentEventId));
                params.add(new BasicNameValuePair("event_name", events.getEventName()));
                params.add(new BasicNameValuePair("details", events.getDetails()));
                params.add(new BasicNameValuePair("location", events.getLocation()));
                params.add(new BasicNameValuePair("type", events.getEventType() + ""));
                params.add(new BasicNameValuePair("start_date", events.getStartDate()));
                params.add(new BasicNameValuePair("end_date", events.getEndDate()));

                params.add(new BasicNameValuePair("lattitude", Sessions.currentLocationLatitude + ""));
                params.add(new BasicNameValuePair("longtitude", Sessions.currentLocationLongtitude + ""));

                params.add(new BasicNameValuePair("query_type", "update"));

                Log.d("request!", "starting");

                JSONObject json = jsonParser.makeHttpRequest(
                        UPDATE_EVENT_URL, "POST", params);

                Log.d("Updating...", json.toString());

                success = json.getInt(TAG_STATUS);

                if (success == 1) {
                    Log.d("Success!", json.toString());

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
                if (message.equals("Successful")) {
                    Toast.makeText(EditEventsActivity.this, message + "!", Toast.LENGTH_SHORT).show();

                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
