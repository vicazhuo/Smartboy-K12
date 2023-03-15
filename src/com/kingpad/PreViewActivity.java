package com.kingpad;

import com.constant.Constant;
import com.data.MetaData;
import com.net.LoadData;
import com.net.RequestListener;
import com.net.RequestParameter;

import android.app.Activity;
import android.os.Bundle;

/**
 * 课前预习界面
 * @author swerit
 *
 */
public class PreViewActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		loadData();
	}
	
	private void loadData(){
		//暂时没有解析数据的方法
		RequestParameter parameter = new RequestParameter();
        parameter.add("type", "Chinese/Memorization");
        parameter.add("mode", "");
        parameter.add("path", "/mnt/sdcard/KingPad/data/Chinese/人教版_2a/同步学习/第一单元/识字1/超前学习/课前预习");
        
        LoadData.loadData(Constant.META_DATA, parameter, new RequestListener() {
			@Override
			public void onError(String error) {
				
			}
			
			@Override
			public void onComplete(Object obj) {
				MetaData data = (MetaData) obj;
//				data.printData();d
			}
		});
	}
}
