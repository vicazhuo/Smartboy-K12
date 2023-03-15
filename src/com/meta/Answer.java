package com.meta;

import java.util.ArrayList;

import com.bean.BasicBean;

/**
 * 解析answer节点中的数据
 * @author lenovo
 *
 */
public interface Answer extends Attribute{
	/**
	 * @return ： 返回answer节点中的数据列表
	 */
	public ArrayList<BasicBean> getList();
}
