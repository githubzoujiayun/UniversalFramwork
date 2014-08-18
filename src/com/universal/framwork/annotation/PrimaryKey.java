package com.universal.framwork.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据库表对应的主键注解，表明该属性对应的列是主键
 * com.universal.framwork.annotation.PrimaryKey
 * @author yuanzeyao <br/>
 * create at 2014年5月30日 下午5:20:28
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PrimaryKey
{
  /**
   * 设置主键名
   * @return
   */
  public String name() default "";
  
  /**
   * 主键默认值
   * @return
   */
  public String defaultValue() default "";
  
  /**
   * 是否自动增长
   * @return
   */
  public boolean autoIncrement() default false;
}
