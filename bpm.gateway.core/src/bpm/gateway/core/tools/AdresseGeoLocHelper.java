package bpm.gateway.core.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.sql.Types;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import bpm.gateway.core.StreamElement;
import bpm.vanilla.platform.core.beans.meta.Meta;
import bpm.vanilla.platform.core.beans.meta.Meta.TypeMeta;

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

	public static final APIBanField GEOLOC = new APIBanField("geoloc", "Coordinate", -1, "String", Coordinate.class.getName());
	
	public static final APIBanField ID = new APIBanField("id", "identifiant de l’adresse (clef d’interopérabilité)", java.sql.Types.VARCHAR, "String", String.class.getName());
	public static final APIBanField TYPE = new APIBanField("type", "type de résultat trouvé", java.sql.Types.VARCHAR, "String", String.class.getName());
//	public static final APIBanField HOUSENUMBER = new APIBanField("housenumber", "numéro « à la plaque »", java.sql.Types.VARCHAR, "String", String.class.getName());
//	public static final APIBanField STREET = new APIBanField("street", "position « à la voie », placé approximativement au centre de celle-ci", java.sql.Types.VARCHAR, "String", String.class.getName());
//	public static final APIBanField LOCALITY = new APIBanField("locality", "lieu-dit", java.sql.Types.VARCHAR, "String", String.class.getName());
//	public static final APIBanField MUNICIPALITY = new APIBanField("municipality", "numéro « à la commune »", java.sql.Types.VARCHAR, "String", String.class.getName());
	public static final APIBanField SCORE = new APIBanField("score", "valeur de 0 à 1 indiquant la pertinence du résultat", java.sql.Types.FLOAT, "Float", Double.class.getName());
	public static final APIBanField HOUSENUMBER = new APIBanField("housenumber", "numéro avec indice de répétition éventuel (bis, ter, A, B)", java.sql.Types.VARCHAR, "String", String.class.getName());
	public static final APIBanField STREET = new APIBanField("street", "nom de la voie", java.sql.Types.VARCHAR, "String", String.class.getName());
	public static final APIBanField NAME = new APIBanField("name", "numéro éventuel et nom de voie ou lieu dit", java.sql.Types.VARCHAR, "String", String.class.getName());
	public static final APIBanField POSTCODE = new APIBanField("postcode", "code postal", java.sql.Types.VARCHAR, "String", String.class.getName());
	public static final APIBanField CITYCODE = new APIBanField("citycode", "code INSEE de la commune", java.sql.Types.VARCHAR, "String", String.class.getName());
	public static final APIBanField CITY = new APIBanField("city", "nom de la commune", java.sql.Types.VARCHAR, "String", String.class.getName());
	public static final APIBanField DISTRICT = new APIBanField("district", "nom de l’arrondissement (Paris/Lyon/Marseille)", java.sql.Types.VARCHAR, "String", String.class.getName());
	public static final APIBanField OLDCITYCODE = new APIBanField("oldcitycode", "code INSEE de la commune ancienne (le cas échéant)", java.sql.Types.VARCHAR, "String", String.class.getName());
	public static final APIBanField OLDCITY = new APIBanField("oldcity", "nom de la commune ancienne (le cas échéant)", java.sql.Types.VARCHAR, "String", String.class.getName());
	public static final APIBanField CONTEXT = new APIBanField("context", "n° de département, nom de département et de région", java.sql.Types.VARCHAR, "String", String.class.getName());
	public static final APIBanField LABEL = new APIBanField("label", "libellé complet de l’adresse", java.sql.Types.VARCHAR, "String", String.class.getName());
	public static final APIBanField X = new APIBanField("x", "coordonnées géographique en projection légale", java.sql.Types.FLOAT, "Float", Double.class.getName());
	public static final APIBanField Y = new APIBanField("y", "coordonnées géographique en projection légale", java.sql.Types.FLOAT, "Float", Double.class.getName());
	public static final APIBanField IMPORTANCE = new APIBanField("importance", "indicateur d’importance (champ technique)", java.sql.Types.FLOAT, "Float", Double.class.getName());
	
	public static final APIBanField[] API_FIELDS = { ID, TYPE, SCORE, HOUSENUMBER, STREET, NAME, POSTCODE, CITYCODE, CITY, DISTRICT, OLDCITYCODE, OLDCITY, CONTEXT, LABEL, X, Y, IMPORTANCE };
	
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
	public static HashMap<String, Object> getGeoloc(boolean onlyOneColumn, String libelle, String codePostal, Double minimumScore) throws Exception {
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

	private static HashMap<String, Object> parseJson(String result, Double minimumScore) throws Exception {
		minimumScore = minimumScore != null ? minimumScore : DEFAULT_SCORE;
		
		HashMap<String, Object> values = new HashMap<String, Object>();
		
		JSONObject json = new JSONObject(result);
		if (!json.isNull("features")) {
			JSONArray features = json.getJSONArray("features");
			JSONObject item = features.length() > 0 ? features.getJSONObject(0) : null;
			if (item != null && !item.isNull("properties") && !item.isNull("geometry")) {
				JSONObject properties = item.getJSONObject("properties");
				if (!properties.isNull("score")) {
					double score = properties.getDouble("score");
					if (score > minimumScore) {
						for (APIBanField field : API_FIELDS) {
							if (!properties.isNull(field.getId())) {
								if (field.getType() == java.sql.Types.FLOAT) {
									values.put(field.getId(), properties.getDouble(field.getId()));
								}
								else {
									values.put(field.getId(), properties.getString(field.getId()));
								}
							}
						}
						
						JSONObject geometry = item.getJSONObject("geometry");
						JSONArray coordinates = geometry.getJSONArray("coordinates");
						if (coordinates.length() > 1) {
							double longitude = coordinates.getDouble(0);
							double latitude = coordinates.getDouble(1);
							
							values.put(GEOLOC.getId(), new Coordinate(latitude, longitude, score));
						}
					}
					else {
						throw new Exception(SCORE_EXCEPTION);
					}
				}
			}
		}
		
		return values;
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

	public static APIBanField getField(String item) {
		for (APIBanField field : API_FIELDS) {
			if (field.getId().equals(item)) {
				return field;
			}
		}
		return null;
	}
	
	public static StreamElement buildColumn(String transfoName, APIBanField field) {
		StreamElement element = new StreamElement();
		element.className = field.getClassName();
		element.name = field.getId();
		element.tableName = "apiban";
		element.transfoName = transfoName;
		element.type = field.getType();
		element.originTransfo = transfoName;
		element.isNullable = true;
		element.defaultValue = null;
		element.typeName = field.getTypeName();
		element.isPrimaryKey = false;
		return element;
	}
}
