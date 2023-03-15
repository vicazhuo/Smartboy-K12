/** 文件作用：动画精灵，用于各种有动画效果的精灵的基类
 *	创建时间：2012-11-17 下午7:49:28
 *  作者： 陈相伯 
 *  描述：
 */
package com.kingPadStudy.sprite;

import android.R.integer;
import android.graphics.Canvas;

/**
 * 描述：动画精灵，用于各种有动画效果的精灵的基类
 *
 */
public abstract class AnimationSprite extends BaseSprite {

	/**
	 * 函数作用：处理触摸坐标
	 * void
	 * @param tx
	 * @param ty
	 */
	abstract public void onTouch(float tx,float ty);
	
	/**
	 * 函数作用：处理画图
	 * void
	 * @param canvas
	 */
	abstract public void onDraw(Canvas canvas);

}
