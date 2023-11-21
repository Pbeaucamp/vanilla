package bpm.metadata.tools;

import java.util.Properties;

import org.fasd.datasource.DataObjectItem;
import org.fasd.datasource.DataObjectOda;
import org.fasd.datasource.DatasourceOda;
import org.fasd.olap.FAModel;
import org.fasd.olap.OLAPCube;
import org.fasd.olap.OLAPDimension;
import org.fasd.olap.OLAPHierarchy;
import org.fasd.olap.OLAPLevel;
import org.fasd.olap.OLAPMeasure;
import org.fasd.olap.OLAPSchema;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.resources.CkanPackage;
import bpm.vanilla.platform.core.utils.CkanHelper;

public class D4CCubeHelper {
	
	private static String ODA_JDBC_DATASOURCE_EXTENSION_ID = "org.eclipse.birt.report.data.oda.jdbc";
	private static String ODA_JDBC_DATASET_EXTENSION_ID = "org.eclipse.birt.report.data.oda.jdbc.JdbcSelectDataSet";
	private static String ODA_JDBC_DRIVER = "odaDriverClass";
	private static String ODA_JDBC_URL = "odaURL";
	private static String ODA_JDBC_USER = "odaUser";
	private static String ODA_JDBC_PASSWORD = "odaPassword";

	public static String generateCubeXml(String ckanUrl, String ckanApi, String ckanOrg, String dbUrl, String dbLogin, String dbPass, String datasetString, boolean update, int id, IRepositoryApi sock, String dimensions, String mesures, String cubeName) throws Exception {
		
		DatasourceOda datasourceOda = new DatasourceOda();
		datasourceOda.setName("datasource");
		
		CkanHelper helper = new CkanHelper(ckanUrl, ckanOrg, ckanApi, "", false);
		CkanPackage p = helper.getCkanPackage(datasetString);
		
		//connection informations
		datasourceOda.setOdaDatasourceExtensionId(ODA_JDBC_DATASOURCE_EXTENSION_ID);
		datasourceOda.setOdaExtensionId(ODA_JDBC_DATASOURCE_EXTENSION_ID);
		
		Properties publicProperties = new Properties();
		publicProperties.put(ODA_JDBC_DRIVER, "org.postgresql.Driver");
		publicProperties.put(ODA_JDBC_URL, dbUrl);
		publicProperties.put(ODA_JDBC_USER, dbLogin);
		publicProperties.put(ODA_JDBC_PASSWORD, dbPass);
		
		Properties privateProperties = new Properties();
		
		datasourceOda.setPublicProperties(publicProperties);
		datasourceOda.setPrivateProperties(privateProperties);
		
		DataObjectOda object = new DataObjectOda(datasourceOda);
		object.setOdaDatasetExtensionId(ODA_JDBC_DATASET_EXTENSION_ID);
		
		object.setName("dataset");
		object.setDataObjectType("fact");

		datasourceOda.addDataObject(object);
		
		OLAPSchema shema = new OLAPSchema();
		OLAPCube cube = new OLAPCube();
		
		String querySelect = "";
		
		String[] dims = dimensions.split(";");
		boolean first = true;
		for(int i = 0 ; i < dims.length ; i++) {
			if(dims[i] == null || dims[i].equals("")) {
				continue;
			}
			String[] levels = dims[i].split(",");		
			OLAPDimension olapDimension = new OLAPDimension();
			olapDimension.setName(levels[0]);
			olapDimension.setCaption(levels[0]);
			OLAPHierarchy hierarchy = new OLAPHierarchy(levels[0]);
			hierarchy.setCaption(levels[0]);
			for (int j = 0 ; j < levels.length ; j++) {
				OLAPLevel lev = new OLAPLevel(levels[j]);
				DataObjectItem item = new DataObjectItem(levels[j]);
				item.setSqlType("VARCHAR");
				item.setOrigin(levels[j]);
				if(first) {
					first = false;
				}
				else {
					querySelect += ",";
				}
				querySelect += levels[j];
				object.addDataObjectItem(item);
				item.setParent(object);
				lev.setItem(item);
				lev.setCaption(levels[j]);
				lev.setParent(hierarchy);
				hierarchy.addLevel(lev);
			}
			olapDimension.addHierarchy(hierarchy);
			shema.addDimension(olapDimension);
			cube.addDim(olapDimension);
			
		}
		String[] mes = mesures.split(";");
		for(int i = 0 ; i < mes.length ; i++) {
			if(mes[i] == null || mes[i].equals("")) {
				continue;
			}
			OLAPMeasure meas = new OLAPMeasure(mes[i]);
			DataObjectItem origin = new DataObjectItem(mes[i]);
			origin.setOrigin(mes[i]);
//			origin.setSqlType(measure.getSqlType());
			object.addDataObjectItem(origin);
			meas.setOrigin(origin);
			querySelect += "," + mes[i];
			meas.setAggregator("sum");
			meas.setColumnName(mes[i]);
			meas.setCaption(mes[i]);
			shema.addMeasure(meas);
			cube.addMes(meas);
		}
		
		object.setQueryText("select " + querySelect + " from \"" + p.getResources().get(0).getId() + "\"");

		cube.setName(cubeName);
		cube.setDescription("");
		cube.setFactDataObjectId(datasourceOda.getDataObjects().get(0).getId());

		shema.addCube(cube);

		FAModel model = new FAModel();
		model.addDataSource(datasourceOda);
		model.setOLAPSchema(shema);
		model.setName(cubeName);
		
		return model.getFAXML();
	}

}
