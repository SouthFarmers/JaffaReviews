package jaffa.com.jaffareviews.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

import java.util.ArrayList;
import java.util.List;

import jaffa.com.jaffareviews.Fragments.MainGridFragment;
import jaffa.com.jaffareviews.POJO.MovieDetailPOJO;
import jaffa.com.jaffareviews.R;
import jaffa.com.jaffareviews.Volley.VolleySingleton;

/**
 * Created by gautham on 11/3/17.
 */

public class GridViewAdapter extends RecyclerView.Adapter<GridViewAdapter.MyViewHolder> {

    private Context mContext;
    private MainGridFragment.OnMainGridFragmentListener mListener;
    TextView movie_title, movie_rating, friend_rating_count, critic_rating_count;
    ImageView movie_image,ratingimage;
    ArrayList<MovieDetailPOJO> list;

    public GridViewAdapter(Context context, ArrayList<MovieDetailPOJO> data) {
        mContext = context;
        this.list = data;
        mListener = (MainGridFragment.OnMainGridFragmentListener) context;

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {


        public final View mView;
        public String mBoundString;

        public MyViewHolder(View view) {
            super(view);
            mView = view;
            movie_image = (ImageView) view.findViewById(R.id.grid_movie_image);
            movie_title = (TextView) view.findViewById(R.id.grid_movie_title);
            movie_rating = (TextView) view.findViewById(R.id.grid_movie_rating);
            ratingimage = (ImageView) view.findViewById(R.id.ratingimage);
            friend_rating_count = (TextView) view.findViewById(R.id.friend_rating_count);
            critic_rating_count = (TextView) view.findViewById(R.id.critic_rating_count);
        }
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.grid_item_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        setImage(list.get(position).getMovieImage(),movie_image );
        movie_title.setText(list.get(position).getMovieTitle());
        friend_rating_count.setText(list.get(position).getFriendRatingCount());
        critic_rating_count.setText(list.get(position).getCriticRatingCount());
        movie_rating.setText(list.get(position).getAvgRating()+" %");
        if(Integer.parseInt(list.get(position).getAvgRating()) > 60){
            ratingimage.setImageResource(R.drawable.heart);
        }else{
            ratingimage.setImageResource(R.drawable.broken);
        }



        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onMovieClick(list.get(position).getMovieTitle(), list.get(position).getMovieID());
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

    public static float dpToPixels(int dp, Context context) {
        return dp * (context.getResources().getDisplayMetrics().density);
    }
}
