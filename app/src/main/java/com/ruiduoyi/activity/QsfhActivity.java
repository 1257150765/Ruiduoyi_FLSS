package com.ruiduoyi.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.ruiduoyi.R;
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

public class QsfhActivity extends BaseActivity {
    private ListView listView_1,listView_2;
    private Handler handler;
    private Button cancle_btn;
    private String jtbh,zzdh,mjbh;
    private PopupDialog dialog;
    private String wkno;
    private boolean isFirst=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qsfh);
        initData();
        initView();
    }


    private void initView(){
        listView_1=(ListView)findViewById(R.id.list_1);
        listView_2=(ListView)findViewById(R.id.list_2);
        cancle_btn=(Button)findViewById(R.id.cancle_btn);
        cancle_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtils.sendCountdownReceiver(QsfhActivity.this);
                finish();
            }
        });
        dialog=new PopupDialog(this,400,360);
        dialog.setTitle("提示");
        dialog.getCancle_btn().setVisibility(View.GONE);
        dialog.getOkbtn().setText("确定");
        dialog.getOkbtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtils.sendCountdownReceiver(QsfhActivity.this);
                dialog.dismiss();
            }
        });
    }


    private void initData(){
        sharedPreferences=getSharedPreferences("info",MODE_PRIVATE);
        jtbh=sharedPreferences.getString("jtbh","");
        zzdh=sharedPreferences.getString("zzdh","");
        mjbh=sharedPreferences.getString("mjbh","");
        Intent intent_from=getIntent();
        wkno=intent_from.getStringExtra("wkno");
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 0x100:
                        try {
                            initList1((JSONArray) msg.obj);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 0x101:
                        initList2((JSONArray) msg.obj);
                        break;
                    case 0x102:
                        initList2(new JSONArray());
                        break;
                    case 0x103://复核成功
                        /*dialog.setMessageTextColor(Color.BLACK);
                        dialog.setMessage("复核成功");
                        dialog.show();*/
                        break;
                    case 0x104://复合失败
                        dialog.setMessageTextColor(Color.RED);
                        dialog.setMessage((String) msg.obj);
                        dialog.show();
                        break;
                }
            }
        };
        getList1Data();
    }

    private void getList1Data(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONArray list= NetHelper.getQuerysqlResultJsonArray("Exec PAD_Get_MoexsFhInf 'A','"+jtbh+"','"+zzdh+"','"+mjbh+"'");
                if (list!=null){
                    Message msg=handler.obtainMessage();
                    msg.what=0x100;
                    msg.obj=list;
                    handler.sendMessage(msg);
                }else {
                    AppUtils.uploadNetworkError("Exec PAD_Get_MoexsFhInf 'A'",jtbh,sharedPreferences.getString("mac",""));
                }
            }
        }).start();
    }

    private void initList1(JSONArray lists) throws JSONException {
        final List<Map<String,String>>data=new ArrayList<>();
        for (int i=0;i<lists.length();i++){
            Map<String,String>map=new HashMap<>();
            map.put("isSelect","0");
            map.put("btn",lists.getJSONObject(i).getString("dxm_cmd"));
            map.put("lab_1",lists.getJSONObject(i).getString("dxm_jtbh"));
            map.put("lab_2",lists.getJSONObject(i).getString("dxm_zzdh"));
            map.put("lab_3",lists.getJSONObject(i).getString("moe_sodh"));
            map.put("lab_4",lists.getJSONObject(i).getString("dxm_wldm"));
            map.put("lab_5",lists.getJSONObject(i).getString("itm_pmgg"));
            map.put("lab_6",lists.getJSONObject(i).getString("dxm_mjbh"));
            map.put("lab_7",lists.getJSONObject(i).getString("itd_xs"));
            map.put("lab_8",lists.getJSONObject(i).getString("dxm_bgxs"));
            map.put("lab_9",lists.getJSONObject(i).getString("dxm_dtsl"));
            map.put("lab_10",lists.getJSONObject(i).getString("dxm_bgrymc"));
            map.put("lab_11",lists.getJSONObject(i).getString("dxm_bgrq"));
            data.add(map);
        }
        EasyArrayAdapter adapter=new EasyArrayAdapter(this,R.layout.list_item_qsfh_1,data) {
            @Override
            public View getEasyView(final int position, View convertView, ViewGroup parent) {
                View view;
                if (convertView!=null){
                    view=convertView;
                }else {
                    view= LayoutInflater.from(getContext()).inflate(R.layout.list_item_qsfh_1,null);
                }
                LinearLayout bg=(LinearLayout)view.findViewById(R.id.bg);
                Button btn=(Button)view.findViewById(R.id.fh_btn);
                TextView lab_1=(TextView)view.findViewById(R.id.lab_1);
                TextView lab_2=(TextView)view.findViewById(R.id.lab_2);
                TextView lab_3=(TextView)view.findViewById(R.id.lab_3);
                TextView lab_4=(TextView)view.findViewById(R.id.lab_4);
                TextView lab_5=(TextView)view.findViewById(R.id.lab_5);
                TextView lab_6=(TextView)view.findViewById(R.id.lab_6);
                TextView lab_7=(TextView)view.findViewById(R.id.lab_7);
                TextView lab_8=(TextView)view.findViewById(R.id.lab_8);
                TextView lab_9=(TextView)view.findViewById(R.id.lab_9);
                TextView lab_10=(TextView)view.findViewById(R.id.lab_10);
                TextView lab_11=(TextView)view.findViewById(R.id.lab_11);
                lab_1.setText(data.get(position).get("lab_1"));
                lab_2.setText(data.get(position).get("lab_2"));
                lab_3.setText(data.get(position).get("lab_3"));
                lab_4.setText(data.get(position).get("lab_4"));
                lab_5.setText(data.get(position).get("lab_5"));
                lab_6.setText(data.get(position).get("lab_6"));
                lab_7.setText(data.get(position).get("lab_7"));
                lab_8.setText(data.get(position).get("lab_8"));
                lab_9.setText(data.get(position).get("lab_9"));
                lab_10.setText(data.get(position).get("lab_10"));
                lab_11.setText(data.get(position).get("lab_11"));
                btn.setText(data.get(position).get("btn"));
                if (data.get(position).get("isSelect").equals("0")){
                    bg.setBackgroundColor(Color.WHITE);
                }else {
                    bg.setBackgroundColor(getResources().getColor(R.color.small));
                }
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AppUtils.sendCountdownReceiver(QsfhActivity.this);
                        final PopupDialog dialog=new PopupDialog(QsfhActivity.this,400,360);
                        dialog.setTitle("提示");
                        dialog.setMessageTextColor(Color.BLACK);
                        dialog.setMessage("确定复核该记录吗？");
                        dialog.getOkbtn().setText("确定");
                        dialog.getCancle_btn().setText("取消");
                        dialog.getOkbtn().setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                fuHeEven(data.get(position).get("lab_2"),data.get(position).get("lab_7"),wkno);
                                dialog.dismiss();
                            }
                        });
                        dialog.getCancle_btn().setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    }
                });
                bg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AppUtils.sendCountdownReceiver(QsfhActivity.this);
                        for (int i=0;i<data.size();i++){
                            if (data.get(i).get("isSelect").equals("1")){
                                data.get(i).put("isSelect","0");
                            }
                        }
                        data.get(position).put("isSelect","1");
                        notifyDataSetChanged();
                        getList2Data(data.get(position).get("lab_1"),data.get(position).get("lab_2"),data.get(position).get("lab_7"));
                    }
                });
                return view;
            }
        };
        if (isFirst){
            if (data.size()>0){
                getList2Data(data.get(0).get("lab_1"),data.get(0).get("lab_2"),data.get(0).get("lab_7"));
                data.get(0).put("isSelect","1");
                adapter.notifyDataSetChanged();
            }
            isFirst=false;
        }

        listView_1.setAdapter(adapter);
    }

    private void getList2Data(final String jtbh, final String zzdh, final String mjbh){
        new Thread(new Runnable() {
            @Override
            public void run() {
                /*List<List<String>>list= NetHelper.getQuerysqlResult("Exec PAD_Get_MoexsFhInf 'B','"+jtbh+"','"+zzdh+"','"+mjbh+"'");
                if (list!=null){
                    if (list.size()>0){
                        if (list.get(0).size()>9){
                            Message msg=handler.obtainMessage();
                            msg.what=0x101;
                            msg.obj=list;
                            handler.sendMessage(msg);
                        }
                    }else {
                        Message msg=handler.obtainMessage();
                        msg.what=0x101;
                        msg.obj=list;
                        handler.sendMessage(msg);
                    }
                }else {
                    AppUtils.uploadNetworkError("Exec PAD_Get_MoexsFhInf 'B'",jtbh,sharedPreferences.getString("mac",""));
                }*/
                JSONArray list= NetHelper.getQuerysqlResultJsonArray("Exec PAD_Get_MoexsFhInf 'B','"+jtbh+"','"+zzdh+"','"+mjbh+"'");
                if (list!=null){
                    Message msg=handler.obtainMessage();
                    msg.what=0x101;
                    msg.obj=list;
                    handler.sendMessage(msg);
                }else {
                    AppUtils.uploadNetworkError("Exec PAD_Get_MoexsFhInf 'B'",jtbh,sharedPreferences.getString("mac",""));
                }
            }
        }).start();
    }

    private void initList2(JSONArray lists){
        try {
            List<Map<String,String>>data=new ArrayList<>();
            for (int i=0;i<lists.length();i++){
                Map<String,String>map=new HashMap<>();
                map.put("lab_1",lists.getJSONObject(i).getString("dxd_dxwz"));
                map.put("lab_2",lists.getJSONObject(i).getString("dxd_yyms"));
                map.put("lab_3",lists.getJSONObject(i).getString("dxd_ztms"));
                map.put("lab_4",lists.getJSONObject(i).getString("dxd_jlrymc"));
                map.put("lab_5",lists.getJSONObject(i).getString("dxd_jlrq"));
                map.put("lab_6",lists.getJSONObject(i).getString("dxd_qrrymc"));
                map.put("lab_7",lists.getJSONObject(i).getString("dxd_qrrq"));
                map.put("lab_8",lists.getJSONObject(i).getString("dxd_fhrymc"));
                map.put("lab_9",lists.getJSONObject(i).getString("dxd_fhrq"));
                map.put("lab_10",lists.getJSONObject(i).getString("dxd_fhcnt"));
                data.add(map);
            }
            SimpleAdapter adapter=new SimpleAdapter(this,data,R.layout.list_item_qsfh_2,
                    new String[]{"lab_1","lab_2","lab_3","lab_4","lab_5","lab_6","lab_7","lab_8","lab_9","lab_10",},
                    new int[]{R.id.lab_1,R.id.lab_2,R.id.lab_3,R.id.lab_4,R.id.lab_5,R.id.lab_6,R.id.lab_7,R.id.lab_8,R.id.lab_9,R.id.lab_10});
            listView_2.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void fuHeEven(final String zzdh, final String mjbh, final String wkno){
        new Thread(new Runnable() {
            @Override
            public void run() {
                /*List<List<String>>list=NetHelper.getQuerysqlResult("Exec PAD_Up_MoexsFh '"+zzdh+"'," +
                        "'"+mjbh+"','"+wkno+"'");
                if (list!=null){
                    if (list.size()>0){
                        if (list.get(0).size()>0){
                            if (list.get(0).get(0).equals("OK")){
                                handler.sendEmptyMessage(0x103);
                            }else {
                                Message msg=handler.obtainMessage();
                                msg.what=0x104;
                                msg.obj=list.get(0).get(0);
                                handler.sendMessage(msg);
                            }
                        }
                    }
                }else {
                    AppUtils.uploadNetworkError("Exec PAD_Up_MoexsFh",jtbh,"");
                }
                handler.sendEmptyMessage(0x102);
                getList1Data();*/
                try {
                    JSONArray list=NetHelper.getQuerysqlResultJsonArray("Exec PAD_Up_MoexsFh '"+zzdh+"'," +
                            "'"+mjbh+"','"+wkno+"'");
                    if (list!=null){
                        if (list.length()>0){
                            if (list.getJSONObject(0).getString("Column1").equals("OK")){
                                handler.sendEmptyMessage(0x103);
                            }else {
                                Message msg=handler.obtainMessage();
                                msg.what=0x104;
                                msg.obj=list.getJSONObject(0).getString("Column1");
                                handler.sendMessage(msg);
                            }
                        }
                    }else {
                        AppUtils.uploadNetworkError("Exec PAD_Up_MoexsFh",jtbh,"");
                    }
                    handler.sendEmptyMessage(0x102);
                    getList1Data();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
