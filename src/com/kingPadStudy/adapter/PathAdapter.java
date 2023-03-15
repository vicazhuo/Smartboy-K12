package com.kingPadStudy.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.TreeMap;


import org.json.JSONException;
import org.json.JSONObject;



import com.bean.MenuData;
import com.kingpad.R;import com.kingPadStudy.views.MenuView;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;


public class PathAdapter extends BaseAdapter {
	
	private LayoutInflater inflater;
	private Context context;
	private ArrayList<String> datas= new ArrayList<String>();
	
	public PathAdapter(Context context){
		this.context=context;
		inflater=LayoutInflater.from(context);
	}
	
	public void clearData(){
		this.datas.clear();
		notifyDataSetChanged();
	}
	
	public void addData(String data){
		this.datas.add(data);
		notifyDataSetChanged();
	}
	
	public void addDatas(ArrayList<String> datas){
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
		final String data = datas.get(position);
		convertView=inflater.inflate(R.layout.item_path, null);
		final TextView name = (TextView)convertView.findViewById(R.id.text_path);
		name.setText(data); 
//		System.out.println("position="+position+",size="+datas.size());
		if(position ==datas.size()-1){
			name.setTextColor(Color.RED);
		}
		return convertView;
	}

	/**
	 * 函数作用：设置菜单视图s
	 * void
	 * @param menuView
	 */
	public void setMenuView(MenuView menuView) {
		// TODO Auto-generated method stub
		this.menuView = menuView;
	}
	
	 private MenuView menuView ;

}
