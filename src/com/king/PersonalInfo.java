package com.king;

import java.util.ArrayList;
import java.util.Date;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bean.Client;
import com.bean.Product;
import com.constant.Constant;
import com.data.ClientData;
import com.kingPadStudy.views.DialogCreator;
import com.kingpad.KingPadApp;
import com.kingpad.R;
import com.net.LoadData;
import com.net.RequestListener;
import com.net.RequestParameter;
//import com.server.data.ClientData;
public class PersonalInfo extends LinearLayout {

	private LayoutInflater mInflater;
	private KingPadApp mApp;
	/**
	 * 显示数据视图
	 */
	private TextView mUserNameText, mTelNumberText, mAvaMoneyText, mEmailText, mRegTimeText, 
		mIdText, mAddressText;
	
	private ListView mListView;
	private ProductAdapter mAdapter;
	private ArrayList<Product> mProductList;
	
	public PersonalInfo(Context context, KingPadApp app) {
		super(context);
		
		mInflater = LayoutInflater.from(context);
		mInflater.inflate(R.layout.personal_info, this, true);
		this.mApp = app;
		init();
		initData();
	}
	
	
	private void init() {
		
		mUserNameText = (TextView) findViewById(R.id.textview_username);
		mTelNumberText = (TextView) findViewById(R.id.textview_tel_number);
		mAvaMoneyText = (TextView) findViewById(R.id.textview_ava_money);
		mEmailText = (TextView) findViewById(R.id.textview_email);		
		mRegTimeText = (TextView) findViewById(R.id.textview_register_time);		
		mIdText = (TextView) findViewById(R.id.textview_id);		
		mAddressText = (TextView) findViewById(R.id.textview_address);		
		
		mListView = (ListView) findViewById(R.id.listview);
		mAdapter = new ProductAdapter();
		mListView.setAdapter(mAdapter);
	} // init
	
	
	
	private void initData() {
		final Dialog dlg = DialogCreator.createLoadingDialog(getContext(),"加载数据中...");
		dlg.show();
		RequestParameter parameter = new RequestParameter();
        parameter.add("productNumber", mApp.getProductNumber());
        parameter.add("clientPassword", mApp.getProductPassword());
        LoadData.loadData(Constant.CLIENT_DATA, parameter, new RequestListener() {
			
			public void onError(String errMsg) {
				dlg.dismiss();
				Toast.makeText(getContext(), "数据加载失败", 0).show();
			}
			
			public void onComplete(Object obj) {
				dlg.dismiss();
				ClientData data = (ClientData) obj;
				setData(data.getClient());
				mProductList = data.getProductList();
				mAdapter.notifyDataSetChanged();
			}
		});
	} // initDatas
	
	private void setData(Client client) {
		mUserNameText.setText(client.getClientName());
		mTelNumberText.setText(client.getClientTelnumber());
		mAvaMoneyText.setText(client.getAvailablEmoney() + "元");
		mEmailText.setText(client.getClientEmail());
		Date date = client.getClientCreatedate();
		String time = (date.getYear()+1900) + "-" + (date.getMonth()+1) + "-" + date.getDay();
		mRegTimeText.setText(time);
		mIdText.setText(client.getClientId());
		mAddressText.setText(client.getClientAddress());
	}
	
	
	/**
	 * 适配器内部类
	 * @author lenovo
	 *
	 */
	private final class ProductAdapter extends BaseAdapter {

		public int getCount() {
			if(mProductList != null)
				return mProductList.size();
			else 
				return 0;
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return 0;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			
			final Product product = mProductList.get(position);
			final ViewHolder viewHolder;
			
			if(convertView == null) {
				convertView = mInflater.inflate(R.layout.product_info_item, null);
				viewHolder 	=	new ViewHolder();
				viewHolder.productNumber = (TextView) convertView.findViewById(R.id.textview_product_number);
				viewHolder.productName = (TextView) convertView.findViewById(R.id.textview_product_name);
				viewHolder.isActivate = (TextView) convertView.findViewById(R.id.textview_product_is_active);
				viewHolder.productStartDate = (TextView) convertView.findViewById(R.id.textview_start_date);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			String text = null;
			text = "产品名称：" + product.getProductName();
			viewHolder.productName.setText(text);
			
			text = " 序 列 号 ：" + product.getProductNumber();
			viewHolder.productNumber.setText(text);
			
			if(product.getProductIsactivate() == 1) {
				text = "产品激活：已激活";
			}
			else {
				text = "产品激活：未激活";
			}
			viewHolder.isActivate.setText(text);
			Date date = product.getProductStarDate();
			text = "购买日期：" + (date.getYear()+1900) + "-" + (date.getMonth()+1) + "-" + date.getDay();
			viewHolder.productStartDate.setText(text);
			return convertView;
		}
		
	} // ProductAdapter

	static class ViewHolder {
		public TextView productName;				// 标题
		public TextView productNumber;				// 产品序列号
		public TextView productStartDate;			// 电脑信息
		public TextView isActivate;					// 是否激活
	}
}
