package com.net;

/**
 * 该接口为数据通知接口
 * @author lenovo
 *
 */
public interface RequestListener
{
	/**
	 * 数据加载完成时通知接口
	 */
	 public void onComplete(Object obj);
	 /**
	  * 加载数据时出错通知接口
	  */
     public void onError(String errMsg);
}
