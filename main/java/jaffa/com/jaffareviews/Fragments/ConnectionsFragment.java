package jaffa.com.jaffareviews.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
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
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
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

        getConnections();
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void getConnections(){
        String url ="http://jaffareviews.com/api/Movie/GetConnections?fbID="+restoreduserid;

        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            ArrayList<leaderboard> friendslistitems = new ArrayList<>();
                            ArrayList<leaderboard> criticslistitems = new ArrayList<>();

                            JSONArray jsonArray = response.getJSONArray("connections");
                            for (int i = 0; i < jsonArray.length(); i++) {

                                if (jsonArray.getJSONObject(i).optString("IsFriend").equalsIgnoreCase("true")) {
                                    leaderboard friends_connections = new leaderboard();
                                    friends_connections.setName((jsonArray.getJSONObject(i).optString("FirstName")) + " " + (jsonArray.getJSONObject(i).optString("LastName")));
                                    friends_connections.setFbid((jsonArray.getJSONObject(i).optString("fbID")));
                                    friendslistitems.add(friends_connections);
                                }else{
                                    leaderboard critics_connections = new leaderboard();
                                    critics_connections.setName((jsonArray.getJSONObject(i).optString("FirstName")) + " " + (jsonArray.getJSONObject(i).optString("LastName")));
                                    critics_connections.setFbid((jsonArray.getJSONObject(i).optString("fbID")));
                                    criticslistitems.add(critics_connections);

                                }

                            }


                            connectionsCriticsRecyerView.setHasFixedSize(true);
                            LinearLayoutManager MyLayoutManager = new LinearLayoutManager(getActivity());
                            MyLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                            if (criticslistitems.size() > 0 & connectionsCriticsRecyerView != null) {
                                connectionsCriticsRecyerView.setAdapter(new ConnectionsAdapter(getActivity(),criticslistitems));
                            }
                            connectionsCriticsRecyerView.setLayoutManager(MyLayoutManager);

                            connectionsFriendsRecyerView.setHasFixedSize(true);
                            LinearLayoutManager MyLayoutManager2 = new LinearLayoutManager(getActivity());
                            MyLayoutManager2.setOrientation(LinearLayoutManager.HORIZONTAL);
                            if (friendslistitems.size() > 0 & connectionsFriendsRecyerView != null) {
                                connectionsFriendsRecyerView.setAdapter(new ConnectionsAdapter(getActivity(),friendslistitems));
                            }
                            connectionsFriendsRecyerView.setLayoutManager(MyLayoutManager2);

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

}
