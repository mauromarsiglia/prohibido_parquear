package alcaldiadebarranquilla.prohibidoparquear.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

@SuppressWarnings("serial")
public class Params extends ArrayList<NameValuePair> {

	public Params() {
		super();
	}

	public void AddParam(String name, String value) {
		add(new BasicNameValuePair(name, value));
	}

	public String htmlParams() {
		String combinedParams = "";
		for (NameValuePair p : this) {
			String paramString = "";
			try {
				paramString = p.getName() + "="
						+ URLEncoder.encode(p.getValue(), "UTF-8");
			} catch (UnsupportedEncodingException e) {
			}
			if (combinedParams.length() > 1) {
				combinedParams += "&" + paramString;
			} else {
				combinedParams += paramString;
			}
		}
		return combinedParams;
	}
}
