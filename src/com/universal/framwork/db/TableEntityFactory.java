package com.universal.framwork.db;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;

import android.util.Log;

import com.universal.framwork.db.entity.PropertyEntity;
import com.universal.framwork.db.entity.TableInfoEntity;

/**
 * 一个创建TableEntity的实例
 * com.universal.framwork.db.TableEntityFactory
 * @author yuanzeyao <br/>
 * create at 2014年6月1日 下午11:22:13
 */
public class TableEntityFactory
{
  private static final String TAG = "TableEntityFactory";
  //用于保存已经创建过的TableInfoEntity,可以减少TableInfoEntity的创建
  public static final HashMap<String,TableInfoEntity> tableEntitys=new HashMap<String,TableInfoEntity>();
  
  //单例模式
  public static TableEntityFactory instance;
  public static TableEntityFactory getInstance()
  {
    if(instance==null)
    {
      instance=new TableEntityFactory();
    }
    return instance;
  }
  
  /**
   * 
   * @param clazz
   * @return
   */
  public TableInfoEntity getTableInfoEntity(Class clazz)
  {
    if(clazz == null)
    {
      throw new NullPointerException("class is null");
    }
    
    TableInfoEntity tableInfo=tableEntitys.get(clazz.getName());
    
    if(tableInfo==null)
    {
      tableInfo=new TableInfoEntity();
      tableInfo.setClassName(clazz.getName());
      tableInfo.setTableName(DBUtils.getTableName(clazz));
      //找到主键字段
      Field field=DBUtils.getPrimaryKeyField(clazz);
      Log.v("sql", "field name-->"+field.getName());
      
      if(field!=null)
      {
        //设置主键的一些属性
        PropertyEntity pk=new PropertyEntity();
        //不允许为空
        pk.setAllowNull(false);
        //是否自动增长
        pk.setAutoIncrement(DBUtils.isAutoIncrement(field));
        //主键数据类型
        pk.setClazz(field.getType());
        //主键字段名称
        pk.setColumnName(DBUtils.getColumnNameByField(field));
        //主键默认值
        pk.setDefaultValue(DBUtils.getPropertyDefaultValue(field));
        pk.setPrimaryKey(true);
        //主键属性名称
        pk.setName(field.getName());
        tableInfo.setmPkEntity(pk);
      }else
      {
        //没有主键
        tableInfo.setmPkEntity(null);
      }
      //获取该表的非主键属性
      List<PropertyEntity> otherProperty=DBUtils.getPropertyList(clazz);
      tableInfo.setProperties(otherProperty);
      tableEntitys.put(clazz.getName(), tableInfo);
     
    }
    return tableInfo;
  }
}
