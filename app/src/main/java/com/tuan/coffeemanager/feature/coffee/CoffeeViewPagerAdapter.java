package com.tuan.coffeemanager.feature.coffee;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.tuan.coffeemanager.feature.coffee.fragment.view.MenuFragment;
import com.tuan.coffeemanager.feature.coffee.fragment.view.MoreFragment;
import com.tuan.coffeemanager.feature.coffee.fragment.view.TableFragment;

public class CoffeeViewPagerAdapter extends FragmentStatePagerAdapter {

    private final int NUM_TAB = 3;

    public CoffeeViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0: {
                return MenuFragment.newInstance();
            }
            case 1: {
                return TableFragment.newInstance();
            }
            case 2: {
                return MoreFragment.newInstance();
            }
            default: {
                return MenuFragment.newInstance();
            }
        }
    }

    @Override
    public int getCount() {
        return NUM_TAB;
    }
}