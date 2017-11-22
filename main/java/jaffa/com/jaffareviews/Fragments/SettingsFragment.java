package jaffa.com.jaffareviews.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SearchView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import jaffa.com.jaffareviews.Adapters.LeaderBoardAdapter;
import jaffa.com.jaffareviews.Adapters.SearchMoviesAdapter;
import jaffa.com.jaffareviews.POJO.SearchResultMoviesPOJO;
import jaffa.com.jaffareviews.POJO.SettingsPOJO;
import jaffa.com.jaffareviews.POJO.leaderboard;
import jaffa.com.jaffareviews.R;
import jaffa.com.jaffareviews.Volley.VolleySingleton;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by gautham on 11/16/17.
 */

public class SettingsFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {


    private SettingsFragment.OnSettingsFragmentListener mListener;
    private SwitchCompat notificationsSwitch, profileTypeSwitch;
    private CheckBox friendsRatingCheckbox, criticsRatingCheckbox, releasingThisWeekCheckbox;
    boolean profileTypeFlag, friendsRatingFlag, criticsRatingFlag, releasingThisWeekFlag;
    String restoreduserid;

    public static SettingsFragment newInstance() {
        Bundle args = new Bundle();
        SettingsFragment fragment = new SettingsFragment();
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
        if (context instanceof SettingsFragment.OnSettingsFragmentListener) {
            mListener = (SettingsFragment.OnSettingsFragmentListener) context;
        }else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSettingsFragmentListener");
        }

    }

    public interface OnSettingsFragmentListener {

    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        SharedPreferences prefs = getActivity().getSharedPreferences(getString(R.string.shared_pref_FbID), MODE_PRIVATE);
        restoreduserid = prefs.getString(getString(R.string.shared_pref_FbID), null);
        notificationsSwitch = (SwitchCompat) rootView.findViewById(R.id.notifications_switch);
        profileTypeSwitch = (SwitchCompat) rootView.findViewById(R.id.profile_type_switch);
        friendsRatingCheckbox = (CheckBox) rootView.findViewById(R.id.friends_ratings_checkbox);
        criticsRatingCheckbox = (CheckBox) rootView.findViewById(R.id.critic_ratings_checkbox);
        releasingThisWeekCheckbox = (CheckBox) rootView.findViewById(R.id.releasing_this_week_checkbox);


        friendsRatingCheckbox.setOnCheckedChangeListener(this);
        criticsRatingCheckbox.setOnCheckedChangeListener(this);
        releasingThisWeekCheckbox.setOnCheckedChangeListener(this);
        notificationsSwitch.setOnCheckedChangeListener(this);
        profileTypeSwitch.setOnCheckedChangeListener(this);

        getInitialSettings();


        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch(buttonView.getId()){
            case R.id.friends_ratings_checkbox:
                friendsRatingFlag = isChecked;
                break;
            case R.id.critic_ratings_checkbox:
                criticsRatingFlag = isChecked;
                break;
            case R.id.releasing_this_week_checkbox:
                releasingThisWeekFlag = isChecked;
                break;
            case R.id.profile_type_switch:
                profileTypeFlag = isChecked;
                break;
            case R.id.notifications_switch:
                break;
        }

        updateSettings();
    }

    public void getInitialSettings(){
        String url ="http://jaffareviews.com/api/Movie/GetSettings?UserFbID="+restoreduserid;

        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            friendsRatingFlag = response.getJSONObject("settings").optBoolean("Notification_FriedRating");
                            criticsRatingFlag = response.getJSONObject("settings").optBoolean("Notification_MyCriticRating");
                            releasingThisWeekFlag = response.getJSONObject("settings").optBoolean("Notification_NewReleases");
                            profileTypeFlag = response.getJSONObject("settings").optBoolean("IsCritic");

                            friendsRatingCheckbox.setChecked(friendsRatingFlag);
                            criticsRatingCheckbox.setChecked(criticsRatingFlag);
                            releasingThisWeekCheckbox.setChecked(releasingThisWeekFlag);
                            profileTypeSwitch.setChecked(profileTypeFlag);

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

    public void updateSettings(){

        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("Notification_FriedRating", friendsRatingFlag);
            jsonObject.put("Notification_MyCriticRating", criticsRatingFlag);
            jsonObject.put("Notification_NewReleases", releasingThisWeekFlag);
            jsonObject.put("IsCritic", profileTypeFlag);
            jsonObject.put("FbID", restoreduserid);
            jsonArray.put(jsonObject);

        } catch (Exception e) {

        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, "http://jaffareviews.com/api/movie/UpdateSettings", jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            Boolean arrData = response.getBoolean("Data");
                            if (!arrData) {
                            }
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

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(jsonObjectRequest);

    }

}
