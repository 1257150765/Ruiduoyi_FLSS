package com.ruiduoyi.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.ruiduoyi.R;
import com.ruiduoyi.model.NetHelper;
import com.ruiduoyi.utils.AppUtils;
import com.ruiduoyi.view.PopupDialog;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SlcsActivity extends BaseActivity implements View.OnClickListener{
    private Handler handler;
    private Button btn_1,btn_2,btn_3,btn_4,btn_5,btn_6,btn_7,btn_8,btn_9,btn_0,btn_clear,btn_submit,btn_del,cancle_btn;
    private TextView sub_text,zzdh_text,cpbh_text,mjbh_text,mjmc_text,mjqs_text,sjqs_text,
        pmgg_text,bfbsd_text,mghlb_text,sjscl_text,smzl_text,smlph_text;
    private String sub_num;
    private Animation anim;
    private ListView listView;
    private PopupDialog dialog;
    private String wkno;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slcs);
        initData();
        initView();
    }


    private void initData(){
        Intent intent_from=getIntent();
        wkno=intent_from.getStringExtra("wkno");

        anim= AnimationUtils.loadAnimation(this,R.anim.sub_num_anim);
        sharedPreferences=getSharedPreferences("info",MODE_PRIVATE);
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 0x100:
                        initListView((JSONArray) msg.obj);
                        break;
                    case 0x101:
                        getListData();
                        bfbsd_text.setText("0");
                        mghlb_text.setText("0");
                        sjscl_text.setText("0");
                        smzl_text.setText("0");
                        smlph_text.setText("");
                        btn_submit.setEnabled(true);
                        btn_submit.setText("提交");
                        break;
                    case 0x102:
                        btn_submit.setEnabled(true);
                        btn_submit.setText("提交");
                        dialog.setMessageTextColor(Color.RED);
                        dialog.setMessage((String) msg.obj);
                        dialog.show();
                        break;
                }
            }
        };
        getListData();
    }


    private void initView(){
        listView=(ListView)findViewById(R.id.list_1);
        zzdh_text=(TextView)findViewById(R.id.zzdh);
        cpbh_text=(TextView)findViewById(R.id.cpbh);
        mjbh_text=(TextView)findViewById(R.id.mjbh);
        mjmc_text=(TextView)findViewById(R.id.mjmc);
        mjqs_text=(TextView)findViewById(R.id.mjqs);
        sjqs_text=(TextView)findViewById(R.id.sjqs);
        pmgg_text=(TextView)findViewById(R.id.pmgg);
        bfbsd_text=(TextView)findViewById(R.id.d1bfbsd);
        mghlb_text=(TextView)findViewById(R.id.mmghlbz);
        sjscl_text=(TextView)findViewById(R.id.csmsjscl);
        smzl_text=(TextView)findViewById(R.id.mmsmzl);
        smlph_text=(TextView)findViewById(R.id.smlph);

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
        btn_del=(Button)findViewById(R.id.btn_del);
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
        btn_del.setOnClickListener(this);
        btn_submit.setOnClickListener(this);
        btn_clear.setOnClickListener(this);

        bfbsd_text.setOnClickListener(this);
        mghlb_text.setOnClickListener(this);
        sjscl_text.setOnClickListener(this);
        smzl_text.setOnClickListener(this);
        smlph_text.setOnClickListener(this);
        sub_text=bfbsd_text;

        zzdh_text.setText(sharedPreferences.getString("zzdh",""));
        cpbh_text.setText(sharedPreferences.getString("cpbh",""));
        mjbh_text.setText(sharedPreferences.getString("mjbh",""));
        mjmc_text.setText(sharedPreferences.getString("mjmc",""));
        mjqs_text.setText(sharedPreferences.getString("mjqs",""));
        sjqs_text.setText(sharedPreferences.getString("sjqs",""));
        pmgg_text.setText(sharedPreferences.getString("pmgg",""));



        dialog=new PopupDialog(this,400,360);
        dialog.setTitle("提示");
        dialog.getCancle_btn().setVisibility(View.GONE);
        dialog.getOkbtn().setText("确定");
        dialog.getOkbtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtils.sendCountdownReceiver(SlcsActivity.this);
                dialog.dismiss();
            }
        });
    }


    private void getListData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                /*List<List<String>>list= NetHelper.getQuerysqlResult("Exec PAD_Get_SllInf '"+sharedPreferences.getString("zzdh","")+"'");
                if (list!=null){
                    if (list.size()>0){
                        if (list.get(0).size()>7){
                            Message msg=handler.obtainMessage();
                            msg.what=0x100;
                            msg.obj=list;
                            handler.sendMessage(msg);
                        }
                    }else {
                        Message msg=handler.obtainMessage();
                        msg.what=0x100;
                        msg.obj=list;
                        handler.sendMessage(msg);
                    }
                }else {
                    AppUtils.uploadNetworkError("Exec PAD_Get_SllInf",sharedPreferences.getString("jtbh",""),"");
                }*/
               JSONArray list= NetHelper.getQuerysqlResultJsonArray("Exec PAD_Get_SllInf '"+sharedPreferences.getString("zzdh","")+"'");
                if (list!=null){
                    Message msg=handler.obtainMessage();
                    msg.what=0x100;
                    msg.obj=list;
                    handler.sendMessage(msg);
                }else {
                    AppUtils.uploadNetworkError("Exec PAD_Get_SllInf",sharedPreferences.getString("jtbh",""),"");
                }
            }
        }).start();
    }

    private void initListView(JSONArray lists){
        try {
            List<Map<String,String>>data=new ArrayList<>();
            for(int i=0;i<lists.length();i++){
                Map<String,String>map=new HashMap<>();
                map.put("lab_1",lists.getJSONObject(i).getString("sll_sjxs"));
                map.put("lab_2",lists.getJSONObject(i).getString("sll_sdrate"));
                map.put("lab_3",lists.getJSONObject(i).getString("sll_glbzl"));
                map.put("lab_4",lists.getJSONObject(i).getString("sll_smsjzl"));
                map.put("lab_5",lists.getJSONObject(i).getString("sll_mmsmzl"));
                map.put("lab_6",lists.getJSONObject(i).getString("sll_smph"));
                map.put("lab_7",lists.getJSONObject(i).getString("sll_jlrymc"));
                map.put("lab_8",lists.getJSONObject(i).getString("sll_jlrq"));
                data.add(map);
            }
            SimpleAdapter adapter=new SimpleAdapter(this,data,R.layout.list_item_slcs,
                    new String[]{"lab_1","lab_2","lab_3","lab_4","lab_5","lab_6","lab_7","lab_8"},
                    new  int[]{R.id.lab_1,R.id.lab_2,R.id.lab_3,R.id.lab_4,R.id.lab_5,R.id.lab_6,R.id.lab_7,R.id.lab_8});
            listView.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean isReady(){
        /*if (bfbsd_text.getText().toString().equals("")){
            dialog.setMessageTextColor(Color.RED);
            dialog.setMessage("请先输入斗1百分比");
            dialog.show();
            return false;
        }*/
        if (Double.parseDouble(bfbsd_text.getText().toString())>100) {
            dialog.setMessageTextColor(Color.RED);
            dialog.setMessage("斗1百分比的值不能大于100");
            dialog.show();
            return false;
        }
        if (mghlb_text.getText().toString().equals("0")){
            dialog.setMessageTextColor(Color.RED);
            dialog.setMessage("请先输入每模盖和料柄重");
            dialog.show();
            return false;
        }
        if (sjscl_text.getText().toString().equals("0")){
            dialog.setMessageTextColor(Color.RED);
            dialog.setMessage("请先输入50s测色母实际输出量");
            dialog.show();
            return false;
        }
        if (smzl_text.getText().toString().equals("0")){
            dialog.setMessageTextColor(Color.RED);
            dialog.setMessage("请先输入每模色母重量");
            dialog.show();
            return false;
        }
        if (smlph_text.getText().toString().equals("")){
            dialog.setMessageTextColor(Color.RED);
            dialog.setMessage("请先输入色母粒批号");
            dialog.show();
            return false;
        }
        return true;
    }

    private void upLoadData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                double bfbsd_num=Double.parseDouble(bfbsd_text.getText().toString());
                double mghlbz_num=Double.parseDouble(mghlb_text.getText().toString());
                double sjscl_num=Double.parseDouble(sjscl_text.getText().toString());
                double smzl_num=Double.parseDouble(smzl_text.getText().toString());
                String bfbsd_str=forMatNum(bfbsd_num,2);
                String mghlbz_str=forMatNum(mghlbz_num,1);
                String sjscl_str=forMatNum(sjscl_num,1);
                String smzl_str=forMatNum(smzl_num,1);

               /* List<List<String>>list=NetHelper.getQuerysqlResult(
                        "Exec PAD_Up_SllList  '"+zzdh_text.getText().toString()+"','"+bfbsd_str+"','"
                                +mghlbz_str+"','"+sjscl_str+"','"+smzl_str+"','"+smlph_text.getText().toString()
                                +"','"+wkno+"'");
                if (list!=null){
                    if (list.size()>0){
                        if (list.get(0).size()>0){
                            if (list.get(0).get(0).equals("OK")){
                                handler.sendEmptyMessage(0x101);
                            }else {
                                Message msg=handler.obtainMessage();
                                msg.what=0x102;
                                msg.obj=list.get(0).get(0);
                                handler.sendMessage(msg);
                            }
                        }else {
                            Message msg=handler.obtainMessage();
                            msg.what=0x102;
                            msg.obj="提交失败";
                            handler.sendMessage(msg);
                        }
                    }else {
                        Message msg=handler.obtainMessage();
                        msg.what=0x102;
                        msg.obj="提交失败";
                        handler.sendMessage(msg);
                    }
                }else {
                    Message msg=handler.obtainMessage();
                    msg.what=0x102;
                    msg.obj="提交失败";
                    handler.sendMessage(msg);
                    AppUtils.uploadNetworkError("Eexc PAD_Up_SllList",sharedPreferences.getString("jtbh",""),"");
                }*/
                try {
                    JSONArray list=NetHelper.getQuerysqlResultJsonArray(
                            "Exec PAD_Up_SllList  '"+zzdh_text.getText().toString()+"','"+bfbsd_str+"','"
                                    +mghlbz_str+"','"+sjscl_str+"','"+smzl_str+"','"+smlph_text.getText().toString()
                                    +"','"+wkno+"'");
                    if (list!=null){
                        if (list.length()>0){
                            if (list.getJSONObject(0).getString("Column1").equals("OK")){
                                handler.sendEmptyMessage(0x101);
                            }else {
                                Message msg=handler.obtainMessage();
                                msg.what=0x102;
                                msg.obj=list.getJSONObject(0).getString("Column1");
                                handler.sendMessage(msg);
                            }
                        }else {
                            Message msg=handler.obtainMessage();
                            msg.what=0x102;
                            msg.obj="提交失败";
                            handler.sendMessage(msg);
                        }
                    }else {
                        Message msg = handler.obtainMessage();
                        msg.what = 0x102;
                        msg.obj = "提交失败";
                        handler.sendMessage(msg);
                        AppUtils.uploadNetworkError("Eexc PAD_Up_SllList", sharedPreferences.getString("jtbh", ""), "");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    @Override
    public void onClick(View v) {
        AppUtils.sendCountdownReceiver(SlcsActivity.this);
        switch (v.getId()){
            case R.id.d1bfbsd:
                bfbsd_text.setBackgroundColor(getResources().getColor(R.color.small));
                mghlb_text.setBackgroundColor(Color.WHITE);
                sjscl_text.setBackgroundColor(Color.WHITE);
                smzl_text.setBackgroundColor(Color.WHITE);
                smlph_text.setBackgroundColor(Color.WHITE);
                sub_text=bfbsd_text;
                break;
            case R.id.mmghlbz:
                bfbsd_text.setBackgroundColor(Color.WHITE);
                mghlb_text.setBackgroundColor(getResources().getColor(R.color.small));
                sjscl_text.setBackgroundColor(Color.WHITE);
                smzl_text.setBackgroundColor(Color.WHITE);
                smlph_text.setBackgroundColor(Color.WHITE);
                sub_text=mghlb_text;
                break;
            case R.id.csmsjscl:
                bfbsd_text.setBackgroundColor(Color.WHITE);
                mghlb_text.setBackgroundColor(Color.WHITE);
                sjscl_text.setBackgroundColor(getResources().getColor(R.color.small));
                smzl_text.setBackgroundColor(Color.WHITE);
                smlph_text.setBackgroundColor(Color.WHITE);
                sub_text=sjscl_text;
                break;
            case R.id.mmsmzl:
                bfbsd_text.setBackgroundColor(Color.WHITE);
                mghlb_text.setBackgroundColor(Color.WHITE);
                sjscl_text.setBackgroundColor(Color.WHITE);
                smzl_text.setBackgroundColor(getResources().getColor(R.color.small));
                smlph_text.setBackgroundColor(Color.WHITE);
                sub_text=smzl_text;
                break;
            case R.id.smlph:
                bfbsd_text.setBackgroundColor(Color.WHITE);
                mghlb_text.setBackgroundColor(Color.WHITE);
                sjscl_text.setBackgroundColor(Color.WHITE);
                smzl_text.setBackgroundColor(Color.WHITE);
                smlph_text.setBackgroundColor(getResources().getColor(R.color.small));
                sub_text=smlph_text;
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
                    final PopupDialog dialog=new PopupDialog(SlcsActivity.this,400,300);
                    dialog.setMessage("是否确认提交？");
                    dialog.setMessageTextColor(Color.BLACK);
                    dialog.setTitle("提示");
                    dialog.getCancle_btn().setText("取消");
                    dialog.getCancle_btn().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialog.getOkbtn().setText("确定");
                    dialog.getOkbtn().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            upLoadData();
                            btn_submit.setEnabled(false);
                            btn_submit.setText("提交中");
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
                break;
            case R.id.btn_del:
                sub_text.startAnimation(anim);
                sub_num=sub_text.getText().toString();
                if (sub_num.equals("")){
                    sub_text.setText("0");
                }else if (sub_num.indexOf(".")<1){
                    sub_text.setText(sub_num+".");
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

    private String forMatNum(double x,int wei){
        NumberFormat ddf1=NumberFormat.getNumberInstance() ;
        ddf1.setMaximumFractionDigits(wei);
        return ddf1.format(x) ;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog.isShow()){
            dialog.dismiss();
        }
    }
}
