package jaffa.com.jaffareviews.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.mingle.widget.LoadingView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import jaffa.com.jaffareviews.Adapters.LeaderBoardAdapter;
import jaffa.com.jaffareviews.Adapters.SearchMoviesAdapter;
import jaffa.com.jaffareviews.Adapters.UpComingMoviesAdapter;
import jaffa.com.jaffareviews.POJO.SearchResultCriticsPOJO;
import jaffa.com.jaffareviews.POJO.SearchResultMoviesPOJO;
import jaffa.com.jaffareviews.POJO.UpComingMoviesPOJO;
import jaffa.com.jaffareviews.POJO.leaderboard;
import jaffa.com.jaffareviews.R;
import jaffa.com.jaffareviews.Volley.VolleySingleton;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by gautham on 11/14/17.
 */

public class SearchFragment extends Fragment implements View.OnClickListener, SearchView.OnQueryTextListener {

    private SearchFragment.OnSearchFragmentListener mListener;
    private SearchView searchview;
    private RecyclerView searchMoviesRecyerView, searchCriticsRecyerView;
    private LinearLayout searchMoviesSection, searchCriticsSection;

    public static SearchFragment newInstance() {
        Bundle args = new Bundle();
        SearchFragment fragment = new SearchFragment();
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
        if (context instanceof SearchFragment.OnSearchFragmentListener) {
            mListener = (SearchFragment.OnSearchFragmentListener) context;
        }else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSearchFragmentListener");
        }

    }

    public interface OnSearchFragmentListener {
        void onSearchClick(String movieName, String movieID);
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_search, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        SharedPreferences prefs = getActivity().getSharedPreferences(getString(R.string.shared_pref_FbID), MODE_PRIVATE);
        searchview = (SearchView) rootView.findViewById(R.id.search);
        searchview.setOnQueryTextListener(this);
        searchMoviesSection = (LinearLayout) rootView.findViewById(R.id.search_movies_section);
        searchCriticsSection = (LinearLayout) rootView.findViewById(R.id.search_critics_section);
        searchMoviesRecyerView= (RecyclerView) rootView.findViewById(R.id.Search_Movies);
        searchCriticsRecyerView= (RecyclerView) rootView.findViewById(R.id.Search_Critics);

        searchview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchview.setIconified(false);
            }
        });


        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if(newText.length() > 2){
            SearchJaffa(newText);
        }
        return true;
    }

    public void SearchJaffa(String key){
        String url ="http://jaffareviews.com/api/Movie/SearchApplication?keyword="+key;

        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            ArrayList<SearchResultMoviesPOJO> listSearchResultMovies = new ArrayList<>();
                            ArrayList<leaderboard> listSearchResultCritics = new ArrayList<>();

                            JSONArray moviejsonArray = response.getJSONArray("movies");
                            for (int i = 0; i < moviejsonArray.length(); i++) {
                                SearchResultMoviesPOJO searchMovies = new SearchResultMoviesPOJO();
                                searchMovies.setMovieID(moviejsonArray.getJSONObject(i).optString("MovieID"));
                                searchMovies.setMovieName(moviejsonArray.getJSONObject(i).optString("MovieName"));
                                searchMovies.setAvgRating(moviejsonArray.getJSONObject(i).optString("AvgRating"));
                                searchMovies.setNumRating(moviejsonArray.getJSONObject(i).optString("NumOfRatings"));
                                searchMovies.setReleaseDate(moviejsonArray.getJSONObject(i).optString("ReleaseDate"));
                                searchMovies.setMovieImage(moviejsonArray.getJSONObject(i).optString("MovieImage"));
                                listSearchResultMovies.add(searchMovies);
                            }

                            JSONArray criticjsonArray = response.getJSONArray("critics");
                            for (int i = 0; i < criticjsonArray.length(); i++) {
                                leaderboard searchCritics = new leaderboard();
                                searchCritics.setName((criticjsonArray.getJSONObject(i).optString("FirstName")) + " " + (criticjsonArray.getJSONObject(i).optString("LastName")));
                                searchCritics.setFollowers((criticjsonArray.getJSONObject(i).optString("NumOfFollowers")));
                                searchCritics.setRatings((criticjsonArray.getJSONObject(i).optString("NumOfRatings")));
                                searchCritics.setFbid((criticjsonArray.getJSONObject(i).optString("fbID")));
                                searchCritics.setFollowing((criticjsonArray.getJSONObject(i).optBoolean("IsFollowing")));
                                listSearchResultCritics.add(searchCritics);
                            }

                            searchMoviesRecyerView.setHasFixedSize(true);
                            LinearLayoutManager MyLayoutManager = new LinearLayoutManager(getActivity());
                            MyLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                            if (listSearchResultMovies.size() > 0 & searchMoviesRecyerView != null) {
                                searchMoviesRecyerView.setAdapter(new SearchMoviesAdapter(getActivity(), listSearchResultMovies));
                            }
                            searchMoviesRecyerView.setLayoutManager(MyLayoutManager);

                            searchCriticsRecyerView.setHasFixedSize(true);
                            LinearLayoutManager MyLayoutManager2 = new LinearLayoutManager(getActivity());
                            MyLayoutManager2.setOrientation(LinearLayoutManager.HORIZONTAL);
                            if (listSearchResultCritics.size() > 0 & searchCriticsRecyerView != null) {
                                searchCriticsRecyerView.setAdapter(new LeaderBoardAdapter(getActivity(),listSearchResultCritics, MainGridFragment.newInstance()));
                            }
                            searchCriticsRecyerView.setLayoutManager(MyLayoutManager2);

                            if(moviejsonArray.length() > 0 && criticjsonArray.length() > 0){
                                searchMoviesSection.setVisibility(View.VISIBLE);
                                searchCriticsSection.setVisibility(View.VISIBLE);
                            }else if (moviejsonArray.length() > 0){
                                searchMoviesSection.setVisibility(View.VISIBLE);
                                searchCriticsSection.setVisibility(View.GONE);
                            }else if(criticjsonArray.length() > 0){
                                searchMoviesSection.setVisibility(View.GONE);
                                searchCriticsSection.setVisibility(View.VISIBLE);
                            }else{
                                searchMoviesSection.setVisibility(View.GONE);
                                searchCriticsSection.setVisibility(View.GONE);
                            }

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
