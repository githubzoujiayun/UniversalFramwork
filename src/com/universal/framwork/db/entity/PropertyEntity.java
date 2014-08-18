package com.universal.framwork.db.entity;
/**
 * 对象属性实体类
 * com.universal.framwork.db.entity.PropertyEntity
 * @author yuanzeyao <br/>
 * create at 2014年5月30日 下午5:53:03
 */
public class PropertyEntity
{
  private static final String TAG = "PropertyEntity";
  /**
   * 对象的属性名
   */
  private String name;
  
  /**
   * 该对象对应表的列名
   */
  private String columnName;
  
  /**
   * 对象所属类型
   */
  private Class<?> clazz;
  
  /**
   * 该列的默认值
   */
  private Object defaultValue;
  
  /**
   * 该列是否允许为空
   */
  private boolean isAllowNull;
  
  /**
   * 列的索引
   */
  private int index;
  
  /**
   * 是否是主键
   */
  private boolean primaryKey;
  
  /**
   * 是否自动增长
   */
  private boolean autoIncrement;
  
  /**
   * 属性实体的构造函数
   * @param name
   * @param clazz
   * @param defaultValue
   * @param primaryKey
   * @param isAllowNull
   * @param autoIncrement
   * @param columnName
   */
  public PropertyEntity(String name,Class<?> clazz,Object defaultValue,boolean primaryKey,
      boolean isAllowNull,boolean autoIncrement,String columnName)
  {
    this.name=name;
    this.clazz=clazz;
    this.defaultValue=defaultValue;
    this.primaryKey=primaryKey;
    this.autoIncrement=autoIncrement;
    this.isAllowNull=isAllowNull;
    this.columnName=columnName;
    
  }
  
  public PropertyEntity()
  {
    
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public String getColumnName()
  {
    return columnName;
  }

  public void setColumnName(String columnName)
  {
    this.columnName = columnName;
  }

  public Class<?> getClazz()
  {
    return clazz;
  }

  public void setClazz(Class<?> clazz)
  {
    this.clazz = clazz;
  }

  public Object getDefaultValue()
  {
    return defaultValue;
  }

  public void setDefaultValue(Object defaultValue)
  {
    this.defaultValue = defaultValue;
  }

  public boolean isAllowNull()
  {
    return isAllowNull;
  }

  public void setAllowNull(boolean isAllowNull)
  {
    this.isAllowNull = isAllowNull;
  }

  public int getIndex()
  {
    return index;
  }

  public void setIndex(int index)
  {
    this.index = index;
  }

  public boolean isPrimaryKey()
  {
    return primaryKey;
  }

  public void setPrimaryKey(boolean primaryKey)
  {
    this.primaryKey = primaryKey;
  }

  public boolean isAutoIncrement()
  {
    return autoIncrement;
  }

  public void setAutoIncrement(boolean autoIncrement)
  {
    this.autoIncrement = autoIncrement;
  }
  
  
}
