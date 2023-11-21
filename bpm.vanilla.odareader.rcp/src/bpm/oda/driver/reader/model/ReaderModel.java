package bpm.oda.driver.reader.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.eclipse.core.runtime.Platform;

import bpm.oda.driver.reader.Activator;
import bpm.oda.driver.reader.actions.ActionExport;
import bpm.oda.driver.reader.model.dataset.DataSet;
import bpm.oda.driver.reader.model.datasource.DataSource;

public class ReaderModel {
	
	private ArrayList<DataSource> listDataSource;
	private ArrayList<DataSet> listDataSet;
	private ArrayList<Snapshot> listSnapshots;
	private ArrayList<DataDefinition> listDataDefinitions;
	
	public final static String DEFINITION_FILE = "_Definition";
	
	
	public ReaderModel() {
		super();
		this.listDataSource = new ArrayList<DataSource>();
		this.listDataSet = new ArrayList<DataSet>();
		this.listSnapshots = new ArrayList<Snapshot>();
		this.listDataDefinitions = new ArrayList<DataDefinition>();
		
		fillDatas();
	}
	
	private void fillDatas() {
		
		String path = Platform.getInstanceLocation().getURL().getPath();
		File repertoire = new File(path, "temp"); 

		if (repertoire.isDirectory ()) {
			File[] list = repertoire.listFiles();

			for(File currentFile : list){
				
				//Difference between a snapshot file, and a data source/set definition
				if(!currentFile.getName().contains(DEFINITION_FILE)){
					
					String snapName = currentFile.getName();
					String snapDataSet = snapName.split(Snapshot.SNAP_NAME_SEPARATOR, 0)[0];
					
					snapName = snapName.substring(0, snapName.length()-4);
					
					Snapshot sShot = new Snapshot(snapName,snapDataSet);
					sShot.fillValues();
					
					listSnapshots.add(sShot);
				}
				
				else{
					DataDefinition dataDef = new DataDefinition(currentFile.getName());
					listDataDefinitions.add(dataDef);
				}
			}
		} 



	}
	
	public String getSnapshotName(String dataSetName){
		
		int countDataSet = 1;
		
		for(Snapshot snap : listSnapshots){
			if(snap.getDataSetName().equals(dataSetName)){
				countDataSet++;
			}
		}
		
		return dataSetName + Snapshot.SNAP_NAME_SEPARATOR + countDataSet;
	}
	
	public boolean removeSnapshot(Snapshot snapToRemove){

		//Remove file
		String path = Platform.getInstanceLocation().getURL().getPath();
		
		boolean findSnapFile = false;
		boolean findDefinitionFile = false;
		
		File fileSnapToRemove = null;
		File fileDefinitionToRemove = null;
		
		File repertoire = new File(path, "temp"); 
		if (repertoire.isDirectory ()) {
		
			File[] list = repertoire.listFiles();
			
			for(File currentFile : list){
				
				if(currentFile.getName().equals(snapToRemove.getName()+".txt")){
					findSnapFile = true;
					fileSnapToRemove = currentFile;
				}
				
				if (currentFile.getName().equals(snapToRemove.getName()+ ReaderModel.DEFINITION_FILE + ".xml")){
					findDefinitionFile = true;
					fileDefinitionToRemove = currentFile;
				}
				
			}
		}
		
//		if((findSnapFile) && (findDefinitionFile)){
			Activator.getInstance().getListSnapshots().remove(snapToRemove);
			if (fileSnapToRemove != null){
				fileSnapToRemove.delete();
			}
			
			if (fileDefinitionToRemove != null){
				fileDefinitionToRemove.delete();
			}
			return true;
//		}
//		
//		else{
//			return false;
//		}
	}


	public DataSource getDataSource(DataSet dataSet){
		
		DataSource temp = null;
		for(DataSource dataSource : listDataSource){
			
			if( dataSet.getDataSourceName().equals(dataSource.getName())){
				temp = dataSource;
			}
		}
		
		return temp;
		
	}

	public boolean rebuildDefinition(Snapshot currentSnap){
		
		for(DataDefinition dataDef : listDataDefinitions){
			
			if(dataDef.getFileName().contains(currentSnap.getName())){
				
				String path = Platform.getInstanceLocation().getURL().getPath();
				File nodeFile = new File(path, "temp");

				File fileXML = new File(nodeFile, dataDef.getFileName());

				
				SAXReader saxReader = new SAXReader();
				Document document;
				try {
					document = saxReader.read(fileXML);
				} catch (DocumentException e) {
					e.printStackTrace();
					return false;
				}
				
				Element rootElement = document.getRootElement();
				
			//+++++++ Get Data sources informations
				Element dataSourceElement = rootElement.element(ActionExport.DATASOURCE);
				
				String name = dataSourceElement.attributeValue(ActionExport.DATASOURCE_NAME);
				String odaExtensionId = dataSourceElement.attributeValue(ActionExport.DATASOURCE_ODA_EXTENSION_ID);
				String odaExtensionDataSourceId = dataSourceElement.attributeValue(ActionExport.DATASOURCE_ODA_EXTENSION_DATASOURCE_ID);
				
				//Public properties
				List<Element> publicProperties = dataSourceElement.elements(ActionExport.PROPERTIE_PUBLIC);
				Properties listPublicProperties = new Properties();
				
				for(Element elem : publicProperties){
					String proKey = elem.attributeValue(ActionExport.PROPERTIE_NAME);
					String proValue = elem.attributeValue(ActionExport.PROPERTIE_VALUE);
					listPublicProperties.put(proKey, proValue);
				}
				
				//Private properties
				List<Element> privateProperties = dataSourceElement.elements(ActionExport.PROPERTIE_PRIVATE);
				Properties listPrivateProperties = new Properties();
				
				for(Element elem : privateProperties){
					String proKey = elem.attributeValue(ActionExport.PROPERTIE_NAME);
					String proValue = elem.attributeValue(ActionExport.PROPERTIE_VALUE);
					listPrivateProperties.put(proKey, proValue);
				}
				
				
				//Build the data source
				DataSource dataSource = new DataSource(name, odaExtensionDataSourceId, odaExtensionId, listPublicProperties, listPrivateProperties);
				Activator.getInstance().getListDataSource().add(dataSource);
				
				
				
		//+++++++ Get Data SET informations
				
				Element dataSetElement = rootElement.element(ActionExport.DATASET);

				String dataSetName = dataSetElement.attributeValue(ActionExport.DATASET_NAME);
				String dataSetNameDataSource = dataSetElement.attributeValue(ActionExport.DATASET_DATASOURCE_NAME);
				String dataSetOdaExtensionDataSetID = dataSetElement.attributeValue(ActionExport.DATASET_ODA_EXTENSION_DATASET_ID);
				String dataSetOdaExtensionDataSourceId = dataSetElement.attributeValue(ActionExport.DATASET_ODA_EXTENSION_DATASOURCE_ID);
				String dataSetQuery = dataSetElement.attributeValue(ActionExport.DATASET_QUERY);
				
				
				//Public properties
				publicProperties = dataSourceElement.elements(ActionExport.PROPERTIE_PUBLIC);
				listPublicProperties = new Properties();

				for(Element elem : publicProperties){
					String proKey = elem.attributeValue(ActionExport.PROPERTIE_NAME);
					String proValue = elem.attributeValue(ActionExport.PROPERTIE_VALUE);
					listPublicProperties.put(proKey, proValue);
				}

				//Private properties
				privateProperties = dataSourceElement.elements(ActionExport.PROPERTIE_PRIVATE);
				listPrivateProperties = new Properties();

				for(Element elem : privateProperties){
					String proKey = elem.attributeValue(ActionExport.PROPERTIE_NAME);
					String proValue = elem.attributeValue(ActionExport.PROPERTIE_VALUE);
					listPrivateProperties.put(proKey, proValue);
				}


				//Build the data source
				DataSet dataSet = new DataSet(dataSetName,dataSetOdaExtensionDataSetID,dataSetOdaExtensionDataSourceId,listPublicProperties,listPrivateProperties,dataSetQuery,dataSource);

				Activator.getInstance().getListDataSet().add(dataSet);

				return true;
			}
		}
		
		return true;
	}
	public ArrayList<DataSource> getListDataSource() {
		return listDataSource;
	}


	public void setListDataSource(ArrayList<DataSource> listDataSource) {
		this.listDataSource = listDataSource;
	}


	public ArrayList<DataSet> getListDataSet() {
		return listDataSet;
	}


	public void setListDataSet(ArrayList<DataSet> listDataSet) {
		this.listDataSet = listDataSet;
	}


	public ArrayList<Snapshot> getListSnapshots() {
		return listSnapshots;
	}


	public void setListSnapshots(ArrayList<Snapshot> listSnapshots) {
		this.listSnapshots = listSnapshots;
	}

	public ArrayList<DataDefinition> getListDataDefinitions() {
		return listDataDefinitions;
	}

	public void setListDataDefinitions(ArrayList<DataDefinition> listDataDefinitions) {
		this.listDataDefinitions = listDataDefinitions;
	}

}
