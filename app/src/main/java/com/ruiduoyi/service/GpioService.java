package com.ruiduoyi.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.Gpio;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.IntDef;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.glongtech.gpio.GpioEvent;
import com.ruiduoyi.R;
import com.ruiduoyi.model.AppDataBase;
import com.ruiduoyi.model.NetHelper;
import com.ruiduoyi.utils.AppUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class GpioService extends Service {
    private int i=0;
    private GpioEvent event_gpio;
    private AppDataBase dataBase;
    private SharedPreferences sharedPreferences;
    private String mac,jtbh;
    private int count;

    /*private File file;
    private PrintStream out;
    private FileOutputStream fileOutputStream;*/
    public GpioService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.w("gpio_oncreat","!");
        initData();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent!=null){
            jtbh=intent.getStringExtra("jtbh");
            mac=intent.getStringExtra("mac");
        }else {
            jtbh=sharedPreferences.getString("jtbh","");
            mac=sharedPreferences.getString("mac","");
            NetHelper.URL=getResources().getString(R.string.service_ip)+":8080/Service.asmx";
           //NetHelper.URL=getResources().getString(R.string.service_ip)+"/Service1.asmx";
        }
        Log.w("starCommand",jtbh+"   "+mac);
        //initGpio();
        return super.onStartCommand(intent, flags, startId);
    }

    private void initData(){

        /*file=new File(Environment.getExternalStorageDirectory().getPath()+"/gpio.txt");
        if (!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            fileOutputStream=new FileOutputStream(file);
            out=new PrintStream(fileOutputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }*/



        dataBase=new AppDataBase(this);
        sharedPreferences=getSharedPreferences("info",MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("isUploadFinish","OK");
        editor.commit();
        initGpio();
        updateGpio();
    }



    public void initGpio(){
        if ("true".equals(getResources().getString(R.string.isTest))){
            Toast.makeText(this,"Gpio不可用",Toast.LENGTH_LONG).show();
            return;
        }
        int g1= Gpio.SetGpioInput("gpio1");
        int g2=Gpio.SetGpioInput("gpio2");
        int g3=Gpio.SetGpioInput("gpio3");
        int g4=Gpio.SetGpioInput("gpio4");
        Log.e("gpio_input",g1+" "+g2+" "+g3+" "+g4);
        event_gpio = new GpioEvent() {
            @Override
            public void onGpioSignal(int index,boolean level) {
                long time=System.currentTimeMillis();
                Date date=new Date(time);
                SimpleDateFormat format2=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

                //发广播给MainActivity接收
                final String ymd_hms=format2.format(date);
                Intent intent=new Intent();
                intent.putExtra("index",index);
                intent.putExtra("level",level);
                intent.setAction(ACTION_GPIOSINAL);
                getApplicationContext().sendBroadcast(intent);
                switch (index){
                    case 1:
                        if(level){
                        }else {
                            dataBase.insertGpio(mac,jtbh,"A","1",ymd_hms,1,"");
                            //out.println(mac+"\t"+jtbh+"\t"+"A\t1\t"+ymd_hms+"\t"+"1\t");
                            //dataBase.insertGpio2(mac,jtbh,"A","1",ymd_hms,1,"");
                            //dataBase.selectGpio();
                        }
                        break;
                    case 2:
                        if(level){

                        }else {
                            dataBase.insertGpio(mac,jtbh,"A","2",ymd_hms,1,"");
                            //out.println(mac+"\t"+jtbh+"\t"+"A\t2\t"+ymd_hms+"\t"+"1\t");
                            //dataBase.insertGpio2(mac,jtbh,"A","1",ymd_hms,1,"");
                            //dataBase.selectGpio();
                        }

                        break;
                    case 3:
                        if(level){

                        }else {
                            dataBase.insertGpio(mac,jtbh,"A","3",ymd_hms,1,"");
                            //out.println(mac+"\t"+jtbh+"\t"+"A\t2\t"+ymd_hms+"\t"+"1\t");
                            //dataBase.insertGpio2(mac,jtbh,"A","1",ymd_hms,1,"");
                            //dataBase.selectGpio();
                        }
                        break;
                    case 4:
                        if(level){

                        }else {
                            dataBase.insertGpio(mac,jtbh,"A","4",ymd_hms,1,"");
                            //out.println(mac+"\t"+jtbh+"\t"+"A\t4\t"+ymd_hms+"\t"+"1\t");
                            //dataBase.insertGpio2(mac,jtbh,"A","1",ymd_hms,1,"");
                            //dataBase.selectGpio();
                        }
                        break;
                    default:
                        break;
                }
            }
        };
        event_gpio.MyObserverStart();
    }

    public static final String ACTION_NOUPLOAD_COUNT = "com.ruiduoyi.GpioNoUploadCount";
    public static final String ACTION_UPLOADERROR_COUNT = "com.ruiduoyi.GpioUploadError";
    public static final String ACTION_GPIOSINAL = "com.ruiduoyi.GpioSinal";
    private ScheduledThreadPoolExecutor executor;

    private void updateGpio(){
        executor =  new ScheduledThreadPoolExecutor(2);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    long count = dataBase.getNoUploadGpioCount();
                    Intent intent = new Intent(ACTION_NOUPLOAD_COUNT);
                    intent.putExtra("count",""+count);
                    sendBroadcast(intent);
                    String sql="";
                    List<Map<String,String>> list=dataBase.selectGpio();
                    String isUploadFinish=sharedPreferences.getString("isUploadFinish","NO");
                    if (isUploadFinish.equals("OK")){
                        SharedPreferences.Editor editor1=sharedPreferences.edit();
                        editor1.putString("isUploadFinish","NO");
                        editor1.commit();
                        for (int i=0;i<list.size();i++){
                            Map<String,String>map=list.get(i);
                            String mac=map.get("mac");
                            String jtbh=map.get("jtbh");
                            String zldm=map.get("zldm");
                            String gpio=map.get("gpio");
                            String time=map.get("time");
                            String num=map.get("num");
                            String desc=map.get("desc");
                            sql="exec PAD_SrvDataUp '"+mac+"','"+jtbh+"','"+zldm+"','"+gpio+"','"+time+"',"+num+",'"+desc+"'\n";
                            JSONArray list_result= NetHelper.getQuerysqlResultJsonArray(sql);
                            if (list_result!=null){
                                if (list_result.length()>0){
                                    try {
                                        if (list_result.getJSONObject(0).getString("Column1").equals("OK")){
                                            //handler.sendEmptyMessage(0x106);
                                            //如果有一次上传成功了，说明网络是通的，先隐藏信息
                                            Intent intent2 = new Intent(ACTION_UPLOADERROR_COUNT);
                                            intent2.putExtra("count","");
                                            sendBroadcast(intent2);
                                            GpioService.this.count = 0;
                                            dataBase.deleteGpio(time);
                                            SharedPreferences.Editor editor2=sharedPreferences.edit();
                                            editor2.putString("isUploadFinish","OK");
                                            editor2.commit();
                                        }else {
                                            SharedPreferences.Editor editor2=sharedPreferences.edit();
                                            editor2.putString("isUploadFinish","OK");
                                            editor2.commit();
                                            break;
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        SharedPreferences.Editor editor2=sharedPreferences.edit();
                                        editor2.putString("isUploadFinish","OK");
                                        editor2.commit();
                                    }
                                }else {
                                    break;
                                }
                            }else {
                                GpioService.this.count++;
                                if (GpioService.this.count >= 1000){
                                    return;
                                }
                                Intent intent3 = new Intent(ACTION_UPLOADERROR_COUNT);
                                intent3.putExtra("count",""+ GpioService.this.count);
                                sendBroadcast(intent3);
                                AppUtils.uploadNetworkError("exec PAD_SrvDataUp NetWorkError",jtbh,mac);
                                break;
                            }
                        }
                        SharedPreferences.Editor editor2=sharedPreferences.edit();
                        editor2.putString("isUploadFinish","OK");
                        editor2.commit();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        executor.scheduleAtFixedRate(runnable,0L,Long.parseLong(getString(R.string.gpio_update_time)), TimeUnit.MILLISECONDS);
        //timer_gpio.schedule(timerTask,0,Integer.parseInt(getString(R.string.gpio_update_time)));
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        executor.shutdown();
        /*try {
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        out.close();*/
        Log.e("gpio_service_des","!");
    }
}
