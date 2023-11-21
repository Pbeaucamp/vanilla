package bpm.vanilla.server.ui.dialogs;

import java.util.List;
import java.util.Properties;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.repository.Comment;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.platform.core.repository.SecuredCommentObject;
import bpm.vanilla.repository.ui.Activator;
import bpm.vanilla.repository.ui.dialogs.DialogNewItem;
import bpm.vanilla.repository.ui.wizards.page.ExportObjectWizard;
import bpm.vanilla.repository.ui.wizards.page.RepositoryExportPage;
import bpm.vanilla.server.ui.Messages;

public class ExportTaskListWizard extends ExportObjectWizard{
	private List<Group> groups;
	private String taskListXml;
	public ExportTaskListWizard(String taskListXml){
		this.taskListXml = taskListXml;
		try{
			groups = bpm.vanilla.server.client.ui.clustering.menu.Activator.getDefault().getVanillaApi().getVanillaSecurityManager().getGroups();
		}catch(Exception ex){
			
		}
	}
	
	protected void addContentPage(){
		contentPage = new RepositoryExportPage("Content"){ //$NON-NLS-1$
			@Override
			protected void attachButtonsListeners() {
				newObject.addSelectionListener(new SelectionAdapter(){
					@Override
					public void widgetSelected(SelectionEvent e) {
						try {
							super.widgetSelected(e);IRepositoryApi sock = wizard.getRepositoryConnection();
							DialogNewItem dial = new DialogNewItem(getShell(), 
									groups);
							
							if (dial.open() != DialogNewItem.OK){
								return;
							}
							RepositoryDirectory target = (RepositoryDirectory)compositeRepository.getSelectedItems().get(0);
							Properties p = dial.getProperties();
							
							
							String[] groupNames = p.getProperty(DialogNewItem.P_GROUP).split(";"); //$NON-NLS-1$
							
							RepositoryItem pt = sock.getRepositoryService().addDirectoryItemWithDisplay(
									Activator.getDefault().getDesignerActivator().getRepositoryItemType(), -1, 
									target, p.getProperty(DialogNewItem.P_NAME), p.getProperty(DialogNewItem.P_DESCRIPTION), 
									"", "", taskListXml, true); //$NON-NLS-1$ //$NON-NLS-2$
							
							
							compositeRepository.refresh();
							
							
							
							/*
							 * create access and run for all groups
							 */
							
							StringBuilder errorSecu = new StringBuilder();
							
							for(String s : groupNames){
								
								try{
									Integer groupId = null;
									
									for(Group g : groups){
										if (g.getName().equals(s)){
											groupId = g.getId();
											break;
										}
									}
									
		
									sock.getAdminService().addGroupForItem(groupId, pt.getId());

									Group dummyGroup = new Group();
									dummyGroup.setId(groupId);
									sock.getAdminService().setObjectRunnableForGroup(dummyGroup.getId(), pt);
									
									SecuredCommentObject secComment = new SecuredCommentObject();
									secComment.setGroupId(groupId);
									secComment.setObjectId(pt.getId());
									secComment.setType(Comment.ITEM);
									sock.getDocumentationService().addSecuredCommentObject(secComment);
								}catch(Exception ex2){
									ex2.printStackTrace();
									errorSecu.append(Messages.ExportTaskListWizard_6 + s + " : " + ex2.getMessage() + "\n");  //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
								}
								
								
							}
							
						} catch (Exception e1) {
							e1.printStackTrace();
							MessageDialog.openError(getShell(), Messages.ExportTaskListWizard_9, Messages.ExportTaskListWizard_10 + e1.getMessage());
						}
					}
					
				});
			}
		};
		contentPage.setTitle(Messages.ExportTaskListWizard_11);
		

		addPage(contentPage);
		
	}
}
