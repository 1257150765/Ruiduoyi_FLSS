package com.ruiduoyi.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.barteksc.pdfviewer.PDFView;
import com.ruiduoyi.R;
import com.ruiduoyi.adapter.EasyArrayAdapter;
import com.ruiduoyi.model.NetHelper;
import com.ruiduoyi.utils.AppUtils;
import com.ruiduoyi.view.PopupDialog;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ZyzdActivity extends BaseActivity {
    private TextView title;
    private com.github.barteksc.pdfviewer.PDFView pdfView;
    private SharedPreferences sharedPreferences;
    private String jtbh,zzdh;
    private Handler handler;
    private ListView listView;
    private String path;
    private ProgressBar progressBar;
    private Button cancle_btn;
    private PopupDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zyzd);
        initData();
        initView();
    }

    private void initView(){
        title=(TextView)findViewById(R.id.title_text);
        pdfView=(com.github.barteksc.pdfviewer.PDFView) findViewById(R.id.pdfview);
        pdfView.fromAsset("default.pdf").defaultPage(0).load();
        listView=(ListView)findViewById(R.id.list_zyzd);
        progressBar=(ProgressBar)findViewById(R.id.progress);
        cancle_btn=(Button)findViewById(R.id.cancle_btn);
        cancle_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtils.sendCountdownReceiver(ZyzdActivity.this);
                finish();
            }
        });
        dialog=new PopupDialog(this,400,360);
        dialog.getCancle_btn().setVisibility(View.GONE);
        dialog.getOkbtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.getOkbtn().setText("确定");
    }

    private void initData(){
        sharedPreferences=getSharedPreferences("info",MODE_PRIVATE);
        jtbh=sharedPreferences.getString("jtbh","");
        zzdh=sharedPreferences.getString("zzdh","");
        path= Environment.getExternalStorageDirectory().getPath()+"/RdyPmes/PDF/";
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 0x100:
                        initListView((JSONArray) msg.obj);
                        break;
                    case 0x101:
                        File file=new File((String) msg.obj);
                        pdfView.recycle();
                        pdfView.fromFile(file).defaultPage(0).load();
                        break;
                    case 0x102:
                        progressBar.setVisibility(View.VISIBLE);
                        break;
                    case 0x103:
                        progressBar.setVisibility(View.GONE);
                        break;
                    case 0x104:
                        pdfView.fromAsset("default.pdf").defaultPage(0).load();
                        dialog.setMessage("未维护对应文件");
                        dialog.show();
                        progressBar.setVisibility(View.GONE);
                        break;
                    default:
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
                /*List<List<String>>list1= NetHelper.getQuerysqlResult("Exec PAD_Get_ZyzdInf  'A','"+zzdh+"',''");
                if (list1!=null){
                    if (list1.size()>0){
                        if (list1.get(0).size()>1){
                            Message msg=handler.obtainMessage();
                            msg.what=0x100;
                            msg.obj=list1;
                            handler.sendMessage(msg);
                        }
                    }
                }else {
                    AppUtils.uploadNetworkError("Exec PAD_Get_ZyzdInf  'A'",jtbh,sharedPreferences.getString("msc",""));
                }*/
                JSONArray list1= NetHelper.getQuerysqlResultJsonArray("Exec PAD_Get_ZyzdInf  'A','"+zzdh+"',''");
                if (list1!=null){
                    if (list1.length()>0){
                        Message msg=handler.obtainMessage();
                        msg.what=0x100;
                        msg.obj=list1;
                        handler.sendMessage(msg);
                    }
                }else {
                    AppUtils.uploadNetworkError("Exec PAD_Get_ZyzdInf  'A'",jtbh,sharedPreferences.getString("msc",""));
                }
            }
        }).start();
    }



    private void initListView(JSONArray lists){
        final List<Map<String,String>>data=new ArrayList<>();
        try {
            for (int i=0;i<lists.length();i++){
                Map<String,String>map=new HashMap<>();
                map.put("lab_1",lists.getJSONObject(i).getString("doc_djbh"));
                map.put("lab_2",lists.getJSONObject(i).getString("doc_name"));
                map.put("isSelect","0");
                data.add(map);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final EasyArrayAdapter adapter=new EasyArrayAdapter(this,R.layout.list_item_zyzd,data) {
            @Override
            public View getEasyView(final int position, View convertView, ViewGroup parent) {
                View view;
                if (convertView!=null){
                    view=convertView;
                }else {
                    view= LayoutInflater.from(ZyzdActivity.this).inflate(R.layout.list_item_zyzd,null);
                }
                Map<String,String>map=data.get(position);
                final LinearLayout bg=(LinearLayout)view.findViewById(R.id.bg);
                TextView lab_1=(TextView)view.findViewById(R.id.lab_1);
                lab_1.setText(map.get("lab_2"));
                if (map.get("isSelect").equals("0")){
                    bg.setBackgroundColor(Color.WHITE);
                }else {
                    bg.setBackgroundColor(getResources().getColor(R.color.small));
                }
                bg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AppUtils.sendCountdownReceiver(ZyzdActivity.this);
                        bg.setBackgroundColor(getResources().getColor(R.color.small));
                        data.get(position).put("isSelect","1");

                        try {
                            onItemClickEven(data,position);
                        } catch (IOException e) {
                            handler.sendEmptyMessage(0x104);
                            e.printStackTrace();
                        }


                        for (int i=0;i<data.size();i++){
                            if (i!=position){
                                data.get(i).put("isSelect","0");
                            }
                        }
                        notifyDataSetChanged();
                    }
                });
                return view;
            }
        };
        listView.setAdapter(adapter);
    }



    private void onItemClickEven(final List<Map<String,String>>data, final int position) throws IOException {
        File dir=new File(path);
        if (!dir.exists()){
            dir.mkdir();
        }
        final String filePath=path+data.get(position).get("lab_1")+".pdf";
        File file=new File(filePath);
        if (file.exists()){
            Message msg=handler.obtainMessage();
            msg.what=0x101;
            msg.obj=filePath;
            handler.sendMessage(msg);
        }else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    /*List<List<String>>list=NetHelper.getQuerysqlResult("Exec PAD_Get_ZyzdInf  'B','"+zzdh+"','"
                            +data.get(position).get("lab_1")+"'");
                    if (list!=null){
                        if (list.size()>0){
                            if (list.get(0).size()>0){
                                String url_str=list.get(0).get(0);
                                try {
                                    handler.sendEmptyMessage(0x102);
                                    URL url=new URL(url_str);
                                    HttpURLConnection urlConnection= (HttpURLConnection) url.openConnection();
                                    urlConnection.setDoInput(true);
                                    urlConnection.setUseCaches(false);
                                    urlConnection.setRequestMethod("GET");
                                    urlConnection.setConnectTimeout(5000);
                                    urlConnection.connect();
                                    InputStream in=urlConnection.getInputStream();
                                    OutputStream out=new FileOutputStream(filePath,false);
                                    byte[] buff=new byte[1024];
                                    int size;
                                    while ((size = in.read(buff)) != -1) {
                                        out.write(buff, 0, size);
                                    }
                                    Message msg=handler.obtainMessage();
                                    msg.what=0x101;
                                    msg.obj=filePath;
                                    handler.sendMessage(msg);
                                    handler.sendEmptyMessage(0x103);
                                } catch (MalformedURLException e) {
                                    e.printStackTrace();
                                    handler.sendEmptyMessage(0x104);
                                } catch (IOException e) {
                                    handler.sendEmptyMessage(0x104);
                                    e.printStackTrace();
                                }
                            }
                        }
                    }*/
                    JSONArray list=NetHelper.getQuerysqlResultJsonArray("Exec PAD_Get_ZyzdInf  'B','"+zzdh+"','"
                            +data.get(position).get("lab_1")+"'");
                    if (list!=null){
                        if (list.length()>0){
                            try {
                                String url_str=list.getJSONObject(0).getString("doc_webpath");
                                handler.sendEmptyMessage(0x102);
                                URL url=new URL(url_str);
                                HttpURLConnection urlConnection= (HttpURLConnection) url.openConnection();
                                urlConnection.setDoInput(true);
                                urlConnection.setUseCaches(false);
                                urlConnection.setRequestMethod("GET");
                                urlConnection.setConnectTimeout(5000);
                                urlConnection.connect();
                                InputStream in=urlConnection.getInputStream();
                                OutputStream out=new FileOutputStream(filePath,false);
                                byte[] buff=new byte[1024];
                                int size;
                                while ((size = in.read(buff)) != -1) {
                                    out.write(buff, 0, size);
                                }
                                Message msg=handler.obtainMessage();
                                msg.what=0x101;
                                msg.obj=filePath;
                                handler.sendMessage(msg);
                                handler.sendEmptyMessage(0x103);
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                                handler.sendEmptyMessage(0x104);
                            } catch (IOException e) {
                                handler.sendEmptyMessage(0x104);
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }).start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog.isShow()){
            dialog.dismiss();
        }
    }
}
