package alcaldiadebarranquilla.prohibidoparquear.util;

import alcaldiadebarranquilla.prohibidoparquear.Address;
import alcaldiadebarranquilla.prohibidoparquear.Categories;
import alcaldiadebarranquilla.prohibidoparquear.GeographicLocation;
import alcaldiadebarranquilla.prohibidoparquear.Main;
import alcaldiadebarranquilla.prohibidoparquear.Preview;
import alcaldiadebarranquilla.prohibidoparquear.TakeAPicture;
//import alcaldiadebarranquilla.prohibidoparquear.TakePicture;
import alcaldiadebarranquilla.prohibidoparquear.Thanks;
import alcaldiadebarranquilla.prohibidoparquear.controller.ActivityDispatcher;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

public class AppGlobal {

	private static AppGlobal instance;
	private ProgressDialog dialog;
	private AlertDialog alertDialog;
	public ActivityDispatcher dispatcher;
	public int answered_questions;

	private AppGlobal() {
	}

	public void initialize(Context context) {

		dispatcher = new ActivityDispatcher();
		dispatcher.addHandler("main", Main.class);
		dispatcher.addHandler("take", TakeAPicture.class);
		dispatcher.addHandler("preview", Preview.class);
		dispatcher.addHandler("geographic", GeographicLocation.class);
		dispatcher.addHandler("thanks", Thanks.class);
		dispatcher.addHandler("address", Address.class);
		dispatcher.addHandler("category", Categories.class);

	}

	public static synchronized AppGlobal getInstance() {
		if (instance == null) {
			instance = new AppGlobal();
		}
		return instance;
	}

	public void showSimpleDialog(Activity handler, String title, String content) {
		AlertDialog.Builder builder = new AlertDialog.Builder(handler);
		builder.setMessage(content);
		builder.setCancelable(true);
		builder.setTitle(title);
		builder.setIcon(android.R.drawable.ic_dialog_alert).setPositiveButton(
				"De acuerdo!", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						alertDialog.dismiss();
						alertDialog = null;
					}
				});
		alertDialog = builder.create();
		alertDialog.show();
	}

	public void showLoading(Activity handler, String message) {
		showLoading(handler, "", message);
	}

	public void showLoading(Activity handler, String title, String message) {
		dialog = ProgressDialog.show(handler, title, message);
	}

	public void hideLoading() {
		if (dialog != null) {
			dialog.dismiss();
		}
	}

}
