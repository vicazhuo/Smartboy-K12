/** 文件作用： 定义各种常量
 *	创建时间：2012-11-17 下午7:39:59
 *  作者： 陈相伯 
 *  描述：
 */
package com.kingPadStudy.constant;

/** 描述： 定义各种常量，方面统一修改
 * 
 */
public class Constant {
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
	/**
	 * 下载资源时传递参数的目录
	 */
	public final static String DOWNLOAD_PATH_PARAM = "/kingpad/download/";
	/**
	 * 保存的本地数据
	 */
	public final static String LOCAL_DATA = "localdata";
	
	
	//public final static String SERVER_URL = "http://10.0.2.2:8888/childrenStudy/";
	 public final static String SERVER_URL = "http://www.rxrj.com.cn:8888/childrenStudy/";
	//public final static String SERVER_URL = "http://192.168.5.102:8888/childrenStudy/";


	/*
	 * FLASH的种类
	 */
	public final static int TYPE_FLASH_YW = 1;
	public final static int TYPE_FLASH_NORMAL = 2;
	public final static int TYPE_FLASH_HUDONG = 3;
	public static final int TYPE_FLASH_ROLE = 4;
	public static final int TYPE_FLASH_Reading = 5;
	/*
	 * HTML页面
	 */
	public static final int VIEW_PRINT = 0x2100;
	/*
	 * 登陆视图
	 */
	public static final int VIEW_LOGIN = 0x0001;
	/*
	 * 主界面视图
	 */
	public static final int VIEW_MAIN = 0x0002;
	/*
	 * 学科主界面
	 */
	public static final int VIWE_MAJOR =0x0004;
	public static final int MAJOR_CHINESE =0x0011;
	public static final int MAJOR_MATH =0x00012;
	public static final int MAJOR_ENGLISH =0x0013;
	
	/*
	 * 激活视图
	 */
	public static final int VIEW_ACTIVATE = 0x0003;
	/*
	 * 设置教材视图
	 */
	public static final int VIEW_BOOK_SET = 0x0005;
	/*
	 * 更新教材视图
	 */
	public static final int VIEW_BOOK_UPDATE = 0x0006;
	/*
	 * 我的睿学视图
	 */
	public static final int VIEW_MYKINGPAD = 0x0009;
	/*
	 *	古诗背诵 视图
	 */
	public static final int VIEW_POEM_RECITE = 0x2010;
	/*
	 * 识字视图 
	 */
	public static final int VIEW_SHIZI = 0x2012;
	/*
	 * 辨析视图
	 */
	public static final int VIEW_STUDY_BIANXI = 0X2013;
	/*
	 * 学习类型
	 */
	//读字
	public static final String RESOURCE_TYPE_SubjectiveQuiz = "SubjectiveQuiz";
	//跟读
	public static final String RESOURCE_TYPE_Chinese_Memorization = "Chinese/Memorization";
	//选择
	public static final String RESOURCE_TYPE_ObjectiveQuiz = "ObjectiveQuiz";
	//背诵
	public static final String RESOURCE_TYPE_Chinese_Recite = "Chinese/Recite";
	//识字
	public static final String RESOURCE_TYPE_Chinese_SubjectiveQuiz_ShiZi = "Chinese/SubjectiveQuiz_ShiZi";
	public static final String RESOURCE_TYPE_English_SubjectiveQuiz = "English/SubjectiveQuiz";
	
	//录音FLASH
	public static final String RESOURCE_TYPE_Chinese_Read = "Chinese/Read";
	//普通FLASH
	public static final String RESOURCE_TYPE_Chinese_Flash_YW = "Chinese/Flash_YW";
	//普通FLASH
	public static final String RESOURCE_TYPE_Flash = "Flash";
	//无播放FLASH
	public static final String RESOURCE_TYPE_InteractiveFlash = "InteractiveFlash";
	//HTML
	public static final String RESOURCE_TYPE_Print = "Print";
	
	
	//FLASH播放的位置
	//互动FLASH
	public static final int X_FLASH_HUDONG = 145;
	public static final int Y_FLASH_HUDONG = 140; 
	
	//普通FLASH
	public static final int X_FLASH_NORMAL = 165;
	public static final int Y_FLASH_NORMAL = 102; 
	//录音FLASH
	public static final int X_FLASH_RECORD = 171;
	public static final int Y_FLASH_RECORD = 90; 

	//语文FLASH ，用于阅读高手等
	public static final int X_FLASH_YW = 0;
	public static final int Y_FLASH_YW = 0; 
	
	

	public final static String META_UTIL_DATA = "meta_util_data";
	
	/*
	 * 菜单视图
	 */
	public static final int VIEW_MENU = 0x1001;
	/*
	 * 学习视图
	 */
	public static final int VIEW_STUDY = 0x2000;
	/*
	 * FLASH视图
	 */
	public static final int VIEW_FLASH = 0x2001; 
	/*
	 * 观看的FLASH
	 */
	public static final int VIEW_FLASH_GUANKAN = 0x2002;
	/*
	 * 朗读的FLASH
	 */
	public static final int VIEW_FLASH_LANGDU = 0x2003;
	/*
	 * 英语SUB
	 */
	public static final int VIEW_ENGLISH_SUB = 0x2004;
	/*
	 * 英语拼写
	 */
	public static final int VIEW_ENGLISH_DictationQuiz = 0x2005;	
	/*
	 * 英语矫正视图
	 */
	public static final int VIEW_ENGLISH_Phonetic = 0x2006;
	/*
	 * 英语预习
	 */
	public static final int VIEW_ENGLISH_Memory = 0x2007;
	/*
	 * 英语较色扮演
	 */
	public static final int VIEW_ENGLISH_ROLE = 0x2008;
	/*
	 * 安全文件名
	 */
	public static String FILE_NAME_SECURITY = "FILE_NAME_SECURITY";
	/*
	 * 软件使用密码的键值
	 */
	public static String KEY_PASSWORD_SECURITY = "KEY_PASSWORD_SECURITY";/*
	 * 平板激活状态的键值
	 */
	public static final String KEY_ACTIVATION_SECURITY = "KEY_ACTIVATION_SECURITY";
	/*
	 *  虚拟像素
	 */
	static int  widthBase = 1024 ;
	static int  heightBase = 768 ;
	/*
	 *  实际像素
	 */
	static int  widthScreen;
	static int  heightScreen;
	/*
	 *  比例   实际/虚拟       用法： 虚拟坐标 *比例
	 */
	static float compareX;
	static float compareY;
	
	
	
	public static int getRightX(int x){
		return (int)(x*compareX);
	}

	public static int getRightY(int y){
		return (int)(y*compareY);
	}

	public static void setCompareIndex(){
		compareX = (float)widthScreen/widthBase;
		compareY = (float)heightScreen/heightBase;
		System.out.println("compareX="+compareX);
		System.out.println("compareY="+compareY);
	}
	
	
	/*
	 * SimpleCrypto 加密类使用密码
	 */
	public static String PASSWORD_SimpleCrypto = "PASSWORD_SimpleCrypto";

	/*
	 * SimpleCrypto 加密类使用密码——资源统一头部
	 */
	public static String Resource_Code_SimpleCrypto = "Resource_Code";
	// ---------------------该工程配置文件目录-----------------------------------
	
	/**
	 * 文件根目录
	 */
	public final static String KING_PAD_PATH = "mnt/sdcard/kingpad";
	/**
	 * 表示该程序的资源文件目录路径
	 */
	public final static String KING_PAD_RES = "mnt/sdcard/kingpad/assert/";
	
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
	/*
	 * 控制视图消息
	 */
	public static final int MESSAGE_CONTROLVIEW = 0x3001;
	/*
	 * 读音视图
	 */
	public static final int VIEW_Chinese_Memorization = 0x2019;
	/*
	 * 录音的事件
	 */
	public static final int EVENT_RECORD_PROGRESS = 0x555;
	/*
	 * 综合实力
	 */
	public static final int VIEW_ZONGHE = 0x2014;
	public static final int VIEW_SUBJECTIVE_QUIZ = 0x2015;
	/*
	 * OBJECTIVEQUIZI 视图
	 */
	public static final int VIEW_OBJECTIVEQUIZ = 0x2016;
	/*
	 *  正确MP3文件路径
	 */
	public static final String FILE_MP3_GOOD = "applaud.mp3";
	/*
	 *  错误MP3文件路径
	 */
	public static final String FILE_MP3_BAD = "failed.wav";
	/*
	 * 全部掌握的提示信息
	 */
	public static final String TEXT_ALL_UNDERSTAND = "你已经完成这个环节的学习,你可以浏览全部内容或返回其他学习环节。";
	public static final String TEXT_BEGIN_TO_LEARN = "请你先完成这一小节所有的内容的筛查,看看你是否都掌握了？";
	public static final String TEXT_REMAIN_SELECT = "有些内容你还没有进行筛查,请继续筛查。";
	/*
	 * 音频的标识
	 */
	public static final int AUDIO_MARK_APPLAUD = 1;
	public static final int AUDIO_MARK_FAILED = 2;
	public static final int AUDIO_MARK_BUTTON = 3;
	public static final int AUDIO_MARK_HINT_DOONE_ = 4;
	public static final int AUDIO_MARK_HINT_REMAIN_DO = 5;
	public static final int AUDIO_MARK_HINT_REMAIN_SELECT = 6;
	public static final int AUDIO_MARK_HINT_TO_LEARN = 7;
	public static final int NIZHIDAO = 8;
	public static final int QINGGEI = 9;
	
	public static final String path_guide1 = "mnt/sdcard/kingpad/assets/sound/你知道这个词的正确解释吗？.mp3";
	public static final String path_guide2 = "mnt/sdcard/kingpad/assets/sound/请给下面的问题选择正确的答案。.mp3";
	public static final String RESOURCE_TYPE_English_DictationQuiz = "English/DictationQuiz";
	public static final String RESOURCE_TYPE_English_Phonetic = "English/Phonetic";
	public static final Object RESOURCE_TYPE_English_Memory = "English/Memorization";
	public static final Object RESOURCE_TYPE_English_Role = "English/RolePlay";
	public static final Object RESOURCE_TYPE_English_Speaking = "English/Speaking";
	public static final String KEY_REGIST_SECURITY = "KEY_REGIST_SECURITY";
	/**
	 * 注册的数据
	 */
	public static final String REGIST_DATA = "regist_data";
	
	public static int getWidthBase() {
		return widthBase;
	}
	
	public static void setWidthBase(int widthBase) {
		Constant.widthBase = widthBase;
	}
	
	public static int getHeightBase() {
		return heightBase;
	}
	public static void setHeightBase(int heightBase) {
		Constant.heightBase = heightBase;
	}
	public static int getWidthScreen() {
		return widthScreen;
	}
	public static void setWidthScreen(int widthScreen) {
		System.out.println("设置屏幕宽度："+widthScreen);
		Constant.widthScreen = widthScreen;
	}
	public static int getHeightScreen() {
		return heightScreen;
	}
	public static void setHeightScreen(int heightScreen) {
		System.out.println("设置屏幕高度："+heightScreen);
		Constant.heightScreen = heightScreen;
	}
	public static float getCompareX() {
		return compareX;
	}
	public static void setCompareX(float compareX) {
		Constant.compareX = compareX;
	}
	public static float getCompareY() {
		return compareY;
	}
	public static void setCompareY(float compareY) {
		Constant.compareY = compareY;
	}
	
	
	
	
	
	
}
