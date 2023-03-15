package com.bean;

import java.util.ArrayList;

/**
 * 该处为文件目录的递归树
 * 
 * 在大类下面的小类，例如在《一级》->《学习》
 * 操作时根据里面的type字段值
 * @author lenovo
 */
public class CatalogBean extends BaseBean{
	/**
	 * 表示目录
	 */
	public final static int CATALOG = 1;
	/**
	 * 最终节点数据
	 */
	public final static int SECTION = 2;
	/**
	 * package包
	 */
	public final static int PACKAGE = 3;
	
	/**
	 * type_msg == CATALOG,通过catalogList获取数据。
	 * type_msg == SECTION,通过sectionList获取数据。
	 * type_msg	==	PACKAGE,通过packageList获取数据。
	 * 
	 * 改值时判断，当前目录的下一级目录的属性，不是当前目录属性
	 */
	public int type_msg;
	/**
	 * 该字段在type == CATALOG时有效，否则返回null
	 */
	public ArrayList<CatalogBean> catalogList;
	/**
	 * 该字段在type == SECTION时有效，否则返回null
	 */
	public ArrayList<CatalogBean> sectionList;
	/**
	 * 该数据为包数据目录
	 */
	public ArrayList<PackageBean> packageList;
}
