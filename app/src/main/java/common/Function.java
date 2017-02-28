package common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import common.DataCleanManager;
import xujiantao.com.chuangwen.R;

public class Function
{

    public static String getType(Object o)
    {
        return o.getClass().toString();
    }

    public static void checkNetworkMsg(Context context, ViewGroup container)
    {
        Boolean netStatus = isNetworkConnected(context);
        if(netStatus == false)
        {
            Snackbar snackbar = Snackbar.make(container, "当前没有网络", Snackbar.LENGTH_SHORT).setActionTextColor(Color.RED);
            setSnackbarMessageTextColor(snackbar,Color.GREEN);
            snackbar.show();
            //Toast.makeText(context, "当前没有网络", Toast.LENGTH_SHORT).show();
        }
    }

    public static void cleanCache(Context context, Boolean showMsg)
    {
        DataCleanManager.cleanInternalCache(context);
        DataCleanManager.cleanDatabases(context);
        //DataCleanManager.cleanSharedPreference(context);
        DataCleanManager.cleanExternalCache(context);
        //DataCleanManager.cleanApplicationData(context);

        if(showMsg)
        {
            Toast.makeText(context, "清理缓存完成", Toast.LENGTH_SHORT).show();
        }
    }

    public static void setSnackbarMessageTextColor(Snackbar snackbar, int color)
    {
        View view = snackbar.getView();
        ((TextView) view.findViewById(R.id.snackbar_text)).setTextColor(color);
    }

    public static boolean isNetworkConnected(Context context)
    {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * 获取网落图片资源
     * @param url
     * @return
     */
    public static Bitmap getHttpBitmap(String url)
    {
        URL myFileURL;
        Bitmap bitmap=null;
        try{
            myFileURL = new URL(url);
            //获得连接
            HttpURLConnection conn=(HttpURLConnection)myFileURL.openConnection();
            //设置超时时间为6000毫秒，conn.setConnectionTiem(0);表示没有时间限制
            conn.setConnectTimeout(6000);
            //连接设置获得数据流
            conn.setDoInput(true);
            //不使用缓存
            conn.setUseCaches(false);
            //这句可有可无，没有影响
            //conn.connect();
            //得到数据流
            InputStream is = conn.getInputStream();
            //解析得到图片
            bitmap = BitmapFactory.decodeStream(is);
            //关闭数据流
            is.close();
        }catch(Exception e){
            e.printStackTrace();
        }

        return bitmap;

    }
}
