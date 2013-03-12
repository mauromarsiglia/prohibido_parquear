package alcaldiadebarranquilla.prohibidoparquear.restful;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

@SuppressLint({ "HandlerLeak", "HandlerLeak" })
public class DoRest {

	private static final String TAG = "DoRest";

	public static enum Verbs {
		GET, POST
	}

	private static final int ON_COMPLETE = 0x1;
	private static final int ON_ERROR = 0x2;

	private String url;
	private HttpEntity params;
	private Verbs verb;

	private DoRestEventListener listener;
	
	public DoRest(){
		
	}

	public DoRest(String url, Verbs verb, List<NameValuePair> params) {
		this.url = url;
		this.verb = verb;
		try {
			this.params = new UrlEncodedFormEntity(params);
		} catch (UnsupportedEncodingException e) {
			Log.e(TAG, e.getMessage());
		}
	}
	
	private Handler doHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Log.i(TAG, "handleMessage: " + msg.what);
			switch (msg.what) {
			case ON_COMPLETE:
				if (getListener() != null) {
					Log.i(TAG, "There are listener and call it");
					Bundle data = msg.getData();
					getListener().onComplete(data.getInt("status"),
							data.getString("body"));
				}
				break;
			case ON_ERROR:
				if (getListener() != null) {
					getListener().onError();
				}
				break;
			}
		}
	};

	private class DoInBackground extends AsyncTask<Void, Integer, String> {

		@Override
		protected String doInBackground(Void... empty) {
			if (params == null || url == null)
				return null;
			return doPost(url, params);
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
		}

		private String handleResponse(HttpResponse response) {
			String responseString = null;
			if (response != null) {
				HttpEntity entityData = response.getEntity();
				try {
					responseString = EntityUtils.toString(entityData);
				} catch (ParseException e) {
					responseString = null;
					Log.e(TAG, e.getMessage());
				} catch (IOException e) {
					responseString = null;
					Log.e(TAG, e.getMessage());
				} finally {
					int status = response.getStatusLine().getStatusCode();
					Bundle data = new Bundle();
					data.putInt("status", status);
					data.putString("body", responseString);

					// Show log
					Log.i(TAG, "HTTP STATUS: " + status);
					Log.i(TAG, "HTTP BODY: " + responseString);

					if (responseString.length() > 0) {
						Log.i(TAG, responseString);
						Message msg = new Message();
						msg.what = ON_COMPLETE;
						msg.setData(data);
						doHandler.sendMessage(msg);
						Log.i(TAG, "Message was sent");
					} else {
						Log.i(TAG, "Message was sent error");
						doHandler.sendEmptyMessage(ON_ERROR);
					}
				}
			} else {
				Log.i(TAG, "Message was sent error");
				doHandler.sendEmptyMessage(ON_ERROR);
			}
			return responseString;
		}

		private String doPost(String url, HttpEntity body) {
			HttpResponse response = null;

			// Create a new HttpClient and Post Header
			if (verb != null) {
			}
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httrest = new HttpPost(url);
			httrest.setHeader("Accept-Language", "es-es");
			httrest.setHeader("Accept", "application/json");
			//httrest.setHeader("Content-type", "application/json");
			httrest.setHeader("Content-Type", "application/x-www-form-urlencoded");
			httrest.setHeader("Accept-Encoding", "gzip, deflate");
			httrest.setEntity(body);

			try {
				Log.i(TAG, "--------------------------------------------------");
				Log.i(TAG, "URL: " + url);
				Log.i(TAG, "BODY: " + body.toString());
				response = httpclient.execute(httrest);
			} catch (ClientProtocolException e) {
				response = null;
				Log.e(TAG, e.getMessage());
			} catch (IOException e) {
				response = null;
				Log.e(TAG, e.getMessage());
			}

			return handleResponse(response);
			
		}
	}
	
	public DoRest(String url, Verbs verb, List<NameValuePair> params, List<Bitmap> images) {
		
		this.url = url;
		this.verb = verb;
		
		try {
			this.params = new UrlEncodedFormEntity(params);
		} catch (UnsupportedEncodingException e) {
			Log.e(TAG, e.getMessage());
		}
	}

	public void call() {
		new DoInBackground().execute();
	}

	public DoRestEventListener getListener() {
		return listener;
	}

	public void setListener(DoRestEventListener listener) {
		this.listener = listener;
	}
}
