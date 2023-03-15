package com.bean;

import java.util.ArrayList;

/**
 * 学习模式数据bean
 * @author lenovo
 *
 */
public class LearnModeBean {
	/**
	 * 问题
	 */
	public LearnBean question;
	/**
	 * 选择题类型中的几个选项，默认为4个
	 */
	public ArrayList<LearnBean> option;
	/**
	 * 问题答案
	 */
	public LearnBean answer;
}
