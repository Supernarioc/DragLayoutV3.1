package com.example.nario.draglayout.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nario.draglayout.R;

import java.util.List;
import java.util.Map;

/**
 * Created by nario on 2017/9/6.
 */

public class ListAdapter extends BaseAdapter {
    private List<Map<String,Object>> data;
    private LayoutInflater layoutInflater;
    private Context context;

    public ListAdapter(Context context,List<Map<String,Object>> data) {
        this.context=context;
        this.data=data;
        this.layoutInflater=LayoutInflater.from(context);
    }
    public final class info{
        public ImageView avatar;
        public TextView title;
        public TextView details;
    }
    public int getCount() {
        return data.size();
    }
    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        info infos=null;
        if(convertView==null){
            infos=new info();
            convertView=layoutInflater.inflate(R.layout.chat_item, null);
            infos.avatar=(ImageView)convertView.findViewById(R.id.img);
            infos.title=(TextView)convertView.findViewById(R.id.name);
            infos.details=(TextView)convertView.findViewById(R.id.info);
            convertView.setTag(infos);
        }else{
            infos=(info)convertView.getTag();
        }
        infos.avatar.setBackgroundResource((Integer)data.get(position).get("image"));
        infos.title.setText((String)data.get(position).get("title"));
        infos.details.setText((String)data.get(position).get("info"));
        return convertView;
    }


}