package metadata.client.wizards.business.table;

import metadata.client.i18n.Messages;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import bpm.metadata.layer.business.AbstractBusinessTable;
import bpm.metadata.layer.business.BusinessModel;
import bpm.metadata.layer.business.IBusinessTable;
import bpm.metadata.layer.business.SQLBusinessTable;
import bpm.metadata.layer.business.UnitedOlapBusinessTable;
import bpm.metadata.layer.logical.IDataStreamElement;

public class BusinessTableWizard extends Wizard implements INewWizard {

	private IBusinessTable table;
	private PageTable tablePage;
	private BusinessModel model;
	private boolean edit = false;
	
	public BusinessTableWizard(){
	}
	
	public BusinessTableWizard(BusinessModel model) {
		this.model = model;
	}
	
	public BusinessTableWizard(BusinessModel model, AbstractBusinessTable table, boolean edit) {
		this.table = table;
		this.model = model;
		this.edit = edit;
	}

	@Override
	public boolean performFinish() {
		
		
		if (!tablePage.isOlap){
			if (table == null){
				table = new SQLBusinessTable(Messages.BusinessTableWizard_0); //$NON-NLS-1$
			}
			((SQLBusinessTable)table).removeAll();
			for(Object e : tablePage.elements){
				((SQLBusinessTable)table).addColumn((IDataStreamElement)e);
				
			}
		}
		
		else{
			if (table == null){
				table = new UnitedOlapBusinessTable();
			}
			((UnitedOlapBusinessTable)table).removeAll();
			for(Object e : tablePage.elements){
				((UnitedOlapBusinessTable)table).addColumn((IDataStreamElement)e);
				
			}
		}
		
		table.setName(tablePage.getTableName());
		
		if(!edit && model != null && model.getBusinessTable(table.getName()) != null){
			MessageDialog.openWarning(getShell(), Messages.BusinessTableWizard_1, Messages.BusinessTableWizard_2); //$NON-NLS-1$ //$NON-NLS-2$
			return false;
		}
				
		
		return true;
	}


	public void init(IWorkbench workbench, IStructuredSelection selection) {}

	@Override
	public void addPages() {
		tablePage = new PageTable("Table"); //$NON-NLS-1$
		tablePage.setTitle(Messages.BusinessTableWizard_4); //$NON-NLS-1$
		tablePage.setDescription(Messages.BusinessTableWizard_5); //$NON-NLS-1$
		addPage(tablePage);
	}
	
	public IBusinessTable getBusinessTable(){
		return table;
	}
}
