package practiceandroidapplication.android.com.meetmeup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import practiceandroidapplication.android.com.meetmeup.Entity.Group;
import practiceandroidapplication.android.com.meetmeup.Entity.Network;
import practiceandroidapplication.android.com.meetmeup.Entity.Sessions;
import practiceandroidapplication.android.com.meetmeup.Entity.User;
import practiceandroidapplication.android.com.meetmeup.Handles.Interactions;
import practiceandroidapplication.android.com.meetmeup.Handles.JSONParser;

public class EditGroupActivity extends AppCompatActivity {


    private static final String RETRIEVE_GROUP_URL = Network.forDeploymentIp + "group_retrieve.php";
    private static final String RETRIEVE_GROUP_MEMBER_URL = Network.forDeploymentIp + "group_member_retrieve.php";
    private static final String UPLOAD_IMAGE_URL = Network.forDeploymentIp + "image_upload.php";

    private static final String UPDATE_GROUP_URL = Network.forDeploymentIp + "group_update.php";

    private static final String TAG_STATUS = "status";
    private static final String TAG_RESPONSE = "response";

    JSONParser jsonParser = new JSONParser();
    private ProgressDialog pDialog;

    Toolbar toolBar;
    EditText txtGroupName, txtDetails;
    TextView lblMembers;
    Button btnImage, btnUpdate;
    ImageView imgGroup;

    ListView listMembers;

    ScrollView scrollView;

    Sessions sessions = Sessions.getSessionsInstance();
    List<Group> currentGroups = Sessions.getSessionsInstance().currentGroups;
    User currentUser = Sessions.getSessionsInstance().currentUser;

    Group group;
    String currentGroupId;

    private static int RESULT_LOAD_IMG = 1;

    String imgDecodableString;
    String encodedImage;

    String fileName;

    boolean isNewImage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        toolBar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        lblMembers = (TextView) findViewById(R.id.lbl_members);

        imgGroup = (ImageView) findViewById(R.id.img_group);

        scrollView = (ScrollView) findViewById(R.id.scroll_view);
        scrollView.setVisibility(View.INVISIBLE);

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

                isNewImage = true;

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
            pDialog = new ProgressDialog(EditGroupActivity.this, R.style.progress);
            pDialog.setCancelable(true);
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
                            jUserObject.getInt("count_members"), jUserObject.getString("group_image"));

                    if(group.getTotalMembers() != 0) {
                        JSONArray groupMembers = jUserObject.getJSONArray("members");
                        JSONObject members;

                        for(int i=0; i < groupMembers.length();i++){
                            members = groupMembers.getJSONObject(i);
                            Log.d("ID:", members.getInt("user_id") + "");
                            memberName.add(members.getString("first_name") + " " + members.getString("last_name"));
                        }
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

                    Toast.makeText(EditGroupActivity.this, message + "!", Toast.LENGTH_SHORT).show();

                    txtGroupName.setText(group.getGroupName());
                    txtDetails.setText(group.getDetails());

                    if(group.getTotalMembers() != 0) {
                        groupAdapter = new ArrayAdapter<String>(EditGroupActivity.this,
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
                    } else {
                        lblMembers.setText("No member");
                        listMembers.setVisibility(View.GONE);
                    }

                    //sample download image 22/01/2016
                    try {
                        if(!group.getGroupImage().equals("")) {
                            new DownloadGroupImage(group.getGroupImage() + ".JPG").execute();
                        } else {
                            scrollView.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }

    private class UploadGroupImage extends AsyncTask<String, String, String> {

        Bitmap image;
        String filename;
        String directory;

        public UploadGroupImage(Bitmap image, String filename, String directory) {
            this.image = image;
            this.filename = filename;
            this.directory = directory;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EditGroupActivity.this, R.style.progress);
            pDialog.setCancelable(true);
            pDialog.setProgressStyle(android.R.style.Widget_Material_ProgressBar_Large);
            pDialog.setMessage("Uploading image...");
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... string) {
            // TODO Auto-generated method stub

            int success;

            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<>();

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

                encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

                params.add(new BasicNameValuePair("image", encodedImage));
                params.add(new BasicNameValuePair("name", filename));
                params.add(new BasicNameValuePair("directory", directory));


                Log.d("Image...", encodedImage);


                Log.d("request!", "starting");

                JSONObject json = jsonParser.makeHttpRequest(
                        UPLOAD_IMAGE_URL, "POST", params);

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
                Toast.makeText(EditGroupActivity.this, "Upload successful!", Toast.LENGTH_SHORT).show();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private class DownloadGroupImage extends AsyncTask<Void, Void, Bitmap> {

        String filename;

        public DownloadGroupImage(String filename) {
            this.filename = filename;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EditGroupActivity.this, R.style.progress);
            pDialog.setCancelable(true);
            pDialog.setProgressStyle(android.R.style.Widget_Material_ProgressBar_Large);
            //pDialog.setMessage("Downloading image...");
            pDialog.show();
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            // TODO Auto-generated method stub

            try {
                final String GROUP_IMAGE_URL = Network.forDeploymentIp + "meetmeup/uploads/groups/" + this.filename;

                Log.d("Image", GROUP_IMAGE_URL);

                URLConnection connection = new URL(GROUP_IMAGE_URL).openConnection();
                connection.setConnectTimeout(1000 * 30);

                return BitmapFactory.decodeStream((InputStream) connection.getContent(), null, null);
            } catch (FileNotFoundException ex) {
                Interactions.showError("File not found", EditGroupActivity.this);
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }


        protected void onPostExecute(Bitmap bitmap) {
            pDialog.dismiss();
            try {
                if(bitmap!=null) {
                    imgGroup.setImageBitmap(bitmap);
                    scrollView.setVisibility(View.VISIBLE);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    class UpdateGroup extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EditGroupActivity.this, R.style.progress);
            pDialog.setCancelable(true);
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

                fileName = Interactions.generateString(new Random(), "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ", 10);


                params.add(new BasicNameValuePair("group_name", groupInfo[0]));
                params.add(new BasicNameValuePair("details", groupInfo[1]));
                params.add(new BasicNameValuePair("id", groupInfo[2]));
                params.add(new BasicNameValuePair("group_image", fileName));

                params.add(new BasicNameValuePair("query_type", "update"));

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

                    if (isNewImage) {
                        Bitmap image = ((BitmapDrawable) imgGroup.getDrawable()).getBitmap();
                        new UploadGroupImage(image, fileName,"groups").execute();
                    }

                    Toast.makeText(EditGroupActivity.this, message + "!", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

}
