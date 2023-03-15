/** 文件作用：动画图片精灵，用于各种有动画图片的精灵的基类
 *	创建时间：2012-11-17 下午7:49:28
 *  作者： 陈相伯 
 *  描述：
 */
package com.kingPadStudy.sprite;


import com.kingPadStudy.activity.KingPadStudyActivity;
import com.kingPadStudy.constant.Constant;

import android.R.bool;
import android.R.integer;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Message;
import android.view.View;

/**
 * 描述：动画图片精灵，用于各种有动画图片的精灵的基类
 *
 */
public abstract class AnimationImageSprite extends AnimationSprite {
	/**
	 *  处理触屏事件
	 */
	@Override
	public abstract void onTouch(float tx, float ty) ;
	
	@Override
	public abstract void onDraw(Canvas canvas);
	
	private int viewType = -1;
	
//	/**
//	 * 函数作用：放大动画
//	 * void
//	 */
//	protected void playAnimationBig(float scale,int viewType){
//		this.viewType = viewType;
//		this.scale = scale;
//		bitmap = Bitmap.createScaledBitmap(bitmap,(int)( this.width * this.scale), (int)(this.height  * this.scale), false);
//		fatherView.postInvalidate();
//		jump();
////		//线程
////		new Thread(new recoveryThread()).start();
//	}
	
	
	/**
	 * 恢复线程
	 * @author Administrator
	 */
	protected class recoveryThread implements Runnable{
		public void run(){
			try {
				Thread.sleep(dTimeAnimation);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			AnimationImageSprite.this.scale = 1;
			bitmap = Bitmap.createScaledBitmap(bitmap,(int)( AnimationImageSprite.this.width * AnimationImageSprite.this.scale), (int)(AnimationImageSprite.this.height  * AnimationImageSprite.this.scale), false);
			fatherView.postInvalidate(); 
			jump();
		}
	}
	
	/**
	 * 函数作用：跳转
	 * void
	 */
	protected void jump(){
		if(viewType != 0){
			Message message = new Message();
			message.what =  Constant.MESSAGE_CONTROLVIEW;
			message.arg1 = viewType;
			KingPadStudyActivity.handler.sendMessage(message);
		}
	}
 

	/**
	 * 函数作用：构造2
	 * void
	 * @param bmp
	 * @param x
	 * @param y
	 * @param type
	 */
	public AnimationImageSprite(Bitmap bmp, float x, float y,View fatherView) {
		this.bitmap = bmp;
		this.width = bmp.getWidth();
		this.height = bmp.getHeight();
		this.x = x;
		this.y = y;
		this.fatherView = fatherView;
	}
	
	/**
	 * 构造函数：
	 *
	 */
	public AnimationImageSprite(Bitmap bmp,float x,float y){
		this.bitmap = bmp;
		this.width = bmp.getWidth();
		this.height = bmp.getHeight();
		this.x = x;
		this.y = y;
	}
	
	/*
	 * 位图
	 */
	protected Bitmap bitmap = null;
	
	/*
	 * 放大倍数
	 */
	protected float scale = 1;
	
	/*
	 * 动画时间
	 */
	protected int dTimeAnimation = 250;
	/*
	 * 所在视图对象
	 */
	protected View fatherView;
	
}
