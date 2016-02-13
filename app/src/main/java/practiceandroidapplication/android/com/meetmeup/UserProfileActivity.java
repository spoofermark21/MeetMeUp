package practiceandroidapplication.android.com.meetmeup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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

import practiceandroidapplication.android.com.meetmeup.Entity.Nationality;
import practiceandroidapplication.android.com.meetmeup.Entity.Network;
import practiceandroidapplication.android.com.meetmeup.Entity.Sessions;
import practiceandroidapplication.android.com.meetmeup.Entity.User;
import practiceandroidapplication.android.com.meetmeup.Handles.JSONParser;
import practiceandroidapplication.android.com.meetmeup.Helpers.ImageHelper;

public class UserProfileActivity extends AppCompatActivity {

    private static final String RETRIEVE_USER_URL = Network.forDeploymentIp + "user_retrieve.php";
    private static final String TAG_STATUS = "status";
    private static final String TAG_RESPONSE = "response";

    JSONParser jsonParser = new JSONParser();
    ProgressDialog pDialog;
    //ProgressBar progressImage;

    TextView lblFullName,lblGender, lblNationality,
            lblLocation, lblMobile, lblEmailAdd, lblBirthdate;

    LinearLayout linearProfile;

    ImageView imgUser;

    Sessions sessions = Sessions.getSessionsInstance();
    User currentUser = Sessions.getSessionsInstance().currentUser;
    User user = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                finish();
            }
        });

        initUI();
        try {
            linearProfile.setVisibility(View.INVISIBLE);
            new RetrieveUser().execute();

        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_edit){
            startActivity(new Intent(UserProfileActivity.this, UserProfileUpdateActivity.class));
            finish();
        } /*else if (id == R.id.action_preferrence ){
            startActivity(new Intent(UserProfileActivity.this, SetPreferenceActivity.class));
            finish();
        }*/

        return super.onOptionsItemSelected(item);
    }

    /*
        functions
     */

    public void initUI(){

        lblBirthdate = (TextView) findViewById(R.id.lbl_age);
        lblFullName = (TextView) findViewById(R.id.lbl_user_fullname);
        lblGender = (TextView) findViewById(R.id.lbl_gender);
        lblNationality = (TextView) findViewById(R.id.lbl_nationality);
        lblLocation = (TextView) findViewById(R.id.lbl_address);
        lblMobile = (TextView) findViewById(R.id.lbl_mobile);
        lblEmailAdd = (TextView) findViewById(R.id.lbl_email);

        linearProfile = (LinearLayout) findViewById(R.id.linear_profile);

        imgUser = (ImageView) findViewById(R.id.img_user);
        imgUser.setBackgroundColor(Color.parseColor("#E6E9ED"));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.btn_save);
        fab.setVisibility(View.GONE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UserProfileActivity.this, UserProfileUpdateActivity.class));
                finish();
            }
        });

    }


    /*
        thread
     */

    class RetrieveUser extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(UserProfileActivity.this, R.style.progress);
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

                Sessions sessions = Sessions.getSessionsInstance();

                Log.d("USER_ID (userProfile)", currentUser.getId() + "");
                params.add(new BasicNameValuePair("type", "current_user"));
                params.add(new BasicNameValuePair("user_id", sessions.currentUser.getId() + ""));



                Log.d("request!", "starting");

                JSONObject json = jsonParser.makeHttpRequest(
                        RETRIEVE_USER_URL, "POST", params);

                Log.d("Fetching...", json.toString());

                success = json.getInt(TAG_STATUS);

                if (success == 1) {
                    Log.d("Success!", json.toString());

                    JSONArray jUserArray = json.getJSONArray("user");
                    JSONObject jUserObject = jUserArray.getJSONObject(0);

                    //currentUser.setId(jUserObject.getInt("id"));

                    Log.d("Fullname (user)", jUserObject.getString("first_name")
                            + " " + jUserObject.getString("last_name"));

                    currentUser.setFirstName(jUserObject.getString("first_name"));
                    currentUser.setLastName(jUserObject.getString("last_name"));
                    currentUser.setBirthDate(jUserObject.getString("bdate"));
                    currentUser.setNationality(new Nationality(jUserObject.getInt("natio_id")));
                    currentUser.setGender(jUserObject.getString("gender").charAt(0));
                    currentUser.setCurrentLocation(jUserObject.getString("location"));
                    currentUser.setEmailAddress(jUserObject.getString("email_address"));
                    currentUser.setContactNumber(jUserObject.getString("contact_number"));
                    currentUser.setPrivacyFlag(jUserObject.getString("active_flag").charAt(0));
                    currentUser.setUserImage(jUserObject.getString("user_image"));

                    return json.getString(TAG_RESPONSE);
                } else {
                    Log.d("Login failed!", json.getString(TAG_RESPONSE));
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
                if(message.equals("Successful")) {

                    // download user image based on the file name query
                    if(!currentUser.getUserImage().equals("null") && !currentUser.getUserImage().equals("")) {
                        new DownloadUserImage(currentUser.getUserImage() + ".JPG").execute();
                    }
                    linearProfile.setVisibility(View.VISIBLE);

                    lblFullName.setText(currentUser.getFirstName() + " "
                            + currentUser.getLastName());
                    lblBirthdate.setText(currentUser.getBirthDate() + "");

                    String gender = currentUser.getGender() == 'M' ? "Male" : "Female";

                    lblGender.setText(gender);
                    lblNationality.setText(currentUser.getNationality().getNatioNalityName());
                    lblLocation.setText(currentUser.getCurrentLocation());
                    lblMobile.setText(currentUser.getContactNumber());
                    lblEmailAdd.setText(currentUser.getEmailAddress());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }
    private class DownloadUserImage extends AsyncTask<Void, Void, Bitmap> {

        String filename;

        public DownloadUserImage(String filename) {
            this.filename = filename;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /*pDialog = new ProgressDialog(UserProfileActivity.this, R.style.progress);
            pDialog.setCancelable(true);
            pDialog.setProgressStyle(android.R.style.Widget_Material_ProgressBar_Large);
            pDialog.setMessage("Downloading image...");
            pDialog.show();*/
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
                //Toast.makeText(UserProfileActivity.this, "Set a profile picture @ update user section.", Toast.LENGTH_SHORT).show();
                return null;
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        }


        protected void onPostExecute(Bitmap bitmap) {
            //pDialog.dismiss();
            try {
                if(bitmap!=null) {
                    imgUser.setImageBitmap(bitmap);
                    //imgUser.setVisibility(View.VISIBLE);
                    //progressImage.setVisibility(View.GONE);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void onBackPressed() {
        finish();
    }

}
