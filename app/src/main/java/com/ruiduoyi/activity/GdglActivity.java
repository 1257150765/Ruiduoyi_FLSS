package com.ruiduoyi.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.ruiduoyi.R;
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

public class GdglActivity extends BaseActivity implements View.OnClickListener{
    private ListView listView;
    private Button cancle_btn;
    private String jtbh,wkno;
    private PopupDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gdgl);
        initView();
        initData();
    }

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0x100:
                    JSONArray list= (JSONArray) msg.obj;
                    initListView(list);
                    break;
                case 0x101:
                    getNetData();
                    AppUtils.sendUpdateInfoFragmentReceiver(GdglActivity.this);
                    String result=(String) msg.obj;
                    if (!result.equals("OK")){
                        dialog.setMessage(result);
                        dialog.show();
                    }
                    break;
            }
        }
    };

    private void initView(){
        dialog=new PopupDialog(this,400,360);
        dialog.setBackgrounpColor(getResources().getColor(R.color.lable));
        dialog.getCancle_btn().setVisibility(View.GONE);
        dialog.getOkbtn().setText("确定");
        dialog.getOkbtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        listView=(ListView)findViewById(R.id.list_b3);
        cancle_btn=(Button)findViewById(R.id.cancle_btn);
        cancle_btn.setOnClickListener(this);
    }


    private void initData(){
        sharedPreferences=getSharedPreferences("info",MODE_PRIVATE);
        jtbh=sharedPreferences.getString("jtbh","");
        Intent intent_from=getIntent();
        wkno=intent_from.getStringExtra("wkno");
        getNetData();
    }

    private void  initListView(JSONArray lists){
       try {
           List<Map<String,String>>data=new ArrayList<>();
           for (int i=0;i<lists.length();i++){
               JSONObject item=lists.getJSONObject(i);
               Map<String,String>map=new HashMap<>();
               map.put("moeid",item.getString("v_moeid"));
               map.put("scrq",item.getString("v_scrq"));
               map.put("scxh",item.getString("v_scxh"));
               map.put("zzdh",item.getString("v_zzdh"));
               map.put("sodh",item.getString("v_sodh"));
               map.put("wgrq",item.getString("v_wgrq"));
               map.put("mjbh",item.getString("v_mjbh"));
               //map.put("mjmc",item.getString("v_mjmc"));
               map.put("wldm",item.getString("v_wldm"));
               map.put("pmgg",item.getString("v_pmgg"));
               map.put("wgrq",item.getString("v_wgrq"));
               map.put("scsl",item.getString("v_scsl"));
               map.put("lpsl",item.getString("v_lpsl"));
               map.put("ztbz",item.getString("v_ztbz"));
               map.put("cfwz",item.getString("v_cwdm"));
               map.put("yjgs",item.getString("v_yjgs"));
               data.add(map);
           }
           WorkOrderAdapter adapter=new WorkOrderAdapter(GdglActivity.this,R.layout.list_item_b3,data,wkno,handler);
           listView.setAdapter(adapter);
       }catch (JSONException e){
           e.printStackTrace();
       }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cancle_btn:
                AppUtils.sendCountdownReceiver(GdglActivity.this);
                finish();
                break;
            default:
                break;
        }
    }

    private void getNetData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                /*List<List<String>>list=NetHelper.getQuerysqlResult("Exec PAD_Get_MoeDet 'A','"+jtbh+"'");
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
                JSONArray list=NetHelper.getQuerysqlResultJsonArray("Exec PAD_Get_MoeDet 'A','"+jtbh+"'");
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog.isShow()){
            dialog.dismiss();
        }
    }
}
