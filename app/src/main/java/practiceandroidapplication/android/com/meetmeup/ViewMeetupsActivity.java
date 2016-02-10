package practiceandroidapplication.android.com.meetmeup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.games.Notifications;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Comment;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import practiceandroidapplication.android.com.meetmeup.Entity.Attendees;
import practiceandroidapplication.android.com.meetmeup.Entity.Comments;
import practiceandroidapplication.android.com.meetmeup.Entity.ListNationalities;
import practiceandroidapplication.android.com.meetmeup.Entity.Meetups;
import practiceandroidapplication.android.com.meetmeup.Entity.Network;
import practiceandroidapplication.android.com.meetmeup.Entity.Notification;
import practiceandroidapplication.android.com.meetmeup.Entity.Preference;
import practiceandroidapplication.android.com.meetmeup.Entity.SensoredWords;
import practiceandroidapplication.android.com.meetmeup.Entity.Sessions;
import practiceandroidapplication.android.com.meetmeup.Entity.User;
import practiceandroidapplication.android.com.meetmeup.Entity.User;
import practiceandroidapplication.android.com.meetmeup.Handles.Interactions;
import practiceandroidapplication.android.com.meetmeup.Handles.JSONParser;

public class ViewMeetupsActivity extends AppCompatActivity {

    private static final String RETRIEVE_MEETUPS_URL = Network.forDeploymentIp + "meetups_retrieve.php";
    private static final String RETRIEVE_ATTENDEES_URL = Network.forDeploymentIp + "is_attendee.php";
    private static final String RETRIEVE_COMMENTS_URL = Network.forDeploymentIp + "comments_retrieve.php";

    private static final String INSERT_COMMENT_URL = Network.forDeploymentIp + "comments_save.php";
    private static final String INSERT_NOTIFICATIONS_URL = Network.forDeploymentIp + "notication_save.php";
    private static final String INSERT_ATTENDEES_URL = Network.forDeploymentIp + "attendees_save.php";


    private static final String TAG_STATUS = "status";
    private static final String TAG_RESPONSE = "response";

    JSONParser jsonParser = new JSONParser();
    ProgressDialog pDialog;

    ImageView imgUser;

    TextView lblSubject, lblPostedDate, lblDetails, lblLocation,
            lblPostedBy, lblComments;

    ListView listComments;
    LinearLayout linearComments;

    EditText txtComment;

    Button btnJoin, btnComment, btnProfile;

    ScrollView scrollView;

    Meetups meetups;
    Attendees attendees;

    Sessions sessions = Sessions.getSessionsInstance();
    User currentUser = Sessions.getSessionsInstance().currentUser;

    String meetupId;
    ArrayAdapter<String> adapter;
    //Timer timer = new Timer();
    Thread thread = new Thread();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_meetups);

        Toolbar toolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        toolBar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ViewMeetupsActivity.this, NewsfeedActivity.class));
                finish();
            }
        });

        initUI();

        meetupId = getIntent().getStringExtra("MEETUPS_ID");

        new RetrieveMeetups().execute();

    }

    public void initUI() {

        imgUser = (ImageView) findViewById(R.id.img_user);

        scrollView = (ScrollView) findViewById(R.id.scroll_view);
        scrollView.setVisibility(View.INVISIBLE);

        lblSubject = (TextView) findViewById(R.id.lbl_subject);
        lblPostedDate = (TextView) findViewById(R.id.lbl_posted_date);
        lblDetails = (TextView) findViewById(R.id.lbl_details);
        lblLocation = (TextView) findViewById(R.id.lbl_location);
        lblPostedBy = (TextView) findViewById(R.id.lbl_posted_by);

        lblComments = (TextView) findViewById(R.id.lbl_comments);
        lblComments.setVisibility(View.GONE);

        listComments = (ListView) findViewById(R.id.list_comments);
        listComments.setVisibility(View.GONE);

        linearComments = (LinearLayout) findViewById(R.id.linear_comments);
        linearComments.setVisibility(View.GONE);

        btnJoin = (Button) findViewById(R.id.btn_join);
        btnJoin.setVisibility(View.GONE);

        txtComment = (EditText) findViewById(R.id.txt_comment);

        btnComment = (Button) findViewById(R.id.btn_comment);
        btnComment.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (isReadyToComment()) {
                    new InsertComment(new Comments(Integer.parseInt(meetupId), 'M',
                            currentUser.getId(), checkComment(txtComment.getText().toString()))).execute();
                    txtComment.setText("");
                    txtComment.setError(null);
                } else {
                    txtComment.setError("This is a required field.");
                }

            }
        });

        btnProfile = (Button) findViewById(R.id.btn_view_profile);
        btnProfile.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                startActivity(new Intent(ViewMeetupsActivity.this, ViewProfileActivity.class).putExtra("USER_ID", meetups.getPostedBy() + ""));
                Log.d("USER_ID", meetups.getPostedBy() + "");
            }
        });
    }

    private boolean isReadyToComment() {
        return !txtComment.getText().toString().trim().equals("");
    }

    private String checkComment(String comment) {

        for (SensoredWords sensoredWords : SensoredWords.values()) {
            comment = comment.replaceAll("(?i)" + sensoredWords.name(), "****");
            Log.d("Words", sensoredWords.name());
        }

        return comment;
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ViewMeetupsActivity.this, NewsfeedActivity.class));
        finish();
    }

    private void loadComments() {

        adapter = new ArrayAdapter<>(ViewMeetupsActivity.this,
                android.R.layout.simple_list_item_1, meetups.listOfComments());

        listComments.setAdapter(adapter);
        listComments.setSelection(listComments.getAdapter().getCount() - 1);

        listComments.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                int itemPosition = position;

                String itemValue = (String) listComments.getItemAtPosition(position);

                Toast.makeText(getApplicationContext(),
                        "Position :" + itemPosition + "  ListItem : " + itemValue, Toast.LENGTH_LONG)
                        .show();
            }
        });
        //new RefreshComment().execute();
    }

    /*
        thread
     */


    class RetrieveMeetups extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ViewMeetupsActivity.this, R.style.progress);
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

                Log.d("Meetup id", meetupId + "");

                params.add(new BasicNameValuePair("id", meetupId));
                params.add(new BasicNameValuePair("filter", "invidual_join_user"));


                Log.d("request!", "starting");

                JSONObject json = jsonParser.makeHttpRequest(
                        RETRIEVE_MEETUPS_URL, "POST", params);

                Log.d("Fetching...", json.toString());

                success = json.getInt(TAG_STATUS);

                if (success == 1) {
                    Log.d("Success!", json.toString());

                    JSONArray jUserArray = json.getJSONArray("meetups");
                    JSONObject jUserObject;


                    jUserObject = jUserArray.getJSONObject(0);

                    meetups = new Meetups(jUserObject.getString("subject"),
                            jUserObject.getString("details"), jUserObject.getInt("posted_by"), jUserObject.getString("location"),
                            new Preference(Integer.parseInt(jUserObject.getString("pref_start_age")),
                                    Integer.parseInt(jUserObject.getString("pref_end_age")),
                                    jUserObject.getString("pref_gender").charAt(0)));

                    meetups.setId(jUserObject.getInt("id"));
                    meetups.setPostedDate(jUserObject.getString("posted_date"));
                    meetups.setPostedByName(jUserObject.getString("posted_by_user"));

                    Log.d("ID:", jUserObject.getInt("id") + "");

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

                    //check if the viewer is the user
                    if(currentUser.getId() == meetups.getPostedBy()) {
                        scrollView.setVisibility(View.VISIBLE);
                        lblSubject.setText(meetups.getSubject());
                        lblPostedDate.setText(meetups.getPostedDate());
                        lblDetails.setText(meetups.getDetails());
                        lblLocation.setText(meetups.getLocation());
                        lblPostedBy.setText(meetups.getPostedByName());

                        new RetrieveComments().execute();

                    } else {
                        //if not join comments will be disable
                        new RetrieveAttendees().execute();
                    }

                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }


    class CheckComments extends TimerTask {
        public void run() {
            new RetrieveComments().execute();
        }
    }


    class RetrieveAttendees extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ViewMeetupsActivity.this, R.style.progress);
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

                params.add(new BasicNameValuePair("id", currentUser.getId() + ""));
                params.add(new BasicNameValuePair("post_id", meetupId + ""));
                params.add(new BasicNameValuePair("type", "M"));

                Log.d("Params", currentUser.getId() + " " + meetupId);

                Log.d("request!", "starting");

                JSONObject json = jsonParser.makeHttpRequest(
                        RETRIEVE_ATTENDEES_URL, "POST", params);

                Log.d("Fetching...", json.toString());

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
                Toast.makeText(ViewMeetupsActivity.this, message + "!", Toast.LENGTH_SHORT).show();

                scrollView.setVisibility(View.VISIBLE);
                lblSubject.setText(meetups.getSubject());
                lblPostedDate.setText(meetups.getPostedDate());
                lblDetails.setText(meetups.getDetails());
                lblLocation.setText(meetups.getLocation());
                lblPostedBy.setText(meetups.getPostedByName());

                if (message.equals("Not yet joined.")) {

                    btnJoin.setVisibility(View.VISIBLE);
                    btnJoin.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            new InsertAttendees(new Attendees(meetups.getId(), 'M', currentUser.getId(),
                                    'N', "NOW()")).execute();
                        }
                    });

                    Log.d("Current user", currentUser.getId() + "");

                } else if (message.equals("Successful")) {
                    //check if there is comments
                    new RetrieveComments().execute();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }

    class RetrieveComments extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ViewMeetupsActivity.this, R.style.progress);
            pDialog.setCancelable(true);
            pDialog.setProgressStyle(android.R.style.Widget_Material_ProgressBar_Large);
        }

        @Override
        protected String doInBackground(String... userInfo) {
            // TODO Auto-generated method stub

            int success;

            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<>();

                params.add(new BasicNameValuePair("id", currentUser.getId() + ""));
                params.add(new BasicNameValuePair("post_id", meetupId + ""));
                params.add(new BasicNameValuePair("type", "M"));

                Log.d("Params", currentUser.getId() + " " + meetupId);

                Log.d("request!", "starting");

                JSONObject jsonObject = jsonParser.makeHttpRequest(
                        RETRIEVE_COMMENTS_URL, "POST", params);

                Log.d("JSON: ", jsonObject.toString());

                success = jsonObject.getInt(TAG_STATUS);

                if (success == 1) {
                    Log.d("Success!", jsonObject.toString());

                    JSONArray jsonArray = jsonObject.getJSONArray("comments");

                    List<Comments> comments = new ArrayList<>();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                        comments.add(new Comments(jsonObject1.getInt("id"), jsonObject1.getInt("post_id"),
                                jsonObject1.getString("post_type").charAt(0), jsonObject1.getInt("user_id"),
                                jsonObject1.getString("comment"), jsonObject1.getString("comment_date"),
                                jsonObject1.getString("user")));

                        Log.d("ID:", jsonObject1.getInt("id") + "");
                    }

                    meetups.setComments(comments);
                    //clear comments
                    //comments.clear();

                    return jsonObject.getString(TAG_RESPONSE);
                } else {
                    Log.d("Fetching failed...", jsonObject.getString(TAG_RESPONSE));
                    return jsonObject.getString(TAG_RESPONSE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }


        protected void onPostExecute(String message) {
            try {
                lblComments.setText("Comments");
                lblComments.setVisibility(View.VISIBLE);
                linearComments.setVisibility(View.VISIBLE);
                listComments.setVisibility(View.VISIBLE);

                if (message.equals("Successful")) {
                    loadComments();
                    //thread.sleep(13 * 1000);
                    //new RetrieveComments().execute();


                } else if (message.equals("No comments")) {
                    lblComments.setText("No Comments");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }


    class InsertComment extends AsyncTask<String, String, String> {

        Comments comments;

        public InsertComment(Comments comments) {
            this.comments = comments;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ViewMeetupsActivity.this, R.style.progress);
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

                params.add(new BasicNameValuePair("post_id", comments.getPostId() + ""));
                params.add(new BasicNameValuePair("post_type", comments.getPostType() + ""));
                params.add(new BasicNameValuePair("user_id", comments.getUserId() + ""));
                params.add(new BasicNameValuePair("comment", comments.getComment()));

                Log.d("request!", "starting");

                JSONObject json = jsonParser.makeHttpRequest(
                        INSERT_COMMENT_URL, "POST", params);

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
                    Toast.makeText(ViewMeetupsActivity.this, message + "!", Toast.LENGTH_SHORT).show();
                    new RetrieveComments().execute();


                    //add new notifications
                    if(currentUser.getId() != meetups.getPostedBy()) {

                        String details = currentUser.getFirstName() + " " + currentUser.getLastName()
                                + " commented to your meetups (" + meetups.getSubject() + ") ";


                        new InsertNotications(new Notification(meetups.getId(), meetups
                                .getPostedBy(), currentUser.getId(), Integer.parseInt(meetupId), 'M', details)).execute();
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    class InsertNotications extends AsyncTask<String, String, String> {

        Notification notification;

        public InsertNotications(Notification notification) {
            this.notification = notification;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ViewMeetupsActivity.this, R.style.progress);
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

                params.add(new BasicNameValuePair("user_id", notification.getUserId() + ""));
                params.add(new BasicNameValuePair("from_id", notification.getFromId() + ""));
                params.add(new BasicNameValuePair("post_comment_id", notification.getPostCommentId() + ""));
                params.add(new BasicNameValuePair("type", notification.getType() + ""));
                params.add(new BasicNameValuePair("details", notification.getDetails()));

                Log.d("request!", "starting");

                JSONObject json = jsonParser.makeHttpRequest(
                        INSERT_NOTIFICATIONS_URL, "POST", params);

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
                    Toast.makeText(ViewMeetupsActivity.this, message + "!", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    class InsertAttendees extends AsyncTask<String, String, String> {

        Attendees attendees;

        public InsertAttendees(Attendees attendees) {
            this.attendees = attendees;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ViewMeetupsActivity.this, R.style.progress);
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

                Log.d("USER_ID (user)", currentUser.getId() + "");

                params.add(new BasicNameValuePair("post_id", attendees.getPostId() + ""));
                params.add(new BasicNameValuePair("post_type", attendees.getPostType() + ""));
                params.add(new BasicNameValuePair("user_id", attendees.getUserId() + ""));
                params.add(new BasicNameValuePair("collaboration_status", attendees.getCollaborationStatus() + ""));
                params.add(new BasicNameValuePair("request_date", attendees.getRequestDate()));

                Log.d("request!", "starting");

                JSONObject json = jsonParser.makeHttpRequest(
                        INSERT_ATTENDEES_URL, "POST", params);

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
                    String details = currentUser.getFirstName() + " " + currentUser.getLastName()
                            + " wants to join the your meetups (" + meetups.getSubject() + ") ";

                    //need to insert the meetup
                    new InsertNotications(new Notification(meetups.getId(), meetups
                            .getPostedBy(), currentUser.getId(), Integer.parseInt(meetupId), 'M', details)).execute();

                    Toast.makeText(ViewMeetupsActivity.this, "Request sent.", Toast.LENGTH_SHORT);
                } else if(message.equals("Already sent a request.")) {
                    Toast.makeText(ViewMeetupsActivity.this, "Already sent a request", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

}
