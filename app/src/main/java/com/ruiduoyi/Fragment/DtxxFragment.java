package com.ruiduoyi.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.ruiduoyi.R;
import com.ruiduoyi.activity.Jtjqsbg2Activity;
import com.ruiduoyi.adapter.EasyArrayAdapter;
import com.ruiduoyi.model.NetHelper;
import com.ruiduoyi.utils.AppUtils;
import com.ruiduoyi.view.PopupDialog;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DtxxFragment extends Fragment {
    private TextView mjbh_text,mjmc_text,mjxs_text,cpxs_text,pmgg_text;
    private String mjbh,mjqs,mjmc,cpqs,jtbh,zzdh,wkno,pmgg;
    private SharedPreferences sharedPreferences;
    private ListView listView;
    private Handler handler;
    private EasyArrayAdapter easyAdapter;
    private List<Map<String,String>>data_dt;
    private PopupDialog dialog;
    private Jtjqsbg2Activity activity;
    public DtxxFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_dtxx, container, false);
        initData();
        initView(view);
        return view;
    }


    private void initView(View view){
        mjbh_text=(TextView)view.findViewById(R.id.mjbh_text);
        mjmc_text=(TextView)view.findViewById(R.id.mjmc_text);
        mjxs_text=(TextView)view.findViewById(R.id.mjxs_text);
        cpxs_text=(TextView)view.findViewById(R.id.cpxs_text);
        listView=(ListView)view.findViewById(R.id.list_2);
        pmgg_text=(TextView)view.findViewById(R.id.pmgg_text);


        mjbh_text.setText(mjbh);
        mjmc_text.setText(mjmc);
        mjxs_text.setText(mjqs);
        cpxs_text.setText(cpqs);
        pmgg_text.setText(pmgg);

        if (!mjqs.equals(cpqs)){
            cpxs_text.setBackgroundColor(getResources().getColor(R.color.small));
        }else {
            cpxs_text.setBackgroundColor(Color.WHITE);
        }

        dialog=new PopupDialog(getActivity(),400,360);
        dialog.setTitle("提示");
        dialog.getCancle_btn().setVisibility(View.GONE);
        dialog.getOkbtn().setText("确定");
        dialog.getOkbtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                AppUtils.sendCountdownReceiver(getContext());
            }
        });
    }



    private void initData(){
        Intent intent_from=getActivity().getIntent();
        mjbh=intent_from.getStringExtra("mjbh");
        mjmc=intent_from.getStringExtra("mjmc");
        mjqs=intent_from.getStringExtra("mjqs");
        cpqs=intent_from.getStringExtra("cpqs");
        zzdh=intent_from.getStringExtra("zzdh");
        wkno=intent_from.getStringExtra("wkno");
        pmgg=intent_from.getStringExtra("pmgg");

        sharedPreferences=getContext().getSharedPreferences("info",Context.MODE_PRIVATE);
        jtbh=sharedPreferences.getString("jtbh","");

        activity= (Jtjqsbg2Activity) getActivity();

        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 0x104:
                        try {
                            JSONArray list_dt= (JSONArray) msg.obj;
                            data_dt=new ArrayList<>();
                            for (int i=0;i<list_dt.length();i++){
                                Map<String,String>map=new HashMap<>();
                                map.put("lab_1",list_dt.getJSONObject(i).getString("dxd_dxwz"));
                                map.put("lab_2",list_dt.getJSONObject(i).getString("dxd_yyms"));
                                map.put("lab_3",list_dt.getJSONObject(i).getString("dxd_jlrymc"));
                                map.put("lab_4",list_dt.getJSONObject(i).getString("dxd_jlrq"));
                                map.put("lab_5",list_dt.getJSONObject(i).getString("dxd_qrrymc"));
                                map.put("lab_6",list_dt.getJSONObject(i).getString("dxd_qrrq"));
                                map.put("lab_0",list_dt.getJSONObject(i).getString("dxd_sn"));
                                data_dt.add(map);
                            }
                            initDutouList(data_dt);
                            getGongdanData();
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                        break;
                    case 0x105:
                        getGongdanData();
                        activity.getDutouListData(zzdh,mjqs);
                        Button button= (Button) msg.obj;
                        //button.setText("已修复");
                        /*dialog.setMessageTextColor(Color.BLACK);
                        dialog.setMessage("提交成功");
                        dialog.show();*/
                        break;
                    case 0x106:
                        dialog.setMessageTextColor(Color.RED);
                        dialog.setMessage((String) msg.obj);
                        dialog.show();
                        break;
                    case 0x109:
                        try {
                            JSONArray list_gongdan= (JSONArray) msg.obj;
                            for (int i=0;i<list_gongdan.length();i++){
                                if (list_gongdan.getJSONObject(i).getString("v_zzdh").equals(zzdh)){
                                    cpxs_text.setText(list_gongdan.getJSONObject(i).getString("v_moexs"));
                                }
                            }
                            if (!mjxs_text.getText().equals(cpxs_text.getText().toString())){
                                cpxs_text.setBackgroundColor(getResources().getColor(R.color.small));
                            }else {
                                cpxs_text.setBackgroundColor(Color.WHITE);
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                }
            }
        };


        //activity.getDutouListData(zzdh);
    }


    //初始化堵头信息表
    private void initDutouList(final List<Map<String,String>> data) {
        easyAdapter = new EasyArrayAdapter(getContext(), R.layout.list_item_dutou_2, data) {
            @Override
            public View getEasyView(final int position, View convertView, ViewGroup parent) {
                View view;
                if (convertView != null) {
                    view = convertView;
                } else {
                    view = LayoutInflater.from(getContext()).inflate(R.layout.list_item_dutou_2, null);
                }
                final Map<String, String> map = data.get(position);
                TextView lab_0 = (TextView)view.findViewById(R.id.lab_0);
                TextView lab_1 = (TextView) view.findViewById(R.id.lab_1);
                TextView lab_2 = (TextView) view.findViewById(R.id.lab_2);
                TextView lab_3 = (TextView) view.findViewById(R.id.lab_3);
                TextView lab_4 = (TextView) view.findViewById(R.id.lab_4);
                TextView lab_5 = (TextView) view.findViewById(R.id.lab_5);
                TextView lab_6 = (TextView) view.findViewById(R.id.lab_6);
                final Button upload = (Button) view.findViewById(R.id.xiufu);
                lab_0.setText(map.get("lab_0"));
                lab_1.setText(map.get("lab_1"));
                lab_2.setText(map.get("lab_2"));
                lab_3.setText(map.get("lab_3"));
                lab_4.setText(map.get("lab_4"));
                lab_5.setText(map.get("lab_5"));
                lab_6.setText(map.get("lab_6"));
                if (map.get("lab_5").equals("") | map.get("lab_6").equals("")) {
                    upload.setText("修复");
                } else {
                    upload.setEnabled(false);
                }
                upload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AppUtils.sendCountdownReceiver(getContext());
                        upload.setText("提交中");
                        upload.setEnabled(false);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    JSONArray list = NetHelper.getQuerysqlResultJsonArray("Exec PAD_Upd_MoeJtXs  'C','" + zzdh + "',''," +
                                            "'" + map.get("lab_1") + "',0,0,'','" + wkno + "'");
                                    if (list != null) {
                                        if (list.length() > 0) {
                                            if (list.getJSONObject(0).getString("Column1").equals("OK")) {
                                                Message msg = handler.obtainMessage();
                                                msg.what = 0x105;
                                                msg.arg1 = position;
                                                msg.obj = upload;
                                                handler.sendMessage(msg);
                                            } else {
                                                Message msg = handler.obtainMessage();
                                                msg.what = 0x106;
                                                msg.arg1 = position;
                                                msg.obj = list.getJSONObject(0).getString("Column1");
                                                handler.sendMessage(msg);
                                            }
                                        }
                                    } else {
                                        AppUtils.uploadNetworkError("Exec PAD_Upd_MoeJtXs  'C'", jtbh, sharedPreferences.getString("mac", ""));
                                    }
                                }catch (JSONException e){
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                });
                return view;
            }
        };

        listView.setAdapter(easyAdapter);
    }





    private void getGongdanData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                //工单信息表
                JSONArray list= NetHelper.getQuerysqlResultJsonArray("Exec PAD_Get_MoeDet 'A','"+jtbh+"'");
                if (list!=null){
                    if (list.length()>0){
                        Message msg=handler.obtainMessage();
                        msg.what=0x109;
                        msg.obj=list;
                        handler.sendMessage(msg);
                    }
                }else {
                    AppUtils.uploadNetworkError("Exec PAD_Get_MoeDet",jtbh,sharedPreferences.getString("mac",""));
                }

            }
        }).start();
    }


    public Handler getHandler(){
        return handler;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
