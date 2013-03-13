package alcaldiadebarranquilla.prohibidoparquear.controller;

import java.io.IOException;
import android.app.Activity;
import java.lang.reflect.Method;

import alcaldiadebarranquilla.prohibidoparquear.Preview;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class SurfaceController extends SurfaceView implements
		SurfaceHolder.Callback {

	private SurfaceHolder mHolder;
	private Camera mCamera;
	private Context context;
	private final String TAG = "SurfaceController";
	protected static final int ON_IMAGE_ERROR = 1;
	protected static final int ON_IMAGE_OK = 2;

	public SurfaceController(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
		mHolder = getHolder();
		mHolder.addCallback(this);
		// deprecated setting, but required on Android versions prior to 3.0
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	public void stop() {
		if (Manager.getInstance().isPreviewing()) {
			mCamera.stopPreview();
			Manager.getInstance().setPreviewing(false);
			mCamera.release();
		}
	}

	public void resume() {
		// try {
		if (mCamera != null) {
			// mCamera.reconnect();
			// mCamera.startPreview();
		}
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}

	public void surfaceCreated(SurfaceHolder holder) {
		// The Surface has been created, now tell the camera where to draw the
		// preview.
		try {

			mCamera = Camera.open();

			if (mCamera != null) {
				mCamera.setPreviewDisplay(holder);
				mCamera.startPreview();
			}
		} catch (IOException e) {
			Log.i(TAG, "Error setting camera preview: " + e.getMessage());
		}

	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		// empty. Take care of releasing the Camera preview in your activity.
		Log.i(TAG, "surfaceDestroyed is call");
		/*
		 * if(Manager.getInstance().isPreviewing()){ mCamera.stopPreview();
		 * Manager.getInstance().setPreviewing(false); mCamera.release(); }
		 */

		// mCamera = null;
	}

	protected void setDisplayOrientation(Camera camera, int angle) {
		Method downPolymorphic;
		try {
			downPolymorphic = camera.getClass().getMethod(
					"setDisplayOrientation", new Class[] { int.class });
			if (downPolymorphic != null)
				downPolymorphic.invoke(camera, new Object[] { angle });
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		// If your preview can change or rotate, take care of those events here.
		// Make sure to stop the preview before resizing or reformatting it.

		if (mHolder.getSurface() == null) {
			// preview surface does not exist
			return;
		}

		// stop preview before making changes
		try {
			mCamera.stopPreview();
		} catch (Exception e) {
			// ignore: tried to stop a non-existent preview
		}

		// set preview size and make any resize, rotate or
		// reformatting changes here
		Camera.Parameters p = null;

		if (mCamera != null) {
			p = mCamera.getParameters();
		}

		if (mCamera != null) {
			// camera.setDisplayOrientation(90);

			int currentapiVersion = android.os.Build.VERSION.SDK_INT;

			if (currentapiVersion >= 8) {

				int orientacion = getResources().getConfiguration().orientation;

				Log.i(TAG, "The current orientation is: "+orientacion);

				if (orientacion == Configuration.ORIENTATION_PORTRAIT) {
					setDisplayOrientation(mCamera, 90);
				}

				if (orientacion == Configuration.ORIENTATION_LANDSCAPE) {
					setDisplayOrientation(mCamera, 0);
				}

			} else {
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

		// start preview with new settings
		try {
			if (mCamera != null) {
				mCamera.setPreviewDisplay(mHolder);
				mCamera.startPreview();
				Manager.getInstance().setPreviewing(true);
			} else {
				Manager.getInstance().setPreviewing(false);
			}
		} catch (Exception e) {
			Manager.getInstance().setPreviewing(false);
			Log.d(TAG, "Error starting camera preview: " + e.getMessage());
		}
	}

	public void takeAPicAction() {

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
						
						int orientacion = getResources().getConfiguration().orientation;



						if (orientacion == Configuration.ORIENTATION_PORTRAIT) {
							matrix.postRotate(90);
						}

						if (orientacion == Configuration.ORIENTATION_LANDSCAPE) {
							matrix.postRotate(0);
						}
						


						// Generate the small bitmap => 800 x 600
						Bitmap scaled_bmp = thumbImage(capturedBitmap, 800, 600);
						Bitmap scaled_with_orientation = Bitmap.createBitmap(
								scaled_bmp, 0, 0, scaled_bmp.getWidth(),
								scaled_bmp.getHeight(), matrix, true);

						// Save the image and call preview
						Manager.getInstance().setImageTemp(
								scaled_with_orientation);
						/*
						 * AppGlobal.getInstance().dispatcher.open( context,
						 * "preview", true);
						 */
						Intent i = new Intent(context, Preview.class);
						context.startActivity(i);
						((Activity)context).finish();
							
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}
		};
		mCamera.takePicture(shutterCallBack, pictureCallBack,
				pictureCallBackJPG);
	}

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

	public static Camera getCameraInstance() {
		Camera c = null;
		try {
			c = Camera.open(); // attempt to get a Camera instance
		} catch (Exception e) {
			// Camera is not available (in use or does not exist)
		}
		return c; // returns null if camera is unavailable
	}

}