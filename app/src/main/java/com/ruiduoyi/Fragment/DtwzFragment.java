package com.ruiduoyi.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;

import com.ruiduoyi.R;
import com.ruiduoyi.activity.Jtjqsbg2Activity;
import com.ruiduoyi.adapter.QsRecyclerAdapter;
import com.ruiduoyi.model.NetHelper;
import com.ruiduoyi.utils.AppUtils;
import com.ruiduoyi.view.PopupDialog;
import com.ruiduoyi.view.PopupWindowSpinner;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DtwzFragment extends Fragment implements View.OnClickListener{

    private Button spinner,btn_submit;
    private String lbdm,jtbh,zzdh,wkno,mjqs;
    private SharedPreferences sharedPreferences;
    private PopupWindowSpinner spinner_list;
    private Handler handler;
    private PopupDialog dialog;
    private RecyclerView recyclerView;
    private Jtjqsbg2Activity activity;
    private List<Map<String,String>>data;
    public DtwzFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_dtwz, container, false);
        initData();
        initView(view);
        return view;
    }

    private void initView(View view){
        spinner=(Button)view.findViewById(R.id.spinner);
        btn_submit=(Button)view.findViewById(R.id.btn_submit);
        recyclerView=(RecyclerView)view.findViewById(R.id.recyclerView);

        spinner.setOnClickListener(this);
        btn_submit.setOnClickListener(this);



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
        zzdh=intent_from.getStringExtra("zzdh");
        wkno=intent_from.getStringExtra("wkno");
        mjqs=intent_from.getStringExtra("mjqs");
        sharedPreferences=getContext().getSharedPreferences("info",Context.MODE_PRIVATE);
        jtbh=sharedPreferences.getString("jtbh","");
        activity= (Jtjqsbg2Activity) getActivity();


        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 0x100:
                        try {
                            final JSONArray list= (JSONArray) msg.obj;
                            final List<String>data_spinner=new ArrayList<>();
                            for (int i=0;i<list.length();i++){
                                data_spinner.add("\t"+list.getJSONObject(i).getString("v_lbmc"));
                            }
                            spinner_list=new PopupWindowSpinner(getContext(),data_spinner,R.layout.spinner_list_yyfx,R.id.lab_1,305);
                            spinner_list.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    AppUtils.sendCountdownReceiver(getContext());
                                    spinner.setText(data_spinner.get(position));
                                    try {
                                        lbdm=list.getJSONObject(position).getString("v_lbdm");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    spinner_list.dismiss();
                                }
                            });
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                        break;
                    case 0x102:
                        activity.getDutouListData(zzdh,mjqs);
                        //getGongdanData();
                        /*dialog.setMessageTextColor(Color.BLACK);
                        dialog.setMessage("提交成功");
                        dialog.show();*/
                        //getDutouListData(zzdh);
                        break;
                    case 0x103:
                        btn_submit.setEnabled(true);
                        btn_submit.setText("提交");
                        dialog.setMessageTextColor(Color.RED);
                        dialog.setMessage((String) msg.obj);
                        dialog.show();
                        break;
                    case 0x104:
                        btn_submit.setEnabled(true);
                        btn_submit.setText("提交");
                        data= (List<Map<String, String>>) msg.obj;
                        QsRecyclerAdapter adapter=new QsRecyclerAdapter(getContext(),data);
                        GridLayoutManager layoutManager = new GridLayoutManager(getContext(),msg.arg1);
                        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setAdapter(adapter);
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
               JSONArray list= NetHelper.getQuerysqlResultJsonArray("Exec PAD_Get_MoeJtxsInf 'C','"+zzdh+"'");
                if (list!=null){
                    if (list.length()>0){
                        Message msg=handler.obtainMessage();
                        msg.what=0x100;
                        msg.obj=list;
                        handler.sendMessage(msg);
                    }
                }else {
                    AppUtils.uploadNetworkError("Exec PAD_Get_MoeJtxsInf 'C'",jtbh,sharedPreferences.getString("mac",""));
                }
            }
        }).start();
        activity.getDutouListData(zzdh,mjqs);

    }


    @Override
    public void onDetach() {
        super.onDetach();
        if (spinner_list!=null){
            spinner_list.dismiss();
        }
        dialog.dismiss();
    }


    private boolean isReady(){
        if (spinner.getText().toString().equals("")){
            dialog.setMessage("请先选择原因");
            dialog.setMessageTextColor(Color.RED);
            dialog.show();
            return false;
        }
        return true;
    }




    private void addDutou(final String zzdh, final String yydm, final String wkno){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int allNum=0;
                    int okNum=0;
                    for (int i=0;i<data.size();i++){
                        if (data.get(i).get("isSelect").equals("2")){
                            allNum=allNum+1;
                            String wz=data.get(i).get("wz");
                            String[] temp=wz.split("-");
                            String row=temp[0];
                            String col=temp[1];
                            JSONArray list=NetHelper.getQuerysqlResultJsonArray("Exec PAD_Upd_MoeJtXs  'B','"+zzdh+"'," +
                                    "'','','"+row+"','"+col+"','"+yydm+"','"+wkno+"'");
                            if (list!=null){
                                if (list.length()>0){
                                    if (list.getJSONObject(0).getString("Column1").equals("OK")){
                                        okNum=okNum+1;
                                    }else {
                                        Message msg=handler.obtainMessage();
                                        msg.what=0x103;
                                        msg.obj=list.getJSONObject(0).getString("Column1");
                                        handler.sendMessage(msg);
                                    }
                                }else {
                                    Message msg=handler.obtainMessage();
                                    msg.what=0x103;
                                    msg.obj="提交失败";
                                    handler.sendMessage(msg);
                                }
                            }else {
                                Message msg=handler.obtainMessage();
                                msg.what=0x103;
                                msg.obj="提交失败";
                                handler.sendMessage(msg);
                                AppUtils.uploadNetworkError("Exec PAD_Upd_MoeJtXs  'B'",jtbh,sharedPreferences.getString("mac",""));
                            }
                        }
                    }
                    /*if (okNum==allNum){
                        handler.sendEmptyMessage(0x102);
                    }*/
                    handler.sendEmptyMessage(0x102);

                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }).start();
    }


    public Handler getHandler(){
        return handler;
    }

    @Override
    public void onClick(View v) {
        AppUtils.sendCountdownReceiver(getContext());
        switch (v.getId()){
            case R.id.spinner:
                if (spinner_list!=null){
                    spinner_list.showDownOn(spinner);
                }
                break;
            case R.id.btn_submit:
                if (isReady()){
                    btn_submit.setText("提交中");
                    btn_submit.setEnabled(false);
                    addDutou(zzdh,lbdm,wkno);
                    //addDutou(zzdh,row_text.getText().toString(),col_text.getText().toString(),lbdm,wkno);
                }
                break;
        }
    }
}
