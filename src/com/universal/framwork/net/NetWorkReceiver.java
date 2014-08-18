package com.universal.framwork.net;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.universal.framwork.util.NetWorkUtil;
import com.universal.framwork.util.NetWorkUtil.NetType;
/**
 * 网络连接的BroadcastReceiver
 * com.universal.framwork.net.NetWorkReceiver
 * @author yuanzeyao <br/>
 * create at 2014年5月24日 上午10:00:06
 */
public class NetWorkReceiver extends BroadcastReceiver
{
  private static final String TAG = "NetWorkReceiver";
  public static final String RECEIVE_ACTION_SYSTEM="android.net.conn.CONNECTIVITY_CHANGE";//系统发出
  public static final String RECEIVE_ACITON_USER="user.net.conn.CONNECTIVITY_CHANGE";//用户自己发出
  //*******************单例实现开始**********************
  private NetWorkReceiver()
  {
    
  }
  public static class SingleHolder{
    public static final NetWorkReceiver INSTANCE=new NetWorkReceiver();
  }
  
  public static NetWorkReceiver getInstance()
  {
    return SingleHolder.INSTANCE;
  }
  //*******************单例实现结束**********************

  private static ArrayList<INetObserver> allObservers;
  
 
  //当前的网络类型
  public NetType currentNetType;
  //当前是否有网络连接
  public boolean isnetconnect=true;
  
  @Override
  public void onReceive(Context context, Intent intent)
  {
    if(intent.getAction().equals(RECEIVE_ACITON_USER)|| intent.getAction().equals(RECEIVE_ACTION_SYSTEM))
    {
      if(NetWorkUtil.isNetWorkAvailable(context))
      {
        isnetconnect=true;
        currentNetType=NetWorkUtil.getNetType(context);
        Log.v("yzy", "has net work");
      }else
      {
        isnetconnect=false;
        currentNetType=NetType.NONET;
        Log.v("yzy", "not has net work");
      }
      
      notifyObserver();
     
    }
  }
  
  //添加网络监听者
  public static void addObserver(INetObserver observer)
  {
    if(allObservers==null)
    {
      allObservers=new ArrayList<INetObserver>();
    }
    allObservers.add(observer);
  }
  
  //移除网络监听者
  public static void removeObserver(INetObserver observer)
  {
    if(allObservers!=null && allObservers.size()>0)
    {
      allObservers.remove(observer);
    }
  }
  
  //通知观察者
  private void notifyObserver()
  {
    if(allObservers!=null)
    {
      for(INetObserver observer:allObservers)
      {
        if(isnetconnect)
        {
          //通知成功连接，并且告知连接类型
          observer.onConnect(currentNetType);
        }else
        {
          //通知断开连接
          observer.onDisConnect();
        }
      }
    }
    
  }
  
  //添加网络监听Reciver
  public static void registerNetBroadcastReceiver(Context context)
  {
    IntentFilter filter=new IntentFilter();
    filter.addAction(RECEIVE_ACTION_SYSTEM);
    filter.addAction(RECEIVE_ACITON_USER);
    context.getApplicationContext().registerReceiver(NetWorkReceiver.getInstance(), filter);
  }
  
  //取消网络监听Receiver
  public static void unRegisNetBroadcastReceiver(Context context)
  {
    context.getApplicationContext().unregisterReceiver(NetWorkReceiver.getInstance());
  }
}
