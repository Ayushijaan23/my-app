package com.example.ayu.phasetwoproject.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.Group;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.ayu.phasetwoproject.LoginActivity;
import com.example.ayu.phasetwoproject.PersonalDetailActivity;
import com.example.ayu.phasetwoproject.R;
import com.example.ayu.phasetwoproject.SessionManager;
import com.example.ayu.phasetwoproject.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PersonalDetailsFragment extends Fragment {

    @BindView(R.id.add_details_button)
    Button mAddDetailsButton;
    @BindView(R.id.edit_details_button)
    Button mEditDetailsButton;
    @BindView(R.id.all_fields_groups)
    Group mFieldsGroup;
    @BindView(R.id.empty_text_view)
    TextView mEmptyTextView;
    @BindView(R.id.name_per_text_view)
    TextView mUserName;
    @BindView(R.id.mobile_per_text_view)
    TextView mUserMobile;
    @BindView(R.id.location_per_text_view)
    TextView mUserLocation;
    @BindView(R.id.links_per_text_view)
    TextView mUserLinks;
    private String mUrl = "http://139.59.65.145:9090//user/personaldetail/";
    private SessionManager mSessionManager;
    private VolleySingleton mVolleySingleton;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_personal_details, container, false);
        ButterKnife.bind(this, view);

        mSessionManager = new SessionManager(getContext());

        //Getting userId and UserEmail from shared preference and check for login functionality
        HashMap<String, String> userData;
        userData = mSessionManager.getUserDetails();
        final String id = userData.get(SessionManager.KEY_USERID);

        //check whether the given user is logged in or not
        if (!mSessionManager.isLoggedIn()) {
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
        } else {
            editUserData(id);
        }


        mEditDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), PersonalDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("user_id", id);
                bundle.putString("flag", "Edit");
                bundle.putString("name", mUserName.getText().toString());
                bundle.putString("mobile", mUserMobile.getText().toString());
                bundle.putString("links", mUserLinks.getText().toString());
                bundle.putString("location", mUserLocation.getText().toString());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        mAddDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), PersonalDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("user_id", id);
                bundle.putString("flag", "Add");
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        return view;
    }

    /***
     * This method is used to retrieve data from the server and preset onto user profile
     * @param id of the user
     */
    private void editUserData(String id) {
        mVolleySingleton = VolleySingleton.getInstance(getContext());
        final RequestQueue requestQueue = mVolleySingleton.getRequestQueue();
        requestQueue.start();

        mUrl += id;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, mUrl,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("personal-details", response.toString());
                if (response.isNull("data")) {
                    mFieldsGroup.setVisibility(View.GONE);
                    mEmptyTextView.setVisibility(View.VISIBLE);
                    mEditDetailsButton.setVisibility(View.GONE);
                    mAddDetailsButton.setVisibility(View.VISIBLE);
                } else {
                    mFieldsGroup.setVisibility(View.VISIBLE);
                    mEmptyTextView.setVisibility(View.GONE);
                    mEditDetailsButton.setVisibility(View.VISIBLE);
                    mAddDetailsButton.setVisibility(View.GONE);

                    try {
                        mUserName.setText(response.getJSONObject("data").getString("name"));
                        mUserMobile.setText(response.getJSONObject("data").getString("mobile_no"));
                        mUserLocation.setText(response.getJSONObject("data").getString("location"));
                        mUserLinks.setText(response.getJSONObject("data").getString("links"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(request);
    }
}
