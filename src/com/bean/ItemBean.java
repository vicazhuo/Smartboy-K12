package com.bean;
/**
 * 解析data.xml文件时的元数据，只解析了.xml,.swf, .mp3格式的数据，其他数据不解析
 * 在该资源中资源分为三类
 * @author mjh
 *
 */
public class ItemBean {
	/**
	 * 表示该文件的类型为xml文件
	 */
	public final static int XML = 1;
	/**
	 * 表示该文件类型为swf文件
	 */
	public final static int SWF = 2;
	/**
	 * 表示该文件类型为mp3类型
	 */
	public final static int MP3 = 3;
	/**
	 * 表示htm文件类型
	 */
	public final static int HTM = 4;
	/**
	 * 资源文件名
	 */
	public String title;
	/**
	 * 该资源分类
	 */
	public int type;

}
