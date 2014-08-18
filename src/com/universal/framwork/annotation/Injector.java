package com.universal.framwork.annotation;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

import com.universal.framwork.util.LogUtil;

/**
 * 用来解析注解的
 * com.universal.framwork.inject.InjectParser
 * @author yuanzeyao <br/>
 * create at 2014年5月23日 下午11:09:25
 */
public class Injector
{
  private static final String TAG = "InjectParser";
  
  public static final void initInJectViewId(Object object,View view)
  {
    if(object==null || view==null)
    {
      return;
    }
    try
    {
      Field[] fields=object.getClass().getDeclaredFields();
      if(fields!=null)
      {
        for(Field field:fields)
        {
          field.setAccessible(true);
          if(field.isAnnotationPresent(InjectView.class))
          {
            InjectView injectView=field.getAnnotation(InjectView.class);
            field.set(object, view.findViewById(injectView.id()));
            
            if(field.get(object)!=null)
            {
              Method clickMethod=field.get(object).getClass().getMethod("setOnClickListener", View.OnClickListener.class);
              if(clickMethod!=null)
              {
                if(!TextUtils.isEmpty(injectView.click()))
                {
                  clickMethod.invoke(field.get(object), new EventListener(object).click(injectView.click()));
                }
              }
            }
          }
        }
      }
    }catch(Exception e)
    {
      e.printStackTrace();
    }
    
  }
  
  public static final void InjectResource(Activity activity)
  {
    Field[] allField=activity.getClass().getDeclaredFields();
    if(allField!=null && allField.length>0)
    {
      for(Field field:allField)
      {
        if(field.isAnnotationPresent(InjectResource.class))
        {
          InjectResource(activity, field);
        }
      }
    }
  }

  private static void InjectResource(Activity activity, Field field)
  {
    field.setAccessible(true);
    InjectResource inJectResource=field.getAnnotation(InjectResource.class);
    int resId=inJectResource.id();
    String typeName=activity.getResources().getResourceTypeName(resId);
    try
    {
      if(typeName.equalsIgnoreCase("string"))
      {
          field.set(activity, activity.getResources().getString(resId));
      }else if(typeName.equalsIgnoreCase("drawable"))
      {
          field.set(activity, activity.getResources().getDrawable(resId));
      }else if(typeName.equalsIgnoreCase("layout"))
      {
          field.set(activity, activity.getResources().getLayout(resId));
      }else if(typeName.equalsIgnoreCase("array"))
      {
        if (field.getType().equals(int[].class))
        {
          field.set(activity, activity.getResources()
              .getIntArray(resId));
        } else if (field.getType().equals(String[].class))
        {
          field.set(activity, activity.getResources()
              .getStringArray(resId));
        } else
        {
          field.set(activity, activity.getResources()
              .getStringArray(resId));
        }
      }else if(typeName.equalsIgnoreCase("color"))
      {
        if(field.getType().equals(Integer.TYPE))
        {
          field.set(activity, activity.getResources().getColor(resId));
        }else
        {
          field.set(activity, activity.getResources().getColorStateList(resId));
        }
      }
    }catch(Exception ex)
    {
      
    }
    
  }
}
