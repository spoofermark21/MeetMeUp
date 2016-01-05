package practiceandroidapplication.android.com.meetmeup;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import practiceandroidapplication.android.com.meetmeup.Entity.Group;
import practiceandroidapplication.android.com.meetmeup.Entity.Meetups;
import practiceandroidapplication.android.com.meetmeup.Entity.Network;
import practiceandroidapplication.android.com.meetmeup.Entity.Sessions;
import practiceandroidapplication.android.com.meetmeup.Entity.User;
import practiceandroidapplication.android.com.meetmeup.Handles.JSONParser;

public class MeetupsActivity extends AppCompatActivity {

    private static final String RETRIEVE_MEETUPS_URL = Network.forDeploymentIp + "meetups_retrieve.php";
    private static final String UPDATE_MEETUPS_URL = Network.forDeploymentIp + "meetups_update.php";
    private static final String TAG_STATUS = "status";
    private static final String TAG_RESPONSE = "response";

    JSONParser jsonParser = new JSONParser();
    ProgressDialog pDialog;

    Toolbar toolbar;
    //ListView listMeetups;
    LinearLayout listOfMeetups;

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
                //startActivity(new Intent(MeetupsActivity.this, NewsfeedActivity.class));
                finish();
            }
        });

        //listMeetups = (ListView) findViewById(R.id.list_meetups);
        listOfMeetups = (LinearLayout) findViewById(R.id.linear_meetups);

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

            final TextView meetupDetails = new TextView(this);
            meetupDetails.setText("Details: " + meetups.getDetails());
            meetupDetails.setTextSize(15);
            meetupDetails.setTextColor(Color.BLACK);

            final TextView meetupLocation = new TextView(this);
            meetupLocation.setText("Location: " + meetups.getLocation());
            meetupLocation.setTextSize(15);
            meetupLocation.setTextColor(Color.BLACK);

            final TextView meetupKey = new TextView(this);
            meetupKey.setText("Key: " + meetups.getKey());
            meetupKey.setTextSize(15);
            meetupKey.setTextColor(Color.BLACK);


            final LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
            params2.weight = 1.0f;
            params2.gravity = Gravity.RIGHT;
            params2.leftMargin = 50;

            LinearLayout options = new LinearLayout(this);
            options.setOrientation(LinearLayout.HORIZONTAL);
            options.setPadding(10, 10, 10, 10);
            options.setLayoutParams(params2);

            final TextView edit = new TextView(this);
            edit.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            edit.setPadding(10, 10, 0, 10);
            edit.setText("edit");
            edit.setTextSize(15);
            edit.setBackgroundColor(Color.TRANSPARENT);


            final TextView delete = new TextView(this);
            delete.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            delete.setPadding(10, 10, 0, 10);
            delete.setText("delete");
            delete.setTextSize(15);
            delete.setBackgroundColor(Color.TRANSPARENT);

            edit.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    final LinearLayout parent = (LinearLayout) v.getParent().getParent();

                    /*Intent event = new Intent(GroupActivity.this, ViewEventsActivity.class);
                    event.putExtra("EVENT_KEY", key.getText().toString());
                    startActivity(event);
                    finish();

                    Toast.makeText(GroupActivity.this, name.getText().toString() + "! "
                            + key.getText().toString(), Toast.LENGTH_SHORT).show();
                    */
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

                                    String eventId = parent.getTag() + "";

                                    new DisableMeetups().execute(eventId);
                                    listOfMeetups.removeView(parent);
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

            options.addView(edit);
            options.addView(delete);

            recordOfMeetups.addView(meetupSubject);
            recordOfMeetups.addView(meetupDetails);
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

        String[] groups = new String[9999];
        ArrayAdapter<String> meetupAdapter;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MeetupsActivity.this, R.style.progress);
            pDialog.setCancelable(false);
            pDialog.setProgressStyle(android.R.style.Widget_Material_ProgressBar_Large);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... userInfo) {
            // TODO Auto-generated method stub

            int success;

            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();

                Log.d("USER_ID (user)", currentUser.getId() + "");

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

                    //sessions.removeGroups();

                    for (int i = 0; i < jUserArray.length(); i++) {
                        jUserObject = jUserArray.getJSONObject(i);

                        currentMeetups.add(new Meetups(jUserObject.getInt("id"), jUserObject.getString("subject"),
                                jUserObject.getString("details"), jUserObject.getString("location"),
                                jUserObject.getString("posted_date"), jUserObject.getString("key")));

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
                if (message.equals("Successful")) {
                    Toast.makeText(MeetupsActivity.this, message + "!", Toast.LENGTH_SHORT).show();
                    displayMeetups();
                    /*meetupAdapter = new ArrayAdapter<String>(MeetupsActivity.this,
                            android.R.layout.simple_list_item_1, sessions.listOfMeetups(currentMeetups));

                    listMeetups.setAdapter(meetupAdapter);

                    listMeetups.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {

                            // ListView Clicked item index
                            int itemPosition = position;

                            // ListView Clicked item value
                            String itemValue = (String) listMeetups.getItemAtPosition(position);

                            // Show Alert
                            Toast.makeText(getApplicationContext(),
                                    "Position :" + itemPosition + "  ListItem : " + itemValue, Toast.LENGTH_LONG)
                                    .show();
                        }
                    });*/

                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    } // end of thread retrieve user

    class DisableMeetups extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MeetupsActivity.this, R.style.progress);
            pDialog.setCancelable(false);
            pDialog.setProgressStyle(android.R.style.Widget_Material_ProgressBar_Large);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... meetupInfo) {
            // TODO Auto-generated method stub

            int success;

            try {
                // Building Parameters
                Log.d("KEY", meetupInfo[0]);
                List<NameValuePair> params = new ArrayList<NameValuePair>();

                params.add(new BasicNameValuePair("id", meetupInfo[0]));
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

}
