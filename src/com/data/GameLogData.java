package com.data;

import com.constant.Constant;
import com.net.AsyncRequest;
import com.net.RequestParameter;
/**
 * 处理游戏记录
 * 请求参数：
 * productNumber=?
 * id = ?
 * @author <a href="mailto:udmjh0508@126.com">莫建华</a>
 * @created 2013-3-14
 */
public final class GameLogData extends BaseData {

	@Override
	public void startParse(RequestParameter parameter) throws Exception {
		AsyncRequest.openUrl(Constant.SERVER_URL + "business/services!downloadRsProduct.action",
				AsyncRequest.HTTP_POST, parameter);
	}

}
