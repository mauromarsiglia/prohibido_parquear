/**
 * 
 */
package alcaldiadebarranquilla.prohibidoparquear;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import alcaldiadebarranquilla.prohibidoparquear.controller.Manager;
import alcaldiadebarranquilla.prohibidoparquear.restfull.DoRest;
import alcaldiadebarranquilla.prohibidoparquear.restfull.DoRestEventListener;
import alcaldiadebarranquilla.prohibidoparquear.restfull.DoRest.Verbs;
import alcaldiadebarranquilla.prohibidoparquear.util.AppConfig;
import alcaldiadebarranquilla.prohibidoparquear.util.AppGlobal;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author Soldier
 *
 */
public class Thanks extends Activity {
	
	private final String TAG = "THANKS";
	private TextView mensaje;
	private LinearLayout thanks_container;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);		
		setContentView(R.layout.activity_thanks);
		AppConfig.setProductionEnviroment();
		
		this.thanks_container = (LinearLayout)findViewById(R.id.thannks_container);
		this.thanks_container.setVisibility(View.GONE);
		
		this.sendEvent(AppConfig.PUSHER_APP_KEY, AppConfig.PUSHER_KEY, Manager.getInstance().getLongitude(),
				Manager.getInstance().getLatitude(), Manager.getInstance().PhoneModel+" - "+Manager.getInstance().AndroidVersion,
				Manager.getInstance().getSelectedCategory()+"", "", "", "");
		
		mensaje = (TextView) findViewById(R.id.thanks);
		
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
	
	public boolean sendEvent(String application_token, String key, String longitude, String latitude,
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
      	
	}
	
	private Dialog crearDialogoAlerta(){
		
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	
    	builder.setTitle(R.string.dialog_no_internet_title);
    	builder.setMessage(R.string.dialog_no_internet_content);
    	builder.setPositiveButton(R.string.dialog_no_internet_btn_reintentar, new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				sendEvent(AppConfig.PUSHER_APP_KEY, AppConfig.PUSHER_KEY, Manager.getInstance().getLongitude(),
						Manager.getInstance().getLatitude(), Manager.getInstance().PhoneModel+" - "+Manager.getInstance().AndroidVersion,
						Manager.getInstance().getSelectedCategory()+"", "", "", "");
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
    	
    }

}
