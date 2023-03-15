
package com.views; 
 
import com.kingpad.R;       

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory; 
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

public class PoemTeachFlashView extends RelativeLayout {
	
	private String flashPath;
	private WebView flash_view;
	private ProgressBar play_progress;
	private ImageButton play;
	private ImageButton stop;
	private int width;					//屏幕宽度，横屏的话就是屏幕的高度
	private int height;
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
		width = ((Activity)context).getWindowManager().getDefaultDisplay().getWidth();
		height = ((Activity)context).getWindowManager().getDefaultDisplay().getHeight();
		bottom_height = BitmapFactory.decodeResource(getResources(), R.drawable.play).getHeight();
		density = ((Activity)getContext()).getResources().getDisplayMetrics().density;
		
		//------
		width = dip2px(context, 460);
		height = dip2px(context, 225);
		
		// 加载播放flash控件
		initWebView();
		
		// 实时更新进度
		handler = new Handler();
	}
	
	private void initWebView(){
		flash_view = (WebView) findViewById(R.id.flash_web_view_7488);
		flash_view.getSettings().setJavaScriptEnabled(true); 
		flash_view.getSettings().setPluginState(PluginState.ON);
		flash_view.setWebChromeClient(new WebChromeClient()); 
		flash_view.getSettings().setAllowFileAccess(true);
		flash_view.getSettings().setPluginsEnabled(true);
		flash_view.getSettings().setSupportZoom(true);
		flash_view.getSettings().setAppCacheEnabled(true);
		flash_view.setWebChromeClient(new MyWebChromeClient());
		flash_view.addJavascriptInterface(new CallJava(), "CallJava");
//		flash_view.getLayoutParams().height = height - bottom_height;          //控制显示flash动画界面的高度
		flash_view.getLayoutParams().height = height;          //控制显示flash动画界面的高度
		flash_view.loadUrl("file:///android_asset/flash_player.html");
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
	public void load(){
		try {
			//等待html文件被完全解析
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		flash_view.loadUrl("javascript:loadSWF(\"" + flashPath + "\", \"" + 
				(width/density) + "\", \"" + ((height)/density) + "\")");
		
//		String url = "javascript:loadSWF(\"" + flashPath + "\", \"" + (width/density) + "\", \"" + ((height)/density) + "\")";
		
//		flash_view.loadDataWithBaseURL(null, url,  "text/html", "utf-8", null);
		
		flash_view.loadUrl("javascript:Pause()");
		handler.post(update_progress);
	}
	
	public void start(){
		if(null != flashPath){
			play.setImageResource(R.drawable.pause);
			
			flash_view.loadUrl("javascript:Play()");
			handler.post(update_progress);
			playing = true;
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
		play.setImageResource(R.drawable.play);
	}
	
	
	public void showError(){
		flash_view.loadUrl("javascript:error()");
	}

	
	public void setFlashPath(String flashPath) {
		this.flashPath = flashPath;
	}
	
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
	
	public void showFlashProgress(float progressSize){
		int size = (int)progressSize;
		play_progress.setProgress(size);
	}
	
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
}
