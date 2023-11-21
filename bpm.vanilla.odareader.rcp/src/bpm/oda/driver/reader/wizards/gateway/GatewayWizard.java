package bpm.oda.driver.reader.wizards.gateway;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.Calendar;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;

import bpm.gateway.core.DataStream;
import bpm.gateway.core.DocumentGateway;
import bpm.gateway.core.manager.ResourceManager;
import bpm.gateway.core.server.file.FileSystemServer;
import bpm.gateway.core.transformations.inputs.OdaInput;
import bpm.gateway.core.transformations.outputs.FileOutputCSV;
import bpm.gateway.core.transformations.outputs.FileOutputXLS;
import bpm.gateway.core.transformations.outputs.FileOutputXML;
import bpm.oda.driver.reader.Activator;
import bpm.oda.driver.reader.model.dataset.DataSet;
import bpm.oda.driver.reader.model.datasource.DataSource;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;

public class GatewayWizard extends Wizard{
	private StoragePage  storagePage;
	private TargetPage targetPage;
	private GatewayBean bean;
	private DataSet dataSet;
	
	public GatewayWizard(DataSet dataSet){
		this.dataSet = dataSet;
	}
	@Override
	public void addPages() {
		bean = new GatewayBean();
		
		targetPage = new TargetPage("Target", bean);
		targetPage.setTitle("Gateway Target Step");
		targetPage.setDescription("Define the target step of the gateway model");
		addPage(targetPage);
		
		storagePage = new StoragePage("Storage", bean);
		storagePage.setTitle("Gateway Destination");
		storagePage.setDescription("Define the destination for teh Gateway model document");
		
		addPage(storagePage);
	}
	
	@Override
	public boolean performFinish() {
		DocumentGateway doc = createGateway();
		
		if (bean.getStorageDestinationType().equals(StoragePage.DESTINATIONS[0])){
			try{
				StringBuffer fName = new StringBuffer();
				fName.append(bean.getStorageDestinattionFolder());
				fName.append("/");
				fName.append(bean.getStorageDestinationName());
				
				if (!fName.toString().endsWith(".gateway")){
					fName.append(".gateway");
				}
				FileOutputStream fos = new FileOutputStream(fName.toString());
				doc.checkWellFormed();
				doc.write(fos);
				fos.close();
				
				MessageDialog.openInformation(getShell(), "Gateway Wizard", "Gateway model saved in " + fName);
				
				return true;
			}catch(Exception ex){
				ex.printStackTrace();
				MessageDialog.openError(getShell(), "Problem", "Unable to generate BigFile : " + ex.getMessage());
			}
			
		}
		else{
			RepositoryDirectory directory = (RepositoryDirectory)bean.getStorageDestinattionFolder();
			
			IRepositoryApi sock = storagePage.getRepositorySocket();
			
			if (sock == null){
				MessageDialog.openWarning(getShell(), "Unable to finish", "The Repository Connection is not defined");
				return false;
			}
			
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			try{
				doc.write(os);
				os.close();
			}catch(Exception ex){
				ex.printStackTrace();
				MessageDialog.openError(getShell(), "Error", "An error occured when generating Gateway XML : " + ex.getMessage());
				return false;
			}
			
			try{
				sock.getRepositoryService().addDirectoryItemWithDisplay(
						IRepositoryApi.GTW_TYPE, -1, directory, bean.getStorageDestinationName(), 
						"", "", "", os.toString(), true);
				
			
				
				MessageDialog.openInformation(getShell(), "Gateway Wizard", "Gateway model saved on the repository");
				return true;
			}catch(Exception ex){
				ex.printStackTrace();
				MessageDialog.openError(getShell(), "Error", "An error occured when saving Gateway on the Repository: " + ex.getMessage());
				return false;
			}
			
			
		}
		
		
		return false;
	}
	
	private DocumentGateway createGateway(){
		DataSource dataSource = Activator.getInstance().getDataSource(dataSet);
		
		DocumentGateway model = new DocumentGateway();
		model.setAuthor(Activator.PLUGIN_ID);
		model.setCreationDate(Calendar.getInstance().getTime());
		model.setName("Extract from Oda to File");
		
		
		
		OdaInput odaStep = new OdaInput();
		odaStep.setName("Extract Data");
		odaStep.setPositionX(50);
		odaStep.setPositionY(50);
		odaStep.setOdaExtensionDataSourceId(dataSource.getOdaExtensionDataSourceId());
		odaStep.setOdaExtensionId(dataSource.getOdaExtensionId());
		odaStep.setTemporaryFilename(ResourceManager.getInstance().getVariable(ResourceManager.VAR_GATEWAY_TEMP).getOuputName() + "/extract" );
		
		odaStep.setDatasourcePrivateProperties(dataSource.getPrivateProperties());
		odaStep.setDatasourcePublicProperties(dataSource.getPublicProperties());
		odaStep.setDatasetPublicProperties(dataSet.getPublicProperties());
		odaStep.setDatasetPrivateProperties(dataSet.getPrivateProperties());
		odaStep.setQueryText(dataSet.getQueryText());
		
		model.addTransformation(odaStep);
		
		StringBuffer fName = new StringBuffer();

		fName.append(bean.getStepOutputFileName());
		
		DataStream fileStep = null;
		
		if (bean.getStepOutputType().equals(TargetPage.OUTPUT_TYPE[0])){
			fileStep = new FileOutputCSV();
			((FileOutputCSV)fileStep).setDelete(bean.getDeleteFile());
			fName.append(".csv");
		}
		else if (bean.getStepOutputType().equals(TargetPage.OUTPUT_TYPE[1])){
			fileStep = new FileOutputXLS();
			((FileOutputXLS)fileStep).setDelete(bean.getDeleteFile());
			fName.append(".xls");
		}
		else if (bean.getStepOutputType().equals(TargetPage.OUTPUT_TYPE[2])){
			fileStep = new FileOutputXML();
			((FileOutputXML)fileStep).setDelete(bean.getDeleteFile());
			fName.append(".xml");
		}
		fileStep.setName("Write in File");
		fileStep.setTemporaryFilename(ResourceManager.getInstance().getVariable(ResourceManager.VAR_GATEWAY_TEMP).getOuputName() + "/writed" );
		fileStep.setPositionX(250);
		fileStep.setPositionY(50);
		fileStep.setServer(FileSystemServer.getInstance());
		
		fileStep.setDefinition(fName.toString());
		
		model.addTransformation(fileStep);
		
		odaStep.addOutput(fileStep);
		
		try {
			fileStep.addInput(odaStep);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return model;
	}

}
