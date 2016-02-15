package practiceandroidapplication.android.com.meetmeup;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

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

import practiceandroidapplication.android.com.meetmeup.Entity.Attendees;
import practiceandroidapplication.android.com.meetmeup.Entity.Group;
import practiceandroidapplication.android.com.meetmeup.Entity.GroupMember;
import practiceandroidapplication.android.com.meetmeup.Entity.Meetups;
import practiceandroidapplication.android.com.meetmeup.Entity.Network;
import practiceandroidapplication.android.com.meetmeup.Entity.Sessions;
import practiceandroidapplication.android.com.meetmeup.Entity.User;
import practiceandroidapplication.android.com.meetmeup.Handles.Interactions;
import practiceandroidapplication.android.com.meetmeup.Handles.JSONParser;

public class ViewMembersAttendees extends AppCompatActivity {

    private static final String RETRIEVE_GROUP_MEMBERS_URL = Network.forDeploymentIp + "group_member_retrieve.php";
    private static final String RETRIEVE_ATTENDEES_URL = Network.forDeploymentIp + "retrieve_attendees.php";
    private static final String LEAVE_URL = Network.forDeploymentIp + "leave.php";

    private static final String TAG_STATUS = "status";
    private static final String TAG_RESPONSE = "response";

    JSONParser jsonParser = new JSONParser();
    ProgressDialog pDialog;

    ScrollView scrollView;
    LinearLayout listOfMembers;

    TextView lblMessage;

    User currentUser = Sessions.getSessionsInstance().currentUser;

    String groupId, postId, type, postedBy;

    List<GroupMember> groupMembers = new ArrayList<>();
    List<Attendees> listAttendees = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_members_attendees);

        Toolbar toolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        toolBar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        postedBy = getIntent().getStringExtra("POSTED_BY");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Details", "ID: " + groupId + " Type " + type);

                if(type.equals("group")) {
                    groupId = getIntent().getStringExtra("GROUP_ID");
                    new RetrieveGroupMembers().execute();
                } else if(type.equals("meetup")) {
                    // do meetup
                    type = "M";
                    postId = getIntent().getStringExtra("POST_ID");
                    new RetrieveAttendees().execute();
                } else if (type.equals("event")){
                    // do event
                    type = "E";
                    postId = getIntent().getStringExtra("POST_ID");

                    new RetrieveAttendees().execute();
                }
            }
        });

        initUI();

        try {
            type = getIntent().getStringExtra("TYPE");

            //Log.d("Details", "ID: " + groupId + " Type " + type);

            if(type.equals("group")) {
                groupId = getIntent().getStringExtra("GROUP_ID");
                new RetrieveGroupMembers().execute();
            } else if(type.equals("meetup")) {
                // do meetup
                type = "M";
                postId = getIntent().getStringExtra("POST_ID");
                new RetrieveAttendees().execute();
            } else if (type.equals("event")){
                // do event
                type = "E";
                postId = getIntent().getStringExtra("POST_ID");
                new RetrieveAttendees().execute();
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    public void initUI() {
        scrollView = (ScrollView) findViewById(R.id.scroll_view);
        lblMessage = (TextView) findViewById(R.id.lbl_message);
        lblMessage.setVisibility(View.GONE);
        listOfMembers = (LinearLayout) findViewById(R.id.linear_members);
    }


    public void displayGroupMembers() {

        listOfMembers.removeAllViews();

        for (GroupMember groupMember : groupMembers) {

            final LinearLayout.LayoutParams linearMeetups = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            linearMeetups.setMargins(0, 0, 0, 15);

            Log.d("User ID", groupMember.getUserId() + "");

            LinearLayout recordOfMembers = new LinearLayout(this);
            recordOfMembers.setLayoutParams(linearMeetups);
            recordOfMembers.setOrientation(LinearLayout.VERTICAL);
            recordOfMembers.setBackgroundResource(R.drawable.main_background);
            recordOfMembers.setPadding(20, 20, 20, 5);
            recordOfMembers.setTag(groupMember.getUserId());

            final LinearLayout.LayoutParams linearPostedBy = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            linearPostedBy.setMargins(0, 0, 0, 30);

            LinearLayout userLayout = new LinearLayout(this);
            userLayout.setLayoutParams(linearPostedBy);
            userLayout.setOrientation(LinearLayout.HORIZONTAL);
            userLayout.setTag(groupMember.getGroupId());
            userLayout.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    final LinearLayout parent = (LinearLayout) v.getParent();
                    final String post = v.getTag() + "";

                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(ViewMembersAttendees.this);
                    dlgAlert.setMessage("Options");
                    dlgAlert.setCancelable(true);

                    dlgAlert.setPositiveButton("View Profile",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(ViewMembersAttendees.this, ViewProfileActivity.class).putExtra("USER_ID", parent.getTag() + ""));
                                    //Interactions.showError(userId , ViewMeetupsActivity.this);
                                }
                            });

                    if (Integer.parseInt(postedBy) == currentUser.getId()) {
                        dlgAlert.setNegativeButton("Delete member",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        new LeaveGroups(groupId, parent.getTag() + "").execute();
                                        listOfMembers.removeView(parent);
                                    }
                                });
                    }


                    dlgAlert.create().show();
                }
            });



            final LinearLayout.LayoutParams userImageLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            userImageLayout.weight = 1.0f;
            userImageLayout.height = 80;
            userImageLayout.width = 80;
            userImageLayout.setMargins(0, 0, 10, 0);
            userImageLayout.gravity = Gravity.LEFT;

            final ImageView userImage = new ImageView(this);
            userImage.setBackgroundColor(Color.parseColor("#E6E9ED"));

            final TextView userName = new TextView(this);
            userName.setText(groupMember.getUserName());
            userName.setTextColor(Color.BLACK);
            userName.setTextSize(17);


            if (!groupMember.getUserImage().equals("null") && !groupMember.getUserImage().equals("")) {

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
                        try {
                            if (bitmap != null) {
                                Log.d("Image", "Success");
                                userImage.setImageBitmap(bitmap);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }

                }

                new DownloadUserImage(groupMember.getUserImage() + ".JPG").execute();
                userImage.setLayoutParams(userImageLayout);
                userImage.setScaleType(ImageView.ScaleType.FIT_XY);
            }

            userLayout.addView(userImage);
            userLayout.addView(userName);

            recordOfMembers.addView(userLayout);
            listOfMembers.addView(recordOfMembers);
        }
    }

    public void displayEventAttendees() {

        listOfMembers.removeAllViews();

        for (Attendees attendees : listAttendees) {

            final LinearLayout.LayoutParams linearMeetups = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            linearMeetups.setMargins(0, 0, 0, 15);

            LinearLayout recordOfMembers = new LinearLayout(this);
            recordOfMembers.setLayoutParams(linearMeetups);
            recordOfMembers.setOrientation(LinearLayout.VERTICAL);
            recordOfMembers.setBackgroundResource(R.drawable.main_background);
            recordOfMembers.setPadding(20, 20, 20, 5);
            recordOfMembers.setTag(attendees.getUserId());

            final LinearLayout.LayoutParams linearPostedBy = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            linearPostedBy.setMargins(0, 0, 0, 30);

            LinearLayout userLayout = new LinearLayout(this);
            userLayout.setLayoutParams(linearPostedBy);
            userLayout.setOrientation(LinearLayout.HORIZONTAL);
            userLayout.setTag(attendees.getPostId());

            Log.d("POST ID", attendees.getPostId() + "");

            userLayout.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    final LinearLayout parent = (LinearLayout) v.getParent();

                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(ViewMembersAttendees.this);
                    dlgAlert.setMessage("Options");
                    dlgAlert.setCancelable(true);

                    dlgAlert.setPositiveButton("View Profile",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(ViewMembersAttendees.this, ViewProfileActivity.class).putExtra("USER_ID", parent.getTag() + ""));
                                }
                            });

                    if (Integer.parseInt(postedBy) == currentUser.getId() && type == "M") {
                        dlgAlert.setNegativeButton("Remove attendees",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        new LeaveMeetups(postId, parent.getTag() + "").execute();
                                        Interactions.showError(postId + " " +
                                                parent.getTag() + "", ViewMembersAttendees.this);
                                        listOfMembers.removeView(parent);
                                    }
                                });
                    }

                    if (Integer.parseInt(postedBy) == currentUser.getId() && type == "E") {
                        dlgAlert.setNegativeButton("Remove attendees",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        new LeaveEvents(postId, parent.getTag() + "").execute();
                                        listOfMembers.removeView(parent);
                                    }
                                });
                    }
                    dlgAlert.create().show();
                }
            });

            Log.d("User ID", attendees.getUserId() + "");

            final LinearLayout.LayoutParams userImageLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            userImageLayout.weight = 1.0f;
            userImageLayout.height = 80;
            userImageLayout.width = 80;
            userImageLayout.setMargins(0, 0, 10, 0);
            userImageLayout.gravity = Gravity.LEFT;

            final ImageView userImage = new ImageView(this);
            userImage.setBackgroundColor(Color.parseColor("#E6E9ED"));

            final TextView userName = new TextView(this);
            userName.setText(attendees.getUserName());
            userName.setTextColor(Color.BLACK);
            userName.setTextSize(17);
            userName.setTag(attendees.getPostId());


            if (!attendees.getUserImage().equals("null") && !attendees.getUserImage().equals("")) {

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
                        try {
                            if (bitmap != null) {
                                Log.d("Image", "Success");
                                userImage.setImageBitmap(bitmap);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }

                }

                new DownloadUserImage(attendees.getUserImage() + ".JPG").execute();
                userImage.setLayoutParams(userImageLayout);
                userImage.setScaleType(ImageView.ScaleType.FIT_XY);
            }

            userLayout.addView(userImage);
            userLayout.addView(userName);

            recordOfMembers.addView(userLayout);
            listOfMembers.addView(recordOfMembers);
        }
    }


    /*
        thread
     */

    class RetrieveGroupMembers extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ViewMembersAttendees.this, R.style.progress);
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
                List<NameValuePair> params = new ArrayList<>();

                Log.d("USER_ID (user)", currentUser.getId() + " Members " + groupId);

                params.add(new BasicNameValuePair("group_id", groupId + ""));
                //params.add(new BasicNameValuePair("query_type", "individual_join_user"));

                Log.d("request!", "starting to " + RETRIEVE_GROUP_MEMBERS_URL);

                JSONObject json = jsonParser.makeHttpRequest(
                        RETRIEVE_GROUP_MEMBERS_URL, "POST", params);

                Log.d("Fetching...", json.toString());

                success = json.getInt(TAG_STATUS);


                if (success == 1) {
                    Log.d("Success!", json.toString());

                    JSONArray jsonArray = json.getJSONArray("group_members");
                    JSONObject jsonObject;

                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);

                        groupMembers.add(new GroupMember(jsonObject.getInt("id_user"),
                                jsonObject.getString("first_name") + " " +
                                        jsonObject.getString("last_name"), jsonObject.getString("user_image")));

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
                scrollView.setVisibility(View.VISIBLE);
                if (message.equals("Successful")) {
                    displayGroupMembers();
                } else {
                    lblMessage.setVisibility(View.VISIBLE);
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
            pDialog = new ProgressDialog(ViewMembersAttendees.this, R.style.progress);
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
                List<NameValuePair> params = new ArrayList<>();

                Log.d("USER_ID (user)", currentUser.getId() + " Attendees " + postId);

                params.add(new BasicNameValuePair("post_type", type));
                params.add(new BasicNameValuePair("post_id", postId));


                JSONObject json = jsonParser.makeHttpRequest(
                        RETRIEVE_ATTENDEES_URL, "POST", params);

                Log.d("Fetching...", json.toString());

                success = json.getInt(TAG_STATUS);


                if (success == 1) {
                    Log.d("Success!", json.toString());

                    JSONArray jsonArray = json.getJSONArray("attendees");
                    JSONObject jsonObject;

                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);

                        listAttendees.add(new Attendees(jsonObject.getInt("id_user"),
                                jsonObject.getString("first_name") + " " +
                                        jsonObject.getString("last_name"), jsonObject.getString("user_image")));


                        /*//get who posted the meetup
                        if(jsonObject.getString("posted_by").equals("null") &&
                                !jsonObject.getString("posted_by").equals(""))  {
                            postedBy = jsonObject.getInt("posted_by");
                        }*/

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
                scrollView.setVisibility(View.VISIBLE);
                if (message.equals("Successful")) {
                    displayEventAttendees();
                } else {
                    lblMessage.setVisibility(View.VISIBLE);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }

    class LeaveGroups extends AsyncTask<String, String, String> {

        String groupId;
        String userId;

        public LeaveGroups(String groupId, String userId) {
            this.groupId = groupId;
            this.userId = userId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ViewMembersAttendees.this, R.style.progress);
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

                params.add(new BasicNameValuePair("id", groupId));
                params.add(new BasicNameValuePair("user_id", userId));
                params.add(new BasicNameValuePair("query_type", "group"));

                Log.d("Leave details", "Group id " + groupId + " User id " + userId);

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
                    Toast.makeText(ViewMembersAttendees.this, message + "!", Toast.LENGTH_SHORT).show();
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
            pDialog = new ProgressDialog(ViewMembersAttendees.this, R.style.progress);
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
                    Toast.makeText(ViewMembersAttendees.this, message + "!", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    class LeaveEvents extends AsyncTask<String, String, String> {

        String eventId;
        String userId;

        public LeaveEvents(String eventId, String userId) {
            this.eventId = eventId;
            this.userId = userId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ViewMembersAttendees.this, R.style.progress);
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

                params.add(new BasicNameValuePair("id", eventId));
                params.add(new BasicNameValuePair("user_id", userId));
                params.add(new BasicNameValuePair("query_type", "attendees"));
                params.add(new BasicNameValuePair("type", "E"));

                Log.d("Leave details", "Meetup id " + eventId + " User id " + userId);

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
                    Toast.makeText(ViewMembersAttendees.this, message + "!", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }


}
