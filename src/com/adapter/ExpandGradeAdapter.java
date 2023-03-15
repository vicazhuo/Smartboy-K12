package com.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bean.Grade;
import com.kingpad.R;
import com.views.ExpandListView;
/**
 * 列表资源目录中的年级目录adapter
 * @author <a href="mailto:udmjh0508@126.com">莫建华</a>
 * @created 2013-1-12
 */
public class ExpandGradeAdapter extends BaseAdapter {
	
	private ArrayList<Grade> mList;
	private Context mContext;
	private LayoutInflater mInflater;
	/**
	 * 那个学科，用于解压数据时的根目录
	 */
	private String course;
	private String courseId;
	
	public ExpandGradeAdapter(Context context, ArrayList<Grade> list, final String course, final String courseId) {
		this.mContext = context;
		mInflater = LayoutInflater.from(mContext);
		if(list == null)
			mList = new ArrayList<Grade>();
		else
			mList = list;
		this.course = course;
		this.courseId = courseId;
	}
	
	public int getCount() {
		return mList.size();
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		
		final ViewHolder viewHolder;
		final Grade grade = mList.get(position);
		
		viewHolder = new ViewHolder();
		convertView = mInflater.inflate(R.layout.expand_grade_item, null);
		viewHolder.imageView = (ImageView) convertView.findViewById(R.id.imageview_grade_item);
		viewHolder.textView = (TextView) convertView.findViewById(R.id.textview_grade_item);
		viewHolder.expandView = (ExpandListView) convertView.findViewById(R.id.expandlistview_grade_item);
		
		// 初始化一年级目录下的数据
		viewHolder.expandView.setAdapter(new ResourceAdapter(mContext, grade.getbResource(),
				 course, courseId, grade.getGradeName(), grade.getGradeId()));
			
		Log.e("tag", "当前年级名：" + grade.getGradeName());
		
		viewHolder.textView.setText(grade.getGradeName());
		viewHolder.textView.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				if(viewHolder.expandView.getVisibility() == View.GONE) {
					viewHolder.expandView.setVisibility(View.VISIBLE);
					viewHolder.imageView.setBackgroundResource(R.drawable.tree_down);
				} else {
					viewHolder.expandView.setVisibility(View.GONE);
					viewHolder.imageView.setBackgroundResource(R.drawable.tree_close);
				}
				
			}
		});
		viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				if(viewHolder.expandView.getVisibility() == View.GONE) {
					viewHolder.expandView.setVisibility(View.VISIBLE);
					viewHolder.imageView.setBackgroundResource(R.drawable.tree_down);
				} else {
					viewHolder.expandView.setVisibility(View.GONE);
					viewHolder.imageView.setBackgroundResource(R.drawable.tree_close);
				}
			}
		});
		
		return convertView;
	}
	
	static class ViewHolder {
		ImageView imageView;
		TextView textView;
		ExpandListView expandView;
	}

}
