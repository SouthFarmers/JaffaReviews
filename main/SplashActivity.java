package jaffa.com.jaffareviews;


import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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
import com.google.android.exoplayer.C;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.mingle.widget.LoadingView;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import jaffa.com.jaffareviews.Helpers.Constants;

/**
 * Created by gautham on 11/21/17.
 */


public class SplashActivity extends AppCompatActivity implements View.OnClickListener{

    private String firstName,lastName, email, coverpic;
    private String userId;
    private String TAG = "LoginActivity";
    private CallbackManager callbackManager;
    private LoginButton loginButton;
    private CheckBox selectfanCheckbox, selectcriticCheckbox;
    Intent intent;
    boolean loggedin;
    private String friendsIDs = "friendsIDs";
    String frindsIDs="";
    LoadingView splashProgress;
    private Button saveSplash;
    private static final int NOTIFICATION_PERMISSION_CODE = 123;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private boolean isReceiverRegistered;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private android.app.AlertDialog alertDialog;
    private AlertDialog alertDialogAndroid;
    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    protected String SENDER_ID = "43283639347";
    private GoogleCloudMessaging gcm =null;
    private String regid = null;
    private Context context= null;

    @RequiresApi(api = Build.VERSION_CODES.M)
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
        selectfanCheckbox = (CheckBox) findViewById(R.id.select_fan_checkbox);
        selectcriticCheckbox = (CheckBox) findViewById(R.id.select_critic_checkbox);
        intent = new Intent(this, MainActivity.class);
        saveSplash = (Button) findViewById(R.id.save_splash);
        selectfanCheckbox.setOnClickListener(this);
        selectcriticCheckbox.setOnClickListener(this);
        saveSplash.setOnClickListener(this);


        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId(context);

            if (regid.isEmpty()) {
                registerInBackground();
            }
        }

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
    protected void onResume() {
        super.onResume();
        checkPlayServices();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.d(TAG, "This device is not supported - Google Play Services.");
                finish();
            }
            return false;
        }
        return true;
    }

    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.d(TAG, "Registration ID not found.");
            return "";
        }
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
//        int currentVersion = getAppVersion(context);
//        if (registeredVersion != currentVersion) {
//            Log.d(TAG, "App version changed.");
//            return "";
//        }
        return registrationId;
    }

    private SharedPreferences getGCMPreferences(Context context) {
        return getSharedPreferences(SplashActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    private void registerInBackground() {
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object...params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(SENDER_ID);
                    final SharedPreferences prefs = getGCMPreferences(context);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString(PROPERTY_REG_ID, regid);
                    editor.commit();
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return null;
            }
            protected void onPostExecute(Object result) {
                //to do here
            };
        }.execute(null, null, null);
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.select_fan_checkbox:
                selectcriticCheckbox.setChecked(false);
                break;
            case R.id.select_critic_checkbox:
                selectfanCheckbox.setChecked(false);
                break;
            case R.id.save_splash:
                registerUserWithDB();
                break;
        }
    }

    public void tryLogin(){
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(this);
        View mView = layoutInflaterAndroid.inflate(R.layout.splash_login_now, null);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(this);
        alertDialogBuilderUserInput.setTitle("Please Login...").setView(mView);
        loginButton = (LoginButton) mView.findViewById(R.id.splash_loginButton);
        loginButton.setReadPermissions("public_profile", "user_friends");
        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, callback);
        intent = new Intent(this, MainActivity.class);
        alertDialogAndroid = alertDialogBuilderUserInput.create();
        alertDialogAndroid.setCanceledOnTouchOutside(false);
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
//                alertDialogAndroid.dismiss();
                splashProgress.setVisibility(View.GONE);
//                startActivity(intent);

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



    private void registerUserWithDB(){
        if(!selectfanCheckbox.isChecked() && !selectcriticCheckbox.isChecked()){
            alertDialog = new android.app.AlertDialog.Builder(this).create();
            alertDialog.setTitle("Alert");
            alertDialog.setMessage("Choose One Option");
            alertDialog.setButton(android.app.AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }else {
            startActivity(intent);
            finish();
            boolean isCritic = false;
            if (selectfanCheckbox.isChecked()) {
                isCritic = false;
            } else if (selectcriticCheckbox.isChecked()) {
                isCritic = true;
            }

            JSONArray jsonArray = new JSONArray();
            JSONObject jsonObject = new JSONObject();

            try {
                jsonObject.put("FirstName", firstName);
                jsonObject.put("LastName", lastName);
                jsonObject.put("EmailID", email);
                jsonObject.put("IsCritic", isCritic);
                jsonObject.put("FbID", userId);
                jsonObject.put("DeviceUDID", Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID));
                jsonObject.put("DeviceToken", regid);
                jsonObject.put("DevicePlatform", "Android");
                jsonObject.put("DeviceOsVersion", android.os.Build.VERSION.RELEASE);
                jsonArray.put(jsonObject);

            } catch (Exception e) {

            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, "http://jaffareviews.com/api/movie/AddUser", jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                boolean arrData = response.getBoolean("Data");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Error.Response", error.toString());
                        }
                    });

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(jsonObjectRequest);
        }
    }

}
