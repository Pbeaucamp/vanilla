package bpm.birep.admin.client.historic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.datatools.connectivity.oda.IQuery;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import adminbirep.Activator;
import adminbirep.Messages;
import adminbirep.icons.Icons;
import bpm.birep.admin.client.content.views.ViewTree;
import bpm.birep.admin.client.trees.TreeItem;
import bpm.birep.admin.client.views.datalists.dialogs.DialogResultSetBrowser;
import bpm.birep.admin.client.views.datalists.wizards.oda.OdaDataSetWizard;
import bpm.birep.admin.client.views.datalists.wizards.oda.OdaDataSourceWizard;
import bpm.dataprovider.odainput.OdaInputDigester;
import bpm.dataprovider.odainput.consumer.OdaHelper;
import bpm.dataprovider.odainput.consumer.QueryHelper;
import bpm.vanilla.platform.core.beans.data.OdaInput;
import bpm.vanilla.platform.core.beans.ged.Definition;
import bpm.vanilla.platform.core.components.IGedComponent;
import bpm.vanilla.platform.core.remote.impl.components.RemoteGedComponent;
import bpm.vanilla.platform.core.repository.DatasProvider;

public class IndexationProvider extends ViewPart implements ISelectionListener {
	public static final String ID = "bpm.es.gedmanager.views.indexationprovider"; //$NON-NLS-1$

	private TableViewer tableViewer;
	private TableViewer linkedDatasProviders;
	private Action linkToReport;
	private DatasProvider currentDatasProvider;
	private ToolItem add, del, edit, browse, refresh;
	private String[] items;
	private IGedComponent gedComponent;

	public IndexationProvider() {
		try {
			Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().addSelectionListener(this);
			gedComponent = new RemoteGedComponent(Activator.getDefault().getVanillaApi().getVanillaUrl(), Activator.getDefault().getLogin(), Activator.getDefault().getUserPassword());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void dispose() {
		Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getSelectionService().removeSelectionListener(this);
		super.dispose();
	}

	@Override
	public void createPartControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(1, true));
		main.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

		createToolbar(main);
		createDatasProviderViewer(main);
		createLinkedProvidersViewer(main);
		updateDatasProviderViewer();
		setDnd();

	}

	private void setDnd() {
		int ops = DND.DROP_COPY;
		Transfer[] transfers = new Transfer[] { TextTransfer.getInstance() };

		DragSource dragSource = new DragSource(tableViewer.getControl(), ops);
		dragSource.setTransfer(transfers);
		dragSource.addDragListener(new DragSourceListener() {

			public void dragStart(DragSourceEvent event) {
				// nothing, DnD always possible
			}

			public void dragSetData(DragSourceEvent event) {
				StructuredSelection ss = (StructuredSelection) tableViewer.getSelection();
				Object o = ss.getFirstElement();

				if (o instanceof DatasProvider) {
					currentDatasProvider = (DatasProvider) o;
					event.data = currentDatasProvider.getId() + ""; //$NON-NLS-1$
				}

			}

			public void dragFinished(DragSourceEvent event) {
				event.data = null;
			}
		});

		DropTarget target = new DropTarget(linkedDatasProviders.getControl(), DND.DROP_COPY);
		target.setTransfer(new Transfer[] { TextTransfer.getInstance() });
		target.addDropListener(new DropTargetListener() {

			public void dropAccept(DropTargetEvent event) {

			}

			public void drop(DropTargetEvent event) {
				if (ViewTree.selectedObject != null && ViewTree.selectedObject instanceof TreeItem) {
					int itemId = ((TreeItem) ViewTree.selectedObject).getItem().getId();

					try {

						List<DatasProvider> currents = Activator.getDefault().getRepositoryApi().getDatasProviderService().getForItem(itemId);
						if (currents.contains(currentDatasProvider)) {
							return;
						}
						Activator.getDefault().getRepositoryApi().getDatasProviderService().link(currentDatasProvider.getId(), itemId);
						updateLinkedDatasProvidersViewer();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}

			public void dragOver(DropTargetEvent event) {

			}

			public void dragOperationChanged(DropTargetEvent event) {

			}

			public void dragLeave(DropTargetEvent event) {

			}

			public void dragEnter(DropTargetEvent event) {
				event.detail = DND.DROP_COPY;
			}
		});
	}

	private void createLinkedProvidersViewer(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(1, true));
		main.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

		ToolBar bar = new ToolBar(main, SWT.NONE);
		bar.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		ToolItem breaklink = new ToolItem(bar, SWT.NONE);
		breaklink.setToolTipText(Messages.IndexationProvider_1);
		breaklink.setImage(Activator.getDefault().getImageRegistry().get(Icons.DEL));

		breaklink.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = ((IStructuredSelection) linkedDatasProviders.getSelection());

				if (selection.isEmpty()) {
					return;
				}
				Object element = selection.getFirstElement();

				if (element == null || !(element instanceof DatasProvider) || ViewTree.selectedObject == null || !(ViewTree.selectedObject instanceof TreeItem)) {
					return;
				}
				int itemId = ((TreeItem) ViewTree.selectedObject).getItem().getId();
				int datasProviderId = ((DatasProvider) element).getId();

				try {
					Activator.getDefault().getRepositoryApi().getDatasProviderService().breakLink(itemId, datasProviderId);
					updateLinkedDatasProvidersViewer();

				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});

		Label l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		l.setText(Messages.IndexationProvider_2);

		linkedDatasProviders = new TableViewer(main, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.VIRTUAL | SWT.FULL_SELECTION);
		linkedDatasProviders.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1, 2));
		linkedDatasProviders.setContentProvider(new IStructuredContentProvider() {

			@Override
			@SuppressWarnings("unchecked")
			public Object[] getElements(Object inputElement) {
				List<DatasProvider> l = (List<DatasProvider>) inputElement;
				return l.toArray(new DatasProvider[l.size()]);
			}

			@Override
			public void dispose() {
			}

			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}

		});

		TableViewerColumn name = new TableViewerColumn(linkedDatasProviders, SWT.NONE);
		name.setLabelProvider(new CellLabelProvider() {

			@Override
			public void update(ViewerCell cell) {
				String s = ((DatasProvider) cell.getElement()).getName();
				cell.setText(s);

			}
		});
		name.getColumn().setText(Messages.IndexationProvider_3);
		name.getColumn().setWidth(100);

		TableViewerColumn description = new TableViewerColumn(linkedDatasProviders, SWT.NONE);
		description.setLabelProvider(new CellLabelProvider() {

			@Override
			public void update(ViewerCell cell) {
				String s = ((DatasProvider) cell.getElement()).getDescription();
				cell.setText(s);

			}
		});
		description.getColumn().setText(Messages.IndexationProvider_4);
		description.getColumn().setWidth(150);

		TableViewerColumn field = new TableViewerColumn(linkedDatasProviders, SWT.NONE);
		field.setLabelProvider(new CellLabelProvider() {

			@Override
			public void update(ViewerCell cell) {
				String s = ((DatasProvider) cell.getElement()).getFieldDefinitionName();
				cell.setText(s);

			}
		});
		field.getColumn().setText(Messages.IndexationProvider_5);
		field.getColumn().setWidth(150);

		field.setEditingSupport(new FieldDefinitionEditor(linkedDatasProviders));

		linkedDatasProviders.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		linkedDatasProviders.getTable().setHeaderVisible(true);
		linkedDatasProviders.getTable().setLinesVisible(true);

	}

	private void createDatasProviderViewer(Composite parent) {
		Label l = new Label(parent, SWT.NONE);
		l.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		l.setText(Messages.IndexationProvider_6);

		tableViewer = new TableViewer(parent, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.VIRTUAL | SWT.FULL_SELECTION);
		tableViewer.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1, 2));
		tableViewer.setContentProvider(new IStructuredContentProvider() {

			@Override
			@SuppressWarnings("unchecked")
			public Object[] getElements(Object inputElement) {
				List<DatasProvider> l = (List<DatasProvider>) inputElement;
				return l.toArray(new DatasProvider[l.size()]);
			}

			@Override
			public void dispose() {
			}

			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}

		});

		TableViewerColumn name = new TableViewerColumn(tableViewer, SWT.NONE);
		name.setLabelProvider(new CellLabelProvider() {

			@Override
			public void update(ViewerCell cell) {
				String s = ((DatasProvider) cell.getElement()).getName();
				cell.setText(s);

			}
		});
		name.getColumn().setText(Messages.IndexationProvider_7);
		name.getColumn().setWidth(100);

		TableViewerColumn description = new TableViewerColumn(tableViewer, SWT.NONE);
		description.setLabelProvider(new CellLabelProvider() {

			@Override
			public void update(ViewerCell cell) {
				String s = ((DatasProvider) cell.getElement()).getDescription();
				cell.setText(s);

			}
		});
		description.getColumn().setText(Messages.IndexationProvider_8);
		description.getColumn().setWidth(150);

		TableViewerColumn field = new TableViewerColumn(tableViewer, SWT.NONE);
		field.setLabelProvider(new CellLabelProvider() {

			@Override
			public void update(ViewerCell cell) {
				String s = ((DatasProvider) cell.getElement()).getFieldDefinitionName();
				cell.setText(s);

			}
		});
		field.getColumn().setText(Messages.IndexationProvider_9);
		field.getColumn().setWidth(150);

		field.setEditingSupport(new FieldDefinitionEditor(tableViewer));

		tableViewer.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		tableViewer.getTable().setHeaderVisible(true);
		tableViewer.getTable().setLinesVisible(true);

		tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				boolean empty = tableViewer.getSelection().isEmpty();
				edit.setEnabled(!empty);
				del.setEnabled(!empty);
				browse.setEnabled(!empty);

			}
		});

	}

	private void updateDatasProviderViewer() {
		try {
			List<DatasProvider> dp = Activator.getDefault().getRepositoryApi().getDatasProviderService().getAll();
			tableViewer.setInput(dp);
		} catch (Exception e) {
			e.printStackTrace();
			tableViewer.setInput(new ArrayList<DatasProvider>());
		}

	}

	private void updateLinkedDatasProvidersViewer() {
		if (ViewTree.selectedObject != null && ViewTree.selectedObject instanceof TreeItem) {
			List<DatasProvider> linked;
			try {
				linked = Activator.getDefault().getRepositoryApi().getDatasProviderService().getForItem(((TreeItem) ViewTree.selectedObject).getItem().getId());
				linkedDatasProviders.setInput(linked);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

	@Override
	public void setFocus() {

	}

	protected void createContextMenu() {
		MenuManager menuMgr = new MenuManager();
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new ViewerMenuListener(tableViewer));
		tableViewer.getControl().setMenu(menuMgr.createContextMenu(tableViewer.getControl()));

	}

	private void createToolbar(Composite main) {
		ToolBar bar = new ToolBar(main, SWT.NONE);
		bar.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		add = new ToolItem(bar, SWT.NONE);
		add.setToolTipText(Messages.IndexationProvider_10);
		add.setImage(Activator.getDefault().getImageRegistry().get(Icons.ADD));
		add.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					/*
					 * open DataSource edition
					 */
					OdaInput input = new OdaInput();

					OdaDataSourceWizard wiz = new OdaDataSourceWizard(input);
					WizardDialog dial = new WizardDialog(getSite().getShell(), wiz);
					dial.setMinimumPageSize(800, 600);
					if (dial.open() != WizardDialog.OK) {
						return;
					}

					/*
					 * open DataSetEdition
					 */
					OdaDataSetWizard wiz2 = new OdaDataSetWizard(input);
					dial = new WizardDialog(getSite().getShell(), wiz2);
					dial.setMinimumPageSize(800, 600);
					if (dial.open() == WizardDialog.OK) {
						String xml = input.getElementAsXml();
						DatasProvider dp = new DatasProvider();
						dp.setCreationDate(new Date());
						dp.setName(input.getName());
						dp.setXml(xml);
						dp.setDescription(input.getDescription());
						int id = Activator.getDefault().getRepositoryApi().getDatasProviderService().createDatasProvider(dp);
						dp.setId(id);

						((List) tableViewer.getInput()).add(dp);

						tableViewer.refresh();

					}
				} catch (Exception _e) {
				}
			}
		});

		del = new ToolItem(bar, SWT.NONE);
		del.setToolTipText(Messages.IndexationProvider_11);
		del.setImage(Activator.getDefault().getImageRegistry().get(Icons.DEL));
		del.setEnabled(false);
		del.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection ss = (IStructuredSelection) tableViewer.getSelection();
				if (ss.isEmpty()) {
					return;
				}

				DatasProvider dp = (DatasProvider) ss.getFirstElement();

				try {
					Activator.getDefault().getRepositoryApi().getDatasProviderService().delete(dp);
				} catch (Exception e1) {
					e1.printStackTrace();
				}

				((List) tableViewer.getInput()).removeAll(ss.toList());
				tableViewer.refresh();

				super.widgetSelected(e);
			}
		});

		edit = new ToolItem(bar, SWT.NONE);
		edit.setToolTipText(Messages.IndexationProvider_12);
		edit.setEnabled(false);
		edit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DatasProvider definition = (DatasProvider) ((IStructuredSelection) tableViewer.getSelection()).getFirstElement();

				OdaInputDigester dig = null;
				try {
					dig = new OdaInputDigester(definition.getXmlDataSourceDefinition());
				} catch (Exception e1) {
					e1.printStackTrace();
					MessageDialog.openError(getSite().getShell(), Messages.IndexationProvider_13, Messages.IndexationProvider_14 + e1.getMessage());
					return;
				}
				OdaInput input = dig.getOdaInput();

				try {
					OdaDataSetWizard wiz2 = new OdaDataSetWizard(input);
					WizardDialog dial = new WizardDialog(getSite().getShell(), wiz2);
					dial.setMinimumPageSize(800, 600);
					if (dial.open() == WizardDialog.OK) {

					}
				} catch (Exception ex) {
					ex.printStackTrace();
					MessageDialog.openError(getSite().getShell(), Messages.IndexationProvider_15, Messages.IndexationProvider_16 + ex.getMessage());
					return;
				}

			}
		});

		browse = new ToolItem(bar, SWT.NONE);
		browse.setToolTipText(Messages.IndexationProvider_17);
		browse.setEnabled(false);
		browse.setImage(Activator.getDefault().getImageRegistry().get(Icons.EDIT));
		browse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DatasProvider definition = (DatasProvider) ((IStructuredSelection) tableViewer.getSelection()).getFirstElement();

				OdaInputDigester dig = null;
				try {
					dig = new OdaInputDigester(definition.getXmlDataSourceDefinition());
				} catch (Exception e1) {
					e1.printStackTrace();
					MessageDialog.openError(getSite().getShell(), Messages.IndexationProvider_18, Messages.IndexationProvider_19 + e1.getMessage());
					return;
				}
				OdaInput input = dig.getOdaInput();

				try {

					IQuery query = QueryHelper.buildquery(input);
					List<List<Object>> values = OdaHelper.getValues(input, 0);

					DialogResultSetBrowser d = new DialogResultSetBrowser(getSite().getShell(), query.getMetaData(), values);
					d.open();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});

		refresh = new ToolItem(bar, SWT.NONE);
		refresh.setToolTipText(Messages.IndexationProvider_20);
		refresh.setImage(Activator.getDefault().getImageRegistry().get(Icons.REFRESH));
		refresh.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					tableViewer.setInput(Activator.getDefault().getRepositoryApi().getDatasProviderService().getAll());
				} catch (Exception ex) {
					ex.printStackTrace();
					MessageDialog.openError(getSite().getShell(), Messages.IndexationProvider_21, Messages.IndexationProvider_22 + ex.getMessage());
				}
			}
		});

	}

	public class ViewerMenuListener implements IMenuListener {

		private Viewer _viewer;

		public ViewerMenuListener(Viewer v) {
			this._viewer = v;
		}

		public void menuAboutToShow(IMenuManager mgr) {
			IStructuredSelection ss = (IStructuredSelection) _viewer.getSelection();
			Object o = ss.getFirstElement();

			if (o instanceof DatasProvider) {
				currentDatasProvider = (DatasProvider) o;
			}

			if (ViewTree.selectedObject != null) {
				mgr.add(linkToReport);
			}

		}
	}

	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		IStructuredSelection ss = (IStructuredSelection) selection;

		if (ss.isEmpty()) {
			ViewTree.selectedObject = null;
			return;
		}
		if (ss.getFirstElement() instanceof TreeItem) {
			ViewTree.selectedObject = (TreeItem) ss.getFirstElement();
			updateLinkedDatasProvidersViewer();
		}
		else {
			ViewTree.selectedObject = null;
		}
	}

	public ComboBoxCellEditor getComboCellEditor(Table parent) {
		ComboBoxCellEditor cell;
		try {
			List<Definition> customs = gedComponent.getFieldDefinitions(true);
			items = new String[customs.size()];

			for (int i = 0; i < customs.size(); i++) {
				items[i] = customs.get(i).getName();
			}

		} catch (Exception e) {
			e.printStackTrace();
			items = new String[0];
		}

		cell = new ComboBoxCellEditor(parent, items);
		return cell;
	}

	class FieldDefinitionEditor extends EditingSupport {
		private TableViewer parent;
		private ComboBoxCellEditor cellEditor;

		public FieldDefinitionEditor(ColumnViewer viewer) {
			super(viewer);
			parent = (TableViewer) viewer;
			cellEditor = getComboCellEditor(parent.getTable());
		}

		@Override
		protected void setValue(Object element, Object value) {
			Integer index = (Integer) value;
			if (index != -1) {
				String field = items[index];
				((DatasProvider) element).setFieldDefinitionName(field);
				try {
					Activator.getDefault().getRepositoryApi().getDatasProviderService().update((DatasProvider) element);
					parent.refresh();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}

		@Override
		protected Object getValue(Object element) {
			String fd = ((DatasProvider) element).getFieldDefinitionName();
			if (fd == null || fd.equalsIgnoreCase("")) { //$NON-NLS-1$
				return new Integer(-1);
			}
			else {
				Integer i = 0;
				for (String s : items) {
					if (s.equalsIgnoreCase(fd)) {
						return i;
					}
					else {
						i++;
					}
				}
			}
			return null;
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			return cellEditor;
		}

		@Override
		protected boolean canEdit(Object element) {
			return true;
		}

	}

}
