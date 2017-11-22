package jaffa.com.jaffareviews.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import jaffa.com.jaffareviews.Adapters.ConnectionsAdapter;
import jaffa.com.jaffareviews.POJO.leaderboard;
import jaffa.com.jaffareviews.R;
import jaffa.com.jaffareviews.Volley.VolleySingleton;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by gautham on 11/21/17.
 */

public class ConnectionsFragment extends Fragment {

    private ConnectionsFragment.OnConnectionsFragmentListener mListener;
    private RecyclerView connectionsFriendsRecyerView, connectionsCriticsRecyerView;
    private TextView connectionsFriendsLabel, connectionsCriticsLabel;
    String restoreduserid;

    public static ConnectionsFragment newInstance() {
        Bundle args = new Bundle();
        ConnectionsFragment fragment = new ConnectionsFragment();
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
        if (context instanceof ConnectionsFragment.OnConnectionsFragmentListener) {
            mListener = (ConnectionsFragment.OnConnectionsFragmentListener) context;
        }else {
            throw new RuntimeException(context.toString()
                    + " must implement OnConnectionsFragmentListener");
        }

    }

    public interface OnConnectionsFragmentListener {
        void onConnectionClick(String fbID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_connections, container, false);

        SharedPreferences prefs = getActivity().getSharedPreferences(getString(R.string.shared_pref_FbID), MODE_PRIVATE);
        restoreduserid = prefs.getString(getString(R.string.shared_pref_FbID), null);

        connectionsFriendsRecyerView = (RecyclerView) rootView.findViewById(R.id.connections_friends);
        connectionsCriticsRecyerView = (RecyclerView) rootView.findViewById(R.id.connections_critics);
        connectionsFriendsLabel = (TextView) rootView.findViewById(R.id.connections_friends_label);
        connectionsCriticsLabel = (TextView) rootView.findViewById(R.id.connections_critics_label);

        connectionsFriendsLabel.setText(getString(R.string.connections_fragment_friends_label), TextView.BufferType.SPANNABLE);
        Spannable spannable = (Spannable) connectionsFriendsLabel.getText();
        spannable.setSpan(new StrikethroughSpan(), 3, 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        connectionsCriticsLabel.setText(getString(R.string.connections_fragment_critics_label), TextView.BufferType.SPANNABLE);
        Spannable spannable2 = (Spannable) connectionsCriticsLabel.getText();
        spannable2.setSpan(new StrikethroughSpan(), 3, 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        getCriticsConnections();
        getFriendsConnections();
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void getCriticsConnections(){
        String url ="http://jaffareviews.com/api/Movie/GetConnections?fbID="+restoreduserid;

        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            ArrayList<leaderboard> listitems = new ArrayList<>();

                            JSONArray jsonArray = response.getJSONArray("critics");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                if(restoreduserid.equalsIgnoreCase(jsonArray.getJSONObject(i).optString("fbID"))) {

                                }else{
                                    leaderboard lboard = new leaderboard();
                                    lboard.setName((jsonArray.getJSONObject(i).optString("FirstName")) + " " + (jsonArray.getJSONObject(i).optString("LastName")));
                                    lboard.setFollowers((jsonArray.getJSONObject(i).optString("NumOfFollowers")));
                                    lboard.setRatings((jsonArray.getJSONObject(i).optString("NumOfRatings")));
                                    lboard.setFbid((jsonArray.getJSONObject(i).optString("fbID")));
                                    lboard.setFollowing((jsonArray.getJSONObject(i).optBoolean("IsFollowing")));
                                    listitems.add(lboard);
                                }
                            }


                            connectionsCriticsRecyerView.setHasFixedSize(true);
                            LinearLayoutManager MyLayoutManager = new LinearLayoutManager(getActivity());
                            MyLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                            if (listitems.size() > 0 & connectionsCriticsRecyerView != null) {
                                connectionsCriticsRecyerView.setAdapter(new ConnectionsAdapter(getActivity(),listitems));
                            }
                            connectionsCriticsRecyerView.setLayoutManager(MyLayoutManager);

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

    public void getFriendsConnections(){
        GraphRequest request= GraphRequest.newMyFriendsRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONArrayCallback() {

            @Override
            public void onCompleted(JSONArray objects, GraphResponse response) {
                // TODO Auto-generated method stub
                try
                {
                    ArrayList<leaderboard> listfriends = new ArrayList<>();
                    leaderboard lboard = new leaderboard();
                    JSONArray raw = response.getJSONObject().getJSONArray("data");
                    for(int x=0;x<objects.length();x++){
                        lboard.setName(raw.getJSONObject(x).getString("name"));
                        lboard.setFbid(raw.getJSONObject(x).getString("id"));
                        listfriends.add(lboard);
                    }

                    connectionsFriendsRecyerView.setHasFixedSize(true);
                    LinearLayoutManager MyLayoutManager = new LinearLayoutManager(getActivity());
                    MyLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                    if (listfriends.size() > 0 & connectionsFriendsRecyerView != null) {
                        connectionsFriendsRecyerView.setAdapter(new ConnectionsAdapter(getActivity(),listfriends));
                    }
                    connectionsFriendsRecyerView.setLayoutManager(MyLayoutManager);

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
}
