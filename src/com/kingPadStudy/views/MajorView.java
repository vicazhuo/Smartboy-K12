/** 文件作用：主界面视图，用于显示主界面
 *	创建时间：2012-11-17 下午7:39:59
 *  作者： 陈相伯 
 *  描述：
 */
package com.kingPadStudy.views;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;

import com.kingPadStudy.activity.KingPadStudyActivity;
import com.kingPadStudy.constant.Constant;
import com.kingPadStudy.controller.ViewController;
import com.kingPadStudy.sprite.children.MajorHomeSprite;
import com.kingPadStudy.sprite.children.MajorRootSprite;
import com.kingpad.R;

/**
 * 描述：显示主界面的视图
 *
 */
public class MajorView  extends BaseView {
	Bitmap bmpBitmap =  BitmapFactory.decodeResource(getResources(), R.drawable.bg_chinese);
	//学科按钮
	int number_button = 0;
	int number_show = 0;
	MajorHomeSprite button_home = null;
	MajorRootSprite[] button_major_root = new MajorRootSprite[9];
	int position_x_1 = 200;
	int position_y_1 = 95;

	int position_x_2 = 280;
	int position_y_2 = 165;

	int position_x_3 = 360;
	int position_y_3 = 235;

	int position_x_4 = 440;
	int position_y_4 = 305;

	int position_x_5 = 520;
	int position_y_5 = 375;

	int position_x_6 = 600;
	int position_y_6 = 450;

	int position_x_7 = 680;
	int position_y_7 = 523;
	
	//各个图片的坐标
	float x_chinese = 319;
	float y_chinese = 263;
	float x_math = 457;
	float y_math = 263;
	float x_english = 597;
	float y_english =263;
	
	/** 
	 * @param context
	 */
	public MajorView(Context context,int index) {
		super(context);
		Bitmap bitmap = null;
		//主页按钮
		bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.button_home);
		button_home = new MajorHomeSprite(bitmap, 920, 20);
		switch(index){
			case Constant.MAJOR_CHINESE:
				number_button = 7;
				number_show = 7;
				//语文
				setBackgroundResource(R.drawable.bg_chinese); 
				com.utils.Util.playBackGroundSound(context, "back_music_chinese.mp3",true);
				//同步
		        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.button_chinese_tongbu);
		        button_major_root[0] = new MajorRootSprite(bitmap,position_x_1, position_y_1,(View)this,0,Constant.MAJOR_CHINESE);
		        //大师
		        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.button_chinese_dashi);
		        button_major_root[1] = new MajorRootSprite(bitmap,position_x_2, position_y_2,(View)this,1,Constant.MAJOR_CHINESE);
		        //基础
		        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.button_chinese_jichu);
		        button_major_root[2] = new MajorRootSprite(bitmap,position_x_3, position_y_3,(View)this,2,Constant.MAJOR_CHINESE);
		        //向导
		        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.button_chinese_xiangdao);
		        button_major_root[3] = new MajorRootSprite(bitmap,position_x_4, position_y_4,(View)this,3,Constant.MAJOR_CHINESE);
		        //高手
		        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.button_chinese_gaoshou);
		        button_major_root[4] = new MajorRootSprite(bitmap,position_x_5, position_y_5,(View)this,4,Constant.MAJOR_CHINESE);
		        //古诗
		        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.button_chinese_gushi);
		        button_major_root[5] = new MajorRootSprite(bitmap,position_x_6, position_y_6,(View)this,5,Constant.MAJOR_CHINESE);
		        //天才
		        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.button_chinese_tiancai);
		        button_major_root[6] = new MajorRootSprite(bitmap,position_x_7, position_y_7,(View)this,6,Constant.MAJOR_CHINESE);
				break;
			case Constant.MAJOR_MATH:
				//数学
				number_button = 2;
				number_show = 7;
				int x_math = 853;
				int gap = 50;
				setBackgroundResource(R.drawable.bg_math); 
				com.utils.Util.playBackGroundSound(context, "music_math.mp3",true);
				//同步
		        Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(), 
		        		R.drawable.button_math_tongbu);
		        button_major_root[0] = new MajorRootSprite(bitmap2,
		        		x_math-15, 180,(View)this,0,Constant.MAJOR_MATH);
		        //拓展
		        bitmap2 = BitmapFactory.decodeResource(getResources(), 
		        		R.drawable.button_math_tuozhan);
		        button_major_root[1] = new MajorRootSprite(bitmap2,
		        		x_math-15, 407,(View)this,1,Constant.MAJOR_MATH);
		        //超前
		        bitmap2 = BitmapFactory.decodeResource(getResources(), 
		        		R.drawable.button_math_chaoqian);
		        button_major_root[2] = new MajorRootSprite(bitmap2,
		        		x_math, 195+gap*1,(View)this,1,Constant.MAJOR_MATH);
//		        //筛查
//		        bitmap2 = BitmapFactory.decodeResource(getResources(), 
//		        		R.drawable.button_math_shaicha);
//		        button_major_root[3] = new MajorRootSprite(bitmap2,
//		        		x_math-8, 170+gap*2-9,(View)this,1,Constant.MAJOR_MATH);
		        //易错
		        bitmap2 = BitmapFactory.decodeResource(getResources(), 
		        		R.drawable.button_math_yicuo);
		        button_major_root[3] = new MajorRootSprite(bitmap2,
		        		x_math, 195+gap*2,(View)this,1,Constant.MAJOR_MATH);
		        //尖子生
		        bitmap2 = BitmapFactory.decodeResource(getResources(), 
		        		R.drawable.button_math_jianzi);
		        button_major_root[4] = new MajorRootSprite(bitmap2,
		        		x_math-8, 195+gap*3-9,(View)this,1,Constant.MAJOR_MATH);
//		        //考前
//		        bitmap2 = BitmapFactory.decodeResource(getResources(), 
//		        		R.drawable.button_math_kaoqian);
//		        button_major_root[6] = new MajorRootSprite(bitmap2,
//		        		x_math, 170+gap*5,(View)this,1,Constant.MAJOR_MATH);
		        //奥术
		        bitmap2 = BitmapFactory.decodeResource(getResources(), 
		        		R.drawable.button_math_aoshubagao);
		        button_major_root[5] = new MajorRootSprite(bitmap2,
		        		x_math, 160+gap*6,(View)this,1,Constant.MAJOR_MATH);
		        //奥术基础
		        bitmap2 = BitmapFactory.decodeResource(getResources(), 
		        		R.drawable.button_math_aoshujichu);
		        button_major_root[6] = new MajorRootSprite(bitmap2,
		        		x_math, 150+gap*7,(View)this,1,Constant.MAJOR_MATH);
				break;
			case Constant.MAJOR_ENGLISH:
				//外语
				number_button = 5;
				number_show = 5;
				com.utils.Util.playBackGroundSound(context, "music_english.mp3",true);
				setBackgroundResource(R.drawable.bg_english);
				int x_english = 111;
				int gap_en = 85;
				int y_begin = 180;
				//同步
		        Bitmap bitmap3 = BitmapFactory.decodeResource(getResources(), 
		        		R.drawable.button_english_tongbu);
		        button_major_root[0] = new MajorRootSprite(bitmap3,
		        		x_english, y_begin  ,(View)this,0,Constant.MAJOR_ENGLISH);
		        //超越
		        bitmap3 = BitmapFactory.decodeResource(getResources(), 
		        		R.drawable.button_english_chaoyue);
		        button_major_root[1] = new MajorRootSprite(bitmap3,
		        		x_english, y_begin + gap_en*1,(View)this,1,Constant.MAJOR_ENGLISH);
				//英语竞赛
		        bitmap3 = BitmapFactory.decodeResource(getResources(), 
		        		R.drawable.button_english_yingyu);
		        button_major_root[2] = new MajorRootSprite(bitmap3,
		        		x_english, y_begin + gap_en*2,(View)this,2,Constant.MAJOR_ENGLISH);
				//课外英语
		        bitmap3 = BitmapFactory.decodeResource(getResources(), 
		        		R.drawable.button_english_kewai);
		        button_major_root[3] = new MajorRootSprite(bitmap3,
		        		x_english, y_begin + gap_en*3,(View)this,3,Constant.MAJOR_ENGLISH);
		        //国际
		        bitmap3 = BitmapFactory.decodeResource(getResources(), 
		        		R.drawable.button_english__guoji);
		        button_major_root[4] = new MajorRootSprite(bitmap3,
		        		x_english, y_begin + gap_en*4,(View)this,4,Constant.MAJOR_ENGLISH);
				
		        break;
		}
		if(ViewController.isBigLoad){
			KingPadStudyActivity.dismissWaitDialog();
		}
	}
	
	
	
	
	
	
	/** 函数作用：
	 * @param canvas 
	 */
	@Override
	public void onDraw(Canvas canvas) {
		for(int i=0;i<number_show;i++){
			button_major_root[i].onDraw(canvas);
		}
		button_home.onDraw(canvas);
	}
	
	
	/** 函数作用：
	 * @param event
	 * @return 
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float tx = event.getX();
		float ty = event.getY();
		for(int i=0;i<number_button;i++){
			button_major_root[i].onTouch(tx, ty);
		}
		button_home.onTouch(tx, ty);
		return false;
	}
	
	
	/** 函数作用：
	 *  
	 */
	@Override
	public void recycle() {
		
	}





	/** 作用：
	 * 时间：2013-1-9 下午3:18:30
	 * void
	 */
	public void dismissDialog() {
		KingPadStudyActivity.dismissWaitDialog();		
	}
}
