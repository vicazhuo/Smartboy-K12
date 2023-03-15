package com.utils;

import java.io.IOException;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.util.Log;

/**
 * 实现录音、播放录音、保存录音
 * @author swerit
 *
 */
public class AudioRecord {

	private MediaRecorder mRecorder = null;
	private String mFileName = null;
	private MediaPlayer mPlayer = null;
	
	private static AudioRecord audioRecord;
	
	/**
	 * 该类使用单例模式
	 * @return
	 */
	public static AudioRecord getInstance(){
		if (null == audioRecord){
			audioRecord = new AudioRecord();
		}
		//System.out.println("在getInstance中,audioRecord="+audioRecord);
		return audioRecord;
	}
	
	private AudioRecord(){
		mRecorder = null;
	}
	
	
	/**
	 * 开始录音
	 */
	public void startRecording() {
		mFileName = FileManager.getAudioFileName();
		if (mFileName == null){
			return;
		}
		//System.out.println("开始录音");
		if(mRecorder == null){
			//System.out.println("mRecorder == null");
			mRecorder = new MediaRecorder();
			mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
			mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		}
		mRecorder.setOutputFile(mFileName);
		try {
			mRecorder.prepare();
		} catch (IOException e) {
			Log.e("录音异常", "prepare() failed:" + e.toString());
		}
		mRecorder.start();
	}
	
	
	/**
	 * 停止录音
	 */
	public void stopRecording() {
		if(mRecorder == null){
			return ;
		}
		//System.out.println("停止录音");
		mRecorder.stop();
		mRecorder.reset();
		mRecorder.release();
		mRecorder = null;
	}
	
	/**
	 * 播放录音
	 */
	public void startPlaying() {
		//System.out.println("进入开始播放录音...mFileName="+mFileName);
		if (null == mFileName || mFileName.equals("")){
			//System.out.println("录音文件不存在...");
			return;
		}
		mPlayer = new MediaPlayer();
		try {
			mPlayer.setDataSource(mFileName);
			mPlayer.prepare();
			mPlayer.start();
		} catch (IOException e) {
			Log.e("播放录音异常", "prepare() failed");
		}
	}
	
	/**
	 * 停止播放录音
	 */
	public void stopPlaying() {
		if(mPlayer == null){
			return ;
		}
		mPlayer.stop();
		mPlayer.release();
		mPlayer = null;
	}
	
	/**
	 * 删除当前录音的文件
	 * @return
	 */
	public boolean deleteCurrentRecordFile(){
		return FileManager.deleteOneAudioFile(mFileName);
	}
	
	/**
	 * 释放录音类，在不使用录音的时候将资源释放
	 */
	public void release(){
		if (mRecorder != null) {
			mRecorder.release();
			mRecorder = null;
		}
		if (mPlayer != null) {
			mPlayer.release();
			mPlayer = null;
		}
	}
}
