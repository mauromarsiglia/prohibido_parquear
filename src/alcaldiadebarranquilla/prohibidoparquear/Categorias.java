package alcaldiadebarranquilla.prohibidoparquear;

import alcaldiadebarranquilla.prohibidoparquear.controller.Manager;
import alcaldiadebarranquilla.prohibidoparquear.library.Categoria;
import alcaldiadebarranquilla.prohibidoparquear.library.ItemCategoriaAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;


public class Categorias extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_categorias);
		llenarCategorias();
		
	}
	
	public void llenarCategorias(){
		Manager manager = Manager.getInstance();
		ListView lista = (ListView) findViewById(R.id.listado_categorias);
		/*ArrayAdapter<Categoria> adaptador = new ArrayAdapter<Categoria>(Categorias.this, android.R.layout.simple_list_item_1,manager.getCategorias());
		lista.setAdapter(adaptador);
		*/
		
        Log.i("por aqui","aqui");
	
	         
	    ItemCategoriaAdapter adapter = new ItemCategoriaAdapter(this, manager.getCategorias());
	         
	    lista.setAdapter(adapter);    
		
		lista.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> array, View view,
					int position, long id) {
				
				Categoria  cat = (Categoria) array.getItemAtPosition(position);
				//Toast.makeText(Categorias.this, cat.toString(), Toast.LENGTH_SHORT).show();
				CambiarActivity(cat);
			}
			
		});
	}

    public void CambiarActivity(Categoria cat){
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
	

}
