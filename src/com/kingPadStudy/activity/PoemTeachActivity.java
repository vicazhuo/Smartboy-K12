package com.kingPadStudy.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import com.kingPadStudy.constant.Constant;
import com.kingPadStudy.controller.ViewController;
import com.kingPadStudy.resource.ResourceLoader;
import com.kingPadStudy.security.Security;
import com.kingPadStudy.views.PoemTeachFlashView;


import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * 古诗精讲
 * @author swerit
 *
 */
public class PoemTeachActivity extends Activity {
	
	private String flashFilePath;				//需要播放的flash文件路径
	private PoemTeachFlashView flash;
	private PackageInfo flashInfo;
	private long mExitTime ;
	
	private ProgressBar play_progress;
	private ImageButton play;
	private ImageButton stop;
	
	private RelativeLayout flashBgLayout;		//控制播放flash的背景
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		//设置屏幕属性
		setScreenAttribute();
		setScreenScale();
		ViewController.intiView(this, this);
		int x  = 160;
		int y = 134;
		int h = 1000;
		int w = 740;
		ResourceLoader.loadMetaData("/mnt/sdcard/kingpad/data/Chinese/package/必背古诗/二级/学习/黄鹤楼送孟浩然之广陵/互动笔记","InteractiveFlash", null);
//        setContentView(R.layout.poem_teach_flash_main);
//        flash = (PoemTeachFlashView) findViewById(R.id.flash);
//        play_progress = (ProgressBar) findViewById(R.id.flash_play_progress_7491);
//        play = (ImageButton) findViewById(R.id.flash_button_play_7495);
//        stop = (ImageButton) findViewById(R.id.flash_button_stop_7494);
//        flash.setStopButton(stop);
//        flash.setPlayButton(play);
//        flash.setProgressBar(play_progress);
//        flashBgLayout = (RelativeLayout) findViewById(R.id.flash_bg);
//        flashBgLayout.setBackgroundResource(R.drawable.s1);
//        //检测flash插件是否存在
//        PackageManager pm = getPackageManager();
//        List<PackageInfo> infoList = pm.getInstalledPackages(PackageManager.GET_SERVICES);  
//        for (PackageInfo info : infoList) {
//            if ("com.adobe.flashplayer".equals(info.packageName)) {
//                flashInfo = info;
//            }
//        }
//        if(null != flashInfo){
////        	flashFilePath = "file:///android_asset/poem_teach.swf";
////        	flashFilePath = "/mnt/sdcard/kingpad/data/Chinese/package/必背古诗/二级/学习/望庐山瀑布/古诗精讲/精讲_望庐山瀑布.swf";
//        	flashFilePath = "/mnt/sdcard/hh.swf";  
//            flash.setFlashPath(flashFilePath);
//            flash.load();
//        } else {
//        	flash.showError();
//        }
    }
    

	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {			
		if (keyCode == KeyEvent.KEYCODE_BACK) {
	        if ((System.currentTimeMillis() - mExitTime) > 2000) {
	                Object mHelperUtils;							
	                Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
	                mExitTime = System.currentTimeMillis();			
	        } else {													
	    		ViewController.stopView();												
	            finish();
	    		System.exit(0);
	        }
	        return true;
	    }
        return super.onKeyDown(keyCode, event);
    }

	/** 作用：初始化
	 * 时间：20122012-11-17下午10:08:37
	 * void
	 */
	private void init() {
		//设置屏幕属性
		setScreenAttribute();
		setScreenScale();
		//初始化视图控制器
		ViewController.intiView(this, this);
		//检测是否激活
		if(Security.isActivated(this)){
			//当已经激活时，设置登陆视图
			ViewController.controllView(Constant.VIEW_LOGIN);
		}else{
			//当还未激活时，设置的激活视图
			ViewController.controllView(Constant.VIEW_ACTIVATE);
		}
	}
	
	
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
	
	
	/** 作用：设置屏幕属性
	 * 时间：20122012-11-18下午2:41:21
	 * void
	 */
	private void setScreenAttribute() {
		//设置全屏
        setTheme(android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        		WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}
    
    
    
    @Override
	protected void onDestroy() {
		super.onDestroy();
		System.exit(0);
	}
}