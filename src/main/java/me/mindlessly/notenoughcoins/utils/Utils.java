package me.mindlessly.notenoughcoins.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class Utils {

	public static JsonElement getJson(String jsonUrl) {
		try {
			// Shamelessly pulling data from Moulberry's website
			URL url = new URL(jsonUrl);
			URLConnection conn = url.openConnection();
			return new JsonParser().parse(new InputStreamReader(conn.getInputStream()));
		} catch (Exception e) {
			Reference.logger.error(e.getMessage(), e);
			return null;
		}
	}
}
