package jaffa.com.jaffareviews.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

import java.util.ArrayList;

import jaffa.com.jaffareviews.Fragments.ConnectionsFragment;
import jaffa.com.jaffareviews.Fragments.MainGridFragment;
import jaffa.com.jaffareviews.Fragments.SearchFragment;
import jaffa.com.jaffareviews.POJO.SearchResultMoviesPOJO;
import jaffa.com.jaffareviews.POJO.UpComingMoviesPOJO;
import jaffa.com.jaffareviews.R;
import jaffa.com.jaffareviews.Volley.VolleySingleton;

/**
 * Created by gautham on 11/15/17.
 */

public class SearchMoviesAdapter extends RecyclerView.Adapter<SearchMoviesAdapter.MyViewHolder> {

    private ArrayList<SearchResultMoviesPOJO> list;
    private SearchFragment.OnSearchFragmentListener mListener;
    TextView movie_title, reviewcount;
    ImageView movie_image, ratingimage;
    TextView avgrating;

    public SearchMoviesAdapter(Context context, ArrayList<SearchResultMoviesPOJO> Data) {
        mListener = (SearchFragment.OnSearchFragmentListener) context;
        list = Data;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {


        public final View mView;

        public MyViewHolder(View view) {
            super(view);
            mView = view;
            movie_image = (ImageView) view.findViewById(R.id.search_movie_image);
            movie_title = (TextView) view.findViewById(R.id.search_movie_name);
            avgrating = (TextView) view.findViewById(R.id.search_movie_rating);
            reviewcount = (TextView) view.findViewById(R.id.search_movie_reviews_count);
            ratingimage= (ImageView) view.findViewById(R.id.search_rating_image);
        }
    }


    @Override
    public SearchMoviesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_movies_card, parent, false);

        return new SearchMoviesAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final SearchMoviesAdapter.MyViewHolder holder, final int position) {

        setImage(list.get(position).getMovieImage(),movie_image );
        movie_title.setText(list.get(position).getMovieName());
        reviewcount.setText(list.get(position).getNumRating()+" user reviews");
        if(list.get(position).getAvgRating().equalsIgnoreCase("0")) {
            avgrating.setText("---");
            ratingimage.setVisibility(View.INVISIBLE);
        }else{
            avgrating.setText(list.get(position).getAvgRating() + " %");
            if (Integer.parseInt(list.get(position).getAvgRating()) > 60) {
                ratingimage.setImageResource(R.drawable.heart);
            } else {
                ratingimage.setImageResource(R.drawable.broken);
            }
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onSearchClick(list.get(position).getMovieName(), list.get(position).getMovieID());
            }
        });


    }

    public void setImage(String url,final  ImageView movie_img){

        ImageRequest imgRequest = new ImageRequest(url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        movie_img.setImageBitmap(response);

                    }
                }, 0, 0, ImageView.ScaleType.FIT_XY, Bitmap.Config.ARGB_8888, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                movie_img.setBackgroundColor(Color.parseColor("#ff0000"));
                error.printStackTrace();

            }
        });
        VolleySingleton.getInstance().addToRequestQueue(imgRequest);
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

}
