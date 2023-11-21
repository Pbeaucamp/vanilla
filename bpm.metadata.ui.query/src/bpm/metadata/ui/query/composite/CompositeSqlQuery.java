package bpm.metadata.ui.query.composite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.hibernate.engine.jdbc.internal.BasicFormatterImpl;

import bpm.metadata.MetaDataBuilder;
import bpm.metadata.layer.business.IBusinessPackage;
import bpm.metadata.layer.business.IBusinessTable;
import bpm.metadata.layer.business.RelationStrategy;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.misc.AggregateFormula;
import bpm.metadata.query.EffectiveQuery;
import bpm.metadata.query.Formula;
import bpm.metadata.query.Ordonable;
import bpm.metadata.query.QuerySql;
import bpm.metadata.query.SavedQuery;
import bpm.metadata.query.SqlQueryBuilder;
import bpm.metadata.resource.ComplexFilter;
import bpm.metadata.resource.Filter;
import bpm.metadata.resource.IFilter;
import bpm.metadata.resource.IResource;
import bpm.metadata.resource.ListOfValue;
import bpm.metadata.resource.Prompt;
import bpm.metadata.resource.SqlQueryFilter;
import bpm.metadata.ui.query.Activator;
import bpm.metadata.ui.query.dialogs.DialogLoadSavedQuery;
import bpm.metadata.ui.query.dialogs.DialogSavedQuery;
import bpm.metadata.ui.query.i18n.Messages;
import bpm.metadata.ui.query.resources.dialogs.DialogComputedColumn;
import bpm.metadata.ui.query.resources.dialogs.DialogRelationStrategySelection;
import bpm.metadata.ui.query.resources.dialogs.DialogResource;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class CompositeSqlQuery extends Composite {
	private static class QueryItem {
		private Ordonable col;
		private String agg;
		private String label;

		public QueryItem(Ordonable col, String agg, String label) {
			super();
			this.col = col;
			this.agg = agg;
			this.label = label;
		}
	}

	private static class Condition {
		private IResource r;

		Condition(IResource r) {
			this.r = r;
		}

		public String getOperator() {
			if (r instanceof Filter) {
				return "IN"; //$NON-NLS-1$
			}
			if (r instanceof ComplexFilter) {
				return ((ComplexFilter) r).getOperator();
			}
			if (r instanceof SqlQueryFilter) {
				return ((SqlQueryFilter) r).getQuery();
			}
			if (r instanceof Prompt) {
				return ((Prompt) r).getOperator();
			}
			return null;
		}

		public String getValues() {
			StringBuffer buf = new StringBuffer();
			if (r instanceof Prompt) {
				return "?"; //$NON-NLS-1$
			}
			if (r instanceof ComplexFilter) {
				for (String s : ((ComplexFilter) r).getValue()) {
					if (!buf.toString().isEmpty()) {
						buf.append(", "); //$NON-NLS-1$
					}
					buf.append(s);
				}
			}
			else if (r instanceof ComplexFilter) {
				for (String s : ((ComplexFilter) r).getValue()) {
					if (!buf.toString().isEmpty()) {
						buf.append(", "); //$NON-NLS-1$
					}
					buf.append(s);
				}
			}
			return buf.toString();
		}

		public boolean supportOperatorEdition() {
			return r instanceof ComplexFilter || r instanceof Prompt;
		}

		public void setOperator(String operator) {
			if (r instanceof Prompt) {
				((Prompt) r).setOperator(operator);
			}
			if (r instanceof ComplexFilter) {
				((ComplexFilter) r).setOperator(operator);
			}
		}

	}

	private static String[] aggregators = new String[] { "None", AggregateFormula.SUM, AggregateFormula.AVG, AggregateFormula.COUNT, AggregateFormula.COUNT_DISTINCT, AggregateFormula.MAX, AggregateFormula.MIN }; //$NON-NLS-1$

	private static class FmdtLabelProvider extends LabelProvider {
		@Override
		public String getText(Object element) {
			if (element instanceof IBusinessTable) {
				return ((IBusinessTable) element).getName();
			}
			else if (element instanceof IResource) {
				return ((IResource) element).getOutputName();
			}
			else if (element instanceof Ordonable) {
				return ((Ordonable) element).getOutputName();
			}
			return super.getText(element);
		}

		@Override
		public Image getImage(Object obj) {
			if (obj instanceof IBusinessTable) {
				return Activator.getDefault().getImageRegistry().get("table"); //$NON-NLS-1$
			}
			else if (obj instanceof IDataStreamElement) {
				return Activator.getDefault().getImageRegistry().get("column"); //$NON-NLS-1$
			}
			else if (obj instanceof Prompt) {
				return Activator.getDefault().getImageRegistry().get("prompt"); //$NON-NLS-1$
			}
			else if (obj instanceof IFilter) {
				if (obj instanceof ComplexFilter) {
					return Activator.getDefault().getImageRegistry().get("complexFilter"); //$NON-NLS-1$
				}
				else if (obj instanceof SqlQueryFilter) {
					return Activator.getDefault().getImageRegistry().get("sqlFilter"); //$NON-NLS-1$

				}
				else if (obj instanceof Filter) {
					return Activator.getDefault().getImageRegistry().get("filter"); //$NON-NLS-1$
				}

				return Activator.getDefault().getImageRegistry().get("filter"); //$NON-NLS-1$

			}
			else if (obj instanceof IResource) {
				if (obj instanceof ListOfValue) {
					return Activator.getDefault().getImageRegistry().get("lov-16"); //$NON-NLS-1$
				}
				return Activator.getDefault().getImageRegistry().get("lov-16"); //$NON-NLS-1$
			}
			return null;
		}
	}

	private static class DialogSql extends Dialog {
		private Text text;
		private String sql;

		protected DialogSql(Shell parentShell, String query) {
			super(parentShell);
			setShellStyle(getShellStyle() | SWT.RESIZE);
			this.sql = query;
		}

		@Override
		protected void createButtonsForButtonBar(Composite parent) {
			createButton(parent, IDialogConstants.OK_ID, Messages.CompositeSqlQuery_1, true);
		}

		@Override
		protected Control createDialogArea(Composite parent) {
			text = new Text(parent, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
			text.setLayoutData(new GridData(GridData.FILL_BOTH));
			text.setEditable(false);
			return text;
		}

		@Override
		protected void initializeBounds() {
			getShell().setText(Messages.CompositeSqlQuery_0);
			getShell().setSize(600, 400);
			text.setText(sql);
		}
	}

	private IBusinessPackage fmdtPakage;
	private String vanillaGroupName;
	private List<IResource> ontheFlyResources = new ArrayList<IResource>();

	private List<RelationStrategy> selectedStrategies = new ArrayList<RelationStrategy>();
	
	private TreeViewer fmdtViewer;
	private TableViewer columnViewer;
	private TableViewer filterViewer;

	private Button distinct, bLimit;
	private Text limit;

	private boolean fromFmdt;
	
	private IContextManager contextManager;
	private IRepositoryContext context;
	private int itemId;
	
	private Text filterText;
	
	private String actualFilter = "";

	public CompositeSqlQuery(Composite parent, int style) {
		super(parent, style);
		buildContent();
	}

	public CompositeSqlQuery(Composite parent, int style, boolean fromFmdt, IContextManager contextManager) {
		super(parent, style);
		this.fromFmdt = fromFmdt;
		this.contextManager = contextManager;
		buildContent();
	}

	private void buildContent() {
		setLayout(new GridLayout(2, false));
		
		Composite searchComp = new Composite(this, SWT.NONE);
		searchComp.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		searchComp.setLayout(new GridLayout(2, false));
		
		filterText = new Text(searchComp, SWT.BORDER);
		filterText.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		
		Button btnSearch = new Button(searchComp, SWT.PUSH);
		btnSearch.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false));
		btnSearch.setText("Search");
		btnSearch.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				actualFilter = filterText.getText();
				fmdtViewer.refresh();
			}
		});
		
//		Label emptyLbl = new Label(this, SWT.NONE);
//		emptyLbl.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 2, 1));
		


		Composite queryC = new Composite(this, SWT.NONE);
		queryC.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 2));
		queryC.setLayout(new GridLayout(2, false));
		
		fmdtViewer = new TreeViewer(this, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.MULTI);
		fmdtViewer.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		createFmdtViewer();

		ToolBar colsBar = new ToolBar(queryC, SWT.VERTICAL);
		colsBar.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, true, 1, 2));
		
		Composite c = new Composite(queryC, SWT.NONE);
		c.setLayout(new FillLayout(SWT.VERTICAL));
		c.setLayoutData(new GridData(GridData.FILL_BOTH));

		columnViewer = new TableViewer(c, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);
		columnViewer.setContentProvider(new ArrayContentProvider());
		createColumnViewer();

		filterViewer = new TableViewer(c, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);
		filterViewer.setContentProvider(new ArrayContentProvider());
		filterViewer.setInput(new ArrayList<Object>());
		createFilterViewer();
		createToolbar(colsBar);

		Composite queryOpts = new Composite(queryC, SWT.NONE);
		queryOpts.setLayoutData(new GridData(GridData.FILL, GridData.END, true, false));
		queryOpts.setLayout(new GridLayout(3, false));

		distinct = new Button(queryOpts, SWT.CHECK);
		distinct.setLayoutData(new GridData());
		distinct.setText(Messages.CompositeSqlQuery_13);

		bLimit = new Button(queryOpts, SWT.CHECK);
		bLimit.setLayoutData(new GridData());
		bLimit.setText(Messages.CompositeSqlQuery_14);
		bLimit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				limit.setEnabled(bLimit.getSelection());
			}
		});

		limit = new Text(queryOpts, SWT.BORDER);
		limit.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		limit.setEnabled(false);
		limit.setText("5"); //$NON-NLS-1$
	}

	private void createFilterViewer() {
		filterViewer.getTable().setHeaderVisible(true);
		filterViewer.getTable().setLinesVisible(true);

		TableViewerColumn col = new TableViewerColumn(filterViewer, SWT.NONE);
		col.getColumn().setWidth(150);
		col.getColumn().setText(Messages.CompositeSqlQuery_16);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((Condition) element).r.getOutputName();
			}

			@Override
			public Image getImage(Object element) {
				Object obj = ((Condition) element).r;
				if (obj instanceof Prompt) {
					return Activator.getDefault().getImageRegistry().get("prompt"); //$NON-NLS-1$
				}
				if (obj instanceof IFilter) {
					if (obj instanceof ComplexFilter) {
						return Activator.getDefault().getImageRegistry().get("complexFilter"); //$NON-NLS-1$
					}
					else if (obj instanceof SqlQueryFilter) {
						return Activator.getDefault().getImageRegistry().get("sqlFilter"); //$NON-NLS-1$

					}
					else if (obj instanceof Filter) {
						return Activator.getDefault().getImageRegistry().get("filter"); //$NON-NLS-1$
					}

					return Activator.getDefault().getImageRegistry().get("filter"); //$NON-NLS-1$

				}
				return null;
			}
		});

		col = new TableViewerColumn(filterViewer, SWT.NONE);
		col.getColumn().setWidth(150);
		col.getColumn().setText(Messages.CompositeSqlQuery_21);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Condition c = (Condition) element;
				if (c.r instanceof IFilter) {
					return ((IFilter) c.r).getOrigin().getOuputName();
				}
				else if (c.r instanceof Prompt) {
					if (((Prompt) c.r).getGotoDataStreamElement() != null) {
						return ((Prompt) c.r).getGotoDataStreamElement().getOuputName();
					}
					else {
						return "SqlPrompt"; //$NON-NLS-1$
					}
				}
				return ""; //$NON-NLS-1$
			}
		});

		col = new TableViewerColumn(filterViewer, SWT.NONE);
		col.getColumn().setWidth(100);
		col.getColumn().setText(Messages.CompositeSqlQuery_24);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Condition c = (Condition) element;
				return c.getOperator();
			}
		});

		col = new TableViewerColumn(filterViewer, SWT.NONE);
		col.getColumn().setWidth(100);
		col.getColumn().setText(Messages.CompositeSqlQuery_25);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Condition c = (Condition) element;
				return c.getValues();
			}
		});

	}

	private void createColumnViewer() {
		columnViewer.getTable().setHeaderVisible(true);
		columnViewer.getTable().setLinesVisible(true);

		TableViewerColumn col = new TableViewerColumn(columnViewer, SWT.NONE);
		col.getColumn().setWidth(150);
		col.getColumn().setText(Messages.CompositeSqlQuery_26);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((QueryItem) element).col.getOutputName();
			}
		});

		col = new TableViewerColumn(columnViewer, SWT.NONE);
		col.getColumn().setWidth(100);
		col.getColumn().setText(Messages.CompositeSqlQuery_27);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				QueryItem agg = (QueryItem) element;
				if (agg.agg == null) {
					return ""; //$NON-NLS-1$
				}
				else {
					return agg.agg;
				}
			}
		});

		col.setEditingSupport(new EditingSupport(columnViewer) {
			ComboBoxCellEditor aggEditor = new ComboBoxCellEditor(columnViewer.getTable(), aggregators, SWT.READ_ONLY);

			@Override
			protected void setValue(Object element, Object value) {
				Integer v = (Integer) value;
				if (v.intValue() == 0) {
					((QueryItem) element).agg = null;
				}
				else {
					((QueryItem) element).agg = aggregators[v.intValue()];
					if (((QueryItem) element).label == null) {
						((QueryItem) element).label = new String(((QueryItem) element).col.getOutputName());
					}
				}
				columnViewer.refresh();
			}

			@Override
			protected Object getValue(Object element) {
				QueryItem f = (QueryItem) element;
				if (f.agg == null) {
					return 0;
				}
				for (int i = 1; i < aggregators.length; i++) {
					if (aggregators[i].equals(f.agg)) {
						return i;
					}
				}
				return 0;
			}

			@Override
			protected CellEditor getCellEditor(Object element) {
				return aggEditor;
			}

			@Override
			protected boolean canEdit(Object element) {
				return true;
			}
		});

		col = new TableViewerColumn(columnViewer, SWT.NONE);
		col.getColumn().setWidth(150);
		col.getColumn().setText(Messages.CompositeSqlQuery_29);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (((QueryItem) element).agg == null) {
					return ((QueryItem) element).col.getOutputName();
				}
				return ((QueryItem) element).label;
			}
		});
		col.setEditingSupport(new EditingSupport(columnViewer) {
			TextCellEditor editor = new TextCellEditor(columnViewer.getTable(), SWT.NONE);

			@Override
			protected void setValue(Object element, Object value) {
				((QueryItem) element).label = (String) value;
				columnViewer.refresh();

			}

			@Override
			protected Object getValue(Object element) {

				return ((QueryItem) element).label;
			}

			@Override
			protected CellEditor getCellEditor(Object element) {
				return editor;
			}

			@Override
			protected boolean canEdit(Object element) {

				return ((QueryItem) element).agg != null;
			}
		});

		col = new TableViewerColumn(columnViewer, SWT.NONE);
		col.getColumn().setWidth(100);
		col.getColumn().setText(Messages.CompositeSqlQuery_30);
		col.setLabelProvider(new ColumnLabelProvider() {
			@SuppressWarnings("unchecked")
			@Override
			public String getText(Object element) {
				int pos = ((List) columnViewer.getInput()).indexOf(element);
				return "" + (pos + 1); //$NON-NLS-1$
			}
		});
	}
	
	private boolean keepTable(IBusinessTable table) {
		for(IBusinessTable sub : table.getChilds(vanillaGroupName)) {
			for(IDataStreamElement elem : sub.getColumns(vanillaGroupName)) {
				if(keepColumn(elem)) {
					return true;
				}
			}
		}
		for(IDataStreamElement elem : table.getColumns(vanillaGroupName)) {
			if(keepColumn(elem)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean keepColumn(IDataStreamElement element) {
		if(element.getOuputName().toLowerCase().contains(actualFilter.toLowerCase())) {
			return true;
		}
		return false;
	}
	
	private boolean keepResource(IResource resource) {
		if(resource.getOutputName().toLowerCase().contains(actualFilter.toLowerCase())) {
			return true;
		}
		return false;
	}

	private void createFmdtViewer() {
		fmdtViewer.setContentProvider(new ITreeContentProvider() {

			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}

			@Override
			public void dispose() {
			}

			@Override
			public Object[] getElements(Object inputElement) {
				IBusinessPackage p = (IBusinessPackage) inputElement;
				List<Object> l = new ArrayList<Object>();
				String groupName = getGroupName();
				
				for(IBusinessTable t : p.getOrderedTables(groupName)) {
					if(keepTable(t)) {
						l.add(t);
					}
				}
				
				for(IResource r : p.getResources(groupName)){
					if(keepResource(r)) {
						l.add(r);
					}
				}
				
				return l.toArray(new Object[l.size()]);
			}

			@Override
			public boolean hasChildren(Object element) {
				if (element instanceof IBusinessTable) {
					String groupName = getGroupName();
					return ((IBusinessTable) element).getChilds(groupName).size() + ((IBusinessTable) element).getColumns(groupName).size() > 0;
				}
				return false;
			}

			@Override
			public Object getParent(Object element) {
				return null;
			}

			@Override
			public Object[] getChildren(Object parentElement) {
				List<Object> l = new ArrayList<Object>();

				if (parentElement instanceof IBusinessTable) {
					String groupName = getGroupName();
					
					for(IBusinessTable t : ((IBusinessTable) parentElement).getChilds(groupName)) {
						if(keepTable(t)) {
							l.add(t);
						}
					}
					
					for(IDataStreamElement elem : ((IBusinessTable) parentElement).getColumnsOrdered(groupName)) {
						if(keepColumn(elem)) {
							l.add(elem);
						}
					}
				}
				return l.toArray(new Object[l.size()]);
			}
		});
		fmdtViewer.setLabelProvider(new FmdtLabelProvider());
	}

	private void createToolbar(ToolBar parent) {
		final ToolItem add = new ToolItem(parent, SWT.PUSH);
		add.setImage(Activator.getDefault().getImageRegistry().get("addCol")); //$NON-NLS-1$
		add.setToolTipText(Messages.CompositeSqlQuery_32);
		add.addSelectionListener(new SelectionAdapter() {
			@SuppressWarnings("unchecked")
			final boolean viewerContains(Ordonable o) {
				for (Object d : ((List) columnViewer.getInput())) {
					if (d instanceof QueryItem) {
						if (((QueryItem) d).col == o) {
							return true;
						}
					}
				}
				return false;
			}

			@SuppressWarnings("unchecked")
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection ss = (IStructuredSelection) fmdtViewer.getSelection();
				for (Object o : ss.toList()) {
					if (o instanceof IResource) {
						if (filterViewer.getInput() == null) {
							filterViewer.setInput(new ArrayList<Object>());
						}
						((List) filterViewer.getInput()).add(new Condition((IResource) o));
					}

					else {
						if (columnViewer.getInput() == null) {
							columnViewer.setInput(new ArrayList<Object>());
						}

						if (o instanceof IBusinessTable) {
							for (Ordonable c : ((IBusinessTable) o).getColumns(getGroupName())) {
								if (!viewerContains(c)) {
									((List) columnViewer.getInput()).add(new QueryItem(c, null, null));
								}

							}

						}
						else {
							if (!viewerContains((Ordonable) o)) {
								((List) columnViewer.getInput()).add(new QueryItem((Ordonable) o, null, null));
							}

						}
					}

				}
				filterViewer.refresh();
				columnViewer.refresh();
			}
		});
		add.setEnabled(false);

		final ToolItem del = new ToolItem(parent, SWT.PUSH);
		del.setImage(Activator.getDefault().getImageRegistry().get("delCol")); //$NON-NLS-1$
		del.setToolTipText(Messages.CompositeSqlQuery_33);
		del.addSelectionListener(new SelectionAdapter() {
			@SuppressWarnings("unchecked")
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection ss = (IStructuredSelection) columnViewer.getSelection();
				for (Object o : ss.toList()) {
					if (o instanceof QueryItem) {
						((List) columnViewer.getInput()).remove(o);
					}

				}
				columnViewer.refresh();
			}
		});
		del.setEnabled(false);

		final ToolItem calc = new ToolItem(parent, SWT.PUSH);
		calc.setImage(Activator.getDefault().getImageRegistry().get("calc")); //$NON-NLS-1$
		calc.setToolTipText(Messages.CompositeSqlQuery_34);
		calc.addSelectionListener(new SelectionAdapter() {
			@SuppressWarnings("unchecked")
			@Override
			public void widgetSelected(SelectionEvent e) {
				DialogComputedColumn d = new DialogComputedColumn(getShell(), fmdtPakage, getGroupName());
				if (d.open() == DialogComputedColumn.OK) {
					QueryItem it = new QueryItem(d.getFormula(), null, d.getFormula().getName());
					if (columnViewer.getInput() == null) {
						columnViewer.setInput(new ArrayList<Object>());
					}
					((List) columnViewer.getInput()).add(it);
				}

				columnViewer.refresh();
			}
		});

		final ToolItem editcalc = new ToolItem(parent, SWT.PUSH);
		editcalc.setImage(Activator.getDefault().getImageRegistry().get("edit")); //$NON-NLS-1$
		editcalc.setToolTipText(Messages.CompositeSqlQuery_35);
		editcalc.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				QueryItem it = (QueryItem) ((IStructuredSelection) columnViewer.getSelection()).getFirstElement();
				DialogComputedColumn d = new DialogComputedColumn(getShell(), fmdtPakage, getGroupName(), (Formula) it.col);
				if (d.open() == DialogComputedColumn.OK) {
				}

				columnViewer.refresh();
			}
		});

		final ToolItem newComplex = new ToolItem(parent, SWT.PUSH);
		newComplex.setToolTipText(Messages.CompositeSqlQuery_36);
		newComplex.setImage(Activator.getDefault().getImageRegistry().get("complexFilter")); //$NON-NLS-1$
		newComplex.addSelectionListener(new SelectionAdapter() {
			@SuppressWarnings("unchecked")
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection ss = (IStructuredSelection) fmdtViewer.getSelection();
				IDataStreamElement col = (IDataStreamElement) ss.getFirstElement();

				ComplexFilter filter = new ComplexFilter();
				filter.setName("newFilter"); //$NON-NLS-1$
				filter.setOrigin(col);
				filter.setOperator(ComplexFilter.OPERATORS[0]);

				DialogResource d = new DialogResource(getShell(), filter);
				if (d.open() == DialogResource.OK) {
					ontheFlyResources.add(filter);
					((List) filterViewer.getInput()).add(new Condition(filter));
					filterViewer.refresh();
				}

			}
		});
		newComplex.setEnabled(false);

		final ToolItem newSql = new ToolItem(parent, SWT.PUSH);
		newSql.setToolTipText(Messages.CompositeSqlQuery_38);
		newSql.setImage(Activator.getDefault().getImageRegistry().get("sqlFilter")); //$NON-NLS-1$
		newSql.addSelectionListener(new SelectionAdapter() {
			@SuppressWarnings("unchecked")
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection ss = (IStructuredSelection) fmdtViewer.getSelection();
				IDataStreamElement col = (IDataStreamElement) ss.getFirstElement();

				SqlQueryFilter filter = new SqlQueryFilter();
				filter.setName("newFilter"); //$NON-NLS-1$
				filter.setOrigin(col);

				DialogResource d = new DialogResource(getShell(), filter);
				if (d.open() == DialogResource.OK) {
					ontheFlyResources.add(filter);
					((List) filterViewer.getInput()).add(new Condition(filter));
					filterViewer.refresh();
				}
			}
		});
		newSql.setEnabled(false);

		final ToolItem newPrompt = new ToolItem(parent, SWT.PUSH);
		newPrompt.setToolTipText(Messages.CompositeSqlQuery_40);
		newPrompt.setImage(Activator.getDefault().getImageRegistry().get("prompt")); //$NON-NLS-1$
		newPrompt.addSelectionListener(new SelectionAdapter() {
			@SuppressWarnings("unchecked")
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection ss = (IStructuredSelection) fmdtViewer.getSelection();
				IDataStreamElement col = (IDataStreamElement) ss.getFirstElement();

				Prompt filter = new Prompt();
				filter.setName("promptOn" + col.getOuputName()); //$NON-NLS-1$
				filter.setOrigin(col);
				filter.setGotoDataStreamElement(col);

				DialogResource d = new DialogResource(getShell(), filter);
				if (d.open() == DialogResource.OK) {
					ontheFlyResources.add(filter);
					((List) filterViewer.getInput()).add(new Condition(filter));
					filterViewer.refresh();
				}
			}
		});
		newPrompt.setEnabled(false);

		final ToolItem editResource = new ToolItem(parent, SWT.PUSH);
		editResource.setImage(Activator.getDefault().getImageRegistry().get("edit")); //$NON-NLS-1$
		editResource.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection ss = (IStructuredSelection) filterViewer.getSelection();
				Condition c = (Condition) ss.getFirstElement();

				DialogResource d = new DialogResource(getShell(), c.r);
				if (d.open() == DialogResource.OK) {
					filterViewer.refresh();
				}
			}
		});
		editResource.setEnabled(false);

		final ToolItem del2 = new ToolItem(parent, SWT.PUSH);
		del2.setImage(Activator.getDefault().getImageRegistry().get("delCol")); //$NON-NLS-1$
		del2.setToolTipText(Messages.CompositeSqlQuery_42);
		del2.addSelectionListener(new SelectionAdapter() {
			@SuppressWarnings("unchecked")
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection ss = (IStructuredSelection) filterViewer.getSelection();
				for (Object o : ss.toList()) {
					if (o instanceof Condition) {
						((List) filterViewer.getInput()).remove(o);
					}

				}
				filterViewer.refresh();
			}
		});
		del2.setEnabled(false);

		final ToolItem relationStrats = new ToolItem(parent, SWT.PUSH);
		relationStrats.setImage(Activator.getDefault().getImageRegistry().get("relstrat")); //$NON-NLS-1$
		relationStrats.setToolTipText(Messages.CompositeSqlQuery_43);
		relationStrats.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DialogRelationStrategySelection dial = new DialogRelationStrategySelection(getShell(), selectedStrategies, fmdtPakage);
				if(dial.open() == Dialog.OK) {
					selectedStrategies = dial.getSelectedStrategies();
				}
			}
		});
		
		final ToolItem sql = new ToolItem(parent, SWT.PUSH);
		sql.setImage(Activator.getDefault().getImageRegistry().get("sql")); //$NON-NLS-1$
		sql.setToolTipText(Messages.CompositeSqlQuery_44);
		sql.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				QuerySql query = createFmdtQuery();

				if (!fmdtPakage.getBusinessModel().getModel().isBuilt()) {
					MetaDataBuilder builder = new MetaDataBuilder(null);
					try {
						builder.build(fmdtPakage.getBusinessModel().getModel(), null, getGroupName());
					} catch (Exception ex) {
						ex.printStackTrace();
						MessageDialog.openError(getShell(), Messages.CompositeSqlQuery_45, Messages.CompositeSqlQuery_46 + "\n" + ex.getMessage());
						return;
					}

				}

				try {
					List<List<String>> prompts = new ArrayList<List<String>>(query.getPrompts().size());
					for (int i = 0; i < query.getPrompts().size(); i++) {
						prompts.add(new ArrayList<String>());
					}

					IVanillaContext ctx = contextManager != null ? contextManager.getVanillaContext() : (context != null ? context.getVanillaContext() : null);
					EffectiveQuery sqlQuery = fmdtPakage.evaluateQuery(ctx, query, prompts, false);
					String resQuery;
					try {
						resQuery = new BasicFormatterImpl().format(sqlQuery.getGeneratedQuery());
					} catch(Exception e1) {
						resQuery = sqlQuery.getGeneratedQuery();
					}
					
					DialogSql d = new DialogSql(getShell(), resQuery.replace("`", "\""));
					d.open();
				} catch (Exception ex) {
					ex.printStackTrace();
					MessageDialog.openError(getShell(), Messages.CompositeSqlQuery_2, Messages.CompositeSqlQuery_50 + "\n" + ex.getMessage());
				}

			}
		});
		
		final ToolItem rowCount = new ToolItem(parent, SWT.PUSH);
		rowCount.setImage(Activator.getDefault().getImageRegistry().get("count")); //$NON-NLS-1$
		rowCount.setToolTipText(Messages.CompositeSqlQuery_51);
		rowCount.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				QuerySql query = createFmdtQuery();

				if (!fmdtPakage.getBusinessModel().getModel().isBuilt()) {
					MetaDataBuilder builder = new MetaDataBuilder(null);
					try {
						builder.build(fmdtPakage.getBusinessModel().getModel(), null, getGroupName());
					} catch (Exception ex) {
						ex.printStackTrace();
						MessageDialog.openError(getShell(), Messages.CompositeSqlQuery_52, Messages.CompositeSqlQuery_53 + "\n" + ex.getMessage());
						return;
					}

				}

				try {
					
					List<List<String>> prompts = new ArrayList<List<String>>(query.getPrompts().size());

					int promptCount = query.getPrompts().size();
					
					List<Prompt> toRm = query.getPrompts(); 
					for(Prompt p : query.getPrompts()) {
						toRm.add(p);
					}
					for(Prompt p : toRm) {
						query.removePrompt(p);
					}
					
					EffectiveQuery sqlQuery = fmdtPakage.evaluateQuery(null, query, prompts);
					
					String queryTocount = sqlQuery.getGeneratedQuery().replace("`", "\""); //$NON-NLS-1$ //$NON-NLS-2$
					
					String newQuery = "select count(*) from (" + queryTocount + ") countTable"; //$NON-NLS-1$ //$NON-NLS-2$
					
					List<List<String>> res = fmdtPakage.executeQuery(0, "Default", newQuery); //$NON-NLS-1$
					
					String count = res.get(0).get(0);
					
					String additionalMessage = ""; //$NON-NLS-1$
					if(promptCount > 0) {
						additionalMessage += "\n" + Messages.CompositeSqlQuery_60;
					}
					
					MessageDialog.openInformation(getShell(), Messages.CompositeSqlQuery_61, Messages.CompositeSqlQuery_62 + count + Messages.CompositeSqlQuery_63 + additionalMessage);
					
				} catch (Exception ex) {
					ex.printStackTrace();
					MessageDialog.openError(getShell(), Messages.CompositeSqlQuery_64, Messages.CompositeSqlQuery_65 + "\n" + ex.getMessage());
				}
				
			}
			
		});
		
		final ToolItem up = new ToolItem(parent, SWT.PUSH);
		up.setImage(Activator.getDefault().getImageRegistry().get("up")); //$NON-NLS-1$
		up.setToolTipText(Messages.CompositeSqlQuery_66);
		up.addSelectionListener(new SelectionAdapter() {
			@SuppressWarnings("unchecked")
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection ss = (IStructuredSelection) columnViewer.getSelection();
				int i = ((List) columnViewer.getInput()).indexOf(ss.getFirstElement());
				if (i > 0) {
					Object old = ((List) columnViewer.getInput()).get(i - 1);
					((List) columnViewer.getInput()).set(i - 1, ss.getFirstElement());
					((List) columnViewer.getInput()).set(i, old);
					columnViewer.refresh();
				}
			}
		});
		up.setEnabled(false);

		final ToolItem down = new ToolItem(parent, SWT.PUSH);
		down.setImage(Activator.getDefault().getImageRegistry().get("down")); //$NON-NLS-1$
		down.setToolTipText(Messages.CompositeSqlQuery_67);
		down.addSelectionListener(new SelectionAdapter() {
			@SuppressWarnings("unchecked")
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection ss = (IStructuredSelection) columnViewer.getSelection();
				int i = ((List) columnViewer.getInput()).indexOf(ss.getFirstElement());
				if (i < ((List) columnViewer.getInput()).size() + 1) {
					Object old = ((List) columnViewer.getInput()).get(i + 1);
					((List) columnViewer.getInput()).set(i + 1, ss.getFirstElement());
					((List) columnViewer.getInput()).set(i, old);
					columnViewer.refresh();
				}
			}
		});
		down.setEnabled(false);
		
		final ToolItem save = new ToolItem(parent, SWT.PUSH);
		save.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_ETOOL_SAVE_EDIT)); //$NON-NLS-1$
		save.setToolTipText(Messages.CompositeSqlQuery_68);
		if(fromFmdt) {
			save.addSelectionListener(new SelectionAdapter() {
				
				@Override
				public void widgetSelected(SelectionEvent e) {
					DialogSavedQuery dial = new DialogSavedQuery(getShell());
					if(dial.open() == Dialog.OK) {
						String savedName = dial.getSavedName();
						String savedDescription = dial.getSavedDescription();
						if(savedName != null && !savedName.isEmpty()) {
							QuerySql query = createFmdtQuery();
							fmdtPakage.addSavedQuery(new SavedQuery(savedName, savedDescription, query));
						}
						else {
							MessageDialog.openInformation(getShell(), "Empty Name", "You need to fill the name field to save the query.");
						}
					}
				}
			});
		}
		else {
			save.addSelectionListener(new SelectionAdapter() {
				
				@Override
				public void widgetSelected(SelectionEvent e) {
					DialogSavedQuery dial = new DialogSavedQuery(getShell());
					if(dial.open() == Dialog.OK) {
						String savedName = dial.getSavedName();
						String savedDescription = dial.getSavedDescription();
						if(savedName != null && !savedName.isEmpty()) {
							QuerySql query = createFmdtQuery();
							fmdtPakage.addSavedQuery(new SavedQuery(savedName, savedDescription, query));
							
							//XXX update metadata on the repository
							if(context != null) {
								try {
									String xml = fmdtPakage.getBusinessModel().getModel().getXml(false);
									
									IRepositoryApi api = new RemoteRepositoryApi(context);
									
									RepositoryItem item = api.getRepositoryService().getDirectoryItem(itemId);
									
									api.getRepositoryService().updateModel(item, xml);
									
								} catch (Exception e1) {
									e1.printStackTrace();
									
									MessageDialog.openInformation(getShell(), "Save failed", "Error while saving the query. " + e1.getMessage());
								}
							}
							else {
								MessageDialog.openInformation(getShell(), "Context null", "You need to be connected to Vanilla to save the query.");
							}
						}
//						else {
//							MessageDialog.openInformation(getShell(), "Empty Name", "You need to fill the name field to save the query.");
//						}
					else {
						MessageDialog.openInformation(getShell(), Messages.CompositeSqlQuery_69, Messages.CompositeSqlQuery_70);

					}
				}
				}
			});
		}
		
		final ToolItem loadQuery = new ToolItem(parent, SWT.PUSH);
		loadQuery.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER)); //$NON-NLS-1$
		loadQuery.setToolTipText(Messages.CompositeSqlQuery_71);
		loadQuery.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				List<SavedQuery> queries = fmdtPakage.getSavedQueries();
				if(queries != null && !queries.isEmpty()) {
					DialogLoadSavedQuery dial = new DialogLoadSavedQuery(getShell(), queries);
					if(dial.open() == Dialog.OK) {
						SavedQuery savedQuery = dial.getSelectedQuery();
						if(savedQuery != null) {
							try {
								fill(savedQuery.loadQuery(vanillaGroupName, fmdtPakage));
							} catch (Exception e1) {
								e1.printStackTrace();
								MessageDialog.openError(getShell(), Messages.CompositeSqlQuery_72, e1.getMessage());
							}
						}
						else {
							MessageDialog.openInformation(getShell(), Messages.CompositeSqlQuery_73, Messages.CompositeSqlQuery_74);
						}
					}
				}
				else {
					MessageDialog.openInformation(getShell(), Messages.CompositeSqlQuery_75, Messages.CompositeSqlQuery_76);
				}
			}
		});

		fmdtViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection ss = (IStructuredSelection) event.getSelection();
				boolean disable = ss.isEmpty() || !(ss.getFirstElement() instanceof IDataStreamElement);
				add.setEnabled(!disable || ss.getFirstElement() instanceof IResource);
				if (!add.isEnabled()) {
					for (Object o : ss.toList()) {
						if (o instanceof IBusinessTable) {
							add.setEnabled(true);
							break;
						}
					}
				}
				newComplex.setEnabled(!disable);
				newPrompt.setEnabled(!disable);
				newSql.setEnabled(!disable);
			}
		});

		filterViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection ss = (IStructuredSelection) event.getSelection();
				boolean disable = ss.isEmpty();
				editResource.setEnabled(!disable && ontheFlyResources.contains(((Condition) ss.getFirstElement()).r));
				del2.setEnabled(!ss.isEmpty());
			}
		});

		columnViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection ss = (IStructuredSelection) event.getSelection();
				del.setEnabled(!ss.isEmpty());
				down.setEnabled(!ss.isEmpty());
				up.setEnabled(!ss.isEmpty());
				if (!ss.isEmpty()) {
					QueryItem i = (QueryItem) ss.getFirstElement();
					editcalc.setEnabled(i.col instanceof Formula);
				}
			}
		});
	}

	@SuppressWarnings("unchecked")
	public QuerySql createFmdtQuery() {
		List<IDataStreamElement> cols = new ArrayList<IDataStreamElement>();
		List<AggregateFormula> aggs = new ArrayList<AggregateFormula>();
		List<Formula> formulas = new ArrayList<Formula>();
		List<IFilter> filters = new ArrayList<IFilter>();
		List<Prompt> prompts = new ArrayList<Prompt>();

		for (QueryItem i : (List<QueryItem>) columnViewer.getInput()) {
			if (i.agg == null) {
				if (i.col instanceof Formula) {
					formulas.add((Formula) i.col);
				}
				else if (i.col instanceof IDataStreamElement) {
					cols.add((IDataStreamElement) i.col);
				}
			}
			else {
				if (i.col instanceof IDataStreamElement) {
					aggs.add(new AggregateFormula(i.agg, (IDataStreamElement) i.col, i.label));
				}
				else {
					aggs.add(new AggregateFormula(i.agg, (Formula) i.col, i.label));
				}

			}
		}

		List<IResource> usedAddedResources = new ArrayList<IResource>();
		for (Condition i : (List<Condition>) filterViewer.getInput()) {
			if (!ontheFlyResources.contains(i.r)) {
				if (i.r instanceof Prompt) {
					prompts.add((Prompt) i.r);
				}
				else if (i.r instanceof IFilter) {
					filters.add((IFilter) i.r);
				}
			}
			else {
				usedAddedResources.add(i.r);
			}

		}
		List<Ordonable> orders = new ArrayList<Ordonable>();
		for (Object o : (List) columnViewer.getInput()) {
			orders.add(((QueryItem) o).col);
		}
		QuerySql query= (QuerySql)SqlQueryBuilder.getQuery(
				getGroupName(), 
				cols, 
				new HashMap<ListOfValue, String>(), 
				aggs, 
				orders, 
				filters, 
				prompts, formulas, selectedStrategies);

		for (IResource r : usedAddedResources) {
			query.addDesignTimeResource(r);
		}

		if (distinct.getSelection()) {
			query.setDistinct(true);
		}
		if (bLimit.getSelection()) {
			query.setLimit(Integer.parseInt(limit.getText()));
			limit.setEnabled(true);
		}

		return query;
	}

	private String getGroupName() {
		return vanillaGroupName;
	}

	public void setConnection(IBusinessPackage fmdtPack, String groupName) throws Exception {
		vanillaGroupName = groupName;
		fmdtPakage = fmdtPack;
		fmdtViewer.setInput(fmdtPakage);

	}

	public void fill(final QuerySql query) {
		List<QueryItem> cols = new ArrayList<QueryItem>();
		ontheFlyResources.clear();
		ontheFlyResources.addAll(query.getDesignTimeResources());

		distinct.setSelection(query.getDistinct());
		bLimit.setSelection(query.getLimit() > 0);
		if (query.getLimit() > 0) {
			limit.setText(query.getLimit() + ""); //$NON-NLS-1$
		}

		for (AggregateFormula agg : query.getAggs()) {
//			if (agg.isBasedOnFormula()) {
//				//agg.getCol()
//				cols.add(new QueryItem(new Formula(agg.getOutputName(), ((ICalculatedElement) agg.getCol()).getFormula(), agg.getInvolvedDataStreamNames()), agg.getFunction(), agg.getOutputName()));
//			}
//			else {
				cols.add(new QueryItem(agg.getCol(), agg.getFunction(), agg.getOutputName()));
//			}

		}
		for (IDataStreamElement c : query.getSelect()) {
			cols.add(new QueryItem(c, null, null));
		}
		for (Formula f : query.getFormulas()) {
			cols.add(new QueryItem(f, null, null));
		}

		/*
		 * apply ordering
		 */
		Collections.sort(cols, new Comparator<QueryItem>() {

			@Override
			public int compare(QueryItem o1, QueryItem o2) {
				Integer i1 = query.getOrderBy().indexOf(o1.col);
				Integer i2 = query.getOrderBy().indexOf(o2.col);
				return i1.compareTo(i2);
			}

		});

		columnViewer.setInput(cols);

		List<Condition> condition = new ArrayList<Condition>();
		for (IFilter f : query.getFilters()) {
			if (!ontheFlyResources.contains(f)) {
				condition.add(new Condition(f));
			}

		}
		for (Prompt f : query.getPrompts()) {
			if (!ontheFlyResources.contains(f)) {
				condition.add(new Condition(f));
			}
		}

		for (IResource r : query.getDesignTimeResources()) {
			condition.add(new Condition(r));

		}
		filterViewer.setInput(condition);
	}

	public void setConnection(IBusinessPackage fmdtPackage, String groupName, IRepositoryContext context, int itemId) throws Exception {
		setConnection(fmdtPackage, groupName);
		
		this.context = context;
		this.itemId = itemId;
	}
}
