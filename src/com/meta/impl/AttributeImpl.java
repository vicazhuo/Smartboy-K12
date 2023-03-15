package com.meta.impl;

import java.util.ArrayList;

import com.meta.Attribute;

public class AttributeImpl implements Attribute {

	private ArrayList<AttrBean> mList;
	
	@Override
	public String getAttributeValue(String name) {
		if(mList == null)
			return null;
		if(name == null)
			return null;
		
		String value = null;
		for(int i = 0; i < mList.size(); i++) {
			if(name.equals(mList.get(i).name)) {
					value = mList.get(i).value;
					break;
			}
		}
		
		return value;
	}

	@Override
	public String getAttributeValue(int index) {
		if(mList == null)
			return null;
		return mList.get(index).value;
	}
	
	@Override
	public int getAttributeCount() {
		if(mList == null)
			return 0;
		
		return mList.size();
	}

	@Override
	public String getAttribueName(int index) {
		if(mList == null)
			return null;
		return mList.get(index).name;
	}
	
	protected void addAttribute(String name, String value) {
		if(name == null)
			throw new NullPointerException("属性名为null");
		if(mList == null) {
			mList = new ArrayList<AttributeImpl.AttrBean>(3);
		}
		
		AttrBean bean = new AttrBean(name, value);
		mList.add(bean);
	}
	
	
	class AttrBean {
		public  String name;
		public String value;
		
		public AttrBean(final String name, final String value) {
			this.name = name;
			this.value = value;
		}
	}

}
