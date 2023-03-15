package com.kingpad;

import java.util.ArrayList;

import com.bean.LearnBean;
import com.bean.LearnModeBean;
import com.constant.Constant;
import com.data.MetaData;
import com.utils.FileManager;
import com.net.LoadData;
import com.net.RequestListener;
import com.net.RequestParameter;
import com.utils.Util;
import com.views.GameView;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 学前筛查界面
 * @author swerit
 *
 */
public class ScreenActivity extends Activity{

	private GameView mBackView,				//返回键的动画
					 mCheckAns,				//核对答案
					 mBtnLast,				//上一题
					 mBtnNext,				//下一题
					 mChkBad,				//完全不会
					 mChkJust,				//一般
					 mChkGood;
	
	private ImageView mLight;
	private ImageView mBtnPlayMusic;		//播放答案按钮
	private TextView mStudy;				//学习的文字内容
	private TextView mIntro;				//标题
	
	private MetaData mData;
	private LearnBean mBean;
	private ArrayList<LearnModeBean> mLearnModeList;
	private int nCurrentIndex;				//当前词语在数组中的下标
	private LinearLayout mChkLayout,		//自我学习记录面板
						 mJumpLayout;		
	
	private RelativeLayout controlPart;		//用来设置背景
	
	private boolean hasChecked = false;		//是否已经对自己的学习进行记录,true-已经记录
	
	private static int CHECK_BAD=1, CHECK_JUST=2, CHECK_GOOD=3;
	private int checkType;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.learn_layout);
		
		initView();
		setListener();
		loadData();
	}
	
	private void initView(){
		mChkLayout			= (LinearLayout) findViewById(R.id.check_layout);
		mJumpLayout			= (LinearLayout) findViewById(R.id.jump_layout);
		mBtnPlayMusic 		= (ImageView) findViewById(R.id.imageview_play_music);
		mBtnLast			= (GameView) findViewById(R.id.button_last);
		mBtnNext			= (GameView) findViewById(R.id.button_next);
		mStudy				= (TextView) findViewById(R.id.study);
		controlPart			= (RelativeLayout) findViewById(R.id.control_part);
		mIntro				= (TextView) findViewById(R.id.intro);
		mBackView			= (GameView) findViewById(R.id.button_back);
		mCheckAns			= (GameView) findViewById(R.id.imageview_check_answer);
		mChkBad				= (GameView) findViewById(R.id.check_bad);
		mChkJust			= (GameView) findViewById(R.id.check_just);
		mChkGood			= (GameView) findViewById(R.id.check_good);
		mLight				= (ImageView) findViewById(R.id.light);
		
		mBtnLast.setVisibility(View.INVISIBLE);
		mBtnPlayMusic.setVisibility(View.INVISIBLE);
		mChkLayout.setVisibility(View.INVISIBLE);
		mLight.setVisibility(View.INVISIBLE);
		
		//设置界面一些控件的动画
		initAnimation();
	}
	
	private void setListener(){
		ButtonListener listener = new ButtonListener();
		
		mBtnPlayMusic.setOnClickListener(listener);
		mCheckAns.setOnClickListener(listener);
		mBtnLast.setOnClickListener(listener);
		mBtnNext.setOnClickListener(listener);
		mStudy.setOnClickListener(listener);
		mBackView.setOnClickListener(listener);
		mChkBad.setOnClickListener(listener);
		mChkJust.setOnClickListener(listener);
		mChkGood.setOnClickListener(listener);
	}
	
	class ButtonListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			if (v == mBtnPlayMusic){
				//播放答案
				playSound(mLearnModeList.get(nCurrentIndex).answer.file);
				
			}else if (v == mCheckAns){
				//核对答案
				gotoCheckModel();
				
			}else if (v == mBtnLast){
				//上一题
				lastTopic();
				
			}else if (v == mBtnNext){
				//下一题
				nextTopic();
				
			}else if (mBackView == v) {
				//返回
				Toast.makeText(ScreenActivity.this, "返回", 0).show();
				
			}else if (v == mChkBad) {
				//不会
				if(hasChecked)
					return;
				hasChecked = true;
				mChkBad.play(false);
				mLight.setBackgroundResource(R.drawable.light_green1);
				gotoCheckEnd(1200);
				
			}else if (v == mChkJust) {
				//一般
				if(hasChecked)
					return;
				hasChecked = true;
				mLight.setBackgroundResource(R.drawable.light_green1);
				mChkJust.play(false);
				gotoCheckEnd(1200);
				
			}else if (v == mChkGood) {
				//完全掌握
				if(hasChecked)
					return;
				hasChecked = true;
				mLight.setBackgroundResource(R.drawable.light_green1);
				mChkGood.play(false);
				gotoCheckEnd(1200);
			}
		}
	}
	
	/**
	 * 进入核对答案结束模式
	 */
	private void gotoCheckEnd(int delay){
		mBackView.postDelayed(new Runnable() {
			@Override
			public void run() {
				mChkLayout.setVisibility(View.INVISIBLE);
				mJumpLayout.setVisibility(View.VISIBLE);
				mLight.setVisibility(View.VISIBLE);
			}
		}, delay);
	}
	
	/**
	 * 进入核对答案模式
	 */
	private void gotoCheckModel(){
		playSound(mLearnModeList.get(nCurrentIndex).answer.file);
		
		mCheckAns.setVisibility(View.INVISIBLE);
		mJumpLayout.setVisibility(View.INVISIBLE);
		
		mBtnPlayMusic.setVisibility(View.VISIBLE);
		mChkLayout.setVisibility(View.VISIBLE);
	}
	
	private void loadData(){
		RequestParameter parameter = new RequestParameter();
        parameter.add("type", "ObjectiveQuiz");
        parameter.add("mode", "study");
        parameter.add("path", "/mnt/sdcard/KingPad/data/Chinese/人教版_2a/同步学习/第一单元/识字1/超前学习/学前筛查");
//        parameter.add("path", "/mnt/sdcard/KingPad/data/Chinese/人教版_2a/同步学习/第一单元/秋天的图画/超前学习/学前筛查");
       
        LoadData.loadData(Constant.META_DATA, parameter, new RequestListener() {
			public void onError(String error) {
			}
			public void onComplete(Object obj) {
				MetaData data = (MetaData) obj;
//				data.printData();d
				
				mData = data;
				mBean = data.getIntro();
				mLearnModeList = data.getLearnList();
				
				if (mBean == null || mLearnModeList == null){
					return;
				}
				
				fillView();
			}
       });
	}
	
	/**
	 * 用数据来填充界面
	 */
	private void fillView(){
		nCurrentIndex = 0;
		mIntro.setText(mBean.ownText);
		mStudy.setText(mLearnModeList.get(nCurrentIndex).question.ownText);
		Drawable drawable = FileManager.getLoacalBitmap(mData.getBg());
		if (drawable != null)
			controlPart.setBackgroundDrawable(drawable);
		
		playSound(mBean.file);
	}
	
	/**
	 * 播放声音
	 * @param soundPath：声音文件在sd卡上的路径
	 */
	private void playSound(String soundPath){
		if(soundPath == null){
			return;
		}
		
		try {
			
			Util.playSound(soundPath,false);
			
		} catch (Exception e){
			Toast.makeText(ScreenActivity.this, "播放声音异常，请检查声音文件是否存在！", Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * 下一题
	 */
	private void nextTopic(){
		if (mBtnNext.isPlaying())
			return;
		
		hasChecked = false;
		recoveryFrame();
		
		if (nCurrentIndex >= mLearnModeList.size()-2){
			nCurrentIndex = mLearnModeList.size() - 1;
			mBtnNext.setVisibility(View.INVISIBLE);
			
		}else {
			if (mBtnLast.getVisibility() == View.INVISIBLE){
				mBtnLast.setVisibility(View.VISIBLE);
				mBtnLast.stop();
			}
			nCurrentIndex++;
		}
		
		mBtnNext.play(true);
		mStudy.postDelayed(new Runnable() {
			@Override
			public void run() {
				initAnimation();
				mStudy.setText(mLearnModeList.get(nCurrentIndex).question.ownText);
			}
		}, 1200);
	}
	
	/**
	 * 上一题
	 */
	private void lastTopic(){
		if(mBtnLast.isPlaying())
			return;
		
		hasChecked = false;
		recoveryFrame();
		
		if (nCurrentIndex <= 1){
			nCurrentIndex = 0;
			mBtnLast.setVisibility(View.INVISIBLE);
			
		}else {
			if (mBtnNext.getVisibility() == View.INVISIBLE){
				mBtnNext.setVisibility(View.VISIBLE);
				mBtnNext.stop();
			}
			nCurrentIndex--;
		}
		
		//播放动画
		mBtnLast.play(true);
		
		mStudy.postDelayed(new Runnable() {
			@Override
			public void run() {
				initAnimation();
				mStudy.setText(mLearnModeList.get(nCurrentIndex).question.ownText);
			}
		}, 1200);
	}
	
	/**
	 * 恢复做题界面
	 */
	private void recoveryFrame(){
		mBtnPlayMusic.setVisibility(View.INVISIBLE);
		mLight.setVisibility(View.INVISIBLE);
		mCheckAns.setVisibility(View.VISIBLE);
	}

	/**
	 * 初始化动画
	 */
	private void initAnimation(){
		mBtnLast.stop();
		mBtnNext.stop();
		mChkBad.stop();
		mChkGood.stop();
		mChkJust.stop();
		
		mBackView.addDrawable(R.drawable.back_default, 1000);
		mBackView.addDrawable(R.drawable.back_hot, 1000);
		mBackView.play(false);
		mCheckAns.addDrawable(R.drawable.check_answer1, 1000);
		mCheckAns.addDrawable(R.drawable.check_answer2, 1000);
		mCheckAns.play(false);
		mBtnLast.addDrawable(R.drawable.jump_left1, 0);
		mBtnLast.addDrawable(R.drawable.jump_left2, 600);
		mBtnLast.addDrawable(R.drawable.jump_left3, 600);
		
		mBtnNext.addDrawable(R.drawable.jump_right1, 0);
		mBtnNext.addDrawable(R.drawable.jump_right2, 600);
		
		mChkBad.addDrawable(R.drawable.check_bad1, 200);
		mChkBad.addDrawable(R.drawable.check_bad2, 200);
		mChkBad.addDrawable(R.drawable.check_bad3, 200);
		mChkBad.addDrawable(R.drawable.check_bad2, 200);
		mChkBad.addDrawable(R.drawable.check_bad1, 200);
		
		mChkJust.addDrawable(R.drawable.check_just1, 200);
		mChkJust.addDrawable(R.drawable.check_just2, 200);
		
		mChkGood.addDrawable(R.drawable.check_good1, 200);
		mChkGood.addDrawable(R.drawable.check_good2, 200);
	}
	
	/**
	 * 释放音乐播放
	 */
	public void releasePlayer(){
		Util.releasePlayer();
	}

	@Override
	protected void onDestroy() {
		
		releasePlayer();
		
		super.onDestroy();
	}
}
