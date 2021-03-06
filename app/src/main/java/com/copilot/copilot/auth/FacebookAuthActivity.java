package com.copilot.copilot.auth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.StringBuilderPrinter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.widget.RatingBar;
import android.widget.TextView;

import com.copilot.com.copilot.global.GlobalConstants;
import com.copilot.copilot.R;
import com.copilot.copilot.RoleActivity;
import com.copilot.copilot.SplashActivity;
import com.copilot.helper.HTTPRequestWrapper;
import com.copilot.helper.VolleyCallback;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import android.app.Dialog;

/**
 * Created by Akash on 2017-06-30.
 */

public class FacebookAuthActivity extends AppCompatActivity {
    private CallbackManager callbackManager;
    private HTTPRequestWrapper request;
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fb_auth);

        callbackManager = CallbackManager.Factory.create();

        request = new HTTPRequestWrapper(GlobalConstants.GLOBAL_URL, FacebookAuthActivity.this);

        final VolleyCallback successCallback = new VolleyCallback() {
            @Override
            public void onSuccessResponse(String response) {
                // Convert the json response;
                JSONObject jsonResponse = null;
                try {
                    jsonResponse = new JSONObject(response);
                } catch (JSONException e) {

                }
                String id = "";
                String token = "";

                try {
                    id = jsonResponse.getString("cpuserid");
                    token = jsonResponse.getString("token");
                } catch (JSONException e) {

                }

                // Save the JWT token and the id.
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(GlobalConstants.ACCESS_TOKEN, token);
                editor.putString(GlobalConstants.USER_ID, id);
                editor.commit();

                String accessToken = sharedPref.getString(GlobalConstants.ACCESS_TOKEN, "");
                Log.e("afasdfsadfas", "this is the access token in the call back: " + accessToken);

                Intent startApp = new Intent(FacebookAuthActivity.this, RoleActivity.class);
                startActivity(startApp);
                finish();
            }
        };

        final VolleyCallback failure = new VolleyCallback() {
            @Override
            public void onSuccessResponse(String response) {
                Toast.makeText(getApplicationContext(), "Login Failed: " + response, Toast.LENGTH_SHORT).show();
            }
        };

        AccessToken fbAt = AccessToken.getCurrentAccessToken();

        if (fbAt != null) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("access_token", fbAt.getToken());

            request.makeGetRequest(GlobalConstants.AUTH_ENDPOINT, params, successCallback, failure, null);
        }

        final LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList(GlobalConstants.FB_PERMISSIONS));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // Login to the app
                String accessToken = loginResult.getAccessToken().getToken();


                Map<String, String> params = new HashMap<String, String>();
                params.put("access_token", loginResult.getAccessToken().getToken());

                request.makeGetRequest(GlobalConstants.AUTH_ENDPOINT, params, successCallback, failure, null);

            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), "CANCELLED", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
