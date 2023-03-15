package com.adapter;
 
import com.kingpad.R; 
 
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * 个人中心的目录结构
 * @author lenovo
 *
 */
public class CatalogAdapter extends BaseAdapter {

	private int[] icons;
	private String[] titles;
	private int count = 0;
	private LayoutInflater mInflater;
	
	public CatalogAdapter(Context context, int icons[], String text[]) {
		this.icons = icons;
		count = icons.length;
		this.titles = text;
		mInflater = LayoutInflater.from(context);
	}
	
	public int getCount() {
		return count;
	}

	public Object getItem(int position) {
		return titles[position];
	}

	public long getItemId(int position) {
		return 0;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		if(convertView == null) {
			convertView = mInflater.inflate(R.layout.catalog_item, null);
			viewHolder = new ViewHolder();
			viewHolder.icon = 	(ImageView) convertView.findViewById(R.id.imageview_catalog);
			viewHolder.text	=	(TextView) convertView.findViewById(R.id.textview_text);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.icon.setBackgroundResource(icons[position]);
		viewHolder.text.setText(titles[position]);
		return convertView;
	}


	static class ViewHolder{
		public ImageView icon;		// 目录图标
		public TextView text;		// 目录文本数据
	}
}
