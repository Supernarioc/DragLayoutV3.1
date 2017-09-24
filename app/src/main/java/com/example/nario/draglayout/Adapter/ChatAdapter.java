package com.example.nario.draglayout.Adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.text.util.Linkify;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.nario.draglayout.Activity.MainActivity;
import com.example.nario.draglayout.ChatModel;
import com.example.nario.draglayout.ItemModel;
import com.example.nario.draglayout.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import com.example.nario.draglayout.Screen_info;
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.BaseAdapter>{

    public interface OnItemClickLitener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    private OnItemClickLitener mOnItemClickLitener = null;

    public void setOnItemClickLitener(OnItemClickLitener listener) {
        this.mOnItemClickLitener = listener;
    }

    private ArrayList<ItemModel> dataList = new ArrayList<>();

    public ChatAdapter(ArrayList<ItemModel> adapterlist) {
        this.dataList = adapterlist;
    }

    public void replaceAll(ArrayList<ItemModel> list) {
        dataList.clear();
        if (list != null && list.size() > 0) {
            dataList.addAll(list);
        }
        notifyDataSetChanged();
    }

    public void addAll(ArrayList<ItemModel> list) {
        if (dataList != null && list != null) {
            dataList.addAll(list);
            notifyItemRangeChanged(dataList.size(), list.size());
        }

    }

    @Override
    public ChatAdapter.BaseAdapter onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ItemModel.CHAT_A:
                return new ChatAViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.rec_view, parent, false));
            case ItemModel.CHAT_B:
                return new ChatBViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.sender_view, parent, false));
            case ItemModel.PHOTO:
                return new PhotoViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.send_image_test, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(ChatAdapter.BaseAdapter holder, final int position) {
        holder.setData(dataList.get(position).object);

        if (mOnItemClickLitener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    mOnItemClickLitener.onItemClick(v, position);
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mOnItemClickLitener.onItemLongClick(v, position);
                    return false;
                }
            });
        }
    }


    @Override
    public int getItemViewType(int position) {
        return dataList.get(position).type;
    }

    @Override
    public int getItemCount() {
        return dataList != null ? dataList.size() : 0;
    }

    public class BaseAdapter extends RecyclerView.ViewHolder {

        public BaseAdapter(View itemView) {
            super(itemView);
        }

        void setData(Object object) {

        }
    }

    private class ChatAViewHolder extends BaseAdapter {
        private ImageView ic_user;
        private TextView tv;

        public ChatAViewHolder(View view) {
            super(view);
            ic_user = (ImageView) itemView.findViewById(R.id.ic_user);
            tv = (TextView) itemView.findViewById(R.id.tv);
            Linkify.addLinks(tv,Linkify.EMAIL_ADDRESSES|Linkify.WEB_URLS|Linkify.PHONE_NUMBERS);
        }

        @Override
        void setData(Object object) {
            super.setData(object);
            ChatModel model = (ChatModel) object;
            Picasso.with(itemView.getContext()).load(model.getIcon()).placeholder(R.mipmap.ic_launcher).into(ic_user);
            tv.setText(model.getContent());
        }
    }

    private class ChatBViewHolder extends BaseAdapter {
        private ImageView ic_user;
        private TextView tv;

        public ChatBViewHolder(View view) {
            super(view);
            ic_user = (ImageView) itemView.findViewById(R.id.ic_user);
            tv = (TextView) itemView.findViewById(R.id.tv);
            Linkify.addLinks(tv,Linkify.EMAIL_ADDRESSES|Linkify.WEB_URLS|Linkify.PHONE_NUMBERS);

        }

        @Override
        void setData(Object object) {
            super.setData(object);
            ChatModel model = (ChatModel) object;
            Picasso.with(itemView.getContext()).load(model.getIcon()).placeholder(R.mipmap.ic_launcher).into(ic_user);
            tv.setText(model.getContent());
        }
    }

    private class PhotoViewHolder extends BaseAdapter {
        private ImageView ic_user;
        private ImageView image;

        public PhotoViewHolder(View view) {
            super(view);
            ic_user = (ImageView) itemView.findViewById(R.id.ic_user);
            image = (ImageView) itemView.findViewById(R.id.send_image);

        }

        @Override
        void setData(Object object) {
            super.setData(object);
            ChatModel model = (ChatModel) object;
//            Picasso.with(itemView.getContext()).load(model.getimg()).placeholder(R.id.send_image).into(image);

            image.setImageBitmap(model.getBitmap());
        }
    }

}
