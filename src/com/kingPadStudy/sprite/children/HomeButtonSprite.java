/** 文件作用：主页按钮精灵，用于在各个界面显示主页图片按钮，并绑定事件。
 *	创建时间：2012-11-17 下午8:05:22
 *  作者： 陈相伯 
 *  描述：
 */
package com.kingPadStudy.sprite.children;

import android.R.integer;
import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.kingPadStudy.sprite.AnimationImageSprite;

/** 描述：主页按钮精灵，用于在各个界面显示主页图片按钮，并绑定事件。
 * 
 */
public class HomeButtonSprite extends AnimationImageSprite {

	public HomeButtonSprite(Bitmap bmp, float x, float y) {
		super(bmp, x, y);
	}
 

	@Override
	public void onDraw(Canvas canvas) {
		
	}

	@Override
	public void onTouch(float tx, float ty) {
		
	}

}
