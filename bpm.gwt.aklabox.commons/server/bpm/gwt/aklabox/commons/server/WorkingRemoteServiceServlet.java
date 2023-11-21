package bpm.gwt.aklabox.commons.server;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.gwt.user.server.rpc.SerializationPolicy;
import com.google.gwt.user.server.rpc.SerializationPolicyLoader;

public class WorkingRemoteServiceServlet extends RemoteServiceServlet {

	private static final long serialVersionUID = -4332306688541651819L;

	/**
	 * Attempt to load the RPC serialization policy normally. If it isn't found,
	 * try loading it using the context path instead of the URL.
	 */
	@Override
	protected SerializationPolicy doGetSerializationPolicy(HttpServletRequest request, String moduleBaseURL, String strongName) {
		SerializationPolicy policy = super.doGetSerializationPolicy(request, moduleBaseURL, strongName);
		if (policy == null) {
			return WorkingRemoteServiceServlet.loadSerializationPolicy(this, request, moduleBaseURL, strongName);
		}
		else {
			return policy;
		}
	}

	/**
	 * Load the RPC serialization policy via the context path.
	 */
	static SerializationPolicy loadSerializationPolicy(HttpServlet servlet, HttpServletRequest request, String moduleBaseURL, String strongName) {

		servlet.log("It's in my servlet thing");

		// The serialization policy path depends only by context path
		String contextPath = request.getContextPath();
		SerializationPolicy serializationPolicy = null;
		String contextRelativePath = "/DocumentManagement/";
		String serializationPolicyFilePath = SerializationPolicyLoader.getSerializationPolicyFileName(contextRelativePath + strongName);

		servlet.log("It's looking here : " + serializationPolicyFilePath);

		// Open the RPC resource file and read its contents.
		InputStream is = servlet.getServletContext().getResourceAsStream(serializationPolicyFilePath);
		try {
			if (is != null) {
				try {
					serializationPolicy = SerializationPolicyLoader.loadFromStream(is, null);
				} catch (ParseException e) {
					servlet.log("ERROR: Failed to parse the policy file '" + serializationPolicyFilePath + "'", e);
				} catch (IOException e) {
					servlet.log("ERROR: Could not read the policy file '" + serializationPolicyFilePath + "'", e);
				}
			}
			else {
				String message = "ERROR: The serialization policy file '" + serializationPolicyFilePath + "' was not found; did you forget to include it in this deployment?";
				servlet.log(message);
			}
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					// Ignore this error
				}
			}
		}

		return serializationPolicy;
	}

}
