/**
 * 
 */
package alcaldiadebarranquilla.prohibidoparquear.controller;

import java.util.LinkedList;
import java.util.List;

import android.graphics.Bitmap;

/**
 * @author Luster
 *
 */
public class Manager {
	
	public final String PhoneModel = android.os.Build.MODEL;
	public final String AndroidVersion = android.os.Build.VERSION.RELEASE;
	private int selectedCategory;
	private Bitmap imageTemp;
	private List<Bitmap> images;
	private String latitude;
	private String longitude;
	private static Manager instance;
	private String address;
	private boolean responseEvent;
	
	public static Manager getInstance(){
		if(instance==null){
			instance = new Manager();
		}
		return instance;
	}
	
	private Manager(){
		this.images = new LinkedList<Bitmap>();
		this.selectedCategory = 2;
	}

	public List<Bitmap> getImages() {
		return images;
	}

	public void addImage() {
		if(imageTemp!=null)
			this.images.add(this.imageTemp);
		imageTemp = null;
	}
	
	public Bitmap getLastImage(){
		return this.images.get(this.images.size()-1);
	}
	
	public void clearImages(){
		this.images.clear();
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

	public int getSelectedCategory() {
		return selectedCategory;
	}

	public void setSelectedCategory(int selectedCategory) {
		this.selectedCategory = selectedCategory;
	}

	public boolean isResponseEvent() {
		return responseEvent;
	}

	public void setResponseEvent(boolean responseEvent) {
		this.responseEvent = responseEvent;
	}
	
	/**
	 * @return the imageTemp
	 */
	public Bitmap getImageTemp() {
		return imageTemp;
	}

	/**
	 * @param imageTemp the imageTemp to set
	 */
	public void setImageTemp(Bitmap imageTemp) {
		this.imageTemp = imageTemp;
	}

}
