package com.universal.framwork.db.sqlbuilder;

import java.util.regex.Pattern;

import android.text.TextUtils;

/**
 * 用于构建select语句
 * com.universal.framwork.db.sqlbuilder.SelectSQLBuilder
 * @author yuanzeyao <br/>
 * create at 2014年6月3日 上午11:25:16
 */
public class SelectSQLBuilder extends SQLBuilder
{
  private static final String TAG = "SelectSQLBuilder";
  protected Pattern sLimitPattern = Pattern
      .compile("\\s*\\d+\\s*(,\\s*\\d+\\s*)?");

  @Override
  public String buildSQL()
  {
    StringBuilder select=new StringBuilder();
    if(TextUtils.isEmpty(groupBy) && !TextUtils.isEmpty(having))
    {
      throw new IllegalArgumentException("having clauses is only permitted only with groupBy");
    }
    
    if(!TextUtils.isEmpty(limit) && !sLimitPattern.matcher(limit).matches())
    {
      throw new IllegalArgumentException("limit format is not correct");
    }
    
    select.append(" select ");
    if(distinct)
    {
      select.append(" distinct ");
    }
    select.append(" * ");
    select.append(" from ");
    select.append(tableName);
    select.append(buildConditionString());
    return select.toString();
  }
}
