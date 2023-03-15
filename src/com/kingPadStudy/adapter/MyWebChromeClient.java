package com.kingPadStudy.adapter;


import com.kingPadStudy.activity.KingPadStudyActivity;
import com.kingpad.KingPadActivity;

import android.webkit.WebChromeClient;
import android.webkit.WebView;

class MyWebChromeClient extends WebChromeClient {
	
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
        	super.onProgressChanged(view, newProgress);
//        	System.out.println("onProgressChanged...");
        	KingPadStudyActivity.dismissWaitDialog();
        }
        
        
        
        
        
}