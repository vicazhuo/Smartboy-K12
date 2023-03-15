package com.kingpad;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.RelativeLayout;

import com.bean.CommonModeBean;
import com.constant.Constant;
import com.data.MetaData;
import com.kingPadStudy.activity.KingPadStudyActivity;
import com.kingPadStudy.controller.ViewController;
import com.utils.FileManager;
import com.net.LoadData;
import com.net.RequestListener;
import com.net.RequestParameter;
import com.utils.Util;
import com.views.GameView;

/**
 * 学前准备
 * @author swerit
 *
 */
public class PrintEngine implements StudyViewHandle{

	private WebView mWebView;
	private RelativeLayout controlPart;		//用来设置背景
	
	
	private View mainView;
	private InitCompleted initCompleted;	//界面初始化之后调用的接口
	private Context context;
	private LayoutInflater mInflater;
	private GameView mBackView;				//返回键的动画
	
	
	public PrintEngine(Context context, String xmlFilePath, String bgPath, InitCompleted initCompleted){
		this.context = context;
		this.initCompleted = initCompleted;
		this.mInflater = LayoutInflater.from(context);
		Util.initSoundPool(context);
		mainView = mInflater.inflate(R.layout.prepare, null);
		File mfile=new File(bgPath);
        if (mfile.exists()) {//若该文件存在
    		bmp_bg = BitmapFactory.decodeFile(bgPath);
    		mainView.setBackgroundDrawable(new BitmapDrawable(bmp_bg));
        }
		initViews(mainView);
		initAnimation();
		setListener();
		loadData(xmlFilePath);
	}
	
	private void setListener() {
		Listener listener = new Listener();
		mBackView.setOnClickListener(listener);
	}

	private void initAnimation() {
		mBackView.addDrawable(R.drawable.back_default, 1000);
		mBackView.addDrawable(R.drawable.back_hot, 1000);
		mBackView.play(false);
	}

	private void initViews(View mainView){
		mWebView			= (WebView) mainView.findViewById(R.id.webview_prepare);
		controlPart			= (RelativeLayout) mainView.findViewById(R.id.control_part);
		mBackView			= (GameView) mainView.findViewById(R.id.button_back);
	}
	
	
	
	private void loadData(final String xmlFilePath){
		RequestParameter parameter = new RequestParameter();
        parameter.add("type", "Print");
        parameter.add("mode", "");
        parameter.add("path", xmlFilePath);
		
        LoadData.loadData(Constant.META_DATA, parameter, new RequestListener() {
			@Override
			public void onComplete(Object obj) {
				MetaData data = (MetaData) obj;
				ArrayList<CommonModeBean> list = data.getList().get(0);
				CommonModeBean bean = list.get(0);
				System.out.println("在PRINT中。。HTML文件为："+bean.file);
				fillView(bean.file, data.getBg());
				initCompleted.doNext();
				KingPadStudyActivity.dismissWaitDialog();
			}

			@Override
			public void onError(String error) {
			}
        });
	}
	
	private void fillView(String url, String path){
		String urlPath = "";
		
		String [] parts = url.split("/");
		String name = parts[parts.length-1];
		name = name.replace(" ", "");
		
		for (int i=0; i<parts.length-1; i++){
			urlPath += ("/" + parts[i]);
		}
		urlPath += ("/" + name);
		
		urlPath = "file://" + urlPath;
		
		mWebView.loadUrl(urlPath);
		File mfile=new File(path);
        if (mfile.exists()) {//若该文件存在
    		bmp_bg_small = BitmapFactory.decodeFile(path);
    		controlPart.setBackgroundDrawable(new BitmapDrawable(bmp_bg_small));
        }
	}
	
	class Listener implements OnClickListener{
		@Override
		public void onClick(View v) {
			if (v == mBackView){
				Util.playSoundPool(context, com.kingPadStudy.constant.Constant.AUDIO_MARK_BUTTON);
				//TODO
				ViewController.backToNormal_print();
				if (!bmp_bg.isRecycled()) {
					bmp_bg.recycle();
				}
				if (!bmp_bg_small.isRecycled()) {
					bmp_bg_small.recycle();
				}
			}
		}
	}

	@Override
	public View getGameView() {
		return mainView;
	}
	
	private Bitmap bmp_bg = null;
	private Bitmap bmp_bg_small = null;
}
