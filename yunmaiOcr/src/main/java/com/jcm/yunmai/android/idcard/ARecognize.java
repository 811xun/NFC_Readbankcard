/*
 * File Name: 		ARecognize.java
 * 
 * Copyright(c) 2011 Yunmai Co.,Ltd.
 * 
 * 		 All rights reserved.
 * 					
 */

package com.jcm.yunmai.android.idcard;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.haiyisoft.sqjw.R;
import com.jcm.yunmai.android.base.BaseActivity;
import com.yunmai.android.engine.OcrEngine;
import com.yunmai.android.vo.IDCard;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * @author fangcm
 * 
 */
public class ARecognize extends BaseActivity implements Runnable {

	private TextView mRecoResult;
	private IDCard idCard;
	private ImageView iv_head;
	private RelativeLayout rl_result;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bcr_recognize);
		mRecoResult = (TextView) findViewById(R.id.reco_result);
		iv_head = (ImageView) findViewById(R.id.iv_result);
		rl_result = (RelativeLayout) findViewById(R.id.rl_result);
		new Thread(this).start();

	}

	private Handler mOcrHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case OcrEngine.RECOG_FAIL:
				Toast.makeText(ARecognize.this, R.string.reco_dialog_blur,Toast.LENGTH_SHORT).show();
				setResult(RESULT_RECOG_FAILED);
				finish();
				break;
			case OcrEngine.RECOG_BLUR:
				Toast.makeText(ARecognize.this, R.string.reco_dialog_blur,Toast.LENGTH_SHORT).show();
				setResult(RESULT_RECOG_FAILED);
				finish();
				break;
			case OcrEngine.RECOG_OK:
				rl_result.setVisibility(View.VISIBLE);
				String img = String.valueOf(msg.obj);
				if(!img.equals("")){
					iv_head.setImageURI(Uri.parse(img));
				}else{
					iv_head.setImageURI(null);
				}
				
				try {
					String res = new String(idCard.getCharInfo(), "gbk");
					mRecoResult.setText(res.trim());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
//				mRecoResult.setText(idCard.toString());
				break;
			case OcrEngine.RECOG_IMEI_ERROR:
				Toast.makeText(ARecognize.this, R.string.reco_dialog_imei,Toast.LENGTH_SHORT).show();
				setResult(RESULT_RECOG_FAILED);
				finish();
				break;
			case OcrEngine.RECOG_FAIL_CDMA:
				Toast.makeText(ARecognize.this, R.string.reco_dialog_cdma,Toast.LENGTH_SHORT).show();
				setResult(RESULT_RECOG_FAILED);
				finish();
				break;
			case OcrEngine.RECOG_LICENSE_ERROR:
				Toast.makeText(ARecognize.this, R.string.reco_dialog_licens,Toast.LENGTH_SHORT).show();
				setResult(RESULT_RECOG_FAILED);
				finish();
				break;
			case OcrEngine.RECOG_TIME_OUT:
				Toast.makeText(ARecognize.this, R.string.reco_dialog_time_out,Toast.LENGTH_SHORT).show();
				setResult(RESULT_RECOG_FAILED);
				finish();
				break;
			case OcrEngine.RECOG_ENGINE_INIT_ERROR:
				Toast.makeText(ARecognize.this, R.string.reco_dialog_engine_init,Toast.LENGTH_SHORT).show();
				setResult(RESULT_RECOG_FAILED);
				finish();
				break;
			case OcrEngine.RECOG_COPY:
				Toast.makeText(ARecognize.this, R.string.reco_dialog_fail_copy,Toast.LENGTH_SHORT).show();
				setResult(RESULT_RECOG_FAILED);
				finish();
				break;
			default:
				Toast.makeText(ARecognize.this, R.string.reco_dialog_blur,Toast.LENGTH_SHORT).show();
				setResult(RESULT_RECOG_FAILED);
				finish();
				break;
			}
		}

	};



	@Override
	public void run() {
		OcrEngine ocrEngine = new OcrEngine();
		try {
			byte[] data = getIntent().getByteArrayExtra("idcardA");
//			saveImg(data);
			String headPath = newHeadPath();//头像图片路径
//			String headPath = "";
//			ocrEngine.setCheckCopy(true);
			idCard = ocrEngine.recognize(ARecognize.this, data, null,headPath);
			if (idCard.getRecogStatus() == OcrEngine.RECOG_OK) {
				mOcrHandler.sendMessage(mOcrHandler.obtainMessage(OcrEngine.RECOG_OK, headPath));
			}else {
				mOcrHandler.sendEmptyMessage(idCard.getRecogStatus());
			}
		} catch (Exception e) {
			mOcrHandler.sendEmptyMessage(OcrEngine.RECOG_FAIL);
		}
	}
	
	
	private void saveImg(byte[] data){
		Bitmap bm = null;
		try {
			bm = BitmapFactory.decodeByteArray(data, 0, data.length);
			File imageFile = new File(Environment.getExternalStorageDirectory().getPath()+"/ccidimg/"+newImageName());
//			if(imageFile.exists()){
//				imageFile.delete();
//			}
			File file = new File(Environment.getExternalStorageDirectory().getPath() + "/ccidimg/");
			if(!file.exists()){
				file.mkdirs();
			}
	        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(imageFile));
	        bm.compress(Bitmap.CompressFormat.JPEG, 50, bos);
	        bos.flush();
	        bos.close();
	       
		} catch (Exception e) {
			// TODO: handle exception
		}finally{
			if(bm != null){
				 bm.recycle();
				 bm = null;
			}
		}
	}
	
	public String newImageName() {
		return "ccyx"+ new SimpleDateFormat("yyMMddHHmmss").format(new Date()) + ".jpg";
	}

	public static String newHeadPath(){
		File file = new File(Environment.getExternalStorageDirectory().getPath() + "/idheadimg/");
		if(!file.exists()){
			file.mkdirs();
		}
		return Environment.getExternalStorageDirectory().getPath() + "/idheadimg/" + getTime("yyMMddHHmmssSSS") + ".jpg";
	}

	private static String getTime(String format){
		return new SimpleDateFormat(format).format(new Date());
	}
}