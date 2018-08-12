package com.fd.login;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.text.SimpleDateFormat;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

public class CameraActivity extends Activity {
	private String tag = "CameraActivity";
	private SurfaceView surfaceView;
	// Surface的控制器
	private SurfaceHolder surfaceHolder;
	private Camera camera;
	private Button saveButton;
	// 拍照的回调接口
	private Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			new SavePictureTask().execute(data);
			camera.startPreview();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera);
		initViews();
	}

	private void initViews() {
		saveButton = (Button) findViewById(R.id.camera_save);
		surfaceView = (SurfaceView) findViewById(R.id.camera_preview);
		surfaceHolder = surfaceView.getHolder();
		// 给SurfaceView当前的持有者 SurfaceHolder 一个回调对象。
		// 用户可以实现此接口接收surface变化的消息。当用在一个SurfaceView 中时，
		// 它只在SurfaceHolder.Callback.surfaceCreated()和SurfaceHolder.Callback.surfaceDestroyed()之间有效。
		// 设置Callback的方法是SurfaceHolder.addCallback.
		// 实现过程一般继承SurfaceView并实现SurfaceHolder.Callback接口
		surfaceHolder.addCallback(surfaceCallback);
		// 设置surface不需要自己的维护缓存区
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		saveButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				camera.takePicture(null, null, pictureCallback);
				// Camera.takePicture(shutterCallback,rawCallback,pictureCallback
				// );
				// private ShutterCallback shutterCallback = new
				// ShutterCallback(){
				// public void onShutter(){
				// /* 按快门瞬间会执行这里的代码 */
				// }
				// };
				// private PictureCallback rawCallback = new PictureCallback(){
				// public void onPictureTaken(byte[] _data, Camera _camera){
				// /* 如需要处理 raw 则在这里 写代码 */
				// }
				// };
				// //当拍照后 存储JPG文件到 sd卡
			}
		});
	}

	// SurfaceView当前的持有者的回调接口的实现
	SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			Log.e(tag, "摄像头Open完成");
			try {
				camera = Camera.open();
				camera.setPreviewDisplay(holder);
			} catch (IOException e) {
				camera = Camera.open(Camera.getNumberOfCameras() - 1);
				camera.release();
				camera = null;
			}
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			Camera.Parameters parameters = camera.getParameters();
			int PreviewWidth = 0;
			int PreviewHeight = 0;
			WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);// 获取窗口的管理器
			Display display = wm.getDefaultDisplay();// 获得窗口里面的屏幕
			List<Camera.Size> sizeList = parameters.getSupportedPreviewSizes();
			if (sizeList.size() > 1) {
				Iterator<Camera.Size> itor = sizeList.iterator();
				while (itor.hasNext()) {
					Camera.Size cur = itor.next();
					if (cur.width >= PreviewWidth
							&& cur.height >= PreviewHeight) {
						PreviewWidth = cur.width;
						PreviewHeight = cur.height;
						break;
					}
				}
			}
			parameters.setPreviewSize(PreviewWidth, PreviewHeight); // 获得摄像区域的大小	
			
			parameters.set("jpeg-quality", 50);// 设置照片质量
			parameters.setPictureSize(PreviewWidth, PreviewHeight);// 设置拍出来的屏幕大小
		
			parameters.setPictureFormat(ImageFormat.JPEG);
			int currentCameraId = getDefaultCameraId();
			camera.setDisplayOrientation(0);
			setCameraDisplayOrientation(CameraActivity.this, currentCameraId,
					camera);
			camera.setParameters(parameters);
			camera.startPreview();

		}

		public void setCameraDisplayOrientation(Activity activity,
				int cameraId, android.hardware.Camera camera) {
			android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
			android.hardware.Camera.getCameraInfo(cameraId, info);
			int rotation = activity.getWindowManager().getDefaultDisplay()
					.getRotation();
			int degrees = 0;
			switch (rotation) {
			case Surface.ROTATION_0:
				degrees = 0;
				break;
			case Surface.ROTATION_90:
				degrees = 90;
				break;
			case Surface.ROTATION_180:
				degrees = 180;
				break;
			case Surface.ROTATION_270:
				degrees = 270;
				break;
			}
			int result;
			if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
				result = (info.orientation + degrees) % 360;
				result = (360 - result) % 360; // compensate the mirror
			} else {
				// back-facing
				result = (info.orientation - degrees + 360) % 360;
			}
			camera.setDisplayOrientation(result);
		}

		private int getDefaultCameraId() {
			int mNumberOfCameras = 0;
			int defaultId = -1;

			// Find the total number of cameras available
			mNumberOfCameras = Camera.getNumberOfCameras();

			// Find the ID of the default camera
			CameraInfo cameraInfo = new CameraInfo();
			for (int i = 0; i < mNumberOfCameras; i++) {
				Camera.getCameraInfo(i, cameraInfo);
				if (cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK) {
					defaultId = i;
				}
			}
			if (-1 == defaultId) {
				if (mNumberOfCameras > 0) {
					// 如果没有后向摄像头
					defaultId = 0;
				} else {
					// 没有摄像头
					Toast.makeText(getApplicationContext(), "无摄像头",
							Toast.LENGTH_LONG).show();
				}
			}
			return defaultId;
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			camera.stopPreview();
			camera.release();
			camera = null;
		}

	};
	class SavePictureTask extends AsyncTask<byte[], String, String> {
		@Override
		protected String doInBackground(byte[]... params) {
			File picture = new File("/sdcard/myImage/");
			picture.mkdirs();// 创建文件夹
			String fileName = "/sdcard/myImage/xu.jpg";
			try {
				FileOutputStream fos = new FileOutputStream(picture.getPath()
						+ "/xu.jpg");
				fos.write(params[0]);
				fos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			Log.e(tag, "照片保存完成");
			CameraActivity.this.finish();		
			try {
				storeImage(compressImage(getimage(fileName)),fileName);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
			return null;
		}
	}
	/** 
	 * 质量压缩方法 
	 * 
	 * @param image 
	 * @return 
	 */  
	public static Bitmap compressImage(Bitmap image) {  	  
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();  
	    image.compress(Bitmap.CompressFormat.JPEG, 60, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中  
	    int options = 60;  
	  
	    while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩  
	        baos.reset(); // 重置baos即清空baos  
	        image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中  
	        options -= 10;// 每次都减少10  
	    }  
	    ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中  
	    Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片  
	    return bitmap;  
	}  
    public void storeImage(Bitmap bitmap, String outPath) throws FileNotFoundException {  
	        FileOutputStream os = new FileOutputStream(outPath);  
	        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, os);  
	    } 
    public Bitmap getBitmap(String imgPath) {  
	        // Get bitmap through image path  
	        BitmapFactory.Options newOpts = new BitmapFactory.Options();  
	        newOpts.inJustDecodeBounds = false;  
	        newOpts.inPurgeable = true;  
	        newOpts.inInputShareable = true;  	       
	        newOpts.inSampleSize = 1;  
	        newOpts.inPreferredConfig = Config.RGB_565;  
	        return BitmapFactory.decodeFile(imgPath, newOpts);  
	    }  
    public static Bitmap getimage(String srcPath) {    
        
        BitmapFactory.Options newOpts = new BitmapFactory.Options();    
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了    
        newOpts.inJustDecodeBounds = true;    
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空    
        newOpts.inJustDecodeBounds = false;    
        int w = newOpts.outWidth;    
        int h = newOpts.outHeight;    
        // 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为    
        float hh = 800f;// 这里设置高度为800f    
        float ww = 480f;// 这里设置宽度为480f    
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可    
        int be = 1;// be=1表示不缩放    
        if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放    
            be = (int) (newOpts.outWidth / ww);    
        } else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放    
            be = (int) (newOpts.outHeight / hh);    
        }    
        if (be <= 0)    
            be = 1;    
        newOpts.inSampleSize = be;// 设置缩放比例    
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了    
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);    
        return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩    
    }    
      
}
