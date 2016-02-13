package practiceandroidapplication.android.com.meetmeup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class ViewProfileActivity extends AppCompatActivity {

    private static final String RETRIEVE_USER_URL = Network.forDeploymentIp + "user_retrieve.php";
    private static final String TAG_STATUS = "status";
    private static final String TAG_RESPONSE = "response";

    JSONParser jsonParser = new JSONParser();
    ProgressDialog pDialog;
    TextView lblFullName,lblGender, lblNationality,
            lblLocation, lblMobile, lblEmailAdd, lblBirthdate;

    LinearLayout linearProfile;

    ImageView imgUser;

    User user = new User();

    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        initUI();
        try {
            linearProfile.setVisibility(View.INVISIBLE);
            new RetrieveUser().execute();
        } catch(Exception ex) {
            ex.printStackTrace();
        }
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


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                finish();
            }
        });

    }

    class RetrieveUser extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ViewProfileActivity.this, R.style.progress);
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

                userId = getIntent().getStringExtra("USER_ID");

                params.add(new BasicNameValuePair("type", "current_user"));
                params.add(new BasicNameValuePair("user_id", userId));


                Log.d("request!", "starting");

                JSONObject json = jsonParser.makeHttpRequest(
                        RETRIEVE_USER_URL, "POST", params);

                Log.d("Fetching...", json.toString());

                success = json.getInt(TAG_STATUS);

                if (success == 1) {
                    Log.d("Success!", json.toString());

                    JSONArray jUserArray = json.getJSONArray("user");
                    JSONObject jUserObject = jUserArray.getJSONObject(0);

                    user.setId(jUserObject.getInt("id"));

                    Log.d("Fullname (user)", jUserObject.getString("first_name")
                            + " " + jUserObject.getString("last_name"));

                    user.setFirstName(jUserObject.getString("first_name"));
                    user.setLastName(jUserObject.getString("last_name"));
                    user.setBirthDate(jUserObject.getString("bdate"));
                    user.setNationality(new Nationality(jUserObject.getInt("natio_id")));
                    user.setGender(jUserObject.getString("gender").charAt(0));
                    user.setCurrentLocation(jUserObject.getString("current_location"));
                    user.setEmailAddress(jUserObject.getString("email_address"));
                    user.setContactNumber(jUserObject.getString("contact_number"));
                    user.setPrivacyFlag(jUserObject.getString("active_flag").charAt(0));
                    user.setUserImage(jUserObject.getString("user_image"));

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
                    if(!user.getUserImage().equals("null") && !user.getUserImage().equals("")) {
                        new DownloadUserImage(user.getUserImage() + ".JPG").execute();
                    }

                    //new DownloadUserImage(user.getUserImage() + ".JPG").execute();

                    linearProfile.setVisibility(View.VISIBLE);

                    lblFullName.setText(user.getFirstName() + " "
                            + user.getLastName());
                    lblBirthdate.setText(user.getBirthDate() + "");

                    String gender = user.getGender() == 'M' ? "Male" : "Female";

                    lblGender.setText(gender);
                    lblNationality.setText(user.getNationality().getNatioNalityName());
                    lblLocation.setText(user.getCurrentLocation());
                    lblMobile.setText(user.getContactNumber());
                    lblEmailAdd.setText(user.getEmailAddress());
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
            pDialog = new ProgressDialog(ViewProfileActivity.this, R.style.progress);
            pDialog.setCancelable(true);
            pDialog.setProgressStyle(android.R.style.Widget_Material_ProgressBar_Large);
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
                Toast.makeText(ViewProfileActivity.this, "Set a profile picture @ update user section.", Toast.LENGTH_SHORT).show();
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
                    imgUser.setImageBitmap(ImageHelper.getRoundedCornerBitmap(bitmap, 20));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void onBackPressed() {
        //startActivity(new Intent(UserProfileActivity.this,NewsfeedActivity.class));
        finish();
    }
}
