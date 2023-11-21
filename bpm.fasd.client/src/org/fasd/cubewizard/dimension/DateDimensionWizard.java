package org.fasd.cubewizard.dimension;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.fasd.datasource.DataObjectItem;
import org.fasd.i18N.LanguageText;
import org.fasd.olap.OLAPDimension;
import org.fasd.olap.OLAPHierarchy;
import org.fasd.olap.OLAPLevel;
import org.fasd.olap.exceptions.HierarchyException;

/**
 * A wizard used to created a time dimension with one column only
 * 
 * @author Marc Lanquetin
 * 
 */
public class DateDimensionWizard extends Wizard implements INewWizard {

	private DateDimensionInfosPage infosPage;
	private DateDimensionLevelsPage levelsPage;

	private OLAPDimension dimension;

	public DateDimensionWizard(OLAPDimension dim) {
		dimension = dim;
	}

	public DateDimensionWizard() {
	}

	@Override
	public boolean performFinish() {
		dimension = new OLAPDimension();
		dimension.setOneColumnDate(true);

		dimension.setName(infosPage.getDimensionName());

		OLAPHierarchy hiera = new OLAPHierarchy();
		hiera.setName(infosPage.getHierarchyName());

		DataObjectItem item = infosPage.getColumn();
		String pattern = infosPage.getPattern();
		String type = infosPage.getColumnType();

		List<OLAPLevel> levels = levelsPage.getDimensionLevels();
		for (OLAPLevel lvl : levels) {
			lvl.setItem(item);
			lvl.setOneColumnDate(true);
			lvl.setDateColumnType(type);
			lvl.setDateColumnPattern(pattern);
			try {
				hiera.addLevel(lvl);
			} catch (HierarchyException e) {
				e.printStackTrace();
			}
		}

		dimension.addHierarchy(hiera);

		dimension.setDate(true);

		return true;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.setForcePreviousAndNextButtons(true);
		this.setWindowTitle(LanguageText.DateDimensionWizard_0);
	}

	@Override
	public void addPages() {

		infosPage = new DateDimensionInfosPage("Dimension informations", dimension); //$NON-NLS-1$
		infosPage.setTitle(LanguageText.DateDimensionWizard_2);
		infosPage.setDescription(LanguageText.DateDimensionWizard_3);
		addPage(infosPage);

		levelsPage = new DateDimensionLevelsPage("Dimension definition", dimension); //$NON-NLS-1$
		levelsPage.setTitle(LanguageText.DateDimensionWizard_5);
		levelsPage.setDescription(LanguageText.DateDimensionWizard_6);
		addPage(levelsPage);
	}

	@Override
	public boolean canFinish() {
		return levelsPage.canFinish();
	}

	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		return super.getNextPage(page);
	}

	public OLAPDimension getResultDimension() {
		return dimension;
	}

}
