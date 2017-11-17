package jaffa.com.jaffareviews.LeaderBoardCards;

import android.support.v7.widget.CardView;

/**
 * Created by gautham on 11/3/17.
 */

public interface CardAdapter {

    int MAX_ELEVATION_FACTOR = 8;

    float getBaseElevation();

    CardView getCardViewAt(int position);

    int getCount();
}
