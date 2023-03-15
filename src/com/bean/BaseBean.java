package com.bean;


/**
 * 文件目录基本属性
 * @author lenovo
 *
 */
public class BaseBean {
	
	/**
	 * 文件路径
	 */
	public String path;
	/**
	 * 文件标题
	 */
	public String title;
	/**
	 * 文件图标
	 */
	public String icon;
	/**
	 * 作者，在目录结构中没有该数据，该数据为null
	 */
	public String intro;
	/**
	 * 数据分类
	 */
	public String type;
	/**
	 * mode值
	 */
	public String mode;
	
	/**
	 * 该目录背景，没有则为null
	 */
	public String bg;
}
