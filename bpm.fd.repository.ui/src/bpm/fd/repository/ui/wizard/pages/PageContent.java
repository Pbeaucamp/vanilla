package bpm.fd.repository.ui.wizard.pages;

import java.io.File;
import java.util.Properties;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Event;

import bpm.fd.api.core.model.FdModel;
import bpm.fd.api.core.model.FdProject;
import bpm.fd.api.core.model.MultiPageFdProject;
import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.editors.FdEditor;
import bpm.fd.design.ui.editors.FdProjectEditorInput;
import bpm.fd.repository.ui.wizard.actions.ActionCheckoutFdProject;
import bpm.fd.repository.ui.wizard.actions.ActionCreateFdProject;
import bpm.fd.repository.ui.wizard.actions.ActionLoadFdProject;
import bpm.fd.repository.ui.wizard.actions.ActionUpdateProject;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IVanillaSecurityManager;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.repository.ui.dialogs.DialogNewDirectory;
import bpm.vanilla.repository.ui.dialogs.DialogNewItem;
import bpm.vanilla.repository.ui.dialogs.DialogShowDependencies;
import bpm.vanilla.repository.ui.versionning.VersionningManager;
import bpm.vanilla.repository.ui.wizards.page.RepositoryExportPage;

public class PageContent extends RepositoryExportPage{

	public PageContent(String pageName) {
		super(pageName);
		
	}
	
	@Override
	protected void attachButtonsListeners() {
		loadModel.addSelectionListener(new SelectionAdapter(){

			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					
					RepositoryItem item = (RepositoryItem)compositeRepository.getSelectedItems().get(0);
					
					FdProject project = new ActionLoadFdProject(wizard.getRepositoryConnection(), item).perform();
					
					
					Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(new FdProjectEditorInput(project.getFdModel()), FdEditor.ID);
					getWizard().performFinish();
				} catch (Exception e1) {
					e1.printStackTrace();
					MessageDialog.openError(getShell(), Messages.PageContent_0, e1.getMessage());
				}
				if (shouldCloseWizardOnImport()){
					if (getWizard().performFinish()){
						getContainer().getShell().close();
					}
				}
			}
			
		});
		newObject.addSelectionListener(new SelectionAdapter(){

			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					RepositoryDirectory target = (RepositoryDirectory)compositeRepository.getSelectedItems().get(0);
					RepositoryItem it = new ActionCreateFdProject(wizard.getRepositoryConnection(), target, wizard.getVanillaApi().getVanillaSecurityManager()).perform(false);
					if (it != null){
						compositeRepository.getInput().add(it);
					}
					compositeRepository.refresh();
				} catch (Exception e1) {
					e1.printStackTrace();
					MessageDialog.openError(getShell(), Messages.PageContent_1, e1.getMessage());
				}
			}
			
		});
		
		newDirectory.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				
				try {
					IRepositoryApi sock = wizard.getRepositoryConnection();
					IVanillaSecurityManager mng = wizard.getVanillaApi().getVanillaSecurityManager();
					DialogNewDirectory dial = new DialogNewDirectory(getShell(), mng.getGroups());
					
					if (dial.open() != DialogNewItem.OK){
						return;
					}
					if (! compositeRepository.getSelectedItems().isEmpty() && !(compositeRepository.getSelectedItems().get(0) instanceof RepositoryDirectory)){
						MessageDialog.openInformation(getShell(), "Directory Creation", "You have to select a Directory Parent" );
						return;
					}
					RepositoryDirectory target = compositeRepository.getSelectedItems().isEmpty() ? null : (RepositoryDirectory)compositeRepository.getSelectedItems().get(0);
					Properties p = dial.getProperties();
					
					RepositoryDirectory i = sock.getRepositoryService().addDirectory(
							p.getProperty(DialogNewDirectory.P_NAME), 
							p.getProperty(DialogNewDirectory.P_DESCRIPTION), 
							target);
					compositeRepository.getInput().add(i);
					
					compositeRepository.refresh();
				} catch (Exception e1) {
					e1.printStackTrace();
					MessageDialog.openError(getShell(), Messages.PageContent_2, Messages.PageContent_3 + e1.getMessage());
				}
			}
			
		});
		
		update.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				new ActionUpdateProject(wizard.getRepositoryConnection(), Activator.getDefault().getProject(), compositeRepository.getInput(), wizard.getVanillaApi()).perform();

			}
		});
		
		delete.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				IRepositoryApi sock = wizard.getRepositoryConnection();
				
				Object o = compositeRepository.getSelectedItems().get(0);
				
				if(o instanceof RepositoryDirectory) {
				
					RepositoryDirectory target = (RepositoryDirectory)o;
				
					try {
						sock.getRepositoryService().delete(target);
						compositeRepository.getInput().remove(target);
						compositeRepository.refresh();
					} catch (Exception e1) {
						e1.printStackTrace();
						MessageDialog.openError(getShell(), Messages.PageContent_4, Messages.PageContent_5 + e1.getMessage());
					}
				}
				else if(o instanceof RepositoryItem) {
					RepositoryItem item = (RepositoryItem) o;
					
					try {
						
						DialogShowDependencies dial = new DialogShowDependencies(getShell(), item, sock, DialogShowDependencies.DELETE_TITLE);
						if(dial.hasDependencies()) {
							if(dial.open() == Dialog.OK) {
								sock.getRepositoryService().delete(item);
								compositeRepository.getInput().remove(item);
								compositeRepository.refresh();
							}
						}
						else {
							sock.getRepositoryService().delete(item);
							compositeRepository.getInput().remove(item);
							compositeRepository.refresh();
						}
						
					} catch (Exception e1) {
						e1.printStackTrace();
						MessageDialog.openError(getShell(), Messages.PageContent_4, Messages.PageContent_5 + e1.getMessage());
					}
				}
			
			}
			
		});
	
	
		share.addSelectionListener(new SelectionAdapter(){
			 @Override
			public void widgetSelected(SelectionEvent e) {
				Object o = compositeRepository.getSelectedItems().get(0);
					
				if (o instanceof RepositoryItem){
					IRepositoryApi sock = wizard.getRepositoryConnection();
					try{
						sock.getVersioningService().share((RepositoryItem)o);
						
						StringBuffer error = new StringBuffer();
						
						for(RepositoryItem repIt : sock.getRepositoryService().getNeededItems(((RepositoryItem)o).getId())){
							if (repIt.getType() != IRepositoryApi.FMDT_TYPE){
								try{
									sock.getVersioningService().share(repIt);
								}catch(Exception ex){
									ex.printStackTrace();
									error.append(Messages.PageContent_6 + repIt.getItemName() + "\n"); //$NON-NLS-2$
								}
							}
						} 
						
						
						if (error.toString().isEmpty()){
							MessageDialog.openInformation(getShell(), Messages.PageContent_8, Messages.PageContent_9);
						}
						else{
							MessageDialog.openWarning(getShell(), Messages.PageContent_10, Messages.PageContent_11 + error.toString());
						}
						
					}catch(Exception ex){
						ex.printStackTrace();
						MessageDialog.openError(getShell(), Messages.PageContent_12, Messages.PageContent_13 + ex.getMessage());
					}
				}
				
			}
		});
		
		checkout.addSelectionListener(new SelectionAdapter(){
			 @Override
			public void widgetSelected(SelectionEvent e) {
				 MessageDialog.openInformation(getShell(), Messages.PageContent_14, Messages.PageContent_15);
				 Object o = compositeRepository.getSelectedItems().get(0);
					
				if (o instanceof RepositoryItem){
										
					try{
						FdProject project = new ActionCheckoutFdProject(wizard.getRepositoryConnection(), (RepositoryItem)o).perform();
						
						FdProjectEditorInput editorInput = new FdProjectEditorInput(project.getFdModel());
						Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(editorInput, FdEditor.ID);
						if (project instanceof MultiPageFdProject){
							for(FdModel m : ((MultiPageFdProject)project).getPagesModels()){
								Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(new FdProjectEditorInput(m), FdEditor.ID);
							}
						}
						
						VersionningManager.getInstance().saveChekout((new File(editorInput.getModel().getProject().getProjectDescriptor().getProjectName())).getAbsolutePath(), wizard.getRepositoryConnection(), (RepositoryItem)o);
						
						
					}catch(Exception ex){
						ex.printStackTrace();
						MessageDialog.openError(getShell(), Messages.PageContent_16, Messages.PageContent_17 + ex.getMessage());
						return;
					}

					
					

				}
			}
		});
		showDep.addSelectionListener(new SelectionAdapter() {			
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					IRepositoryApi sock = wizard.getRepositoryConnection();
					RepositoryItem target = null;
					if (!compositeRepository.getSelectedItems().isEmpty()){
						target = (RepositoryItem)compositeRepository.getSelectedItems().get(0);
						
						DialogShowDependencies dial = new DialogShowDependencies(getShell(), target, sock, DialogShowDependencies.TITLE);
						if(dial.hasDependencies()) {
							dial.open();
						}
						else {
							MessageDialog.openInformation(getShell(), "No dependencies", "This item has no dependent item");
						}
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				
			}
		});
		
		//to allow load on doubleclik
		compositeRepository.getViewer().addDoubleClickListener(new IDoubleClickListener() {
			
			@Override
			public void doubleClick(DoubleClickEvent event) {
				if (loadModel.getEnabled()){
					loadModel.setSelection(true);
					loadModel.notifyListeners(SWT.Selection, new Event());
				}
				
			}
		});
	}

}
