package com.universal.framwork.db;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.database.Cursor;
import android.text.TextUtils;

import com.universal.framwork.annotation.Column;
import com.universal.framwork.annotation.PrimaryKey;
import com.universal.framwork.annotation.Table;
import com.universal.framwork.annotation.Transient;
import com.universal.framwork.db.entity.PropertyEntity;
import com.universal.framwork.db.entity.TableInfoEntity;
import com.universal.framwork.util.LogUtil;

public class DBUtils
{
  private static final String TAG = "DBUtils";
  /**
   * 获得某个类对应的表名
   * @param clazz
   * @return
   */
  public static String getTableName(Class<?> clazz)
  {
    Table table=clazz.getAnnotation(Table.class);
    //获得该类的Table注解，如果注解不为null,那么使用注解作为表名，否则使用类名
    if(table!=null && !TextUtils.isEmpty(table.name()))
    {
      return table.name();
    }else
    {
      return clazz.getSimpleName();
    }
  }
  
  /**
   * 获取一个对象中的主键字段
   * @param clazz
   * @return
   */
  public static Field getPrimaryKeyField(Class<?> clazz)
  {
    Field primaryField=null;
    Field [] fields=clazz.getDeclaredFields();
    for(Field tmp :fields)
    {
      if(tmp.isAnnotationPresent(PrimaryKey.class))
      {
        primaryField=tmp;
        break;
      }
    }
    //没有使用PrimaryKey注解,寻找字段名为“_id”的字段作为主键
    if(primaryField==null)
    {
      for(Field tmp:fields)
      {
        if(tmp.getName().equals("_id"))
        {
          primaryField=tmp;
        }
      }
    }
    //没有字段名为"_id"的字段，那么则寻找“id”字段
    if(primaryField==null)
    {
      for(Field tmp:fields)
      {
        if(tmp.getName().equals("id"))
        {
          primaryField=tmp;
        }
      }
    }
    
    if(primaryField==null)
    {
      throw new RuntimeException(clazz.getSimpleName()+" not set primary key");
    }
    return primaryField;
  }
  
  /**
   * 获取主键字段名称，如果没有主键，则返回空
   * @param clazz
   *          需要被获取主键名称的类的Class
   * @return 
   *          返回主键字段名称 ，当没有主键时，返回Null
   */
  public static String getPrimaryKeyName(Class<?> clazz)
  {
    Field field=getPrimaryKeyField(clazz);
    if(field!=null)
    {
      return field.getName();
    }else
    {
      return null;
    }
  }
  
  /**
   * 获取与某一表对应的对象的所有属性
   * @param clazz
   *          和某个表对应的对象
   * @return
   *          该对象的所有属性对应的列
   */
  public static List<PropertyEntity> getPropertyList(Class<?> clazz)
  {
    List<PropertyEntity> propertys=new ArrayList<PropertyEntity>();
    try
    {
      //获取主键名称
      String pkname=getPrimaryKeyName(clazz);
      //拿到所有的字段
      Field[] fields=clazz.getDeclaredFields();
      for(Field tmp:fields)
      {
        if(!isTransient(tmp) && isBaseDateType(tmp))
        {
          if(tmp.getName().equals(pkname))
            continue;
          PropertyEntity entity=new PropertyEntity();
          entity.setColumnName(getColumnNameByField(tmp));
          entity.setName(tmp.getName());
          entity.setDefaultValue(getPropertyDefaultValue(tmp));
          entity.setClazz(tmp.getType());
          propertys.add(entity);
        }
      }
    }catch (Exception e) {
      // TODO: handle exception
      e.printStackTrace();
    }
    return propertys;
  }
  
  /**
   * 判断某一字段是否为透明字段，如果某一个字段被{@link Transient }注解，那么就是
   * 透明字段
   * @param field
   *         需要检查的字段
   * @return
   *         true 则有Transient注解，false 没有Transient注解
   */
  public static boolean isTransient(Field field)
  {
    if(field!=null)
    {
      if(field.isAnnotationPresent(Transient.class))
      {
        return true;
      }else
      {
        return false;
      }
    }else
    {
      throw new RuntimeException("传入的字段不能为空！");
    }
  }
  
  /**
   * 是否为基本的数据类型
   * 
   * @param field
   * @return
   */
  public static boolean isBaseDateType(Field field)
  {
    Class<?> clazz = field.getType();
    return clazz.equals(String.class) || clazz.equals(Integer.class)
        || clazz.equals(Byte.class) || clazz.equals(Long.class)
        || clazz.equals(Double.class) || clazz.equals(Float.class)
        || clazz.equals(Character.class) || clazz.equals(Short.class)
        || clazz.equals(Boolean.class) || clazz.equals(Date.class)
        || clazz.equals(java.util.Date.class)
        || clazz.equals(java.sql.Date.class) || clazz.isPrimitive();
  }
  
  
  /**
   * 根据某一个字段，获取数据库的列名
   * @param field
   *          要获取列名的字段，不要传入Null,否则会抛出{@link NullPointerException}
   * @return
   *          如果有{@link Column} 那么就返回Column的值，如果有{@link PrimaryKey} 那么就返回PrimaryKey的值
   */
  public static String getColumnNameByField(Field field)
  {
    if(field!=null)
    {
      Column column=field.getAnnotation(Column.class);
      if(column!=null && column.name().trim().length()!=0)
      {
        return column.name();
      }
      
      PrimaryKey primayrKey=field.getAnnotation(PrimaryKey.class);
      if(primayrKey!=null && primayrKey.name().trim().length()!=0)
      {
        return primayrKey.name();
      }
      return field.getName();
    }else
    {
      throw new NullPointerException("field is null");
    }
  }
  
  /**
   * 获得默认值
   * 
   * @param field
   * @return
   */
  public static String getPropertyDefaultValue(Field field)
  {
    Column column = field.getAnnotation(Column.class);
    if (column != null && column.defaultValue().trim().length()!=0)
    {
      return column.defaultValue();
    }
    return null;
  }
  
  
  /**
   * 检查是否自增
   * 
   * @param field
   * @return
   */
  public static boolean isAutoIncrement(Field field)
  {
    PrimaryKey primaryKey = field.getAnnotation(PrimaryKey.class);
    if (null != primaryKey)
    {
      LogUtil.v("sql", "is auto -->"+primaryKey.autoIncrement());
      return primaryKey.autoIncrement();
    }
      LogUtil.v("sql", "is auto -->false");
    return false;
  }
  
  public static String createTableSQL(Class clazz) 
  {
    //根据clazz对象得到对应表的一些属性
    TableInfoEntity tableInfoEntity=TableEntityFactory.getInstance().getTableInfoEntity(clazz);
    PropertyEntity pk=tableInfoEntity.getmPkEntity();
    StringBuilder sb=new StringBuilder("create table if not exists '").append(tableInfoEntity.getTableName()).append("'(");
    //如果该表有主键
    if(pk!=null)
    {
        if(pk.getClazz()==int.class || pk.getClazz()==Integer.class)
        {
          //主键时int类型
          if(pk.isAutoIncrement())
          {
            //主键自动增长
            sb.append(pk.getColumnName()).append(" ").append("integer primary key autoincrement,");
          }else
          {
            //主键不自动增长
            sb.append(pk.getColumnName()).append(" ").append("integer primary key,");
          }
          
        }else
        {
          //主键非int类型，不能自动增长
          sb.append(pk.getColumnName()).append(" ").append("text primary key,");
        }
    }else
    {
      //如果没有主键，我们自已给该表添加一个主键id
      sb.append("id integer primary key autoincrement,");
    }
    
    Collection<PropertyEntity> otherProperty=tableInfoEntity.getProperties();
    for(PropertyEntity entity:otherProperty)
    {
      sb.append(entity.getColumnName()).append(" integer").append(",");
    }
    
    sb.deleteCharAt(sb.length()-1);
    sb.append(")");
    LogUtil.d("sql", sb.toString());
    return sb.toString();
  }
  
  /**
   * 将某一个Cursor中的数据存入一个HashMap中
   * @param cursor
   *           需要保存数据的Cursor
   * @return
   */
  public static HashMap<String,String> getRowData(Cursor cursor)
  {
    HashMap<String, String> result=null;
    if(cursor!=null && cursor.getColumnCount()>0)
    {
      result=new HashMap<String, String>();
      int count=cursor.getColumnCount();
      for(int i=0;i<count;i++)
      {
        result.put(cursor.getColumnName(i), cursor.getString(i));
      }
     
    }
    return result;
  }

}
