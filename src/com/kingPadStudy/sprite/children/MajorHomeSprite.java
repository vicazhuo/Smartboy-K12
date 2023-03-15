/** 文件作用：
 *	创建时间：2013-1-21 上午10:50:40
 *  作者： 陈相伯 
 *  描述：
 */
package com.kingPadStudy.sprite.children;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import com.kingPadStudy.activity.KingPadStudyActivity;
import com.kingPadStudy.constant.Constant;
import com.kingPadStudy.controller.ViewController;
import com.kingPadStudy.sprite.AnimationImageSprite;

/** 描述：
 * 
 */
public class MajorHomeSprite extends AnimationImageSprite{

	/** 
	 * @param bmp
	 * @param x
	 * @param y
	 */
	public MajorHomeSprite(Bitmap bmp, float x, float y) {
		super(bmp, x, y);
	}

	/** 函数作用：
	 * @param tx
	 * @param ty 
	 */
	@Override
	public void onTouch(float tx, float ty) {
		if(tx>= this.x && tx<= this.x+this.width && ty>=this.y && ty<=this.y + this.height){
			KingPadStudyActivity.showWaitDialog();
			//正好点击到
			jumpToHome();
			com.utils.Util.releasePlayer();
		}
	}

	/** 作用：
	 * 时间：2013-1-21 上午10:52:22
	 * void
	 */
	private void jumpToHome() {
		KingPadStudyActivity.showWaitDialog();
		ViewController.controllView(Constant.VIEW_MAIN);		
	}

	/** 函数作用：
	 * @param canvas 
	 */
	@Override
	public void onDraw(Canvas canvas) {
		 int w_move = 0;
		 int h_move = 0;
		 if(this.scale != 1){
			 w_move = (int)(this.width*(scale-1));
			 h_move = (int)(this.height*(scale-1));
		 }
		 Rect rect_dist = new Rect((int)x-w_move, (int)y-h_move, (int)(x+width*scale), (int)(y+height*scale) ); 
		 Rect rect_src = new Rect(0,0, (int)(width*scale) ,(int)(height*scale));
		 canvas.drawBitmap(this.bitmap, rect_src, rect_dist,null);		
	}

}
