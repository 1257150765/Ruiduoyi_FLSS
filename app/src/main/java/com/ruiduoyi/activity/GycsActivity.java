package com.ruiduoyi.activity;

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
import android.widget.TextView;

import com.ruiduoyi.R;
import com.ruiduoyi.adapter.EasyArrayAdapter;
import com.ruiduoyi.model.NetHelper;
import com.ruiduoyi.utils.AppUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GycsActivity extends BaseActivity {
    private ListView listView;
    private Handler handler;
    private TextView wjbh_text,bb_text,jtbh_text,cpbh_text,pmgg_text,mjbh_text,mjmc_text,cpqs_text,sml_text,
        cxzq_text,lqsj_text,sjsj_text,sdry_text,
        flb_1,flb_2,
        sdz_1,sdz_2,sdz_3,sdz_4,sdz_5,sjz_1,sjz_2,sjz_3,sjz_4,sjz_5,
        sd_1,sd_2,sd_3,sd_4,sd_5,sd_6,sd_7,sd_8,sd_9,sd_10,sd_11,sd_12,
        yl_1,yl_2,yl_3,yl_4,yl_5,yl_6,yl_7,yl_8,yl_9,yl_10,yl_11,yl_12,

        pz_1,pz_2,pz_3,pz_4,pz_5,pz_6,pz_7,pz_8,pz_9,pz_10,pz_11,pz_12,
                pz_13,pz_14,pz_15,pz_16,pz_17,pz_18,pz_19,pz_20,pz_21,pz_22,pz_23,pz_24,
                pz_25,pz_26,pz_27,pz_28,pz_29,pz_30,pz_31,pz_32,pz_33,pz_34,pz_35,pz_36,
        bzms_text,bzxx_text,
        tip;
    private Button cancle_btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gycs);
        initData();
        initView();
    }

    private void initData(){
        sharedPreferences=getSharedPreferences("info",MODE_PRIVATE);
        jtbh=sharedPreferences.getString("jtbh","");
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 0x100:
                        try {
                            initListView((JSONArray) msg.obj);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 0x101:
                       try {
                           JSONArray list1= (JSONArray) msg.obj;
                           jtbh_text.setText(list1.getJSONObject(0).getString("csm_jtbh"));
                           cpbh_text.setText(list1.getJSONObject(0).getString("csm_wldm"));
                           pmgg_text.setText(list1.getJSONObject(0).getString("csm_pmgg"));
                           mjbh_text.setText(list1.getJSONObject(0).getString("csm_mjbh"));
                           mjmc_text.setText(list1.getJSONObject(0).getString("csm_mjmc"));
                           cpqs_text.setText(list1.getJSONObject(0).getString("csm_cpxs"));
                           cxzq_text.setText(list1.getJSONObject(0).getString("csm_cxsj"));
                           lqsj_text.setText(list1.getJSONObject(0).getString("csm_lqsj"));
                           sjsj_text.setText(list1.getJSONObject(0).getString("csm_sjsj"));
                           sml_text.setText(list1.getJSONObject(0).getString("csm_sml"));
                           sdry_text.setText(list1.getJSONObject(0).getString("csm_sdry"));
                           bzms_text.setText(list1.getJSONObject(0).getString("csm_bzms"));
                           bzxx_text.setText(list1.getJSONObject(0).getString("csm_desc"));
                       }catch (JSONException e){
                           e.printStackTrace();
                       }
                        break;
                    case 0x102:
                        try {
                            JSONArray list2= (JSONArray) msg.obj;
                            sdz_1.setText(list2.getJSONObject(0).getString("csd_val1"));
                            sdz_2.setText(list2.getJSONObject(0).getString("csd_val2"));
                            sdz_3.setText(list2.getJSONObject(0).getString("csd_val3"));
                            sdz_4.setText(list2.getJSONObject(0).getString("csd_val4"));
                            sdz_5.setText(list2.getJSONObject(0).getString("csd_val5"));
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                        break;
                    case 0x103:
                       try {
                           JSONArray list3= (JSONArray) msg.obj;
                           sjz_1.setText(list3.getJSONObject(0).getString("csd_val1"));
                           sjz_2.setText(list3.getJSONObject(0).getString("csd_val2"));
                           sjz_3.setText(list3.getJSONObject(0).getString("csd_val3"));
                           sjz_4.setText(list3.getJSONObject(0).getString("csd_val4"));
                           sjz_5.setText(list3.getJSONObject(0).getString("csd_val5"));
                       }catch (JSONException e){
                           e.printStackTrace();
                       }
                        break;
                    case 0x104:
                       try {
                           JSONArray list4= (JSONArray) msg.obj;
                           flb_1.setText(list4.getJSONObject(0).getString("csd_val1"));
                           flb_2.setText(list4.getJSONObject(0).getString("csd_val2"));
                       }catch (JSONException e){
                           e.printStackTrace();
                       }
                        break;
                    case 0x105:
                        try {
                            JSONArray list5= (JSONArray) msg.obj;
                            sd_1.setText(list5.getJSONObject(0).getString("csd_val1"));
                            sd_2.setText(list5.getJSONObject(0).getString("csd_val2"));
                            sd_3.setText(list5.getJSONObject(0).getString("csd_val3"));
                            sd_4.setText(list5.getJSONObject(0).getString("csd_val4"));
                            sd_5.setText(list5.getJSONObject(0).getString("csd_val5"));
                            sd_6.setText(list5.getJSONObject(0).getString("csd_val6"));
                            sd_7.setText(list5.getJSONObject(0).getString("csd_val7"));
                            sd_8.setText(list5.getJSONObject(0).getString("csd_val8"));
                            sd_9.setText(list5.getJSONObject(0).getString("csd_val9"));
                            sd_10.setText(list5.getJSONObject(0).getString("csd_val10"));
                            sd_11.setText(list5.getJSONObject(0).getString("csd_val11"));
                            sd_12.setText(list5.getJSONObject(0).getString("csd_val12"));
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                        break;
                    case 0x106:
                        try {
                            JSONArray list6= (JSONArray) msg.obj;
                            yl_1.setText(list6.getJSONObject(0).getString("csd_val1"));
                            yl_2.setText(list6.getJSONObject(0).getString("csd_val2"));
                            yl_3.setText(list6.getJSONObject(0).getString("csd_val3"));
                            yl_4.setText(list6.getJSONObject(0).getString("csd_val4"));
                            yl_5.setText(list6.getJSONObject(0).getString("csd_val5"));
                            yl_6.setText(list6.getJSONObject(0).getString("csd_val6"));
                            yl_7.setText(list6.getJSONObject(0).getString("csd_val7"));
                            yl_8.setText(list6.getJSONObject(0).getString("csd_val8"));
                            yl_9.setText(list6.getJSONObject(0).getString("csd_val9"));
                            yl_10.setText(list6.getJSONObject(0).getString("csd_val10"));
                            yl_11.setText(list6.getJSONObject(0).getString("csd_val11"));
                            yl_12.setText(list6.getJSONObject(0).getString("csd_val12"));
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                        break;
                    case 0x107:
                        try {
                            JSONArray list7= (JSONArray) msg.obj;
                            pz_1.setText(list7.getJSONObject(0).getString("csd_val1"));
                            pz_2.setText(list7.getJSONObject(0).getString("csd_val2"));
                            pz_3.setText(list7.getJSONObject(0).getString("csd_val3"));
                            pz_4.setText(list7.getJSONObject(0).getString("csd_val4"));
                            pz_5.setText(list7.getJSONObject(0).getString("csd_val5"));
                            pz_6.setText(list7.getJSONObject(0).getString("csd_val6"));
                            pz_7.setText(list7.getJSONObject(0).getString("csd_val7"));
                            pz_8.setText(list7.getJSONObject(0).getString("csd_val8"));
                            pz_9.setText(list7.getJSONObject(0).getString("csd_val9"));
                            pz_10.setText(list7.getJSONObject(0).getString("csd_val10"));
                            pz_11.setText(list7.getJSONObject(0).getString("csd_val11"));
                            pz_12.setText(list7.getJSONObject(0).getString("csd_val12"));
                            pz_13.setText(list7.getJSONObject(0).getString("csd_val13"));
                            pz_14.setText(list7.getJSONObject(0).getString("csd_val14"));
                            pz_15.setText(list7.getJSONObject(0).getString("csd_val15"));
                            pz_16.setText(list7.getJSONObject(0).getString("csd_val16"));
                            pz_17.setText(list7.getJSONObject(0).getString("csd_val17"));
                            pz_18.setText(list7.getJSONObject(0).getString("csd_val18"));
                            pz_19.setText(list7.getJSONObject(0).getString("csd_val19"));
                            pz_20.setText(list7.getJSONObject(0).getString("csd_val20"));
                            pz_21.setText(list7.getJSONObject(0).getString("csd_val21"));
                            pz_22.setText(list7.getJSONObject(0).getString("csd_val22"));
                            pz_23.setText(list7.getJSONObject(0).getString("csd_val23"));
                            pz_24.setText(list7.getJSONObject(0).getString("csd_val24"));
                            pz_25.setText(list7.getJSONObject(0).getString("csd_val25"));
                            pz_26.setText(list7.getJSONObject(0).getString("csd_val26"));
                            pz_27.setText(list7.getJSONObject(0).getString("csd_val27"));
                            pz_28.setText(list7.getJSONObject(0).getString("csd_val28"));
                            pz_29.setText(list7.getJSONObject(0).getString("csd_val29"));
                            pz_30.setText(list7.getJSONObject(0).getString("csd_val30"));
                            pz_31.setText(list7.getJSONObject(0).getString("csd_val31"));
                            pz_32.setText(list7.getJSONObject(0).getString("csd_val32"));
                            pz_33.setText(list7.getJSONObject(0).getString("csd_val33"));
                            pz_34.setText(list7.getJSONObject(0).getString("csd_val34"));
                            pz_35.setText(list7.getJSONObject(0).getString("csd_val35"));
                            pz_36.setText(list7.getJSONObject(0).getString("csd_val36"));
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                        break;

                }
            }
        };
        getListData();
    }

    private void initView(){
        listView=(ListView)findViewById(R.id.list_1);
        wjbh_text=(TextView)findViewById(R.id.wjbh);
        bb_text=(TextView)findViewById(R.id.bb);
        jtbh_text=(TextView)findViewById(R.id.jtbh);
        cpbh_text=(TextView)findViewById(R.id.cpbh);
        mjbh_text=(TextView)findViewById(R.id.mjbh);
        mjmc_text=(TextView)findViewById(R.id.mjmc);
        cpqs_text=(TextView)findViewById(R.id.cpqs);
        sml_text=(TextView)findViewById(R.id.sml);
        cxzq_text=(TextView)findViewById(R.id.cxzq);
        lqsj_text=(TextView)findViewById(R.id.lqsj);
        sjsj_text=(TextView)findViewById(R.id.sjsj);
        sdry_text=(TextView)findViewById(R.id.sdry);
        pmgg_text=(TextView)findViewById(R.id.pmgg);
        bzms_text=(TextView)findViewById(R.id.bzms);
        bzxx_text=(TextView)findViewById(R.id.bzxx);

        sdz_1=(TextView)findViewById(R.id.sdz_1);
        sdz_2=(TextView)findViewById(R.id.sdz_2);
        sdz_3=(TextView)findViewById(R.id.sdz_3);
        sdz_4=(TextView)findViewById(R.id.sdz_4);
        sdz_5=(TextView)findViewById(R.id.sdz_5);

        sjz_1=(TextView)findViewById(R.id.sjz_1);
        sjz_2=(TextView)findViewById(R.id.sjz_2);
        sjz_3=(TextView)findViewById(R.id.sjz_3);
        sjz_4=(TextView)findViewById(R.id.sjz_4);
        sjz_5=(TextView)findViewById(R.id.sjz_5);

        flb_1=(TextView)findViewById(R.id.flb_1);
        flb_2=(TextView)findViewById(R.id.flb_2);

        sd_1=(TextView)findViewById(R.id.sd_1);
        sd_2=(TextView)findViewById(R.id.sd_2);
        sd_3=(TextView)findViewById(R.id.sd_3);
        sd_4=(TextView)findViewById(R.id.sd_4);
        sd_5=(TextView)findViewById(R.id.sd_5);
        sd_6=(TextView)findViewById(R.id.sd_6);
        sd_7=(TextView)findViewById(R.id.sd_7);
        sd_8=(TextView)findViewById(R.id.sd_8);
        sd_9=(TextView)findViewById(R.id.sd_9);
        sd_10=(TextView)findViewById(R.id.sd_10);
        sd_11=(TextView)findViewById(R.id.sd_11);
        sd_12=(TextView)findViewById(R.id.sd_12);

        yl_1=(TextView)findViewById(R.id.yl_1);
        yl_2=(TextView)findViewById(R.id.yl_2);
        yl_3=(TextView)findViewById(R.id.yl_3);
        yl_4=(TextView)findViewById(R.id.yl_4);
        yl_5=(TextView)findViewById(R.id.yl_5);
        yl_6=(TextView)findViewById(R.id.yl_6);
        yl_7=(TextView)findViewById(R.id.yl_7);
        yl_8=(TextView)findViewById(R.id.yl_8);
        yl_9=(TextView)findViewById(R.id.yl_9);
        yl_10=(TextView)findViewById(R.id.yl_10);
        yl_11=(TextView)findViewById(R.id.yl_11);
        yl_12=(TextView)findViewById(R.id.yl_12);

        pz_1=(TextView)findViewById(R.id.pz_1);
        pz_2=(TextView)findViewById(R.id.pz_2);
        pz_3=(TextView)findViewById(R.id.pz_3);
        pz_4=(TextView)findViewById(R.id.pz_4);
        pz_5=(TextView)findViewById(R.id.pz_5);
        pz_6=(TextView)findViewById(R.id.pz_6);
        pz_7=(TextView)findViewById(R.id.pz_7);
        pz_8=(TextView)findViewById(R.id.pz_8);
        pz_9=(TextView)findViewById(R.id.pz_9);
        pz_10=(TextView)findViewById(R.id.pz_10);
        pz_11=(TextView)findViewById(R.id.pz_11);
        pz_12=(TextView)findViewById(R.id.pz_12);
        pz_13=(TextView)findViewById(R.id.pz_13);
        pz_14=(TextView)findViewById(R.id.pz_14);
        pz_15=(TextView)findViewById(R.id.pz_15);
        pz_16=(TextView)findViewById(R.id.pz_16);
        pz_17=(TextView)findViewById(R.id.pz_17);
        pz_18=(TextView)findViewById(R.id.pz_18);
        pz_19=(TextView)findViewById(R.id.pz_19);
        pz_20=(TextView)findViewById(R.id.pz_20);
        pz_21=(TextView)findViewById(R.id.pz_21);
        pz_22=(TextView)findViewById(R.id.pz_22);
        pz_23=(TextView)findViewById(R.id.pz_23);
        pz_24=(TextView)findViewById(R.id.pz_24);
        pz_25=(TextView)findViewById(R.id.pz_25);
        pz_26=(TextView)findViewById(R.id.pz_26);
        pz_27=(TextView)findViewById(R.id.pz_27);
        pz_28=(TextView)findViewById(R.id.pz_28);
        pz_29=(TextView)findViewById(R.id.pz_29);
        pz_30=(TextView)findViewById(R.id.pz_30);
        pz_31=(TextView)findViewById(R.id.pz_31);
        pz_32=(TextView)findViewById(R.id.pz_32);
        pz_33=(TextView)findViewById(R.id.pz_33);
        pz_34=(TextView)findViewById(R.id.pz_34);
        pz_35=(TextView)findViewById(R.id.pz_35);
        pz_36=(TextView)findViewById(R.id.pz_36);

        tip=(TextView)findViewById(R.id.tip);

        cancle_btn=(Button)findViewById(R.id.cancle_btn);
        cancle_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getListData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                /*List<List<String>>list= NetHelper.getQuerysqlResult("Exec PAD_Get_CsmInf 'A','"+jtbh+"',''");
                if (list!=null){
                    if (list.size()>0){
                        if (list.get(0).size()>3){
                            Message msg=handler.obtainMessage();
                            msg.what=0x100;
                            msg.obj=list;
                            handler.sendMessage(msg);
                        }
                    }
                }else {
                    AppUtils.uploadNetworkError("Exec PAD_Get_CsmInf 'A'",jtbh,"");
                }*/
                JSONArray list= NetHelper.getQuerysqlResultJsonArray("Exec PAD_Get_CsmInf 'A','"+jtbh+"',''");
                if (list!=null){
                    if (list.length()>0){
                        Message msg=handler.obtainMessage();
                        msg.what=0x100;
                        msg.obj=list;
                        handler.sendMessage(msg);
                    }
                }else {
                    AppUtils.uploadNetworkError("Exec PAD_Get_CsmInf 'A'",jtbh,"");
                }
            }
        }).start();
    }

    private void initListView(JSONArray lists) throws JSONException {
        final List<Map<String,String>>data=new ArrayList<>();
        for (int i=0;i<lists.length();i++){
            Map<String,String>map=new HashMap<>();
            map.put("lab_1",lists.getJSONObject(i).getString("v_djbh"));
            map.put("lab_2",lists.getJSONObject(i).getString("v_jtbh"));
            map.put("lab_3",lists.getJSONObject(i).getString("v_ver"));
            map.put("isSelect","0");
            data.add(map);
        }
        EasyArrayAdapter adapter=new EasyArrayAdapter(this,R.layout.list_item_gycs,data) {
            @Override
            public View getEasyView(int position, View convertView, ViewGroup parent) {
                View view;
                if (convertView!=null){
                    view=convertView;
                }else {
                    view= LayoutInflater.from(GycsActivity.this).inflate(R.layout.list_item_gycs,null);
                }
                TextView lab_1=(TextView)view.findViewById(R.id.lab_1);
                TextView lab_2=(TextView)view.findViewById(R.id.lab_2);
                TextView lab_3=(TextView)view.findViewById(R.id.lab_3);
                LinearLayout bg=(LinearLayout)view.findViewById(R.id.bg);
                final Map<String,String>map=data.get(position);
                lab_1.setText(map.get("lab_1"));
                lab_2.setText(map.get("lab_2"));
                lab_3.setText(map.get("lab_3"));
                if (map.get("isSelect").equals("0")){
                    bg.setBackgroundColor(getResources().getColor(R.color.fragment_bg));
                }else {
                    bg.setBackgroundColor(getResources().getColor(R.color.small));
                }
                bg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!map.get("lab_2").equals(jtbh)){
                            tip.setText("提示：所选机台与本机台不一致，仅供参考");
                        }else {
                            tip.setText("");
                        }
                        wjbh_text.setText(map.get("lab_1"));
                        bb_text.setText(map.get("lab_3"));
                        for (int i=0;i<data.size();i++){
                            data.get(i).put("isSelect","0");
                        }
                        map.put("isSelect","1");
                        notifyDataSetChanged();
                        onItemSelectEven(map.get("lab_1"));
                    }
                });
                return view;
            }
        };
        listView.setAdapter(adapter);

    }


    private void onItemSelectEven(final String wjbh){
        AppUtils.sendCountdownReceiver(this);
        jtbh_text.setText("");
        cpbh_text.setText("");
        mjbh_text.setText("");
        mjmc_text.setText("");
        cpqs_text.setText("");
        sml_text.setText("");
        cxzq_text.setText("");
        lqsj_text.setText("");
        sjsj_text.setText("");
        sdry_text.setText("");

        sdz_1.setText("");
        sdz_2.setText("");
        sdz_3.setText("");
        sdz_4.setText("");
        sdz_5.setText("");

        sjz_1.setText("");
        sjz_2.setText("");
        sjz_3.setText("");
        sjz_4.setText("");
        sjz_5.setText("");

        flb_1.setText("");
        flb_2.setText("");

        sd_1.setText("");
        sd_2.setText("");
        sd_3.setText("");
        sd_4.setText("");
        sd_5.setText("");
        sd_6.setText("");
        sd_7.setText("");
        sd_8.setText("");
        sd_9.setText("");
        sd_10.setText("");
        sd_11.setText("");
        sd_12.setText("");

        yl_1.setText("");
        yl_2.setText("");
        yl_3.setText("");
        yl_4.setText("");
        yl_5.setText("");
        yl_6.setText("");
        yl_7.setText("");
        yl_8.setText("");
        yl_9.setText("");
        yl_10.setText("");
        yl_11.setText("");
        yl_12.setText("");

        pz_1.setText("");
        pz_2.setText("");
        pz_3.setText("");
        pz_4.setText("");
        pz_5.setText("");
        pz_6.setText("");
        pz_7.setText("");
        pz_8.setText("");
        pz_9.setText("");
        pz_10.setText("");
        pz_11.setText("");
        pz_12.setText("");
        pz_13.setText("");
        pz_14.setText("");
        pz_15.setText("");
        pz_16.setText("");
        pz_17.setText("");
        pz_18.setText("");
        pz_19.setText("");
        pz_20.setText("");
        pz_21.setText("");
        pz_22.setText("");
        pz_23.setText("");
        pz_24.setText("");
        pz_25.setText("");
        pz_26.setText("");
        pz_27.setText("");
        pz_28.setText("");
        pz_29.setText("");
        pz_30.setText("");
        pz_31.setText("");
        pz_32.setText("");
        pz_33.setText("");
        pz_34.setText("");
        pz_35.setText("");
        pz_36.setText("");
        new Thread(new Runnable() {
            @Override
            public void run() {
                //第一栏基础信息
                /*List<List<String>>list1=NetHelper.getQuerysqlResult("Exec PAD_Get_CsmInf 'B','','"+wjbh+"'");
                if (list1!=null){
                    if (list1.size()>0){
                        if (list1.get(0).size()>19){
                            Message msg=handler.obtainMessage();
                            msg.what=0x101;
                            msg.obj=list1;
                            handler.sendMessage(msg);
                        }
                    }
                }else {
                    AppUtils.uploadNetworkError("Exec PAD_Get_CsmInf 'B'",jtbh,"");
                }*/
                JSONArray list1=NetHelper.getQuerysqlResultJsonArray("Exec PAD_Get_CsmInf 'B','','"+wjbh+"'");
                if (list1!=null){
                    if (list1.length()>0){
                        Message msg=handler.obtainMessage();
                        msg.what=0x101;
                        msg.obj=list1;
                        handler.sendMessage(msg);
                    }
                }else {
                    AppUtils.uploadNetworkError("Exec PAD_Get_CsmInf 'B'",jtbh,"");
                }


                //设定值
               /* List<List<String>>list2=NetHelper.getQuerysqlResult("Exec PAD_Get_CsmInf 'RJSD','','"+wjbh+"'");
                if (list2!=null){
                    if (list2.size()>0){
                        if (list2.get(0).size()>4){
                            Message msg=handler.obtainMessage();
                            msg.what=0x102;
                            msg.obj=list2;
                            handler.sendMessage(msg);
                        }
                    }
                }else {
                    AppUtils.uploadNetworkError("Exec PAD_Get_CsmInf 'RJSD'",jtbh,"");
                }*/
                JSONArray list2=NetHelper.getQuerysqlResultJsonArray("Exec PAD_Get_CsmInf 'RJSD','','"+wjbh+"'");
                if (list2!=null){
                    if (list2.length()>0){
                        Message msg=handler.obtainMessage();
                        msg.what=0x102;
                        msg.obj=list2;
                        handler.sendMessage(msg);
                    }
                }else {
                    AppUtils.uploadNetworkError("Exec PAD_Get_CsmInf 'RJSD'",jtbh,"");
                }



                //实际值
                /*List<List<String>>list3=NetHelper.getQuerysqlResult("Exec PAD_Get_CsmInf 'RJSJ','','"+wjbh+"'");
                if (list3!=null){
                    if (list3.size()>0){
                        if (list3.get(0).size()>4){
                            Message msg=handler.obtainMessage();
                            msg.what=0x103;
                            msg.obj=list3;
                            handler.sendMessage(msg);
                        }
                    }
                }else {
                    AppUtils.uploadNetworkError("Exec PAD_Get_CsmInf 'RJSD'",jtbh,"");
                }*/
                JSONArray list3=NetHelper.getQuerysqlResultJsonArray("Exec PAD_Get_CsmInf 'RJSJ','','"+wjbh+"'");
                if (list3!=null){
                    if (list3.length()>0){
                        Message msg=handler.obtainMessage();
                        msg.what=0x103;
                        msg.obj=list3;
                        handler.sendMessage(msg);
                    }
                }else {
                    AppUtils.uploadNetworkError("Exec PAD_Get_CsmInf 'RJSD'",jtbh,"");
                }


                //分流板
               /* List<List<String>>list4=NetHelper.getQuerysqlResult("Exec PAD_Get_CsmInf 'FLB','','"+wjbh+"'");
                if (list4!=null){
                    if (list4.size()>0){
                        if (list4.get(0).size()>1){
                            Message msg=handler.obtainMessage();
                            msg.what=0x104;
                            msg.obj=list4;
                            handler.sendMessage(msg);
                        }
                    }
                }else {
                    AppUtils.uploadNetworkError("Exec PAD_Get_CsmInf 'FLB'",jtbh,"");
                }*/
                JSONArray list4=NetHelper.getQuerysqlResultJsonArray("Exec PAD_Get_CsmInf 'FLB','','"+wjbh+"'");
                if (list4!=null){
                    if (list4.length()>0){
                        Message msg=handler.obtainMessage();
                        msg.what=0x104;
                        msg.obj=list4;
                        handler.sendMessage(msg);
                    }
                }else {
                    AppUtils.uploadNetworkError("Exec PAD_Get_CsmInf 'FLB'",jtbh,"");
                }


                //速度
               /* List<List<String>>list5=NetHelper.getQuerysqlResult("Exec PAD_Get_CsmInf 'SD','','"+wjbh+"'");
                if (list5!=null){
                    if (list5.size()>0){
                        if (list5.get(0).size()>11){
                            Message msg=handler.obtainMessage();
                            msg.what=0x105;
                            msg.obj=list5;
                            handler.sendMessage(msg);
                        }
                    }
                }else {
                    AppUtils.uploadNetworkError("Exec PAD_Get_CsmInf 'SD'",jtbh,"");
                }*/
                JSONArray list5=NetHelper.getQuerysqlResultJsonArray("Exec PAD_Get_CsmInf 'SD','','"+wjbh+"'");
                if (list5!=null){
                    if (list5.length()>0){
                        Message msg=handler.obtainMessage();
                        msg.what=0x105;
                        msg.obj=list5;
                        handler.sendMessage(msg);
                    }
                }else {
                    AppUtils.uploadNetworkError("Exec PAD_Get_CsmInf 'SD'",jtbh,"");
                }



                //压力
               /* List<List<String>>list6=NetHelper.getQuerysqlResult("Exec PAD_Get_CsmInf 'YL','','"+wjbh+"'");
                if (list6!=null){
                    if (list6.size()>0){
                        if (list6.get(0).size()>11){
                            Message msg=handler.obtainMessage();
                            msg.what=0x106;
                            msg.obj=list6;
                            handler.sendMessage(msg);
                        }
                    }
                }else {
                    AppUtils.uploadNetworkError("Exec PAD_Get_CsmInf 'YL'",jtbh,"");
                }*/
                JSONArray list6=NetHelper.getQuerysqlResultJsonArray("Exec PAD_Get_CsmInf 'YL','','"+wjbh+"'");
                if (list6!=null){
                    if (list6.length()>0){
                        Message msg=handler.obtainMessage();
                        msg.what=0x106;
                        msg.obj=list6;
                        handler.sendMessage(msg);
                    }
                }else {
                    AppUtils.uploadNetworkError("Exec PAD_Get_CsmInf 'YL'",jtbh,"");
                }


                //喷嘴
                /*List<List<String>>list7=NetHelper.getQuerysqlResult("Exec PAD_Get_CsmInf 'PZ','','"+wjbh+"'");
                if (list7!=null){
                    if (list7.size()>0){
                        if (list7.get(0).size()>35){
                            Message msg=handler.obtainMessage();
                            msg.what=0x107;
                            msg.obj=list7;
                            handler.sendMessage(msg);
                        }
                    }
                }else {
                    AppUtils.uploadNetworkError("Exec PAD_Get_CsmInf 'PZ'",jtbh,"");
                }*/
                JSONArray list7=NetHelper.getQuerysqlResultJsonArray("Exec PAD_Get_CsmInf 'PZ','','"+wjbh+"'");
                if (list7!=null){
                    if (list7.length()>0){
                        Message msg=handler.obtainMessage();
                        msg.what=0x107;
                        msg.obj=list7;
                        handler.sendMessage(msg);
                    }
                }else {
                    AppUtils.uploadNetworkError("Exec PAD_Get_CsmInf 'PZ'",jtbh,"");
                }

            }
        }).start();
    }

}
