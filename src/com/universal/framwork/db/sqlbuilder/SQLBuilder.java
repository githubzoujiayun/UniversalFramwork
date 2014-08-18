package com.universal.framwork.db.sqlbuilder;

import java.util.ArrayList;

import org.apache.http.NameValuePair;

import android.text.TextUtils;

import com.universal.framwork.db.DBUtils;
import com.universal.framwork.util.StringUtils;

/**
 * 构建SQL语句的抽象类
 * com.universal.framwork.db.sqlbuilder.SQLBuilder
 * @author yuanzeyao <br/>
 * create at 2014年6月3日 上午11:20:22
 */
public abstract class SQLBuilder
{
  private static final String TAG = "SQLBuilder";
  
  protected boolean distinct;
  protected String where;
  protected String groupBy;
  protected String having;
  protected String orderBy;
  protected String limit;
  protected Class<?> clazz;
  protected String tableName;
  protected Object entity;
  protected ArrayList<NameValuePair> fields;
  
  public SQLBuilder(Object entity)
  {
    this.entity=entity;
    this.clazz=entity.getClass();
  }
  
  public void setCondition(boolean distinct,String where,String groupBy,String having,String orderBy,String limit)
  {
    this.distinct=distinct;
    this.where=where;
    this.groupBy=groupBy;
    this.having=having;
    this.orderBy=orderBy;
    this.limit=limit;
  }
  
  
  final public String getSQLStatement() throws IllegalArgumentException, IllegalAccessException
  {
    onPreGetStatement();
    return buildSQL();
  }
  
  public void onPreGetStatement() throws IllegalArgumentException, IllegalAccessException
  {
    
  }
  
  public abstract String buildSQL() throws IllegalArgumentException, IllegalAccessException;
  
  protected String buildConditionString()
  {
    StringBuilder query=new StringBuilder();
    appendCondition(query, " WHERE ", where);
    appendCondition(query, " GROUP BY ", where);
    appendCondition(query, " HAVING ", where);
    appendCondition(query, " ORDER BY ", where);
    appendCondition(query, " LIMIT ", where);
    
    return query.toString();
  }
  
  private void appendCondition(StringBuilder sb,String name,String clause)
  {
    if(sb!=null && !TextUtils.isEmpty(clause))
    {
      sb.append(name).append(clause);
    }
  }
  
  public String buildWhere(ArrayList<NameValuePair> conditions)
  {
    StringBuilder sb=new StringBuilder();
    if(conditions!=null)
    {
      sb.append(" WHERE ");
      for(int i=0;i<conditions.size();i++)
      {
        NameValuePair pair=conditions.get(i);
        sb.append(pair.getName()).append("=").append(StringUtils.isNumeric(pair.getValue())?pair.getValue():"'"+pair.getValue()+"'");
        if(i+1<conditions.size())
        {
          sb.append(" AND ");
        }
      }
    }
    return sb.toString();
  }
  
  public void setEntity(Object entity)
  {
    this.entity=entity;
    setClazz(entity.getClass());
  }
  
  public Object getEntity()
  {
    return entity;
  }
  
  public ArrayList<NameValuePair> getFields()
  {
    return fields;
  }
  
  public void setFields(ArrayList<NameValuePair> fields)
  {
    this.fields=fields;
  }
  
  public SQLBuilder()
  {
    
  }
  
  public SQLBuilder(Class<?> clazz)
  {
    setTableName(clazz);
  }
  
  public void setTableName(String tableName)
  {
    this.tableName = tableName;
  }

  public void setTableName(Class<?> clazz)
  {
    this.tableName = DBUtils.getTableName(clazz);
  }
  
  public String getTableName()
  {
    return tableName;
  }
  
  public Class<?> getClazz()
  {
    return clazz;
  }

  public void setClazz(Class<?> clazz)
  {
    setTableName(clazz);
    this.clazz = clazz;
  }
  
  
}
