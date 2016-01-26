package practiceandroidapplication.android.com.meetmeup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
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

import java.util.ArrayList;
import java.util.List;

import practiceandroidapplication.android.com.meetmeup.Entity.Meetups;
import practiceandroidapplication.android.com.meetmeup.Entity.Network;
import practiceandroidapplication.android.com.meetmeup.Entity.Preference;
import practiceandroidapplication.android.com.meetmeup.Entity.Sessions;
import practiceandroidapplication.android.com.meetmeup.Entity.User;
import practiceandroidapplication.android.com.meetmeup.Handles.JSONParser;

public class ViewMeetupsActivity extends AppCompatActivity {

    private static final String RETRIEVE_MEETUPS_URL = Network.forDeploymentIp + "meetups_retrieve.php";
    private static final String TAG_STATUS = "status";
    private static final String TAG_RESPONSE = "response";

    JSONParser jsonParser = new JSONParser();
    ProgressDialog pDialog;

    ImageView imgUser;

    TextView lblSubject, lblPostedDate, lblDetails, lblLocation,
            lblPostedBy, lblComments;

    ListView listComments;

    EditText txtComment;

    Button btnInsert;

    ScrollView scrollView;

    Meetups meetups;

    Sessions sessions = Sessions.getSessionsInstance();
    //List<Meetups> currentMeetups = new ArrayList<>();
    User currentUser = Sessions.getSessionsInstance().currentUser;

    String meetupId;

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
                finish();
            }
        });

        initUI();
        meetupId = getIntent().getStringExtra("MEETUPS_ID");
        new RetrieveMeetups().execute();

    }

    public void initUI () {

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

        txtComment = (EditText) findViewById(R.id.txt_comment);

        btnInsert = (Button) findViewById(R.id.btn_comment);
        btnInsert.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                // insert comment
            }
        });

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
                List<NameValuePair> params = new ArrayList<NameValuePair>();

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
                            jUserObject.getString("details"),jUserObject.getInt("posted_by"), jUserObject.getString("location"),
                            new Preference(Integer.parseInt(jUserObject.getString("pref_start_age")),
                                    Integer.parseInt(jUserObject.getString("pref_end_age")),
                                    jUserObject.getString("pref_gender").charAt(0)));

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
                    scrollView.setVisibility(View.VISIBLE);

                    Toast.makeText(ViewMeetupsActivity.this, message + "!", Toast.LENGTH_SHORT).show();

                    //displayMeetups
                    lblSubject.setText(meetups.getSubject());
                    lblPostedDate.setText(meetups.getPostedDate());
                    lblDetails.setText(meetups.getDetails());
                    lblLocation.setText(meetups.getLocation());
                    //lblPassKey.setText();
                    lblPostedBy.setText(meetups.getPostedByName());

                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }

}
