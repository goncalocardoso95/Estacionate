package com.example.estacionate.ui.main;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.estacionate.FragmentData;
import com.example.estacionate.FragmentLocal;
import com.example.estacionate.R;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2};
    private final Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {

        Fragment fragment= null;
        switch(position){

            case 0:
                Fragment preco = new FragmentLocal();
                fragment =  preco;


                break;

            case 1:
                Fragment data = new FragmentData();
                fragment=  data;
                break;

        }

        return fragment;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch(position){

            case 0:
                return "Ordenar Local";


            case 1:
                return "Ordenar Data";


        }
        return  null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}