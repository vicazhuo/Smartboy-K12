/** 文件作用：定义基础视图类，用于各种视图的基类
 *	创建时间：2012-11-17 下午7:39:59
 *  作者： 陈相伯 
 *  描述：
 */
package com.kingPadStudy.views;

import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;

/**
 * 描述：各种视图的基类
 *
 */
public abstract class BaseView extends View{

	/** 构造
	 * @param context
	 */
	public BaseView(Context context) {
		super(context);
	}
	
	/**
	 * 函数作用：处理画布事件
	 * @param canvas
	 */
	public abstract void onDraw(Canvas canvas) ; 

	
	/**
	 * 函数作用：处理触屏事件
	 * @param event
	 * @return
	 */
	public abstract  boolean onTouchEvent(MotionEvent event);

	
	/**
	 * 作用：处理视图背部变量的回收
	 * 时间：20122012-11-18下午1:08:08
	 * void
	 */
	public  abstract void recycle();
	
	
}
