package alcaldiadebarranquilla.prohibidoparquear.restful;

import java.util.EventListener;

public interface DoRestEventListener extends EventListener {
	void onComplete(int status, String body);
	void onError();
}
