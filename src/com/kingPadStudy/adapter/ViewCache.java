package com.kingPadStudy.adapter;

import com.kingpad.R;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ViewCache {
	public ImageView image ;
	public TextView text_title;
	public TextView text_profile;
	public Button button_download;
	public ProgressBar bar_download;
	public TextView text_progress;
    private View baseView;
	public boolean isDownloading ;
	public int number;
	
	
	
    
    
	public boolean isDownloading() {
		return isDownloading;
	}
	public void setDownloading(boolean isDownloading) {
		this.isDownloading = isDownloading;
	}
	public ViewCache(View img){
		this.baseView = img;
	}
	public ImageView getImage() {
		if(image == null){
			image = (ImageView)baseView.findViewById(R.id.image);
		}
		return image;
	}
	
	public TextView getText_title() {
		if(text_title == null){
			text_title = (TextView)baseView.findViewById(R.id.text_title);
		}
		return text_title;
	}
	
	public TextView getText_profile() {
		if(text_profile == null){
			text_profile = (TextView)baseView.findViewById(R.id.text_profile);
		}
		return text_profile;
	}
	
	public Button getButton_download() {
		if(button_download == null){
			button_download = (Button)baseView.findViewById(R.id.button_download);
		}
		return button_download;
	}
	
	public ProgressBar getBar_download() {
		if(bar_download == null){
			bar_download = (ProgressBar)baseView.findViewById(R.id.ProgressBar);
		}
		return bar_download;
	}
	
	public TextView getTextProgress(){
		if(text_progress == null){
			text_progress = (TextView)baseView.findViewById(R.id.text_downloadpercent);
		}
		return text_progress;
	}
	
}
