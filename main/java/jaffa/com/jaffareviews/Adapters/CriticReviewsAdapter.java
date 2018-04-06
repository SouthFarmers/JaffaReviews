package jaffa.com.jaffareviews.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import jaffa.com.jaffareviews.Fragments.MovieDetailFragment;
import jaffa.com.jaffareviews.POJO.MoviedetailReviewsPOJO;
import jaffa.com.jaffareviews.R;
import jaffa.com.jaffareviews.Volley.VolleySingleton;

/**
 * Created by gautham on 11/6/17.
 */

public class CriticReviewsAdapter extends BaseAdapter implements View.OnClickListener{

    Context mContext;
    private MovieDetailFragment.OnMovieDetailFragmentListener mListener;
    private ArrayList<MoviedetailReviewsPOJO> list;

    private static class ViewHolder {
        TextView revtext;
        RatingBar revrating;
    }

    public CriticReviewsAdapter(Context context, ArrayList<MoviedetailReviewsPOJO> Data) {
        this.mContext=context;
        this.list = Data;
        mListener = (MovieDetailFragment.OnMovieDetailFragmentListener) context;


    }

    @Override
    public void onClick(View v) {


    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
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
        TextView revrating;
        TextView revdate;
        ImageView revGif, revReaction;
        TextView revtag;
    }



    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        CriticReviewsAdapter.Holder holder=new CriticReviewsAdapter.Holder();
        View rowView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.moviedetail_review_row, parent, false);

        holder.revtext = (TextView) rowView.findViewById(R.id.reviewer_text);
        holder.revName = (TextView) rowView.findViewById(R.id.reviewer_name);
        holder.fb_img = (ImageView) rowView.findViewById(R.id.fb_icon);
        holder.revrating = (TextView) rowView.findViewById(R.id.reviewer_rating);
        holder.revdate = (TextView) rowView.findViewById(R.id.reviewer_date);
        holder.revGif = (ImageView) rowView.findViewById(R.id.gifView);
        holder.revtag = (TextView) rowView.findViewById(R.id.reviewer_tag);
        holder.revReaction = (ImageView) rowView.findViewById(R.id.reviewer_reaction);
        setImage("https://graph.facebook.com/"+list.get(position).getUserFBId()+"/picture?type=large&w‌​idth=100&height=150",holder);
        String as = list.get(position).getUserRating();

        if(Float.parseFloat(list.get(position).getUserRating()) > 2){
            holder.revReaction.setImageResource(R.drawable.heart);
        }else{
            holder.revReaction.setImageResource(R.drawable.broken);
        }
        holder.revtext.setText(list.get(position).getUserReviewText());
        holder.revName.setText(list.get(position).getUserName());
        holder.revrating.setText(list.get(position).getUserRating()+"/5");
        holder.revdate.setText(list.get(position).getUserReviewDate());
        holder.revtag.setText("#"+list.get(position).getUserReviewtag());
        if(list.get(position).getUserReviewGif().equalsIgnoreCase("") || list.get(position).getUserReviewGif().equalsIgnoreCase("null")) {
            holder.revGif.setVisibility(View.GONE);
        }else{
            Glide.with(mContext)
                    .load(list.get(position).getUserReviewGif())
                    .into(holder.revGif);
        }
        if(list.get(position).getUserReviewText().equalsIgnoreCase("") || list.get(position).getUserReviewText().equalsIgnoreCase("null")){
            holder.revtext.setVisibility(View.GONE);
        }
        if(list.get(position).getUserReviewtag().equalsIgnoreCase("") || list.get(position).getUserReviewtag().equalsIgnoreCase("null")){
            holder.revtag.setVisibility(View.GONE);
        }else if(list.get(position).getUserReviewtag().contains(",")){
            String tags[] = list.get(position).getUserReviewtag().split(",");
            holder.revtag.setText("#"+tags[0]+" "+ "#"+tags[1]);
        }


        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onReviewClick(list.get(position).getUserFBId());
            }
        });

        return rowView;
    }


    public void setImage(String url, final CriticReviewsAdapter.Holder holder){
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
