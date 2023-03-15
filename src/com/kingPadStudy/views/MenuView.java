/** 文件作用：菜单视图类
 *	创建时间：2012-11-17 下午7:47:24
 *  作者： 陈相伯 
 *  描述：
 */
package com.kingPadStudy.views;


import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bean.ContentStateBean;
import com.bean.MenuData;
import com.kingPadStudy.activity.KingPadStudyActivity;
import com.kingPadStudy.adapter.CenterMenuAdapter;
import com.kingPadStudy.adapter.LeftMenuAdapter;
import com.kingPadStudy.adapter.PathAdapter;
import com.kingPadStudy.constant.Constant;
import com.kingPadStudy.controller.ViewController;
import com.kingPadStudy.resource.ResourceLoader;
import com.kingpad.R;
import com.utils.DatabaseAdapter;

/** 描述：菜单视图类，所有的科目的菜单界面都在此类中显示。
 */
public class MenuView  extends RelativeLayout {
	private ListView list_leftMenu = null;
	private ListView list_centerMenu = null;
	private LeftMenuAdapter adapter_left = null;
	private CenterMenuAdapter adapter_center = null;
	private Context context = null;
	private ArrayList<MenuData> datas_menuleft;
	private ArrayList<MenuData> datas_menucenter;
	private HorizontialListView list_path = null;
	private PathAdapter adapter_path = null;
	private ArrayList<String> datas_path = null;
	
	
	/** 
	 * @param context
	 */
	public MenuView(Context context) {
		super(context);
		this.context = context;
	}
	
	/** 
	 * @param context
	 */
	public MenuView(Context context,AttributeSet attt) {
		super(context,attt);
		this.context = context;
	}
	
	/**
	 * 函数作用：设置内容
	 * void
	 */
	public void setContent(int index){
		while(true){
			if(ResourceLoader.bigIndex !=null){
				break;
			}
		}
		this.index = index;
		setBackground(index);
		//首先初始化数据
		setData();
		//左边菜单列表
		list_leftMenu=(ListView)findViewById(R.id.listview_leftMenu);
		adapter_left=new LeftMenuAdapter(context);
		adapter_left.addDatas(datas_menuleft);
		list_leftMenu.setAdapter(adapter_left);
		list_leftMenu.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//当不为叶子节点的时候进行响应
				handleLeftMenuTouch(position);
			}
		});
		//中间菜单列表
		list_centerMenu=(ListView)findViewById(R.id.listview_centerMenu);
		adapter_center=new CenterMenuAdapter(context);
		adapter_center.addDatas(datas_menucenter);
		list_centerMenu.setAdapter(adapter_center);
		list_centerMenu.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				handleLCenterMenuTouch(position);
			}
		});
		//上方列表
		list_path = (HorizontialListView)findViewById(R.id.listview_path);
		adapter_path = new PathAdapter(context);
		adapter_path.addDatas(datas_path);
		adapter_path.setMenuView(this);
		list_path.setAdapter(adapter_path);
		list_path.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				TextView textView = (TextView)view.findViewById(R.id.text_path);
				textView.setTextColor(Color.RED);
				text_path_current = textView;
				handlePathMenuTouch(position);
			}
		});
        ImageView button_home =  (ImageView) findViewById(R.id.button_home);
        button_home.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				KingPadStudyActivity.showWaitDialog();
				ViewController.controllView(Constant.VIEW_MAIN);
			}
		});
		//设置ResourceLoader的视图实例
		ResourceLoader.setMenuView(this);
		KingPadStudyActivity.dismissWaitDialog();
	}
	
	
	
	/**
	 * 函数作用：设置显示内容
	 * void
	 */
	public void setShow(){
		adapter_left.clearData();
		adapter_left.addDatas(datas_menuleft);

		adapter_center.clearData();
		adapter_center.addDatas(datas_menucenter);

		adapter_path.clearData();
		adapter_path.addDatas(datas_path);
		postInvalidate();
		//System.out.println("postInvalidate");
	}
	
	
	/**
	 * 函数作用：处理左边菜单点击事件
	 * void
	 * @param position
	 */
	private void handleLeftMenuTouch(int position) {
		if(ResourceLoader.getIndex_path()[0] == 2 ){
			this.index = position;
			setBackground(index);
			ViewController.setIndex(index);
		}
		ResourceLoader.touchLeftMenu(position);
	}
	
	/**
	 * 函数作用：处理中间菜单点击事件
	 * void
	 * @param position
	 */
	private void handleLCenterMenuTouch(int position) {
		if(ResourceLoader.isLeafNode()){
			KingPadStudyActivity.showWaitDialog();
			//根据类型来加载学习视图 
			playStudyViewByType(position);
		}else{
			ResourceLoader.touchCenterMenu(position);
		}
	} 
	
	
	/**
	 * 函数作用：根据类型来加载学习视图
	 * void
	 */
	private void playStudyViewByType(int position){
		ViewController.studyState = 0;
		ResourceLoader.loadResource(position);
	}
	
	

	/**
	 * 函数作用：处理上方路径点击事件
	 * void
	 * @param position
	 */
	public void handlePathMenuTouch(int position) {
		if(position == 0){
			KingPadStudyActivity.showWaitDialog();
			ViewController.controllView(Constant.VIWE_MAJOR);
			return ;
		}
		ResourceLoader.touchPathMenu(position+1);
	}
	
	/**
	 * 函数作用：设置背景
	 * void
	 * @param index2
	 */
	private void setBackground(int index2) {
		String file_imageString = ResourceLoader.getMajorBackImageUrl(index2);
		//System.out.println("setBackground中。 file_imageString="+file_imageString);
        File mfile=new File(file_imageString);
        if (mfile.exists()){ 
        	//若该文件存在
        	Bitmap bm = BitmapFactory.decodeFile(file_imageString);
    		setBackgroundDrawable(new BitmapDrawable(bm));
    		postInvalidate();
        }
	}
	
	
	
	/**
	 * 函数作用：数据初始化
	 * void
	 */
	public void setData() {
		datas_menuleft = new ArrayList<MenuData>();
		datas_menucenter = new ArrayList<MenuData>();
		datas_path = new ArrayList<String>();
		//左边菜单
		ArrayList<String> icon_menu_left = ResourceLoader.getIcon_menu_left();
		ArrayList<String> name_menu_left = ResourceLoader.getName_menu_left();
		int size = icon_menu_left.size();
		for(int i=0;i<size;i++){
			//循环计入
			MenuData data = new MenuData();
			data.icon = icon_menu_left.get(i);
			data.name = name_menu_left.get(i);
			datas_menuleft.add(data);
		}
		//中间菜单
		ArrayList<String> icon_menu_center = ResourceLoader.getIcon_menu_center();
		ArrayList<String> name_menu_center = ResourceLoader.getName_menu_center();
		int size_center = name_menu_center.size();
		for(int i=0;i<size_center;i++){
			//循环计入
			MenuData data = new MenuData();
			data.icon = icon_menu_center.get(i);
			data.name = name_menu_center.get(i);
			//System.out.println("ResourceLoader.isLastLevel="+ResourceLoader.isLastLevel);
			if(ResourceLoader.isLastLevel){
				//如果是最后一级目录
				com.bean.CatalogBean bean = ResourceLoader.getLeafBean(i); 
				// 获取路径和mode
				String path = bean.path;
				String mode = bean.mode;
				data.path = path;
				//System.out.println("路径：" + path);
				//System.out.println("mode：" + mode);
				int state_restart = DatabaseAdapter.getInstance(context).getLishtState(path);
				if(mode == null){
					//没有mode属性
				}else{
					if(mode.equals("study")){
						//有复习模式的
						data.isReview = true;
						//获取三个按钮的状态
						ContentStateBean bean_Content = com.utils.DatabaseAdapter.getInstance(context).getContentState(path);
						//System.out.println("bean_Content="+bean_Content);
						if(bean_Content != null){
							//当数据库中有记录时
							data.state_important = bean_Content.importantState;
							data.state_secendImportant = bean_Content.secondState;
							data.state_restart = bean_Content.restartState;
							//System.out.println("data.state_restart="+data.state_restart);
							//获取灯的状态
							data.state_light = bean_Content.lightState;
						}else{
							data.state_important = 0;
							data.state_secendImportant = 0;
							data.state_restart = 0;
							//System.out.println("data.state_restart=0");
							//获取灯的状态
							data.state_light = 0;
						}
						if(data.state_light == 0){
							data.isLight = false;
						}else{
							data.isLight = true;
						}
					}else{
						//只有灯
						//获取三个按钮的状态
						ContentStateBean bean_Content = com.utils.DatabaseAdapter.getInstance(context).getContentState(path);
						//System.out.println("bean_Content="+bean_Content);
						if(bean_Content != null){
							//当数据库中有记录时
							//获取灯的状态
							data.state_light = bean_Content.lightState;
						}else{
							//获取灯的状态
							data.state_light = 0;
						}
						if(data.state_light == 0){
							data.isLight = false;
						}else{
							data.isLight = true;
						}
					}
				}
				//判断 mode是否为study
			}
			//TODO 设置是否复习属性
			datas_menucenter.add(data);
		}
		ResourceLoader.isLastLevel = false;
		//上方路径
		int[] index_path = ResourceLoader.getIndex_path();
		String[] string_path = ResourceLoader.getString_path();
		for(int i=1;i<=index_path[0];i++){
			if(i==index_path[0]){
				datas_path.add(string_path[i]);
			}else{
				datas_path.add(string_path[i]+" >> ");
			}
		}
	}

	/**
	 * 构造函数：2
	 * @param context
	 */
	public MenuView(Context context,int index) {
		super(context);
		this.index = index;
		this.context = context;
	}
	
	public void setBackGroundImage(){
		switch(index){
			case 0:
				break;
		}
	}
	
	private TextView text_path_current = null;
	private int index =0 ;

}
