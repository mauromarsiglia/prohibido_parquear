package alcaldiadebarranquilla.prohibidoparquear;

import alcaldiadebarranquilla.prohibidoparquear.util.AppGlobal;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.View;

public class Main extends Activity {
	
	private String primeravez="no";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		AppGlobal.getInstance().initialize(this);
		
		Bundle parametros = getIntent().getExtras();
		
		String take = null;
		if(parametros!=null){
			take = parametros.getString("take");
		}
		
		if(take == null){
			
			SharedPreferences settings = getSharedPreferences("perfil", MODE_PRIVATE);
			String uso = settings.getString("uso", "false");
			
			if(uso.equalsIgnoreCase("false")){
				SharedPreferences.Editor editor = settings.edit();
				editor.putString("uso","true");
				editor.commit();
				primeravez="si";
				
				if(parametros!=null)
					primeravez= parametros.getString("primeravez");
				
				
			}else{
				Intent i = new Intent(this, TakePicture.class);
				if(parametros!=null)
					primeravez= parametros.getString("primeravez");
				i.putExtra("primeravez", primeravez);
				startActivity(i);
				finish();
			}
			
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	public void takePicture(View view){
		Bundle parametros = getIntent().getExtras();
		Intent i = new Intent(this, TakePicture.class);
		if(parametros!=null)
			primeravez= parametros.getString("primeravez");
		
		Log.i("primeravez",primeravez+"");
		i.putExtra("primeravez", primeravez);
		startActivity(i);
		finish();
	}

}
