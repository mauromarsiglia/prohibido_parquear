/**
 * 
 */
package alcaldiadebarranquilla.prohibidoparquear;

import alcaldiadebarranquilla.prohibidoparquear.controller.Manager;
import alcaldiadebarranquilla.prohibidoparquear.util.AppGlobal;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;

/**
 * @author Soldier
 * 
 */
public class Preview extends Activity {

	private ImageButton btn_done;
	private ImageButton btn_cancel;
	private ImageView image_preview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_preview);

		btn_done = (ImageButton) findViewById(R.id.btn_done);
		btn_done.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				Manager.getInstance().addImage();
				AppGlobal.getInstance().dispatcher.open(Preview.this,
						"geographic", true);
			}
		});

		btn_cancel = (ImageButton) findViewById(R.id.btn_cancel);
		btn_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AppGlobal.getInstance().dispatcher.open(Preview.this, "take",
						true);
			}
		});

		// The Original image
		Bitmap image = Manager.getInstance().getImageTemp();

		// set the image
		image_preview = (ImageView) findViewById(R.id.image_preview);
		image_preview.setImageBitmap(image);

	}

}
