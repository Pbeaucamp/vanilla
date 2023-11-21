package bpm.gateway.core.transformations.inputs.odaconsumer;

import java.math.BigInteger;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.eclipse.datatools.connectivity.oda.IParameterMetaData;
import org.eclipse.datatools.connectivity.oda.IQuery;
import org.eclipse.datatools.connectivity.oda.IResultSet;
import org.eclipse.datatools.connectivity.oda.IResultSetMetaData;

import bpm.dataprovider.odainput.consumer.QueryHelper;
import bpm.gateway.core.Activator;
import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.StreamElement;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.transformations.inputs.OdaInput;
import bpm.gateway.core.transformations.inputs.OdaInputWithParameters;

public class OdaHelper {

	/**
	 * ERE: 
	 * @param input
	 * @return StreamDescriptor
	 * @throws Exception
	 */
	public static StreamDescriptor createDescriptorWithParameters(OdaInputWithParameters input) throws Exception{
		
		bpm.vanilla.platform.core.beans.data.OdaInput odaInput = new bpm.vanilla.platform.core.beans.data.OdaInput();
		odaInput.setDatasetPrivateProperties(input.getDatasetPrivateProperties());
		odaInput.setDatasetPublicProperties(input.getDatasetPublicProperties());
		odaInput.setDatasourcePrivateProperties(input.getDatasourcePrivateProperties());
		odaInput.setDatasourcePublicProperties(input.getDatasourcePublicProperties());
		odaInput.setOdaExtensionDataSourceId(input.getOdaExtensionDataSourceId());
		odaInput.setOdaExtensionId(input.getOdaExtensionId());
		
		//ere added
		odaInput.setQueryText(input.getQueryText());
		
		IQuery query = QueryHelper.buildquery(odaInput);
			
		IResultSetMetaData meta =  query.getMetaData();;
		
		DefaultStreamDescriptor desc = new DefaultStreamDescriptor();
		
		for( int i = 1; i <= meta.getColumnCount(); i ++){
			StreamElement e = new StreamElement();
			e.name = meta.getColumnName(i);
			e.type = meta.getColumnType(i);
			e.typeName = meta.getColumnTypeName(i);
			e.className = getJavaClassName(e.type);
			e.transfoName = input.getName();
			e.originTransfo = input.getName();
			desc.addColumn(e);
		}
		
		IParameterMetaData pmeta = query.getParameterMetaData();

		HashMap<String, Integer> oldParamValuew = new HashMap<String, Integer>();
		 
		for(String s :input.getParameterNames()){
			oldParamValuew.put(s, input.getParameterValue(s));
		}
		
		input.removeAllParameters();
		
		for(int i = 1; i <= pmeta.getParameterCount(); i++){
			String name = pmeta.getParameterName(i);
			if (name == null){
				name = "parameter_" + i; //do not change unless you want to break compatibility for vanilla disco
			}
			input.addParameterNames(name);
			
			
			StreamElement ele = createColumn(name, input.getName());
			desc.addColumn(ele);
			Activator.getLogger().debug("ODA Helper Created new parameter-column pname " + name + " on input " + input.getName());
			
			for(String s : oldParamValuew.keySet()){
				if (s.equals(name)){
					input.setParameter(name, oldParamValuew.get(s));
				}
			}
		}
		
		query.close();
		QueryHelper.removeQuery(query);
		
		return desc;
	}
	
	public static StreamDescriptor createDescriptor(Transformation t, bpm.vanilla.platform.core.beans.data.OdaInput odaInput) throws Exception{
		IQuery query = QueryHelper.buildquery(odaInput);
		
		IResultSetMetaData meta =  query.getMetaData();;
		
		DefaultStreamDescriptor desc = new DefaultStreamDescriptor();
		
		for( int i = 1; i <= meta.getColumnCount(); i ++){
			StreamElement e = new StreamElement();
			e.name = meta.getColumnName(i);
			e.type = meta.getColumnType(i);
			e.typeName = meta.getColumnTypeName(i);
			e.className = getJavaClassName(e.type);
			e.transfoName = t.getName();
			e.originTransfo = t.getName();
			desc.addColumn(e);
		}
		
		query.close();
		QueryHelper.removeQuery(query);
		QueryHelper.closeConnectionFor(query);
		return desc;
		
	}
	public static StreamDescriptor createDescriptor(OdaInput input) throws Exception{
		
		
		
		
		IQuery query = QueryHelper.buildquery(convert(input));
		
		IResultSetMetaData meta =  query.getMetaData();;
		
		DefaultStreamDescriptor desc = new DefaultStreamDescriptor();
		
		for( int i = 1; i <= meta.getColumnCount(); i ++){
			StreamElement e = new StreamElement();
			e.name = meta.getColumnName(i);
			e.type = meta.getColumnType(i);
			e.typeName = meta.getColumnTypeName(i);
			e.className = getJavaClassName(e.type);
			e.transfoName = input.getName();
			e.originTransfo = input.getName();
			desc.addColumn(e);
		}
		
		IParameterMetaData pmeta = query.getParameterMetaData();

		HashMap<String, Integer> oldParamValuew = new HashMap<String, Integer>();
		
		for(String s :input.getParameterNames()){
			oldParamValuew.put(s, input.getParameterValue(s));
		}
		
		input.removeAllParameters();
		
		for(int i = 1; i <= pmeta.getParameterCount(); i++){
			String name = pmeta.getParameterName(i);
			if (name == null){
				name = "parameter_" + i;
			}
			input.addParameterNames(name);
			
			for(String s : oldParamValuew.keySet()){
				if (s.equals(name)){
					input.setParameter(name, oldParamValuew.get(s));
				}
			}
		}
		
		query.close();
		QueryHelper.removeQuery(query);
		return desc;
	}
	
	/**
	 * Private method to add a column from a parameter variable
	 * @param parameter
	 * @return
	 */
	private static StreamElement createColumn(String parameter, String transformation) {
		StreamElement e = new StreamElement();
		e.name = parameter;
		e.type = 1;
		e.typeName = "Non-defined";
		e.className = getJavaClassName(e.type);
		e.transfoName = transformation;
		e.originTransfo = transformation;
		
		return e;
	}
	
	private static String getJavaClassName(int typeCode){
		switch(typeCode){
		case Types.BIGINT:
			return BigInteger.class.getName();
		case Types.INTEGER:
		case Types.SMALLINT:
			return Integer.class.getName();
			
		case Types.BOOLEAN:
			return Boolean.class.getName();
			
		case Types.CHAR:
		case Types.LONGVARCHAR:
		case Types.VARCHAR:
		case Types.VARBINARY:
			return String.class.getName();
			
		case Types.FLOAT:
			return Float.class.getName();
		case Types.REAL:	
		case Types.DECIMAL:
		case Types.DOUBLE:
		case Types.NUMERIC:
			return Double.class.getName();
			
		
		case Types.DATE:
		case Types.TIME:
		case Types.TIMESTAMP:
			return Date.class.getName();
		}
		return Object.class.getName();
	}

	
	public static bpm.vanilla.platform.core.beans.data.OdaInput convert(OdaInput input){
		bpm.vanilla.platform.core.beans.data.OdaInput odaInput = new bpm.vanilla.platform.core.beans.data.OdaInput();
		odaInput.setDatasetPrivateProperties(input.getDatasetPrivateProperties());
		odaInput.setDatasetPublicProperties(input.getDatasetPublicProperties());
		odaInput.setDatasourcePrivateProperties(input.getDatasourcePrivateProperties());
		odaInput.setDatasourcePublicProperties(input.getDatasourcePublicProperties());
		odaInput.setOdaExtensionDataSourceId(input.getOdaExtensionDataSourceId());
		odaInput.setOdaExtensionId(input.getOdaExtensionId());
		//ere added
		odaInput.setQueryText(input.getQueryText());
		
		return odaInput;
	}
	
	public static List<List<Object>> getValues(OdaInput input, int rowNumber) throws Exception {
		IQuery query = QueryHelper.buildquery(convert(input));
		
		query.setMaxRows(rowNumber);
		
		List<List<Object>> values = new ArrayList<List<Object>>();
		IResultSet rs = query.executeQuery();
//		IResultSetMetaData rsmd = query.getMetaData();
		
		while(rs.next()){
			List<Object> row = new ArrayList<Object>();
			for(int i = 1; i <= input.getDescriptor(null).getColumnCount(); i++){
				row.add(rs.getString(i));
			}
			values.add(row);
		}
		rs.close();
		query.close();
		QueryHelper.removeQuery(query);
		return values;
	}

	public static List<List<Object>> getCountDistinctFieldValues(StreamElement field, OdaInput stream) throws Exception {
		IQuery query = QueryHelper.buildquery(convert(stream));
		
		List<List<Object>> values = new ArrayList<List<Object>>();
		IResultSet rs = query.executeQuery();
		IResultSetMetaData rsmd = query.getMetaData();
		
		while(rs.next()){
			Object value = null;
			boolean present = false;
			
			for(int i = 1; i <= rsmd.getColumnCount(); i++){
				if (field.name.equals(rsmd.getColumnName(i))){
					value = rs.getObject(i);
				}
			}
			
			
			
			for(List<Object> l : values){
				if (l.get(0).equals(value)){
					l.set(1, (Integer)l.get(1) + 1);
					present = true;
					break;
				}
			}
			
			if (!present){
				List<Object> l = new ArrayList<Object>();
				l.add(value);
				l.add(1);
				values.add(l);
			}
		}
		query.close();
		QueryHelper.removeQuery(query);
		return values;
	}
}
