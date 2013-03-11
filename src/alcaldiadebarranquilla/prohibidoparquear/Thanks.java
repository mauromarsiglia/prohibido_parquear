/**
 * 
 */
package alcaldiadebarranquilla.prohibidoparquear;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import alcaldiadebarranquilla.prohibidoparquear.controller.Manager;
import alcaldiadebarranquilla.prohibidoparquear.util.AppConfig;
import alcaldiadebarranquilla.prohibidoparquear.util.AppGlobal;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @author Soldier
 *
 */
public class Thanks extends Activity {
	
	private final String TAG = "THANKS";
	private TextView mensaje;
	private LinearLayout thanks_container;
	private RelativeLayout wait_container;
	private static final int GLOBAL_ERROR = 1;
	private static final int COMPLETE_UPLOAD = 2;
	private static final int IMAGE_ERROR = 3;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);		
		setContentView(R.layout.activity_thanks);
		AppConfig.setDeveloperEnviroment();
		
		this.thanks_container = (LinearLayout)findViewById(R.id.thannks_container);
		this.thanks_container.setVisibility(View.GONE);
		this.sendData();
		
		mensaje = (TextView) findViewById(R.id.thanks);
		mensaje.setText(R.string.thanks_layout_title_ok_content);
		
	}
	
	private void sendData() {
		// save the report
		new SaveInBackground().execute("");
	}
	
	public void exit(View view){
		this.finish();
	}
	
	public void nuevoReporte(View view){
		
		Manager.getInstance().setAddress(null);
		Manager.getInstance().clearImages();
		Manager.getInstance().setLongitude(null);
		Manager.getInstance().setLatitude(null);
		
		AppGlobal.getInstance().dispatcher.open(
				Thanks.this, "take", true);
		
	}
	
	/*public boolean sendEvent(String application_token, String key, String longitude, String latitude,
			String device_model, String category_id, String address_description, String data, String description){
		
		setProgressBarIndeterminateVisibility(true);
		
		ArrayList<NameValuePair> postparameters2send= new ArrayList<NameValuePair>();
		postparameters2send.add(new BasicNameValuePair("application_token", application_token));
		postparameters2send.add(new BasicNameValuePair("key", key));
		postparameters2send.add(new BasicNameValuePair("event[longitude]", longitude));
		postparameters2send.add(new BasicNameValuePair("event[latitude]", latitude));
		postparameters2send.add(new BasicNameValuePair("event[device_model]", device_model));
		postparameters2send.add(new BasicNameValuePair("event[category_id]", category_id));
		postparameters2send.add(new BasicNameValuePair("event[address_description]", address_description));
		postparameters2send.add(new BasicNameValuePair("event[data]", data));
		postparameters2send.add(new BasicNameValuePair("event[description]", description));
		//event[photos_attributes][][image]
		//		
		//postparameters2send.add(new BasicNameValuePair("event[description]", description));
		
		
		DoRest rest_get_categories = new DoRest(AppConfig.ADD_EVENT_URL,
				Verbs.POST, postparameters2send, Manager.getInstance().getImages());
		
		rest_get_categories.setListener(new DoRestEventListener() {

			public void onError() {
				//Colocar Alert
				setProgressBarIndeterminateVisibility(false);
				crearDialogoAlerta().show();
			}

			public void onComplete(int status, String data) {
				setProgressBarIndeterminateVisibility(false);
				if (status == 200) {
					JSONObject json_data;
					try {
						json_data = new JSONObject(data);
						Log.i(TAG, json_data.toString());
						
						boolean status_response = json_data.getBoolean("status");
						
						Manager.getInstance().setResponseEvent(status_response);
						if(!status_response){
							mensaje.setText(R.string.thanks_layout_title_ok_error);
						}
						
						thanks_container.setVisibility(View.VISIBLE);
						
					} catch (JSONException e) {
						Log.e(TAG, e.getMessage());
					}
				}//82 
			}
		});
		rest_get_categories.call();
		return true;
      	
	}*/
	
	/*private Dialog crearDialogoAlerta(){
		
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	
    	builder.setTitle(R.string.dialog_no_internet_title);
    	builder.setMessage(R.string.dialog_no_internet_content);
    	builder.setPositiveButton(R.string.dialog_no_internet_btn_reintentar, new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				sendData();
			}
		});
    	builder.setNegativeButton(R.string.dialog_no_internet_btn_cancelar, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.cancel();
				AppGlobal.getInstance().dispatcher.open(
						Thanks.this, "take", true);
			}
		});
    	
    	return builder.create();
    	
    }*/
	
	public HttpResponse postIncident(String url, MultipartEntity entity) {
		
		HttpResponse response = null;
		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(url);
		
		Log.i(TAG, "I'm here HttpResponse");
		
		try {
			// Add your data
			httppost.setEntity(entity); // new UrlEncodedFormEntity(params,
										// HTTP.UTF_8));
			//httppost.setHeader("Content-Type", "multipart/form-data");
			// Execute HTTP Post Request
			response = httpclient.execute(httppost);
			
			if (response != null) {
				HttpEntity data = response.getEntity();
				Log.i(TAG, response.getStatusLine().getStatusCode()+"");
				String responseString = EntityUtils.toString(data);
				Log.i(TAG, responseString);
			}else{
				Log.i(TAG, "Response is null");
			}
			
		} catch (ClientProtocolException e) {
			response = null;
		} catch (IOException e) {
			response = null;
		}
		return response;
	}
	
	private Handler responseHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			getResponse(msg);
		}
	};
	
	protected void getResponse(Message msg) {
		
		AppGlobal.getInstance().hideLoading();
		
		switch (msg.what) {
		case IMAGE_ERROR:
			
			mensaje.setText(R.string.thanks_layout_title_ok_error);
			
			// final AlertDialog.Builder builder = new
			// AlertDialog.Builder(this);
			// builder.setMessage(R.string.no_fue_posible)
			// .setTitle(R.string.error_texto)
			// .setIcon(android.R.drawable.ic_dialog_alert)
			// .setCancelable(false)
			// .setPositiveButton(R.string.de_acuerdo,
			// new DialogInterface.OnClickListener() {
			// public void onClick(
			// final DialogInterface dialog,
			// final int id) {
			// dialog.cancel();
			// AppGlobal.getInstance().image = null;
			// sendData();
			// }
			// })
			// .setNegativeButton("Intentar de nuevo",
			// new DialogInterface.OnClickListener() {
			// public void onClick(
			// final DialogInterface dialog,
			// final int id) {
			// dialog.cancel();
			// sendData();
			// }
			// });
			// final AlertDialog alert = builder.create();
			// alert.show();
			// break;
		case GLOBAL_ERROR:
			
			mensaje.setText(R.string.thanks_layout_title_ok_error);
			
			new AlertDialog.Builder(this)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setTitle(R.string.dialog_no_internet_title)
					.setMessage(R.string.dialog_no_internet_content)
					.setPositiveButton(R.string.dialog_no_internet_btn_reintentar,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.cancel();
									sendData();
								}
							})
					.setNegativeButton(R.string.dialog_no_internet_btn_cancelar,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									AppGlobal.getInstance().dispatcher.open(
											Thanks.this, "take", true);
								}

							}).show();
			break;
		case COMPLETE_UPLOAD:
			mensaje.setText(R.string.thanks_layout_title_ok_content);
			thanks_container.setVisibility(View.VISIBLE);
			this.wait_container = (RelativeLayout)findViewById(R.id.wait_container);
			this.wait_container.setVisibility(View.GONE);
			break;
		}
	}
	
	private class SaveInBackground extends AsyncTask<String, Integer, Void> {

		@Override
		protected Void doInBackground(String... params) {

			// Build params list
			MultipartEntity entity = new MultipartEntity(
					HttpMultipartMode.BROWSER_COMPATIBLE);
			
			boolean entityDone = true;
			
			try {
				
				String addres = Manager.getInstance().getAddress();
				if(addres == null){
					addres = "";
				}
				
				entity.addPart("application_token", new StringBody(AppConfig.PUSHER_APP_KEY));
				entity.addPart("key", new StringBody(AppConfig.PUSHER_KEY));
				entity.addPart("event[longitude]", new StringBody(Manager.getInstance().getLongitude()));
				entity.addPart("event[latitude]", new StringBody(Manager.getInstance().getLatitude()));
				entity.addPart("event[device_model]", new StringBody(Manager.getInstance().PhoneModel+" - "+Manager.getInstance().AndroidVersion));
				entity.addPart("event[category_id]", new StringBody(Manager.getInstance().getSelectedCategory()+""));
				entity.addPart("event[address_description]", new StringBody(addres));
				entity.addPart("event[data]", new StringBody(""));
				entity.addPart("event[description]", new StringBody(""));
				
				entityDone = true;
				
			} catch (UnsupportedEncodingException e) {
				Log.i(TAG, "UnsupportedEncodingException => entityDone = false");
				entityDone = false;
			} finally {
				
				if (entityDone) {
					// Save the files
					
					for(Bitmap image : Manager.getInstance().getImages()){
						
						String imagePath = saveImage(image);
						
						Log.i(TAG, "Image saved at: " + imagePath);
						
						if (imagePath != null) {
							FileBody body = new FileBody(
									new File(imagePath));
							String tencodign = body.getTransferEncoding();
							Log.i(TAG, tencodign);
							entity.addPart("event[photos_attributes][][image]", body);
						} else {
							Log.e(TAG, "Error guardando imagen");
						}
						
					}
					
					// Call to post incident
					if (postIncident(AppConfig.ADD_EVENT_URL, entity) != null) {
						Message msg = new Message();
						msg.what = COMPLETE_UPLOAD;
						responseHandler.sendMessage(msg);
					} else {
						responseHandler.sendEmptyMessage(GLOBAL_ERROR);
					}
					
				} else {
					responseHandler.sendEmptyMessage(GLOBAL_ERROR);
				}
			}

			return null;
		}

		private String saveImage(Bitmap image) {

			String the_path = Environment.getExternalStorageDirectory()
					+ File.separator + "prohibidoparquear";
			File root = new File(the_path);
			boolean isReady = root.exists();
			
			if (!isReady) {
				try {
					if (Environment.getExternalStorageDirectory().canWrite()) {
						root.mkdirs();
						isReady = true;
					}
				} catch (Exception e) {
					isReady = false;
				}
			} else {
				
				String uid = UUID.randomUUID().toString();
				String the_file = the_path + File.separator + uid + ".jpg";
				OutputStream stream;
				boolean done = false;
				try {
					stream = new FileOutputStream(the_file);
					image.compress(CompressFormat.JPEG, 80, stream);
					stream.flush();
					stream.close();
					done = true;
				} catch (FileNotFoundException e) {
					done = false;
				} catch (IOException e) {
					done = false;
				} finally {
					if (done) {
						return the_file;
					}
				}

			}

			return null;
		}

	}

}
