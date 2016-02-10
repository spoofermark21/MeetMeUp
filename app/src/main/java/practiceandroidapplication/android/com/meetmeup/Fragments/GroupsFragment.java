package practiceandroidapplication.android.com.meetmeup.Fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

import practiceandroidapplication.android.com.meetmeup.EditGroupActivity;
import practiceandroidapplication.android.com.meetmeup.Entity.Group;
import practiceandroidapplication.android.com.meetmeup.Entity.Meetups;
import practiceandroidapplication.android.com.meetmeup.Entity.Network;
import practiceandroidapplication.android.com.meetmeup.Entity.Sessions;
import practiceandroidapplication.android.com.meetmeup.Entity.User;
import practiceandroidapplication.android.com.meetmeup.Handles.JSONParser;
import practiceandroidapplication.android.com.meetmeup.Helpers.ImageHelper;
import practiceandroidapplication.android.com.meetmeup.R;
import practiceandroidapplication.android.com.meetmeup.ViewGroupActivity;
import practiceandroidapplication.android.com.meetmeup.ViewMeetupsActivity;
import practiceandroidapplication.android.com.meetmeup.ViewProfileActivity;


public class GroupsFragment extends Fragment {

    private static final String RETRIEVE_GROUP_URL = Network.forDeploymentIp + "group_retrieve.php";
    private static final String TAG_STATUS = "status";
    private static final String TAG_RESPONSE = "response";

    JSONParser jsonParser = new JSONParser();
    ProgressDialog pDialog;

    Toolbar toolbar;

    LinearLayout listOfGroups;
    TextView lblMessage;

    LinearLayout listOfFeeds;

    Button btnGroup;

    Sessions sessions = Sessions.getSessionsInstance();
    List<Group> currentGroups = new ArrayList<>();
    User currentUser = Sessions.getSessionsInstance().currentUser;

    //List<Bitmap> groupImages = new ArrayList<>();
    //int index = 0;

    //Bitmap image;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_group, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listOfFeeds = (LinearLayout) getActivity().findViewById(R.id.linear_feeds);

        listOfGroups = (LinearLayout) getActivity().findViewById(R.id.linear_groups);
        lblMessage = (TextView) getActivity().findViewById(R.id.lbl_message);

        listOfGroups.setVisibility(View.INVISIBLE);
        lblMessage.setVisibility(View.GONE);

        /*btnGroup = (Button) getActivity().findViewById(R.id.btn_groups);
        btnGroup.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                new RetrieveGroups().execute();
            }
        });*/

        new RetrieveGroups().execute();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    /* thread */

    public void retrieve () {
        new RetrieveGroups().execute();
    }

    public void displayGroups() {

        listOfGroups.removeAllViews();

        for (Group group : currentGroups) {

            final LinearLayout.LayoutParams linearGroups = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            linearGroups.setMargins(0, 0, 0, 15);

            LinearLayout recordOfGroups = new LinearLayout(getActivity());
            recordOfGroups.setLayoutParams(linearGroups);
            recordOfGroups.setOrientation(LinearLayout.VERTICAL);
            recordOfGroups.setBackgroundResource(R.drawable.main_background);
            recordOfGroups.setPadding(20, 20, 20, 5);


            final LinearLayout.LayoutParams linearPostedBy = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            linearPostedBy.setMargins(0, 0, 0, 30);

            LinearLayout postedByLayout = new LinearLayout(getActivity());
            postedByLayout.setLayoutParams(linearPostedBy);
            postedByLayout.setOrientation(LinearLayout.HORIZONTAL);
            postedByLayout.setTag(group.getCreatedBy());
            postedByLayout.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    startActivity(new Intent(getActivity(), ViewProfileActivity.class).putExtra("USER_ID", view.getTag() + "" + ""));
                    Log.d("USER_ID", view.getTag() + "");
                }
            });

            final LinearLayout.LayoutParams linearDate = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            //linearGroups.setMargins(0, 0, 0, 30);

            LinearLayout postedByDate = new LinearLayout(getActivity());
            postedByDate.setLayoutParams(linearDate);
            postedByDate.setOrientation(LinearLayout.VERTICAL);


            Log.d("Group", group.getGroupName());
            recordOfGroups.setTag(group.getId());

            final LinearLayout.LayoutParams imageGroupParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            imageGroupParams.weight = 1.0f;
            imageGroupParams.height = 80;
            imageGroupParams.width = 80;
            imageGroupParams.setMargins(0, 0, 10, 0);
            imageGroupParams.gravity = Gravity.LEFT;

            final ImageView groupPostedByImage = new ImageView(getActivity());
            groupPostedByImage.setBackgroundColor(Color.parseColor("#E6E9ED"));

            final TextView groupPostedBy = new TextView(getActivity());
            groupPostedBy.setText(group.getCreatedByName());
            groupPostedBy.setTextSize(17);
            groupPostedBy.setTextColor(Color.BLACK);

            if(!group.getCreatedByNameImage().equals("null") && !group.getCreatedByNameImage().equals("")) {

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
                            if(bitmap!=null) {
                                Log.d("Image", "Success");
                                groupPostedByImage.setImageBitmap(bitmap);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }

                }

                new DownloadGroupImage(group.getCreatedByNameImage() + ".JPG").execute();
                groupPostedByImage.setLayoutParams(imageGroupParams);
                groupPostedByImage.setScaleType(ImageView.ScaleType.FIT_XY);
            }



            final TextView groupPostedDate = new TextView(getActivity());
            groupPostedDate.setText(timeDiff(group.getCreatedDate()) + "");
            groupPostedDate.setTextSize(15);
            groupPostedDate.setTextColor(Color.BLACK);

            final TextView groupName = new TextView(getActivity());
            groupName.setText(group.getGroupName());
            groupName.setTextSize(25);
            groupName.setTextColor(Color.BLACK);
            groupName.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    final LinearLayout parent = (LinearLayout) v.getParent();

                    Intent meetups = new Intent(getActivity(), ViewGroup.class);
                    meetups.putExtra("GROUP_ID", parent.getTag() + "");

                    Log.d("GROUP_ID", parent.getTag() + "");

                    startActivity(meetups);
                    getActivity().finish();
                }
            });

            final TextView groupDetails = new TextView(getActivity());
            groupDetails.setText(group.getDetails());
            groupDetails.setTextSize(15);
            groupDetails.setTextColor(Color.BLACK);

            final LinearLayout.LayoutParams imageGroupParams1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            imageGroupParams1.weight = 1.0f;
            imageGroupParams1.height = 350;
            imageGroupParams1.width = 350;
            imageGroupParams1.setMargins(0, 20, 0, 10);
            imageGroupParams1.gravity = Gravity.CENTER;

            final ImageView groupImage = new ImageView(getActivity());
            groupImage.setBackgroundColor(Color.parseColor("#E6E9ED"));

            if(!group.getGroupImage().equals("null") && !group.getGroupImage().equals("")) {

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
                                groupImage.setImageBitmap(bitmap);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }

                }

                new DownloadGroupImage(group.getGroupImage() + ".JPG").execute();
                groupImage.setLayoutParams(imageGroupParams1);
                groupImage.setScaleType(ImageView.ScaleType.FIT_XY);
            }



            final LinearLayout.LayoutParams optionLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            optionLayout.weight = 1.0f;
            optionLayout.gravity = Gravity.CENTER;

            LinearLayout options = new LinearLayout(getActivity());
            options.setOrientation(LinearLayout.HORIZONTAL);
            options.setLayoutParams(optionLayout);

            final LinearLayout.LayoutParams btnLayout = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
            btnLayout.weight = 1.0f;
            btnLayout.setMargins(10, 10, 10, 10);

            final Button view = new Button(getActivity());
            view.setLayoutParams(btnLayout);
            view.setText("View");
            view.setTextSize(15);
            view.setAllCaps(false);
            view.setBackgroundColor(Color.parseColor("#00000000"));
            view.setGravity(Gravity.CENTER);
            view.setTextColor(Color.BLACK);

            view.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    final LinearLayout parent = (LinearLayout) v.getParent().getParent();

                    Intent meetups = new Intent(getActivity(), ViewGroupActivity.class);
                    meetups.putExtra("GROUP_ID", parent.getTag() + "");

                    Log.d("GROUP_ID", parent.getTag() + "");

                    startActivity(meetups);
                    getActivity().finish();
                }
            });

            options.addView(view);

            recordOfGroups.addView(postedByLayout);
            postedByLayout.addView(groupPostedByImage);
            postedByLayout.addView(postedByDate);

            postedByDate.addView(groupPostedBy);
            postedByDate.addView(groupPostedDate);

            recordOfGroups.addView(groupName);
            recordOfGroups.addView(groupDetails);
            recordOfGroups.addView(groupImage);

            recordOfGroups.addView(options);

            listOfGroups.addView(recordOfGroups);
        }
        currentGroups.clear();

        Button btnSeeMore = new Button(getActivity());
        btnSeeMore.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        btnSeeMore.setText("see more");
        btnSeeMore.setTextSize(15);
        btnSeeMore.setAllCaps(false);
        btnSeeMore.setGravity(Gravity.CENTER);
        btnSeeMore.setBackgroundColor(Color.parseColor("#00000000"));
        listOfGroups.addView(btnSeeMore);
    }

    public String timeDiff(String timeDiff) {

        Log.d("Time diff", timeDiff);

        String Str = new String(timeDiff);
        String date[] = Str.split(":", 3);
        String time = "";

        if(Integer.parseInt(date[0]) > 24) {
            time = Integer.parseInt(date[0]) / 24 + " days";
        } else if (Integer.parseInt(date[0]) < 24 && Integer.parseInt(date[0]) > 0) {
            time = Integer.parseInt(date[0]) + " hrs";
        } else if (Integer.parseInt(date[0]) == 0 && Integer.parseInt(date[1]) != 0) {
            time = Integer.parseInt(date[1]) + " mins";
        } else if (Integer.parseInt(date[0]) == 0 && Integer.parseInt(date[1]) == 0) {
            time = Integer.parseInt(date[2]) + " secs";
        }

        return time;
    }

    /*
        thread
     */

    public void loadGroupImage() {
        for(Group group : currentGroups) {
            //new DownloadUserImage(group.getGroupImage() + ".JPG");
        }

        displayGroups();
    }

    class RetrieveGroups extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity(), R.style.progress);
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
                List<NameValuePair> params = new ArrayList<NameValuePair>();

                Log.d("USER_ID (user)", currentUser.getId() + "");
                params.add(new BasicNameValuePair("id", currentUser.getId() + ""));
                params.add(new BasicNameValuePair("query_type", "newsfeed"));

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
                    for (int i = 0; i < jUserArray.length(); i++) {
                        jUserObject = jUserArray.getJSONObject(i);


                        currentGroups.add(new Group(jUserObject.getInt("id"),
                                jUserObject.getString("group_name"), jUserObject.getString("details"),
                                jUserObject.getInt("created_by"), jUserObject.getString("time_diff"),
                                jUserObject.getInt("count_members"), jUserObject.getString("group_image"),
                                jUserObject.getString("created_by_user"), jUserObject.getString("user_image")));

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
                listOfFeeds.setVisibility(View.VISIBLE);
                listOfGroups.setVisibility(View.VISIBLE);

                if (message.equals("Successful")) {
                    Toast.makeText(getActivity(), message + "!", Toast.LENGTH_SHORT).show();
                    displayGroups();


                } else if (message.equals("No group")) {
                    lblMessage.setVisibility(View.VISIBLE);
                } else {
                    lblMessage.setVisibility(View.VISIBLE);
                    lblMessage.setText("Please check your internet connection");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    } // end of thread group activity


}
