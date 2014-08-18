package com.universal.framwork.exception;
/**
 * 数据库没有打开异常
 * com.universal.framwork.exception.DBNotOpenException
 * @author yuanzeyao <br/>
 * create at 2014年6月2日 下午5:18:18
 */
public class DBNotOpenException extends Exception
{
  private static final String TAG = "DBNotOpenException";
  public DBNotOpenException(String message)
  {
    super(message);
  }
}
