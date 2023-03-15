package com.data;

import com.net.RequestParameter;

/**
 * 加载本地数据基类
 * @author lenovo
 *
 */
public abstract class BaseData {
	/**
	 * 返回状态码
	 */
	protected int mStatus;
	/**
	 * 解析json数据
	 * @param parameter ： 请求参数
	 * @throws Exception ： 抛出错误信息
	 */
	public abstract void startParse(final RequestParameter parameter) throws Exception;
	/**
	 * @return ： 返回状态码
	 */
	public int getStatus() {
		return this.mStatus;
	}
	
	static String getErrorMessage(final int status) {
		String rs = null;
		if(status == 0) {
			rs = "参数错误";
		}
		else if(status == 2) {
			rs = "密码错误";
		} 
		else if(status == 3) {
			rs = "产品未注册";
		}
		
		return rs;
	}
}
