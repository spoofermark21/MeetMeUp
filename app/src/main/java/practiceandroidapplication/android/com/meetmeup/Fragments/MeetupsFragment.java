package practiceandroidapplication.android.com.meetmeup.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import practiceandroidapplication.android.com.meetmeup.Entity.Meetups;
import practiceandroidapplication.android.com.meetmeup.Entity.Network;
import practiceandroidapplication.android.com.meetmeup.Entity.Sessions;
import practiceandroidapplication.android.com.meetmeup.Entity.User;
import practiceandroidapplication.android.com.meetmeup.Handles.JSONParser;
import practiceandroidapplication.android.com.meetmeup.Helpers.ImageHelper;
import practiceandroidapplication.android.com.meetmeup.R;
import practiceandroidapplication.android.com.meetmeup.ViewMapsActivity;
import practiceandroidapplication.android.com.meetmeup.ViewMeetupsActivity;
import practiceandroidapplication.android.com.meetmeup.ViewProfileActivity;


public class MeetupsFragment extends Fragment {

    private static final String RETRIEVE_MEETUPS_URL = Network.forDeploymentIp + "meetups_retrieve.php";
    private static final String TAG_STATUS = "status";
    private static final String TAG_RESPONSE = "response";

    JSONParser jsonParser = new JSONParser();
    ProgressDialog pDialog;

    Toolbar toolbar;

    LinearLayout listOfMeetups;
    TextView lblMessage;

    LinearLayout listOfFeeds;

    //Button btnMeetups;
    EditText txtSearch;

    List<Meetups> currentMeetups = new ArrayList<>();

    User currentUser = Sessions.getSessionsInstance().currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_meetups, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listOfFeeds = (LinearLayout) getActivity().findViewById(R.id.linear_feeds);

        listOfMeetups = (LinearLayout) getActivity().findViewById(R.id.linear_meetups);
        lblMessage = (TextView) getActivity().findViewById(R.id.lbl_message);

        listOfMeetups.setVisibility(View.INVISIBLE);
        lblMessage.setVisibility(View.GONE);

        txtSearch = (EditText) getActivity().findViewById(R.id.txt_search);
        txtSearch.setVisibility(View.GONE);

        txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //new RetrieveMeetups().execute();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.e("TextWatcherTest", "afterTextChanged:\t" + s.toString());
            }
        });
        new RetrieveMeetups().execute();
    }


    // to be examined
    @Override
    public void onResume() {
        super.onResume();
        //new RetrieveMeetups().execute();
    }

    public void displayMeetups() {

        listOfMeetups.removeAllViews();


        for (Meetups meetups : currentMeetups) {

            final LinearLayout.LayoutParams linearMeetups = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            linearMeetups.setMargins(0, 0, 0, 15);

            LinearLayout recordOfMeetups = new LinearLayout(getActivity());
            recordOfMeetups.setLayoutParams(linearMeetups);
            recordOfMeetups.setOrientation(LinearLayout.VERTICAL);
            recordOfMeetups.setBackgroundResource(R.drawable.main_background);
            recordOfMeetups.setPadding(20, 20, 20, 5);


            final LinearLayout.LayoutParams linearPostedBy = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            linearPostedBy.setMargins(0, 0, 0, 30);

            LinearLayout postedByLayout = new LinearLayout(getActivity());
            postedByLayout.setLayoutParams(linearPostedBy);
            postedByLayout.setOrientation(LinearLayout.HORIZONTAL);
            postedByLayout.setTag(meetups.getPostedBy());
            postedByLayout.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    startActivity(new Intent(getActivity(), ViewProfileActivity.class).putExtra("USER_ID", view.getTag() + "" + ""));
                    Log.d("USER_ID", view.getTag() + "");
                }
            });

            final LinearLayout.LayoutParams linearDate = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            //linearMeetups.setMargins(0, 0, 0, 30);

            LinearLayout postedByDate = new LinearLayout(getActivity());
            postedByDate.setLayoutParams(linearDate);
            postedByDate.setOrientation(LinearLayout.VERTICAL);


            Log.d("Meetup", meetups.getDetails());
            recordOfMeetups.setTag(meetups.getId());

            final LinearLayout.LayoutParams imageMeetupsParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            imageMeetupsParams.weight = 1.0f;
            imageMeetupsParams.height = 80;
            imageMeetupsParams.width = 80;
            imageMeetupsParams.setMargins(0, 0, 10, 0);
            imageMeetupsParams.gravity = Gravity.LEFT;

            final ImageView meetupPostedByImage = new ImageView(getActivity());
            meetupPostedByImage.setBackgroundColor(Color.parseColor("#E6E9ED"));

            final TextView meetupPostedBy = new TextView(getActivity());
            meetupPostedBy.setText(meetups.getPostedByName());
            meetupPostedBy.setTextColor(Color.BLACK);
            meetupPostedBy.setTextSize(17);

            if (!meetups.getPostedByNameImage().equals("null") && !meetups.getPostedByNameImage().equals("")) {

                class DownloadMeetupsImage extends AsyncTask<Void, Void, Bitmap> {

                    String filename;

                    public DownloadMeetupsImage(String filename) {
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
                                meetupPostedByImage.setImageBitmap(bitmap);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }

                }

                new DownloadMeetupsImage(meetups.getPostedByNameImage() + ".JPG").execute();
                meetupPostedByImage.setLayoutParams(imageMeetupsParams);
                meetupPostedByImage.setScaleType(ImageView.ScaleType.FIT_XY);
            }


            final TextView meetupPostedDate = new TextView(getActivity());
            meetupPostedDate.setText(timeDiff(meetups.getPostedDate()) + "");
            meetupPostedDate.setTextSize(15);

            final TextView meetupSubject = new TextView(getActivity());
            meetupSubject.setText(meetups.getSubject());
            meetupSubject.setTextSize(25);
            meetupSubject.setTextColor(Color.BLACK);
            meetupSubject.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    final LinearLayout parent = (LinearLayout) v.getParent();

                    Intent meetups = new Intent(getActivity(), ViewMeetupsActivity.class);
                    meetups.putExtra("MEETUPS_ID", parent.getTag() + "");

                    Log.d("MEETUPS_ID", parent.getTag() + "");

                    startActivity(meetups);
                    getActivity().finish();
                }
            });

            final TextView meetupDetails = new TextView(getActivity());
            meetupDetails.setText(meetups.getDetails());
            meetupDetails.setTextSize(15);
            meetupDetails.setTag(meetups.getLattitude());

            final TextView meetupLocation = new TextView(getActivity());
            meetupLocation.setText("Location: " + meetups.getLocation());
            meetupLocation.setTextSize(15);
            meetupLocation.setTag(meetups.getLongtitude());


            final TextView border = new TextView(getActivity());
            border.setText("__________________________");
            border.setTextSize(15);
            border.setGravity(Gravity.CENTER);

            final LinearLayout.LayoutParams optionLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            optionLayout.weight = 1.0f;
            optionLayout.gravity = Gravity.CENTER;

            LinearLayout options = new LinearLayout(getActivity());
            options.setOrientation(LinearLayout.HORIZONTAL);
            options.setLayoutParams(optionLayout);
            options.setTag(meetups.getLattitude() + "," + meetups.getLongtitude());

            final LinearLayout.LayoutParams btnLayout = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
            btnLayout.weight = 1.0f;
            btnLayout.setMargins(10, 10, 10, 10);

            final Button view = new Button(getActivity());
            view.setLayoutParams(btnLayout);
            view.setText("View");
            view.setTextSize(15);
            view.setAllCaps(false);
            view.setBackgroundColor(Color.parseColor("#00000000"));
            //view.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_info_black_24dp,0,0,0);
            view.setGravity(Gravity.CENTER);

            view.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    final LinearLayout parent = (LinearLayout) v.getParent().getParent();

                    Intent meetups = new Intent(getActivity(), ViewMeetupsActivity.class);
                    meetups.putExtra("MEETUPS_ID", parent.getTag() + "");

                    Log.d("MEETUPS_ID", parent.getTag() + "");

                    startActivity(meetups);
                    getActivity().finish();
                }
            });

            final Button map = new Button(getActivity());
            map.setLayoutParams(btnLayout);
            map.setText("Map");
            map.setTextSize(15);
            map.setAllCaps(false);
            map.setBackgroundColor(Color.parseColor("#00000000"));
            map.setGravity(Gravity.CENTER);

            map.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    final LinearLayout parent = (LinearLayout) v.getParent();
                    Intent intent = new Intent(getActivity(), ViewMapsActivity.class);

                    Log.d("Location", parent.getTag() + "");

                    String Str = new String(parent.getTag() + "");
                    String location[] = Str.split(",", 3);

                    intent.putExtra("LATTITUDE", location[0]);
                    intent.putExtra("LONGTITUDE", location[1]);

                    startActivity(intent);
                    //getActivity().finish();
                }
            });

            options.addView(view);
            options.addView(map);


            recordOfMeetups.addView(postedByLayout);
            postedByLayout.addView(meetupPostedByImage);

            postedByLayout.addView(postedByDate);
            postedByDate.addView(meetupPostedBy);
            postedByDate.addView(meetupPostedDate);


            recordOfMeetups.addView(meetupSubject);
            recordOfMeetups.addView(meetupDetails);
            //recordOfMeetups.addView(meetupPostedDate);
            recordOfMeetups.addView(meetupLocation);
            //recordOfMeetups.addView(border);
            recordOfMeetups.addView(options);

            listOfMeetups.addView(recordOfMeetups);
        }
        currentMeetups.clear();

        Button btnSeeMore = new Button(getActivity());
        btnSeeMore.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        btnSeeMore.setText("see more");
        btnSeeMore.setTextSize(15);
        btnSeeMore.setAllCaps(false);
        btnSeeMore.setGravity(Gravity.CENTER);
        btnSeeMore.setBackgroundColor(Color.parseColor("#00000000"));
        listOfMeetups.addView(btnSeeMore);
    }

    public String timeDiff(String timeDiff) {

        Log.d("Time diff", timeDiff);

        String Str = new String(timeDiff);
        String date[] = Str.split(":", 3);
        String time = "";

        if (Integer.parseInt(date[0]) > 24) {
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

    /* thread */

    class RetrieveMeetups extends AsyncTask<String, String, String> {

        String searchParameter;

        public RetrieveMeetups() {

        }

        public RetrieveMeetups(String searchParameter) {
            this.searchParameter = searchParameter;
        }

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
                List<NameValuePair> params = new ArrayList<>();

                Log.d("USER_ID (user)", currentUser.getId() + "");

                params.add(new BasicNameValuePair("id", currentUser.getId() + ""));
                params.add(new BasicNameValuePair("filter", "newsfeed"));
                params.add(new BasicNameValuePair("user_id", currentUser.getId() + ""));

                Log.d("request!", "starting");

                JSONObject json = jsonParser.makeHttpRequest(
                        RETRIEVE_MEETUPS_URL, "POST", params);

                Log.d("Fetching...", json.toString());

                success = json.getInt(TAG_STATUS);

                if (success == 1) {
                    Log.d("Success!", json.toString());

                    JSONArray jUserArray = json.getJSONArray("meetups");
                    JSONObject jUserObject;

                    currentMeetups.clear();

                    for (int i = 0; i < jUserArray.length(); i++) {
                        jUserObject = jUserArray.getJSONObject(i);

                        Meetups meetup = new Meetups(jUserObject.getInt("id"), jUserObject.getString("subject"),
                                jUserObject.getString("details"), jUserObject.getString("location"),
                                jUserObject.getString("posted_date"), jUserObject.getString("key"));

                        meetup.setPostedDate(jUserObject.getString("time_diff"));
                        meetup.setPostedBy(jUserObject.getInt("posted_by"));
                        meetup.setPostedByName(jUserObject.getString("posted_by_user"));
                        meetup.setPostedByNameImage(jUserObject.getString("user_image"));

                        meetup.setLattitude(jUserObject.getString("lattitude"));
                        meetup.setLongtitude(jUserObject.getString("longtitude"));

                        Log.d("Location", meetup.getLattitude() + " " + meetup.getLongtitude());

                        currentMeetups.add(meetup);

                        Log.d("ID:", jUserObject.getInt("id") + "");
                        Log.d("Meetups size", currentMeetups.size() + "");
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
                listOfMeetups.setVisibility(View.VISIBLE);

                if (message.equals("Successful")) {
                    Toast.makeText(getActivity(), message + "!", Toast.LENGTH_SHORT).show();
                    displayMeetups();
                } else if (message.equals("No meetups")) {
                    lblMessage.setVisibility(View.VISIBLE);
                } else {
                    lblMessage.setVisibility(View.VISIBLE);
                    lblMessage.setText("Please check your internet connection");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    } // end of thread retrieve user


}
