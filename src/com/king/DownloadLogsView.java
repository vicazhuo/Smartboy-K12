package com.king;
import java.util.ArrayList;
import java.util.Date;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bean.DownloadLogs;
import com.constant.Constant;
import com.data.DownloadLogData;
import com.kingPadStudy.views.DialogCreator;
import com.kingpad.KingPadApp;
import com.kingpad.R;
import com.net.LoadData;
import com.net.RequestListener;
import com.net.RequestParameter;
//import com.server.data.DownloadLogData;

public final class DownloadLogsView extends LinearLayout {

	private KingPadApp mApp;
	private LayoutInflater mInflater;
	private Dialog mDialog;
	private int nPageNo = 1;
	private int nTotalPageNo = 1;
	private boolean bBottom = false;
	
	private ListView mListView;
	private ArrayList<DownloadLogs> mList = new ArrayList<DownloadLogs>();
	private DownloadLogAdapter mAdapter;
	
	public DownloadLogsView(Context context, KingPadApp app) {
		super(context);
		this.mApp = app;
		mInflater = LayoutInflater.from(context);
		mInflater.inflate(R.layout.download_logs, this, true);
		init();
	}
	
	private void init() {
		mListView = (ListView) findViewById(R.id.listview_from);
		mAdapter = new DownloadLogAdapter();
		mListView.setAdapter(mAdapter);
		loadData(nPageNo);
		mListView.setOnScrollListener(new ListView.OnScrollListener() {
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if(view.getLastVisiblePosition() != view.getCount() - 1)
					return;
				if(mDialog.isShowing())
					return;
				if(bBottom == false)
				{
					nPageNo++;
					loadData(nPageNo);
				}
			}
			
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				
			}
		});
	} // init
	
	private void loadData(final int page) {
		if(page > nTotalPageNo) {
        	Toast.makeText(getContext(), "数据已完全加载", 0).show();
        	return;
        }
		mDialog = DialogCreator.createLoadingDialog(getContext(),"加载数据中...");mDialog.show();
		RequestParameter parameter = new RequestParameter();
        parameter.add("productNumber", mApp.getProductNumber());
        parameter.add("clientPassword", mApp.getProductPassword());
        parameter.add("pager.pageNumber", page + "");
		LoadData.loadData(Constant.DOWNLOAD_LOGS_DATA, parameter, new RequestListener() {
			public void onError(String errMsg) {
				mDialog.dismiss();
				Toast.makeText(getContext(), errMsg, 0).show();
			}
			public void onComplete(Object obj) {
				mDialog.dismiss(); 
				DownloadLogData data = (DownloadLogData) obj;
				nTotalPageNo = data.getPageNumber();
				ArrayList<DownloadLogs> list = data.getDownloadLogsList();
				if(list != null) {
					mList.addAll(list);
					mAdapter.notifyDataSetChanged();
				}
			}
		});
	}
	
	private class DownloadLogAdapter extends BaseAdapter {

		public int getCount() {
			return mList == null ? 0 : mList.size();
		}

		public Object getItem(int arg0) {
			return null;
		}

		public long getItemId(int arg0) {
			return 0;
		}

		public View getView(int position, View convertView, ViewGroup arg2) {
			final ViewHolder viewHolder;
			final com.bean.DownloadLogs logs = mList.get(position);
			
			if(convertView == null) {
				convertView = mInflater.inflate(R.layout.download_logs_item, null);
				viewHolder = new ViewHolder();
				viewHolder.resourceName	=	(TextView) convertView.findViewById(R.id.buy_name);
				viewHolder.buyDate 	=	(TextView) convertView.findViewById(R.id.buy_date);
				viewHolder.resourcePrice	=	(TextView) convertView.findViewById(R.id.resource_price);
				viewHolder.buyPrice	=	(TextView) convertView.findViewById(R.id.buy_money);
				convertView.setTag(viewHolder);
			}
			else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			Date date = logs.getDownloadDate();
			Log.e("time", date.toGMTString());
			String text = (date.getYear()+1900) + "-" + (date.getMonth()+1) + "-" + date.getDate();
			viewHolder.buyDate.setText("购买日期：" + text);
			viewHolder.resourceName.setText("产品价格：" + logs.getResourcePrice() + "元");
			viewHolder.resourceName.setText("资源名称：" + logs.getCourseName() + "-" + logs.getResourceName());
			
			viewHolder.buyPrice.setText("购买金额：" + logs.getDownloadCost() + "元");
			
			return convertView;
		}
		
	}

	static class ViewHolder {
		TextView resourceName;			//	资源名
		TextView buyDate;				// 购买日期
		TextView resourcePrice;			// 资源价格
		TextView buyPrice;				// 购买价格
	}
}
