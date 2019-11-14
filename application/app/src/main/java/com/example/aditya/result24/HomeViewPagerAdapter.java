package com.example.aditya.result24;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class HomeViewPagerAdapter extends FragmentPagerAdapter {

    public HomeViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) return new JobFragment();
        else if (position == 1) return new QuizFragment();
        else return new ProfileFragment();
    }

    @Override
    public int getCount() {
        return 3;
    }
}
