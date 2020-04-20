/*
 * File Name: 		App.java
 * 
 * Copyright(c) 2011 Yunmai Co.,Ltd.
 * 
 * 		 All rights reserved.
 * 					
 */

package com.jcm.yunmai.android.idcard;

import android.app.Application;
import android.os.Environment;

import java.io.File;

public class AppDemo extends Application {
	
	public static final String SDCARD_ROOT_PATH = Environment.getExternalStorageDirectory().getPath();
	public static final String SDCARD_BASE_PATH = SDCARD_ROOT_PATH + "/hotcard";
	public static final String IMAGE_FOLDER = "/images";
	public static String FIELD_DIR;
	
	public static final int THUMB_WIDTH = 640;
	public static final int THUMB_HEIGHT = 480;

	public static final int ICON_WIDTH = 80;
	public static final int ICON_HEIGHT = 60;
	
	@Override
	public void onCreate() {
		super.onCreate();
		FIELD_DIR = getFilesDir() + "/tmp";
		initDir();
	}

	/**
	 * 初始化目录
	 */
	private void initDir() {
		File imageDir = new File(AppDemo.getImagesDir());
        if (!imageDir.exists()) {
        	imageDir.mkdirs();
		}
        File fieldDir = new File(FIELD_DIR);
        if (!fieldDir.exists()) {
        	fieldDir.mkdir();
        }
	}
	
	/**
	 * 存储卡基目录
	 * @return
	 */
	public static String getBaseDir() {
		return SDCARD_BASE_PATH ;
	}
	
	/**
	 * 图像目录
	 * @return
	 */
	public static String getImagesDir() {
		return SDCARD_BASE_PATH + IMAGE_FOLDER;
	}
	
	/**
	 * 根据图像名获取图像完整路径
	 * @param imageName
	 * @return
	 */
	public static String getImagePath(String imageName) {
		return SDCARD_BASE_PATH + IMAGE_FOLDER + File.separator + imageName;
	}
	
	
}