package com.meta;

import java.util.ArrayList;

import com.bean.BasicBean;
/**
 * 获取question节点中的数据
 * @author lenovo
 *
 */
public interface Question extends Attribute{
	/**
	 * @return : 返回该节点中的数据列表
	 */
	public ArrayList<BasicBean> getList();
}
