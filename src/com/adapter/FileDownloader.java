package com.adapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;

import com.constant.Constant;
import com.kingPadStudy.tools.FileHandler;

public class FileDownloader extends Thread {
	String _urlStr;//下载文件
	public File _fileSave;//目标文件
	Handler _myHandler;
	int _position;
	public int progress = 0;
	Activity activity ;
	String packageName;
	Intent intent_install;
	/**
	 * 对应的按钮的状态
	 */
	public int APKState = 0;	//0 —— 没有下载， 1—— 正在下载，2——下载完毕，未安装 ，3——已安装
	/*
	 * 是否能够发送消息
	 */
	public boolean canSendMessage = true ;
	/*
	 * 是否停止
	 */
	public boolean isStop = false;
	
	public FileDownloader(String urlStr,File file,Handler myHandler,int position,Activity acti)
	{
		activity = acti;
		_position = position;
		_urlStr = urlStr;
		_fileSave = file;
		_myHandler = myHandler;
	}
	
	@Override
	public void run() {
		if(_fileSave.exists())
		{
			_fileSave.delete();
		}
		try {
			
			
			
			
			
		    URL url = new URL(_urlStr);    
		    URLConnection con = url.openConnection(); 
		    int contentLength = con.getContentLength();
//		    System.out.println("下载文件大小:"+contentLength);
		    InputStream is = con.getInputStream();   
		    int hasRead = 0; //
		    progress = 0;
		    byte[] bs = new byte[128];    
		    int len;    
		    OutputStream os = new FileOutputStream(_fileSave.getPath());
		    APKState = 1;  //开始下载
		    int accumulate = 0;
		    while ((len = is.read(bs)) != -1) {
		    	os.write(bs, 0, len); 
		    	hasRead += len;
		    	accumulate ++;
		    	if(accumulate == 500 ){
//		    		System.out.println("线程准备发送更新请求，canSendMessage="+canSendMessage);
		    		if( canSendMessage ){ 
				      progress = (int)((double)hasRead/(double)contentLength * 100);// 
				      Message msg = _myHandler.obtainMessage(); 
				      msg.arg1 = progress; 
				      msg.arg2 = _position; 
				      msg.sendToTarget(); 
				      accumulate = 0;
		    		}
		    	} 
		    } 
		    //让进度条到满 
		    Message msg = _myHandler.obtainMessage(); 
		    msg.arg1 = 100; 
		    msg.arg2 = _position;
		    msg.sendToTarget();
		    os.close();   
		    is.close(); 
		    isStop = true;
			//写入文件这个下载文件的记录
			FileHandler.setStringToFile(activity, _urlStr, _fileSave.getPath());
			
			
			
			
			
			
			
		    //下载完毕后自动安装
		    if(activity != null){
			  //If is APK file, the request to install.
			  if (_fileSave.getPath().endsWith(".apk")) {
				intent_install = new Intent();
				intent_install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent_install.setAction(android.content.Intent.ACTION_VIEW);
				intent_install.setDataAndType(Uri.fromFile(_fileSave),
			  			"application/vnd.android.package-archive");
			    activity.startActivity(intent_install);
			  }
		    }
		    //下载完毕，处于未安装状态
		    APKState = 2;  //开始下载
		    new Thread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					while(true){
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
//						System.out.println("监听安装activity是否退出...");
						if(isAppOnForeground()){
							//系统中不存在这个activity
//							System.out.println("安装程序已经退出了。。。发送更新消息...");
							//向GameListAdapter 发送一条按钮更新消息
						    Message msg = _myHandler.obtainMessage(); 
							msg.what = Constant.MESSAGE_BUTTON_CHANGE;
							msg.arg1 = _position;
							msg.sendToTarget(); 
							break;
						}
						
					}
				}
			}).start();
		    
		    
		    
		    
		    
		    
		} catch (Exception e) {
				e.printStackTrace();
		}
	}

	
	
	private boolean isAppOnForeground() {
		ActivityManager activityManager = (ActivityManager) activity.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
		String packageName = activity.getApplicationContext().getPackageName();
		List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses(); 
		if (appProcesses == null)
			return false;
			for (RunningAppProcessInfo appProcess : appProcesses) {
				if(appProcess.processName.equals(packageName)&&appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) 
				{
					return true;
				}
			}
		return false;
	}
	
}
