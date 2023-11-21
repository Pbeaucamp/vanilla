package metadata.client.wizards.datasources;

import java.util.ArrayList;
import java.util.List;

import metadata.client.helper.GroupHelper;
import metadata.client.i18n.Messages;
import metadataclient.Activator;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import bpm.metadata.layer.logical.AbstractDataSource;
import bpm.metadata.layer.logical.olap.UnitedOlapFactoryDatasource;

public class DataSourceWizard extends Wizard implements INewWizard {
	

	protected PageConnection connectionPage;
	protected PageSelection selectionPage;
	protected PageProperties propertiesPage;
	protected PageRelations relationsPage;
	protected PageType typePage;
	protected PageOlap olapPage;
	protected List<String> groups = new ArrayList<String>();
	protected AbstractDataSource dataSource;
	
	public DataSourceWizard() {
		groups.addAll(GroupHelper.getGroups(0, 0));
	}

	@Override
	public boolean performFinish() {
		try {
			if (olapPage.isCurrentPage()){
				dataSource = UnitedOlapFactoryDatasource.createUnitedOlapDatasource(olapPage.getOlapConnection());
				dataSource.setName(olapPage.getName());
			}
			
			
			return true;
		} catch (Exception e) {
			MessageDialog.openError(getShell(), Messages.DataSourceWizard_0, e.getMessage()); //$NON-NLS-1$
			Activator.getLogger().error(e.getMessage(), e);
			e.printStackTrace();
			return false;
		}
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {}


	public void addPages() {
		
		typePage = new PageType("Connection"); //$NON-NLS-1$
		typePage.setTitle(Messages.DataSourceWizard_2); //$NON-NLS-1$
		typePage.setDescription(Messages.DataSourceWizard_3); //$NON-NLS-1$
		addPage(typePage);
		
		olapPage = new PageOlap("Olap"); //$NON-NLS-1$
		olapPage.setTitle(Messages.DataSourceWizard_5); //$NON-NLS-1$
		olapPage.setDescription(Messages.DataSourceWizard_6); //$NON-NLS-1$
		addPage(olapPage);
		
		connectionPage = new PageConnection("Connection"); //$NON-NLS-1$
		connectionPage.setTitle(Messages.DataSourceWizard_8); //$NON-NLS-1$
		connectionPage.setDescription(Messages.DataSourceWizard_9); //$NON-NLS-1$
		addPage(connectionPage);
		
		selectionPage = new PageSelection("selection"); //$NON-NLS-1$
		selectionPage.setTitle(Messages.DataSourceWizard_11); //$NON-NLS-1$
		selectionPage.setDescription(Messages.DataSourceWizard_12); //$NON-NLS-1$
		addPage(selectionPage);
		
		
		propertiesPage = new PageProperties("properties"); //$NON-NLS-1$
		propertiesPage.setTitle(Messages.DataSourceWizard_14); //$NON-NLS-1$
		propertiesPage.setDescription(Messages.DataSourceWizard_15); //$NON-NLS-1$
		addPage(propertiesPage);
		
		relationsPage = new PageRelations("relations"); //$NON-NLS-1$
		relationsPage.setTitle(Messages.DataSourceWizard_17); //$NON-NLS-1$
		relationsPage.setDescription(Messages.DataSourceWizard_18); //$NON-NLS-1$
		addPage(relationsPage);
	}
	
	public AbstractDataSource getDataSource(){
		return dataSource;
	}

	@Override
	public boolean canFinish() {
		if (getContainer().getCurrentPage() == relationsPage ){
			return true;
		}
		else if (getContainer().getCurrentPage() == olapPage){
			return olapPage.isPageComplete();
		}
		return super.canFinish();
	}

	

	
}
