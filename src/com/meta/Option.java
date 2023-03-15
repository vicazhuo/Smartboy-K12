package com.meta;

import java.util.ArrayList;

import com.bean.BasicBean;

/**
 * option节点中的数据
 * @author lenovo
 *
 */
public interface Option extends Attribute{
	/**
	 * @return ： 返回option节点中的数据列表
	 */
	public ArrayList<BasicBean> getList();
}
