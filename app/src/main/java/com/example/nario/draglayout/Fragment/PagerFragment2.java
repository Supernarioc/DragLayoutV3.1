package com.example.nario.draglayout.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.nario.draglayout.FriendList.FriendsListActivity;
import com.example.nario.draglayout.FriendList.HintSideBar;
import com.example.nario.draglayout.FriendList.SideBar;
import com.example.nario.draglayout.FriendList.User;
import com.example.nario.draglayout.FriendList.UserAdapter;
import com.example.nario.draglayout.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PagerFragment2 extends Fragment implements SideBar.OnChooseLetterChangedListener{
	public Activity mActivity;
	public LayoutInflater inflater;
    private List<User> userList;

    private UserAdapter adapter;

    private LinearLayoutManager manager;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mActivity = getActivity();
		inflater = (LayoutInflater) mActivity
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
		View view = initView();
        HintSideBar hintSideBar = (HintSideBar)view.findViewById(R.id.hintSideBar);
        RecyclerView rv_userList = (RecyclerView) view.findViewById(R.id.rv_userList);
        hintSideBar.setOnChooseLetterChangedListener(this);
        manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rv_userList.setLayoutManager(manager);
        userList = new ArrayList<>();
        adapter = new UserAdapter(getActivity());
        initData();
        adapter.setData(userList);
        rv_userList.setAdapter(adapter);
		return view;
	}

	private View initView() {
		View localView = inflater.inflate(R.layout.friends_main, null);

		return localView;
	}
    public void initData() {
        User user1 = new User("A", "12345678");
        User user2 = new User("b", "12345678");
        User user3 = new User("c", "12345678");
        User user4 = new User("d", "12345678");
        User user5 = new User("u", "12345678");
        User user6 = new User("Y", "12345678");
        User user7 = new User("C", "12345678");
        User user15 = new User("A", "12345678");
        User user16 = new User("#", "12345678");
        User user17 = new User("@", "12345678");
        User user18 = new User("s", "12345678");
        User user19 = new User("89", "12345678");
        User user20 = new User("09", "12345678");
        User user21 = new User("oiu", "12345678");
        User user22 = new User("youj", "12345678");
        User user23 = new User("peter", "12345678");
        User user24 = new User("sdf", "12345678");
        User user25 = new User("cxv", "12345678");
        User user26 = new User("BA", "12345678");
        userList.add(user1);
        userList.add(user2);
        userList.add(user3);
        userList.add(user4);
        userList.add(user5);
        userList.add(user6);
        userList.add(user7);
        userList.add(user15);
        userList.add(user16);
        userList.add(user17);
        userList.add(user18);
        userList.add(user19);
        userList.add(user20);
        userList.add(user21);
        userList.add(user22);
        userList.add(user23);
        userList.add(user24);
        userList.add(user25);
        userList.add(user26);
        userList.add(user20);
        userList.add(user21);
        userList.add(user22);
        userList.add(user23);
        userList.add(user24);
        userList.add(user25);
        userList.add(user26);
        Collections.sort(userList);
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

}
