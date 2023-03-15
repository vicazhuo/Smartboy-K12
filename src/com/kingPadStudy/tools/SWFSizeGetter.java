package com.kingPadStudy.tools;


import java.io.FileInputStream;

import android.R.integer;

public class SWFSizeGetter {

	public static  Demension  getFlashSize(String fileName){
		Demension d=null;
		try {
			FileInputStream is=new FileInputStream(fileName); 
			int size = is.available(); 
			if(size > 5000000){
				System.out.println("文件大于5M");
				d = new Demension(1021, 721);
				return d;
			}
			byte[] oldBytes=new byte[30];
			is.read(oldBytes);
			is.close(); 
			String type=new String(oldBytes).substring(0,3);
			if(type.equals("FWS")){
				d=SwfUtils.getPreferredSize2(oldBytes);
			}
			else if(type.equals("CWS")){
				byte [] newBytes=new SWFDecompressor().readFile2(fileName);
				d=SwfUtils.getPreferredSize2(newBytes);
			}
			else{
				System.out.println("格式不正确,type:"+type);
			}
			System.out.println("SWF的大小:"+type+" size:"+(int)d.getWidth()+"*"+(int)d.getHeight());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return d;
	}
}
