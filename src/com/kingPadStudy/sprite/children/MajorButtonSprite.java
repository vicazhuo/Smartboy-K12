/** 文件作用：科目按钮精灵
 *	创建时间：2012-11-17 下午7:49:28
 *  作者： 陈相伯 
 *  描述：
 */
package com.kingPadStudy.sprite.children;

import java.text.DecimalFormat;

import android.R.integer;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import com.data.BookData;
import com.kingPadStudy.activity.KingPadStudyActivity;
import com.kingPadStudy.constant.Constant;
import com.kingPadStudy.resource.ResourceLoader;
import com.kingPadStudy.sprite.AnimationImageSprite;
import com.kingPadStudy.tools.FileHandler;
import com.kingPadStudy.tools.SimpleCrypto;
import com.kingPadStudy.views.DialogCreator;
import com.kingpad.KingPadApp;
import com.kingpad.R.color;
import com.net.LoadData;
import com.net.RequestListener;
import com.net.RequestParameter;

/**
 * 描述：科目按钮精灵
 *
 */
public class MajorButtonSprite extends AnimationImageSprite{
	
	public MajorButtonSprite(Bitmap bmp, float x, float y) {
		super(bmp, x, y);
	}
	
	/**
	 * 构造函数：
	 * @param bmp
	 * @param x
	 * @param y
	 * @param type
	 */
	public MajorButtonSprite(Bitmap bmp, float x, float y,int type,View father) {
		super(bmp, x, y,father); 
		type_Major = type;
	}
	
	@Override
	public void onTouch(float tx, float ty) {
		if(tx>= this.x && tx<= this.x+this.width && ty>=this.y && ty<=this.y + this.height){
			if(!isKingPad){
				KingPadStudyActivity.showWaitDialog();
			}
			jump();
		}
	}
	
	private String getBookPath(){
		//读取文件中的密码
		return  FileHandler.getBookPath(context,type_Major);
		
	}
	
	private void showCheckDialog(){
		Dialog dlg = null;
		String show = "您无权使用本资源，请重新设置教材！";
		dlg = new AlertDialog.Builder(context)
		.setTitle("提示")
		.setMessage(show)
		.setNeutralButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {			
			}
		}) .create();
		dlg.show();
	}
	
	private void showHintDialog() {
		Dialog dlg = null;
		String show = "";
		if(type_Major == Constant.MAJOR_CHINESE){ //参数错误
			show = "您还没有设置语文的教材版本，请进入\"产品首页\"进行设置！";
		}else if(type_Major == Constant.MAJOR_MATH){	//已存在
			show = "您还没有设置数学的教材版本，请进入\"产品首页\"进行设置！";
		}else if(type_Major == Constant.MAJOR_ENGLISH){	//成功
			show = "您还没有设置英语的教材版本，请进入\"产品首页\"进行设置！";
		}
		dlg = new AlertDialog.Builder(context)
		.setTitle("提示")
		.setMessage(show)
		.setNeutralButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {			
			}
		}) .create();
		dlg.show();
	}

	/**
	 * 函数作用：跳转
	 * void
	 */
	protected void jump(){
		final String path = FileHandler.getBookPath(context, type_Major);
		final String grade = FileHandler.getGrade(context);
		if(!isKingPad){
			if(path == null || "".equals(path) || path.length()<= 1  ){
				//如果没有设置教材
				showHintDialog();
				KingPadStudyActivity.dismissWaitDialog();
				return ;
			}
		}
		if(!isKingPad){
			//当顺利通过了教材设置检查，现在检查——资源文件夹加密 
			//System.out.println("顺利通过了教材设置检查，path="+path);
			//获取文件夹大小
			double size_save = 0;	
			double size_real = 0;  	
			String sizeEncode = FileHandler.readStringFromFile(context, "size_"+path);	//获取加密大小
			final String seed = KingPadStudyActivity
					.getAndroidId();  
			String sizeDecode ="";  
			try { 
				sizeDecode = SimpleCrypto.decrypt(seed, sizeEncode);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			size_save = Float.parseFloat(sizeDecode);
			try {
				//计算资源文件夹的大小
				size_real = com.utils.Util.getSize(new java.io.File(path));
				//精确到一定的位数
				DecimalFormat df = new DecimalFormat("#.00");
				String string_size = df.format(size_real);
				size_real = Float.parseFloat(string_size);
				if(size_real != size_save){
					KingPadStudyActivity.dismissWaitDialog();
					showCheckDialog();
					return ;
				}else{ //大小一致 ，检查目录结构
					/*
					 * 获取随机数组
					 */
					String numbers_encode =  FileHandler
							.readStringFromFile(context, "content_numbers_"
									+path);
					String number_decode = SimpleCrypto
							.decrypt(seed, numbers_encode);
					final int[] radoms = com.utils.Util.getRadomNumbers(number_decode);
					/*
					 * 获取文件保存金刚 
					 */
					String kingkang_encode =  FileHandler
							.readStringFromFile(context, "content_titles_"
									+ path);
					final String kingkang_decode = SimpleCrypto
							.decrypt(seed, kingkang_encode);
					//获取实际目录的随机结构  
					RequestParameter parameter = new RequestParameter();
					parameter.add("path", path);
					parameter.add("packagePath", com.utils.Util.getPackageUrl(path));
					LoadData.loadData(Constant.BOOK_DATA, parameter, new RequestListener() {
						public void onError(String error) { 
							Toast.makeText(context, "异常：" + error,
									0).show();
							KingPadStudyActivity.dismissWaitDialog();
							showCheckDialog();
						}
						public void onComplete(Object obj) {
							BookData bigIndex = (BookData) obj;
							String kingkang_title = com.utils.Util.getTitleKingKang(radoms,bigIndex);
							if(kingkang_title.equals(kingkang_decode)){
								//如果目录结构验证成功
								switch(type_Major){
								case Constant.MAJOR_CHINESE:
									ResourceLoader.loadBigBookData(context,path,"/mnt/sdcard/kingpad/data/Chinese/"+grade,Constant.MAJOR_CHINESE);
									break;
								case Constant.MAJOR_MATH:
									ResourceLoader.loadBigBookData(context,path,"/mnt/sdcard/kingpad/data/Math/"+grade,Constant.MAJOR_MATH);
									break;
								case Constant.MAJOR_ENGLISH:
									ResourceLoader.loadBigBookData(context,path,"/mnt/sdcard/kingpad/data/English/"+grade,Constant.MAJOR_ENGLISH);
									break;
								}
								Message message = new Message();
								message.what =  Constant.VIWE_MAJOR;
								message.arg1 = type_Major;
								KingPadStudyActivity.handler.sendMessage(message);
								}else{
									KingPadStudyActivity.dismissWaitDialog();
									showCheckDialog();
									return ;
								}
							}
						});
				}
			} catch (Exception e1) {
				e1.printStackTrace();
				KingPadStudyActivity.dismissWaitDialog();
				showCheckDialog();
			}
		} else{
			//点击“产品主页”
			KingPadStudyActivity activity = (KingPadStudyActivity) context;
			KingPadApp mApp = activity.getmApp();
			if(mApp.getProductPassword() == null || mApp.getProductPassword().equals("")){
				//当没有登陆过时
				((KingPadStudyActivity)context).Login_MyKingPad();
			}else{
				//已经登陆过，那么显示个人中心
				((KingPadStudyActivity)context).showMain();
			}
		} 
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

	private int type_Major = -1;

	/** 作用：
	 * 时间：2013-1-30 下午7:09:20
	 * void
	 * @param context
	 */
	public void setContext(Context context) {
		this.context = context;
	}
	
	private Context context;

	private boolean isKingPad = false;
	
	/** 作用：设置类型为 产品主页
	 * 时间：2013-1-27 下午12:19:14
	 * void
	 */
	public void setMyKingPad() {
		isKingPad = true;
	}
}
