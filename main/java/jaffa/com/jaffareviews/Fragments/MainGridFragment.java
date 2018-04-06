package jaffa.com.jaffareviews.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.cleveroad.pulltorefresh.firework.FireworkyPullToRefreshLayout;
import com.mingle.widget.LoadingView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import jaffa.com.jaffareviews.Adapters.GridViewAdapter;
import jaffa.com.jaffareviews.Adapters.LeaderBoardAdapter;
import jaffa.com.jaffareviews.Adapters.UpComingMoviesAdapter;
import jaffa.com.jaffareviews.LeaderBoardCards.CardFragmentPagerAdapter;
import jaffa.com.jaffareviews.LeaderBoardCards.ShadowTransformer;
import jaffa.com.jaffareviews.POJO.MovieDetailPOJO;
import jaffa.com.jaffareviews.POJO.UpComingMoviesPOJO;
import jaffa.com.jaffareviews.POJO.leaderboard;
import jaffa.com.jaffareviews.R;
import jaffa.com.jaffareviews.Volley.VolleySingleton;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by gautham on 11/3/17.
 */

public class MainGridFragment extends Fragment implements View.OnClickListener {


    private OnMainGridFragmentListener mListener;
    private static final int REFRESH_DELAY = 4500;
    private boolean mIsRefreshing;
    static GridViewAdapter mainadapter;
    static RecyclerView recyclerView;
    RecyclerView MyRecyclerView, upcomingRecyclerview;
    LoadingView progress;
    FireworkyPullToRefreshLayout mPullToRefresh;
    private static List<String> listMovieTitle, listMovieRating, listMovieImage,listMovieFriendRatingCount,listMovieCriticRatingCount;
    String restoreduserid;


    public static MainGridFragment newInstance() {
        Bundle args = new Bundle();
        MainGridFragment fragment = new MainGridFragment();
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
        if (context instanceof OnMainGridFragmentListener) {
            mListener = (OnMainGridFragmentListener) context;
        }else {
            throw new RuntimeException(context.toString()
                    + " must implement OnCheeseCategoriesFragmentListener");
        }

    }

    public interface OnMainGridFragmentListener {
        void onMovieClick(String title, String movieid);
        void onLeaderBoadClick(String FbID);
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.main_grid_fragment, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        SharedPreferences prefs = getActivity().getSharedPreferences(getString(R.string.shared_pref_FbID), MODE_PRIVATE);
        restoreduserid = prefs.getString(getString(R.string.shared_pref_FbID), null);
        progress = (LoadingView) rootView.findViewById(R.id.main_progress);
        mPullToRefresh = (FireworkyPullToRefreshLayout) rootView.findViewById(R.id.pullToRefresh);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.main_list);
        MyRecyclerView = (RecyclerView) rootView.findViewById(R.id.leaderboardcardView);
        upcomingRecyclerview = (RecyclerView) rootView.findViewById(R.id.upcomingcardView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(mLayoutManager);

        allMoviesvolley();
        loadLeaderboard();
        getUpcomingMovies();
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initRefreshView();
        mPullToRefresh.post(new Runnable() {
            @Override
            public void run() {
                mPullToRefresh.setRefreshing(mIsRefreshing);
            }
        });
    }

    public void allMoviesvolley(){
        String url ="http://jaffareviews.com/api/Movie/GetNewReleases?UserfbID="+restoreduserid;

        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            listMovieTitle = new ArrayList<String>();
                            listMovieRating = new ArrayList<String>();
                            listMovieImage = new ArrayList<String>();
                            listMovieFriendRatingCount = new ArrayList<String>();
                            listMovieCriticRatingCount = new ArrayList<String>();
                            ArrayList<MovieDetailPOJO> MovieDetailslistitems = new ArrayList<>();


                            if (response.has("movies")) {
                                JSONArray jsonArray = response.getJSONArray("movies");

                                for (int i = 0; i < jsonArray.length(); i++) {

                                    if (jsonArray.getJSONObject(i).has("MovieName")) {
                                        MovieDetailPOJO moviedetails = new MovieDetailPOJO();
                                        moviedetails.setMovieTitle(jsonArray.getJSONObject(i).optString("MovieName"));
                                        moviedetails.setMovieID(jsonArray.getJSONObject(i).optString("MovieID"));
                                        moviedetails.setAvgRating(jsonArray.getJSONObject(i).optString("AvgRating"));
                                        moviedetails.setMovieImage(jsonArray.getJSONObject(i).optString("MovieImage"));
                                        moviedetails.setFriendRatingCount(jsonArray.getJSONObject(i).optString("NumFriendRatings"));
                                        moviedetails.setCriticRatingCount(jsonArray.getJSONObject(i).optString("NumCriticRatings"));
                                        MovieDetailslistitems.add(moviedetails);
                                    }
                                }
                            }
                            mainadapter = new GridViewAdapter(getActivity(), MovieDetailslistitems);
                            recyclerView.setAdapter(mainadapter);

                            recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {

                                @Override
                                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                                    recyclerView.removeOnLayoutChangeListener(this);
                                    final Handler handler = new Handler();
                                    progress.setVisibility(View.GONE);
                                }
                            });

                            mainadapter.notifyDataSetChanged();
                            mPullToRefresh.setRefreshing(mIsRefreshing = false);

                        } catch (JSONException e) {

                            e.printStackTrace();
                            progress.setVisibility(View.GONE);
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        progress.setVisibility(View.GONE);
                    }
                });

        VolleySingleton.getInstance().addToRequestQueue(jsonRequest);
    }


    private void initRefreshView() {
        mPullToRefresh.setOnRefreshListener(new FireworkyPullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mIsRefreshing = true;
                allMoviesvolley();
            }
        });
    }

    public void loadLeaderboard(){
        String url ="http://jaffareviews.com/api/Movie/GetTopCritics?UserFbID="+restoreduserid;

        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            ArrayList<leaderboard> listitems = new ArrayList<>();

                            JSONArray jsonArray = response.getJSONArray("critics");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                String ds = jsonArray.getJSONObject(i).optString("fbID");
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

                            MyRecyclerView.setHasFixedSize(true);
                            LinearLayoutManager MyLayoutManager = new LinearLayoutManager(getActivity());
                            MyLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                            if (listitems.size() > 0 & MyRecyclerView != null) {
                                MyRecyclerView.setAdapter(new LeaderBoardAdapter(getActivity(),listitems, MainGridFragment.this));
                            }
                            MyRecyclerView.setLayoutManager(MyLayoutManager);

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

    public void getUpcomingMovies(){

        String url ="http://jaffareviews.com/api/Movie/GetUpcomingMovies";

        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            ArrayList<UpComingMoviesPOJO> listUpComingMovies = new ArrayList<>();

                            JSONArray jsonArray = response.getJSONArray("movies");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                UpComingMoviesPOJO upComingMovies = new UpComingMoviesPOJO();
                                upComingMovies.setMovieName(jsonArray.getJSONObject(i).optString("MovieName"));
                                upComingMovies.setMovieReleaseDate(jsonArray.getJSONObject(i).optString("ReleaseDate"));
                                upComingMovies.setMovieImage(jsonArray.getJSONObject(i).optString("MovieImage"));
                                listUpComingMovies.add(upComingMovies);
                            }

                            upcomingRecyclerview.setHasFixedSize(true);
                            LinearLayoutManager MyLayoutManager = new LinearLayoutManager(getActivity());
                            MyLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                            if (listUpComingMovies.size() > 0 & upcomingRecyclerview != null) {
                                upcomingRecyclerview.setAdapter(new UpComingMoviesAdapter(listUpComingMovies));
                            }
                            upcomingRecyclerview.setLayoutManager(MyLayoutManager);


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
