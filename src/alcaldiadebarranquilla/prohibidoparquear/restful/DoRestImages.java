/**
 * 
 */
package alcaldiadebarranquilla.prohibidoparquear.restful;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
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

import alcaldiadebarranquilla.prohibidoparquear.R;
import alcaldiadebarranquilla.prohibidoparquear.util.AppGlobal;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Message;
import android.util.Log;

/**
 * @author Luster
 *
 */
public class DoRestImages {
	
	/*private static final int GLOBAL_ERROR = 1;
	private static final int COMPLETE_UPLOAD = 2;
	private static final int IMAGE_ERROR = 3;
	private static final String TAG = "DoRestImages";
	
	private void sendData() {
		// save the report
		(new SaveInBackground()).execute("");
	}
	
	public HttpResponse postIncident(String url, MultipartEntity entity) {
		HttpResponse response = null;
		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(url);
		
		try {
			// Add your data
			httppost.setEntity(entity); // new UrlEncodedFormEntity(params,
										// HTTP.UTF_8));
			// Execute HTTP Post Request
			response = httpclient.execute(httppost);
			
			if (response != null) {
				HttpEntity data = response.getEntity();
				String responseString = EntityUtils.toString(data);
				Log.i(TAG, responseString);
			}
			
		} catch (ClientProtocolException e) {
			response = null;
		} catch (IOException e) {
			response = null;
		}
		return response;
	}
	
	private class SaveInBackground extends AsyncTask<String, Integer, Void> {

		@Override
		protected Void doInBackground(String... params) {

			// Date params
			Date date = Calendar.getInstance().getTime();
			SimpleDateFormat submitFormat = new SimpleDateFormat(
					"MM/dd/yyyy hh:mm:ss aa", Locale.US);
			String string_date = submitFormat.format(date);
			String dates[] = string_date.split(" ");
			String time[] = dates[1].split(":");

			// Categories params
			String categories = "1";

			// Build params list
			MultipartEntity entity = new MultipartEntity(
					HttpMultipartMode.BROWSER_COMPATIBLE);
			
			boolean entityDone = true;
			
			try {
				entity.addPart("task", new StringBody("report"));
				entity.addPart("incident_title", new StringBody(ANDROID_REPORT));
				entity.addPart("incident_description", new StringBody(description));
				entity.addPart("incident_date", new StringBody(dates[0]));
				entity.addPart("incident_hour", new StringBody(time[0]));
				entity.addPart("incident_minute", new StringBody(time[1]));
				String ampm_s = dates[2].toLowerCase();
				entity.addPart("incident_ampm", new StringBody(ampm_s));
				entity.addPart("incident_category", new StringBody(categories));
				entity.addPart("latitude", new StringBody(latitude));
				entity.addPart("longitude", new StringBody(longitude));
				entity.addPart("location_name", new StringBody(address_description));
				entityDone = true;
			} catch (UnsupportedEncodingException e) {
				entityDone = false;
			} finally {
				if (entityDone) {
					// Save the file
					if (AppGlobal.getInstance().image != null) {
						String imagePath = saveImage(AppGlobal.getInstance().image);
						Log.i(TAG, "Image saved at: " + imagePath);
						if (imagePath != null) {
							FileBody body = new FileBody(
									new File(imagePath));
							String tencodign = body.getTransferEncoding();
							Log.i(TAG, tencodign);
							entity.addPart("incident_photo[]", body);
						} else {
							Log.e(TAG, "Error guardando imagen");
							responseHandler.sendEmptyMessage(IMAGE_ERROR);
						}
					}

					// Call to post incident
					if (postIncident("http://190.145.115.12:8015/ushahidi/api",
							entity) != null) {
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
					+ File.separator + "quierosonar";
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

	}*/
	

}
