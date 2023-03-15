package com.meta.impl;

import java.util.ArrayList;

import com.meta.Intro;
import com.meta.Item;
import com.meta.Items;
/**
 * items数据节点数据对象
 * @author lenovo
 *
 */
public final class ItemsImpl extends AttributeImpl implements Items {
	
	/**
	 * 介绍
	 */
	private IntroImpl mIntro;
	/**
	 * 数据item列表，长度一般不固定
	 */
	private ArrayList<Item> mList;
	/**
	 * 每一个items下面的items节点
	 */
	private ItemsImpl mItemsImpl;
	

	@Override
	public Intro getIntro() {
		return mIntro;
	}

	@Override
	public ArrayList<Item> getItemList() {
		return mList;
	}
	
	@Override
	public Items getItems() {
		return mItemsImpl;
	}
	
	protected void setIntro(final IntroImpl intro) {
		this.mIntro = intro;
	}
	
	protected void addList(final Item item) {
		if(mList == null) {
			mList = new ArrayList<Item>();			
		}
		
		mList.add(item);
	}

	protected void setItems(final ItemsImpl items) {
		this.mItemsImpl = items;
	}
	

}
