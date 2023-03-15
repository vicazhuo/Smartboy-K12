/** 文件作用：学习视图
 *	创建时间：2012-11-17 下午7:39:59
 *  作者： 陈相伯 
 *  描述：
 */
package com.kingPadStudy.views;

import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.widget.FrameLayout;

/**
 * 描述：学习视图,用于各种类型的学习视图的基类
 *
 */
public class StudyView  extends BaseView {

	/** 
	 * @param context
	 */
	public StudyView(Context context) {
		super(context);
	}

	/** 函数作用：
	 * @param canvas 
	 */
	@Override
	public void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		
	}

	/** 函数作用：
	 * @param event
	 * @return 
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	/** 函数作用：
	 *  
	 */
	@Override
	public void recycle() {
		// TODO Auto-generated method stub
		
	}

}
