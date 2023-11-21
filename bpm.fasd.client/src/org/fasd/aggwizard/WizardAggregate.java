package org.fasd.aggwizard;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.fasd.datasource.DataObject;
import org.fasd.i18N.LanguageText;
import org.fasd.olap.OLAPRelation;
import org.fasd.olap.aggregate.AggregateTable;
import org.fasd.views.CubeView;
import org.fasd.views.SQLView;
import org.freeolap.FreemetricsPlugin;

public class WizardAggregate extends Wizard implements INewWizard {

	private PageDefinition defPage;
	private PageCubeAttachment cubePage;
	protected AggregateTable aggTable;
	protected OLAPRelation foreignKeys;

	@Override
	public boolean performFinish() {
		DataObject t = defPage.getAggTable();
		t.setDataObjectType(LanguageText.WizardAggregate_0);
		t.getDataSource().addDataObject(t);
		IWorkbenchPage page = FreemetricsPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();

		try {
			// add the mondrian aggobeject to the cube
			cubePage.createAggStructure();

			// add the foreignKey
			FreemetricsPlugin.getDefault().getFAModel().addRelation(foreignKeys);

			CubeView cview = (CubeView) page.findView(CubeView.ID);
			cview.refresh();
		} catch (Exception e) {

		}

		// refresh views

		SQLView view = (SQLView) page.findView(SQLView.ID);
		view.refresh(true);

		return true;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}

	@Override
	public void addPages() {
		defPage = new PageDefinition(LanguageText.WizardAggregate_1);
		defPage.setTitle(LanguageText.WizardAggregate_2);
		defPage.setDescription(LanguageText.WizardAggregate_3);
		addPage(defPage);

		cubePage = new PageCubeAttachment(LanguageText.WizardAggregate_4);
		cubePage.setTitle(LanguageText.WizardAggregate_5);
		cubePage.setDescription(LanguageText.WizardAggregate_6);
		addPage(cubePage);
	}

}
