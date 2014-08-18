package com.universal.framwork.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class DontPressWithParentImageView extends ImageView
{
  private static final String TAG = "DontPressWithParentImageView";
  public DontPressWithParentImageView(Context context, AttributeSet attrs)
  {
    super(context, attrs);
  }
  
  @Override
  public void setPressed(boolean pressed)
  {
    if (pressed && ((View) getParent()).isPressed())
    {
      return;
    }

    super.setPressed(pressed);
  }

  
}
