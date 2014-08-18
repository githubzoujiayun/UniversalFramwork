package com.universal.framwork.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用来加载资源的注解
 * com.universal.framwork.inject.InjectResource
 * @author yuanzeyao <br/>
 * create at 2014年5月23日 下午11:03:46
 */

@Target({ElementType.FIELD,ElementType.METHOD,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface InjectResource
{
  int id() default -1;
}
