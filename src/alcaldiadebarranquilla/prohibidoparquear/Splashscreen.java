package alcaldiadebarranquilla.prohibidoparquear;



import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import alcaldiadebarranquilla.prohibidoparquear.controller.Manager;
import alcaldiadebarranquilla.prohibidoparquear.library.Categoria;
import alcaldiadebarranquilla.prohibidoparquear.restful.DoRest;
import alcaldiadebarranquilla.prohibidoparquear.restful.DoRest.Verbs;
import alcaldiadebarranquilla.prohibidoparquear.restful.DoRestEventListener;
import alcaldiadebarranquilla.prohibidoparquear.util.AppConfig;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;

public class Splashscreen extends Activity {

	 private boolean mIsBackButtonPressed;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);	
      	this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);	
		setContentView(R.layout.activity_splashscreen);
		obtenerListadoCategorias();
		Handler handler  = new Handler();
		handler.postDelayed(getRunnableStartApp(), 1000);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	
	private Runnable getRunnableStartApp(){
		Runnable runnable = new Runnable(){
		@Override
		public void run(){
			finish();
			if(!mIsBackButtonPressed){
				Intent intent = new Intent(Splashscreen.this,Main.class);
				startActivity(intent);
				overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
			}
		}
		};
		return runnable;
	}
	
	private void obtenerListadoCategorias(){
		AppConfig.setProductionEnviroment();
		List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("key", AppConfig.PUSHER_KEY));
        params.add(new BasicNameValuePair("application_token",AppConfig.PUSHER_APP_KEY));
        DoRest rest_get_categories = new DoRest(AppConfig.EVENT_CATEGORIES_URL,
          Verbs.POST, params);
        	rest_get_categories.setListener(new DoRestEventListener() {
        	
        	@Override	
        	public void onError() {
        		Log.i("error","error");
        		
        	}

        	@Override	
        	public void onComplete(int status, String data) {
        		Log.i("test", "test");
        		try {
					JSONObject json = new JSONObject(data);
					JSONArray array = json.getJSONObject("data").getJSONArray("categories");
					
					ArrayList<Categoria> categorias = new ArrayList<Categoria>();
					for(int i=0 ; i<array.length(); i++){
						 	JSONObject obj = array.getJSONObject(i);
						 	Categoria cate = new Categoria(obj.getString("name"), Integer.parseInt(obj.getString("id")));				           
						 	categorias.add(cate);
					}
					Manager manager = Manager.getInstance();
					manager.setCategorias(categorias);
					
					
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
        	});
        rest_get_categories.call();
	}
	
	@Override
	public void onBackPressed() {
		 
        // set the flag to true so the next activity won't start up
        mIsBackButtonPressed = true;
        super.onBackPressed();
 
    }
}
