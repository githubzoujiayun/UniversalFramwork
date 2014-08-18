package com.universal.framwork.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 用来判断当前是否有网络，网络连接类型
 * com.universal.framwork.net.util.NetWorkUtil
 * @author yuanzeyao <br/>
 * create at 2014年5月24日 上午10:15:42
 */
public class NetWorkUtil
{
  private static final String TAG = "NetWorkUtil";
  //定义了网络类型
  public enum NetType
  {
    WIFI,//wifi网络
    MOBILE,//手机网络
    NONET//没有网络
  }
  
  /**
   * 判断当前是否有网络可以使用
   * @param context
   * @return true 表示有  false 表示没有
   */
  public static boolean isNetWorkAvailable(Context context)
  {
    ConnectivityManager manager=(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo[] infos=manager.getAllNetworkInfo();
    if(infos!=null)
    {
      for(NetworkInfo info:infos)
      {
        if(info.getState()==NetworkInfo.State.CONNECTED)
        {
          return true;
        }
      }
    }
    return false;
  }
  
  /**
   * 判断网络是否连接
   * @param context
   * @return
   */
  public static boolean isNetWorkConnect(Context context)
  {
    ConnectivityManager manager=(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo info=manager.getActiveNetworkInfo();
    if(info!=null)
    {
      return info.isAvailable();
    }
    return false;
  }
  
  /**
   * 判断wifi网络是否连接
   * @param context
   * @return
   */
  public static boolean isWifiConnect(Context context)
  {
    ConnectivityManager manager=(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo wifiInfo=manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
    if(wifiInfo!=null)
    {
      return wifiInfo.isAvailable();
    }
    return false;
  }
  
  /**
   * 判断手机网络是否连接
   * @param context
   * @return
   */
  public static boolean isMobileConnect(Context context)
  {
    ConnectivityManager manager=(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo infoMobile=manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
    if(infoMobile!=null)
    {
      return infoMobile.isAvailable();
    }
    return false;
  }
  
  /**
   * 获取当前网络类型
   * @param context
   * @return
   */
  public static NetType getNetType(Context context)
  {
    if(!isNetWorkAvailable(context))
      return NetType.NONET;
    else
    {
      ConnectivityManager manager=(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
      NetworkInfo info=manager.getActiveNetworkInfo();
      if(info!=null)
      {
        if(info.getType()==ConnectivityManager.TYPE_WIFI)
        {
          return NetType.WIFI;
        }else if(info.getType()==ConnectivityManager.TYPE_MOBILE)
        {
          return NetType.MOBILE;
        }else
          return NetType.NONET;
      }
      else
        return NetType.NONET;
    }
  }
  
  
}


