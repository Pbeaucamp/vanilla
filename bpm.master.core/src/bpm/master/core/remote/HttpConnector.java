package bpm.master.core.remote;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.lang3.SerializationUtils;

import bpm.master.core.IMasterService;

public class HttpConnector {

	private String runtimeUrl;

	public HttpConnector(String runtimeUrl) {
		this.runtimeUrl = runtimeUrl;
	}
	
	public byte[] executeAction(RemoteAction action) throws Exception {
		if (action.getAction() instanceof IMasterService.ActionType) {
			return callServlet(IMasterService.SERVLET, action);
		}
		throw new Exception("Unsuported action");
	}
	
	private byte[] callServlet(String servlet, RemoteAction action) throws Exception {
		try {
			URL obj = new URL(runtimeUrl + servlet);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
	
			// add request header
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-type", "application/octet-stream");
	
			byte[] request = SerializationUtils.serialize(action);
	
			// Send post request
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeInt(request.length);
			wr.write(request);
			wr.flush();
			wr.close();
	
			DataInputStream in = new DataInputStream(con.getInputStream());
			if (in.available() > 0) {
				int length = in.readInt(); // read length of incoming message
				if (length > 0) {
					byte[] result = new byte[length];
					in.readFully(result, 0, result.length); // read the message
					con.disconnect();
					return result;
				}
			}

			con.disconnect();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
}
