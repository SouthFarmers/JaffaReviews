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

import jaffa.com.jaffareviews.Fragments.ConnectionsFragment;
import jaffa.com.jaffareviews.POJO.leaderboard;
import jaffa.com.jaffareviews.R;
import jaffa.com.jaffareviews.Volley.VolleySingleton;

/**
 * Created by gautham on 11/22/17.
 */

public class ConnectionsAdapter extends RecyclerView.Adapter<ConnectionsAdapter.MyViewHolder> {

    private Context mContext;
    private ConnectionsFragment.OnConnectionsFragmentListener mListener;
    private ArrayList<leaderboard> list;
    TextView nameTextView;
    ImageView imgImageView;

    public ConnectionsAdapter(Context context, ArrayList<leaderboard> Data) {
        mContext = context;
        mListener = (ConnectionsFragment.OnConnectionsFragmentListener) context;
        list = Data;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {


        public final View mView;
        public String mBoundString;

        public MyViewHolder(View view) {
            super(view);
            mView = view;
            nameTextView = (TextView) view.findViewById(R.id.connection_name);
            imgImageView = (ImageView) view.findViewById(R.id.connection_image);
        }
    }


    @Override
    public ConnectionsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.connection_grid_item, parent, false);

        return new ConnectionsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ConnectionsAdapter.MyViewHolder holder, final int position) {

        nameTextView.setText(list.get(position).getName());
        setImage("https://graph.facebook.com/"+list.get(position).getFbid()+"/picture?type=large&w\u200C\u200Bidth=100&height=150",holder);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onConnectionClick(list.get(position).getFbid());
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static float dpToPixels(int dp, Context context) {
        return dp * (context.getResources().getDisplayMetrics().density);
    }

    public void setImage(String url, final ConnectionsAdapter.MyViewHolder holder){
        ImageRequest imgRequest = new ImageRequest(url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        imgImageView.setImageBitmap(response);
                    }
                }, 0, 0, ImageView.ScaleType.FIT_XY, Bitmap.Config.ARGB_8888, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                imgImageView.setBackgroundColor(Color.parseColor("#ff0000"));
                error.printStackTrace();
            }
        });
        VolleySingleton.getInstance().addToRequestQueue(imgRequest);
    }

}