package practiceandroidapplication.android.com.meetmeup;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import practiceandroidapplication.android.com.meetmeup.Entity.Group;
import practiceandroidapplication.android.com.meetmeup.Entity.Network;
import practiceandroidapplication.android.com.meetmeup.Entity.Sessions;
import practiceandroidapplication.android.com.meetmeup.Entity.User;
import practiceandroidapplication.android.com.meetmeup.Handles.JSONParser;

public class GroupActivity extends AppCompatActivity {

    private static final String RETRIEVE_GROUP_URL = Network.forDeploymentIp + "group_retrieve.php";
    private static final String UPDATE_GROUP_URL = Network.forDeploymentIp + "group_update.php";
    private static final String LEAVE_URL = Network.forDeploymentIp + "leave.php";

    private static final String TAG_STATUS = "status";
    private static final String TAG_RESPONSE = "response";

    JSONParser jsonParser = new JSONParser();
    ProgressDialog pDialog;

    Toolbar toolbar;

    TextView lblMessage;

    LinearLayout listOfGroups;

    Sessions sessions = Sessions.getSessionsInstance();
    List<Group> currentGroups = new ArrayList<>();
    User currentUser = Sessions.getSessionsInstance().currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        //listGroup = (ListView) findViewById(R.id.list_group);
        listOfGroups = (LinearLayout) findViewById(R.id.linear_groups);
        listOfGroups.setVisibility(View.INVISIBLE);

        lblMessage = (TextView) findViewById(R.id.lbl_message);
        lblMessage.setVisibility(View.INVISIBLE);

        new RetrieveGroups().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.group_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_create) {
            startActivity(new Intent(GroupActivity.this, CreateGroupActivity.class));
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void displayGroups() {

        for (Group group : currentGroups) {

            LinearLayout recordOfGroups = new LinearLayout(this);
            recordOfGroups.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            recordOfGroups.setOrientation(LinearLayout.VERTICAL);
            //recordOfGroups.setPadding(10, 10, 10, 10);
            //recordOfEvents.setBackgroundColor(getResources().getColor(R.color.colorMainBackground));
            //recordOfEvents.setBackgroundResource(R.drawable.edit_text);

            Log.d("Group name", group.getGroupName());
            recordOfGroups.setTag(group.getId());

            /*final LinearLayout.LayoutParams imageGroupParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
            //imageGroupParams.weight = 1.0f;
            imageGroupParams.height = 150;
            imageGroupParams.width = 150;
            imageGroupParams.gravity = Gravity.LEFT;
            //imageGroupParams.leftMargin = 50;

            final ImageView groupImage = new ImageView(this);
            groupImage.setImageResource(R.drawable.meetmeup);
            groupImage.setLayoutParams(imageGroupParams);*/

            final TextView groupName = new TextView(this);
            groupName.setText(group.getGroupName());
            groupName.setTextSize(25);
            groupName.setTextColor(Color.BLACK);

            final TextView groupDetails = new TextView(this);
            groupDetails.setText(group.getDetails());
            groupDetails.setTextSize(15);
            groupDetails.setTextColor(Color.BLACK);

            final TextView groupCreatedBy = new TextView(this);
            groupCreatedBy.setText("Created by: " + group.getCreatedByName());
            groupCreatedBy.setTextSize(15);
            groupCreatedBy.setTextColor(Color.BLACK);

            final TextView groupCountMembers = new TextView(this);
            groupCountMembers.setText(group.getTotalMembers() + " members");
            groupCountMembers.setTextSize(15);
            groupCountMembers.setTextColor(Color.BLACK);

            final LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
            params2.weight = 1.0f;
            params2.gravity = Gravity.RIGHT;
            params2.leftMargin = 50;

            final LinearLayout.LayoutParams btnLayout = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
            btnLayout.weight = 1.0f;
            btnLayout.setMargins(10, 10, 10, 10);

            LinearLayout options = new LinearLayout(this);
            options.setOrientation(LinearLayout.HORIZONTAL);
            options.setPadding(10, 10, 10, 10);
            options.setLayoutParams(params2);

            final TextView view = new TextView(this);
            view.setLayoutParams(btnLayout);
            view.setPadding(10, 10, 0, 10);
            view.setText("view");
            view.setTextSize(15);
            view.setBackgroundColor(Color.TRANSPARENT);

            //final ImageButton edit = new ImageButton(this);
            //edit.setImageResource(R.drawable.ic_mode_edit_black_24dp);
            final TextView edit = new TextView(this);
            edit.setLayoutParams(btnLayout);
            edit.setPadding(10, 10, 0, 10);
            edit.setText("edit");
            edit.setTextSize(15);
            edit.setBackgroundColor(Color.TRANSPARENT);

            /*final ImageButton delete = new ImageButton(this);
            delete.setImageResource(R.drawable.ic_delete_black_24dp);
            delete.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            delete.setPadding(10, 10, 0, 10);
            delete.setBackgroundColor(Color.TRANSPARENT);*/
            final TextView delete = new TextView(this);
            delete.setLayoutParams(btnLayout);
            delete.setPadding(10, 10, 0, 10);
            delete.setText("delete");
            delete.setTextSize(15);
            delete.setBackgroundColor(Color.TRANSPARENT);
            delete.setTextColor(Color.parseColor("#D46A6A"));

            final TextView leave = new TextView(this);
            leave.setLayoutParams(btnLayout);
            leave.setPadding(10, 10, 0, 10);
            leave.setText("leave");
            leave.setTextSize(15);
            leave.setGravity(Gravity.CENTER);
            leave.setBackgroundColor(Color.TRANSPARENT);
            leave.setTextColor(Color.parseColor("#D46A6A"));


            view.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    final LinearLayout parent = (LinearLayout) v.getParent().getParent();

                    Intent group = new Intent(GroupActivity.this, ViewGroupActivity.class);
                    group.putExtra("GROUP_ID", parent.getTag() + "");
                    startActivity(group);

                }
            });

            edit.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    final LinearLayout parent = (LinearLayout) v.getParent().getParent();

                    Intent group = new Intent(GroupActivity.this, EditGroupActivity.class);
                    group.putExtra("GROUP_ID", parent.getTag() + "");
                    startActivity(group);
                    finish();

                    Toast.makeText(GroupActivity.this, parent.getTag() + "!", Toast.LENGTH_SHORT).show();

                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {

                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(GroupActivity.this);
                    dlgAlert.setMessage("Are you sure to delete this event?");
                    dlgAlert.setTitle("Warning!");
                    dlgAlert.setCancelable(true);

                    dlgAlert.setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    Toast.makeText(GroupActivity.this, "Deleted!", Toast.LENGTH_SHORT).show();

                                    final LinearLayout parent = (LinearLayout) v.getParent().getParent();

                                    String groupId = parent.getTag() + "";

                                    listOfGroups.removeView(parent);
                                    new DisableGroup(groupId).execute();

                                    Log.d("Group count", listOfGroups.getChildCount() + "");

                                    if (listOfGroups.getChildCount() == 1)
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

                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(GroupActivity.this);
                    dlgAlert.setMessage("Are you sure to leave this Group?");
                    dlgAlert.setTitle("Warning!");
                    dlgAlert.setCancelable(true);
                    dlgAlert.setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(GroupActivity.this, "Successful!", Toast.LENGTH_SHORT).show();

                                    final LinearLayout parent = (LinearLayout) v.getParent().getParent();

                                    String eventId = parent.getTag() + "";

                                    listOfGroups.removeView(parent);

                                    new LeaveGroups(eventId, currentUser.getId() + "").execute();

                                    if (listOfGroups.getChildCount() == 1)
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

            options.addView(view);

            //recordOfGroups.addView(groupImage);
            recordOfGroups.addView(groupName);
            recordOfGroups.addView(groupDetails);

            if(group.getCreatedBy() == currentUser.getId()) {
                options.addView(edit);
                options.addView(delete);
            } else {
                options.addView(leave);
                recordOfGroups.addView(groupCreatedBy);
            }

            recordOfGroups.addView(groupCountMembers);

            recordOfGroups.addView(options);

            listOfGroups.addView(recordOfGroups);
        }
    }

    /*
        thread
     */

    class RetrieveGroups extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(GroupActivity.this, R.style.progress);
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
                List<NameValuePair> params = new ArrayList<NameValuePair>();

                Log.d("USER_ID (user)", currentUser.getId() + "");
                params.add(new BasicNameValuePair("id", currentUser.getId() + ""));
                params.add(new BasicNameValuePair("query_type", "all"));

                Log.d("request!", "starting");

                JSONObject json = jsonParser.makeHttpRequest(
                        RETRIEVE_GROUP_URL, "POST", params);

                Log.d("Fetching...", json.toString());

                success = json.getInt(TAG_STATUS);

                if (success == 1) {
                    Log.d("Success!", json.toString());

                    JSONArray jUserArray = json.getJSONArray("group");
                    JSONObject jUserObject;

                    currentGroups.clear();

                    for(int i=0; i < jUserArray.length();i++){
                        jUserObject = jUserArray.getJSONObject(i);

                        currentGroups.add(new Group(jUserObject.getInt("id"),
                                jUserObject.getString("group_name"), jUserObject.getString("details"),
                                jUserObject.getInt("created_by"), jUserObject.getString("created_date"),
                                jUserObject.getString("created_by_user"),jUserObject.getInt("count_members")));

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
                listOfGroups.setVisibility(View.VISIBLE);
                if (message.equals("Successful")) {
                    Toast.makeText(GroupActivity.this, message + "!", Toast.LENGTH_SHORT).show();

                    displayGroups();
                    /*
                    int[] groupImages = {R.drawable.meetmeup,R.drawable.meetmeup};
                    String[] prgmNameList={"Let Us C","c++"};

                    listGroup.setAdapter(new CustomAdapter(GroupActivity.this, prgmNameList, groupImages));
                    groupAdapter = new ArrayAdapter<String>(GroupActivity.this,
                            android.R.layout.simple_list_item_1, sessions.listOfGroups());

                    groupAdapter = new ArrayAdapter<String>(GroupActivity.this,
                            android.R.layout.simple_list_item_1, sessions.listOfGroups(currentGroups));

                    listGroup.setAdapter(groupAdapter);

                    listGroup.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {

                            // ListView Clicked item index
                            int itemPosition = position;

                            // ListView Clicked item value
                            String itemValue = (String) listGroup.getItemAtPosition(position);

                            // Show Alert
                            Toast.makeText(getApplicationContext(),
                                    "Position :" + itemPosition + "  ListItem : " + itemValue, Toast.LENGTH_LONG)
                                    .show();
                        }
                    });
                    */
                } else if(message.equals("No group")) {
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

    class DisableGroup extends AsyncTask<String, String, String> {

        String groupId;

        public DisableGroup (String groupId) {
            this.groupId = groupId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(GroupActivity.this, R.style.progress);
            pDialog.setCancelable(true);
            pDialog.setProgressStyle(android.R.style.Widget_Material_ProgressBar_Large);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            // TODO Auto-generated method stub

            int success;

            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();

                params.add(new BasicNameValuePair("id", groupId));
                params.add(new BasicNameValuePair("query_type", "disable"));

                Log.d("request!", "starting");

                JSONObject json = jsonParser.makeHttpRequest(
                        UPDATE_GROUP_URL, "POST", params);

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
                    Toast.makeText(GroupActivity.this, message + "!", Toast.LENGTH_SHORT).show();
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
            pDialog = new ProgressDialog(GroupActivity.this, R.style.progress);
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
                    Toast.makeText(GroupActivity.this, message + "!", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

}
