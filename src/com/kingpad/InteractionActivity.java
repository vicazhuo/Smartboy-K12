package com.kingpad;

import java.util.List;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.RelativeLayout;

import com.views.InterationView;
import com.views.PoemTeachFlashView;

public class InteractionActivity extends Activity{

	private String flashFilePath;				//需要播放的flash文件路径
	private InterationView flash;
	private PackageInfo flashInfo;
	
	private RelativeLayout flashBgLayout;		//控制播放flash的背景
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.interaction_main);
		flash = (InterationView) findViewById(R.id.flash);
        flashBgLayout = (RelativeLayout) findViewById(R.id.flash_bg);
        flashBgLayout.setBackgroundResource(R.drawable.interation_bg);
		
		//检测flash插件是否存在
        PackageManager pm = getPackageManager();
        List<PackageInfo> infoList = pm.getInstalledPackages(PackageManager.GET_SERVICES);  
        for (PackageInfo info : infoList) {
            if ("com.adobe.flashplayer".equals(info.packageName)) {
                flashInfo = info;
            }
        }
        
        if(null != flashInfo){
//        	flashFilePath = "/mnt/sdcard/1.swf";
        	flashFilePath = "file:///android_asset/interation.swf";
        	
            flash.setFlashPath(flashFilePath);
            flash.load();
            
        } else {
        	flash.showError();
        }
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		System.exit(0);
	}
}
