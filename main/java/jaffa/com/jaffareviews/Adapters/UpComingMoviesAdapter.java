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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import jaffa.com.jaffareviews.Fragments.MainGridFragment;
import jaffa.com.jaffareviews.POJO.UpComingMoviesPOJO;
import jaffa.com.jaffareviews.R;
import jaffa.com.jaffareviews.Volley.VolleySingleton;

/**
 * Created by gautham on 11/14/17.
 */

public class UpComingMoviesAdapter extends RecyclerView.Adapter<UpComingMoviesAdapter.MyViewHolder> {

    private ArrayList<UpComingMoviesPOJO> list;
    TextView movie_title, release_date;
    ImageView movie_image;

    public UpComingMoviesAdapter(ArrayList<UpComingMoviesPOJO> Data) {
        list = Data;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {


        public final View mView;

        public MyViewHolder(View view) {
            super(view);
            mView = view;
            movie_image = (ImageView) view.findViewById(R.id.upcoming_movie_image);
            movie_title = (TextView) view.findViewById(R.id.upcoming_movie_title);
            release_date = (TextView) view.findViewById(R.id.release_date);

        }
    }


    @Override
    public UpComingMoviesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.upcoming_movies_card, parent, false);

        return new UpComingMoviesAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final UpComingMoviesAdapter.MyViewHolder holder, final int position) {

        setImage(list.get(position).getMovieImage(),movie_image );
        movie_title.setText(list.get(position).getMovieName());
        Calendar mydate = new GregorianCalendar();
        Date thedate = null;
        try {
            thedate = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH).parse(list.get(position).getMovieReleaseDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        mydate.setTime(thedate);
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd");
        release_date.setText(formatter.format(mydate.getTime()));

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

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public static float dpToPixels(int dp, Context context) {
        return dp * (context.getResources().getDisplayMetrics().density);
    }
}
