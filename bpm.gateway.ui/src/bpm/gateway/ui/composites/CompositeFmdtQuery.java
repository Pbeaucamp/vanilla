package bpm.gateway.ui.composites;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableColumn;

import bpm.gateway.ui.Activator;
import bpm.gateway.ui.i18n.Messages;
import bpm.gateway.ui.icons.IconsNames;
import bpm.gateway.ui.tools.dialogs.DialogPickupParameter;
import bpm.gateway.ui.viewer.TreeBusinessTable;
import bpm.gateway.ui.viewer.TreeContentProvider;
import bpm.gateway.ui.viewer.TreeDataStreamElement;
import bpm.gateway.ui.viewer.TreeLabelProvider;
import bpm.gateway.ui.viewer.TreeParent;
import bpm.metadata.layer.business.IBusinessPackage;
import bpm.metadata.layer.business.IBusinessTable;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.query.IQuery;
import bpm.metadata.query.QuerySql;
import bpm.metadata.resource.IResource;
import bpm.metadata.resource.Prompt;

public class CompositeFmdtQuery extends Composite {
	public static String QUERY_CHANGED = Messages.CompositeFmdtQuery_0;

	private TreeViewer treeColumns;
	private ListViewer listColumns;

	private ListViewer prompts;
	private TableViewer parameters;

	private Button isDictinct;
	private ISelectionChangedListener selectionListener;

	private PropertyChangeSupport listeners = new PropertyChangeSupport(this);
	// private HashMap<Prompt, String> promptMap = new HashMap<Prompt,
	// String>();
	private HashMap<String, List<String>> promptValue = new HashMap<String, List<String>>();

	private IQuery queryFmdt;

	public CompositeFmdtQuery(Composite parent, int style) {
		super(parent, style);
		this.setBackground(getParent().getBackground());
		buildContent();
		queryFmdt = new QuerySql();
		parameters.setInput(queryFmdt.getPrompts());
	}

	public void addListener(PropertyChangeListener listener) {
		listeners.addPropertyChangeListener(listener);
	}

	public void removeListener(PropertyChangeListener listener) {
		listeners.removePropertyChangeListener(listener);
	}

	public void populateTree(IBusinessPackage pack) {
		TreeParent root = new TreeParent(""); //$NON-NLS-1$

		if (pack == null) {
			treeColumns.setInput(root);
			prompts.setInput(new ArrayList<Prompt>());
			return;
		}
		for (IBusinessTable t : pack.getBusinessTables("none")) { //$NON-NLS-1$

			TreeBusinessTable treeT = new TreeBusinessTable(t, Activator.getDefault().getRepositoryContext().getGroup().getName());

			root.addChild(treeT);

		}
		treeColumns.setInput(root);

		List<Prompt> l = new ArrayList<Prompt>();

		for (IResource r : pack.getResources()) {
			if (r instanceof Prompt) {
				l.add((Prompt) r);
			}
		}

		prompts.setInput(l);

	}

	private void buildContent() {
		this.setLayout(new GridLayout(3, false));

		Label l = new Label(this, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText("Select Columns"); //$NON-NLS-1$
		l.setBackground(getParent().getBackground());

		Composite c = new Composite(this, SWT.NONE);
		c.setLayoutData(new GridData(GridData.CENTER, GridData.FILL, false, true, 1, 3));
		c.setLayout(new GridLayout());
		c.setBackground(getParent().getBackground());

		Composite cc = new Composite(c, SWT.NONE);
		cc.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, false, true));
		cc.setLayout(new GridLayout());
		cc.setBackground(getParent().getBackground());

		Button add = new Button(cc, SWT.PUSH);
		add.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, false, false));
		add.setImage(Activator.getDefault().getImageRegistry().get(IconsNames.arrow_right_16)); //$NON-NLS-1$
		add.setToolTipText("Add Column"); //$NON-NLS-1$
		add.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!treeColumns.getSelection().isEmpty()) {
					for (Object o : ((IStructuredSelection) treeColumns.getSelection()).toList()) {
						if (o instanceof TreeBusinessTable) {
							for (IDataStreamElement el : ((TreeBusinessTable) o).getTable().getColumns("none")) { //$NON-NLS-1$

								if (!listContains(el)) {
									((List<IDataStreamElement>) listColumns.getInput()).add(el);
									if (queryFmdt == null) {
										queryFmdt = new QuerySql();

									}
									queryFmdt.getSelect().add(el);
									listeners.firePropertyChange(QUERY_CHANGED, null, queryFmdt);
								}
							}
						}
						else if (o instanceof TreeDataStreamElement) {
							if (!listContains(((TreeDataStreamElement) o).getDataStreamElement())) {
								((List<IDataStreamElement>) listColumns.getInput()).add(((TreeDataStreamElement) o).getDataStreamElement());
								if (queryFmdt == null) {
									queryFmdt = new QuerySql();

								}
								queryFmdt.getSelect().add(((TreeDataStreamElement) o).getDataStreamElement());
								listeners.firePropertyChange(QUERY_CHANGED, null, queryFmdt);
							}
						}
					}
					listColumns.refresh();
				}
				else if (!prompts.getSelection().isEmpty()) {

					Prompt p = (Prompt) ((IStructuredSelection) prompts.getSelection()).getFirstElement();
					DialogPickupParameter dial = new DialogPickupParameter(getShell(), Activator.getDefault().getCurrentInput().getDocumentGateway().getParameters());

					if (dial.open() == DialogPickupParameter.OK) {

						List<String> l = new ArrayList<String>();
						l.add(dial.getValue());
						promptValue.put(p.getName(), l);
						queryFmdt.addPrompt(p);

						parameters.setInput(queryFmdt.getPrompts());
						listeners.firePropertyChange(QUERY_CHANGED, null, queryFmdt);

					}

				}

			}

		});

		Button remove = new Button(cc, SWT.PUSH);
		remove.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, false, false));
		remove.setImage(Activator.getDefault().getImageRegistry().get(IconsNames.arrow_left_16)); //$NON-NLS-1$
		remove.setToolTipText(Messages.CompositeFmdtQuery_4);
		remove.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!listColumns.getSelection().isEmpty()) {
					IStructuredSelection ss = (IStructuredSelection) listColumns.getSelection();

					for (IDataStreamElement c : (List<IDataStreamElement>) ss.toList()) {
						((List<IDataStreamElement>) listColumns.getInput()).remove(c);
						queryFmdt.getSelect().remove(c);
						listeners.firePropertyChange(QUERY_CHANGED, null, queryFmdt);
					}

					listColumns.refresh();
					return;
				}
				else if (!parameters.getSelection().isEmpty()) {
					IStructuredSelection ss = (IStructuredSelection) parameters.getSelection();
					queryFmdt.removePrompt((Prompt) ss.getFirstElement());
					parameters.setInput(queryFmdt.getPrompts());
					listeners.firePropertyChange(QUERY_CHANGED, null, queryFmdt);
				}
			}
		});

		Label l2 = new Label(this, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l2.setText(Messages.CompositeFmdtQuery_5);
		l2.setBackground(getParent().getBackground());

		treeColumns = new TreeViewer(this, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI);
		treeColumns.getTree().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		treeColumns.setLabelProvider(new TreeLabelProvider());
		treeColumns.setContentProvider(new TreeContentProvider());

		listColumns = new ListViewer(this, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI);
		listColumns.getList().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		listColumns.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				return ((IDataStreamElement) element).getName();
			}

		});
		listColumns.setContentProvider(new IStructuredContentProvider() {

			public Object[] getElements(Object inputElement) {
				List<IDataStreamElement> l = (List<IDataStreamElement>) inputElement;
				return l.toArray(new IDataStreamElement[l.size()]);
			}

			public void dispose() {
				

			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				

			}

		});
		listColumns.setInput(new ArrayList<IDataStreamElement>());

		prompts = new ListViewer(this, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI);
		prompts.getList().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		prompts.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(Object element) {
				if (element instanceof Prompt) {
					return ((Prompt) element).getOutputName();
				}
				return ""; //$NON-NLS-1$
			}

		});
		prompts.setContentProvider(new IStructuredContentProvider() {

			public Object[] getElements(Object inputElement) {
				List<Prompt> l = (List<Prompt>) inputElement;
				return l.toArray(new Prompt[l.size()]);
			}

			public void dispose() {
				

			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				

			}

		});

		parameters = new TableViewer(this, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);
		parameters.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		parameters.setContentProvider(new IStructuredContentProvider() {

			public Object[] getElements(Object inputElement) {
				List<Prompt> mp = (List<Prompt>) inputElement;
				return mp.toArray(new Prompt[mp.size()]);
			}

			public void dispose() {
				

			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				

			}

		});
		parameters.setLabelProvider(new ITableLabelProvider() {

			public Image getColumnImage(Object element, int columnIndex) {
				
				return null;
			}

			public String getColumnText(Object element, int columnIndex) {
				if (columnIndex == 0) {
					return ((Prompt) element).getOutputName();
				}
				else if (columnIndex == 1) {
					for (String s : promptValue.keySet()) {
						if (s.equals(((Prompt) element).getName())) {
							String r = ""; //$NON-NLS-1$
							for (String l : promptValue.get(s)) {
								r += ";" + l; //$NON-NLS-1$
							}
							return r;

						}
					}

				}
				return null;
			}

			public void addListener(ILabelProviderListener listener) {
				

			}

			public void dispose() {
				

			}

			public boolean isLabelProperty(Object element, String property) {
				
				return false;
			}

			public void removeListener(ILabelProviderListener listener) {
				

			}

		});

		parameters.getTable().setHeaderVisible(true);
		parameters.getTable().setLinesVisible(true);

		TableColumn promptName = new TableColumn(parameters.getTable(), SWT.NONE);
		promptName.setText(Messages.CompositeFmdtQuery_9);
		promptName.setWidth(200);

		TableColumn value = new TableColumn(parameters.getTable(), SWT.NONE);
		value.setText(Messages.CompositeFmdtQuery_10);
		value.setWidth(200);

		isDictinct = new Button(this, SWT.CHECK);
		isDictinct.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 3, 1));
		isDictinct.setText(Messages.CompositeFmdtQuery_11);

		selectionListener = new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				Object source = event.getSource();

				if (source == treeColumns) {
					prompts.removeSelectionChangedListener(this);
					prompts.setSelection(StructuredSelection.EMPTY);
					prompts.addSelectionChangedListener(this);
				}
				else if (source == parameters) {
					listColumns.removeSelectionChangedListener(this);
					listColumns.setSelection(StructuredSelection.EMPTY);
					listColumns.addSelectionChangedListener(this);
				}
				else if (source == prompts) {
					treeColumns.removeSelectionChangedListener(this);
					treeColumns.setSelection(StructuredSelection.EMPTY);
					treeColumns.addSelectionChangedListener(this);
				}
				else if (source == listColumns) {
					parameters.removeSelectionChangedListener(this);
					parameters.setSelection(StructuredSelection.EMPTY);
					parameters.addSelectionChangedListener(this);
				}

			}
		};

		parameters.addSelectionChangedListener(selectionListener);
		prompts.addSelectionChangedListener(selectionListener);
		listColumns.addSelectionChangedListener(selectionListener);
		treeColumns.addSelectionChangedListener(selectionListener);
	}

	private boolean listContains(IDataStreamElement e) {
		for (IDataStreamElement el : (List<IDataStreamElement>) listColumns.getInput()) {
			if (el == e) {
				return true;
			}
		}
		return false;
	}

	public IQuery getQuery() {
		return queryFmdt;
	}

	public HashMap<String, List<String>> getPromptValues() {

		return promptValue;
	}

	public void setQuery(IQuery queryFmdt, HashMap<String, List<String>> promptValues) {
		this.queryFmdt = queryFmdt;
		((List) listColumns.getInput()).clear();

		promptValue = promptValues;

		if (queryFmdt != null) {
			for (IDataStreamElement e : this.queryFmdt.getSelect()) {
				((List) listColumns.getInput()).add(e);
			}
			parameters.setInput(queryFmdt.getPrompts());

		}
		else {
			this.queryFmdt = new QuerySql();
			parameters.setInput(this.queryFmdt.getPrompts());
		}

		listColumns.refresh();

	}
}
