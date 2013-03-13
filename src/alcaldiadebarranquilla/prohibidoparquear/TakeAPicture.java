/**
 * 
 */
package alcaldiadebarranquilla.prohibidoparquear;

import java.io.FileNotFoundException;
import java.io.IOException;

import alcaldiadebarranquilla.prohibidoparquear.controller.Manager;
import alcaldiadebarranquilla.prohibidoparquear.controller.SurfaceController;
import alcaldiadebarranquilla.prohibidoparquear.util.AppGlobal;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

/**
 * @author Luster
 * 
 */
@SuppressLint("HandlerLeak")
public class TakeAPicture extends Activity {

	private SurfaceController mPreview;
	private ImageButton btn_take_a_pic;
	private final String TAG = "TakeAPicture";
	private static final int SELECT_PHOTO = 100;
	protected static final int ON_IMAGE_ERROR = 1;
	protected static
	final int ON_IMAGE_OK = 2;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.i(TAG, "onCreate");

		setContentView(R.layout.activity_take_picture);

		// Create our Preview view and set it as the content of our activity.
		mPreview = new SurfaceController(this);
		FrameLayout preview = (FrameLayout) findViewById(R.id.camera_surface);
		preview.addView(mPreview);

		Manager manager = Manager.getInstance();

		if (!manager.isPrimeraVezAyuda(this)) {
			RelativeLayout ventanaflotante = (RelativeLayout) findViewById(R.id.ventana_flotante);
			ventanaflotante.setVisibility(View.GONE);
			activarBotones();
		}

	}

	public void cerrarVentana(View view) {
		RelativeLayout ventanaflotante = (RelativeLayout) findViewById(R.id.ventana_flotante);
		ventanaflotante.setVisibility(View.GONE);
		activarBotones();
		SharedPreferences settings = getSharedPreferences("perfil",
				MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("primeravez", "no");
		editor.commit();
		activarBotones();
	}

	@Override
	public void onStop() {
		super.onStop();
		Log.i(TAG, "onStop");
		mPreview.stop();
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.i(TAG, "onResume");
		mPreview.resume();
	}

	public void takePicture() {
		Log.i(TAG, "takePicture");
		mPreview.takeAPicAction();
	}

	public void activarBotones() {
		btn_take_a_pic = (ImageButton) findViewById(R.id.btn_pic);
		btn_take_a_pic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				takePicture();
			}
		});
	}

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

		private Bitmap prepareImageUri(Uri image) {

			Log.i(TAG, "prepareImageUri");

			Bitmap original_bmp = null;
			try {
				original_bmp = MediaStore.Images.Media.getBitmap(
						TakeAPicture.this.getContentResolver(), image);
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

	@SuppressLint("HandlerLeak")
	private Handler preparedImage = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			AppGlobal.getInstance().hideLoading();

			switch ((int) msg.what) {
			case ON_IMAGE_ERROR:
				AppGlobal
						.getInstance()
						.showSimpleDialog(
								TakeAPicture.this,
								"Hubo un inconveniente",
								"No fue posible procesar la fotografía, verifica que has seleccionado una imagen de el álbum de fotos de tú cámara e inténtalo nuevamente. Algunas imágenes hacen parte de álbumes que están en internet.");
				break;
			case ON_IMAGE_OK:
				Bitmap bmp = (Bitmap) msg.obj;
				Manager.getInstance().setImageTemp(bmp);
				AppGlobal.getInstance().dispatcher.open(TakeAPicture.this,
						"preview", true);
				break;
			}
		}
	};

	private int getRotateAngle(Uri imageUri) {

		Log.i(TAG, "getRotateAngle");

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
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			AppGlobal.getInstance().dispatcher.open(TakeAPicture.this,
					"category", true);
		}
		return super.onKeyDown(keyCode, event);
	}

	
	



}
