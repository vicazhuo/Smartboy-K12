package com.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Random;

import org.apache.tools.ant.taskdefs.Manifest.Section;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

import android.R.integer;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.Log;
import android.widget.Toast;

import com.bean.CatalogBean;
import com.bean.PackageBean;
import com.data.BookData;
import com.kingPadStudy.activity.KingPadStudyActivity;
import com.kingPadStudy.constant.Constant;
import com.kingPadStudy.tools.FileHandler;
import com.kingPadStudy.tools.SimpleCrypto;
import com.kingpad.R;
import com.net.LoadData;


/**
 * 程序所用工具包
 * @author lenovo
 *
 */
public class Util {
	
	/**
	 * 将资源加密
	 */
	public static void encodeResource(final Context context,
			final String resourceName,String unzipPath) {
//		//System.out.println("encodeResource，resourceName="
//				+ resourceName+",unzipPath="+unzipPath);
		// 数据库操作完毕，将资源加密。大小和目录内容
		final String folder_path = unzipPath +"/"
				+ resourceName;
		String path_root = unzipPath+"/";
		//System.out.println("path_root="+path_root);
		java.io.File file_folder = new java.io.File(folder_path);
		double size = 0;
		try {
			//计算资源文件夹的大小
			size = com.utils.Util.getSize(file_folder);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		//精确到一定的位数
		DecimalFormat df = new DecimalFormat("#.00");
		//System.out.println("精确2位的数字："+df.format(size));
		String string_size = df.format(size);
		float float_size = Float.parseFloat(string_size);
		//System.out.println("还原后的数字："+float_size);
		final String seed = KingPadStudyActivity
				.getAndroidId(); 
		//System.out.println("加密文件夹size=" + string_size);
		String sizeEncode = null;
		try {
			sizeEncode = SimpleCrypto.encrypt(
					seed, string_size);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		FileHandler.setStringToFile(context,
				"size_" + folder_path,
				sizeEncode); // 加密大小
		String path = folder_path;
		Log.e("tag", "path=" + path);
		com.net.RequestParameter parameter = new com.net.RequestParameter();
		parameter.add("path", path);
		parameter.add("packagePath", path_root);
		final com.net.RequestListener r = new com.net.RequestListener(){
			public void onError(String error) { 
				Toast.makeText(context, "异常：" + error,
						0).show();
			}
			public void onComplete(Object obj) {
				//System.out.println("获取到index.xml…………………………………………………………");
				BookData bigIndex = (BookData) obj;
				// 获取10个随机数，组成一个随机数字符串，加密后存入文件中
				int[] radoms = com.utils.Util
						.getRadoms(10, 6);
				String string_radoms = "";
				for (int i = 0; i < 10; i++) {
					string_radoms += radoms[i];
				}
				String radoms_encode;
				//System.out.println("获得的随机数字符串是："+string_radoms);
				try {
					radoms_encode = SimpleCrypto
							.encrypt(seed, string_radoms);
					// 将这个字符串存入文件
					FileHandler
							.setStringToFile(
									context,
									"content_numbers_"
											+folder_path,
									radoms_encode);
				} catch (Exception e) {
					e.printStackTrace();
				}
				//根据10个位置获取标题字符串金刚
				String kingkang_title = com.utils.Util.getTitleKingKang(radoms,bigIndex);
				try {
					String kingkang_encode = SimpleCrypto.encrypt(seed, kingkang_title);
					//System.out.println("获取到的金刚字符串是："+kingkang_title);
					FileHandler
					.setStringToFile(
							context,
							"content_titles_"
									+ folder_path,
									kingkang_encode);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		LoadData.loadData(com.constant.Constant.BOOK_DATA, parameter, r);		
	}

 
	

	/**
	 * 获取一个数组
	 * @param number
	 * @param max
	 * @return
	 */
	public static int[] getRadoms(int number,int max){
		int[] res = new int[number];
		 Random random = new Random();
        for(int i = 0; i < number;i++) {
        	res[i] = Math.abs(random.nextInt())%max;
        }
		return res;
	}
	
	
	/**
	 * 从一个字符串中提取数字数组 
	 * @param numbers
	 * @return
	 */
	public static  int[] getRadomNumbers(String numbers){
		int [] res;
		int length = numbers.length();
		res = new int[length];
		int rad;
		for(int i = 0 ;i <length;i++){
			rad = Integer.parseInt(numbers.charAt(i)+"");
			res[i] = rad; 
		}
		return res;
		
	}
	
	

    /**  
     * 计算文件或者文件夹的大小 ，单位 MB  
     * @param file 要计算的文件或者文件夹 ， 类型：java.io.File  
     * @return 大小，单位：MB  
     */  
    public static double getSize(java.io.File file) {   
        //判断文件是否存在   
        if (file.exists()) {   
            //如果是目录则递归计算其内容的总大小，如果是文件则直接返回其大小   
            if (!file.isFile()) {   
                //获取文件大小   
            	java.io.File[] fl = file.listFiles();   
                double ss = 0;   
                for (java.io.File f : fl)   
                    ss += getSize(f);   
                return ss;   
            } else {   
                double ss = (double) file.length() / 1024 / 1024;   
                return ss;   
            }   
        } else {   
            //System.out.println("文件或者文件夹不存在，请检查路径是否正确！");   
            return 0.0;   
        }   
    }   
	
	private static MediaPlayer mPlayer = new MediaPlayer();			//播放声音
	
	/**
	 * 解压带zip格式的文件包
	 * @param archive ： zip文件路径
	 * @param decompressDir ： 解压之后文件路径，不含'/'
	 * @throws Exception
	 */
	public static void readByApacheZipFile(String archive, String decompressDir) 
			throws Exception {
		BufferedInputStream bi; 
		ZipFile zf = new ZipFile(archive, "GBK");//支持中文
		Enumeration e = zf.getEntries();
		while (e.hasMoreElements()) {
			ZipEntry ze2 = (ZipEntry) e.nextElement();
			String entryName = ze2.getName();
			String path = decompressDir + "/" + entryName;
			if (ze2.isDirectory()) {
				File decompressDirFile = new File(path);
				if (!decompressDirFile.exists()) 
				{
					decompressDirFile.mkdirs();
				}
			}
			else 
			{
				String fileDir = path.substring(0, path.lastIndexOf("/"));
				File fileDirFile = new File(fileDir);
				if (!fileDirFile.exists()) 
				{
					fileDirFile.mkdirs();
				}
				BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(decompressDir + "/" + entryName));
				bi = new BufferedInputStream(zf.getInputStream(ze2));
				byte[] readContent = new byte[1024];
				int readCount = bi.read(readContent);
				while (readCount != -1) 
				{
					bos.write(readContent, 0, readCount);
					readCount = bi.read(readContent);
				}
				bos.close();
			}
		}
		zf.close();
	}
	
	
//	/**
//    * 该函数的功能为解压ZIP文件到指定的目录下。
//    * @throws Exception
//    */
//    public int upZipFile(File zipFile, String folderPath)throws ZipException,IOException {
//            ZipFile zfile=new ZipFile(zipFile);
//            @SuppressWarnings("rawtypes")
//			Enumeration zList=zfile.entries();
//            ZipEntry ze=null;
//            
//            byte[] buf=new byte[1024];
//            while(zList.hasMoreElements()){
//            	
//                ze=(ZipEntry)zList.nextElement();
//                if(ze.isDirectory()){
//                    String dirstr = folderPath + ze.getName();
//                    dirstr = new String(dirstr.getBytes());
//                    File f=new File(dirstr);
//                    f.mkdir();
//                    continue;
//                } // if
//
//                OutputStream os=new BufferedOutputStream(new FileOutputStream(getRealFileName(folderPath, ze.getName())));
//                InputStream is=new BufferedInputStream(zfile.getInputStream(ze));
//                
//                int readLen=0;
//                while ((readLen=is.read(buf, 0, 1024))!=-1) {
//                    os.write(buf, 0, readLen);
//                }
//                is.close();
//                os.close();
//            }
//        zfile.close();
//        return 0;
//    }
//
//    /**
//    * 给定根目录，返回一个相对路径所对应的实际文件名.
//    * @param baseDir 指定根目录
//    * @param absFileName 相对路径名，来自于ZipEntry中的name
//    * @return java.io.File 实际的文件
//    */
//    public static File getRealFileName(String baseDir, String absFileName){
//        String[] dirs=absFileName.split("/");
//        File ret=new File(baseDir);
//        String substr = null;
//        if(dirs.length>1){
//            for (int i = 0; i < dirs.length-1;i++) {
//                substr = dirs[i];
//                substr = new String(substr.getBytes());
//                ret=new File(ret, substr);
//            }
//            if(!ret.exists())
//                    ret.mkdirs();
//            
//            substr = dirs[dirs.length-1];
//			 substr = new String(substr.getBytes());
//
//            ret=new File(ret, substr);
//            return ret;
//        }
//        return ret;
//    }
    
    /**
     * 用于解析是判断该图片是应用程序目录还是当前目录下
     * @param path ： 当前目录程序path
     * @param text ： 获取到的图片相对path路径
     * @return ：图片数据的真实path
     */
    public static String dealResPath(final String path, String text) {
		if(text != null && text.contains("app:/assets")) {
			return text.replaceAll("app:/assets", Constant.KING_PAD_RES);
		}
		return path + "/" + text;
	}
    
    
    /**
	 * 播放声音
	 * @param soundPath：声音文件在sd卡上的路径
	 */
    public static void playSound(String soundPath,boolean isLow) throws IllegalArgumentException, SecurityException, IllegalStateException, IOException{
    	if(mPlayer == null){
    		mPlayer = new MediaPlayer();
    	}
    	mPlayer.reset();
		mPlayer.setDataSource(soundPath);
		if(isLow){
			mPlayer.setVolume(0.4f, 0.4f);
		}
		mPlayer.prepare();
		mPlayer.start();
    }
    
    public static int getSoundDuration(String soundPath){
    	if (null == soundPath || "".equals(soundPath))
    		return 0;
    	MediaPlayer mPlayer = new MediaPlayer();
    	try {
			mPlayer.setDataSource(soundPath);
			mPlayer.prepare();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return mPlayer.getDuration();
    }
    
    /**
     * 函数作用：播放背景音乐
     * void
     * @param context
     * @param id
     * @throws IOException 
     */
    public static void playBackGroundSound(Context context,String file,boolean isLoop)  {
    	AssetManager assetManager = context.getAssets();
    	AssetFileDescriptor fileDescriptor;
		try {
			fileDescriptor = assetManager.openFd(file);
	    	mPlayer = new MediaPlayer();
	    	mPlayer.setLooping(isLoop);
	    	mPlayer.setDataSource(
	    			fileDescriptor.getFileDescriptor(),
	    			fileDescriptor.getStartOffset(),
	    			fileDescriptor.getLength() );
	    	mPlayer.prepare();
	    	mPlayer.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public static void stopBackGroundSound(){
    	if(mPlayer!=null){
    		mPlayer.stop();
    		mPlayer.release();
    	}
    }
    
    
    /**
     * 函数作用：停止音乐
     * void
     */
    public static void stopHint(){
    	if(mPlayer!=null){
    		mPlayer.stop();
    	}
    }
    
    
    /**
	 * 释放音乐播放
	 */
    public static void releasePlayer(){
    	if(null != mPlayer){
    		mPlayer.stop();
			mPlayer.release();
			mPlayer = null;
    	}
	}
    
    /**
     * 将flash路径进行编码，flash文件路径示例：/mnt/sdcard/精讲_山行.swf
     * @param filePath：flash文件的路径
     * @return：经过编码的flash文件路径
     * @throws UnsupportedEncodingException
     */
    public static String encodeFilePath(String filePath)  {
     StringBuffer stringBuffer = new StringBuffer();
     String nodes[] = filePath.split("/");
     for (int i=1; i<nodes.length; i++){
	     stringBuffer.append("/");
	     try {
			stringBuffer.append(URLEncoder.encode(nodes[i], "utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
     }
     
     return stringBuffer.toString();
    }
    
    
   public static MediaPlayer getMediaPlayer(){
	   return mPlayer;
   }

   /**
    * 函数作用：特殊播放
    * void
    * @param mp3_intro
    */
	public static void playSound_OnComplete(String mp3_intro) {
		player_onComplete = new MediaPlayer();
		player_onComplete.setVolume(0.5f,0.5f);
		try {
			player_onComplete.setDataSource(mp3_intro);
			player_onComplete.prepare();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	player_onComplete.start();
	}
     
    public static MediaPlayer player_onComplete;
    
    
    /**
     * 函数作用：播放assets中的mp3
     * void
     * @param context
     * @param state 
	 *	  Constant.AUDIO_MARK_HINT_DOONE_   ——完成循环学习 
	 *    Constant.AUDIO_MARK_HINT_REMAIN_DO   ——还有些题没有掌握   
	 *    Constant.AUDIO_MARK_HINT_REMAIN_SELECT  ——还有些题没有筛查
	 *    Constant.AUDIO_MARK_HINT_TO_LEARN   ——开始学习
     */
	public static void playSoundPool(Context context,int state) {
		//载入音频流
		switch(state){
		case Constant.AUDIO_MARK_APPLAUD:
			soundPool.play(row_appaud, 1, 1, 0, 0, 1); 
			break;
		case Constant.AUDIO_MARK_FAILED:
			soundPool.play(row_failed, 1, 1, 0, 0, 1); 
			break;
		case Constant.AUDIO_MARK_BUTTON:
			soundPool.play(row_button, 1, 1, 0, 0, 1); 
			break;
		case Constant.AUDIO_MARK_HINT_DOONE_:
			soundPool.play(row_hint_done, 1, 1, 0, 0, 1);
			id_playing = row_hint_done; 
			break;
		case Constant.AUDIO_MARK_HINT_REMAIN_DO:
			soundPool.play(row_hint_remain_do, 1, 1, 0, 0, 1); 
			id_playing = row_hint_remain_do; 
			break;
		case Constant.AUDIO_MARK_HINT_REMAIN_SELECT:
			soundPool.play(row_hint_remian_select, 1, 1, 0, 0, 1); 
			id_playing = row_hint_remian_select; 
			break;
		case Constant.AUDIO_MARK_HINT_TO_LEARN:
			soundPool.play(row_hint_to_learn, 1, 1, 0, 0, 1);
			id_playing = row_hint_to_learn; 
			break;
		case Constant.NIZHIDAO:
			soundPool.play(row_nishidao, 1, 1, 0, 0, 1);
			id_playing = row_nishidao; 
			break;
		case Constant.QINGGEI:
			soundPool.play(row_qinggei, 1, 1, 0, 0, 1);
			id_playing = row_qinggei; 
			break;
		}
	}
	
	/**
	 * 函数作用：播放mp3列表中的一个mp3
	 * void
	 * @param context
	 * @param index 
	 */
	public static void playSoundPoolMp3(Context context,int index){
		if(soundPool != null){
			soundPool.stop(id_playing);
			id_playing =  soundPool.play(mp3List[index], 1, 1, 0, 0, 1); 
		}else{
			if(soundPool == null){
				initSoundPool(context);
			}
		}
	}
	 
	/**
	 * 函数作用：播放mp3列表中的一个mp3
	 * void
	 * @param context
	 * @param index
	 */
	public static void playSoundPoolAnswerMp3(Context context,int index){
		if(soundPool == null){
			initSoundPool(context);
		}
		soundPool.stop(id_playing);
		id_playing = soundPool.play(answerMp3List[index], 1, 1, 0, 0, 1); 
	}
	
   /**
    * 函数作用：初始化音频池
    * void
    */
   public static void initSoundPool(Context context){
	   if(soundPool!=null ){
		   return ;
	   }
		soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC,0);
		row_appaud = soundPool.load(context, R.raw.applaud, 0);
		row_failed = soundPool.load(context, R.raw.failed, 0);
		row_button = soundPool.load(context, R.raw.button, 0);
		row_hint_done = soundPool.load(context, R.raw.hint_done, 0);
		row_hint_remain_do = soundPool.load(context, R.raw.hint_remain_do, 0);
		row_hint_remian_select = soundPool.load(context, R.raw.hint_remani_select, 0);
		row_hint_to_learn = soundPool.load(context, R.raw.hint_to_learn, 0);
		row_nishidao = soundPool.load(context, R.raw.nishidao, 0);
		row_qinggei = soundPool.load(context, R.raw.qinggei, 0);
		soundPool.setVolume(row_appaud,0.5f,0.5f);
		soundPool.setVolume(row_failed,0.5f,0.5f);
		soundPool.setVolume(row_button,0.5f,0.5f);
		soundPool.setVolume(row_hint_done,0.5f,0.5f);
		soundPool.setVolume(row_hint_remain_do,0.5f,0.5f);
		soundPool.setVolume(row_hint_remian_select,0.5f,0.5f);
		soundPool.setVolume(row_hint_to_learn,0.5f,0.5f);
		mp3_number = 0;
		answerMp3_number = 0;
   }
	
   
   public static void addMp3List(Context context,String mp3){
	   mp3List[mp3_number++] = soundPool.load(mp3, 1);
   }
   
   
   public static void addAnswerMp3List(Context context,String mp3){
	   answerMp3List[answerMp3_number++] = soundPool.load(mp3, 1);
   }
   
   public static void clearMp3(){
	   mp3List = new int[200];
	   answerMp3List = new int[200];
	   mp3_number = 0;
	   answerMp3_number = 0;
	   //停止播放
	   if(palyer_gendu_two!=null){
		   palyer_gendu_two.stop();
		   palyer_gendu_two.release();
		   palyer_gendu_two = null;
	   }


	   if(player_onComplete!=null){
		   player_onComplete.stop();
		   player_onComplete.release();
		   player_onComplete = null;
	   }
	    

	   if(mPlayer!=null){
		   mPlayer.stop();
		   mPlayer.release();
		   mPlayer = null;
	   }

	   if(soundPool!=null){
		   soundPool.stop(id_playing);
		   soundPool.release();
		   soundPool = null;
	   }
   }
   

   /**
    * 函数作用：特殊播放
    * void
    * @param mp3_intro
    */
	public static void playSound_Gendu(String mp3_intro) {
		if(palyer_gendu_two!=null){
			palyer_gendu_two.stop();
			palyer_gendu_two.reset();
		}
		palyer_gendu_two = new MediaPlayer();
		palyer_gendu_two.setVolume(1f,1f);
		try {
			palyer_gendu_two.setDataSource(mp3_intro);
			palyer_gendu_two.prepare();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		palyer_gendu_two.start();
	}

	public static void stopGendu() {
		if(palyer_gendu_two!=null){
			palyer_gendu_two.stop();
		}
	}
   
    public  static MediaPlayer palyer_gendu_two = null;
    
    
   
    //音频播放池
	private static SoundPool soundPool = null;
	//Objective中问题的mp3列表
	private static int[] mp3List = new int[200];
	private static int mp3_number = 0;
	private static int[] answerMp3List = new int[200];
//	private static int[] mp3_guide ;
	private static int answerMp3_number = 0;
	//答对
	private static int row_appaud;
	//打错
	private static int row_failed;
	//按钮音
	private static int row_button;
	//已经完成循环学习
	private static int row_hint_done;
	//还有题目未掌握
	private static int row_hint_remain_do;
	//还有题目未筛查
	private static int row_hint_remian_select;
	//开始学习
	private static int row_hint_to_learn;
	//OBJECTIVIEW的问题MP3
	private static int row_nishidao;
	private static int row_qinggei;
	//正在播放的资源ID
	private static int id_playing ;
	

	/**
	 * 从资源文件路径中提取package路径 
	 * @param path_resource
	 * @return
	 */
	public static String getPackageUrl(String path_resource){
		String [] devides = path_resource.split("/");
		int length = devides.length;
		String res = "";
		for(int i=0;i<length;i++){
			if(i == length -1){
				break;
			}
			if(devides[i].equals("")){
				continue;
			}
			res +="/"+ devides[i];
		}
		return res;
	}
	
	
	/**获取标题金刚
	 * @param radoms
	 * @param bigIndex
	 * @return
	 */
	public static String getTitleKingKang(int[] radoms, BookData bigIndex) {
		String res = "";
		int length = radoms.length;
		ArrayList<CatalogBean> list_index = bigIndex.getmList().get(0).catalogList;
		int length_index = list_index.size();
		CatalogBean  bean;
		String title;
		for(int i=0;i<length;i++){
			if(i == length_index){
				//当长度超过目录块的总长度时，跳出 
				break;
			}
			bean = list_index.get(i);
			int position = radoms[i];
			if(position == 0){//如果为0，那么就是这个bean的标题
				title = bean.title;
			}else {
				ArrayList<CatalogBean> list_catalogs = bean.catalogList;
				if(list_catalogs != null){
					//取catalog值
					int length_catalogs = list_catalogs.size();
					if(length_catalogs >= position){
						title = list_catalogs.get(position-1).title;
					}else{	//若长度不够，那么就用第一个值
						title = list_catalogs.get(0).title;
					}
				}else{
					ArrayList<PackageBean> list_package = bean.packageList;
					if(list_package !=null){
						//取package值
						int length_packages = list_package.size();
						if(length_packages >= position){
							title = list_package.get(position-1).title;
						}else{	//若长度不够，那么就用第一个值
							title = list_package.get(0).title;
						}
					}else{
						ArrayList<CatalogBean> list_section = bean.sectionList;
						if(list_section !=null){
							//取section值
							int length_section = list_section.size();
							if(length_section >= position){
								title = list_section.get(position-1).title;
							}else{	//若长度不够，那么就用第一个值
								title = list_section.get(0).title;
							}
						}else{
							//如果全为空，那么区根部的标题
							title = bean.title;
						}
					}
				} 
			}
			res += title+"**";
		}
		return res;
	}

	
}
