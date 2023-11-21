package bpm.metadata.layer.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import bpm.metadata.layer.logical.IDataSource;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.layer.physical.olap.UnitedOlapConnection;
import bpm.metadata.query.UnitedOlapQuery;
import bpm.metadata.query.UnitedOlapQueryGenerator;
import bpm.metadata.resource.IFilter;
import bpm.metadata.resource.Prompt;
import bpm.vanilla.platform.core.IVanillaContext;

public class UnitedOlapBusinessTable extends AbstractBusinessTable {

	public UnitedOlapBusinessTable(){}
	
	public void addFilter(List groupNames, IFilter filter){
		for(Object o : groupNames){
			addFilter((String)o, filter);
		}
	}
	
	public UnitedOlapBusinessTable(String name) {
		super(name);
	}

	@Override
	public List<List<String>> executeQuery(IVanillaContext vanillaCtx, int numRows) throws Exception {
		UnitedOlapQuery q = new UnitedOlapQuery(
				new ArrayList<IDataStreamElement>(getColumns()), 
				new ArrayList<IFilter>(), new ArrayList<Prompt>() , 
				((UnitedOlapConnection)getDefaultConnection()).getRuntimeContext().getGroupName(), 
				true);
		
		UnitedOlapBusinessPackage dummy = new UnitedOlapBusinessPackage();
		dummy.addBusinessTable(this);
		
		boolean containsMeasure = false;
		boolean containsDimension = false;
		for(IDataStreamElement col : q.getSelect()){
			if (col.getType().getParentType() == IDataStreamElement.Type.MEASURE){
				containsMeasure = true;
			}
			else{
				containsDimension = true;
			}
		}
		
		if (containsDimension && !containsMeasure){
			return getDefaultConnection().executeQuery(name, numRows, null);
		}
		else if (containsDimension && containsMeasure){
			return getDefaultConnection().executeQuery(q, numRows, vanillaCtx, new ArrayList<List<String>>(), dummy);
		}
		else if (!containsDimension && containsMeasure){
			throw new Exception("Cannot browse content when only measures are present");
		}
		else{
			throw new Exception("The query has no column");
		}
		
	}
	
	public UnitedOlapConnection getDefaultConnection() throws Exception{
		List<IDataSource> l = getDataSources();
		if (l.size() != 1){
			throw new Exception("more than one datasource not supported yet");
		}
		return (UnitedOlapConnection)l.get(0).getConnections(null).get(0);

	}
	
	
	/**
	 * get all datasources taking part of the businesstable
	 * @return
	 */
	public List<IDataSource> getDataSources(){
		List<IDataSource> list = new ArrayList<IDataSource>();
		
		for(IDataStreamElement c : columns.values()){
			if (!list.contains(c.getDataStream().getDataSource())){
				list.add(c.getDataStream().getDataSource());
			}
		}
		
		for(IBusinessTable t : childs){
			for(IDataSource ds : ((UnitedOlapBusinessTable)t).getDataSources()){
				if (!list.contains(ds)){
					list.add(ds);
				}
			}
		}
		
		return list;
	}
	
	@Override
	public String getXml() {
		StringBuffer buf = new StringBuffer();
		buf.append("        <unitedOlapBusinessTable>\n");
		buf.append("            <name>" + name + "</name>\n");
		buf.append("            <positionX>" + positionX + "</positionX>\n");
		buf.append("            <positionY>" + positionY + "</positionY>\n");
		buf.append("            <description>" + description + "</description>\n");
		buf.append("            <drillable>" + isDrillable + "</drillable>\n");
		
		
		if (parent != null){
			buf.append("            <parent>" + parent.getName() + "</parent>\n");
		}
		
		for(IDataStreamElement c : columns.values()){
			buf.append("            <column>\n");
			buf.append("                <dataStreamName>" + c.getDataStream().getName() + "</dataStreamName>\n");
			buf.append("                <dataStreamElementName>" + c.getName() + "</dataStreamElementName>\n");
			buf.append("            </column>\n");
		}
		
		for(IDataStreamElement c : order){
			if (c == null){
				continue;
			}
			buf.append("            <order>\n");
			buf.append("                <position>" + order.indexOf(c) + "</position>\n");
			buf.append("                <dataStreamName>" + c.getDataStream().getName() + "</dataStreamName>\n");
			buf.append("                <dataStreamElementName>" + c.getName() + "</dataStreamElementName>\n");
			buf.append("            </order>\n");
		}
		
		//grants
		buf.append("            <groupNames>");
		boolean first = true;
		for(String s : granted.keySet()){
			if(granted.get(s)){
				if(first){
					first = false;
				}
				else {
					buf.append(",");
				}
				buf.append(s);
			}
		}
		buf.append("</groupNames>\n");
		
		//outputname
		for(Locale l : outputName.keySet()){
			buf.append("            <outputname>\n");
			buf.append("                <country>" + l.getCountry() + "</country>\n");
			buf.append("                <language>" + l.getLanguage() + "</language>\n");
			buf.append("                <value>" + outputName.get(l) + "</value>\n");
			buf.append("            </outputname>\n");
		}
		
		
		//filters
//		for(String s : filters.keySet()){
//			if (filters.get(s).size() > 0){
//				
//				buf.append("            <filters>\n");
//				buf.append("                <groupName>" + s + "</groupName>\n");
//				for(IFilter f : filters.get(s)){
//					buf.append(f.getXml());
//				}
//				buf.append("            </filters>\n");
//			}
//			
//		}
		
		for(IFilter f : getFilters()){
			buf.append("            <filters>\n");
			boolean contained = false;
			for(String s : filters.keySet()){
				if (filters.get(s).contains(f)){
					contained = true;
					buf.append("                <groupName>" + s + "</groupName>\n");
				}
			}
			
			if (contained){
				buf.append(f.getXml());
			}
			
			buf.append("            </filters>\n");
		
		}
		
		buf.append("        </unitedOlapBusinessTable>\n");
		
		
		for(IBusinessTable b : childs){
			buf.append(b.getXml());;
		}
		
		return buf.toString();
	}
}
