package bpm.birep.admin.client.views.datasources;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.part.ViewPart;

import adminbirep.Activator;
import adminbirep.Messages;
import adminbirep.icons.Icons;
import bpm.birep.admin.client.trees.TreeContentProvider;
import bpm.birep.admin.client.trees.TreeDatasource;
import bpm.birep.admin.client.trees.TreeItem;
import bpm.birep.admin.client.trees.TreeLabelProvider;
import bpm.birep.admin.client.trees.TreeParent;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.DataSource;
import bpm.vanilla.platform.core.repository.DataSourceImpact;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class ViewDataSource extends ViewPart {

	public static String ID = "viewDataSources"; //$NON-NLS-1$
	private TreeViewer viewer;

	@Override
	public void createPartControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout());

		ToolBar tb = new ToolBar(container, SWT.NONE);
		tb.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		fillToolbar(tb);

		viewer = new TreeViewer(container, SWT.BORDER | SWT.V_SCROLL);
		viewer.getTree().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 2));
		viewer.setContentProvider(new TreeContentProvider());
		viewer.setLabelProvider(new TreeLabelProvider());

		try {
			createModel();
		} catch (Exception e) {
			e.printStackTrace();
		}
		getSite().setSelectionProvider(viewer);

		IToolBarManager mngr = this.getViewSite().getActionBars().getToolBarManager();
		mngr.add(new DatasourceActionRefresh(this));
	}

	@Override
	public void setFocus() {
	}

	public void createModel() throws Exception {
		TreeParent root = new TreeParent(""); //$NON-NLS-1$

		List<DataSource> dss = Activator.getDefault().getRepositoryApi().getImpactDetectionService().getAllDatasources();
		for (DataSource ds : dss) {
			TreeDatasource tds = new TreeDatasource(ds);

			IRepositoryApi repApi = Activator.getDefault().getRepositoryApi();

			List<RepositoryItem> l = new ArrayList<RepositoryItem>();
			for (DataSourceImpact dsi : repApi.getImpactDetectionService().getForDataSourceId(ds.getId())) {
				RepositoryItem it = repApi.getRepositoryService().getDirectoryItem(dsi.getDirectoryItemId());
				if (it != null) {
					if (((RepositoryItem) it).isVisible()) {
						l.add((RepositoryItem) it);
					}
				}
			}
			for (RepositoryItem it : l) {
				tds.addChild(new TreeItem(it));
			}
			root.addChild(tds);
		}

		viewer.setInput(root);
	}

	private void fillToolbar(ToolBar tb) {
		ToolItem add = new ToolItem(tb, SWT.PUSH);
		add.setImage(Activator.getDefault().getImageRegistry().get("add")); //$NON-NLS-1$
		add.setToolTipText(Messages.ViewDataSource_3);
		add.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				DataSource datasource = new DataSource();

				try {
					OdaDataSourceWizard wiz = new OdaDataSourceWizard(datasource);
					WizardDialog dial = new WizardDialog(getSite().getShell(), wiz);
					dial.setMinimumPageSize(800, 600);
					if (dial.open() == WizardDialog.OK) {
						datasource = wiz.getDatasource();
						int dsId = Activator.getDefault().getRepositoryApi().getImpactDetectionService().add(datasource);
						datasource.setId(dsId);
						TreeParent root = (TreeParent) viewer.getInput();
						root.addChild(new TreeDatasource(datasource));
						viewer.refresh();

						return;
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});

		ToolItem edit = new ToolItem(tb, SWT.PUSH);
		edit.setImage(Activator.getDefault().getImageRegistry().get(Icons.EDIT)); //$NON-NLS-1$
		edit.setToolTipText(Messages.ViewDataSource_0);
		edit.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();

				if (ss.isEmpty()) {
					return;
				}

				if (ss.getFirstElement() instanceof TreeDatasource) {
					try {
						DataSource datasource = ((TreeDatasource) ss.getFirstElement()).getDataSource();

						OdaDataSourceWizard wiz = new OdaDataSourceWizard(datasource);
						WizardDialog dial = new WizardDialog(getSite().getShell(), wiz);
						dial.setMinimumPageSize(800, 600);
						if (dial.open() == WizardDialog.OK) {
							Activator.getDefault().getRepositoryApi().getImpactDetectionService().update(datasource);

							viewer.refresh();
							return;
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		});

		ToolItem del = new ToolItem(tb, SWT.PUSH);
		del.setImage(Activator.getDefault().getImageRegistry().get("del")); //$NON-NLS-1$
		del.setToolTipText(Messages.ViewDataSource_7);
		del.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();

				if (ss.isEmpty()) {
					return;
				}
				TreeParent root = (TreeParent) viewer.getInput();

				for (Object o : ss.toList()) {
					if (o instanceof TreeDatasource) {
						DataSource ds = ((TreeDatasource) o).getDataSource();
						try {
							Activator.getDefault().getRepositoryApi().getImpactDetectionService().del(ds);
							root.removeChild((TreeDatasource) o);
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				}

				viewer.refresh();
			}
		});

	}
}
