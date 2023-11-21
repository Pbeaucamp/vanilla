package bpm.architect.web.server.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Methode d'aide pour la gestion de la geolocalisation des adresses
 * 
 * On prend le premier resultat au dessus de $DEFAULT_SCORE
 */
public class AdresseGeoLocHelper {

	public static final String SCORE_EXCEPTION = "Score is lower than the minimum score.";
	private static final double DEFAULT_SCORE = 0.7;

	private static final String GOUV_ADRESSE_API = "https://api-adresse.data.gouv.fr/search/";
	private static final String PARAM_QUERY = "q";
	private static final String PARAM_POST_CODE = "postcode";
	// Not use because we don't have this code
	// private static final String PARAM_CITY_CODE = "citycode";
	
	private static AdresseGeoLocHelper instance;
	
	public static AdresseGeoLocHelper getInstance() {
		if (instance == null) {
			instance = new AdresseGeoLocHelper();
		}
		return instance;
	}

	/**
	 * Recuperation de la geolocalisation d'une adresse
	 * @param onlyOneColumn 
	 * 
	 * @param logger
	 * @param adresse
	 * @return coordonnees
	 * @throws Exception 
	 */
	public static Coordinate getGeoloc(boolean onlyOneColumn, String libelle, String codePostal, Double minimumScore) throws Exception {
		if (libelle == null || libelle.isEmpty() || codePostal == null || codePostal.isEmpty()) {
			return null;
		}
		
		StringBuffer buf = new StringBuffer();
		buf.append("?");
		buf.append(PARAM_QUERY);
		buf.append("=");
		buf.append(URLEncoder.encode(libelle, "UTF-8"));
		if (!onlyOneColumn) {
			buf.append("&");
			buf.append(PARAM_POST_CODE);
			buf.append("=");
			buf.append(URLEncoder.encode(codePostal, "UTF-8"));
		}

		String result = callUrl(GOUV_ADRESSE_API + buf.toString());
		return parseJson(result, minimumScore);
	}

	private static Coordinate parseJson(String result, Double minimumScore) throws Exception {
		minimumScore = minimumScore != null ? (minimumScore > 1 ? minimumScore / 100 : minimumScore) : DEFAULT_SCORE;
		
		JSONObject json = new JSONObject(result);
		if (!json.isNull("features")) {
			JSONArray features = json.getJSONArray("features");
			JSONObject item = features.length() > 0 ? features.getJSONObject(0) : null;
			if (item != null && !item.isNull("properties") && !item.isNull("geometry")) {
				JSONObject properties = item.getJSONObject("properties");
				if (!properties.isNull("score")) {
					double score = properties.getDouble("score");
					if (score > minimumScore) {
						JSONObject geometry = item.getJSONObject("geometry");
						JSONArray coordinates = geometry.getJSONArray("coordinates");
						if (coordinates.length() > 1) {
							double longitude = coordinates.getDouble(0);
							double latitude = coordinates.getDouble(1);
							
							return new Coordinate(latitude, longitude);
						}
					}
					else {
						JSONObject geometry = item.getJSONObject("geometry");
						JSONArray coordinates = geometry.getJSONArray("coordinates");
						if (coordinates.length() > 1) {
							double longitude = coordinates.getDouble(0);
							double latitude = coordinates.getDouble(1);
							
							return new Coordinate(latitude, longitude);
						}
						System.out.println("score : " + score);
					}
				}
			}
		}
		return null;
	}

	private static String callUrl(String requestUrl) throws Exception {
		try (InputStream is = new URL(requestUrl).openStream()) {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			return readAll(rd);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}
}
