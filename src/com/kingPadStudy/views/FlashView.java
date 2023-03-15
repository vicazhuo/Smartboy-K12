package com.kingPadStudy.views;


import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
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


@SuppressLint("NewApi")
public class FlashView extends RelativeLayout{

	/**
	 * 函数作用：开始工作
	 * void
	 */
	public void setStart(int index,String url,String background_frame,
			int w_web, int h_web,
			int x,int y,int w,int h,boolean isController,boolean isFrontHidden,
			int top_front,int right_front,boolean isWebViewHandle){
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
		this.top_front = top_front;
		this.right_front = right_front;
		this.isFrontHidden  = isFrontHidden;
		//初始化WebView
		initWebView(x,y,isController,isWebViewHandle);
	}
	
	/**
	 * 函数作用：初始化WebView
	 * void
	 */
	private void initWebView(int x,int y,boolean isControll,boolean isWebViewHandle ) { 
		//设置WebView宽高和位置
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				width_web , height_web      
        ); 
		params.leftMargin = x;
		params.topMargin = y; 
		webView.setLayoutParams(params);  
		//设置WebView基本属性
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setBackgroundColor(0);  	//背景透明
		webView.getSettings().setAllowFileAccess(true);	//支持文件操作
		webView.getSettings().setPluginState(PluginState.ON);	//支持插件
		webView.addJavascriptInterface(new CallJava(), "CallJava");		//设置JS交互接口
		webView.setFocusableInTouchMode(false);
		webView.requestFocusFromTouch();
		webView.setOnDragListener(new OnDragListener() {
			@Override
			public boolean onDrag(View v, DragEvent event) {
				// TODO Auto-generated method stub
				//System.out.println("调用WebView的onDrag。。。。。。。。。。。。");
				return false;
			}
		});
		webView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//System.out.println("调用WebView的onClick....");
			}
		});
		webView.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				//System.out.println("调用WebView的onFocusChange....");
			}
		});
		//设置 webChromeClient 属性
		setWebChrome();
//		webView.requestFocusFromTouch(); 	//支持获取手势焦点
//		webView.setFocusableInTouchMode(true);
//		WebView.OnFocusChangeListener mFocusChangeListener = new WebView.OnFocusChangeListener(){  
//		    public void onFocusChange(View v, boolean hasFocus) {  
//		        Log.i("OnFocusChangeListener", "调用onFocusChange。。。");  
//		          if (hasFocus) {  
//		        	  //System.out.println("hasFocus = true");
//		                   // 如果是touchmode就执行click，否则就会只是选中。   
//		              if (v.isInTouchMode()){  
//			        	  //System.out.println("isInTouchMode = true");
//		                  ((WebView)v).performClick();  
//		              }   
//		          } else {  
//		        	  //System.out.println("hasFocus=false");
//		          }  
//		          
//		    }  
//		};
		webView.setOnCustomScroolChangeListener(new ScrollInterface(){             
			 @Override             
			 public void onSChanged(int l, int t, int oldl, int oldt) {   
				 if(touchState == 1){ 
					 //System.out.println("onSChanged。。。 touchState =1。。webView.getScrollY()="+webView.getScrollY());
					 //只有当UP的时候，才对webview的滑动位置进行调整
					 Message msg = new Message();
					 msg.what = 2;
					 handler_webvie.sendMessage(msg);
				 }
			 }                  
		}); 
		
		
		webView.setOnTouchListener ( new View .OnTouchListener () {
	   		 @Override 
	   		 public boolean onTouch ( final View v , MotionEvent event ) {
			        Log.i("onTouch", "调用onTouch。。。event . getAction ()="+event . getAction ());
		             switch ( event . getAction ()) { 
		             	case MotionEvent.ACTION_DOWN :
		             		touchState = 0;
		             		y_down = webView.getScrollY();
		             		//System.out.println("y_down="+y_down);
			   		         return false ; 
		             	case MotionEvent.ACTION_HOVER_MOVE:
		             		touchState = 2;
		             		Log.i("onTouch", "MotionEvent.ACTION_HOVER_MOVE");
			   		        return true;
		                 case MotionEvent . ACTION_UP :
		             		 y_up = webView.getScrollY();
			             		//System.out.println("y_up="+y_up);
		             		 if(y_down == y_up){
				             		//System.out.println("y_down == y_up");
				             		touchState = 1; 
			   		                thread_touchWebView =  new Thread(new Runnable() {
										@Override
										public void run() {
											Message msg = new Message();
											msg.what = 1;
											handler_webvie.sendMessage(msg);
										}
									});
			   		                thread_touchWebView.start();
		             		 }
			   		        return false ; 
		             }
	   		         return false ; 
	   		 }
   	   });
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
        	try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        	pauseFlash();
        }else{
        	/*
        	 * 设置遮挡
        	 */
        	if(isFrontHidden){
	        	try {
					Thread.sleep(2200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
        		image_front = (ImageView)findViewById(R.id.book_front);
        		image_front.setVisibility(View.VISIBLE);
        		if(top_front != -1){
	        		//设置WebView宽高和位置
	                RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(
	                		145 , 110      
	                ); 
	                //System.out.println("右边的距离："+right_front);
	                params2.leftMargin = right_front;
	                params2.topMargin = top_front; 
	        		image_front.setLayoutParams(params2);  
        		}
        		image_front.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						//System.out.println("点击了遮挡视图。。。。");
					}
				});
        	}
        	try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    		webView.setFocusableInTouchMode(false);
    		webView.requestFocusFromTouch();
        	
        }
	}

	
	

	/** 作用：
	 * 时间：2013-1-25 上午11:06:49
	 * void
	 */
	private void setWebChrome() {
			//设置 WebChromeClient 和 WebViewClient
			webView.setWebChromeClient(new WebChromeClient() {
			/** 函数作用：
			 * @param view
			 * @param newProgress 
			 */
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
		        //消除加载条
		        KingPadStudyActivity.dismissWaitDialog();
				super.onProgressChanged(view, newProgress);
			}
			
			/** 函数作用：
			 * @param view
			 * @param title 
			 */
			@Override
			public void onReceivedTitle(WebView view, String title) {
				Log.i("webChrome", "调用webChrome的onReceivedTitle。。。。。。。。。");
				super.onReceivedTitle(view, title);
			}
		});		
	}

	/**
	 * 函数作用：装载FLASH 
	 * void
	 * @param b 是否直接播放
	 */
	private void loadFlash() {
		//装载SWF，并直接播放f
		//System.out.println("在loadFlash中。height_flash="+height_flash);
        String url_script = "javascript:loadSWF(\"" + url_flash + "\", \"" + 
        		width_flash + "\", \"" + height_flash + "\")";
        webView.loadUrl(url_script);	
		webView.setBackgroundResource(R.drawable.transparent);
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
				webView.loadUrl("javascript:Pause()");
				//重置进度条
				handler.removeCallbacks(runnable_progress);
				webView.destroy();
				ViewController.setIsFrontHidden(false);
				ViewController.setFrontPosition(-1, -1);
				ViewController.setIsWebViewHandle(false);
				ViewController.setControllerPositionY(-1);
				if(thread_touchWebView != null && !thread_touchWebView.isInterrupted()){
					thread_touchWebView.interrupt();
				}
				//点击返回
				ViewController.backToNormal_from_relative();
				if (bmp_bg !=null && !bmp_bg.isRecycled()) {
					bmp_bg.recycle();
				}
				if (bmp_frame !=null && !bmp_frame.isRecycled()) {
					bmp_frame.recycle();
				}
			}
		}
	}


	/**
	 * 函数作用：播放Flash
	 * void
	 */
	public void playFlash() {
		//System.out.println("播放Flash");
		//调用JS开启FLASH
		webView.loadUrl("javascript:Play()");
		//启动进度条线程
		handler.post(runnable_progress);
		webView.setBackgroundResource(R.drawable.transparent);
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
			//设置控制条的位置
			//System.out.println("y_controller="+y_controller);
			if(y_controller != -1){//设置WebView宽高和位置
		        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
		        		RelativeLayout.LayoutParams.MATCH_PARENT , RelativeLayout.LayoutParams.MATCH_PARENT 
		        ); 
				params.setMargins(300, 570, 300, y_controller);
				layout_controll.setLayoutParams(params);  
			}
			button_play = (ImageButton)findViewById(R.id.button_play);
			button_stop = (ImageButton)findViewById(R.id.button_stop);
		}
		webView = (MyWebView)findViewById(R.id.webview);
		progress = (ProgressBar)findViewById(R.id.progress);
		button_back = (GameView) findViewById(R.id.button_return);
		button_back.addDrawable(R.drawable.back_default, 800);
		button_back.addDrawable(R.drawable.back_hot, 800);
		button_back.play(false);
	}



	/**
	 * 构造函数：
	 * @param context
	 * @param attrs
	 */
	public FlashView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
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
    
    /*
     * 处理播放进度条的Handler
     */
    Handler handler; 
    
    boolean isManifestScroll = false;
    
    /*
     * 处理WebView的Handler
     */
    Handler handler_webvie = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			if(msg.what == 1){
				//System.out.println("msg.what == 1 掉用requestFocusFromTouch。。。。");
				super.handleMessage(msg);
				webView.requestFocusFromTouch();
			}else if(msg.what == 2){
				//滑动回原来的位置
				//System.out.println("msg.what == 2  ");
				//System.out.println("isManifestScroll="+isManifestScroll);
				if(!isManifestScroll){
					//System.out.println("setScrollY - 40");
					webView.setScrollY(webView.getScrollY() -40 );
				} 
				isManifestScroll = !isManifestScroll;
			}
			
//			webView.scrollTo(webView.getScrollX(), webView.getScrollY()-40);
		}
    };
    
    /*
     * FLASH变量
     */
    String url_flash;
    int width_flash;
    int height_flash;
    int width_web;
    int height_web;
	private int top_front;
	private int right_front;

    /*
     * 控制变量
     */
    boolean isPlaying;
	private boolean isFrontHidden = false;
	/*
	 * 视图控件
	 */
	RelativeLayout layout_bg;	//背景布局
	RelativeLayout layout_frame;	//播放框布局
	LinearLayout layout_controll; 	//控制布局 
	MyWebView webView;	//WebView
	ImageView image_front ; //用于较色扮演进行遮挡 的视图
	ImageButton button_play;	//播放按钮
	ImageButton button_stop;	//停止按钮
	ProgressBar progress;		//进度条
	GameView button_back;		//返回按钮
	
	/*
	 * 需要回收的Bitmap
	 */
	Bitmap bmp_bg ; //背景
	Bitmap bmp_frame ;  //播放框 
	/*
	 * 
	 */
	protected Thread thread_touchWebView;
	/*
	 * 是否结束视图
	 */
	private boolean isOverView = false;
	
	/*
	 * 屏幕点击事件的分类	0-down 1-up 2-move
	 */
	private int touchState =  -1;	
	private int y_down = -1;	//手指按下的Y坐标
	private int y_up = -1;		//手指上抬的Y坐标			
	private int y_controller = -1;
	
	/*
	 * 上下文环境 
	 */
	Context context;

	/** 作用：设置控制条的Y坐标
	 * 时间：2013-1-28 下午3:47:41
	 * void
	 * @param positionY
	 */
	public void setControllerPositionY(int positionY) {
		// TODO Auto-generated method stub
		y_controller = positionY;
	}
}