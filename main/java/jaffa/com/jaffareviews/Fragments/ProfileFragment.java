package jaffa.com.jaffareviews.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.LoginButton;
import com.mingle.widget.LoadingView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import jaffa.com.jaffareviews.Adapters.LeaderBoardAdapter;
import jaffa.com.jaffareviews.Adapters.SearchMoviesAdapter;
import jaffa.com.jaffareviews.FullScreenDialog.FullScreenDialogController;
import jaffa.com.jaffareviews.Helpers.AnimUtils;
import jaffa.com.jaffareviews.Helpers.DroidDialog;
import jaffa.com.jaffareviews.Helpers.ImageHelper;
import jaffa.com.jaffareviews.POJO.SearchResultMoviesPOJO;
import jaffa.com.jaffareviews.POJO.leaderboard;
import jaffa.com.jaffareviews.R;
import jaffa.com.jaffareviews.SplashActivity;
import jaffa.com.jaffareviews.Volley.VolleySingleton;

import static android.content.Context.MODE_PRIVATE;

public class ProfileFragment extends Fragment implements View.OnClickListener{

    private ProfileFragment.OnProfileFragmentListener mListener;
    private FullScreenDialogController dialogController;
    public static final String EXTRA_NAME = "EXTRA_NAME";
    ImageView profilepicture, profileclose;
    TextView profileName, profileFollowers, profileReviews, profileType, profileMemberSince, profileMyReviews, profilePrivacyPolicy, profileDisclaimer, profileAboutUS;
    LoadingView profileProgress;
    private LoginButton logoutButton;
    String restoreduserid;

    public static ProfileFragment newInstance() {
        Bundle args = new Bundle();
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(getString(R.string.app_name));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ProfileFragment.OnProfileFragmentListener) {
            mListener = (ProfileFragment.OnProfileFragmentListener) context;
        }else {
            throw new RuntimeException(context.toString()
                    + " must implement OnProfileFragmentListener");
        }

    }

    public interface OnProfileFragmentListener {
        void onMyReviewClick(String fbID);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.profile_privacy:
                openAboutUS();
                break;
            case R.id.profile_disclaimer:
                openAboutUS();
                break;
            case R.id.profile_aboutus:
                openAboutUS();
                break;
            case R.id.profile_myReviews:
                mListener.onMyReviewClick(restoreduserid);
                break;
            case R.id.profile_close:
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.popBackStack();
                break;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        SharedPreferences prefs = getActivity().getSharedPreferences(getString(R.string.shared_pref_FbID), MODE_PRIVATE);
        profilepicture = (ImageView) rootView.findViewById(R.id.profile_picture);
        profileName = (TextView) rootView.findViewById(R.id.user_profile_name);
        profileFollowers = (TextView) rootView.findViewById(R.id.user_profile_followers);
        profileReviews = (TextView) rootView.findViewById(R.id.user_profile_reviews);
        profileType = (TextView) rootView.findViewById(R.id.user_profile_type);
        profileMemberSince = (TextView) rootView.findViewById(R.id.user_profile_member_since);
        profileProgress = (LoadingView) rootView.findViewById(R.id.profile_progress);
        profileMyReviews = (TextView) rootView.findViewById(R.id.profile_myReviews);
        profilePrivacyPolicy = (TextView) rootView.findViewById(R.id.profile_privacy);
        profileDisclaimer = (TextView) rootView.findViewById(R.id.profile_disclaimer);
        profileAboutUS = (TextView) rootView.findViewById(R.id.profile_aboutus);
        profileclose = (ImageView) rootView.findViewById(R.id.profile_close);

        profileMyReviews.setOnClickListener(this);
        profilePrivacyPolicy.setOnClickListener(this);
        profileDisclaimer.setOnClickListener(this);
        profileAboutUS.setOnClickListener(this);
        profileclose.setOnClickListener(this);

        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        logoutButton = (LoginButton) rootView.findViewById(R.id.loginButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });
        restoreduserid = prefs.getString(getString(R.string.shared_pref_FbID), null);

        getUserInfo();

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void logout(){
        LoginManager.getInstance().logOut();
        Intent splash = new Intent(getActivity(), SplashActivity.class);
        splash.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        splash.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        splash.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(splash);
    }

    public void getUserInfo(){

        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("FbID", restoreduserid);
            jsonObject.put("DeviceUDID", Settings.Secure.getString(getContext().getContentResolver(),Settings.Secure.ANDROID_ID));
            jsonObject.put("DeviceToken", "");
            jsonObject.put("DevicePlatform", "Android");
            jsonObject.put("DeviceOsVersion", android.os.Build.VERSION.RELEASE);

            jsonArray.put(jsonObject);

        } catch (Exception e) {

        }

        String url ="http://jaffareviews.com/api/Movie/CheckUser";

        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            profileReviews.setText(response.getJSONObject("User").optString("ReviewsCount"));
                            profileName.setText(response.getJSONObject("User").optString("FirstName")+" "+response.getJSONObject("User").optString("LastName"));
                            profileFollowers.setText(response.getJSONObject("User").optString("FollowersCount"));
                            if(response.getJSONObject("User").optBoolean("IsCritic")){
                                profileType.setText("Film Critic");
                            }else{
                                profileType.setVisibility(View.GONE);
                            }
                            profileMemberSince.setText(response.getJSONObject("User").optString("MemberSince"));

                            setProfilePic(response.getJSONObject("User").optString("FbID"));

                        } catch (JSONException e) {

                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });

        VolleySingleton.getInstance().addToRequestQueue(jsonRequest);
    }

    public void setProfilePic(String userId){
        ImageRequest imgRequest = new ImageRequest("https://graph.facebook.com/" + userId + "/picture?width=400&height=400",
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        profilepicture.setImageBitmap(ImageHelper.getRoundedCornerBitmap(getActivity(), response, 150, 400, 400, false, false, false, false));
                        profileProgress.setVisibility(View.GONE);

                    }
                }, 0, 0, ImageView.ScaleType.FIT_CENTER, Bitmap.Config.ARGB_8888, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                profilepicture.setBackgroundColor(Color.parseColor("#ff0000"));
                profileProgress.setVisibility(View.GONE);
                error.printStackTrace();
            }
        });
        VolleySingleton.getInstance().addToRequestQueue(imgRequest);
    }

//    public void openPrivacyPolicy(){
//
//        new DroidDialog.Builder(getContext())
//                .icon(R.drawable.jaffa)
//                .title("All Well!")
//                .content(getString(R.string.long_text))
//                .cancelable(true, true)
//                .positiveButton("OK", new DroidDialog.onPositiveListener() {
//                    @Override
//                    public void onPositive(Dialog droidDialog) {
////                        Toast.makeText(getContext(), "ok", Toast.LENGTH_SHORT).show();
//                        droidDialog.dismiss();
//                    }
//                })
//                .show();
//
//    }

//    public void openDisclaimer(){
//        new DroidDialog.Builder(getContext())
//                .icon(R.drawable.jaffa)
//                .title("All Well!")
//                .content(getString(R.string.short_text))
//                .cancelable(true, true)
//                .positiveButton("OK", new DroidDialog.onPositiveListener() {
//                    @Override
//                    public void onPositive(Dialog droidDialog) {
//                        droidDialog.dismiss();
//                    }
//                })
//                .typeface("regular.ttf")
//                .animation(AnimUtils.AnimFadeInOut)
//                .color(ContextCompat.getColor(getContext(), R.color.indigo), ContextCompat.getColor(getContext(), R.color.white),
//                        ContextCompat.getColor(getContext(), R.color.dark_indigo))
//                .divider(true, ContextCompat.getColor(getContext(), R.color.orange))
//                .show();
//
//    }

    public void openAboutUS(){
        new DroidDialog.Builder(getContext())
                .icon(R.drawable.jaffa)
                .title("All Well!")
                .content(getString(R.string.short_text))
                .cancelable(true, true)
                .positiveButton("ok", new DroidDialog.onPositiveListener() {
                    @Override
                    public void onPositive(Dialog droidDialog) {
                        droidDialog.dismiss();
                    }
                })
                .typeface("regular.ttf")
                .animation(AnimUtils.AnimLeftRight)
                .show();

    }

}