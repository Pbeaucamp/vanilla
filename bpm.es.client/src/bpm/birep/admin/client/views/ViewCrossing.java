package bpm.birep.admin.client.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import adminbirep.Activator;
import adminbirep.Messages;
import bpm.birep.admin.client.trees.TreeContentProvider;
import bpm.birep.admin.client.trees.TreeDatasource;
import bpm.birep.admin.client.trees.TreeItem;
import bpm.birep.admin.client.trees.TreeLabelProvider;
import bpm.birep.admin.client.trees.TreeParent;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.DataSource;
import bpm.vanilla.platform.core.repository.DataSourceImpact;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class ViewCrossing extends ViewPart implements ISelectionListener {

	public static final String ID = "viewCrossing"; //$NON-NLS-1$
	protected TreeViewer viewer;

	protected Button showDependantItem, showRequestedItems;
	protected RepositoryItem selectedItem;
	protected int behavior = 0;

	private static final int SHOW_DEPENDANT = 0;
	private static final int SHOW_REQUESTED = 1;

	public ViewCrossing() {
		Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().addSelectionListener(this);
	}
	
	@Override
	public void dispose() {
		Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().removeSelectionListener(this);	
		super.dispose();
	}

	@Override
	public void createPartControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout());

		Composite behavior = new Composite(container, SWT.NONE);
		behavior.setLayout(new GridLayout(2, true));
		behavior.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		SelectionListener behaviorListener = new SelectionAdapter() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse
			 * .swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {

				if (showDependantItem.getSelection()) {
					ViewCrossing.this.behavior = SHOW_DEPENDANT;
				}
				else {
					ViewCrossing.this.behavior = SHOW_REQUESTED;
				}

				if (selectedItem == null) {
					return;
				}
				try {
					createModel(selectedItem, ViewCrossing.this.behavior);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}

		};

		showDependantItem = new Button(behavior, SWT.RADIO);
		showDependantItem.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		showDependantItem.setText(Messages.Client_Views_ViewCrossing_1);
		showDependantItem.setSelection(true);
		showDependantItem.addSelectionListener(behaviorListener);

		showRequestedItems = new Button(behavior, SWT.RADIO);
		showRequestedItems.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		showRequestedItems.setText(Messages.Client_Views_ViewCrossing_2);
		showRequestedItems.addSelectionListener(behaviorListener);

		viewer = new TreeViewer(container, SWT.BORDER | SWT.V_SCROLL);
		viewer.getTree().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 2));
		viewer.setContentProvider(new TreeContentProvider());
		viewer.setLabelProvider(new TreeLabelProvider());
	}

	@Override
	public void setFocus() {
	}

	protected void createModel(RepositoryItem dirIt, int behavior) throws Exception {
		StringBuffer errors = new StringBuffer();

		TreeParent root = new TreeParent(""); //$NON-NLS-1$
		IRepositoryApi sock = Activator.getDefault().getRepositoryApi();
		switch (behavior) {
			case SHOW_DEPENDANT:
				List<RepositoryItem> dirs;
				try {
					dirs = Activator.getDefault().getRepositoryApi().getRepositoryService().getDependantItems(dirIt);
				} catch (Exception e) {
					e.printStackTrace();
					break;
				}
				for (RepositoryItem ds : dirs) {
					try {
						if (ds.getType() == IRepositoryApi.CUST_TYPE) {
							RepositoryItem tmp = sock.getRepositoryService().getDirectoryItem(ds.getId());
							ds = (RepositoryItem) tmp;
						}
						TreeItem tds = new TreeItem(ds);

						root.addChild(tds);
					} catch (Exception ex) {
						ex.printStackTrace();
						errors.append("- Error on item " + ds.getId() + ": " + ex.getMessage() + "\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					}

				}
				break;
			case SHOW_REQUESTED:
				List<RepositoryItem> _dirs;
				try {
					_dirs = Activator.getDefault().getRepositoryApi().getRepositoryService().getNeededItems(dirIt.getId());
				} catch (Exception e) {
					e.printStackTrace();
					break;
				}
				for (RepositoryItem ds : _dirs) {

					try {
						if (ds.getType() == IRepositoryApi.CUST_TYPE) {
							RepositoryItem tmp = sock.getRepositoryService().getDirectoryItem(ds.getId());
							ds = (RepositoryItem) tmp;
						}
						TreeItem tds = new TreeItem(ds);
						try {
							for (RepositoryItem child : Activator.getDefault().getRepositoryApi().getRepositoryService().getNeededItems(ds.getId())) {
								if (child.getType() == IRepositoryApi.CUST_TYPE) {
									RepositoryItem tmp = sock.getRepositoryService().getDirectoryItem(ds.getId());
									child = (RepositoryItem) tmp;
								}
								tds.addChild(new TreeItem(child));
							}
						} catch (Exception e) {
							e.printStackTrace();
							break;
						}
						root.addChild(tds);
					} catch (Exception ex) {
						ex.printStackTrace();
						errors.append("- Error on item " + ds.getId() + ": " + ex.getMessage() + "\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

					}

				}
				break;
		}

		viewer.setInput(root);

		if (errors.length() > 0) {
			MessageDialog.openWarning(getSite().getShell(), Messages.Client_Views_ViewCrossing_0, errors.toString());
		}
	}

	private void createModel(DataSource ds) {
		TreeParent root = new TreeParent(""); //$NON-NLS-1$

		List<RepositoryItem> dir;
		try {
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

			for (RepositoryItem di : l) {
				TreeItem tds = new TreeItem(di);
				root.addChild(tds);
			}
			viewer.setInput(root);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (selection.isEmpty()) {
			return;
		}

		boolean timed = false;

		IStructuredSelection ss = (IStructuredSelection) selection;
		Object o = ss.getFirstElement();

		if (o instanceof TreeItem) {
			selectedItem = ((TreeItem) o).getItem();
			try {
				createModel(selectedItem, behavior);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if (o instanceof TreeDatasource) {
			createModel(((TreeDatasource) o).getDataSource());
		}
	}

}
