package bpm.fa.api.olap.xmla;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import bpm.fa.api.connection.XMLAConnection;
import bpm.fa.api.olap.Dimension;
import bpm.fa.api.olap.Hierarchy;
import bpm.fa.api.olap.Level;
import bpm.fa.api.olap.Measure;
import bpm.fa.api.olap.MeasureGroup;
import bpm.fa.api.olap.OLAPMember;

public class XMLADiscover {

	/**
	 * 
	 * @param url
	 * @return
	 * @throws XMLAException
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	public static String[] discoverDataSources(URL url, String login, String password) throws XMLAException, SAXException, IOException, ParserConfigurationException {
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<SOAP-ENV:Envelope \n" +
        "xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" \n" +
        "SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n" +
        "	<SOAP-ENV:Body>\n" +
        "		<Discover xmlns=\"urn:schemas-microsoft-com:xml-analysis\" \n" +
        "		SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n" +
        "			<RequestType>DISCOVER_DATASOURCES</RequestType>\n" +
        "				<Restrictions>\n" +
        "					<RestrictionList>\n" +
        "					</RestrictionList>\n" +
        "				</Restrictions>\n" +
        "			<Properties>\n" +
        "			<PropertyList>\n" +
        "				<Content>Data</Content>\n" +
        "			</PropertyList>\n" +
        "			</Properties>\n" +
        "		</Discover>\n" +
        "	</SOAP-ENV:Body>\n" +
        "</SOAP-ENV:Envelope>";
		
		HttpURLConnection sock = connect(url, login, password);
		
		setDiscoverMimeHeaders(sock);
		
		write(sock, xml.getBytes("UTF-8"));
		
    	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    	
    	DocumentBuilder builder = factory.newDocumentBuilder();
		Document d = builder.parse(sock.getInputStream());
        
        String[] buf = new String[d.getElementsByTagName("DataSourceInfo").getLength()];
        
        for (int i=0; i < buf.length; i++) {
        	buf[i] = d.getElementsByTagName("DataSourceInfo").item(i).getTextContent();
        }
        
        return buf;
	}
	
	public static String[] discoverSchema(URL url, XMLAConnection con) throws XMLAException, SAXException, IOException, ParserConfigurationException {
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
		"<SOAP-ENV:Envelope\n" +
		"xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
		"SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n" +
		"	<SOAP-ENV:Body>\n" +
		"		<Discover xmlns=\"urn:schemas-microsoft-com:xml-analysis\"\n" +
		"		SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n" +
		"			<RequestType>DBSCHEMA_CATALOGS</RequestType>\n" +
		"				<Restrictions>\n" +
		"					<RestrictionList>\n" +
		"				</RestrictionList>\n" +
		"			</Restrictions>\n" +
		"			<Properties>\n" +
		"				<PropertyList>\n" +
		"					<DataSourceInfo>" + con.getDatasource() + "</DataSourceInfo>\n" +
		"					<Content>Data</Content>" +
		"				</PropertyList>" +
		"			</Properties>" +
		"		</Discover>" +
		"	</SOAP-ENV:Body>\n" +
		"</SOAP-ENV:Envelope>";
		
		HttpURLConnection sock = connect(url, con.getUser(), con.getPass());
		
		setDiscoverMimeHeaders(sock);
		
		write(sock, xml.getBytes("UTF-8"));
		
    	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    	
    	DocumentBuilder builder = factory.newDocumentBuilder();
		Document d = builder.parse(sock.getInputStream());
        
        String[] buf = new String[d.getElementsByTagName("CATALOG_NAME").getLength()];
        
        for (int i=0; i < buf.length; i++) {
        	buf[i] = d.getElementsByTagName("CATALOG_NAME").item(i).getTextContent();
        }
        
        return buf;
	}
	
	public static String[] discoverCubes(URL url, XMLAConnection con) throws XMLAException, SAXException, IOException, ParserConfigurationException {
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
		"<SOAP-ENV:Envelope\n" +
		"	xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
		"	SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n" +
		"	<SOAP-ENV:Body>\n" +
		"		<Discover xmlns=\"urn:schemas-microsoft-com:xml-analysis\"\n" +
		"		SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n" +
		"			<RequestType>MDSCHEMA_CUBES</RequestType>\n" +
		"			<Restrictions>\n" +
		"				<RestrictionList>\n" +
		"				</RestrictionList>\n" +
		"			</Restrictions>\n" +
		"			<Properties>\n" +
		"				<PropertyList>\n" +
		"					<DataSourceInfo>" + con.getDatasource() + "</DataSourceInfo>\n" +
		"					<Catalog>" + con.getSchema() + "</Catalog>\n" +
		"					<Format>Tabular</Format>\n" +
		"					<Content>SchemaData</Content>\n" +
		"				</PropertyList>\n" +
		"			</Properties>\n" +
		"		</Discover>\n" +
		"	</SOAP-ENV:Body>\n" +
		"</SOAP-ENV:Envelope>";
		
		HttpURLConnection sock = connect(url, con.getUser(), con.getPass());
		
		setDiscoverMimeHeaders(sock);
		
		write(sock, xml.getBytes("UTF-8"));
		
    	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    	
    	DocumentBuilder builder = factory.newDocumentBuilder();
		Document d = builder.parse(sock.getInputStream());
        
        String[] buf = new String[d.getElementsByTagName("CUBE_NAME").getLength()];
        
        for (int i=0; i < buf.length; i++) {
        	buf[i] = d.getElementsByTagName("CUBE_NAME").item(i).getTextContent();
        }
        
        return buf;
	}
	
	/**
	 * Explore Dimensions
	 * 
	 * automatically hides Measures
	 * 
	 * @param url
	 * @param con
	 * @return
	 * @throws XMLAException
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	public static ArrayList<Dimension> discoverDimensions(URL url, XMLAConnection con) throws XMLAException, SAXException, IOException, ParserConfigurationException {
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
		"<SOAP-ENV:Envelope\n" +
		"	xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
		"	SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n" +
		"		<SOAP-ENV:Body>\n" +
		"			<Discover xmlns=\"urn:schemas-microsoft-com:xml-analysis\"\n" +
		"				SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n" +
		"				<RequestType>MDSCHEMA_DIMENSIONS</RequestType>\n" +
		"				<Restrictions>\n" +
		"					<RestrictionList>\n" +
		"						<CATALOG_NAME>" + con.getSchema() + "</CATALOG_NAME>\n" +
		"						<CUBE_NAME>" + con.getCube() + "</CUBE_NAME>\n" +
		"					</RestrictionList>\n" +
		"				</Restrictions>\n" +
		"				<Properties>\n" +
		"					<PropertyList>\n" +
		"						<DataSourceInfo>" + con.getDatasource() + "</DataSourceInfo>\n" +
		"						<Catalog>" + con.getSchema() + "</Catalog>\n" +
		"						<Format>Tabular</Format>\n" +
		"						<Content>SchemaData</Content>\n" +
		"					</PropertyList>\n" +
		"				</Properties>\n" +
		"			</Discover>\n" +
		"		</SOAP-ENV:Body>\n" +
		"</SOAP-ENV:Envelope>";
		
		HttpURLConnection sock = connect(url, con.getUser(), con.getPass());
		
		setDiscoverMimeHeaders(sock);
		
		write(sock, xml.getBytes("UTF-8"));
		
    	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    	
    	String rep = IOUtils.toString(sock.getInputStream(), "UTF-8");
    	
    	
    	try{
		XMLWriter w = new XMLWriter(System.out, OutputFormat.createPrettyPrint());
		org.dom4j.Document doc = DocumentHelper.parseText(rep);
		w.write(doc.getRootElement());
		w.flush();
	} catch (Exception ex) {
		ex.printStackTrace();
	}
    	
    	DocumentBuilder builder = factory.newDocumentBuilder();
		Document d = builder.parse(IOUtils.toInputStream(rep, "UTF-8"));
        
        ArrayList<Dimension> list = new ArrayList<Dimension>();
        NodeList node = d.getElementsByTagName("row");
        
        for (int i=0; i < node.getLength(); i++) {
        	NodeList l = node.item(i).getChildNodes();
        	String name = "", uname = "", caption = "";
        	
        	for (int j=0; j < l.getLength(); j++) {
        		if (l.item(j).getNodeName().equalsIgnoreCase("DIMENSION_NAME")) {
        			name = l.item(j).getTextContent();
        		}
        		else if (l.item(j).getNodeName().equalsIgnoreCase("DIMENSION_UNIQUE_NAME")) {
        			uname = l.item(j).getTextContent();
        		}
        		else if (l.item(j).getNodeName().equalsIgnoreCase("DIMENSION_CAPTION")) {
        			caption = l.item(j).getTextContent();
        		}
        	}//this is a normal testjjs
        	if (!uname.equalsIgnoreCase("[Measures]")) {
        		list.add(new Dimension(name, uname, caption));
        	}
        	//buf[i] = d.getElementsByTagName("CATALOG_NAME").item(i).getTextContent();
        }
        
        return list;
	}
	
	public static ArrayList<Hierarchy> discoverHierarchy(URL url, XMLAConnection con, Dimension curr_dim) throws XMLAException, SAXException, IOException, ParserConfigurationException {
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
		"<SOAP-ENV:Envelope\n" +
		"	xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
		"	SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n" +
		"		<SOAP-ENV:Body>\n" +
		"			<Discover xmlns=\"urn:schemas-microsoft-com:xml-analysis\"\n" +
		"				SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n" +
		"				<RequestType>MDSCHEMA_HIERARCHIES</RequestType>\n" +
		"				<Restrictions>\n" +
		"					<RestrictionList>\n" +
		"						<CATALOG_NAME>" + con.getSchema() + "</CATALOG_NAME>\n" +
		"						<CUBE_NAME>" + con.getCube() + "</CUBE_NAME>\n" +
		"						<DIMENSION_UNIQUE_NAME>" + curr_dim.getUniqueName() + "</DIMENSION_UNIQUE_NAME>\n" +
		"					</RestrictionList>\n" +
		"				</Restrictions>\n" +
		"				<Properties>\n" +
		"					<PropertyList>\n" +
		"						<DataSourceInfo>" + con.getDatasource() + "</DataSourceInfo>\n" +
		"						<Catalog>" + con.getSchema() + "</Catalog>\n" +
		"						<Format>Tabular</Format>\n" +
		"						<Content>SchemaData</Content>\n" +
		"					</PropertyList>\n" +
		"				</Properties>\n" +
		"			</Discover>\n" +
		"		</SOAP-ENV:Body>\n" +
		"</SOAP-ENV:Envelope>";
		
		HttpURLConnection sock = connect(url, con.getUser(), con.getPass());
		
		setDiscoverMimeHeaders(sock);
		
		write(sock, xml.getBytes("UTF-8"));
		
    	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    	
    	DocumentBuilder builder = factory.newDocumentBuilder();
		Document d = builder.parse(sock.getInputStream());
        
		NodeList node = d.getElementsByTagName("row");
        ArrayList<Hierarchy> hieras = new ArrayList<Hierarchy>();
        
        for (int i=0; i < node.getLength(); i++) {
        	NodeList l = node.item(i).getChildNodes();
        	String name = "", uname = "", caption = "";
        	
        	for (int j=0; j < l.getLength(); j++) {
        		if (l.item(j).getNodeName().equalsIgnoreCase("HIERARCHY_NAME")) {
        			name = l.item(j).getTextContent();
        		}
        		else if (l.item(j).getNodeName().equalsIgnoreCase("HIERARCHY_UNIQUE_NAME")) {
        			uname = l.item(j).getTextContent();
        		}
        		else if (l.item(j).getNodeName().equalsIgnoreCase("HIERARCHY_CAPTION")) {
        			caption = l.item(j).getTextContent();
        		}
        		//XXX these are not used... error?
//        		else if (l.item(j).getNodeName().equalsIgnoreCase("HIERARCHY_ORDINAL")) {
//        			lvl = new Integer(l.item(j).getTextContent());
//        		}
//        		else if (l.item(j).getNodeName().equalsIgnoreCase("HIERARCHY_ORIGIN")) {
//        			root = new Integer(l.item(j).getTextContent());
//        		}
        	}
        	
        	hieras.add(new Hierarchy(name, uname, caption, null)); //XXX null, we don't know default member yet
        }
        
        return hieras;
	}
	
	public static ArrayList<Level> discoverLevel(URL url, XMLAConnection con, Dimension dim, Hierarchy h) throws XMLAException, SAXException, IOException, ParserConfigurationException {
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
		"<SOAP-ENV:Envelope\n" +
		"	xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
		"	SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n" +
		"		<SOAP-ENV:Body>\n" +
		"			<Discover xmlns=\"urn:schemas-microsoft-com:xml-analysis\"\n" +
		"				SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n" +
		"				<RequestType>MDSCHEMA_LEVELS</RequestType>\n" +
		"				<Restrictions>\n" +
		"					<RestrictionList>\n" +
		"						<CATALOG_NAME>" + con.getSchema() + "</CATALOG_NAME>\n" +
		"						<CUBE_NAME>" + con.getCube() + "</CUBE_NAME>\n" +
		"						<DIMENSION_UNIQUE_NAME>" + dim.getUniqueName() + "</DIMENSION_UNIQUE_NAME>\n" +
		"						<HIERARCHY_UNIQUE_NAME>" + h.getUniqueName() + "</HIERARCHY_UNIQUE_NAME>\n" +
		"					</RestrictionList>\n" +
		"				</Restrictions>\n" +
		"				<Properties>\n" +
		"					<PropertyList>\n" +
		"						<DataSourceInfo>" + con.getDatasource() + "</DataSourceInfo>\n" +
		"						<Catalog>" + con.getSchema() + "</Catalog>\n" +
		"						<Format>Tabular</Format>\n" +
		"						<Content>SchemaData</Content>\n" +
		"					</PropertyList>\n" +
		"				</Properties>\n" +
		"			</Discover>\n" +
		"		</SOAP-ENV:Body>\n" +
		"</SOAP-ENV:Envelope>";
		
		HttpURLConnection sock = connect(url, con.getUser(), con.getPass());
		
		setDiscoverMimeHeaders(sock);
		
		write(sock, xml.getBytes("UTF-8"));
		
    	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    	
//    	System.out.println(xml);
    	
    	
    	DocumentBuilder builder = factory.newDocumentBuilder();
    	String _answer = IOUtils.toString(sock.getInputStream());
    	
//    	try{
//			XMLWriter w = new XMLWriter(System.out, OutputFormat.createPrettyPrint());
//			org.dom4j.Document doc = DocumentHelper.parseText(_answer);
//			w.write(doc.getRootElement());
//			w.flush();
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
    	
		Document d = builder.parse(IOUtils.toInputStream(_answer));
		
        NodeList node = d.getElementsByTagName("row");
        ArrayList<Level> lvls = new ArrayList<Level>();
        
        for (int i=0; i < node.getLength(); i++) {
        	NodeList l = node.item(i).getChildNodes();
        	String name = "", uname = "", caption = "";
        	int lvlnb = 0;
        	
        	for (int j=0; j < l.getLength(); j++) {
        		if (l.item(j).getNodeName().equalsIgnoreCase("LEVEL_NAME")) {
        			name = l.item(j).getTextContent();
        		}
        		else if (l.item(j).getNodeName().equalsIgnoreCase("LEVEL_UNIQUE_NAME")) {
        			uname = l.item(j).getTextContent();
        		}
        		else if (l.item(j).getNodeName().equalsIgnoreCase("LEVEL_CAPTION")) {
        			caption = l.item(j).getTextContent();
        		}
        		else if (l.item(j).getNodeName().equalsIgnoreCase("LEVEL_NUMBER")) {
        			lvlnb = new Integer(l.item(j).getTextContent());
        		}
        	}
        	lvls.add(new Level(name, uname, caption, lvlnb));
        }
        
        return lvls;
	}
	
	public static ArrayList<OLAPMember> discoverMember(URL url, XMLAConnection con, Dimension dim, Hierarchy h, Level lvl) throws XMLAException, SAXException, IOException, ParserConfigurationException {
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
		"<SOAP-ENV:Envelope\n" +
		"	xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
		"	SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n" +
		"		<SOAP-ENV:Body>\n" +
		"			<Discover xmlns=\"urn:schemas-microsoft-com:xml-analysis\"\n" +
		"				SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n" +
		"				<RequestType>MDSCHEMA_MEMBERS</RequestType>\n" +
		"				<Restrictions>\n" +
		"					<RestrictionList>\n" +
		"						<CATALOG_NAME>" + con.getSchema() + "</CATALOG_NAME>\n" +
		"						<CUBE_NAME>" + con.getCube() + "</CUBE_NAME>\n" +
		"						<DIMENSION_UNIQUE_NAME>" + dim.getUniqueName() + "</DIMENSION_UNIQUE_NAME>\n" +
		"						<HIERARCHY_UNIQUE_NAME>" + h.getUniqueName() + "</HIERARCHY_UNIQUE_NAME>\n";
		if (!con.getProvider().equals(XMLAConnection.HyperionProvider)){
			xml += "						<LEVEL_UNIQUE_NAME>" + lvl.getUniqueName() + "</LEVEL_UNIQUE_NAME>\n";	
		}
		xml += "" +
		"					</RestrictionList>\n" +
		"				</Restrictions>\n" +
		"				<Properties>\n" +
		"					<PropertyList>\n" +
		"						<DataSourceInfo>" + con.getDatasource() + "</DataSourceInfo>\n" +
		"						<Catalog>" + con.getSchema() + "</Catalog>\n" +
		"						<Format>Tabular</Format>\n" +
		"						<Content>SchemaData</Content>\n" +
		"					</PropertyList>\n" +
		"				</Properties>\n" +
		"			</Discover>\n" +
		"		</SOAP-ENV:Body>\n" +
		"</SOAP-ENV:Envelope>";
		
		HttpURLConnection sock = connect(url, con.getUser(), con.getPass());
		
		setDiscoverMimeHeaders(sock);
//		System.out.println(xml);
		write(sock, xml.getBytes("UTF-8"));
		
    	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    	
    	DocumentBuilder builder = factory.newDocumentBuilder();
		
    	String _answer = IOUtils.toString(sock.getInputStream(), "UTF-8");
    	
//    	try{
//			XMLWriter w = new XMLWriter(System.out, OutputFormat.createPrettyPrint());
//			org.dom4j.Document doc = DocumentHelper.parseText(_answer);
//			w.write(doc.getRootElement());
//			w.flush();
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
    	
		Document d = builder.parse(IOUtils.toInputStream(_answer, "UTF-8"));
		
        NodeList node = d.getElementsByTagName("row");
        
        ArrayList<OLAPMember> mbs = new ArrayList<OLAPMember>();
        
        for (int i=0; i < node.getLength(); i++) {
        	NodeList l = node.item(i).getChildNodes();
        	String name = "", uname = "", caption = "", lvlname = "";
        	String parentUname = "";
        	String depth = "";
        	int ordinal = 0;
        	int lvlnb = 0;
        	
        	for (int j=0; j < l.getLength(); j++) {
        		if (l.item(j).getNodeName().equalsIgnoreCase("MEMBER_NAME")) {
        			name = l.item(j).getTextContent().replace("&", "&amp;");
        		}
        		else if (l.item(j).getNodeName().equalsIgnoreCase("MEMBER_UNIQUE_NAME")) {
        			uname = l.item(j).getTextContent().replace("&", "&amp;");
        		}
        		else if (l.item(j).getNodeName().equalsIgnoreCase("MEMBER_CAPTION")) {
        			caption = l.item(j).getTextContent().replace("&", "&amp;");
        		}
        		else if (l.item(j).getNodeName().equalsIgnoreCase("LEVEL_UNIQUE_NAME")) {
        			lvlname = l.item(j).getTextContent().replace("&", "&amp;");
        		}
        		else if (l.item(j).getNodeName().equalsIgnoreCase("PARENT_UNIQUE_NAME")) {
        			parentUname = l.item(j).getTextContent().replace("&", "&amp;");
        		}
        		else if (l.item(j).getNodeName().equalsIgnoreCase("MEMBER_ORDINAL")) {
        			ordinal = Integer.parseInt(l.item(j).getTextContent().replace("&", "&amp;"));
        		}
        		else if (l.item(j).getNodeName().equalsIgnoreCase("LEVEL_NUMBER")) {
        			depth = l.item(j).getTextContent().replace("&", "&amp;");
        		}
        	}
        	
        	// we get only the Firsts members
        	if ("".equals(parentUname)){
        		mbs.add(new XMLAMember(name, uname, caption, dim, h, lvl, depth, parentUname));
        	}
        	
        }
        
        if (con.getProvider().equals(XMLAConnection.HyperionProvider)){
        	for(OLAPMember m : mbs){
        		m.setSynchro(true);
    			addChild(m, mbs, h);
    		}
            
            OLAPMember b = mbs.get(0);
            mbs.remove(b);
            mbs.add(0, mbs.get(mbs.size() - 1));
            mbs.remove(mbs.size() - 1);
            mbs.add(b);

        }
                
//        for(OLAPMember m : mbs){
//        	System.out.println(m.getUniqueName());
//        }
        return mbs;
	}
	
	
	private static void addChild(OLAPMember mb, List<OLAPMember> list, Hierarchy h){
		for(OLAPMember m : list){
			
			if (!mb.getMembers().contains(m) && mb.getUniqueName().contains(((XMLAMember)m).getParentUName()) && mb.getLevelDepth() + 1 == m.getLevelDepth()){
//				((XMLAMember)m).setUname(h.getUniqueName() + "." + mb.getUniqueName() + "." + m.getUniqueName());
				mb.addMember(m);
				addChild(m, list, h);
			}
		}
	}
	
	
	public static void discoverMemberSons(URL url, XMLAConnection con, Dimension dim, Hierarchy h, Level lvl, OLAPMember mb) throws XMLAException, SAXException, IOException, ParserConfigurationException {
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
		"<SOAP-ENV:Envelope\n" +
		"	xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
		"	SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n" +
		"		<SOAP-ENV:Body>\n" +
		"			<Discover xmlns=\"urn:schemas-microsoft-com:xml-analysis\"\n" +
		"				SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n" +
		"				<RequestType>MDSCHEMA_MEMBERS</RequestType>\n" +
		"				<Restrictions>\n" +
		"					<RestrictionList>\n" +
		"						<CATALOG_NAME>" + con.getSchema() + "</CATALOG_NAME>\n" +
		"						<CUBE_NAME>" + con.getCube() + "</CUBE_NAME>\n" +
		"						<DIMENSION_UNIQUE_NAME>" + dim.getUniqueName() + "</DIMENSION_UNIQUE_NAME>\n" +
		"						<HIERARCHY_UNIQUE_NAME>" + h.getUniqueName() + "</HIERARCHY_UNIQUE_NAME>\n" +
		"						<LEVEL_UNIQUE_NAME>" + lvl.getUniqueName() + "</LEVEL_UNIQUE_NAME>\n" +
		"						<MEMBER_UNIQUE_NAME>" + mb.getUniqueName() + "</MEMBER_UNIQUE_NAME>\n" +
		"<PARENT_UNIQUE_NAME>"+ mb.getUniqueName() + "</PARENT_UNIQUE_NAME>\n" +
		"						<TREE_OP>1</TREE_OP>\n" +
		"					</RestrictionList>\n" +
		"				</Restrictions>\n" +
		"				<Properties>\n" +
		"					<PropertyList>\n" +
		"						<DataSourceInfo>" + con.getDatasource() + "</DataSourceInfo>\n" +
		"						<Catalog>" + con.getSchema() + "</Catalog>\n" +
		"						<Format>Tabular</Format>\n" +
		"						<Content>SchemaData</Content>\n" +
		"					</PropertyList>\n" +
		"				</Properties>\n" +
		"			</Discover>\n" +
		"		</SOAP-ENV:Body>\n" +
		"</SOAP-ENV:Envelope>";
		
		HttpURLConnection sock = connect(url, con.getUser(), con.getPass());
		
		setDiscoverMimeHeaders(sock);
		
		write(sock, xml.getBytes("UTF-8"));
		
    	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    	
    	DocumentBuilder builder = factory.newDocumentBuilder();
    	
    	String _answer = IOUtils.toString(sock.getInputStream());
    	
//    	try{
//			XMLWriter w = new XMLWriter(System.out, OutputFormat.createPrettyPrint());
//			org.dom4j.Document doc = DocumentHelper.parseText(_answer);
//			w.write(doc.getRootElement());
//			w.flush();
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
    	
		Document d = builder.parse(IOUtils.toInputStream(_answer));
		
        NodeList node = d.getElementsByTagName("row");
        
        for (int i=0; i < node.getLength(); i++) {
        	NodeList l = node.item(i).getChildNodes();
        	
        	String name = "", uname = "", caption = "", lvlname = "", parentuname="";
        	int lvlnb = 0;
        	
        	for (int j=0; j < l.getLength(); j++) {
        		if (l.item(j).getNodeName().equalsIgnoreCase("MEMBER_NAME")) {
        			name = l.item(j).getTextContent().replace("&", "&amp;");
        		}
        		else if (l.item(j).getNodeName().equalsIgnoreCase("MEMBER_UNIQUE_NAME")) {
        			uname = l.item(j).getTextContent().replace("&", "&amp;");
        		}
        		else if (l.item(j).getNodeName().equalsIgnoreCase("MEMBER_CAPTION")) {
        			caption = l.item(j).getTextContent().replace("&", "&amp;");
        		}
        		else if (l.item(j).getNodeName().equalsIgnoreCase("LEVEL_UNIQUE_NAME")) {
        			lvlname = l.item(j).getTextContent().replace("&", "&amp;");
        		}
        		else if (l.item(j).getNodeName().equalsIgnoreCase("PARENT_UNIQUE_NAME")) {
        			parentuname = l.item(j).getTextContent().replace("&", "&amp;");
        		}
        	}
        	
        	if (mb.getUniqueName().equals(parentuname)){
        		for(Level _l : h.getLevel()){
        			if (_l.getUniqueName().equals(lvlname)){
        				mb.addMember(new XMLAMember(name, uname, caption, dim, h, _l));
        				break;
        			}
        		}
        		
        	}
        	
        }
        
        
	}
	
	public static ArrayList<MeasureGroup> discoverMeasureGroup(URL url, XMLAConnection con) throws XMLAException, SAXException, IOException, ParserConfigurationException {
		if (con.getProvider().equals(XMLAConnection.HyperionProvider)){
			return discoverMeasureGroupHyperion(url, con);
		}
		
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
		"<SOAP-ENV:Envelope\n" +
		"	xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
		"	SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n" +
		"		<SOAP-ENV:Body>\n" +
		"			<Discover xmlns=\"urn:schemas-microsoft-com:xml-analysis\"\n" +
		"				SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n" +
		"				<RequestType>MDSCHEMA_MEASUREGROUPS</RequestType>\n" +
		"				<Restrictions>\n" +
		"					<RestrictionList>\n" +
		"						<CATALOG_NAME>" + con.getSchema() + "</CATALOG_NAME>\n" +
		"						<CUBE_NAME>" + con.getCube() + "</CUBE_NAME>\n" +
		"					</RestrictionList>\n" +
		"				</Restrictions>\n" +
		"				<Properties>\n" +
		"					<PropertyList>\n" +
		"						<DataSourceInfo>" + con.getDatasource() + "</DataSourceInfo>\n" +
		"						<Catalog>" + con.getSchema() + "</Catalog>\n" +
		"						<Format>Tabular</Format>\n" +
		"						<Content>SchemaData</Content>\n" +
		"					</PropertyList>\n" +
		"				</Properties>\n" +
		"			</Discover>\n" +
		"		</SOAP-ENV:Body>\n" +
		"</SOAP-ENV:Envelope>";
		
		HttpURLConnection sock = connect(url, con.getUser(), con.getPass());
		
		setDiscoverMimeHeaders(sock);
		
		write(sock, xml.getBytes("UTF-8"));
		
    	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    	
    	DocumentBuilder builder = factory.newDocumentBuilder();
		Document d = builder.parse(sock.getInputStream());
		
        ArrayList<MeasureGroup> list = new ArrayList<MeasureGroup>();
        NodeList node = d.getElementsByTagName("row");
        
        for (int i=0; i < node.getLength(); i++) {
        	NodeList l = node.item(i).getChildNodes();
        	String name = "", uname = "", caption = "";
        	String type = "";
        	
        	
        	for (int j=0; j < l.getLength(); j++) {
        		if (l.item(j).getNodeName().equalsIgnoreCase("MEASUREGROUP_NAME")) {
        			name = l.item(j).getTextContent();
        		}
        		else if (l.item(j).getNodeName().equalsIgnoreCase("MEASUREGROUP_CAPTION")) {
        			caption = l.item(j).getTextContent();
        		}
        	}

        	list.add(new MeasureGroup(name, caption));
        	
        }
        
        return list;
	}
	
	public static ArrayList<Measure> discoverMeasure(URL url, XMLAConnection con, MeasureGroup gr) throws XMLAException, SAXException, IOException, ParserConfigurationException {
		String test = "";
		
		if (!gr.getUniqueName().equalsIgnoreCase(""))
			test = "						<MEASUREGROUP_NAME>" + gr.getUniqueName() + "</MEASUREGROUP_NAME>\n" ;
		
		
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
		"<SOAP-ENV:Envelope\n" +
		"	xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
		"	SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n" +
		"		<SOAP-ENV:Body>\n" +
		"			<Discover xmlns=\"urn:schemas-microsoft-com:xml-analysis\"\n" +
		"				SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n" +
		"				<RequestType>MDSCHEMA_MEASURES</RequestType>\n" +
		"				<Restrictions>\n" +
		"					<RestrictionList>\n" +
		"						<CATALOG_NAME>" + con.getSchema() + "</CATALOG_NAME>\n" +
		"						<CUBE_NAME>" + con.getCube() + "</CUBE_NAME>\n" +
								test +
		"					</RestrictionList>\n" +
		"				</Restrictions>\n" +
		"				<Properties>\n" +
		"					<PropertyList>\n" +
		"						<DataSourceInfo>" + con.getDatasource() + "</DataSourceInfo>\n" +
		"						<Catalog>" + con.getSchema() + "</Catalog>\n" +
		"						<Format>Tabular</Format>\n" +
		"						<Content>SchemaData</Content>\n" +
		"					</PropertyList>\n" +
		"				</Properties>\n" +
		"			</Discover>\n" +
		"		</SOAP-ENV:Body>\n" +
		"</SOAP-ENV:Envelope>";
		
		HttpURLConnection sock = connect(url, con.getUser(), con.getPass());
		setDiscoverMimeHeaders(sock);
		
		write(sock, xml.getBytes("UTF-8"));
		
    	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    	
    	DocumentBuilder builder = factory.newDocumentBuilder();
    	
    	String res = IOUtils.toString(sock.getInputStream(), "UTF-8");
    	
		Document d = builder.parse(IOUtils.toInputStream(res));
		
		ArrayList<Measure> list = new ArrayList<Measure>();
        NodeList node = d.getElementsByTagName("row");
        
        for (int i=0; i < node.getLength(); i++) {
        	NodeList l = node.item(i).getChildNodes();
        	String name = "", uname = "", caption = "", type = "";
        	
        	for (int j=0; j < l.getLength(); j++) {
        		if (l.item(j).getNodeName().equalsIgnoreCase("MEASURE_NAME")) {
        			name = l.item(j).getTextContent();
        		}
        		else if (l.item(j).getNodeName().equalsIgnoreCase("MEASURE_UNIQUE_NAME")) {
        			uname = l.item(j).getTextContent();
        		}
        		else if (l.item(j).getNodeName().equalsIgnoreCase("MEASURE_CAPTION")) {
        			caption = l.item(j).getTextContent();
        		}
        	}
        	list.add(new Measure(name, uname, caption, ""));
        }
        
        return list;
	}
	
	private static void write(HttpURLConnection sock, byte[] buf) throws IOException {
		BufferedOutputStream os = null;
		
//		try {
	    	  os = new BufferedOutputStream(sock.getOutputStream());
	          os.write(buf);
	          os.flush();
//	    }
//		catch (IOException e) {
//			e.printStackTrace();
//			try {
//				if (os != null) {
//					os.close();
//				}
//				if (sock != null) {
//					sock.getOutputStream().close();
//				}
//			} catch (IOException ee) {
//				//nothing to do...
//			}
//	    }
	}
	
	private static HttpURLConnection connect(URL url, String login, String password) throws IOException {
		HttpURLConnection sock = (HttpURLConnection) url.openConnection();
    	
		
		if ((login != null)  && !(login.equals(""))){
    		String userPassword = login + ":" + password;
        	String encoding = Base64.encodeBase64String(userPassword.getBytes());
        	sock.setRequestProperty ("Authorization", "Basic " + encoding);
    	}
		
    	sock.setRequestMethod("POST");
    	sock.setDoInput(true);
    	sock.setDoOutput(true);
    	
    	return sock;
	}
	
	private static void setDiscoverMimeHeaders(HttpURLConnection sock) {
		sock.setRequestProperty("SOAPAction", "\"urn:schemas-microsoft-com:xml-analysis:Discover\"");
		sock.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
		sock.setRequestProperty("Accept", "application/soap+xml, application/dime, multipart/related, text/*");
	}
	
	
	private static ArrayList<MeasureGroup> discoverMeasureGroupHyperion(URL url, XMLAConnection con) throws XMLAException, SAXException, IOException, ParserConfigurationException {
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
		"<SOAP-ENV:Envelope\n" +
		"	xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
		"	SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n" +
		"		<SOAP-ENV:Body>\n" +
		"			<Discover xmlns=\"urn:schemas-microsoft-com:xml-analysis\"\n" +
		"				SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n" +
		"				<RequestType>MDSCHEMA_DIMENSIONS</RequestType>\n" +
		"				<Restrictions>\n" +
		"					<RestrictionList>\n" +
		"						<CATALOG_NAME>" + con.getSchema() + "</CATALOG_NAME>\n" +
		"						<CUBE_NAME>" + con.getCube() + "</CUBE_NAME>\n" +
		"						<DIMENSION_TYPE>2</DIMENSION_TYPE>\n" +
		"					</RestrictionList>\n" +
		"				</Restrictions>\n" +
		"				<Properties>\n" +
		"					<PropertyList>\n" +
		"						<DataSourceInfo>" + con.getDatasource() + "</DataSourceInfo>\n" +
		"						<Catalog>" + con.getSchema() + "</Catalog>\n" +
		"						<Format>Tabular</Format>\n" +
		"						<Content>SchemaData</Content>\n" +
		"					</PropertyList>\n" +
		"				</Properties>\n" +
		"			</Discover>\n" +
		"		</SOAP-ENV:Body>\n" +
		"</SOAP-ENV:Envelope>";
		
		HttpURLConnection sock = connect(url, con.getUser(), con.getPass());
		
		setDiscoverMimeHeaders(sock);
		
		write(sock, xml.getBytes("UTF8"));
		
    	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    	String res = IOUtils.toString(sock.getInputStream(), "UTF-8");
    	
    	DocumentBuilder builder = factory.newDocumentBuilder();
		Document d = builder.parse(IOUtils.toInputStream(res));
		
        ArrayList<MeasureGroup> list = new ArrayList<MeasureGroup>();
        NodeList node = d.getElementsByTagName("row");
        
        for (int i=0; i < node.getLength(); i++) {
        	NodeList l = node.item(i).getChildNodes();
        	String name = "", uname = "", caption = "";
        	String type = "";
        	
        	for (int j=0; j < l.getLength(); j++) {
        		if (l.item(j).getNodeName().equalsIgnoreCase("DIMENSION_UNIQUE_NAME")) {
        			name = l.item(j).getTextContent();
        		}
        		else if (l.item(j).getNodeName().equalsIgnoreCase("DIMENSION_CAPTION")) {
        			caption = l.item(j).getTextContent();
        		}
        		else if (l.item(j).getNodeName().equalsIgnoreCase("DIMENSION_TYPE")) {
        			type = l.item(j).getTextContent();
        		}
        	}
        	if (type.equals("2")){
        		list.add(new MeasureGroup(name, caption));
        	}
        	
        	
        }
        
        return list;
	}
}
