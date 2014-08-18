package com.universal.framwork.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * 将数据库表中的某一列和某个对象的某一属性关联起来
 * com.universal.framwork.annotation.Column
 * @author yuanzeyao <br/>
 * create at 2014年5月30日 下午4:51:28
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Column
{
  public String name() default "";
  
  public String defaultValue() default "";
}
