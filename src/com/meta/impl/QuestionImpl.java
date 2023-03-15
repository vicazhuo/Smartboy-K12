package com.meta.impl;

import java.util.ArrayList;

import com.bean.BasicBean;
import com.meta.Question;

public final class QuestionImpl extends AttributeImpl implements Question {
	/**
	 * Question节点中额数据列表
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
	protected void addList(final ArrayList<BasicBean> list) {
		if(mList == null) {
			mList = new ArrayList<BasicBean>(2);
		}
		mList.addAll(list);
	}
	

}
