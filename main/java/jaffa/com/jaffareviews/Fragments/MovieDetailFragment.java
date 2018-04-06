package jaffa.com.jaffareviews.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.mingle.widget.LoadingView;
import com.twitter.sdk.android.tweetui.BasicTimelineFilter;
import com.twitter.sdk.android.tweetui.FilterValues;
import com.twitter.sdk.android.tweetui.SearchTimeline;
import com.twitter.sdk.android.tweetui.TimelineFilter;
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import jaffa.com.jaffareviews.Adapters.CriticReviewsAdapter;
import jaffa.com.jaffareviews.Adapters.FriendReviewsAdapter;
import jaffa.com.jaffareviews.Adapters.OthersReviewsAdapter;
import jaffa.com.jaffareviews.Helpers.ExpandedListView;
import jaffa.com.jaffareviews.Helpers.ImageHelper;
import jaffa.com.jaffareviews.Helpers.NonScrollListView;
import jaffa.com.jaffareviews.POJO.MoviedetailReviewsPOJO;
import jaffa.com.jaffareviews.R;
import jaffa.com.jaffareviews.Volley.VolleySingleton;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by gautham on 11/6/17.
 */

public class MovieDetailFragment extends ListFragment implements View.OnClickListener {

    private static final String movie_NAME = "prop_name";
    private String movieName, movieDirector, movieRating, movieReleaseDate, movieMusicDirector, movieImage, movieID, numberofreviews, movieCoverPic, movieCast;
    private OnMovieDetailFragmentListener mListener;
    TextView movie_name, movie_director, movie_rating, movie_releasedate, movie_musicdirector, movie_title, numof_reiews, movie_cast;
    ImageView movie_img, movie_cover;
    private String friendsIDs = "friendsIDs";
    String frindsIDs = "";
    NonScrollListView frndrevlistView, criticrevlistview;
    static FriendReviewsAdapter frndsrevadapter;
    static CriticReviewsAdapter criticrevadapter;
    LoadingView detailProgress;
    View view;
    String restoreduserid;
    Button addRating ;
    boolean hasMyrating;
    LinearLayout backbutton;
    //RotationRatingBar rotationRatingBar;

//    final List<String> handles = Arrays.asList("ericfrohnhoefer", "benward", "vam_si");
//    final FilterValues filterValues = new FilterValues(null, null, handles, null); // or load from JSON, XML, etc
//    final TimelineFilter timelineFilter = new BasicTimelineFilter(filterValues, Locale.ENGLISH);


    public static MovieDetailFragment newInstance(String title, String movieID) {
        Bundle args = new Bundle();
        args.putString(movie_NAME, title);
        args.putString("MovieID", movieID);
        MovieDetailFragment fragment = new MovieDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        SharedPreferences prefs2 = getActivity().getSharedPreferences(friendsIDs, MODE_PRIVATE);
        frindsIDs = prefs2.getString(friendsIDs, null);

        SharedPreferences prefs = getActivity().getSharedPreferences(getString(R.string.shared_pref_FbID), MODE_PRIVATE);
        restoreduserid = prefs.getString(getString(R.string.shared_pref_FbID), null);

        if (getArguments() != null) {
            movieName = getArguments().getString(movie_NAME);
            movieID = getArguments().getString("MovieID");
        }
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

//        final SearchTimeline searchTimeline = new SearchTimeline.Builder()
//                .query("#" + movieName.replaceAll("\\s+", ""))
//                .resultType(SearchTimeline.ResultType.POPULAR)
//                .languageCode(Locale.ENGLISH.getLanguage())
//                .build();
//        final TweetTimelineListAdapter adapter = new TweetTimelineListAdapter.Builder(getActivity())
//                .setTimeline(searchTimeline)
//                .build();
//        setListAdapter(adapter);


    }

    @Override
    public void onResume() {
        super.onResume();
//         ((MainActivity) getActivity()).getToolbar().hideOverflowMenu();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMovieDetailFragmentListener) {
            mListener = (OnMovieDetailFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnCheeseDetailFragmentListener");
        }
    }

    public interface OnMovieDetailFragmentListener {
        void onReviewClick(String fbID);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.movie_details_back_button:
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.popBackStack();
                break;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.movie_details_view, container, false);
        detailProgress = (LoadingView) view.findViewById(R.id.detail_progress);
        movie_img = (ImageView) view.findViewById(R.id.detail_thumbnail);
        movie_cover = (ImageView) view.findViewById(R.id.detail_cover_pic);
        movie_title = (TextView) view.findViewById(R.id.movie_title_label);
        movie_director = (TextView) view.findViewById(R.id.movie_detail_director);
        movie_rating = (TextView) view.findViewById(R.id.movie_detail_rating);
        numof_reiews = (TextView) view.findViewById(R.id.numof_reiews);
        movie_releasedate = (TextView) view.findViewById(R.id.movie_detail_releasedate);
        movie_musicdirector = (TextView) view.findViewById(R.id.movie_detail_mdirector);
        movie_cast = (TextView) view.findViewById(R.id.movie_detail_cast);
        frndrevlistView = (NonScrollListView) view.findViewById(R.id.friends_review_list);
        criticrevlistview = (NonScrollListView) view.findViewById(R.id.critic_review_list);
        addRating = (Button) view.findViewById(R.id.add_rating_button);
        backbutton = (LinearLayout) view.findViewById(R.id.movie_details_back_button);
        frndrevlistView.setFocusable(false);
        criticrevlistview.setFocusable(false);
        backbutton.setOnClickListener(this);

        addRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.mainlist_fragment, AddRatingFragment.newInstance(movieID, hasMyrating));
                ft.addToBackStack(null);
                ft.commit();
            }
        });
        moviedetailsvolley();

        return view;
    }

    public void moviedetailsvolley() {

        String url = "http://jaffareviews.com/api/Movie/GetMovie?movieName=" + movieName + "&fbIds=" + frindsIDs + "&UserFbID=" + restoreduserid;

        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.print(response);
                        try {

                            ArrayList<MoviedetailReviewsPOJO> listUserReviewItems_Friends = new ArrayList<>();
                            ArrayList<MoviedetailReviewsPOJO> listUserReviewItems_Critics = new ArrayList<>();

                            if (response.has("Movie")) {
                                JSONObject responseObject = response.getJSONObject("Movie");

                                movieID = responseObject.optString("MovieID");
                                movieName = responseObject.optString("MovieName");
                                movieDirector = responseObject.optString("Director");
                                movieRating = responseObject.optString("AvgRating");
                                movieReleaseDate = responseObject.optString("ReleaseDate");
                                movieMusicDirector = responseObject.optString("MusicDirector");
                                movieImage = responseObject.optString("MovieImage");
                                movieCoverPic = responseObject.optString("WideImage");
                                numberofreviews = responseObject.optString("NumOfRatings")+ " Reviews";
                                movieCast = responseObject.optString("Cast");
                                hasMyrating = responseObject.optBoolean("HasMyRating");

                                if (responseObject.has("Reviews")) {
                                    JSONArray jsonArray = responseObject.getJSONArray("Reviews");

                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        if (jsonArray.getJSONObject(i).optString("IsFriend").equalsIgnoreCase("true")) {
                                            MoviedetailReviewsPOJO  friendReviews = new MoviedetailReviewsPOJO();
                                            friendReviews.setUserFBId(jsonArray.getJSONObject(i).optString("FbID"));
                                            friendReviews.setUserRating(jsonArray.getJSONObject(i).optString("MovieRating"));
                                            friendReviews.setUserReviewText(jsonArray.getJSONObject(i).optString("MovieReview"));
                                            friendReviews.setUserName(jsonArray.getJSONObject(i).optString("ReviewerName"));
                                            friendReviews.setUserReviewDate(jsonArray.getJSONObject(i).optString("ReviewDate"));
                                            friendReviews.setUserReviewGif(jsonArray.getJSONObject(i).optString("GifURL"));
                                            friendReviews.setUserReviewTag(jsonArray.getJSONObject(i).optString("UserTags"));
                                            listUserReviewItems_Friends.add(friendReviews);
                                        } else {
                                            MoviedetailReviewsPOJO  criticReviews = new MoviedetailReviewsPOJO();
                                            criticReviews.setUserFBId(jsonArray.getJSONObject(i).optString("FbID"));
                                            criticReviews.setUserRating(jsonArray.getJSONObject(i).optString("MovieRating"));
                                            criticReviews.setUserReviewText(jsonArray.getJSONObject(i).optString("MovieReview"));
                                            criticReviews.setUserName(jsonArray.getJSONObject(i).optString("ReviewerName"));
                                            criticReviews.setUserReviewDate(jsonArray.getJSONObject(i).optString("ReviewDate"));
                                            criticReviews.setUserReviewGif(jsonArray.getJSONObject(i).optString("GifURL"));
                                            criticReviews.setUserReviewTag(jsonArray.getJSONObject(i).optString("UserTags"));
                                            listUserReviewItems_Critics.add(criticReviews);
                                        }
                                    }
                                }

                                if (listUserReviewItems_Friends.size() > 0) {
                                    frndsrevadapter = new FriendReviewsAdapter(getActivity(), listUserReviewItems_Friends);
                                    frndrevlistView.setAdapter(frndsrevadapter);
                                    new Task().execute();

                                }
                                if (listUserReviewItems_Critics.size() > 0) {
                                    criticrevadapter = new CriticReviewsAdapter(getActivity(), listUserReviewItems_Critics);
                                    criticrevlistview.setAdapter(criticrevadapter);

                                }
//                                if (listRevother_fbId.size() > 0) {
//                                    othersrevadapter = new OthersReviewsAdapter(getActivity(), listRevother_fbId, listRevother_rating, listRevother_revtext, listRevother_revname, restoreduserid);
//                                    otherlistview.setAdapter(othersrevadapter);
//
//                                    otherlistview.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
//
//                                        @Override
//                                        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
//                                            otherlistview.removeOnLayoutChangeListener(this);
//                                            //detailProgress.setVisibility(View.GONE);
//                                        }
//                                    });
//
//                                    othersrevadapter.notifyDataSetChanged();
//
//                                }
                                setMovieValues();
                                setCoverPic(movieCoverPic);
                                setImage(movieImage);
//                                setProfilePic(movieImage);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            detailProgress.setVisibility(View.GONE);
                            Toast.makeText(getActivity(), "Something went wrong!",
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        detailProgress.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), "Something went wrong!",
                                Toast.LENGTH_LONG).show();
                    }
                });

        VolleySingleton.getInstance().addToRequestQueue(jsonRequest);
    }

    public void setImage(String url) {
        final Handler handler = new Handler();
        ImageRequest imgRequest = new ImageRequest(url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {

                        Bitmap mbitmap = response;
                        Bitmap imageRounded = Bitmap.createBitmap(mbitmap.getWidth(), mbitmap.getHeight(), mbitmap.getConfig());
                        Canvas canvas = new Canvas(imageRounded);
                        Paint mpaint = new Paint();
                        mpaint.setAntiAlias(true);
                        mpaint.setShader(new BitmapShader(mbitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
                        canvas.drawRoundRect((new RectF(0, 0, mbitmap.getWidth(), mbitmap.getHeight())), 100, 100, mpaint);// Round Image Corner 100 100 100 100
                        movie_img.setImageBitmap(imageRounded);

//                        movie_img.setImageBitmap(response);
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                detailProgress.setVisibility(View.GONE);
                            }
                        }, 1000);
                    }
                }, 0, 0, ImageView.ScaleType.FIT_XY, Bitmap.Config.ARGB_8888, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                movie_img.setBackgroundColor(Color.parseColor("#ff0000"));
                handler.postDelayed(new Runnable() {
                    public void run() {
                        detailProgress.setVisibility(View.GONE);
                    }
                }, 500);
                error.printStackTrace();

            }
        });
        VolleySingleton.getInstance().addToRequestQueue(imgRequest);
    }

    public void setMovieValues() {
        movie_title.setText(movieName);
        movie_releasedate.setText(setDate(movieReleaseDate));
        movie_director.setText(movieDirector);
        movie_musicdirector.setText(movieMusicDirector);
        movie_rating.setText(movieRating + " %");
        numof_reiews.setText(numberofreviews);
        movie_cast.setText(movieCast);
        if(hasMyrating)
            addRating.setText("Edit Rating");
    }


    public static String setDate(String date) {

        Calendar cal = Calendar.getInstance();
        try {
            DateFormat df = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
            Date da = df.parse(date);
            cal.setTime(da);
        }catch(ParseException e){}
        return cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH)+" "+ cal.get(Calendar.DATE)+" "+cal.get(Calendar.YEAR);


    }

    public void AddReview(final String text, final int movieID, final Long FbID, final float rating) {

        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("MovieID", movieID);
            jsonObject.put("Rating", rating);
            jsonObject.put("Review", text);
            jsonObject.put("FbID", FbID);
            jsonArray.put(jsonObject);

        } catch (Exception e) {

        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, "http://jaffareviews.com/api/movie/AddRating", jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            Boolean arrData = response.getBoolean("Data");
                            if (!arrData) {
                                moviedetailsvolley();
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

    class Task extends AsyncTask<String, Integer, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean result) {

            super.onPostExecute(result);
        }

        @Override
        protected Boolean doInBackground(String... params) {
            return null;
        }
    }

    public void setCoverPic(String url){
        final Handler handler = new Handler();
        ImageRequest imgRequest = new ImageRequest(url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        movie_cover.setImageBitmap(response);
                        handler.postDelayed(new Runnable() {
                            public void run() {
                            }
                        }, 1000);
                    }
                }, 0, 0, ImageView.ScaleType.FIT_XY, Bitmap.Config.ARGB_8888, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                movie_cover.setBackgroundColor(Color.parseColor("#ff0000"));
                handler.postDelayed(new Runnable() {
                    public void run() {
                    }
                }, 500);
                error.printStackTrace();

            }
        });
        VolleySingleton.getInstance().addToRequestQueue(imgRequest);
    }

    public void setProfilePic(String url){
        ImageRequest imgRequest = new ImageRequest(url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        movie_img.setImageBitmap(ImageHelper.getRoundedCornerBitmap(getActivity(), response, 150, 342, 513, true, true, true, true));
                        detailProgress.setVisibility(View.GONE);

                    }
                }, 0, 0, ImageView.ScaleType.FIT_CENTER, Bitmap.Config.ARGB_8888, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                movie_img.setBackgroundColor(Color.parseColor("#ff0000"));
                detailProgress.setVisibility(View.GONE);
                error.printStackTrace();
            }
        });
        VolleySingleton.getInstance().addToRequestQueue(imgRequest);
    }

}
