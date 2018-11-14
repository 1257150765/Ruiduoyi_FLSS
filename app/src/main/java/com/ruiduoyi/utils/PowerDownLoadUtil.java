package com.ruiduoyi.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Chen on 2018-11-09.
 */

public class PowerDownLoadUtil {
    private static final String TAG = PowerDownLoadUtil.class.getSimpleName();
    private static Context mContext;
    private DownLoadListener downLoadListener;
    private int retryCount;

    private boolean isDownLoading = false;
    private static PowerDownLoadUtil INSTANCE;
    /*public static PowerDownLoadUtil getInstance(Context context){
        if (INSTANCE == null){
            synchronized (PowerDownLoadUtil.class){
                INSTANCE = new PowerDownLoadUtil(context);
            }
        }
        return INSTANCE;
    }*/
    public static PowerDownLoadUtil getInstance(Context context, DownLoadListener downLoadListener){
        if (INSTANCE == null){
            synchronized (PowerDownLoadUtil.class){
                if (INSTANCE == null) {
                    INSTANCE = new PowerDownLoadUtil(context, downLoadListener);
                }
            }
        }
        return INSTANCE;
    }

    private PowerDownLoadUtil(Context mContext) {
        this(mContext,null);

    }

    private PowerDownLoadUtil(Context mContext, DownLoadListener downLoadListener) {
        this(mContext,10,downLoadListener);
    }

    private PowerDownLoadUtil(Context mContext, final int retryCount, final DownLoadListener downLoadListener) {
        Log.d(TAG, "PowerDownLoadUtil:初始化 ");
        this.mContext = mContext;
        this.retryCount = retryCount;
        this.downLoadListener = downLoadListener;
        if (mContext == null){
            throw new NullPointerException("Context 不能为空!");
        }
        FileDownloader.setup(mContext);
    }

    /*this.url_str = url_str;
        this.filePath = filePath;
        this.fileName = fileName;
        *//*SharedPreferences downloadinfo = mContext.getSharedPreferences("DOWNLOADINFO", Context.MODE_PRIVATE);
        int alreadDown = downloadinfo.getInt("progress", -1);
        RandomAccessFile tempFile;
                    File f = new File(Environment.getExternalStorageDirectory().getPath()+"/temp.apk");

        tempFile = new RandomAccessFile(f,"rw");
            if (alreadDown <= 0){
                alreadDown = 0;
            }

        *//*
        while(true){
            try {
                Thread.sleep(3000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                //准备下载
                Message message = Message.obtain();
                message.what = Statu.PREPARE;
                handler.sendMessage(message);
                URL url= null;
                url = new URL(url_str);
                HttpURLConnection urlConnection=(HttpURLConnection) url.openConnection();
                urlConnection.setDoInput(true);
                urlConnection.setUseCaches(false);
                urlConnection.setRequestMethod("GET");
                urlConnection.setConnectTimeout(5000);
            *//*urlConnection.setRequestProperty("Range", "bytes=" + alreadDown
                    + "-" + getContentLength(url_str));*//*
                urlConnection.connect();
                int responseCode = urlConnection.getResponseCode();
                if (responseCode != 200){
                    message = Message.obtain();
                    message.what = Statu.ERROE;
                    message.obj = "";
                    handler.sendMessage(message);
                    continue;
                }
                double fileSize=urlConnection.getContentLength();
                Log.e("getLastModified()",urlConnection.getLastModified()+"");
                InputStream in=urlConnection.getInputStream();
                OutputStream out=new FileOutputStream(filePath+"/"+fileName,false);
                byte[] buff=new byte[1024];
                int downloadSize=0;
                int size;
                while ((size = in.read(buff)) != -1) {
                    downloadSize += size;
                    int progress=(int) (downloadSize/fileSize*100);
                    if (progress  <100){
                        message = Message.obtain();
                        message.what = Statu.DOWNLOADING;
                        message.arg1 = progress;
                        handler.sendMessage(message);
                    }
                    //Log.e("download",downloadSize/fileSize+"");

                    out.write(buff, 0, size);
                }
                if (downloadSize < fileSize){
                    message = Message.obtain();
                    message.what = Statu.ERROE;
                    message.obj = "下载文件不完整!";
                    handler.sendMessage(message);
                    continue;
                }
                message = Message.obtain();
                message.what = Statu.SUCCEED;
                message.obj = "下载成功!";
                handler.sendMessage(message);

           *//*
            byte[] b = new byte[1024];
            int l = -1;
            while((l = tempFile.read(b)) != -1){
                out.write(b,0,l);
            }
            out.flush();
            out.close();*//*
            installAPK(filePath+"/"+fileName);
            return;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Message message = Message.obtain();
                message.what = Statu.ERROE;
                message.obj = "URL不正确!";
                handler.sendMessage(message);
                continue;
            } catch (IOException e) {
                e.printStackTrace();
                Message message = Message.obtain();
                message.what = Statu.ERROE;
                message.obj = "IO异常!";
                handler.sendMessage(message);
                continue;
            }
        }*/
    public void downloadAPK(final String url_str, final String filePath, final String fileName){
        if (isDownLoading){
            return;
        }
        File file = new File(filePath+"/"+fileName);
        if (file.exists()){
            file.delete();
        }
        isDownLoading = true;

        int downLoadId = FileDownloader.getImpl().create(url_str).setPath(file.getAbsolutePath())
                .setAutoRetryTimes(retryCount)
                .setListener(new FileDownloadListener() {
                    @Override
                    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        if (downLoadListener != null) {
                            downLoadListener.onPrepare();
                        }
                    }

                    @Override
                    protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
                    }

                    @Override
                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        Log.d(TAG, "progress: soFarBytes:" + soFarBytes + "---totalBytes:" + totalBytes);
                        if (downLoadListener != null) {
                            downLoadListener.onDownLoading(soFarBytes, totalBytes);
                        }
                    }

                    @Override
                    protected void blockComplete(BaseDownloadTask task) {
                    }

                    @Override
                    protected void retry(final BaseDownloadTask task, final Throwable ex, final int retryingTimes, final int soFarBytes) {
                        Log.d(TAG, "retry: retryingTimes" + retryingTimes + "--" + soFarBytes);
                    }

                    @Override
                    protected void completed(BaseDownloadTask task) {
                        installAPK(filePath + "/" + fileName);
                        if (downLoadListener != null) {
                            downLoadListener.onSucceed();
                        }
                        isDownLoading = false;
                    }

                    @Override
                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        isDownLoading = false;
                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        isDownLoading = false;
                        if (downLoadListener != null) {
                            downLoadListener.onError(e.getMessage().toString());
                        }

                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {
                        Log.d(TAG, "warn: " + task.getErrorCause());
                    }
                }).start();

    }

    //下载到本地后执行安装
    public void installAPK( String filePath) {
        //获取下载文件的Uri
        Uri downloadFileUri = Uri.fromFile(new File(filePath));
        if (downloadFileUri != null) {
            Intent intent= new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(downloadFileUri, "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        }
    }

    /**
     * 下载的状态
     */
    private static class Statu{
        public static final int PREPARE = 1;
        public static final int DOWNLOADING = 2;
        public static final int SUCCEED = 3;
        public static final int ERROE = 4;
    }
    public interface DownLoadListener{
        void onPrepare();
        void onDownLoading(int progress, int totalBytes);
        void onSucceed();
        void onError(String errorInfo);
    }
    public int getContentLength(String urlStr){
        int len = -1;
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            len = conn.getContentLength();//获取请求资源的总长度。
            conn.disconnect();
            return len;
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }
}
