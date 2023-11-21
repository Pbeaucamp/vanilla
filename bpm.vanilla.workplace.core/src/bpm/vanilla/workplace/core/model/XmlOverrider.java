package bpm.vanilla.workplace.core.model;

import java.util.List;
import java.util.Properties;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.vanilla.platform.core.IRepositoryApi;

public class XmlOverrider {

	public static Document override(String xml, ImportItem item) throws DocumentException{
		Document doc = DocumentHelper.parseText(xml);
		Element root = doc.getRootElement();

		switch(item.getType()){
		case IRepositoryApi.FASD_TYPE:
			for(Element e : (List<Element>)root.elements("xmla")){
				generateFasd(e, item);
			}
			for(Element e : (List<Element>)root.element("datasources").elements("datasource-item")){
				generateFasd(e, item);
			}
			break;
		case IRepositoryApi.FD_DICO_TYPE:
			for(Element e : (List<Element>)root.elements("dataSource")){
				generateFdDico(e, item);
			}
			break;
		case IRepositoryApi.FMDT_TYPE:

			for(Element o : (List<Element>)root.elements("sqlDataSource")){
				for(Element e : (List<Element>)o.elements("sqlConnection")){
					generateFmdt(e, item);
				}
			}
			break;
		case IRepositoryApi.GTW_TYPE:
			Element rr = root.element("servers");
			//vanillaServer
			for(Object vs : rr.elements("vanillaServer")){
				for(Object vc : ((Element)vs).elements("vanillaConnection")){
					Element e = (Element)vc;
					generateGateway(e, item);
					
				}
			}
			//repositoryServer
			for(Object rs : rr.elements("repositoryServer")){
				for(Object rc : ((Element)rs).elements("repositoryConnection")){
					Element e = (Element)rc;
					generateGateway(e, item);
				}
			}
			//ldapServer
			for(Object ls : rr.elements("ldapServer")){
				for(Object lc : ((Element)ls).elements("ldapConnection")){
					Element e = (Element)lc;
					generateGateway(e, item);
				}
			}
			//databaseServer
			for(Object ds : rr.elements("dataBaseServer")){
				for(Object dc : ((Element)ds).elements("dataBaseConnection")){
					Element e = (Element)dc;
					generateGateway(e, item);
					

				}
			}
			//freemetricsServer
			for(Object ds : rr.elements("freemetricsServer")){
				for(Object dc : ((Element)ds).elements("dataBaseConnection")){
					Element e = (Element)dc;
					generateGateway(e, item);
					
				}
			}
			break;
		case IRepositoryApi.FWR_TYPE:
			break;
		case IRepositoryApi.CUST_TYPE:
			
			if (root.getName().equals("report")){
				for(Element e : (List<Element>)root.element("data-sources").elements("oda-data-source")){
					generateBirt(e, item);
				}
			}
			break;
		}
		
		return doc;
		
	}
	
	private static void generateBirt(Element e, ImportItem item){

		if (!e.attribute("extensionID").getText().equals("bpm.metadata.birt.oda.runtime")){
			return;
		}
		for(Object _o : e.elements("property")){
			Element _p = (Element)_o;
			if ("URL".equals(_p.attribute("name").getText())){
				_p.setText(getReplacedString(_p.getStringValue(), item));
			}
		}
			

	}
	private static  void generateFmdt(Element e, ImportItem item){
		e.element("host").setText(getReplacedString(e.element("host").getStringValue(), item));
		e.element("username").setText(getReplacedString(e.element("username").getStringValue(), item));
		e.element("password").setText(getReplacedString(e.element("password").getStringValue(), item));
		e.element("portNumber").setText(getReplacedString(e.element("portNumber").getStringValue(), item));
		e.element("dataBaseName").setText(getReplacedString(e.element("dataBaseName").getStringValue(), item));
		if (e.element("schemaName") != null){
			e.element("schemaName").setText(getReplacedString(e.element("schemaName").getStringValue(), item));
		}
		e.element("driverName").setText(getReplacedString(e.element("driverName").getStringValue(), item));

		

	}
	
	private static  void generateCust(Element e, Properties p){
		if (e.getName().equals("property")){
			e.setText(p.getProperty("repositoryUrl"));
		}
	}
	
	private static  String getReplacedString(String value, ImportItem item){
		String s = new String(value);
		for(Replacement r : item.getAutoReplacements()){
			s = s.replace(r.getOriginalString(), r.getReplacementString());
		}
		return s;
	}
	
	private static  void generateFdDico(Element e, ImportItem item){
		
		if (e.element("odaExtensionDataSourceId").getStringValue().equals("bpm.metadata.birt.oda.runtime")){
			
			for(Object _o : e.elements("publicProperty")){
				Element _p = (Element)_o;
				if ("URL".equals(_p.attribute("name").getText())){
					_p.setText(getReplacedString(_p.getStringValue(), item));
				}
			}
		}
		else if (e.element("odaExtensionDataSourceId").getStringValue().equals("org.eclipse.birt.report.data.oda.jdbc")){
			for(Object _o : e.elements("publicProperty")){
				Element _p = (Element)_o;
				if ("odaDriverClass".equals(_p.attribute("name").getText())){
					_p.setText(getReplacedString(_p.getStringValue(), item));
				}
				if ("odaURL".equals(_p.attribute("name").getText())){
					_p.setText(getReplacedString(_p.getStringValue(), item));
				}
				if ("odaPassword".equals(_p.attribute("name").getText())){
					_p.setText(getReplacedString(_p.getStringValue(), item));
				}
				if ("odaUser".equals(_p.attribute("name").getText())){
					_p.setText(getReplacedString(_p.getStringValue(), item));
				}
			}
		}
	}
	private static  void generateGateway(Element e, ImportItem item ){
		Element _e = null;
		if ((_e = e.element("host")) != null){
			_e.setText(getReplacedString(_e.getStringValue(), item));
		}
		if ((_e = e.element("port")) != null){
			_e.setText(getReplacedString(_e.getStringValue(), item));
		}
		if ((_e = e.element("dataBaseName")) != null){
			_e.setText(getReplacedString(_e.getStringValue(), item));
		}
		if ((_e = e.element("driverName")) != null){
			_e.setText(getReplacedString(_e.getStringValue(), item));
		}
		if ((_e = e.element("login")) != null){
			_e.setText(getReplacedString(_e.getStringValue(), item));
		}
		if ((_e = e.element("password")) != null){
			_e.setText(getReplacedString(_e.getStringValue(), item));
		}
		if ((_e = e.element("url")) != null){
			_e.setText(getReplacedString(_e.getStringValue(), item));
		}
		
		
	}
	private static  void generateFd(Element e, ImportItem item){
		if (e.getName().equals("datasource-fmdt")){
			e.element("repositoryUrl").setText(getReplacedString(e.getStringValue(), item));
		}
		else if (e.getName().equals("")){

		
			e.element("user").setText(getReplacedString(e.element("user").getStringValue(), item));
			e.element("password").setText(getReplacedString(e.element("user").getStringValue(), item));
			e.element("schemaName").setText(getReplacedString(e.element("schemaName").getStringValue(), item));
			e.element("driver").setText(getReplacedString(e.element("driver").getStringValue(), item));
			e.element("url").setText(getReplacedString(e.element("url").getStringValue(), item));

		}
	}
	
	private static  void generateFasd(Element e, ImportItem item){
		if (e.getName().equals("XMLAConnection")){
			e.element("url").setText(getReplacedString(e.element("url").getStringValue(), item));
			e.element("user").setText(getReplacedString(e.element("user").getStringValue(), item));
			e.element("password").setText(getReplacedString(e.element("password").getStringValue(), item));

		}
		else{
			e.element("password").setText(getReplacedString(e.element("password").getStringValue(), item));
			e.element("user").setText(getReplacedString(e.element("user").getStringValue(), item));
			e.element("driver").setText(getReplacedString(e.element("driver").getStringValue(), item));			
			e.element("url").setText(getReplacedString(e.element("url").getStringValue(), item));

		}
	}
	

}
