package com.kingPadStudy.views;


import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.kingPadStudy.activity.KingPadStudyActivity;
import com.kingPadStudy.constant.Constant;
import com.kingPadStudy.controller.ViewController;
import com.kingPadStudy.resource.ResourceLoader;
import com.kingpad.R;
import com.utils.Util;
import com.views.GameView;


public class FlashGuankanView extends RelativeLayout{
	/**
	 * 函数作用：开始工作
	 * void
	 */
	public void setStart(int index,String url,String background_frame,int w_web,int h_web,int x,int y,int w,int h,boolean isController){
		//System.out.println("setStart...isController="+isController);
		Util.initSoundPool(context);
		//初始化视图控件
		initView(isController);
		//设置监听
		setListener(isController);
		//设置背景
		setBackground(index,background_frame);
		//初始化变量
		url_flash = url;
        url_flash = encodeFlashFilePath(url_flash);	//中文转码
		width_flash = w;
		height_flash = h;
		width_web = w_web;
		height_web = h_web;
		//初始化WebView
		initWebView(x,y,isController);
        //消除加载条
        KingPadStudyActivity.dismissWaitDialog();
	}
	
	/**
	 * 函数作用：初始化WebView
	 * void
	 */
	private void initWebView(int x,int y,boolean isControll) {
		//设置WebView宽高和位置
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				width_web , height_web      
        );   
		params.leftMargin = x;
		params.topMargin = y;
		webView.setLayoutParams(params);
		//设置WebView基本属性
		webView.setBackgroundColor(0);  	//背景透明
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setPluginsEnabled(true);
		webView.getSettings().setAllowFileAccess(true);
		webView.getSettings().setPluginState(PluginState.ON);
		webView.addJavascriptInterface(new CallJava(), "CallJava");			
		handler = new Handler();											
        String url_html = "file:///android_asset/flash_player.html";		
        webView.loadUrl(url_html);	//加载HTML背景 							
        try {
			//等待html文件被完全解析
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        //装载FLASH 
        loadFlash();
        if(isControll){
        	pauseFlash();
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
	 * 函数作用：设置监听
	 * void
	 */
	private void setListener(boolean isControll) {
		MyClickListener listener = new MyClickListener();
		if(isControll){
			button_play.setOnClickListener(listener);
			button_stop.setOnClickListener(listener);
		}
		button_switch_langdu.setOnClickListener(listener);
		button_back.setOnClickListener(listener);
	}
	
	
	class MyClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			if(v == button_play ){
				//点击播放/暂停
				if(isPlaying){
					button_play.setImageResource(R.drawable.play);
					pauseFlash();
				}else{
					button_play.setImageResource(R.drawable.pause);
					playFlash();
				}
				isPlaying = !isPlaying;
			}else if(v == button_stop){
				//点击停止
				stopFlash();
			}else if(v == button_back){
				Util.playSoundPool(context, Constant.AUDIO_MARK_BUTTON);
				recycle();
				if(view_langdu!=null){
					view_langdu.recycle();
				}
				ViewController.resetRecord();
			}else if(v == button_switch_langdu){
				//点击朗读按钮
				ViewController.switchRecord(2);
			}
		}
		
	}
	
	
	/**
	 * 作用：按返回时，回收
	 * 时间：2013-1-3 下午1:26:38
	 * void
	 */
	public void recycle(){
		webView.loadUrl("javascript:Pause()");
		//重置进度条
		handler.removeCallbacks(runnable_progress);
		webView.destroy();
		//点击返回
		ViewController.backToNormal_from_relative();
		if (bmp_bg!= null && !bmp_bg.isRecycled()) {
			bmp_bg.recycle();
		}
		if (bmp_frame!= null && !bmp_frame.isRecycled()) {
			bmp_frame.recycle();
		}
	}
	


	/**
	 * 函数作用：播放Flash
	 * void
	 */
	public void playFlash() {
		//调用JS开启FLASH
		webView.loadUrl("javascript:Play()");
		//启动进度条线程
		handler.post(runnable_progress);
	}
	

	/**
	 * 函数作用：暂停Flash
	 * void
	 */
	public void pauseFlash() {
		//System.out.println("暂停FLASH");
		webView.loadUrl("javascript:Pause()");
		
	}

	/**
	 * 停止播放
	 */
	public void stopFlash(){
		webView.loadUrl("file:///android_asset/flash_player.html");
		try {
			//暂停500ms让之前加载的html文件被系统完全编译
			Thread.sleep(500);
		} catch (InterruptedException e) {
			
		}
		webView.loadUrl("javascript:loadSWF(\"" + url_flash + "\", \"" + 
				(width_flash) + "\", \"" + (height_flash) + "\")");
		webView.loadUrl("javascript:Pause()");
		isPlaying = false;
		button_play.setImageResource(R.drawable.play);
		//重置进度条
		handler.removeCallbacks(runnable_progress);
		progress.setProgress(0);
	}

	
	/**
	 * 请求进度的线程
	 */
	Runnable runnable_progress = new Runnable() {
		public void run() {
			if (webView != null){
				webView.loadUrl("javascript:showcount()");
				handler.postDelayed(runnable_progress, 1000);
//				if (progress.getProgress() == 0){
//					//已经播放完毕动画
//					stopFlash();
//					handler.removeCallbacks(runnable_progress);
//					return;
//				}
			}
		}
	};
	
	
	/**
	 * 与WebView的 JS回调类，负责响应进度，并显示在进度条上
	 * @author Administrator
	 *
	 */
	private final class CallJava{
		public void consoleFlashProgress(float progressSize){
			showFlashProgress(progressSize);
		}
		
		public void goToMarket(){
			handler.post(new Runnable() {
				public void run() {
					Intent installIntent = new Intent("android.intent.action.VIEW");
					installIntent.setData(Uri.parse("market://details?id=com.adobe.flashplayer"));
					((Activity)getContext()).startActivity(installIntent);
				}
			});
		}
	}
	
	
	/**
	 * 函数作用：设置进度条的进度
	 * void
	 * @param progressSize
	 */
	public void showFlashProgress(float progressSize){
		int size = (int)progressSize;
		progress.setProgress(size);
	}
	
	
	/**
	 * 函数作用：初始化视图控件
	 * void
	 */
	private void initView(boolean isControll) { 
		layout_bg = (RelativeLayout)findViewById(R.id.layout_background);
		layout_frame = (RelativeLayout)findViewById(R.id.layout_frame);
		if(isControll){
			layout_controll = (LinearLayout)findViewById(R.id.layout_controll);
			layout_controll.setVisibility(View.VISIBLE);
			button_play = (ImageButton)findViewById(R.id.button_play);
			button_stop = (ImageButton)findViewById(R.id.button_stop);
		}
		webView = (WebView)findViewById(R.id.webview);
		progress = (ProgressBar)findViewById(R.id.progress);
		button_back = (GameView) findViewById(R.id.button_return);
		button_back.addDrawable(R.drawable.back_default, 800);
		button_back.addDrawable(R.drawable.back_hot, 800);
		button_back.play(false);
		button_switch_langdu = (ImageButton)findViewById(R.id.button_switch_langdu);
	}


	
	/**
     * 将flash路径进行编码，flash文件路径示例：/mnt/sdcard/精讲_山行.swf
     * @param filePath：flash文件的路径
     * @return：经过编码的flash文件路径
     * @throws UnsupportedEncodingException
     */
    public static String encodeFlashFilePath(String filePath)  {
    	StringBuffer stringBuffer = new StringBuffer();
    	String nodes[] = filePath.split("/");
    	for (int i=1; i<nodes.length; i++){
    		stringBuffer.append("/");
    		String temp = null;
			try {
				temp = URLEncoder.encode(nodes[i], "utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
    		temp = temp.replace("+", "%20");
    		stringBuffer.append(temp);
    	}
    	return stringBuffer.toString();
    }
    

	public FlashGuankanView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

    
    /*
     * 处理播放进度条的Handler
     */
    Handler handler;
    
    /*
     * FLASH变量
     */
    String url_flash;
    int width_flash;
    int height_flash;
    int width_web;
    int height_web;

    /*
     * 控制变量
     */
    boolean isPlaying;
	
	/*
	 * 视图控件
	 */
	RelativeLayout layout_bg;	//背景布局
	RelativeLayout layout_frame;	//播放框布局
	LinearLayout layout_controll; 	//控制布局 
	WebView webView;	//WebView
	ImageButton button_play;	//播放按钮
	ImageButton button_stop;	//停止按钮
	ProgressBar progress;		//进度条
	GameView button_back;		//返回按钮
	ImageButton button_switch_langdu;	//切换按钮
	
	/*
	 * 需要回收的Bitmap
	 */
	Bitmap bmp_bg ; //背景
	Bitmap bmp_frame ;  //播放框 
	/*
	 * 朗读视图
	 */
	FlashLangduView view_langdu;
	
	/*
	 * 上下文环境 
	 */
	Context context;

	/** 作用：设置朗读视图
	 * 时间：2013-1-3 下午1:33:53
	 * void
	 * @param view_record_record
	 */
	public void setLandDuView(FlashLangduView view_record_record) {
		view_langdu = view_record_record;
	}
}