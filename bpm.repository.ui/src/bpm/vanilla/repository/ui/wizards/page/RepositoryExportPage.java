package bpm.vanilla.repository.ui.wizards.page;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Properties;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.services.ISourceProviderService;

import bpm.repository.ui.SessionSourceProvider;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.IVanillaSecurityManager;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.repository.Comment;
import bpm.vanilla.platform.core.repository.Repository;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.platform.core.repository.SecuredCommentObject;
import bpm.vanilla.platform.core.utils.IOWriter;
import bpm.vanilla.repository.ui.Activator;
import bpm.vanilla.repository.ui.Messages;
import bpm.vanilla.repository.ui.dialogs.DialogNewDirectory;
import bpm.vanilla.repository.ui.dialogs.DialogNewItem;
import bpm.vanilla.repository.ui.dialogs.DialogShowDependencies;
import bpm.vanilla.repository.ui.icons.IconsRegistry;
import bpm.vanilla.repository.ui.versionning.VersionningManager;
import bpm.vanilla.repository.ui.viewers.RepositoryViewerFilter;

public class RepositoryExportPage extends RepositoryContentBasePage{

	public static final int UPDATE_BUTTON_ID = 1;
	public static final int CHECKOUT_BUTTON_ID = 2;
	public static final int SHARE_BUTTON_ID = 3;
	
	protected ToolItem newObject, delete, newDirectory, update, loadModel;
	protected ToolItem checkout, share, showDep;

	protected ExportObjectWizard wizard;
	
	public RepositoryExportPage(String pageName) {
		super(pageName);
	}
	
	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		
		if (Activator.getDefault().getDesignerActivator() != null && Activator.getDefault().getDesignerActivator().getRepositoryConnection() != null){
			try {
				compositeRepository.addViewerFilter(new RepositoryViewerFilter(new int[]{Activator.getDefault().getDesignerActivator().getRepositoryItemType()}, new int[]{}));
				fillContent(Activator.getDefault().getDesignerActivator().getRepositoryContext(), Activator.getDefault().getDesignerActivator().getRepositoryConnection());
				((ExportObjectWizard)getWizard()).setRepositoryConnection(Activator.getDefault().getDesignerActivator().getRepositoryConnection());
				
				IRepositoryContext ctx = Activator.getDefault().getDesignerActivator().getRepositoryContext();
				IVanillaContext vanillaCtx = new BaseVanillaContext(ctx.getVanillaContext().getVanillaUrl(), 
						ctx.getVanillaContext().getLogin(), ctx.getVanillaContext().getPassword());
				
				((ExportObjectWizard)getWizard()).setVanillaApi(new RemoteVanillaPlatform(vanillaCtx));	
			} catch (Exception e) {
				e.printStackTrace();
				MessageDialog.openError(getShell(), Messages.RepositoryExportPage_0, Messages.RepositoryExportPage_1 + e.getMessage());
			}
		}
		
	}
	
	

	
	protected void attachButtonsListeners(){
		
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
							MessageDialog.openInformation(getShell(), Messages.RepositoryExportPage_5, Messages.RepositoryExportPage_6);
						}
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				
			}
		});
		
		newDirectory.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				IRepositoryApi sock = wizard.getRepositoryConnection();
				IVanillaSecurityManager mng = wizard.getVanillaApi().getVanillaSecurityManager();
				
				try {
					
					DialogNewDirectory dial = new DialogNewDirectory(getShell(), mng.getGroups());
					
					if (dial.open() != DialogNewItem.OK){
						return;
					}
					RepositoryDirectory target = null;
					if (!compositeRepository.getSelectedItems().isEmpty()){
						target = (RepositoryDirectory)compositeRepository.getSelectedItems().get(0);
					}
					Properties p = dial.getProperties();
					RepositoryDirectory newDir = sock.getRepositoryService().addDirectory( p.getProperty(DialogNewDirectory.P_NAME), p.getProperty(DialogNewDirectory.P_DESCRIPTION), target);

					((Repository)compositeRepository.getInput()).add(newDir);
					compositeRepository.refresh();
				} catch (Exception e1) {
					e1.printStackTrace();
					MessageDialog.openError(getShell(), Messages.RepositoryExportPage_2, Messages.RepositoryExportPage_3 + e1.getMessage());
				}
				
				
			}
		});
		
		newObject.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				IRepositoryApi sock = wizard.getRepositoryConnection();
				IVanillaSecurityManager mng = wizard.getVanillaApi().getVanillaSecurityManager();
				
				try {
					DialogNewItem dial = new DialogNewItem(getShell(), mng.getGroups());
					
					if (dial.open() != DialogNewItem.OK){
						return;
					}
					RepositoryDirectory target = (RepositoryDirectory)compositeRepository.getSelectedItems().get(0);
					Properties p = dial.getProperties();
					
					String modelXml = wizard.getDesignerActivator().getCurrentModelXml();
					
					String[] groupNames = p.getProperty(DialogNewItem.P_GROUP).split(";"); //$NON-NLS-1$
					
					RepositoryItem newItem = sock.getRepositoryService().addDirectoryItemWithDisplay(
							Activator.getDefault().getDesignerActivator().getRepositoryItemType(), 
							-1,
							target, 
							p.getProperty(DialogNewItem.P_NAME), 
							p.getProperty(DialogNewItem.P_DESCRIPTION), 
							"", 
							"", 
							modelXml,
							true); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					
					compositeRepository.getInput().add(newItem);
					compositeRepository.refresh();
					
					try{
						ISourceProviderService service = (ISourceProviderService) Activator.getDefault().getWorkbench()
						.getActiveWorkbenchWindow().getService(
								ISourceProviderService.class);
						SessionSourceProvider pro =  (SessionSourceProvider) service.getSourceProvider(SessionSourceProvider.CONNECTION_STATE);
						pro.setDirectoryItemId(newItem.getId());
						
						wizard.getDesignerActivator().setCurrentDirectoryItemId(newItem.getId());
//						pro.setContext(context)
					}catch(Exception ex){
						
					}
					/*
					 * create access and run for all groups
					 */
					
					StringBuilder errorSecu = new StringBuilder();
					
					for(String s : groupNames){
						
						try{
							Integer groupId = null;
							
							try{
								groupId = mng.getGroupByName(s).getId();
							}catch(Exception ex){
								throw new Exception(Messages.RepositoryExportPage_4 + s);
							}
							
							sock.getAdminService().addGroupForItem(groupId, newItem.getId());

							Group dummyGroup = new Group();
							dummyGroup.setId(groupId);
							sock.getAdminService().setObjectRunnableForGroup(dummyGroup.getId(), newItem);
							
							SecuredCommentObject secComment = new SecuredCommentObject();
							secComment.setGroupId(groupId);
							secComment.setObjectId(newItem.getId());
							secComment.setType(Comment.ITEM);
							sock.getDocumentationService().addSecuredCommentObject(secComment);
						}catch(Exception ex2){
							ex2.printStackTrace();
							errorSecu.append(Messages.RepositoryExportPage_9 + s + " : " + ex2.getMessage() + "\n");  //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
						}
						
						
					}
					
					if (errorSecu.toString().length() > 0){
						throw new Exception(Messages.RepositoryExportPage_12 + errorSecu.toString());
					}
					
				} catch (Exception e1) {
					e1.printStackTrace();
					MessageDialog.openError(getShell(), Messages.RepositoryExportPage_13, Messages.RepositoryExportPage_14 + e1.getMessage());
				}
				
				if (shouldCloseWizardOnImport()){
					if (getWizard().performFinish()){
						getContainer().getShell().close();
					}
				}
			}
		});
		
		delete.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				IRepositoryApi sock = wizard.getRepositoryConnection();
				
				Object target = compositeRepository.getSelectedItems().get(0);
				
				if (target instanceof RepositoryDirectory){
					try {
						
						sock.getRepositoryService().delete((RepositoryDirectory)target);
						compositeRepository.getInput().remove((RepositoryDirectory)target);
						compositeRepository.refresh();
					} catch (Exception e1) {
						e1.printStackTrace();
						MessageDialog.openError(getShell(), Messages.RepositoryExportPage_15, Messages.RepositoryExportPage_16 + e1.getMessage());
					}
				}
				else if (target instanceof RepositoryItem){
					try {
						
						DialogShowDependencies dial = new DialogShowDependencies(getShell(), ((RepositoryItem)target), sock, DialogShowDependencies.DELETE_TITLE);
						if(dial.hasDependencies()) {
							if(dial.open() != Dialog.OK) {
								return;
							}
							
						}
						sock.getRepositoryService().delete((RepositoryItem)target);
						compositeRepository.getInput().remove((RepositoryItem)target);
						compositeRepository.refresh();
					} catch (Exception e1) {
						e1.printStackTrace();
						MessageDialog.openError(getShell(), Messages.RepositoryExportPage_17, Messages.RepositoryExportPage_18 + e1.getMessage());
					}
				}
				
			}
		});
		
		update.addSelectionListener(new SelectionAdapter(){
			@Override
				public void widgetSelected(SelectionEvent e) {
					IRepositoryApi sock = Activator.getDefault().getDesignerActivator().getRepositoryConnection();
					try {
						
						DialogShowDependencies dial = new DialogShowDependencies(getShell(), (RepositoryItem)compositeRepository.getSelectedItems().get(0), sock, DialogShowDependencies.UPDATE_TITLE);
						if(dial.hasDependencies()) {
							if(dial.open() != Dialog.OK) {
								return;
							}
						}
						sock.getRepositoryService().updateModel((RepositoryItem)compositeRepository.getSelectedItems().get(0), Activator.getDefault().getDesignerActivator().getCurrentModelXml());
						MessageDialog.openInformation(getShell(), Messages.RepositoryExportPage_19, Messages.RepositoryExportPage_20);
						
					} catch (Exception e1) {
						e1.printStackTrace();
						MessageDialog.openInformation(getShell(), Messages.RepositoryExportPage_21, Messages.RepositoryExportPage_22 + e1.getMessage()) ;
					}
					
					if (shouldCloseWizardOnImport()){
						if (getWizard().performFinish()){
							getContainer().getShell().close();
						}
					}
				}	
			});
		
		loadModel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String modelXml = null;
				try{
					modelXml = wizard.getRepositoryConnection().getRepositoryService().loadModel((RepositoryItem)compositeRepository.getSelectedItems().get(0));
				}catch(Exception ex){
					ex.printStackTrace();
					MessageDialog.openError(getShell(), Messages.RepositoryExportPage_23, Messages.RepositoryExportPage_24 + ex.getMessage());
					return;
				}
				
				try{
					wizard.getDesignerActivator().setCurrentModel(modelXml,  ((RepositoryItem)compositeRepository.getSelectedItems().get(0)).getId());
				}catch(Exception ex){
					ex.printStackTrace();
					MessageDialog.openError(getShell(), Messages.RepositoryExportPage_25, Messages.RepositoryExportPage_26 + ex.getMessage());
				}
				
				if (shouldCloseWizardOnImport()){
					if (getWizard().performFinish()){
						getContainer().getShell().close();
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
						MessageDialog.openInformation(getShell(), Messages.RepositoryExportPage_27, Messages.RepositoryExportPage_28);
					}catch(Exception ex){
						ex.printStackTrace();
						MessageDialog.openError(getShell(), Messages.RepositoryExportPage_29, Messages.RepositoryExportPage_30 + ex.getMessage());
					}
				}
			}
		});
		
		checkout.addSelectionListener(new SelectionAdapter(){
			 @Override
			public void widgetSelected(SelectionEvent e) {
				 Object o = compositeRepository.getSelectedItems().get(0);
					
				if (o instanceof RepositoryItem){
					IRepositoryApi sock = wizard.getRepositoryConnection();
					
					String xml = null;
					try{
						InputStream is = sock.getVersioningService().checkOut((RepositoryItem)o);
						ByteArrayOutputStream bos = new ByteArrayOutputStream();
						IOWriter.write(is, bos, true, true);
						xml = bos.toString("UTF-8");
					}catch(Exception ex){
						ex.printStackTrace();
						MessageDialog.openError(getShell(), Messages.RepositoryExportPage_31, Messages.RepositoryExportPage_32 + ex.getMessage());
						return;
					}
					
					Object oldModel = Activator.getDefault().getDesignerActivator().getCurrentModel();
					
					try{
						Activator.getDefault().getDesignerActivator().setCurrentModel(xml, ((RepositoryItem)o).getId());
					}catch(Exception ex){
						ex.printStackTrace();
						MessageDialog.openError(getShell(), Messages.RepositoryExportPage_33, Messages.RepositoryExportPage_34 + ex.getMessage());
						return;
					}
					
					Object saveResult = null;
					try{
						saveResult = Activator.getDefault().getDesignerActivator().saveCurrentModel();
					}catch(Exception ex){
						ex.printStackTrace();
						MessageDialog.openError(getShell(), Messages.RepositoryExportPage_35, Messages.RepositoryExportPage_36 + ex.getMessage());
						return;
					}
					if (saveResult == null){
						Activator.getDefault().getDesignerActivator().setCurrentModel(oldModel);
						MessageDialog.openError(getShell(), Messages.RepositoryExportPage_37, Messages.RepositoryExportPage_38);
						return;
					}
					
					if (saveResult instanceof File){
						VersionningManager.getInstance().saveChekout(((File)saveResult).getAbsolutePath(), sock, (RepositoryItem)o);
					}
					else{
						MessageDialog.openError(getShell(), Messages.RepositoryExportPage_39, Messages.RepositoryExportPage_40 );
					}

				}
			}
		});
	
	
		//to allow load on doubleclik
		compositeRepository.getViewer().addDoubleClickListener(new IDoubleClickListener() {
			
			@Override
			public void doubleClick(DoubleClickEvent event) {
				if (loadModel.getEnabled()){
					loadModel.notifyListeners(SWT.Selection, new Event());
				}
				
			}
		});
	}
	
	
	@Override
	protected void fillToolbar(ToolBar bar) {
		newDirectory = new ToolItem(bar, SWT.NONE);
		newDirectory.setText(Messages.RepositoryExportPage_41);
		newDirectory.setToolTipText(Messages.RepositoryExportPage_42);
		newDirectory.setImage(Activator.getDefault().getImageRegistry().get(IconsRegistry.FOLDER));
		
		
		newObject = new ToolItem(bar, SWT.NONE);
		newObject.setText(Messages.RepositoryExportPage_43);
		newObject.setToolTipText(Messages.RepositoryExportPage_44);
		newObject.setImage(Activator.getDefault().getImageRegistry().get(IconsRegistry.ADD));
		
	
		delete = new ToolItem(bar, SWT.NONE);
		delete.setText(Messages.RepositoryExportPage_45);
		delete.setToolTipText(Messages.RepositoryExportPage_46);
		delete.setImage(Activator.getDefault().getImageRegistry().get(IconsRegistry.DEL));
		
	
		update = new ToolItem(bar, SWT.NONE);
		update.setText(Messages.RepositoryExportPage_47);
		update.setImage(Activator.getDefault().getImageRegistry().get(IconsRegistry.UPDATE));
		update.setToolTipText(Messages.RepositoryExportPage_48);
		update.setEnabled(false);
		
		
		loadModel = new ToolItem(bar, SWT.NONE);
		loadModel.setText(Messages.RepositoryExportPage_49);
		loadModel.setToolTipText(Messages.RepositoryExportPage_50);
		loadModel.setEnabled(false);
		loadModel.setImage(Activator.getDefault().getImageRegistry().get(IconsRegistry.IMPORT));
		
		
		
		share = new ToolItem(bar, SWT.NONE);
		share.setText(Messages.RepositoryExportPage_51);
		share.setToolTipText(Messages.RepositoryExportPage_52);
		share.setEnabled(false);
		share.setImage(Activator.getDefault().getImageRegistry().get(IconsRegistry.SHARE));
		
		
		checkout = new ToolItem(bar, SWT.NONE);
		checkout.setText(Messages.RepositoryExportPage_53);
		checkout.setToolTipText(Messages.RepositoryExportPage_54);
		checkout.setEnabled(false);
		checkout.setImage(Activator.getDefault().getImageRegistry().get(IconsRegistry.CHECKOUT));
		
		showDep = new ToolItem(bar, SWT.NONE);
		showDep.setText(Messages.RepositoryExportPage_8);
		showDep.setToolTipText(Messages.RepositoryExportPage_10);
		showDep.setEnabled(false);
		showDep.setImage(Activator.getDefault().getImageRegistry().get(IconsRegistry.DEPENDENCIES));
		
		compositeRepository.addSelectionChangedListener(new ISelectionChangedListener() {
			
			public void selectionChanged(SelectionChangedEvent event) {
				
				Object o = ((IStructuredSelection)event.getSelection()).getFirstElement();
				if (o instanceof RepositoryItem){
					loadModel.setEnabled(true);
					if (wizard.getDesignerActivator().getCurrentModelDirectoryItemId() == null || wizard.getDesignerActivator().getCurrentModelDirectoryItemId() <= 0) {
						update.setEnabled(false);
					}
					else{
						if (((RepositoryItem)o).getId() == wizard.getDesignerActivator().getCurrentModelDirectoryItemId()){
							update.setEnabled(true);
						}
						else{
							update.setEnabled(false);
						}
					}
					share.setEnabled(true);
					checkout.setEnabled(true);
					showDep.setEnabled(true);
				}
				else{
					update.setEnabled(false);
					loadModel.setEnabled(false);
					share.setEnabled(false);
					checkout.setEnabled(false);
					showDep.setEnabled(false);
				}
				
				newObject.setEnabled((o instanceof RepositoryDirectory ) && Activator.getDefault().getDesignerActivator().getCurrentModel() != null);
			}
		});
	
		attachButtonsListeners();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.WizardPage#setWizard(org.eclipse.jface.wizard.IWizard)
	 */
	@Override
	public void setWizard(IWizard newWizard) {
		super.setWizard(newWizard);
		if (newWizard instanceof ExportObjectWizard){
			wizard = (ExportObjectWizard)newWizard;
		}
	}
	

	@Override
	public boolean isPageComplete() {
		return true;
	}
	
	
}
