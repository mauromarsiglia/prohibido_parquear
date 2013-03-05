/**
 * 
 */
package alcaldiadebarranquilla.prohibidoparquear;

import alcaldiadebarranquilla.prohibidoparquear.controller.Manager;
import alcaldiadebarranquilla.prohibidoparquear.util.AppGlobal;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;

/**
 * @author Luster
 *
 */
public class GeographicLocation extends Activity implements LocationListener{

	private LocationManager manager;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.acticity_geographic_location);
	}
	
	private void setGPSLocation(){
		
		this.manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
		if (!manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			buildAlertMessageNoGps();
		}else{
			manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1,
					0, this);
		}
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		setGPSLocation();
	}
	
	private void buildAlertMessageNoGps() {
		
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.dialog_no_gps_title)
				.setTitle(R.string.dialog_no_gps_content)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setCancelable(false)
				.setPositiveButton(R.string.dialog_no_gps_btn_active,
						new DialogInterface.OnClickListener() {
							public void onClick(final DialogInterface dialog,
									final int id) {
								startActivity(new Intent(
										Settings.ACTION_LOCATION_SOURCE_SETTINGS));
							}
						})
				.setNegativeButton(R.string.dialog_no_gps_btn_direccion,
						new DialogInterface.OnClickListener() {
							public void onClick(final DialogInterface dialog,
									final int id) {
								AppGlobal.getInstance().dispatcher.open(
										GeographicLocation.this, "address", true);
								dialog.cancel();
							}
						});
		final AlertDialog alert = builder.create();
		alert.show();
		
	}
	
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub

		if (location != null) {
			Manager.getInstance().setLongitude(Double.toString(location.getLongitude()));
			Manager.getInstance().setLatitude(Double.toString(location.getLatitude()));
			
			this.manager.removeUpdates(this);
			//Ir a gracias, puede ser aquí el error
			AppGlobal.getInstance().dispatcher.open(GeographicLocation.this, "thanks", true);
			
		}
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

}
