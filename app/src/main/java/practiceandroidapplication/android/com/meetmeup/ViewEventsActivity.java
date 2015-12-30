package practiceandroidapplication.android.com.meetmeup;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
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
import practiceandroidapplication.android.com.meetmeup.Entity.ListNationalities;
import practiceandroidapplication.android.com.meetmeup.Entity.Network;
import practiceandroidapplication.android.com.meetmeup.Entity.Sessions;
import practiceandroidapplication.android.com.meetmeup.Entity.User;
import practiceandroidapplication.android.com.meetmeup.Handles.Interactions;
import practiceandroidapplication.android.com.meetmeup.Handles.JSONParser;

public class ViewEventsActivity extends AppCompatActivity {

    private static final String RETRIEVE_EVENT_URL = Network.forDeploymentIp + "events_retrieve.php";
    private static final String UPDATE_EVENT_URL = Network.forDeploymentIp + "event_save.php";

    private static final String TAG_STATUS = "status";
    private static final String TAG_RESPONSE = "response";

    JSONParser jsonParser = new JSONParser();
    private ProgressDialog pDialog;

    Toolbar toolBar;

    EditText txtEventName, txtLocation, txtDetails;
    Spinner spnEventType;
    DatePicker startDate, endDate;

    Button btnCreate;//, btnDisable;

    Sessions sessions = Sessions.getSessionsInstance();

    List<Group> currentGroups = Sessions.getSessionsInstance().currentGroups;

    User currentUser = Sessions.getSessionsInstance().currentUser;

    Events events = new Events();
    String[] eventType = {"Traditional", "Personal", "Blah"};

    String currentEventKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_events);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        toolBar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ViewEventsActivity.this, EventsActivity.class));
                finish();
            }
        });

        initUI();
        loadEventType();

        //init
        Intent intent = getIntent();
        currentEventKey = intent.getStringExtra("EVENT_KEY");

        //fetch
        new RetrieveEvent().execute();

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

        btnCreate = (Button) findViewById(R.id.btn_create);
        btnCreate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (validateForm()) {
                    //events = new Events(txtEventName.getText().toString(), txtDetails.getText().toString(),
                    //        txtLocation.getText().toString());
                    try {
                        events.setEventName(txtEventName.getText().toString());
                        events.setDetails(txtDetails.getText().toString());
                        events.setLocation(txtLocation.getText().toString());
                        events.setStartDate(startDate.getMonth() + "-" + startDate.getMonth()
                                + "-" + startDate.getDayOfMonth());
                        events.setEndDate(endDate.getMonth() + "-" + endDate.getMonth()
                                + "-" + endDate.getDayOfMonth());

                        events.setEventType(spnEventType.getSelectedItem().toString().charAt(0));

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    //new UpdateEvent().execute();
                }

            }
        });
        /*btnDisable = (Button) findViewById(R.id.btn_disable);
        btnDisable.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {

                if (events.getEventType() != 'A') {
                    if (Interactions.showQuestion("Are you sure to disable this event?",
                            ViewEventsActivity.this)) {
                        // update
                        Log.d("QUESTION","true");
                    }
                } else {
                    if (Interactions.showQuestion("Are you sure to enable this event?",
                            ViewEventsActivity.this)) {
                        // update
                        Log.d("QUESTION","false");
                    }
                }

            }
        });*/
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
        for(String eventString : eventType) {
            if(eventString.charAt(0) == events.getEventType()) {
                break;
            }
            index += 1;
        }
        return index;
    }

    public void onBackPressed() {
        startActivity(new Intent(ViewEventsActivity.this, EventsActivity.class));
        finish();
    }

    /*
        thread
     */


    class RetrieveEvent extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ViewEventsActivity.this, R.style.progress);
            pDialog.setCancelable(false);
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

                Log.d("EVENT_KEY (event)", currentEventKey);

                params.add(new BasicNameValuePair("id", currentEventKey));
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
                    events.setDetails(jUserObject.getString("location"));
                    events.setLocation(jUserObject.getString("details"));
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
                    txtEventName.setText(events.getEventName());
                    txtDetails.setText(events.getDetails());
                    txtLocation.setText(events.getLocation());

                    spnEventType.setSelection(selectEventType());

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
            pDialog = new ProgressDialog(ViewEventsActivity.this, R.style.progress);
            pDialog.setCancelable(false);
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

                Log.d("USER_ID (user)", currentUser.getId() + "");

                params.add(new BasicNameValuePair("user_id", currentUser.getId()+""));
                params.add(new BasicNameValuePair("event_name", events.getEventName()));
                params.add(new BasicNameValuePair("details", events.getDetails()));
                params.add(new BasicNameValuePair("location", events.getLocation()));
                params.add(new BasicNameValuePair("type", events.getEventType() + ""));
                params.add(new BasicNameValuePair("start_date", events.getStartDate()));
                params.add(new BasicNameValuePair("end_date", events.getEndDate()));

                Log.d("request!", "starting");

                JSONObject json = jsonParser.makeHttpRequest(
                        RETRIEVE_EVENT_URL, "POST", params);

                Log.d("Saving...", json.toString());

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
                    Toast.makeText(ViewEventsActivity.this, message + "!", Toast.LENGTH_SHORT).show();

                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
