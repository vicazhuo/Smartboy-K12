package com.meta;

import java.util.ArrayList;

import com.bean.BasicBean;

/**
 * data.xml数据文件中的items的数据节点
 * @author lenovo
 *
 */
public interface Items extends Attribute{
	/**
	 * @return ： 返回每一个items的介绍头数据
	 */
	public Intro getIntro();
	/**
	 * @return ： 返回items中的每一个基本item对象
	 */
	public ArrayList<Item> getItemList();
	/**
	 * @return : 返回一个items下面的items数据节点
	 * -- package\语文小天才\一级\文学常识积累（一）\居庸关
	 */
	public Items getItems();
}
