package alcaldiadebarranquilla.prohibidoparquear;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import alcaldiadebarranquilla.prohibidoparquear.controller.Manager;
import alcaldiadebarranquilla.prohibidoparquear.library.Category;
import alcaldiadebarranquilla.prohibidoparquear.library.ItemCategoryAdapter;
import alcaldiadebarranquilla.prohibidoparquear.restful.DoRest;
import alcaldiadebarranquilla.prohibidoparquear.restful.DoRest.Verbs;
import alcaldiadebarranquilla.prohibidoparquear.restful.DoRestEventListener;
import alcaldiadebarranquilla.prohibidoparquear.util.AppConfig;
import alcaldiadebarranquilla.prohibidoparquear.util.AppGlobal;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;

public class Categories extends Activity {

	private static final String TAG = "Categories";
	private Manager manager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_categorias);

		manager = Manager.getInstance();
		if (manager.getCategories() != null) {
			fillCategories();
		} else {
			getCategories();
		}
	
	}

	public void fillCategories() {

		ListView lista = (ListView) findViewById(R.id.listado_categorias);

		Log.i(TAG, "Llenando el adapter de las categorias");

		ItemCategoryAdapter adapter = new ItemCategoryAdapter(this,
				manager.getCategories());
		lista.setAdapter(adapter);
		ProgressBar progreso = (ProgressBar) findViewById(R.id.carga_categoria_progreso);
		progreso.setVisibility(View.GONE);
		lista.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> array, View view,
					int position, long id) {
				
				Category cat = (Category) array.getItemAtPosition(position);
				Log.e(TAG, cat+"");
				CambiarActivity(cat);
			}

		});
	}

	public void CambiarActivity(Category cat) {
		
		Manager manager = Manager.getInstance();
		manager.setSelectedCategory(cat.getId());
		Log.i(TAG,Categories.this+"");
		
		AppGlobal.getInstance().dispatcher.open(Categories.this,
				"take", false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	/**
	 * Obtener listado de categor’as de WS
	 */
	private void getCategories() {
		AppConfig.setProductionEnviroment();
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("key", AppConfig.PUSHER_KEY));
		params.add(new BasicNameValuePair("application_token",
				AppConfig.PUSHER_APP_KEY));
		DoRest rest_get_categories = new DoRest(AppConfig.EVENT_CATEGORIES_URL,
				Verbs.POST, params);
		rest_get_categories.setListener(new DoRestEventListener() {

			@Override
			public void onError() {
				Log.e(TAG, "Se presento un error al cargar las categorias");
				//showCategoriesError();
				showCategoriesError();
			}

			@Override
			public void onComplete(int status, String json_data_string) {
				if (status == 200) {
					JSONObject response = null;
					try {
						response = new JSONObject(json_data_string);
					} catch (JSONException e) {
						response = null;
					} finally {
						if (response != null) {
							JSONArray categories = null;
							JSONObject data = null;
							try {
								if (response.getBoolean("status")) {
									data = response.getJSONObject("data");
									categories = data
											.getJSONArray("categories");
								} else {
									JSONArray errors = response
											.getJSONArray("erros");
									Log.e(TAG, errors.toString());
									showCategoriesError();
								}
							} catch (JSONException e) {
								Log.e(TAG, e.getMessage());
							} finally {
								if (categories != null) {
									ArrayList<Category> categories_array = new ArrayList<Category>();
									for (int i = 0; i < categories.length(); i++) {
										JSONObject category = null;
										Category category_model = null;
										try {
											category = categories
													.getJSONObject(i);
											category_model = new Category(
													category.getString("name"),
													Integer.parseInt(category
															.getString("id")));
										} catch (JSONException e) {
										} finally {
											if (category_model != null)
												categories_array
														.add(category_model);
										}
									}
									manager.setCategorias(categories_array);
									fillCategories();
								} else {
									showCategoriesError();
								}
							}
						} else {
							showCategoriesError();
						}
					}
				} else {
					showCategoriesError();
				}
			}
		});
		rest_get_categories.call();
	}

	protected void showCategoriesError() {
		ProgressBar progreso = (ProgressBar) findViewById(R.id.carga_categoria_progreso);
		progreso.setVisibility(View.GONE);
		if(!verificaConexion(this)){
			new AlertDialog.Builder(this)
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setTitle(R.string.dialog_no_internet_title)
			.setMessage(R.string.dialog_no_internet_content_category)
			.setPositiveButton(
					R.string.dialog_no_internet_btn_reintentar,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							showProgress();
							getCategories();
							dialog.cancel();
						}
					})
			.setNegativeButton(
					R.string.dialog_no_category_btn_salir,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							Categories.this.finish();
						}

					}).show();
	

				
		}else{

			new AlertDialog.Builder(this)
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setTitle(R.string.dialog_no_category_title)
			.setMessage(R.string.dialog_no_category_content)
			.setPositiveButton(
					R.string.dialog_no_category_btn_reintentar,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							showProgress();
							getCategories();
							dialog.cancel();
						}
					})
			.setNegativeButton(
					R.string.dialog_no_category_btn_salir,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							Categories.this.finish();
						}

					}).show();
	

			
			Log.e(TAG,
					"Error, debe mostrar mensaje de alerta con opcion de reintentar y validad conexion a internet");
		}
	}
	public static boolean verificaConexion(Context ctx) {
	    boolean bConectado = false;
	    ConnectivityManager connec = (ConnectivityManager) ctx
	            .getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo[] redes = connec.getAllNetworkInfo();
	    for (int i = 0; i < 2; i++) {
	  
	        if (redes[i].getState() == NetworkInfo.State.CONNECTED) {
	            bConectado = true;
	        }
	    }
	    return bConectado;
	}
	
	
	
	
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			this.buildAlertExit();
		}
		return super.onKeyDown(keyCode, event);
	}

	private void buildAlertExit() {

		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.dialog_exit_content)
				.setTitle(R.string.dialog_exit_title)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setCancelable(false)
				.setPositiveButton(R.string.dialog_exit_btn_aceptar,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(final DialogInterface dialog,
									final int id) {
								Categories.this.finish();
							}
						})
				.setNegativeButton(R.string.dialog_exit_btn_cancelar,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(final DialogInterface dialog,
									final int id) {
								dialog.cancel();
							}
						});
		final AlertDialog alert = builder.create();
		alert.show();

	}
	
	public void showProgress(){
		ProgressBar progreso = (ProgressBar) findViewById(R.id.carga_categoria_progreso);
		progreso.setVisibility(View.VISIBLE);
	}
}


