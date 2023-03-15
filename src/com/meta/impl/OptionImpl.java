package com.meta.impl;

import java.util.ArrayList;

import org.xml.sax.helpers.AttributesImpl;

import com.bean.BasicBean;
import com.meta.Option;
/**
 * option节点中的数据
 * @author lenovo
 *
 */
public final class OptionImpl extends AttributeImpl implements Option {

	/**
	 * option节点中的数据
	 */
	private ArrayList<BasicBean> mList;
	
	@Override
	public ArrayList<BasicBean> getList() {
		return mList;
	}
	
	protected void addList(final BasicBean basic) {
		if(mList == null) {
			mList = new ArrayList<BasicBean>(1);
		}
		
		mList.add(basic);
	}
	
	protected void addList(ArrayList<BasicBean> list) {
		if(mList == null) {
			mList = new ArrayList<BasicBean>(2);
		}
		
		mList.addAll(list);
	}
}
