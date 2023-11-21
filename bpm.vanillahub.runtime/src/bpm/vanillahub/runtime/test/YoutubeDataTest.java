package bpm.vanillahub.runtime.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Thumbnail;
import com.google.gson.Gson;

public class YoutubeDataTest {
	
	private static final String API_KEY = "AIzaSyB6eab9d8yjrsb1CARzVSR9tjT-F6cT8pM";

    private static final long NUMBER_OF_TOPICS_RETURNED = 5;
    private static final long NUMBER_OF_VIDEOS_RETURNED = 25;

    /**
     * Define a global instance of a Youtube object, which will be used
     * to make YouTube Data API requests.
     */
    private static YouTube youtube;

	public static void main(String[] args) {
//		searchKeyword();
		searchTopic();
//		searchGeoloc();
	}
    
    private static void searchKeyword() {

        try {
            // This object is used to make YouTube Data API requests. The last
            // argument is required, but since we don't need anything
            // initialized when the HttpRequest is initialized, we override
            // the interface and provide a no-op function.
            youtube = new YouTube.Builder(new NetHttpTransport(), new GsonFactory(), new HttpRequestInitializer() {
				@Override
				public void initialize(HttpRequest arg0) throws IOException { }
            }).setApplicationName("youtube-vanilla-hub-sample").build();

            // Prompt the user to enter a query term.
            String queryTerm = getInputQuery();

            // Define the API request for retrieving search results.
            YouTube.Search.List search = youtube.search().list("id,snippet");

            search.setKey(API_KEY);
            search.setQ(queryTerm);

            // Restrict the search results to only include videos. See:
            // https://developers.google.com/youtube/v3/docs/search/list#type
            search.setType("video");

            // To increase efficiency, only retrieve the fields that the
            // application uses.
            search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)");
            search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);

            // Call the API and print results.
            SearchListResponse searchResponse = search.execute();
            List<SearchResult> searchResultList = searchResponse.getItems();
            if (searchResultList != null) {
            	prettyPrintSearch(searchResultList.iterator(), queryTerm);
            }
        } catch (GoogleJsonResponseException e) {
            System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
                    + e.getDetails().getMessage());
        } catch (IOException e) {
            System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    /*
     * Prompt the user to enter a query term and return the user-specified term.
     */
    private static String getInputQuery() throws IOException {

        String inputQuery = "";

        System.out.print("Please enter a search term: ");
        BufferedReader bReader = new BufferedReader(new InputStreamReader(System.in));
        inputQuery = bReader.readLine();

        if (inputQuery.length() < 1) {
            // Use the string "YouTube Developers Live" as a default.
            inputQuery = "YouTube Developers Live";
        }
        return inputQuery;
    }

    /*
     * Prints out all results in the Iterator. For each result, print the
     * title, video ID, and thumbnail.
     *
     * @param iteratorSearchResults Iterator of SearchResults to print
     *
     * @param query Search query (String)
     */
    private static void prettyPrintSearch(Iterator<SearchResult> iteratorSearchResults, String query) {

        System.out.println("\n=============================================================");
        System.out.println(
                "   First " + NUMBER_OF_VIDEOS_RETURNED + " videos for search on \"" + query + "\".");
        System.out.println("=============================================================\n");

        if (!iteratorSearchResults.hasNext()) {
            System.out.println(" There aren't any results for your query.");
        }

        while (iteratorSearchResults.hasNext()) {

            SearchResult singleVideo = iteratorSearchResults.next();
            ResourceId rId = singleVideo.getId();

            // Confirm that the result represents a video. Otherwise, the
            // item will not contain a video ID.
            if (rId.getKind().equals("youtube#video")) {
                Thumbnail thumbnail = singleVideo.getSnippet().getThumbnails().getDefault();

                System.out.println(" Video Id" + rId.getVideoId());
                System.out.println(" Title: " + singleVideo.getSnippet().getTitle());
                System.out.println(" Thumbnail: " + thumbnail.getUrl());
                System.out.println("\n-------------------------------------------------------------\n");
            }
        }
    }

    /**
     * Execute a search request that starts by calling the Freebase API to
     * retrieve a topic ID matching a user-provided term. Then initialize a
     * YouTube object to search for YouTube videos and call the YouTube Data
     * API's youtube.search.list method to retrieve a list of videos associated
     * with the selected Freebase topic and with another search query term,
     * which the user also enters. Finally, display the titles, video IDs, and
     * thumbnail images for the first five videos in the YouTube Data API
     * response.
     *
     * @param args This application does not use command line arguments.
     */
    private static void searchTopic() {

        try {
            // Retrieve a Freebase topic ID based on a user-entered query term.
            String topicsId = getTopicId();
            if (topicsId.length() < 1) {
                System.out.println("No topic id will be applied to your search.");
            }

            // Prompt the user to enter a search query term. This term will be
            // used to retrieve YouTube search results related to the topic
            // selected above.
            String queryTerm = getInputQuery("search");

            // This object is used to make YouTube Data API requests. The last
            // argument is required, but since we don't need anything
            // initialized when the HttpRequest is initialized, we override
            // the interface and provide a no-op function.
            youtube = new YouTube.Builder(new NetHttpTransport(), new GsonFactory(), new HttpRequestInitializer() {
				@Override
				public void initialize(HttpRequest arg0) throws IOException { }
            })
            .setApplicationName("youtube-cmdline-search-sample")
            .build();

            YouTube.Search.List search = youtube.search().list("id,snippet");

            // Set your developer key from the Google Developers Console for
            // non-authenticated requests. See:
            // https://console.developers.google.com/
            String apiKey = API_KEY;
            search.setKey(apiKey);
            search.setQ(queryTerm);
            if (topicsId.length() > 0) {
                search.setTopicId(topicsId);
            }

            // Restrict the search results to only include videos. See:
            // https://developers.google.com/youtube/v3/docs/search/list#type
            search.setType("video");

            // To increase efficiency, only retrieve the fields that the
            // application uses.
            search.setFields("items(*)");
            search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);
            SearchListResponse searchResponse = search.execute();

            List<SearchResult> searchResultList = searchResponse.getItems();

            if (searchResultList != null) {
                prettyPrint(searchResultList.iterator(), queryTerm, topicsId);
            } else {
                System.out.println("There were no results for your query.");
            }
        } catch (GoogleJsonResponseException e) {
            System.err.println("There was a service error: " + e.getDetails().getCode() +
                    " : " + e.getDetails().getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /*
     * Prompt the user to enter a search term and return the user's input.
     *
     * @param searchCategory This value is displayed to the user to clarify the information that the application is requesting.
     */
    private static String getInputQuery(String searchCategory) throws IOException {

        String inputQuery = "";

        BufferedReader bReader = new BufferedReader(new InputStreamReader(System.in));

        do {
            System.out.print("Please enter a " + searchCategory + " term: ");
            inputQuery = bReader.readLine();
        } while (inputQuery.length() < 1);

        return inputQuery;
    }

    /**
     * Retrieve Freebase topics that match a user-provided query term. Then
     * prompt the user to select a topic and return its topic ID.
     */
    private static String getTopicId() throws IOException {

        // The application will return an empty string if no matching topic ID
        // is found or no results are available.
        String topicsId = "";

        // Prompt the user to enter a query term for finding Freebase topics.
        String topicQuery = getInputQuery("topics");

        // The Freebase Java library does not provide search functionality, so
        // the application needs to call directly against the URL. This code
        // constructs the proper URL, then uses jackson classes to convert the
        // JSON response into a Java object. You can learn more about the
        // Freebase search calls at: http://wiki.freebase.com/wiki/ApiSearch.
        HttpClient httpclient = new DefaultHttpClient();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("query", topicQuery));
        params.add(new BasicNameValuePair("limit", Long.toString(NUMBER_OF_TOPICS_RETURNED)));

        String serviceURL = "https://www.googleapis.com/freebase/v1/search";
        String url = serviceURL + "?" + URLEncodedUtils.format(params, "UTF-8");

        HttpResponse httpResponse = httpclient.execute(new HttpGet(url));
        HttpEntity entity = httpResponse.getEntity();

        if (entity != null) {
            InputStream instream = entity.getContent();
            try {
                // Convert the JSON to an object. This code does not do an
                // exact map from JSON to POJO (Plain Old Java object), but
                // you could create additional classes and use them with the
                // mapper.readValue() function to get that exact mapping.
                ObjectMapper mapper = new ObjectMapper();
                JsonNode rootNode = mapper.readValue(instream, JsonNode.class);

                // Confirm that the HTTP request was handled successfully by
                // checking the API response's HTTP response code.
                if (rootNode.get("status").asText().equals("200 OK")) {
                    // In the API response, the "result" field contains the
                    // list of needed results.
                    ArrayNode arrayNodeResults = (ArrayNode) rootNode.get("result");
                    // Prompt the user to select a topic from the list of API
                    // results.
                    topicsId = getUserChoice(arrayNodeResults);
                }
            } finally {
                instream.close();
            }
        }
        return topicsId;
    }

    /**
     * Output results from the Freebase topic search to the user, prompt the
     * user to select a topic, and return the appropriate topic ID.
     *
     * @param freebaseResults ArrayNode This object contains the search results.
     */
    private static String getUserChoice(ArrayNode freebaseResults) throws IOException {

        String freebaseId = "";

        if (freebaseResults.size() < 1) {
            return freebaseId;
        }

        // Print a list of topics retrieved from Freebase.
        for (int i = 0; i < freebaseResults.size(); i++) {
            JsonNode node = freebaseResults.get(i);
            System.out.print(" " + i + " = " + node.get("name").asText());
            if (node.get("notable") != null) {
                System.out.print(" (" + node.get("notable").get("name").asText() + ")");
            }
            System.out.println("");
        }

        BufferedReader bReader = new BufferedReader(new InputStreamReader(System.in));
        String inputChoice;

        // Prompt the user to select a topic.
        do {
            System.out.print("Choose the number of the Freebase Node: ");
            inputChoice = bReader.readLine();
        } while (!isValidIntegerSelection(inputChoice, freebaseResults.size()));

        // Return the topic ID needed for the API request that retrieves
        // YouTube search results.
        JsonNode node = freebaseResults.get(Integer.parseInt(inputChoice));
        freebaseId = node.get("mid").asText();
        return freebaseId;
    }

    /**
     * Confirm that the string represents a valid, positive integer, that is
     * less than or equal to 999,999,999. (Since the API will not return a
     * billion results for a query, any integer that the user enters will need
     * to be less than that to be valid, anyway.)
     *
     * @param input The string to test.
     * @param max   The integer must be less then this maximum number.
     */
    public static boolean isValidIntegerSelection(String input, int max) {
        if (input.length() > 9)
            return false;

        boolean validNumber = false;
        // Only accept positive numbers with a maximum of nine digits.
        Pattern intsOnly = Pattern.compile("^\\d{1,9}$");
        Matcher makeMatch = intsOnly.matcher(input);

        if (makeMatch.find()) {
            int number = Integer.parseInt(makeMatch.group());
            if ((number >= 0) && (number < max)) {
                validNumber = true;
            }
        }
        return validNumber;
    }

    /*
     * Prints out all results in the Iterator. For each result, print the
     * title, video ID, and thumbnail.
     *
     * @param iteratorSearchResults Iterator of SearchResults to print
     * @param query Search query (String)
     */
    private static void prettyPrint(Iterator<SearchResult> iteratorSearchResults, String query, String topicsId) {

        System.out.println("\n=============================================================");
        System.out.println("   First " + NUMBER_OF_VIDEOS_RETURNED + " videos for search on \"" + query + "\" with Topics id: " + topicsId + ".");
        System.out.println("=============================================================\n");

        if (!iteratorSearchResults.hasNext()) {
            System.out.println(" There aren't any results for your query.");
        }

        while (iteratorSearchResults.hasNext()) {

            SearchResult singleVideo = iteratorSearchResults.next();
            ResourceId rId = singleVideo.getId();

            // Confirm that the result represents a video. Otherwise, the
            // item will not contain a video ID.
            if (rId.getKind().equals("youtube#video")) {
                Thumbnail thumbnail = singleVideo.getSnippet().getThumbnails().getDefault();

                System.out.println(" Video Id" + rId.getVideoId());
                System.out.println(" Title: " + singleVideo.getSnippet().getTitle());
                System.out.println(" Thumbnail: " + thumbnail.getUrl());
                System.out.println("\n-------------------------------------------------------------\n");
            }
        }
    }
    
    /**
     * Initialize a YouTube object to search for videos on YouTube. Then
     * display the name and thumbnail image of each video in the result set.
     *
     * @param args command line args.
     */
    //NOT WORKING
//    private static void searchGeoloc() {
//
//        try {
//            // This object is used to make YouTube Data API requests. The last
//            // argument is required, but since we don't need anything
//            // initialized when the HttpRequest is initialized, we override
//            // the interface and provide a no-op function.
//            youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {
//                @Override
//                public void initialize(HttpRequest request) throws IOException {
//                }
//            }).setApplicationName("youtube-cmdline-geolocationsearch-sample").build();
//
//            // Prompt the user to enter a query term.
//            String queryTerm = getInputQuery();
//
//            // Prompt the user to enter location coordinates.
//            String location = getInputLocation();
//
//            // Prompt the user to enter a location radius.
//            String locationRadius = getInputLocationRadius();
//
//            // Define the API request for retrieving search results.
//            YouTube.Search.List search = youtube.search().list("id,snippet");
//
//            // Set your developer key from the Google Developers Console for
//            // non-authenticated requests. See:
//            // https://console.developers.google.com/
//            String apiKey = API_KEY;
//            search.setKey(apiKey);
//            search.setQ(queryTerm);
//            search.setLocation(location);
//            search.setLocationRadius(locationRadius);
//
//            // Restrict the search results to only include videos. See:
//            // https://developers.google.com/youtube/v3/docs/search/list#type
//            search.setType("video");
//
//            // As a best practice, only retrieve the fields that the
//            // application uses.
//            search.setFields("items(id/videoId)");
//            search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);
//
//            // Call the API and print results.
//            SearchListResponse searchResponse = search.execute();
//            List<SearchResult> searchResultList = searchResponse.getItems();
//            List<String> videoIds = new ArrayList<String>();
//
//            if (searchResultList != null) {
//
//                // Merge video IDs
//                for (SearchResult searchResult : searchResultList) {
//                    videoIds.add(searchResult.getId().getVideoId());
//                }
//                Joiner stringJoiner = Joiner.on(',');
//                String videoId = stringJoiner.join(videoIds);
//
//                // Call the YouTube Data API's youtube.videos.list method to
//                // retrieve the resources that represent the specified videos.
//                YouTube.Videos.List listVideosRequest = youtube.videos().list("snippet, recordingDetails").setId(videoId);
//                VideoListResponse listResponse = listVideosRequest.execute();
//
//                List<Video> videoList = listResponse.getItems();
//
//                if (videoList != null) {
//                    prettyPrintGeoloc(videoList.iterator(), queryTerm);
//                }
//            }
//        } catch (GoogleJsonResponseException e) {
//            System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
//                    + e.getDetails().getMessage());
//        } catch (IOException e) {
//            System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
//        } catch (Throwable t) {
//            t.printStackTrace();
//        }
//    }
//
//    /*
//     * Prompt the user to enter location coordinates and return the user-specified coordinates.
//     */
//    private static String getInputLocation() throws IOException {
//
//        String inputQuery = "";
//
//        System.out.print("Please enter location coordinates (example: 37.42307,-122.08427): ");
//        BufferedReader bReader = new BufferedReader(new InputStreamReader(System.in));
//        inputQuery = bReader.readLine();
//
//        if (inputQuery.length() < 1) {
//            // Use the string "37.42307,-122.08427" as a default.
//            inputQuery = "37.42307,-122.08427";
//        }
//        return inputQuery;
//    }
//
//    /*
//     * Prompt the user to enter a location radius and return the user-specified radius.
//     */
//    private static String getInputLocationRadius() throws IOException {
//
//        String inputQuery = "";
//
//        System.out.print("Please enter a location radius (examples: 5km, 8mi):");
//        BufferedReader bReader = new BufferedReader(new InputStreamReader(System.in));
//        inputQuery = bReader.readLine();
//
//        if (inputQuery.length() < 1) {
//            // Use the string "5km" as a default.
//            inputQuery = "5km";
//        }
//        return inputQuery;
//    }
//
//    /*
//     * Prints out all results in the Iterator. For each result, print the
//     * title, video ID, location, and thumbnail.
//     *
//     * @param iteratorVideoResults Iterator of Videos to print
//     *
//     * @param query Search query (String)
//     */
//    private static void prettyPrintGeoloc(Iterator<Video> iteratorVideoResults, String query) {
//
//        System.out.println("\n=============================================================");
//        System.out.println(
//                "   First " + NUMBER_OF_VIDEOS_RETURNED + " videos for search on \"" + query + "\".");
//        System.out.println("=============================================================\n");
//
//        if (!iteratorVideoResults.hasNext()) {
//            System.out.println(" There aren't any results for your query.");
//        }
//
//        while (iteratorVideoResults.hasNext()) {
//
//            Video singleVideo = iteratorVideoResults.next();
//
//            Thumbnail thumbnail = singleVideo.getSnippet().getThumbnails().getDefault();
//            GeoPoint location = singleVideo.getRecordingDetails().getLocation();
//
//            System.out.println(" Video Id" + singleVideo.getId());
//            System.out.println(" Title: " + singleVideo.getSnippet().getTitle());
//            System.out.println(" Location: " + location.getLatitude() + ", " + location.getLongitude());
//            System.out.println(" Thumbnail: " + thumbnail.getUrl());
//            System.out.println("\n-------------------------------------------------------------\n");
//        }
//    }
}
