package bpm.birep.admin.client.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import adminbirep.Activator;
import bpm.vanilla.platform.core.repository.RepositoryLog;

public class ViewLogs extends ViewPart {

	public static final String ID = "bpm.birep.admin.client.viewlog"; //$NON-NLS-1$
	private TableViewer viewer;

	public ViewLogs() {
	}

	@Override
	public void createPartControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout());
		main.setLayoutData(new GridData(GridData.FILL_BOTH));

		viewer = new TableViewer(main, SWT.BORDER);
		viewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.setContentProvider(new IStructuredContentProvider() {

			@Override
			@SuppressWarnings("unchecked")
			public Object[] getElements(Object inputElement) {
				List<RepositoryLog> l = (List<RepositoryLog>) inputElement;
				return l.toArray(new RepositoryLog[l.size()]);
			}

			@Override
			public void dispose() {
			}

			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}

		});

		viewer.getTable().setHeaderVisible(true);

		TableViewerColumn app = new TableViewerColumn(viewer, SWT.NONE);
		app.getColumn().setText("App"); //$NON-NLS-1$
		app.getColumn().setWidth(100);
		app.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				RepositoryLog a = (RepositoryLog) element;
				return a.getAppName();
			}
		});

		TableViewerColumn operation = new TableViewerColumn(viewer, SWT.NONE);
		operation.getColumn().setText("operation"); //$NON-NLS-1$
		operation.getColumn().setWidth(100);
		operation.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				RepositoryLog a = (RepositoryLog) element;
				return a.getOperation();
			}
		});

		TableViewerColumn time = new TableViewerColumn(viewer, SWT.NONE);
		time.getColumn().setText("time"); //$NON-NLS-1$
		time.getColumn().setWidth(100);
		time.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				RepositoryLog a = (RepositoryLog) element;
				return "" + a.getTime(); //$NON-NLS-1$
			}
		});

		TableViewerColumn user = new TableViewerColumn(viewer, SWT.NONE);
		user.getColumn().setText("user"); //$NON-NLS-1$
		user.getColumn().setWidth(100);
		user.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				RepositoryLog a = (RepositoryLog) element;
				if (a.getUserName() != null) {
					return a.getUserName();
				}
				return "" + a.getUserId(); //$NON-NLS-1$
			}
		});

		TableViewerColumn group = new TableViewerColumn(viewer, SWT.NONE);
		group.getColumn().setText("group"); //$NON-NLS-1$
		group.getColumn().setWidth(100);
		group.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				RepositoryLog a = (RepositoryLog) element;
				if (a.getGroupName() != null) {
					return a.getGroupName();
				}
				if (a.getGroupId() == null) {
					return ""; //$NON-NLS-1$
				}
				return "" + a.getGroupId(); //$NON-NLS-1$
			}
		});

		viewer.setColumnProperties(new String[] { "id", "App", "operation", "time", "user", "group", /* "adress" */}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
	}

	public void setInput() {
		List<RepositoryLog> l;
		try {
			l = new ArrayList<RepositoryLog>(Activator.getDefault().getRepositoryApi().getRepositoryLogService().getLogs());
			viewer.setInput(l);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void setFocus() { }

}
