package com.universal.framwork;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.universal.framwork.db.DBParams;
import com.universal.framwork.db.SQLiteDataBasePool;
import com.universal.framwork.job.JobConfig;
import com.universal.framwork.job.JobManager;
import com.universal.framwork.manager.AppManager;
import com.universal.framwork.net.INetObserver;
import com.universal.framwork.net.NetWorkReceiver;
import com.universal.framwork.util.LogUtil;
import com.universal.framwork.util.NetWorkUtil.NetType;
/**
 * 
 * com.universal.framwork.XApplication
 * @author yuanzeyao <br/>
 * create at 2014年5月24日 上午11:24:58
 */
public class XApplication extends Application implements INetObserver
{
  private static final String TAG = "XApplication";
  //配置JobManager
  public static final int THREAD_LOAD_FACTOR=2;
  public static final int THREAD_MAX_COUNT=6;
  public static final int THREAD_MIN_COUNT=0;
  public static final int THREAD_KEEPLIVE_TIME=2*60;//线程空闲等待时间
  
  //配置Volley
  
  private static XApplication instance;
  //主要管理应用程序中所有的Activity和退出应用
  private AppManager appManager;
  
  private SQLiteDataBasePool dbPool;
  
  //获得Application实例
  public static XApplication getInstance()
  {
    return instance;
  }
  
  @Override
  public void onCreate()
  {
    LogUtil.d("applife", "onCreate");
    super.onCreate();
    afterApplicationCreate();
  }
  
  private void afterApplicationCreate()
  {
    instance=this;
    appManager=AppManager.getInstance();
    NetWorkReceiver.addObserver(this);
    NetWorkReceiver.registerNetBroadcastReceiver(this);
    configJobManager();
  }
  
  private void configJobManager()
  {
    JobConfig config=new JobConfig.Builder(this)
        .loadFactor(THREAD_LOAD_FACTOR)//线程负载因子为2
        .maxConsumerCount(THREAD_MAX_COUNT)
        .minConsumerCount(THREAD_MIN_COUNT)
        .consumerKeepAlive(THREAD_KEEPLIVE_TIME)
        .build();
    JobManager.build(this, config);
  }
  
  public SQLiteDataBasePool getSQLiteDataBasePool()
  {
    if(dbPool==null)
    {
      dbPool=SQLiteDataBasePool.getInstance(this, new DBParams(), true);
    }
    return dbPool;
  }


  public AppManager getAppManager() {
    return appManager;
  }


  /**
   * 退出应用
   * @param runback 是否在后台运行
   */
  public void exitApp(boolean runback)
  {
    //清除所有正在执行或者还没有执行的任务
    JobManager.get().clear();
    //关闭所有的Activity,并退出应用
    appManager.exitApp(true);
    
  }

  /**
   * 当网络连接时，通过当前与用户交互的Activity
   */
  @Override
  public void onConnect(NetType netType)
  {
    if(appManager!=null && appManager.currentActivity!=null)
    {
      appManager.currentActivity.onConnect(netType);
    }
  }
  
  
  /**
   * 当网络断开时，通过当前与用户交互的Activity
   */
  @Override
  public void onDisConnect()
  {
    if(appManager!=null && appManager.currentActivity!=null)
    {
      appManager.currentActivity.onDisConnect();
    }
  }
  
  
  
  @Override
  public void onTerminate() {
    
    super.onTerminate();
    LogUtil.d("applife", "onTerminate");
    exitApp(false);
  }
}
