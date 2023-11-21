package bpm.vanillahub.runtime.utils;

import java.io.IOException;
import java.util.List;

import com.google.api.client.auth.oauth2.AuthorizationCodeTokenRequest;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

public class OAuth2Helper {
	
	public OAuth2Helper() {
	}
	
	public String requestAccessToken(String accessTokenUrl, String grantType, String clientId, String clientSecret, List<String> scopes) throws IOException {
		try {
			HttpRequestInitializer requestInitializer = new HttpRequestInitializer() {
				
				@Override
				public void initialize(HttpRequest request) throws IOException {
                    // Also set JSON accept header
                    request.getHeaders().setAccept("application/json");
				}
			};
            
			TokenResponse response = new AuthorizationCodeTokenRequest(new NetHttpTransport(), new GsonFactory(),
		            new GenericUrl(accessTokenUrl), "")
						.setGrantType(grantType)
			            .setClientAuthentication(new ClientParametersAuthentication(clientId, clientSecret))
			            .setScopes(scopes)
			            .setRequestInitializer(requestInitializer)
			            .execute();
			return response.getAccessToken();
		} catch (TokenResponseException e) {
			throw e;
//			if (e.getDetails() != null) {
//				System.err.println("Error: " + e.getDetails().getError());
//				if (e.getDetails().getErrorDescription() != null) {
//					System.err.println(e.getDetails().getErrorDescription());
//				}
//				if (e.getDetails().getErrorUri() != null) {
//					System.err.println(e.getDetails().getErrorUri());
//				}
//			}
//			else {
//				System.err.println(e.getMessage());
//			}
		}
	}
}
