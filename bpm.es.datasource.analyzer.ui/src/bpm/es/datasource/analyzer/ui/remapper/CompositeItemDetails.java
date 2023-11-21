package bpm.es.datasource.analyzer.ui.remapper;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import adminbirep.Activator;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class CompositeItemDetails {
	public static enum RenderType {
		TREE, TABLE, LIST;
	}

	private CompositeItem details;
	private StructuredViewer items;
	private Composite client;

	private RenderType type = RenderType.TABLE;
	private String label = ""; //$NON-NLS-1$
	private ISelectionChangedListener selectionListener = new ISelectionChangedListener() {

		public void selectionChanged(SelectionChangedEvent event) {
			if(event.getSelection().isEmpty()) {

			}
			Object o = ((IStructuredSelection) event.getSelection()).getFirstElement();

			if(o instanceof TreeNode) {
				o = ((TreeNode) o).getValue();
			}

			RepositoryItem item = null;

			if(o instanceof RepositoryItem) {
				item = ((RepositoryItem) o);
			}

			if(item == null) {
				details.clear();
				return;
			}
			String xml = null;
			try {
				xml = Activator.getDefault().getRepositoryApi().getRepositoryService().loadModel(item);
			} catch(Exception e) {
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				e.printStackTrace(new PrintWriter(os));
				xml = os.toString();
			}

			details.setDatas(item, xml);

		}
	};

	public CompositeItemDetails(String label, RenderType type) {
		this.type = type;
		this.label = label;
	}

	public Composite getClient() {
		return client;
	}

	public void addDoubleClickListener(IDoubleClickListener listener) {
		items.addDoubleClickListener(listener);

	}

	public void removeDoubleClickListener(IDoubleClickListener listener) {

		items.removeDoubleClickListener(listener);

	}

	public Composite createContent(Composite parent, IStructuredContentProvider contentProvider, LabelProvider labelprovider) {
		client = new Composite(parent, SWT.NONE);
		client.setLayout(new GridLayout(2, true));

		Label l = new Label(client, SWT.NONE);
		l.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		l.setText(label);

		switch(type) {
			case LIST:
				items = new ListViewer(client, SWT.V_SCROLL | SWT.H_SCROLL | SWT.VIRTUAL | SWT.FULL_SELECTION | SWT.BORDER);

				break;
			case TABLE:
				items = new TableViewer(client, SWT.V_SCROLL | SWT.H_SCROLL | SWT.VIRTUAL | SWT.FULL_SELECTION | SWT.BORDER);
				break;
			case TREE:
				items = new TreeViewer(client, SWT.V_SCROLL | SWT.H_SCROLL | SWT.VIRTUAL | SWT.FULL_SELECTION | SWT.BORDER);

				break;
			default:
				items = new TableViewer(client, SWT.V_SCROLL | SWT.H_SCROLL | SWT.VIRTUAL | SWT.FULL_SELECTION | SWT.BORDER);

		}

		items.setContentProvider(contentProvider);
		items.setLabelProvider(labelprovider);

		items.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));

		details = new CompositeItem();
		details.createContent(client);
		details.getClient().setLayoutData(new GridData(GridData.FILL_BOTH));

		return client;
	}

	public void setDatas(Object repositoryContent) {
		items.removeSelectionChangedListener(selectionListener);
		if(repositoryContent instanceof TreeNode) {
			items.setInput(((TreeNode) repositoryContent).getValue());
		}
		else {
			items.setInput(repositoryContent);
		}

		details.clear();
		items.addSelectionChangedListener(selectionListener);
	}

	public void setViewerFilter(ViewerFilter filter) {
		if(filter == null) {
			items.setFilters(new ViewerFilter[] {});
		}
		else {
			items.setFilters(new ViewerFilter[] { filter });
		}

	}

	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		items.addSelectionChangedListener(listener);
	}

	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		items.removeSelectionChangedListener(listener);
	}
}
