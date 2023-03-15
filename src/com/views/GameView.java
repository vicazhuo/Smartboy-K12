package com.views;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

/**
 * 动画测试类，用来播放按钮按下去之后的动画显示
 * @author swerit
 *
 */


public class GameView extends View{

	private AnimationDrawable frameAnimationDrawable = null;
	private Drawable mBitAnimation = null;
	private Context mContext = null;
	

	public void reset(){
		frameAnimationDrawable = new AnimationDrawable();
	}
	
	
	public GameView(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		mContext = context;
		frameAnimationDrawable = new AnimationDrawable();
	}
	
	public void clear(){
		frameAnimationDrawable = new AnimationDrawable();
	}
	
	/**
	 * 
	 * @param oneShot
	 */
	public void play(boolean oneShot){
		frameAnimationDrawable.setOneShot(oneShot);
		frameAnimationDrawable.start();
	}
	
	/**
	 * 添加动画图片，以及相应的播放时间
	 */
	public void addDrawable(int drawableId, int delay){
		mBitAnimation = getResources().getDrawable(drawableId);
		frameAnimationDrawable.addFrame(mBitAnimation, delay); 
		this.setBackgroundDrawable(frameAnimationDrawable);
	}
	
	
	public void stop(){
		frameAnimationDrawable.stop();
	}
	
	public boolean isPlaying(){
		return frameAnimationDrawable.isRunning();
	}


	public AnimationDrawable getFrameAnimationDrawable() {
		return frameAnimationDrawable;
	}


	public void setFrameAnimationDrawable(AnimationDrawable frameAnimationDrawable) {
		this.frameAnimationDrawable = frameAnimationDrawable;
	}
	
	
	
}
