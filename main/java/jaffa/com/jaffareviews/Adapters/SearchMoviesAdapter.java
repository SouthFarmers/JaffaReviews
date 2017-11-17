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

import jaffa.com.jaffareviews.Fragments.MainGridFragment;
import jaffa.com.jaffareviews.POJO.SearchResultMoviesPOJO;
import jaffa.com.jaffareviews.POJO.UpComingMoviesPOJO;
import jaffa.com.jaffareviews.R;
import jaffa.com.jaffareviews.Volley.VolleySingleton;

/**
 * Created by gautham on 11/15/17.
 */

public class SearchMoviesAdapter extends RecyclerView.Adapter<SearchMoviesAdapter.MyViewHolder> {

    private ArrayList<SearchResultMoviesPOJO> list;
    TextView movie_title, reviewcount;
    ImageView movie_image;
    RatingBar avgrating;

    public SearchMoviesAdapter(ArrayList<SearchResultMoviesPOJO> Data) {
        list = Data;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {


        public final View mView;

        public MyViewHolder(View view) {
            super(view);
            mView = view;
            movie_image = (ImageView) view.findViewById(R.id.search_movie_image);
            movie_title = (TextView) view.findViewById(R.id.search_movie_name);
            avgrating = (RatingBar) view.findViewById(R.id.search_movie_rating);
            reviewcount = (TextView) view.findViewById(R.id.search_movie_reviews_count);
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
        reviewcount.setText(list.get(position).getNumRating()+" reviews");
        avgrating.setRating(Float.parseFloat(list.get(position).getAvgRating()));


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
