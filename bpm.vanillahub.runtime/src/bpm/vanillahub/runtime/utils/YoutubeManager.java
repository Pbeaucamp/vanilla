package bpm.vanillahub.runtime.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import bpm.vanillahub.core.Constants;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Thumbnail;

public class YoutubeManager {

	private YouTube youtube;

	public YoutubeManager() throws Exception {
		this.youtube = getYoutube();
	}

	private YouTube getYoutube() throws Exception {
		YouTube youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, new HttpRequestInitializer() {
            public void initialize(HttpRequest request) throws IOException {
            }
        }).setApplicationName("youtube-vanilla-hub-manager").build();
		return youtube;
	}

	public ByteArrayInputStream doAction(String apiKey, int method, String topic, String query, long nbVideos) throws Exception {
		if (method == Constants.YOUTUBE_METHOD_SEARCH_KEYWORD) {
			return searchKeyword(apiKey, query, nbVideos);
		}
		else if (method == Constants.YOUTUBE_METHOD_SEARCH_TOPIC) {
			return searchTopic(apiKey, topic, query, nbVideos);
		}
		return null;
	}

	private ByteArrayInputStream searchKeyword(String apiKey, String query, long nbVideos) throws Exception {
		try {
			// Define the API request for retrieving search results.
			YouTube.Search.List search = youtube.search().list("id,snippet");

			search.setKey(apiKey);
			search.setQ(query);

			// Restrict the search results to only include videos. See:
			// https://developers.google.com/youtube/v3/docs/search/list#type
			search.setType("video");

			// To increase efficiency, only retrieve the fields that the
			// application uses.
			search.setFields("items(*)");
			search.setMaxResults(nbVideos);

			// Call the API and print results.
			SearchListResponse searchResponse = search.execute();
			List<SearchResult> searchResultList = searchResponse.getItems();
			return transformResult(searchResultList);
		} catch (IOException e) {
			throw new Exception(e.getCause() + " : " + e.getMessage());
		}
	}

	private ByteArrayInputStream searchTopic(String apiKey, String topic, String query, long nbVideos) throws Exception {
		try {
			if (topic == null || topic.isEmpty()) {
				throw new Exception("A topic is needed to do the search.");
			}
			
			YouTube.Search.List search = youtube.search().list("id,snippet");

			search.setKey(apiKey);
			search.setQ(query);
			search.setTopicId(topic);

			// Restrict the search results to only include videos. See:
			// https://developers.google.com/youtube/v3/docs/search/list#type
			search.setType("video");

			// To increase efficiency, only retrieve the fields that the
			// application uses.
			search.setFields("items(*)");
			search.setMaxResults(nbVideos);
			SearchListResponse searchResponse = search.execute();

			List<SearchResult> searchResultList = searchResponse.getItems();
			return transformResult(searchResultList);
		} catch (IOException e) {
			throw new Exception(e.getCause() + " : " + e.getMessage());
		}
	}

	private ByteArrayInputStream transformResult(List<SearchResult> searchResultList) {
		Iterator<SearchResult> it = searchResultList.iterator();

		StringBuilder builder = new StringBuilder("<youtube>\n");
		if (!it.hasNext()) {
			builder.append("No videos found");
        }
		else {
			while (it.hasNext()) {
	            SearchResult singleVideo = it.next();
	            ResourceId rId = singleVideo.getId();

	            // Confirm that the result represents a video. Otherwise, the
	            // item will not contain a video ID.
	            if (rId.getKind().equals("youtube#video")) {
	                Thumbnail thumbnail = singleVideo.getSnippet().getThumbnails().getDefault();

	        		builder.append("    <video>\n");
	        		builder.append("        <id>" + rId.getVideoId() + "</id>\n");
	        		builder.append("        <title>" + singleVideo.getSnippet().getTitle() + "</title>\n");
	        		builder.append("        <thumbnailUrl>" + thumbnail.getUrl() + "</thumbnailUrl>\n");
	        		if (singleVideo.getUnknownKeys() != null) {
	        			for (String key : singleVideo.getUnknownKeys().keySet()) {
	    	        		builder.append("        <" + key + ">" + singleVideo.getUnknownKeys().get(key).toString() + "</" + key + ">\n");
	        			}
	        		}
	        		builder.append("    </video>\n");
	            }
	        }
		}
		
		builder.append("</youtube>\n");
		return new ByteArrayInputStream(builder.toString().getBytes());
	}
}
