package jaffa.com.jaffareviews.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import jaffa.com.jaffareviews.Fragments.MovieDetailFragment;
import jaffa.com.jaffareviews.R;
import jaffa.com.jaffareviews.Volley.VolleySingleton;

/**
 * Created by gautham on 11/6/17.
 */

public class OthersReviewsAdapter extends BaseAdapter implements View.OnClickListener{

    Context mContext;
    private MovieDetailFragment.OnMovieDetailFragmentListener mListener;
    String userFbID;
    private List<String> listRevother_fbId, listRevother_rating,listRevother_revtext, listRevother_revname;

    public OthersReviewsAdapter(Context context, List<String> listRevother_fbId, List<String> listRevother_rating, List<String>listRevother_revtext, List<String>listRevother_revname, String restoreduserid) {
        this.mContext=context;
        this.mContext=context;
        this.listRevother_fbId = listRevother_fbId;
        this.listRevother_rating = listRevother_rating;
        this.listRevother_revtext = listRevother_revtext;
        this.listRevother_revname = listRevother_revname;
        this.userFbID = restoreduserid;

        mListener = (MovieDetailFragment.OnMovieDetailFragmentListener) context;


    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return listRevother_fbId.size();
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
        Button follow;
        RatingBar revrating;
    }



    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        OthersReviewsAdapter.Holder holder=new OthersReviewsAdapter.Holder();
        View rowView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.moviedetail_others_review_row, parent, false);

        holder.revtext = (TextView) rowView.findViewById(R.id.reviewer_text);
        holder.revName = (TextView) rowView.findViewById(R.id.reviewer_name);
        holder.fb_img = (ImageView) rowView.findViewById(R.id.fb_icon);
        holder.revrating = (RatingBar) rowView.findViewById(R.id.reviewer_rating);
        holder.follow = (Button) rowView.findViewById(R.id.followbutton);
        holder.follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FollowUser(userFbID,listRevother_fbId.get(position).toString());
            }
        });
        setImage("https://graph.facebook.com/"+listRevother_fbId.get(position).toString()+"/picture?type=large&w‌​idth=100&height=150",holder);

        holder.revtext.setText(listRevother_revtext.get(position).toString());
        holder.revName.setText(listRevother_revname.get(position).toString());
        holder.revrating.setRating(Float.parseFloat(listRevother_rating.get(position).toString()));
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return rowView;
    }


    public void setImage(String url, final OthersReviewsAdapter.Holder holder){
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


    public void FollowUser(final String userFbID, final String FollowID){

        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();

        try{
            jsonObject.put("CriticFbID",FollowID);
            jsonObject.put("UserFbID",userFbID);
            jsonArray.put(jsonObject);

        }catch(Exception e){

        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, "http://jaffareviews.com/api/movie/FollowCritic", jsonObject,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        notifyDataSetChanged();
//                        try {
//                            //JSONArray arrData = response.getJSONArray("data");
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error.Response", error.toString());
                        Toast.makeText(mContext, "Something went wrong!",
                                Toast.LENGTH_LONG).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(jsonObjectRequest);
    }
}