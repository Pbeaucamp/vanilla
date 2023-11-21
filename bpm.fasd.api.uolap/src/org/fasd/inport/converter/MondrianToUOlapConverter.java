package org.fasd.inport.converter;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.fasd.datasource.DataObject;
import org.fasd.datasource.DataObjectItem;
import org.fasd.datasource.DataObjectOda;
import org.fasd.datasource.DataSource;
import org.fasd.datasource.DatasourceOda;
import org.fasd.inport.DigesterFasd;
import org.fasd.olap.FAModel;

/**
 * A tool class to convert a fasd/mondrian schema to a UnitedOlap schema
 * @author Marc Lanquetin
 *
 */
public class MondrianToUOlapConverter {
	
	private static String ODA_JDBC_DATASOURCE_EXTENSION_ID = "org.eclipse.birt.report.data.oda.jdbc";
	private static String ODA_JDBC_DATASET_EXTENSION_ID = "org.eclipse.birt.report.data.oda.jdbc.JdbcSelectDataSet";
	private static String ODA_JDBC_DRIVER = "odaDriverClass";
	private static String ODA_JDBC_URL = "odaURL";
	private static String ODA_JDBC_USER = "odaUser";
	private static String ODA_JDBC_PASSWORD = "odaPassword";

	public static FAModel convertFromXml(String mondrianXml) throws Exception {
		InputStream stream = IOUtils.toInputStream(mondrianXml,"UTF-8");
		DigesterFasd dig = new DigesterFasd(stream);
		return convertFromMondrianToUOlap(dig.getFAModel());
	}
	
	public static FAModel convertFromStream(InputStream stream) throws Exception {
		DigesterFasd dig = new DigesterFasd(stream);
		return convertFromMondrianToUOlap(dig.getFAModel());
	}

	public static FAModel convertFromFile(String filePath) throws Exception {
		DigesterFasd dig = new DigesterFasd(filePath);
		return convertFromMondrianToUOlap(dig.getFAModel());
	}
	
	public static FAModel convertFromMondrianToUOlap(FAModel mondrianModel) {
		
		System.out.println(mondrianModel.getDataSources());
		
		List<DataSource> mondrianDatasources = new ArrayList<DataSource>(mondrianModel.getDataSources());
		
		mondrianModel.getDataSources().clear();
		
		for(DataSource mondrianDs : mondrianDatasources) {
			
			if(mondrianDs instanceof DatasourceOda) {
				mondrianModel.getListDataSource().add(mondrianDs);
			}
			else {
				DatasourceOda uolapDs = convertToUnitedOlapDatasource(mondrianDs);
				mondrianModel.addDataSource(uolapDs);
			}
		}
		
		return mondrianModel;
	}

	private static DatasourceOda convertToUnitedOlapDatasource(DataSource mondrianDs) {
		DatasourceOda result = new DatasourceOda();
		
		//get common informations
		result.setName(mondrianDs.getDSName());
		result.setId(mondrianDs.getId());
		result.setDSName(mondrianDs.getDSName());
		
		//get connection informations
		result.setOdaDatasourceExtensionId(ODA_JDBC_DATASOURCE_EXTENSION_ID);
		result.setOdaExtensionId(ODA_JDBC_DATASOURCE_EXTENSION_ID);
		
		Properties publicProperties = new Properties();
		publicProperties.put(ODA_JDBC_DRIVER, mondrianDs.getDriver().getDriver());
		publicProperties.put(ODA_JDBC_URL, mondrianDs.getDriver().getUrl());
		publicProperties.put(ODA_JDBC_USER, mondrianDs.getDriver().getUser());
		publicProperties.put(ODA_JDBC_PASSWORD, mondrianDs.getDriver().getPass());
		
		Properties privateProperties = new Properties();
		
		result.setPublicProperties(publicProperties);
		result.setPrivateProperties(privateProperties);
		
		//get dataobjects
		for(DataObject mondrianDo : mondrianDs.getDataObjects()) {
			DataObjectOda uolapDo = new DataObjectOda(result);
			uolapDo.setOdaDatasetExtensionId(ODA_JDBC_DATASET_EXTENSION_ID);
			uolapDo.setQueryText(mondrianDo.getSelectStatement());
			uolapDo.setName(mondrianDo.getName());
			uolapDo.setId(mondrianDo.getId());
			uolapDo.setDataObjectType(mondrianDo.getDataObjectType());
			for(DataObjectItem doi : mondrianDo.getColumns()) {
				uolapDo.addDataObjectItem(doi);
			}
			result.addDataObject(uolapDo);
		}
		
		return result;
	}
	
}
