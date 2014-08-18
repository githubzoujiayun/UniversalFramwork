package com.universal.framwork.db;
/**
 * 对{@link XSQLiteDataBase} 类的封装，对于放入到连接池中的XSQLiteDataBase，记录
 * 其是否处于忙碌状态
 * com.universal.framwork.db.PooledSQLiteDataBase
 * @author yuanzeyao <br/>
 * create at 2014年5月30日 下午1:21:21
 */
public class PooledSQLiteDataBase
{
  private static final String TAG = "PooledSQLiteDataBase";
  //数据库实例
  private XSQLiteDataBase mSQLiteDataBase;
  
  //是否处理忙碌状态
  private boolean isBusy;
  
  public PooledSQLiteDataBase(XSQLiteDataBase db,boolean isBusy)
  {
    this.mSQLiteDataBase=db;
    this.isBusy=isBusy;
  }

  public XSQLiteDataBase getmSQLiteDataBase()
  {
    return mSQLiteDataBase;
  }

  public void setmSQLiteDataBase(XSQLiteDataBase mSQLiteDataBase)
  {
    this.mSQLiteDataBase = mSQLiteDataBase;
  }

  public boolean isBusy()
  {
    return isBusy;
  }

  public void setBusy(boolean isBusy)
  {
    this.isBusy = isBusy;
  }
  
  
}
