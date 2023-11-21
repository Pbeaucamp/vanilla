package org.fasd.cubewizard;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.fasd.datasource.DataSource;
import org.fasd.datasource.DataSourceConnection;
import org.fasd.i18N.LanguageText;
import org.fasd.views.composites.CompositeDataSource;

public class PageConnection extends WizardPage {

	private CompositeDataSource composite;
	private DataSource dataSource, result;

	protected PageConnection(String pageName) {
		super(pageName);
	}

	public void createControl(Composite parent) {
		// create main composite & set layout
		Composite mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayout(new FillLayout());
		mainComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		// create contents
		createPageContent(mainComposite);

		// page setting
		setControl(mainComposite);
		setPageComplete(true);

	}

	private void createPageContent(Composite parent) {
		dataSource = ((DataSourceWizard) (this.getWizard())).getWokingDataSource();
		result = ((DataSourceWizard) (this.getWizard())).getDataSource();
		composite = new CompositeDataSource(parent, SWT.NONE, dataSource, true);
		composite.setButtonsVisible(false);
	}

	@Override
	public boolean canFlipToNextPage() {
		return true;
	}

	@Override
	public IWizardPage getNextPage() {
		try {

			if (composite.isDataBase() && composite.getSelectdFolder() != 1) {
				composite.selectOtherFolder();
				return this;
			}

			if (getWizard() instanceof DataSourceWizard) {
				PageDataSource nextPage = (PageDataSource) ((DataSourceWizard) getWizard()).getNextPage(this);
				composite.setDataSource();
				result.setDSName(dataSource.getDSName());

				if (dataSource.getDriver().getType().equals("DataBase")) { //$NON-NLS-1$
					composite.connect();

				}
				result.setDriver(new DataSourceConnection());
				result.getDriver().setDataSourceLocation(dataSource.getDriver().getDataSourceLocation());
				result.getDriver().setSchemaName(dataSource.getDriver().getSchemaName());
				result.getDriver().setDesc(dataSource.getDriver().getDesc());
				result.getDriver().setDriver(dataSource.getDriver().getDriver());
				result.getDriver().setDriverFile(dataSource.getDriver().getDriverFile());
				result.getDriver().setFileLocation(dataSource.getDriver().getFileLocation());
				result.getDriver().setName(dataSource.getDriver().getName());
				result.getDriver().setPass(dataSource.getDriver().getPass());
				result.getDriver().setUser(dataSource.getDriver().getUser());
				result.getDriver().setServer(dataSource.getDriver().getServer());
				result.getDriver().setTransUrl(dataSource.getDriver().getTransUrl());
				result.getDriver().setType(dataSource.getDriver().getType());
				result.getDriver().setUrl(dataSource.getDriver().getUrl());

				nextPage.createModel();

				if (result.getDriver().getType().equals("XML")) { //$NON-NLS-1$
					nextPage.enableToolBar(false);
				} else {
					nextPage.enableToolBar(true);
				}

				return nextPage;
			}

			return this;

		} catch (Exception e) {
			e.printStackTrace();
			if (dataSource.getDriver().getType().equals("Pentaho MetaData XMI") && dataSource.getDriver().getFileLocation().trim().equals("")) { //$NON-NLS-1$ //$NON-NLS-2$
				MessageDialog.openError(getShell(), LanguageText.PageConnection_Error, LanguageText.PageConnection_SelectAFile);
			} else
				MessageDialog.openError(getShell(), LanguageText.PageConnection_Error, e.getMessage());

			return this;
		}

	}

	@Override
	public boolean isCurrentPage() {
		return super.isCurrentPage();
	}

}
