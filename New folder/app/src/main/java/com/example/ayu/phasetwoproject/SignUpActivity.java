package com.example.ayu.phasetwoproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
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

public class SignUpActivity extends AppCompatActivity {

    /***
     * Binding all the required fields and button using Butterknife library
     */
    @BindView(R.id.user_email_for_sign_up)
    EditText mUserEmail;
    @BindView(R.id.user_pwd_for_sign_up)
    EditText mUserPassword;
    @BindView(R.id.sign_up_button)
    Button mSignUpButton;
    private String mPwd;
    private String mEmail;
    /***
     * creating mUrl and VolleySingleton class variable
     */
    private VolleySingleton mVolleySingleton;
    private String mUrl = "http://139.59.65.145:9090/user/signup";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);


        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEmail = mUserEmail.getText().toString();
                mPwd = mUserPassword.getText().toString();
                if (isValidEmail(mEmail)) {
                    if (!isValidPassword(mPwd)) {
                        setNewVolleyRequest();
                        postSignUpData(mEmail);
                    } else {
                        Toast.makeText(getApplicationContext(), "Password cannot be empty", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Enter Valid Email Address and password cannot be empty", Toast.LENGTH_SHORT).show();
                }
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
        final RequestQueue requestQueue = mVolleySingleton.getRequestQueue(cache, network);
        requestQueue.start();
    }

    /***
     * This method is used to check whether the given email address is valid or not
     * @param email provide by the user
     * @return
     */
    private boolean isValidEmail(String email) {
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    /***
     * This method is used to check whether the given password is valid or not
     * @param password provide by the user
     * @return
     */
    private boolean isValidPassword(String password) {
        return (TextUtils.isEmpty(password));
    }

    /***
     * posting signup data onto the server and get some response
     */
    private void postSignUpData(String emailId) {
        JSONObject userCredential = new JSONObject();
        try {
            userCredential.put("email", emailId);
            userCredential.put("password", mUserPassword.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                mUrl, userCredential,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("Response-signUp", response.toString());
                        if (!response.isNull("data")) {
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(intent);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error", error.toString());
            }
        });

        mVolleySingleton.addToRequestQueue(request);
    }
}
