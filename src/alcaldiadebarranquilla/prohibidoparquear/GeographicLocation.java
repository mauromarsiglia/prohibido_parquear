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
import android.util.Log;

/**
 * @author Luster
 * 
 */
public class GeographicLocation extends Activity implements LocationListener {

	private LocationManager manager;
	private boolean rotate=false;
	private String  TAG="GEOGRAPHICLOCATION";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.acticity_geographic_location);
		if(!isFirst(savedInstanceState)){
			rotate=true;
		}
	
		
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString("rotate", "rotate");
		super.onSaveInstanceState(outState);
	 }

	
	private boolean isFirst(Bundle savedInstanceState){
		if(savedInstanceState!=null){
			if(savedInstanceState.getString("rotate")!=null){
				return  false;
			}else{
				return true;
			}
		}else{
			return true;
		}
	
	}
	private void setGPSLocation() {

		this.manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

			buildAlertMessageNoGps();

		} else {

			manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1,
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
		builder.setMessage(R.string.dialog_no_gps_content)
				.setTitle(R.string.dialog_no_gps_title)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setCancelable(false)
				.setPositiveButton(R.string.dialog_no_gps_btn_active,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(final DialogInterface dialog,
									final int id) {
								startActivity(new Intent(
										Settings.ACTION_LOCATION_SOURCE_SETTINGS));
							}
						})
				.setNegativeButton(R.string.dialog_no_gps_btn_direccion,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(final DialogInterface dialog,
									final int id) {
								AppGlobal.getInstance().dispatcher.open(
										GeographicLocation.this, "address",
										true);
								dialog.cancel();
							}
						});
		final AlertDialog alert = builder.create();
		alert.show();

	}

	@Override
	public void onLocationChanged(Location location) {

		if (location != null) {
			Manager.getInstance().setLongitude(
					Double.toString(location.getLongitude()));
			Manager.getInstance().setLatitude(
					Double.toString(location.getLatitude()));

			this.manager.removeUpdates(this);

			Log.i("localizacion", "localizacion");
					if(!rotate){
						AppGlobal.getInstance().dispatcher.open(
								GeographicLocation.this,
								"thanks", true);
					}
				
		
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