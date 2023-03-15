package com.bean;

public class Game {
	public String image ;
	public String url;	//下载地址（APK）
	public String title;
	public String profile;
	public String file_state; //null——没有下载，"0"——未安装，"1"——已安装
	public boolean  canClick;	//按钮是否可以点击
	public boolean isDownLoading; //是否正在下载 
	public int progress; //进度
	public String save_path; //保存路径 
}
