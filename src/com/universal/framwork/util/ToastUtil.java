package com.universal.framwork.util;

import android.view.Gravity;
import android.widget.Toast;

import com.universal.framwork.XApplication;

/**
 * 封装Toast,便于操作
 * com.universal.framwork.util.ToastUtil
 * @author yuanzeyao <br/>
 * create at 2014年5月24日 上午11:05:36
 */
public class ToastUtil
{
  private static final String TAG = "ToastUtil";
  
  /**
   * 居中显示，传入字符串id
   * @param msg 字符串id
   */
  public static final void AlertMessageInCenter(int msg)
  {
    XApplication application=XApplication.getInstance();
    String str_msg=application.getResources().getString(msg);
    Toast toast=Toast.makeText(application, str_msg, Toast.LENGTH_SHORT);
    toast.setGravity(Gravity.CENTER, 0, 0);
    toast.show();
  }
  
  /**
   * 居中显示
   * @param msg  直接传入要显示的字符串
   */
  public static final void AlertMessageInCenter(String msg)
  {
    XApplication application=XApplication.getInstance();
    Toast toast=Toast.makeText(application, msg, Toast.LENGTH_SHORT);
    toast.setGravity(Gravity.CENTER, 0, 0);
    toast.show();
  }
  
  
  /**
   * 底部显示
   * @param msg 要显示字符串的id
   */
  public static final void AlertMessageInBottom(int msg)
  {
    XApplication application=XApplication.getInstance();
    String str_msg=application.getResources().getString(msg);
    Toast.makeText(application, str_msg, Toast.LENGTH_SHORT).show();;
  }
  
  /**
   * 底部显示
   * @param msg 要显示的字符串
   */
  public static final void AlertMessageInBottom(String msg)
  {
    XApplication application=XApplication.getInstance();
    Toast.makeText(application, msg, Toast.LENGTH_SHORT).show();;
  }
}
