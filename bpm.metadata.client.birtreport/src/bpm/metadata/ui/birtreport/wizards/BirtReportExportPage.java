package bpm.metadata.ui.birtreport.wizards;

import java.util.Properties;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.thoughtworks.xstream.XStream;

import bpm.fwr.api.beans.FWRReport;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IVanillaSecurityManager;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.remote.RemoteVanillaPlatform;
import bpm.vanilla.platform.core.repository.Comment;
import bpm.vanilla.platform.core.repository.Repository;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.platform.core.repository.SecuredCommentObject;
import bpm.vanilla.repository.ui.Activator;
import bpm.vanilla.repository.ui.composites.CompositeRepositoryItemSelecter;
import bpm.vanilla.repository.ui.dialogs.DialogNewDirectory;
import bpm.vanilla.repository.ui.dialogs.DialogNewItem;
import bpm.vanilla.repository.ui.icons.IconsRegistry;

public class BirtReportExportPage extends WizardPage{
	
	private IRepositoryApi sock;
	private IVanillaSecurityManager mng;
	private FWRReport report;
	
	protected CompositeRepositoryItemSelecter compositeRepository;
	
	protected ToolItem newObject, delete, newDirectory;

	public BirtReportExportPage(String pageName) {
		super(pageName);
		
		try {
			mng = new RemoteVanillaPlatform(metadataclient.Activator.getDefault().getVanillaContext()).getVanillaSecurityManager();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void createControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, false));
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		ToolBar bar = new ToolBar(main, SWT.NONE);
		bar.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		
		
		compositeRepository = new CompositeRepositoryItemSelecter(main, SWT.BORDER);
		compositeRepository.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		
		setControl(main);
		fillToolbar(bar);
	}
	
	public void fillContent(FWRReport re, IRepositoryApi s) {
		this.report = re;
		this.sock = s;
	
		try{			
			compositeRepository.setInput(new Repository(s, IRepositoryApi.FWR_TYPE));
		}catch(Exception ex){
			ex.printStackTrace();
			compositeRepository.setInput(new Repository(s));
			MessageDialog.openError(getShell(), "Error", "Unable to browse Repository.\n" + ex.getMessage());
		}
	}
	
	protected void attachButtonsListeners() {
		newDirectory.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					DialogNewDirectory dial = new DialogNewDirectory(getShell(), mng.getGroups());
					
					if (dial.open() != DialogNewItem.OK){
						return;
					}
					RepositoryDirectory target = (RepositoryDirectory)compositeRepository.getSelectedItems().get(0);
					Properties p = dial.getProperties();

					
					RepositoryDirectory i = sock.getRepositoryService().addDirectory(p.getProperty(DialogNewDirectory.P_NAME), p.getProperty(DialogNewDirectory.P_DESCRIPTION), target);
					compositeRepository.getInput().add(i);
					compositeRepository.refresh();
				} catch (Exception e1) {
					e1.printStackTrace();
					MessageDialog.openError(getShell(), "Problem", "Unable to create DirectoryItem :" + e1.getMessage());
				}
				
				
			}
		});
		
		newObject.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				try {
					
					DialogNewItem dial = new DialogNewItem(getShell(), mng.getGroups());
					
					if (dial.open() != DialogNewItem.OK){
						return;
					}
					RepositoryDirectory target = (RepositoryDirectory)compositeRepository.getSelectedItems().get(0);
					Properties p = dial.getProperties();
					
					String[] groupNames = p.getProperty(DialogNewItem.P_GROUP).split(";");
					
					XStream xstream = new XStream();
					String modelXml = xstream.toXML(report);
					
					RepositoryItem i = sock.getRepositoryService().addDirectoryItemWithDisplay(
							IRepositoryApi.FWR_TYPE, -1,
							target, 
							p.getProperty(DialogNewItem.P_NAME), 
							p.getProperty(DialogNewItem.P_DESCRIPTION), "", "", modelXml, true);
					
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
								throw new Exception("Unable to find groupId for Group " + s);
							}
							
							sock.getAdminService().addGroupForItem(groupId, i.getId());

							Group dummyGroup = new Group();
							dummyGroup.setId(groupId);
							sock.getAdminService().setObjectRunnableForGroup(dummyGroup.getId(), i);
							
							SecuredCommentObject secComment = new SecuredCommentObject();
							secComment.setGroupId(groupId);
							secComment.setObjectId(i.getId());
							secComment.setType(Comment.ITEM);
							sock.getDocumentationService().addSecuredCommentObject(secComment);
						}catch(Exception ex2){
							ex2.printStackTrace();
							errorSecu.append("\t- Unable to apply grants for Group " + s + " : " + ex2.getMessage() + "\n");
						}
						
						
					}
					
					compositeRepository.getInput().add(i);
					compositeRepository.refresh();
					
					if (errorSecu.toString().length() > 0){
						throw new Exception("The DirectoryItem has been created but some problems occured when applied security: \n" + errorSecu.toString());
					}
					
				} catch (Exception e1) {
					e1.printStackTrace();
					MessageDialog.openError(getShell(), "Problem", "Unable to create DirectoryItem :" + e1.getMessage());
				}
				
				
			}
		});
		
		delete.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {				
				Object target = compositeRepository.getSelectedItems().get(0);
				
				if (target instanceof RepositoryDirectory){
					try {
						
						sock.getRepositoryService().delete((RepositoryDirectory)target);
						compositeRepository.getInput().remove((RepositoryDirectory)target);
						compositeRepository.refresh();
					} catch (Exception e1) {
						e1.printStackTrace();
						MessageDialog.openError(getShell(), "Problem", "Unable to delete Directory :" + e1.getMessage());
					}
				}
				else if (target instanceof RepositoryItem){
					try {
						
						sock.getRepositoryService().delete((RepositoryItem)target);
						compositeRepository.getInput().remove((RepositoryItem)target);
						compositeRepository.refresh();
					} catch (Exception e1) {
						e1.printStackTrace();
						MessageDialog.openError(getShell(), "Problem", "Unable to delete DirectoryItem :" + e1.getMessage());
					}
				}
				
				
				
				
			}
		});
	}
	
	protected void fillToolbar(ToolBar bar) {
		newDirectory = new ToolItem(bar, SWT.NONE);
		newDirectory.setText("Directory");
		newDirectory.setToolTipText("Create Directory");
		newDirectory.setImage(Activator.getDefault().getImageRegistry().get(IconsRegistry.FOLDER));
		
		
		newObject = new ToolItem(bar, SWT.NONE);
		newObject.setText("Item");
		newObject.setToolTipText("Create DirectoryItem");
		newObject.setImage(Activator.getDefault().getImageRegistry().get(IconsRegistry.ADD));
		
	
		delete = new ToolItem(bar, SWT.NONE);
		delete.setText("Delete");
		delete.setToolTipText("Delete");
		delete.setImage(Activator.getDefault().getImageRegistry().get(IconsRegistry.DEL));		
		
		compositeRepository.addSelectionChangedListener(new ISelectionChangedListener() {
			
			public void selectionChanged(SelectionChangedEvent event) {
				
				Object o = ((IStructuredSelection)event.getSelection()).getFirstElement();
				
				newObject.setEnabled((o instanceof RepositoryDirectory ) && Activator.getDefault().getDesignerActivator().getCurrentModel() != null);
			}
		});
		attachButtonsListeners();
	}
}
