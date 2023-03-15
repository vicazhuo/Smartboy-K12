package com.kingPadStudy.views;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.kingPadStudy.controller.ViewController;
import com.kingpad.R;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class PoemTeachFlashView extends RelativeLayout {
	private String flashPath;
	private MyWebView flash_view;
	private ProgressBar play_progress;
	private ImageButton play;
	private ImageButton stop;
	private int width = 705;				//屏幕宽度，横屏的话就是屏幕的高度
	private int height = 1000;
	private int bottom_height;			//底部操作栏的高度
	private boolean playing = false;
	private float density;
	private Context context;
	private boolean isPlayingEnd = false;	//flash是否播放结束，false-没有结束
	private Handler handler;			//flash进度条控制刷新

	//构造方法，必有
	public PoemTeachFlashView(Context context, AttributeSet attrs) {
		super(context, attrs);
		onCreate();
	}
	
	//构造方法
	public PoemTeachFlashView(Context context) {
		super(context);
		onCreate();
	}

	//初始化界面
	public void onCreate(){
		context = getContext();
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.flash_view, PoemTeachFlashView.this);
		//获取屏幕的宽和高
		bottom_height = BitmapFactory.decodeResource(getResources(), R.drawable.play).getHeight();
		density = ((Activity)getContext()).getResources().getDisplayMetrics().density;
		// 加载播放flash控件
		initWebView();
		// 实时更新进度
		handler = new Handler();
	}
	
	
	private void initWebView(){
		flash_view = (MyWebView) findViewById(R.id.flash_web_view_7488);
		flash_view.setWebChromeClient(new MyWebChromeClient());
		flash_view.setBackgroundColor(0);  
		flash_view.getSettings().setJavaScriptEnabled(true); 
		flash_view.getSettings().setPluginState(PluginState.ON);
		flash_view.setWebChromeClient(new WebChromeClient()); 
		flash_view.getSettings().setAllowFileAccess(true);
		flash_view.getSettings().setPluginsEnabled(true);
		flash_view.getSettings().setSupportZoom(false);
		flash_view.getSettings().setAppCacheEnabled(true);
		flash_view.setWebChromeClient(new MyWebChromeClient());
		flash_view.addJavascriptInterface(new CallJava(), "CallJava");
		//TODO
		flash_view.getLayoutParams().height = height;          //控制显示flash动画界面的高度
		String url_flash = "file:///android_asset/flash_player.html";
		flash_view.loadUrl(url_flash);
		Toast.makeText(context, "设置底部HTML:"+url_flash, 0).show();
		postInvalidate();
	}
	
	class MyWebChromeClient extends WebChromeClient {
        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            Log.d("WebViewDemo", message);   
            
            result.confirm();
            
            return true;
        }
    }
	
	/**
	 * 加载指定路径的swf文件
	 */
	public void load(LinearLayout controll){
		setLayout_controll(controll);
		try {
			//等待html文件被完全解析
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//System.out.println("装载swf.............");
		String url_script = "javascript:loadSWF(\"" + flashPath + "\", \"" + 
				width + "\", \"" + height + "\")";
		Toast.makeText(context, "装载swf，url_script=  "+url_script, 0).show();
		flash_view.loadUrl(url_script);
		flash_view.loadUrl("javascript:Pause()");
		handler.post(update_progress);
	}
	

	
	/**
	 * 加载指定路径的swf文件
	 */
	public void loadRecord(){
		try {
			//等待html文件被完全解析
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		flash_view.loadUrl("javascript:loadSWF(\"" + flashPath + "\", \"" + 
				width + "\", \"" + height + "\")");
		flash_view.loadUrl("javascript:Pause()");
		handler.post(update_progress);
		//System.out.println("loadRecord..时flashPath="+flashPath);
	}
	
	
	
	public void start(){
		if(null != flashPath){
			play.setImageResource(R.drawable.pause);
			try{
				flash_view.loadUrl("javascript:Play()");
				handler.post(update_progress);
				Toast.makeText(context, "loadUrl('javascript:Play()')", 0).show();
				playing = true;
			}catch(Exception exception){
				Toast.makeText(context, "播放失败，exception="+exception.getMessage(), 0).show();
			}
		}
	}
	
	public void start_record(Thread thread){
		thread_progress = thread;
		if(null != flashPath){
			//System.out.println("在录音的开始按钮中，不换图片。。");
			flash_view.loadUrl("javascript:Play()");
			handler.post(update_progress);
			playing = true;
			new Thread(new StopThread()).start();
		}
	}
	
	public class StopThread implements Runnable{
		@Override
		public void run() {
			try {
				Thread.sleep(2500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			pause_record();
			ViewController.isRecording = true;
		}
	}
	
	
	
	public void pause(){
		if(null != flashPath){
			flash_view.loadUrl("javascript:Pause()");
			handler.removeCallbacks(update_progress);
			play.setImageResource(R.drawable.play);
			playing = false;
		}
	}
	
	
	public void pause_record(){
		if(null != flashPath){
			flash_view.loadUrl("javascript:Pause()");
			handler.removeCallbacks(update_progress);
			playing = false;
		}
	}
	
	
	
	
	
	
	
	/**
	 * 停止播放，数据复原
	 */
	public void stop(){
		flash_view.loadUrl("file:///android_asset/flash_player.html");
		try {
			//暂停500ms让之前加载的html文件被系统完全编译
			Thread.sleep(500);
		} catch (InterruptedException e) {
			
		}
		flash_view.loadUrl("javascript:loadSWF(\"" + flashPath + "\", \"" + 
				(width/density) + "\", \"" + ((height)/density) + "\")");
		flash_view.loadUrl("javascript:Pause()");
		play_progress.setProgress(0);
		playing = false;
		if(play != null){
			play.setImageResource(R.drawable.play);
		}
	}
	
	public void showError(){
		flash_view.loadUrl("javascript:error()");
	}

//	public String getFlashPath() {
//		return flashPath;
//	}
	
	public void setFlashPath(String flashPath) {
		this.flashPath = flashPath;
		//System.out.println("setFlashPath="+this.flashPath);
	}
	
	
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
		play_progress.setProgress(size);
	}
	
	
	/**
	 * 请求进度的线程
	 */
	Runnable update_progress = new Runnable() {
		public void run() {
			if (flash_view != null){
				flash_view.loadUrl("javascript:showcount()");
				/*if (play_progress.getProgress() == 0){
					//已经播放完毕动画
					stop();
					handler.removeCallbacks(update_progress);
					return;
				}*/
				handler.postDelayed(update_progress, 1000);
			}
		}
	};
	
	private int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}
	
	//---------------------------------------------------------
	public void setPlayButton(ImageButton play){
		this.play = play;
		this.play.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(!playing){
					start();
				} else {
					pause();
				}
			}
		});
	}

	public void destory(){
		this.handler.removeCallbacks(update_progress);
		this.flash_view.destroy();
	}
	
	public void setStopButton(ImageButton stop){
		this.stop = stop;
		this.stop.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				stop();
			}
		});
	}
	public void setProgressBar(ProgressBar bar){
		this.play_progress = bar;
		play_progress.getLayoutParams().width = width / 3;
	}

	/*
	 * 设置宽度
	 */
	public void setWidth(int w) {
		width = w;
	}

	/*
	 * 设置高度
	 */
	public void setHeight(int h) {
		height = h;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas); 
		//System.out.println("调用poemView的onDraw..");
		Paint paint = new Paint(); 
		// 将边框设为黑色. 
		paint.setColor(android.graphics.Color.BLACK); 
		// 画TextView的4个边. 
		canvas.drawLine(0, 0, this.getWidth() - 1, 0, paint); 
		canvas.drawLine(0, 0, 0, this.getHeight() - 1, paint); 
		canvas.drawLine(this.getWidth() - 1, 0, this.getWidth() - 1, this.getHeight() - 1, paint); 
		canvas.drawLine(0, this.getHeight() - 1, this.getWidth() - 1, this.getHeight() - 1, paint); 
	}
	
	/*
	 * 控制控件的布局
	 */
	private LinearLayout layout_controll ;

	public LinearLayout getLayout_controll() {
		return layout_controll;
	}

	public void setLayout_controll(LinearLayout layout_controll) {
		this.layout_controll = layout_controll;
	}
	
	/*
	 * 录音进度条开关线程
	 */
	private Thread thread_progress;
	
	
}
