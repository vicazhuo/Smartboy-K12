/** 文件作用：文件处理类
 *	创建时间：2012-11-17 下午9:12:05
 *  作者： 陈相伯 
 *  描述：
 */
package com.kingPadStudy.tools;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.kingPadStudy.constant.Constant;

/** 描述：文件处理类
 * 
 */
public class FileHandler {
	 private final int[] MODES=new int[]{          
			 Activity.MODE_PRIVATE, //默认操作模式，代表该文件是私有数据，只能被应用本身访问，在该模式下，写入的内容会覆盖原文件的内容，如果想把新写入的内容追加到原文件中，可以使用Activity.MODE_APPEND          
			 Activity.MODE_WORLD_READABLE,//表示当前文件可以被其他应用读取，         
			 Activity.MODE_WORLD_WRITEABLE,//表示当前文件可以被其他应用写入；    //如果希望文件被其他应用读和写，可以传入:Activity.MODE_WORLD_READABLE+Activity.MODE_WORLD_WRITEABLE          
			 Activity.MODE_APPEND//该模式会检查文件是否存在，存在就往文件追加内容，否则就创建新文件      
			 };     

	/*
	 * 文件处理对象
	 */
	private static SharedPreferences sp;
	/*
	 * 文件数据编辑器
	 */
	private static Editor editor;
	
	/**
	 * 作用：从文件中读取平板的激活状态
	 * 时间：2012-11-18下午3:46:13
	 * String
	 * @param context
	 * @return
	 */
	public static boolean getActivationState(Context context){
		return readBooleanFromFile(context,Constant.KEY_ACTIVATION_SECURITY);
	}
	
	/**
	 * 作用：设置激活状态
	 * 时间：2012 2012-11-18下午3:48:27
	 * void
	 * @param context
	 */
	public static void setActivationState(Context context,boolean value){
		setBooleanToFile(context,Constant.KEY_ACTIVATION_SECURITY,value);
	}
	
	
	/**
	 * 作用：从文件中读取软件使用权限的密码字符串
	 * 时间： 2012-11-17下午9:23:16
	 * String
	 * @param context
	 * @return
	 */
	public static String getSoftWareAuthorityPassword(Context context){
		return readStringFromFile(context,Constant.KEY_PASSWORD_SECURITY);
	}
	
	/**
	 * 作用：向文件中设置软件使用密码字符串
	 * 时间： 2012-11-17下午9:27:05
	 * void
	 */
	public static boolean setSoftWareAuthorityPassword(Context context,String password){
		return setStringToFile(context,Constant.KEY_PASSWORD_SECURITY,password);
	}
	
	

	/**
	 * 作用：从文件中读取一个字符串
	 * 时间： 2012-11-17下午9:22:56
	 * String
	 * @param context
	 * @param key
	 * @return
	 */
	public static String readStringFromFile(Context context,String key){
		sp = context.getSharedPreferences(Constant.FILE_NAME_SECURITY, context.MODE_WORLD_WRITEABLE);
		//System.out.println("读取的字符串为："+sp.getString(key, null));
		return sp.getString(key, null);
	}
	
	
	public static String readStringFromeLocalData(Context context ,String key){
		SharedPreferences sp2 = context.getSharedPreferences(Constant.LOCAL_DATA, context.MODE_WORLD_WRITEABLE);
		return sp2.getString(key, null);
	}
	
	
	/**
	 * 作用：设置文件中的一个字符串数据值 
	 * 时间： 2012-11-17下午9:25:15
	 * void
	 * @param context
	 * @param key
	 * @param value
	 */
	public static boolean setStringToFile(Context context,String key,String value){
		sp = context.getSharedPreferences(Constant.FILE_NAME_SECURITY, context.MODE_WORLD_WRITEABLE);
		editor = sp.edit();
		editor.putString(key, value);
		return editor.commit();
	}
	
	
	/**
	 * 作用：从文件中读取一个布尔值
	 * 时间： 2012-11-17下午9:22:56
	 * String
	 * @param context
	 * @param key
	 * @return
	 */
	public static boolean readBooleanFromFile(Context context,String key){
		sp = context.getSharedPreferences(Constant.FILE_NAME_SECURITY, context.MODE_WORLD_WRITEABLE);
		//System.out.println("读取的布尔值为：" + sp.getBoolean(key, false));
		return sp.getBoolean(key, false);
	}
	 
	/**
	 * 作用：设置文件中的一个字符串数据值 
	 * 时间： 2012-11-17下午9:25:15
	 * void
	 * @param context
	 * @param key
	 * @param value
	 */
	public static void setBooleanToFile(Context context,String key,boolean value){
		sp = context.getSharedPreferences(Constant.FILE_NAME_SECURITY, context.MODE_WORLD_WRITEABLE);
		//System.out.println("设置的布尔值为：" + value);
		editor = sp.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	/** 作用：					
	 * 时间：2013-1-26 下午11:09:36	
	 * void
	 * @param context 
	 * @param b
	 */
	public static void setRegistState(Context context, boolean b) {
		setBooleanToFile(context,Constant.KEY_REGIST_SECURITY,b);
	}

	/** 作用：获取注册状态
	 * 时间：2013-1-26 下午11:11:53
	 * boolean
	 * @param context
	 * @return
	 */
	public static boolean getRegistState(Context context) {
		return readBooleanFromFile(context,Constant.KEY_REGIST_SECURITY);
	}

	/** 作用：
	 * 时间：2013-1-27 下午3:54:49
	 * String
	 * @param context
	 * @param type_Major
	 * @return
	 */
	public static String getBookPath(Context context, int type_Major) {
		switch(type_Major){
			case Constant.MAJOR_CHINESE:
				return readStringFromeLocalData(context, "china"); 
			case Constant.MAJOR_MATH:
				return readStringFromeLocalData(context, "math"); 
			case Constant.MAJOR_ENGLISH:
				return readStringFromeLocalData(context, "english"); 
		}
		return null;
	}
	
	public static String getGrade(Context context){
		return readStringFromeLocalData(context, "grade"); 
	}
	
	
}
