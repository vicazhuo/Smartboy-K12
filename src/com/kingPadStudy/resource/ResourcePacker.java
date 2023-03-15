package com.kingPadStudy.resource;

/** 文件作用：资源压缩、解压工具。
			配合ResourceLoader使用
 *	创建时间：2012-11-17 下午8:23:44
 *  作者： 陈相伯 
 *  描述：
 */

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;


import android.util.Log;

/** 描述：资源压缩、解压工具。
			配合ResourceLoader使用
 * 
 */
public class ResourcePacker {

	/*

     * 这个是解压ZIP格式文件的方法

     * 

     * @zipFileName：是传进来你要解压的文件路径，包括文件的名字；

     * 

     * @outputDirectory:选择你要保存的路劲；

     * 

     */ 
	public static void Unzip(String zipFile, String targetDir) {
    	int BUFFER = 4096; //这里缓冲区我们使用4KB，
    	String strEntry; //保存每个zip的条目名称
    	try {
    		BufferedOutputStream dest = null; //缓冲输出流
    	    FileInputStream fis = new FileInputStream(zipFile);
    	    ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
    	    ZipEntry entry; //每个zip条目的实例
    	    while ((entry = zis.getNextEntry()) != null) {
    	    	try {
    	    		int count;
    	    		byte data[] = new byte[BUFFER];
    	    		strEntry = entry.getName();
    	    		File entryFile = new File(targetDir + strEntry);
    	    		File entryDir = new File(entryFile.getParent());
    	    		if (!entryDir.exists()) {
    	    			entryDir.mkdirs();
    	    		}
    	    		FileOutputStream fos = new FileOutputStream(entryFile);
    	    		dest = new BufferedOutputStream(fos, BUFFER);
    	    		while ((count = zis.read(data, 0, BUFFER)) != -1) {
    	    			
    	    			dest.write(data, 0, count);
    	    		}
    	    		dest.flush();
    	    		dest.close();
    	    	} catch (Exception ex) {
    	    		ex.printStackTrace();
    	    	}
    	    }
    	    zis.close();
    	} catch (Exception cwj) {
    		cwj.printStackTrace();
    	}
	}


}
