package com.universal.framwork.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用来关联View和ViewId，简化findViewById操作
 * com.universal.framwork.inject.InjectView
 * @author yuanzeyao <br/>
 * create at 2014年5月23日 下午11:04:19
 */

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface InjectView
{
   public int id() default -1;
   public String click() default "";
}
