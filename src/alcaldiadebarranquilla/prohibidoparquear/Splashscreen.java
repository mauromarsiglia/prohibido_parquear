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
import alcaldiadebarranquilla.prohibidoparquear.restful.DoRest;
import alcaldiadebarranquilla.prohibidoparquear.restful.DoRest.Verbs;
import alcaldiadebarranquilla.prohibidoparquear.restful.DoRestEventListener;
import alcaldiadebarranquilla.prohibidoparquear.util.AppConfig;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;

public class Splashscreen extends Activity {

	private boolean mIsBackButtonPressed;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_splashscreen);
		Handler handler = new Handler();
		handler.postDelayed(getRunnableStartApp(), 1000);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	private Runnable getRunnableStartApp() {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				finish();
				if (!mIsBackButtonPressed) {
					Intent intent = new Intent(Splashscreen.this, Main.class);
					startActivity(intent);
					overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
				}
			}
		};
		return runnable;
	}

	@Override
	public void onBackPressed() {

		// set the flag to true so the next activity won't start up
		mIsBackButtonPressed = true;
		super.onBackPressed();

	}
}
