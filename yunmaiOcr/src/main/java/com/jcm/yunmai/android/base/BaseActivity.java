/*
 * File Name: 		BaseActivity.java
 * 
 * Copyright(c) 2011 Yunmai Co.,Ltd.
 * 
 * 		 All rights reserved.
 * 					
 */

package com.jcm.yunmai.android.base;

import android.app.Activity;
import android.os.Bundle;

/**
 * @author Иεtwμ
 *
 */
public class BaseActivity extends Activity {
	
	public static final int REQUEST_CODE_RECOG = 113;			//	识别
	
	/**
	 * 识别成功
	 */
	public static final int RESULT_RECOG_SUCCESS = 103;
	
	/**
	 * 识别失败
	 */
	public static final int RESULT_RECOG_FAILED = 104;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
}
