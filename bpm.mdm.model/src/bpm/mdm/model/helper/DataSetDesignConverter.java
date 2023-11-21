package bpm.mdm.model.helper;

import org.eclipse.datatools.connectivity.oda.IQuery;
import org.eclipse.datatools.connectivity.oda.IResultSetMetaData;
import org.eclipse.datatools.connectivity.oda.design.ColumnDefinition;
import org.eclipse.datatools.connectivity.oda.design.DataSetDesign;
import org.eclipse.datatools.connectivity.oda.design.DataSourceDesign;
import org.eclipse.datatools.connectivity.oda.design.DesignFactory;
import org.eclipse.datatools.connectivity.oda.design.Property;
import org.eclipse.datatools.connectivity.oda.design.ResultSetDefinition;

import bpm.dataprovider.odainput.consumer.QueryHelper;
import bpm.vanilla.platform.core.beans.data.OdaInput;

public class DataSetDesignConverter {

	public static OdaInput convert(DataSetDesign dataSetDesign){
		OdaInput odaInput = new OdaInput();
		odaInput.setName(dataSetDesign.getName());
		if (dataSetDesign.getPrivateProperties() != null){
			for(Property p : dataSetDesign.getPrivateProperties().getProperties()){
				if (p.getValue() == null){
					continue;
				}
				odaInput.getDatasetPrivateProperties().setProperty(p.getName(), p.getValue());
			}
		}
		
		if (dataSetDesign.getPublicProperties() != null){
			for(Property p : dataSetDesign.getPublicProperties().getProperties()){
				if (p.getValue() == null){
					continue;
				}
				odaInput.getDatasetPublicProperties().setProperty(p.getName(), p.getValue());
			}
		}
		if (dataSetDesign.getDataSourceDesign().getPrivateProperties() != null){
			for(Property p : dataSetDesign.getDataSourceDesign().getPrivateProperties().getProperties()){
				if (p.getValue() == null){
					continue;
				}
				odaInput.getDatasourcePrivateProperties().setProperty(p.getName(), p.getValue());
			}
		}
		if (dataSetDesign.getDataSourceDesign().getPublicProperties() != null){
			for(Property p : dataSetDesign.getDataSourceDesign().getPublicProperties().getProperties()){
				if (p.getValue() == null){
					continue;
				}
				odaInput.getDatasourcePublicProperties().setProperty(p.getName(), p.getValue());
			}
		}
		
		odaInput.setQueryText(dataSetDesign.getQueryText());
		odaInput.setOdaExtensionDataSourceId(dataSetDesign.getOdaExtensionDataSourceId());
		odaInput.setOdaExtensionId(dataSetDesign.getOdaExtensionDataSetId());
		return odaInput;
	}
	
	public static DataSetDesign convert(OdaInput oda){
		DataSourceDesign d = DesignFactory.eINSTANCE.createDataSourceDesign();
		d.setOdaExtensionId(oda.getOdaExtensionId());
		d.setOdaExtensionDataSourceId(oda.getOdaExtensionDataSourceId());
		
		
		
		for(Object s : oda.getDatasourcePrivateProperties().keySet()){
			if (oda.getDatasourcePrivateProperties().get(s) == null){
				continue;
			}
			if (d.getPrivateProperties() == null){
				d.setPrivateProperties(DesignFactory.eINSTANCE.createProperties());
			}
			d.getPrivateProperties().setProperty((String)s, oda.getDatasourcePrivateProperties().getProperty((String)s));
		}
		
		
		for(Object s : oda.getDatasourcePublicProperties().keySet()){
			if (oda.getDatasourcePublicProperties().get(s) == null){
				continue;
			}
			if (d.getPublicProperties() == null){
				d.setPublicProperties(DesignFactory.eINSTANCE.createProperties());
			}
			d.getPublicProperties().setProperty((String)s, oda.getDatasourcePublicProperties().getProperty((String)s));
		}
		
		
		
		DataSetDesign ds = DesignFactory.eINSTANCE.createDataSetDesign();
		ds.setDataSourceDesign(d);
		ds.setName(oda.getName());
		ds.setOdaExtensionDataSetId(oda.getOdaExtensionId());
		
		
		for(Object s : oda.getDatasetPrivateProperties().keySet()){
			if (oda.getDatasetPrivateProperties().get(s) == null){
				continue;
			}
			if (ds.getPrivateProperties() == null){
				ds.setPrivateProperties(DesignFactory.eINSTANCE.createProperties());
			}
			ds.getPrivateProperties().setProperty((String)s, oda.getDatasetPrivateProperties().getProperty((String)s));
		}
		
		
		for(Object s : oda.getDatasetPublicProperties().keySet()){
			if (oda.getDatasetPublicProperties().get(s) == null){
				continue;
			}
			if (ds.getPublicProperties() == null){
				ds.setPublicProperties(DesignFactory.eINSTANCE.createProperties());
			}
			ds.getPublicProperties().setProperty((String)s, oda.getDatasetPublicProperties().getProperty((String)s));
		}

		ds.setQueryText(oda.getQueryText());
		
		return ds;
	}



	public static void rebuildDataSetDesign(DataSetDesign ds){
		if (ds.getPrimaryResultSet() == null){
			
			IQuery q = null;
			try{
				q = QueryHelper.buildquery(convert(ds));
				IResultSetMetaData rsmd = q.getMetaData();
				
				ResultSetDefinition rsd = DesignFactory.eINSTANCE.createResultSetDefinition();
				rsd.setResultSetColumns(DesignFactory.eINSTANCE.createResultSetColumns());
				
				
				
				for(int i = 1; i <= rsmd.getColumnCount(); i++){
					ColumnDefinition def = DesignFactory.eINSTANCE.createColumnDefinition();
					def.setAttributes(DesignFactory.eINSTANCE.createDataElementAttributes());
					def.getAttributes().setName(rsmd.getColumnName(i));
					def.getAttributes().setNativeDataTypeCode(rsmd.getColumnType(i));
					def.getAttributes().setUiDisplayName(rsmd.getColumnLabel(i));
					rsd.getResultSetColumns().getResultColumnDefinitions().add(def);
				}
			
				ds.setPrimaryResultSet(rsd);
			}catch(Exception ex){
				ex.printStackTrace();
			}finally{
				try{
					q.close();
					QueryHelper.removeQuery(q);
				}catch(Exception ex){
					
				}
			}
			
			
			
		}
	}
}
