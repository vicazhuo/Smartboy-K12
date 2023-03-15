package com.kingPadStudy.adapter;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.tools.ant.types.CommandlineJava.SysProperties;

import android.R.integer;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bean.GameBean;
import com.constant.Constant;
import com.kingPadStudy.service.DownloadBean;
import com.kingPadStudy.service.DownloadService;
import com.kingPadStudy.tools.FileHandler;
import com.kingpad.R;
import com.utils.AsyncImageLoader;
import com.utils.AsyncImageLoader.ImageCallback;
 
public class GameListAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private Context context;
	private ArrayList<GameBean> datas = new ArrayList<GameBean>();
	private AsyncImageLoader asyncImageLoader;
	private ListView listView;
	private static String TAG = "GameListAdapter";
	private HashMap<Integer, ViewCache> map_cache = new HashMap<Integer, ViewCache>();
	/**
	 * Activity对象，用于下载线程
	 */
	private Activity activity;
	/**
	 * ListView显示的最小个数
	 */
	private int number_show = 4;

	private int number_cache = -1;
	/**
	 * ListView上一个实例化的item的位置标号
	 */
	private int position_last;
	/**
	 * 当前ListView的位置队列 所有的位置都是最前方的，被显示出来的
	 */
	private int length_queue = 0;
	private int[] queue_position = new int[4];
	/**
	 * 当前ListView的视图缓存编号队列 和queue_position一一对应
	 */
	private int[] queue_cachenumber = new int[4];
	private HashMap<String, String> map_download_file;

	public GameListAdapter(Context context, ListView listView2) {
		this.context = context;
		activity = (Activity) context;
		this.listView = listView2;
		inflater = LayoutInflater.from(context);
		asyncImageLoader = new AsyncImageLoader();
		map_download_file = new HashMap<String, String>();
		// 广播过滤的初始化
		IntentFilter filter = new IntentFilter();
		filter.addAction(DownloadService.ACTION_GAME_ERROR);
		filter.addAction(DownloadService.ACTION_GAME_START);
		filter.addAction(DownloadService.ACTION_GAME_COMPLETE);
		filter.addAction(DownloadService.ACTION_GAME_PROGRESS);
		filter.addAction(DownloadService.ACTION_GAME_GET_ALL);
		filter.addAction(DownloadService.ACTION_GAME_ADD_DOWNLOAD);
		filter.addAction(DownloadService.ACTION_GAME_RETURN_ALL);
//		System.out.println("GameListAdapter注册一个receiver...");
		activity.registerReceiver(mReceiver, filter); // 注册广播接收器
		// 请求下载的哈希表
		handler_download_all_request.sendEmptyMessageDelayed(0, 500);
	}

	/**
	 * 向本地服务发送 所有下载数据请求
	 */
	private Handler handler_download_all_request = new Handler() {
		public void handleMessage(android.os.Message msg) {
			Intent intent = new Intent(DownloadService.ACTION_GAME_GET_ALL);
			activity.sendBroadcast(intent);
		};
	};

	
	/**
	 * 判断一个字符串是否是数字
	 * @param str
	 * @return
	 */
	public static boolean isNum(String str){
		if(str == null){
			return false;
		}
		return str.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$");
	}
	
	/**
	 * 初始化广播接收器
	 */
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
//			System.out.println("游戏下载的mReceiver接收到消息action="+action);
			if (action.equals(DownloadService.ACTION_GAME_RETURN_ALL)) {
//				System.out.println("获取全部下载的哈希表");
				// 获取当前下载的状态
				HashMap<String, DownloadBean> hashMap = (HashMap<String, DownloadBean>) intent
						.getSerializableExtra(DownloadService.ACTION_GAME_RETURN_ALL);
				initDownload(hashMap); 
				return ;
			}
			// 获取到下载的唯一标识
			String key = intent.getStringExtra(DownloadService.KEY);
//			System.out.println("接受到的key="+key);
			final int position ;
			if(isNum(key)){
				position = Integer.parseInt(key);
			}else{
//				System.out.println("不是数字，直接返回...");
				return ;
			}
//			System.out.println("position="+position);
			System.out.println("cache position="+findCacheNumber(position));
			ViewCache viewHolder = map_cache.get(findCacheNumber(position)); // 下载的item标号作为每个ViewCache的key
//			System.out.println("获取viewholder, key=" + key);
//			System.out.println("viewHolder=" + viewHolder);
			ProgressBar bar = null;
			TextView textView = null;
			Button button = null;
			if (viewHolder != null) {
				bar = viewHolder.bar_download;
				textView = viewHolder.text_progress;
				button = viewHolder.button_download;
			} else {
//				System.out.println("没有找到对应的view直接，返回");
				// 消息不是本个Adapter的，就直接返回
				return;
			}
			if (action.equals(DownloadService.ACTION_GAME_ERROR)) {
				Toast.makeText(activity, "下载出现错误", 0).show();
//				Log.e(TAG, key + ",下载出现错误");
			} else if (action.equals(DownloadService.ACTION_GAME_PASUE)) {
				Toast.makeText(activity, "正在等待下载", 0).show();
//				Log.e(TAG, key + ",正在等待下载");
			} else if (action.equals(DownloadService.ACTION_GAME_START)) {
				Toast.makeText(activity, "开始下载", 0).show();
				datas.get(position).isDownLoading = true;
				String save_path = intent
						.getStringExtra(Constant.BROAD_CAST_SAVE_PATH);
				datas.get(position).save_path = save_path;
				FileHandler.setStringToFile(context, "game_download_path"+datas.get(position).url, datas.get(position).save_path);
//				  Log.e("GAME","开始下载 "+position+"，download 的 savePath 设置为："+datas.get(position).save_path);
//				System.out.println("datas.get("+position+").save_path="+datas.get(position).save_path);
//				Log.e(TAG, key + "， 正在下载");
			} else if (action.equals(DownloadService.ACTION_GAME_COMPLETE)) {
				Toast.makeText(activity, "所有操作均已完成", 0).show();
				datas.get(position).isDownLoading = false;
				textView.setVisibility(View.GONE);
				bar.setVisibility(View.GONE);
				button.setBackgroundResource(R.drawable.background_button_shape_yello);
				button.setText("安装");
			    //下载完毕后自动安装
//				System.out.println("activity="+activity);
			    if(activity != null){
				  //If is APK file, the request to install.
			      String save_path = datas.get(position).save_path;
//				  System.out.println("activity="+activity);
				  if (save_path.endsWith(".apk")) {
					Intent intent_install = new Intent();
					intent_install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent_install.setAction(android.content.Intent.ACTION_VIEW);
					intent_install.setDataAndType(Uri.fromFile(new File(save_path)),
				  			"application/vnd.android.package-archive");
				    activity.startActivity(intent_install);
				  }
			    }
			    //下载完毕，处于未安装状态
//			    APKState = 2;  //开始下载
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
//							System.out.println("监听安装activity是否退出...");
							if (isAppOnForeground()) {
								// 系统中不存在这个activity
//								System.out.println("安装程序已经退出了。。。发送更新消息...");
								// 向GameListAdapter 发送一条按钮更新消息
								Message msg = new Message();
								msg.what = Constant.MESSAGE_BUTTON_CHANGE;
								msg.arg1 = position;
								handler.sendMessage(msg);
								break;
							}
						}
					}
				}).start();
			} else if (action.equals(DownloadService.ACTION_GAME_PROGRESS)) {
				int progress = intent.getIntExtra(
						DownloadService.ACTION_GAME_PROGRESS, 0);
//				System.out.println("progress = "+progress);
				textView.setText(progress + "%");
				datas.get(position).progress = progress;
				bar.setVisibility(View.VISIBLE);
				bar.setProgress(progress);
			} 
		}
	};

	// TODO
	/**
	 * 根据当前资源是否正在下载 更新下载的进度条
	 * 
	 * @param hashMap
	 */
	private void initDownload(HashMap<String, DownloadBean> hashMap) {
//		Log.e(TAG, "获取到service数据");
//		System.out.println("hashmap大小：" + hashMap.size());
		Iterator iter = hashMap.entrySet().iterator();
		while (iter.hasNext()) {
			java.util.Map.Entry entry = (java.util.Map.Entry) iter.next();
			DownloadBean bean = (DownloadBean) entry.getValue();// 获得value，都要强制转换一下
			String status = bean.getStatus();
			int position = Integer
					.parseInt(entry.getKey().toString());
//			System.out.println("初始化：pos="+position);
			ViewCache viewHolder = map_cache.get(findCacheNumber(position)); // 下载的item标号作为每个ViewCache的key
			ProgressBar bar = null;
			TextView textView = null;
			if (status.equals(DownloadService.ACTION_GAME_PASUE)) {
//				System.out.println("ACTION_GAME_PASUE");
				if (viewHolder == null) {
					continue;
				}
				bar = viewHolder.getBar_download();
				textView = viewHolder.getTextProgress();
				textView.setText("正在等待下载");
				textView.setVisibility(View.VISIBLE);
				bar.setVisibility(View.INVISIBLE);
			} else if (status.equals(DownloadService.ACTION_GAME_START)) {
				if(position >= datas.size()){
					continue;
				}
//				System.out.println("ACTION_GAME_START");
				datas.get(position).isDownLoading = true;
				String file_save = FileHandler.readStringFromFile(
						context,"game_download_path"+ datas.get(position).url);
				datas.get(position).save_path = file_save;
				datas.get(position).progress = bean.getProgress();
//				System.out.println("初始化进度为："+datas.get(position).progress);
//				 Log.e("GAME","回来继续下载 "+position+"，download 的 savePath 设置为："+datas.get(position).save_path);
				if (viewHolder == null) {
					continue;
				}
				bar = viewHolder.getBar_download();
				textView = viewHolder.getTextProgress();
				textView.setText(bean.getProgress() + "%");
				textView.setVisibility(View.VISIBLE);
				bar.setVisibility(View.VISIBLE);
				bar.setVisibility(bean.getProgress());
			} else if (status.equals(DownloadService.ACTION_GAME_COMPLETE)) {
				if(position >= datas.size()){
					continue;
				}
				datas.get(position).save_path = bean.getSavePath();
				if (viewHolder == null) {
					continue;
				}
				bar = viewHolder.getBar_download();
				textView = viewHolder.getTextProgress();
				textView.setVisibility(View.INVISIBLE);
				bar.setVisibility(View.INVISIBLE);
			}
		}
	}

	public void clearData() {
		this.datas.clear();
		notifyDataSetChanged();
	}

	public void addData(GameBean data) {
		this.datas.add(data);
		notifyDataSetChanged();
	}

	public void addDatas(ArrayList<GameBean> datas) {
		this.datas = datas;
		notifyDataSetChanged();
	}

	public int getCount() {
		return datas.size();
	}

	public Object getItem(int arg0) {
		return datas.get(arg0);
	}

	public long getItemId(int arg0) {
		return arg0;
	}

	public void add(int[] queue, int number, boolean isFront) {
		if (!isFront) {
			// 从前插入
			for (int i = length_queue > 3 ? 3 : length_queue; i > 0; i--) {
				queue[i] = queue[i - 1];
			}
			queue[0] = number;
		} else {
			// 从后插入
			if (length_queue < 4) {
				queue[length_queue] = number;
			} else {
				for (int i = 0; i < 3; i++) {
					queue[i] = queue[i + 1];
				}
				queue[3] = number;
			}
		}
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
//		System.out.println("调用getview,pos=" + position);
		int distance_position = Math.abs(position - position_last);
		if (distance_position < number_show && distance_position > 1) { // 如果当前getView和上一个getView之间的位差大于1，并且小于最小显示数量
			// 非法getView，直接返回
//			System.out.println("  %%%%非法位置%%%% ");
			return convertView;
		}
		// 获取数据
		final GameBean data = datas.get(position);
		final ViewCache viewCache;
		if (convertView == null) {
			// 更新缓存对象的编号
			number_cache++;
			/**** 获取item视图和缓存对象 *****/
			convertView = inflater.inflate(R.layout.item_game, null);
			viewCache = new ViewCache(convertView);
			// 加入哈希表
			if (!map_cache.containsValue(viewCache)) {
				map_cache.put(number_cache, viewCache);
			}
			viewCache.number = number_cache;
			convertView.setTag(viewCache);
			// 更新当前ListView显示的item位置的队列
			add(queue_position, position, true);
			// 对出队列的item上的控件和对应的线程做处理
			if (length_queue == 4) {
				// 让被挤出的 ProgressBar不可见
				int number_cache_out = queue_cachenumber[0]; // 获取被挤出的item的缓存对象标号
				alterDownLoadView(number_cache_out, View.INVISIBLE, 0);
			}
			// 更新ListView中当前显示的换对象编号队列
			add(queue_cachenumber, viewCache.number, true);
			// 更新队列长度
			length_queue = length_queue > 3 ? 4 : length_queue + 1;
		} else {
			viewCache = (ViewCache) convertView.getTag();
			// 对出队列的item进行处理
			if (position > position_last) {// 向下滑动
				// 让被挤出的 ProgressBar不可见
				int number_cache_out = queue_cachenumber[0]; // 获取被挤出的item的缓存对象标号
				alterDownLoadView(number_cache_out, View.INVISIBLE, 0);
				// 更新队列
				add(queue_position, position, true);
				add(queue_cachenumber, viewCache.number, true);
			} else {// 向上滑动
					// 让被挤出的 ProgressBar不可见
				int number_cache_out = queue_cachenumber[length_queue - 1]; // 获取被挤出的item的缓存对象标号
				alterDownLoadView(number_cache_out, View.INVISIBLE, 0);
				// 更新队列
				add(queue_position, position, false);
				add(queue_cachenumber, viewCache.number, false);
			}
		}
		/*
		 * 对新出现的item进行处理
		 */
		// 填写标题
		TextView text_title = viewCache.getText_title();
		text_title.setText(data.title);
		// 填写简介
		TextView text_profile = viewCache.getText_profile();
		text_profile.setText("简介："+data.profile);
		// 更新下载有关视图
		final ProgressBar bar = viewCache.getBar_download();
		final TextView text_downloadpercent = viewCache.getTextProgress();
		if (data.isDownLoading) {// 如果该item有下载任务
			// 恢复进度条显示
			alterDownLoadView(findCacheNumber(position), View.VISIBLE, position);
		}
		// 按钮点击事件
		final int apkState;
		final Button btn_download = viewCache.getButton_download();
		String state_file = FileHandler.readStringFromFile(context, data.url);
//		System.out.println("url=" + data.url + "的文件状态：" + state_file);
		if (state_file == null) {
			if (data.isDownLoading == false) {
				apkState = 0;
			} else {
				apkState = 1;
			}
		} else {
			if (state_file.equals("1")) {
				// 已安装
				apkState = 3;
			} else {
				// 未安装
				apkState = 2;
			}
		}
//		System.out.println("apkState=" + apkState);
		if (apkState == 0) {
			// 处于未下载状态
			btn_download
					.setBackgroundResource(R.drawable.background_button_shape);
			data.canClick = true;
			btn_download.setText("下载");
		} else if (apkState == 1) {
			// 处于正在下载状态
			btn_download
					.setBackgroundResource(R.drawable.background_button_shape);
			data.canClick = false;
			btn_download.setText("下载");
		} else if (apkState == 2) {
			// 处于未安装状态
			btn_download
					.setBackgroundResource(R.drawable.background_button_shape_yello);
			data.canClick = true;
			btn_download.setText("安装");
		} else {
			// 处于已安装状态
			data.canClick = false;
			btn_download
					.setBackgroundResource(R.drawable.background_button_shape_gray);
			btn_download.setText("已安装");
		}

		btn_download.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (data.canClick == false) {
//					System.out.println("不可点击...");
					return;
				}
				String state_file = FileHandler.readStringFromFile(context,
						data.url);
//				System.out.println("点击按钮，url=" + data.url + ",文件状态："
//						+ state_file);
				if (state_file != null && !state_file.equals("1")) {
					// 如果处于未安装状态，安装APK
					File file_save;
					if (data.isDownLoading == true) {
						file_save = new File(data.save_path);
					} else {
						file_save = new File(FileHandler.readStringFromFile(
								context,"game_download_path"+ data.url));
					}
//					System.out.println("准备安装APK，路径：" + file_save.getPath());
					if (file_save.getPath().endsWith(".apk")) {
						Intent intent_install = new Intent();
						intent_install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent_install
								.setAction(android.content.Intent.ACTION_VIEW);
						intent_install.setDataAndType(Uri.fromFile(file_save),
								"application/vnd.android.package-archive");
						activity.startActivity(intent_install);
					}
					// 开启监听
					new Thread(new Runnable() {
						@Override
						public void run() {
							while (true) {
								try {
									Thread.sleep(1000);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
//								System.out.println("监听安装activity是否退出...");
								if (isAppOnForeground()) {
									// 系统中不存在这个activity
//									System.out.println("安装程序已经退出了。。。发送更新消息...");
									// 向GameListAdapter 发送一条按钮更新消息
									Message msg = new Message();
									msg.what = Constant.MESSAGE_BUTTON_CHANGE;
									msg.arg1 = position;
									handler.sendMessage(msg);
									break;
								}
							}
						}
					}).start();
				} else {
					viewCache.setDownloading(true);
					// 下载APK
					bar.setVisibility(View.VISIBLE); // 进度条可见
					bar.setProgress(0);
					text_downloadpercent.setVisibility(View.VISIBLE); // 进度条同步文字可见
					text_downloadpercent.setText("0%");
					DownloadBean download = new DownloadBean();
					// 下载的key
					download.setKey(position + "");
					download.setResourceName(data.title);
					download.setResrouceId(data.id);
					//TODO
//					System.out.println("点击时，设置资源ID:"+download.getResrouceId());
					download.setResourceUrl(data.url);
					download.setResourceNumber(position + "");
					download.setFileName(data.title + ".apk");
					// 发送一条广播，用于添加一条下载记录信息
//					System.out.println("发送下载信息：" + download.getResourceName());
					Intent intent = new Intent(
							DownloadService.ACTION_GAME_ADD_DOWNLOAD);
					intent.putExtra(DownloadService.ACTION_GAME_ADD_DOWNLOAD,
							download);
					activity.sendBroadcast(intent);
					// 下载按钮不可点击
					datas.get(position).canClick = false;
				}
			}
		});
		// 异步加载图片
		ImageView imageView = viewCache.getImage();
		imageView.setTag(data.image);
		Drawable cachedImage = asyncImageLoader.loadDrawable(data.image,
				new ImageCallback() {
					public void imageLoaded(Drawable imageDrawable,
							String imageUrl) {
//						System.out.println("listView="+listView);
						ImageView imageViewByTag = (ImageView) listView
								.findViewWithTag(imageUrl);
						if (imageViewByTag != null) {
							imageViewByTag.setImageDrawable(imageDrawable);
							listView.postInvalidate();
						}
					}
				});
		if (cachedImage == null) {
			imageView.setImageResource(R.drawable.default_image);
		} else {
			imageView.setImageDrawable(cachedImage);
		}
		position_last = position;
		return convertView;
	}

	private boolean isAppOnForeground() {
		ActivityManager activityManager = (ActivityManager) activity
				.getApplicationContext().getSystemService(
						Context.ACTIVITY_SERVICE);
		String packageName = activity.getApplicationContext().getPackageName();
		List<RunningAppProcessInfo> appProcesses = activityManager
				.getRunningAppProcesses();
		if (appProcesses == null)
			return false;
		for (RunningAppProcessInfo appProcess : appProcesses) {
			if (appProcess.processName.equals(packageName)
					&& appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param number_cache_out
	 * @param invisible
	 */
	private void alterDownLoadView(int number_cache_out, int visibility,
			int position) {
		ViewCache cache_temp = map_cache.get(number_cache_out);
		ProgressBar bar = cache_temp.getBar_download();
		TextView text = cache_temp.getTextProgress();
		bar.setVisibility(visibility);
		text.setVisibility(visibility);
		if (visibility == View.VISIBLE) {
			int progress = datas.get(position).progress;
			bar.setProgress(progress);
			text.setText(progress + "%");
		}
		bar.postInvalidate();
		text.postInvalidate();
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == Constant.MESSAGE_BUTTON_CHANGE) {
				// 如果想要改变按钮的状态
				int position = msg.arg1; // 获取线程的编号
				String archiveFilePath;
				if (datas.get(position).save_path != null) {
					archiveFilePath = datas.get(position).save_path;// 安装包路径
				} else {
					archiveFilePath = FileHandler.readStringFromFile(context,
							datas.get(position).url);
				}
//				System.out.println("handler更新按钮。。。position=" + position
//						+ ",获取APK的本地路径=" + archiveFilePath);
				PackageManager pm = activity.getPackageManager();
				PackageInfo info = pm.getPackageArchiveInfo(archiveFilePath,
						PackageManager.GET_ACTIVITIES);
				if (info == null) {
					// 没有安装
//					System.out.println("不存在这个包");
				} else {
//					System.out.println("存在包：" + info.packageName);
					try {
						if ((activity.getPackageManager().getPackageInfo(
								info.packageName, 0)) != null) {
//							System.out.println("已经安装,info.packageName="
//									+ info.packageName + "info.versionName="
//									+ info.versionName);
							// 写入文件这个下载文件的记录
							FileHandler.setStringToFile(context,
									datas.get(position).url, "1"); 
							ViewCache viewCache = map_cache  
									.get(findCacheNumber(position));
							if (viewCache != null) {
								Button button = viewCache.getButton_download();
								// 处于已安装状态
								datas.get(position).canClick = false;
								button.setBackgroundResource(R.drawable.background_button_shape_gray);
								button.setText("已安装");
								button.postInvalidate();
							}
						} else {
							datas.get(position).canClick = true;
//							System.out.println("没有安装,info.packageName="
//									+ info.packageName + "info.versionName="
//									+ info.versionName);
						}
					} catch (NameNotFoundException e) {
						datas.get(position).canClick = true;
//						System.out.println("没有安装");
					}
				}
				return;
			}
			// 改变进度条的进度
			int progress = msg.arg1;
			int position = msg.arg2;
			int pos_cache = findCacheNumber(position);
//			System.out.println("handler   progress=" + progress + ",position="
//					+ position + ",pos_cache=" + pos_cache);
			if (pos_cache == -1) {
				return;
			} else {
				ViewCache viewCache = map_cache.get(pos_cache);
				ProgressBar bar = viewCache.getBar_download();
				bar.setVisibility(View.VISIBLE);
				bar.setProgress(progress);
				TextView text = viewCache.getTextProgress();
				text.setVisibility(View.VISIBLE);
				text.setText(progress + "%");
				bar.postInvalidate();
				if (progress == 100) {
					text.setVisibility(View.INVISIBLE);
					bar.setVisibility(View.INVISIBLE);
					// 按钮变成安装按钮。
					Button button = (Button) viewCache.getButton_download();
					button.setBackgroundResource(R.drawable.background_button_shape_yello);
					button.setText("安装");
					// 恢复可以点击
					datas.get(position).canClick = true;
					Toast.makeText(context,
							datas.get(position).title + "下载完毕！", 0).show();
				}
			}
		}
	};

//	/**
//	 * 打印队列
//	 */
//	private void printQueue() {
////		System.out.println("postion队列:");
//		for (int i = 0; i < length_queue; i++) {
//			System.out.println("position[" + i + "]=" + queue_position[i]);
//		}
//		System.out.println("cache队列:");
//		for (int i = 0; i < length_queue; i++) {
//			System.out.println("cache[" + i + "]=" + queue_cachenumber[i]);
//		}
//	}

	/**
	 * 根据ListView的position找到ViewCache的编号
	 * 
	 * @param position
	 * @return
	 */
	protected int findCacheNumber(int position) {
		for (int i = 0; i < length_queue; i++) {
			if (queue_position[i] == position) {
				return queue_cachenumber[i];
			}
		}
		return -1;
	}

	/**
	 * 获取唯一值
	 * 
	 * @return
	 */
	private int getUniqueNumber() {
		Calendar ca = Calendar.getInstance();
		int year = ca.get(Calendar.YEAR);// 获取年份
		int month = ca.get(Calendar.MONTH);// 获取月份
		int day = ca.get(Calendar.DATE);// 获取日
		int minute = ca.get(Calendar.MINUTE);// 分
		int hour = ca.get(Calendar.HOUR);// 小时
		int second = ca.get(Calendar.SECOND);// 秒
		return year * 100000 + month * 10000 + day * 1000 + minute * 100 + hour
				* 10 + second;
	}

	/**
	 * 根据网络URL获取BitMap
	 * 
	 * @param image
	 * @return
	 */
	private Bitmap getHttpBitmap(String image) {
		URL url_file = null;
		Bitmap bmp_return = null;
		try {
			url_file = new URL(image);
			HttpURLConnection conn = (HttpURLConnection) url_file
					.openConnection();
			conn.setDoInput(true);
			conn.setConnectTimeout(100000);
			conn.setUseCaches(false);
			InputStream is = conn.getInputStream();
			bmp_return = BitmapFactory.decodeStream(is);
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bmp_return;
	}

}
