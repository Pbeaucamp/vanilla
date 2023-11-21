package bpm.fa.api.olap.xmla;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.codec.binary.Base64;
import org.xml.sax.SAXException;

import bpm.fa.api.connection.XMLAConnection;
import bpm.fa.api.olap.xmla.parse.XMLAParseQuery;
import bpm.fa.api.olap.xmla.parse.XMLAResult;
import bpm.fa.api.utils.log.Log;

/**
 * 
 * @author ereynard
 * 
 */
public class XMLAExecute {
	
	public static XMLAResult executeMDX(URL url, XMLAConnection con, String mdx) throws XMLAException, SAXException, IOException, ParserConfigurationException {
		
		boolean drillThrought = mdx.startsWith("drillthrough"); 
		
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
		"<SOAP-ENV:Envelope\n" +
		"	xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
		"	SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n" +
		"		<SOAP-ENV:Body>\n" +
		"			<Execute xmlns=\"urn:schemas-microsoft-com:xml-analysis\"\n" +
		"				SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n" +
		"				<Command>\n" +
		"					<Statement>\n" +
		"						<![CDATA["+	mdx.replace("&amp;", "&") + "]]>\n"+
		"					</Statement>\n" +
		"				</Command>\n" +		
		"				<Properties>\n" +
		"					<PropertyList>\n" +
		"						<DataSourceInfo>" + con.getDatasource() + "</DataSourceInfo>\n" +
		"						<Catalog>" + con.getSchema() + "</Catalog>\n" ;
		if (drillThrought){
			xml += "						<Format>Tabular</Format>\n";
//			xml += "						<AxisFormat>TupleFormat</AxisFormat>";
		}
		else{
			xml += "						<Format>Multidimensional</Format>\n";
		}
		xml += 	"						<Content>SchemaData</Content>\n" +
		"					</PropertyList>\n" +
		"				</Properties>\n" +
		"			</Execute>\n" +
		"		</SOAP-ENV:Body>\n" +
		"</SOAP-ENV:Envelope>";
		
		HttpURLConnection sock = connect(url, con.getUser(), con.getPass());
		
		setExecuteMimeHeaders(sock);
		
		Log.debug("sending : \n" + xml);
		
		write(sock, xml.getBytes("UTF8"));
		
//		System.out.println(IOUtils.toString(sock.getInputStream()));
    	//XXX FIXME
    	XMLAParseQuery parser = new XMLAParseQuery(sock.getInputStream(), con.getProvider());
        
    	XMLAResult res = parser.getResult();
		
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
		if (login != null && password != null){
    		String userPassword = login + ":" + password;
        	String encoding = Base64.encodeBase64String(userPassword.getBytes());
        	sock.setRequestProperty ("Authorization", "Basic " + encoding);
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
		sock.setRequestProperty( "Authorization", "Basic  : "  );
	}
	
}
