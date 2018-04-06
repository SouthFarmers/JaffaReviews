package jaffa.com.jaffareviews.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import jaffa.com.jaffareviews.Adapters.ConnectionsAdapter;
import jaffa.com.jaffareviews.Adapters.ReviewsByUserAdapter;
import jaffa.com.jaffareviews.Adapters.SearchMoviesAdapter;
import jaffa.com.jaffareviews.Helpers.ExpandedListView;
import jaffa.com.jaffareviews.MainActivity;
import jaffa.com.jaffareviews.POJO.ReviewsByUserPOJO;
import jaffa.com.jaffareviews.POJO.leaderboard;
import jaffa.com.jaffareviews.R;
import jaffa.com.jaffareviews.Volley.VolleySingleton;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by gautham on 11/22/17.
 */

public class ReviewsByUserFragment extends Fragment {

    private ReviewsByUserFragment.OnReviewsByUserFragmentListener mListener;
    private static final String fb_ID = "fb_ID";
    private String userFbID = "";
    private ImageView reviewByUserImage;
    private TextView reviewByUserName, reviewByUserStatus, reviewByUserSince, reviewByUserFollowers, reviewByUserReviews, reviewByUserToolbarName;
    private ImageButton reviewByUserFollowButton, reviewByUserBackButton;
    private RecyclerView reviewByUserList;
    private String restoreduserid;
    private boolean isFollowing = false;

    public static ReviewsByUserFragment newInstance(String fbID) {
        Bundle args = new Bundle();
        args.putString(fb_ID, fbID);
        ReviewsByUserFragment fragment = new ReviewsByUserFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            userFbID = getArguments().getString(fb_ID);
        }
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
        if (context instanceof ReviewsByUserFragment.OnReviewsByUserFragmentListener) {
            mListener = (ReviewsByUserFragment.OnReviewsByUserFragmentListener) context;
        }else {
            throw new RuntimeException(context.toString()
                    + " must implement OnReviewsByUserFragmentListener");
        }

    }

    public interface OnReviewsByUserFragmentListener {
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        SharedPreferences prefs = getActivity().getSharedPreferences(getString(R.string.shared_pref_FbID), MODE_PRIVATE);
        restoreduserid = prefs.getString(getString(R.string.shared_pref_FbID), null);
        View rootView = inflater.inflate(R.layout.fragment_review_by_user, container, false);
        reviewByUserToolbarName =  (TextView) rootView.findViewById(R.id.review_by_user_toolbar_name);
        reviewByUserBackButton = (ImageButton) rootView.findViewById(R.id.review_by_user_toolbar_back);
        reviewByUserImage = (ImageView) rootView.findViewById(R.id.review_by_user_image);
        reviewByUserName = (TextView) rootView.findViewById(R.id.review_by_user_name);
        reviewByUserStatus = (TextView) rootView.findViewById(R.id.review_by_user_status);
        reviewByUserSince = (TextView) rootView.findViewById(R.id.review_by_user_since);
        reviewByUserFollowers = (TextView) rootView.findViewById(R.id.review_by_user_follower_count);
        reviewByUserReviews = (TextView) rootView.findViewById(R.id.review_by_user_reviewcount);
        reviewByUserFollowButton = (ImageButton) rootView.findViewById(R.id.review_by_user_followbutton);
        reviewByUserList = (RecyclerView) rootView.findViewById(R.id.review_by_user_list);

        reviewByUserFollowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FollowUser();
            }
        });

        reviewByUserBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getCriticsConnections();
    }

    public void onBackPressed()
    {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        fm.popBackStack();
    }

    public void getCriticsConnections(){
        String url ="http://jaffareviews.com/api/Movie/GetReviewsByUser?UserFbID="+userFbID+ "&FollowerFbID="+restoreduserid;

        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            reviewByUserToolbarName.setText(response.getJSONObject("UserDetails").optString("FirstName"));
                            reviewByUserName.setText(response.getJSONObject("UserDetails").optString("FirstName")+ " " +response.getJSONObject("UserDetails").optString("LastName"));
                            reviewByUserSince.setText("Member since "+setDate(response.getJSONObject("UserDetails").optString("MemberSince")));
                            reviewByUserReviews.setText(response.getJSONObject("UserDetails").optString("ReviewsCount")+" Reviews");
                            if(response.getJSONObject("UserDetails").optBoolean("IsCritic")){
                                reviewByUserStatus.setText("Movie Critic");
                            }else{
                                reviewByUserStatus.setText("User");
                            }
                            reviewByUserFollowers.setText(response.getJSONObject("UserDetails").optString("FollowersCount"));
                            if(response.getJSONObject("UserDetails").optBoolean("IsFollowing")){
                                reviewByUserFollowButton.setBackgroundResource(R.drawable.unfollow);
                                isFollowing = true;
                            }else{
                                reviewByUserFollowButton.setBackgroundResource(R.drawable.follow);
                                isFollowing = false;
                            }

                            setUserPic("https://graph.facebook.com/" + userFbID + "/picture?width=400&height=400");

                            ArrayList<ReviewsByUserPOJO> listUserReviewItems = new ArrayList<>();

                            JSONArray jsonArray = response.getJSONArray("Reviews");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                ReviewsByUserPOJO searchMovies = new ReviewsByUserPOJO();
                                searchMovies.setMovieName(jsonArray.getJSONObject(i).optString("MovieName"));
                                searchMovies.setMovieRating(jsonArray.getJSONObject(i).optString("RatingStars"));
                                searchMovies.setMovieTag(jsonArray.getJSONObject(i).optString("UserTags"));
                                searchMovies.setRMovieGif(jsonArray.getJSONObject(i).optString("GifURL"));
                                searchMovies.setReview(jsonArray.getJSONObject(i).optString("Review"));
                                listUserReviewItems.add(searchMovies);
                            }

                            reviewByUserList.setHasFixedSize(true);
                            LinearLayoutManager MyLayoutManager = new LinearLayoutManager(getActivity());
                            MyLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                            if (listUserReviewItems.size() > 0 & reviewByUserList != null) {
                                reviewByUserList.setAdapter(new ReviewsByUserAdapter(getContext(), listUserReviewItems));
                            }
                            reviewByUserList.setLayoutManager(MyLayoutManager);


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


    public void setUserPic(String url){
        final Handler handler = new Handler();
        ImageRequest imgRequest = new ImageRequest(url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        reviewByUserImage.setImageBitmap(response);
                        handler.postDelayed(new Runnable() {
                            public void run() {
                            }
                        }, 1000);
                    }
                }, 0, 0, ImageView.ScaleType.FIT_XY, Bitmap.Config.ARGB_8888, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                reviewByUserImage.setBackgroundColor(Color.parseColor("#ff0000"));
                handler.postDelayed(new Runnable() {
                    public void run() {
                    }
                }, 500);
                error.printStackTrace();

            }
        });
        VolleySingleton.getInstance().addToRequestQueue(imgRequest);
    }

    public static String setDate(String date) {

        Calendar mydate = new GregorianCalendar();
        Date thedate = null;
        try {
            thedate = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH).parse(date);
        } catch (ParseException e) {e.printStackTrace();}
        mydate.setTime(thedate);
        SimpleDateFormat formatter = new SimpleDateFormat("MMM YYYY ");
        return formatter.format(mydate.getTime());
    }

    public void FollowUser(){

        String url = "";
        if(isFollowing){
            url = "http://jaffareviews.com/api/movie/UnFollowCritic";
            isFollowing = false;
            reviewByUserFollowButton.setBackgroundResource(R.drawable.follow);
        }else{
            url = "http://jaffareviews.com/api/movie/FollowCritic";
            isFollowing = true;
            reviewByUserFollowButton.setBackgroundResource(R.drawable.unfollow);
        }
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();

        try{
            jsonObject.put("CriticFbID",userFbID);
            jsonObject.put("UserFbID",restoreduserid);
            jsonArray.put(jsonObject);
        }catch(Exception e){}

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Boolean arrData = response.getBoolean("Data");
                            if (arrData) {

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error.Response", error.toString());
                        Toast.makeText(getContext(), "Something went wrong!",
                                Toast.LENGTH_LONG).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonObjectRequest);
    }
}