package jaffa.com.jaffareviews.LeaderBoardCards;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import jaffa.com.jaffareviews.R;

import static android.content.Context.MODE_PRIVATE;

public class CardFragment extends Fragment {

    private CardView mCardView;
    String leaderboardname, leaderboardfollowers, leaderboardratings, leaderboardfbId;
    Boolean leaderboardisfollowing;
    TextView cardtitle,cardfollowers,cardreviews;
    Button cardfollowbutton;
    ImageView cardrank;
    String restoreduserid;

    public static CardFragment newInstance(String leaderboardname, String leaderboardfollowers, String leaderboardratings, String leaderboardfbId, Boolean leaderboardisfollowing) {
        CardFragment fragment = new CardFragment();
        Bundle bundleFeatures = new Bundle();
        bundleFeatures.putString("leaderboardname", leaderboardname);
        bundleFeatures.putString("leaderboardfollowers", leaderboardfollowers);
        bundleFeatures.putString("leaderboardratings", leaderboardratings);
        bundleFeatures.putString("leaderboardfbId", leaderboardfbId);
        bundleFeatures.putBoolean("leaderboardisfollowing", leaderboardisfollowing);
        fragment.setArguments(bundleFeatures);

        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getArguments() != null) {
            leaderboardname = getArguments().getString("leaderboardname");
            leaderboardfollowers = getArguments().getString("leaderboardfollowers");
            leaderboardratings = getArguments().getString("leaderboardratings");
            leaderboardfbId = getArguments().getString("leaderboardfbId");
            leaderboardisfollowing = getArguments().getBoolean("leaderboardisfollowing");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = getActivity().getSharedPreferences(getString(R.string.shared_pref_FbID), MODE_PRIVATE);
        restoreduserid = prefs.getString(getString(R.string.shared_pref_FbID), null);
        if (getArguments() != null) {
            leaderboardname = getArguments().getString("leaderboardname");
            leaderboardfollowers = getArguments().getString("leaderboardfollowers");
            leaderboardratings = getArguments().getString("leaderboardratings");
            leaderboardfbId = getArguments().getString("leaderboardfbId");
            leaderboardisfollowing = getArguments().getBoolean("leaderboardisfollowing");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.leaderboard_card, container, false);
        cardtitle = (TextView) view.findViewById(R.id.cardtitle);
        cardfollowers = (TextView) view.findViewById(R.id.cardfollowers);
        cardreviews = (TextView) view.findViewById(R.id.cardreviews);
        cardfollowbutton = (Button) view.findViewById(R.id.cardfollowbutton);
        cardrank = (ImageView) view.findViewById(R.id.cardrank);

        if(leaderboardisfollowing){
            cardfollowbutton.setText("Following");
        }

        cardfollowbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!leaderboardisfollowing) {
                    FollowUser(restoreduserid, leaderboardfbId);
                }
            }
        });

        cardtitle.setText(leaderboardname);
        cardfollowers.setText(leaderboardfollowers);
        cardreviews.setText(leaderboardratings);

        mCardView = (CardView) view.findViewById(R.id.cardView);
        mCardView.setMaxCardElevation(mCardView.getCardElevation()
                * CardAdapter.MAX_ELEVATION_FACTOR);
        return view;
    }

    public CardView getCardView() {
        return mCardView;
    }

    public void FollowUser(final String userFbID, final String FollowID){

        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();

        try{
            jsonObject.put("CriticFbID",FollowID);
            jsonObject.put("UserFbID",userFbID);
            jsonArray.put(jsonObject);

        }catch(Exception e){

        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, "http://jaffareviews.com/api/movie/FollowCritic", jsonObject,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        cardfollowbutton.setText("Following");
//                        try {
//                            //JSONArray arrData = response.getJSONArray("data");
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error.Response", error.toString());
                        Toast.makeText(getActivity(), "Something went wrong!",
                                Toast.LENGTH_LONG).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(jsonObjectRequest);
    }
}

