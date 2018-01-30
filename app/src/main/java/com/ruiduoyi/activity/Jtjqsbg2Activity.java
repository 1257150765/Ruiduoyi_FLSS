package com.ruiduoyi.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ruiduoyi.Fragment.DtwzFragment;
import com.ruiduoyi.Fragment.DtxxFragment;
import com.ruiduoyi.R;
import com.ruiduoyi.adapter.EasyArrayAdapter;
import com.ruiduoyi.adapter.SigleSelectJtjqsbg;
import com.ruiduoyi.adapter.ViewPagerAdapter;
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

public class Jtjqsbg2Activity extends BaseActivity implements View.OnClickListener{
    private Handler handler;
    private Animation anim;
    private String jtbh,zzdh,wkno,mjbh, mjmc, mjqs, cpqs;
    private SharedPreferences sharedPreferences;
    private TextView jtbh_text,new_jtbh_text;
    private Button cancle_btn;
    private ListView listView_1;
    private PopupDialog dialog;
    private Button jtbhSub_btn;
    private FrameLayout dtxx_btn,dtwz_btn;
    private ViewPager viewPager;
    private TextView dtxx_text,dtwz_text;
    private ViewPagerAdapter vPadapter;
    private DtwzFragment dtwzFragment;
    private DtxxFragment dtxxFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jtjqsbg2);
        initData();
        initView();
    }


    private void initView(){
        jtbhSub_btn=(Button)findViewById(R.id.jtbh_sub);
        cancle_btn=(Button)findViewById(R.id.cancle_btn);
        jtbh_text=(TextView)findViewById(R.id.jtbh_text);
        new_jtbh_text=(TextView)findViewById(R.id.new_jtbh_text);
        listView_1=(ListView)findViewById(R.id.list_1);
        viewPager=(ViewPager)findViewById(R.id.viewPager);
        dtxx_btn=(FrameLayout) findViewById(R.id.dtxx_btn);
        dtwz_btn =(FrameLayout)findViewById(R.id.dtwz_btn);
        dtxx_text=(TextView)findViewById(R.id.dtxx_text);
        dtwz_text=(TextView)findViewById(R.id.dtwz_text);
        dtxx_btn.setOnClickListener(this);
        dtwz_btn.setOnClickListener(this);

        jtbhSub_btn.setOnClickListener(this);
        cancle_btn.setOnClickListener(this);


        jtbh_text.setText(jtbh);


        dialog=new PopupDialog(this,400,360);
        dialog.setTitle("提示");
        dialog.getCancle_btn().setVisibility(View.GONE);
        dialog.getOkbtn().setText("确定");
        dialog.getOkbtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                AppUtils.sendCountdownReceiver(Jtjqsbg2Activity.this);
            }
        });

        vPadapter=new ViewPagerAdapter(getSupportFragmentManager());
        dtwzFragment=new DtwzFragment();
        dtxxFragment=new DtxxFragment();
        vPadapter.addFragment(dtxxFragment,"");
        vPadapter.addFragment(dtwzFragment,"");
        viewPager.setAdapter(vPadapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        dtxx_btn.setBackgroundColor(getResources().getColor(R.color.blue_sl_false));
                        dtxx_text.setTextColor(Color.WHITE);
                        dtwz_btn.setBackgroundColor(getResources().getColor(R.color.blue_sl_true));
                        dtwz_text.setTextColor(getResources().getColor(R.color.color_9));
                        break;
                    case 1:
                        dtxx_btn.setBackgroundColor(getResources().getColor(R.color.blue_sl_true));
                        dtxx_text.setTextColor(getResources().getColor(R.color.color_9));
                        dtwz_btn.setBackgroundColor(getResources().getColor(R.color.blue_sl_false));
                        dtwz_text.setTextColor(Color.WHITE);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initData(){
        anim= AnimationUtils.loadAnimation(this,R.anim.sub_num_anim);



        sharedPreferences=getSharedPreferences("info",MODE_PRIVATE);
        jtbh=sharedPreferences.getString("jtbh","");
        Intent intent_from=getIntent();
        zzdh=intent_from.getStringExtra("zzdh");
        wkno=intent_from.getStringExtra("wkno");
        mjbh=intent_from.getStringExtra("mjbh");
        mjmc=intent_from.getStringExtra("mjmc");
        mjqs=intent_from.getStringExtra("mjqs");
        cpqs=intent_from.getStringExtra("cpqs");




        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 0x101:
                        try {
                            JSONArray list1= (JSONArray) msg.obj;
                            List<Map<String,String>>data=new ArrayList<>();
                            for (int i=0;i<list1.length();i++){
                                Map<String,String>map=new HashMap<>();
                                map.put("lab_1",list1.getJSONObject(i).getString("v_jtbh"));
                                map.put("lab_2",list1.getJSONObject(i).getString("v_ccsl"));
                                map.put("lab_3",list1.getJSONObject(i).getString("v_rate"));
                                map.put("lab_4",list1.getJSONObject(i).getString("v_newsj"));
                                data.add(map);
                            }
                            SigleSelectJtjqsbg adapter=new SigleSelectJtjqsbg(Jtjqsbg2Activity.this,R.layout.list_item_jtjqsbg_2,data) {
                                @Override
                                public void onRadioSelectListener(int position, Map<String, String> map) {
                                    new_jtbh_text.setText(map.get("lab_1"));
                                }
                            };
                            listView_1.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 0x102:
                        String wz= (String) msg.obj;
                        dialog.setMessage("存在超出模具腔数矩阵以外的腔:"+wz+"，请检查并修复");
                        dialog.setMessageTextColor(Color.RED);
                        dialog.show();
                        break;
                    case 0x106:
                        dialog.setMessageTextColor(Color.RED);
                        dialog.setMessage((String) msg.obj);
                        dialog.show();
                        break;
                    case 0x107:
                        jtbhSub_btn.setEnabled(true);
                        jtbhSub_btn.setText("提交");
                        jtbh_text.setText(new_jtbh_text.getText().toString());
                        new_jtbh_text.setText("");
                        break;
                    case 0x108:
                        jtbhSub_btn.setEnabled(true);
                        jtbhSub_btn.setText("提交");
                        dialog.setMessageTextColor(Color.RED);
                        dialog.setMessage((String) msg.obj);
                        dialog.show();
                        break;
                    case 0x109:
                        List<List<String>>list_gongdan= (List<List<String>>) msg.obj;
                        for (int i=0;i<list_gongdan.size();i++){
                            if (list_gongdan.get(i).get(3).equals(zzdh)){
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
        };


        getNetData();

    }


    private void getNetData(){

        getList2Data(zzdh);

    }



    //机台信息表
    private void getList2Data(final String zzdh){
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONArray list= NetHelper.getQuerysqlResultJsonArray("Exec PAD_Get_MoeJtxsInf 'B','"+zzdh+"'");
                if (list!=null){
                    if (list.length()>0){
                        Message msg=handler.obtainMessage();
                        msg.what=0x101;
                        msg.obj=list;
                        handler.sendMessage(msg);
                    }
                }else {
                    AppUtils.uploadNetworkError("Exec PAD_Get_MoeJtXs  'B'",jtbh,sharedPreferences.getString("mac",""));
                }

            }
        }).start();
    }





    private void uploadJtbh(final String new_jtbh, final String wkno, final String zzdh){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONArray list=NetHelper.getQuerysqlResultJsonArray("Exec PAD_Upd_MoeJtXs  'A','"+zzdh+"','"+new_jtbh+"','',0,0,'','"+wkno+"'");
                    if (list!=null){
                        if (list.length()>0){
                            if (list.getJSONObject(0).getString("Column1").equals("OK")){
                                handler.sendEmptyMessage(0x107);
                            }else {
                                Message msg=handler.obtainMessage();
                                msg.what=0x108;
                                msg.obj=list.getJSONObject(0).getString("Column1");
                                handler.sendMessage(msg);
                            }
                        }
                    }else {
                        AppUtils.uploadNetworkError("Exec PAD_Upd_MoeJtXs  'A'",jtbh,sharedPreferences.getString("mac",""));
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private boolean jtbhIsReady(){
        if (new_jtbh_text.getText().toString().equals("")){
            dialog.setMessageTextColor(Color.RED);
            dialog.setMessage("请先选择机台编号");
            dialog.show();
            return false;
        }
        if (new_jtbh_text.getText().toString().equals(jtbh_text.getText().toString())){
            dialog.setMessageTextColor(Color.RED);
            dialog.setMessage("机台编号没有发生变更");
            dialog.show();
            return false;
        }
        return true;
    }



    @Override
    public void onClick(View v) {
        AppUtils.sendCountdownReceiver(Jtjqsbg2Activity.this);
        switch (v.getId()){
            case R.id.dtxx_btn:
                dtxx_btn.setBackgroundColor(getResources().getColor(R.color.blue_sl_false));
                dtxx_text.setTextColor(Color.WHITE);
                dtwz_btn.setBackgroundColor(getResources().getColor(R.color.blue_sl_true));
                dtwz_text.setTextColor(getResources().getColor(R.color.color_9));
                viewPager.setCurrentItem(0);
                break;
            case R.id.dtwz_btn:
                dtxx_btn.setBackgroundColor(getResources().getColor(R.color.blue_sl_true));
                dtxx_text.setTextColor(getResources().getColor(R.color.color_9));
                dtwz_btn.setBackgroundColor(getResources().getColor(R.color.blue_sl_false));
                dtwz_text.setTextColor(Color.WHITE);
                viewPager.setCurrentItem(1);
                break;
            case R.id.jtbh_sub:
                if (jtbhIsReady()){
                    jtbhSub_btn.setEnabled(false);
                    jtbhSub_btn.setText("提交中");
                    uploadJtbh(new_jtbh_text.getText().toString(),wkno,zzdh);
                }
                break;
            case R.id.cancle_btn:
                setResult(1,new Intent());
                finish();
                break;
        }
    }

    //堵头信息表
    public void getDutouListData(final String zzdh, final String cpqs){
        //堵头信息表
        new Thread(new Runnable() {
            @Override
            public void run() {
                int qs=Integer.parseInt(cpqs);
                int horizontal=8;
                int vertical=8;
                switch (qs){
                    case 64:
                        horizontal=8;
                        vertical=8;
                        break;
                    case 144:
                        horizontal=12;
                        vertical=12;
                        break;
                    case 96:
                        horizontal=8;
                        vertical=12;
                        break;
                    case 32:
                        horizontal=8;
                        vertical=4;
                        break;
                    default:
                        qs=64;
                        horizontal=8;
                        vertical=8;
                        break;
                }
                try {
                    JSONArray list_dt=NetHelper.getQuerysqlResultJsonArray("Exec PAD_Get_MoeJtxsInf 'A','"+zzdh+"'");
                    if (list_dt!=null){
                        if (list_dt.length()>0){
                            //初始化堵头信息的ListView
                            Message msg=handler.obtainMessage();
                            msg.what=0x104;
                            msg.obj=list_dt;
                            dtxxFragment.getHandler().sendMessage(msg);


                            //初始化堵头位置的RecyclerView;
                            List<Map<String,String>>data=new ArrayList<Map<String, String>>();
                            for (int i=0;i<qs;i++){
                                Map<String,String>map=new HashMap<String, String>();
                                int w=((i+1)/horizontal)+1;
                                int h=(i+1)%horizontal;
                                if (h==0){
                                    h=horizontal;
                                    w=w-1;
                                }
                                String w_str=w+"";
                                String h_str=h+"";
                                if (w<10){
                                    w_str="0"+w;
                                }
                                if (h<10){
                                    h_str="0"+h;
                                }
                                map.put("wz",w_str+"-"+h_str);
                                map.put("isSelect","0");
                                data.add(map);
                            }
                            for (int i=0;i<list_dt.length();i++){
                                String wz=list_dt.getJSONObject(i).getString("dxd_dxwz");
                                String[] temp=wz.split("-");
                                int w=Integer.parseInt(temp[0]);
                                int h=Integer.parseInt(temp[1]);
                                int position=(w-1)*horizontal+h-1;
                                if (position>-1){
                                    try {
                                        data.get(position).put("isSelect","1");
                                    }catch (IndexOutOfBoundsException e){
                                        Message msg_error=handler.obtainMessage();
                                        msg_error.obj=wz;
                                        msg_error.what=0x102;
                                        handler.sendMessage(msg_error);
                                    }
                                }


                            }


                            Message msg_wz=handler.obtainMessage();
                            msg_wz.what=0x104;
                            msg_wz.obj=data;
                            msg_wz.arg1=horizontal;
                            msg_wz.arg2=vertical;
                            dtwzFragment.getHandler().sendMessage(msg_wz);
                        }else {
                            //初始化堵头信息ListView
                            Message msg=handler.obtainMessage();
                            msg.what=0x104;
                            msg.obj=list_dt;
                            dtxxFragment.getHandler().sendMessage(msg);

                            //初始化堵头位置listView
                            List<Map<String,String>>data=new ArrayList<Map<String, String>>();
                            for (int i=0;i<qs;i++){
                                Map<String,String>map=new HashMap<String, String>();
                                int w=((i+1)/horizontal)+1;
                                int h=(i+1)%horizontal;
                                if (h==0){
                                    h=horizontal;
                                    w=w-1;
                                }
                                String w_str=w+"";
                                String h_str=h+"";
                                if (w<10){
                                    w_str="0"+w;
                                }
                                if (h<10){
                                    h_str="0"+h;
                                }
                                map.put("wz",w_str+"-"+h_str);
                                map.put("isSelect","0");
                                data.add(map);
                            }
                            Message msg_wz=handler.obtainMessage();
                            msg_wz.what=0x104;
                            msg_wz.obj=data;
                            msg_wz.arg1=horizontal;
                            msg_wz.arg2=vertical;
                            dtwzFragment.getHandler().sendMessage(msg_wz);
                        }
                    }else {
                        AppUtils.uploadNetworkError("Exec PAD_Get_MoeJtxsInf 'A'",jtbh,sharedPreferences.getString("mac",""));
                    }
                }catch (JSONException e){
                    e.printStackTrace();
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
