package jaffa.com.jaffareviews.Adapters;

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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import jaffa.com.jaffareviews.POJO.ReviewsByUserPOJO;
import jaffa.com.jaffareviews.POJO.SearchResultMoviesPOJO;
import jaffa.com.jaffareviews.R;
import jaffa.com.jaffareviews.Volley.VolleySingleton;

/**
 * Created by gautham on 11/22/17.
 */

public class ReviewsByUserAdapter extends RecyclerView.Adapter<ReviewsByUserAdapter.MyViewHolder> {

    private ArrayList<ReviewsByUserPOJO> list;
    TextView reviewByUserItemMovieTitle, reviewByUserItemMovieReview, reviewByUserItemMovieReleaseDate;
    ImageView reviewByUserItemMovieImage;
    RatingBar reviewByUserItemMovieRating;

    public ReviewsByUserAdapter(ArrayList<ReviewsByUserPOJO> Data) {
        list = Data;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {


        public final View mView;

        public MyViewHolder(View view) {
            super(view);
            mView = view;
            reviewByUserItemMovieImage = (ImageView) view.findViewById(R.id.review_by_user_item_image);
            reviewByUserItemMovieTitle = (TextView) view.findViewById(R.id.review_by_user_item_name);
            reviewByUserItemMovieRating = (RatingBar) view.findViewById(R.id.review_by_user_item_rating);
            reviewByUserItemMovieReview = (TextView) view.findViewById(R.id.review_by_user_item_text);
            reviewByUserItemMovieReleaseDate = (TextView) view.findViewById(R.id.review_by_user_item_release_date);
        }
    }


    @Override
    public ReviewsByUserAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_by_user_item, parent, false);

        return new ReviewsByUserAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ReviewsByUserAdapter.MyViewHolder holder, final int position) {

        setImage(list.get(position).getMovieImage(),reviewByUserItemMovieImage );
        reviewByUserItemMovieTitle.setText(list.get(position).getMoviename());
        reviewByUserItemMovieReview.setText(list.get(position).getReview());
        reviewByUserItemMovieRating.setRating(Float.parseFloat(list.get(position).getMovieRating()));
        reviewByUserItemMovieReleaseDate.setText(setDate(list.get(position).getReleaseDate()));


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

    public static String setDate(String date) {

        Calendar cal = Calendar.getInstance();
        try {
            DateFormat df = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
            Date da = df.parse(date);
            cal.setTime(da);
        }catch(ParseException e){}
        return cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH)+" "+ cal.get(Calendar.DATE)+" "+cal.get(Calendar.YEAR);
    }

}
