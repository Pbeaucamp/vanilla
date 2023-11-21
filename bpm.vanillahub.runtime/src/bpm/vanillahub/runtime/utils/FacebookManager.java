package bpm.vanillahub.runtime.utils;

import java.io.ByteArrayInputStream;

import bpm.vanillahub.core.beans.resources.SocialNetworkServer.FacebookMethod;
import facebook4j.Facebook;
import facebook4j.FacebookFactory;
import facebook4j.Place;
import facebook4j.ResponseList;
import facebook4j.auth.AccessToken;

public class FacebookManager {

	private Facebook facebook;
	
	public FacebookManager(String token) throws Exception {
		this.facebook = getSearchBearerToken(token);
	}
	
	private Facebook getSearchBearerToken(String token) throws Exception {
		Facebook facebook = new FacebookFactory().getInstance();
		facebook.setOAuthAppId("", "");
		facebook.setOAuthAccessToken(new AccessToken(token, null));
        return facebook;
	}

	public ByteArrayInputStream doAction(int method, String query) throws Exception {
		switch (FacebookMethod.valueOf(method)) {
		case SEARCH_PLACES:
			return searchPlaces(query);
		default:
			break;
		}
		return null;
	}
	
//	private ByteArrayInputStream search(String query) throws Exception {
//		ResponseList<JSONObject> result = facebook.callGetAPI(query);
//        return transformResult(result);
//	}
	
	private ByteArrayInputStream searchPlaces(String query) throws Exception {
		StringBuffer buf = new StringBuffer();
		
		ResponseList<Place> posts = facebook.searchPlaces(query);
		if (posts != null) {
			for (Place post : posts) {
				buf.append(post.toString());
				buf.append("\n");
			}
		}
		return new ByteArrayInputStream(buf.toString().getBytes());
	}

//	private ByteArrayInputStream transformResult(ResponseList<?> result) {
//		StringBuilder builder = new StringBuilder("<result>\n");
//		if (result.size() > 0) {
//			for (Iterator<?> it = result.iterator(); it.hasNext();){
//				if (it instanceof JSONObject) {
//					builder.append("    <object>\n");
//					builder.append(it.toString());
//					builder.append("    </object>\n");
//				}
//			}
//		}
//		else {
//			builder.append("No data found");
//		}
//		builder.append("</result>");
//		return new ByteArrayInputStream(builder.toString().getBytes());
//	}

//	private String buildPost(Post post) {
//		StringBuilder builder = new StringBuilder("    <post>\n");
//		builder.append("        <id>" + post.getId() + "</id>\n");
//		builder.append("        <name>" + post.getName() + "</name>\n");
//		builder.append("        <statusType>" + post.getStatusType() + "</statusType>\n");
//		builder.append("        <caption>" + post.getCaption() + "</caption>\n");
//		builder.append("        <createdTime>" + post.getCreatedTime().getTime() + "</createdTime>\n");
//		builder.append("        <link>" + post.getLink() + "</link>\n");
//		builder.append("        <description>" + post.getDescription() + "</description>\n");
//		builder.append("        <sharesCount>" + post.getSharesCount() + "</sharesCount>\n");
//		builder.append("        <likesCount>" + post.getLikes().getCount() + "</likesCount>\n");
//		builder.append("        <message>" + post.getMessage() + "</message>\n");
//		builder.append("    </post>\n");
//		return builder.toString();
//	}
//
//	private String buildUser(User user) {
//		StringBuilder builder = new StringBuilder("    <user>\n");
//		builder.append("        <id>" + user.getId() + "</id>\n");
//		builder.append("        <name>" + user.getName() + "</name>\n");
//		builder.append("        <userName>" + user.getUsername() + "</userName>\n");
//		builder.append("        <firstName>" + user.getFirstName() + "</firstName>\n");
//		builder.append("        <middleName>" + user.getMiddleName() + "</middleName>\n");
//		builder.append("        <lastName>" + user.getLastName() + "</lastName>\n");
//		builder.append("        <gender>" + user.getGender() + "</gender>\n");
//		builder.append("        <email>" + user.getEmail() + "</email>\n");
//		builder.append("        <birthday>" + user.getBirthday() + "</birthday>\n");
//		builder.append("    </user>\n");
//		return builder.toString();
//	}
//
//	private String buildEvent(Event event) {
//		StringBuilder builder = new StringBuilder("    <user>\n");
//		builder.append("        <id>" + event.getId() + "</id>\n");
//		builder.append("        <name>" + event.getName() + "</name>\n");
//		builder.append("        <description>" + event.getDescription() + "</description>\n");
//		builder.append("        <startTime>" + event.getStartTime().getTime() + "</startTime>\n");
//		builder.append("        <endTime>" + event.getEndTime() + "</endTime>\n");
//		builder.append("        <location>" + event.getLocation() + "</location>\n");
//		builder.append("    </user>\n");
//		return builder.toString();
//	}
//
//	private String buildGroup(Group group) {
//		StringBuilder builder = new StringBuilder("    <user>\n");
//		builder.append("        <id>" + group.getId() + "</id>\n");
//		builder.append("        <name>" + group.getName() + "</name>\n");
//		builder.append("        <description>" + group.getDescription() + "</description>\n");
//		builder.append("        <email>" + group.getEmail() + "</email>\n");
//		builder.append("        <bookmarkOrder>" + group.getBookmarkOrder() + "</bookmarkOrder>\n");
//		builder.append("        <version>" + group.getVersion() + "</version>\n");
//		builder.append("    </user>\n");
//		return builder.toString();
//	}
}
