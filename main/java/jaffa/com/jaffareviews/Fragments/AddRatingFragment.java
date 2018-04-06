package jaffa.com.jaffareviews.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.pchmn.materialchips.model.ChipInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import jaffa.com.jaffareviews.Adapters.AddRatingTagsAdapter;
import jaffa.com.jaffareviews.Adapters.GiphyResultsAdapter;
import jaffa.com.jaffareviews.Adapters.UpComingMoviesAdapter;
import jaffa.com.jaffareviews.Helpers.TagChips;
import jaffa.com.jaffareviews.POJO.TagChipsPOJO;
import jaffa.com.jaffareviews.POJO.UpComingMoviesPOJO;
import jaffa.com.jaffareviews.R;
import jaffa.com.jaffareviews.Volley.VolleySingleton;

import static android.content.Context.MODE_PRIVATE;

public class AddRatingFragment extends Fragment implements View.OnClickListener, AddRatingTagsAdapter.ItemClickListener, GiphyResultsAdapter.ItemClickListener {

    String restoreduserid;
    RatingBar addRatingStars;
    RecyclerView addRatingsTags, giphySearchResults;
    AddRatingTagsAdapter addRatingTagsAdapter;
    TagChips chipsInput;
    EditText giphySearch, reviewText;
    GiphyResultsAdapter gifResultsAdapter;
    String selectedGIF, selctedTag, MovieID;
    Button saveButton;
    String addReviewURL = "http://jaffareviews.com/api/movie/AddRating";
    private AddRatingFragment.OnAddRatingFragmentListener mListener;
    ArrayList<String> tagNames = new ArrayList<>();
    AlertDialog alertDialog;
    boolean hasMyRating;

    public static AddRatingFragment newInstance(String movieID, boolean hasmyrating) {
        Bundle args = new Bundle();
        args.putString("MOVIEID", movieID);
        args.putBoolean("hasmyrating", hasmyrating);
        AddRatingFragment fragment = new AddRatingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            MovieID = getArguments().getString("MOVIEID");
            hasMyRating = getArguments().getBoolean("hasmyrating");
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
        if (context instanceof AddRatingFragment.OnAddRatingFragmentListener) {
            mListener = (AddRatingFragment.OnAddRatingFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnAddRatingFragmentListener");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.add_review, container, false);

        SharedPreferences prefs = getActivity().getSharedPreferences(getString(R.string.shared_pref_FbID), MODE_PRIVATE);
        restoreduserid = prefs.getString(getString(R.string.shared_pref_FbID), null);

        addRatingStars = (RatingBar) rootView.findViewById(R.id.add_rating_stars);
        addRatingsTags = (RecyclerView) rootView.findViewById(R.id.add_rating_tags_list_now);
        giphySearchResults = (RecyclerView) rootView.findViewById(R.id.add_review_gif_search_results);
        chipsInput = (TagChips) rootView.findViewById(R.id.chips_input);
        giphySearch = (EditText) rootView.findViewById(R.id.add_review_giphy_search);
        reviewText = (EditText) rootView.findViewById(R.id.add_review_text);
        saveButton = (Button) rootView.findViewById(R.id.add_review_save);

        getGifs("https://api.giphy.com/v1/gifs/trending?api_key=7SuuTI8CjhyUnvjRRlFlMd2mTTGpinQo&limit=25&rating=G");

        tagNames.add("Awesome");
        tagNames.add("Feel Good");
        tagNames.add("Superb");
        tagNames.add("Average");
        tagNames.add("One time watch");
        tagNames.add("Time pass");
        tagNames.add("Bisket");
        tagNames.add("Disaster");
        tagNames.add("Worst");

        addRatingsTags.setHasFixedSize(true);
        LinearLayoutManager MyLayoutManager = new LinearLayoutManager(getActivity());
        MyLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        addRatingTagsAdapter = new AddRatingTagsAdapter(getActivity(), tagNames);
        addRatingTagsAdapter.setClickListener(this);
        addRatingsTags.setAdapter(addRatingTagsAdapter);
        addRatingsTags.setLayoutManager(MyLayoutManager);


        addRatingStars.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                tagNames.clear();
                if (ratingBar.getRating() <= 1) {
                    tagNames.add("Bisket");
                    tagNames.add("Disaster");
                    tagNames.add("Worst");
                    addRatingTagsAdapter.notifyDataSetChanged();
                } else if (ratingBar.getRating() < 3 && ratingBar.getRating() > 1) {
                    tagNames.add("Average");
                    tagNames.add("One time watch");
                    tagNames.add("Time pass");
                    addRatingTagsAdapter.notifyDataSetChanged();
                } else {
                    tagNames.add("Awesome");
                    tagNames.add("Feel Good");
                    tagNames.add("Superb");
                    addRatingTagsAdapter.notifyDataSetChanged();
                }
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (addRatingStars.getRating() == 0) {
                    alertDialog = new AlertDialog.Builder(getContext()).create();
                    alertDialog.setTitle("Alert");
                    alertDialog.setMessage("Choose Rating");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                } else
                    AddReviewToDB();
            }
        });

        giphySearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    giphySearch.clearFocus();
                    InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(giphySearch.getWindowToken(), 0);
                    getGifs("https://api.giphy.com/v1/gifs/search?api_key=7SuuTI8CjhyUnvjRRlFlMd2mTTGpinQo&q=" + giphySearch.getText().toString() + "&limit=25&offset=0&rating=G&lang=en");
                    return true;
                }
                return false;
            }
        });

        if(hasMyRating)
            getMyPreviousRating();

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    @Override
    public void onItemClick(View view, String tag) {
        List<ChipInterface> contactsSelected = (List<ChipInterface>) chipsInput.getSelectedChipList();
        if (contactsSelected.size() > 1) {
//            Toast.makeText(getActivity(), "Select only two tags. ", Toast.LENGTH_LONG).show();
            alertDialog = new AlertDialog.Builder(getContext()).create();
            alertDialog.setTitle("Thank you!");
            alertDialog.setMessage("Select only two tags.");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        } else
            chipsInput.addChip(tag, tag);
    }

    @Override
    public void onGifClick(View view, String url) {
        selectedGIF = url;
    }

    public void getGifs(String url) {

        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            ArrayList<String> giphyresults = new ArrayList<>();

                            JSONArray jsonArray = response.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                giphyresults.add(jsonArray.getJSONObject(i).optString("id"));
                            }

                            giphySearchResults.setHasFixedSize(true);
                            LinearLayoutManager MyLayoutManager = new LinearLayoutManager(getActivity());
                            MyLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                            gifResultsAdapter = new GiphyResultsAdapter(getActivity(), giphyresults);
                            gifResultsAdapter.setClickListener(AddRatingFragment.this);
                            giphySearchResults.setAdapter(gifResultsAdapter);
                            giphySearchResults.setLayoutManager(MyLayoutManager);


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

    public void AddReviewToDB() {

        List<ChipInterface> contactsSelected = (List<ChipInterface>) chipsInput.getSelectedChipList();
        if (contactsSelected.size() > 1)
            selctedTag = contactsSelected.get(0).getLabel()+ "," + contactsSelected.get(1).getLabel();
        else
            selctedTag = contactsSelected.get(0).getLabel();

        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("MovieID", MovieID);
            jsonObject.put("Rating", addRatingStars.getRating());
            jsonObject.put("Review", reviewText.getText().toString());
            jsonObject.put("FbID", restoreduserid);
            jsonObject.put("UserTags", selctedTag);
            jsonObject.put("GifURL", selectedGIF);
            jsonArray.put(jsonObject);

        } catch (Exception e) {

        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, addReviewURL, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            Boolean arrData = response.getBoolean("Data");
                            if (arrData) {
                                alertDialog = new AlertDialog.Builder(getContext()).create();
                                alertDialog.setTitle("Thank you!");
                                alertDialog.setMessage("You gave "+addRatingStars.getRating()+" rating out of 5");
                                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                alertDialog.show();
                                FragmentManager fm = getActivity().getSupportFragmentManager();
                                fm.popBackStack();
                            } else {
                                Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                                FragmentManager fm = getActivity().getSupportFragmentManager();
                                fm.popBackStack();
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

    public interface OnAddRatingFragmentListener {

    }

    public void getMyPreviousRating(){

        String url ="http://jaffareviews.com/api/movie/GetRating?FbID="+restoreduserid+"&MovieID="+MovieID;

        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            if (response.has("rating")) {
                                JSONObject responseObject = response.getJSONObject("rating");
                                reviewText.setText(responseObject.optString("Review"));
                                addRatingStars.setRating(Float.parseFloat(responseObject.optString("RatingStars")));
                                String previoustags = responseObject.optString("UserTags");
                                if(previoustags.contains(",")){
                                    String tags[] = previoustags.split(",");
                                    chipsInput.addChip(tags[0], tags[0]);
                                    chipsInput.addChip(tags[1], tags[1]);
                                }else{
                                    chipsInput.addChip(previoustags, previoustags);
                                }
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
