package com.kingPadStudy.sprite.children;

import android.R.integer;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Message;
import android.view.View;

import com.kingPadStudy.activity.KingPadStudyActivity;
import com.kingPadStudy.constant.Constant;
import com.kingPadStudy.controller.ViewController;
import com.kingPadStudy.resource.ResourceLoader;
import com.kingPadStudy.sprite.AnimationImageSprite;

public class MajorRootSprite extends AnimationImageSprite{

	
	public MajorRootSprite(Bitmap bmp, float x, float y) {
		super(bmp, x, y);
	}

	
	public MajorRootSprite(Bitmap bmp, float x, float y,View father,int index,int m) {
		super(bmp, x, y,father);
		this.index = index;
		this.major = m;
		//System.out.println("初始化精灵的时候，index="+index);
	}

	
	@Override
	public void onTouch(float tx, float ty) {
		if(tx>= this.x && tx<= this.x+this.width && ty>=this.y && ty<=this.y + this.height){
			//System.out.println("点击到MajorRootSprite");
			KingPadStudyActivity.showWaitDialog();
			//正好点击到
			jump();
			com.utils.Util.releasePlayer();
		}
	}
	 
	
	/**
	 * 函数作用：跳转
	 * void
	 */
	protected void jump(){
  		ViewController.setIndex(index);
		ResourceLoader.setMajorRoot(major, index);
	}
 

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

	
	private int index;
	private int major;
	
}
