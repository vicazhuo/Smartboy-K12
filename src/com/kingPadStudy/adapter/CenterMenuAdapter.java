package com.kingPadStudy.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.TreeMap;


import org.json.JSONException;
import org.json.JSONObject;


import com.bean.MenuData;
import com.kingpad.KingPadActivity;
import com.kingpad.R;import com.kingPadStudy.activity.KingPadStudyActivity;
import com.kingPadStudy.controller.ViewController;
import com.kingPadStudy.resource.ResourceLoader;
import com.utils.DatabaseAdapter;
import com.views.GameView;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class CenterMenuAdapter extends BaseAdapter {
	
	private LayoutInflater inflater;
	private Context context;
	private ArrayList<MenuData> datas= new ArrayList<MenuData>();
	
//	class OnSectionClickListener implements OnClickListener{
//		@Override
//		public void onClick(View v) {
//			if(v == name){
//				//点击了名字
//			}else if(v == ){
//		}
//	}
	
	
	public CenterMenuAdapter(Context context){
		this.context=context;
		inflater=LayoutInflater.from(context);
		
	}
	
	public void clearData(){
		this.datas.clear();
		notifyDataSetChanged();
	}
	
	public void addData(MenuData data){
		this.datas.add(data);
		notifyDataSetChanged();
	}
	
	public void addDatas(ArrayList<MenuData> datas){
		this.datas.addAll(datas);
		notifyDataSetChanged();
	}

	
	public int getCount() {
		return datas.size();
	}

	
	public Object getItem(int arg0) {
		return datas.get(arg0);
	}

	
	public long getItemId(int arg0) {
		return arg0;
	}

	
	public View getView(final int position, View convertView, ViewGroup parent) {
		final MenuData data=datas.get(position);
		convertView=inflater.inflate(R.layout.item_centermenu, null);
		name = (TextView)convertView.findViewById(R.id.text_centerMenu);
		name.setText(data.name);
		iconImageView = (ImageView)convertView.findViewById(R.id.icon_centerMenu);
        File mfile=new File(data.icon);
        if (mfile.exists()) {//若该文件存在
        	Bitmap bm = BitmapFactory.decodeFile(data.icon);
    		iconImageView.setImageBitmap(bm);
        }
        name.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//点击了名字
				ViewController.studyState = 0;
				playResource(position);
			}
		});
        iconImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//点击了图标
				ViewController.studyState = 0;
				playResource(position);
			}
		});
        
        if(data.isLight){
        	image_light = (GameView)convertView.findViewById(R.id.light_centerMenu);
        	image_light.setVisibility(View.VISIBLE);
        	switch(data.state_light){
	        	case 1:
	            	image_light.addDrawable(R.drawable.light_green1, 500);
	            	image_light.addDrawable(R.drawable.light_green2, 500);
	            	image_light.play(false);
	        		break;
	        	case 2:
	            	image_light.addDrawable(R.drawable.light_yellow1, 500);
	            	image_light.addDrawable(R.drawable.light_yellow2, 500);
	            	image_light.play(false);
	        		break;
	        	case 3:
	            	image_light.addDrawable(R.drawable.light_red1, 500);
	            	image_light.addDrawable(R.drawable.light_red2, 500);
	            	image_light.play(false);
	        		break;
        	}
        }
        if(data.isReview){
        	LinearLayout layout_review = (LinearLayout)convertView.findViewById(R.id.layout_review);
        	layout_review.setVisibility(View.VISIBLE);
        	final int isImport = data.state_important;
        	final int isLessImport = data.state_secendImportant;
        	final int isRestart = data.state_restart;
        	view_important = (ImageView)convertView.findViewById(R.id.button_important);
        	view_lessimportant = (ImageView)convertView.findViewById(R.id.button_lessimportant);
        	view_restart = (ImageView)convertView.findViewById(R.id.button_restart);
        	view_restart.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(isRestart == 1){
						//当点击了重新学习
						//删除DB，并启动学习
						DatabaseAdapter.getInstance(context).deleteOnePathRecord(data.path);
						//播放资源
						ViewController.studyState = 0;
						playResource(position);
					}
				}
			});
        	view_important.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(isImport == 1){
						//播放资源
						ViewController.studyState = 1;
						playResource(position);
					}
				}
			});
        	view_lessimportant.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(isLessImport == 1){
						//播放资源
						ViewController.studyState = 2;
						playResource(position);
					}
				}
			});
        	if(isImport == 1){
        		view_important.setImageResource(R.drawable.button_important);
        	}else{
        		view_important.setImageResource(R.drawable.button_important2);
        	}
        	if(isLessImport == 1){
        		view_lessimportant.setImageResource(R.drawable.button_lessimportant);
        	}else{
        		view_lessimportant.setImageResource(R.drawable.button_lessimportant2);
        	}
        	if(isRestart == 1){
        		view_restart.setImageResource(R.drawable.button_restart);
        	}else{
        		view_restart.setImageResource(R.drawable.button_restart2);
        	}
        } 
        convertView.postInvalidate();
		return convertView;
	}
	
	
	/**
	 * 函数作用：播放对应的资源
	 * void
	 * @param position
	 */
	protected void playResource(int position) {
		if(ResourceLoader.isLeafNode()){
			KingPadStudyActivity.showWaitDialog();  
			//根据类型来加载学习视图 
			ResourceLoader.loadResource(position);
		}else{
			ResourceLoader.touchCenterMenu(position);
		}
//		System.out.println("在playResource中.index_path[0]="+ResourceLoader.index_path[0]);
	}

	private TextView name;
	private ImageView iconImageView;
	private GameView image_light;
	private ImageView view_important ;
	private ImageView view_lessimportant ;
	private ImageView view_restart;
}
