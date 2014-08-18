package com.universal.framwork.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import android.webkit.WebSettings.RenderPriority;
/**
 * 将数据库中的表和某一个类关联起来
 * com.universal.framwork.annotation.Table
 * @author yuanzeyao <br/>
 * create at 2014年5月30日 下午4:48:52
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Table
{
  public String name() default "";
}
