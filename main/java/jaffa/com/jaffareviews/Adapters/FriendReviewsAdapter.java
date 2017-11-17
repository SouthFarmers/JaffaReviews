package jaffa.com.jaffareviews.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

import java.util.List;

import jaffa.com.jaffareviews.Fragments.MovieDetailFragment;
import jaffa.com.jaffareviews.R;
import jaffa.com.jaffareviews.Volley.VolleySingleton;

/**
 * Created by gautham on 11/6/17.
 */

public class FriendReviewsAdapter extends BaseAdapter implements View.OnClickListener{

    Context mContext;
    private MovieDetailFragment.OnMovieDetailFragmentListener mListener;
    private List<String> listRevfrnd_fbId, listRevfrnd_rating,listRevfrnd_revtext, listRevfrnd_revname;

    private static class ViewHolder {
        TextView revtext;
        RatingBar revrating;
    }

    public FriendReviewsAdapter(Context context, List<String> listRevfrnd_fbId, List<String> listRevfrnd_rating, List<String>listRevfrnd_revtext,List<String>listRevfrnd_revname) {
        this.mContext=context;
        this.mContext=context;
        this.listRevfrnd_fbId = listRevfrnd_fbId;
        this.listRevfrnd_rating = listRevfrnd_rating;
        this.listRevfrnd_revtext = listRevfrnd_revtext;
        this.listRevfrnd_revname = listRevfrnd_revname;
        mListener = (MovieDetailFragment.OnMovieDetailFragmentListener) context;


    }

    @Override
    public void onClick(View v) {


    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return listRevfrnd_fbId.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public class Holder
    {
        TextView revtext;
        TextView revName;
        ImageView fb_img;
        RatingBar revrating;
    }



    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder=new Holder();
        View rowView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.moviedetail_review_row, parent, false);

        holder.revtext = (TextView) rowView.findViewById(R.id.reviewer_text);
        holder.revName = (TextView) rowView.findViewById(R.id.reviewer_name);
        holder.fb_img = (ImageView) rowView.findViewById(R.id.fb_icon);
        holder.revrating = (RatingBar) rowView.findViewById(R.id.reviewer_rating);
        setImage("https://graph.facebook.com/"+listRevfrnd_fbId.get(position).toString()+"/picture?type=large&w‌​idth=100&height=150",holder);

        holder.revtext.setText(listRevfrnd_revtext.get(position).toString());
        holder.revName.setText(listRevfrnd_revname.get(position).toString());
        holder.revrating.setRating(Float.parseFloat(listRevfrnd_rating.get(position).toString()));

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        return rowView;
    }


    public void setImage(String url, final Holder holder){
        Log.d("",url);
        ImageRequest imgRequest = new ImageRequest(url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        holder.fb_img.setImageBitmap(response);
                    }
                }, 0, 0, ImageView.ScaleType.FIT_XY, Bitmap.Config.ARGB_8888, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                holder.fb_img.setBackgroundColor(Color.parseColor("#ff0000"));
                error.printStackTrace();
            }
        });
        VolleySingleton.getInstance().addToRequestQueue(imgRequest);
    }
}
