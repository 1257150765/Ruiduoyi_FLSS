package com.ruiduoyi.activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.glongtech.gpio.GpioEvent;
import com.ruiduoyi.Fragment.InfoFragment;
import com.ruiduoyi.Fragment.ManageFragment;
import com.ruiduoyi.Fragment.StatusFragment;
import com.ruiduoyi.Fragment.TestFragment;
import com.ruiduoyi.R;
import com.ruiduoyi.RdyApplication;
import com.ruiduoyi.adapter.ViewPagerAdapter;
import com.ruiduoyi.model.AppDataBase;
import com.ruiduoyi.model.NetHelper;
import com.ruiduoyi.service.GpioService;
import com.ruiduoyi.utils.AppUtils;
import com.ruiduoyi.utils.OnDoubleClickListener;
import com.ruiduoyi.service.SerialPortService;
import com.ruiduoyi.utils.PowerDownLoadUtil;
import com.ruiduoyi.view.AppDialog;
import com.ruiduoyi.view.PopupDialog;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends BaseActivity implements View.OnClickListener, PowerDownLoadUtil.DownLoadListener {
    private ViewPager mviewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private TabLayout mtabLayout;
    private Timer timer_time,timer_CountdownToInfo;
    private TextView time_tx;
    private TextView ymd_tx;
    private ImageView wifi_ig,gpio_1,gpio_2,gpio_3,gpio_4,rdy_logo_img;
    private FrameLayout bottom1,bottom2,bottom3;
    private TextView bottom_text1,bottom_text2,bottom_text3,companyName;
    private String mac;
    private TextView gpioErrorInfo,gpioNoUpload;
    private PopupDialog dialog,updata_tip;
    private BroadcastReceiver gpioSignalReceiver;
    private StatusFragment statusFragment;
    private android.os.Handler handler;
    private BroadcastReceiver returnToInfoReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            AppUtils.removeActivityWithoutThis(MainActivity.this);
            mviewPager.setCurrentItem(0);
            //timer_CountdownToInfo.cancel();
        }
    };
    private BroadcastReceiver countdownReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            CountdownToInfo();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initView();
        //开启刷卡串口服务
        Intent intent_service=new Intent(this, SerialPortService.class);
        startService(intent_service);
        //setSystemTime(MainActivity.this,"20141028.115500");
        intent_service=new Intent(this, GpioService.class);
        intent_service.putExtra("mac",mac);
        intent_service.putExtra("jtbh",jtbh);
        startService(intent_service);
        updateAppVersion();
        //CountdownToInfo();
    }

    public void  initData(){
        sharedPreferences=getSharedPreferences("info",MODE_PRIVATE);
        jtbh=sharedPreferences.getString("jtbh","");
        WifiManager wifiManager=((WifiManager)getApplicationContext().getSystemService(WIFI_SERVICE));
        String mac_temp=wifiManager.getConnectionInfo().getMacAddress();
        //mac_temp="c0:21:0d:94:26:fb";
        if(mac_temp==null&&sharedPreferences.getString("mac","").equals("")) {
            //Toast.makeText(this,"获取网卡物理地址失败，请连接wifi",Toast.LENGTH_LONG).show();
        }else {
            String[] mac_sz = mac_temp.split(":");
            mac = "";
            for (int i = 0; i < mac_sz.length; i++) {
                mac = mac + mac_sz[i];
            }
            jtbh=sharedPreferences.getString("jtbh","");
            //sbID=mac;
        }

        IntentFilter receiverfilter=new IntentFilter();
        receiverfilter.addAction("com.Ruiduoyi.returnToInfoReceiver");
        registerReceiver(returnToInfoReceiver,receiverfilter);
        receiverfilter=new IntentFilter();
        receiverfilter.addAction("com.Ruiduoyi.CountdownToInfo");
        registerReceiver(countdownReceiver,receiverfilter);
        getCompanyName();
        handler=new android.os.Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 0x100://更新wifi和时间
                        List<String>strs=(List<String>)msg.obj;
                        time_tx.setText(strs.get(0));
                        ymd_tx.setText(strs.get(1));
                        int level=msg.arg1;
                        if(level>=0&&level<50){
                            wifi_ig.setImageResource(R.drawable.wifi_4);
                        }else if(level>49&&level<70){
                            wifi_ig.setImageResource(R.drawable.wifi_3);
                        }else if(level>69&&level<90){
                            wifi_ig.setImageResource(R.drawable.wifi_2);
                        }else if(level>89&&level<100){
                            wifi_ig.setImageResource(R.drawable.wifi_1);
                        }else {
                            wifi_ig.setImageResource(R.drawable.empty);
                        }
                        break;
                    case 0x101:
                        ImageView view=(ImageView)msg.obj;
                        view.setImageResource(R.drawable.gpio_false);
                        break;
                    case 0x102:
                        ImageView view2=(ImageView)msg.obj;
                        view2.setImageResource(R.drawable.gpio_true);
                        break;
                    case 0x104:
                        try {
                            final JSONArray array= (JSONArray) msg.obj;
                            dialog.setMessage("当前版本:"+array.getJSONObject(0).getString("oldver")+"\n最新版本:"+array.getJSONObject(0).getString("v_WebAppVer")+"\n是否立即更新？");
                            dialog.getOkbtn().setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.getOkbtn().setEnabled(false);
                                    dialog.setMessage("下载更新包中...");
                                    try {
                                        PowerDownLoadUtil util =  PowerDownLoadUtil.getInstance(getApplicationContext(),MainActivity.this);
                                        util.downloadAPK(array.getJSONObject(0).getString("v_WebAppPath"),
                                                Environment.getExternalStorageDirectory().getPath(),"RdyPmes.apk");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            /*if (array.getJSONObject(0).getString("v_WebAppVer").equals(array.getJSONObject(0).getString("oldver"))){
                                dialog.setMessage("当前版本:"+array.getJSONObject(0).getString("oldver")+"\n最新版本:"+array.getJSONObject(0).getString("v_WebAppVer")+"\n是否立即更新？");
                                dialog.getOkbtn().setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.getOkbtn().setEnabled(false);
                                        dialog.setMessage("下载更新包当中...");
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    *//*NetHelper.DownLoadFileByUrl(array.getJSONObject(0).getString("v_WebAppPath"),
                                                            Environment.getExternalStorageDirectory().getPath(),"RdyPmes.apk");
                                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                                    intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory().getPath()+"/RdyPmes.apk")),
                                                            "application/vnd.android.package-archive");
                                                    startActivity(intent);*//*
                                                    PowerDownLoadUtil util =  PowerDownLoadUtil.getInstance(getApplicationContext(),MainActivity.this);
                                                    util.downloadAPK(array.getJSONObject(0).getString("v_WebAppPath"),
                                                            Environment.getExternalStorageDirectory().getPath(),"RdyPmes.apk");
                                                }catch (JSONException e){
                                                    e.printStackTrace();
                                                }
                                            }
                                        }).start();
                                    }
                                });
                            }else {
                                dialog.setMessage("当前版本:"+array.getJSONObject(0).getString("oldver")+"\n最新版本:"+array.getJSONObject(0).getString("v_WebAppVer")+"\n是否立即更新？");
                                dialog.getOkbtn().setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.getOkbtn().setEnabled(false);
                                        dialog.setMessage("下载更新包当中...");
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    *//*NetHelper.DownLoadFileByUrl(array.getJSONObject(0).getString("v_WebAppPath"),
                                                            Environment.getExternalStorageDirectory().getPath(),"RdyPmes.apk");
                                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                                    intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory().getPath()+"/RdyPmes.apk")),
                                                            "application/vnd.android.package-archive");
                                                    startActivity(intent);*//*
                                                    PowerDownLoadUtil util =  PowerDownLoadUtil.getInstance(getApplicationContext(),MainActivity.this);
                                                    util.downloadAPK(array.getJSONObject(0).getString("v_WebAppPath"),
                                                            Environment.getExternalStorageDirectory().getPath(),"RdyPmes.apk");
                                                }catch (JSONException e){
                                                    e.printStackTrace();
                                                }
                                            }
                                        }).start();
                                    }
                                });
                            }*/
                            dialog.show();
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                        break;
                    case 0x105:
                        //Toast.makeText(MainActivity.this,"更新失败",Toast.LENGTH_SHORT).show();
                        break;
                    case 0x106:
                        companyName.setText((String)msg.obj);
                        break;
                    case 0x110:
                        updata_tip.setMessage((String) msg.obj);
                        updata_tip.show();
                        updata_tip.getCancle_btn().setVisibility(View.VISIBLE);
                        break;
                    case 0x111:
                        //updata_tip.setMessage("下载更新包当中...");
                        Toast.makeText(MainActivity.this,"下载更新包中...",Toast.LENGTH_LONG).show();
                        updata_tip.dismiss();
                        updata_tip.getCancle_btn().setVisibility(View.GONE);

                        break;
                    case 0x112:
                        updata_tip.dismiss();
                    default:
                        break;
                }
            }
        };

    }

    public void initView(){

        //初始化ViewPager
        mviewPager=(ViewPager)findViewById(R.id.viewPager);
        mtabLayout=(TabLayout)findViewById(R.id.tabLayout);
        mtabLayout.addTab(mtabLayout.newTab().setText(getResources().getString(R.string.production_information)));
        mtabLayout.addTab(mtabLayout.newTab().setText(getResources().getString(R.string.engineering_status)));
        mtabLayout.addTab(mtabLayout.newTab().setText(getResources().getString(R.string.production_management)));
        mtabLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        viewPagerAdapter=new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(InfoFragment.newInstance(),getResources().getString(R.string.production_information));
        statusFragment=new StatusFragment();
        viewPagerAdapter.addFragment(statusFragment,getResources().getString(R.string.engineering_status));
        viewPagerAdapter.addFragment(ManageFragment.newInstance(),getResources().getString(R.string.production_management));
        //viewPagerAdapter.addFragment(TestFragment.newInstance(),"作业指导");
        mviewPager.setAdapter(viewPagerAdapter);
        mviewPager.setOffscreenPageLimit(3);
        mtabLayout.setupWithViewPager(mviewPager);
        //初始化底部导航栏
        bottom1=(FrameLayout)findViewById(R.id.bottom_btn1);
        bottom2=(FrameLayout)findViewById(R.id.bottom_btn2);
        bottom3=(FrameLayout)findViewById(R.id.bottom_btn3);
        bottom_text1=(TextView)findViewById(R.id.bottom_btn_text1);
        bottom_text2=(TextView)findViewById(R.id.bottom_btn_text2);
        bottom_text3=(TextView)findViewById(R.id.bottom_btn_text3);
        companyName=(TextView)findViewById(R.id.company_name);
        rdy_logo_img=(ImageView)findViewById(R.id.rdy_logo_img);
        gpioErrorInfo=(TextView)findViewById(R.id.tv_gpio_errorInfo);
        gpioNoUpload=(TextView)findViewById(R.id.tv_gpio_noupload);
        initLogoClieckEvent();
        //初始化gpio
        initGpio();
        mviewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        if (timer_CountdownToInfo!=null){
                            timer_CountdownToInfo.cancel();
                        }
                        bottom_text1.setTextColor(getResources().getColor(R.color.white));
                        bottom_text2.setTextColor(getResources().getColor(R.color.bottom_bt_sl));
                        bottom_text3.setTextColor(getResources().getColor(R.color.bottom_bt_sl));
                        break;
                    case 1:
                        AppUtils.sendCountdownReceiver(MainActivity.this);
                        bottom_text1.setTextColor(getResources().getColor(R.color.bottom_bt_sl));
                        bottom_text2.setTextColor(getResources().getColor(R.color.white));
                        bottom_text3.setTextColor(getResources().getColor(R.color.bottom_bt_sl));
                        break;
                    case 2:
                        AppUtils.sendUpdateOeeReceiver(MainActivity.this);
                        AppUtils.sendCountdownReceiver(MainActivity.this);
                        bottom_text1.setTextColor(getResources().getColor(R.color.bottom_bt_sl));
                        bottom_text2.setTextColor(getResources().getColor(R.color.bottom_bt_sl));
                        bottom_text3.setTextColor(getResources().getColor(R.color.white));
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        bottom1.setOnClickListener(this);
        bottom2.setOnClickListener(this);
        bottom3.setOnClickListener(this);


        //测试
        //mviewPager.setCurrentItem(1);

        time_tx=(TextView)findViewById(R.id.time_tx);
        ymd_tx=(TextView)findViewById(R.id.ymd_tx);
        wifi_ig=(ImageView)findViewById(R.id.wifi_ig);
        updateTime();

        dialog=new PopupDialog(this,400,360);
        dialog.setBackgrounpColor(getResources().getColor(R.color.color_9));
        dialog.getCancle_btn().setText("取消");
        dialog.getOkbtn().setText("确定");
        dialog.setTitle("提示");
        dialog.getCancle_btn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        updata_tip=new PopupDialog(this,450,350);
        updata_tip.setMessageTextColor(Color.BLACK);
        updata_tip.getCancle_btn().setText("确定");
        updata_tip.getOkbtn().setVisibility(View.GONE);
        updata_tip.getCancle_btn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putBoolean("isKnow",true);
                editor.commit();
                updata_tip.dismiss();
            }
        });
    }

    public void initLogoClieckEvent(){
        //logo双击检查更新事件
        rdy_logo_img.setOnTouchListener(new OnDoubleClickListener(new OnDoubleClickListener.DoubleClickCallback() {
            @Override
            public void onDoubleClick() {
                dialog.getOkbtn().setEnabled(true);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONArray array=NetHelper.getQuerysqlResultJsonArray("Exec PAD_Get_WebAddr");
                            if (array!=null){
                                if (array.length()>0){
                                    String oldVersionName= AppUtils.getAppVersionName(MainActivity.this);
                                    String newVersionName=array.getJSONObject(0).getString("v_WebAppVer");
                                    array.getJSONObject(0).put("oldver",oldVersionName);
                                    Message msg=handler.obtainMessage();
                                    msg.what=0x104;
                                    msg.obj=array;
                                    handler.sendMessage(msg);
                                }
                            }else {
                                AppUtils.uploadNetworkError("Exec PAD_Get_WebAddr NetWordError",jtbh,mac);
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        }));
    }

    //定时返回InfoFragment
    private void CountdownToInfo(){
       if (timer_CountdownToInfo!=null){
           timer_CountdownToInfo.cancel();
           timer_CountdownToInfo=null;
       }
       timer_CountdownToInfo=new Timer();
        final TimerTask timerTask=new TimerTask() {
            @Override
            public void run() {
                Intent intent=new Intent();
                intent.setAction("com.Ruiduoyi.returnToInfoReceiver");
                sendBroadcast(intent);
                timer_CountdownToInfo.cancel();
            }
        };
        int time=Integer.parseInt(sharedPreferences.getString("countdownNum","5"));
        Log.e("重置计时",time+"分钟");
        timer_CountdownToInfo.schedule(timerTask,time*60*1000);
    }


    //更新导航栏时间
    private void updateTime(){
        timer_time=new Timer();
        TimerTask timerTask=new TimerTask() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {
                long time=System.currentTimeMillis();
                Date date=new Date(time);
                SimpleDateFormat format1=new SimpleDateFormat("HH:mm");
                SimpleDateFormat format2=new SimpleDateFormat("yyyy年MM月dd日  EEEE");
                List<String>strs=new ArrayList<>();
                strs.add(format1.format(date));
                strs.add(format2.format(date));
                int level = Math.abs(((WifiManager)getApplicationContext().getSystemService(WIFI_SERVICE)).getConnectionInfo().getRssi());
                Message msg=handler.obtainMessage();
                msg.what=0x100;
                msg.obj=strs;
                msg.arg1=level;
                handler.sendMessage(msg);
                AutoUpdateApp();
            }
        };
        timer_time.schedule(timerTask,0,5000);
    }

    private void getCompanyName(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONArray array=NetHelper.getQuerysqlResultJsonArray("Exec PAD_Get_CmpName");
                    if (array!=null){
                        if (array.length()>0){
                            String name=array.getJSONObject(0).getString("cmp_gsmc");
                            Message msg=handler.obtainMessage();
                            msg.what=0x106;
                            msg.obj=name;
                            handler.sendMessage(msg);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    public void initGpio(){
        gpio_1=(ImageView)findViewById(R.id.gpio1);
        gpio_2=(ImageView)findViewById(R.id.gpio2);
        gpio_3=(ImageView)findViewById(R.id.gpio3);
        gpio_4=(ImageView)findViewById(R.id.gpio4);
        gpioSignalReceiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (GpioService.ACTION_UPLOADERROR_COUNT.equals(action)){
                    /*if (dialog !=null && dialog.isShow()){
                        dialog.dismiss();
                    }
                    dialog.setMessage("网络异常，请检查网络！");
                    dialog.show();
                    return;*/
                    String count = intent.getStringExtra("count");
                    if ("".equals(count)){
                        if (gpioErrorInfo.getVisibility() == View.VISIBLE){
                            gpioErrorInfo.setVisibility(View.GONE);
                        }
                    }else {
                        gpioErrorInfo.setVisibility(View.VISIBLE);
                        gpioErrorInfo.setText("信号上传失败("+count+")");
                    }
                    return;
                }else if (GpioService.ACTION_UPLOADERROR_COUNT.equals(action)){
                    String count = intent.getStringExtra("count");
                    gpioNoUpload.setText(count);
                    return;
                }
                int index=intent.getIntExtra("index",5);
                boolean level=intent.getBooleanExtra("level",false);
                switch (index){
                    case 1:
                        Message msg1=handler.obtainMessage();
                        if(level){
                            msg1.what=0x101;
                        }else {
                            msg1.what=0x102;
                            //mac,jtbh,"A","1",ymd_hms,1,"");
                            //dataBase.insertGpio2(mac,jtbh,"A","1",ymd_hms,1,"");
                            //dataBase.selectGpio();
                        }
                        msg1.obj=gpio_1;
                        handler.sendMessage(msg1);
                        break;
                    case 2:
                        Message msg2=handler.obtainMessage();
                        if(level){
                            msg2.what=0x101;
                        }else {
                            msg2.what=0x102;
                            //dataBase.insertGpio(mac,jtbh,"A","2",ymd_hms,1,"");
                            //dataBase.insertGpio2(mac,jtbh,"A","2",ymd_hms,1,"");
                        }
                        msg2.obj=gpio_2;
                        handler.sendMessage(msg2);
                        break;
                    case 3:
                        Message msg3=handler.obtainMessage();
                        if(level){
                            msg3.what=0x101;
                        }else {
                            msg3.what=0x102;
                            //dataBase.insertGpio(mac,jtbh,"A","3",ymd_hms,1,"");
                            //dataBase.insertGpio2(mac,jtbh,"A","3",ymd_hms,1,"");
                        }
                        msg3.obj=gpio_3;
                        handler.sendMessage(msg3);
                        break;
                    case 4:
                        Message msg4=handler.obtainMessage();
                        if(level){
                            msg4.what=0x101;
                        }else {
                            msg4.what=0x102;
                            //dataBase.insertGpio(mac,jtbh,"A","4",ymd_hms,1,"");
                            //dataBase.insertGpio2(mac,jtbh,"A","4",ymd_hms,1,"");
                        }
                        msg4.obj=gpio_4;
                        handler.sendMessage(msg4);
                        break;
                    default:
                        break;
                }
            }
        };
        IntentFilter receiverfilter=new IntentFilter();
        receiverfilter.addAction(GpioService.ACTION_GPIOSINAL);
        receiverfilter.addAction(GpioService.ACTION_NOUPLOAD_COUNT);
        receiverfilter.addAction(GpioService.ACTION_UPLOADERROR_COUNT);
        registerReceiver(gpioSignalReceiver,receiverfilter);
    }

    public StatusFragment getStatusFragment() {
        return statusFragment;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(returnToInfoReceiver);
        unregisterReceiver(gpioSignalReceiver);
        unregisterReceiver(countdownReceiver);
        timer_time.cancel();
        if (dialog!=null){
            if (dialog.isShow()){
                dialog.dismiss();
            }
        }
        if (timer_CountdownToInfo!=null){
            timer_CountdownToInfo.cancel();
        }
        AppUtils.removAllActivity();
        System.exit(0);
    }

    private void AutoUpdateApp(){
        int auto_updata_num=sharedPreferences.getInt("auto_updata_num",0);
        Log.e("auto_updata_num",auto_updata_num+"");
        String delay_time=getResources().getString(R.string.update_app_delay_time);
        if (auto_updata_num<Integer.parseInt(delay_time)){
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putInt("auto_updata_num",auto_updata_num+1);
            editor.commit();
        }else {
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putInt("auto_updata_num",0);
            editor.commit();
            updateAppVersion();
        }

    }

    private void updateAppVersion(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONArray list=NetHelper.getQuerysqlResultJsonArray("Exec PAD_Get_WebAddr");
                    if (list!=null){
                        if (list.length()>0){
                            String oldVersionName= AppUtils.getAppVersionName(MainActivity.this);
                            String newVersionName=list.getJSONObject(0).getString("v_WebAppVer");
                            if (!oldVersionName.equals(newVersionName)){
                                int i=60;
                                while (true){
                                    boolean isKnow = sharedPreferences.getBoolean("isKnow", false);
                                    i=i-1;
                                    if (i>0){
                                        if (isKnow){
                                            continue;
                                        }
                                        try {
                                            Message msg=handler.obtainMessage();
                                            msg.what=0x110;
                                            msg.obj="将在"+i+"秒后自动更新，请保存数据";
                                            handler.sendMessage(msg);
                                            Thread.currentThread().sleep(1000);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }else {
                                        handler.sendEmptyMessage(0x111);
                                        /*NetHelper.downLoadFileByUrl(list.getJSONObject(0).getString("v_WebAppPath"),
                                                Environment.getExternalStorageDirectory().getPath(),"RdyPmes.apk");
                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                        intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory().getPath()+"/RdyPmes.apk")),
                                                "application/vnd.android.package-archive");
                                        startActivity(intent);*/
                                        PowerDownLoadUtil util =  PowerDownLoadUtil.getInstance(getApplicationContext(),MainActivity.this);
                                        util.downloadAPK(list.getJSONObject(0).getString("v_WebAppPath"),
                                                Environment.getExternalStorageDirectory().getPath(),"RdyPmes.apk");
                                        handler.sendEmptyMessage(0x112);

                                        break;
                                    }
                                }

                            }
                            SharedPreferences.Editor editor=sharedPreferences.edit();
                            editor.putBoolean("isKnow",false);
                            editor.commit();

                        }
                    }else {
                        NetHelper.uploadNetworkError("Exec PAD_Get_WebAddr NetWordError",jtbh,mac);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bottom_btn1:
                bottom_text1.setTextColor(getResources().getColor(R.color.white));
                bottom_text2.setTextColor(getResources().getColor(R.color.bottom_bt_sl));
                bottom_text3.setTextColor(getResources().getColor(R.color.bottom_bt_sl));
                mviewPager.setCurrentItem(0);
                break;
            case R.id.bottom_btn2:
                AppUtils.sendCountdownReceiver(MainActivity.this);
                bottom_text1.setTextColor(getResources().getColor(R.color.bottom_bt_sl));
                bottom_text2.setTextColor(getResources().getColor(R.color.white));
                bottom_text3.setTextColor(getResources().getColor(R.color.bottom_bt_sl));
                mviewPager.setCurrentItem(1);
                break;
            case R.id.bottom_btn3:
                AppUtils.sendCountdownReceiver(MainActivity.this);
                bottom_text1.setTextColor(getResources().getColor(R.color.bottom_bt_sl));
                bottom_text2.setTextColor(getResources().getColor(R.color.bottom_bt_sl));
                bottom_text3.setTextColor(getResources().getColor(R.color.white));
                mviewPager.setCurrentItem(2);
                break;
        }
    }


    @Override
    public void onPrepare() {

    }
    DecimalFormat decimalFormat=new DecimalFormat(".00");//构造方法的字符格式这里如果小数不足2位,会以0补足.

    @Override
    public void onDownLoading(int progress, int totalBytes) {
        if (dialog != null && dialog.isShow()){
            float f = progress / totalBytes * 100f;
            String p=decimalFormat.format(f);//format 返回的是字符串
            dialog.setMessage("下载更新包中...\n"+"已下载："+(progress/1024)+"("+p+"%)\n 文件大小:"+(totalBytes/1024));
        }
    }

    @Override
    public void onSucceed() {
        if (updata_tip != null && updata_tip.isShow()){
            updata_tip.dismiss();
        }
        if (dialog != null && dialog.isShow()){
            dialog.dismiss();
        }
    }

    @Override
    public void onError(String errorInfo) {
        if (dialog != null){
            dialog.setMessage(errorInfo);
            dialog.show();
        }
    }
}
