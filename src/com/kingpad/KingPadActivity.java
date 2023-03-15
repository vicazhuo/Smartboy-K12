package com.kingpad;

import android.app.Activity;
import android.os.Bundle;

import com.constant.Constant;
import com.data.BookData;
import com.data.MetaUtilData;
import com.meta.impl.MetaUtil;
import com.net.LoadData;
import com.net.RequestListener;
import com.net.RequestParameter;

public class KingPadActivity extends Activity {
	
	private String mParentPath = "/mnt/sdcard/kingpad/";
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //解析index记录
        RequestParameter parameter = new RequestParameter();
        parameter.add("type", "ObjectiveQuiz"); 
        parameter.add("path", "/mnt/sdcard/Chinese/人教版_2a/同步学习/第一单元/黄山奇石/筛查记忆/字音"); 
        LoadData.loadData(Constant.META_UTIL_DATA, parameter, new RequestListener() {
			public void onError(String error) {
			}
			public void onComplete(Object obj) {
//				BookData data = (BookData) obj;
//				data.printData();
				
				MetaUtilData data = (MetaUtilData) obj;
				//data.printData();
				MetaUtil.printData(data.getData());
			}
       });
        
    }
}
