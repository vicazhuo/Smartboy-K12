package com.king;

import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.adapter.CatalogAdapter;
import com.constant.Constant;
import com.database.Database;
import com.kingpad.KingPadApp;
import com.kingpad.R;
public class MainActivity extends Activity {
	private KingPadApp mApp;
	/**
	 * 目录结构的listview对象
	 */
	private ListView mListView;
	/**
	 * adapter对象
	 */
	private CatalogAdapter mAdapter;
	/**
	 * 容器
	 */
	private LinearLayout mContainer;
	/**
	 * View对象的hashmap
	 */
	private HashMap<String, View> mHashMap;
	
	
	/**
	 * 充值记录关键字
	 */
	public final static int PAY_LOG = 4;
	public final static int RESOURCE = 3;
	public final static int DOWNLOAD_LOGS = 2;

	public static Integer sDownloading = new Integer(0);
	
	private TextView mTextProgress;

	public static Database db = null;
	
	public static Database getDatabase(){
		if(db == null){
			db = new Database();
		}
		return db;
	}
	
	
	
	/**
	 * 界面的初始化操作
	 */
	private void init() {
		mHashMap = new HashMap<String, View>();
		mContainer = (LinearLayout) findViewById(R.id.container);
		mTextProgress = (TextView) findViewById(R.id.progress);
		// 加载listview控件
		mListView = (ListView)findViewById(R.id.listview_catalog);
		int iconArray[] = {R.drawable.btn1,R.drawable.btn1,R.drawable.btn1,
							R.drawable.btn1,R.drawable.btn1, R.drawable.btn1};
		String textArray[] = {"选择学习资源", "个人资料", "下载记录", "下载资源", "充值记录", "修改密码"};
		// 加载目录数据
		mAdapter = new CatalogAdapter(this, iconArray, textArray);
		mListView.setAdapter(mAdapter);
	}
	
	private void setListener() {
		mListView.setOnItemClickListener(new ListView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				selectCatalog(position);
			}
		});
	}
	
	private void selectCatalog(final int index) {
		View view = null;
		mContainer.removeAllViews();
		if(index == 0)
		{
			//makeText("选择学习资源");
			view = mHashMap.get("select_teach");
			if(view == null) {
				view = new SelectTeach(this);
				mHashMap.put("select_teach", view);
				mContainer.addView(view);
			}
			else {
				mContainer.addView(view);
			}
			
		} 
		else if(index == 1) 
		{
			view = mHashMap.get("personal_detail");
			if(view == null) {
				view = new PersonalInfo(this, mApp);
				mHashMap.put("personal_detail", view);
			}
			mContainer.addView(view);
		} 
		else if(index == DOWNLOAD_LOGS) {
			view = mHashMap.get("download_logs");
			if(view == null) {
				view = new DownloadLogsView(this, mApp);
				mHashMap.put("download_logs", view);
			}
			mContainer.addView(view);
		}
		else if(index == RESOURCE) 
		{
			view = mHashMap.get("resource_view");
			if(view == null) {
				view = new ResourceView(mApp, this);
				mHashMap.put("resource_view", view);
				mContainer.addView(view);
			} else {
				mContainer.addView(view);
			}
		}
		else if(index == PAY_LOG) 
		{
			view = mHashMap.get("pay_log");
			if(view == null) {
				view = new PayLogView(this, mApp);
				mHashMap.put("pay_log", view);
				mContainer.addView(view);
			} else {
				mContainer.addView(view);
			}
		}
		else if(index == 5)
		{
			view = mHashMap.get("update_pwd");
			if(view == null) {
				view = new UpdatePwd(this);
				mHashMap.put("update_pwd", view);
				mContainer.addView(view);
			}
			else {
				mContainer.addView(view);
			}
		}
	}
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApp = (KingPadApp) getApplication();
        //TODO 没有激活则登陆    
        // 保存用户配置文件 
		SharedPreferences sp = getSharedPreferences(Constant.LOCAL_DATA, Activity.MODE_WORLD_READABLE);
		Editor editor = sp.edit();
		String productNumber = sp.getString("productNumber", "");
		if(productNumber == null || "".equals(productNumber)) {
			Activate activate = new Activate(this, mApp,this);
			setContentView(activate);
		}
		else {
			Login login = new Login(this, mApp);
			setContentView(login);
		}
    }
    
    public void showMain() {
      setContentView(R.layout.main);
      init();
      setListener();
      // 初始化数据默认显示选择学习资源目录
      selectCatalog(0);
    }
    
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK)
		{
			showQuitDialog();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	
	private void showQuitDialog() {
		Dialog dlg = new AlertDialog.Builder(MainActivity.this)
		.setTitle("提示")
		.setMessage("是否退出程序")
		.setNeutralButton("是", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		})
		.setNegativeButton("否", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) { }
			
		}).create();
		dlg.show();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		android.os.Process.killProcess(android.os.Process.myPid());
	}
	
	public void setProgressText() {
		if(sDownloading <= 0) {
			mTextProgress.setText("");
		} else {
			mTextProgress.setText("当前正在下载" + sDownloading + "个资源");
		}
	}
}
