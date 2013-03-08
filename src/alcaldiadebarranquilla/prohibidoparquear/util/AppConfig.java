package alcaldiadebarranquilla.prohibidoparquear.util;

public class AppConfig {

	// WS paths
	private static final String EVENTS_ADD = "events";
	private static final String EVENTS_CATEGORIES = "events/categories";

	// Developer URLs
	private static final String DEV_PUSHERS_API_URL = "http://192.168.1.15:3000/api/pushers/";
	private static final String DEV_PUSHER_KEY = "ae02fa82a0566c9270159ade86003f60d30de97e";
	private static final String DEV_PUSHER_APP_KEY = "ac7db10cc30c7a2829f2c68e61edc8fe419fc9fe";

	// Production URLs
	private static final String PRO_PUSHERS_API_URL = "http://geoevents.herokuapp.com/api/pushers/";
	private static final String PRO_PUSHER_KEY = "6a62f4e09633bd32b4efcac14f694bb0b525f3d4";
	private static final String PRO_PUSHER_APP_KEY = "89b0ea9c3293aa3ccf64b1cc72e254d7cc4e3a87";
	//APP KEY => 89b0ea9c3293aa3ccf64b1cc72e254d7cc4e3a87
	//IDENTIFICADOR = 2
	//KEY => 6a62f4e09633bd32b4efcac14f694bb0b525f3d4
	//APP TOKEN => 89b0ea9c3293aa3ccf64b1cc72e254d7cc4e3a87

	// URL
	public static String PUSHERS_API_URL;
	public static String EVENT_CATEGORIES_URL;
	public static String ADD_EVENT_URL;

	// Developer pusher
	public static String PUSHER_KEY;
	public static String PUSHER_APP_KEY;

	public static void setDeveloperEnviroment() {
		PUSHERS_API_URL = DEV_PUSHERS_API_URL;
		EVENT_CATEGORIES_URL = PUSHERS_API_URL + EVENTS_CATEGORIES;
		ADD_EVENT_URL = PUSHERS_API_URL + EVENTS_ADD;
		PUSHER_KEY = DEV_PUSHER_KEY;
		PUSHER_APP_KEY = DEV_PUSHER_APP_KEY;
	}

	public static void setProductionEnviroment() {
		PUSHERS_API_URL = PRO_PUSHERS_API_URL;
		EVENT_CATEGORIES_URL = PUSHERS_API_URL + EVENTS_CATEGORIES;
		ADD_EVENT_URL = PUSHERS_API_URL + EVENTS_ADD;
		PUSHER_KEY = PRO_PUSHER_KEY;
		PUSHER_APP_KEY = PRO_PUSHER_APP_KEY;
	}
}
