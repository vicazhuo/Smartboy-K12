package com.constant;
/**
 * 常量类，保存常量配置文件和加载数据时的调用接口
 * @author lenovo
 *
 */
public final class Constant {
	/*
	 * SimpleCrypto 加密类使用密码
	 */
	public static String PASSWORD_SimpleCrypto = "PASSWORD_SimpleCrypto";
	public static String RESOURCE_SimpleCrypto = "RESOURCE_SimpleCrypto";
	// ---------------------该工程配置文件目录-----------------------------------
	
	/**
	 * 应用程序注册接口
	 */
	public final static String ACTIVATE_DATA = "activation_data";
	/**
	 * 获取年级资源列表
	 */
	public final static String GRADE_DATA = "grade_data";
	/**
	 * 获取需要付费的资源数据接口
	 */
	public final static String BUY_RESOURCE_DATA = "buy_resource_data";
	/**
	 * 获取充值记录接口数据
	 */
	public final static String PAY_LOG_DATA = "pay_log_data";
	/**
	 * 客户信息接口
	 */
	public final static String CLIENT_DATA = "client_data";
	/**
	 * 更新密码
	 */
	public final static String UPDATE_PWD_DATA = "update_pwd_data";
	public final static String RESOURCE_DATA = "resource_data";
	
	public final static String GAME_DATA = "game_data";
	
	/**
	 * 下载资源关键字
	 */
	public final static String DOWNLOAD_DATA = "download_data";
	public final static String DOWNLOAD_PACKAGE_DATA = "download_package_data";
	public final static String DOWNLOAD_LOGS_DATA = "download_logs_data";
	
	
	/**
	 * 资源文件根目录
	 */
	public final static String RESOURCE_PATH = "/mnt/sdcard/kingpad/data";
	/**
	 * 下载资源目录
	 */
	public final static String DOWNLOAD_PATH ="/mnt/sdcard/kingpad/download";
	public static final String PATH_DOWNLOAD_GAME = "/mnt/sdcard/kingpad/download/game";
	/**
	 * 下载资源时传递参数的目录
	 */
	public final static String DOWNLOAD_PATH_PARAM = "/kingpad/download/";
	/**
	 * 保存的本地数据
	 */
	public final static String LOCAL_DATA = "localdata";
	/**
	 * 下载记录数据库保存位置
	 */
	public final static String DATABASE_PATH = "/mnt/sdcard/kingpad/data";
	
	//public final static String SERVER_URL = "http://10.0.2.2:8888/childrenStudy/";
	 public final static String SERVER_URL = "http://www.rxrj.com.cn:8888/childrenStudy/";
	// public final static String SERVER_URL = "http://192.168.5.102:8888/childrenStudy/";
	 
	 
	// ---------------------该工程配置文件目录-----------------------------------
	
	/**
	 * 文件根目录
	 */
	public final static String KING_PAD_PATH = "/mnt/sdcard/kingpad";
	/**
	 * 表示该程序的资源文件目录路径
	 */
	public final static String KING_PAD_RES = "/mnt/sdcard/kingpad/";
	public final static String KING_PAD_ICON = "/mnt/sdcard/kingpad/assets/";
	
	// -------------------------获取本地请求关键字目录----------------------------------
	/**
	 * 获取该根目录中当前的index.xml文件中的数据
	 */
	public final static String CATALOG_DATA = "catalog_data";
	/**
	 * 解析data.xml文件数据
	 */
	public final static String META_DATA = "meta_data";
	/**
	 * 解析诗词数据
	 */
	public final static String POEM_DATA = "poem_data";
	/**
	 * 解析教程所对应的index.xml数据
	 */
	public final static String BOOK_DATA = "book_data";
	public final static String META_UTIL_DATA = "meta_util_data";
	/**
	 * 加载package目录下的文件数据
	 */
	public final static String PACKAGE_DATA = "package_data";
	/**
	 * 注册的数据
	 */
	public static final String REGIST_DATA = "regist_data";
	/**
	 *	注册用户的数据
	 */
	public static final String REGIST_USER_DATA = "regist_user_data";

	public static final int MESSAGE_BUTTON_CHANGE = 1002;

	public static final String BROAD_CAST_SAVE_PATH = "SAVE_PATH";
	public static final String GAME_LOG_DATA = "game_log_data";
}
