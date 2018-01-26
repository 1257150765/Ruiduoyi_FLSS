package com.ruiduoyi.adapter;

/**
 * Created by DengJf on 2017/7/31.
 */

import java.util.List;
import java.util.Map;


import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.ruiduoyi.R;
import com.ruiduoyi.utils.AppUtils;

public class QsRecyclerAdapter extends RecyclerView.Adapter<QsRecyclerAdapter.MyViewHolder> {

    private List<Map<String,String>> data;
    private Context mContext;
    private LayoutInflater inflater;

    public QsRecyclerAdapter(Context context, List<Map<String,String>> data){
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
        final Map<String,String>map=data.get(position);
        holder.wz.setText(map.get("wz"));
        //0为不堵塞 绿色
        //1为堵塞  红色
        //2为选中  灰色
       switch (map.get("isSelect")){
            case "0":
                holder.select_btn.setCardBackgroundColor(mContext.getResources().getColor(R.color.color_2));
                break;
            case "1":
                holder.select_btn.setCardBackgroundColor(Color.RED);
                break;
            case "2":
                holder.select_btn.setCardBackgroundColor(mContext.getResources().getColor(R.color.color_9));
                break;
        }
        holder.bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtils.sendCountdownReceiver(mContext);
                if (map.get("isSelect").equals("0")){
                    map.put("isSelect","2");
                    holder.select_btn.setCardBackgroundColor(mContext.getResources().getColor(R.color.color_9));
                }else if (map.get("isSelect").equals("2")){
                    map.put("isSelect","0");
                    holder.select_btn.setCardBackgroundColor(mContext.getResources().getColor(R.color.color_2));
                }
            }
        });
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.list_item_jtjbhbg_xz,parent, false);
        MyViewHolder holder= new MyViewHolder(view);
        return holder;
    }

    class MyViewHolder extends ViewHolder{

        TextView wz;
        CardView select_btn;
        FrameLayout bg;
        public MyViewHolder(View view) {
            super(view);
            wz=(TextView)view.findViewById(R.id.wz_text);
            select_btn=(CardView)view.findViewById(R.id.select_btn);
            bg=(FrameLayout)view.findViewById(R.id.bg);

        }

    }
}
