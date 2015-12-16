package practiceandroidapplication.android.com.meetmeup;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import practiceandroidapplication.android.com.meetmeup.Entity.ListNationalities;
import practiceandroidapplication.android.com.meetmeup.Entity.Nationality;
import practiceandroidapplication.android.com.meetmeup.Entity.Network;
import practiceandroidapplication.android.com.meetmeup.Entity.User;
import practiceandroidapplication.android.com.meetmeup.Handles.Interactions;
import practiceandroidapplication.android.com.meetmeup.Handles.JSONParser;

public class NewsfeedActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    JSONParser jsonParser = new JSONParser();

    ImageView imgUserProfile;
    TextView txtUserFullName, txtUserNationality;

    private ProgressDialog pDialog;

    // web service
    private static final String LOGOUT_URL = Network.forDeploymentIp + "user_logout.php";
    private static final String TAG_STATUS = "status";
    private static final String TAG_RESPONSE = "response";

    Fragment userProfile = new UserProfileFragment();
    FragmentManager fragmentManager = getFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newsfeed);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // navigation bar
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        //set UI
        initUI();
        setUserProfile();

    }

    public void initUI() {
        imgUserProfile = (ImageView) findViewById(R.id.user_image);
        txtUserFullName = (TextView) findViewById(R.id.user_fullname);
        txtUserNationality = (TextView) findViewById(R.id.user_nationality);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.newsfeed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_user_profile) {
            navUserProfile();
        } else if (id == R.id.nav_groups) {
            //navGroups();
            startActivity(new Intent(NewsfeedActivity.this, UserProfileActivity.class));
        } else if (id == R.id.nav_meetups) {
            startActivity(new Intent(NewsfeedActivity.this, SampleActivity.class));
        } else if (id == R.id.nav_events) {

        } else if (id == R.id.nav_notifations) {

        } else if (id == R.id.nav_logout) {
            navLogOutEvent();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    class Logout extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(NewsfeedActivity.this, R.style.progress);
            pDialog.setCancelable(false);
            pDialog.setProgressStyle(android.R.style.Widget_Material_ProgressBar_Large);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... user) {
            // TODO Auto-generated method stub

            int success;

            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("user_id", user[0]));

                Log.d("request!", "starting");

                JSONObject json = jsonParser.makeHttpRequest(
                        LOGOUT_URL, "POST", params);

                Log.d("Logout...", json.toString());

                success = json.getInt(TAG_STATUS);

                if (success == 1) {
                    return json.getString(TAG_RESPONSE);
                } else {
                    Log.d("Logout failed!", json.getString(TAG_RESPONSE));
                    return json.getString(TAG_RESPONSE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String message) {
            pDialog.dismiss();
            if (message.equals("Successful")) {
                //go back to login page
                startActivity(new Intent(NewsfeedActivity.this, LoginActivity.class));
                finish();
                //System.exit(0);
            }
        }
    }

    public void navLogOutEvent() {
        try {
            Log.d("Debugging", user().getId() + "");

            new Logout().execute(user().getId() + "");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void navGroups() {
        //fragmentTransaction.addToBackStack(null);
        //fragmentTransaction.commit();
    }

    public void navUserProfile() {
        Log.d("USER_ID (newsfeed)",user().getId() + "");
        startActivity(new Intent(NewsfeedActivity.this, UserProfileActivity.class).putExtra("USER_ID",user().getId() + ""));
            /*fragmentTransaction.add(R.id.focus_layout, userProfile,"USER_PROFILE_FRAGMENT");
            fragmentTransaction.commit();*/
    }

    public void setUserProfile() {
        txtUserFullName.setText(user().getFirstName() + " " + user().getLastName());
        txtUserNationality.setText(user().getNationality().getNationality());
    }

    public User user() {
        Intent intent = getIntent();
        User user = new User();
        user.setId(Integer.parseInt(intent.getStringExtra("USER_ID")));
        user.setFirstName(intent.getStringExtra("USER_FIRSTNAME"));
        user.setLastName(intent.getStringExtra("USER_LASTNAME"));

        ListNationalities listNationalities = ListNationalities.getInstanceListNationalities();

        int natioId = Integer.parseInt(intent.getStringExtra("USER_NATIONALITY"));

        for (Nationality nationality : listNationalities.nationalities) {
            if (natioId == nationality.getId()) {
                user.setNationality(new Nationality(natioId, nationality.getNationality()));
            }
        }

        return user;
    }

}
