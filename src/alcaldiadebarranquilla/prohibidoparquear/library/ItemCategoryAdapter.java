package alcaldiadebarranquilla.prohibidoparquear.library;

import java.util.ArrayList;

import alcaldiadebarranquilla.prohibidoparquear.R;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ItemCategoryAdapter extends BaseAdapter {

	protected Activity activity;
	protected ArrayList<Category> items;

	public ItemCategoryAdapter(Activity activity, ArrayList<Category> items) {
		this.activity = activity;
		this.items = items;
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int posicion) {
		return items.get(posicion);
	}

	@Override
	public long getItemId(int posicion) {
		return items.get(posicion).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;

		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) activity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			vi = inflater.inflate(R.layout.list_item_layout, null);
		}

		Category item = items.get(position);

		ImageView image = (ImageView) vi.findViewById(R.id.imagen);
		int imageResource = activity.getResources().getIdentifier(
				"@drawable/ic_launcher", null, activity.getPackageName());
		image.setImageDrawable(activity.getResources().getDrawable(
				imageResource));

		TextView nombre = (TextView) vi.findViewById(R.id.nombre_categoria);
		nombre.setText(item.getCategoria());

		// TextView tipo = (TextView) vi.findViewById(R.id.tipo);
		// tipo.setText(item.getTipo());

		return vi;
	}

}
