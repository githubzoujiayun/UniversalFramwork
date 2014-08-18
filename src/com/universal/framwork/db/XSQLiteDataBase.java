package com.universal.framwork.db;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.NameValuePair;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.universal.framwork.db.sqlbuilder.SQLBuilder;
import com.universal.framwork.db.sqlbuilder.SQLBuilderFactory;
import com.universal.framwork.exception.DBNotOpenException;
import com.universal.framwork.util.LogUtil;
import com.universal.framwork.util.ToastUtil;

/**
 * 数据库管理类，通过此类对数据库进行操作
 * com.universal.framwork.db.XSQLiteDataBase
 * @author yuanzeyao <br/>
 * create at 2014年5月30日 上午11:27:59
 */
public class XSQLiteDataBase
{
  private static final String TAG="XSQLiteDataBase";
  /**
   * 存放查询结果
   */
  private Cursor queryCursor;
  
  /**
   * 用于判断当前数据库mSqliteDatabase实例是否可以使用
   */
  private boolean isConnect=false;
  
  /**
   * 操作数据库实例
   */
  private SQLiteDatabase mSqliteDatabase;
  private XDBHelper dbHelper;
  
  /**
   * 数据库升级监听接口
   */
  private XDBUpdateListener updateListener;
  
  /**
   * 构造函数
   * @param context 上下文对象
   */
  public XSQLiteDataBase(Context context)
  {
    this(context,new DBParams());
  }
  
  /**
   * 构造函数
   * @param context
   *            上下文对象
   * @param params
   *            构造数据库的参数类，比如数据库名称、版本
   */
  public XSQLiteDataBase(Context context,DBParams params)
  {
    dbHelper=new XDBHelper(context, params.getDb_name(), null, params.getDb_version());
  }
  
  public void setOnUpdateListener(XDBUpdateListener updateListener)
  {
    this.updateListener=updateListener;
    if(dbHelper!=null)
    {
      dbHelper.setOnUpdateListener(updateListener);
    }
  }
  
  /**
   * 打开一个数据库对象
   * @param updateListener
   *              数据库升级监听接口
   * @param isWrite
   *              数据库是否可写
   * @return
   *              一个操作数据库的实例
   */
  public SQLiteDatabase openDataBase(XDBUpdateListener updateListener,boolean isWrite)
  {
    if(isWrite)
    {
      mSqliteDatabase=openWriteDataBase(updateListener);
    }else
    {
      mSqliteDatabase=openReadDataBase(updateListener);
    }
    return mSqliteDatabase;
  }
  
  /**
   * 创建一个可以写的SQLiteDatabase
   * @return
   */
  private SQLiteDatabase openWriteDataBase(XDBUpdateListener updateListener)
  {
    this.updateListener=updateListener;
    if(dbHelper!=null)
    {
      dbHelper.setOnUpdateListener(updateListener);
    }
    try
    {
      mSqliteDatabase=dbHelper.getWritableDatabase();
      isConnect=true;
    }catch(Exception e)
    {
      isConnect=false;
    }
    
    return mSqliteDatabase;
  }
  
  
  /**
   * 创建一个只读的SQLiteDatabase
   * @return
   */
  private SQLiteDatabase openReadDataBase(XDBUpdateListener updateListener)
  {
    this.updateListener=updateListener;
    if(dbHelper!=null)
    {
      dbHelper.setOnUpdateListener(updateListener);
    }
    try
    {
      mSqliteDatabase=dbHelper.getReadableDatabase();
      isConnect=true;
    }catch(Exception e)
    {
      isConnect=false;
    }
    
    return mSqliteDatabase;
  }
  
  /**
   * 判断一个表是否存在
   * @param clazz
   *        表对应类 类型
   * @return
   *        如果表已经存在返回true,否则返回false
   * @throws DBNotOpenException
   *        如果数据库操作实力没有初始化，则抛出此异常
   */
  public boolean hasTable(Class<?> clazz) throws DBNotOpenException
  {
    String name=DBUtils.getTableName(clazz);
    return hasTable(name);
  }
  
  /**
   * 通过表的名字判断一个表是否存在
   * @param tableName
   *            表名
   * @return
   *            存在返回true,否则返回false
   * @throws DBNotOpenException
   *             如果数据库操作实力没有初始化，则抛出此异常.
   */
  public boolean hasTable(String tableName) throws DBNotOpenException
  {
    if(!TextUtils.isEmpty(tableName))
    {
      if(isUseable())
      {
        tableName=tableName.trim();
        //sqlite 中数据库中的表信息 放在“sqlite_master”中
        String queryStr="select count(*) as c from sqlite_master where type='table' and name='"+tableName+"'";
        free();
        queryCursor=mSqliteDatabase.rawQuery(queryStr, null);
        if(queryCursor.moveToNext())
        {
          int count=queryCursor.getInt(0);
          if(count>0)
            return true;
        }
      }else
      {
        throw new DBNotOpenException("数据库已经关闭！");
      }
    }else
    {
      return false;
    }
    return false;
  }
  
  /**
   * 创建一个数据库表
   * @param clazz
   *        数据库表对应的类 类型
   * @return
   *        创建成功返回true,创建失败返回false
   */
  public boolean createTable(Class<?> clazz)
  {
    boolean success=false;
    String createSQL;
    try
    {
      createSQL = DBUtils.createTableSQL(clazz);
      if(createSQL!=null)
      {
       execute(createSQL,null);
       success=true;
      }
    } catch (Exception e)
    {
      success=false;
      Log.v("sql", e.getMessage());
    }
   
    return success;
  }
  
  /**
   * 通过mSqliteDatabase实例完成对数据库的操作
   * @param exeSql
   *          要执行的sql语句（不能是select语句），其中参数适用?占位
   * @param args
   *          sql语句需要的参数
   * @throws DBNotOpenException
   */
  public void execute(String exeSql,Object[] args) throws DBNotOpenException
  {
    if(!TextUtils.isEmpty(exeSql))
    {
      if(isUseable())
      {
        if(args==null)
        {
          mSqliteDatabase.execSQL(exeSql);
        }else
        {
          mSqliteDatabase.execSQL(exeSql, args);
        }
        
      }
      else
      {
        throw new DBNotOpenException("数据库已经关闭");
      }
    }
  }
  
  public boolean execute(SQLBuilder sqlBuilder)
  {
    boolean success=false;
    if(sqlBuilder!=null)
    {
      try
      {
        if(isUseable())
        {
          String sql=sqlBuilder.getSQLStatement();
          LogUtil.v("sql", "execute-->"+sql);
          mSqliteDatabase.execSQL(sql);
          success=true;
        }else
        {
          success=false;
          Log.v("sql", "notUseable");
        }
        
      } catch (IllegalArgumentException e)
      {
        success=false;
        ToastUtil.AlertMessageInBottom(e.getMessage());
      } catch (IllegalAccessException e)
      {
        success=false;
        ToastUtil.AlertMessageInBottom(e.getMessage());
      }
    }
    return success;
  }
  
  /**
   * 根据查询语句，查询数据
   * @param sql
   *          一条select 语句
   * @param selectionArgs
   *          select语句的参数
   * @return
   *          返回查询结果，如果查询异常，则会返回null
   * @throws DBNotOpenException
   *     
   */
  public ArrayList<HashMap<String,String>> query(String sql,String[]selectionArgs) throws DBNotOpenException
  {
    if(isUseable())
    {
      if(sql!=null)
      {
        //释放Cursor
        free();
        queryCursor= mSqliteDatabase.rawQuery(sql, selectionArgs);
        return getResultFromCurosr(queryCursor);
      }
    }else
    {
      throw new DBNotOpenException("数据库已经关闭！");
    }
    return null;
  }
  
  /**
   * 主要用来执行select语句，通过SQLiteDataBase.query实现
   * @param table
   *        查询的表名
   * @param columns
   *        需要返回的列，如果传入空，那么会返回所有列
   * @param selection
   *        过滤条件，其中条件使用？占位
   * @param selectionArgs
   *        过滤条件中的参数
   * @param groupBy
   *        按某列分组
   * @param having
   *        having语句
   * @param orderBy
   *        按某一字段升序或者降序
   * @return
   * @throws DBNotOpenException
   */
  public ArrayList<HashMap<String,String>> query(String table,String[]columns,String selection,
      String[] selectionArgs,String groupBy,String having,String orderBy) throws DBNotOpenException
  {
    if(isUseable())
    {
      free();
      queryCursor=mSqliteDatabase.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
      return getResultFromCurosr(queryCursor);
    }else
    {
      throw new DBNotOpenException("数据库已经关闭!");
    }
  }
  
  /**
   * 
   * @param distinct
   *        是否去掉相同的行
   * @param table
   *        表名。相当于select语句from关键字后面的部分。如果是多表联合查询，可以用逗号将两个表名分开。
   * @param columns
   *        要查询出来的列名。相当于select语句select关键字后面的部分。
   * @param selection
   *        查询条件子句，相当于select语句where关键字后面的部分，在条件子句允许使用占位符“?”
   * @param selectionArgs
   *        对应于selection语句中占位符的值，值在数组中的位置与占位符在语句中的位置必须一致，否则就会有异常。
   * @param groupBy
   *        相当于select语句group by关键字后面的部分
   * @param having
   *        相当于select语句having关键字后面的部分
   * @param orderBy
   *        相当于select语句order by关键字后面的部分，如：personid desc, age asc;
   * @param limit
   *        指定偏移量和获取的记录数，相当于select语句limit关键字后面的部分。
   * @return
   * @throws DBNotOpenException
   */
  public ArrayList<HashMap<String,String>> query(boolean distinct,String table,String[]columns,String selection,
      String[]selectionArgs,String groupBy,String having,String orderBy,String limit) throws DBNotOpenException
  {
    if(isUseable())
    {
      free();
      queryCursor=mSqliteDatabase.query(distinct,table,columns,selection,selectionArgs,groupBy,having,orderBy,limit);
      return getResultFromCurosr(queryCursor);
    }else
    {
      throw new DBNotOpenException("数据库已经关闭!");
    }
  }
  
  /**
   * 
    @param table
   *        表名。相当于select语句from关键字后面的部分。如果是多表联合查询，可以用逗号将两个表名分开。
   * @param columns
   *        要查询出来的列名。相当于select语句select关键字后面的部分。
   * @param selection
   *        查询条件子句，相当于select语句where关键字后面的部分，在条件子句允许使用占位符“?”
   * @param selectionArgs
   *        对应于selection语句中占位符的值，值在数组中的位置与占位符在语句中的位置必须一致，否则就会有异常。
   * @param groupBy
   *        相当于select语句group by关键字后面的部分
   * @param having
   *        相当于select语句having关键字后面的部分
   * @param orderBy
   *        相当于select语句order by关键字后面的部分，如：personid desc, age asc;
   * @param limit
   *        指定偏移量和获取的记录数，相当于select语句limit关键字后面的部分。
   * @return
   * @throws DBNotOpenException
   */
  public ArrayList<HashMap<String,String>> query(String table,String[]columns,String selection,
      String[]selectionArgs,String groupBy,String having,String orderBy,String limit) throws DBNotOpenException
  {
    if(isUseable())
    {
      free();
      queryCursor=mSqliteDatabase.query(table,columns,selection,selectionArgs,groupBy,having,orderBy,limit);
      return getResultFromCurosr(queryCursor);
    }else
    {
      throw new DBNotOpenException("数据库已经关闭!");
    }
  }
  
  /**
   * 从Cursor中获取数据
   * @param cursor
   *        需要获取数据的Cursor
   * @return
   */
  private ArrayList<HashMap<String,String>> getResultFromCurosr(Cursor cursor)
  {
    ArrayList<HashMap<String,String>> results=new ArrayList<HashMap<String,String>>();
    if(cursor!=null)
    {
      while(cursor.moveToNext())
      {
        HashMap<String,String> map=DBUtils.getRowData(cursor);
        results.add(map);
      }
      free();
    }
    return results;
  }
  
  /**
   * 删除一个表
   * @param clazz
   *        要删除的表对应的Class类型
   * @return
   *        true：删除成功   false：删除失败
   * @throws DBNotOpenException
   *        如果数据库关闭，则抛出此异常
   */
  public boolean dropTable(Class<?> clazz) throws DBNotOpenException
  {
    String name=DBUtils.getTableName(clazz);
    return dropTable(name);
  }
  
  /**
   * 根据表名删除数据库表
   * @param tableName
   *        要删除数据库的表名
   * @return
   *        true 删除成功  false 删除失败
   * @throws DBNotOpenException
   *        如果数据库关闭，则抛出此异常
   */
  public boolean dropTable(String tableName) throws DBNotOpenException
  {
    boolean success=false;
    if(isUseable())
    {
      if(!TextUtils.isEmpty(tableName))
      {
        String sql="drop table "+tableName;
        execute(sql, null);
        success=true;
      }
    }else
    {
      success=false;
      throw new DBNotOpenException("数据库已经关闭！");
    }
    return success;
  }
  
  /**
   * 判断该数据库连接是否可以使用
   * @return
   *      true 可以使用  false 不能使用
   */
  public boolean isUseable()
  {
    if(isConnect)
    {
      if(mSqliteDatabase!=null && mSqliteDatabase.isOpen())
      {
        return true;
      }else
      {
        return false;
      }
    }else
    {
      return false;
    }
  }
  
  /**
   * 关闭该数据库连接
   */
  public void close()
  {
    if(mSqliteDatabase!=null)
    {
      mSqliteDatabase.close();
      mSqliteDatabase=null;
    }
  }
  
  /**
   * 释放查询结果
   */
  public void free()
  {
    if (queryCursor != null)
    {
      try
      {
        this.queryCursor.close();
      } catch (Exception e)
      {
        // TODO: handle exception
      }
    }

  }
  
  /**
   * 往数据库里面插入一条记录，插入该对象所有字段
   * @param entity
   *        要插入的对象
   * @return
   *        true 成功    false 失败
   */
  public boolean insert(Object entity)
  {
    return insert(entity,null);
  }
  
  /**
   * 往数据库里面插入一条记录，插入给定字段
   * @param entity
   *          要插入的实体对象
   * @param pairs
   *          需要插入的键值对
   * @return
   *          true 成功  false 失败
   */
  public boolean insert(Object entity,ArrayList<NameValuePair> pairs)
  {
    SQLBuilder builder=SQLBuilderFactory.getInsance().getSQLBuilder(SQLBuilderFactory.INSERT);
    builder.setEntity(entity);
    builder.setFields(pairs);
    return execute(builder);
  }
  
  /**
   * 往数据库里面插入一条记录
   * @param entity
   * @param nullColumn
   * @param values
   * @return
   */
  public boolean insert(Object entity,String nullColumn,ContentValues values)
  {
    if(isUseable())
    {
     return  mSqliteDatabase.insert(DBUtils.getTableName(entity.getClass()), nullColumn, values)>0;
    }else
    {
      return false;
    }
    
  }
  
  
  /**
   * 数据库中删除一条记录
   * @param entity
   *        要被删除的记录
   * @param where
   *        where 条件 使用?作为占位符
   * @param whereArgs
   *        占位符中的参数
   * @return
   *        true 删除成功    false  删除失败
   */
  public boolean delete(Object entity,String where,String[]whereArgs)
  {
    if(isUseable())
    {
      return mSqliteDatabase.delete(DBUtils.getTableName(entity.getClass()), where, whereArgs)>0;
    }else
    {
      return false;
    }
  }
  
  /**
   * 删除一条记录
   * @param clazz
   *        需要删除数据表对应的Class类
   * @param where
   *        where语句
   * @return
   */
  public boolean delete(Class<?> clazz,String where)
  {
    if(isUseable())
    {
      SQLBuilder builder=SQLBuilderFactory.getInsance().getSQLBuilder(SQLBuilderFactory.DELETE);
      builder.setClazz(clazz);
      builder.setCondition(false, where, null, null, null, null);
      return execute(builder);
    }else
    {
      return false;
    }
  }
  
  /**
   * 删除一条记录
   * @param entity
   *        要删除的数据实体
   * @return
   *        true 删除成功   false  删除失败
   */
  public boolean delete(Object entity)
  {
    if(isUseable())
    {
      SQLBuilder builder=SQLBuilderFactory.getInsance().getSQLBuilder(SQLBuilderFactory.DELETE);
      builder.setEntity(entity);
      return execute(builder);
    }else
    {
      return false;
    }
  }
  
  /**
   * 跟新某条记录
   * @param table
   *        需要跟新的表名
   * @param values
   *        需要跟新的字段
   * @param whereClause
   *        where语句 使用?占位
   * @param whereArgs
   *        where参数
   * @return
   */
  public boolean update(String table,ContentValues values,String whereClause,String[] whereArgs)
  {
    if(isUseable())
    {
      return mSqliteDatabase.update(table, values, whereClause, whereArgs)>0;
    }else
    {
      return false;
    }
  }
  
  /**
   * 跟新一条记录
   * @param entity
   *        需要跟新的实体，默认跟新所有字段
   * @return
   */
  public boolean update(Object entity)
  {
    return update(entity,null);
  }
  
  /**
   * 跟新一条记录
   * @param entity
   *        需要跟新的实体，默认跟新所有字段
   * @param where
   *        where语句
   * @return
   *    
   */
  public boolean update(Object entity,String where)
  {
    if(isUseable())
    {
      SQLBuilder builder=SQLBuilderFactory.getInsance().getSQLBuilder(SQLBuilderFactory.UPDATE);
      builder.setCondition(false, where, null, null, null, null);
      builder.setEntity(entity);
      return execute(builder);
    }else
    {
      Log.v("sql","sql-->not use");
      return false;
    }
  }
  
  
  
  
  
  
  /**
   * Interface 数据库升级回调
   */
  public interface XDBUpdateListener
  {
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);
  }
}
