package com.meta.impl;

import com.bean.BasicBean;
import com.meta.Intro;

public final class IntroImpl extends AttributeImpl implements Intro{

	private BasicBean mIntro;
	@Override
	public BasicBean getBasicBean() {
		return mIntro;
	}

	protected void setIntro(BasicBean intro) {
		this.mIntro = intro;
	}
}
