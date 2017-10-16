package com.example.nario.draglayout.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.nario.draglayout.Activity.People_info;
import com.example.nario.draglayout.DBHelper;
import com.example.nario.draglayout.FriendList.HintSideBar;
import com.example.nario.draglayout.FriendList.SideBar;
import com.example.nario.draglayout.FriendList.User;
import com.example.nario.draglayout.FriendList.UserAdapter;
import com.example.nario.draglayout.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PagerFragment2 extends Fragment implements SideBar.OnChooseLetterChangedListener {
    public Activity mActivity;
    public LayoutInflater inflater;
    private List<User> userList;
    private DBHelper dbHelper;
    private UserAdapter adapter;
    private HintSideBar hintSideBar;
    private RecyclerView rv_userList;
    private LinearLayoutManager manager;
    private boolean allowed_refresh = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivity = getActivity();
        inflater = (LayoutInflater) mActivity
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = initView();

        dbHelper = new DBHelper(getActivity());
        //creat database
        hintSideBar = (HintSideBar) view.findViewById(R.id.hintSideBar);
        rv_userList = (RecyclerView) view.findViewById(R.id.rv_userList);
        hintSideBar.setOnChooseLetterChangedListener(this);
        manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rv_userList.setLayoutManager(manager);
        userList = new ArrayList<>();
        adapter = new UserAdapter(getActivity());
        initData();
        adapter.setData(userList);
        adapter.setOnItemClickLitener(new UserAdapter.mOnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(getActivity(), position + " click", Toast.LENGTH_SHORT).show();
                allowed_refresh =true;
                Intent intent = new Intent();
                intent.setClass(getActivity(), People_info.class);
                intent.putExtra("name", userList.get(position).getUserName());
                intent.putExtra("info", userList.get(position).getInfo());
                intent.putExtra("sex", userList.get(position).getSex());
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                Toast.makeText(getActivity(), position + " long click", Toast.LENGTH_SHORT).show();
                final User p = userList.get(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Are you sure you want to delete this person？").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dbHelper.delete(p.getId());
                        initData();
                        adapter.refresh(userList);
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create().show();

                dbHelper.close();
            }
        });
        rv_userList.setAdapter(adapter);
        return view;
    }

    private View initView() {
        View localView = inflater.inflate(R.layout.friends_main, null);

        return localView;
    }

    public void initData() {
        userList = new ArrayList<>();
        Cursor c = dbHelper.query();
        while (c.moveToNext()) {
            int _id = c.getInt(c.getColumnIndex("_id"));
            String name = c.getString(c.getColumnIndex("name"));
            String sex = c.getString(c.getColumnIndex("sex"));
            String info = c.getString(c.getColumnIndex("info"));
            if (!name.equals("")) {
                User user = new User(name, sex, info);
                user.setId(_id);
                userList.add(user);
            }
        }

        Collections.sort(userList);
        dbHelper.close();
    }

    @Override
    public void onChooseLetter(String s) {
        int i = adapter.getFirstPositionByChar(s.charAt(0));
        if (i == -1) {
            return;
        }
        manager.scrollToPositionWithOffset(i, 0);
    }

    @Override
    public void onNoChooseLetter() {

    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
        adapter.refresh(userList);
    }

}
