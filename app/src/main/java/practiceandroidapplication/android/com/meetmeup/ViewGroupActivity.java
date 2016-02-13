package practiceandroidapplication.android.com.meetmeup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
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

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import practiceandroidapplication.android.com.meetmeup.Entity.Attendees;
import practiceandroidapplication.android.com.meetmeup.Entity.Comments;
import practiceandroidapplication.android.com.meetmeup.Entity.Group;
import practiceandroidapplication.android.com.meetmeup.Entity.GroupMember;
import practiceandroidapplication.android.com.meetmeup.Entity.Meetups;
import practiceandroidapplication.android.com.meetmeup.Entity.Network;
import practiceandroidapplication.android.com.meetmeup.Entity.Notification;
import practiceandroidapplication.android.com.meetmeup.Entity.Sessions;
import practiceandroidapplication.android.com.meetmeup.Entity.User;
import practiceandroidapplication.android.com.meetmeup.Handles.JSONParser;

public class ViewGroupActivity extends AppCompatActivity {

    private static final String RETRIEVE_GROUP_URL = Network.forDeploymentIp + "group_retrieve.php";
    private static final String RETRIEVE_MEMBERS_URL = Network.forDeploymentIp + "members_retrieve.php";

    private static final String INSERT_NOTIFICATIONS_URL = Network.forDeploymentIp + "notication_save.php";
    private static final String INSERT_GROUP_MEMBER = Network.forDeploymentIp + "insert_group_member.php";

    private static final String TAG_STATUS = "status";
    private static final String TAG_RESPONSE = "response";

    JSONParser jsonParser = new JSONParser();
    ProgressDialog pDialog;

    ImageView imgGroup;

    TextView lblGroupName, lblPostedDate, lblDetails,
            lblPostedBy;

    Button btnJoin, btnProfile, btnViewMembers;

    ScrollView scrollView;

    User currentUser = Sessions.getSessionsInstance().currentUser;

    String groupId;
    ArrayAdapter<String> adapter;

    Group group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_group);

        Toolbar toolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        toolBar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(ViewGroupActivity.this, NewsfeedActivity.class));
                finish();
            }
        });

        groupId = getIntent().getStringExtra("GROUP_ID");
        initUI();
        new RetrieveGroups().execute();
    }

    protected void initUI() {
        imgGroup = (ImageView) findViewById(R.id.img_group);

        scrollView = (ScrollView) findViewById(R.id.scroll_view);
        scrollView.setVisibility(View.GONE);

        lblGroupName = (TextView) findViewById(R.id.lbl_group_name);
        lblPostedDate = (TextView) findViewById(R.id.lbl_posted_date);
        lblDetails = (TextView) findViewById(R.id.lbl_details);
        lblPostedBy = (TextView) findViewById(R.id.lbl_posted_by);

        btnJoin = (Button) findViewById(R.id.btn_join);
        btnJoin.setVisibility(View.GONE);

        btnProfile = (Button) findViewById(R.id.btn_view_profile);
        btnProfile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(ViewGroupActivity.this, ViewProfileActivity.class).putExtra("USER_ID", group.getCreatedBy() + ""));
                Log.d("USER_ID", group.getCreatedBy() + "");
            }
        });

        btnViewMembers = (Button) findViewById(R.id.btn_view_members);
        btnViewMembers.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent viewMembers = new Intent(ViewGroupActivity.this, ViewMembersAttendees.class);
                viewMembers.putExtra("GROUP_ID", groupId);
                viewMembers.putExtra("TYPE", "group");
                startActivity(viewMembers);
            }
        });
    }

    class RetrieveGroups extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ViewGroupActivity.this, R.style.progress);
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

                Log.d("USER_ID (user)", currentUser.getId() + "" + groupId);
                params.add(new BasicNameValuePair("id", groupId + ""));
                params.add(new BasicNameValuePair("query_type", "individual_join_user"));

                Log.d("request!", "starting");

                JSONObject json = jsonParser.makeHttpRequest(
                        RETRIEVE_GROUP_URL, "POST", params);

                Log.d("Fetching...", json.toString());

                success = json.getInt(TAG_STATUS);


                if (success == 1) {
                    Log.d("Success!", json.toString());

                    JSONArray jsonArray = json.getJSONArray("group");
                    JSONObject jsonObject = jsonArray.getJSONObject(0);

                    group = new Group(jsonObject.getInt("id"),
                            jsonObject.getString("group_name"), jsonObject.getString("details"),
                            jsonObject.getInt("created_by"), jsonObject.getInt("count_members"), jsonObject.getString("group_image"),
                            jsonObject.getString("created_by_user"), jsonObject.getString("user_image"));

                    group.setCreatedDate(jsonObject.getString("created_date"));

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

                    if(currentUser.getId() == group.getCreatedBy()) {
                        scrollView.setVisibility(View.VISIBLE);
                        lblGroupName.setText(group.getGroupName());
                        lblPostedDate.setText(group.getCreatedDate());
                        lblDetails.setText(group.getDetails());
                        lblPostedBy.setText(group.getCreatedByName());

                        if(!group.getGroupImage().equals("") && !group.getGroupImage().equals("null")) {
                            new DownloadGroupImage(group.getGroupImage() + ".JPG").execute();
                        }

                    } else {
                        //if not join comments will be disable
                        new RetrieveMembers().execute();
                    }

                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }

    class RetrieveMembers extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ViewGroupActivity.this, R.style.progress);
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

                params.add(new BasicNameValuePair("id", groupId + ""));
                params.add(new BasicNameValuePair("user_id", currentUser.getId() + ""));
                params.add(new BasicNameValuePair("query_type", "is_member"));

                Log.d("Params", currentUser.getId() + " " + groupId);

                Log.d("request!", "starting");

                JSONObject json = jsonParser.makeHttpRequest(
                        RETRIEVE_MEMBERS_URL, "POST", params);

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
                Toast.makeText(ViewGroupActivity.this, message + "!", Toast.LENGTH_SHORT).show();

                scrollView.setVisibility(View.VISIBLE);

                lblGroupName.setText(group.getGroupName());
                lblPostedDate.setText(group.getCreatedDate());
                lblDetails.setText(group.getDetails());
                lblPostedBy.setText(group.getCreatedByName());
                new DownloadGroupImage(group.getGroupImage() + ".JPG").execute();

                if (message.equals("Not yet joined.")) {

                    btnJoin.setVisibility(View.VISIBLE);
                    btnJoin.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            new InsertMembers(new GroupMember(group.getId(), currentUser.getId())).execute();
                        }
                    });

                    Log.d("Current user", currentUser.getId() + "");

                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }


        class DownloadGroupImage extends AsyncTask<Void, Void, Bitmap> {

            String filename;

            public DownloadGroupImage(String filename) {
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
                    final String USER_IMAGE_URL = Network.forDeploymentIp + "meetmeup/uploads/groups/" + this.filename;
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
                    if(bitmap!=null) {
                        Log.d("Image", "Success");
                        imgGroup.setImageBitmap(bitmap);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

        }

    class InsertMembers extends AsyncTask<String, String, String> {

        GroupMember groupMember;

        public InsertMembers(GroupMember groupMember) {
            this.groupMember = groupMember;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ViewGroupActivity.this, R.style.progress);
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

                params.add(new BasicNameValuePair("user_id", currentUser.getId() + ""));
                params.add(new BasicNameValuePair("group_id", group.getId() + ""));

                Log.d("User and Group", currentUser.getId() + " " + group.getId());

                Log.d("request!", "starting");

                JSONObject json = jsonParser.makeHttpRequest(
                        INSERT_GROUP_MEMBER, "POST", params);

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
                            + " wants to join the your group (" + group.getGroupName() + ") ";

                    //need to insert the meetup
                    new InsertNotications(new Notification(group.getId(), group.getCreatedBy()
                            , currentUser.getId(), Integer.parseInt(groupId), 'G', details)).execute();

                    Toast.makeText(ViewGroupActivity.this, "Request sent.", Toast.LENGTH_SHORT);
                } else if(message.equals("Already sent a request.")) {
                    Toast.makeText(ViewGroupActivity.this, "Already sent a request", Toast.LENGTH_SHORT).show();
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
            pDialog = new ProgressDialog(ViewGroupActivity.this, R.style.progress);
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
                    Toast.makeText(ViewGroupActivity.this, message + "!", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

}
