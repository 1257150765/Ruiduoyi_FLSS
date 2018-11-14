package com.ruiduoyi.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ruiduoyi.R;
import com.ruiduoyi.adapter.YichangfenxiAdapter;
import com.ruiduoyi.adapter.YyfxAdapter;
import com.ruiduoyi.model.NetHelper;
import com.ruiduoyi.utils.AppUtils;
import com.ruiduoyi.view.PopupDialog;
import com.ruiduoyi.view.PopupWindowSpinner;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YcfxActivity extends BaseActivity implements View.OnClickListener{
    private Button cancle_btn,sub_btn;
    private ListView listView,blmsList;
    private Button spinner_1;
    private TextView fsrqText,jxpfText,pfText,clhText,kssjText,jssjText,ksryText,jsryText,bzText,ysText,text_2;
    private String jtbh,lbdm;
    private YyfxAdapter adapter;
    private String wkno="";
    private String keyid;
    private PopupWindowSpinner spinner_list;
    private PopupDialog dialog;
    private PopupDialog dialog_tip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ycfx);
        initView();
        initData();
    }

    private void initView(){
        cancle_btn=(Button)findViewById(R.id.cancle_btn);
        sub_btn=(Button)findViewById(R.id.sub_btn);
        text_2=(TextView)findViewById(R.id.text_2);
        fsrqText=(TextView)findViewById(R.id.fsrq);
        jxpfText=(TextView)findViewById(R.id.jxpf);
        pfText=(TextView)findViewById(R.id.pf);
        clhText=(TextView)findViewById(R.id.clh);
        kssjText=(TextView)findViewById(R.id.kssj);
        jssjText=(TextView)findViewById(R.id.jssj);
        ysText=(TextView)findViewById(R.id.ys);
        bzText=(TextView)findViewById(R.id.bz);
        ksryText=(TextView)findViewById(R.id.ksry);
        jsryText=(TextView)findViewById(R.id.jsry);
        spinner_1=(Button) findViewById(R.id.spinner_1);
        blmsList=(ListView)findViewById(R.id.list_bl);
        spinner_1.setOnClickListener(this);
        listView=(ListView)findViewById(R.id.list_b7);
        cancle_btn.setOnClickListener(this);
        sub_btn.setOnClickListener(this);

        dialog=new PopupDialog(this,400,360);
        dialog.setTitle("提示");
        dialog.getCancle_btn().setVisibility(View.GONE);
        dialog.getOkbtn().setText("确定");


        dialog_tip=new PopupDialog(this,400,360);
        dialog_tip.setTitle("提示");
        dialog_tip.getCancle_btn().setVisibility(View.GONE);
        dialog_tip.getOkbtn().setText("确定");
        dialog_tip.getOkbtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_tip.dismiss();
            }
        });
    }

    private void initData(){
        Intent intent_from=getIntent();
        wkno=intent_from.getStringExtra("wkno");
        sharedPreferences=getSharedPreferences("info",MODE_PRIVATE);
        jtbh=sharedPreferences.getString("jtbh","");
        getYcListInfo();
    }

    private void initListView(JSONArray list){
        try {
            List<Map<String,String>>data=new ArrayList<>();
            for (int i=0;i<list.length();i++){
                Map<String,String>map=new HashMap<>();
                map.put("lab_1",list.getJSONObject(i).getString("v_rq"));
                map.put("lab_2",list.getJSONObject(i).getString("v_mjbh"));
                //map.put("lab_3",list.getJSONObject(i).getString("v_mjmc"));
                map.put("lab_4",list.getJSONObject(i).getString("v_wlmd"));
                map.put("lab_5",list.getJSONObject(i).getString("v_pmgg"));
                map.put("lab_6",list.getJSONObject(i).getString("v_kssj"));
                map.put("lab_7",list.getJSONObject(i).getString("v_jssj"));
                map.put("lab_8",list.getJSONObject(i).getString("v_sjgs"));
                map.put("lab_9",list.getJSONObject(i).getString("v_bzgs"));
                map.put("lab_10",list.getJSONObject(i).getString("v_ksname"));
                map.put("lab_11",list.getJSONObject(i).getString("v_jsname"));
                map.put("lab_12",list.getJSONObject(i).getString("v_zldm"));
                map.put("keyid",list.getJSONObject(i).getString("v_keyid"));
                data.add(map);
            }
            List<TextView>list_text=new ArrayList<>();
            list_text.add(fsrqText);
            list_text.add(text_2);
            list_text.add(jxpfText);
            list_text.add(pfText);
            list_text.add(clhText);
            list_text.add(kssjText);
            list_text.add(jssjText);
            list_text.add(ysText);
            list_text.add(bzText);
            list_text.add(ksryText);
            list_text.add(jsryText);
            YichangfenxiAdapter adapter=new YichangfenxiAdapter(YcfxActivity.this,R.layout.list_item_b7,data,list_text,handler);
            listView.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0x100:
                    JSONArray list_1= (JSONArray) msg.obj;
                    initListView(list_1);
                    break;
                case 0x107:
                    Map<String,String>map_select= (Map<String, String>) msg.obj;
                    String zldm= map_select.get("lab_12");
                    keyid=map_select.get("keyid");
                    getYylb(zldm);
                    break;
                case 0x108:
                    try {
                        final JSONArray list= (JSONArray) msg.obj;
                        final List<String>data=new ArrayList<>();
                        for (int i=0;i<list.length();i++){
                            data.add(list.getJSONObject(i).getString("v_lbdm")+"\t\t"+list.getJSONObject(i).getString("v_lbmc"));
                        }
                        if (data.size()>0){
                            getListData(list,0,data);
                        }
                        spinner_list=new PopupWindowSpinner(YcfxActivity.this,data,R.layout.spinner_list_yyfx,
                                R.id.lab_1,445);
                        spinner_list.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                                AppUtils.sendCountdownReceiver(YcfxActivity.this);
                                getListData(list,position,data);
                                spinner_list.dismiss();
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 0x109:
                    try {
                        JSONArray list1= (JSONArray) msg.obj;
                        List<Map<String,String>>data1=new ArrayList<>();
                        for (int i=0;i<list1.length();i++){
                            Map<String,String>map=new HashMap<>();
                            map.put("lab_1",list1.getJSONObject(i).getString("v_lbdm"));
                            map.put("lab_2",list1.getJSONObject(i).getString("v_lbmc"));
                            data1.add(map);
                        }
                        adapter=new YyfxAdapter(YcfxActivity.this,R.layout.list_item_ycfx,data1);
                        blmsList.setAdapter(adapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 0x110:
                    AppUtils.sendUpdateInfoFragmentReceiver(YcfxActivity.this);
                    dialog.setMessage("提交成功");
                    dialog.setMessageTextColor(Color.BLACK);
                    dialog.getOkbtn().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AppUtils.sendCountdownReceiver(YcfxActivity.this);
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                    getYcListInfo();
                    break;
                case 0x111:
                    dialog.setMessageTextColor(Color.RED);
                    dialog.setMessage("提交失败");
                    dialog.getOkbtn().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AppUtils.sendCountdownReceiver(YcfxActivity.this);
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                    break;
                case 0x102:
                    dialog.setMessageTextColor(Color.RED);
                    dialog.setMessage((String) msg.obj);
                    dialog.getOkbtn().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AppUtils.sendCountdownReceiver(YcfxActivity.this);
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                    break;


            }
        }
    };


    private void getYylb(final String zldm){
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONArray list=NetHelper.getQuerysqlResultJsonArray("Exec PAD_Get_ZlmYywh 'B','"+jtbh+"','"+zldm+"'");
                if (list!=null){
                    Message msg=handler.obtainMessage();
                    msg.what=0x108;
                    msg.obj=list;
                    handler.sendMessage(msg);
                }
            }
        }).start();
    }





    //请求表格数据
    public void getYcListInfo(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONArray list= NetHelper.getQuerysqlResultJsonArray("Exec PAD_Get_YcmInf '"+jtbh+"'");
                Message msg=handler.obtainMessage();
                if(list!=null){
                    msg.what=0x100;
                    msg.obj=list;
                }
                handler.sendMessage(msg);
            }
        }).start();
    }


    private boolean isReady(){
        if (fsrqText.getText().toString().equals("")){
            dialog_tip.setMessageTextColor(Color.RED);
            dialog_tip.setMessage("请先选取异常信息");
            dialog_tip.show();
            return false;
        }

        if (spinner_1.getText().toString().equals("")){
            dialog_tip.setMessageTextColor(Color.RED);
            dialog_tip.setMessage("请先选取原因类别");
            dialog_tip.show();
            return false;
        }
        final List<Map<String,String>>select_data=adapter.getSelectData();
        if (!(select_data.size()>0)){
            dialog_tip.setMessageTextColor(Color.RED);
            dialog_tip.setMessage("请先选取原因描述");
            dialog_tip.show();
            return false;
        }
        return true;
    }


    /**
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        AppUtils.sendCountdownReceiver(YcfxActivity.this);
        switch (v.getId()){
            case R.id.cancle_btn:
                finish();
                break;
            case R.id.spinner_1:
                if (spinner_list!=null){
                    spinner_list.showDownOn(spinner_1);
                }
                break;
            case R.id.sub_btn:
                if (isReady()){
                    final List<Map<String,String>>select_data=adapter.getSelectData();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String select_str="";
                            for (int i=0;i<select_data.size();i++){
                                Map<String,String>uplaod_data=select_data.get(i);
                                select_str=select_str+uplaod_data.get("lab_1")+";";
                            }
                            try {
                                JSONArray list=NetHelper.getQuerysqlResultJsonArray("Exec PAD_Upd_YclInfo " +
                                        "'"+jtbh+"','"+text_2.getText().toString()+"','"+lbdm+"'," + "'"+select_str+"',"+keyid+",0,'"+wkno+"'");
                                if (list!=null){
                                    if (list.length()>0){
                                        if (list.getJSONObject(0).getString("Column1").equals("OK")){
                                            handler.sendEmptyMessage(0x110);
                                        }else {
                                            Message msg=handler.obtainMessage();
                                            msg.obj=list.getJSONObject(0).getString("Column1");
                                            handler.sendMessage(msg);
                                        }
                                    }
                                }else {
                                    handler.sendEmptyMessage(0x111);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
                break;
            default:
                break;
        }
    }


    private void getListData(final JSONArray list, final int position, List<String>data){
        new Thread(new Runnable() {
            @Override
            public void run() {
               try {
                   JSONArray list1=NetHelper.getQuerysqlResultJsonArray("Exec PAD_Get_ZlmYywh 'C','"+jtbh+"','"+
                           list.getJSONObject(position).getString("v_lbdm")+"'");
                   if (list1!=null){
                       Message msg=handler.obtainMessage();
                       msg.what=0x109;
                       msg.obj=list1;
                       handler.sendMessage(msg);
                   }
               } catch (JSONException e) {
                   e.printStackTrace();
               }
            }
        }).start();
        try {
            lbdm=list.getJSONObject(position).getString("v_lbdm");
            spinner_1.setText(data.get(position));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


   

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppUtils.sendUpdateInfoFragmentReceiver(this);
        if (dialog!=null){
            dialog.dismiss();
        }
        if (dialog_tip!=null){
            dialog_tip.dismiss();
        }
    }
}
