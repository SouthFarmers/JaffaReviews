package jaffa.com.jaffareviews;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.LoggingBehavior;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.mingle.widget.LoadingView;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import jaffa.com.jaffareviews.Helpers.Constants;

/**
 * Created by gautham on 11/21/17.
 */

public class SplashActivity extends AppCompatActivity{

    private String firstName,lastName, email, coverpic;
    private String userId;
    private String TAG = "LoginActivity";
    private CallbackManager callbackManager;
    private LoginButton loginButton;
    Intent intent;
    boolean loggedin;
    private String friendsIDs = "friendsIDs";
    String frindsIDs="";
    LoadingView splashProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        FacebookSdk.setIsDebugEnabled(true);
        FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);

//        TwitterConfig config = new TwitterConfig.Builder(this)
//                .twitterAuthConfig(new TwitterAuthConfig(Constants.TWITTER_CONSUMER_KEY, Constants.TWITTER_CONSUMER_SECRET))
//                .build();
//        Twitter.initialize(config);

        setContentView(R.layout.activity_splash);
        splashProgress = (LoadingView) findViewById(R.id.splash_progress);
        intent = new Intent(this, MainActivity.class);


        Handler handler = new Handler();
        handler.postDelayed(new Runnable(){
            @Override
            public void run(){
                if(isLoggedIn()){
                    getFriends();
                    startActivity(intent);
                    splashProgress.setVisibility(View.GONE);
                }else{
                    tryLogin();
                }
            }
        }, 5);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void tryLogin(){
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(this);
        View mView = layoutInflaterAndroid.inflate(R.layout.splash_login_now, null);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(this);
        alertDialogBuilderUserInput.setTitle("Please Login...").setCancelable(true).setNegativeButton(
                "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                }).setView(mView);
        loginButton = (LoginButton) mView.findViewById(R.id.splash_loginButton);
        loginButton.setReadPermissions("public_profile", "user_friends");
        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, callback);
        intent = new Intent(this, MainActivity.class);
        AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        alertDialogAndroid.show();
    }

    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {

            GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(JSONObject object, GraphResponse response) {

                    try {
                        //AccessToken token = AccessToken.getCurrentAccessToken();
                        userId = object.getString("id");
                        if(object.has("first_name"))
                            firstName = object.getString("first_name");
                        if(object.has("last_name"))
                            lastName = object.getString("last_name");
                        if(object.has("email"))
                            email = object.getString("email");
                        if(object.has("cover"))
                            coverpic = object.getJSONObject("cover").getString("source");
                        getFriends();
                        SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.shared_pref_FbID), MODE_PRIVATE).edit();
                        editor.putString(getString(R.string.shared_pref_FbID), userId);
                        editor.commit();
                        //registerUserWithDB();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id, first_name, last_name, email, cover");
            request.setParameters(parameters);
            request.executeAsync();
        }

        @Override
        public void onCancel() {
        }

        @Override
        public void onError(FacebookException e) {
            e.printStackTrace();
        }
    };

    private void getFriends() {
        GraphRequest request= GraphRequest.newMyFriendsRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONArrayCallback() {

        @Override
        public void onCompleted(JSONArray objects, GraphResponse response) {
            // TODO Auto-generated method stub
            try
            {
                JSONArray raw = response.getJSONObject().getJSONArray("data");
                for(int x=0;x<objects.length();x++){
                    frindsIDs = raw.getJSONObject(x).getString("id");
                    frindsIDs = frindsIDs+",";
                }
                SharedPreferences.Editor editor3 = getSharedPreferences(friendsIDs, MODE_PRIVATE).edit();
                editor3.putString(friendsIDs, frindsIDs);
                editor3.commit();
                splashProgress.setVisibility(View.GONE);
                startActivity(intent);

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,installed");
        request.setParameters(parameters);
        request.executeAsync();

    }


//    private void registerUserWithDB(){
//        Boolean isCritic = false;
//        startActivity(intent);
//        finish();
//
//        SharedPreferences prefs = getSharedPreferences(getString(R.string.user_tag), MODE_PRIVATE);
//        if(prefs.getString(getString(R.string.shared_pref_FbID), null) == "IsCritic"){
//            isCritic = true;
//        }
//
//        JSONArray jsonArray = new JSONArray();
//        JSONObject jsonObject = new JSONObject();
//
//        try{
//            jsonObject.put("FirstName",firstName);
//            jsonObject.put("LastName",lastName);
//            jsonObject.put("EmailID", email);
//            jsonObject.put("IsCritic", isCritic);
//            jsonObject.put("FbID",userId);
//            jsonArray.put(jsonObject);
//
//        }catch(Exception e){
//
//        }
//
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, "http://jaffareviews.com/api/movie/AddUser", jsonObject,
//                new Response.Listener<JSONObject>(){
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        try {
//                            JSONArray arrData = response.getJSONArray("data");
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                },
//                new Response.ErrorListener(){
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Log.e("Error.Response", error.toString());
//                    }
//                });
//
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        requestQueue.add(jsonObjectRequest);
//    }

}
