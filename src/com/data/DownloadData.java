package com.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import com.constant.Constant;
import com.net.RequestParameter;
/**
 * 下载资源接口
 * @author <a href="mailto:udmjh0508@126.com">莫建华</a>
 * @created 2013-1-20
 * @version V-1.0
 */
public final class DownloadData extends BaseData {

	/**
	 * 转发的链接地址
	 */
	private String mUrl;
	
	@Override
	public void startParse(RequestParameter parameter) throws Exception {
		final String resourceId = parameter.getValue("resourceId");
		if(resourceId == null || resourceId.equals(""))
			throw new NullPointerException("参数错误");
		final String productNumber = parameter.getValue("productNumber");
		if(productNumber == null || productNumber.equals("")) 
			throw new NullPointerException("参数错误");
		final String url = Constant.SERVER_URL + "business/services!downLoadOld.action?" +
		"productNumber=" + productNumber + "&resourceId=" + resourceId;
		getConnection(url);
	}
	
	private void getConnection(final String urlString) throws Exception{
		URL url = new URL(urlString);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setDoOutput(true);
		connection.setRequestMethod("GET");
		connection.setUseCaches(false);
		connection.setConnectTimeout(5000);
		connection.setReadTimeout(5000);
		connection.setInstanceFollowRedirects(false);
		connection.connect();
		if(connection.getResponseCode() == 302)
		{
			mUrl = connection.getHeaderField("Location");
			mStatus = 2;				// 标志当前资源数据获取成功
		}
		else {
			String rs = getWebSource(connection.getInputStream(), "utf-8");
			JSONObject json = new JSONObject(rs);
			this.mStatus = json.getInt("status");
			//throw new Exception(getErrorMessage(mStatus));
		}
	}
	
	protected String getWebSource(final InputStream inputStream, final String encode) throws IOException
	{
		// 获取网页源代码
		InputStreamReader inputStreamReader = new InputStreamReader(inputStream, encode);
		BufferedReader reader = new BufferedReader(inputStreamReader);
		
		StringBuffer buffer = new StringBuffer();
		String line = null;
		while ( ( line = reader.readLine() ) != null)
		{
			buffer.append(line + "\n");
		}
		reader.close();
		inputStreamReader.close();
		inputStream.close();
		
		return buffer.toString();
	}
	
	public String getUrl() {
		return this.mUrl;
	}

}
