package com.universal.framwork.net;

import com.universal.framwork.util.NetWorkUtil.NetType;


/**
 * 网络连接监听
 * com.universal.framwork.net.INetObserver
 * @author yuanzeyao <br/>
 * create at 2014年5月24日 上午9:56:32
 */
public interface INetObserver
{
  /**
   * 成功连接到网络
   * @param netType wifi/mobile/no net
   */
  public void onConnect(NetType netType);
  
  /**
   * 网络断开
   */
  public void onDisConnect();
}
