package bpm.vanillahub.runtime.utils;

import java.io.ByteArrayInputStream;
import java.util.Iterator;
import java.util.Map;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.RateLimitStatus;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.OAuth2Token;
import twitter4j.conf.ConfigurationBuilder;
import bpm.vanillahub.core.Constants;

public class TwitterManager {
	
	private static TwitterManager instance;

	private Twitter twitter;
	
	public TwitterManager() throws Exception {
		this.twitter = getSearchBearerToken();
	}
	
	public static TwitterManager getInstance() throws Exception {
		if (instance == null) {
			instance = new TwitterManager();
		}
		return instance;
	}
	
	private Twitter getSearchBearerToken() throws Exception {
		ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.setApplicationOnlyAuthEnabled(true);
        builder.setOAuthConsumerKey(Constants.TWITTER_KEY);
        builder.setOAuthConsumerSecret(Constants.TWITTER_SECRET);
		
        Twitter twitter = new TwitterFactory(builder.build()).getInstance();
        OAuth2Token token = twitter.getOAuth2Token();
        if (!token.getTokenType().equals("bearer")) {
        	throw new Exception("A problem occured during twitter's app authentication. Please try again later.");
        }

        Map<String, RateLimitStatus> rateLimitStatus = twitter.getRateLimitStatus("search");
        RateLimitStatus searchTweetsRateLimit = rateLimitStatus.get("/search/tweets");
        if(searchTweetsRateLimit != null && searchTweetsRateLimit.getLimit() < 450) {
            throw new Exception("Limit of the 450 call has been reached. Please wait '" + searchTweetsRateLimit.getResetTimeInSeconds() + "' sec to try again.");
        }
        return twitter;
	}

	public ByteArrayInputStream doAction(int method, String query) throws Exception {
		if (method == Constants.TWITTER_METHOD_SEARCH) {
			return search(query);
		}
		else if (method == Constants.TWITTER_METHOD_TIMELINE) {
			return getTimeline(query);
		}
		return null;
	}
	
	private ByteArrayInputStream search(String query) throws TwitterException {
		Query searchQuery = new Query(query);
		
        QueryResult result = twitter.search(searchQuery);
        return transformResult(result);
	}
	
	private ByteArrayInputStream getTimeline(String query) throws Exception {
        try {
        	long userId = Long.parseLong(query);
            ResponseList<Status> result = twitter.getUserTimeline(userId);
            return transformResult(result);
        } catch(Exception e) {
        	ResponseList<Status> result = twitter.getUserTimeline(query);
            return transformResult(result);
        }
	}

	private ByteArrayInputStream transformResult(ResponseList<Status> result) {
		StringBuilder builder = new StringBuilder("<result>\n");
		if (result.size() > 0) {
			for (Iterator<Status> it = result.iterator(); it.hasNext(); ){
				builder.append(buildTweet(it.next()));
			}
		}
		else {
			builder.append("No tweet found");
		}
		builder.append("</result>");
		return new ByteArrayInputStream(builder.toString().getBytes());
	}

	private ByteArrayInputStream transformResult(QueryResult result) {
		StringBuilder builder = new StringBuilder("<result>\n");
		if (result.getCount() > 0) {
			for(Status status : result.getTweets()) {
				builder.append(buildTweet(status));
			}
		}
		else {
			builder.append("No tweet found");
		}
		builder.append("</result>");
		return new ByteArrayInputStream(builder.toString().getBytes());
	}

	private String buildTweet(Status status) {
		StringBuilder builder = new StringBuilder("    <tweet>\n");
		if(status.getUser() != null) {
			builder.append("        <userId>" + status.getUser().getId() + "</userId>\n");
			builder.append("        <userName>" + status.getUser().getName() + "</userName>\n");
		}
		builder.append("        <favoriteCount>" + status.getFavoriteCount() + "</favoriteCount>\n");
		builder.append("        <retweetCount>" + status.getRetweetCount() + "</retweetCount>\n");
		builder.append("        <text>" + status.getText() + "</text>\n");
		builder.append("        <createdAt>" + status.getCreatedAt().getTime() + "</createdAt>\n");
		builder.append("    </tweet>\n");
		return builder.toString();
	}
}
