package com.jcm.yunmai.cc.idcard.activity;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.haiyisoft.sqjw.R;
import com.jcm.yunmai.cc.idcard.view.ViewfinderView;
import com.yunmai.cc.idcard.controler.CameraManager;
import com.yunmai.cc.idcard.controler.OcrConstant;
import com.yunmai.cc.idcard.controler.OcrManager;
import com.yunmai.cc.idcard.vo.IdCardInfo;

/**
 * 视频识别类
 * @author fangcm 2013-03-18
 *
 */

public class CameraActivity extends Activity implements SurfaceHolder.Callback{



	private final String TAG = "cc_smart";
	
	private SurfaceView sv_preview;
	private SurfaceHolder surfaceHolder;
	private CameraManager cameraManager;
	private boolean autoFoucs = true;
	private ViewfinderView finderView;
	private OcrManager ocrManager ;
	private Rect rect;
	private boolean cameraError = false;
//	private boolean over = false;
	
	private Button btnFlash, btnCancel;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera);
		initView();
		cameraManager = new CameraManager(getBaseContext(),mHandler);
		mCameraOpenThread.start();
		try {
			mCameraOpenThread.join();
			mCameraOpenThread = null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			cameraError = true;
		}
		if(cameraError){
			 Toast.makeText(getBaseContext(), "照相机未启动！", Toast.LENGTH_SHORT).show();
			 finish();
			 return;
		}
		setParameters();
		
		
	}
	
	
	
	private Thread mCameraOpenThread = new Thread(new Runnable() {
        public void run() {
        	try {
    			cameraManager.openCamera();
    		} catch (Exception e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    			cameraError = true;
    		}
        }
    });
	
	
	private void setParameters(){
		cameraManager.setCameraFlashModel(Camera.Parameters.FLASH_MODE_OFF);
		cameraManager.setPreviewSize();
		
		int pWidth = cameraManager.getPreviewWidth();
		int pHeight = cameraManager.getPreviewHeight();
		if(pWidth == 0 || pHeight==0){
			 Toast.makeText(getBaseContext(), "照相机未启动！！", Toast.LENGTH_SHORT).show();
			 finish();
			 return;
		}
		
		WindowManager manager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		int wWidth = display.getWidth();
		int wHeight = display.getHeight();
		if(android.os.Build.MANUFACTURER.equals("Lenovo") && android.os.Build.MODEL.equals("IdeaTabS2110AH")){
			wHeight = 800;
		}


	// 验收前漏洞扫描对应注释Log.d(TAG, wWidth+"<--------W----WindowManager-----H------->"+wHeight);
		int tempWidth = pWidth;
		int tempHeidht = pHeight;
		float x = 100.0f;
		int tempW = pWidth;
		int tempH = pHeight;
		if(wWidth > pWidth && wHeight > pHeight){
			while(wWidth > tempW && wHeight > tempH){
				x ++;
	// 验收前漏洞扫描对应注释Log.d(TAG, "---xx----->"+x/100.0);
				tempW = (int) (pWidth * x/100.0);
				tempH = (int) (pHeight * x/100.0);
				if(wWidth > tempW && wHeight > tempH){
					tempWidth = tempW;
					tempHeidht = tempH;
				}
			}
	// 验收前漏洞扫描对应注释Log.d(TAG,"<------11--wWidth > pWidth && wHeight > pHeight------>");
		}else{
			while(tempWidth > wWidth || tempHeidht > wHeight){
				x --;
	// 验收前漏洞扫描对应注释Log.d(TAG, "---xx----->"+x/100.0);
				tempWidth  = (int) (pWidth * x/100.0);
				tempHeidht = (int) (pHeight * x/100.0);
			}
	// 验收前漏洞扫描对应注释Log.d(TAG,"<-----22---tempWidth > wWidth || tempHeidht > wHeight------>");
		}

	// 验收前漏洞扫描对应注释Log.d(TAG, tempWidth+"<--------W----setParameters-----H------->"+tempHeidht);
//		tempWidth = 854;
//		tempHeidht = 480;
		ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams) sv_preview.getLayoutParams();
	    lp.width = tempWidth;
	    lp.height = tempHeidht;
	    sv_preview.getHolder().setFixedSize(tempWidth, tempHeidht);
	    sv_preview.setLayoutParams(lp);
		
		surfaceHolder = (SurfaceHolder) sv_preview.getHolder();
		surfaceHolder.addCallback(CameraActivity.this);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		
		finderView.initFinder(tempWidth, tempHeidht, mHandler);
	}
	
	
	
	
	private void initView(){
		sv_preview = (SurfaceView) findViewById(R.id.camera_sv);
		finderView = (ViewfinderView) findViewById(R.id.camera_finderView);
		btnCancel = (Button) findViewById(R.id.bt_cancel);
		btnFlash = (Button) findViewById(R.id.bt_flash);
		btnFlash.setOnClickListener(listener);
		btnCancel.setOnClickListener(listener);
	}
	
	private boolean isFlashOn = false;

	
	private OnClickListener listener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (v.getId()== R.id.bt_cancel) {
				setResult(998);
				finish();
			}else if (v.getId()== R.id.bt_flash) {
				if (isFlashOn) {
					if (cameraManager.closeFlashlight()) {
						btnFlash.setBackgroundDrawable(getResources().getDrawable(R.mipmap.flash_on_s));
						isFlashOn = false;
					}
				} else {
					if (cameraManager.openFlashlight()) {
						btnFlash.setBackgroundDrawable(getResources().getDrawable(R.mipmap.flash_off_s));
						isFlashOn = true;
					}
				}
			}

		}
	};
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case OcrConstant.TAKE_PREVIEW_DATA_OK:
				 
				if(ocrManager == null){
					ocrManager = new OcrManager(mHandler,CameraActivity.this);
					try {
						rect = cameraManager.getViewfinder(finderView.getFinder());
					} catch (Exception e) {
						// TODO: handle exception
						return;
					}
					
				}
				
				byte[] data_p = (byte[])msg.obj;
				if(data_p != null && data_p.length >0){
//					if(over){
//						return;
//					}
//					ocrManager.recognBC(data_p,cameraManager.getPreviewWidth(), cameraManager.getPreviewHeight(),rect);
					ocrManager.recognBC(data_p,cameraManager.getPreviewWidth(), cameraManager.getPreviewHeight(),rect,1);
					mHandler.sendEmptyMessageDelayed(OcrConstant.START_AUTOFOCUS, 100);
				}else{
					finderView.setLineRect(0);
					Toast.makeText(getBaseContext(), "相机出现问题，请重启手机！", Toast.LENGTH_SHORT).show();
					mHandler.sendEmptyMessageDelayed(OcrConstant.START_AUTOFOCUS, 500);
				}
				break;
			case OcrConstant.RECOGN_OK:
				mHandler.removeMessages(OcrConstant.TAKE_PREVIEW_DATA_OK);
				mHandler.removeMessages(OcrConstant.START_AUTOFOCUS);
				String imgPath = "/sdcard/aidtest.jpg";
				String headPath = "/sdcard/aidheadtest.jpg";
				IdCardInfo idCardInfo = ocrManager.getResult(imgPath,headPath);
				Intent data2 = new Intent();
				data2.putExtra("idcardinfo", idCardInfo);
				setResult(200, data2);
				
				finish();
				
				break;
			case OcrConstant.REPEAT_AUTOFOCUS:
				cameraManager.autoFoucs();
				mHandler.sendEmptyMessageDelayed(OcrConstant.REPEAT_AUTOFOCUS, 2000);
				break;
			case OcrConstant.RECOGN_EG_TIME_OUT:
				Toast.makeText(getBaseContext(), "引擎过期，请尽快更新！", Toast.LENGTH_LONG).show();
				finish();
				break;
			case OcrConstant.RECOGN_EG_LICENSE:
				int ret = 0;
				if(msg.obj != null){
					ret = (Integer) msg.obj;
				}
				Toast.makeText(getBaseContext(), "授权失败-->"+ret, Toast.LENGTH_LONG).show();
				finish();
				break;
			case OcrConstant.RECOGN_EG_INIT_ERROR:
				Toast.makeText(getBaseContext(), "引擎初始化失败！", Toast.LENGTH_LONG).show();
				finish();
				break;
			case OcrConstant.START_AUTOFOCUS:
				if(autoFoucs){
					cameraManager.autoFoucs();
					autoFoucs = false;
					mHandler.sendEmptyMessageDelayed(OcrConstant.START_AUTOFOCUS, 500);
					mHandler.sendEmptyMessageDelayed(OcrConstant.REPEAT_AUTOFOCUS, 1500);
				}else {
					cameraManager.autoFocusAndPreviewCallback();	
				}
				break;
			case OcrConstant.RECOGN_LINE_IN_RECT:
				int restult =  (Integer) msg.obj;
				finderView.setLineRect(restult);
				break;
			default:
				cameraManager.initDisplay();
				mHandler.sendEmptyMessageDelayed(OcrConstant.START_AUTOFOCUS, 500);
				Toast.makeText(getBaseContext(), "<>"+msg.what, Toast.LENGTH_SHORT).show();
				break;
			}
		}
		
	};
	
	
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
	// 验收前漏洞扫描对应注释Log.d(TAG, "surfaceCreated");
		if(!cameraManager.cameraOpened()){
			cameraManager.openCamera();
			setParameters();
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		if (holder.getSurface() == null) {
	// 验收前漏洞扫描对应注释Log.d(TAG, "holder.getSurface() == null");
            return;
        }
	// 验收前漏洞扫描对应注释Log.v(TAG, "surfaceChanged. w=" + width + ". h=" + height);
		surfaceHolder = holder;
		cameraManager.setPreviewDisplay(surfaceHolder);
		cameraManager.initDisplay();
		mHandler.sendEmptyMessageDelayed(OcrConstant.START_AUTOFOCUS, 500);
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
	// 验收前漏洞扫描对应注释Log.d(TAG, "surfaceDestroyed");
		cameraManager.closeCamera();
		surfaceHolder = null;
	}
	
	private void finishAll(){
		if(cameraManager != null){
			cameraManager.closeCamera();
		}
		
	}
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mHandler.removeMessages(OcrConstant.TAKE_PREVIEW_DATA_OK);
		mHandler.removeMessages(OcrConstant.START_AUTOFOCUS);
		finishAll();
		
	}



}
