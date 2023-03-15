/** 文件作用：安全类
		1，防止软件移植（初始化时，与ServerInfoHandler配合判断是否授权）
		2，防止资源移植（初始化时，与ServerInfoHandler配合加载已付费资源列表）
		3，防止对资源进行更改删除。（当选择被误删、或出错的资源进行重新下载）
		4,   1,2点中的授权数据的使用时机为：每次View初始化和每次资源加载。
		5，在开启时，有一个密码登陆验证。（无网络时）
 *	创建时间：2012-11-17 下午8:26:39
 *  作者： 陈相伯 
 *  描述：
 */
package com.kingPadStudy.security;

import com.kingPadStudy.constant.Constant;
import com.kingPadStudy.tools.FileHandler;
import com.kingPadStudy.tools.SimpleCrypto;

import android.content.Context;

/** 描述：安全类
		1，防止软件移植（ 密码验证 本地 ）
		2，防止资源移植（ 资源列表 本地）
		3，防止对资源进行更改删除。（当选择被误删、或出错的资源进行重新下载）
		4,   1点中使用时机为：MainActivity和每次View初始化，1点中使用时机为：每次资源加载。
 */
public class Security {
	
	public static void registPad(Context context){
		FileHandler.setRegistState(context, true);
	}
	
	/**
	 * 作用：判断平板是注册
	 * 时间：20122012-11-18下午3:32:58
	 * boolean
	 * @return
	 */
	public static boolean isregistPad(Context context){	
		//读取文件中的密码	
		return  FileHandler.getRegistState(context);	
	}
	
	/**
	 * 作用：判断平板是否激活
	 * 时间：20122012-11-18下午3:32:58
	 * boolean
	 * @return
	 */
	public static boolean isActivated(Context context){	
		//读取文件中的密码	
		String passwordFile = FileHandler.getSoftWareAuthorityPassword(context);
		//TODO
		System.out.println("判断激活时，passwordFile="+passwordFile);
		if(passwordFile == null || "".equals(passwordFile)){ 	
			return false;	
		} 
		return true;
	}
	
	/**
	 * 作用：激活平板电脑
	 * 时间：2012-11-18 下午4:59:35
	 * void
	 * @param context
	 */
	public static void activatePad(Context context){
		FileHandler.setActivationState(context, true);
	}
	
	/**
	 * 作用：注册软件使用密码
	 * 时间：20122012-11-17下午9:30:25
	 * void
	 * @param password
	 */
	public static boolean registSoftWareAuthorityPassword(Context context,String password){
		//将密码字符串通过SimpleCrypto算法加密
		String passwordIncode = ""; 
		try {
			passwordIncode = SimpleCrypto.encrypt(Constant.PASSWORD_SimpleCrypto, password);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//将加密密码写入文件
		return FileHandler.setSoftWareAuthorityPassword(context, passwordIncode);
	}
	
	
	
	
	/**
	 * 作用：用户打开软件时的登陆
	 * 时间：20122012-11-17下午9:03:31
	 * boolean 返回是否登陆成功
	 * @param password 用户在平板电脑上输入的密码
	 */
	public static boolean login(Context context,String password){
		//读取文件中的密码
		String passwordFile = FileHandler.getSoftWareAuthorityPassword(context);
		//将文件中的密码通过SimpleCrypto算法解密
		String passwordDecode = "";
		try {
			passwordDecode = SimpleCrypto.decrypt(Constant.PASSWORD_SimpleCrypto, passwordFile);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		//将用户输入密码与解密密码匹配
		if(password.equals(passwordDecode)){
			return true;
		}else{
			return false;
		}
	}
}
