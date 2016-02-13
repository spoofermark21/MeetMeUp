package practiceandroidapplication.android.com.meetmeup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import practiceandroidapplication.android.com.meetmeup.Entity.Attendees;
import practiceandroidapplication.android.com.meetmeup.Entity.Comments;
import practiceandroidapplication.android.com.meetmeup.Entity.Events;
import practiceandroidapplication.android.com.meetmeup.Entity.Meetups;
import practiceandroidapplication.android.com.meetmeup.Entity.Network;
import practiceandroidapplication.android.com.meetmeup.Entity.Notification;
import practiceandroidapplication.android.com.meetmeup.Entity.SensoredWords;
import practiceandroidapplication.android.com.meetmeup.Entity.Sessions;
import practiceandroidapplication.android.com.meetmeup.Entity.User;
import practiceandroidapplication.android.com.meetmeup.Handles.JSONParser;

public class ViewEventsActivity extends AppCompatActivity {

    private static final String RETRIEVE_EVENT_URL = Network.forDeploymentIp + "events_retrieve.php";
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

    TextView lblEventName, lblPostedDate, lblDetails, lblLocation,
            lblPostedBy, lblComments, lblEventDate;

    ListView listComments;
    LinearLayout linearComments;

    EditText txtComment;

    Button btnJoin, btnComment, btnProfile, btnViewMembers;

    ScrollView scrollView;

    Events event;
    Attendees attendees;

    User currentUser = Sessions.getSessionsInstance().currentUser;

    String eventId;
    ArrayAdapter<String> adapter;

    boolean isExit = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_events);

        Toolbar toolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        toolBar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(ViewEventsActivity.this, NewsfeedActivity.class));
                finish();
                isExit = true;
            }
        });

        eventId = getIntent().getStringExtra("EVENTS_ID");

        initUI();

        new RetrieveEvent().execute();
    }

    public void initUI() {

        imgUser = (ImageView) findViewById(R.id.img_user);

        scrollView = (ScrollView) findViewById(R.id.scroll_view);
        scrollView.setVisibility(View.INVISIBLE);

        lblEventName = (TextView) findViewById(R.id.lbl_event_name);
        lblPostedDate = (TextView) findViewById(R.id.lbl_posted_date);
        lblDetails = (TextView) findViewById(R.id.lbl_details);
        lblLocation = (TextView) findViewById(R.id.lbl_location);
        lblPostedBy = (TextView) findViewById(R.id.lbl_posted_by);
        lblEventDate = (TextView) findViewById(R.id.lbl_date);

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
                    new InsertComment(new Comments(Integer.parseInt(eventId), 'E',
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
                startActivity(new Intent(ViewEventsActivity.this, ViewProfileActivity.class).putExtra("USER_ID", event.getPostedBy() + ""));
                Log.d("USER_ID", event.getPostedBy() + "");
            }
        });

        btnViewMembers = (Button) findViewById(R.id.btn_view_members);
        btnViewMembers.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent viewMembers = new Intent(ViewEventsActivity.this, ViewMembersAttendees.class);
                viewMembers.putExtra("POST_ID", eventId);
                viewMembers.putExtra("TYPE", "event");
                viewMembers.putExtra("POSTED_BY", event.getPostedBy() + "");

                startActivity(viewMembers);
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

    private void loadComments() {

        adapter = new ArrayAdapter<>(ViewEventsActivity.this,
                android.R.layout.simple_list_item_1, event.listOfComments());

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

    @Override
    public void onBackPressed() {
        //startActivity(new Intent(ViewMeetupsActivity.this, NewsfeedActivity.class));
        isExit = true;
        finish();
    }


    /*
        thread
     */

    class RetrieveEvent extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ViewEventsActivity.this, R.style.progress);
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
                List<NameValuePair> params = new ArrayList<>();

                Log.d("EVENT_ID (event)", eventId);

                params.add(new BasicNameValuePair("id", eventId));
                params.add(new BasicNameValuePair("filter", "individual_join_user"));


                Log.d("request!", "starting");

                JSONObject json = jsonParser.makeHttpRequest(
                        RETRIEVE_EVENT_URL, "POST", params);

                Log.d("Fetching...", json.toString());


                success = json.getInt(TAG_STATUS);

                if (success == 1) {
                    Log.d("Success!", json.toString());

                    JSONArray jUserArray = json.getJSONArray("events");
                    JSONObject jUserObject = jUserArray.getJSONObject(0);

                    event = new Events(jUserObject.getInt("id"), jUserObject.getString("event_name"),
                            jUserObject.getString("details"), jUserObject.getString("location"), jUserObject.getString("key"),
                            jUserObject.getString("start_date"), jUserObject.getString("end_date"));

                    Log.d("Event name", jUserObject.getString("event_name"));

                    event.setPostedDate(jUserObject.getString("posted_date"));
                    event.setPostedBy(jUserObject.getInt("posted_by"));
                    event.setPostedByName(jUserObject.getString("posted_by_user"));


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
                    if(currentUser.getId() == event.getPostedBy()) {
                        scrollView.setVisibility(View.VISIBLE);

                        lblEventName.setText(event.getEventName());
                        lblPostedDate.setText(event.getPostedDate());
                        lblDetails.setText(event.getDetails());
                        lblLocation.setText(event.getLocation());
                        lblPostedBy.setText(event.getPostedByName());

                        Log.d("Event date", event.getStartDate() + " - "  + event.getEndDate());
                        lblEventDate.setText(event.getStartDate() + " - "  + event.getEndDate());

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

    class InsertComment extends AsyncTask<String, String, String> {

        Comments comments;

        public InsertComment(Comments comments) {
            this.comments = comments;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ViewEventsActivity.this, R.style.progress);
            pDialog.setCancelable(true);
            pDialog.setProgressStyle(android.R.style.Widget_Material_ProgressBar_Large);
            //pDialog.show();
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
            //pDialog.dismiss();
            try {
                if (message.equals("Successful")) {
                    Toast.makeText(ViewEventsActivity.this, message + "!", Toast.LENGTH_SHORT).show();
                    new RetrieveComments().execute();


                    //add new notifications
                    if(currentUser.getId() != event.getPostedBy()) {

                        String details = currentUser.getFirstName() + " " + currentUser.getLastName()
                                + " commented to your event (" + event.getEventName() + ") ";

                        new InsertNotications(new Notification(event.getId(), event.getPostedBy(),
                                currentUser.getId(), Integer.parseInt(eventId), comments.getPostType(), details)).execute();
                    }
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
            pDialog = new ProgressDialog(ViewEventsActivity.this, R.style.progress);
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
                params.add(new BasicNameValuePair("post_id", eventId + ""));
                params.add(new BasicNameValuePair("type", "E"));

                Log.d("Params", currentUser.getId() + " " + eventId);

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

                    event.setComments(comments);
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
                    if(!isExit) {
                        new RetrieveComments().execute();
                    }

                } else if (message.equals("No comments")) {
                    lblComments.setText("No Comments");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }

    class RetrieveAttendees extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ViewEventsActivity.this, R.style.progress);
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
                params.add(new BasicNameValuePair("post_id", eventId + ""));
                params.add(new BasicNameValuePair("type", "E"));

                Log.d("Params", currentUser.getId() + " " + eventId);

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
                Toast.makeText(ViewEventsActivity.this, message + "!", Toast.LENGTH_SHORT).show();

                scrollView.setVisibility(View.VISIBLE);
                lblEventName.setText(event.getEventName());
                lblPostedDate.setText(event.getPostedDate());
                lblDetails.setText(event.getDetails());
                lblLocation.setText(event.getLocation());
                lblPostedBy.setText(event.getPostedByName());
                lblEventDate.setText(event.getStartDate() + " - "  + event.getEndDate());

                if (message.equals("Not yet joined.")) {

                    btnJoin.setVisibility(View.VISIBLE);
                    btnJoin.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            new InsertAttendees(new Attendees(event.getId(), 'E', currentUser.getId(),
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

    class InsertAttendees extends AsyncTask<String, String, String> {

        Attendees attendees;

        public InsertAttendees(Attendees attendees) {
            this.attendees = attendees;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ViewEventsActivity.this, R.style.progress);
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
                            + " wants to join the your events (" + event.getEventName() + ") ";

                    //need to insert the meetup
                    new InsertNotications(new Notification(event.getId(), event.getPostedBy(),
                            currentUser.getId(), Integer.parseInt(eventId), 'E', details)).execute();

                    Toast.makeText(ViewEventsActivity.this, "Request sent.", Toast.LENGTH_SHORT);
                } else if(message.equals("Already sent a request.")) {
                    Toast.makeText(ViewEventsActivity.this, "Already sent a request" + attendees.getPostId()
                            + attendees.getPostType() + attendees.getUserId(), Toast.LENGTH_SHORT).show();
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
            pDialog = new ProgressDialog(ViewEventsActivity.this, R.style.progress);
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
                    Toast.makeText(ViewEventsActivity.this, message + "!", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

}
