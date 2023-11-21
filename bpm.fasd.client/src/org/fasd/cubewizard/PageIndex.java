package org.fasd.cubewizard;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.fasd.datasource.DataSourceConnection;
import org.fasd.i18N.LanguageText;
import org.fasd.inport.ParserPentahoJDBCFile;
import org.fasd.sql.SQLConnection;
import org.freeolap.FreemetricsPlugin;

public class PageIndex extends WizardPage {
	private Button create, local, repository;
	private Combo list;
	private List<SQLConnection> connexions = new ArrayList<SQLConnection>();
	private DataSourceConnection sock;
	private ComboViewer reposirotyList;

	protected PageIndex(String pageName) {
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

	public void createPageContent(Composite parent) {
		final Composite page0 = new Composite(parent, SWT.NONE);
		page0.setLayout(new GridLayout());

		Label lbl = new Label(page0, SWT.None);
		lbl.setImage(new Image(parent.getDisplay(), Platform.getInstallLocation().getURL().getPath() + "icons/test1.jpg")); //$NON-NLS-1$

		Group gr = new Group(page0, SWT.NONE);
		gr.setText(LanguageText.PageIndex_Database);
		gr.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		gr.setLayout(new GridLayout(2, false));

		create = new Button(gr, SWT.RADIO);
		create.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		create.setText(LanguageText.PageIndex_CreateNewConnection);
		create.setSelection(true);
		create.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent e) {
				if (create.getSelection()) {
					list.setEnabled(false);
					reposirotyList.getCombo().setEnabled(false);
				}
			}
		});

		local = new Button(gr, SWT.RADIO);
		local.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		local.setText(LanguageText.PageIndex_0);
		local.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (local.getSelection()) {
					list.setEnabled(true);
					reposirotyList.getCombo().setEnabled(false);
				}
			}

		});

		repository = new Button(gr, SWT.RADIO);
		repository.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		repository.setText(LanguageText.PageIndex_1);
		repository.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (repository.getSelection()) {
					list.setEnabled(false);
					reposirotyList.getCombo().setEnabled(true);
				}
			}

		});

		if (FreemetricsPlugin.getDefault().getRepositoryConnection() != null) {
			repository.setEnabled(true);
		} else {
			repository.setEnabled(false);
		}

		Label lbl1 = new Label(gr, SWT.NONE);
		lbl1.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		lbl1.setText(LanguageText.PageIndex_SelectAnExsistingConnection);

		list = new Combo(gr, SWT.NONE);
		list.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		list.setEnabled(false);

		Label l2 = new Label(gr, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l2.setText(LanguageText.PageIndex_2);

		reposirotyList = new ComboViewer(gr, SWT.READ_ONLY);
		reposirotyList.getCombo().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		reposirotyList.getCombo().setEnabled(false);
		reposirotyList.setContentProvider(new IStructuredContentProvider() {

			public Object[] getElements(Object inputElement) {
				List<bpm.vanilla.platform.core.repository.DataSource> l = (List<bpm.vanilla.platform.core.repository.DataSource>) inputElement;
				return l.toArray(new bpm.vanilla.platform.core.repository.DataSource[l.size()]);
			}

			public void dispose() {
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}

		});

		reposirotyList.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				return ((bpm.vanilla.platform.core.repository.DataSource) element).getName();
			}

		});

		ParserPentahoJDBCFile parser = new ParserPentahoJDBCFile(Platform.getInstallLocation().getURL().getPath() + "/system/simple-jndi/jdbc.properties"); //$NON-NLS-1$ //$NON-NLS-2$

		try {
			connexions = parser.getListConnection();
			String[] items = new String[connexions.size()];

			for (int i = 0; i < connexions.size(); i++)
				items[i] = connexions.get(i).getSchemaName();

			list.setItems(items);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean canFlipToNextPage() {
		return true;
	}

	@Override
	public IWizardPage getNextPage() {
		return null;
	}

	public DataSourceConnection getSock() {
		return sock;
	}

	public boolean getFlag() {
		return create.getSelection();
	}
}
