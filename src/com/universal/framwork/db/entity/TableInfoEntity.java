package com.universal.framwork.db.entity;

import java.util.ArrayList;
import java.util.List;
/**
 * 表的实体类
 * com.universal.framwork.db.entity.TableInfoEntity
 * @author yuanzeyao <br/>
 * create at 2014年6月1日 下午10:57:25
 */
public class TableInfoEntity
{
  private static final String TAG = "TableInfoEntity";
  /**
   * 表对应的类名
   */
  private String className;
  
  /**
   * 表名
   */
  private String tableName;
  
  /**
   * 主键对应的类
   */
  private PropertyEntity mPkEntity;
  
  /**
   * 非主键对应的类
   */
  private List<PropertyEntity> properties=new ArrayList<PropertyEntity>();

  public String getClassName()
  {
    return className;
  }

  public void setClassName(String className)
  {
    this.className = className;
  }

  public String getTableName()
  {
    return tableName;
  }

  public void setTableName(String tableName)
  {
    this.tableName = tableName;
  }

  public PropertyEntity getmPkEntity()
  {
    return mPkEntity;
  }

  public void setmPkEntity(PropertyEntity mPkEntity)
  {
    this.mPkEntity = mPkEntity;
  }

  public List<PropertyEntity> getProperties()
  {
    return properties;
  }

  public void setProperties(List<PropertyEntity> properties)
  {
    this.properties = properties;
  }
  
  
}
