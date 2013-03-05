/**
 * 
 */
package alcaldiadebarranquilla.prohibidoparquear.restful;

/**
 * @author Luster
 *
 */
public class DoRestImages {
	/*
	private static final int GLOBAL_ERROR = 1;
	private static final int COMPLETE_UPLOAD = 2;
	private static final int IMAGE_ERROR = 3;
	private static final String TAG = "DoRestImages";
	private Context context;
	
	public DoRestImages(Context context){
		this.context = context;
	}
	

	
	private void sendData() {
		// save the report
		new SaveInBackground().execute("");
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

	}
	
	private Dialog crearDialogoAlerta(){
		
    	AlertDialog.Builder builder = new AlertDialog.Builder(context);
    	
    	builder.setTitle(R.string.dialog_no_internet_title);
    	builder.setMessage(R.string.dialog_no_internet_content);
    	builder.setPositiveButton(R.string.dialog_no_internet_btn_reintentar, new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				new SaveInBackground().execute("");
			}
		});
    	builder.setNegativeButton(R.string.dialog_no_internet_btn_cancelar, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.cancel();
				Intent i = new Intent(context, TakePicture.class);
				context.startActivity(i);
			}
		});
    	
    	return builder.create();
    	
    }
	*/

}
