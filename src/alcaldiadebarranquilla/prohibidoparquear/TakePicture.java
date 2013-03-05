/**
 * 
 */
package alcaldiadebarranquilla.prohibidoparquear;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

import alcaldiadebarranquilla.prohibidoparquear.controller.Manager;
import alcaldiadebarranquilla.prohibidoparquear.controller.Params;
import alcaldiadebarranquilla.prohibidoparquear.util.AppGlobal;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

/**
 * @author Soldier
 *
 */
public class TakePicture extends Activity implements
						SurfaceHolder.Callback, SensorEventListener{
	
	protected static final String TAG = "TakeAPicActivity";
	private Camera camera;
	private SurfaceView camera_view;
	private SurfaceHolder surfaceHolder;
	boolean previewing = false;
	
	private long last_update = 0, last_movement = 0;
	private float prevX = 0, prevY = 0, prevZ = 0;
	private float curX = 0, curY = 0, curZ = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_take_picture);
		handler.post(loadCamera);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}
	
	@Override
	protected void onResume() {
	    super.onResume();
	    SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
	    List<Sensor> sensors = sm.getSensorList(Sensor.TYPE_ACCELEROMETER);        
	    if (sensors.size() > 0) {
	        sm.registerListener(this, sensors.get(0), SensorManager.SENSOR_DELAY_GAME);
	    }
	}
	
	@Override
	protected void onStop() {
	    SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);        
	    sm.unregisterListener(this);
	    super.onStop();
	}
	
	protected void takeAPicAction() {

		ShutterCallback shutterCallBack = new ShutterCallback() {
			@Override
			public void onShutter() {
				Log.i(TAG, "onShutter");
			}
		};

		PictureCallback pictureCallBack = new PictureCallback() {
			@Override
			public void onPictureTaken(byte[] data, Camera camera) {
				Log.i(TAG, "Photo was taken RAW");
			}
		};

		PictureCallback pictureCallBackJPG = new PictureCallback() {
			@Override
			public void onPictureTaken(byte[] data, Camera camera) {

				Bitmap capturedBitmap = BitmapFactory.decodeByteArray(data, 0,
						data.length);
				Log.i(TAG, "Photo was taken JPG");
				if (data != null) {
					try {

						// Get image orientation
						Matrix matrix = new Matrix();
						matrix.postRotate(90);

						// Generate the small bitmap => 800 x 600
						Bitmap scaled_bmp = thumbImage(capturedBitmap, 800, 600);
						Bitmap scaled_with_orientation = Bitmap.createBitmap(
								scaled_bmp, 0, 0, scaled_bmp.getWidth(),
								scaled_bmp.getHeight(), matrix, true);

						// Save the image and call preview
						Manager.getInstance().addImage(scaled_with_orientation);
						
						AppGlobal.getInstance().dispatcher.open(
								TakePicture.this, "preview", true);
					} catch (Exception e) {
					}
				}

			}
		};
		camera.takePicture(shutterCallBack, pictureCallBack, pictureCallBackJPG);
	}
	
	public void help(View view){
		Params p = new Params();
		p.AddParam("take", "1");
		AppGlobal.getInstance().dispatcher.open(TakePicture.this, "main", true, p);
	}
	
	public void take(View view){
		takeAPicAction();
	}
	
	public void next(View view){
		
		List<Bitmap> images = Manager.getInstance().getImages();
		
		if(images!=null){
			if(!images.isEmpty()){
				AppGlobal.getInstance().dispatcher.open(TakePicture.this, "geographic", true);
			}else{
				this.displayDialogError();
			}
		}else{
			this.displayDialogError();
		}
		
	}
	
	public void displayDialogError(){
		
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.dialog_no_image_title)
				.setTitle(R.string.dialog_no_image_content)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setCancelable(false)
				.setPositiveButton(R.string.dialog_no_gps_btn_active,
						new DialogInterface.OnClickListener() {
							public void onClick(final DialogInterface dialog,
									final int id) {
								dialog.dismiss();
							}
						});
		final AlertDialog alert = builder.create();
		alert.show();
		
	}
	
	protected void setDisplayOrientation(Camera camera, int angle) {
		Method downPolymorphic;
		try {
			downPolymorphic = camera.getClass().getMethod(
					"setDisplayOrientation", new Class[] { int.class });
			if (downPolymorphic != null)
				downPolymorphic.invoke(camera, new Object[] { angle });
		} catch (Exception e1) {
		}
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		if (previewing) {
			if (camera != null)
				camera.stopPreview();
		}

		Camera.Parameters p = camera.getParameters();
		
		if (camera != null)
			camera.setParameters(p);

		if (camera != null) {
			// camera.setDisplayOrientation(90);
			if (Integer.parseInt(Build.VERSION.SDK) >= 8)
				setDisplayOrientation(camera, 90);
			else {
				if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
					p.set("orientation", "portrait");
					p.set("rotation", 90);
				}
				if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
					p.set("orientation", "landscape");
					p.set("rotation", 90);
				}
			}
		}

		try {
			if (camera != null) {
				camera.setPreviewDisplay(holder);
				camera.startPreview();
				previewing = true;
			} else {
				previewing = false;
			}
		} catch (IOException e) {
			previewing = false;
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		try {
			camera = Camera.open();
			camera.setPreviewDisplay(holder);
		} catch (IOException e) {
			if (camera != null)
				camera.release();
			camera = null;
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		if (camera != null) {
			camera.setPreviewCallback(null);
			camera.stopPreview();
			camera.setPreviewCallback(null);
			previewing = false;
			camera.release();
			camera = null;
		}
	}

	private Handler handler = new Handler();
	private final Runnable loadCamera = new Runnable() {
		public void run() {
			loadCamera();
		}
	};
	
	private Bitmap thumbImage(Bitmap bmp, int newWidth, int newHeight) {
		int origWidth = bmp.getWidth();
		int origHeight = bmp.getHeight();
		if (newWidth >= origWidth) {
			newWidth = origWidth;
		}
		int tNH = (int) Math.round(((float) origHeight / (float) origWidth)
				* (float) newWidth);
		Bitmap scaled = Bitmap.createScaledBitmap(bmp, newWidth, tNH, true);
		return scaled;
	}

	@SuppressWarnings("deprecation")
	private void loadCamera() {
		try {
			camera_view = (SurfaceView) findViewById(R.id.camera_surface);
			surfaceHolder = camera_view.getHolder();
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		} catch (Exception e) {
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		 synchronized (this) {
	        long current_time = event.timestamp;
	         
	        curX = event.values[0];
	        curY = event.values[1];
	        curZ = event.values[2];
	         
	        if (prevX == 0 && prevY == 0 && prevZ == 0) {
	            last_update = current_time;
	            last_movement = current_time;
	            prevX = curX;
	            prevY = curY;
	            prevZ = curZ;
	        }
	 
	        long time_difference = current_time - last_update;
	        if (time_difference > 0) {
	            float movement = Math.abs((curX + curY + curZ) - (prevX - prevY - prevZ)) / time_difference;
	            int limit = 1500;
	            float min_movement = 1E-6f;
	            if (movement > min_movement) {
	                if (current_time - last_movement >= limit) {
	                	//Log.i(TAG, "X: "+curX+" Y: "+curY+" Z: "+curZ);
	                }
	                last_movement = current_time;
	            }
	            prevX = curX;
	            prevY = curY;
	            prevZ = curZ;
	            last_update = current_time;
	        }    
		 }
	}

}
