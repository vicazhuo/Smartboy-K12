package com.kingPadStudy.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.webkit.WebView;

public class MyWebView extends WebView { 
	ScrollInterface mt; 
	   
	public void setOnCustomScroolChangeListener(ScrollInterface scrollInterface){         
		this.mt=scrollInterface;     
	} 
	
	
	public MyWebView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public MyWebView(Context context,AttributeSet set) {
		super(context,set);
	}
	
	 @Override     
	 protected void onScrollChanged(int l, int t, int oldl, int oldt) {         
		 super.onScrollChanged(l, t, oldl, oldt); 
		 //System.out.println("调用webview 的onScrollChanged... ");
		 mt.onSChanged(l, t, oldl, oldt);     
	 }  
	
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		//System.out.println("MyWebView的onMeasure。。。。。。。。。。。。。");
        invalidate();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
	
	protected void shouldOverrideKeyEvent(WebView view, KeyEvent event) {
		//System.out.println("MyWebView的shouldOverrideKeyEvent。。。。。。。。。。。。。");
//        invalidate();
    }


	protected void onScaleChanged(WebView view, float oldScale, float newScale) {
		//System.out.println("MyWebView的onScaleChanged。。。。。。。。。。。。。");
//        invalidate();
    }
	
}
