package com.universal.framwork.db;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;

import com.universal.framwork.db.XSQLiteDataBase.XDBUpdateListener;

import android.content.Context;

/**
 * 数据库连接池管理类
 * com.universal.framwork.db.SQLiteDataBasePool
 * @author yuanzeyao <br/>
 * create at 2014年5月30日 上午11:05:50
 */
public class SQLiteDataBasePool 
{
  private static final String TAG = "SQLiteDataBasePool";
  //创建一个数据库连接池的默认大小是2
  private int initDbConnectionSize=2;
  //数据库连接池的增长大小是2
  private int dbConnectionIncreSize=2;
  //最大连接池的大小
  private int maxConnectionSize=10;
  //记录了所有可以使用的数据库对象
  private Vector<PooledSQLiteDataBase> pDataBases;
  //上下文对象
  private Context context;
  //数据库升级监听接口
  private XDBUpdateListener updateListener;
  //数据库连接对象构造参数
  private DBParams params;
  private boolean isWrite=false;
  //存放所有的连接池，一个连接池对应一个数据库
  private static HashMap<String,SQLiteDataBasePool> allPool=new HashMap<String, SQLiteDataBasePool>();
  
  //单例模式
  public synchronized static SQLiteDataBasePool getInstance(Context context,DBParams params,boolean isWrite)
  {
    String dbName=params.getDb_name();
    SQLiteDataBasePool pool=allPool.get(dbName);
    if(pool==null)
    {
      pool=new SQLiteDataBasePool(context, params, isWrite);
      allPool.put(dbName, pool);
    }
    return pool;
  }
  
  //单例模式
  public static SQLiteDataBasePool getInstance(Context context,boolean isWrite)
  {
    return getInstance(context,new DBParams(),isWrite);
  }
  
  /**
   * 构造函数
   * @param context
   *            上下文对象
   * @param params
   *            构建数据库连接对象的参数
   * @param isWrite
   *            是否可写
   */           
  public SQLiteDataBasePool(Context context,DBParams params,boolean isWrite)
  {
    this.context=context;
    this.params=params;
    this.isWrite=isWrite;
  }
  
  /**
   * 设置升级监听
   * @param updateListener
   */
  public void setOnUpdateListener(XDBUpdateListener updateListener)
  {
    this.updateListener=updateListener;
  }
  
  /**
   * 返回连接池的默认大小
   */
  public int getInitPoolSize()
  {
    return initDbConnectionSize;
  }

  /**
   * 获取每次增加连接的大小
   * @return
   */
  public int getDbConnectionIncreSize()
  {
    return dbConnectionIncreSize;
  }

  /**
   * 设置每次增加的大小
   * @param dbConnectionIncreSize
   */
  public void setDbConnectionIncreSize(int dbConnectionIncreSize)
  {
    this.dbConnectionIncreSize = dbConnectionIncreSize;
  }

  /**
   * 获取连接池最大连接数
   * @return
   */
  public int getMaxConnectionSize()
  {
    return maxConnectionSize;
  }

  /**
   * 设置连接池最大连接数
   * @param maxConnectionSize
   */
  public void setMaxConnectionSize(int maxConnectionSize)
  {
    this.maxConnectionSize = maxConnectionSize;
  }
  
  /**
   * 创建一个数据库连接池，默认大小
   */
  public synchronized void createDBPool()
  {
    if(pDataBases!=null)
    {
      //pDataBases不为空，那么连接池已经创建，直接返回
      return;
    }
    if(this.initDbConnectionSize>0 && this.maxConnectionSize>0 && this.initDbConnectionSize<=this.maxConnectionSize)
    {
      //创建initDbConnectionSize个数据库连接
      createDBPool(this.initDbConnectionSize);
    }
  }
  
  /**
   * 创建指定大小的数据库连接池
   * @param size
   */
  private void createDBPool(int size)
  {
    pDataBases=new Vector<PooledSQLiteDataBase>();
    for(int i=0;i<size;i++)
    {
      pDataBases.addElement(new PooledSQLiteDataBase(newSQLiteDataBase(), false));
    }
    
  }
  
  /**
   * 新建一个数据库连接对象
   * @return
   */
  private XSQLiteDataBase newSQLiteDataBase()
  {
    XSQLiteDataBase xdb=new XSQLiteDataBase(context, params);
    xdb.openDataBase(updateListener, isWrite);
    return xdb;
  }
  
  /**
   * 向连接池中添加数据库连接
   */
  private void addNumOfDataBasePool()
  {
    if(pDataBases==null)
    {
      createDBPool();
    }else
    {
      for(int i=0;i<dbConnectionIncreSize;i++)
      {
        if(pDataBases.size()>=maxConnectionSize)
          return;
        pDataBases.addElement(new PooledSQLiteDataBase(newSQLiteDataBase(), false));
      }
    }
    
    
  }
  
  /**
   * 获取一个空闲的SQLiteDataBase对象，如果第一次获取为空，那么向连接池中添加dbConnectionIncreSize个连接，
   * 然后再次获取，如果获取成功则返回，否则返回null
   * @return
   */
  private XSQLiteDataBase getFreeSQLiteDataBase()
  {
    XSQLiteDataBase xdb=findFreeSQLiteDataBase();
    if(xdb==null)
    {
      addNumOfDataBasePool();
      xdb=findFreeSQLiteDataBase();
    }
    return xdb;
  }
  
  /**
   * 从连接池中找一个空闲的连接，如果该连接已经失效，则重新创建该连接
   * @return  一个空闲连接，如果没有空闲连接则返回null
   */
  private XSQLiteDataBase findFreeSQLiteDataBase()
  {
    XSQLiteDataBase db=null;
    PooledSQLiteDataBase pdb=null;
    
    if(pDataBases==null)
    {
      //数据库连接池还没有建立
      return null;
    }
    Enumeration<PooledSQLiteDataBase> pooledDbs=pDataBases.elements();
    while(pooledDbs.hasMoreElements())
    {
      pdb=pooledDbs.nextElement();
      if(!pdb.isBusy())
      {
        db=pdb.getmSQLiteDataBase();
        if(!db.isUseable())
        {
          db=newSQLiteDataBase();
          pdb.setmSQLiteDataBase(db);
        }
        break;
      }
    }
    return db;
  }
  
  /**
   * 获取一个数据库连接对象，如果没有获取到连接，那么等待250 ms,然后继续寻找空闲连接，知道成功获取
   * @return 一个有效的数据库连接
   * @throws InterruptedException  wait过程中被打断
   */
  public synchronized XSQLiteDataBase getSQLiteDataBase() 
  {
    XSQLiteDataBase xdb=null;
    if(pDataBases==null)
    {
      createDBPool();
    }
    xdb=getFreeSQLiteDataBase();
    while(xdb==null)
    {
      wait(250);
      xdb=getFreeSQLiteDataBase();
    }
    return xdb;
  }
  
  /**
   * 释放指定数据库连接对象
   * @param sqliteDatabase  要被释放的数据库连接对象
   */
  public void releaseSQLiteDataBase(XSQLiteDataBase sqliteDatabase)
  {
    if(sqliteDatabase==null)
    {
      return;
    }
    Enumeration<PooledSQLiteDataBase> poolDatabase=pDataBases.elements();
    PooledSQLiteDataBase pdb=null;
    while(poolDatabase.hasMoreElements())
    {
      pdb=poolDatabase.nextElement();
      if(sqliteDatabase==pdb.getmSQLiteDataBase())
      {
        pdb.setBusy(false);
        break;
      }
    }
  }
  
  
  public void closeSQLiteDataBase(XSQLiteDataBase xdb)
  {
    if(xdb!=null)
    {
      xdb.close();
    }
  }
  
  public synchronized void cloaseAllSQLiteDataBase()
  {
    //确保连接池存在
    if(pDataBases==null)
    {
      //还没有创建连接池，不需要关闭
      return;
    }
    Enumeration<PooledSQLiteDataBase> poolDataBase=pDataBases.elements();
    PooledSQLiteDataBase pdb=null;
    //遍历连接池里面的所有连接，如果连接处于忙碌状态，那么等待5s,否则直接关闭连接
    while(poolDataBase.hasMoreElements())
    {
      pdb=poolDataBase.nextElement();
      if(pdb.isBusy())
      {
        //等待5s总
        wait(5000);
      }
      closeSQLiteDataBase(pdb.getmSQLiteDataBase());
      //从连接池里面移除该对象
      pDataBases.removeElement(pdb);
      
    }
    //将连接池置空
    pDataBases=null;
  }
  
  public synchronized void refreshAllDaseBase()
  {
    //确保连接池已经创建
    if(pDataBases==null)
    {
      return;
    }
    Enumeration<PooledSQLiteDataBase> poolDataBase=pDataBases.elements();
    PooledSQLiteDataBase pdb=null;
    //遍历连接池里面的所有连接，如果连接处于忙碌状态，那么等待5s,否则直接关闭，然后重建
    while(poolDataBase.hasMoreElements())
    {
      pdb=poolDataBase.nextElement();
      if(pdb.isBusy())
      {
        //等待5s
        wait(5000);
      }
      closeSQLiteDataBase(pdb.getmSQLiteDataBase());
      pdb.setmSQLiteDataBase(newSQLiteDataBase());
      pdb.setBusy(false);
    }
  }
  
  /**
   * 使程序等待给定的毫秒数
   * 
   * @param 给定的毫秒数
   */

  private void wait(int mSeconds)
  {
    try
    {
      Thread.sleep(mSeconds);
    } catch (InterruptedException e)
    {
    }
  }
  
  
  
}
