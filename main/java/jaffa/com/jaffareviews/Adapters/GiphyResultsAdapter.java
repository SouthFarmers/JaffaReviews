package jaffa.com.jaffareviews.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import jaffa.com.jaffareviews.R;

public class GiphyResultsAdapter extends RecyclerView.Adapter<GiphyResultsAdapter.ViewHolder> {

    private List<String> mData;
    private LayoutInflater mInflater;
    Context mcontext;
    private ItemClickListener mClickListener;
    int selectedPosition=-1;

    // data is passed into the constructor
    public GiphyResultsAdapter(Context context, List<String> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.mcontext = context;
    }

    // inflates the row layout from xml when needed
    @Override
    public GiphyResultsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.giphy_results_item_row, parent, false);
        return new GiphyResultsAdapter.ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(GiphyResultsAdapter.ViewHolder holder, int position) {
        if(selectedPosition==position)
            holder.itemView.setBackgroundResource(R.drawable.add_rating_button_border);
        else
            holder.itemView.setBackgroundResource(android.R.color.transparent);
        Glide.with(mcontext)
                .load("https://media3.giphy.com/media/"+mData.get(position)+"/100.gif")
                .into(holder.myGifView);

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView myGifView;

        ViewHolder(View itemView) {
            super(itemView);
            myGifView = itemView.findViewById(R.id.gifs);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null)
                mClickListener.onGifClick(view, "https://media3.giphy.com/media/"+mData.get(getAdapterPosition())+"/100.gif" );
            selectedPosition=getAdapterPosition();
            notifyDataSetChanged();
        }
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onGifClick(View view, String url);
    }
}
