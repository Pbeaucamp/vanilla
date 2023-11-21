package bpm.birt.comment.item.ui.wizard;

import org.eclipse.birt.report.model.api.ExtendedItemHandle;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import bpm.birt.comment.item.core.reportitem.CommentItem;

public class WizardEditor extends Wizard implements INewWizard {

	private static final String PAGE_DEFINITION_TITLE = "Définition de l'objet commentaire";
	private static final String PAGE_DEFINITION_DESCRIPTION = "Entrez les informations de l'objet commentaire";
	
	private PageDefinition pageDefinition;
	
	private CommentItem commentItem;
	private ExtendedItemHandle handle;
	
	public WizardEditor(CommentItem commentItem, ExtendedItemHandle handle) {
		this.commentItem = commentItem;
		this.handle = handle;
	}
	
	@Override
	public void addPages() {
		pageDefinition = new PageDefinition("Définition de l'objet commentaire", commentItem, handle);
		pageDefinition.setTitle(PAGE_DEFINITION_TITLE);
		pageDefinition.setDescription(PAGE_DEFINITION_DESCRIPTION);
		addPage(pageDefinition);
	}
	
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) { }

	@Override
	public boolean performFinish() {
		pageDefinition.defineCommentItem();
		return true;
	}

	@Override
	public boolean canFinish() {
		return pageDefinition.isPageComplete();
	}
	
}
