package com.kingPadStudy.views;


import com.kingpad.R;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.widget.RelativeLayout;


public class InterationView extends RelativeLayout {
	
	private String flashPath;
	private WebView flash_view;
	
	private int width;					//屏幕宽度，横屏的话就是屏幕的高度
	private int height;
	private boolean playing = false;
	private float density;
	
	private Context context;
	

	//构造方法，必有
	public InterationView(Context context, AttributeSet attrs) {
		super(context, attrs);
		onCreate();
	}
	
	//构造方法
	public InterationView(Context context) {
		super(context);
		onCreate();
	}

	//初始化界面
	public void onCreate(){
		context = getContext();
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.flash_view, InterationView.this);
		 
		//获取屏幕的宽和高
		width = ((Activity)context).getWindowManager().getDefaultDisplay().getWidth();
		height = ((Activity)context).getWindowManager().getDefaultDisplay().getHeight();
		density = ((Activity)getContext()).getResources().getDisplayMetrics().density;
		
		//------
		width = dip2px(context, 800);
		height = dip2px(context, 1200);
		
		// 加载播放flash控件
		initWebView();
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
//		flash_view.getLayoutParams().height = height;          //控制显示flash动画界面的高度
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
	}
	
	public void showError(){
		flash_view.loadUrl("javascript:error()");
	}

	public void setFlashPath(String flashPath) {
		this.flashPath = flashPath;
	}
	
	private int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}
}

