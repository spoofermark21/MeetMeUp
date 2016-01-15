package practiceandroidapplication.android.com.meetmeup;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.widget.Toast;

import practiceandroidapplication.android.com.meetmeup.Adapter.TabsPagerAdapter;
import practiceandroidapplication.android.com.meetmeup.Entity.ListNationalities;
import practiceandroidapplication.android.com.meetmeup.Entity.Nationality;
import practiceandroidapplication.android.com.meetmeup.Entity.Network;
import practiceandroidapplication.android.com.meetmeup.Entity.Sessions;
import practiceandroidapplication.android.com.meetmeup.Entity.User;
import practiceandroidapplication.android.com.meetmeup.Fragments.EventsFragment;
import practiceandroidapplication.android.com.meetmeup.Fragments.MeetupsFragment;
import practiceandroidapplication.android.com.meetmeup.Handles.JSONParser;
//import practiceandroidapplication.android.com.meetmeup.Newsfeed.adapter.FeedListAdapter;


public class NewsfeedActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // web service
    private static final String LOGOUT_URL = Network.forDeploymentIp + "user_logout.php";
    private static final String RETRIEVE_MEETUPS_URL = Network.forDeploymentIp + "user_logout.php";
    private static final String RETRIEVE_GROUPS_URL = Network.forDeploymentIp + "user_logout.php";
    private static final String RETRIEVE_EVENTS_URL = Network.forDeploymentIp + "user_logout.php";


    private static final String TAG_STATUS = "status";
    private static final String TAG_RESPONSE = "response";

    JSONParser jsonParser = new JSONParser();
    ProgressDialog pDialog;

    ImageView imgUserProfile;
    TextView txtUserFullName, txtUserNationality;

    Button btnMeetups, btnEvents, btnGroups;

    Sessions sessions = Sessions.getSessionsInstance();
    User currentUser = Sessions.getSessionsInstance().currentUser;

    LinearLayout listOfFeeds;

    Fragment meetups = new MeetupsFragment();
    Fragment events = new EventsFragment();
    Fragment groups;

    FragmentManager fragmentManager = getFragmentManager();

    int btnCounter = 0;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newsfeed);

        //set toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
        //setUserProfile();
        setUserProfile();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_user_profile) {
            navUserProfile();
        } else if (id == R.id.nav_groups) {
            navGroups();
        } else if (id == R.id.nav_meetups) {
            navMeetups();
        } else if (id == R.id.nav_events) {
            navEvents();
        } else if (id == R.id.nav_notifations) {

        } else if (id == R.id.nav_logout) {
            navLogOutEvent();
        } else if (id == R.id.nav_location) {
            navLocation();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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

        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_search) {
            startActivity(new Intent(NewsfeedActivity.this, SearchActivity.class));
        } else if (id == R.id.action_refresh) {
            Toast.makeText(NewsfeedActivity.this, "You clicked refresh."
                    , Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

    /*
        functions
     */

    public void initUI() {
        //imgUserProfile = (ImageView) findViewById(R.id.user_image);
        txtUserFullName = (TextView) findViewById(R.id.user_fullname);
        txtUserNationality = (TextView) findViewById(R.id.user_nationality);

        listOfFeeds = (LinearLayout) findViewById(R.id.linear_feeds);
        listOfFeeds.setVisibility(View.INVISIBLE);

        btnMeetups = (Button) findViewById(R.id.btn_meetups);
        btnEvents = (Button) findViewById(R.id.btn_events);
        btnGroups = (Button) findViewById(R.id.btn_groups);


        btnMeetups.setOnClickListener(new View.OnClickListener(){
            public void onClick (View view) {
                try {
                    listOfFeeds.setVisibility(View.INVISIBLE);
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.linear_feeds, meetups, "MEETUPS_FRAGMENT");
                    fragmentTransaction.commit();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                btnMeetups.setBackgroundResource(R.color.gray);
                btnEvents.setBackgroundResource(R.color.transparent);
                btnGroups.setBackgroundResource(R.color.transparent);
            }
        });

        btnEvents.setOnClickListener(new View.OnClickListener(){
            public void onClick (View view) {
                try {
                    listOfFeeds.setVisibility(View.INVISIBLE);
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.linear_feeds, events, "EVENTS_FRAGMENT");
                    fragmentTransaction.commit();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                btnMeetups.setBackgroundResource(R.color.transparent);
                btnEvents.setBackgroundResource(R.color.gray);
                btnGroups.setBackgroundResource(R.color.transparent);
            }
        });

        btnGroups.setOnClickListener(new View.OnClickListener(){
            public void onClick (View view) {
                //fragmentTransaction.add(R.id.linear_feeds, meetups,"GROUP_FRAGMENT");
                //fragmentTransaction.commit();
                btnMeetups.setBackgroundResource(R.color.transparent);
                btnEvents.setBackgroundResource(R.color.transparent);
                btnGroups.setBackgroundResource(R.color.gray);
            }
        });



    }


    public void navUserProfile() {
        Log.d("USER_ID (newsfeed)", currentUser.getId() + "");
        startActivity(new Intent(NewsfeedActivity.this, UserProfileActivity.class));//.putExtra("USER_ID",user().getId() + ""));
            /*fragmentTransaction.add(R.id.focus_layout, userProfile,"USER_PROFILE_FRAGMENT");
            fragmentTransaction.commit();*/
    }

    public void navGroups() {
        //fragmentTransaction.addToBackStack(null);
        //fragmentTransaction.commit();
        startActivity(new Intent(NewsfeedActivity.this, GroupActivity.class));
    }

    public void navMeetups() {
        startActivity(new Intent(NewsfeedActivity.this, MeetupsActivity.class));
    }

    public void navEvents() {
        startActivity(new Intent(NewsfeedActivity.this, EventsActivity.class));
    }

    public void navLocation() {
        startActivity(new Intent(NewsfeedActivity.this, MapsActivity.class));
        /*Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?saddr=20.344,34.34&daddr=20.5666,45.345"));
        startActivity(intent);*/
    }

    public void navLogOutEvent() {
        try {
            Log.d("Debugging", currentUser.getId() + "");
            new Logout().execute(currentUser.getId() + "");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void navUserPreference() {
        Log.d("USER_ID (newsfeed)", currentUser.getId() + "");
        startActivity(new Intent(NewsfeedActivity.this, SetPreferenceActivity.class));
    }

    public void setUserProfile() {
        txtUserFullName.setText(currentUser.getFirstName() + " "
                + currentUser.getLastName());
        txtUserNationality.setText(currentUser.getNationality()
                .getNatioNalityName());
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            moveTaskToBack(true);
        }
    }


    /*
        thread
     */

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

}


  /*
    //volley
    private static final String TAG = NewsfeedActivity.class.getSimpleName();
    private ListView listView;
    private FeedListAdapter listAdapter;
    private List<FeedItem> feedItems;
    private String URL_FEED = "http://localhost/meetmeup/feed.json";*/


        /*listView = (ListView) findViewById(R.id.list_feed);

        feedItems = new ArrayList<FeedItem>();

        listAdapter = new FeedListAdapter(this, feedItems);
        listView.setAdapter(listAdapter);

        // These two lines not needed,
        // just to get the look of facebook (changing background color & hiding the icon)
        getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3b5998")));
        getActionBar().setIcon(
                new ColorDrawable(getResources().getColor(android.R.color.transparent)));

        // We first check for cached request
        Cache cache = AppController.getInstance().getRequestQueue().getCache();
        Entry entry = cache.get(URL_FEED);
        if (entry != null) {
            // fetch the data from cache
            try {
                String data = new String(entry.data, "UTF-8");
                try {
                    parseJsonFeed(new JSONObject(data));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        } else {
            // making fresh volley request and getting json
            JsonObjectRequest jsonReq = new JsonObjectRequest(Method.GET,
                    URL_FEED, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    VolleyLog.d(TAG, "Response: " + response.toString());
                    if (response != null) {
                        parseJsonFeed(response);
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                }
            });

            // Adding request to volley request queue
            AppController.getInstance().addToRequestQueue(jsonReq);
        }
        */