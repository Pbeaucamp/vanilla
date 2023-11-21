package bpm.gateway.ui.dialogs.database.wizard.transfert;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import bpm.gateway.core.DocumentGateway;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.transformations.inputs.FMDTInput;
import bpm.gateway.core.transformations.outputs.FileOutputCSV;
import bpm.gateway.ui.Activator;
import bpm.gateway.ui.editors.GatewayEditorInput;
import bpm.gateway.ui.editors.GatewayEditorPart;
import bpm.gateway.ui.gatewaywizard.GatewayInformationPage;
import bpm.gateway.ui.i18n.Messages;
import bpm.metadata.layer.business.IBusinessTable;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.query.QuerySql;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class FmdtTransfertWizard extends Wizard {

	public static final String PROP_ITEM_ID = "repositoryItemId"; //$NON-NLS-1$
	public static final String PROP_BU_MODEL = "businessModel"; //$NON-NLS-1$
	public static final String PROP_BU_PACKAGE = "businessPackage"; //$NON-NLS-1$
	public static final String PROP_CONNECTION_NAME = "connectionName"; //$NON-NLS-1$
	
	public static final String PROP_OUTPUT_FILE = "outputFile"; //$NON-NLS-1$
	public static final String PROP_OUTPUT_ENCODING = "outputEncoding"; //$NON-NLS-1$
	public static final String PROP_DELETE_FILE = "deleteFile"; //$NON-NLS-1$
	
	private static final String SELECTION_PAGE_TITLE = Messages.DBMigrationWizard_0;
	private static final String SELECTION_PAGE_DESCRIPTION = Messages.DBMigrationWizard_1;
	
	private static final String INFORMATION_PAGE_NAME = Messages.DBMigrationWizard_5;
	private static final String INFORMATION_PAGE_DESCRIPTION = Messages.DBMigrationWizard_6;
	private static final String INFORMATION_PAGE_TITLE = Messages.DBMigrationWizard_7;

	private GatewayInformationPage docPage;
	private FmdtTransfertPage definitionPage;

	@Override
	public boolean performFinish() {
		DocumentGateway doc = new DocumentGateway();
		
		IRepositoryContext ctx = Activator.getDefault().getRepositoryContext();
		
		Properties docProp = docPage.getProperties();
		doc.setAuthor(docProp.getProperty("author")); //$NON-NLS-1$
		doc.setDescription(docProp.getProperty("description")); //$NON-NLS-1$
		doc.setName(docProp.getProperty("name")); //$NON-NLS-1$
		doc.setMode(docProp.getProperty("mode")); //$NON-NLS-1$
		doc.setRepositoryContext(ctx);
		
		try {
			Properties pInput = definitionPage.getInputProperties();
			Properties pOutput = definitionPage.getOuputProperties();
			List<IBusinessTable> businessTables = definitionPage.getSelectedTables();
			RepositoryItem item = definitionPage.getSelectedItem();
			
			buildTransformations(doc, pInput, pOutput, businessTables, item);
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(getShell(), Messages.FmdtTransfertWizard_7, e.getMessage());
			return false;
		}
			
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		try {
			page.openEditor(new GatewayEditorInput(docProp.getProperty("fileName"), doc), GatewayEditorPart.ID, false); //$NON-NLS-1$
			return true;
		} catch (PartInitException e) {
			e.printStackTrace();
		}
		
		return false;
	}

	@Override
	public boolean canFinish() {
		for(IWizardPage p : getPages()){
			if (!p.isPageComplete()){
				return false;
			}
		}
		return true;
	}
	
	@Override
	public void addPages() {
		docPage = new GatewayInformationPage(INFORMATION_PAGE_NAME);
		docPage.setDescription(INFORMATION_PAGE_DESCRIPTION);
		docPage.setTitle(INFORMATION_PAGE_TITLE);
		addPage(docPage);
		
		definitionPage = new FmdtTransfertPage(SELECTION_PAGE_TITLE);
		definitionPage.setDescription(SELECTION_PAGE_DESCRIPTION);
		definitionPage.setTitle(SELECTION_PAGE_TITLE);
		addPage(definitionPage);
	}

	private void buildTransformations(DocumentGateway doc, Properties pInput, Properties pOutput, 
			List<IBusinessTable> businessTables, RepositoryItem selectedItem){
		
		List<Transformation> l = new ArrayList<Transformation>();
		int j = 1;
		
		for(IBusinessTable bt : businessTables){
			FMDTInput fmdtInput = new FMDTInput();
			fmdtInput.setName(Messages.FmdtTransfertWizard_0 + bt.getName());
			fmdtInput.setDocumentGateway(doc);
			fmdtInput.setRepositoryItemId(pInput.getProperty(PROP_ITEM_ID));
			fmdtInput.setBusinessModelName(pInput.getProperty(PROP_BU_MODEL));
			fmdtInput.setBusinessPackageName(pInput.getProperty(PROP_BU_PACKAGE));
			fmdtInput.setConnectionName(pInput.getProperty(PROP_CONNECTION_NAME));
			fmdtInput.setRepositoryItem(selectedItem);
			QuerySql queryFmdt = new QuerySql();
			for(IDataStreamElement el : bt.getColumns("none")){ //$NON-NLS-1$
				queryFmdt.getSelect().add(el);
			}
			fmdtInput.setDefinition(queryFmdt.getXml());
			fmdtInput.setPositionX(50);
			fmdtInput.setPositionY(j * 100);
			l.add(fmdtInput);
			
			FileOutputCSV csvOutput = new FileOutputCSV();
			csvOutput.setName(Messages.FmdtTransfertWizard_1 + bt.getName());
			csvOutput.setDocumentGateway(doc);
			csvOutput.setDefinition(pOutput.getProperty(PROP_OUTPUT_FILE) + File.separator + bt.getName().trim() + ".csv"); //$NON-NLS-1$
			csvOutput.setEncoding(pOutput.getProperty(PROP_OUTPUT_ENCODING)); //$NON-NLS-1$
			csvOutput.setDelete(pOutput.getProperty(PROP_DELETE_FILE));
			csvOutput.setPositionX(250);
			csvOutput.setPositionY(j * 100);
			l.add(csvOutput);

			try {
				doc.addTransformation(fmdtInput);
				doc.addTransformation(csvOutput);
				
				fmdtInput.initDescriptor();
				csvOutput.initDescriptor();
				
				csvOutput.addInput(fmdtInput);
				fmdtInput.addOutput(csvOutput);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			j++;
		}
	}
}
