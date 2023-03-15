/** 文件作用：主页按钮精灵，用于在各个界面显示主页图片按钮，并绑定事件。
 *	创建时间：2012-11-17 下午8:05:22
 *  作者： 陈相伯 
 *  描述：
 */
package com.kingPadStudy.sprite.children;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import com.kingPadStudy.activity.KingPadStudyActivity;
import com.kingPadStudy.sprite.AnimationImageSprite;

/** 描述： 退出按钮精灵
 * 
 */
public class QuitSprte extends AnimationImageSprite {

	private Activity activity = null;
	
	public QuitSprte(Bitmap bmp, float x, float y,Activity activity) {
		super(bmp, x, y,null);
		this.activity = activity;
	}
 

	@Override
	public void onDraw(Canvas canvas) {
		 Rect rect_dist = new Rect((int)x, (int)y, (int)(x+width*scale), (int)(y+height*scale) ); 
		 Rect rect_src = new Rect(0,0, (int)(width*scale) ,(int)(height*scale));
		 canvas.drawBitmap(this.bitmap, rect_src, rect_dist,null);
	}

	@Override
	public void onTouch(float tx, float ty) {
		if(tx>= this.x && tx<= this.x+this.width && ty>=this.y && ty<=this.y + this.height){
			//点击退出

		new AlertDialog.Builder(activity).setTitle("确认退出吗？")  .setIcon(android.R.drawable.ic_dialog_info) 
		    .setPositiveButton("确定", new DialogInterface.OnClickListener() { 
		        @Override 
		        public void onClick(DialogInterface dialog, int which) { 
		        // 点击“确认”后的操作 
		        	activity.finish(); 
		        	System.exit(0);
		        } 
		    }) 
		    .setNegativeButton("返回", new DialogInterface.OnClickListener() { 
		 
		        @Override 
		        public void onClick(DialogInterface dialog, int which) { 
		        // 点击“返回”后的操作,这里不设置没有任何操作 
		        } 
		    }).show();  
				}
	}

}
