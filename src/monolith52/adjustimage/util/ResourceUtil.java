package monolith52.adjustimage.util;

import java.util.ResourceBundle;

public class ResourceUtil {
	
	private static ResourceBundle bundle = null;
	
	public static ResourceBundle getBundle() {
		if (bundle != null) return bundle;
		ResourceBundle bundle = ResourceBundle.getBundle("monolith52.adjustimage.Message");
		return bundle;
	}
	
	public static String getString(String key) {
		return getBundle().getString(key);
	}
	
	public static float getFloat(String key) {
		return Float.parseFloat(getBundle().getString(key));
	}
	
	public static boolean getBoolean(String key) {
		return Boolean.parseBoolean(getBundle().getString(key));
	}
}
