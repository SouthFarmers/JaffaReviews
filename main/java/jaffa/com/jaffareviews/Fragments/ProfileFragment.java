package jaffa.com.jaffareviews.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.droidbyme.dialoglib.DroidDialog;
import com.mingle.widget.LoadingView;

import org.json.JSONException;
import org.json.JSONObject;

import jaffa.com.jaffareviews.FullScreenDialog.FullScreenDialogContent;
import jaffa.com.jaffareviews.FullScreenDialog.FullScreenDialogController;
import jaffa.com.jaffareviews.Helpers.ImageHelper;
import jaffa.com.jaffareviews.R;
import jaffa.com.jaffareviews.Volley.VolleySingleton;

/**
 * Created by gautham on 11/17/17.
 */

public class ProfileFragment extends Fragment implements FullScreenDialogContent, View.OnClickListener {

    private FullScreenDialogController dialogController;
    public static final String EXTRA_NAME = "EXTRA_NAME";
    ImageView profilepicture;
    TextView profileName, profileEmail, profileFollowers, profileReviews, profileType, profileMemberSince, profilePrivacyPolicy, profileDisclaimer, profileAboutUS;
    LoadingView profileProgress;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);

    }

    @Override
    public void onDialogCreated(final FullScreenDialogController dialogController) {
        this.dialogController = dialogController;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        profilepicture = (ImageView) getView().findViewById(R.id.profile_picture);
        profileName = (TextView) getView().findViewById(R.id.user_profile_name);
        profileEmail = (TextView) getView().findViewById(R.id.user_profile_email);
        profileFollowers = (TextView) getView().findViewById(R.id.user_profile_followers);
        profileReviews = (TextView) getView().findViewById(R.id.user_profile_reviews);
        profileType = (TextView) getView().findViewById(R.id.user_profile_type);
        profileMemberSince = (TextView) getView().findViewById(R.id.user_profile_member_since);
        profileProgress = (LoadingView) getView().findViewById(R.id.profile_progress);
        profilePrivacyPolicy = (TextView) getView().findViewById(R.id.profile_privacy);
        profileDisclaimer = (TextView) getView().findViewById(R.id.profile_disclaimer);
        profileAboutUS = (TextView) getView().findViewById(R.id.profile_aboutus);

        profilePrivacyPolicy.setOnClickListener(this);
        profileDisclaimer.setOnClickListener(this);
        profileAboutUS.setOnClickListener(this);
        getUserInfo();

    }

    @Override
    public boolean onConfirmClick(FullScreenDialogController dialog) {
        return true;
    }

    @Override
    public boolean onDiscardClick(FullScreenDialogController dialog) {
        dialogController.discard();
        return true;
    }

    public void getUserInfo(){
        String url ="http://jaffareviews.com/api/Movie/CheckUser?UserFbID=1468306842";

        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            profileReviews.setText(response.getJSONObject("User").optString("ReviewsCount"));
                            profileEmail.setText(response.getJSONObject("User").optString("EmailID"));
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


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.profile_privacy:
                openPrivacyPolicy();
                break;
            case R.id.profile_disclaimer:
                openDisclaimer();
                break;
            case R.id.profile_about_us:
                openAboutUS();
                break;
        }
    }


    public void openPrivacyPolicy(){

        new DroidDialog.Builder(getContext())
                .title("All Well!")
                .content(getString(R.string.short_text))
                .cancelable(false, false)
                .positiveButton("OK", new DroidDialog.onPositiveListener() {
                    @Override
                    public void onPositive(Dialog droidDialog) {
                        Toast.makeText(getContext(), "ok", Toast.LENGTH_SHORT).show();
                        droidDialog.dismiss();
                    }
                })
                .show();

    }

    public void openDisclaimer(){

    }

    public void openAboutUS(){

    }

}
