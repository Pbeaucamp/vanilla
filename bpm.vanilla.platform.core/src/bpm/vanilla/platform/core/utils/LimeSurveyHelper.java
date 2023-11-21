package bpm.vanilla.platform.core.utils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import bpm.vanilla.platform.core.beans.resources.LimeSurveyFile;

//https://api.limesurvey.org/classes/remotecontrol_handle.html

public class LimeSurveyHelper {
	
	//TODO: Method
	// invite_participants
	// list_surveys
	// mail_registered_participants
	// remind_participants

	private String limeSurveyUrl;
	private String login;
	private String password;

	public LimeSurveyHelper(String limeSurveyUrl, String login, String password) {
		this.limeSurveyUrl = limeSurveyUrl;
		this.login = login;
		this.password = password;
	}

	public static String parse(String jsonLine) throws Exception {
		JsonElement jelement = new JsonParser().parse(jsonLine);
		JsonObject jobject = jelement.getAsJsonObject();
		try {
			return jobject.get("result").getAsString();
		} catch(Exception e) {
			//We tried to get an error from the json
			JsonObject resultObject = jobject.get("result").getAsJsonObject();
			if (resultObject.get("status") != null) {
				throw new Exception(resultObject.get("status").getAsString());
			}
			
			throw e;
		}
	}

	private static JsonArray parseArray(String jsonLine) {
		JsonElement jelement = new JsonParser().parse(jsonLine);
		JsonObject jobject = jelement.getAsJsonObject();
		return jobject.get("result").getAsJsonArray();
	}

	private String getSession() throws Exception {
		try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {

			HttpPost post = new HttpPost(limeSurveyUrl + "/index.php/admin/remotecontrol");
			post.setHeader("Content-type", "application/json");
			post.setEntity(new StringEntity("{\"method\": \"get_session_key\", \"params\": [\"" + login + "\", \"" + password + "\" ], \"id\": 1}"));

			HttpResponse response = httpClient.execute(post);
			if (response.getStatusLine().getStatusCode() == 200) {
				HttpEntity entity = response.getEntity();
				return parse(EntityUtils.toString(entity));
			}

			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * Return survey response as a String
	 * 
	 * @param limeSurveyId
	 * @param format
	 *            (pdf, csv, xls, doc, json)
	 * @return file as String
	 * @throws Exception
	 */
	public byte[] getSurveyResponses(String limeSurveyId, String format, String[] fields) throws Exception {
		String sessionKey = getSession();

		try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
			HttpPost post = new HttpPost(limeSurveyUrl + "/index.php/admin/remotecontrol");
			post.setHeader("Content-type", "application/json");
			if (fields != null) {
				post.setEntity(new StringEntity("{\"method\": \"export_responses\", \"params\": [ \"" + sessionKey + "\", \"" + limeSurveyId + "\", \"" + format + "\", \"\", \"all\", \"code\", \"short\", \"\", \"\", [\"token\"]], \"id\": 1}"));
			}
			else {
				post.setEntity(new StringEntity("{\"method\": \"export_responses\", \"params\": [ \"" + sessionKey + "\", \"" + limeSurveyId + "\", \"" + format + "\", \"\", \"all\", \"code\", \"long\" ], \"id\": 1}"));
			}
			HttpResponse response = httpClient.execute(post);
			if (response.getStatusLine().getStatusCode() == 200) {
				HttpEntity entity = response.getEntity();
				String result = parse(EntityUtils.toString(entity));
				return Base64.getDecoder().decode(result);
//				String decodedString = new String(decodedBytes, Charset.forName("UTF-8"));
//				return UTF8ToAnsiUtils.removeUTF8BOM(decodedString);
			}

			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	/**
	 * Return survey data from vmap linked to LimeSurvey as a String
	 * 
	 * @param limeSurveyId
	 * @param format
	 *            (pdf, csv, xls, doc, json)
	 * @return file as String
	 * @throws Exception
	 */
	public String getVMapData(String limeSurveyId) throws Exception {
		String sessionKey = getSession();

		try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
			HttpPost post = new HttpPost(limeSurveyUrl + "/index.php/admin/remotecontrol");
			post.setHeader("Content-type", "application/json");
			post.setEntity(new StringEntity("{\"method\": \"list_questions\", \"params\": [ \"" + sessionKey + "\", \"" + limeSurveyId + "\" ], \"id\": 1}"));

			HttpResponse response = httpClient.execute(post);
			if (response.getStatusLine().getStatusCode() == 200) {
				HttpEntity entity = response.getEntity();
				JsonArray arrayQuestions = parseArray(EntityUtils.toString(entity));
				for (int i = 0; i < arrayQuestions.size(); i++) {
					JsonObject item = arrayQuestions.get(i).getAsJsonObject();
					String title = !item.get("title").isJsonNull() ? item.get("title").getAsString() : null;
					if (title != null && !title.isEmpty() && title.contains("VMAP")) {
						//The id is contains in the title, we remove VMAP and send back the result
						return title.replace("VMAP", "");
					}
				}
			}

			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * Returns a list of Base64 encoded files
	 * 
	 * @param limeSurveyId
	 * @return
	 * @throws Exception
	 */
	public List<LimeSurveyFile> getSurveyUploadFiles(String limeSurveyId) throws Exception {
		String sessionKey = getSession();

		try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
			HttpPost post = new HttpPost(limeSurveyUrl + "/index.php/admin/remotecontrol");
			post.setHeader("Content-type", "application/json");

			List<LimeSurveyFile> files = new ArrayList<LimeSurveyFile>();

			List<String> tokens = extractSurveyTokens(sessionKey, limeSurveyId);
			if (tokens != null) {
				for (String token : tokens) {
					if (token != null && !token.isEmpty()) {
						List<LimeSurveyFile> uploadFiles = getSurveyUploadFile(httpClient, post, sessionKey, limeSurveyId, token);
						files.addAll(uploadFiles);
					}
				}
			}

			return files;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

	}

	private List<LimeSurveyFile> getSurveyUploadFile(CloseableHttpClient httpClient, HttpPost post, String sessionKey, String limeSurveyId, String token) throws Exception {
		List<LimeSurveyFile> files = new ArrayList<LimeSurveyFile>();
		
		post.setEntity(new StringEntity("{\"method\": \"get_uploaded_files\", \"params\": [ \"" + sessionKey + "\", \"" + limeSurveyId + "\", \"" + token + "\" ], \"id\": 1}"));
		HttpResponse response = httpClient.execute(post);
		if (response.getStatusLine().getStatusCode() == 200) {
			HttpEntity entity = response.getEntity();
			String result = EntityUtils.toString(entity);
			
			JsonElement jelement = new JsonParser().parse(result);
			JsonObject jobject = jelement.getAsJsonObject();
			if (!jobject.get("result").isJsonNull() && !jobject.get("result").isJsonArray()) {
				JsonObject filesObject = jobject.get("result").getAsJsonObject();
				
				Set<Entry<String, JsonElement>> items = filesObject.entrySet();
				for (Entry<String, JsonElement> item : items) {
					JsonObject element = item.getValue().getAsJsonObject();
					String content = !element.get("content").isJsonNull() ? element.get("content").getAsString() : null;
					byte[] contentByte = Base64.getDecoder().decode(content);
					
					JsonObject meta = !element.get("meta").isJsonNull() ? element.get("meta").getAsJsonObject() : null;
					String title = !meta.get("title").isJsonNull() ? meta.get("title").getAsString() : null;
					String size = !meta.get("size").isJsonNull() ? meta.get("size").getAsString() : null;
					String name = !meta.get("name").isJsonNull() ? meta.get("name").getAsString() : null;
					String filename = !meta.get("filename").isJsonNull() ? meta.get("filename").getAsString() : null;
					String ext = !meta.get("ext").isJsonNull() ? meta.get("ext").getAsString() : null;
					files.add(new LimeSurveyFile(title, name, size, filename, ext, contentByte));
				}
			}
		}

		return files;
	}

	private List<String> extractSurveyTokens(String sessionKey, String limeSurveyId) throws Exception {
		String[] fields = new String[1];
		fields[0] = "token";

		String tokens = decodeResponse(getSurveyResponses(limeSurveyId, "json", fields));
		JsonElement jelement = new JsonParser().parse(tokens);
		JsonObject jobject = jelement.getAsJsonObject();
		JsonArray arrayTokens = jobject.get("responses").getAsJsonArray();

		List<String> tokensResult = new ArrayList<String>();
		for (int i = 0; i < arrayTokens.size(); i++) {
			JsonObject item = arrayTokens.get(i).getAsJsonObject();
			String token = !item.get("token").isJsonNull() ? item.get("token").getAsString() : null;
			if (token != null && !token.isEmpty() && !tokensResult.contains(token)) {
				tokensResult.add(token);
			}
		}
		return tokensResult;
	}
	
	private String decodeResponse(byte[] decodedBytes) {
		String decodedString = new String(decodedBytes, Charset.forName("UTF-8"));
		return UTF8ToAnsiUtils.removeUTF8BOM(decodedString);
	}
}
