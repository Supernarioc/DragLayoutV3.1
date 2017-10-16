package com.example.nario.draglayout.Activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nario.draglayout.Adapter.FragmentAdapter;
import com.example.nario.draglayout.DragLayout;
import com.example.nario.draglayout.DragLayout.OnLayoutDragingListener;
import com.example.nario.draglayout.Fragment.PagerFragment1;
import com.example.nario.draglayout.Fragment.PagerFragment2;
import com.example.nario.draglayout.Fragment.PagerFragment3;
import com.example.nario.draglayout.Fragment.PagerFragment4;
import com.example.nario.draglayout.MainContentLayout;
import com.example.nario.draglayout.R;
import com.nineoldandroids.view.ViewHelper;

public class MainActivity extends AppCompatActivity implements OnClickListener {
    private DragLayout mDragLayout;
    private String[] titles = new String[]{"Chat", "Contact", "Nearby", "Setting"};
    private TabLayout mTabLayout;
    private boolean isFocus = false;
    private ImageView topbarLeftImage;
    private ImageView profile_b;
    private ViewPager mViewPager;
    private List<Fragment> fragmentList;
    private MainContentLayout mMainContent;
    private FragmentAdapter adapter;
    private List<String> mTitles;
    private int[] mImgs = new int[]{R.drawable.selector_bg, R.drawable.selector_bg_attach, R.drawable.selector_bg_info,
            R.drawable.selector_tab_message};
    private static Context context;

    public static Context getMainContext() {
        return MainActivity.context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDragLayout = (DragLayout) findViewById(R.id.dl);
        topbarLeftImage = (ImageView) findViewById(R.id.topbar_left_button);
        mViewPager = (ViewPager) findViewById(R.id.pager_view);
        mMainContent = (MainContentLayout) findViewById(R.id.mainContent);
        mTabLayout = (TabLayout) findViewById(R.id.tablayout);
        profile_b = (ImageView) findViewById(R.id.profile_button);
        profile_b.setOnClickListener(this);
        mMainContent.setDragLayout(mDragLayout);

        fragmentList = new ArrayList<Fragment>();
        PagerFragment1 mPage1 = new PagerFragment1();
        PagerFragment2 mPage2 = new PagerFragment2();
        PagerFragment3 mPage3 = new PagerFragment3();
        PagerFragment4 mPage4 = new PagerFragment4();
        fragmentList.add(mPage1);
        fragmentList.add(mPage2);
        fragmentList.add(mPage3);
        fragmentList.add(mPage4);

        mTitles = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            mTitles.add(titles[i]);
        }

        topbarLeftImage.setOnClickListener(this);
        mViewPager.setAdapter(new ChartTabAdapter(getSupportFragmentManager()));
        mViewPager.setOnPageChangeListener(new MyOnPageChangeListener());
        adapter = new FragmentAdapter(getSupportFragmentManager(), fragmentList, mTitles);
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setSelectedTabIndicatorHeight(0);
        for (int i = 0; i < mTitles.size(); i++) {
            TabLayout.Tab itemTab = mTabLayout.getTabAt(i);
            if (itemTab != null) {
                itemTab.setCustomView(R.layout.item_tab);
                TextView textView = (TextView) itemTab.getCustomView().findViewById(R.id.tv_name);
                textView.setText(mTitles.get(i));
                ImageView imageView = (ImageView) itemTab.getCustomView().findViewById(R.id.iv_img);
                imageView.setImageResource(mImgs[i]);
            }
        }
        mTabLayout.getTabAt(0).getCustomView().setSelected(true);

        mDragLayout.setOnLayoutDragingListener(new OnLayoutDragingListener() {

            @Override
            public void onOpen() {

            }

            @Override
            public void onDraging(float percent) {
                ViewHelper.setAlpha(topbarLeftImage, 1 - percent);
            }

            @Override
            public void onClose() {

            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (!isFocus && hasFocus) {
            MainContentLayout.LayoutParams viewpagerlayout = new MainContentLayout.LayoutParams(mViewPager.getLayoutParams());
            viewpagerlayout.width = mViewPager.getWidth();
            viewpagerlayout.height = mViewPager.getHeight() - 130;
            viewpagerlayout.addRule(MainContentLayout.BELOW, R.id.topbar);
            mViewPager.setLayoutParams(viewpagerlayout);
            isFocus = true;
        }

    }

    private class ChartTabAdapter extends FragmentStatePagerAdapter {

        public ChartTabAdapter(FragmentManager fm) {
            super(fm);
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

    private class MyOnPageChangeListener implements OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }

        @Override
        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int postion) {
            switch (postion) {
                case 0:
                    mDragLayout.setDrag(true);
                    break;
                case 1:
                    mDragLayout.setDrag(false);
                    break;
                case 2:
                    mDragLayout.setDrag(false);
                    break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.topbar_left_button:
                mDragLayout.open();
                break;
            case R.id.profile_button:
                Intent intent = new Intent(this, Profile.class);
                startActivity(intent);
                break;
        }
    }

    public List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<>();
        int i = 0;
        while (i < 10) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("image", R.drawable.avatar);
            map.put("title", "name" + i);
            map.put("info", "last message" + i);
            list.add(map);
            i++;
        }
        return list;
    }
}
