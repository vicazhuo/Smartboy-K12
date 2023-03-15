package com.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;

/**
 * 操作sd卡上的文件资源类
 * @author swerit
 *
 */
public class FileManager {

	//sd卡上资源的路径
	private static String path = Environment.getExternalStorageDirectory().getPath() + "/kingpad/audio/";
	private static String preName = "audio_";
	
	
	/**
	 * 通过该接口获取保存录音的一个有效的包含路径的文件名
	 * @return
	 */
	public static String getAudioFileName(){
		String name = path;
		File file = new File(path);
		if (!file.exists()){
			if (!file.mkdir()){
				return null;
			}
		}
		File[] fileList = file.listFiles();
		if (fileList == null || fileList.length == 0){
			name += preName + "0.mp4";
			return name;
		}
		/**
		 * 获取文件名字中最大的数字
		 */
		int max = getNumber(fileList[0]);
		for(int i=1; i<fileList.length; i++){
			int n = getNumber(fileList[i]);
			if (n > max){
				max = n;
			}
		}
		name += preName + (max+1) + ".mp4";
		return name;
	}
	
	
	
	private static int getNumber(File file){
		int k = -1;
		if (file.getName().contains(preName)) {
			String str = file.getName().substring(0, file.getName().length() - 4);
			try {
				k = Integer.parseInt(str.substring(preName.length()));
			} catch (Exception e) {
				// TODO
				Log.e("数字转换str=", e.toString());
			}
		}
		return k;
	}
	
	/**
	 * 删除一个指定名字的录音文件
	 * @param name：录音文件的名字
	 * @return：true-删除成功； false-删除失败
	 */
	public static boolean deleteOneAudioFile(String name){
		File file = new File(name);
		if (file.exists()){
			return file.delete();
		}
		return false;
	}
	
	/**
	 *   从sd卡上加载图片资源
	 * @param filePath：图片的路径
	 * @return
	 */
	public static Drawable getLoacalBitmap(String filePath){
		try {
            FileInputStream fis = new FileInputStream(filePath);
            Drawable drawable = new BitmapDrawable(BitmapFactory.decodeStream(fis)); 
            
            return drawable;  ///把流转化为Bitmap图片        

         } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
       }
	}
}
