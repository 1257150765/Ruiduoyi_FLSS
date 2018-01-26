package com.ruiduoyi.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.ruiduoyi.R;
import com.ruiduoyi.utils.AppUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by DengJf on 2017/8/3.
 */

public class ZLRecyclerViewAdapter extends RecyclerView.Adapter<ZLRecyclerViewAdapter.MyViewHolder> {

    private List<Map<String,String>> data;
    private Context mContext;
    private LayoutInflater inflater;

    public ZLRecyclerViewAdapter(Context context, List<Map<String,String>> data){
        this. mContext=context;
        this. data=data;
        inflater=LayoutInflater. from(mContext);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        //final Map<String,String>map=data.get(position);


    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.list_item_zl,parent, false);
        MyViewHolder holder= new MyViewHolder(view);
        return holder;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView zlmc_text,unread_text;
        public CardView zl_btn;
        public FrameLayout unread_bg;
        public MyViewHolder(View view) {
            super(view);
            zlmc_text=(TextView)view.findViewById(R.id.zlmc);
            zl_btn=(CardView)view.findViewById(R.id.cardView);
            unread_text=(TextView)view.findViewById(R.id.unread_text);
            unread_bg=(FrameLayout)view.findViewById(R.id.unread_bg);
        }

    }
}