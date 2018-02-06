package com.ruiduoyi.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ruiduoyi.R;
import com.ruiduoyi.activity.BlYyfxActivity;
import com.ruiduoyi.activity.GycsActivity;
import com.ruiduoyi.activity.QsfhActivity;
import com.ruiduoyi.activity.SbxxActivity;
import com.ruiduoyi.activity.MjxxActivity;
import com.ruiduoyi.activity.PzglActivity;
import com.ruiduoyi.activity.ScrzActivity;
import com.ruiduoyi.activity.DialogGActivity;
import com.ruiduoyi.activity.SlcsActivity;
import com.ruiduoyi.activity.ZyzdActivity;
import com.ruiduoyi.adapter.ZLRecyclerViewAdapter;
import com.ruiduoyi.model.NetHelper;
import com.ruiduoyi.utils.AppUtils;
import com.ruiduoyi.view.PopupDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class StatusFragment extends Fragment implements View.OnClickListener{
    private CardView cardView_js,cardView_gdgl,cardView_blfx,cardView_ycfx,cardView_qsbg,cardView_qsfh,cardView_gycs,cardView_mjxx,
            cardView_sbdj,cardView_sbxx,cardView_pzgl,cardView_scrz,cardView_zyzd,cardView_slcs,cardView_hddj;
    private String startType,startZldm,startZlmc;
    private Animation anim;
    private SharedPreferences sharedPreferences;
    private PopupDialog dialog;
    private FrameLayout Yc_unread_bg,Dj_unread_bg;
    private TextView Yc_unread_text,Dj_unread_text;
    private RecyclerView gRecycler,bRecycler;
    private List<Map<String,String>>gdata,bdata;
    private boolean isFirst=true;
    private ProgressBar progressBar;
    public StatusFragment() {

    }


    public static StatusFragment newInstance() {
        StatusFragment fragment = new StatusFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        anim= AnimationUtils.loadAnimation(getContext(),R.anim.scale_anim);
        sharedPreferences=getContext().getSharedPreferences("info", Context.MODE_PRIVATE);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_status, container, false);
        initView(view);
        getZlData();
        return view;
    }

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0x100:
                    try {
                        JSONObject list= (JSONObject) msg.obj;
                        startType=list.getString("v_yytype");
                        startZldm=list.getString("v_zldm");
                        startZlmc=list.getString("v_zlmc");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 0x101:
                    progressBar.setVisibility(View.GONE);
                    break;
                case 0x102:
                    gdata=new ArrayList<>();
                    bdata=new ArrayList<>();
                    initRecyclerView((JSONArray) msg.obj,gRecycler,"OPR",gdata);
                    initRecyclerView((JSONArray) msg.obj,bRecycler,"DOC",bdata);
                    break;
            }
        }
    };


    private void exitEven(){
        progressBar.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String zldming;
                String zlmcing;
                String waring;

                //请求当前指令及是否超时信息
                try {
                    JSONArray list2= NetHelper.getQuerysqlResultJsonArray("Exec PAD_Get_JtmZtInfo '"+sharedPreferences.getString("jtbh","")+"'");
                    if(list2!=null) {
                        if (list2.length() > 0) {
                            zldming= list2.getJSONObject(0).getString("kbl_zldm");
                            waring = list2.getJSONObject(0).getString("kbl_waring");
                        }else {
                            return;
                        }
                    }else {
                        return;
                    }



                    JSONArray list= NetHelper.getQuerysqlResultJsonArray("Exec PAD_Get_ZlmYywh 'A','"+sharedPreferences.getString("jtbh","")+"','"+zldming+"'");

                    if (list!=null){
                        if (list.length()>0){
                            String startType=list.getJSONObject(0).getString("v_yytype");
                            zlmcing=list.getJSONObject(0).getString("v_zlmc");
                            switch (startType) {
                                case "A":
                                    Intent intent_g21 = new Intent(getContext(), DialogGActivity.class);
                                    intent_g21.putExtra("zldm", getContext().getString(R.string.js));
                                    intent_g21.putExtra("title", "结束");
                                    intent_g21.putExtra("type", "OPR");
                                    startActivity(intent_g21);
                                    break;
                                case "B":
                                    Intent intent1 = new Intent(getContext(), BlYyfxActivity.class);
                                    intent1.putExtra("title", zlmcing);
                                    intent1.putExtra("zldm",zldming);
                                    startActivity(intent1);
                                    break;
                                case "C":
                                    Intent intent2 = new Intent(getContext(), BlYyfxActivity.class);
                                    intent2.putExtra("title", zlmcing);
                                    intent2.putExtra("zldm", zldming);
                                    startActivity(intent2);
                                    break;
                                case "S":
                                    if (waring.equals("1")){//如果超时了则必须要弹出蓝框
                                        Intent intent_blyyfx=new Intent(getContext(),BlYyfxActivity.class);
                                        intent_blyyfx.putExtra("title",zlmcing);
                                        intent_blyyfx.putExtra("zldm",zldming);
                                        startActivity(intent_blyyfx);
                                    }else {//如果没有超时则直接启动结束
                                        Intent intent_js = new Intent(getContext(), DialogGActivity.class);
                                        intent_js.putExtra("zldm", getContext().getString(R.string.js));
                                        intent_js.putExtra("title", "结束");
                                        intent_js.putExtra("type", "OPR");
                                        startActivity(intent_js);
                                    }
                                    break;
                                default:
                                    Intent intent_g3 = new Intent(getContext(), DialogGActivity.class);
                                    intent_g3.putExtra("zldm", getContext().getString(R.string.js));
                                    intent_g3.putExtra("title", "结束");
                                    intent_g3.putExtra("type", "OPR");
                                    startActivity(intent_g3);
                                    break;
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }finally {
                    handler.sendEmptyMessage(0x101);
                }
            }
        }).start();
    }

    public void initView(View view){
        cardView_js=(CardView)view.findViewById(R.id.js);

        cardView_gdgl=(CardView)view.findViewById(R.id.gdgl);
        cardView_blfx=(CardView)view.findViewById(R.id.blfx);
        cardView_ycfx=(CardView)view.findViewById(R.id.ycfx);
        cardView_qsbg=(CardView)view.findViewById(R.id.jtjqsbg);
        cardView_hddj=(CardView)view.findViewById(R.id.hddj);
        cardView_gycs=(CardView)view.findViewById(R.id.gycs);
        cardView_sbdj=(CardView)view.findViewById(R.id.djby);
        cardView_sbxx=(CardView)view.findViewById(R.id.sbxx);
        cardView_mjxx=(CardView)view.findViewById(R.id.mjxx);
        cardView_pzgl=(CardView)view.findViewById(R.id.pzgl);
        cardView_scrz=(CardView)view.findViewById(R.id.scrz);
        cardView_zyzd=(CardView)view.findViewById(R.id.zyzd);
        cardView_slcs=(CardView)view.findViewById(R.id.slcs);
        cardView_qsfh=(CardView)view.findViewById(R.id.qsfh);
        gRecycler=(RecyclerView) view.findViewById(R.id.zl_list);
        bRecycler=(RecyclerView) view.findViewById(R.id.bRecycler);
        progressBar=(ProgressBar)view.findViewById(R.id.wait_progress);

        cardView_js.setOnClickListener(this);
        cardView_gdgl.setOnClickListener(this);
        cardView_blfx.setOnClickListener(this);
        cardView_ycfx.setOnClickListener(this);
        cardView_qsbg.setOnClickListener(this);
        cardView_hddj.setOnClickListener(this);
        cardView_gycs.setOnClickListener(this);
        cardView_sbdj.setOnClickListener(this);
        cardView_sbxx.setOnClickListener(this);
        cardView_mjxx.setOnClickListener(this);
        cardView_pzgl.setOnClickListener(this);
        cardView_scrz.setOnClickListener(this);
        cardView_zyzd.setOnClickListener(this);
        cardView_slcs.setOnClickListener(this);
        cardView_qsfh.setOnClickListener(this);


        dialog=new PopupDialog(getActivity(),400,360);
        dialog.setTitle("提示");
        dialog.getCancle_btn().setVisibility(View.GONE);
        dialog.getOkbtn().setText("确定");
        dialog.getOkbtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setMessage("正在指令状态下，请先结束指令");
    }


    private void initRecyclerView(JSONArray lists, RecyclerView recyclerView, final String place, final List<Map<String,String>>data){
        int max=0;
        if (place.equals("OPR")){
            max=19;
        }else if (place.equals("DOC")){
            max=13;
        }
        try {
            for (int i=0;i<lists.length();i++){
                if (lists.getJSONObject(i).getString("zlm_type").equals(place)){
                    Map<String,String>map=new HashMap<>();
                    map.put("zldm",lists.getJSONObject(i).getString("zlm_zldm"));
                    map.put("type",lists.getJSONObject(i).getString("zlm_cardflag"));
                    map.put("zlmc",lists.getJSONObject(i).getString("zlm_zlmc"));
                    data.add(map);
                    if (data.size()>max){
                        break;
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        ZLRecyclerViewAdapter adapter=new ZLRecyclerViewAdapter(getContext(),data){
            @Override
            public void onBindViewHolder(final ZLRecyclerViewAdapter.MyViewHolder holder, int position, List<Object> payloads) {
                final Map<String,String>map=data.get(position);
                holder.zlmc_text.setText(map.get("zlmc"));
                if (place.equals("OPR")){
                    holder.zl_btn.setCardBackgroundColor(getResources().getColor(R.color.zl_btn));
                }else {
                    holder.zl_btn.setCardBackgroundColor(getResources().getColor(R.color.zl_bbtn));
                }
                if (map.get("zldm").equals(getResources().getString(R.string.ycfx))){
                    Yc_unread_bg=holder.unread_bg;
                    Yc_unread_text=holder.unread_text;
                }
                if (map.get("zldm").equals(getResources().getString(R.string.sbdj))){
                    Dj_unread_bg=holder.unread_bg;
                    Dj_unread_text=holder.unread_text;
                }
                holder.zl_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        progressBar.setVisibility(View.VISIBLE);
                        AppUtils.sendCountdownReceiver(getContext());
                        holder.zl_btn.startAnimation(anim);
                        if (map.get("type").equals("OPR")){
                            if (isReady(map.get("zldm"))){
                                startActivityByNetResult(map.get("zldm"),map.get("zlmc"),map.get("type"));
                            }else {
                                progressBar.setVisibility(View.GONE);
                            }
                        }else if (map.get("type").equals("DOC")){
                            progressBar.setVisibility(View.GONE);
                            Intent intent=new Intent(getContext(),DialogGActivity.class);
                            intent.putExtra("title",map.get("zlmc"));
                            intent.putExtra("zldm",map.get("zldm"));
                            intent.putExtra("type","DOC");
                            startActivity(intent);
                        }else if (map.get("type").equals("NO")){
                            progressBar.setVisibility(View.GONE);
                            String zldm_no=map.get("zldm");
                            Intent no_intent;
                            if (zldm_no.equals(getResources().getString(R.string.sbxx))){
                                no_intent=new Intent(getContext(),SbxxActivity.class);
                                startActivity(no_intent);
                            }else if (zldm_no.equals(getResources().getString(R.string.mjxx))){
                                no_intent=new Intent(getContext(),MjxxActivity.class);
                                startActivity(no_intent);
                            }else if (zldm_no.equals(getResources().getString(R.string.pzgl))){
                                no_intent=new Intent(getContext(),PzglActivity.class);
                                startActivity(no_intent);
                            }else if (zldm_no.equals(getResources().getString(R.string.scrz))){
                                no_intent=new Intent(getContext(),ScrzActivity.class);
                                startActivity(no_intent);
                            }else if (zldm_no.equals(getResources().getString(R.string.gycs))){
                                no_intent=new Intent(getContext(),GycsActivity.class);
                                startActivity(no_intent);
                            }else if (zldm_no.equals(getResources().getString(R.string.zyzd))){
                                no_intent=new Intent(getContext(),ZyzdActivity.class);
                                startActivity(no_intent);
                            }
                        }
                    }
                });
            }
        };
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(),7){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

    }

    private void startActivityByNetResult(final String zldm, final String title, final String type){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONArray list= NetHelper.getQuerysqlResultJsonArray("Exec PAD_Get_ZlmYywh 'A','"+sharedPreferences.getString("jtbh","")+"','"+zldm+"'");
                    if (list!=null){
                        if (list.length()>0){
                            Message msg=handler.obtainMessage();
                            msg.obj=list.getJSONObject(0);
                            msg.what=0x100;
                            handler.sendMessage(msg);
                            Intent intent;
                            switch (list.getJSONObject(0).getString("v_yytype")){
                                case "A":
                                    intent=new Intent(getContext(), BlYyfxActivity.class);
                                    intent.putExtra("title",title);
                                    intent.putExtra("zldm",zldm);
                                    startActivity(intent);
                                    break;
                                case "B":
                                    intent=new Intent(getContext(), DialogGActivity.class);
                                    intent.putExtra("title",title);
                                    intent.putExtra("zldm",zldm);
                                    intent.putExtra("type",type);
                                    startActivity(intent);
                                    break;
                                case "C":
                                    intent=new Intent(getContext(), BlYyfxActivity.class);
                                    intent.putExtra("title",title);
                                    intent.putExtra("zldm",zldm);
                                    startActivity(intent);
                                    break;
                                case "S":
                                    //请求当前指令及是否超时信息
                                    JSONArray list2= NetHelper.getQuerysqlResultJsonArray("Exec PAD_Get_JtmZtInfo '"+sharedPreferences.getString("jtbh","")+"'");
                                    if(list2!=null) {
                                        if (list2.length() > 0) {
                                            String waring = list2.getJSONObject(0).getString("kbl_waring");
                                            if (waring.equals("1")){//如果超时了则必须要弹出蓝框
                                                Intent intent_blyyfx=new Intent(getContext(),BlYyfxActivity.class);
                                                intent_blyyfx.putExtra("zldm",zldm);
                                                intent_blyyfx.putExtra("title",title);
                                                startActivity(intent_blyyfx);
                                            }else {//如果没有超时则直接启动结束
                                                Intent intent_js = new Intent(getContext(), DialogGActivity.class);
                                                intent_js.putExtra("zldm", zldm);
                                                intent_js.putExtra("title", title);
                                                intent_js.putExtra("type", "OPR");
                                                startActivity(intent_js);
                                            }
                                        }
                                    }
                                    break;
                                default:
                                    intent=new Intent(getContext(), DialogGActivity.class);
                                    intent.putExtra("title",title);
                                    intent.putExtra("zldm",zldm);
                                    intent.putExtra("type",type);
                                    startActivity(intent);
                                    break;
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }finally {
                    handler.sendEmptyMessage(0x101);
                }
            }
        }).start();
    }

    private void getZlData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONArray list=NetHelper.getQuerysqlResultJsonArray("Exec PAD_Get_ZlmMstr");
                if (list!=null){
                    Message msg=handler.obtainMessage();
                    msg.what=0x102;
                    msg.obj=list;
                    handler.sendMessage(msg);
                }else {
                    try {
                        Thread.currentThread().sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    getZlData();
                }
            }
        }).start();
    }


    private boolean isReady(String zldm){
        String zldming=sharedPreferences.getString("zldm_ss","");
        if (zldm.equals(getResources().getString(R.string.rysg))){
            return true;
        } else if (zldming.equals("50")|zldming.equals("51")|zldming.equals("52")|zldming.equals("53")|zldming.equals("54")|
                zldming.equals("55")|zldming.equals("56")|zldming.equals("57")|zldming.equals("58")|zldming.equals("60")|zldming.equals("61")|zldming.equals("62")|
                zldming.equals("63")|zldming.equals("64")|zldming.equals("65")|zldming.equals("66")|
                zldming.equals("67")|zldming.equals("68")|zldming.equals("69")|zldming.equals("70")){

            dialog.show();
            return false;
        }else if (zldming.equals("59")){
            if (zldm.equals(getResources().getString(R.string.tiaoji))|
                    zldm.equals(getResources().getString(R.string.pzyc))|
                    zldm.equals(getResources().getString(R.string.mjwx))){
                return true;
            }else {
                dialog.show();
                return false;
            }
        }
        return true;
    }


    public void setYcUnread(String num){
        if (Yc_unread_text!=null&&Yc_unread_bg!=null){
            if (num.equals("0")|num.equals("")){
                Yc_unread_bg.setVisibility(View.GONE);
            }else if (num.length()>2){
                Yc_unread_text.setVisibility(View.VISIBLE);
                Yc_unread_text.setText("");
                Yc_unread_bg.setBackground(getResources().getDrawable(R.drawable.unread_more));
            }else {
                Yc_unread_text.setText(num);
                Yc_unread_bg.setVisibility(View.VISIBLE);
                Yc_unread_bg.setBackground(getResources().getDrawable(R.drawable.unread_red));
            }
        }
    }


    public void setDjUnread(String num){
        if (Dj_unread_text!=null&&Dj_unread_bg!=null){
            if (num.equals("0")|num.equals("")){
                Dj_unread_bg.setVisibility(View.GONE);
            }else if (num.length()>2){
                Dj_unread_text.setVisibility(View.VISIBLE);
                Dj_unread_text.setText("");
                Dj_unread_bg.setBackground(getResources().getDrawable(R.drawable.unread_more));
            }else {
                Dj_unread_text.setText(num);
                Dj_unread_bg.setVisibility(View.VISIBLE);
                Dj_unread_bg.setBackground(getResources().getDrawable(R.drawable.unread_red));
            }
        }
    }


    //遍历RecyclerView获取三个textView并将其颜色变成红色
    public void setNGTextColor(int Color){
        if (isFirst){
            isFirst=false;
            return;
        }
        if (gdata!=null){
            for (int i=0;i<gdata.size();i++){
                String zldm=gdata.get(i).get("zldm");
                if (zldm.equals(getResources().getString(R.string.tiaoji))|zldm.equals(getResources().getString(R.string.pzyc))|
                        zldm.equals(getResources().getString(R.string.mjwx))){
                    if (gRecycler!=null){
                        TextView zltext=(TextView) gRecycler.getChildAt(i).findViewById(R.id.zlmc);
                        zltext.setTextColor(Color);
                    }
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        AppUtils.sendCountdownReceiver(getContext());
        switch (v.getId()){
            case R.id.sbxx://设备信息
                cardView_sbxx.startAnimation(anim);
                Intent intent=new Intent(getContext(), SbxxActivity.class);
                startActivity(intent);
                break;
            case R.id.mjxx:
                cardView_mjxx.startAnimation(anim);
                Intent intent_b2=new Intent(getContext(), MjxxActivity.class);
                startActivity(intent_b2);
                break;
            case R.id.gdgl:
                cardView_gdgl.startAnimation(anim);
                Intent intent_b3=new Intent(getContext(),DialogGActivity.class );
                intent_b3.putExtra("title","工单管理");
                intent_b3.putExtra("zldm",getContext().getString(R.string.gdgl));
                intent_b3.putExtra("type","DOC");
                startActivity(intent_b3);
                break;
            case R.id.pzgl:
                cardView_pzgl.startAnimation(anim);
                Intent intent_b4=new Intent(getContext(), PzglActivity.class);
                startActivity(intent_b4);
                break;
            case R.id.scrz:
                cardView_scrz.startAnimation(anim);
                Intent intent_b6=new Intent(getContext(), ScrzActivity.class);
                startActivity(intent_b6);
                break;
            case R.id.ycfx:
                cardView_ycfx.startAnimation(anim);
                Intent intent_b7=new Intent(getContext(),DialogGActivity.class );
                intent_b7.putExtra("title","异常分析");
                intent_b7.putExtra("zldm",getContext().getString(R.string.ycfx));
                intent_b7.putExtra("type","DOC");
                startActivity(intent_b7);
                break;
            case R.id.blfx:
                cardView_blfx.startAnimation(anim);
                Intent intent_b8=new Intent(getContext(), DialogGActivity.class);
                intent_b8.putExtra("title","不良分析");
                intent_b8.putExtra("zldm",getContext().getString(R.string.blfx));
                intent_b8.putExtra("type","DOC");
                startActivity(intent_b8);
                break;
            case R.id.jtjqsbg:
                cardView_qsbg.startAnimation(anim);
                Intent intent_10=new Intent(getContext(), DialogGActivity.class);
                intent_10.putExtra("title","腔数变更");
                intent_10.putExtra("zldm",getResources().getString(R.string.jtjqsbg));
                intent_10.putExtra("type","DOC");
                startActivity(intent_10);
                break;
            case R.id.hddj:
                cardView_hddj.startAnimation(anim);
                Intent intent_hddj=new Intent(getContext(), DialogGActivity.class);
                intent_hddj.putExtra("title","耗电登记");
                intent_hddj.putExtra("zldm",getResources().getString(R.string.hddj));
                intent_hddj.putExtra("type","DOC");
                startActivity(intent_hddj);
                break;
            case R.id.gycs:
                cardView_gycs.startAnimation(anim);
                Intent intent_gycs=new Intent(getContext(), GycsActivity.class);
                startActivity(intent_gycs);
                break;
            case R.id.zyzd:
                cardView_zyzd.startAnimation(anim);
                Intent intent_zyzd=new Intent(getContext(), ZyzdActivity.class);
                startActivity(intent_zyzd);
                break;
            case R.id.djby:
                cardView_sbdj.startAnimation(anim);
                Intent intent_djby=new Intent(getContext(), DialogGActivity.class);
                intent_djby.putExtra("title","设备点检");
                intent_djby.putExtra("type","DOC");
                intent_djby.putExtra("zldm",getResources().getString(R.string.sbdj));
                startActivity(intent_djby);
                break;
            case R.id.slcs:
                cardView_slcs.startAnimation(anim);
                Intent intent_slcs=new Intent(getContext(), DialogGActivity.class);
                intent_slcs.putExtra("title","上料参数");
                intent_slcs.putExtra("type","DOC");
                intent_slcs.putExtra("zldm",getResources().getString(R.string.slcs));
                startActivity(intent_slcs);
            break;
            case R.id.qsfh:
                cardView_qsfh.startAnimation(anim);
                Intent intent_qsfh=new Intent(getContext(), DialogGActivity.class);
                intent_qsfh.putExtra("title","腔数复核");
                intent_qsfh.putExtra("type","DOC");
                intent_qsfh.putExtra("zldm",getResources().getString(R.string.qsfh));
                startActivity(intent_qsfh);
                break;
            case R.id.js:
                cardView_js.startAnimation(anim);
                exitEven();
                break;
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dialog!=null){
            dialog.dismiss();
        }
    }
}



