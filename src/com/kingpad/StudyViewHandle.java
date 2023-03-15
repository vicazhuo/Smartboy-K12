package com.kingpad;

import android.content.Context;
import android.view.View;

public interface StudyViewHandle {

	/**
	 * 获取引擎的view
	 * @param xmlFilePath：xml数据文件的绝对路径
	 * @return
	 */
	View getGameView();
}
