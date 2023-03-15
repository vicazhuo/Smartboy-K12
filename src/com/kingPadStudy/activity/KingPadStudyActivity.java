/** 文件作用：主活动
 *	创建时间：2012-11-17 下午7:49:28
 *  作者： 陈相伯 
 *  描述：
 */
package com.kingPadStudy.activity; 
import java.io.IOException;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings.Secure;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.adapter.CatalogAdapter;
import com.data.RegistData;
import com.king.Activate;
import com.king.DownloadLogsView;
import com.king.GameDownLoad;
import com.king.HelpView;
import com.king.Login;
import com.king.PayLogView;
import com.king.PersonalInfo;
import com.king.ResourceView;
import com.king.SelectTeach;
import com.king.UpdatePwd;
import com.kingPadStudy.constant.Constant;
import com.kingPadStudy.controller.ViewController;
import com.kingPadStudy.security.Security;
import com.kingPadStudy.service.DownloadService;
import com.kingPadStudy.tools.EuipmentInfoCatcher;
import com.kingPadStudy.tools.SimpleCrypto;
import com.kingPadStudy.views.DialogCreator;
import com.kingPadStudy.views.MainView;
import com.kingpad.KingPadApp;
import com.kingpad.R;
import com.net.LoadData;
import com.net.RequestListener;
import com.net.RequestParameter;
/**
 * 描述：主活动
 *
 */
public class KingPadStudyActivity extends Activity implements OnCompletionListener, OnErrorListener, OnInfoListener,
 OnPreparedListener, OnSeekCompleteListener, OnVideoSizeChangedListener, SurfaceHolder.Callback{
	/*
	 * 两次退出按键事件的间隔时间
	 */
	private long mExitTime ;
	/*
	 * 上下文
	 */
	private static Context context;
	/*
	 * 进度条
	 */
	private static Dialog progressDialog;
	/*
	 * APP
	 */
	private KingPadApp mApp;
	
	public BroadcastReceiver receiver;
	
	public KingPadApp getmApp() {
		return mApp;
	}

	/**
	 * 目录结构的listview对象
	 */
	private ListView mListView;
	/**
	 * 当前选择的模块下标
	 */
	private int position_now;
	/**
	 * adapter对象
	 */
	private CatalogAdapter mAdapter;
	/**
	 * 容器
	 */
	private LinearLayout mContainer;
	/**
	 * View对象的hashmap
	 */
	private HashMap<String, View> mHashMap;
	/**
	 * 充值记录关键字
	 */
	public final static int HELP = 7;
	public final static int GAME_DOWNLOAD = 6;
	public final static int PAY_LOG = 4;
	public final static int RESOURCE = 3;
	public final static int DOWNLOAD_LOGS = 2;
	public static Integer sDownloading = new Integer(0);
	public static Integer number_unzip = 0;
	/*
	 * 弹出框
	 */
	private TextView mTextProgress;
	private Display mDisplay;
	private SurfaceView mSurfaceView;
	private SurfaceHolder mSurfaceHolder;
	private MediaPlayer mPlayer;
	int videoWidth = 0;
	int videoHeight = 0;
	boolean readyToPlay = false;
	public final static String TAG = "VIDEO_PLAY";
    private static final int    sDefaultTimeout = 5000;
    private static final int    FADE_OUT = 1;
    private static final int    SHOW_PROGRESS = 2;
    StringBuilder               mFormatBuilder;
    Formatter                   mFormatter;
    private TextView            mEndTime, mCurrentTime;
    private boolean mDragging = false;
	private boolean isFirstSelectResource = true;
    private MainView mainView;
	
    /** 作用：动画欢迎界面
	 * 时间：2013-1-26 下午8:41:51
	 * void
	 */
	private void welcome() {
		//首先释放资源
		clearPlayer();
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.video);
        mDisplay = getWindow().getWindowManager().getDefaultDisplay();
        initMP4();
        mainView = new MainView(context, this);
	}
	
	/**
	 * 清理播放器 
	 */
	private void clearPlayer(){
		if(mPlayer != null){
			if(mPlayer.isPlaying()){
				mPlayer.stop();
			}
			mPlayer.release();
		}
	}

	/**
     * 初始化
     */
    private void initMP4() {
    	 mSurfaceView = (SurfaceView) findViewById(R.id.SurfaceView);
         mSurfaceHolder = mSurfaceView.getHolder();
         mSurfaceHolder.addCallback(this);
         mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
         mPlayer = new MediaPlayer();
         mPlayer.setOnCompletionListener(this);
         mPlayer.setOnErrorListener(this);
         mPlayer.setOnInfoListener(this);
         mPlayer.setOnPreparedListener(this);
         mPlayer.setOnSeekCompleteListener(this);
         mPlayer.setOnVideoSizeChangedListener(this);
         try {
             AssetFileDescriptor fileDescriptor = getAssets().openFd("kingpad.mp4");
        	 mPlayer.setDataSource(fileDescriptor.getFileDescriptor(),
        	         fileDescriptor.getStartOffset(),
        	         fileDescriptor.getLength());
 		} catch (Exception e) {
 			Toast.makeText(context, "媒体播放器错误："+e.getMessage(), 0).show();
 			finish();
 		}
	     mFormatBuilder = new StringBuilder();
	     mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
    }
    
    /**
     * 格式化当前时间格式
     * @param timeMs
     * @return
     */
	private String stringForTime(int timeMs) {
        int totalSeconds = timeMs / 1000;
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours   = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }
    
    
    private int number_surface_create = 0;
    // ------------------------------------------------------------------------------
	/**
	 * 设置视频的显示view
	 */
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if(number_surface_create >0){
			return ;
		}
		number_surface_create ++;
		try{
			mPlayer.setDisplay(mSurfaceHolder);
		}catch(Exception e){
			return ;
		}
		try {
			mPlayer.prepare();
		} catch (IllegalStateException e) {
			Toast.makeText(context, "媒体播放器状态错误", 0).show();
			finish();
		} catch (IOException e) {
			Toast.makeText(context, "媒体播放器输出错误", 0).show();
			finish();
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		Log.i(TAG, "SufaceChanged Called");
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.i(TAG, "SufaceDestroyed Called");
	}

	@Override
	public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
		Log.i(TAG, "SufaceChanged Called");
	}

	@Override
	public void onSeekComplete(MediaPlayer mp) {
		Log.i(TAG, "SeekComplete Called");
	}

	/**
	 * 调整surface视屏播放控件大小
	 */
	@Override  
	public void onPrepared(MediaPlayer mp) {
		Log.v(TAG, "onPrepared Called");
		videoWidth = mp.getVideoWidth();
		videoHeight = mp.getVideoHeight();
		mSurfaceView.setLayoutParams(new FrameLayout.LayoutParams(videoWidth, videoHeight));
		mp.start();
		mSurfaceView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				isComplete = true;
				mPlayer.stop();
				//释放播放器
				mPlayer.release();
				if(!Security.isregistPad(KingPadStudyActivity.this)){ 
					//若没有注册过，那么注册  
					final Dialog dlg = DialogCreator.createLoadingDialog(context,"加载数据中...");
					dlg.show();
					String imei = KingPadStudyActivity.getAndroidId();
					int SysVersion = EuipmentInfoCatcher.getSysVersion();
					String CupInfo = EuipmentInfoCatcher.getCpuInfo();
					String ProductName = EuipmentInfoCatcher.getProductName();
					String ModelName = EuipmentInfoCatcher.getModelName();
					String ManufacturerName = EuipmentInfoCatcher.getManufacturerName();
					RequestParameter parameter = new RequestParameter();
					parameter.add("productNumber", imei);
					parameter.add("productName", ProductName);
					parameter.add("productCupinfo", CupInfo);
					parameter.add("productModelname", ModelName);
					parameter.add("productManufacturername", ManufacturerName);
					parameter.add("productSysversion", SysVersion+"");
					LoadData.loadData(Constant.REGIST_DATA, parameter, new RequestListener() {
						public void onError(String errMsg) {
							dlg.dismiss();
							showRegistDialog(-1);
						}
						public void onComplete(Object obj) {
							dlg.dismiss();
							RegistData data = (RegistData)obj;
							int res = data.getStatus();
							if(res == 0 || res == 1){
								//将注册平板信息放到本地
								Security.registPad(KingPadStudyActivity.this);
							}
							showRegistDialog(res);
						}
					});
				}
		        // 保存用户配置文件 
				SharedPreferences sp = getSharedPreferences(Constant.LOCAL_DATA, Activity.MODE_WORLD_READABLE);
				//获取产品序列号
				String productNumber = sp.getString("productNumber", "");
				//将序列号解密
				if(productNumber != null && !productNumber.equals("")){
					try {
						productNumber = SimpleCrypto.decrypt(Constant.PASSWORD_SimpleCrypto, productNumber);
					} catch (Exception e) {
						makeText("解密异常："+e.getMessage());
					}
				} 
				
				//TODO 此处屏蔽了激活关口
				//进入主界面  或者是激活界面
				if(!Security.isActivated(context) 
						|| productNumber == null 
						|| "".equals(productNumber)) {
					Activate activate = new Activate(context, mApp,KingPadStudyActivity.this);
					setContentView(activate);
				} else {
					//检查此设备是否拥有使用权限
					String android_id = KingPadStudyActivity.getAndroidId();
		            boolean res_check_right = Security.login(context, android_id);
		            if(res_check_right){
		            	enterMainView();
		            }else{
		            	//弹出对话框
		            	showQuitDialog_right();
		            }
				}
			}
		});
	}

	@Override
	public boolean onInfo(MediaPlayer mp, int what, int extra) {
		if (what == MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING) {
			Log.v(TAG, "Media Info, Media Info Bad Interleaving " + extra);
		} else if (what == MediaPlayer.MEDIA_INFO_NOT_SEEKABLE) {
			Log.v(TAG, "Media Info, Media Info Not Seekable " + extra);
		} else if (what == MediaPlayer.MEDIA_INFO_UNKNOWN) {
			Log.v(TAG, "Media Info, Media Info Unknown " + extra);
		} else if (what == MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING) {
			Log.v(TAG, "MediaInfo, Media Info Video Track Lagging " + extra);
		}
		return false;
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		if (what == MediaPlayer.MEDIA_ERROR_SERVER_DIED) {
			Log.v(TAG, "Media Error, Server Died " + extra);
		} else if (what == MediaPlayer.MEDIA_ERROR_UNKNOWN) {
			Log.v(TAG, "Media Error, Error Unknown " + extra);
		}
		return false;
	}

	/**
	 * 当视屏播放完时的处理事件
	 */
	@Override
	public void onCompletion(MediaPlayer mp) {
		if(isComplete){
			return ;
		}
		//释放播放器
		mPlayer.stop();
		mPlayer.release();
		if(!Security.isregistPad(this)){
			//若没有注册过，那么注册  
			final Dialog dlg = DialogCreator.createLoadingDialog(context,"正在注册平板...");
			dlg.show();
			String imei = KingPadStudyActivity.getAndroidId();
			int SysVersion = EuipmentInfoCatcher.getSysVersion();
			String CupInfo = EuipmentInfoCatcher.getCpuInfo();
			String ProductName = EuipmentInfoCatcher.getProductName();
			String ModelName = EuipmentInfoCatcher.getModelName();
			String ManufacturerName = EuipmentInfoCatcher.getManufacturerName();
			RequestParameter parameter = new RequestParameter();
			parameter.add("productNumber", imei);
			parameter.add("productName", ProductName);
			parameter.add("productCupinfo", CupInfo);
			parameter.add("productModelname", ModelName);
			parameter.add("productManufacturername", ManufacturerName);
			parameter.add("productSysversion", SysVersion+"");
			LoadData.loadData(Constant.REGIST_DATA, parameter, new RequestListener() {
				public void onError(String errMsg) {
					dlg.dismiss();
					showRegistDialog(-1);
				}
				public void onComplete(Object obj) {
					dlg.dismiss();
					RegistData data = (RegistData)obj;
					int res = data.getStatus();
					if(res == 0 || res == 1){
						//首次，或者二次注册都算注册成功
						Security.registPad(KingPadStudyActivity.this);
						showRegistDialog(res);
					}else{
						showRegistDialog(-1);
					}
				}
			});
		}
        // 保存用户配置文件 
		SharedPreferences sp = getSharedPreferences(Constant.LOCAL_DATA, Activity.MODE_WORLD_READABLE);
		//获取产品序列号
		String productNumber = sp.getString("productNumber", "");
		//将序列号解密
		if(productNumber != null && !productNumber.equals("")){
			try {
				productNumber = SimpleCrypto.decrypt(Constant.PASSWORD_SimpleCrypto, productNumber);
			} catch (Exception e){
				makeText("解密异常："+e.getMessage());
			}
		}
		//进入主界面  或者是激活界面
		if(!Security.isActivated(context) 
				|| productNumber == null 
				|| "".equals(productNumber)) {
			Activate activate = new Activate(context, mApp,KingPadStudyActivity.this);
			setContentView(activate);
		} else {
			//检查此设备是否拥有使用权限
			String android_id = KingPadStudyActivity.getAndroidId();
            boolean res_check_right = Security.login(context, android_id);
            if(res_check_right){   
            	enterMainView();   
            }else{				   
            	//弹出对话框		   
            	showQuitDialog_right();
            }					   
		}
		isComplete = true;
	}

	
	private void showRegistDialog(int res) {
		Dialog dlg = null;
		String show = "";
		if(res == -1){ //参数错误
			show = "平板注册失败，请检查网络情况，或者联系服务商！";
		}else if(res == 0){	//已存在
		}else if(res == 1){	//成功
			show = "恭喜！您的平板电脑已经在服务器上注册成功！";
		}
		if(res!=0){
			dlg = new AlertDialog.Builder(KingPadStudyActivity.this)
			.setTitle("提示")
			.setMessage(show)
			.setNeutralButton("确定", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {			
				}
			}) .create();
			dlg.show();
		}
	}
	
	private void makeText(final String text) {
		Toast.makeText(this, text, 0).show();
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this; 
        mApp = (KingPadApp) getApplication();
        welcome();
        //设置屏幕属性
  		setScreenScale();
  		ViewController.intiView(this, this);
        //开启后台下载的服务
//        System.out.println("开启服务..");
        Intent intent = new Intent(this, DownloadService.class);
        DownloadService.context = this;
        startService(intent);
        String resourceName = "人教版_2a";
        String path = Constant.RESOURCE_PATH + "/Chinese/二年级";
        /*
         * 初始化LoadData的与主UI有关的变量，以便于在服务中使用 LoadDataa
         */
        LoadData.newHandler();
    }
    
	/**
	 * 激活界面的初始化操作
	 */
	private void init() {
		mHashMap = new HashMap<String, View>();
		mContainer = (LinearLayout) findViewById(R.id.container);
		mTextProgress = (TextView) findViewById(R.id.progress);
		// 加载listview控件
		mListView = (ListView)findViewById(R.id.listview_catalog);
		int iconArray[] = {R.drawable.btn1,R.drawable.btn1,R.drawable.btn1,
							R.drawable.btn1,R.drawable.btn1, R.drawable.btn1, R.drawable.btn1,R.drawable.btn1};
		String textArray[] = {"教材设置", "用户信息", "下载记录", "下载资源", "充值记录", "修改密码","益智小游戏","常见问题解答"};
		// 加载目录数据
		mAdapter = new CatalogAdapter(this, iconArray, textArray); 
		mListView.setAdapter(mAdapter);
	}
	
	private void setListener() {
		mListView.setOnItemClickListener(new ListView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				position_now = position;
				selectCatalog(position,false);
			}
		});
	}
	
	/**
	 * 作用：显示我的睿学界面
	 * 时间：2013-1-30 下午3:22:43
	 * void
	 */
	public void showMain() {
      setContentView(R.layout.main);
      //注册退出事件
      Button btn_quit = (Button)findViewById(R.id.button_quit);
      btn_quit.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
				if(sDownloading>0 || number_unzip>0){
					String message = "正在下载或者解压资源，不能退出！";
					Dialog dlg = new AlertDialog.Builder(KingPadStudyActivity.this)
					.setTitle("提示")
					.setMessage(message)
					.setNeutralButton("我知道了", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) { }
					}) .create();
					dlg.show();
				}else{
					enterMainView();
				}
		}
      });
      //注册刷新事件
      Button btn_refresh = (Button)findViewById(R.id.button_refresh);
      btn_refresh.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
			//刷新当前页
			selectCatalog(position_now, true);
		}
	  });
	  init();
      setListener();
      // 初始化数据默认显示选择学习资源目录
      selectCatalog(0,false);
	}
	    
	
	private void selectCatalog(final int index,boolean isRefresh) {
		View view = null;
		mContainer.removeAllViews();
		if(index == 0)
		{
			//makeText("选择学习资源");
			if(!isRefresh){
				view = mHashMap.get("select_teach");
			}
			if(view == null) {
				view = new SelectTeach(this);
				mHashMap.put("select_teach", view);
				mContainer.addView(view);
			}
			else {
				mContainer.addView(view);
			}
			
		} 
		else if(index == 1) 
		{
			if(!isRefresh){
				view = mHashMap.get("personal_detail");
			}
			if(view == null) {
				view = new PersonalInfo(this, mApp);
				mHashMap.put("personal_detail", view);
			}
			mContainer.addView(view);
		} 
		else if(index == DOWNLOAD_LOGS) {
			if(!isRefresh){
				view = mHashMap.get("download_logs");
			}
			if(view == null) {
				view = new DownloadLogsView(this, mApp);
				mHashMap.put("download_logs", view);
			}
			mContainer.addView(view);
		}
		else if(index == RESOURCE) 
		{
			if(!isRefresh){
				view = mHashMap.get("resource_view");
			}
			if(view == null) {
				view = new ResourceView(mApp, this);
				mHashMap.put("resource_view", view);
				mContainer.addView(view);
			} else {
				mContainer.addView(view);
			}
			isFirstSelectResource = false;
		}
		else if(index == PAY_LOG) 
		{
			if(!isRefresh){
				view = mHashMap.get("pay_log");
			}
			if(view == null) {
				view = new PayLogView(this, mApp);
				mHashMap.put("pay_log", view);
				mContainer.addView(view);
			} else {
				mContainer.addView(view);
			}
		}
		else if(index == 5)
		{
			if(!isRefresh){
				view = mHashMap.get("update_pwd");
			}
			if(view == null) {
				view = new UpdatePwd(this);
				mHashMap.put("update_pwd", view);
				mContainer.addView(view);
			}
			else {
				mContainer.addView(view);
			}
		}else if(index == GAME_DOWNLOAD)
		{
			if(!isRefresh){
				view = mHashMap.get("game_download");
			}
			if(view == null) {
				view = new GameDownLoad(this,mApp);
				mHashMap.put("game_download", view);
				mContainer.addView(view);
			}
			else {
				mContainer.addView(view);
			}
		}else if(index == HELP)
		{
			if(!isRefresh){
				view = mHashMap.get("help");
			}
			if(view == null) {
				view = new HelpView(this);
				mHashMap.put("help", view);
				mContainer.addView(view);
			}
			else {
				mContainer.addView(view);
			}
		}
	}
	
	


	/**
	 * 作用：获取设备唯一标识
	 * 时间：2013-1-30 下午4:08:22
	 * String
	 * @return
	 */
	public static String getAndroidId(){
		String android_id = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
		return android_id;
	}
		
		
	//OnResume被调用的次数，用于当用户点击主Activity返回键，自动返回本Activity时，将本Activity也退出
	private int count_Onresume = 0;
	
	
	/**
	 * 作用：登陆 我的睿学				
	 * 时间：2013-1-30 下午7:10:45	
	 * void						
	 */							
	public  void Login_MyKingPad() {
		Login login = new Login(context, mApp);
		setContentView(login);
	}
	
	
	
	/** 作用：进入主界面
	 * 时间：2013-1-21 上午10:14:44
	 * void
	 */
	public  void enterMainView() {
		ViewController.controllMainView(mainView, Constant.VIEW_MAIN);
	}

	 public static Handler handler =  new Handler(){
	    	public void handleMessage(Message msg){
	    		ViewController.setMajor(msg.arg1);
	    		ViewController.controllView(msg.what);
	    	}
	 };
	
	/**
	 *	函数作用：设置屏幕比例
	 * void
	 */
	private void setScreenScale(){
		//初始化屏幕适配
        DisplayMetrics dm = new DisplayMetrics();   
        getWindowManager().getDefaultDisplay().getMetrics(dm);  
        Constant.setWidthScreen(dm.widthPixels); 
        Constant.setHeightScreen(dm.heightPixels); 
        Constant.setCompareIndex(); 
	}
	
	
	public static void showWaitDialog(){
		dismissWaitDialog();
		progressDialog = DialogCreator.createLoadingDialog(context,"数据加载中...");
		progressDialog.show();
	}
	
	public static void dismissWaitDialog(){
		if (null != progressDialog && progressDialog.isShowing()){
			progressDialog.dismiss();
		}
	}
	
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {			
		if(keyCode == KeyEvent.KEYCODE_BACK)
		{
			showQuitDialog();
			return true;
		}
		return super.onKeyDown(keyCode, event);
    }
	
	/**
	 * 作用：当无权限的时候弹出的对话框
	 * 时间：2013-1-30 下午4:22:16
	 * void
	 */
	private void showQuitDialog_right() {
		Dialog dlg = new AlertDialog.Builder(KingPadStudyActivity.this)
		.setTitle("提示")
		.setMessage("对不起，您无权使用本软件！")
		.setNeutralButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {			
	    		ViewController.stopView();	
				finish();
	            System.gc();
	            System.exit(0);
			}
		})
		.create();
		dlg.show();
	}
	
	private void showQuitDialog() {
		if(sDownloading>0 || number_unzip >0){
			String message = "正在下载或者解压资源，不能退出！";
			Dialog dlg = new AlertDialog.Builder(KingPadStudyActivity.this)
			.setTitle("提示")
			.setMessage(message)
			.setNeutralButton("我知道了", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) { }
			}) .create();
			dlg.show();
		}else{
			String message = "";
			message = "是否退出程序?";
			Dialog dlg = new AlertDialog.Builder(KingPadStudyActivity.this)
			.setTitle("提示")
			.setMessage(message)
			.setNeutralButton("是", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
//					System.out.println("退出程序。");
		    		ViewController.stopView();	
					finish();
				}
			})
			.setNegativeButton("否", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) { }
				
			}).create();
			dlg.show();
		}
		
	}

	@Override
	protected void onDestroy() {
//		 System.out.println("onDestroy..");
		super.onDestroy();
    	// 如果当前没有数据正在下载，则停止服务
    	if(DownloadService.canClose() == true) {
    		stopService(new Intent(this, DownloadService.class));
    	}else{
//   		 	System.out.println("还有下载，不关闭服务..");
    	}
	}
	
	public void setProgressText() {
//		System.out.println("改变显示信息。。sDownloading="+sDownloading+",number_unzip="+number_unzip);
		String text = "当前正在";
		if(sDownloading  > 0) {
			text += "下载" + sDownloading + "个资源 ";
		}  
		if(number_unzip >0){
			if(sDownloading>0){
				text += "，";
			}
			text += "解压"+number_unzip+"个资源";
		}
		if(sDownloading<=0 && number_unzip<=0){
			text = "";
		}
//		System.out.println("改变显示信息。。text="+text);
		mTextProgress.setText(text);
		mTextProgress.postInvalidate();
	}
	
	private boolean isComplete = false;
	
	
	
	/**
	 * 复写onActivityResult，这个方法
	 * 是要等到SimpleTaskActivity点了提交过后才会执行的
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
//		//可以根据多个请求代码来作相应的操作
//		if(20==resultCode)
//		{
//			String bookname=data.getExtras().getString("bookname");
//			String booksale=data.getExtras().getString("booksale");
//			TextView_result.setText("书籍名称:"+bookname+"书籍价钱"+booksale+"元");
//		}
//		if(data == null){
//			System.out.println("requestCode="+requestCode+",resultCode="+resultCode+",data=null");
//		}else{
//			System.out.println("requestCode="+requestCode+",resultCode="+resultCode+",data="+data.getAction());
//		}
		super.onActivityResult(requestCode, resultCode, data);
	}	
	
	
	
	
	
	
	
}