package com.universal.framwork.db.sqlbuilder;
/**
 * 生产各种SQLBuilder的工厂类
 * com.universal.framwork.db.sqlbuilder.SQLBuilderFactory
 * @author yuanzeyao <br/>
 * create at 2014年6月3日 上午11:26:38
 */
public class SQLBuilderFactory
{
  private static final String TAG = "SQLBuilderFactory";
  /**
   * 生产{@link SelectSQLBuilder}
   */
  public static final int SELECT=1;
  
  /**
   * 生产{@link UpdateSQLBuilder}
   */
  public static final int UPDATE=2;
  
  /**
   * 生产{@link DeleteSQLBuilder}
   */
  public static final int DELETE=3;
  
  /**
   * 生产{@link InsertSQLBuilder}
   */
  public static final int INSERT=4;
  
  /**
   * 使用单例模式
   */
  public static SQLBuilderFactory instance;
  public static SQLBuilderFactory getInsance()
  {
    if(instance==null)
    {
      instance=new SQLBuilderFactory();
    }
    return instance;
  }
  
  /**
   * 根据需求生产不同的SQLBuilder
   * @param type
   *        
   * @return
   */
  public SQLBuilder getSQLBuilder(int type)
  {
    switch(type)
    {
      case SELECT:
        return new SelectSQLBuilder();
      case UPDATE:
        return new UpdateSQLBuilder();
      case DELETE:
        return new DeleteSQLBuilder();
      case INSERT:
        return new InsertSQLBuilder();
      default:
        return null;
    }
  }
}
