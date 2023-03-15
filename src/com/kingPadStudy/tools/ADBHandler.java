/** 文件作用：
 *	创建时间：2013-1-29 下午4:36:58
 *  作者： 陈相伯 
 *  描述：
 */
package com.kingPadStudy.tools;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/** 描述：
 * 
 */
public class ADBHandler {
	 /** 作用：调用SHELL
		 * 时间：2013-1-29 下午3:41:44
		 * void
		 */
		private void callAdbShell() {
			//命令数组
			String[] args = new String[3];
			args[0] = "touch file_temp_cxb";
			 args[1] = "ls";
			 args[2] = "-l";
			 try
			 {
			  Process process = Runtime.getRuntime().exec(args);
			  InputStream stderr = process.getErrorStream();
			  InputStreamReader isrerr = new InputStreamReader(stderr);
			  BufferedReader brerr = new BufferedReader(isrerr);
			  //get the output line  
			  InputStream outs = process.getInputStream();
			  InputStreamReader isrout = new InputStreamReader(outs);
			  BufferedReader brout = new BufferedReader(isrout);
			  String line = null;
			  String result = "";
			  // get the whole error message string  
			  while ( (line = brerr.readLine()) != null)
			  {
				  result += line;
				  result += "/n/n";
			  } 
			  if( result != "" )
			  {
				  //System.out.println("………………………………………………………………");
				  //System.out.println("………………………………………………………………");
				  //System.out.println("………………………………………………………………");
				  //System.out.println("………………………………………………………………");
				  //System.out.println("………………………………………………………………");
				  //System.out.println("执行命令的error结果："+result);
			   // put the result string on the screen
			  }
			  // get the whole standard output string
			  while ( (line = brout.readLine()) != null)
			  {
			   result += line;
			   result += "/n/n";
			  }
			  if( result != "" )
			  {
				  //System.out.println("………………………………………………………………");
				  //System.out.println("………………………………………………………………");
				  //System.out.println("………………………………………………………………");
				  //System.out.println("………………………………………………………………");
				  //System.out.println("………………………………………………………………");
				  //System.out.println("执行命令的output结果："+result);
			   // put the result string on the screen
			  }
			 }catch(Throwable t)
			 {
			  t.printStackTrace();
			 }
		}

}
