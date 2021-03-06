package jaffa.com.jaffareviews.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import jaffa.com.jaffareviews.Fragments.MainGridFragment;
import jaffa.com.jaffareviews.POJO.leaderboard;
import jaffa.com.jaffareviews.R;
import jaffa.com.jaffareviews.Volley.VolleySingleton;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by gautham on 11/14/17.
 */

public class LeaderBoardAdapter extends RecyclerView.Adapter<LeaderBoardAdapter.MyViewHolder> {

    private Context mContext;
    private MainGridFragment.OnMainGridFragmentListener mListener;
    private MainGridFragment maingridfragment;
    private ArrayList<leaderboard> list;
    TextView titleTextView,followersTextView,ratingsTextView;
    ImageButton followbutton;
    boolean isFollowing;
    private String restoreduserid;
    public ImageView coverImageView;

    public LeaderBoardAdapter(Context context, ArrayList<leaderboard> Data, MainGridFragment fragment) {
        mContext = context;
        maingridfragment = fragment;
        mListener = (MainGridFragment.OnMainGridFragmentListener) context;
        list = Data;
        SharedPreferences prefs = context.getSharedPreferences("UserFbID", MODE_PRIVATE);
        restoreduserid = prefs.getString("UserFbID", null);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {


        public final View mView;
        public String mBoundString;

        public MyViewHolder(View view) {
            super(view);
            mView = view;
            titleTextView = (TextView) view.findViewById(R.id.cardtitle);
            coverImageView = (ImageView) view.findViewById(R.id.cardrank);
            followersTextView = (TextView) view.findViewById(R.id.cardfollowers);
            ratingsTextView = (TextView) view.findViewById(R.id.cardreviews);
            followbutton = (ImageButton) view.findViewById(R.id.cardfollowbutton);
        }
    }


    @Override
    public LeaderBoardAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.leaderboard_card, parent, false);

        return new LeaderBoardAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final LeaderBoardAdapter.MyViewHolder holder, final int position) {

        titleTextView.setText(list.get(position).getName());
        setImage("https://graph.facebook.com/"+list.get(position).getFbid()+"/picture?type=large&w\u200C\u200Bidth=100&height=150",holder);
        followersTextView.setText(list.get(position).getFollowers());
        ratingsTextView.setText(list.get(position).getRatings());
        if(list.get(position).isFollowing()){
            followbutton.setBackgroundResource(R.drawable.following);
            isFollowing = true;

        }else{
            isFollowing = false;
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onLeaderBoadClick(list.get(position).getFbid());
            }
        });

        followbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isFollowing) {
                    FollowUser(restoreduserid, list.get(position).getFbid());
//                    followbutton.setBackgroundResource(R.drawable.following);
                }
            }
        });

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

    public void setImage(String url, final LeaderBoardAdapter.MyViewHolder holder){
        ImageRequest imgRequest = new ImageRequest(url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        coverImageView.setImageBitmap(response);
                    }
                }, 0, 0, ImageView.ScaleType.FIT_XY, Bitmap.Config.ARGB_8888, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                coverImageView.setBackgroundColor(Color.parseColor("#ff0000"));
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
                        try {
                            Boolean arrData = response.getBoolean("Data");
                            if (arrData) {
                            maingridfragment.loadLeaderboard();
//                                Toast.makeText(mContext, "Success!", Toast.LENGTH_SHORT);
                            }else{
//                                Toast.makeText(mContext, "Something went wrong", Toast.LENGTH_SHORT);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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

