package practiceandroidapplication.android.com.meetmeup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
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
import android.widget.ListView;
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

public class ViewGroupActivity extends AppCompatActivity {


    private static final String RETRIEVE_GROUP_URL = Network.forDeploymentIp + "group_retrieve.php";
    private static final String RETRIEVE_GROUP_MEMBER_URL = Network.forDeploymentIp + "group_member_retrieve.php";

    private static final String UPDATE_GROUP_URL = Network.forDeploymentIp + "group_update.php";

    private static final String TAG_STATUS = "status";
    private static final String TAG_RESPONSE = "response";

    JSONParser jsonParser = new JSONParser();
    private ProgressDialog pDialog;

    Toolbar toolBar;
    EditText txtGroupName, txtDetails;
    Button btnImage, btnUpdate;
    ImageView imgGroup;

    ListView listMembers;


    Sessions sessions = Sessions.getSessionsInstance();
    List<Group> currentGroups = Sessions.getSessionsInstance().currentGroups;
    User currentUser = Sessions.getSessionsInstance().currentUser;

    Group group;
    String currentGroupId;

    private static int RESULT_LOAD_IMG = 1;
    String imgDecodableString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_group);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        toolBar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ViewGroupActivity.this, GroupActivity.class));
                finish();
            }
        });

        initUI();
        currentGroupId = getIntent().getStringExtra("GROUP_ID");

        new RetrieveGroups().execute();

    }

    public void initUI() {
        txtGroupName = (EditText) findViewById(R.id.txt_group_name);
        txtDetails = (EditText) findViewById(R.id.txt_details);

        listMembers = (ListView) findViewById(R.id.list_members);

        imgGroup = (ImageView) findViewById(R.id.img_group);

        btnImage = (Button) findViewById(R.id.btn_image);
        btnImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
            }
        });

        btnUpdate = (Button) findViewById(R.id.btn_update);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (validateForm()) {
                    Group updateGroup = new Group(txtGroupName.getText().toString(),
                            txtDetails.getText().toString(), currentUser.getId());

                    new UpdateGroup().execute(updateGroup.getGroupName(), updateGroup.getDetails(),
                            currentGroupId);
                }

            }
        });
    }

    public boolean validateForm() {
        boolean isReadyToSave = true;

        if (txtGroupName.getText().toString().equals("")) {
            txtGroupName.setError("Group name is required.");
            isReadyToSave = false;
        } else
            txtGroupName.setError(null);

        if (txtDetails.getText().toString().equals("")) {
            txtDetails.setError("Details is required.");
            isReadyToSave = false;
        } else
            txtDetails.setError(null);

        return isReadyToSave;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
                cursor.close();

                // Set the Image in ImageView after decoding the String
                imgGroup.setImageBitmap(BitmapFactory
                        .decodeFile(imgDecodableString));

            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

    }

    public void onBackPressed() {
        startActivity(new Intent(ViewGroupActivity.this, GroupActivity.class));
        finish();
    }

    /*
        thread
     */

    class RetrieveGroups extends AsyncTask<String, String, String> {

        ArrayAdapter<String> groupAdapter;
        List<String> memberName = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ViewGroupActivity.this, R.style.progress);
            pDialog.setCancelable(false);
            pDialog.setProgressStyle(android.R.style.Widget_Material_ProgressBar_Large);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... groupInfo) {
            // TODO Auto-generated method stub

            int success;

            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();

                Log.d("USER_ID (user)", currentUser.getId() + "");

                params.add(new BasicNameValuePair("id", currentGroupId));
                params.add(new BasicNameValuePair("query_type", "individual"));

                Log.d("request!", "starting");

                JSONObject json = jsonParser.makeHttpRequest(
                        RETRIEVE_GROUP_URL, "POST", params);

                Log.d("Fetching...", json.toString());

                success = json.getInt(TAG_STATUS);

                if (success == 1) {
                    Log.d("Success!", json.toString());

                    JSONArray jUserArray = json.getJSONArray("group");
                    JSONObject jUserObject;

                    //sessions.removeGroups();

                    jUserObject = jUserArray.getJSONObject(0);

                    //groups[i] = jUserObject.getString("group_name");
                    group = new Group(jUserObject.getInt("id"),
                            jUserObject.getString("group_name"), jUserObject.getString("details"),
                            jUserObject.getInt("created_by"), jUserObject.getString("created_date"),
                            jUserObject.getInt("count_members"));

                    JSONArray groupMembers = jUserObject.getJSONArray("members");
                    JSONObject members;

                    for(int i=0; i < groupMembers.length();i++){
                        members = groupMembers.getJSONObject(i);
                        Log.d("ID:", members.getInt("user_id") + "");
                        memberName.add(members.getString("first_name") + " " + members.getString("last_name"));
                    }



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
                    Toast.makeText(ViewGroupActivity.this, message + "!", Toast.LENGTH_SHORT).show();

                    txtGroupName.setText(group.getGroupName());
                    txtDetails.setText(group.getDetails());

                    groupAdapter = new ArrayAdapter<String>(ViewGroupActivity.this,
                            android.R.layout.simple_list_item_1, memberName);

                    listMembers.setAdapter(groupAdapter);

                    listMembers.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {

                            // ListView Clicked item index
                            int itemPosition = position;

                            // ListView Clicked item value
                            String itemValue = (String) listMembers.getItemAtPosition(position);

                            // Show Alert
                            Toast.makeText(getApplicationContext(),
                                    "Position :" + itemPosition + "  ListItem : " + itemValue, Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    } // end of thread retrieve user

    class UpdateGroup extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ViewGroupActivity.this, R.style.progress);
            pDialog.setCancelable(false);
            pDialog.setProgressStyle(android.R.style.Widget_Material_ProgressBar_Large);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... groupInfo) {
            // TODO Auto-generated method stub

            int success;

            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();

                Log.d("USER_ID (user)", currentUser.getId() + "");

                params.add(new BasicNameValuePair("group_name", groupInfo[0]));
                params.add(new BasicNameValuePair("details", groupInfo[1]));
                params.add(new BasicNameValuePair("id", groupInfo[2]));

                Log.d("request!", "starting");

                JSONObject json = jsonParser.makeHttpRequest(
                        UPDATE_GROUP_URL, "POST", params);

                Log.d("Updating...", json.toString());

                success = json.getInt(TAG_STATUS);

                if (success == 1) {
                    Log.d("Success!", json.toString());

                    return json.getString(TAG_RESPONSE);
                } else {
                    Log.d("Updating failed...", json.getString(TAG_RESPONSE));
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

                    new Thread() {
                        public void run() {
                            try {
                                sleep(500);
                                startActivity(new Intent(ViewGroupActivity.this, GroupActivity.class));
                                finish();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }.start();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

}
