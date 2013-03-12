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
import alcaldiadebarranquilla.prohibidoparquear.restful.DoRestEventListener;
import alcaldiadebarranquilla.prohibidoparquear.restful.DoRest.Verbs;
import alcaldiadebarranquilla.prohibidoparquear.util.AppConfig;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

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
		lista.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> array, View view,
					int position, long id) {
				Category cat = (Category) array.getItemAtPosition(position);
				CambiarActivity(cat);
			}

		});
	}

	public void CambiarActivity(Category cat) {
		Intent intent = new Intent();
		intent.setClass(this, TakeAPicture.class);
		Manager manager = Manager.getInstance();
		manager.setSelectedCategory(cat.getId());
		startActivity(intent);
		finish();
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
				Log.e(TAG, "Se present— un error al cargar las categorias");
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
		Log.e(TAG,
				"Error, debe mostrar mensaje de alerta con opcion de reintentar y validad conexion a internet");
	}

}
