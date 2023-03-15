package com.data;

import com.meta.Data;
import com.meta.impl.MetaUtil;
import com.net.RequestParameter;

public class MetaUtilData extends BaseData{

	private Data data;
	
	@Override
	public void startParse(RequestParameter parameter) throws Exception {
		String path = parameter.getValue("path");
		if(path == null || path.equals(""))
			throw new NullPointerException("请求参数为path为null");
		String type = parameter.getValue("type");
		if(type == null || type.equals(""))
			throw new NullPointerException("请求参数type为null");
		String mode = parameter.getValue("mode");
		data = MetaUtil.analyzeSubjective(path);
	}
	
	
	public Data getData() {
		return this.data;
	}
	
	
}
