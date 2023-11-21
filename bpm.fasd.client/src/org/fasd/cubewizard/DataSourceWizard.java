package org.fasd.cubewizard;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.fasd.datasource.DataObject;
import org.fasd.datasource.DataSource;
import org.fasd.i18N.LanguageText;
import org.fasd.olap.OLAPRelation;

public class DataSourceWizard extends Wizard implements INewWizard {

	private PageConnection connectionPage;
	private PageDataSource dataSourcePage;
	private PageRelation relationPage;
	private PageIndex indexPage;
	private DataSource dataSource = new DataSource();
	private DataSource result = new DataSource();
	private List<OLAPRelation> rel = new ArrayList<OLAPRelation>();

	public List<OLAPRelation> getRelations() {
		return rel;
	}

	public DataSourceWizard() {
	}

	@Override
	public boolean canFinish() {
		if (connectionPage.isCurrentPage())
			return false;
		else
			return true;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.setForcePreviousAndNextButtons(true);
		this.setWindowTitle(LanguageText.DataSourceWizard_DataSourceWizard);

	}

	@Override
	public void addPages() {
		// create and add second page
		indexPage = new PageIndex(LanguageText.DataSourceWizard_Connection);
		indexPage.setTitle(LanguageText.DataSourceWizard_ConnectionToDatasource);
		indexPage.setDescription(LanguageText.DataSourceWizard_CreateANewConnectionOrChoose);
		addPage(indexPage);

		connectionPage = new PageConnection(LanguageText.DataSourceWizard_ChooseDataBase);
		connectionPage.setTitle(LanguageText.DataSourceWizard_ConnectionDef);
		connectionPage.setDescription(LanguageText.DataSourceWizard_DefineTheConnToYourDB);
		addPage(connectionPage);

		dataSourcePage = new PageDataSource(LanguageText.DataSourceWizard_SelectTables);
		dataSourcePage.setTitle(LanguageText.DataSourceWizard_SelectTables);
		dataSourcePage.setDescription(LanguageText.DataSourceWizard_ChooseTbToAddFromDBToDataSource);
		addPage(dataSourcePage);

		relationPage = new PageRelation(LanguageText.DataSourceWizard_DefineRelations);
		relationPage.setTitle(LanguageText.DataSourceWizard_DefineRelations);
		relationPage.setDescription(LanguageText.DataSourceWizard_DefineRelationsBetweenTables);
		addPage(relationPage);

	}

	@Override
	public boolean performFinish() {
		if (result.getDriver().getType().equals("XML")) { //$NON-NLS-1$
			result.addDataObject(dataSourcePage.getXMLTable());
		}

		// the connection is duplicted for some reason in this creapy wizard
		try {
			result.removeConnection(result.getDrivers().get(0));
		} catch (Exception ex) {

		}

		return true;
	}

	public DataSource getDataSource() {
		return result;
	}

	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		if (page == indexPage) {
			if (indexPage.getFlag()) {
				return connectionPage;

			}

			else {
				try {
					dataSourcePage.createModel();
				} catch (SQLException e) {
					e.printStackTrace();
				} catch (FileNotFoundException e) {
					MessageDialog.openError(this.getShell(), LanguageText.DataSourceWizard_Error, LanguageText.DataSourceWizard_JarFileNotFound);
					e.printStackTrace();
				} catch (Exception e) {
					MessageDialog.openError(this.getShell(), LanguageText.DataSourceWizard_Error, e.getMessage());
					e.printStackTrace();
				}
				return dataSourcePage;
			}
		} else if (page == connectionPage) {
			return dataSourcePage;
		} else if (page == dataSourcePage) {
			// dataSourcePage
			return relationPage;
		}

		else
			return null;
	}

	public DataSource getWokingDataSource() {
		return dataSource;
	}

	public void setRelations(List<OLAPRelation> relations) {
		rel = relations;

	}

	public void removeRelationsFor(DataObject data) {
		List<OLAPRelation> toRemove = new ArrayList<OLAPRelation>();

		for (OLAPRelation r : rel) {
			if (r.getLeftObject() == data || r.getRightObject() == data) {
				toRemove.add(r);
			}
		}

		rel.removeAll(toRemove);
	}

}
