package com.kk.taurus.avplayer.list_to_detail;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by dumingwei on 2020-02-07.
 * Desc:
 */
public class VideoImageAdapter extends FragmentPagerAdapter {

    List<Fragment> fragmentList;

    public VideoImageAdapter(FragmentManager fm, List<Fragment> fragmentList) {
        super(fm);
        this.fragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }
}
