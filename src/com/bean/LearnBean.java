package com.bean;
/**
 * 学习模式基本数据类型
 * @author lenovo
 *
 */
public class LearnBean {
	/**
	 * 字体颜色
	 */
	public String color;
	/**
	 * 字体
	 */
	public String font;
	/**
	 * 字体大小
	 */
	public String fontSize;
	/**
	 * 该标签中内容
	 */
	public String ownText;
	/**
	 * 该标签中可能的sourceFile数据，没有数据则为null
	 */
	public String file;
	
	public LearnBean() {}
	
	public LearnBean(LearnBean bean) {
		this.color	= 	bean.color;
		this.file	= 	bean.file;
		this.font	=	bean.font;
		this.fontSize	=	bean.fontSize;
		this.ownText	=	bean.ownText;
	}
}
