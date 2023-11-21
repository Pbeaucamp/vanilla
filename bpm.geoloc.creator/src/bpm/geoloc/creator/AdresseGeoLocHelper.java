package bpm.geoloc.creator;

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

import bpm.geoloc.creator.model.Coordinate;

/**
 * Méthode d'aide pour la gestion de la géolocalisation des adresses
 * 
 * On prend le premier résultat au dessus de $DEFAULT_SCORE
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
	 * Récupération de la géolocalisation d'une adresse
	 * @param onlyOneColumn 
	 * 
	 * @param logger
	 * @param adresse
	 * @return coordonnées
	 * @throws Exception 
	 */
	public static Coordinate getGeoloc(boolean onlyOneColumn, String numero, String rue, String libelle, String codePostal, String ville, Double minimumScore) throws Exception {
		if ((libelle == null || libelle.isEmpty()) && (ville == null || ville.isEmpty()) && (codePostal == null || codePostal.isEmpty())) {
			return null;
		}
		
		String adresse = buildAdresse(numero, rue, libelle, ville);
		
		StringBuffer buf = new StringBuffer();
		buf.append("?");
		buf.append(PARAM_QUERY);
		buf.append("=");
		buf.append(URLEncoder.encode(adresse, "UTF-8"));
		if (!onlyOneColumn && codePostal != null && !codePostal.isEmpty()) {
			buf.append("&");
			buf.append(PARAM_POST_CODE);
			buf.append("=");
			buf.append(URLEncoder.encode(cleanValue(codePostal), "UTF-8"));
		}

		String result = callUrl(GOUV_ADRESSE_API + buf.toString());
		return parseJson(result, minimumScore);
	}
	
	private static String cleanValue(String value) {
		return value.trim().replace(" ", "");
	}

	private static String buildAdresse(String numero, String rue, String libelle, String ville) {
		StringBuffer buf = new StringBuffer();
		if (numero != null && !numero.isEmpty()) {
			buf.append(numero + " ");
		}
		if (rue != null && !rue.isEmpty()) {
			buf.append(rue + " ");
		}
		if (libelle != null && !libelle.isEmpty()) {
			buf.append(libelle);
		}
		if (ville != null && !ville.isEmpty()) {
			if (!buf.toString().isEmpty()) {
				buf.append(", ");
			}
			buf.append(ville);
		}
		return buf.toString();
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
						throw new Exception(SCORE_EXCEPTION);
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
