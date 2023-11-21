package bpm.fa.api.olap.xmla;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import bpm.fa.api.connection.XMLAConnection;
import bpm.fa.api.item.Item;
import bpm.fa.api.item.ItemElement;
import bpm.fa.api.item.ItemValue;
import bpm.fa.api.olap.OLAPResult;
import bpm.fa.api.utils.log.Log;

public class XMLADrillThrough {

	public static OLAPResult executeMDX(URL url, XMLAConnection con, String mdx) throws XMLAException, SAXException, IOException, ParserConfigurationException {

		boolean drillThrought = mdx.startsWith("drillthrough");

		mdx.substring("drillthrough ".length());

		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<SOAP-ENV:Envelope\n" + "	xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" + "	SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n" + "		<SOAP-ENV:Body>\n" + "			<Execute xmlns=\"urn:schemas-microsoft-com:xml-analysis\"\n" + "				SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n" + "				<Command>\n" + "					<Statement>\n" + mdx + "					</Statement>\n" + "				</Command>\n" + "				<Properties>\n" + "					<PropertyList>\n" + "						<DataSourceInfo>" + con.getDatasource() + "</DataSourceInfo>\n" + "						<Catalog>" + con.getSchema() + "</Catalog>\n" + "						<Format>Tabular</Format>\n" + "						<Content>SchemaData</Content>\n" + "					</PropertyList>\n" + "				</Properties>\n" + "			</Execute>\n" + "		</SOAP-ENV:Body>\n" + "</SOAP-ENV:Envelope>";

		HttpURLConnection sock = connect(url, con.getUser(), con.getPass());

		setExecuteMimeHeaders(sock);

		Log.info("sending : \n" + xml);

		write(sock, xml.getBytes("UTF8"));

		OLAPResult res = parseDrillThrought(sock.getInputStream());
		sock.disconnect();

		return res;
	}

	private static void write(HttpURLConnection sock, byte[] buf) throws IOException {
		BufferedOutputStream os = null;

		os = new BufferedOutputStream(sock.getOutputStream());
		os.write(buf);
		os.flush();
	}

	private static HttpURLConnection connect(URL url, String login, String password) throws IOException {
		HttpURLConnection sock = (HttpURLConnection) url.openConnection();
		if(login != null && password != null) {
			String userPassword = login + ":" + password;
			String encoding = Base64.encodeBase64String(userPassword.getBytes());
			sock.setRequestProperty("Authorization", "Basic " + encoding);
		}
		sock.setRequestMethod("POST");
		sock.setDoInput(true);
		sock.setDoOutput(true);

		return sock;
	}

	private static void setExecuteMimeHeaders(HttpURLConnection sock) {
		sock.setRequestProperty("SOAPAction", "\"urn:schemas-microsoft-com:xml-analysis:Execute\"");
		sock.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
		sock.setRequestProperty("Accept", "application/soap+xml, application/dime, multipart/related, text/*");
	}

	private static OLAPResult parseDrillThrought(InputStream is) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		String s = IOUtils.toString(is, "UTF-8");;

		Document d = builder.parse(IOUtils.toInputStream(s));
		NodeList node = d.getElementsByTagName("row");

		ArrayList<ArrayList<Item>> table = new ArrayList<ArrayList<Item>>();
		for(int i = 1; i < node.getLength(); i++) {
			NodeList l = node.item(i).getChildNodes();

			ArrayList<Item> row = new ArrayList<Item>();
			ArrayList<Item> row0 = new ArrayList<Item>();
			for(int j = 0; j < l.getLength(); j++) {
				if(!l.item(j).getNodeName().equalsIgnoreCase("#text")) {
					if(i == 1) {
						row0.add(new ItemElement(l.item(j).getNodeName(), true, false));
					}
					row.add(new ItemValue(l.item(j).getTextContent(), null));

				}
			}
			if(i == 0) {
				table.add(row0);
			}
			table.add(row);
		}
		OLAPResult res = new OLAPResult(table, table.get(0).size(), table.size());

		return res;
	}
}
