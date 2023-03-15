/** 文件作用：菜单中间图片精灵，用于菜单视图的中间的菜单图标的显示和事件绑定。
 *	创建时间：2012-11-17 下午7:58:56
 *  作者： 陈相伯 
 *  描述：
 */
package com.kingPadStudy.sprite.children;

import android.R.integer;
import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.kingPadStudy.sprite.AnimationImageSprite;

/** 描述：菜单中间图片精灵，用于菜单视图的中间的菜单图标的显示和事件绑定。
 * 
 */
public class MenuCenterImageSprite extends AnimationImageSprite{

	public MenuCenterImageSprite(Bitmap bmp, float x, float y) {
		super(bmp, x, y);
		// TODO Auto-generated constructor stub
	}
 

	@Override
	public void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onTouch(float tx, float ty) {
		// TODO Auto-generated method stub
		
	}

}
