package practiceandroidapplication.android.com.meetmeup;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
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

import practiceandroidapplication.android.com.meetmeup.Entity.Sessions;

public class MapsActivity extends AppCompatActivity {

    private GoogleMap googleMap;
    MarkerOptions markerOptions;

    LatLng latLng;

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
                startActivity(new Intent(MapsActivity.this, NewsfeedActivity.class));
                finish();
            }
        });

        try {
            // Loading map
            initilizeMap();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void onBackPressed() {
        startActivity(new Intent(MapsActivity.this, NewsfeedActivity.class));
        finish();
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

            // Getting reference to btn_find of the layout activity_main
            Button btn_find = (Button) findViewById(R.id.btn_find);

            // Defining button click event listener for the find button
            View.OnClickListener findClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Getting reference to EditText to get the user input location
                    EditText etLocation = (EditText) findViewById(R.id.et_location);

                    // Getting user input location
                    String location = etLocation.getText().toString();

                    if(location!=null && !location.equals("")){
                        new GeocoderTask().execute(location);
                    }
                }
            };

            // Setting button click event listener for the find button
            btn_find.setOnClickListener(findClickListener);

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

            if(addresses==null || addresses.size()==0){
                Toast.makeText(getBaseContext(), "No Location found", Toast.LENGTH_SHORT).show();
            }

            // Clears all the existing markers on the map
            googleMap.clear();

            // Adding Markers on Google Map for each matching address
            for(int i=0;i<addresses.size();i++){

                Address address = addresses.get(i);

                // Creating an instance of GeoPoint, to display in Google Map
                latLng = new LatLng(address.getLatitude(), address.getLongitude());

                Sessions.currentLocationLatitude = address.getLatitude();
                Sessions.currentLocationLatitude = address.getLongitude();

                String addressText = String.format("%s, %s",
                        address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
                        address.getCountryName());

                markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(addressText);

                googleMap.addMarker(markerOptions);

                // Locate the first location
                if(i==0)
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
