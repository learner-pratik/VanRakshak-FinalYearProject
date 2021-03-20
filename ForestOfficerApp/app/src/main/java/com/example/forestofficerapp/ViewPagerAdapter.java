package com.example.forestofficerapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private static int TAB_COUNT = 2;

    public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0 : {
                return CameraAlertFragment.newInstance();
            }
            case 1 : {
                return SOSAlertFragment.newInstance();
            }
        }
        return null;
    }

    @Override
    public int getCount() {
        return TAB_COUNT;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        switch (position) {
            case 0 : {
                return CameraAlertFragment.TITLE;
            }
            case 1 : {
                return SOSAlertFragment.TITLE;
            }
        }
        return super.getPageTitle(position);
    }
}
