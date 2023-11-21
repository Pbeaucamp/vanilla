package bpm.es.gedmanager.wizards;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

import bpm.es.gedmanager.api.GedModel;
import bpm.vanilla.platform.core.beans.ged.GedDocument;

public class ImportDocumentWizard extends Wizard {

	private ImportDocumentTypePage typePage;
	private ImportNewDocumentPage newDocumentPage;
	private ImportVersionToExistingDocumentPage newVersionPage;
	
	private GedModel model;
	
	public ImportDocumentWizard(GedModel model) {
		this.model = model;
	}
	
	@Override
	public boolean canFinish() {
		return newDocumentPage.isPageComplete() || newVersionPage.isPageComplete();
	}
	
	@Override
	public boolean performFinish() {
		
		if(newDocumentPage.isPageComplete()) {
			String name = newDocumentPage.getName();
			String file = newDocumentPage.getFilename();
//			int dirId = newDocumentPage.getDirectoryId();
			
			try {
				model.createNewDocument(name, file/*, dirId*/);
			} catch (Exception e) {
				e.printStackTrace();
				MessageDialog.openError(getShell(), "Error", "An error occured while importing the document - " + e.getMessage());
			}
		}
		else {
			if(newVersionPage.isPageComplete()) {
				String file = newVersionPage.getFile();
				GedDocument doc = newVersionPage.getDocument();
				
				String format = "any";
				try {
					format = file.substring(file.lastIndexOf("."));
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				model.addVersion(doc, format, file);
			}
		}
		
		return true;
	}

	@Override
	public void addPages() {
		typePage = new ImportDocumentTypePage("Import document");
		typePage.setTitle("Import document");
		typePage.setDescription("Import a document in the document manager");
		addPage(typePage);
		
		newDocumentPage = new ImportNewDocumentPage("Create a new document");
		newDocumentPage.setTitle("Create a new document");
		newDocumentPage.setDescription("Create a new document in the document manager");
		addPage(newDocumentPage);
		
		newVersionPage = new ImportVersionToExistingDocumentPage("Add a new version", model);
		newVersionPage.setTitle("Add a new version");
		newVersionPage.setDescription("Add a new version to an existing document");
		addPage(newVersionPage);
	}
	
	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		if(page == typePage) {
			if(typePage.getSelection() == ImportDocumentTypePage.NEW_DOCUMENT) {
				return newDocumentPage;
			}
			else {
				return newVersionPage;
			}
		}
		return super.getNextPage(page);
	}
}
