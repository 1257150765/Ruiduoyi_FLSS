package com.ruiduoyi.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.ruiduoyi.R;
import com.ruiduoyi.adapter.EasyArrayAdapter;
import com.ruiduoyi.adapter.Jtqsbg1Adapter;
import com.ruiduoyi.adapter.YyfxAdapter;
import com.ruiduoyi.model.NetHelper;
import com.ruiduoyi.utils.AppUtils;
import com.ruiduoyi.view.PopupDialog;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.R.id.list;

public class PgxjActivity extends BaseActivity implements View.OnClickListener{
    private Button save_btn,cancle_btn;
    private RadioButton radio_ok,radio_ng;
    private RadioGroup radioGroup;
    private TextView tsqx_text,zzdh_text,gddh_text,scph_text,wgrq_text,cpbh_text,pmgg_text,jhsl_text,lpsl_text,mjbh_text,mjmc_text,cpxs_text,sjxs_text;
    private ListView congDanListView,yuanYinLisView;
    private Handler handler;
    private String wkno,jtbh;
    private List<Integer>num_list=new ArrayList<>();
    private List<Map<String,String>>data_cong;
    private YyfxAdapter adapter_yy;
    private PopupDialog dialog;
    private List<Map<String,String>>upload_data;
    private PopupDialog dialog2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pgxj);
        initData();
        initView();
    }

    private void initData(){
        Intent inten_from=getIntent();
        wkno=inten_from.getStringExtra("wkno");
        sharedPreferences=getSharedPreferences("info",MODE_PRIVATE);
        jtbh=sharedPreferences.getString("jtbh","");




        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 0x100://初始化工单信息
                        JSONArray list= (JSONArray) msg.obj;
                        try {
                            initGongDanList(list);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 0x101://从工单信息
                        try {
                            initCongDanList((JSONArray) msg.obj);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 0x102:
                        dialog.setMessage((String) msg.obj);
                        dialog.show();
                        break;
                    case 0x103://原因列表
                        try {
                            JSONArray list1= (JSONArray) msg.obj;
                            List<Map<String,String>>data1=new ArrayList<>();
                            for (int i=0;i<list1.length();i++){
                                Map<String,String>map=new HashMap<>();
                                map.put("lab_1",list1.getJSONObject(i).getString("bll_bldm"));
                                map.put("lab_2",list1.getJSONObject(i).getString("bll_blmc"));
                                data1.add(map);
                            }
                            adapter_yy=new YyfxAdapter(PgxjActivity.this,R.layout.list_item_pgxj_yy,data1);
                            yuanYinLisView.setAdapter(adapter_yy);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 0x104://工单信息选择发回来的信息
                        Map<String,String>map= (Map<String, String>) msg.obj;
                        getCongListData(map.get("zzdh"));
                        num_list.clear();
                        Log.w("num_list.clear",num_list.toString());
                        break;
                    default:
                        break;
                }
            }
        };

        getNetData();
    }

    private void initView(){
        cancle_btn=(Button)findViewById(R.id.cancle_btn);
        save_btn=(Button)findViewById(R.id.save_btn);
        radio_ok=(RadioButton)findViewById(R.id.ok_btn);
        radio_ng=(RadioButton)findViewById(R.id.ng_btn);
        congDanListView=(ListView)findViewById(R.id.list_2);
        yuanYinLisView=(ListView)findViewById(R.id.list_3);
        zzdh_text=(TextView)findViewById(R.id.dq_1);
        gddh_text=(TextView)findViewById(R.id.dq_2);
        scph_text=(TextView)findViewById(R.id.dq_3);
        wgrq_text=(TextView)findViewById(R.id.dq_4);
        cpbh_text=(TextView)findViewById(R.id.dq_5);
        pmgg_text=(TextView)findViewById(R.id.dq_6);
        jhsl_text=(TextView)findViewById(R.id.dq_7);
        lpsl_text=(TextView)findViewById(R.id.dq_8);
        mjbh_text=(TextView)findViewById(R.id.dq_9);
        mjmc_text=(TextView)findViewById(R.id.dq_10);
        cpxs_text=(TextView)findViewById(R.id.dq_11);
        sjxs_text=(TextView)findViewById(R.id.dq_12);
        tsqx_text=(TextView)findViewById(R.id.dq_13);
        save_btn.setOnClickListener(this);
        cancle_btn.setOnClickListener(this);
        dialog=new PopupDialog(this,400,360);
        dialog.getCancle_btn().setVisibility(View.GONE);
        dialog.getOkbtn().setText("确定");
        dialog.getOkbtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        radioGroup=(RadioGroup)findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId){
                    case R.id.ok_btn:
                        yuanYinLisView.setVisibility(View.GONE);
                        break;
                    case R.id.ng_btn:
                        yuanYinLisView.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });
        dialog2=new PopupDialog(PgxjActivity.this,400,360);
    }

    private void getNetData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                //工单信息表
                /*List<List<String>>list= NetHelper.getQuerysqlResult("Exec PAD_Get_MoeDet 'C','"+jtbh+"'");
                if (list!=null){
                    if (list.size()>0){
                        if (list.get(0).size()>15){
                            Message msg=handler.obtainMessage();
                            msg.what=0x100;
                            msg.obj=list;
                            handler.sendMessage(msg);
                        }
                    }
                }else {
                    AppUtils.uploadNetworkError("Exec PAD_Get_MoeDet",jtbh,sharedPreferences.getString("mac",""));
                }*/
                JSONArray list= NetHelper.getQuerysqlResultJsonArray("Exec PAD_Get_MoeDet 'C','"+jtbh+"'");
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

                //原因列表
                /*List<List<String>>list_yy=NetHelper.getQuerysqlResult("Exec PAD_Get_XjlInf  'B',''");
                if (list_yy!=null){
                    if (list_yy.size()>0){
                        if (list_yy.get(0).size()>1){
                            Message msg=handler.obtainMessage();
                            msg.what=0x103;
                            msg.obj=list_yy;
                            handler.sendMessage(msg);
                        }
                    }
                }else {
                    AppUtils.uploadNetworkError("Exec PAD_Get_XjlInf  'B',''",jtbh,sharedPreferences.getString("mac",""));
                }*/
                JSONArray list_yy=NetHelper.getQuerysqlResultJsonArray("Exec PAD_Get_XjlInf  'B',''");
                if (list_yy!=null){
                    if (list_yy.length()>0){
                        Message msg=handler.obtainMessage();
                        msg.what=0x103;
                        msg.obj=list_yy;
                        handler.sendMessage(msg);
                    }
                }else {
                    AppUtils.uploadNetworkError("Exec PAD_Get_XjlInf  'B',''",jtbh,sharedPreferences.getString("mac",""));
                }

            }
        }).start();

    }


    private void getCongListData(final String zzdh){
        new Thread(new Runnable() {
            @Override
            public void run() {
                //从工单信息
                /*List<List<String>>list6= NetHelper.getQuerysqlResult("Exec PAD_Get_XjlInf  'A','"+zzdh+"'");
                if (list6!=null){
                    if (list6.size()>0){
                        if (list6.get(0).size()>8){
                            Message msg=handler.obtainMessage();
                            msg.what=0x101;
                            msg.obj=list6;
                            handler.sendMessage(msg);
                        }
                    }
                }else {
                    AppUtils.uploadNetworkError("Exec PAD_Get_XjlInf  'A'",jtbh,sharedPreferences.getString("mac",""));
                }*/
                JSONArray list6= NetHelper.getQuerysqlResultJsonArray("Exec PAD_Get_XjlInf  'A','"+zzdh+"'");
                if (list6!=null){
                    if (list6.length()>0){
                        Message msg=handler.obtainMessage();
                        msg.what=0x101;
                        msg.obj=list6;
                        handler.sendMessage(msg);
                    }
                }else {
                    AppUtils.uploadNetworkError("Exec PAD_Get_XjlInf  'A'",jtbh,sharedPreferences.getString("mac",""));
                }

            }
        }).start();
    }

    private void  initGongDanList(JSONArray lists) throws JSONException {
        zzdh_text.setText(lists.getJSONObject(0).getString("v_zzdh"));
        gddh_text.setText(lists.getJSONObject(0).getString("v_sodh"));
        scph_text.setText(lists.getJSONObject(0).getString("v_ph"));
        wgrq_text.setText(lists.getJSONObject(0).getString("v_wgrq"));
        cpbh_text.setText(lists.getJSONObject(0).getString("v_wldm"));
        pmgg_text.setText(lists.getJSONObject(0).getString("v_pmgg"));
        jhsl_text.setText(lists.getJSONObject(0).getString("v_scsl"));
        lpsl_text.setText(lists.getJSONObject(0).getString("v_lpsl"));
        mjbh_text.setText(lists.getJSONObject(0).getString("v_mjbh"));
        mjmc_text.setText(lists.getJSONObject(0).getString("v_mjmc"));
        cpxs_text.setText(lists.getJSONObject(0).getString("v_itdxs"));
        sjxs_text.setText(lists.getJSONObject(0).getString("v_moexs"));
        tsqx_text.setText(sharedPreferences.getString("tszlqx",""));
        if (tsqx_text.getText().toString().equals("")){
            tsqx_text.setBackgroundColor(Color.WHITE);
        }else {
            tsqx_text.setBackgroundColor(getResources().getColor(R.color.small));
        }
        getCongListData(zzdh_text.getText().toString());
    }

    private void initCongDanList(JSONArray lists) throws JSONException {
        data_cong=new ArrayList<>();
        for (int i=0;i<lists.length();i++){
            Map<String,String>map=new HashMap<>();
            map.put("lab_1",lists.getJSONObject(i).getString("v_wldm"));
            map.put("lab_2",lists.getJSONObject(i).getString("v_pmgg"));
            map.put("lab_3",lists.getJSONObject(i).getString("v_bzjz"));
            map.put("lab_4",lists.getJSONObject(i).getString("v_jz"));
            map.put("lab_5",lists.getJSONObject(i).getString("v_bzskzl"));
            map.put("lab_6",lists.getJSONObject(i).getString("v_skzl"));
            map.put("lab_7",lists.getJSONObject(i).getString("v_moexs"));
            map.put("lab_8",lists.getJSONObject(i).getString("v_xjxs"));
            map.put("zzdh",lists.getJSONObject(i).getString("v_zzdh"));
            data_cong.add(map);
        }
        EasyArrayAdapter adapter=new EasyArrayAdapter(this,R.layout.list_item_pgxj_cong,data_cong) {
            @Override
            public View getEasyView(final int position, View convertView, ViewGroup parent) {
                View view;
                if (convertView!=null){
                    view=convertView;
                }else {
                    view= LayoutInflater.from(PgxjActivity.this).inflate(R.layout.list_item_pgxj_cong,null);
                }
                TextView lab_1=(TextView)view.findViewById(R.id.lab_1);
                TextView lab_2=(TextView)view.findViewById(R.id.lab_2);
                TextView lab_3=(TextView)view.findViewById(R.id.lab_3);
                TextView lab_4=(TextView)view.findViewById(R.id.lab_4);
                TextView lab_5=(TextView)view.findViewById(R.id.lab_5);
                TextView lab_6=(TextView)view.findViewById(R.id.lab_6);
                final TextView lab_7=(TextView)view.findViewById(R.id.lab_7);
                final TextView lab_8=(TextView)view.findViewById(R.id.lab_8);
                Button add_btn=(Button)view.findViewById(R.id.add);
                Button del_btn=(Button)view.findViewById(R.id.delete);
                final Map<String,String>map=data_cong.get(position);
                lab_1.setText(map.get("lab_1"));
                lab_2.setText(map.get("lab_2"));
                lab_3.setText(map.get("lab_3"));
                lab_4.setText(map.get("lab_4"));
                lab_5.setText(map.get("lab_5"));
                lab_6.setText(map.get("lab_6"));
                lab_7.setText(map.get("lab_7"));
                lab_8.setText(map.get("lab_8"));
                add_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int temp=Integer.parseInt(lab_8.getText().toString())+1;
                        lab_8.setText(temp+"");
                        data_cong.get(position).put("lab_8",temp+"");
                    }
                });
                del_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int temp=Integer.parseInt(lab_8.getText().toString())-1;
                        if (!(temp<0)){
                            lab_8.setText(temp+"");
                            data_cong.get(position).put("lab_8",temp+"");
                        }
                        Log.w("num_list",data_cong.toString());
                    }
                });
                return view;
            }
        };
        congDanListView.setAdapter(adapter);
    }


    private boolean isReady(){
        if (data_cong==null){
            dialog.setMessage("从工单信息表未初始化");
            dialog.show();
            return false;
        }
        if (adapter_yy==null){
            dialog.setMessage("原因信息表未初始化");
            dialog.show();
            return false;
        }
        if (!(radio_ng.isChecked()|radio_ok.isChecked())){
            dialog.setMessage("请先选择判定结果");
            dialog.show();
            return false;
        }
        if (radio_ok.isChecked()){
            upload_data=new ArrayList<>();
            for (int i=0;i<data_cong.size();i++){
                if (!data_cong.get(i).get("lab_7").equals(data_cong.get(i).get("lab_8"))){
                    upload_data.add(data_cong.get(i));
                }
            }
            if (upload_data.size()>0){
                dialog.setMessage("实际腔数与巡查腔数不一致，判定结果不能选择OK");
                dialog.show();
                return false;
            }
        }
        if (radio_ng.isChecked()){
            if (!(adapter_yy.getSelectData().size()>0)){
                dialog.setMessage("请先选择原因");
                dialog.show();
                return false;
            }

        }
        return true;
    }


    private void uploadData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (data_cong==null){
                        Message msg=handler.obtainMessage();
                        msg.what=0x102;
                        msg.obj="从工单信息表未初始化";
                        handler.sendMessage(msg);
                        return;
                    }
                    if (adapter_yy==null){
                        Message msg=handler.obtainMessage();
                        msg.what=0x102;
                        msg.obj="原因信息表未初始化";
                        handler.sendMessage(msg);
                        return;
                    }
                    if (radio_ok.isChecked()){//Ok情况下
                        for (int i=0;i<data_cong.size();i++){
                            Map<String,String>map=data_cong.get(i);

                            JSONArray list=NetHelper.getQuerysqlResultJsonArray(
                                    "Exec PAD_Up_Xjllist   'A','"+jtbh+"','"+zzdh_text.getText().toString()+"','"+map.get("lab_7")+"'," +
                                            "'"+map.get("lab_8")+"','OK','','"+wkno+"'");
                            if (list!=null){
                                if (list.length()>0){
                                    if (list.getJSONObject(0).getString("Column1").equals("OK")){
                                        finish();
                                    }else {
                                        Message msg=handler.obtainMessage();
                                        msg.what=0x102;
                                        msg.obj="品质异常发出错误："+list.getJSONObject(0).getString("Column1");
                                        handler.sendMessage(msg);
                                    }
                                }
                            }else {
                                AppUtils.uploadNetworkError("Exec PAD_Up_Xjllist",jtbh,sharedPreferences.getString("mac",""));
                            }
                        }
                    }else if (radio_ng.isChecked()){//NG情况下
                        String yydms="";
                        for (int i=0;i<adapter_yy.getSelectData().size();i++){
                            yydms=yydms+adapter_yy.getSelectData().get(i).get("lab_1")+";";
                        }
                        List<String>result=new ArrayList<String>();
                        for (int i=0;i<data_cong.size();i++){
                            Map<String,String>map=data_cong.get(i);

                            JSONArray list=NetHelper.getQuerysqlResultJsonArray(
                                    "Exec PAD_Up_Xjllist  'A','"+jtbh+"','"+zzdh_text.getText().toString()+"','"+map.get("lab_7")+"'," +
                                            "'"+map.get("lab_8")+"','NG','"+yydms+"','"+wkno+"'");
                            if (list!=null){
                                if (list.length()>0){
                                    if (list.getJSONObject(0).getString("Column1").equals("OK")){
                                        result.add("OK");
                                    }else {
                                        Message msg=handler.obtainMessage();
                                        msg.what=0x102;
                                        msg.obj="品质异常发出错误："+list.getJSONObject(0).getString("Column1");
                                        handler.sendMessage(msg);
                                    }
                                }
                            }else {
                                AppUtils.uploadNetworkError("Exec PAD_Up_Xjllist",jtbh,sharedPreferences.getString("mac",""));
                            }


                        }
                        //如果所有都上传成功则发出品质异常
                        if (AppUtils.calculate(result.toString(),"OK")==data_cong.size()){
                            JSONArray list2=NetHelper.getQuerysqlResultJsonArray(
                                    "Exec PAD_Up_Xjllist  'B','"+jtbh+"','',''," +
                                            "'','','','"+wkno+"'");
                            if (list2!=null){
                                if (list2.length()>0){
                                    if (list2.getJSONObject(0).getString("Column1").equals("OK")){
                                        AppUtils.sendUpdateInfoFragmentReceiver(PgxjActivity.this);
                                        AppUtils.sendReturnToInfoReceiver(PgxjActivity.this);
                                        finish();
                                    }else {
                                        Message msg=handler.obtainMessage();
                                        msg.what=0x102;
                                        msg.obj="品质异常发出错误："+list2.getJSONObject(0).getString("Column1");
                                        handler.sendMessage(msg);
                                    }
                                }
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }




    @Override
    public void onClick(View v) {
        AppUtils.sendCountdownReceiver(PgxjActivity.this);
        switch (v.getId()){
            case R.id.cancle_btn:
                finish();
                break;
            case R.id.save_btn:
                if (isReady()){
                    if (radio_ok.isChecked()){
                        dialog2.setTitle("提示");
                        dialog2.setMessage("请确认巡查腔数与实际腔数是否一致？");
                        dialog2.getCancle_btn().setText("取消");
                        dialog2.getCancle_btn().setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog2.dismiss();
                            }
                        });
                        dialog2.getOkbtn().setText("确定");
                        dialog2.getOkbtn().setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                uploadData();
                            }
                        });
                        dialog2.show();
                    }else {
                        uploadData();
                    }
                }
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog.isShow()){
            dialog.dismiss();
        }
        if (dialog2.isShow()){
            dialog2.dismiss();
        }
    }
}
