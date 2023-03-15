package com.kingPadStudy.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.TreeMap;


import org.json.JSONException;
import org.json.JSONObject;


import com.bean.MenuData;
import com.kingpad.R;
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
import android.widget.TextView;
import android.widget.Toast;


public class LeftMenuAdapter extends BaseAdapter {
	
	private LayoutInflater inflater;
	private Context context;
	private ArrayList<MenuData> datas= new ArrayList<MenuData>();
	
	public LeftMenuAdapter(Context context){
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
		convertView=inflater.inflate(R.layout.item_leftmenu, null);
		TextView name = (TextView)convertView.findViewById(R.id.text_leftMenu);
		name.setText(data.name);
		ImageView iconImageView = (ImageView)convertView.findViewById(R.id.icon_leftMenu);
        File mfile=new File(data.icon);
        if (mfile.exists()) {//若该文件存在
        	Bitmap bm = BitmapFactory.decodeFile(data.icon);
    		iconImageView.setImageBitmap(bm);
        }
		return convertView;
	}
	
	 

}
