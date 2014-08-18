package com.universal.framwork.db.sqlbuilder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.text.TextUtils;
import android.util.Log;

import com.universal.framwork.annotation.PrimaryKey;
import com.universal.framwork.db.DBUtils;
import com.universal.framwork.util.StringUtils;

/**
 * ┏┛┻━━━┛┻┓
┃｜｜｜｜｜｜｜┃
┃　　　━　　　┃
┃　┳┛ 　┗┳ 　┃
┃　　　　　　　┃
┃　　　┻　　　┃
┃　　　　　　　┃
┗━┓　　　┏━┛
　　┃　   　┃　　
　　┃　   　┃　　
　　┃　   　┃　　
　　┃　   　┃
　　┃　　　┗━━━┓
　　┃                ┣┓
　　┃                  ┃
　　┗┓┓┏━┳┓┏┛
　　　┃┫┫　┃┫┫
　　　┗┻┛　┗┻┛

 * 用来构建跟新语句
 * com.universal.framwork.db.sqlbuilder.UpdateSQLBuilder
 * @author yuanzeyao <br/>
 * create at 2014年6月3日 上午11:26:09
 */
public class UpdateSQLBuilder extends SQLBuilder
{
  private static final String TAG = "UpdateSQLBuilder";

  @Override
  public void onPreGetStatement() throws IllegalArgumentException, IllegalAccessException
  {
    if(fields==null)
    {
      setFields(getFieldsAndValue(entity));
    }
  }
  @Override
  public String buildSQL() throws IllegalArgumentException, IllegalAccessException
  {
    
    StringBuilder stringBuilder = new StringBuilder(256);
    stringBuilder.append("update ");
    stringBuilder.append(tableName).append(" set ");

    ArrayList<NameValuePair> needUpdate = getFields();
    for (int i = 0; i < needUpdate.size(); i++)
    {
      NameValuePair nameValuePair = needUpdate.get(i);
      stringBuilder
          .append(nameValuePair.getName())
          .append(" = ")
          .append(StringUtils.isNumeric(nameValuePair.getValue()) ? nameValuePair.getValue() : "'"
              + nameValuePair.getValue() + "'");
      if (i + 1 < needUpdate.size())
      {
        stringBuilder.append(", ");
      }
    }
    if (!StringUtils.isEmpty(this.where))
    {
      stringBuilder.append(buildConditionString());
    } else
    {
      stringBuilder.append(buildWhere(buildWhere(this.entity)));
    }
    Log.v("sql", stringBuilder.toString());
    return stringBuilder.toString();
  }
  
  /**
   * 从实体加载,更新的数据
   * 
   * @return
   * @throws IllegalArgumentException
   * @throws IllegalAccessException
   */
  public static ArrayList<NameValuePair> getFieldsAndValue(Object entity)
      throws IllegalArgumentException,
      IllegalAccessException
  {
    // TODO Auto-generated method stub
    ArrayList<NameValuePair> arrayList = new ArrayList<NameValuePair>();
    if(entity!=null)
    {
      Class<?> clazz = entity.getClass();
      Field[] fields = clazz.getDeclaredFields();
      for (Field field : fields)
      {
        field.setAccessible(true);
        if (!DBUtils.isTransient(field))
        {
          if (DBUtils.isBaseDateType(field))
          {
            PrimaryKey annotation = field
                .getAnnotation(PrimaryKey.class);
            if (annotation == null || !annotation.autoIncrement())
            {
              String columnName = DBUtils.getColumnNameByField(field);
              if(field.get(entity)!=null)
              {
                arrayList.add(new BasicNameValuePair(!TextUtils.isEmpty(columnName) ? columnName : field
                    .getName(), field.get(entity).toString()));
              }
              
             
            }
          }
        }
      }
      return arrayList;
    }
    return null;
  }
  
  public ArrayList<NameValuePair> buildWhere(Object entity) throws IllegalArgumentException, IllegalAccessException
  {
    ArrayList<NameValuePair> property=new ArrayList<NameValuePair>();
    if(entity!=null)
    {
      
      Class clazz=entity.getClass();
      Field[]fields=clazz.getDeclaredFields();
      for(Field field :fields)
      {
        field.setAccessible(true);
        if(!DBUtils.isTransient(field))
        {
          if(DBUtils.isBaseDateType(field))
          {
            Annotation annotation = field
                .getAnnotation(PrimaryKey.class);
            if (annotation != null)
            {
              String columnName = DBUtils.getColumnNameByField(field);
              property.add(new BasicNameValuePair((columnName != null && !columnName
                  .equals("")) ? columnName : field.getName(), field.get(entity).toString()));
             
            }
          }
        }
      }
    }
    if(!property.isEmpty())
      return property;
    else
      return null;
  }
}
