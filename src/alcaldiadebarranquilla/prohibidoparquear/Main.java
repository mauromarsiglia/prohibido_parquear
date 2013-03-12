package alcaldiadebarranquilla.prohibidoparquear;

import alcaldiadebarranquilla.prohibidoparquear.controller.Manager;
import alcaldiadebarranquilla.prohibidoparquear.util.AppGlobal;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class Main extends Activity {
	
	//private String primeravez="no";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i("Bundle",savedInstanceState+"");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		AppGlobal.getInstance().initialize(this);
		
		Manager manager = Manager.getInstance();
		Bundle parametros = getIntent().getExtras();
		
		String take = null;
		if(parametros!=null){
			take = parametros.getString("take");
		}
		
		if(savedInstanceState!=null){
			if(savedInstanceState.getString("rotar")!=null){
				take="";
			}
		}
		
		if(take == null){
			if(!manager.isPrimeraVezMensaje(this)){
					Intent i = new Intent(this, Categorias.class);
					startActivity(i);
					finish();
			}	
		}
	}
	
	public void takePicture(View view){
		Intent i = new Intent(this, Categorias.class);
		startActivity(i);
		finish();
	}
	
	
	@Override
	public void onConfigurationChanged(Configuration config){
		
		super.onConfigurationChanged(config);
		Log.i("cambio","cambio");
	}
	
	
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString("rotar", "rotar");
		super.onSaveInstanceState(outState);
		Log.i("cambio2","cambio2");
	 }
}
