package com.example.nario.draglayout.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.nario.draglayout.Adapter.ListAdapter;
import com.example.nario.draglayout.Activity.ChatActivity;
import com.example.nario.draglayout.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PagerFragment1 extends Fragment {
	public Activity mActivity;
	public LayoutInflater Inflater;
//	private List<info> mlistInfo = new ArrayList<info>();
	private List<Map<String, Object>> listinfo;
	private ListView listView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mActivity = getActivity();
		Inflater = (LayoutInflater) mActivity
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);


	}
	
	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
//		View view = initView();
		View view = Inflater.inflate(R.layout.pager1,null);
		listView = (ListView)view.findViewById(R.id.list);
		List<Map<String, Object>> list=getData();
		listView.setAdapter(new ListAdapter(getActivity(),list));

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//				String infoTitle = listinfo.get(position).toString();    //获取信息标题
//				String infoDetails = listinfo.get(position).toString();    //获取信息详情
//				Toast.makeText(getActivity(), "信息:" + infoTitle + " " + infoDetails, Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(getActivity(),ChatActivity.class);
				intent.putExtra("chatfragment",listinfo.get(position).get("title")+"");
				startActivity(intent);
			}
		});
		return view;
	}

	private View initView() {
		View localView = Inflater.inflate(R.layout.pager1, null);
		return localView;
	}
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);

	}

	public List<Map<String,Object>> getData() {
		listinfo=new ArrayList<Map<String,Object>>();
		for (int i = 0; i < 10; i++) {
			Map<String, Object> map=new HashMap<String, Object>();
			map.put("image", R.drawable.avatar);
			map.put("title", "Name "+i);
			map.put("info", "Last message "+i);
			listinfo.add(map);
		}
		return listinfo;
	}


}
