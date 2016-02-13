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

import practiceandroidapplication.android.com.meetmeup.Entity.Group;
import practiceandroidapplication.android.com.meetmeup.Entity.GroupMember;
import practiceandroidapplication.android.com.meetmeup.Entity.Meetups;
import practiceandroidapplication.android.com.meetmeup.Entity.Network;
import practiceandroidapplication.android.com.meetmeup.Entity.Sessions;
import practiceandroidapplication.android.com.meetmeup.Entity.User;
import practiceandroidapplication.android.com.meetmeup.Handles.JSONParser;

public class ViewMembersAttendees extends AppCompatActivity {

    private static final String RETRIEVE_GROUP_MEMBERS_URL = Network.forDeploymentIp + "group_member_retrieve.php";

    private static final String TAG_STATUS = "status";
    private static final String TAG_RESPONSE = "response";

    JSONParser jsonParser = new JSONParser();
    ProgressDialog pDialog;

    ScrollView scrollView;
    LinearLayout listOfMembers;

    TextView lblMessage;

    User currentUser = Sessions.getSessionsInstance().currentUser;

    String groupId;
    ArrayAdapter<String> adapter;

    List<GroupMember> groupMembers = new ArrayList<>();

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


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        initUI();

        try {
            groupId = getIntent().getStringExtra("GROUP_ID");
            new RetrieveGroupMembers().execute();
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    public void initUI() {
        scrollView = (ScrollView) findViewById(R.id.scroll_view);
        lblMessage = (TextView) findViewById(R.id.lbl_message);
        lblMessage.setVisibility(View.GONE);
        //scrollView.setVisibility(View.GONE);
        listOfMembers = (LinearLayout) findViewById(R.id.linear_members);


    }


    public void displayGroupMembers() {

        listOfMembers.removeAllViews();

        for (GroupMember groupMember : groupMembers) {

            final LinearLayout.LayoutParams linearMeetups = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            linearMeetups.setMargins(0, 0, 0, 15);

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
            userLayout.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    final LinearLayout parent = (LinearLayout) v.getParent();

                    startActivity(new Intent(ViewMembersAttendees.this, ViewProfileActivity.class).putExtra("USER_ID", parent.getTag() + ""));
                    Log.d("USER_ID", parent.getTag() + "");
                }
            });

            Log.d("User ID", groupMember.getUserId() + "");

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
                        Network.forDeploymentIp + "group_member_retrieve.php", "POST", params);

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

}
