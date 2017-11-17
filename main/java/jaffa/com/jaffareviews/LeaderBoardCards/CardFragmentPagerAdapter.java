package jaffa.com.jaffareviews.LeaderBoardCards;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gautham on 11/3/17.
 */

public class CardFragmentPagerAdapter extends FragmentStatePagerAdapter implements CardAdapter {

    private List<CardFragment> mFragments;
    private float mBaseElevation;
    private List<String> leaderboardname, leaderboardfollowers,leaderboardratings, leaderboardfbId;
    private List<Boolean> leaderboardisfollowing;

    public CardFragmentPagerAdapter(FragmentManager fm, float baseElevation, List<String>leaderboardname, List<String>leaderboardfollowers, List<String>leaderboardratings, List<String>leaderboardfbId, List<Boolean> leaderboardisfollowing) {
        super(fm);
        mFragments = new ArrayList<>();
        mBaseElevation = baseElevation;
        this.leaderboardname = leaderboardname;
        this.leaderboardfollowers = leaderboardfollowers;
        this.leaderboardratings = leaderboardratings;
        this.leaderboardfbId = leaderboardfbId;
        this.leaderboardisfollowing = leaderboardisfollowing;

        for(int i = 0; i< leaderboardname.size(); i++){
            addCardFragment(new CardFragment(), leaderboardname.get(i).toString(),leaderboardfollowers.get(i).toString(),leaderboardratings.get(i).toString(),leaderboardfbId.get(i), leaderboardisfollowing.get(i));
        }
    }

    @Override
    public float getBaseElevation() {
        return mBaseElevation;
    }

    @Override
    public CardView getCardViewAt(int position) {
        return mFragments.get(position).getCardView();
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Object fragment = super.instantiateItem(container, position);
        mFragments.set(position, (CardFragment) fragment);
        return fragment;
    }

    @Override
    public int getItemPosition(Object object){
        return PagerAdapter.POSITION_NONE;
    }

    public void addCardFragment(CardFragment fragment, String leaderboardname, String leaderboardfollowers, String leaderboardratings, String leaderboardfbId, Boolean leaderboardisfollowing) {


        mFragments.add(fragment.newInstance(leaderboardname, leaderboardfollowers, leaderboardratings, leaderboardfbId, leaderboardisfollowing));


    }

}
