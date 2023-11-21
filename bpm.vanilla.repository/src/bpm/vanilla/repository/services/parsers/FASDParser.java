package bpm.vanilla.repository.services.parsers;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Element;

import bpm.vanilla.platform.core.repository.DataSource;

public class FASDParser extends AbstractGeneralParser{
	public FASDParser(String xmlModel) throws Exception {
		super(xmlModel);
		
	}
	@Override
	protected void parseDependancies() throws Exception{
		Element root = document.getRootElement();
		/*
		 * parseDependancies
		 */
		dependanciesId = new ArrayList<Integer>();
		
		Element dataSources = root.element("datasources");
		
		if (dataSources == null){
			return;
		}
		for(Element ds : (List<Element>)dataSources.elements("datasource-oda")){
			if (ds.element("odaextensionid") != null && ds.element("odaextensionid").getText().equals(DataSource.DATASOURCE_FMDT)){
				
				for(Element eP : (List<Element>)ds.element("public-properties").elements("property")){
					if (eP.element("name") != null && eP.element("name").getText().equals("DIRECTORY_ITEM_ID")){
						
						Element eVal = eP.element("value");
						if (eVal != null){
							try{
								dependanciesId.add(Integer.parseInt(eVal.getText()));
							}catch(Exception ex){
								Logger.getLogger(getClass()).warn("Error when parsing FMDT dependancy - " + ex.getMessage(), ex);
							}
						}
						
					}
				}
			}
			else if (ds.element("odaextensionid") != null && ds.element("odaextensionid").getText().equals(DataSource.DATASOURCE_EXCEL)){
				
				for(Element eP : (List<Element>)ds.element("private-properties").elements("property")){
					if (eP.element("name") != null && eP.element("name").getText().equals("repository.item.id")){
						
						Element eVal = eP.element("value");
						if (eVal != null){
							try{
								dependanciesId.add(Integer.parseInt(eVal.getText()));
							}catch(Exception ex){
								Logger.getLogger(getClass()).warn("Error when parsing FMDT dependancy - " + ex.getMessage(), ex);
							}
						}
						
					}
				}
			}
			else if (ds.element("odaextensionid") != null && ds.element("odaextensionid").getText().equals(DataSource.DATASOURCE_CSV)){
				
				for(Element eP : (List<Element>)ds.element("private-properties").elements("property")){
					if (eP.element("name") != null && eP.element("name").getText().equals("repository.item.id")){
						
						Element eVal = eP.element("value");
						if (eVal != null){
							try{
								dependanciesId.add(Integer.parseInt(eVal.getText()));
							}catch(Exception ex){
								Logger.getLogger(getClass()).warn("Error when parsing FMDT dependancy - " + ex.getMessage(), ex);
							}
						}
						
					}
				}
			}
		}

	}
	
	@Override
	public List<Integer> getDataSourcesReferences() {
		return new ArrayList<Integer>();
	}
	@Override
	public String overrideXml(Object object) throws Exception {
		return getXmlModelDefinition();
	}
	@Override
	protected void parseParameters() throws Exception {
		
	}
}
