package com.kingPadStudy.views;


import java.io.File;
import java.io.IOException;
import java.util.List;

import com.kingPadStudy.activity.KingPadStudyActivity;
import com.kingPadStudy.constant.Constant;
import com.kingPadStudy.controller.ViewController;
import com.kingPadStudy.resource.ResourceLoader;
import com.kingpad.R;
import com.utils.AudioRecord;
import com.utils.Util;
import com.views.GameView;

import android.R.integer;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.renderscript.Element;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebSettings.PluginState;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class FlashLangduView extends RelativeLayout{

	/**
	 * 作用：
	 * 时间：2013-1-3 下午3:04:48
	 * void
	 * @param index 学科的模块下标，用于寻找背景 
	 * @param bg_poem 背景框框
	 * @param path_mp3 MP3路径
	 * @param time_record 朗读时间	
	 * @param title	诗歌标题
	 * @param author 诗歌作者
	 * @param content 诗歌ner
	 */
	public void setStart(int index,String bg_poem, String music_background,
			int time, String title, String author, String content){
		//初始化声音 播放池
		Util.initSoundPool(context);
		music_background_read = music_background;
		time_read = time;
		//初始化视图控件
		initView(title,author,content,time);
		//设置监听
		setListener();
		//设置背景
		setBackground(index,bg_poem);
		//初始化WebView
		initWebView();
		//初始化界面处理器
		initHandlerView();
		//进入不可播放状态
		enterStateCantPlay();
        //消除加载条
        KingPadStudyActivity.dismissWaitDialog();
	}
	

	/**
	 * 函数作用：初始化视图控件
	 * void
	 */  
	private void initView(String title,String author,String content,int time) {
		//背景
		layout_bg = (RelativeLayout)findViewById(R.id.layout_background);
		layout_frame = (RelativeLayout)findViewById(R.id.layout_frame);
		//诗文
		text_title = (TextView)findViewById(R.id.title);
		text_title.setText(title);
		text_auther = (TextView)findViewById(R.id.author);
		text_auther.setText(author);
		text_content = (TextView)findViewById(R.id.content);
		text_content.setText(content);
		//进度条
        layout_progress = (LinearLayout)findViewById(R.id.linear_progress);	 
        progressHorizontal = (ProgressBar) findViewById(R.id.progress_horizontal);
        progressHorizontal.setIndeterminate(false);  //设置进度条 不为 不确定模式 
        progressHorizontal.setMax(time);
        progressHorizontal.setProgress(time); 
        //显示时间
        text_time = (TextView)findViewById(R.id.text_time);
        //各种按钮
		button_record = (ImageButton)findViewById(R.id.button_record);		//录音按钮
        button_play = (ImageButton)findViewById(R.id.button_play_record);	//播放按钮
		button_switch_guankan = (ImageButton)findViewById(R.id.button_switch_guankan); //切换按钮
		button_back = (GameView) findViewById(R.id.button_return);
		button_back.addDrawable(R.drawable.back_default, 800);
		button_back.addDrawable(R.drawable.back_hot, 800);
		button_back.play(false);
	}

	
	/**
	 * 函数作用：设置监听
	 * void
	 */
	private void setListener() {
		MyClickListener listener = new MyClickListener();
		button_play.setOnClickListener(listener);
		button_record.setOnClickListener(listener);
		button_switch_guankan.setOnClickListener(listener);
		button_back.setOnClickListener(listener);
	}
	
	
	/**
	 * 描述：按钮监听类
	 */
	class MyClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			if(v == button_play ){
				//点击播放按钮
				if(state == 4){
					//当状态为可播放时，则进入播放状态
					enterStatePlay();
				}else if(state == 3){
					//当状态为正在播放时，进入可播放状态 
					enterStateCanPlay();
				}
			}else if(v == button_record){
				//点击录音按钮
				if(state == 4 || state == 5){
					//当状态为可播放和不可播放状态是，进入倒计时状态
					enterStateCount();
				}else if(state == 2){
					//当状态为正在录音时，进入可播放状态 
					enterStateCanPlay();
				}
			}else if(v == button_back){
				Util.playSoundPool(context, Constant.AUDIO_MARK_BUTTON);
				//关闭线程d
				isOver = true;
				recycle();
				view_guankan.recycle();
				ViewController.resetRecord();
			}else if(v == button_switch_guankan){
				//点击朗读按钮
				ViewController.switchRecord(1);
			}
		}
	}
	
	
	/**
	 * 作用：进入倒计时状态
	 * 时间：2013-1-3 下午4:02:57
	 * void
	 */
	public void enterStateCount(){
		//显示倒计时
		webView.setVisibility(View.VISIBLE);
		//改变状态值
		state = 1;
		//改变界面
		changeView();
		//播放swf，并定时进入录音状态
		playFlash(); 
		thread_daojishi = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(2600);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if(!isOver){
					pauseFlash();
					//隐藏倒计时
					Message message = new Message();
					message.what = 3;
					handler_view.sendMessage(message);
					enterStateRecord();
				}
			}
		});
		thread_daojishi.start();
	}
	

	/**
	 * 作用：进入录音状态
	 * 时间：2013-1-3 下午4:02:57
	 * void
	 */
	public void enterStateRecord(){
		//改变状态值
		state = 2;
		//改变界面
		changeView();
		//打开录音 
		AudioRecord.getInstance().startRecording();
	}
	
	
	/**
	 * 作用：进入播放录音状态
	 * 时间：2013-1-3 下午4:02:57
	 * void
	 */
	public void enterStatePlay(){
		//改变状态值
		state = 3;
		//改变界面
		changeView();
		//开始播放
		playBackgroundSound(music_background_read);
		AudioRecord.getInstance().startPlaying();
		
	}
	
	
	/**
	 * 作用：进入可播放状态
	 * 时间：2013-1-3 下午4:02:57
	 * void
	 */
	public void enterStateCanPlay(){
		//改变状态值
		state = 4;
		//改变界面
		changeView();
	}
	
	
	/**
	 * 作用：进入不可播放状态
	 * 时间：2013-1-3 下午4:02:57
	 * void
	 */
	public void enterStateCantPlay(){
		//改变状态值
		state = 5;
		//改变界面
		changeView();
	}
	

	/** 作用：改变视图
	 * 时间：2013-1-3 下午4:07:23
	 * void
	 */
	private void changeView() {
		Message message = new Message();
		message.what = 2;
		handler_view.sendMessage(message);
	}
	
	
	/**
	 * 描述：进度线程
	 */
	class ThreadProgress implements Runnable{
		@Override
		public void run() {
			//显示进度条布局
			Message message_layout = new Message();
			message_layout.what = 4;
			handler_view.sendMessage(message_layout);
			if(state == 2 ){	//录音状态
			    progressHorizontal.setMax(time_read);
				//System.out.println("开启ThreadProgress。。。state = 2");
				//设置当前录音时间
				time_current_read = 0;
				//初始化进度条及文字
				Message message_progress = new Message();
				message_progress.what = 5;
				handler_view.sendMessage(message_progress);
				while(state == 2){
					//当录音时间结束，进入可播放状态
					if(time_current_read == time_read){
						enterStateCanPlay();
						break;
					}
					//更新进度条及文字
					Message message = new Message();
					message.what = 1;
					handler_view.sendMessage(message);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					time_current_read ++;
				}
				time_recorded = time_current_read;
				//停止录音
				AudioRecord.getInstance().stopRecording();
			}else if(state == 3){ //播放录音状态
				//System.out.println("开启ThreadProgress。。。state = 3");
				//设置当前播放时间
				time_current_play = 0;
			    progressHorizontal.setMax(time_recorded);
				//初始化进度条及文字
				Message message_progress = new Message();
				message_progress.what = 6;
				handler_view.sendMessage(message_progress);
				while(state == 3){
					//当录音时间结束，进入可播放状态
					if(time_current_play == time_recorded){
						enterStateCanPlay();
						break;
					}
					//更新进度条及文字
					Message message = new Message();
					message.what = 0;
					handler_view.sendMessage(message);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					time_current_play ++;
				}
				AudioRecord.getInstance().stopPlaying();	//停止播放录音
				stopPlayBackgroundSound();
			}
		}
		
	}
	

	/** 作用：初始化视图处理器
	 * 时间：2013-1-3 下午5:06:03
	 * void
	 */
	private void initHandlerView() {
		handler_view = new Handler(){
	    	@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if(msg.what == 0){
					//System.out.println("播放录音第"+time_current_play+"秒");
					//改变当前进度条的进度
					progressHorizontal.setProgress(time_current_play);
					//改变文字
					text_time.setText("播放录音第"+time_current_play+"秒");
				} else if(msg.what == 1){
					//System.out.println("录音第"+time_current_read+"秒");
					//改变当前进度条的进度
					progressHorizontal.setProgress(time_read - time_current_read);
					//改变文字
					text_time.setText("录音第"+time_current_read+"秒");
				}else if(msg.what == 2){
					switch (state) {
					case 1:
						//倒计时
						button_record.setBackgroundResource(R.drawable.button_stop_record3);	//录音按钮变灰
						button_play.setBackgroundResource(R.drawable.button_play_record3); //播放按钮变灰
						break;
					case 2:
						//System.out.println("进入handler_view的case 2：");
						//录音
						button_record.setBackgroundResource(R.drawable.button_stop_record1);	//录音按钮变停止录音
						button_play.setBackgroundResource(R.drawable.button_play_record3); //播放按钮变灰
						thread_progress = new Thread(new ThreadProgress()); //初始化进度线程 
						thread_progress.start();  //开启进度线程 
						break;
					case 3:
						//播放
						button_record.setBackgroundResource(R.drawable.button_record3);	//录音按钮变灰
						button_play.setBackgroundResource(R.drawable.button_stop_playing); //播放按钮变停止播放
						thread_progress = new Thread(new ThreadProgress()); //初始化进度线程
						thread_progress.start();  //开启进度线程
						break;
					case 4:
						//可播放
						layout_progress.setVisibility(View.INVISIBLE);
						button_record.setBackgroundResource(R.drawable.button_record1);	//录音按钮变亮
						button_play.setBackgroundResource(R.drawable.button_play_record1); //播放按钮变亮
						break;
					case 5:
						//不可播放
						button_record.setBackgroundResource(R.drawable.button_record1);	//录音按钮变亮
						button_play.setBackgroundResource(R.drawable.button_play_record3); //播放按钮变灰
						break;
					}
				}else if(msg.what == 3){ 
					//隐藏倒计时
					webView.setVisibility(View.INVISIBLE);
				}else if(msg.what == 4){
					//隐藏进度条布局
					layout_progress.setVisibility(View.VISIBLE);
				}else if(msg.what == 5){
					//初始化进度条为录音状态
					progressHorizontal.setProgress(time_read);
					text_time.setText("");
				}else if(msg.what == 6){
					//初始化进度条为播放录音状态
					progressHorizontal.setProgress(0);
					text_time.setText("");
				}
			}
		};
	}
	
	
	/**
	 * 函数作用：设置背景
	 * void
	 */  
	private void setBackground(int index,String background_frame) {
		//设置背景
		String file_background = ResourceLoader.getMajorBackImageUrl(index);
        File file=new File(file_background);
        if (file.exists()) {//若该文件存在
        	bmp_bg = BitmapFactory.decodeFile(file_background);
        	layout_bg.setBackgroundDrawable(new BitmapDrawable(bmp_bg));
        	layout_bg.getBackground().setAlpha(160);
        }
		//设置播放框
        File file_frame=new File(background_frame);
        if (file_frame.exists()) {//若该文件存在
        	bmp_frame = BitmapFactory.decodeFile(background_frame);
        	layout_frame.setBackgroundDrawable(new BitmapDrawable(bmp_frame));
        }
	}

	
	/**
	 * 函数作用：初始化WebView
	 * void
	 */	 
	private void initWebView() {
		webView = (WebView)findViewById(R.id.webview);
		PackageInfo flashInfo = null;
		int x_flash  = 350 ;
		int y_flash = 490 ;
		//设置flash的坐标和大小
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				width_flash , height_flash      
        );   
		params.leftMargin = x_flash;
		params.topMargin = y_flash;
		webView.setLayoutParams(params);
        webView.setBackgroundColor(0);  	//背景透明
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setPluginsEnabled(true);
		webView.getSettings().setAllowFileAccess(true);
		webView.getSettings().setPluginState(PluginState.ON);
        String url_html = "file:///android_asset/flash_player.html";		
        webView.loadUrl(url_html);	//加载HTML背景 					
		//检测flash插件是否存在
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> infoList = pm.getInstalledPackages(PackageManager.GET_SERVICES);  
        for (PackageInfo info : infoList) {
            if ("com.adobe.flashplayer".equals(info.packageName)) {
                flashInfo = info;
            }
        }
        try {
			//等待html文件被完全解析
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        //装载FLASH
        if(null != flashInfo){
        	loadFlash();
        } else {
        	showError();
        }
    }

	
	/**
	 * 播放声音
	 * @param soundPath：声音文件在sd卡上的路径
	 */ //TODO
	private static void playBackgroundSound(String soundPath){
		if(soundPath == null){
			return;
		}
		File file = new File(soundPath);
		if(file.exists()){
			try {
				//System.out.println("开始播放背景。。。");
				Util.playSound(soundPath,true);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			//System.out.println("古诗背景音乐不存在..............");
			//不存在
		}
	}
	
	
	/**
	 * 停止声音
	 * @param soundPath：声音文件在sd卡上的路径
	 */ //TODO
	private static void stopPlayBackgroundSound(){
		//System.out.println("停止播放背景 。。。");
		try {
			Util.releasePlayer();
		} catch (Exception e){
			
		}
	}
	
	
	/**
	 * 函数作用：装载FLASH 
	 * void
	 * @param b 是否直接播放
	 */  
	private void loadFlash() {
		//装载SWF，并直接播放
        String url_script = "javascript:loadSWF(\"" + url_flash + "\", \"" + 
        		width_flash + "\", \"" + height_flash + "\")";
        webView.loadUrl(url_script);	
        pauseFlash();
	}
	
	
	/**
	 * 函数作用：播放Flash
	 * void
	 */ 
	public void playFlash() {
		//调用JS开启FLASH
		webView.loadUrl("javascript:Play()");
	}
	
	
	/**
	 * 函数作用：暂停Flash
	 * void
	 */  
	public void pauseFlash() {
		webView.loadUrl("javascript:Pause()");
		
	}
	
	
	/**
	 * 作用：显示错误
	 * 时间：2013-1-3 下午2:44:07
	 * void
	 */ 
	public void showError(){
		webView.loadUrl("javascript:error()");
	}

	
	/**
	 * 作用：按返回时，回收
	 * 时间：2013-1-3 下午1:26:38
	 * void
	 */  
	public void recycle(){
		//停止录音
		AudioRecord.getInstance().stopRecording();
		//停止背景音
		stopPlayBackgroundSound();
		//停止播放录音
		AudioRecord.getInstance().stopPlaying();
		webView.loadUrl("javascript:Pause()");
		webView.destroy();
		ViewController.backToNormal_from_relative();
		if (bmp_bg!= null && !bmp_bg.isRecycled()) {
			bmp_bg.recycle();
		}
		if (bmp_frame!= null && !bmp_frame.isRecycled()) {
			bmp_frame.recycle();
		}
	}

	
	

	/** 作用：设置观看视图
	 * 时间：2013-1-3 下午1:35:57
	 * void
	 * @param view_record_flash
	 */
	public void setGuanKanView(FlashGuankanView view_record_flash) {
		view_guankan = view_record_flash;
	}
	
	
	/** 
	 * @param context
	 * @param attrs
	 */
	public FlashLangduView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}
	
	
	
	
	
	
	/*
	 * 录音变量
	 */
	private String music_background_read ; //朗读背景音乐
	private int time_read;  //朗读时间 
	private int state;	// 1倒计时状态  2录音状态 3播放录音状态  4可播放状态 5不可播放状态 
	private int time_current_read;	//当前录音时间
	private int time_recorded;	//录音时间
	private int time_current_play;  //当前播放时间
	private Thread thread_progress; //进度线程
	private Thread thread_daojishi ;//倒计时线程
	private boolean isOver = false;
	private Handler handler_view;  //界面处理器
	/*
	 * 倒计时FLASH参数
	 */
	String url_flash = "file:///android_asset/countDown.swf";				//需要播放的flash文件路径
	int width_flash = 293;
	int height_flash = 33;

	/*
	 * 视图控件
	 */
	RelativeLayout layout_bg;	//背景布局
	RelativeLayout layout_frame;	//播放框布局
	
	WebView webView;	//WebView
	
	TextView text_title;	//诗题
	TextView text_auther;	//作者
	TextView text_content;	//内容
	
	ImageButton button_record;	//录音按钮
	ImageButton button_play ; 	//播放按钮

	GameView button_back;		//返回按钮
	ImageButton button_switch_guankan;	//切换按钮
	
	LinearLayout layout_progress;  //进度条 布局
	ProgressBar progressHorizontal;   //水平进度条
	TextView text_time; 	//时间显示文字
	
	/*
	 * 需要回收的Bitmap
	 */
	Bitmap bmp_bg ; //背景
	Bitmap bmp_frame ;  //播放框 
	
	private Context context;
	private FlashGuankanView view_guankan = null;
	
}