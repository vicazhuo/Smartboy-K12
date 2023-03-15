/** 文件作用：主界面视图，用于显示主界面
 *	创建时间：2012-11-17 下午7:39:59
 *  作者： 陈相伯 
 *  描述：
 */
package com.kingPadStudy.views;


import java.util.ArrayList;

import com.kingpad.R;
import com.kingPadStudy.constant.Constant;
import com.kingPadStudy.sprite.AnimationImageSprite;
import com.kingPadStudy.sprite.children.MajorButtonSprite;
import com.kingPadStudy.sprite.children.QuitSprte;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

/**
 * 描述：显示主界面的视图
 *
 */
public class MainView  extends BaseView {
	Bitmap bmpBitmap =  BitmapFactory.decodeResource(getResources(), R.drawable.bg_main);
	//学科按钮
	MajorButtonSprite button_chinese ;
	MajorButtonSprite button_math;
	MajorButtonSprite button_english;
	//其他按钮
//	MajorButtonSprite button_book_set;
//	MajorButtonSprite button_book_update;
	MajorButtonSprite button_mykingpad;
	//退出按钮
	QuitSprte button_quit;
	//各个图片的坐标
	float x_chinese = 230;
	float y_chinese = 236;
	float x_math = 420;
	float y_math = 236;
	float x_english = 610;
	float y_english =236;
	
	float x_mykingpad = 330;
	float y_mykingpad = 550;
	float x_set = 380;
	float y_set = 550;
	float x_update = 530;
	float y_update = 550;
	float x_quit= 550;
	float y_quit = 550;
	Activity activity  = null;
	
	/** 
	 * @param context
	 */
	public MainView(Context context,Activity activity) {
		super(context);
		//System.out.println("进入MainView。。。。。。。。。。。");
        setBackgroundResource(R.drawable.bg_main); 
        //语文按钮
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.button_chinese2);
        button_chinese = new MajorButtonSprite(bitmap,x_chinese, y_chinese,Constant.MAJOR_CHINESE,(View)this);
        button_chinese.setContext(context);
        //数学按钮
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.button_math2);
        button_math = new MajorButtonSprite(bitmap,x_math, y_math,Constant.MAJOR_MATH,(View)this);
        button_math.setContext(context);
        //英语按钮
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.button_english);
        button_english = new MajorButtonSprite(bitmap,x_english, y_english,Constant.MAJOR_ENGLISH,(View)this);
        button_english.setContext(context);
//        //设置教材按钮
//        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.button_book_select);
//        button_book_set = new MajorButtonSprite(bitmap,x_set, y_set,Constant.VIEW_BOOK_SET,(View)this);
        //我的睿学按钮
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.button_mykingpad);
        button_mykingpad = new MajorButtonSprite(bitmap,x_mykingpad, y_mykingpad,Constant.VIEW_MYKINGPAD,(View)this);
        button_mykingpad.setMyKingPad();
        button_mykingpad.setContext(context);
//        //更新教材按钮
//        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.button_update);
//        button_book_update = new MajorButtonSprite(bitmap,x_update, y_update,Constant.VIEW_BOOK_SET,(View)this);
        // 退出按钮
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.button_quit);
        button_quit = new QuitSprte(bitmap,x_quit, y_quit,activity);
	}
	
	
	/** 函数作用：
	 * @param canvas 
	 */
	@Override
	public void onDraw(Canvas canvas) {
		button_chinese.onDraw(canvas);
		button_math.onDraw(canvas);
		button_english.onDraw(canvas);
//	    button_book_set.onDraw(canvas);
//		button_book_update.onDraw(canvas);
		button_mykingpad.onDraw(canvas);
		button_quit.onDraw(canvas);
	}
	
	
	/** 函数作用：
	 * @param event
	 * @return 
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float tx = event.getX();
		float ty = event.getY();
		button_chinese.onTouch(tx, ty);
		button_math.onTouch(tx, ty);
		button_english.onTouch(tx, ty);
//	    button_book_set.onTouch(tx, ty);
//		button_book_update.onTouch(tx, ty);
		button_mykingpad.onTouch(tx, ty);
		button_quit.onTouch(tx, ty);
		return false;
	}
	
	
	/** 函数作用：
	 *  
	 */
	@Override
	public void recycle() {
		
	}
}
