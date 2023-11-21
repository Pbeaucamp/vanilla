package org.fasd.wizard.inline;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.fasd.datasource.DataInline;
import org.fasd.datasource.DataObject;
import org.fasd.i18N.LanguageText;


public class InlineWizard extends Wizard implements INewWizard {

	private PageStructure structPage;
	private PageDatas datasPage;

	
	protected DataObject inlineTable = new DataObject();
	
	public InlineWizard() {
	}

	@Override
	public boolean performFinish() {
		return true;
	}
	
	public DataObject getInlineTable(){
		return inlineTable;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.setWindowTitle(LanguageText.InlineWizard_Inline_Table_Wizard);
	}
	
	@Override
	public void addPages() {
		structPage = new PageStructure(LanguageText.InlineWizard_Inline_Table_Structure);
		structPage.setTitle(LanguageText.InlineWizard_Inline_Table_Structure);
		structPage.setDescription(LanguageText.InlineWizard_Define_Inline_Structure);
		addPage(structPage);
		
		datasPage = new PageDatas(LanguageText.InlineWizard_Inline_Table_Datas);
		datasPage.setTitle(LanguageText.InlineWizard_Inline_Table_Datas);
		datasPage.setDescription(LanguageText.InlineWizard_Fill);
		addPage(datasPage);
	}

	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		if(page == structPage){
			inlineTable.setDatas(new DataInline());
			inlineTable.getDatas().setDataObject(inlineTable);
			inlineTable.setInline(true);
			datasPage.buildTable();
			return datasPage;
		}
		return page;
	}

	@Override
	public boolean canFinish() {
		if (datasPage.isPageComplete() && datasPage.isCurrentPage())
			return true;
		
		return false;
	}

}
