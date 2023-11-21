package bpm.vanillahub.runtime;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class TestMail {

	public static void main(String[] args) {
		// String host = "imap.gmail.com";
		// String userName = "vanilla.hub.data@gmail.com";
		// String password = "qYX5rJLssrfb";
		//
		// String saveDirectory = "D:/DATA/Test/1109";
		//
		// try {
		// MailHelper receiver = new MailHelper();
		//// receiver.setSaveDirectory(saveDirectory);
		// receiver.getEmail(host, userName, password, saveDirectory);
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

//		String url = "https://api.emploi-store.fr/partenaire/infotravail/v1/datastore_search_sql?sql=SELECT * FROM \"6c74b2b7-8ec5-474a-8706-1069670c2035\" WHERE \"FAP_CODE\" LIKE 'B2Z40' AND  \"CATCHMENT_AREA_CODE\" LIKE '9113'";

		try {
			String url = "https://api.emploi-store.fr/partenaire/infotravail/v1/datastore_search_sql?sql=";
			url = url + URLEncoder.encode("SELECT * FROM \"6c74b2b7-8ec5-474a-8706-1069670c2035\" WHERE \"FAP_CODE\" LIKE 'B2Z40' AND  \"CATCHMENT_AREA_CODE\" LIKE '9113'", "UTF-8");
			
			HttpURLConnection sock = (HttpURLConnection) new URL(url).openConnection();

			sock.setRequestProperty("Authorization", "Bearer kbcb9ZU6feEtg954iYywj4XYRQk");
			sock.setRequestProperty("Content-Type", "application/json");
			sock.setRequestProperty("Accept", "application/json");
			sock.setDoInput(true);
			sock.setDoOutput(true);
			sock.setRequestMethod("GET");
			
			InputStream initialStream = sock.getInputStream();
			byte[] buffer = new byte[initialStream.available()];
			initialStream.read(buffer);

			File targetFile = new File("D:/DATA/Test/1005/sample.json");
			OutputStream outStream = new FileOutputStream(targetFile);
			outStream.write(buffer);
		} catch (Exception ex) {
			ex.printStackTrace();;
		}

	}

}
