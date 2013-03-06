/**
 * 
 */
package alcaldiadebarranquilla.prohibidoparquear;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

import alcaldiadebarranquilla.prohibidoparquear.controller.Manager;
import alcaldiadebarranquilla.prohibidoparquear.util.AppGlobal;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsoluteLayout;
import android.widget.ImageButton;

@SuppressLint("HandlerLeak")
public class TakePicture extends Activity implements
		SurfaceHolder.Callback {

	protected static final String TAG = "TakeAPicActivity";
	private static final int SELECT_PHOTO = 100;

	protected static final int ON_IMAGE_ERROR = 1;
	protected static final int ON_IMAGE_OK = 2;

	private Camera camera;
	private SurfaceView camera_view;
	private SurfaceHolder surfaceHolder;
	boolean previewing = false;

	private Handler handler = new Handler();
	private final Runnable loadCamera = new Runnable() {
		public void run() {
			loadCamera();
		}
	};

	private ImageButton btn_take_a_pic;
	private ImageButton btn_next;
	private ImageButton btn_gallery;

	private Handler preparedImage = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			
			AppGlobal.getInstance().hideLoading();
			
			switch ((int) msg.what) {
			case ON_IMAGE_ERROR:
				AppGlobal
						.getInstance()
						.showSimpleDialog(
								TakePicture.this,
								"Hubo un inconveniente",
								"No fue posible procesar la fotografía, verifica que has seleccionado una imagen de el álbum de fotos de tú cámara e inténtalo nuevamente. Algunas imágenes hacen parte de álbumes que están en internet.");
				break;
			case ON_IMAGE_OK:
				Bitmap bmp = (Bitmap) msg.obj;
				Manager.getInstance().setImageTemp(bmp);
				AppGlobal.getInstance().dispatcher.open(
						TakePicture.this, "preview", true);
				break;
			}
		}
	};

	private class PrepareImageTask extends AsyncTask<Uri, Integer, Bitmap> {

		@Override
		protected Bitmap doInBackground(Uri... args) {
			Uri selected_image = args[0];
			return prepareImageUri(selected_image);
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			if (result == null) {
				preparedImage.sendEmptyMessage(ON_IMAGE_ERROR);
			} else {
				Message msg = new Message();
				msg.what = ON_IMAGE_OK;
				msg.obj = result;
				preparedImage.sendMessage(msg);
			}
		}

		// private String getFilePathFromContentUri(Uri selectedVideoUri,
		// ContentResolver contentResolver) {
		// String filePath;
		// String[] filePathColumn = { MediaColumns.DATA };
		//
		// Cursor cursor = contentResolver.query(selectedVideoUri,
		// filePathColumn, null, null, null);
		// cursor.moveToFirst();
		//
		// int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
		// filePath = cursor.getString(columnIndex);
		// cursor.close();
		// return filePath;
		// }

		private Bitmap prepareImageUri(Uri image) {
			Bitmap original_bmp = null;
			try {
				original_bmp = MediaStore.Images.Media.getBitmap(
						TakePicture.this.getContentResolver(), image);
			} catch (FileNotFoundException e) {
				original_bmp = null;
			} catch (IOException e) {
				original_bmp = null;
			} finally {
				if (original_bmp != null) {

					// Get image orientation
					int orientation = getRotateAngle(image);
					Matrix matrix = new Matrix();
					matrix.postRotate(orientation);

					// Generate the small bitmap
					Bitmap small_bmp = thumbImage(original_bmp, 800, 600);
					Bitmap scaled_with_orientation = Bitmap.createBitmap(
							small_bmp, 0, 0, small_bmp.getWidth(),
							small_bmp.getHeight(), matrix, true);

					return scaled_with_orientation;
				}
			}
			return null;
		}

		// private Bitmap prepareImageUri(Uri image) {
		// Bitmap return_bmp = null;
		// try {
		// Uri uri = image;
		// String[] projection = { MediaStore.Images.Media.DATA };
		//
		// Cursor cursor = getContentResolver().query(uri, projection,
		// null, null, null);
		// cursor.moveToFirst();
		//
		// int columnIndex = cursor.getColumnIndex(projection[0]);
		// String image_path = cursor.getString(columnIndex);
		// cursor.close();
		//
		// // String image_path = getFilePathFromContentUri(image,
		// // TakeAPicActivity.this.getContentResolver());
		// Log.i(TAG, "Path: " + image_path);
		// return_bmp = decodeSampledBitmapFromFile(
		// image_path, 800, 600);
		//
		// int orientation;
		// Matrix matrix;
		//
		// // Get image orientation
		// orientation = getRotateAngle(image);
		// matrix = new Matrix();
		// matrix.postRotate(orientation);
		//
		// Bitmap scaled_with_orientation = Bitmap.createBitmap(
		// return_bmp, 0, 0, return_bmp.getWidth(),
		// return_bmp.getHeight(), matrix, true);
		//
		// return_bmp = scaled_with_orientation;
		// System.gc();
		// } catch (Exception e) {
		// return_bmp = null;
		// }
		// return return_bmp;
		// // Bitmap return_bmp = null;
		// // try {
		// // return_bmp = MediaStore.Images.Media.getBitmap(
		// // TakeAPicActivity.this.getContentResolver(), image);
		// // } catch (FileNotFoundException e) {
		// // return_bmp = null;
		// // } catch (IOException e) {
		// // return_bmp = null;
		// // } finally {
		// // if (return_bmp != null) {
		// // try {
		// // ImageResizer.decodeSampledBitmapFromFile(filename, reqWidth,
		// // reqHeight)
		// // } catch (Exception e) {
		// // }
		// // // Bitmap scaled_with_orientation;
		// // // try {
		// // // int orientation;
		// // // Matrix matrix;
		// // //
		// // // // Get image orientation
		// // // orientation = getRotateAngle(image);
		// // // matrix = new Matrix();
		// // // matrix.postRotate(orientation);
		// // //
		// // // // Generate the small bitmap => 800 x 600
		// // // Bitmap small_bmp = thumbImage(return_bmp, 800, 600);
		// // // scaled_with_orientation = Bitmap.createBitmap(
		// // // small_bmp, 0, 0, small_bmp.getWidth(),
		// // // small_bmp.getHeight(), matrix, true);
		// // //
		// // // return_bmp = scaled_with_orientation;
		// // // } catch (Exception e) {
		// // // return_bmp = null;
		// // // }
		// // }
		// // }
		// // return return_bmp;
		// }
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_take_picture);
		handler.post(loadCamera);

		
		Bundle parametros = getIntent().getExtras();
		
		setContentView(R.layout.activity_take_picture);
		if(parametros!=null){
			if(parametros.getString("primeravez").equalsIgnoreCase("no")){
				AbsoluteLayout ventanaflotante = (AbsoluteLayout) findViewById(R.id.ventana_flotante);
				ventanaflotante.setVisibility(View.GONE);
			}
		}else{
			AbsoluteLayout ventanaflotante = (AbsoluteLayout) findViewById(R.id.ventana_flotante);
			ventanaflotante.setVisibility(View.GONE);
		}
		
		btn_take_a_pic = (ImageButton) findViewById(R.id.btn_pic);
		btn_take_a_pic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				takeAPicAction();
			}
		});

		/*btn_next = (ImageButton) findViewById(R.id.btn_next);
		btn_next.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				cancelPhotoDialog();
			}
		});*/

		btn_gallery = (ImageButton) findViewById(R.id.btn_back);
		btn_gallery.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				openUserGallery();
			}
		});
	}

	/*@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			AppGlobal.getInstance().dispatcher.open(TakePicture.this,
					"main", true);
		}
		return super.onKeyDown(keyCode, event);
	}*/

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_CANCELED) {
			if (requestCode == SELECT_PHOTO && resultCode == RESULT_OK
					&& null != data) {
				AppGlobal.getInstance().showLoading(TakePicture.this,
						getString(R.string.thanks_layout_espere_texto));
				Uri selected_image = data.getData();
				// prepareImageUri(selectedImage);
				(new PrepareImageTask()).execute(selected_image);
			} else {
				Log.e(TAG, "Error selecting image from albums");
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	protected void openUserGallery() {
		loadIntentForLoadImages();
	}

	private void loadIntentForLoadImages() {
		Intent photoPickerIntent = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		photoPickerIntent.setType("image/*");
		startActivityForResult(photoPickerIntent, SELECT_PHOTO);
	}

	private int getRotateAngle(Uri imageUri) {
		String[] columns = { MediaStore.Images.Media.DATA,
				MediaStore.Images.Media.ORIENTATION };
		Cursor cursor = getContentResolver().query(imageUri, columns, null,
				null, null);
		if (cursor == null) {
			return 0;
		}
		cursor.moveToFirst();

		int orientationColumnIndex = cursor.getColumnIndex(columns[1]);
		int orientation = cursor.getInt(orientationColumnIndex);
		cursor.close();
		return orientation;
	}

	private void cancelPhotoDialog() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.dialog_no_image_content)
				.setTitle(R.string.dialog_no_image_title)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setCancelable(false)
				.setPositiveButton(R.string.dialog_no_gps_btn_active,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(final DialogInterface dialog,
									final int id) {
								dialog.dismiss();
							}
						});
		final AlertDialog alert = builder.create();
		alert.show();
	}

	protected void takeAPicAction() {
		// camera.stopPreview();
		// System.gc();
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
						Manager.getInstance().setImageTemp(scaled_with_orientation);
						AppGlobal.getInstance().dispatcher.open(
								TakePicture.this, "preview", true);
					} catch (Exception e) {
					}
				}

			}
		};
		camera.takePicture(shutterCallBack, pictureCallBack, pictureCallBackJPG);
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
		
		try{
			if (previewing) {
				if (camera != null)
					camera.stopPreview();
			}

			// Set camera parameters
			Camera.Parameters p = camera.getParameters();
			// List<String> focusModes = p.getSupportedFocusModes();
			// if (focusModes
			// .contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
			// p.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
			// }
			// List<String> flashModes = p.getSupportedFlashModes();
			// if (flashModes.contains(Camera.Parameters.FLASH_MODE_AUTO)) {
			// p.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
			// }
			// List<String> sceneModes = p.getSupportedSceneModes();
			// if (sceneModes.contains(Camera.Parameters.SCENE_MODE_ACTION)) {
			// p.setSceneMode(Camera.Parameters.SCENE_MODE_ACTION);
			// }
			// p.setPictureFormat(PixelFormat.JPEG);
			// p.setPreviewSize(width, height);
			// p.setPictureSize(width, height);
			if (camera != null)
				camera.setParameters(p);

			// if (android.os.Build.VERSION.SDK_INT >= 8) {
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
		}catch(Exception e){
			e.printStackTrace();
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
			e.printStackTrace();
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		try {
			camera = Camera.open();
			camera.setPreviewDisplay(holder);
		} catch (IOException e) {
			if (camera != null)
				camera.release();
			camera = null;
			e.printStackTrace();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		if (camera != null) {
			try{
				camera.setPreviewCallback(null);
				camera.stopPreview();
				camera.setPreviewCallback(null);
				previewing = false;
				camera.release();
				camera = null;
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}
	}
	
	public void cerrarVentana(View view){
		
		AbsoluteLayout ventanaflotante = (AbsoluteLayout) findViewById(R.id.ventana_flotante);
		ventanaflotante.setVisibility(View.GONE);
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
		builder.setMessage(R.string.dialog_no_image_content)
				.setTitle(R.string.dialog_no_image_title)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setCancelable(false)
				.setPositiveButton(R.string.dialog_no_image_btn_active,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(final DialogInterface dialog,
									final int id) {
								dialog.dismiss();
							}
						});
		final AlertDialog alert = builder.create();
		alert.show();
		
	}
	
	/*public void help(View view){
		
		Params p = new Params();
		p.AddParam("take", "1");
		Bundle parametros = getIntent().getExtras();
		p.AddParam("primeravez", parametros.getString("primeravez"));
		AppGlobal.getInstance().dispatcher.open(TakePicture.this, "main", true, p);
		
	}*/
	
}