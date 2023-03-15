package com.king;
 
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adapter.ExpandGradeAdapter;
import com.constant.Constant;
import com.data.ResourceData;
import com.kingPadStudy.activity.KingPadStudyActivity;
import com.kingPadStudy.views.DialogCreator;
import com.kingpad.KingPadActivity;
import com.kingpad.KingPadApp;
import com.kingpad.R;
import com.net.LoadData;
import com.net.RequestListener;
import com.net.RequestParameter;
//import com.server.data.ResourceData;
import com.views.ExpandListView;
public class ResourceView extends LinearLayout {
	
	private KingPadApp mApp;
	private LayoutInflater mInflater;
	
//	private LinearLayout mChineseLayout, mMathLayout, mEnglishLayout;
	private ExpandListView mChineseView, mMathView, mEnglishView;
	private ImageView mChineseImageView, mMathImageView, mEnglishImageView;
	private TextView mChinese, mMath, mEnglish;
	
	public ResourceView(KingPadApp app, Context context) {
		super(context);
		this.mApp = app;
		mInflater = LayoutInflater.from(context);
		mInflater.inflate(R.layout.study_resource, this, true);
		init();
		initData();
	}
	
	private void init() {
		mChinese = (TextView) findViewById(R.id.textview_chinese);
		mMath = (TextView) findViewById(R.id.textview_math);
		mEnglish = (TextView) findViewById(R.id.textview_english);
		mChineseView = (ExpandListView) findViewById(R.id.expandlistview_chinese);
		mMathView = (ExpandListView) findViewById(R.id.expandlistview_math);
		mEnglishView = (ExpandListView) findViewById(R.id.expandlistview_english);
		mChineseImageView = (ImageView) findViewById(R.id.imageview_chinese);
		mMathImageView = (ImageView) findViewById(R.id.imageview_math);
		mEnglishImageView = (ImageView) findViewById(R.id.imageview_english);
		mChinese.setOnClickListener(listener);
		mMath.setOnClickListener(listener);
		mEnglish.setOnClickListener(listener);
		mChineseImageView.setOnClickListener(listener);
		mMathImageView.setOnClickListener(listener);
		mEnglishImageView.setOnClickListener(listener);
	} // init
	
	private void initData() {
		final Dialog dlg  = DialogCreator.createLoadingDialog(getContext(),"数据加载中...");
		dlg.show();
		RequestParameter parameter = new RequestParameter();
		parameter.add("productNumber", KingPadStudyActivity.getAndroidId());
//		System.out.println("ProductNumber="+mApp.getProductNumber());
//		parameter.add("clientPassword", mApp.getProductPassword());
		LoadData.loadData(Constant.RESOURCE_DATA, parameter, new RequestListener() {
			public void onError(String errMsg) {
				Toast.makeText(getContext(), errMsg, 0).show();
				dlg.dismiss();
			}
			
			public void onComplete(Object obj) {
				dlg.dismiss();
				ResourceData data = (ResourceData) obj;
				Log.e("tag", "courseid:" + data.getChinese().getCourseId());
				mChineseView.setAdapter(new ExpandGradeAdapter(getContext(), data.getChinese().getGrades(), "Chinese", data.getChinese().getCourseId()));
				mMathView.setAdapter(new ExpandGradeAdapter(getContext(), data.getMath().getGrades(), "Math", data.getMath().getCourseId()));
				mEnglishView.setAdapter(new ExpandGradeAdapter(getContext(), data.getEnglish().getGrades(), "English", data.getEnglish().getCourseId()));
			}
		});
      
	}
	
	private View.OnClickListener listener = new View.OnClickListener() {
		
		public void onClick(View v) {
			if(v == mChinese || v == mChineseImageView) {
				if(mChineseView.getVisibility() == View.GONE) {
					mChineseView.setVisibility(View.VISIBLE);
					mChineseImageView.setBackgroundResource(R.drawable.tree_down);
				} else {
					mChineseView.setVisibility(View.GONE);
					mChineseImageView.setBackgroundResource(R.drawable.tree_close);
				}
			}
			
			if(v == mMath || v == mMathImageView) {
				if(mMathView.getVisibility() == View.GONE) {
					mMathView.setVisibility(View.VISIBLE);
					mMathImageView.setBackgroundResource(R.drawable.tree_down);
				} else {
					mMathView.setVisibility(View.GONE);
					mMathImageView.setBackgroundResource(R.drawable.tree_close);
				}
			}
			
			if(v == mEnglish || v == mEnglishImageView) {
				if(mEnglishView.getVisibility() == View.GONE) {
					mEnglishView.setVisibility(View.VISIBLE);
					mEnglishImageView.setBackgroundResource(R.drawable.tree_down);
				} else {
					mEnglishView.setVisibility(View.GONE);
					mEnglishImageView.setBackgroundResource(R.drawable.tree_close);
				}
			}
		}
	};
	
}
