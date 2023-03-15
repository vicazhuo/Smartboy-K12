package com.kingPadStudy.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
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
import com.kingPadStudy.activity.KingPadStudyActivity;
import com.kingPadStudy.tools.FileHandler;
import com.kingpad.KingPadApp;
import com.net.LoadData;
import com.net.RequestParameter;
import com.utils.Util;


 
public final class DownloadService extends Service {
	private final IBinder mBinder = new LocalBinder();
	private final static String TAG = "DownloadService";
	private final ExecutorService mExecutorService = Executors
			.newCachedThreadPool();
	private final static int number_download_max = 4;
	private final static Semaphore semaphore = new Semaphore(
			number_download_max);
	// 改名，下载队列
	public final static HashMap<String, DownloadBean> mHashMap = new HashMap<String, DownloadBean>();
	// 改名，下载队列
	public final static HashMap<String, DownloadBean> mHashMap_Game = new HashMap<String, DownloadBean>();
	// 消息关键字key
	public final static String KEY = "key";
	// 下载数据错误
	public final static String ACTION_ERROR = "action_error";
	// 未下载，正在等待下载，
	public final static String ACTION_PASUE = "action_pause";
	// 开始下载
	public final static String ACTION_START = "action_start";
	// 开始解压
	public final static String ACTION_UNZIP = "action_unzip";
	// 所有操作都完成
	public final static String ACTION_COMPLETE = "action_complete";
	// 下载进度
	public final static String ACTION_PROGRESS = "action_progress";

	// 下载游戏数据错误
	public final static String ACTION_GAME_ERROR = "action_game_error";
	// 未下载游戏，正在等待下载游戏，
	public final static String ACTION_GAME_PASUE = "action_game_pause";
	// 开始下载游戏
	public final static String ACTION_GAME_START = "action_game_start";
	// 所有游戏操作都完成
	public final static String ACTION_GAME_COMPLETE = "action_game_complete";
	// 游戏下载进度
	public final static String ACTION_GAME_PROGRESS = "action_game_progress";
	// 移除map中的下载数据
	public final static String ACTION_REMOVE = "action_remove";
	// 移除游戏map中的下载数据
	public final static String ACTION_GAME_REMOVE = "action_game_remove";
	// 获取当前所有的下载信息记录，返回当前的hashmap对象
	public final static String ACTION_GET_ALL = "action_get_all";
	// 获取当前所有的游戏下载信息记录，返回当前的hashmap对象
	public final static String ACTION_GAME_GET_ALL = "action_game_get_all";
	// 接收到ACTION_GET_ALL消息之后，发送一条ACTION_RETURN_ALL广播，发送的内容为HashMap对象
	public final static String ACTION_GAME_RETURN_ALL = "action_game_return_all";
	// 接收到ACTION_GET_ALL消息之后，发送一条ACTION_RETURN_ALL广播，发送的内容为HashMap对象
	public final static String ACTION_RETURN_ALL = "action_return_all";
	// 发送一条广播用于天际一条下载记录信息
	public final static String ACTION_ADD_DOWNLOAD = "action_add_download";
	// 益智小游戏 添加
	public final static String ACTION_GAME_ADD_DOWNLOAD = "action_game_add_download";

	public static Context context;

	public class LocalBinder extends Binder {
		public DownloadService getService() {
			return DownloadService.this;
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_REMOVE);
		filter.addAction(ACTION_GET_ALL);
		filter.addAction(ACTION_ADD_DOWNLOAD);
		filter.addAction(ACTION_GAME_REMOVE);
		filter.addAction(ACTION_GAME_GET_ALL);
		filter.addAction(ACTION_GAME_ADD_DOWNLOAD);
		registerReceiver(mReceiver, filter);
//		System.out.println("服务器onCreate..");
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
//			System.out.println("服务onReceive..");
			String action = intent.getAction();
			if (action.equals(ACTION_REMOVE)) {
//				Log.e(TAG, "接收到删除请求");
				String key = intent.getStringExtra(ACTION_REMOVE);
				if (key == null)
					return;
				mHashMap.remove(mHashMap.get(key));
			} else if (action.equals(ACTION_GET_ALL)) {
//				Log.e(TAG, "接收到哈希表请求");
				Intent intent2 = new Intent(ACTION_RETURN_ALL);
				intent2.putExtra(ACTION_RETURN_ALL, mHashMap);
				sendBroadcast(intent2);
			} else if (action.equals(ACTION_ADD_DOWNLOAD)) {
				Log.e(TAG, "接收到下载请求");
				DownloadBean bean = (DownloadBean) intent
						.getParcelableExtra(ACTION_ADD_DOWNLOAD);
				System.out.println("接受的下载资源ID：" + bean.getResrouceId());
				download(bean);
			} else if (action.equals(ACTION_GAME_REMOVE)) {
//				Log.e(TAG, "接收到游戏删除请求");
				String key = intent.getStringExtra(ACTION_GAME_REMOVE);
				if (key == null)
					return;
				mHashMap.remove(mHashMap.get(key));
			} else if (action.equals(ACTION_GAME_GET_ALL)) {
//				Log.e(TAG, "接收到游戏哈希表请求");
				Intent intent2 = new Intent(ACTION_GAME_RETURN_ALL);
				intent2.putExtra(ACTION_GAME_RETURN_ALL, mHashMap_Game);
				sendBroadcast(intent2);
			} else if (action.equals(ACTION_GAME_ADD_DOWNLOAD)) {
//				Log.e(TAG, "接收到游戏下载请求");
				DownloadBean bean = (DownloadBean) intent
						.getParcelableExtra(ACTION_GAME_ADD_DOWNLOAD);
//				System.out.println("接受的游戏下载资源名称：" + bean.getResourceName());
				download_game(bean);
			}
		}
	};

	
	@Override
	public boolean onUnbind(Intent intent) {
		// unregisterReceiver(mReceiver);
		return super.onUnbind(intent);
	}
	
	
	@Override
	public IBinder onBind(Intent intent) {

		return mBinder;
	}
	
	
	private void download(final DownloadBean download) {
		if (null != mHashMap.get(download.getKey())) {
			mHashMap.remove(download.getKey());
			return;
		}
		mHashMap.put(download.getKey(), download);
		final String key = download.getKey();
		final String resourceName = download.getFileName()
				.replaceAll(".zip", "");
		System.out.println("开始下载资源：" +resourceName);
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				try {
					// 获取信号量
					semaphore.acquire();
					Intent intent = new Intent(ACTION_START);
					intent.putExtra(KEY, key);
					sendBroadcast(intent);
					download.setStatus(ACTION_START);
					// 创建文件目录
					File file = new File(Constant.DOWNLOAD_PATH);
					if (!file.exists())
						file.mkdirs();
					final String savePath = Constant.DOWNLOAD_PATH + "/"
							+ download.getResourceNumber() + "_"
							+ download.getFileName();
					HttpParams params = new BasicHttpParams();
					HttpConnectionParams.setConnectionTimeout(params, 20000);
					HttpConnectionParams.setSoTimeout(params, 8000);
					HttpClient httpClient = new DefaultHttpClient(params);
					String url = download.getResourceUrl();
					System.out.println("组装之前："+url);
					url = URLDecoder.decode(url,"utf-8");
					String[] url_units = url.split("/");
					int len_url = url_units.length;
					url = ""; 
					int count_http = 0;
					for(int i=0;i<len_url;i++){
						if(url_units[i].equals("http:")){
							System.out.println("node.equalshttp");
							count_http ++;
							if(count_http ==2){
								url = ""; 
							}
						}
						if(i != len_url - 1){
							url += url_units[i] + "/";
						}else{
							url += url_units[i];
						}
					}
					System.out.println("组装之后："+url);
					HttpGet get = new HttpGet(url);
					HttpResponse response = httpClient.execute(get);
					if (response.getStatusLine().getStatusCode() != 200) {
						throw new IOException("错误代码："
								+ response.getStatusLine().getStatusCode());
					}
					// 先删除原有文件，然后创建新文件
					file = new File(savePath);
					if (file.exists())
						file.delete();
					file.createNewFile();
					long current = 0;
					long total = (long) response.getEntity().getContentLength();
					InputStream inputStream = response.getEntity().getContent();
					FileOutputStream outputStream = new FileOutputStream(file);
					byte[] buffer = new byte[10240];
					int ch;
					int j = 0;
//					Log.e(TAG, "在DownloadService中，开始下载。。");
					while ((ch = inputStream.read(buffer)) != -1) {
						outputStream.write(buffer, 0, ch);
						current += ch;
						int pos = (int) ((current * 1.0 / total) * 100);
						if (j == 50) {
							intent = new Intent(ACTION_PROGRESS);
							intent.putExtra(ACTION_PROGRESS, pos);
							intent.putExtra(KEY, key);
							sendBroadcast(intent);
							download.setProgress(pos);
							j = 0;
						}
						j++;
					}
					inputStream.close();
					outputStream.close();
					intent = new Intent(ACTION_PROGRESS);
					intent.putExtra(ACTION_PROGRESS, 100);
					intent.putExtra(KEY, key);
					sendBroadcast(intent);
					download.setProgress(100);
					// 发送广播开始解压
					intent = new Intent(ACTION_UNZIP);
					intent.putExtra(KEY, key);
					sendBroadcast(intent);
					download.setStatus(ACTION_UNZIP);
					// 此处解压下载文件
					String unzipPath = Constant.RESOURCE_PATH + "/"
							+ download.getCourse() + "/" + download.getGrade();
					file = new File(unzipPath);
					if (file.exists())
						file.delete();
					file.mkdirs();
					Util.readByApacheZipFile(savePath, unzipPath);
					intent = new Intent(ACTION_COMPLETE);
					intent.putExtra(KEY, key);
					sendBroadcast(intent);
					download.setStatus(ACTION_COMPLETE);
					try {	
						// 解压成功之后，把数据对象保存到db文件中	
						Down down = new Down(download.getCourse(),	
								download.getGrade(), download.getIspackage(),	
								resourceName, download.getResourceNumber());	
						MainActivity.getDatabase().addResource(down); 
						// 数据加载列表中 
						if (down.getIsPackage() == 1) 
							return; 
						HashMap<String, ArrayList<String>> map = SelectTeach.mHashMap
								.get(down.getCourse());
						if (map == null) {
							map = new HashMap<String, ArrayList<String>>();
							SelectTeach.mHashMap.put(down.getCourse(), map);
						}
						ArrayList<String> list = map.get(down.getGrade());
						if (list == null) {
							list = new ArrayList<String>(4);
							map.put(down.getGrade(), list);
						}
						int i = 0, len = list.size();
						for (i = 0; i <= len; i++) {
							if (i == len) {
								list.add(resourceName);
								break;
							}
							if (resourceName.equals(list.get(i)))
								break;
						}
						// 将资源文件加密 
						com.utils.Util.encodeResource(context,resourceName,unzipPath);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} catch (Exception e) {
					Intent intent = new Intent(ACTION_ERROR);
					intent.putExtra(KEY, key);
					//将本个下载任务移除哈希表
					mHashMap.remove(key);
					sendBroadcast(intent);
					e.printStackTrace();
				} finally {
					semaphore.release();
				}
			}
		};
		mExecutorService.execute(runnable);
	}


	private void download_game(final DownloadBean download) {
		if (null != mHashMap_Game.get(download.getKey())) {
			//将本个下载任务移除哈希表
			mHashMap_Game.remove(download.getKey());
			return;
		}
		mHashMap_Game.put(download.getKey(), download);
		final String key = download.getKey();
		//TODO 此处准备下载的URL
		final RequestParameter parameter = new RequestParameter();
		KingPadApp app = (KingPadApp) getApplication();
		parameter.add("productNumber", KingPadStudyActivity.getAndroidId());
		parameter.add("id", download.getResrouceId());
		LoadData.loadData(Constant.GAME_LOG_DATA, parameter, null);
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				try {
					// 获取信号量
					semaphore.acquire();
					// 创建文件目录
					File file = new File(Constant.PATH_DOWNLOAD_GAME);
					if (!file.exists())
						file.mkdirs();
					final String savePath = Constant.PATH_DOWNLOAD_GAME + "/"
							+ download.getResourceNumber() + "_"
							+ download.getFileName();
					download.setSavePath(savePath);
//					System.out.println("download_game开始下载资源：" + download.getResrouceId());
					Intent intent = new Intent(ACTION_GAME_START);
					intent.putExtra(KEY, key);
					intent.putExtra(Constant.BROAD_CAST_SAVE_PATH, savePath);
					sendBroadcast(intent);
					download.setStatus(ACTION_GAME_START);
					HttpParams params = new BasicHttpParams();
					HttpConnectionParams.setConnectionTimeout(params, 20000);
					HttpConnectionParams.setSoTimeout(params, 8000);
					HttpClient httpClient = new DefaultHttpClient(params);
					//将下载链接地址转码，以防有中文 
					String url_init = download.getResourceUrl();
					String [] url_units = url_init.split("/");
					String url = "";
					int len_units = url_units.length;
					for(int i=0;i<len_units;i++){
						if(i == len_units -1){
							//最后一个，转码 
							url += URLEncoder.encode(url_units[i],"utf-8");
						}else{
							url += url_units[i]+"/";
						}
					}
					System.out.println("转码后的url="+url);
					HttpGet get = new HttpGet(url); 
//					System.out.println(download.getResourceUrl());
					HttpResponse response = httpClient.execute(get);
					if (response.getStatusLine().getStatusCode() != 200) {
						throw new IOException("错误代码："
								+ response.getStatusLine().getStatusCode());
					}
					// 先删除原有文件，然后创建新文件
					file = new File(savePath);
					if (file.exists())
						file.delete();
					file.createNewFile();
					long current = 0;
					long total = (long) response.getEntity().getContentLength();
					InputStream inputStream = response.getEntity().getContent();
					FileOutputStream outputStream = new FileOutputStream(file);
					byte[] buffer = new byte[10240];
					int ch;
					int j = 0;
//					System.out.println("在DownloadService的游戏模块中，开始下载。。");
					while ((ch = inputStream.read(buffer)) != -1) {
						outputStream.write(buffer, 0, ch);
						current += ch;
						int pos = (int) ((current * 1.0 / total) * 100);
						if (j == 50) {
							intent = new Intent(ACTION_GAME_PROGRESS);
							intent.putExtra(ACTION_GAME_PROGRESS, pos);
							intent.putExtra(KEY, key);
							sendBroadcast(intent);
							download.setProgress(pos);
							j = 0;
						}
						j++;
					}
//					System.out.println("在Download 的Game中，下载完毕。。");
					inputStream.close();
					outputStream.close();
					intent = new Intent(ACTION_GAME_PROGRESS);
					intent.putExtra(ACTION_GAME_PROGRESS, 100);
					intent.putExtra(KEY, key);
					sendBroadcast(intent);
//					System.out.println("在DownloadService中Game，发送100%消息。。");
					download.setProgress(100);
					// 将APK下载完毕状态和APK保存路径写入文件
					FileHandler.setStringToFile(context,
							download.getResourceUrl(), "0");
					// 发送下载成功广播
					intent = new Intent(ACTION_GAME_COMPLETE);
					intent.putExtra(KEY, key);
					intent.putExtra(ACTION_GAME_COMPLETE, savePath);
					sendBroadcast(intent);
//					System.out
//							.println("在DownloadService中Game，发送ACTION_COMPLETE消息。。");
					download.setStatus(ACTION_GAME_COMPLETE);
				} catch (Exception e) {
					Intent intent = new Intent(ACTION_GAME_ERROR);
					intent.putExtra(KEY, key);
					sendBroadcast(intent);
					e.printStackTrace();
				} finally {
					semaphore.release();
				}
			}
		};
		mExecutorService.execute(runnable);
	}

	/**
	 * 判断下载是否空闲
	 * 
	 * @return ： 又资源正在下载返回false，否则true
	 */
	public static boolean canClose() {
		return semaphore.availablePermits() == number_download_max ? true
				: false;
	}

	public void onDestroy() {
		super.onDestroy();
//		Log.e(TAG, "退出调用");
		mExecutorService.shutdown();
		// 该数据请求之后，第二次在打开时就不能在显示当前那些数据已经下载过，
		mHashMap.clear();
		mHashMap_Game.clear();
		unregisterReceiver(mReceiver);
	};
}
