package com.bean;

/**
 * 学前筛查里面使用的每一个学习界面中的信息
 * @author swerit
 *
 */
public class ScreenBean {

	public String text1;				//学习的主要内容1,显示给学生的信息
	public String text2;				//学习的主要内容2，学生看答案之后可以看见的信息
	public String soundFile;			//答案语音文件的路径
	public String soundFile1;			//有写文件数据中有两种声音，这里是多添加一种，适应两种声音的模式
	public int soundRepeat = 1;			//语音播放重复次数，默认读一次
	public int soundDelay = 0;			//语音重复播放的时间间隔，秒为单位
	
	
	public static final int FRESH = -1;			//还没有做过该题
	public static final int NOT_UNDERSTAND = 0;	//完全不会
	public static final int VAGUE = 1;			//模糊
	public static final int UNDERSTAND = 2;		//明白了
	public int result = -1;			//自己鉴定的结果：0-完全不会；1-模糊；2-明白了；-1  还没有做过该题
	
	
//	public String buttonSoundFile;		//按钮声音文件的路径
}
