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

import practiceandroidapplication.android.com.meetmeup.Entity.Meetups;
import practiceandroidapplication.android.com.meetmeup.Entity.Network;
import practiceandroidapplication.android.com.meetmeup.Entity.Sessions;
import practiceandroidapplication.android.com.meetmeup.Entity.User;
import practiceandroidapplication.android.com.meetmeup.Handles.JSONParser;

public class MeetupsActivity extends AppCompatActivity {

    private static final String RETRIEVE_MEETUPS_URL = Network.forDeploymentIp + "meetups_retrieve.php";
    private static final String UPDATE_MEETUPS_URL = Network.forDeploymentIp + "meetups_update.php";
    private static final String LEAVE_URL = Network.forDeploymentIp + "leave.php";

    private static final String TAG_STATUS = "status";
    private static final String TAG_RESPONSE = "response";

    JSONParser jsonParser = new JSONParser();
    ProgressDialog pDialog;

    Toolbar toolbar;
    //ListView listMeetups;
    LinearLayout listOfMeetups;
    TextView lblMessage;

    Sessions sessions = Sessions.getSessionsInstance();
    List<Meetups> currentMeetups = new ArrayList<>();
    User currentUser = Sessions.getSessionsInstance().currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meetups);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        listOfMeetups = (LinearLayout) findViewById(R.id.linear_meetups);
        listOfMeetups.setVisibility(View.INVISIBLE);
        lblMessage = (TextView) findViewById(R.id.lbl_message);
        lblMessage.setVisibility(View.GONE);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.btn_save);
        fab.setVisibility(View.GONE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MeetupsActivity.this, CreateMeetupActivity.class));
            }
        });

        new RetrieveMeetups().execute();
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
            startActivity(new Intent(MeetupsActivity.this, CreateMeetupActivity.class));
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void displayMeetups() {
        for (Meetups meetups : currentMeetups) {

            LinearLayout recordOfMeetups = new LinearLayout(this);
            recordOfMeetups.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            recordOfMeetups.setOrientation(LinearLayout.VERTICAL);
            recordOfMeetups.setPadding(10, 10, 10, 10);

            Log.d("Meetup", meetups.getDetails());
            recordOfMeetups.setTag(meetups.getId());

            final TextView meetupSubject = new TextView(this);
            meetupSubject.setText(meetups.getSubject());
            meetupSubject.setTextSize(20);
            meetupSubject.setTextColor(Color.BLACK);
            meetupSubject.setTag(meetups.getLattitude());

            final TextView meetupDetails = new TextView(this);
            meetupDetails.setText("Details: " + meetups.getDetails());
            meetupDetails.setTextSize(15);
            meetupDetails.setTextColor(Color.BLACK);
            meetupDetails.setTag(meetups.getLongtitude());

            final TextView meetupLocation = new TextView(this);
            meetupLocation.setText("Location: " + meetups.getLocation());
            meetupLocation.setTextSize(15);
            meetupLocation.setTextColor(Color.BLACK);

            Log.d("Get posted by", meetups.getPostedBy() + "");

            final TextView meetupKey = new TextView(this);
            meetupKey.setText("Key: " + meetups.getKey());
            meetupKey.setTextSize(15);
            meetupKey.setTextColor(Color.BLACK);

            final TextView meetupPostedBy = new TextView(this);
            meetupPostedBy.setText("Posted by: " + meetups.getPostedByName());
            meetupPostedBy.setTextSize(15);
            meetupPostedBy.setTextColor(Color.BLACK);



            final LinearLayout.LayoutParams optionLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
            optionLayout.weight = 1.0f;
            optionLayout.gravity = Gravity.RIGHT;

            LinearLayout options = new LinearLayout(this);
            options.setOrientation(LinearLayout.HORIZONTAL);
            options.setPadding(10, 10, 10, 10);
            options.setLayoutParams(optionLayout);

            final LinearLayout.LayoutParams btnLayout = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
            btnLayout.weight = 1.0f;
            btnLayout.setMargins(10, 10, 10, 10);

            final TextView map = new TextView(this);
            map.setLayoutParams(btnLayout);
            map.setPadding(10, 10, 0, 10);
            map.setText("map");
            map.setTextSize(15);
            map.setGravity(Gravity.CENTER);
            map.setBackgroundColor(Color.TRANSPARENT);

            final TextView view = new TextView(this);
            view.setLayoutParams(btnLayout);;
            view.setPadding(10, 10, 0, 10);
            view.setText("view");
            view.setTextSize(15);
            view.setGravity(Gravity.CENTER);
            view.setBackgroundColor(Color.TRANSPARENT);

            final TextView leave = new TextView(this);
            leave.setLayoutParams(btnLayout);
            leave.setPadding(10, 10, 0, 10);
            leave.setText("leave");
            leave.setTextSize(15);
            leave.setGravity(Gravity.CENTER);
            leave.setBackgroundColor(Color.TRANSPARENT);
            leave.setTextColor(Color.parseColor("#D46A6A"));


            final TextView edit = new TextView(this);
            edit.setLayoutParams(btnLayout);
            edit.setPadding(10, 10, 0, 10);
            edit.setText("edit");
            edit.setTextSize(15);
            edit.setGravity(Gravity.CENTER);
            edit.setBackgroundColor(Color.TRANSPARENT);


            final TextView delete = new TextView(this);
            delete.setLayoutParams(btnLayout);
            delete.setPadding(10, 10, 0, 10);
            delete.setText("delete");
            delete.setTextSize(15);
            delete.setGravity(Gravity.CENTER);
            delete.setBackgroundColor(Color.TRANSPARENT);
            delete.setTextColor(Color.parseColor("#D46A6A"));

            map.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    final LinearLayout parent = (LinearLayout) v.getParent().getParent();

                    Log.d("Location", parent.getChildAt(0) + " " + parent.getChildAt(1));

                    Intent map = new Intent(MeetupsActivity.this, ViewMapsActivity.class);
                    map.putExtra("LATTITUDE", parent.getChildAt(0).getTag() + "");
                    map.putExtra("LONGTITUDE", parent.getChildAt(1).getTag() + "");

                    startActivity(map);
                    Toast.makeText(MeetupsActivity.this, parent.getTag() + "! "
                            , Toast.LENGTH_SHORT).show();
                }
            });

            view.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    final LinearLayout parent = (LinearLayout) v.getParent().getParent();

                    Intent meetups = new Intent(MeetupsActivity.this, ViewMeetupsActivity.class);
                    meetups.putExtra("MEETUPS_ID", parent.getTag() + "");
                    startActivity(meetups);
                }
            });

            edit.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    final LinearLayout parent = (LinearLayout) v.getParent().getParent();

                    Intent meetups = new Intent(MeetupsActivity.this, EditMeetupActivity.class);
                    meetups.putExtra("MEETUPS_ID", parent.getTag() + "");
                    startActivity(meetups);
                    finish();

                    Toast.makeText(MeetupsActivity.this, parent.getTag() + "! "
                            , Toast.LENGTH_SHORT).show();

                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {

                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(MeetupsActivity.this);
                    dlgAlert.setMessage("Are you sure to delete this Meetup?");
                    dlgAlert.setTitle("Warning!");
                    dlgAlert.setCancelable(true);

                    dlgAlert.setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(MeetupsActivity.this, "Deleted!", Toast.LENGTH_SHORT).show();
                                    final LinearLayout parent = (LinearLayout) v.getParent().getParent();

                                    String meetupId = parent.getTag() + "";

                                    listOfMeetups.removeView(parent);
                                    new DisableMeetups(meetupId).execute();


                                    if (listOfMeetups.getChildCount() == 1)
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

            leave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {

                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(MeetupsActivity.this);
                    dlgAlert.setMessage("Are you sure to leave this Meetup?");
                    dlgAlert.setTitle("Warning!");
                    dlgAlert.setCancelable(true);
                    dlgAlert.setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(MeetupsActivity.this, "Successful!", Toast.LENGTH_SHORT).show();

                                    final LinearLayout parent = (LinearLayout) v.getParent().getParent();

                                    String meetupId = parent.getTag() + "";

                                    listOfMeetups.removeView(parent);

                                    new LeaveMeetups(meetupId, currentUser.getId() + "").execute();

                                    if (listOfMeetups.getChildCount() == 1)
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

            Log.d("Check", meetups.getPostedBy() + " " + currentUser.getId());


            recordOfMeetups.addView(meetupSubject);
            recordOfMeetups.addView(meetupDetails);

            if(meetups.getPostedBy() == currentUser.getId()) {
                options.addView(edit);
                options.addView(delete);
            } else {
                options.addView(leave);
                recordOfMeetups.addView(meetupPostedBy);
            }

            recordOfMeetups.addView(meetupLocation);
            recordOfMeetups.addView(meetupKey);
            recordOfMeetups.addView(options);

            listOfMeetups.addView(recordOfMeetups);
        }

    }
    /*
        thread
     */

    class RetrieveMeetups extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MeetupsActivity.this, R.style.progress);
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
                List<NameValuePair> params = new ArrayList<>();

                Log.d("USER_ID (user)", currentUser.getId() + "");

                params.add(new BasicNameValuePair("id", currentUser.getId() + ""));
                //to be investigate
                params.add(new BasicNameValuePair("user_id", currentUser.getId() + ""));
                params.add(new BasicNameValuePair("filter", "all"));

                Log.d("request!", "starting");

                JSONObject json = jsonParser.makeHttpRequest(
                        RETRIEVE_MEETUPS_URL, "POST", params);

                Log.d("Fetching...", json.toString());

                success = json.getInt(TAG_STATUS);

                if (success == 1) {
                    Log.d("Success!", json.toString());

                    JSONArray jUserArray = json.getJSONArray("meetups");
                    JSONObject jUserObject;

                    currentMeetups.clear();

                    for (int i = 0; i < jUserArray.length(); i++) {
                        jUserObject = jUserArray.getJSONObject(i);

                        Meetups meetups = new Meetups(jUserObject.getInt("id"), jUserObject.getString("subject"),
                                jUserObject.getString("details"), jUserObject.getString("location"),
                                jUserObject.getString("posted_date"), jUserObject.getString("key"),
                                jUserObject.getInt("posted_by"), jUserObject.getString("posted_by_user"));

                        Log.d("Posted by", jUserObject.getInt("posted_by") + "");

                        meetups.setLattitude(jUserObject.getString("lattitude"));
                        meetups.setLongtitude(jUserObject.getString("longtitude"));

                        currentMeetups.add(meetups);

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
                listOfMeetups.setVisibility(View.VISIBLE);
                if (message.equals("Successful")) {
                    Toast.makeText(MeetupsActivity.this, message + "!", Toast.LENGTH_SHORT).show();
                    displayMeetups();
                } else if (message.equals("No meetups")) {
                    lblMessage.setVisibility(View.VISIBLE);
                } else {
                    lblMessage.setVisibility(View.VISIBLE);
                    lblMessage.setText("Please check your internet connection");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }

    class DisableMeetups extends AsyncTask<String, String, String> {

        String meetupId;

        public DisableMeetups(String meetupId) {
            this.meetupId = meetupId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MeetupsActivity.this, R.style.progress);
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

                params.add(new BasicNameValuePair("id", meetupId));
                params.add(new BasicNameValuePair("query_type", "disable"));

                Log.d("request!", "starting");

                JSONObject json = jsonParser.makeHttpRequest(
                        UPDATE_MEETUPS_URL, "POST", params);

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
                    Toast.makeText(MeetupsActivity.this, message + "!", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    class LeaveMeetups extends AsyncTask<String, String, String> {

        String meetupId;
        String userId;

        public LeaveMeetups(String meetupId, String userId) {
            this.meetupId = meetupId;
            this.userId = userId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MeetupsActivity.this, R.style.progress);
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

                params.add(new BasicNameValuePair("id", meetupId));
                params.add(new BasicNameValuePair("user_id", userId));
                params.add(new BasicNameValuePair("query_type", "attendees"));
                params.add(new BasicNameValuePair("type", "M"));

                Log.d("Leave details", "Meetup id" + meetupId + " User id " + userId);

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
                    Toast.makeText(MeetupsActivity.this, message + "!", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

}
