package com.kingPadStudy.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.bean.Down;
import com.constant.Constant;
import com.king.MainActivity;
import com.king.SelectTeach;


public final class GameDownloadService extends Service {
	private final IBinder mBinder = new LocalBinder();
	private final static String TAG = "DownloadService";
	private final ExecutorService mExecutorService = Executors.newCachedThreadPool();
	private final static int number_download_max = 5;
	private final static Semaphore semaphore = new Semaphore(number_download_max);
	//TODO 改名，下载队列
	public final static HashMap<String, DownloadBean> mHashMap = new HashMap<String, DownloadBean>();
	// 下载数据错误
	public final static String ACTION_ERROR = "game_action_error";
	// 未下载，正在等待下载，
	public final static String ACTION_PASUE = "game_action_pause";
	// 开始下载
	public final static String ACTION_START = "game_action_start";
	// 所有操作都完成
	public final static String ACTION_COMPLETE = "game_action_complete";
	// 下载进度
	public final static String ACTION_PROGRESS = "game_action_progress";
	// 移除map中的下载数据
	public final static String ACTION_REMOVE = "game_action_remove";
	// 获取当前所有的下载信息记录，返回当前的hashmap对象
	public final static String ACTION_GET_ALL = "game_action_get_all";
	// 接收到ACTION_GET_ALL消息之后，发送一条ACTION_RETURN_ALL广播，发送的内容为HashMap对象
	public final static String ACTION_RETURN_ALL = "game_action_return_all";
	// 发送一条广播用于天际一条下载记录信息
	public final static String ACTION_ADD_DOWNLOAD = "game_action_add_download";
	
	
	
	// 消息关键字key
	public final static String KEY = "key";
	
	public class LocalBinder extends Binder {
		public GameDownloadService getService() {
			return GameDownloadService.this;
		}
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_REMOVE);
        filter.addAction(ACTION_GET_ALL);
        filter.addAction(ACTION_ADD_DOWNLOAD);
        registerReceiver(mReceiver, filter);
        System.out.println("GameDownloadService  服务器onCreate..");
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		
		return mBinder;
	}
	
	
	private void download(final DownloadBean download) {
		if(null != mHashMap.get(download.getKey())) {
			return;
		}
		mHashMap.put(download.getKey(), download);
		final String key = download.getKey();
		System.out.println("开始下载资源："+download.getResourceName());
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				try {
					// 获取信号量
					semaphore.acquire();
					// 创建文件目录
					File file = new File(Constant.DOWNLOAD_PATH);
				    if(!file.exists())
				    	file.mkdirs();
				    final String savePath = Constant.DOWNLOAD_PATH + "/" + 
				    		download.getResourceNumber() + "_" + download.getFileName();
				    download.setSavePath(savePath); 
					Intent intent = new Intent(ACTION_START);
					intent.putExtra(KEY, key);
					intent.putExtra(Constant.BROAD_CAST_SAVE_PATH, savePath); 
					sendBroadcast(intent);
					download.setStatus(ACTION_START);
				    HttpParams params = new BasicHttpParams();
					HttpConnectionParams.setConnectionTimeout(params, 20000);
					HttpConnectionParams.setSoTimeout(params,8000);
					HttpClient httpClient = new DefaultHttpClient(params);
					HttpGet get = new HttpGet(download.getResourceUrl());
					System.out.println(download.getResourceUrl());
					HttpResponse response = httpClient.execute(get);
					if(response.getStatusLine().getStatusCode() != 200) {
				    	throw new IOException("错误代码：" + response.getStatusLine().getStatusCode());
				    }
					// 先删除原有文件，然后创建新文件
					file = new File(savePath);
					if(file.exists())
						file.delete();
					file.createNewFile();
					long current = 0;
					long total = (long) response.getEntity().getContentLength();
					InputStream inputStream = response.getEntity().getContent();
					FileOutputStream outputStream = new FileOutputStream(file);
					byte [] buffer =  new byte[10240]; 
					int ch;
					int j = 0;
					System.out.println("在GameDownloadService中，开始下载。。");
					while((ch = inputStream.read(buffer)) != -1) {
						outputStream.write(buffer, 0, ch);
						current += ch;
						int pos = (int) ((current * 1.0 / total) * 100);
						if(j == 50) {
							intent = new Intent(ACTION_PROGRESS);
							intent.putExtra(ACTION_PROGRESS, pos);
							intent.putExtra(KEY, key);
							sendBroadcast(intent);
							download.setProgress(pos);
							j = 0;
						}
						j++;
					}
					System.out.println("在GameDownloadService中，下载完毕。。");
					inputStream.close();
					outputStream.close();
					intent = new Intent(ACTION_PROGRESS);
					intent.putExtra(ACTION_PROGRESS, 100);
					intent.putExtra(KEY, key);
					sendBroadcast(intent);
					System.out.println("在GameDownloadService中，发送100%消息。。");
					download.setProgress(100);
					//发送下载成功广播
					intent = new Intent(ACTION_COMPLETE);
					intent.putExtra(KEY, key);
					sendBroadcast(intent);
					System.out.println("在GameDownloadService中，发送ACTION_COMPLETE消息。。");
					download.setStatus(ACTION_COMPLETE);
				} catch (Exception e) {
					Intent intent = new Intent(ACTION_ERROR);
					intent.putExtra(KEY, key);
					sendBroadcast(intent);
					e.printStackTrace();
				}
				finally {
					semaphore.release();
				}
			}
		};
		mExecutorService.execute(runnable);
	}

	/**
	 * 判断下载是否空闲
	 * @return ： 又资源正在下载返回false，否则true
	 */ //TODO
	public static boolean canClose() {
		return semaphore.availablePermits() == number_download_max ? true : false;
	}
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("服务onReceive..");
			String action = intent.getAction();
			if(action.equals(ACTION_REMOVE)) {
				String key = intent.getStringExtra(ACTION_REMOVE);
				if(key == null)
					return;
				mHashMap.remove(mHashMap.get(key));
			}
			else if(action.equals(ACTION_GET_ALL)) {
				Intent intent2 = new Intent(ACTION_RETURN_ALL);
				intent2.putExtra(ACTION_RETURN_ALL, mHashMap);
				sendBroadcast(intent2);
			}
			else if(action.equals(ACTION_ADD_DOWNLOAD)) {
				Log.e(TAG, "接收到下载请求");
				DownloadBean bean = (DownloadBean) intent.getParcelableExtra(ACTION_ADD_DOWNLOAD);
				System.out.println("接受的下载资源名称："+bean.getResourceName());
				download(bean);
			}
		}
	};
	
	public void onDestroy() {
		super.onDestroy();
		//TODO： 在发布时，注释该数据
		Log.e(TAG, "退出调用");
		mExecutorService.shutdown();
		// 该数据请求之后，第二次在打开时就不能在显示当前那些数据已经下载过，
		// TODO：清除与不清除看需要
		mHashMap.clear();
		unregisterReceiver(mReceiver);
	};
}

