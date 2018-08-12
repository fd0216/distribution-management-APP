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
	// Surface�Ŀ�����
	private SurfaceHolder surfaceHolder;
	private Camera camera;
	private Button saveButton;
	// ���յĻص��ӿ�
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
		// ��SurfaceView��ǰ�ĳ����� SurfaceHolder һ���ص�����
		// �û�����ʵ�ִ˽ӿڽ���surface�仯����Ϣ��������һ��SurfaceView ��ʱ��
		// ��ֻ��SurfaceHolder.Callback.surfaceCreated()��SurfaceHolder.Callback.surfaceDestroyed()֮����Ч��
		// ����Callback�ķ�����SurfaceHolder.addCallback.
		// ʵ�ֹ���һ��̳�SurfaceView��ʵ��SurfaceHolder.Callback�ӿ�
		surfaceHolder.addCallback(surfaceCallback);
		// ����surface����Ҫ�Լ���ά��������
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
				// /* ������˲���ִ������Ĵ��� */
				// }
				// };
				// private PictureCallback rawCallback = new PictureCallback(){
				// public void onPictureTaken(byte[] _data, Camera _camera){
				// /* ����Ҫ���� raw �������� д���� */
				// }
				// };
				// //�����պ� �洢JPG�ļ��� sd��
			}
		});
	}

	// SurfaceView��ǰ�ĳ����ߵĻص��ӿڵ�ʵ��
	SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			Log.e(tag, "����ͷOpen���");
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
			WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);// ��ȡ���ڵĹ�����
			Display display = wm.getDefaultDisplay();// ��ô����������Ļ
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
			parameters.setPreviewSize(PreviewWidth, PreviewHeight); // �����������Ĵ�С	
			
			parameters.set("jpeg-quality", 50);// ������Ƭ����
			parameters.setPictureSize(PreviewWidth, PreviewHeight);// �����ĳ�������Ļ��С
		
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
					// ���û�к�������ͷ
					defaultId = 0;
				} else {
					// û������ͷ
					Toast.makeText(getApplicationContext(), "������ͷ",
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
			picture.mkdirs();// �����ļ���
			String fileName = "/sdcard/myImage/xu.jpg";
			try {
				FileOutputStream fos = new FileOutputStream(picture.getPath()
						+ "/xu.jpg");
				fos.write(params[0]);
				fos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			Log.e(tag, "��Ƭ�������");
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
	 * ����ѹ������ 
	 * 
	 * @param image 
	 * @return 
	 */  
	public static Bitmap compressImage(Bitmap image) {  	  
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();  
	    image.compress(Bitmap.CompressFormat.JPEG, 60, baos);// ����ѹ������������100��ʾ��ѹ������ѹ��������ݴ�ŵ�baos��  
	    int options = 60;  
	  
	    while (baos.toByteArray().length / 1024 > 100) { // ѭ���ж����ѹ����ͼƬ�Ƿ����100kb,���ڼ���ѹ��  
	        baos.reset(); // ����baos�����baos  
	        image.compress(Bitmap.CompressFormat.JPEG, options, baos);// ����ѹ��options%����ѹ��������ݴ�ŵ�baos��  
	        options -= 10;// ÿ�ζ�����10  
	    }  
	    ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// ��ѹ���������baos��ŵ�ByteArrayInputStream��  
	    Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// ��ByteArrayInputStream��������ͼƬ  
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
        // ��ʼ����ͼƬ����ʱ��options.inJustDecodeBounds ���true��    
        newOpts.inJustDecodeBounds = true;    
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// ��ʱ����bmΪ��    
        newOpts.inJustDecodeBounds = false;    
        int w = newOpts.outWidth;    
        int h = newOpts.outHeight;    
        // ���������ֻ��Ƚ϶���800*480�ֱ��ʣ����ԸߺͿ���������Ϊ    
        float hh = 800f;// �������ø߶�Ϊ800f    
        float ww = 480f;// �������ÿ��Ϊ480f    
        // ���űȡ������ǹ̶��������ţ�ֻ�ø߻��߿�����һ�����ݽ��м��㼴��    
        int be = 1;// be=1��ʾ������    
        if (w > h && w > ww) {// �����ȴ�Ļ����ݿ�ȹ̶���С����    
            be = (int) (newOpts.outWidth / ww);    
        } else if (w < h && h > hh) {// ����߶ȸߵĻ����ݿ�ȹ̶���С����    
            be = (int) (newOpts.outHeight / hh);    
        }    
        if (be <= 0)    
            be = 1;    
        newOpts.inSampleSize = be;// �������ű���    
        // ���¶���ͼƬ��ע���ʱ�Ѿ���options.inJustDecodeBounds ���false��    
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);    
        return compressImage(bitmap);// ѹ���ñ�����С���ٽ�������ѹ��    
    }    
      
}
