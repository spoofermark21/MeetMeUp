package practiceandroidapplication.android.com.meetmeup.Fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import practiceandroidapplication.android.com.meetmeup.EditGroupActivity;
import practiceandroidapplication.android.com.meetmeup.Entity.Group;
import practiceandroidapplication.android.com.meetmeup.Entity.Meetups;
import practiceandroidapplication.android.com.meetmeup.Entity.Network;
import practiceandroidapplication.android.com.meetmeup.Entity.Sessions;
import practiceandroidapplication.android.com.meetmeup.Entity.User;
import practiceandroidapplication.android.com.meetmeup.Handles.JSONParser;
import practiceandroidapplication.android.com.meetmeup.R;


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
        lblMessage.setVisibility(View.INVISIBLE);

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

            LinearLayout recordOfGroups = new LinearLayout(getActivity());
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

            final ImageView groupImage = new ImageView(getActivity());
            groupImage.setImageResource(R.drawable.meetmeup);
            groupImage.setLayoutParams(imageGroupParams);*/

            final TextView groupName = new TextView(getActivity());
            groupName.setText(group.getGroupName());
            groupName.setTextSize(25);
            groupName.setTextColor(Color.BLACK);

            final TextView groupDetails = new TextView(getActivity());
            groupDetails.setText(group.getDetails());
            groupDetails.setTextSize(15);
            groupDetails.setTextColor(Color.BLACK);

            final TextView groupCountMembers = new TextView(getActivity());
            groupCountMembers.setText(group.getTotalMembers() + " members");
            groupCountMembers.setTextSize(15);
            groupCountMembers.setTextColor(Color.BLACK);

            final TextView createdBy = new TextView(getActivity());
            createdBy.setText("Created by: Mark D. Sibi");
            createdBy.setTextSize(15);
            createdBy.setTextColor(Color.BLACK);

            final LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
            params2.weight = 1.0f;
            params2.gravity = Gravity.RIGHT;
            params2.leftMargin = 50;


            LinearLayout options = new LinearLayout(getActivity());
            options.setOrientation(LinearLayout.HORIZONTAL);
            options.setPadding(10, 10, 10, 10);
            options.setLayoutParams(params2);

            final TextView view = new TextView(getActivity());
            view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            view.setPadding(10, 10, 0, 10);
            view.setText("view");
            view.setTextSize(15);
            view.setBackgroundColor(Color.TRANSPARENT);


            view.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    final LinearLayout parent = (LinearLayout) v.getParent().getParent();

                    Intent groups = new Intent(getActivity(), EditGroupActivity.class);
                    groups.putExtra("Groups ID", parent.getTag() + "");
                    startActivity(groups);

                    Toast.makeText(getActivity(), parent.getTag() + "!", Toast.LENGTH_SHORT).show();
                }
            });

            options.addView(view);

            //recordOfGroups.addView(groupImage);
            recordOfGroups.addView(groupName);
            recordOfGroups.addView(groupDetails);
            recordOfGroups.addView(groupCountMembers);
            recordOfGroups.addView(createdBy);
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
            pDialog = new ProgressDialog(getActivity(), R.style.progress);
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
                                jUserObject.getInt("created_by"), jUserObject.getString("created_date"),
                                jUserObject.getInt("count_members")));

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
