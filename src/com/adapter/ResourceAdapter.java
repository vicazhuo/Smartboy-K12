package com.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bean.BResource;
import com.bean.Down;
import com.constant.Constant;
import com.data.DownloadData;
import com.king.MainActivity;
import com.king.SelectTeach;
import com.kingPadStudy.activity.KingPadStudyActivity;
import com.kingPadStudy.service.DownloadBean;
import com.kingPadStudy.service.DownloadService;
import com.kingpad.KingPadApp;
import com.kingpad.R;
import com.net.LoadData;
import com.net.RequestListener;
import com.net.RequestParameter;
import com.utils.Util;
//import android.R.integer;


/**
 * 每一种资源
 * @author <a href="mailto:udmjh0508@126.com">莫建华</a>
 * @created 2013-1-14
 * @version V-1.0
 */
@TargetApi(9)
public class ResourceAdapter extends BaseAdapter {
	private static String TAG = "ResourceAdapter";
	/** 
	 * 用于保存资源是否下载  
	 */ 
	private static HashMap<String, BResource> map = new HashMap<String, BResource>();
	private static HashSet<String> mSet = new HashSet<String>();
	private Context mContext;
	private LayoutInflater mInflater;
	private ArrayList<BResource> mList;
	private static DownloadManager mDownloadManager;
	private KingPadStudyActivity activity;
	private HashMap<String, ViewHolder> map_viewHolder;
	private HashMap<String, DownloadBean> map_downloadBean;
	private boolean isInitDownLoad = false;	//是否已经初始化下载界面了
	/**
	 * 该课程跟的目录
	 */
	/**
	 * 课程id号
	 */
	private String courseId;
	/**
	 * 课程名Math,Chinese,English
	 */
	private String course;
	/**
	 * 年级名 , '一年级，二年级'
	 */
	private String grade;
	/*
	 * 正在解压的资源
	 */
	private int number_unzip = 0;
	/** 
	 * 年级id号  
	 */	
	private String gradeId;  
	final static int UNZIP_ERROR = 1;  
	final static int UNZIP_SUCCESS = 2;  
	final static int DOWNLOAD_STATING = 3;   
	final static Lock lock = new ReentrantLock();     
	private LinearLayout linear_1; 
	private int id_download = 0;
	private String url_download ;
	private int id_get = 1;
	private int state_download = 0; 
	private boolean  isSecondDownload = false; 
	
	public ResourceAdapter(final Context context, ArrayList<BResource> list,   
			String course, String courseId, String grade, String gradeId) {  
		System.out.println("ResourceAdapter...构造。。。");
		map_viewHolder = new HashMap<String, ResourceAdapter.ViewHolder>();
		this.mContext = context;
		activity = (KingPadStudyActivity)context;
		mInflater = LayoutInflater.from(mContext);
		if(list == null){
			mList = new ArrayList<BResource>();
		}else{
			mList = list;
		}
		mDownloadManager = (DownloadManager) mContext.getSystemService(Activity.DOWNLOAD_SERVICE);
		this.courseId = courseId;
		this.course = course;
		this.grade = grade;
		this.gradeId = gradeId;
        //广播过滤的初始化
        IntentFilter filter = new IntentFilter();
        filter.addAction(DownloadService.ACTION_ERROR);
        filter.addAction(DownloadService.ACTION_START);
        filter.addAction(DownloadService.ACTION_UNZIP);
        filter.addAction(DownloadService.ACTION_COMPLETE);
        filter.addAction(DownloadService.ACTION_PROGRESS);
        filter.addAction(DownloadService.ACTION_RETURN_ALL);
        activity.registerReceiver(mReceiver, filter); // 注册广播接收器
        activity.receiver = mReceiver;
        //请求下载的哈希表 
        handler_download_all_request.sendEmptyMessageDelayed(0, 100);
	}
	
	
    /**
     * 向本地服务发送 所有下载数据请求
     */
    private Handler handler_download_all_request = new Handler() {
    	public void handleMessage(android.os.Message msg) {
    		Intent intent = new Intent(DownloadService.ACTION_GET_ALL);
    		System.out.println("发送请求");
    		activity.sendBroadcast(intent);
    	};
    };
    
    
    /**
	 * 初始化广播接收器
	 */
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
//			System.out.println("onReceive。。action="+action);
			if(action.equals(DownloadService.ACTION_RETURN_ALL)){
				//如果是获取哈希表的返回请求
//				System.out.println("获取全部下载的哈希表");
				//获取当前下载的状态
				map_downloadBean = (HashMap<String, DownloadBean>) intent.getSerializableExtra(DownloadService.ACTION_RETURN_ALL);
				initDownload(map_downloadBean);
				return ;
			}
			//获取到下载的唯一标识
			String key = intent.getStringExtra(DownloadService.KEY);
//			System.out.println("获取到的唯一标识key="+key);
			ViewHolder viewHolder = map_viewHolder.get(key);
			LinearLayout layout = null ;
			ProgressBar bar = null;
			TextView textView = null;
			if(viewHolder != null){
				layout = viewHolder.layout_1;
				bar = viewHolder.progressBar1;
				textView = viewHolder.text_1;
				layout.setVisibility(View.VISIBLE);
			}else{
				//消息不是本个Adapter的，就直接返回
				return ;
			}
			if(action.equals(DownloadService.ACTION_ERROR)) {
				Toast.makeText(activity, "下载出现错误", 0).show();
				//将当前的下载条隐藏
				bar.setVisibility(View.GONE);
				textView.setVisibility(View.GONE);
				viewHolder.download.setClickable(true);
//				Log.e(TAG, key + ",下载出现错误");
			}
			else if(action.equals(DownloadService.ACTION_PASUE)) {
//				Toast.makeText(activity, "正在等待下载", 0).show();
//				Log.e(TAG, key + ",正在等待下载");
			}
			else if(action.equals(DownloadService.ACTION_START)) {
//				Toast.makeText(activity, "正在下载", 0).show();
//				Log.e(TAG, key + "， 正在下载");
			} 
			else if(action.equals(DownloadService.ACTION_UNZIP)) {
				Toast.makeText(activity, "下载完成，正在解压", 0).show();
//				Log.e(TAG, key + ", 下载完成，正在解压");
				textView.setText("正在解压");
				bar.setVisibility(View.GONE);
			}
			else if(action.equals(DownloadService.ACTION_COMPLETE)) {
				Toast.makeText(activity, "所有操作均已完成", 0).show();
				textView.setText("下载解压均完成");
				bar.setVisibility(View.GONE);
				viewHolder.download.setClickable(true);
			}
			else if(action.equals(DownloadService.ACTION_PROGRESS)) {
				int progress = intent.getIntExtra(DownloadService.ACTION_PROGRESS, 0);
				textView.setText("下载进度：");
				bar.setVisibility(View.VISIBLE);
				bar.setProgress(progress);
			}
		}
	};
    
	
	/**
	 * 根据当前资源是否正在下载      更新下载的进度条
	 * @param hashMap
	 */
	private void initDownload(HashMap<String, DownloadBean> hashMap) {
		if(isInitDownLoad){
			return ;
		}else{
			isInitDownLoad = true;
		}
		Log.e(TAG, "initDownload..");
		System.out.println("hashmap大小：" + hashMap.size());
		Iterator iter = hashMap.entrySet().iterator();
        while(iter.hasNext()){
        	java.util.Map.Entry entry = (java.util.Map.Entry)iter.next();
            DownloadBean bean = (DownloadBean) entry.getValue();//获得value，都要强制转换一下
			String status = bean.getStatus();
			System.out.println("key="+entry.getKey().toString()+"——的下载bean");
			ViewHolder viewHolder = map_viewHolder.get(entry.getKey().toString());
			if(viewHolder == null){
				continue;
			}
			System.out.println("下载状态status="+status);
			TextView text_download = viewHolder.text_1;
			ProgressBar bar = viewHolder.progressBar1;
			LinearLayout linearLayout = viewHolder.layout_1;
			linearLayout.setVisibility(View.VISIBLE);
			if(status.equals(DownloadService.ACTION_PASUE)) {
				text_download.setText("正在等待下载");
				bar.setVisibility(View.INVISIBLE);
				text_download.setVisibility(View.VISIBLE);
			}
			else if(status.equals(DownloadService.ACTION_START)) {
				text_download.setText("下载进度：");
				text_download.setVisibility(View.VISIBLE);
				bar.setVisibility(View.VISIBLE);
				bar.setVisibility(bean.getProgress());
			}
			else if(status.equals(DownloadService.ACTION_UNZIP)) {
				text_download.setText("正在解压");
				text_download.setVisibility(View.VISIBLE);
				bar.setVisibility(View.INVISIBLE);
			}
			else if(status.equals(DownloadService.ACTION_COMPLETE)) {
				text_download.setText("下载解压均已完成");
				text_download.setVisibility(View.VISIBLE);
				bar.setVisibility(View.INVISIBLE);
			}
			text_download.postInvalidate();
			bar.postInvalidate();
        }
	}
	
	
	private void handleDownLoad(){
		Toast.makeText(mContext, "文件下载成功, 开始解压文件", 1).show();
		Log.i("tar", "开始解压解压文件");
		KingPadStudyActivity.number_unzip += 1; 
//		System.out.println("number_unzip++，number_unzip="+KingPadStudyActivity.number_unzip);
		BResource resource = null;
//		System.out.println("准备获取资源："+id_get);
//		System.out.println("mList.size()="+mList.size());
		if(state_download == 1){
			//只有资源，没package
			linear_1.setVisibility(View.GONE);
			if(isSecondDownload){
				resource = map.get("p");
			}else{
				resource = map.get("r");
			}
		}else {
			resource = map.get("r");
			linear_1.setVisibility(View.GONE);
			state_download = 1;
			isSecondDownload = true;
		}
		String path = Constant.DOWNLOAD_PATH + "/" + resource.getResourceNumber() + "_" + resource.getFileName();
		unZip(path, resource);
		synchronized (KingPadStudyActivity.sDownloading) {
			//TODO 强制转换
			KingPadStudyActivity.sDownloading -= 1;
			KingPadStudyActivity activity = (KingPadStudyActivity) mContext;
			activity.setProgressText(); 
		}
	}
	
	
	
	public int getCount() {
		return mList.size();
	}
	
	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		//TODO
		System.out.println("getView.pos="+position);
		final ViewHolder viewHolder;
		final BResource resource = mList.get(position);
		viewHolder = new ViewHolder();
		convertView = mInflater.inflate(R.layout.resource_item, null);
		viewHolder.title = (TextView) convertView.findViewById(R.id.textview_file_name);
		viewHolder.price = (TextView) convertView.findViewById(R.id.textview_price);
		viewHolder.space = (TextView) convertView.findViewById(R.id.textview_space);
		viewHolder.download = (Button) convertView.findViewById(R.id.button_download);
		viewHolder.isBuy = (TextView) convertView.findViewById(R.id.textview_is_buy);
		viewHolder.progressBar1 = (ProgressBar)convertView.findViewById(R.id.ProgressBar01);
		viewHolder.layout_1 = (LinearLayout)convertView.findViewById(R.id.linear_progress);
		viewHolder.text_1 = (TextView)convertView.findViewById(R.id.text_download);
		linear_1 = viewHolder.layout_1;
		viewHolder.title.setText("资源名：" + resource.getResourceName());
		if(!map_viewHolder.containsKey(resource.getResourceId())){
			map_viewHolder.put(resource.getResourceId(), viewHolder);
			//TODO
			System.out.println("加入哈希表："+resource.getResourceId());
		}
		viewHolder.price.setText("价格：" + resource.getResourcePrice());
		int space = (int)(resource.getResourceSpace() * 1.0 / (1048576));
		viewHolder.space.setText("资源大小:" + space + "M");
		if(resource.getDownload() == 1) {
			viewHolder.isBuy.setText("已购买");
		} else {
			viewHolder.isBuy.setText("未购买");
		} 
		viewHolder.download.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				//TODO 点击事件
				showDialog(resource,viewHolder.download);
			}
		});
		return convertView;
	}
	
	
	/**
	 * 显示一个确认对话框
	 */
	private void showDialog(final BResource resource,final Button button_click) {
		//TODO 弹出对话
		View view = mInflater.inflate(R.layout.dlg_sure, null);
		TextView textView = (TextView) view.findViewById(R.id.textview_text);
		String text = null;
		if(resource.getDownload() == 1) {
			text = "该资源已下载，下载该资源不扣费";
		}
		else {
			text = "下载"+resource.getResourceName()+"将会被扣除" + resource.getResourcePrice() + "元费用";
		}
		textView.setText(text);
		Dialog dlg = new AlertDialog.Builder(mContext)
		.setTitle("提示")
		.setView(view)
		.setNeutralButton("是", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
//				Log.i("resourceAdapter", "开始下载资源");
				Message msg = new Message();
				msg.what = DOWNLOAD_STATING;
				msg.obj = resource;
				handler.sendMessage(msg);
				button_click.setClickable(false);	//设置本个按钮不可点击 
			}
		})
		.setNegativeButton("否", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) { }
		}).create();
		dlg.show();
	}
	
	
	static class ViewHolder {
		TextView title;
		TextView price;
		TextView space;
		TextView isBuy;
		Button download;
		ProgressBar progressBar1;
		TextView text_1;
		LinearLayout layout_1;
		
	}
	
	/**
	 * 下载资源
	 * @param resource : 资源信息
	 * @param id ： 资源id号
	 * @param url ： 资源下载链接
	 * @param name ： 资源名,该资源必须得为zip文件
	 */
	private void download(final BResource resource,String url) {
        // 创建目录
        File file = new File(Constant.DOWNLOAD_PATH);
        if(!file.exists())
        	file.mkdirs();
        // 以前下载过该文件时删除该文件
        file = new File(Constant.DOWNLOAD_PATH + "/" + resource.getResourceNumber() + "_" + resource.getFileName());
        if(file.exists())
        	file.delete();
		//要下载的文件路径
		String urlDownload = "";
		urlDownload = url;
		//启动文件下载线程
		linear_1.setVisibility(View.VISIBLE);
		resource.setId(id_download);
		map.put("r", resource);
		BResource r = map.get("r");
		synchronized (KingPadStudyActivity.sDownloading) {
			KingPadStudyActivity.sDownloading += 1;
			KingPadStudyActivity activity = (KingPadStudyActivity) mContext;
			activity.setProgressText();
		}
	}
	
	/**
	 * 开启线程加压文件
	 * @param path : zip文件完全路径
	 * @param resourceName : 资源的名 aa.zip
	 */
	private void unZip(final String path, final BResource resource) {
		new Thread(new Runnable() {
			public void run() {
				try {
					String resourcePath;
					resourcePath = Constant.RESOURCE_PATH + "/" +  course + "/" + grade;
					File file = new File(resourcePath);
					if(file.exists())
						file.delete();
//					Log.i(TAG, "当前文件路径：" + resourcePath);
					Util.readByApacheZipFile(path, resourcePath);
					Message msg = new Message();
					msg.what = UNZIP_SUCCESS;
					msg.obj = path;
					handler.sendMessage(msg);
					// 解压成功之后，把数据对象保存到db文件中
					Down down = new Down(course, grade, resource.getIspackage(), resource.getResourceName(), resource.getResourceId());
					MainActivity.db.addResource(down);
					// 数据加载列表中
					if(down.getIsPackage() == 1)
						return;
					HashMap<String, ArrayList<String>> map = SelectTeach.mHashMap.get(down.getCourse());
					if(map == null) {
						map = new HashMap<String, ArrayList<String>>();
						SelectTeach.mHashMap.put(down.getCourse(), map);
					}
					ArrayList<String> list = map.get(down.getGrade());
					if(list == null) {
						list = new ArrayList<String>(4);
						map.put(down.getGrade(), list);
					}
					int i = 0, len = list.size();
					for( i = 0; i <= len; i++) {
						if(i == len) {
							list.add(down.getResourceName());
							break;
						}
						if(down.getResourceName().equals(list.get(i))) 
							break;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				finally {
					handler.sendEmptyMessage(100);
				}
			}
		}).start(); 
	} // unZip
	
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch(msg.what) {
			case UNZIP_ERROR:
				String text = "解压文件失败, 文件路径为：" + (String)msg.obj + "\n 你可以手动解压到该教材对应目录";
				Toast.makeText(mContext, text, 1).show();
				synchronized (KingPadStudyActivity.number_unzip) {
					KingPadStudyActivity activity = (KingPadStudyActivity) mContext;
					activity.setProgressText(); 
				}
				break;
			case UNZIP_SUCCESS:
				Toast.makeText(mContext, "解压文件成功!", 1).show();
				synchronized (KingPadStudyActivity.number_unzip) {
					KingPadStudyActivity activity = (KingPadStudyActivity) mContext;
					activity.setProgressText(); 
				}
				break;
			case DOWNLOAD_STATING:
				synchronized (KingPadStudyActivity.number_unzip) {
					KingPadStudyActivity activity = (KingPadStudyActivity) mContext;
					activity.setProgressText(); 
				}
				downloadResource((BResource) msg.obj);
				break;
			}
		};
	};

	public void downloadResource(final BResource resource) {
		final KingPadApp application = (KingPadApp) activity.getApplication();
		RequestParameter parameter = new RequestParameter();
		System.out.println("调用downloadResource...resource.getResourceId()="+resource.getResourceId());
		parameter.add("productNumber", KingPadStudyActivity.getAndroidId());
		parameter.add("resourceId", resource.getResourceId());
		LoadData.loadData(Constant.DOWNLOAD_DATA, parameter, new RequestListener() {
			public void onError(String errMsg) {
				Toast.makeText(mContext, "下载当前数据包失败！", 0).show();
			}
			
			public void onComplete(Object obj) {
				DownloadData data = (DownloadData) obj;
				int status = data.getStatus();
				if(status == 0) {
					Toast.makeText(mContext, "余额不足，请充值", 0).show();
					return;
				}
				else if(status == 1) {
					Toast.makeText(mContext, "资源已被删除", 0).show();
					return;
				}
				else if(status == 2) {
					isSecondDownload = false;
					KingPadStudyActivity.number_unzip = 0;
					String url = data.getUrl();
					url_download = url;
					resource.setResourceUrl(url);
					DownloadBean download = new DownloadBean();
					//下载的key
					download.setKey(resource.getResourceId());
					download.setResourceName(resource.getResourceName());
					//TODO
					System.out.println("发送一个key="+resource.getResourceId()+"的下载请求.");
					download.setResrouceId(resource.getResourceId());
					download.setResourceUrl(url);
					download.setResourceNumber(resource.getResourceId());
					download.setFileName(resource.getResourceName()+".zip");
					download.setGrade(grade);
					download.setCourse(course);
					// 发送一条广播，用于添加一条下载记录信息 
					Intent intent = new Intent(DownloadService.ACTION_ADD_DOWNLOAD);
					intent.putExtra(DownloadService.ACTION_ADD_DOWNLOAD, download);
					activity.sendBroadcast(intent);
				}
				else {
					Toast.makeText(mContext, "资源下载失败！", 0).show();
				}
			}
		});
	}
	
	
	/**
	 * 判断该目录下是否存在package包
	 * @return : 是否存在package目录
	 */
	private boolean packageExists() {
		File file = new File(Constant.RESOURCE_PATH + "/" + course + "/" + grade);
		String[] fi = file.list();
		if(fi == null)
			return false;
		
		for(int i = 0; i < fi.length; i++)
			if("package".equals(fi[i]) || "Package".equals(fi[i])) {
				return true;
			}
		
		return false;
	}
	

}
