package com.universal.framwork.db.sqlbuilder;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.universal.framwork.annotation.PrimaryKey;
import com.universal.framwork.db.DBUtils;
import com.universal.framwork.util.StringUtils;

/**
 * 用于构建插入语句的SQL
 * com.universal.framwork.db.sqlbuilder.InsertSQLBuilder
 * @author yuanzeyao <br/>
 * create at 2014年6月3日 上午11:21:37
 */
public class InsertSQLBuilder extends SQLBuilder
{
  private static final String TAG = "InsertSQLBuilder";

  @Override
  public void onPreGetStatement() throws IllegalArgumentException, IllegalAccessException
  {
    if(null==getFields())
    {
      setFields(getFieldAndValues(this.getEntity()));
    }
  }
  
  @Override
  public String buildSQL()
  {
    StringBuilder column=new StringBuilder("insert into "+this.getTableName()).append("(");
    StringBuilder values=new StringBuilder("values(");
    for(int i=0;i<this.getFields().size();i++)
    {
      NameValuePair pair=this.getFields().get(i);
      column.append(pair.getName());
      values.append(StringUtils.isNumeric(pair.getValue())?pair.getValue():"'"+pair.getValue()+"'");
      if(i+1<this.getFields().size())
      {
        column.append(",");
        values.append(",");
      }else
      {
        column.append(")");
        values.append(")");
      }
    }
    column.append(values);
    if(this.getFields().size()>0)
      return column.toString();
    return null;
  }
  
  private ArrayList<NameValuePair> getFieldAndValues(Object entity) throws IllegalArgumentException, IllegalAccessException
  {
    ArrayList<NameValuePair> property=new ArrayList<NameValuePair>();
    if(entity!=null)
    {
      Class clazz=entity.getClass();
      Field[] fields=clazz.getDeclaredFields();
      for(Field field:fields)
      {
        if(!DBUtils.isTransient(field))
        {
          if(DBUtils.isBaseDateType(field))
          {
            if(!field.isAnnotationPresent(PrimaryKey.class))
            {
              String columnName=DBUtils.getColumnNameByField(field);
              field.setAccessible(true);
              property.add(new BasicNameValuePair(columnName!=null?columnName:field.getName(), field.get(entity)==null?"":field.get(entity).toString()));
            }
          }
        }
      }
    }
    return property;
  }
}
