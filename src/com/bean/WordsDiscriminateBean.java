package com.bean;

import java.util.ArrayList;


/**
 * 识字1/尖子生拓展/字词辨析-使用的数据
 * @author swerit
 *
 */
public class WordsDiscriminateBean {

	public String soundFile;			//问题的语音
	public String text1;				//学习的主要内容1,显示给学生的信息
	
	public static final int FRESH = -1;			//还没有做过该题
	public static final int NOT_UNDERSTAND = 0;	//完全不会
	public static final int VAGUE = 1;			//模糊
	public static final int UNDERSTAND = 2;		//明白了
	public int result = -1;			//自己鉴定的结果：0-完全不会；1-模糊；2-明白了；-1  还没有做过该题
	
	public ArrayList<String> question = new ArrayList<String>();			//具有红色字的问题数据
	public int redIndex = -1;												//红色字的位置指针
	
	public ArrayList<String> option = new ArrayList<String>();				//答案的选项
	public int rightIndex = -1;				//正确答案的指针
	
	public ArrayList<String> answer = new ArrayList<String>();				//答案
	public int ansRedIndex = -1;											//答案中具有红色字的位置指针
}
