package com.king;

import java.util.ArrayList;

import android.R.integer;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.bean.GameBean;
import com.constant.Constant;
import com.data.GameData;
import com.kingPadStudy.activity.KingPadStudyActivity;
import com.kingPadStudy.adapter.GameListAdapter;
import com.kingPadStudy.tools.FileHandler;
import com.kingPadStudy.views.DialogCreator;
import com.kingpad.KingPadApp;
import com.kingpad.R;
import com.net.LoadData;
import com.net.RequestListener;
import com.net.RequestParameter;

public class GameDownLoad  extends LinearLayout{

	public GameDownLoad(Context context, KingPadApp app) {
		super(context);
		this.context = context;
		this.mApp = app;
		LayoutInflater.from(context).inflate(R.layout.layout_gamedownload, this, true);
		initData();
	}
					

	
	/**
	 * 初始化数据ProgressDialog
	 */
	private void initData() {
		mDialog = DialogCreator.createLoadingDialog(getContext(),"数据加载中...");
		mDialog.show(); 
		RequestParameter parameter = new RequestParameter();
        parameter.add("productNumber", KingPadStudyActivity.getAndroidId());
        parameter.add("pager.pageNumber", "1");
        parameter.add("pager.pageSize", "10");
        LoadData.loadData(Constant.GAME_DATA, parameter, new RequestListener() {
			public void onError(String errMsg) {
				Toast.makeText(getContext(), errMsg, 0).show();
				mDialog.dismiss();
			}
			public void onComplete(Object obj) {
				mDialog.dismiss();
				GameData gameData = (GameData)obj;
//				ArrayList<GameBean> list_games = gameData.getGameList();
//				datas_game = new ArrayList<GameBean>();
//				int size_list = list_games.size();   	
//				for(int j = 0 ; j <size_list;j++){		 
//					GameBean gdata = new GameBean(); 
//					GameBean data = list_games.get(j);
//					gdata.image = data.image;
//					gdata.url = data.url;
//					gdata.title = data.title;
//					gdata.profile = data.profile;
//					gdata.id = data.id;
//					//TODO
//					System.out.println("获取到的资源ID:"+gdata.id);
//					datas_game.add(gdata);
//				}
				list_game = (ListView)findViewById(R.id.listview);
				adapter = new GameListAdapter(context,list_game);
				adapter.addDatas(gameData.getGameList());
				list_game.setAdapter(adapter);
				adapter.notifyDataSetInvalidated();
			}
		});
	}

	/**
	 * ListView
	 */
	private ListView list_game ;
	/**
	 * 数据
	 */
	private ArrayList<GameBean> datas_game;
	private GameListAdapter adapter;
	/**
	 * 当前页面标号
	 */
	private int page = 1;
	/**
	 * 当前页面总数量
	 */
	private int nTotalPageNo = 1;
	/**
	 * 进度条
	 */
	private Dialog mDialog;
	/**
	 * 全局应用变量
	 */
	private KingPadApp mApp;
	/**
	 * 上下文
	 */
	private Context context;
}
