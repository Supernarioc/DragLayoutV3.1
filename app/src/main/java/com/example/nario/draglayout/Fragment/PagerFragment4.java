package com.example.nario.draglayout.Fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.nario.draglayout.R;

public class PagerFragment4 extends Fragment {
        public Activity mActivity;
        public LayoutInflater inflater;
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mActivity = getActivity();
            inflater = (LayoutInflater) mActivity
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = initView();
            return view;
        }

        private View initView() {
            View localView = inflater.inflate(R.layout.pager4, null);
            return localView;
        }
    }
