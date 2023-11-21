package metadata.client.wizards.datasources;

import metadata.client.i18n.Messages;
import metadata.client.model.dialog.DialogError;
import metadata.client.views.ViewTree;
import metadataclient.Activator;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import bpm.metadata.layer.logical.AbstractDataSource;
import bpm.metadata.layer.logical.IDataSource;
import bpm.metadata.layer.logical.IDataStream;
import bpm.metadata.layer.physical.sql.SQLConnection;

public class TableWizard extends Wizard implements INewWizard {

	protected PageSelection selectionPage;
	protected PageTableGeneral generalPage;
	protected PageQuery queryPage;
	protected IDataSource dataSource;
	protected IDataStream table;
	
	public TableWizard(IDataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public boolean performFinish() {
		if (queryPage.isCurrentPage()){
			try {
				table = queryPage.getTable();
	
			} catch (Exception e) {
				e.printStackTrace();
				DialogError dial = new DialogError(getShell(), Messages.TableWizard_3 + ":\n" + e.getMessage());
				dial.open();
				
				return false;
			}
		}
		ViewTree v = (ViewTree)Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ViewTree.ID);
		v.reInit();
		
		
		
		return true;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {}
	
	@Override
	public boolean canFinish() {
		return true;
	}

	protected SQLConnection getConnection(){
		return (SQLConnection)((AbstractDataSource)dataSource).getConnection();
	}
	
	public void addPages() {
		generalPage = new PageTableGeneral("Table Properties"); //$NON-NLS-1$
		generalPage.setTitle(Messages.TableWizard_1); //$NON-NLS-1$
		generalPage.setDescription(Messages.TableWizard_2); //$NON-NLS-1$
		addPage(generalPage);
		
		queryPage = new PageQuery("Query"); //$NON-NLS-1$
		queryPage.setTitle(Messages.TableWizard_4); //$NON-NLS-1$
		queryPage.setDescription(Messages.TableWizard_5); //$NON-NLS-1$
		addPage(queryPage);
		
		selectionPage = new PageSelection("selection"); //$NON-NLS-1$
		selectionPage.setTitle(Messages.TableWizard_7); //$NON-NLS-1$
		selectionPage.setDescription(Messages.TableWizard_8); //$NON-NLS-1$
		addPage(selectionPage);
	}

	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		if (page == generalPage && generalPage.isFromQuery()){
			return queryPage;
		}
		else if (page == generalPage && !generalPage.isFromQuery()){
			try {
				selectionPage.createModel((AbstractDataSource)dataSource);
			} catch (Exception e) {
				e.printStackTrace();
				MessageDialog.openError(getShell(), Messages.TableWizard_9, e.getMessage()); //$NON-NLS-1$
				
			}
			return selectionPage;
		}
		return super.getNextPage(page);
	}

	public IDataStream getTable(){
		return table;
	}

}
