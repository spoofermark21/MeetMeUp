package practiceandroidapplication.android.com.meetmeup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import practiceandroidapplication.android.com.meetmeup.Entity.Events;
import practiceandroidapplication.android.com.meetmeup.Entity.Group;
import practiceandroidapplication.android.com.meetmeup.Entity.ListLocations;
import practiceandroidapplication.android.com.meetmeup.Entity.ListNationalities;
import practiceandroidapplication.android.com.meetmeup.Entity.Meetups;
import practiceandroidapplication.android.com.meetmeup.Entity.Network;
import practiceandroidapplication.android.com.meetmeup.Entity.Preference;
import practiceandroidapplication.android.com.meetmeup.Entity.Sessions;
import practiceandroidapplication.android.com.meetmeup.Entity.User;
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

    Button btnCreate, btnFind;

    User currentUser = Sessions.getSessionsInstance().currentUser;

    Events events = new Events();
    String location;

    boolean isCreate = false;

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
                //startActivity(new Intent(CreateEventActivity.this, EventsActivity.class));
                finish();
            }
        });

        initUI();
        loadEventType();
        loadLocations();

        try {
            // Loading map
            initilizeMap();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * function to load map. If map is not created it will create it for you
     * */
    private void initilizeMap() {
        if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(
                    R.id.map)).getMap();

            double latitude = 10.342887;
            double longitude = 123.960722;

            CameraPosition cameraPosition = new CameraPosition.Builder().target(
                    new LatLng(latitude, longitude)).zoom(12).build();

            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            /*MarkerOptions marker = new MarkerOptions().position(new LatLng(latitude, longitude)).title("Meetmeup developer home");
            marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

            googleMap.addMarker(marker);*/

            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            googleMap.getUiSettings().setZoomGesturesEnabled(true);
            googleMap.getUiSettings().setCompassEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            googleMap.getUiSettings().setRotateGesturesEnabled(true);
            googleMap.getUiSettings().setTiltGesturesEnabled(true);
            googleMap.getUiSettings().setIndoorLevelPickerEnabled(true);

            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(getApplicationContext(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    public void initUI() {
        txtEventName = (EditText) findViewById(R.id.txt_event_name);
        txtDetails = (EditText) findViewById(R.id.txt_details);
        txtLocation = (EditText) findViewById(R.id.txt_location);
        spnEventType = (Spinner) findViewById(R.id.spn_event_type);
        spnLocation = (Spinner) findViewById(R.id.spn_location);

        startDate = (DatePicker) findViewById(R.id.start_date);
        endDate = (DatePicker) findViewById(R.id.end_date);

        btnFind = (Button) findViewById(R.id.btn_find);
        btnFind.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                location = txtLocation.getText().toString() + ", " + spnLocation.getSelectedItem().toString();

                if (location != null && !location.equals("")) {
                    isCreate = false;
                    new GeocoderTask().execute(location);
                }
            }
        });


        btnCreate = (Button) findViewById(R.id.btn_create);
        btnCreate.setVisibility(View.GONE);
        btnCreate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (validateForm()) {
                    //events = new Events(txtEventName.getText().toString(), txtDetails.getText().toString(),
                    //        txtLocation.getText().toString());
                    try {
                        location = txtLocation.getText().toString() + ", " + spnLocation.getSelectedItem().toString();

                        if (location != null && !location.equals("")) {
                            isCreate = true;
                            new GeocoderTask().execute(location);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    //new CreateEvent().execute();
                }

            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.btn_save);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                if (validateForm()) {
                    //events = new Events(txtEventName.getText().toString(), txtDetails.getText().toString(),
                    //        txtLocation.getText().toString());
                    try {
                        location = txtLocation.getText().toString() + ", " + spnLocation.getSelectedItem().toString();

                        if (location != null && !location.equals("")) {
                            isCreate = true;
                            new GeocoderTask().execute(location);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }

    public void loadEventType(){
        ArrayAdapter<String> adapter;
        String[] eventType = {"Generic","Traditional","Travel", "Personal"};

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



    public boolean validateForm(){
        boolean isReadyToSave = true;

        if(txtEventName.getText().toString().equals("")) {
            txtEventName.setError("Event name is required.");
            isReadyToSave = false;
        } else
            txtEventName.setError(null);

        if(txtLocation.getText().toString().equals("")) {
            txtLocation.setError("Location is required.");
            isReadyToSave = false;
        } else
            txtLocation.setError(null);

        if(txtDetails.getText().toString().equals("")) {
            txtDetails.setError("Details is required.");
            isReadyToSave = false;
        } else
            txtDetails.setError(null);

        return isReadyToSave;
    }


    public void onBackPressed() {
        //startActivity(new Intent(CreateEventActivity.this, EventsActivity.class));
        finish();
    }

    /*
        thread
     */


    private class GeocoderTask extends AsyncTask<String, Void, List<Address>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(CreateEventActivity.this, R.style.progress);
            pDialog.setCancelable(true);
            pDialog.setProgressStyle(android.R.style.Widget_Material_ProgressBar_Large);
            pDialog.setMessage("Searching location...");
            pDialog.show();
        }

        @Override
        protected List<Address> doInBackground(String... locationName) {
            Geocoder geocoder = new Geocoder(getBaseContext());
            List<Address> addresses = null;

            try {
                // Getting a maximum of 3 Address that matches the input text
                addresses = geocoder.getFromLocationName(locationName[0], 3);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return addresses;
        }

        @Override
        protected void onPostExecute(List<Address> addresses) {
            pDialog.dismiss();
            if (addresses == null || addresses.size() == 0) {
                Toast.makeText(CreateEventActivity.this, "No Location found", Toast.LENGTH_SHORT).show();
            } else {

                try {

                    googleMap.clear();

                    Address address = addresses.get(0);

                    latLng = new LatLng(address.getLatitude(), address.getLongitude());

                    String addressText = String.format("%s %s",
                            address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
                            address.getCountryName());

                    CameraPosition cameraPosition = new CameraPosition.Builder().target(
                            new LatLng(address.getLatitude(),
                                    address.getLongitude())).zoom(17).build();

                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                    /*MarkerOptions marker = new MarkerOptions().position(new LatLng(address.getLatitude(),
                            address.getLongitude()));
                    marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

                    googleMap.addMarker(marker);*/

                    markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.title(addressText);

                    googleMap.addMarker(markerOptions);

                    // if btn is click trigger create meetups
                    if(isCreate) {
                        events.setEventName(txtEventName.getText().toString());
                        events.setDetails(txtDetails.getText().toString());
                        events.setLocation(location);

                        events.setStartDate(startDate.getYear() + "-" + startDate.getMonth()
                                + "-" + startDate.getDayOfMonth());
                        events.setEndDate(endDate.getYear() + "-" + endDate.getMonth()
                                + "-" + endDate.getDayOfMonth());

                        events.setEventType(spnEventType.getSelectedItem().toString().charAt(0));

                        events.setLattitude(address.getLatitude());
                        events.setLongtitude(address.getLongitude());

                        new CreateEvent().execute();
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                } // end of try catch

            }

        }
    }

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
                params.add(new BasicNameValuePair("event_type", events.getEventType()+""));
                params.add(new BasicNameValuePair("start_date", events.getStartDate()));
                params.add(new BasicNameValuePair("end_date", events.getEndDate()));

                params.add(new BasicNameValuePair("lattitude", events.getLattitude() + ""));
                params.add(new BasicNameValuePair("longtitude", events.getLongtitude() + ""));


                Log.d("request!", "starting");

                JSONObject json = jsonParser.makeHttpRequest(
                        CREATE_EVENT_URL, "POST", params);

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
                    Toast.makeText(CreateEventActivity.this, message + "!", Toast.LENGTH_SHORT).show();

                    new Thread() {
                        public void run() {
                            try {
                                sleep(1000);
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
