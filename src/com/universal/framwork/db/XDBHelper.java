package com.universal.framwork.db;

import com.universal.framwork.db.XSQLiteDataBase.XDBUpdateListener;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class XDBHelper extends SQLiteOpenHelper 
{
  private static final String TAG = "XDBHelper";
  //数据库升级监听接口
  public XDBUpdateListener mUpdateListener;
  
  /**
   * 
   * @param context
   *              上下文对象
   * @param name
   *              数据库名称
   * @param factory
   *              游标工厂，当执行一个查询语句时，此处用来生成一个游标
   * @param version
   *              数据库版本
   */
  public XDBHelper(Context context, String name, CursorFactory factory, int version) 
  {
    super(context, name, factory, version);
  }
  
  /**
   * 
   * @param context
   *              上下文对象
   * @param name  
   *              数据库名称
   * @param factory
   *              游标工厂，当执行一个查询语句时，此处用来生成一个游标
   * @param version
   *              数据库版本
   * @param listener
   *              数据库升级回调接口
   */
  public XDBHelper(Context context, String name, CursorFactory factory, int version,XDBUpdateListener listener) 
  {
    super(context, name, factory, version);
    mUpdateListener=listener;
  }

  /**
   * 数据库第一次创建会执行此方法
   */
  @Override
  public void onCreate(SQLiteDatabase db) 
  {
    
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
  {
    if(mUpdateListener!=null)
    {
      mUpdateListener.onUpgrade(db, oldVersion, newVersion);
    }
  }
  
  /**
   * 设置一个数据库升级监听器
   * @param updateListener  当数据库升级时，该接口被回调
   */
  public void setOnUpdateListener(XDBUpdateListener updateListener)
  {
    this.mUpdateListener=updateListener;
  }
}
