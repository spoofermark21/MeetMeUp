package practiceandroidapplication.android.com.meetmeup;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.opengl.GLException;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

import practiceandroidapplication.android.com.meetmeup.Entity.Meetups;
import practiceandroidapplication.android.com.meetmeup.Entity.Preference;
import practiceandroidapplication.android.com.meetmeup.Entity.Sessions;
import practiceandroidapplication.android.com.meetmeup.Handles.Interactions;

public class MapsActivity extends AppCompatActivity {

    private GoogleMap googleMap;
    MarkerOptions markerOptions;

    Button btnFind;
    EditText etLocation;

    LatLng latLng;

    String type;
    String location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Sessions.currentLocationLatitude = 0;
                Sessions.currentLocationLongtitude = 0;
                finish();
            }
        });

        type = getIntent().getStringExtra("TYPE");
        //reset the sessions
        Sessions.currentLocationLatitude = 0;
        Sessions.currentLocationLongtitude = 0;

        try {
            btnFind = (Button) findViewById(R.id.btn_find);
            etLocation = (EditText) findViewById(R.id.et_location);

            btnFind.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {

                    String location = etLocation.getText().toString();

                    if (location != null && !location.equals("")) {
                        new GeocoderTask().execute(location);
                    }
                }
            });

            // Loading map
            initilizeMap();

            if (type.equals("creation")) {
                location = getIntent().getStringExtra("LOCATION");
                etLocation.setText(location);
                new GeocoderTask().execute(location);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.only_save_menu, menu);
        MenuItem item = menu.findItem(R.id.action_save);

        if (!type.equals("creation")) {
            item.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_save) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed() {
        Sessions.currentLocationLatitude = 0;
        Sessions.currentLocationLongtitude = 0;
        finish();
    }

    private void initilizeMap() {
        if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(
                    R.id.map)).getMap();

            if (type.equals("creation")) {
                //not allowed in viewing mode
                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

                    @Override
                    public void onMapClick(LatLng latLng) {

                        googleMap.clear();
                        //own code
                        Sessions.currentLocationLatitude = latLng.latitude;
                        Sessions.currentLocationLongtitude = latLng.longitude;

                        CameraPosition cameraPosition1 = new CameraPosition.Builder().target(
                                new LatLng(latLng.latitude, latLng.longitude)).zoom(17).build();

                        //googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition1));

                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(latLng);
                        googleMap.addMarker(markerOptions);

                    }
                });
            }
            double latitude = 10.342887;
            double longitude = 123.960722;

            CameraPosition cameraPosition = new CameraPosition.Builder().target(
                    new LatLng(latitude, longitude)).zoom(10).build();

            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

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

    // An AsyncTask class for accessing the GeoCoding Web Service
    private class GeocoderTask extends AsyncTask<String, Void, List<Address>> {

        @Override
        protected List<Address> doInBackground(String... locationName) {
            // Creating an instance of Geocoder class
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

            if (addresses == null || addresses.size() == 0) {
                Toast.makeText(getBaseContext(), "No Location found", Toast.LENGTH_SHORT).show();
                Sessions.currentLocationLatitude = 0;
                Sessions.currentLocationLongtitude = 0;
            }
            googleMap.clear();

            for (int i = 0; i < addresses.size(); i++) {

                Address address = addresses.get(i);

                // Creating an instance of GeoPoint, to display in Google Map
                latLng = new LatLng(address.getLatitude(), address.getLongitude());

                //own code
                Sessions.currentLocationLatitude = address.getLatitude();
                Sessions.currentLocationLongtitude = address.getLongitude();

                String addressText = String.format("%s %s",
                        address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
                        address.getCountryName());

                /*Interactions.showError("Admin area " + address.getAdminArea() + " Feature name " +
                        address.getFeatureName() +  " Locality " + address.getLocality(), MapsActivity.this);*/

                CameraPosition cameraPosition1 = new CameraPosition.Builder().target(
                        new LatLng(latLng.latitude, latLng.longitude)).zoom(17).build();

                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition1));

                markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(addressText);

                googleMap.addMarker(markerOptions);

                // Locate the first location
                if (i == 0)
                    googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            initilizeMap();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


}
