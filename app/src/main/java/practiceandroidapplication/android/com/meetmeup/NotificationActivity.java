package practiceandroidapplication.android.com.meetmeup;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.games.Notifications;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import practiceandroidapplication.android.com.meetmeup.Entity.Meetups;
import practiceandroidapplication.android.com.meetmeup.Entity.Network;
import practiceandroidapplication.android.com.meetmeup.Entity.Notification;
import practiceandroidapplication.android.com.meetmeup.Entity.Sessions;
import practiceandroidapplication.android.com.meetmeup.Entity.User;
import practiceandroidapplication.android.com.meetmeup.Handles.JSONParser;

public class NotificationActivity extends AppCompatActivity {

    private static final String RETRIEVE_NOTIFICATIONS_URL = Network.forDeploymentIp + "notification_retrieve.php";
    private static final String UPDATE_ATTENDEES_URL = Network.forDeploymentIp + "attendees_update.php";
    private static final String UPDATE_NOTIFICATIONS_URL = Network.forDeploymentIp + "notification_update.php";

    private static final String TAG_STATUS = "status";
    private static final String TAG_RESPONSE = "response";

    JSONParser jsonParser = new JSONParser();
    ProgressDialog pDialog;

    //ListView listOfNotifications;
    LinearLayout listOfNotifications;
    TextView lblMessage;

    List<Notification> notification = new ArrayList<>();

    User currentUser = Sessions.getSessionsInstance().currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NotificationActivity.this, NewsfeedActivity.class));
                finish();
            }
        });

        listOfNotifications = (LinearLayout) findViewById(R.id.linear_notifications);
        lblMessage = (TextView) findViewById(R.id.lbl_message);
        lblMessage.setVisibility(View.GONE);

        new RetrieveNotifications().execute();

    }

    public void displayNotifs() {
        for (Notification notif : notification) {

            LinearLayout recordOfNotifications = new LinearLayout(this);
            recordOfNotifications.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            recordOfNotifications.setOrientation(LinearLayout.VERTICAL);
            recordOfNotifications.setPadding(10, 10, 10, 10);

            Log.d("Notif", notif.getDetails());
            recordOfNotifications.setTag(notif.getId());

            final TextView notifFromUser = new TextView(this);
            notifFromUser.setText(notif.getFromUser());
            notifFromUser.setTextSize(20);
            notifFromUser.setTextColor(Color.BLACK);
            notifFromUser.setTag(notif.getFromId());

            final TextView notifDetails = new TextView(this);
            notifDetails.setText(notif.getDetails());
            notifDetails.setTextSize(15);
            notifDetails.setTextColor(Color.BLACK);
            notifDetails.setTag(notif.getPostCommentId());

            Log.d("POST COMMENT ID", notif.getPostCommentId() + "");

            final TextView notifDate = new TextView(this);
            notifDate.setText(notif.getDateNotified());
            notifDate.setTextSize(15);
            notifDate.setTextColor(Color.BLACK);
            notifDate.setTag(notif.getDetails());

            final LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
            params2.weight = 1.0f;
            params2.gravity = Gravity.RIGHT;
            params2.leftMargin = 50;

            LinearLayout options = new LinearLayout(this);
            options.setOrientation(LinearLayout.HORIZONTAL);
            options.setPadding(10, 10, 10, 10);
            options.setLayoutParams(params2);

            final TextView accept = new TextView(this);
            accept.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            accept.setPadding(10, 10, 0, 10);
            accept.setText("accept");
            accept.setTextSize(15);
            accept.setBackgroundColor(Color.TRANSPARENT);


            final TextView ignore = new TextView(this);
            ignore.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            ignore.setPadding(10, 10, 0, 10);
            ignore.setText("ignore");
            ignore.setTextSize(15);
            ignore.setBackgroundColor(Color.TRANSPARENT);

            final TextView view = new TextView(this);
            view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            view.setPadding(10, 10, 0, 10);
            view.setText("view");
            view.setTextSize(15);
            view.setBackgroundColor(Color.TRANSPARENT);

            accept.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    final LinearLayout parent = (LinearLayout) v.getParent().getParent();

                    String notifId = parent.getTag() + "";
                    String fromId = parent.getChildAt(0).getTag() + "";
                    String postCommentId = parent.getChildAt(1).getTag() + "";
                    String details = parent.getChildAt(1).getTag() + "";

                    Log.d("FROM ID", fromId);
                    Log.d("POST COMMENT ID", postCommentId);

                    listOfNotifications.removeView(parent);

                    new UpdateAttendees(fromId, postCommentId, notifId, details).execute();

                    if (listOfNotifications.getChildCount() == 1)
                        lblMessage.setVisibility(View.VISIBLE);
                    else
                        lblMessage.setVisibility(View.GONE);
                }
            });

            ignore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {

                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(NotificationActivity.this);
                    dlgAlert.setMessage("Are you sure to ignore this request?");
                    dlgAlert.setTitle("Warning!");
                    dlgAlert.setCancelable(true);

                    dlgAlert.setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    final LinearLayout parent = (LinearLayout) v.getParent().getParent();

                                    String notifId = parent.getTag() + "";

                                    Log.d("NOTIF ID", notifId + "");

                                    try {
                                        new UpdateNotification(Integer.parseInt(notifId)).execute();
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
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

            view.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    final LinearLayout parent = (LinearLayout) v.getParent().getParent();

                    String notifId = parent.getTag() + "";
                    String postCommentId = parent.getChildAt(1).getTag() + "";

                    Log.d("POST COMMENT ID", postCommentId);

                    listOfNotifications.removeView(parent);

                    startActivity(new Intent(NotificationActivity.this, ViewMeetupsActivity.class).putExtra("MEETUPS_ID", postCommentId + ""));

                    try {
                        new UpdateNotification(Integer.parseInt(notifId)).execute();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    if (listOfNotifications.getChildCount() == 1)
                        lblMessage.setVisibility(View.VISIBLE);
                    else
                        lblMessage.setVisibility(View.GONE);
                }
            });

            if(notif.getDetails().contains("join")) {
                options.addView(accept);
                options.addView(ignore);
                Toast.makeText(NotificationActivity.this, "Join", Toast.LENGTH_SHORT).show();
            } else if(notif.getDetails().contains("comment")) {
                Toast.makeText(NotificationActivity.this, "Comment", Toast.LENGTH_SHORT).show();
                options.addView(view);
            }

            recordOfNotifications.addView(notifFromUser);
            recordOfNotifications.addView(notifDetails);
            recordOfNotifications.addView(notifDate);

            recordOfNotifications.addView(options);

            listOfNotifications.addView(recordOfNotifications);
        }

    }

    public void onBackPressed() {
        startActivity(new Intent(NotificationActivity.this, NewsfeedActivity.class));
        finish();
    }

    /*
        threads
     */

    class RetrieveNotifications extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(NotificationActivity.this, R.style.progress);
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

                Log.d("USER_ID (user)", currentUser.getId() + "");

                params.add(new BasicNameValuePair("user_id", currentUser.getId() + ""));

                Log.d("request!", "starting");

                JSONObject json = jsonParser.makeHttpRequest(
                        RETRIEVE_NOTIFICATIONS_URL, "POST", params);

                Log.d("Fetching...", json.toString());

                success = json.getInt(TAG_STATUS);

                if (success == 1) {
                    Log.d("Success!", json.toString());

                    JSONArray jsonArray = json.getJSONArray("notifications");
                    JSONObject jsonObject;

                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);

                        Log.d("ID:", jsonObject.getInt("id") + "");

                        notification.add(new Notification(jsonObject.getInt("id"), jsonObject.getInt("user_id"), jsonObject.getInt("from_id"),
                                jsonObject.getInt("post_comment_id"), jsonObject.getString("type").charAt(0), jsonObject.getString("details"),
                                jsonObject.getString("view_flag").charAt(0), jsonObject.getString("date_notified"), jsonObject.getString("from_user")));


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
                //listOfMeetups.setVisibility(View.VISIBLE);
                if (message.equals("New notifications.")) {
                    Toast.makeText(NotificationActivity.this, message + "!", Toast.LENGTH_SHORT).show();
                    displayNotifs();
                } else if (message.equals("No notifications.")) {
                    lblMessage.setVisibility(View.VISIBLE);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }

    class UpdateNotification extends AsyncTask<String, String, String> {

        int notifId;
        int userId;
        int fromId;
        int postCommentId;
        String details;

        public UpdateNotification(int notifId) {
            this.notifId = notifId;
        }
        /*
        public UpdateNotification(int userId, int postCommentId, String details) {
            this.userId = userId;
            this.postCommentId = postCommentId;
            this.details = details;
        }

        public UpdateNotification(int userId, int fromId, int postCommentId, String details) {
            this.userId = userId;
            this.fromId = fromId;
            this.postCommentId = postCommentId;
            this.details = details;
        }*/

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(NotificationActivity.this, R.style.progress);
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

                Log.d("NOTIF ID", notifId + "");
                params.add(new BasicNameValuePair("notif_id", notifId + ""));

                /*params.add(new BasicNameValuePair("user_id", userId + ""));
                params.add(new BasicNameValuePair("from_id", fromId + ""));
                params.add(new BasicNameValuePair("post_comment_id", postCommentId + ""));
                params.add(new BasicNameValuePair("details", details));

                Log.d("PARAMS", userId + " " + fromId + " " + postCommentId + " " + details);*/
                Log.d("request!", "starting");

                JSONObject json = jsonParser.makeHttpRequest(
                        UPDATE_NOTIFICATIONS_URL, "POST", params);

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
                //listOfMeetups.setVisibility(View.VISIBLE);
                if (message.equals("Successful")) {
                    Toast.makeText(NotificationActivity.this, message + "!", Toast.LENGTH_SHORT).show();
                    //displayNotifs();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }

    class UpdateAttendees extends AsyncTask<String, String, String> {

        String userId;
        String postCommentId;
        String notifId;
        String details;

        public UpdateAttendees(String userId, String postCommentId, String notifId, String details) {
            this.userId = userId;
            this.postCommentId = postCommentId;
            this.notifId = notifId;
            this.details = details;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(NotificationActivity.this, R.style.progress);
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


                params.add(new BasicNameValuePair("user_id", userId));
                params.add(new BasicNameValuePair("post_comment_id", postCommentId));

                Log.d("request!", "starting");

                JSONObject json = jsonParser.makeHttpRequest(
                        UPDATE_ATTENDEES_URL, "POST", params);

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
                //listOfMeetups.setVisibility(View.VISIBLE);
                if (message.equals("Successful")) {
                    Toast.makeText(NotificationActivity.this, message + "!", Toast.LENGTH_SHORT).show();

                    try {
                        new UpdateNotification(Integer.parseInt(notifId)).execute();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }

}
