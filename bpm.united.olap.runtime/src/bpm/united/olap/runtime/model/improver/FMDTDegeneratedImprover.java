package bpm.united.olap.runtime.model.improver;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.eclipse.datatools.connectivity.oda.IQuery;
import org.eclipse.datatools.connectivity.oda.IResultSetMetaData;

import bpm.united.olap.api.model.Level;
import bpm.vanilla.platform.core.beans.data.OdaInput;

public class FMDTDegeneratedImprover extends DegeneratedHierarchyLevelImprover{

	@Override
	public IQuery improveQuery(IQuery query, OdaInput odaInput) throws Exception{
		List<Point> usedIndex = getUsedIndex();
		
		
		
		
		//rebuild FMDT Query
		String fmdtQuery = odaInput.getQueryText();
		Document doc = DocumentHelper.parseText(fmdtQuery);
		Element root = doc.getRootElement().element("sqlQuery");
		
		Element distinct = null;
		if ((distinct = root.element("distinct")) == null){
			distinct = root.addElement("distinct");
		}
		distinct.setText("true");
		
		List<Element> elToRemove = new ArrayList<Element>();
		
		List<Element> cols = new ArrayList<Element>();
		cols.addAll((List<Element>)root.elements("select"));
		cols.addAll((List<Element>)root.elements("formula"));
		
		for(int i = 0; i < cols.size(); i++){
			boolean found = false;
			
			for(Point p : usedIndex){
				if (p.x == i){
					found = true;
					break;
				}
			}
			
			if (!found){
				elToRemove.add(cols.get(i));
			}
		}
		
		elToRemove.addAll(root.elements("orderBy"));
		for(Element e : elToRemove){
			root.remove(e);
		}
		
		
		
		StringBuilder b = new StringBuilder();
		b.append(doc.getRootElement().asXML());
		Logger.getLogger(getClass()).info("Changed FMDT Query {" + fmdtQuery + "} into {" + b.toString() + "}");
		
		
				
		/*
		 * compute offset
		 */
		IResultSetMetaData rsmd = query.getMetaData();
		int count = 0;
		for(int i = 0; i < rsmd.getColumnCount(); i++){
			boolean added = false;
			for(Point p : usedIndex){
				if (p.x == i){
					p.y = count;
					added = true;
				}
			}
			
			if (added){
				count++;
			}
		}
		

		/*
		 * apply offsets
		 */
		
		for(Level l : levelIndex.keySet()){
			levelIndex.get(l).update(usedIndex);
		}
		
		/*
		 * create new Query
		 */
		OdaInput input = new OdaInput();
		input.setDatasetPrivateProperties((Properties)odaInput.getDatasetPrivateProperties().clone());
		input.setDatasetPublicProperties((Properties)odaInput.getDatasetPublicProperties().clone());
		input.setDatasourcePrivateProperties((Properties)odaInput.getDatasourcePrivateProperties().clone());
		input.setDatasourcePublicProperties((Properties)odaInput.getDatasourcePublicProperties().clone());
		input.setOdaExtensionDataSourceId(odaInput.getOdaExtensionDataSourceId());
		input.setOdaExtensionId(odaInput.getOdaExtensionId());
		
		input.setQueryText(b.toString());
		
		/*
		 * close given query
		 */
		query.close();
		bpm.dataprovider.odainput.consumer.QueryHelper.closeConnectionFor(query);
		bpm.dataprovider.odainput.consumer.QueryHelper.removeQuery(query);

		

		
		return getOdaQuery(input);
	}

}
