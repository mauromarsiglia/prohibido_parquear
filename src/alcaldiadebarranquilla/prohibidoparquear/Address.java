/**
 * 
 */
package alcaldiadebarranquilla.prohibidoparquear;

import alcaldiadebarranquilla.prohibidoparquear.util.AppGlobal;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * @author Soldier
 * 
 */
public class Address extends Activity {

	private EditText direccion;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_address);
		this.direccion = (EditText) findViewById(R.id.editText1);
	}

	public void btn_ok(View view) {

		String dir = this.direccion.getText().toString();

		if (dir.length() > 4) {
			AppGlobal.getInstance().dispatcher.open(Address.this, "thanks",
					true);
		} else {
			Toast.makeText(
					getBaseContext(),
					getResources().getString(
							R.string.direccion_layout_error_input),
					Toast.LENGTH_LONG).show();
		}

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
		builder.setMessage(R.string.dialog_back_address_content)
				.setTitle(R.string.dialog_back_address_title)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setCancelable(false)
				.setPositiveButton(R.string.dialog_back_address_btn_aceptar,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(final DialogInterface dialog,
									final int id) {
								dialog.dismiss();
								AppGlobal.getInstance().dispatcher.open(
										Address.this, "geographic", true);

							}
						})
				.setNegativeButton(R.string.dialog_back_address_btn_cancelar,
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

}
