package bpm.vanilla.server.ui.wizard.list;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Properties;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.Parameter;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.repository.ui.viewers.RepositoryLabelProvider;
import bpm.vanilla.server.ui.Messages;
import bpm.vanilla.server.ui.views.composite.RunningOptionComposite;

public class ListPage extends WizardPage {

	private RunningOptionComposite runningComposite;
	
	private ListViewer objectViewer;
	private TableViewer parameterViewer;
	private IRepositoryApi repSocket;

	private HashMap<RepositoryItem, HashMap<Parameter, String>> params = new HashMap<RepositoryItem, HashMap<Parameter, String>>();

	public ListPage(String pageName) {
		super(pageName);
		this.setTitle(Messages.ListPage_1);
		setDescription(Messages.ListPage_7);
	}

	public void createControl(Composite parent) {
		TabFolder folder = new TabFolder(parent, SWT.NONE);
		folder.setLayout(new GridLayout());
		folder.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		buildRunningOptionPart(folder);
		buildParameterPart(folder);
		
		setControl(folder);
	}
	
	private void buildRunningOptionPart(TabFolder folder) {
		runningComposite = new RunningOptionComposite(folder, SWT.NONE);
		runningComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		runningComposite.setLayout(new GridLayout());
		
		TabItem tabItem = new TabItem(folder, SWT.NONE);
		tabItem.setText(Messages.ListPage_8);
		tabItem.setControl(runningComposite);
	}

	private void buildParameterPart(TabFolder folder) {
		Composite main = new Composite(folder, SWT.NONE);
		main.setLayout(new GridLayout(2, true));
		main.setLayoutData(new GridData(GridData.FILL_BOTH));

		Label l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.ListPage_0);

		l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.ListPage_4);

		objectViewer = new ListViewer(main, SWT.BORDER);
		objectViewer.setLabelProvider(new RepositoryLabelProvider());
		objectViewer.setContentProvider(new IStructuredContentProvider() {

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}

			public void dispose() {
			}

			public Object[] getElements(Object inputElement) {
				Collection c = (Collection) inputElement;
				return c.toArray(new Object[c.size()]);
			}
		});
		objectViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				if (objectViewer.getSelection().isEmpty()) {
					parameterViewer.setInput(new ArrayList<String>());
				}
				else {
					RepositoryItem it = (RepositoryItem) ((IStructuredSelection) objectViewer.getSelection()).getFirstElement();
					if (params.get(it) == null) {
						HashMap<Parameter, String> h = new HashMap<Parameter, String>();
						try {
							for (Parameter p : repSocket.getRepositoryService().getParameters(it)) {
								h.put(p, ""); //$NON-NLS-1$
							}
						} catch (Exception ex) {
							ex.printStackTrace();
							MessageDialog.openError(getShell(), Messages.ListPage_2, Messages.ListPage_3 + ex.getMessage());
						}

						params.put(it, h);
					}

					parameterViewer.setInput(params.get(it).keySet());
				}

			}
		});
		objectViewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));

		parameterViewer = new TableViewer(main, SWT.BORDER);
		parameterViewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		parameterViewer.getTable().setHeaderVisible(true);
		parameterViewer.getTable().setLinesVisible(true);
		parameterViewer.setContentProvider(new IStructuredContentProvider() {

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}

			public void dispose() {
			}

			public Object[] getElements(Object inputElement) {
				Collection c = (Collection) inputElement;
				return c.toArray(new Object[c.size()]);
			}
		});

		TableViewerColumn paramName = new TableViewerColumn(parameterViewer, SWT.NONE);
		paramName.getColumn().setText(Messages.ListPage_5);
		paramName.getColumn().setWidth(200);

		paramName.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				return ((Parameter) element).getName();
			}

		});

		TableViewerColumn paramValue = new TableViewerColumn(parameterViewer, SWT.NONE);
		paramValue.getColumn().setText(Messages.ListPage_6);
		paramValue.getColumn().setWidth(200);

		paramValue.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				RepositoryItem it = (RepositoryItem) ((IStructuredSelection) objectViewer.getSelection()).getFirstElement();
				return params.get(it).get((Parameter) element);
			}

		});
		paramValue.setEditingSupport(new EditingSupport(parameterViewer) {

			TextCellEditor editor = new TextCellEditor(parameterViewer.getTable());

			@Override
			protected void setValue(Object element, Object value) {
				RepositoryItem it = (RepositoryItem) ((IStructuredSelection) objectViewer.getSelection()).getFirstElement();

				params.get(it).put((Parameter) element, (String) value);
				parameterViewer.refresh();
			}

			@Override
			protected Object getValue(Object element) {
				RepositoryItem it = (RepositoryItem) ((IStructuredSelection) objectViewer.getSelection()).getFirstElement();
				return params.get(it).get(element);
			}

			@Override
			protected CellEditor getCellEditor(Object element) {
				return editor;
			}

			@Override
			protected boolean canEdit(Object element) {
				return true;
			}
		});
		
		TabItem tabItem = new TabItem(folder, SWT.NONE);
		tabItem.setText(Messages.ListPage_9);
		tabItem.setControl(main);
	}

	public void setInput(Collection<RepositoryItem> items, IRepositoryApi sock) {
		this.repSocket = sock;

		params.clear();
		objectViewer.setInput(items);
	}

	public HashMap<RepositoryItem, HashMap<Parameter, String>> getParameters() {
		return params;
	}

	public Properties getRunProperties() {
		return runningComposite.getRunProperties();
	}

	@Override
	public boolean isPageComplete() {
		return runningComposite.isPageComplete();
	}
}
