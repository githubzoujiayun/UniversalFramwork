package com.universal.framwork.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * 说明该字段为透明字段，也就是不用存入数据库的字段
 * com.universal.framwork.annotation.Transient
 * @author yuanzeyao <br/>
 * create at 2014年5月30日 下午5:43:01
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Transient
{

}
