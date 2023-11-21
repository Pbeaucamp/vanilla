package bpm.vanillahub.runtime.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import bpm.vanilla.platform.core.utils.CommunicatorHelper;
import bpm.vanillahub.core.Constants;
import bpm.vanillahub.core.utils.NutchJobState;

public class CrawlHelper {

	public static String extractReference(String url, String start, String end, String regex) throws IOException {
		String html = getHtml(url);
		int indexOfStart = html.indexOf(start) + start.length();

		if (indexOfStart - start.length() > 0) {
			int indexOfEndOfSpan = html.indexOf(end, indexOfStart);
			String price = html.substring(indexOfStart, indexOfEndOfSpan);
			return price;
		}

		return null;
	}

	public static List<String> getReference(String url, String reference, int charBefore, int charAfter) throws IOException {
		String html = getHtml(url);
		return extractReferenceParts(html, reference, charBefore, charAfter);
	}

	private static String getHtml(String url) throws IOException {
		URL websiteUrl = new URL(url);
		BufferedReader br = new BufferedReader(new InputStreamReader(websiteUrl.openStream()));

		StringBuffer buf = new StringBuffer();
		String strTemp = "";
		while (null != (strTemp = br.readLine())) {
			buf.append(strTemp);
		}
		return buf.toString();
	}

	private static List<String> extractReferenceParts(String html, String keywords, int startIndex, int endIndex) {
		List<String> references = new ArrayList<>();

		int index = html.indexOf(keywords);
		while (index >= 0) {
			int indexStart = 0;
			if (index - startIndex > 0) {
				indexStart = index - startIndex;
			}

			int indexEnd = html.length();
			if (index + endIndex < html.length()) {
				indexEnd = index + endIndex;
			}

			references.add(html.substring(indexStart, indexEnd));
			index = html.indexOf(keywords, index + keywords.length());
		}

		return references;
	}
	
	public static String launchNutchWorkflow(String nutchUrl, String url, int depth) throws Exception {
		boolean isUp = isNutchServerAvailable(nutchUrl);
		
		if (isUp) {
			String crawlId = "VanillaHub-crawl-" + new Object().hashCode();
			
			String folderUrl = seed(nutchUrl, url);
			String jobInjectId = inject(nutchUrl, folderUrl, crawlId);
			
			waitAction(nutchUrl, jobInjectId);
			
			for (int i=0; i<depth; i++) {
				long time = new Date().getTime();
				String batch = time + "-" + new Object().hashCode();
				
				String jobBatchId = generate(nutchUrl, crawlId, time, batch);
				waitAction(nutchUrl, jobBatchId);
				
				String jobFetchId = fecth(nutchUrl, crawlId, batch);
				waitAction(nutchUrl, jobFetchId);
				
				String jobParseId = parse(nutchUrl, crawlId, batch);
				waitAction(nutchUrl, jobParseId);
				
				String jobUpdateDBId = updateDB(nutchUrl, crawlId, batch);
				waitAction(nutchUrl, jobUpdateDBId);
				
				String jobIndexId = index(nutchUrl, crawlId, batch);
				waitAction(nutchUrl, jobIndexId);
			}
		}
		
		return "";
	}

	private static void waitAction(String nutchUrl, String crawlId) throws Exception {
		NutchJobState jobState = new NutchJobState(NutchJobState.STATE_RUNNING, NutchJobState.MESSAGE_OK);
		while (jobState.isRunning()) {
			Thread.sleep(5000);
			jobState = getJobStatus(nutchUrl, crawlId);
		}
		
		if (StringUtils.containsIgnoreCase(jobState.getMessage(), "ERROR")) {
			throw new Exception(jobState.getMessage());
		}
	}

	public static String seed(String nutchUrl, String url) throws Exception {
		JSONObject value = new JSONObject()
			.put("id", String.valueOf(new Object().hashCode()))
			.put("name", "nutch")
			.put("seedUrls", new JSONArray()
				.put(url));

		String json = value.toString();
		return CommunicatorHelper.sendPostMessage(nutchUrl, Constants.NUTCH_SEED, json);
	}

	private static String inject(String nutchUrl, String folderUrl, String crawlId) throws Exception {
		JSONObject value = new JSONObject()
			.put("crawlId", crawlId)
			.put("type", "INJECT")
			.put("args", new JSONObject()
				.put("url_dir", folderUrl));

		String json = value.toString();
		String result = CommunicatorHelper.sendPostMessage(nutchUrl, Constants.NUTCH_JOB, json);
		return parseNutchResult(result);
	}

	private static String generate(String nutchUrl, String crawlId, long time, String batch) throws Exception {
		JSONObject value = new JSONObject()
			.put("crawlId", crawlId)
			.put("type", "GENERATE")
			.put("args", new JSONObject()
				.put("normalize", false)
				.put("filter", true)
				.put("crawlId", crawlId)
				.put("curTime", time)
				.put("batch", batch));

		String json = value.toString();
		String result = CommunicatorHelper.sendPostMessage(nutchUrl, Constants.NUTCH_JOB, json);
		return parseNutchResult(result);
	}

	private static String fecth(String nutchUrl, String crawlId, String batch) throws Exception {
		long time = new Date().getTime();

		JSONObject value = new JSONObject()
			.put("crawlId", crawlId)
			.put("type", "FETCH")
			.put("args", new JSONObject()
				.put("threads", "50")
				.put("crawlId", crawlId)
				.put("curTime", time)
				.put("batch", batch));

		String json = value.toString();
		String result = CommunicatorHelper.sendPostMessage(nutchUrl, Constants.NUTCH_JOB, json);
		return parseNutchResult(result);
	}

	private static String parse(String nutchUrl, String crawlId, String batch) throws Exception {
		JSONObject value = new JSONObject()
			.put("crawlId", crawlId)
			.put("type", "PARSE")
			.put("args", new JSONObject()
				.put("crawlId", crawlId)
				.put("batch", batch));

		String json = value.toString();
		String result = CommunicatorHelper.sendPostMessage(nutchUrl, Constants.NUTCH_JOB, json);
		return parseNutchResult(result);
	}

	private static String updateDB(String nutchUrl, String crawlId, String batch) throws Exception {
		JSONObject value = new JSONObject()
			.put("crawlId", crawlId)
			.put("type", "UPDATEDB")
			.put("args", new JSONObject()
				.put("crawlId", crawlId)
				.put("batch", batch));
		
		String json = value.toString();
		String result = CommunicatorHelper.sendPostMessage(nutchUrl, Constants.NUTCH_JOB, json);
		return parseNutchResult(result);
	}

	private static String index(String nutchUrl, String crawlId, String batch) throws Exception {
		JSONObject value = new JSONObject()
			.put("crawlId", crawlId)
			.put("type", "INDEX")
			.put("args", new JSONObject()
				.put("crawlId", crawlId)
				.put("batch", batch));
		
		String json = value.toString();
		String result = CommunicatorHelper.sendPostMessage(nutchUrl, Constants.NUTCH_JOB, json);
		return parseNutchResult(result);
	}
	
	private static String parseNutchResult(String json) throws JSONException {
		JSONObject result = new JSONObject(json);
		return !result.isNull("id") ? result.getString("id") : null;
	}

	private static NutchJobState getJobStatus(String nutchUrl, String jobId) throws Exception {
		String jsonStatus = CommunicatorHelper.sendGetMessage(nutchUrl, Constants.NUTCH_JOB_STATUS + "/" + jobId);
		if(jsonStatus == null || jsonStatus.isEmpty()) {
			throw new Exception("Nutch is not available.");
		}

        JSONObject jsonObject = new JSONObject(jsonStatus);
        String state = !jsonObject.isNull(Constants.JOB_STATUS_STATE) ? jsonObject.getString(Constants.JOB_STATUS_STATE) : null;
		String message = !jsonObject.isNull(Constants.JOB_STATUS_MSG) ? jsonObject.getString(Constants.JOB_STATUS_MSG) : null;
		
		return new NutchJobState(state, message);
	}

	public static boolean isNutchServerAvailable(String nutchUrl) throws Exception {
		String jsonStatus = CommunicatorHelper.sendGetMessage(nutchUrl, Constants.NUTCH_SERVER_STATUS);
		return jsonStatus != null && !jsonStatus.isEmpty();
	}
}
