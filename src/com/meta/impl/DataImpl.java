package com.meta.impl;

import java.util.ArrayList;

import org.xml.sax.helpers.AttributesImpl;

import com.meta.Attribute;
import com.meta.Data;
import com.meta.Items;
/**
 * 解析数据data.xml文件接口实现
 * @author lenovo
 *
 */
public final class DataImpl extends AttributeImpl implements Data {

	/**
	 * items列表节点
	 */
	private ArrayList<Items> mList;
	
	protected void addList(final Items items) {
		if(mList == null) {
			// 设置该处list长度大小为2，一般大小为1
			mList = new ArrayList<Items>(2);
		}
		
		mList.add(items);
	}

	@Override
	public ArrayList<Items> getItemsList() {
		// TODO Auto-generated method stub
		return mList;
	}

}
