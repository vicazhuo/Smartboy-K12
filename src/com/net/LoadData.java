package com.net;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;

import com.constant.Constant;
import com.data.ActivateData;
import com.data.BaseData;
import com.data.BookData;
import com.data.BuyResourceData;
import com.data.CatalogData;
import com.data.ClientData;
import com.data.DownloadData;
import com.data.DownloadLogData;
import com.data.DownloadPackageData;
import com.data.GameData;
import com.data.GameLogData;
import com.data.MetaData;
import com.data.MetaUtilData;
import com.data.PackageData;
import com.data.PayLogData;
import com.data.PoemData;
import com.data.RegistData;
import com.data.ResourceData;
import com.data.UpdatePwdData;
import com.data.UserRegisterData;

/**
 * 加载数据接口类
 * 
 * 该类的实现为：
 * 使用两个静态内部类，对消息进行调度，主要是为了节约handler资源，
 * MESSAGE_OK:表示数据请求成功
 * MESSAGE_ERROR:表示数据请求失败
 * sThreadPool:线程池提交接口
 * sHandler：handler数据处理接口
 * @author mjh
 *
 */
public final class LoadData {
	private final static int MESSAGE_OK = 0x1;
	private final static int MESSAGE_ERROR = 0x2;
	private static ThreadPoolExecutor sThreadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(3);
	private static IntervalHandler sHandler = new IntervalHandler();
	
	public static void newHandler(){
		sHandler = new IntervalHandler();
		sThreadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(3);
	}
	
	public static void clear() {
		sThreadPool.shutdown();
	}
	
	public static void shutdownNow() {
		sThreadPool.shutdownNow();
	}
	
	public static void loadData(final String control_name, final RequestParameter parameter,
			final RequestListener listener) {
		
		sThreadPool.submit(new Runnable() {
			public void run() {
				// 设置为后台线程，当主线程退出时，所有的后台线程都会退出
				Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
				
				// 保存请求关键字信息
				Bundle bundle = new Bundle();
				bundle.putString("control_name", control_name);
				
				BaseData data = loadObject(control_name);
				Message msg = null;
				try	{
					if(data == null) 
					{
						// 请求关键字错误
						msg = Message.obtain(sHandler, MESSAGE_ERROR, new DataBean(null, listener, "请求关键字错误！"));
						 data = null;
					}
					else
					{
						// 解析数据
						data.startParse(parameter);
						msg = Message.obtain(sHandler, MESSAGE_OK, new DataBean(data, listener, null));
					}
				}
				catch (Exception e)	{
					e.printStackTrace();
					msg = Message.obtain(sHandler, MESSAGE_ERROR, new DataBean(null, listener, "错误消息：" + e.getMessage()));
					data = null;
				}
				finally	{
					msg.setData(bundle);
					msg.sendToTarget();
				}
				
			} // run
		});
		
	} // loadData
	
	
	
	
	public void loadData2(final String control_name, final RequestParameter parameter,
			final RequestListener listener) {
		
		sThreadPool.submit(new Runnable() {
			
			public void run() {
//				System.out.println("^^^^^^^loadData2^^^^^^^^^^^开始跑线程。。。 ");
				// 设置为后台线程，当主线程退出时，所有的后台线程都会退出
				Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
				
				// 保存请求关键字信息
				Bundle bundle = new Bundle();
				bundle.putString("control_name", control_name);
				
				BaseData data = loadObject(control_name);
				Message msg = null;
				
				try	{
					if(data == null) 
					{
						// 请求关键字错误
						msg = Message.obtain(sHandler, MESSAGE_ERROR, new DataBean(null, listener, "请求参数错误！"));
						 data = null;
					}
					else
					{
						// 解析数据
						data.startParse(parameter);
						msg = Message.obtain(sHandler, MESSAGE_OK, new DataBean(data, listener, null));
					}
					
				} 
				catch (Exception e)	{
					
					e.printStackTrace();
					msg = Message.obtain(sHandler, MESSAGE_ERROR, new DataBean(null, listener, "错误消息：" + e.getMessage()));
					data = null;
					
				}
				finally	{
					msg.setData(bundle);
					msg.sendToTarget();
				}
				
			} // run
		});
		
	} // loadData
	
	
	
	
	
	
	
	
	private static BaseData loadObject(final String control_name) {
//		System.out.println("loadObject  关键字为："+control_name+",Constant.BOOK_DATA="+Constant.BOOK_DATA);
		BaseData data = null;
		if(control_name.equals(Constant.CATALOG_DATA)) 
		{
			data = new CatalogData();
		}else if(control_name.equals(Constant.REGIST_USER_DATA)){
			data = new UserRegisterData();
		}
		else if(control_name.equals(Constant.META_DATA)) 
		{
			data = new MetaData();
		}
		else if(control_name.equals(Constant.POEM_DATA)) 
		{
			data = new PoemData();
		}
		else if(control_name.equals(Constant.BOOK_DATA)) 
		{
			data = new BookData();
		}
		else if(control_name.equals(Constant.META_UTIL_DATA))
		{
			data= new MetaUtilData();
		}
		else if(control_name.equals(Constant.PACKAGE_DATA))
		{
			data = new PackageData();
		} 
		else if(control_name.equals(Constant.REGIST_DATA))
		{  
			data = new RegistData();
		}
		else if(control_name.equals(Constant.ACTIVATE_DATA))
		{ 
			data = new ActivateData();
		}
		else if(control_name.equals(Constant.BUY_RESOURCE_DATA))
		{
			data = new BuyResourceData();
		}
		else if(control_name.equals(Constant.UPDATE_PWD_DATA))
		{
			data = new UpdatePwdData();
		}
		else if(control_name.equals(Constant.CLIENT_DATA)) 
		{
			data = new ClientData();
		}
		else if(control_name.equals(Constant.PAY_LOG_DATA)) 
		{
			data = new PayLogData();
		}
		else if(control_name.equals(Constant.RESOURCE_DATA))
		{
			data = new ResourceData();
		}
		else if(control_name.equals(Constant.DOWNLOAD_DATA))
		{
			data = new DownloadData();
		}
		else if(control_name.equals(Constant.DOWNLOAD_PACKAGE_DATA)) 
		{
			data = new DownloadPackageData();
		}
		else if(control_name.equals(Constant.DOWNLOAD_LOGS_DATA)) 
		{
			data = new DownloadLogData();
		}
		else if(control_name.equals(Constant.GAME_DATA))
		{
			 data = new GameData();
		}
		else if(control_name.equals(Constant.GAME_LOG_DATA)) 
		{
			data = new GameLogData();
		}
		else 
		{
			return null;
		}
		
		return data;
		
	} // loadDta
	



			
	private final static class IntervalHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			DataBean bean = (DataBean) msg.obj;
			if(bean == null || bean.listener == null)
				return;
			
			switch(msg.what) {
			case MESSAGE_OK:
				bean.listener.onComplete(bean.obj);
				break;
			case MESSAGE_ERROR:
				bean.listener.onError(bean.error);
				break;
			}
		} // handleMessage
		
	} // IntervalHandler
	
	
	private final static class DataBean {
		public Object obj;
		public RequestListener listener;
		public String error;
		public DataBean(Object obj, RequestListener listener, String error) {
			super();
			this.obj = obj;
			this.listener = listener;
			this.error = error;
		}
	}
	
}