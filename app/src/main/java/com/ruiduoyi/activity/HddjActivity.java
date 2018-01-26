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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ruiduoyi.R;
import com.ruiduoyi.adapter.EasyArrayAdapter;
import com.ruiduoyi.adapter.WorkOrderAdapter;
import com.ruiduoyi.model.NetHelper;
import com.ruiduoyi.utils.AppUtils;
import com.ruiduoyi.view.PopupDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HddjActivity extends BaseActivity implements View.OnClickListener{
    private Button cancle_btn,btn_1,btn_2,btn_3,btn_4,btn_5,btn_6,btn_7,btn_8,btn_9,btn_0,btn_clear,btn_submit;
    private ListView listView_gd,listView_hd;
    private TextView zzdh_text,gddh_text,scph_text,cpbh_text,pmgg_text,jhsl_text,ksdu_text,
            jsds_text,sub_text;
    private Handler handler;
    private String sub_num;
    private Animation anim;
    private String wkno;
    private PopupDialog dialog,delTipDialog;
    private EasyArrayAdapter adapter_mx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hddj);
        initData();
        initView();
    }

    private void initView(){
        gddh_text=(TextView)findViewById(R.id.gddh);
        zzdh_text=(TextView)findViewById(R.id.zzdh);
        scph_text=(TextView)findViewById(R.id.scph);
        cpbh_text=(TextView)findViewById(R.id.cpbh);
        pmgg_text=(TextView)findViewById(R.id.pmgg);
        jhsl_text=(TextView)findViewById(R.id.jhsl);
        ksdu_text=(TextView)findViewById(R.id.ksds);
        jsds_text=(TextView)findViewById(R.id.jsds);
        listView_gd=(ListView)findViewById(R.id.list_1);
        listView_hd=(ListView)findViewById(R.id.list_2);


        btn_0=(Button)findViewById(R.id.btn_0);
        btn_1=(Button)findViewById(R.id.btn_1);
        btn_2=(Button)findViewById(R.id.btn_2);
        btn_3=(Button)findViewById(R.id.btn_3);
        btn_4=(Button)findViewById(R.id.btn_4);
        btn_5=(Button)findViewById(R.id.btn_5);
        btn_6=(Button)findViewById(R.id.btn_6);
        btn_7=(Button)findViewById(R.id.btn_7);
        btn_8=(Button)findViewById(R.id.btn_8);
        btn_9=(Button)findViewById(R.id.btn_9);
        btn_submit=(Button)findViewById(R.id.btn_submit);
        btn_clear=(Button)findViewById(R.id.btn_clear);
        cancle_btn=(Button)findViewById(R.id.cancle_btn);


        cancle_btn.setOnClickListener(this);
        btn_0.setOnClickListener(this);
        btn_1.setOnClickListener(this);
        btn_2.setOnClickListener(this);
        btn_3.setOnClickListener(this);
        btn_4.setOnClickListener(this);
        btn_5.setOnClickListener(this);
        btn_6.setOnClickListener(this);
        btn_7.setOnClickListener(this);
        btn_8.setOnClickListener(this);
        btn_9.setOnClickListener(this);
        btn_submit.setOnClickListener(this);
        btn_clear.setOnClickListener(this);
        ksdu_text.setOnClickListener(this);
        jsds_text.setOnClickListener(this);

        sub_text=ksdu_text;

        dialog=new PopupDialog(this,400,360);
        dialog.setTitle("提示");
        dialog.getCancle_btn().setVisibility(View.GONE);
        dialog.getOkbtn().setText("确定");
        dialog.setCancelable(true);
        dialog.getOkbtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtils.sendCountdownReceiver(HddjActivity.this);
                dialog.dismiss();
            }
        });
        delTipDialog=new PopupDialog(this,400,360);
        delTipDialog.setTitle("提示");
        delTipDialog.getCancle_btn().setText("取消");
        delTipDialog.getOkbtn().setText("确定");

    }


    private void initData(){
        anim= AnimationUtils.loadAnimation(this,R.anim.sub_num_anim);
        Intent intent_from=getIntent();
        wkno=intent_from.getStringExtra("wkno");


        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 0x100:
                        JSONArray list= (JSONArray) msg.obj;
                        initListView(list);
                        break;
                    case 0x101:
                        initHdmxListView((JSONArray) msg.obj);
                        break;
                    case 0x102:
                        try {
                            JSONArray list_zh= (JSONArray) msg.obj;
                            if (!list_zh.getJSONObject(0).getString("v_ksds").equals("0")){
                                ksdu_text.setText(list_zh.getJSONObject(0).getString("v_ksds"));
                                jsds_text.setText(list_zh.getJSONObject(0).getString("v_jsds"));
                                ksdu_text.setEnabled(false);
                                ksdu_text.setBackgroundColor(Color.WHITE);
                                jsds_text.setBackgroundColor(getResources().getColor(R.color.small));
                                sub_text=jsds_text;
                            }else {
                                ksdu_text.setText(list_zh.getJSONObject(0).getString("v_ksds"));
                                jsds_text.setText(list_zh.getJSONObject(0).getString("v_jsds"));
                                ksdu_text.setEnabled(true);
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                        break;
                    case 0x103:
                        getHdmxData(zzdh_text.getText().toString(),"A",0x101);
                        /*dialog.setMessageTextColor(Color.BLACK);
                        dialog.setMessage("提交成功");
                        dialog.show();*/
                        break;
                    case 0x104:
                        dialog.setMessageTextColor(Color.RED);
                        dialog.setMessage((String) msg.obj);
                        dialog.show();
                        break;
                    case 0x105:
                        getHdmxData(zzdh_text.getText().toString(),"A",0x101);
                        /*dialog.setMessageTextColor(Color.BLACK);
                        dialog.setMessage("删除成功");
                        dialog.show();*/
                        break;
                }
            }
        };

        getNetData();
    }


    private void getNetData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                /*List<List<String>>list= NetHelper.getQuerysqlResult("Exec PAD_Get_MoeDet 'A','"+jtbh+"'");
                if (list!=null){
                    if (list.size()>0){
                        if (list.get(0).size()>13){
                            Message msg=handler.obtainMessage();
                            msg.what=0x100;
                            msg.obj=list;
                            handler.sendMessage(msg);
                        }
                    }
                }else {
                    AppUtils.uploadNetworkError("Exec PAD_Get_MoeDet",jtbh,sharedPreferences.getString("mac",""));
                }*/
                JSONArray list= NetHelper.getQuerysqlResultJsonArray("Exec PAD_Get_MoeDet 'A','"+jtbh+"'");
                if (list!=null){
                    if (list.length()>0){
                        Message msg=handler.obtainMessage();
                        msg.what=0x100;
                        msg.obj=list;
                        handler.sendMessage(msg);
                    }
                }else {
                    AppUtils.uploadNetworkError("Exec PAD_Get_MoeDet",jtbh,sharedPreferences.getString("mac",""));
                }
            }
        }).start();
    }


    private void  initListView(JSONArray lists){
        try {
            final List<Map<String,String>>data=new ArrayList<>();
            for (int i=0;i<lists.length();i++){
                JSONObject item=lists.getJSONObject(i);
                Map<String,String>map=new HashMap<>();
                map.put("moeid",item.getString("v_moeid"));
                map.put("scrq",item.getString("v_scrq"));
                map.put("scxh",item.getString("v_scxh"));
                map.put("zzdh",item.getString("v_zzdh"));
                map.put("sodh",item.getString("v_sodh"));
                map.put("ph",item.getString("v_ph"));
                map.put("mjbh",item.getString("v_mjbh"));
                map.put("mjmc",item.getString("v_mjmc"));
                map.put("wldm",item.getString("v_wldm"));
                map.put("pmgg",item.getString("v_pmgg"));
                map.put("wgrq",item.getString("v_wgrq"));
                map.put("scsl",item.getString("v_scsl"));
                map.put("lpsl",item.getString("v_lpsl"));
                map.put("ztbz",item.getString("v_ztbz"));
                data.add(map);
            }
            EasyArrayAdapter adapter=new EasyArrayAdapter(HddjActivity.this,R.layout.list_item_hddj_gd,data) {
                @Override
                public View getEasyView(int position, View convertView, ViewGroup parent) {
                    View view;
                    if (convertView!=null){
                        view=convertView;
                    }else {
                        view= LayoutInflater.from(HddjActivity.this).inflate(R.layout.list_item_hddj_gd,null);
                    }
                    final Map<String,String>map=data.get(position);
                    final TextView lab_4=(TextView)view.findViewById(R.id.lab_4);
                    TextView lab_5=(TextView)view.findViewById(R.id.lab_5);
                    TextView lab_6=(TextView)view.findViewById(R.id.lab_6);
                    TextView lab_7=(TextView)view.findViewById(R.id.lab_7);
                    TextView lab_8=(TextView)view.findViewById(R.id.lab_8);
                    TextView lab_9=(TextView)view.findViewById(R.id.lab_9);
                    TextView lab_10=(TextView)view.findViewById(R.id.lab_10);
                    TextView lab_11=(TextView)view.findViewById(R.id.lab_11);
                    TextView lab_12=(TextView)view.findViewById(R.id.lab_12);
                    TextView lab_13=(TextView)view.findViewById(R.id.lab_13);
                    TextView lab_14=(TextView)view.findViewById(R.id.lab_14);
                    TextView lab_15=(TextView)view.findViewById(R.id.lab_15);
                    LinearLayout backgroup=(LinearLayout)view.findViewById(R.id.bg);
                    final Button select_btn=(Button)view.findViewById(R.id.select_btn);
                    final int index=position+1;
                    select_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AppUtils.sendCountdownReceiver(getContext());
                            zzdh_text.setText(map.get("zzdh"));
                            gddh_text.setText(map.get("sodh"));
                            scph_text.setText(map.get("ph"));
                            cpbh_text.setText(map.get("wldm"));
                            pmgg_text.setText(map.get("pmgg"));
                            jhsl_text.setText(map.get("scsl"));
                            getHdmxData(map.get("zzdh"),"A",0x101);
                            getHdmxData(map.get("zzdh"),"B",0x102);

                        }
                    });
                    lab_4.setText(map.get("scrq"));
                    lab_5.setText(map.get("scxh"));
                    lab_6.setText(map.get("zzdh"));
                    lab_7.setText(map.get("sodh"));
                    lab_8.setText(map.get("ph"));
                    lab_9.setText(map.get("mjbh"));
                    lab_10.setText(map.get("mjmc"));
                    lab_11.setText(map.get("wldm"));
                    lab_12.setText(map.get("pmgg"));
                    lab_13.setText(map.get("wgrq"));
                    lab_14.setText(map.get("scsl"));
                    lab_15.setText(map.get("lpsl"));

                    return view;
                }
            };
            listView_gd.setAdapter(adapter);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    private void getHdmxData(final String zzdh, final String type, final int what){
        new Thread(new Runnable() {
            @Override
            public void run() {
               /* List<List<String>>list=NetHelper.getQuerysqlResult("Exec PAD_Get_DllInf '"+type+"','"+zzdh+"'");
                if (list!=null){
                    if (list.size()>0){
                            Message msg=handler.obtainMessage();
                            msg.what=what;
                            msg.obj=list;
                            handler.sendMessage(msg);
                    }
                    if (what==0x101){
                        Message msg=handler.obtainMessage();
                        msg.what=what;
                        msg.obj=list;
                        handler.sendMessage(msg);
                    }
                }*/
                JSONArray list=NetHelper.getQuerysqlResultJsonArray("Exec PAD_Get_DllInf '"+type+"','"+zzdh+"'");
                if (list!=null){
                    if (list.length()>0){
                        Message msg=handler.obtainMessage();
                        msg.what=what;
                        msg.obj=list;
                        handler.sendMessage(msg);
                    }
                    if (what==0x101){
                        Message msg=handler.obtainMessage();
                        msg.what=what;
                        msg.obj=list;
                        handler.sendMessage(msg);
                    }
                }
            }
        }).start();
    }


    private void initHdmxListView(JSONArray lists){
        try {
            final List<Map<String,String>>data=new ArrayList<>();
            for (int i=0;i<lists.length();i++){
                Map<String,String>map=new HashMap<>();
                map.put("lab_1",lists.getJSONObject(i).getString("dll_jtbh"));//机台编号
                map.put("lab_2",lists.getJSONObject(i).getString("dll_ksds"));//开始度数
                map.put("lab_3",lists.getJSONObject(i).getString("dll_jsds"));//终止度数
                map.put("zzdh",lists.getJSONObject(i).getString("dll_zzdh"));//制造单号
                map.put("lab_4",lists.getJSONObject(i).getString("dll_dlds"));//耗电度数
                map.put("id",lists.getJSONObject(i).getString("dll_id"));//id
                map.put("lab_5",lists.getJSONObject(i).getString("dll_ksrymc"));
                map.put("lab_6",lists.getJSONObject(i).getString("dll_ksrq"));
                data.add(map);
            }
            adapter_mx=new EasyArrayAdapter(this,R.layout.list_item_hddj_mx,data) {
                @Override
                public View getEasyView(int position, View convertView, ViewGroup parent) {
                    View view;
                    if (convertView!=null){
                        view=convertView;
                    }else {
                        view=LayoutInflater.from(HddjActivity.this).inflate(R.layout.list_item_hddj_mx,null);
                    }
                    Map<String,String>map=data.get(position);
                    TextView lab_1=(TextView)view.findViewById(R.id.lab_1);
                    final TextView lab_2=(TextView)view.findViewById(R.id.lab_2);
                    final TextView lab_3=(TextView)view.findViewById(R.id.lab_3);
                    TextView lab_4=(TextView)view.findViewById(R.id.lab_4);
                    TextView lab_5=(TextView)view.findViewById(R.id.lab_5);
                    TextView lab_6=(TextView)view.findViewById(R.id.lab_6);
                    Button del_btn=(Button)view.findViewById(R.id.del_btn);
                    lab_1.setText(map.get("lab_1"));
                    lab_2.setText(map.get("lab_2"));
                    lab_3.setText(map.get("lab_3"));
                    lab_4.setText(map.get("lab_4"));
                    lab_5.setText(map.get("lab_5"));
                    lab_6.setText(map.get("lab_6"));

                    del_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            AppUtils.sendCountdownReceiver(HddjActivity.this);
                            delData(lab_2.getText().toString(),lab_3.getText().toString());
                        }
                    });
                    return view;
                }
            };
            listView_hd.setAdapter(adapter_mx);
        }catch (JSONException  e){
            e.printStackTrace();
        }

    }


    private boolean isReady(){
        if (zzdh_text.getText().toString().equals("")){
            dialog.setMessageTextColor(Color.RED);
            dialog.setMessage("请先选择工单信息");
            dialog.show();
            return false;
        }
        if (ksdu_text.getText().toString().equals("")|jsds_text.getText().toString().equals("")){
            dialog.setMessageTextColor(Color.RED);
            dialog.setMessage("请先输入开始和结束度数");
            dialog.show();
            return false;
        }
        if ((Integer.parseInt(jsds_text.getText().toString())<=Integer.parseInt(ksdu_text.getText().toString()))
                &&!jsds_text.getText().toString().equals("0")){
            dialog.setMessageTextColor(Color.RED);
            dialog.setMessage("结束度数一定要大于开始度数");
            dialog.show();
            return false;
        }
        return true;
    }

    private void delData(final String ksds, final String jsds){
        delTipDialog.setMessage("确定要删除这条记录？\n"+"开始度数:"+ksds+"\n结束度数:"+jsds);
        delTipDialog.getOkbtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delTipDialog.dismiss();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        /*List<List<String>>list=NetHelper.getQuerysqlResult("Exec  PAD_Up_Dlllist " +
                                "  'B','"+zzdh_text.getText().toString()+"','"+jtbh+"',"+ksds+"," +
                                jsds+",'"+wkno+"'");
                        if (list!=null){
                            if (list.size()>0){
                                if (list.get(0).size()>0){
                                    if (list.get(0).get(0).equals("OK")){
                                        handler.sendEmptyMessage(0x105);
                                    }else {
                                        Message msg=handler.obtainMessage();
                                        msg.what=0x104;
                                        msg.obj=list.get(0).get(0);
                                        handler.sendMessage(msg);
                                    }
                                }
                            }
                        }else {
                            AppUtils.uploadNetworkError("Exec  PAD_Up_Dlllist 'A'",jtbh,sharedPreferences.getString("mac",""));
                        }*/
                        try {
                            JSONArray list=NetHelper.getQuerysqlResultJsonArray("Exec  PAD_Up_Dlllist " +
                                    "  'B','"+zzdh_text.getText().toString()+"','"+jtbh+"',"+ksds+"," +
                                    jsds+",'"+wkno+"'");
                            if (list!=null){
                                if (list.length()>0){
                                    if (list.getJSONObject(0).getString("Column1").equals("OK")){
                                        handler.sendEmptyMessage(0x105);
                                    }else {
                                        Message msg=handler.obtainMessage();
                                        msg.what=0x104;
                                        msg.obj=list.getJSONObject(0).getString("Column1");
                                        handler.sendMessage(msg);
                                    }
                                }
                            }else {
                                AppUtils.uploadNetworkError("Exec  PAD_Up_Dlllist 'A'",jtbh,sharedPreferences.getString("mac",""));
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
        delTipDialog.show();

    }


    private void addData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
               /* List<List<String>>list=NetHelper.getQuerysqlResult("Exec  PAD_Up_Dlllist " +
                        "  'A','"+zzdh_text.getText().toString()+"','"+jtbh+"',"+ksdu_text.getText().toString()+"," +
                        jsds_text.getText().toString()+",'"+wkno+"'");
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
                    AppUtils.uploadNetworkError("Exec  PAD_Up_Dlllist 'A'",jtbh,sharedPreferences.getString("mac",""));
                }*/
                try {
                    JSONArray list=NetHelper.getQuerysqlResultJsonArray("Exec  PAD_Up_Dlllist " +
                            "  'A','"+zzdh_text.getText().toString()+"','"+jtbh+"',"+ksdu_text.getText().toString()+"," +
                            jsds_text.getText().toString()+",'"+wkno+"'");
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
                        AppUtils.uploadNetworkError("Exec  PAD_Up_Dlllist 'A'",jtbh,sharedPreferences.getString("mac",""));
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }).start();
    }


    @Override
    public void onClick(View v) {
        AppUtils.sendCountdownReceiver(HddjActivity.this);
        switch (v.getId()){
            case R.id.ksds:
                ksdu_text.setBackgroundColor(getResources().getColor(R.color.small));
                jsds_text.setBackgroundColor(Color.WHITE);
                sub_text=ksdu_text;
                break;
            case R.id.jsds:
                jsds_text.setBackgroundColor(getResources().getColor(R.color.small));
                ksdu_text.setBackgroundColor(Color.WHITE);
                sub_text=jsds_text;
                break;
            case R.id.cancle_btn:
                finish();
                break;
            case R.id.btn_0:
                sub_text.startAnimation(anim);
                sub_num=sub_text.getText().toString();
                if((!sub_num.equals("0"))&&(!sub_num.equals("-"))){
                    sub_text.setText(sub_num+"0");
                }
                break;
            case R.id.btn_1:
                sub_text.startAnimation(anim);
                sub_num=sub_text.getText().toString();
                if(!sub_num.equals("0")){
                    sub_text.setText(sub_num+"1");
                }else {
                    sub_text.setText("1");
                }
                break;
            case R.id.btn_2:
                sub_text.startAnimation(anim);
                sub_num=sub_text.getText().toString();
                if(!sub_num.equals("0")){
                    sub_text.setText(sub_num+"2");
                }else {
                    sub_text.setText("2");
                }
                break;
            case R.id.btn_3:
                sub_text.startAnimation(anim);
                sub_num=sub_text.getText().toString();
                if(!sub_num.equals("0")){
                    sub_text.setText(sub_num+"3");
                }else {
                    sub_text.setText("3");
                }
                break;
            case R.id.btn_4:
                sub_text.startAnimation(anim);
                sub_num=sub_text.getText().toString();
                if(!sub_num.equals("0")){
                    sub_text.setText(sub_num+"4");
                }else {
                    sub_text.setText("4");
                }
                break;
            case R.id.btn_5:
                sub_text.startAnimation(anim);
                sub_num=sub_text.getText().toString();
                if(!sub_num.equals("0")){
                    sub_text.setText(sub_num+"5");
                }else {
                    sub_text.setText("5");
                }
                break;
            case R.id.btn_6:
                sub_text.startAnimation(anim);
                sub_num=sub_text.getText().toString();
                if(!sub_num.equals("0")){
                    sub_text.setText(sub_num+"6");
                }else {
                    sub_text.setText("6");
                }
                break;
            case R.id.btn_7:
                sub_text.startAnimation(anim);
                sub_num=sub_text.getText().toString();
                if(!sub_num.equals("0")){
                    sub_text.setText(sub_num+"7");
                }else {
                    sub_text.setText("7");
                }
                break;
            case R.id.btn_8:
                sub_text.startAnimation(anim);
                sub_num=sub_text.getText().toString();
                if(!sub_num.equals("0")){
                    sub_text.setText(sub_num+"8");
                }else {
                    sub_text.setText("8");
                }
                break;
            case R.id.btn_9:
                sub_text.startAnimation(anim);
                sub_num=sub_text.getText().toString();
                if(!sub_num.equals("0")){
                    sub_text.setText(sub_num+"9");
                }else {
                    sub_text.setText("9");
                }
                break;
            case R.id.btn_submit:
               if (isReady()){
                    addData();
                }
                break;
            case R.id.btn_clear:
                sub_text.startAnimation(anim);
                sub_text.setText("0");
                break;
            default:
                break;
        }
    }
}
