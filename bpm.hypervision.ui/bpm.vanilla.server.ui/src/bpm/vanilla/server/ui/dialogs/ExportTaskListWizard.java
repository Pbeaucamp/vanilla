package bpm.vanilla.server.ui.dialogs;

import java.io.ByteArrayOutputStream;
import java.util.Properties;

import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import bpm.repository.api.model.Directory;
import bpm.repository.api.model.DirectoryItem;
import bpm.repository.api.model.IDirectory;
import bpm.repository.api.model.IRepositoryConnection;
import bpm.repository.api.model.Item;
import bpm.vanilla.repository.ui.Activator;
import bpm.vanilla.repository.ui.dialogs.DialogNewItem;
import bpm.vanilla.repository.ui.wizards.page.ExportObjectWizard;
import bpm.vanilla.repository.ui.wizards.page.RepositoryExportPage;

public class ExportTaskListWizard extends ExportObjectWizard{

	private String taskListXml;
	public ExportTaskListWizard(String taskListXml){
		this.taskListXml = taskListXml;
	}
	
	protected void addContentPage(){
		contentPage = new RepositoryExportPage("Content"){
			@Override
			protected void attachButtonsListeners() {
				newObject.addSelectionListener(new SelectionAdapter(){
					@Override
					public void widgetSelected(SelectionEvent e) {
						
						super.widgetSelected(e);IRepositoryConnection sock = wizard.getRepositoryConnection();
						DialogNewItem dial = new DialogNewItem(getShell(), sock.getGroups());
						
						if (dial.open() != DialogNewItem.OK){
							return;
						}
						IDirectory target = (IDirectory)compositeRepository.getSelectedItems().get(0);
						Properties p = dial.getProperties();
						try {
							
							ByteArrayOutputStream baos = new ByteArrayOutputStream();
							
							
							
							int i = sock.addItem(Activator.getDefault().getDesignerActivator().getRepositoryItemType(), target, p.getProperty(DialogNewItem.P_GROUP), p.getProperty(DialogNewItem.P_NAME), p.getProperty(DialogNewItem.P_DESCRIPTION), "", "", "", taskListXml);
							DirectoryItem dummy = new DirectoryItem();
							dummy.setItem(new Item());
							dummy.getItem().setName(p.getProperty(DialogNewItem.P_NAME));
							dummy.setId(i);
							
							((Directory)target).addPublicDirectoryItem(dummy);
							compositeRepository.refresh();
						} catch (Exception e1) {
							e1.printStackTrace();
							MessageDialog.openError(getShell(), "Problem", "Unable to create DirectoryItem :" + e1.getMessage());
						}
					}
					
				});
			}
		};
		contentPage.setTitle("Repository Content");
		

		addPage(contentPage);
		
	}
}
