package com.net;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;

public class AsyncRequest 
{
	
	public static final String HTTP_POST = "POST";
	public static final String HTTP_GET = "GET";

	private static final int SET_CONNECTION_TIMEOUT = 20000;
	private static final int SET_SOCKET_TIMEOUT = 20000;

	
	public static String encodeGetParameters(RequestParameter parameters,boolean isPost)
	{
		if (parameters == null)
		{
			return "";
		}

		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (int loc = 0; loc < parameters.size(); loc++)
		{
			if (first)
				first = false;
			else
				sb.append("&");
			sb.append(parameters.getKey(loc) + "="
					+ parameters.getValue(loc));
		}
		return sb.toString();
	}
	
	
	public static String encodePostParameters(RequestParameter httpParams)
	{
		if (null == httpParams || httpParams.size() == 0)
		{
			return "";
		}
		StringBuilder buf = new StringBuilder();
		int j = 0;
		for (int loc = 0; loc < httpParams.size(); loc++)
		{
			String key = httpParams.getKey(loc);
			if (j != 0)
			{
				buf.append("&");
			}
			try
			{
				buf.append(URLEncoder.encode(key, "UTF-8"))
						.append("=")
						.append(URLEncoder.encode(httpParams.getValue(key),
								"UTF-8"));
			}
			catch (java.io.UnsupportedEncodingException neverHappen)
			{
			}
			j++;
		}
		return buf.toString();

	}
	
	/**
	 * 打开一个url，同时返回请求数据
	 * @throws Exc 
	 */
	public static String openUrl(String url, String method,
			RequestParameter parameter) throws Exception
	{
		BasicHttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters,
				AsyncRequest.SET_CONNECTION_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParameters,
				AsyncRequest.SET_SOCKET_TIMEOUT);
		HttpClient client = new DefaultHttpClient(httpParameters);

		String rlt = null;
			HttpUriRequest request = null;
			ByteArrayOutputStream boStream = null;
			if (method.equals("GET"))
			{
				url = url + "?" + encodeGetParameters(parameter,false);
//				//TODO
//				System.out.println("openUrl中，url="+url);
				HttpGet get = new HttpGet(url);
				request = get;
			}
			else if (method.equals("POST"))
			{
				HttpPost post = new HttpPost(url);
				byte[] data = null;
				boStream = new ByteArrayOutputStream(1024 * 50);

				post.setHeader("Content-Type",
						"application/x-www-form-urlencoded");
				String postParam = encodePostParameters(parameter);
				data = postParam.getBytes("UTF-8");
				boStream.write(data);

				data = boStream.toByteArray();
				boStream.close();
				ByteArrayEntity formEntity = new ByteArrayEntity(data);
				post.setEntity(formEntity);
				request = post;
			}
			
			request.setHeader("User-Agent", System.getProperties()
					.getProperty("http.agent"));
			HttpResponse response = client.execute(request);
			StatusLine status = response.getStatusLine();
			int statusCode = status.getStatusCode();
			if (statusCode == 200)
			{
				rlt = read(response);
			}
			else
			{
				throw new Exception("数据请求返回结果错误！");
			}
			
		return rlt;
	}

	private static String read(HttpResponse response) throws Exception
	{
		//String result = null;
		HttpEntity entity = response.getEntity();
		InputStream inputStream;
		
		inputStream = entity.getContent();
		ByteArrayOutputStream content = new ByteArrayOutputStream();

		Header header = response.getFirstHeader("Content-Encoding");
		if (header != null
				&& header.getValue().toLowerCase().indexOf("gzip") > -1)
		{
			inputStream = new GZIPInputStream(inputStream);
		}

		// Read response into a buffered stream
		int readBytes = 0;
		byte[] sBuffer = new byte[10240];
		while ((readBytes = inputStream.read(sBuffer)) != -1)
		{
			content.write(sBuffer, 0, readBytes);
		}
		if(content.size() == 0)
			return null;
		else 
			return new String(content.toByteArray());
		
	} // read
}
