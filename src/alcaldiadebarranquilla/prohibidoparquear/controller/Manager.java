/**
 * 
 */
package alcaldiadebarranquilla.prohibidoparquear.controller;

import android.graphics.Bitmap;

/**
 * @author Luster
 *
 */
public class Manager {
	
	private Bitmap image;
	private String latitude;
	private String longitude;
	private static Manager instance;
	private String address;
	
	public static Manager getInstance(){
		if(instance==null){
			instance = new Manager();
		}
		return instance;
	}
	
	private Manager(){
		
	}

	public Bitmap getImage() {
		return image;
	}

	public void setImage(Bitmap image) {
		this.image = image;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

}
