/**
 * 
 */
package alcaldiadebarranquilla.prohibidoparquear;

import alcaldiadebarranquilla.prohibidoparquear.controller.Manager;
import alcaldiadebarranquilla.prohibidoparquear.util.AppGlobal;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;

/**
 * @author Soldier
 *
 */
public class Thanks extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_thanks);
	}
	
	public void exit(View view){
		this.finish();
	}
	
	public void nuevoReporte(View view){
		
		Manager.getInstance().setAddress(null);
		Manager.getInstance().setImage(null);
		Manager.getInstance().setLongitude(null);
		Manager.getInstance().setLatitude(null);
		
		AppGlobal.getInstance().dispatcher.open(
				Thanks.this, "take", true);
		
	}

}
