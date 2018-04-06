package jaffa.com.jaffareviews.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
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
import com.bumptech.glide.Glide;

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

    Context mContext;
    private ArrayList<ReviewsByUserPOJO> list;
    TextView reviewByUserItemTitle, reviewByUserItemReview, reviewByUserItemTag, reviewByUserItemrating;
    ImageView reviewByUserItemGif, reviewByUserItemReaction;

    public ReviewsByUserAdapter(Context context, ArrayList<ReviewsByUserPOJO> Data) {
        this.mContext=context;
        list = Data;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {


        public final View mView;

        public MyViewHolder(View view) {
            super(view);
            mView = view;
            reviewByUserItemGif = (ImageView) view.findViewById(R.id.review_by_user_item_gifView);
            reviewByUserItemReaction = (ImageView) view.findViewById(R.id.review_by_user_item_reaction);
            reviewByUserItemTitle = (TextView) view.findViewById(R.id.review_by_user_item_name);
            reviewByUserItemrating = (TextView) view.findViewById(R.id.review_by_user_item_rating);
            reviewByUserItemReview = (TextView) view.findViewById(R.id.review_by_user_item_text);
            reviewByUserItemTag = (TextView) view.findViewById(R.id.review_by_user_item_tag);
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

        reviewByUserItemTitle.setText(list.get(position).getMovieName());
        reviewByUserItemrating.setText(list.get(position).getMovieRating()+"/5");
        reviewByUserItemTag.setText(list.get(position).getMovieTag());
        reviewByUserItemReview.setText(list.get(position).getReview() );

        if(Float.parseFloat(list.get(position).getMovieRating()) > 2){

            reviewByUserItemReaction.setImageResource(R.drawable.heart);
        }else{
            reviewByUserItemReaction.setImageResource(R.drawable.broken);
        }

        if(list.get(position).getMovieGif().equalsIgnoreCase("") || list.get(position).getMovieGif().equalsIgnoreCase("null")) {
            reviewByUserItemGif.setVisibility(View.GONE);
        }else{
            Glide.with(mContext)
                    .load(list.get(position).getMovieGif())
                    .into(reviewByUserItemGif);
        }

        if(list.get(position).getReview().equalsIgnoreCase("") || list.get(position).getReview().equalsIgnoreCase("null")){
            reviewByUserItemReview.setVisibility(View.GONE);
        }
        if(list.get(position).getMovieTag().equalsIgnoreCase("") || list.get(position).getMovieTag().equalsIgnoreCase("null")){
            reviewByUserItemTag.setVisibility(View.GONE);
        }

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
