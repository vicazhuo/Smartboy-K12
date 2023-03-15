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

import com.bean.PayLogs;
import com.constant.Constant;
import com.data.PayLogData;
import com.kingPadStudy.views.DialogCreator;
import com.kingpad.KingPadApp;
import com.kingpad.R;
import com.net.LoadData;
import com.net.RequestListener;
import com.net.RequestParameter;
//import com.server.data.PayLogData;
/**
 * 充值记录界面
 * @author <a href="mailto:udmjh0508@126.com">莫建华</a>
 * @created 2013-1-12
 */
public class PayLogView extends LinearLayout {

	private KingPadApp mApp;
	private LayoutInflater mInflater;
	private ListView mListView;
	private PayAdapter mAdapter;
	private Dialog dlg;
	private boolean bBottom = false;
	/**
	 * 保存当前页面
	 */
	private int nPageNo = 1;
	
	private int nTotalPageNo = 1;
	
	private ArrayList<PayLogs> mPayList = new ArrayList<PayLogs>();
	
	public PayLogView(Context context, KingPadApp app) {
		super(context);
		
		this.mApp = app;
		
		mInflater = LayoutInflater.from(context);
		mInflater.inflate(R.layout.pay_logs, this, true);
		init();
	}
	
	private void init() {
		mListView = (ListView) findViewById(R.id.listview_from);
		mAdapter = new PayAdapter();
		
		mListView.setAdapter(mAdapter);
		loadData(nPageNo);
		
		mListView.setOnScrollListener(new ListView.OnScrollListener() {
			
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if(view.getLastVisiblePosition() != view.getCount() - 1)
					return;
				if(dlg.isShowing())
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
	}
	
	/**
	 * 
	 * @param page
	 */
	private void loadData(final int page) {
		if(page > nTotalPageNo) {
        	Toast.makeText(getContext(), "数据已完全加载", 0).show();
        	return;
        }
		
		dlg   = DialogCreator.createLoadingDialog(getContext(),"数据加载中...");
		dlg.show();
		RequestParameter parameter = new RequestParameter();
        parameter.add("productNumber", mApp.getProductNumber());
        Log.e("k", " mApp.getProductPassword()="+ mApp.getProductPassword());
        parameter.add("clientPassword", mApp.getProductPassword());
        parameter.add("pager.pageNumber", page + "");
        
		LoadData.loadData(Constant.PAY_LOG_DATA, parameter, new RequestListener() {
			
			public void onError(String errMsg) {
				dlg.dismiss();
				Toast.makeText(getContext(), "数据加载失败", 0).show();
			}
			
			public void onComplete(Object obj) {
				dlg.dismiss();
				
				PayLogData data = (PayLogData) obj;
				nTotalPageNo = data.getTotalPage();
				ArrayList<PayLogs> list = data.getList();
				if(list != null) {
					mPayList.addAll(list);
					mAdapter.notifyDataSetChanged();
				}
			}
		});
	}
	
	
	private class PayAdapter extends BaseAdapter {
		
		public int getCount() {
			return mPayList == null ? 0 : mPayList.size();
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder viewHolder;
			final PayLogs pay = mPayList.get(position);
			
			if(convertView == null) {
				convertView = mInflater.inflate(R.layout.pay_logs_item, null);
				viewHolder = new ViewHolder();
				viewHolder.payDate	=	(TextView) convertView.findViewById(R.id.textview_pay_date);
				viewHolder.payUser 	=	(TextView) convertView.findViewById(R.id.textview_pay_user);
				viewHolder.payType	=	(TextView) convertView.findViewById(R.id.textview_pay_type);
				viewHolder.payMoney	=	(TextView) convertView.findViewById(R.id.textview_pay_money);
				convertView.setTag(viewHolder);
			}
			else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			Date date = pay.getPayDate();
			String text = (date.getYear()+1900) + "-" + (date.getMonth()+1) + "-" + date.getDay();
			viewHolder.payDate.setText(text);
			viewHolder.payMoney.setText(pay.getPayMoney() + "");
			viewHolder.payType.setText(pay.getPayType());
			viewHolder.payUser.setText(pay.getPayUser());
			
			return convertView;
		}
	} // PayAdapter
	
	static class ViewHolder {
		TextView payDate;			//	消费日期
		TextView payUser;			// 经手人
		TextView payType;			// 消费方式
		TextView payMoney;			// 消费金额
	}
}
