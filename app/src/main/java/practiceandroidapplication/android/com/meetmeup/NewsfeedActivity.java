package practiceandroidapplication.android.com.meetmeup;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
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
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.widget.Toast;

import practiceandroidapplication.android.com.meetmeup.Adapter.TabsPagerAdapter;
import practiceandroidapplication.android.com.meetmeup.Entity.ListNationalities;
import practiceandroidapplication.android.com.meetmeup.Entity.Meetups;
import practiceandroidapplication.android.com.meetmeup.Entity.Nationality;
import practiceandroidapplication.android.com.meetmeup.Entity.Network;
import practiceandroidapplication.android.com.meetmeup.Entity.Sessions;
import practiceandroidapplication.android.com.meetmeup.Entity.User;
import practiceandroidapplication.android.com.meetmeup.Fragments.EventsFragment;
import practiceandroidapplication.android.com.meetmeup.Fragments.GroupsFragment;
import practiceandroidapplication.android.com.meetmeup.Fragments.MeetupsFragment;
import practiceandroidapplication.android.com.meetmeup.Handles.Interactions;
import practiceandroidapplication.android.com.meetmeup.Handles.JSONParser;
//import practiceandroidapplication.android.com.meetmeup.Newsfeed.adapter.FeedListAdapter;


public class NewsfeedActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // web service
    private static final String LOGOUT_URL = Network.forDeploymentIp + "user_logout.php";
    private static final String NOTIFICATIONS_URL = Network.forDeploymentIp + "notification_retrieve.php";

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
    Fragment groups = new GroupsFragment();

    FragmentManager fragmentManager = getFragmentManager();

    private char currentFragmentSelected = 'A';

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

        //check new notifications
        new RetrieveNotications().execute();

        //meetups as default newsfeed
        try {
            listOfFeeds.setVisibility(View.INVISIBLE);
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.linear_feeds, meetups, "MEETUPS_FRAGMENT");
            fragmentTransaction.commit();

            //Log.d("User Preference: ", currentUser.getPreference().getGender() + "");
        } catch (Exception ex) {
            ex.printStackTrace();
        }


        btnMeetups.setTextColor(Color.DKGRAY);
        /*btnMeetups.setBackgroundResource(R.color.gray);
        btnEvents.setBackgroundResource(R.color.transparent);
        btnGroups.setBackgroundResource(R.color.transparent);*/
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
            navNotications();
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
        getMenuInflater().inflate(R.menu.newsfeed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        } /*else if (id == R.id.action_refresh) {
            try {

                switch(currentFragmentSelected) {
                    case 'M':
                    case 'A':
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.detach(meetups);
                        fragmentTransaction.attach(meetups);

                        MeetupsFragment meetupsFragment = new MeetupsFragment();
                        meetupsFragment.refresh();

                        currentFragmentSelected = 'M';
                        // for debugging
                        Toast.makeText(NewsfeedActivity.this, "You clicked refresh meetup."
                                , Toast.LENGTH_SHORT).show();
                    case 'E':
                        fragmentTransaction.detach(events);
                        fragmentTransaction.attach(events);
                        currentFragmentSelected = 'E';
                        // for debugging
                        Toast.makeText(NewsfeedActivity.this, "You clicked refresh events."
                                , Toast.LENGTH_SHORT).show();
                        break;
                    case 'G':
                        // for debugging
                        Toast.makeText(NewsfeedActivity.this, "You clicked refresh groups."
                                , Toast.LENGTH_SHORT).show();
                        fragmentTransaction.detach(groups);
                        fragmentTransaction.attach(groups);
                        currentFragmentSelected = 'G';
                        break;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }*/

        return super.onOptionsItemSelected(item);
    }

    /*
        functions
     */

    public void initUI() {
        imgUserProfile = (ImageView) findViewById(R.id.user_image);
        imgUserProfile.setBackgroundColor(Color.parseColor("#E6E9ED"));
        txtUserFullName = (TextView) findViewById(R.id.user_fullname);
        txtUserNationality = (TextView) findViewById(R.id.user_nationality);

        listOfFeeds = (LinearLayout) findViewById(R.id.linear_feeds);
        listOfFeeds.setVisibility(View.INVISIBLE);

        btnMeetups = (Button) findViewById(R.id.btn_meetups);
        btnEvents = (Button) findViewById(R.id.btn_events);
        btnGroups = (Button) findViewById(R.id.btn_groups);


        btnMeetups.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                try {
                    if (currentFragmentSelected != 'M' && currentFragmentSelected != 'A') {
                        listOfFeeds.setVisibility(View.INVISIBLE);
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.linear_feeds, meetups, "MEETUPS_FRAGMENT");
                        fragmentTransaction.commit();
                        currentFragmentSelected = 'M';
                        Log.d("Fragment", currentFragmentSelected + "");
                    } else {
                        Log.d("Fragment", "else");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                btnMeetups.setTextColor(Color.DKGRAY);
                btnEvents.setTextColor(Color.GRAY);
                btnGroups.setTextColor(Color.GRAY);

                /*btnMeetups.setBackgroundResource(R.color.gray);
                btnEvents.setBackgroundResource(R.color.transparent);
                btnGroups.setBackgroundResource(R.color.transparent);*/
            }
        });

        btnEvents.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                try {
                    if (currentFragmentSelected != 'E') {
                        listOfFeeds.setVisibility(View.INVISIBLE);
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.linear_feeds, events, "EVENTS_FRAGMENT");
                        fragmentTransaction.commit();
                        currentFragmentSelected = 'E';
                        Log.d("Fragment", currentFragmentSelected + "");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                btnMeetups.setTextColor(Color.GRAY);
                btnEvents.setTextColor(Color.DKGRAY);
                btnGroups.setTextColor(Color.GRAY);
            }
        });

        btnGroups.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                try {
                    if (currentFragmentSelected != 'G') {
                        listOfFeeds.setVisibility(View.INVISIBLE);
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.linear_feeds, groups, "GROUPS_FRAGMENT");
                        fragmentTransaction.commit();
                        currentFragmentSelected = 'G';
                        Log.d("Fragment", currentFragmentSelected + "");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                btnMeetups.setTextColor(Color.GRAY);
                btnEvents.setTextColor(Color.GRAY);
                btnGroups.setTextColor(Color.DKGRAY);
            }
        });


    }


    public void navUserProfile() {
        Log.d("USER_ID (newsfeed)", currentUser.getId() + "");
        startActivity(new Intent(NewsfeedActivity.this, UserProfileActivity.class));//.putExtra("USER_ID",user().getId() + ""));
        finish();
    }

    public void navGroups() {
        //fragmentTransaction.addToBackStack(null);
        //fragmentTransaction.commit();
        startActivity(new Intent(NewsfeedActivity.this, GroupActivity.class));
        finish();
    }

    public void navMeetups() {
        startActivity(new Intent(NewsfeedActivity.this, MeetupsActivity.class));
        finish();
    }

    public void navEvents() {
        startActivity(new Intent(NewsfeedActivity.this, EventsActivity.class));
        finish();
    }

    public void navLocation() {
        startActivity(new Intent(NewsfeedActivity.this, MapsActivity.class));
        finish();
        /*Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?saddr=20.344,34.34&daddr=20.5666,45.345"));
        startActivity(intent);*/
    }

    public void navNotications() {
        startActivity(new Intent(NewsfeedActivity.this, NotificationActivity.class));
        finish();
    }

    public void navLogOutEvent() {
        try {
            Log.d("Debugging", currentUser.getId() + "");
            new Logout().execute(currentUser.getId() + "");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setUserProfile() {
        txtUserFullName.setText(currentUser.getFirstName() + " "
                + currentUser.getLastName());
        txtUserNationality.setText(currentUser.getNationality()
                .getNatioNalityName());

        if(!currentUser.getUserImage().equals("null") && !currentUser.getUserImage().equals("")) {

            class DownloadUserImage extends AsyncTask<Void, Void, Bitmap> {

                String filename;

                public DownloadUserImage(String filename) {
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
                    //pDialog.dismiss();
                    try {
                        if(bitmap!=null) {
                            Log.d("Image", "Success");
                            imgUserProfile.setImageBitmap(bitmap);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

            }

            new DownloadUserImage(currentUser.getUserImage() + ".JPG").execute();
            imgUserProfile.setScaleType(ImageView.ScaleType.FIT_XY);
        }
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

    class RetrieveNotications extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... info) {
            // TODO Auto-generated method stub

            int success;

            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<>();
                Log.d("request!", "starting");

                params.add(new BasicNameValuePair("user_id", currentUser.getId() + ""));

                JSONObject json = jsonParser.makeHttpRequest(
                        NOTIFICATIONS_URL, "POST", params);

                Log.d("Fetching...", json.toString());

                success = json.getInt(TAG_STATUS);

                if (success == 1) {
                    Log.d("Successful!", json.toString());

                    return json.getString(TAG_RESPONSE);
                } else {
                    Log.d("Fetching failed!", json.getString(TAG_RESPONSE));
                    return json.getString(TAG_RESPONSE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String message) {
            try {

                if(message.equals("New notifications.")) {
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(NewsfeedActivity.this)
                                    .setSmallIcon(R.drawable.ic_arrow_back_black)
                                    .setContentTitle("Notifications")
                                    .setContentText("You have new notifications");
                    mBuilder.build();

// Creates an explicit intent for an Activity in your app
                    Intent resultIntent = new Intent(NewsfeedActivity.this, NotificationActivity.class);

// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(NewsfeedActivity.this);
// Adds the back stack for the Intent (but not the Intent itself)
                    stackBuilder.addParentStack(LoginActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
                    stackBuilder.addNextIntent(resultIntent);
                    PendingIntent resultPendingIntent =
                            stackBuilder.getPendingIntent(
                                    0,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                            );
                    mBuilder.setContentIntent(resultPendingIntent);
                    NotificationManager mNotificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
                    mNotificationManager.notify(1,mBuilder.build());


                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }

    }

    class Logout extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(NewsfeedActivity.this, R.style.progress);
            pDialog.setCancelable(true);
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