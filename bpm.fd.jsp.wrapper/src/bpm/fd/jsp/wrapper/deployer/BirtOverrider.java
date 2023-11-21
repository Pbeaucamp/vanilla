package bpm.fd.jsp.wrapper.deployer;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import bpm.metadata.MetaDataReader;
import bpm.metadata.digester.SqlQueryDigester;
import bpm.metadata.layer.business.IBusinessModel;
import bpm.metadata.layer.business.IBusinessPackage;
import bpm.metadata.layer.logical.ICalculatedElement;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.layer.physical.IConnection;
import bpm.metadata.layer.physical.sql.SQLConnection;
import bpm.metadata.query.QuerySql;
import bpm.metadata.resource.Prompt;
import bpm.studio.jdbc.management.config.IConstants;
import bpm.studio.jdbc.management.model.DriverInfo;
import bpm.studio.jdbc.management.model.ListDriver;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class BirtOverrider {

	private HashMap<String, Collection<IBusinessModel>> businessModels = new HashMap<String, Collection<IBusinessModel>> ();
	private String birtPath;
	
	public BirtOverrider(String birtPath){
		this.birtPath = birtPath;
		
	}
	/**
	 * replace FMDT dataSource by JDBC datasources
	 * generate the rptdesign
	 * and return its path
	 * @param doc
	 * @param groupId
	 * @param _groupName
	 * @param dirItId
	 * @param directoryItemName
	 * @return
	 * @throws Exception
	 */
	public String generateBirt(IRepositoryContext repCtx, Document doc, int dirItId, String directoryItemName) throws Exception{

		try {
			Element root = doc.getRootElement();
			
			
			Element dataSources = root.element("data-sources");
			if (dataSources == null){
				throw new Exception("Error while converting BIRT FMDT into JDBC : no datasources in BIRT xml" );
			}
			
			
			// to store the pacakge for each FMDT datasources
			HashMap<String, IBusinessPackage> packages = new HashMap<String, IBusinessPackage>();
			HashMap<String, String> groupNames = new HashMap<String, String>();
			
			//datasources + connections
			HashMap<Element, List<Element>> odaDataSources = new HashMap<Element, List<Element>>();
			
			//look for BusinessPackage and rebuild them
			for(Object o : dataSources.elements("oda-data-source")){
				Element oda = (Element)o;
				if (!oda.attribute("extensionID").getValue().equals("bpm.metadata.birt.oda.runtime")){
					continue;
				}
				
				//we store the datasource to find its datasets
				String dataSourceName = oda.attribute("name").getValue();
				
				
				//get the FMDT informations
				String connection = null; String repositoryUrl = null; String user = null; String password = null;
				String packageName = null; String modelName = null; String groupName = null;
				String directoryItemId = null;
				
				odaDataSources.put(oda, new ArrayList<Element>());
				
				for(Object p : oda.elements("property")){
					
					Element prop = (Element)p;
					//System.out.println(prop.asXML());
					if (prop.attribute("name").getStringValue().equals("USER")){
						user = prop.getStringValue();
						odaDataSources.get(oda).add(prop);
					}
					if (prop.attribute("name").getStringValue().equals("PASSWORD")){
						password = prop.getStringValue();
						odaDataSources.get(oda).add(prop);
					}
					if (prop.attribute("name").getStringValue().equals("URL")){
						repositoryUrl = prop.getStringValue();
						odaDataSources.get(oda).add(prop);
					}
					if (prop.attribute("name").getStringValue().equals("DIRECTORY_ITEM_ID")){
						directoryItemId = prop.getStringValue();
						odaDataSources.get(oda).add(prop);
					}
					if (prop.attribute("name").getStringValue().equals("BUSINESS_MODEL")){
						modelName = prop.getStringValue();
						odaDataSources.get(oda).add(prop);
					}
					if (prop.attribute("name").getStringValue().equals("BUSINESS_PACKAGE")){
						packageName = prop.getStringValue();
						odaDataSources.get(oda).add(prop);
					}
					if (prop.attribute("name").getStringValue().equals("GROUP_NAME")){
						groupName = prop.getStringValue();
						groupNames.put(dataSourceName, groupName);
						odaDataSources.get(oda).add(prop);
					}
					
				}
				
				
				if (oda.element("list-property") != null && oda.element("list-property").attribute("name").getStringValue().equals("privateDriverProperties")){
					odaDataSources.get(oda).add(oda.element("list-property"));
				}
				
				try{
					
					//look if the fmdt havent already built
					String alreadyBuiltKey = null;
					if (this.businessModels != null){
						for(String _dirItId : this.businessModels.keySet()){
							if (_dirItId.equals(directoryItemId) && this.businessModels.get(_dirItId) != null){
								alreadyBuiltKey = _dirItId;
							}
						}
					}
					
					
					if (alreadyBuiltKey == null){
						//System.out.println("Generation : getting FMDT with id " + directoryItemId+ " for BIRT");
						IRepositoryApi sock = new RemoteRepositoryApi(repCtx);
						RepositoryItem itFmdt = sock.getRepositoryService().getDirectoryItem(Integer.parseInt(directoryItemId));
						String fmdt = sock.getRepositoryService().loadModel(itFmdt);
						
						if (this.businessModels == null){
							this.businessModels = new HashMap<String, Collection<IBusinessModel>>();
						}
						
						this.businessModels.put(directoryItemId, MetaDataReader.read(groupName, IOUtils.toInputStream(fmdt), sock, false));
						alreadyBuiltKey = directoryItemId;
						
					}
					
					
					for(IBusinessModel bM : this.businessModels.get(alreadyBuiltKey)){
						if (bM.getName().equals(modelName)){
							
							for(IBusinessPackage p : bM.getBusinessPackages(groupName) ){
								if (p.getName().equals(packageName)){
									packages.put(dataSourceName, p);
								}
							}
							
						}
					}
					
					
					//System.out.println("Generation : Birt parsed ");	
				}catch(Exception e){
					e.printStackTrace();
				}
				
				
				
			}
			
			//look for dataSet and rebuild them
			Element dataSets = root.element("data-sets");
			
			for(Object ds : dataSets.elements("oda-data-set")){
				Element dts = (Element)ds;
				if (!dts.attribute("extensionID").getValue().equals("bpm.metadata.birt.oda.runtime.dataSet")){
					continue;
				}
				
				//look for DataSourceName element and queryText element
				Element dataSourceName = null;
				Element query = null;
				
				
				
				for(Object _p : dts.elements("property")){
					if (((Element)_p).attribute("name").getValue().equals("dataSource")){
						dataSourceName =(Element)_p; 
					}
					if (((Element)_p).attribute("name").getValue().equals("queryText")){
						query =(Element)_p;
					}
					//
					if (query != null && dataSourceName != null){
						break;
					}
				}
				if (query == null) {
					for(Object _p : dts.elements("xml-property")){
						if (((Element)_p).attribute("name").getValue().equals("queryText")){
							query =(Element)_p;
						}
						if (query != null && dataSourceName != null){
							break;
						}
					}
				}
				
				//rebuild query
				IBusinessPackage pack = null;
				String groupName = null;
				for(String key : packages.keySet()){
					if (key.equals(dataSourceName.getStringValue())){
						pack = packages.get(key);
						break;
					}
				}
				
				for(String key : groupNames.keySet()){
					if (key.equals(dataSourceName.getStringValue())){
						groupName = groupNames.get(key);
						break;
					}
				}
				
				SqlQueryDigester dig = new SqlQueryDigester(IOUtils.toInputStream(query.getStringValue()), groupName,  pack);
				QuerySql q = dig.getModel();
				
				//modify the nodes dts, query
				dts.attribute("extensionID").setValue("org.eclipse.birt.report.data.oda.jdbc.JdbcSelectDataSet");
				
				List<List<String>> l =new ArrayList<List<String>>();
				for(Prompt p : q.getPrompts()){
					l.add(new ArrayList<String>());
					l.get(0).add("?");
					
				}
				query.setText(pack.getQuery(null, repCtx.getVanillaContext(), q, l).replace("`", "\""));
				query.setText(query.getStringValue().replace("'?'", "?"));
				
				
				//
				List<Element> nativeColumn = new ArrayList<Element>();
				for(Object _p : dts.elements("list-property")){
					if (!(((Element)_p).attribute("name").getStringValue().equals("resultSet"))){
						continue;
					}
					
					for(Object _s : ((Element)_p).elements("structure")){
						for(Object _o : ((Element)_s).elements("property")){
							
							if (((Element)_o).attribute("name").getStringValue().equals("nativeName")){
								//TODO
								
								for(IDataStreamElement c : q.getSelect()){
									if (c.getName().equals(((Element)_o).getStringValue())){
										
										if (c instanceof ICalculatedElement){
											String colName = c.getName();
											if (colName.contains(".")){
												colName = colName.substring(colName.indexOf(".") + 1);
											}
											
											((Element)_o).setText(colName);
										}
										else{
											String colName = c.getOrigin().getName();
											if (colName.contains(".")){
												colName = colName.substring(colName.indexOf(".") + 1);
											}
											
											((Element)_o).setText(colName);
										}
										
										
									}
								}
							}
							
						}
					}
					
				}
				
			}
			
			
			for(Element e : odaDataSources.keySet()){
				e.attribute("extensionID").setValue("org.eclipse.birt.report.data.oda.jdbc");
				
				//delete fmdt properties
				String fmdtConnectionName = null;
				for(Object son : e.elements()){
					Element elemChild =  (Element)son;
					if (odaDataSources.get(e).contains(elemChild)){
						e.remove(elemChild);
					}
					
					if (elemChild.getName().equals("list-property")){
						fmdtConnectionName = elemChild.element("ex-property").element("value").getStringValue();
					}
				}
				
				//find the package and the group to insert connection informations
				IBusinessPackage p = null;
				
				for(String s : packages.keySet()){
					if (s.equals(e.attribute("name").getValue())){
						p = packages.get(s);
						break;
					}
				}
				
				String groupName = null;
				for(String s : groupNames.keySet()){
					if (s.equals(e.attribute("name").getValue())){
						groupName = groupNames.get(s);
						break;
					}
				}
				
				//insert JDBC info
				
				List<String> conNames = p.getConnectionsNames(groupName);
				
				IConnection c = null;
				
				for(String s : conNames){
					if (s.equals(fmdtConnectionName)){
						c = p.getConnection(groupName, s);
						break;
					}
				}
				
				
				
				
				
				
				//
				DriverInfo info = ListDriver.getInstance(IConstants.getJdbcDriverXmlFile()).getInfo(((SQLConnection)c).getDriverName());
				
				String url = info.getUrlPrefix()  +  ((SQLConnection)c).getHost() + ":" + ((SQLConnection)c).getPortNumber() + "/" + ((SQLConnection)c).getDataBaseName(); 
				
				e.addElement("property").addAttribute("name", "odaDriverClass").setText(info.getClassName());
				e.addElement("property").addAttribute("name", "odaURL").setText(url);
				e.addElement("property").addAttribute("name", "odaUser").setText(((SQLConnection)c).getUsername());
				e.addElement("encrypted-property").addAttribute("name", "odaPassword").addAttribute("encryptionID", "base64").setText(SimpleEncryptionHelper.getInstance().encrypt(((SQLConnection)c).getPassword()));
				
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Error when converting Birt FMDT DataSource to JDBC DataSource.\n" + e.getMessage() , e);
			
		}
		
		try{
			String path = birtPath + File.separator + formatName(directoryItemName) + "_" + dirItId + "_" + repCtx.getGroup().getName() + ".rptdesign";
			XMLWriter writer = new XMLWriter(new FileOutputStream(path), OutputFormat.createPrettyPrint());
			writer.write(doc);
			writer.close();
			
			
			return path;
		}catch(Exception ex){
			throw new Exception("Error when writing BIRT rptdesign after conversion\n" + ex.getMessage(), ex);
		}
		
			//System.out.println("Generation : Birt writed");
		
	}
	
	
	public String formatName(String chaine) {
	    if (chaine == null)
	        return null;
	   
	      String temp = "";
	      for (int i=0; i < chaine.length(); i++) {
	        if (! ((chaine.charAt(i) < 48 && chaine.charAt(i) != 32) || chaine.charAt(i) == 255 ||
	               chaine.charAt(i) == 208 || chaine.charAt(i) == 209 ||
	               chaine.charAt(i) == 215 || chaine.charAt(i) == 216 ||
	               (chaine.charAt(i) < 65 && chaine.charAt(i) > 57) ||
	               (chaine.charAt(i) < 192 && chaine.charAt(i) > 122) ||
	               (chaine.charAt(i) < 65 && chaine.charAt(i) > 57))) {
	          temp = temp + chaine.charAt(i);
	        }
	      }

	     
	      
	      return temp.toLowerCase();
	    }
}
