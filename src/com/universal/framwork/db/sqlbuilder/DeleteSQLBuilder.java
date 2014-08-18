package com.universal.framwork.db.sqlbuilder;

import java.lang.reflect.Field;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.text.TextUtils;
import android.util.Log;

import com.universal.framwork.db.DBUtils;
import com.universal.framwork.util.LogUtil;

/**
 * 用来构建删除语句
 * com.universal.framwork.db.sqlbuilder.DeleteSQLBuilder
 * @author yuanzeyao <br/>
 * create at 2014年6月3日 上午11:25:51
 */
public class DeleteSQLBuilder extends SQLBuilder
{
  private static final String TAG = "DeleteSQLBuilder";

  @Override
  public String buildSQL() throws IllegalArgumentException, IllegalAccessException
  {
    StringBuilder delete=new StringBuilder();
    delete.append("delete from ").append(tableName);
    if(entity==null)
    {
      delete.append(buildConditionString());
    }else
    {
      delete.append(buildWhere(buildWhere(entity)));
    }
    Log.v("sql", delete.toString());
    return delete.toString();
  }
  
  private ArrayList<NameValuePair> buildWhere(Object entity) throws IllegalArgumentException, IllegalAccessException
  {
    ArrayList<NameValuePair> where=new ArrayList<NameValuePair>();
    if(entity!=null)
    {
      Class<?> clazz=entity.getClass();
      Field[] fields=clazz.getDeclaredFields();
      for(Field field : fields)
      {
        field.setAccessible(true);
        if(!DBUtils.isTransient(field))
        {
          if(DBUtils.isBaseDateType(field))
          {
            if(!DBUtils.isAutoIncrement(field))
            {
              String columnName=DBUtils.getColumnNameByField(field);
              LogUtil.d("sql", "field.get(entity)"+field.get(entity));
              if(field.get(entity)!=null && field.get(entity).toString().length()>0)
              {
                where.add(new BasicNameValuePair(!TextUtils.isEmpty(columnName)?columnName:field.getName(), field.get(entity).toString()));
              }
            }
          }
        }
      }
      
      if(!where.isEmpty())
      return where;
      else
      return null;
    }
    return null;
  }
}
