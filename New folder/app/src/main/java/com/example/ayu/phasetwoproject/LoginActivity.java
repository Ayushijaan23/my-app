package com.example.ayu.phasetwoproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    /***
     * Binding all the required fields and button using Butterknife library
     */
    @BindView(R.id.user_email_for_login)
    EditText mUserEmail;
    @BindView(R.id.user_pwd_for_login)
    EditText mUserPassword;
    @BindView(R.id.login_button)
    Button mLoginButton;
    @BindView(R.id.new_user_signup_button)
    Button mSignUpButton;
    /***
     * Create RequestQueue, mUrl, VolleySingleton and SessionManager class variable
     */
    private RequestQueue mRequestQueue;
    private String mUrl = "http://139.59.65.145:9090/user/login";
    private VolleySingleton mVolleySingleton;
    private SessionManager mSessionManager;
    /***
     * These variables stored the UserId and UserEmail received form JSON response
     */
    private String mId;
    private String mEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        mSessionManager = new SessionManager(getApplicationContext());
        setNewVolleyRequest();

        //perform click function of loginButton
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postLoginData();
            }
        });

        //perform click function of signUpButton
        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

    }

    /***
     * Setting new VolleyRequest and create new RequestQueue object
     * for both login details and signUp details
     */
    private void setNewVolleyRequest() {
        mVolleySingleton = VolleySingleton.getInstance(this);
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024);
        Network network = new BasicNetwork(new HurlStack());
        mRequestQueue = mVolleySingleton.getRequestQueue(cache, network);
        mRequestQueue.start();
    }

    /***
     * posting login data onto the server and get some response
     */
    private void postLoginData() {
        //Creating JSON object from the input provided by the user
        JSONObject userCredential = new JSONObject();
        try {
            userCredential.put("email", mUserEmail.getText().toString());
            userCredential.put("password", mUserPassword.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Creating new JsonObjectRequest object and perform the posting of login data and receive json response from the server
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                mUrl, userCredential,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("Response-info", response.toString());
                        if (!response.isNull("status_message")) {
                            Toast.makeText(LoginActivity.this, "Please Enter your correct credential or create new account", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this, "Successfully login", Toast.LENGTH_SHORT).show();
                            try {
                                mId = response.getJSONObject("data").getString("id");
                                mEmail = response.getJSONObject("data").getString("email");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            mSessionManager.createLoginSession(mId, mEmail);
                            Intent intent = new Intent(getApplicationContext(), ViewPagerActivity.class);
                            startActivity(intent);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error", error.toString());
            }
        });
        mRequestQueue.add(request);
    }
}
