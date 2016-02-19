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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import practiceandroidapplication.android.com.meetmeup.Entity.Events;
import practiceandroidapplication.android.com.meetmeup.Entity.ListLocations;
import practiceandroidapplication.android.com.meetmeup.Entity.Network;
import practiceandroidapplication.android.com.meetmeup.Entity.Sessions;
import practiceandroidapplication.android.com.meetmeup.Entity.User;
import practiceandroidapplication.android.com.meetmeup.Handles.Interactions;
import practiceandroidapplication.android.com.meetmeup.Handles.JSONParser;

public class CreateEventActivity extends AppCompatActivity {

    private static final String CREATE_EVENT_URL = Network.forDeploymentIp + "event_save.php";
    private static final String TAG_STATUS = "status";
    private static final String TAG_RESPONSE = "response";

    JSONParser jsonParser = new JSONParser();
    private ProgressDialog pDialog;

    private GoogleMap googleMap;
    MarkerOptions markerOptions;

    LatLng latLng;


    Toolbar toolBar;

    EditText txtEventName, txtLocation, txtDetails;
    Spinner spnEventType, spnLocation;
    DatePicker startDate, endDate;

    Button btnCreate, btnSetMapLocation;

    User currentUser = Sessions.getSessionsInstance().currentUser;

    Events events = new Events();
    String location;

    boolean isCreate = false;
    boolean isSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        toolBar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CreateEventActivity.this, EventsActivity.class));
                finish();
            }
        });

        initUI();
        loadEventType();
        loadLocations();
        //set cebu as default
        spnLocation.setSelection(16);
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
            if (validateForm()) {
                location = txtLocation.getText().toString() + ", " + spnLocation.getSelectedItem().toString();

                Interactions.showError("Lat " + Sessions.currentLocationLatitude + " Long " +
                        Sessions.currentLocationLongtitude, CreateEventActivity.this);

                if (Sessions.currentLocationLatitude != 0 && Sessions.currentLocationLongtitude != 0) {
                    if (validateForm()) {
                        try {
                            location = txtLocation.getText().toString() + ", " + spnLocation.getSelectedItem().toString();

                            events.setEventName(txtEventName.getText().toString());
                            events.setDetails(txtDetails.getText().toString());
                            events.setLocation(location);

                            events.setStartDate(startDate.getYear() + "-" + (startDate.getMonth() + 1)
                                    + "-" + startDate.getDayOfMonth());
                            events.setEndDate(endDate.getYear() + "-" + (endDate.getMonth() + 1)
                                    + "-" + endDate.getDayOfMonth());

                            events.setEventType(spnEventType.getSelectedItem().toString().charAt(0));

                            //events.setLattitude(address.getLatitude());
                            //events.setLongtitude(address.getLongitude());

                            AlertDialog.Builder dlgAlert = new AlertDialog.Builder(CreateEventActivity.this);
                            dlgAlert.setMessage("Create as");
                            dlgAlert.setCancelable(true);

                            dlgAlert.setPositiveButton("Your account",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            isSet = true;
                                        }
                                    });

                            dlgAlert.setNegativeButton("Your group",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            isSet = true;
                                        }
                                    });

                            dlgAlert.create().show();


                            new CreateEvent().execute();

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                        //new CreateEvent().execute();
                    }
                } else {
                    Interactions.showError("Please set a map location.", CreateEventActivity.this);
                }
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public void initUI() {
        txtEventName = (EditText) findViewById(R.id.txt_event_name);
        txtDetails = (EditText) findViewById(R.id.txt_details);
        txtLocation = (EditText) findViewById(R.id.txt_location);
        spnEventType = (Spinner) findViewById(R.id.spn_event_type);
        spnLocation = (Spinner) findViewById(R.id.spn_location);

        startDate = (DatePicker) findViewById(R.id.start_date);
        startDate.setMinDate(System.currentTimeMillis() - 1000);

        endDate = (DatePicker) findViewById(R.id.end_date);
        endDate.setMinDate(System.currentTimeMillis() - 1000);

        btnSetMapLocation = (Button) findViewById(R.id.btn_set_map);

        btnSetMapLocation.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {

                        if (!txtLocation.getText().toString().trim().equals("")) {
                            Intent intent = new Intent(CreateEventActivity.this, MapsActivity.class);
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

    public void loadEventType() {
        ArrayAdapter<String> adapter;
        String[] eventType = {"Generic", "Traditional", "Travel", "Personal"};

        adapter = new ArrayAdapter<>(getApplicationContext(),
                R.layout.spinner_layout, eventType);
        adapter.setDropDownViewResource(R.layout.spinner_layout);
        spnEventType.setAdapter(adapter);
    }

    public void loadLocations() {
        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<>(getApplicationContext(),
                R.layout.spinner_layout, ListLocations.getInstanceListLocations().loadLocations());
        adapter.setDropDownViewResource(R.layout.spinner_layout);
        spnLocation.setAdapter(adapter);
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


    public void onBackPressed() {
        startActivity(new Intent(CreateEventActivity.this, EventsActivity.class));
        finish();
    }

    /*
        thread
     */


    class CreateEvent extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(CreateEventActivity.this, R.style.progress);
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

                Log.d("USER_ID (user)", currentUser.getId() + "");

                params.add(new BasicNameValuePair("user_id", currentUser.getId() + ""));
                params.add(new BasicNameValuePair("event_name", events.getEventName()));
                params.add(new BasicNameValuePair("details", events.getDetails()));
                params.add(new BasicNameValuePair("location", events.getLocation()));
                params.add(new BasicNameValuePair("event_type", events.getEventType() + ""));
                params.add(new BasicNameValuePair("start_date", events.getStartDate()));
                params.add(new BasicNameValuePair("end_date", events.getEndDate()));

                params.add(new BasicNameValuePair("lattitude", Sessions.currentLocationLatitude + ""));
                params.add(new BasicNameValuePair("longtitude", Sessions.currentLocationLongtitude + ""));


                Log.d("request!", "starting");

                JSONObject json = jsonParser.makeHttpRequest(
                        CREATE_EVENT_URL, "POST", params);

                Log.d("Saving...", json.toString());

                success = json.getInt(TAG_STATUS);

                if (success == 1) {
                    Log.d("Success!", json.toString());

                    return json.getString(TAG_RESPONSE);
                } else {
                    isCreate = false;
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
                    Toast.makeText(CreateEventActivity.this, message + "!", Toast.LENGTH_SHORT).show();

                    new Thread() {
                        public void run() {
                            try {
                                sleep(100);
                                startActivity(new Intent(CreateEventActivity.this, EventsActivity.class));
                                finish();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }.start();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
