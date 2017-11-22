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
import android.support.v4.app.ListFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import jaffa.com.jaffareviews.R;
import jaffa.com.jaffareviews.Volley.VolleySingleton;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by gautham on 11/6/17.
 */

public class MovieDetailFragment extends ListFragment implements View.OnClickListener {

    private static final String movie_NAME = "prop_name";
    private String movieName, movieDirector, movieRating, movieReleaseDate, movieMusicDirector, movieImage, movieID, numberofreviews, movieCoverPic;
    private OnMovieDetailFragmentListener mListener;
    TextView movie_name, movie_director, movie_rating, movie_releasedate, movie_musicdirector, movie_title, numof_reiews;
    ImageView movie_img, movie_cover;
    private String friendsIDs = "friendsIDs";
    String frindsIDs = "";
    private static List<String> listRevfrnd_fbId, listRevfrnd_rating, listRevfrnd_revtext, listRevfrnd_revname,
            listRevcritic_fbId, listRevcritic_rating, listRevcritic_revtext, listRevcritic_revname,
            listRevother_fbId, listRevother_rating, listRevother_revtext, listRevother_revname;
    ExpandedListView frndrevlistView, criticrevlistview, otherlistview;
    static FriendReviewsAdapter frndsrevadapter;
    static CriticReviewsAdapter criticrevadapter;
    static OthersReviewsAdapter othersrevadapter;
    LoadingView detailProgress;
    View view;
    String restoreduserid;
    //RotationRatingBar rotationRatingBar;

    final List<String> handles = Arrays.asList("ericfrohnhoefer", "benward", "vam_si");
    final FilterValues filterValues = new FilterValues(null, null, handles, null); // or load from JSON, XML, etc
    final TimelineFilter timelineFilter = new BasicTimelineFilter(filterValues, Locale.ENGLISH);

    public MovieDetailFragment() {
    }

    public static MovieDetailFragment newInstance(String title) {
        Bundle args = new Bundle();
        args.putString(movie_NAME, title);
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
        }
        moviedetailsvolley();
        // ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        final SearchTimeline searchTimeline = new SearchTimeline.Builder()
                .query("#" + movieName.replaceAll("\\s+", ""))
                .resultType(SearchTimeline.ResultType.POPULAR)
                .languageCode(Locale.ENGLISH.getLanguage())
                .build();
        final TweetTimelineListAdapter adapter = new TweetTimelineListAdapter.Builder(getActivity())
                .setTimeline(searchTimeline)
                .build();
        setListAdapter(adapter);


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
        frndrevlistView = (ExpandedListView) view.findViewById(R.id.friends_review_list);
        criticrevlistview = (ExpandedListView) view.findViewById(R.id.critic_review_list);
        otherlistview = (ExpandedListView) view.findViewById(R.id.others_review_list);




        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

//    @Override
//    public void onCreateOptionsMenu(
//            Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.menu_add_review, menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // handle item selection
//        switch (item.getItemId()) {
//            case R.id.add_review:
//                LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getActivity());
//                View mView = layoutInflaterAndroid.inflate(R.layout.add_review, null);
//                AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(getActivity());
//                alertDialogBuilderUserInput.setView(mView);
//                final EditText addreviewText = (EditText) mView.findViewById(R.id.add_review_text);
//                final Resources resources = getResources();
//                rotationRatingBar = (RotationRatingBar) mView.findViewById(R.id.rotationratingbar_main);
//                rotationRatingBar.setStarPadding(20);
//
//                alertDialogBuilderUserInput
//                        .setCancelable(false)
//                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialogBox, int id) {
//                                // ToDo get user input here
//                                AddReview(addreviewText.getText().toString(), Integer.parseInt(movieID), Long.parseLong(restoreduserid), rotationRatingBar.getRating());
//                            }
//                        })
//
//                        .setNegativeButton("Cancel",
//                                new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialogBox, int id) {
//                                        dialogBox.cancel();
//                                    }
//                                });
//
//                AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
//                alertDialogAndroid.show();
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }

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

    @Override
    public void onResume() {
        super.onResume();
//         ((MainActivity) getActivity()).getToolbar().hideOverflowMenu();

    }

    public interface OnMovieDetailFragmentListener {

    }

    public void moviedetailsvolley() {

        String url = "http://jaffareviews.com/api/Movie/GetMovie?movieName=" + movieName + "&fbIds=" + frindsIDs + "&UserFbID=" + restoreduserid;
//        String url = "http://jaffareviews.com/api/Movie/GetMovie?movieName=fidaa&fbIds=715741731&UserFbID=107886109817802";
        //String url = "http://jaffareviews.com/api/Movie/GetMovie?movieName=manam&fbIds="+frindsIDs+"&UserFbID="+restoreduserid;

        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.print(response);
                        // the response is already constructed as a JSONObject!
                        try {
                            listRevfrnd_fbId = new ArrayList<String>();
                            listRevfrnd_rating = new ArrayList<String>();
                            listRevfrnd_revtext = new ArrayList<String>();
                            listRevfrnd_revname = new ArrayList<String>();

                            listRevcritic_fbId = new ArrayList<String>();
                            listRevcritic_rating = new ArrayList<String>();
                            listRevcritic_revtext = new ArrayList<String>();
                            listRevcritic_revname = new ArrayList<String>();

                            listRevother_fbId = new ArrayList<String>();
                            listRevother_rating = new ArrayList<String>();
                            listRevother_revtext = new ArrayList<String>();
                            listRevother_revname = new ArrayList<String>();

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

                                if (responseObject.has("Reviews")) {
                                    JSONArray jsonArray = responseObject.getJSONArray("Reviews");
                                    numberofreviews = jsonArray.length() + " Reviews";

                                    int j = 0, k = 0, l = 0;
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        if (jsonArray.getJSONObject(i).optString("IsFriend").equalsIgnoreCase("true")) {
                                            listRevfrnd_fbId.add(j, (jsonArray.getJSONObject(i).optString("FbID")));
                                            listRevfrnd_rating.add(j, (jsonArray.getJSONObject(i).optString("MovieRating")));
                                            listRevfrnd_revtext.add(j, (jsonArray.getJSONObject(i).optString("MovieReview")));
                                            listRevfrnd_revname.add(j, (jsonArray.getJSONObject(i).optString("ReviewerName")));
                                            j++;
                                        } else if (jsonArray.getJSONObject(i).optString("IsFollower").equalsIgnoreCase("true") && jsonArray.getJSONObject(i).optString("IsCritic").equalsIgnoreCase("true")) {
                                            listRevcritic_fbId.add(k, (jsonArray.getJSONObject(i).optString("FbID")));
                                            listRevcritic_rating.add(k, (jsonArray.getJSONObject(i).optString("MovieRating")));
                                            listRevcritic_revtext.add(k, (jsonArray.getJSONObject(i).optString("MovieReview")));
                                            listRevcritic_revname.add(k, (jsonArray.getJSONObject(i).optString("ReviewerName")));
                                            k++;
                                        } else if (jsonArray.getJSONObject(i).optString("IsCritic").equalsIgnoreCase("true")) {
                                            listRevother_fbId.add(l, (jsonArray.getJSONObject(i).optString("FbID")));
                                            listRevother_rating.add(l, (jsonArray.getJSONObject(i).optString("MovieRating")));
                                            listRevother_revtext.add(l, (jsonArray.getJSONObject(i).optString("MovieReview")));
                                            listRevother_revname.add(l, (jsonArray.getJSONObject(i).optString("ReviewerName")));
                                            l++;
                                        }
                                    }
                                }

                                if (listRevfrnd_fbId.size() > 0) {
                                    frndsrevadapter = new FriendReviewsAdapter(getActivity(), listRevfrnd_fbId, listRevfrnd_rating, listRevfrnd_revtext, listRevfrnd_revname);
                                    frndrevlistView.setAdapter(frndsrevadapter);
                                    new Task().execute();

                                }
                                if (listRevcritic_fbId.size() > 0) {
                                    criticrevadapter = new CriticReviewsAdapter(getActivity(), listRevcritic_fbId, listRevcritic_rating, listRevcritic_revtext, listRevcritic_revname);
                                    criticrevlistview.setAdapter(criticrevadapter);

                                }
                                if (listRevother_fbId.size() > 0) {
                                    othersrevadapter = new OthersReviewsAdapter(getActivity(), listRevother_fbId, listRevother_rating, listRevother_revtext, listRevother_revname, restoreduserid);
                                    otherlistview.setAdapter(othersrevadapter);

                                    otherlistview.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {

                                        @Override
                                        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                                            otherlistview.removeOnLayoutChangeListener(this);
                                            //detailProgress.setVisibility(View.GONE);
                                        }
                                    });

                                    othersrevadapter.notifyDataSetChanged();

                                }
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    public void setMovieValues() {
        movie_title.setText(movieName);
        movie_releasedate.setText(setDate(movieReleaseDate));
        movie_director.setText(movieDirector);
        movie_musicdirector.setText(movieMusicDirector);
        movie_rating.setText(movieRating + " %");
        numof_reiews.setText(numberofreviews);

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
