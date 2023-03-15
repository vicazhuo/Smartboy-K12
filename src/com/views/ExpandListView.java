package com.views;
 
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
/**
 * 扩展listview，用于显示资源目录
 * @author <a href="mailto:udmjh0508@126.com">莫建华</a>
 * @created 2013-1-12
 */
public class ExpandListView extends android.widget.LinearLayout {

	private android.widget.BaseAdapter mAdapter;
	
	public ExpandListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public ExpandListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ExpandListView(Context context) {
		super(context);
	}

	private void fillView() {
		int count = mAdapter.getCount();
		for(int i = 0; i < count; i++) {
			View view = mAdapter.getView(i, null, null);
			
			addView(view);
//			view.setVisibility(View.GONE);
		}
	}
	
	public android.widget.BaseAdapter getAdapter() {
		return mAdapter;
	}
	
	public void setAdapter(android.widget.BaseAdapter adapter) {
		this.mAdapter = adapter;
		
		// 加载view对象
		fillView();
	}
}
