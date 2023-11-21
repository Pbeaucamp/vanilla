package bpm.gwt.aklabox.commons.server.servlets;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import bpm.document.management.core.model.User;
import bpm.gwt.aklabox.commons.client.services.exceptions.ServiceException;
import bpm.gwt.aklabox.commons.server.security.CommonSessionHelper;
import bpm.gwt.aklabox.commons.server.security.CommonSessionManager;
import bpm.gwt.aklabox.commons.server.security.DocumentManagementSession;
import bpm.gwt.aklabox.commons.server.security.ExceptionHelper;
import bpm.master.core.model.InstanceIdentifier;
import bpm.master.core.model.OzwilloRequest;
import bpm.master.core.remote.HttpConnector;
import bpm.master.core.remote.RemoteMasterService;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;

public class SSOServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			
			System.out.println("SSO servlet -------------------");
			
			String url = ConfigurationManager.getProperty("bpm.sso.url");
			VanillaConfiguration config = ConfigurationManager.getInstance().getVanillaConfiguration();
			String urlRedirect = config.getVanillaServerUrl().replace("VanillaRuntime", "AklaBox/ssoServlet");

			InstanceIdentifier id = new InstanceIdentifier();
			id.setUrl(config.getVanillaServerUrl());
			id.setLogin(config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN));
			id.setPassword(config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD));
			id.setName(config.getVanillaServerUrl());
			RemoteMasterService remote = new RemoteMasterService(new HttpConnector(ConfigurationManager.getProperty("bpm.master.url")));

			OzwilloRequest request = remote.getOzwilloRequestByInstance(id);

			// access_token received
			if (req.getParameter("code") != null && !req.getParameter("code").isEmpty()) {
				System.out.println("access_token received ----------------");
				HttpClient client = HttpClientBuilder.create().build();

				HttpPost post = new HttpPost(url + "/a/token");
				
//				post.setHeader("Authorization", "basic " + Base64.getEncoder().encodeToString(((String)(request.getClient_id() + ":" + request.getClient_secret())).getBytes()));
				
				List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
				urlParameters.add(new BasicNameValuePair("grant_type", "authorization_code"));
				urlParameters.add(new BasicNameValuePair("redirect_uri", urlRedirect));
				urlParameters.add(new BasicNameValuePair("code", req.getParameter("code")));
//				urlParameters.add(new BasicNameValuePair("code_verifier", "abcdef"));

				post.setEntity(new UrlEncodedFormEntity(urlParameters));

				HttpResponse response = client.execute(post);

				String reqString = IOUtils.toString(response.getEntity().getContent(), "UTF-8");
				
				System.out.println(reqString);
				
				JSONObject obj = new JSONObject(reqString);
				String token = obj.getString("access_token");
				
				HttpClient clientGet = HttpClientBuilder.create().build();

				HttpGet get = new HttpGet(url + "/a/userinfo");
				get.setHeader("Authorization", "bearer " + token);
				HttpResponse responseUserInfo = clientGet.execute(get);
				
				String respUserInfo = IOUtils.toString(responseUserInfo.getEntity().getContent(), "UTF-8");
				
				System.out.println(respUserInfo);
				
				JSONObject objUser = new JSONObject(respUserInfo);
				String userEmail = objUser.getString("email");

				String login = config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_LOGIN);
				String password = config.getProperty(VanillaConfiguration.P_VANILLA_ROOT_PASSWORD);
				
				User u = new User();
				u.setEmail(login);
				u.setPassword(password);
				
				DocumentManagementSession session = createSession(u, req);
				User connected = session.getAklaboxService().connect(u);
				
				resp.sendRedirect(config.getProperty(VanillaConfiguration.P_AKLABOX_WEBAPP_URL) + "?sessionid=" + connected.getSessionId());
			}
			else if (req.getParameter("error") != null && !req.getParameter("error").isEmpty()) {
				System.out.println("error get access_token ----------------");
				System.out.println(req.getParameter("error"));
				System.out.println(req.getParameter("error_description"));
				System.out.println(req.getParameter("error_uri"));
			}
			// get an access_token
			else {
				System.out.println("get access_token ----------------");
				String encodedRed = URLEncoder.encode(urlRedirect, "UTF-8");

				String urlToCall = url + "/a/auth?" + "response_type=code" + "&client_id=" + request.getClient_id() + "" + "&scope=openid%20email" + "&claims=%7B%22userinfo%22%3A%7B%22nickname%22%3Anull%2C%22locale%22%3Anull%7D%7D" + "&redirect_uri=" + encodedRed + "&state=security_token%3D67%26url%3Dhttps%3A%2F%2Fbox.aklabox.com" + "&nonce=543298 HTTP/1.1";

				resp.sendRedirect(urlToCall);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	private DocumentManagementSession createSession(User user, HttpServletRequest req) throws ServiceException {
		try {
			String sessionId = CommonSessionManager.createSession(DocumentManagementSession.class);
			CommonSessionHelper.setCurrentSessionId(sessionId, req);
		} catch (Exception e) {
			e.printStackTrace();
			throw ExceptionHelper.getClientException(e, "Unable to create a session.", true);
		}

		DocumentManagementSession session = CommonSessionHelper.getCurrentSession(req, DocumentManagementSession.class);
		session.init(user);

		return session;
	}
}
