package com.universal.framwork.manager;

import java.util.Stack;

import com.universal.framwork.BaseActivity;

import android.app.Activity;

/**
 * 应用程序管理类
 * com.universal.framwork.manager.AppManager
 * @author yuanzeyao <br/>
 * create at 2014年5月23日 下午10:26:31
 */
public class AppManager
{
  private static final String TAG = "AppManager";
  
  //*********实现单例模式**********************
  private AppManager()
  {
    
  }
  public static class SingleHolder
  {
    public static final AppManager instance=new AppManager();
  }
  
  public static AppManager getInstance()
  {
    return SingleHolder.instance;
  }
  //**************结束单例**********************
  /**
   * 用于保存当前应用中的所有的Activity
   */
  public Stack<BaseActivity> allActiivtys;
  public BaseActivity currentActivity=null;
  
  public void addActivity(BaseActivity activity)
  {
    if(allActiivtys==null)
    {
      allActiivtys=new Stack<BaseActivity>();
    }
    allActiivtys.push(activity);
    currentActivity=allActiivtys.peek();
  }
  
  /**
   * 移除相应的Activity
   * @param activity
   */
  public void removeActivity(BaseActivity activity)
  {
    if(allActiivtys!=null && allActiivtys.size()>0)
    {
      for(Activity tmp:allActiivtys)
      {
        if(tmp==activity)
        {
          allActiivtys.remove(activity);
        }
      }
      //设置当前的Actiivty
      if(allActiivtys.size()==0)
      {
        //如果所有的Activity都被移除了，那么当前Activity设置为Null
        currentActivity=null;
      }else
      {
        //如果不为空，那么设置为栈顶的Activity
        currentActivity=allActiivtys.peek();
      }
    }else
    {
      currentActivity=null;
    }
    
  }
  
  /**
   * 退出应用程序
   * @param runback 是否后台运行
   */
  public void exitApp(boolean runback)
  {
    for(Activity activity:allActiivtys)
    {
      activity.finish();
    }
    //不需要再后台执行
    if(!runback)
    {
      System.exit(0);
    }
  }
  
  
}
