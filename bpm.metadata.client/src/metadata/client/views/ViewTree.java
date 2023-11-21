package metadata.client.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import metadata.client.helper.GroupHelper;
import metadata.client.i18n.Messages;
import metadata.client.model.dialog.DialogBusinessTable;
import metadata.client.model.dialog.DialogColumn;
import metadata.client.model.dialog.DialogComplexFilter;
import metadata.client.model.dialog.DialogConnection;
import metadata.client.model.dialog.DialogDataSource;
import metadata.client.model.dialog.DialogDataStreamSelecter;
import metadata.client.model.dialog.DialogDateSplit;
import metadata.client.model.dialog.DialogEditSqlTable;
import metadata.client.model.dialog.DialogFilter;
import metadata.client.model.dialog.DialogFormula;
import metadata.client.model.dialog.DialogGenericFilter;
import metadata.client.model.dialog.DialogImportRelation;
import metadata.client.model.dialog.DialogLov;
import metadata.client.model.dialog.DialogMultiRelation;
import metadata.client.model.dialog.DialogOlapQueryBuilder;
import metadata.client.model.dialog.DialogPackage;
import metadata.client.model.dialog.DialogPackageFlex;
import metadata.client.model.dialog.DialogPrompt;
import metadata.client.model.dialog.DialogPromptSql;
import metadata.client.model.dialog.DialogQueryBuilder;
import metadata.client.model.dialog.DialogRelation;
import metadata.client.model.dialog.DialogRelationStrategy;
import metadata.client.model.dialog.DialogSelectDataSource;
import metadata.client.model.dialog.DialogSqlFilter2;
import metadata.client.model.dialog.fields.DialogFieldsValues;
import metadata.client.model.dialog.fields.FieldValuesHelper;
import metadata.client.model.dialogs.tools.DialogDeltaTable;
import metadata.client.preferences.PreferenceConstants;
import metadata.client.scripting.DialogScript;
import metadata.client.scripting.DialogVariable;
import metadata.client.trees.TreeBusinessTable;
import metadata.client.trees.TreeComplexFilter;
import metadata.client.trees.TreeConnection;
import metadata.client.trees.TreeDataSource;
import metadata.client.trees.TreeDataStream;
import metadata.client.trees.TreeDataStreamElement;
import metadata.client.trees.TreeFilter;
import metadata.client.trees.TreeFormula;
import metadata.client.trees.TreeLov;
import metadata.client.trees.TreeModel;
import metadata.client.trees.TreeMultiRelation;
import metadata.client.trees.TreeObject;
import metadata.client.trees.TreePackage;
import metadata.client.trees.TreeParent;
import metadata.client.trees.TreePrompt;
import metadata.client.trees.TreeRelation;
import metadata.client.trees.TreeRelationStrategy;
import metadata.client.trees.TreeResource;
import metadata.client.trees.TreeSavedQuery;
import metadata.client.viewer.TreeContentProvider;
import metadata.client.viewer.TreeLabelProvider;
import metadata.client.wizards.business.table.BusinessTableWizard;
import metadata.client.wizards.datasources.DataSourceWizard;
import metadata.client.wizards.datasources.TableWizard;
import metadata.client.wizards.dimension.DialogDimension;
import metadata.client.wizards.measure.DialogMeasure;
import metadataclient.Activator;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import bpm.metadata.MetaData;
import bpm.metadata.layer.business.AbstractBusinessTable;
import bpm.metadata.layer.business.BusinessModel;
import bpm.metadata.layer.business.BusinessPackage;
import bpm.metadata.layer.business.IBusinessModel;
import bpm.metadata.layer.business.IBusinessPackage;
import bpm.metadata.layer.business.IBusinessTable;
import bpm.metadata.layer.business.SQLBusinessTable;
import bpm.metadata.layer.business.UnitedOlapBusinessTable;
import bpm.metadata.layer.logical.AbstractDataSource;
import bpm.metadata.layer.logical.ICalculatedElement;
import bpm.metadata.layer.logical.IDataSource;
import bpm.metadata.layer.logical.IDataStream;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.layer.logical.Join;
import bpm.metadata.layer.logical.MultiDSRelation;
import bpm.metadata.layer.logical.Relation;
import bpm.metadata.layer.logical.olap.UnitedOlapDataStream;
import bpm.metadata.layer.logical.olap.UnitedOlapDatasource;
import bpm.metadata.layer.logical.sql.SQLDataSource;
import bpm.metadata.layer.logical.sql.SQLDataStream;
import bpm.metadata.layer.logical.sql.SQLDataStreamElement;
import bpm.metadata.layer.physical.IConnection;
import bpm.metadata.layer.physical.sql.SQLConnection;
import bpm.metadata.misc.MetaDataChecker;
import bpm.metadata.query.SavedQuery;
import bpm.metadata.resource.ComplexFilter;
import bpm.metadata.resource.Filter;
import bpm.metadata.resource.IFilter;
import bpm.metadata.resource.IResource;
import bpm.metadata.resource.ListOfValue;
import bpm.metadata.resource.Prompt;
import bpm.metadata.resource.SqlQueryFilter;
import bpm.metadata.resource.complex.FmdtDimension;
import bpm.metadata.resource.complex.FmdtMeasure;
import bpm.metadata.scripting.Script;
import bpm.metadata.scripting.Variable;

public class ViewTree extends ViewPart {
	public static final String ID = "metadataclient.views.tree"; //$NON-NLS-1$

	private static enum HIGHLIGHTTYPE {
		ERROR, USED_RELATION, NONE
	}

	private boolean showSize = false;
	private HIGHLIGHTTYPE decorationType = HIGHLIGHTTYPE.NONE;

	private TreeViewer viewer;
	private ToolBar toolbar;

	private MtdAction addDataSource, addBusinessTable, createLov;
	private MtdAction createFilter, createPrompt, createComplexFilter, createSqlFilter, createPromptSql;
	private MtdAction addModel, addPackage, editConnection;
	private MtdAction addMultiRelation, addRelation;
	private MtdAction addLogicalTable;
	private MtdAction browse, browseColumns, edit;
	private MtdAction queryBuilder;
	private MtdAction delete, importRelation;
	private MtdAction addFormula;
	private MtdAction addAlternateConnection;
	private MtdAction duplicateDataStream;
	private MtdAction browseDataStream;
	private MtdAction packageFlexBrowser;
	private MtdAction editFormula;
	private MtdAction editDimension;
	private MtdAction editMeasure;
	private MtdAction editSqlTable;
	private MtdAction splitDate;

	private MtdAction createVariable;
	private MtdAction createScript;
	private MtdAction dataStreamToBusinessTable;
	private MtdAction browseRelationData;
	private MtdAction createGenericFilter;

	private MtdAction deltaDataStream;
	private MtdAction highLightDataStreamNotRelationed, highLightDetectedErrorsRelationed, addDataStreamSize;

	private IAction addStrategy;
	private IAction editStrategy;

	private final TreeParent variablesNode = new TreeParent(Messages.ViewTree_3);
	private final TreeParent scriptsNode = new TreeParent(Messages.ViewTree_4);

	private boolean onOlap = false;

	public ViewTree() {

	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout());
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));

		toolbar = new ToolBar(parent, SWT.NONE);
		toolbar.setLayoutData(new GridData(SWT.FILL));

		viewer = new TreeViewer(parent, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI);// |
																					// SWT.MULTI);
		viewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.setContentProvider(new TreeContentProvider() {
			@Override
			public Object[] getChildren(Object parent) {
				List l = null;
				if (parent == variablesNode) {
					l = Activator.getDefault().getModel().getVariables();
					return l.toArray(new Object[l.size()]);
				} else if (parent == scriptsNode) {
					l = Activator.getDefault().getModel().getScripts();
					return l.toArray(new Object[l.size()]);
				}
				return super.getChildren(parent);
			}

			@Override
			public boolean hasChildren(Object parent) {
				if (parent == variablesNode) {
					return !Activator.getDefault().getModel().getVariables().isEmpty();
				} else if (parent == scriptsNode) {
					return !Activator.getDefault().getModel().getScripts().isEmpty();
				}
				return super.hasChildren(parent);
			}

			@Override
			public Object getParent(Object child) {
				if (child instanceof Variable) {
					return variablesNode;
				}
				if (child instanceof Script) {
					return scriptsNode;
				}
				return super.getParent(child);
			}
		});
		viewer.setLabelProvider(new DataStreamDecorator(PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator()));
		viewer.setComparator(new ViewerComparator() {

			@Override
			public int compare(Viewer viewer, Object e1, Object e2) {
				if (e1 instanceof TreeDataStream && e2 instanceof TreeDataStream) {
					return ((TreeObject) e1).getName().compareTo(((TreeObject) e2).getName());
				}

				if (e1 instanceof TreeDataStreamElement && e2 instanceof TreeDataStreamElement) {
					return ((TreeObject) e1).getName().compareTo(((TreeObject) e2).getName());
				}

				if (e1 instanceof TreeResource && e2 instanceof TreeResource) {
					IResource r1 = ((TreeResource) e1).getResource();
					IResource r2 = ((TreeResource) e2).getResource();

					if (r1.getClass() == r2.getClass()) {
						return r1.getName().compareTo(r2.getName());
					} else {
						return r1.getClass().getName().compareTo(r2.getClass().getName());
					}

				}
				return 0;
			}

		});
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();

				if (ss.isEmpty() || !(ss.getFirstElement() instanceof TreeObject)) {
					for (int i = 0; i < toolBarItems.length; i++) {
						toolBarItems[i].setEnabled((i == IT_DATASOURCE) || (i == IT_MODEL));

					}
					return;
				}
				for (int i = 0; i < toolBarItems.length; i++) {
					toolBarItems[i].setEnabled((i == IT_DATASOURCE) || (i == IT_MODEL));

				}

				TreeObject obj = (TreeObject) ss.getFirstElement();

				if (obj.getName().equals(Messages.ViewTree_5)) {
					if (onOlap) {
						toolBarItems[IT_FILTER].setEnabled(true);
						toolBarItems[IT_PROMPT].setEnabled(true);
					} else {
						for (int i = 0; i < toolBarItems.length; i++) {
							toolBarItems[i].setEnabled((i == IT_SQL_FILTER) || (i == IT_COMPLXEX_FILTER) || (i == IT_FILTER) || (i == IT_LOV) || (i == IT_PROMPT));
						}
					}

				} else if (obj instanceof TreeModel) {
					if (onOlap) {
						toolBarItems[IT_FILTER].setEnabled(true);
						toolBarItems[IT_PROMPT].setEnabled(true);
					} else {
						for (int i = 0; i < toolBarItems.length; i++) {
							toolBarItems[i].setEnabled((i == IT_SQL_FILTER) || (i == IT_COMPLXEX_FILTER) || (i == IT_FILTER) || (i == IT_LOV) || (i == IT_PROMPT));
						}
					}
				} else if (obj.getName().equals(Messages.ViewTree_6)) {
					for (int i = 0; i < toolBarItems.length; i++) {
						toolBarItems[i].setEnabled((i == IT_TABLE));
					}
				} else if (obj.getName().equals(Messages.ViewTree_7)) {
					for (int i = 0; i < toolBarItems.length; i++) {
						toolBarItems[i].setEnabled((i == IT_PACKAGE));
					}
				}

				boolean delete = (obj instanceof TreeResource || obj instanceof TreeRelation || obj instanceof TreeDataStream || obj instanceof TreeDataStreamElement || obj instanceof TreeBusinessTable || obj instanceof TreePackage || obj instanceof TreeDataSource || obj instanceof TreeSavedQuery);
				toolBarItems[IT_DELETE].setEnabled(delete);
			}
		});

		getSite().setSelectionProvider(viewer);

		createActions();
		createContextMenu();
		createToolbar();
		createModel();
		setDnd();
		setLoD();
	}

	@Override
	public void setFocus() {
		if (Activator.getDefault().getRepositoryContext() != null) {
			getViewSite().getActionBars().getStatusLineManager().setMessage(Messages.ViewTree_1 + Activator.getDefault().getRepositoryContext().getRepository().getUrl()); //$NON-NLS-1$
		} else {
			getViewSite().getActionBars().getStatusLineManager().setMessage(Messages.ViewTree_2); //$NON-NLS-1$
		}
	}

	// create the model from the Activator
	private void createModel() {
		((DataStreamDecorator) viewer.getLabelProvider()).map.clear();
		((DataStreamDecorator) viewer.getLabelProvider()).counter.clear();
		MetaData model = Activator.getDefault().getModel();

		TreeParent root = new TreeParent(""); //$NON-NLS-1$

		root.addChild(variablesNode);
		root.addChild(scriptsNode);

		// create DataSource Branch
		TreeParent dataSources = new TreeParent("DataSources"); //$NON-NLS-1$
		for (AbstractDataSource ds : model.getDataSources()) {

			if (ds instanceof UnitedOlapDatasource) {
				onOlap = true;
			} else {
				onOlap = false;
			}
			dataSources.addChild(new TreeDataSource(ds));

		}
		root.addChild(dataSources);

		// create Resources Branch
		TreeParent resources = new TreeParent("Resources"); //$NON-NLS-1$
		for (IResource r : model.getResources()) {
			if (r instanceof Filter) {
				resources.addChild(new TreeFilter((Filter) r));
			} else if (r instanceof ListOfValue) {
				resources.addChild(new TreeLov((ListOfValue) r));
			} else if (r instanceof Prompt) {
				resources.addChild(new TreePrompt((Prompt) r));
			} else if (r instanceof ComplexFilter) {
				resources.addChild(new TreeComplexFilter((ComplexFilter) r));
			} else {
				resources.addChild(new TreeResource((IResource) r));
			}

		}
		root.addChild(resources);

		// create ExternalRelation Branch
		TreeParent relations = new TreeParent("External relations"); //$NON-NLS-1$
		for (MultiDSRelation r : model.getMultiDataSourceRelations()) {
			relations.addChild(new TreeMultiRelation(r));
		}
		dataSources.addChild(relations);

		TreeParent models = new TreeParent("Business Models"); //$NON-NLS-1$
		root.addChild(models);
		for (IBusinessModel m : Activator.getDefault().getModel().getBusinessModels()) {
			models.addChild(new TreeModel(m, "none")); //$NON-NLS-1$

		}

		viewer.setInput(root);
	}

	private void createContextMenu() {
		final MenuManager menuMgr = new MenuManager();
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {

			

			public void menuAboutToShow(IMenuManager mgr) {
				IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();
				Object o = ss.getFirstElement();

				if (o instanceof TreeObject) {
					menuMgr.add(delete);
				}

				boolean relationAdded = false;

				if (o instanceof TreeDataStream) {
					if (!onOlap) {
						menuMgr.add(addFormula);
					}
					if ((((TreeDataStream) o).getDataStream() instanceof SQLDataStream) && ((SQLDataStream) ((TreeDataStream) o).getDataStream()).getSql() != null && !((SQLDataStream) ((TreeDataStream) o).getDataStream()).getSql().equals("")) {
						menuMgr.add(editSqlTable);
					}
					menuMgr.add(duplicateDataStream);
					menuMgr.add(browseDataStream);
					menuMgr.add(browseColumns);

					menuMgr.add(deltaDataStream);
					IDataStream _s = ((TreeDataStream) o).getDataStream();
					deltaDataStream.setEnabled(_s instanceof SQLDataStream && ((SQLDataStream) _s).getOrigin() != null);

					menuMgr.add(new Separator());
					menuMgr.add(createGenericFilter);
				}
				if (o instanceof TreeDataStreamElement && ((TreeDataStreamElement) o).getDataStreamElement().isCalculated()) {
					menuMgr.add(editFormula);
				}
				if (o instanceof TreeDataStreamElement && !((TreeDataStreamElement) o).getDataStreamElement().isCalculated()) {
					menuMgr.add(splitDate);
				}
				if (o instanceof TreeDataStreamElement && ((TreeObject) o).getParent() instanceof TreeDataStream) {
					menuMgr.add(browseColumns);
				}

				if (o instanceof TreeBusinessTable) {
					menuMgr.add(browseColumns);
					menuMgr.add(browse);
					menuMgr.add(edit);
					menuMgr.add(addBusinessTable);
				} else if (o instanceof TreeResource) {
					menuMgr.add(edit);
					// menuMgr.add(browse);
				} else if (o instanceof TreeDataSource) {
					menuMgr.add(edit);
					if (!onOlap) {
						menuMgr.add(addRelation);
					}
					relationAdded = true;
					menuMgr.add(addLogicalTable);
					menuMgr.add(addAlternateConnection);

					if (((TreeDataSource) o).getDataSource() instanceof SQLDataSource) {
						menuMgr.add(editDimension);
						menuMgr.add(editMeasure);
					}
				} else if (o instanceof TreeConnection) {
					menuMgr.add(editConnection);
				} else if (o instanceof TreeRelation) {
					menuMgr.add(edit);
					menuMgr.add(browseRelationData);
				} else if (o instanceof TreeModel) {
					menuMgr.add(addBusinessTable);
					menuMgr.add(addPackage);
					if (!onOlap) {
						menuMgr.add(createLov);
					}
					menuMgr.add(createFilter);
					menuMgr.add(createPrompt);
					menuMgr.add(createPromptSql);
					if (!onOlap) {
						menuMgr.add(createSqlFilter);
						menuMgr.add(createComplexFilter);

						menuMgr.add(addRelation);
						menuMgr.add(importRelation);
						menuMgr.add(addStrategy);
					}
					menuMgr.add(dataStreamToBusinessTable);
					dataStreamToBusinessTable.setEnabled(false);

					for (IDataSource ds : Activator.getDefault().getModel().getDataSources()) {
						if (ds instanceof SQLDataSource) {
							dataStreamToBusinessTable.setEnabled(true);
							break;
						}
					}

				} else if (o instanceof TreePackage) {
					menuMgr.add(queryBuilder);
					menuMgr.add(packageFlexBrowser);
					menuMgr.add(edit);
				} else if (o instanceof TreeRelationStrategy) {
					menuMgr.add(editStrategy);
				} else if (o instanceof TreeParent && !(o instanceof TreeDataStreamElement)) {

					TreeParent tp = (TreeParent) o;
					if (tp.getName().equals("Relations")) { //$NON-NLS-1$
						if (!onOlap) {
							menuMgr.add(addRelation);
						}
						relationAdded = true;
					} else if (tp.getName().equals("Resources")) { //$NON-NLS-1$
						menuMgr.add(createFilter);
						menuMgr.add(createPrompt);
						menuMgr.add(createPromptSql);
						if (!onOlap) {
							menuMgr.add(createLov);
							menuMgr.add(createSqlFilter);
							menuMgr.add(createComplexFilter);
						}
					} else if (tp.getName().equals("Business Packages")) { //$NON-NLS-1$
						menuMgr.add(addPackage);
					} else if (tp.getName().equals("Business Tables")) { //$NON-NLS-1$
						menuMgr.add(addBusinessTable);
					} else if (tp.getName().equals("DataSources")) { //$NON-NLS-1$
						menuMgr.add(addDataSource);
					} else if (tp.getName().equals("Business Models")) { //$NON-NLS-1$
						menuMgr.add(addModel);
					} else if (tp.getName().equals("Relation Strategies")) {
						menuMgr.add(addStrategy);
					}

					menuMgr.add(new Separator());
					menuMgr.add(createVariable);
					menuMgr.add(createScript);
					menuMgr.add(new Separator());

					if (Activator.getDefault().getModel().getDataSources().size() > 1) {
						if (!onOlap) {
							menuMgr.add(addMultiRelation);
						}
					}
				}
				TreeObject obj = (TreeObject) o;

				if (!relationAdded) {
					if ((obj.getParent().getName().equals(Messages.ViewTree_8) && obj.getParent() instanceof TreeDataSource) || obj instanceof TreeDataStream || obj instanceof TreeRelation || (obj instanceof TreeDataStreamElement && obj.getParent().getParent() instanceof TreeDataSource)) {
						if (!onOlap) {
							menuMgr.add(addRelation);
						}
					}
				}
				menuMgr.add(new Separator());
				menuMgr.add(highLightDataStreamNotRelationed);
				highLightDataStreamNotRelationed.setChecked(decorationType == HIGHLIGHTTYPE.USED_RELATION);
				menuMgr.add(highLightDetectedErrorsRelationed);
				highLightDetectedErrorsRelationed.setChecked(decorationType == HIGHLIGHTTYPE.ERROR);

				menuMgr.add(addDataStreamSize);
				addDataStreamSize.setChecked(showSize);

				menuMgr.update();

			}
		});
		viewer.getControl().setMenu(menuMgr.createContextMenu(viewer.getControl()));

	}

	private void createActions() {

		createGenericFilter = new MtdAction(Messages.ViewTree_9) {
			public void run() {
				IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();
				if (ss.isEmpty()) {
					return;
				}

				TreeDataStream stream = (TreeDataStream) ss.getFirstElement();

				DialogGenericFilter dial = new DialogGenericFilter(getSite().getShell(), stream.getDataStream());
				if (dial.open() == DialogGenericFilter.OK) {
					stream.getDataStream().addGenericFilter(dial.getFilter());
					stream.addChild(new TreeResource(dial.getFilter()));
					viewer.refresh();
				}

			}
		};

		addDataStreamSize = new MtdAction(Messages.ViewTree_17) {
			public void run() {

				showSize = !showSize;

				Job job = new Job(Messages.ViewTree_18) {

					@Override
					protected IStatus run(IProgressMonitor monitor) {
						monitor.beginTask(Messages.ViewTree_27, 1);
						Display.getDefault().asyncExec(new Runnable() {
							public void run() {
								viewer.refresh();
							}
						});
						return Status.OK_STATUS;
					}

				};
				job.setUser(true);
				job.schedule();

			}
		};

		highLightDataStreamNotRelationed = new MtdAction(Messages.ViewTree_10) {
			public void run() {
				if (decorationType != HIGHLIGHTTYPE.USED_RELATION) {
					decorationType = HIGHLIGHTTYPE.USED_RELATION;
				} else {
					decorationType = HIGHLIGHTTYPE.NONE;
				}
				viewer.refresh();
			}
		};

		highLightDetectedErrorsRelationed = new MtdAction(Messages.ViewTree_11) {
			public void run() {
				if (decorationType != HIGHLIGHTTYPE.ERROR) {
					decorationType = HIGHLIGHTTYPE.ERROR;
					((DataStreamDecorator) viewer.getLabelProvider()).map = MetaDataChecker.checkModel(Activator.getDefault().getModel());
				} else {
					((DataStreamDecorator) viewer.getLabelProvider()).map.clear();
					decorationType = HIGHLIGHTTYPE.NONE;
				}
				viewer.refresh();
			}
		};

		deltaDataStream = new MtdAction(Messages.ViewTree_12) {
			public void run() {
				IStructuredSelection ss = ((IStructuredSelection) viewer.getSelection());
				IDataStream t = ((TreeDataStream) ss.getFirstElement()).getDataStream();

				DialogDeltaTable d = new DialogDeltaTable(getSite().getShell(), (SQLDataStream) t);
				if (d.open() == DialogDeltaTable.OK) {
					((TreeDataStream) ss.getFirstElement()).refresh();
					viewer.refresh();
				}
			}
		};

		browseRelationData = new MtdAction(Messages.ViewTree_13) {
			public void run() {
				IStructuredSelection ss = ((IStructuredSelection) viewer.getSelection());
				TreeRelation t = (TreeRelation) ss.getFirstElement();
				Relation relation = t.getRelation();

				SQLBusinessTable table = new SQLBusinessTable();
				for (Join j : relation.getJoins()) {
					if (!table.getColumns().contains(j.getLeftElement())) {
						table.addColumn(j.getLeftElement());
					}
					if (!table.getColumns().contains(j.getRightElement())) {
						table.addColumn(j.getRightElement());
					}
				}
				;
				MetaData model = Activator.getDefault().getModel();

				BusinessModel m = new BusinessModel();
				model.addBusinessModel(m);
				m.addBusinessTable(table);

				DialogBusinessTable d = new DialogBusinessTable(getSite().getShell(), table);
				d.open();

				model.removeBusinessModel(m);
			}
		};
		browseRelationData.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor("browse_col")); //$NON-NLS-1$

		dataStreamToBusinessTable = new MtdAction(Messages.ViewTree_16) {
			public void run() {
				IStructuredSelection ss = ((IStructuredSelection) viewer.getSelection());

				TreeModel t = (TreeModel) ss.getFirstElement();

				DialogDataStreamSelecter d = new DialogDataStreamSelecter(getSite().getShell(), t.getModel());
				if (d.open() == DialogDataStreamSelecter.OK) {

					HashMap<IDataStream, String> map = d.getBusinessTablesToCreate();

					for (IDataStream ds : map.keySet()) {
						SQLBusinessTable table = new SQLBusinessTable(map.get(ds));

						for (IDataStreamElement e : ds.getElements()) {
							table.addColumn(e);
						}
						for (String groupName : GroupHelper.getGroups(0, 0)) {
							table.setGranted(groupName, true);
						}

						((BusinessModel) t.getModel()).addBusinessTable(table);

					}
					t.refresh("none"); //$NON-NLS-1$
				}

			}
		};
		dataStreamToBusinessTable.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor("physical_to_business")); //$NON-NLS-1$
		createVariable = new MtdAction(Messages.ViewTree_19) {
			public void run() {
				DialogVariable d = new DialogVariable(getSite().getShell());
				if (d.open() == DialogVariable.OK) {
					Activator.getDefault().getModel().addVariable(d.getVariable());
					viewer.refresh();
				}
			}
		};

		createScript = new MtdAction(Messages.ViewTree_20) {
			public void run() {
				DialogScript d = new DialogScript(getSite().getShell());
				if (d.open() == DialogVariable.OK) {
					Activator.getDefault().getModel().addScript(d.getScript());
					viewer.refresh();
				}
			}
		};

		editMeasure = new MtdAction(Messages.ViewTree_22) {
			public void run() {
				IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();

				if (((TreeDataSource) ss.getFirstElement()).getDataSource() instanceof SQLDataSource) {
					DialogMeasure d = new DialogMeasure(getSite().getShell(), ((TreeDataSource) ss.getFirstElement()).getDataSource());
					if (d.open() == DialogMeasure.OK) {
						FmdtMeasure dim = d.getMeasure();

						Activator.getDefault().getModel().addResource(dim);
						TreeParent tp = (TreeParent) ((TreeParent) viewer.getInput()).getChildNamed(Messages.ViewTree_23);
						tp.addChild(new TreeResource(dim));
						viewer.refresh();
					}
				}

			}
		};

		addStrategy = new org.eclipse.jface.action.Action("Add Relation Strategy") {
			@Override
			public void run() {

				IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();
				if (ss.isEmpty()) {
					return;
				}

				BusinessModel bModel = null;
				TreeModel t = null;

				if (ss.getFirstElement() instanceof TreeModel) {
					t = ((TreeModel) ss.getFirstElement());
				} else {
					t = ((TreeModel) ((TreeParent) ss.getFirstElement()).getParent());
				}

				bModel = (BusinessModel) t.getModel();

				DialogRelationStrategy dial = new DialogRelationStrategy(getSite().getShell(), bModel, null);
				if (dial.open() == Dialog.OK) {
					t.refresh("none");
					viewer.refresh();
					Activator.getDefault().setChanged();
				}

			}
		};

		editStrategy = new org.eclipse.jface.action.Action("Edit Relation Strategy") {
			@Override
			public void run() {

				IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();
				if (ss.isEmpty()) {
					return;
				}

				TreeRelationStrategy strat = ((TreeRelationStrategy) ss.getFirstElement());

				BusinessModel bModel = (BusinessModel) ((TreeModel) strat.getParent().getParent()).getModel();

				DialogRelationStrategy dial = new DialogRelationStrategy(getSite().getShell(), bModel, strat.getStrategy());
				if (dial.open() == Dialog.OK) {
					((TreeModel) strat.getParent().getParent()).refresh("none");
				}

			}
		};

		editDimension = new MtdAction(Messages.ViewTree_24) {
			public void run() {
				IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();

				if (((TreeDataSource) ss.getFirstElement()).getDataSource() instanceof SQLDataSource) {
					DialogDimension dial = new DialogDimension(getSite().getShell(), (SQLDataSource) ((TreeDataSource) ss.getFirstElement()).getDataSource());
					if (dial.open() == DialogDimension.OK) {
						FmdtDimension dim = dial.getDimension();

						Activator.getDefault().getModel().addResource(dim);
						TreeParent tp = (TreeParent) ((TreeParent) viewer.getInput()).getChildNamed(Messages.ViewTree_25);
						tp.addChild(new TreeResource(dim));
						viewer.refresh();
					}
				}

			}
		};
		editSqlTable = new MtdAction("Edit Table") {
			public void run() {
				IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();
				IDataStream stream = ((TreeDataStream) ss.getFirstElement()).getDataStream();
				DialogEditSqlTable dial = new DialogEditSqlTable(getSite().getShell(), stream);
				if (dial.open() == Dialog.OK) {
					ViewTree.this.reInit();
				}
			};
		};

		splitDate = new MtdAction("Split Date") {
			public void run() {
				IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();
				IDataStreamElement stream = ((TreeDataStreamElement) ss.getFirstElement()).getDataStreamElement();
				DialogDateSplit dial = new DialogDateSplit(getSite().getShell(), stream);
				if (dial.open() == Dialog.OK) {
					ViewTree.this.reInit();
				}
			};
		};
		editFormula = new MtdAction(Messages.ViewTree_26) {
			public void run() {

				IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();

				ICalculatedElement c = (ICalculatedElement) ((TreeDataStreamElement) ss.getFirstElement()).getDataStreamElement();

//				IDataSource ds = ((TreeDataStreamElement) ss.getFirstElement()).getDataStreamElement().getDataStream().getDataSource();
//
//				IConnection con = ds.getConnections("none").get(0); //$NON-NLS-1$
				List<IDataStream> tables = new ArrayList<IDataStream>();
				try {
					tables.add(c.getDataStream());
					DialogFormula dial = new DialogFormula(getSite().getShell(), tables, c);
					dial.open();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		};
		editFormula.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor("edit")); //$NON-NLS-1$

		packageFlexBrowser = new MtdAction(Messages.ViewTree_29) {
			public void run() {

				IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();

				if (!(ss.getFirstElement() instanceof TreePackage)) {
					return;
				}

				IBusinessPackage pckg = ((TreePackage) ss.getFirstElement()).getPackage();

				DialogPackageFlex dial = new DialogPackageFlex(getSite().getShell(), pckg);
				dial.open();

			}
		};

		// browseData
		browseDataStream = new MtdAction(Messages.ViewTree_30) {
			public void run() {

				IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();

				if (!(ss.getFirstElement() instanceof TreeDataStream)) {
					return;
				}

				// create a fakeBusinessTable to use the Dialog
				IDataStream str = ((TreeDataStream) ss.getFirstElement()).getDataStream();
				AbstractBusinessTable t = null;
				if (str instanceof UnitedOlapDataStream) {
					t = new UnitedOlapBusinessTable(str.getName());
				} else {
					t = new SQLBusinessTable(str.getName());
				}
				for (IDataStreamElement c : str.getElements()) {
					t.addColumn(c);
				}
				BusinessModel m = new BusinessModel();
				try {
					Activator.getDefault().getModel().addBusinessModel(m);
					m.addBusinessTable(t);
					DialogBusinessTable dial = new DialogBusinessTable(getSite().getShell(), t);
					dial.open();
				} finally {
					Activator.getDefault().getModel().removeBusinessModel(m);
				}

			}
		};
		browseDataStream.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor("browse")); //$NON-NLS-1$

		// browse Columns
		browseColumns = new MtdAction(Messages.ViewTree_32) {
			public void run() {
				IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();

				Dialog d = null;
				if ((ss.getFirstElement() instanceof TreeDataStream)) {
					d = new DialogFieldsValues(getSite().getShell(), ((TreeDataStream) ss.getFirstElement()).getDataStream());
				} else if (ss.getFirstElement() instanceof TreeDataStreamElement) {

					d = new DialogFieldsValues(getSite().getShell(), ((TreeDataStreamElement) ss.getFirstElement()).getDataStreamElement());
				} else if ((ss.getFirstElement() instanceof TreeBusinessTable)) {
					d = new DialogFieldsValues(getSite().getShell(), ((TreeBusinessTable) ss.getFirstElement()).getTable());
				}

				d.open();
			}
		};
		browseColumns.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor("browse_col")); //$NON-NLS-1$

		// complexFilter
		createComplexFilter = new MtdAction(Messages.ViewTree_15) { //$NON-NLS-1$
			@Override
			public void run() {

				IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();
				if (ss.isEmpty()) {
					return;
				}

				TreeParent to = (TreeParent) ss.getFirstElement();

				DialogComplexFilter dial = new DialogComplexFilter(getSite().getShell());
				if (dial.open() == Window.OK) {
					ComplexFilter l = dial.getFilter();

					if (to instanceof TreeModel) {
						((BusinessModel) ((TreeModel) ss.getFirstElement()).getModel()).addResource(l);
						// add the resource to the MetaDataModel
						Activator.getDefault().getModel().addResource(l);

						TreeParent root = (TreeParent) viewer.getInput();
						((TreeParent) root.getChildNamed("Resources")).addChild(new TreeComplexFilter(l)); //$NON-NLS-1$
						((TreeModel) ss.getFirstElement()).refresh("none"); //$NON-NLS-1$
					} else {
						Activator.getDefault().getModel().addResource(l);
						TreeParent root = (TreeParent) viewer.getInput();
						((TreeParent) root.getChildNamed("Resources")).addChild(new TreeComplexFilter(l)); //$NON-NLS-1$

						if (to.getParent() instanceof TreeModel) {
							TreeModel tm = (TreeModel) to.getParent();
							((BusinessModel) tm.getModel()).addResource(l);
							tm.refresh("none"); //$NON-NLS-1$
						}
					}

					viewer.refresh();
					update();
				}
			}
		};
		createComplexFilter.setImageDescriptor(ImageDescriptor.createFromImage(Activator.getDefault().getImageRegistry().get("filter_complex"))); //$NON-NLS-1$

		// sqlFilter
		createSqlFilter = new MtdAction(Messages.ViewTree_35) {
			@Override
			public void run() {

				IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();
				if (ss.isEmpty()) {
					return;
				}

				TreeParent to = (TreeParent) ss.getFirstElement();

				DialogSqlFilter2 dial = new DialogSqlFilter2(getSite().getShell());
				if (dial.open() == Window.OK) {
					SqlQueryFilter l = dial.getFilter();

					if (to instanceof TreeModel) {
						((BusinessModel) ((TreeModel) ss.getFirstElement()).getModel()).addResource(l);
						// add the resource to the MetaDataModel
						Activator.getDefault().getModel().addResource(l);

						TreeParent root = (TreeParent) viewer.getInput();
						((TreeParent) root.getChildNamed("Resources")).addChild(new TreeResource(l)); //$NON-NLS-1$
						((TreeModel) ss.getFirstElement()).refresh("none"); //$NON-NLS-1$
					} else {
						Activator.getDefault().getModel().addResource(l);
						TreeParent root = (TreeParent) viewer.getInput();

						((TreeParent) root.getChildNamed("Resources")).addChild(new TreeResource(l)); //$NON-NLS-1$

						if (to.getParent() instanceof TreeModel) {
							TreeModel tm = (TreeModel) to.getParent();
							((BusinessModel) tm.getModel()).addResource(l);
							tm.refresh("none"); //$NON-NLS-1$
						}
					}

					viewer.refresh();
					update();
				}

			}
		};
		createSqlFilter.setImageDescriptor(ImageDescriptor.createFromImage(Activator.getDefault().getImageRegistry().get("filter_sql"))); //$NON-NLS-1$

		// prompts
		createPrompt = new MtdAction(Messages.ViewTree_21) { //$NON-NLS-1$
			@Override
			public void run() {

				IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();
				if (ss.isEmpty()) {
					return;
				}

				TreeParent to = (TreeParent) ss.getFirstElement();

				DialogPrompt dial = new DialogPrompt(getSite().getShell());
				if (dial.open() == Window.OK) {
					Prompt l = dial.getPrompt();

					if (to instanceof TreeModel) {
						((BusinessModel) ((TreeModel) ss.getFirstElement()).getModel()).addResource(l);
						// add the resource to the MetaDataModel
						Activator.getDefault().getModel().addResource(l);

						TreeParent root = (TreeParent) viewer.getInput();
						((TreeParent) root.getChildNamed("Resources")).addChild(new TreePrompt(l)); //$NON-NLS-1$
						((TreeModel) ss.getFirstElement()).refresh("none"); //$NON-NLS-1$
					} else {
						Activator.getDefault().getModel().addResource(l);
						TreeParent root = (TreeParent) viewer.getInput();
						((TreeParent) root.getChildNamed("Resources")).addChild(new TreePrompt(l)); //$NON-NLS-1$

						if (to.getParent() instanceof TreeModel) {
							TreeModel tm = (TreeModel) to.getParent();
							((BusinessModel) tm.getModel()).addResource(l);
							tm.refresh("none"); //$NON-NLS-1$
						}
					}

					viewer.refresh();
					update();
				}
			}
		};
		createPrompt.setImageDescriptor(ImageDescriptor.createFromImage(Activator.getDefault().getImageRegistry().get("prompt"))); //$NON-NLS-1$

		createPromptSql = new MtdAction("Create Sql Prompt") {
			@Override
			public void run() {
				IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();
				if (ss.isEmpty()) {
					return;
				}

				TreeParent to = (TreeParent) ss.getFirstElement();

				DialogPromptSql dial = new DialogPromptSql(getSite().getShell());
				if (dial.open() == Window.OK) {
					Prompt l = dial.getPrompt();

					if (to instanceof TreeModel) {
						((BusinessModel) ((TreeModel) ss.getFirstElement()).getModel()).addResource(l);
						// add the resource to the MetaDataModel
						Activator.getDefault().getModel().addResource(l);

						TreeParent root = (TreeParent) viewer.getInput();
						((TreeParent) root.getChildNamed("Resources")).addChild(new TreePrompt(l)); //$NON-NLS-1$
						((TreeModel) ss.getFirstElement()).refresh("none"); //$NON-NLS-1$
					} else {
						Activator.getDefault().getModel().addResource(l);
						TreeParent root = (TreeParent) viewer.getInput();
						((TreeParent) root.getChildNamed("Resources")).addChild(new TreePrompt(l)); //$NON-NLS-1$

						if (to.getParent() instanceof TreeModel) {
							TreeModel tm = (TreeModel) to.getParent();
							((BusinessModel) tm.getModel()).addResource(l);
							tm.refresh("none"); //$NON-NLS-1$
						}
					}

					viewer.refresh();
					update();
				}
			}
		};

		// filters
		createFilter = new MtdAction("Create Filter") { //$NON-NLS-1$
			@Override
			public void run() {

				IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();
				if (ss.isEmpty()) {
					return;
				}

				TreeParent to = (TreeParent) ss.getFirstElement();

				DialogFilter dial = new DialogFilter(getSite().getShell(), onOlap);
				if (dial.open() == Window.OK) {
					Filter l = dial.getFilter();

					if (to instanceof TreeModel) {
						((BusinessModel) ((TreeModel) ss.getFirstElement()).getModel()).addResource(l);
						// add the resource to the MetaDataModel
						Activator.getDefault().getModel().addResource(l);

						TreeParent root = (TreeParent) viewer.getInput();
						((TreeParent) root.getChildNamed("Resources")).addChild(new TreeFilter(l)); //$NON-NLS-1$
						((TreeModel) ss.getFirstElement()).refresh("none"); //$NON-NLS-1$
					} else {
						Activator.getDefault().getModel().addResource(l);
						TreeParent root = (TreeParent) viewer.getInput();
						((TreeParent) root.getChildNamed("Resources")).addChild(new TreeFilter(l)); //$NON-NLS-1$

						if (to.getParent() instanceof TreeModel) {
							TreeModel tm = (TreeModel) to.getParent();
							((BusinessModel) tm.getModel()).addResource(l);
							tm.refresh("none"); //$NON-NLS-1$
						}
					}

					viewer.refresh();
					update();
				}
			}
		};
		createFilter.setImageDescriptor(ImageDescriptor.createFromImage(Activator.getDefault().getImageRegistry().get("filter"))); //$NON-NLS-1$

		addDataSource = new MtdAction(Messages.ViewTree_0) { //$NON-NLS-1$
			@Override
			public void run() {
				DataSourceWizard wizard = new DataSourceWizard();
				wizard.init(Activator.getDefault().getWorkbench(), (IStructuredSelection) Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection());

				WizardDialog dialog = new WizardDialog(getSite().getShell(), wizard);
				dialog.create();
				dialog.getShell().setSize(800, 600);
				dialog.getShell().setText(Messages.ViewTree_34); //$NON-NLS-1$

				if (dialog.open() == WizardDialog.OK) {
					TreeDataSource tds = new TreeDataSource(wizard.getDataSource());
					if (wizard.getDataSource() instanceof UnitedOlapDatasource) {
						onOlap = true;
					} else {
						onOlap = false;
					}
					TreeParent input = (TreeParent) viewer.getInput();
					TreeParent dsRoot = (TreeParent) input.getChildNamed("DataSources"); //$NON-NLS-1$
					dsRoot.addChild(tds);

					Activator.getDefault().getModel().addDataSource(wizard.getDataSource());
					viewer.refresh();
					update();
				}
			}
		};
		addDataSource.setImageDescriptor(ImageDescriptor.createFromImage(Activator.getDefault().getImageRegistry().get("datasource"))); //$NON-NLS-1$

		addModel = new MtdAction("Create Business Model") { //$NON-NLS-1$
			@Override
			public void run() {
				InputDialog d = new InputDialog(getSite().getShell(), Messages.ViewTree_38, Messages.ViewTree_39, "", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				if (d.open() == InputDialog.OK) {
					BusinessModel m = new BusinessModel();
					m.setName(d.getValue());
					((TreeParent) ((TreeParent) viewer.getInput()).getChildNamed("Business Models")).addChild(new TreeModel(m, "none")); //$NON-NLS-1$ //$NON-NLS-2$

					for (String groupName : GroupHelper.getGroups(0, 0)) {
						m.setGranted(groupName, true);
					}

					Activator.getDefault().getModel().addBusinessModel(m);
					viewer.refresh();
					update();
				}

			}
		};
		addModel.setImageDescriptor(ImageDescriptor.createFromImage(Activator.getDefault().getImageRegistry().get("model"))); //$NON-NLS-1$

		addPackage = new MtdAction(Messages.ViewTree_44) { //$NON-NLS-1$
			@Override
			public void run() {
				IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();
				if (ss.isEmpty()) {
					return;
				}

				BusinessModel bModel = null;

				if (ss.getFirstElement() instanceof TreeModel) {
					bModel = (BusinessModel) ((TreeModel) ss.getFirstElement()).getModel();
				} else {

					bModel = (BusinessModel) ((TreeModel) ((TreeParent) ss.getFirstElement()).getParent()).getModel();
				}
				DialogPackage dial = new DialogPackage(getSite().getShell(), bModel);

				if (dial.open() == Window.OK) {
					IBusinessPackage pack = dial.getBusinessPackage();
					bModel.addBusinessPackage(pack);

					for (String groupName : GroupHelper.getGroups(0, 0)) {
						pack.setGranted(groupName, true);
					}

					if (ss.getFirstElement() instanceof TreeModel) {
						((TreeModel) ss.getFirstElement()).refresh("none"); //$NON-NLS-1$
					} else {
						((TreeModel) ((TreeParent) ss.getFirstElement()).getParent()).refresh("none"); //$NON-NLS-1$
					}

					viewer.refresh();
					update();
				}
			}
		};
		addPackage.setImageDescriptor(ImageDescriptor.createFromImage(Activator.getDefault().getImageRegistry().get("package"))); //$NON-NLS-1$

		addBusinessTable = new MtdAction(Messages.ViewTree_48) { //$NON-NLS-1$
			@Override
			public void run() {
				IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();
				if (ss.isEmpty()) {
					return;
				}

				BusinessModel bModel = null;
				BusinessTableWizard wizard = null;

				if (ss.getFirstElement() instanceof TreeModel) {
					bModel = (BusinessModel) ((TreeModel) ss.getFirstElement()).getModel();
				} else if (!(ss.getFirstElement() instanceof TreeBusinessTable)) {
					bModel = (BusinessModel) ((TreeModel) ((TreeParent) ss.getFirstElement()).getParent()).getModel();
				}

				if (bModel != null) {
					wizard = new BusinessTableWizard(bModel);
				} else {

					wizard = new BusinessTableWizard();
				}

				wizard.init(Activator.getDefault().getWorkbench(), (IStructuredSelection) Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection());

				WizardDialog dialog = new WizardDialog(getSite().getShell(), wizard);
				dialog.create();
				dialog.getShell().setSize(800, 600);
				dialog.getShell().setText(Messages.ViewTree_49); //$NON-NLS-1$
				if (dialog.open() == Window.OK) {
					TreeBusinessTable tbt = new TreeBusinessTable(wizard.getBusinessTable(), "none"); //$NON-NLS-1$

					if (bModel == null) {
						((AbstractBusinessTable) ((TreeBusinessTable) ss.getFirstElement()).getTable()).addChild(wizard.getBusinessTable());
						((TreeBusinessTable) ss.getFirstElement()).refresh("none"); //$NON-NLS-1$
						((AbstractBusinessTable) ((TreeBusinessTable) ss.getFirstElement()).getTable()).getModel().updateRelations(false);

						TreeObject cur = ((TreeObject) ss.getFirstElement());
						do {
							cur = cur.getParent();
						} while (cur != null && (!(cur instanceof TreeModel)));

						if (cur != null) {
							((TreeModel) cur).refresh("none"); //$NON-NLS-1$
						}

					}

					else if (ss.getFirstElement() instanceof TreeModel) {
						((TreeParent) ((TreeModel) ss.getFirstElement()).getChildNamed("Business Tables")).addChild(tbt); //$NON-NLS-1$
						bModel.addBusinessTable(wizard.getBusinessTable());
						((TreeModel) ss.getFirstElement()).refresh("none"); //$NON-NLS-1$
					} else if (ss.getFirstElement() instanceof TreeBusinessTable) {
						((AbstractBusinessTable) ((TreeBusinessTable) ss.getFirstElement()).getTable()).addChild(tbt.getTable());

						((TreeModel) ((TreeBusinessTable) ss.getFirstElement()).getParent().getParent()).refresh("none"); //$NON-NLS-1$
					} else {

						bModel.addBusinessTable(wizard.getBusinessTable());

						((TreeModel) ((TreeObject) ss.getFirstElement()).getParent()).refresh("none"); //$NON-NLS-1$
					}

					for (String groupName : GroupHelper.getGroups(0, 0)) {
						wizard.getBusinessTable().setGranted(groupName, true);
					}
					viewer.refresh();
					update();
				}

			}
		};
		addBusinessTable.setImageDescriptor(ImageDescriptor.createFromImage(Activator.getDefault().getImageRegistry().get("bus_table"))); //$NON-NLS-1$

		browse = new MtdAction(Messages.ViewTree_54) { //$NON-NLS-1$
			@Override
			public void run() {
				IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();
				if (ss.getFirstElement() instanceof TreeBusinessTable) {
					TreeBusinessTable t = (TreeBusinessTable) ss.getFirstElement();
					getSite().getShell().setCursor(new Cursor(Display.getCurrent(), SWT.CURSOR_WAIT));
					DialogBusinessTable dial = new DialogBusinessTable(getSite().getShell(), (AbstractBusinessTable) t.getTable());

					dial.open();
				} else if (ss.getFirstElement() instanceof TreeLov) {
					TreeLov t = (TreeLov) ss.getFirstElement();

					getSite().getShell().setCursor(new Cursor(Display.getCurrent(), SWT.CURSOR_WAIT));
					DialogColumn dial = new DialogColumn(getSite().getShell(), t.getLov());
					dial.open();
				}

				getSite().getShell().setCursor(new Cursor(Display.getCurrent(), SWT.CURSOR_ARROW));
			}
		};
		browse.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor("browse")); //$NON-NLS-1$

		createLov = new MtdAction(Messages.ViewTree_55) { //$NON-NLS-1$
			@Override
			public void run() {

				IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();
				if (ss.isEmpty()) {
					return;
				}

				TreeParent to = (TreeParent) ss.getFirstElement();

				DialogLov dial = new DialogLov(getSite().getShell());
				if (dial.open() == Window.OK) {
					ListOfValue l = dial.getLov();

					if (to instanceof TreeModel) {
						((BusinessModel) ((TreeModel) ss.getFirstElement()).getModel()).addResource(l);
						// add the resource to the MetaDataModel
						Activator.getDefault().getModel().addResource(l);

						TreeParent root = (TreeParent) viewer.getInput();
						((TreeParent) root.getChildNamed("Resources")).addChild(new TreeLov(l)); //$NON-NLS-1$
						((TreeModel) ss.getFirstElement()).refresh("none"); //$NON-NLS-1$
					} else {
						Activator.getDefault().getModel().addResource(l);
						TreeParent root = (TreeParent) viewer.getInput();
						((TreeParent) root.getChildNamed("Resources")).addChild(new TreeLov(l)); //$NON-NLS-1$

						if (to.getParent() instanceof TreeModel) {
							TreeModel tm = (TreeModel) to.getParent();
							((BusinessModel) tm.getModel()).addResource(l);
							tm.refresh("none"); //$NON-NLS-1$
						}

					}

					update();
					viewer.refresh();
				}
			}
		};
		createLov.setImageDescriptor(ImageDescriptor.createFromImage(Activator.getDefault().getImageRegistry().get("lov"))); //$NON-NLS-1$

		edit = new MtdAction(Messages.ViewTree_61) { //$NON-NLS-1$
			@Override
			public void run() {
				// get the selected item type
				IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();
				if (ss.isEmpty()) {
					return;
				}

				Object o = ss.getFirstElement();

				if (o instanceof TreeBusinessTable) {
					AbstractBusinessTable bT = (AbstractBusinessTable) ((TreeBusinessTable) o).getTable();

					BusinessTableWizard wizard = new BusinessTableWizard(bT.getModel(), bT, true);
					wizard.init(Activator.getDefault().getWorkbench(), (IStructuredSelection) Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection());

					WizardDialog dialog = new WizardDialog(getSite().getShell(), wizard);
					dialog.create();
					dialog.getShell().setSize(800, 600);
					dialog.getShell().setText(Messages.ViewTree_62); //$NON-NLS-1$
					if (dialog.open() == Window.OK) {
						((TreeBusinessTable) o).refresh("none"); //$NON-NLS-1$
						((AbstractBusinessTable) ((TreeBusinessTable) o).getTable()).getModel().updateRelations(false);

						TreeObject cur = (TreeObject) o;
						do {
							cur = cur.getParent();
						} while (cur != null && (!(cur instanceof TreeModel)));

						if (cur != null) {
							((TreeModel) cur).refresh("none"); //$NON-NLS-1$
						}
						if (((TreeBusinessTable) o).getParent() instanceof TreePackage) {
							TreeModel tm = (TreeModel) ((TreeBusinessTable) o).getParent().getParent().getParent();
							((TreeBusinessTable) ((TreeParent) tm.getChildNamed("Business Tables")).getChildNamed(((TreeParent) o).getName())).refresh("none"); //$NON-NLS-1$ //$NON-NLS-2$
						}

						viewer.refresh();
						update();
					}

				} else if (o instanceof TreePackage) {
					IBusinessPackage p = ((TreePackage) o).getPackage();
					DialogPackage d = new DialogPackage(getSite().getShell(), (BusinessModel) p.getBusinessModel(), p);
					if (d.open() == DialogPackage.OK) {
						((TreePackage) o).refresh("none"); //$NON-NLS-1$
						viewer.refresh();
						viewer.setSelection(new StructuredSelection(o));
					}
				} else if (o instanceof TreeDataSource) {
					DialogDataSource d = new DialogDataSource(getSite().getShell(), ((TreeDataSource) o).getDataSource());

					if (d.open() == Window.OK) {

					}
					update();
					viewer.refresh();

				} else if (o instanceof TreeLov) {
					DialogLov d = new DialogLov(getSite().getShell(), ((TreeLov) o).getLov());
					if (d.open() == Window.OK) {
						update();
						viewer.refresh();
					}
				} else if (o instanceof TreeComplexFilter) {
					DialogComplexFilter d = new DialogComplexFilter(getSite().getShell(), ((TreeComplexFilter) o).getFilter());
					if (d.open() == Window.OK) {
						update();
						viewer.refresh();
					}
				} else if (o instanceof TreeFilter) {
					DialogFilter d = new DialogFilter(getSite().getShell(), ((TreeFilter) o).getFilter(), onOlap);
					if (d.open() == Window.OK) {
						update();
						viewer.refresh();
					}
				} else if (o instanceof TreePrompt) {
					DialogPrompt d = new DialogPrompt(getSite().getShell(), ((TreePrompt) o).getPrompt());
					if (d.open() == Window.OK) {
						update();
						viewer.refresh();
					}
				} else if (o instanceof TreeResource) {
					IResource r = ((TreeResource) o).getResource();

					if (r instanceof ComplexFilter) {
						DialogComplexFilter d = new DialogComplexFilter(getSite().getShell(), ((ComplexFilter) r));
						if (d.open() == Window.OK) {
							update();
							viewer.refresh();
						}
					} else if (r instanceof SqlQueryFilter) {
						DialogSqlFilter2 d = new DialogSqlFilter2(getSite().getShell(), ((SqlQueryFilter) r));
						if (d.open() == Window.OK) {
							update();
							viewer.refresh();
						}
					} else if (r instanceof Prompt) {
						DialogPrompt d = new DialogPrompt(getSite().getShell(), ((Prompt) r));
						if (d.open() == Window.OK) {
							update();
							viewer.refresh();
						}
					} else if (r instanceof ListOfValue) {
						DialogLov d = new DialogLov(getSite().getShell(), ((ListOfValue) r));
						if (d.open() == Window.OK) {
							update();
							viewer.refresh();
						}
					} else if (r instanceof Filter) {
						DialogFilter d = new DialogFilter(getSite().getShell(), ((Filter) r), onOlap);
						if (d.open() == Window.OK) {
							update();
							viewer.refresh();
						}
					}

				} else if (o instanceof TreeRelation) {
					TreeRelation tr = (TreeRelation) o;
					DialogRelation d = new DialogRelation(getSite().getShell(), tr.getRelation().getDataSource(), tr.getRelation());
					if (d.open() == DialogDimension.OK) {
//						Relation r = d.getRelation();
						
						viewer.refresh();
					}

				} else if (ss.getFirstElement() instanceof TreeResource && ((TreeResource) ss.getFirstElement()).getResource() instanceof FmdtDimension) {
					DialogDimension dial = new DialogDimension(getSite().getShell(), (FmdtDimension) ((TreeResource) ss.getFirstElement()).getResource());
					if (dial.open() == DialogDimension.OK) {
						viewer.refresh();
					}
				} else if (ss.getFirstElement() instanceof TreeResource && ((TreeResource) ss.getFirstElement()).getResource() instanceof FmdtMeasure) {
					DialogMeasure dial = new DialogMeasure(getSite().getShell(), (FmdtMeasure) ((TreeResource) ss.getFirstElement()).getResource());
					if (dial.open() == DialogDimension.OK) {
						viewer.refresh();
					}
				}

			}
		};
		edit.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor("edit")); //$NON-NLS-1$
		queryBuilder = new MtdAction(Messages.ViewTree_66) { //$NON-NLS-1$
			@Override
			public void run() {
				IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();
				if (ss.isEmpty()) {
					return;
				}

				Object o = ss.getFirstElement();

				if (o instanceof TreePackage) {

					IBusinessPackage pack = ((TreePackage) o).getPackage();

					if (!pack.isMonoDataSource()) {
						MessageDialog.openInformation(getSite().getShell(), Messages.ViewTree_67, Messages.ViewTree_68); //$NON-NLS-1$ //$NON-NLS-2$
					} else {
						if (pack.isOnOlapDataSource()) {
							DialogOlapQueryBuilder d = new DialogOlapQueryBuilder(getSite().getShell(), ((TreePackage) o).getPackage());
							d.open();
						} else {
							DialogQueryBuilder d = new DialogQueryBuilder(getSite().getShell(), ((TreePackage) o).getPackage());
							d.open();

							for (TreeObject child : ((TreePackage) o).getChildren()) {
								if (child instanceof TreeParent && child.getName().equals("Queries")) {
									((TreeParent) child).removeAll();
									if (pack.getSavedQueries() != null) {
										for (SavedQuery query : pack.getSavedQueries()) {
											((TreeParent) child).addChild(new TreeSavedQuery(query));
										}
									}
								}
							}

							viewer.refresh();
							update();
						}
					}
				}
			}
		};

		editConnection = new MtdAction(Messages.ViewTree_69) { //$NON-NLS-1$
			@Override
			public void run() {
				IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();

				if (!ss.isEmpty() && ss.getFirstElement() instanceof TreeConnection) {
					IConnection con = ((TreeConnection) ss.getFirstElement()).getConnection();
					if (con instanceof SQLConnection) {
						DialogConnection d = new DialogConnection(getSite().getShell(), (SQLConnection) con);

						if (d.open() == Window.OK) {

						}
						update();
					}

					else {

						DataSourceWizard wizard = new DataSourceWizard();
						wizard.init(Activator.getDefault().getWorkbench(), (IStructuredSelection) Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection());

						WizardDialog dialog = new WizardDialog(getSite().getShell(), wizard);
						dialog.create();
						dialog.getShell().setSize(800, 600);
						dialog.getShell().setText(Messages.ViewTree_34); //$NON-NLS-1$

						if (dialog.open() == WizardDialog.OK) {
							TreeDataSource tds = new TreeDataSource(wizard.getDataSource());
							if (wizard.getDataSource() instanceof UnitedOlapDatasource) {
								onOlap = true;
							} else {
								onOlap = false;
							}
							TreeParent input = (TreeParent) viewer.getInput();
							TreeParent dsRoot = (TreeParent) input.getChildNamed("DataSources"); //$NON-NLS-1$
							dsRoot.addChild(tds);

							Activator.getDefault().getModel().addDataSource(wizard.getDataSource());
							viewer.refresh();
							update();
						}
					}
				}

			}
		};
		editConnection.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor("edit")); //$NON-NLS-1$

		addMultiRelation = new MtdAction(Messages.ViewTree_70) { //$NON-NLS-1$
			public void run() {
				MultiDSRelation rel = new MultiDSRelation();
				DialogMultiRelation d = new DialogMultiRelation(getSite().getShell(), rel);
				if (d.open() == DialogMultiRelation.OK) {
					Activator.getDefault().getModel().addMultiRelation(rel);

					TreeParent tp = (TreeParent) viewer.getInput();
					if (rel.getLeftDataSource() == rel.getRightDataSource()) {

						for (TreeObject obj : tp.getChildren()) {
							BusinessModel m = (BusinessModel) ((TreeModel) obj).getModel();
							m.updateRelations(false);
							((TreeModel) obj).refresh("none"); //$NON-NLS-1$
						}
					} else {
						tp = (TreeParent) tp.getChildNamed("DataSources"); //$NON-NLS-1$
						((TreeParent) tp.getChildNamed("External relations")).addChild(new TreeMultiRelation(rel)); //$NON-NLS-1$

					}
					viewer.refresh();
					update();
				}
			}
		};

		delete = new MtdAction(Messages.ViewTree_73) { //$NON-NLS-1$
			public void run() {
				IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();
				if (ss.isEmpty()) {
					return;
				}

				boolean confirDelete = Activator.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.P_CONFIRM_DELETION);

				if (confirDelete) {
					if (!MessageDialog.openConfirm(getSite().getShell(), Messages.ViewTree_45, Messages.ViewTree_46)) {
						return;
					}
				}

				Object o = ss.getFirstElement();
				MetaData model = Activator.getDefault().getModel();
				TreeObject tO = null;
				try {
					tO = (TreeObject) o;
				} catch (Exception e) {

					if (o instanceof Variable) {
						model.removeVariable((Variable) o);
					} else if (o instanceof Script) {
						model.removeScript((Script) o);
					}

					viewer.refresh();
					update();

					return;
				}
				// DataSource
				if (o instanceof TreeDataSource) {
					model.removeDataSource(((TreeDataSource) o).getDataSource());
					((TreeParent) o).getParent().removeChild(tO);
				}
				// alternate connection
				if (o instanceof TreeConnection) {
					IConnection c = ((TreeConnection) o).getConnection();
					SQLDataSource s = (SQLDataSource) ((TreeDataSource) ((TreeConnection) o).getParent()).getDataSource();

					if (s.getConnectionNames().size() > 1) {
						s.removeConnection(c);
						((TreeDataSource) ((TreeConnection) o).getParent()).refresh();
						viewer.refresh();
					} else {
						MessageDialog.openInformation(getSite().getShell(), "Alternate Connection deletion", "Cannot delete the only DataSource's connection.");

					}

					return;
				}

				if (o instanceof TreeDataStream) {
					TreeDataStream tds = (TreeDataStream) o;
					boolean b = MessageDialog.openQuestion(getSite().getShell(), Messages.ViewTree_47, Messages.ViewTree_50);

					if (b) {
						List<Relation> removed = ((TreeDataSource) tds.getParent()).getDataSource().remove(tds.getDataStream());
						tds.getParent().removeChild(tds);
						tds.getDataStream().getDataSource().remove(tds.getDataStream());

						viewer.refresh();
					}

				}

				if (o instanceof TreeDataStreamElement) {

					TreeDataStreamElement tdse = (TreeDataStreamElement) o;
					// calculated column for DataStream
					if (tdse.getParent() instanceof TreeDataStream) {
						if (MessageDialog.openConfirm(getSite().getShell(), Messages.ViewTree_51, Messages.ViewTree_52)) {
							((TreeDataStream) tdse.getParent()).getDataStream().removeDataStreamElement(tdse.getDataStreamElement());
							tdse.getParent().removeChild(tdse);
							
							TreeParent root = (TreeParent) viewer.getInput();
							TreeParent bmodels = (TreeParent) root.getChildNamed("Business Models");
							
							for(TreeObject mod : bmodels.getChildren()) {
								TreeParent btables = (TreeParent) ((TreeParent)mod).getChildNamed("Business Tables");
								for(TreeObject tab : btables.getChildren()) {
									TreeObject c = null;
									for(TreeObject col : ((TreeParent)tab).getChildren()) {
										if(((TreeDataStreamElement)col).getDataStreamElement().getName().equals(tdse.getDataStreamElement().getName())){
											c = col;
											break;
										}
									}
									if(c != null) {
										((TreeParent)tab).removeChild(c);
										((IBusinessTable)tab.getContainedModelObject()).removeColumn(tdse.getDataStreamElement());
									}
								}
							}
							
//							for(IBusinessModel mo : model.getBusinessModels()) {
//								for(IBusinessTable ta : mo.getBusinessTables()) {
//									IDataStreamElement remove = null;
//									for(IDataStreamElement elem : ta.getColumns("none")) {
//										if(elem.getName().equals(tdse.getDataStreamElement().getName())) {
//											remove = elem;
//											break;
//										}
//									}
//									if(remove != null) {
//										ta.removeColumn(remove);
//									}
//								}
//							}
							
							viewer.refresh();
						}
//						((TreeDataStream) tdse.getParent()).getDataStream().removeDataStreamElement(tdse.getDataStreamElement());
//						tdse.getParent().removeChild(tdse);
//						viewer.refresh();
					} else if (tdse.getParent() instanceof TreeBusinessTable && ((TreeBusinessTable) tdse.getParent()).getTable() instanceof SQLBusinessTable) {
						((TreeBusinessTable) tdse.getParent()).getTable().removeColumn(tdse.getDataStreamElement());
						tdse.getParent().removeChild(tdse);
						viewer.refresh();
					} 
//					else if (tdse.getParent() instanceof TreeDataStream) {
//
//						if (MessageDialog.openConfirm(getSite().getShell(), Messages.ViewTree_51, Messages.ViewTree_52)) {
//							((TreeDataStream) tdse.getParent()).getDataStream().removeDataStreamElement(tdse.getDataStreamElement());
//							tdse.getParent().removeChild(tdse);
//							viewer.refresh();
//						}
//
//					}

				}

				// BusinessModel
				if (o instanceof TreeModel) {
					model.removeBusinessModel(((TreeModel) o).getModel());
					((TreeParent) o).getParent().removeChild(tO);
				}
				// BusinessPackage
				if (o instanceof TreePackage) {
					IBusinessPackage t = ((TreePackage) o).getPackage();
					((BusinessPackage) t).getBusinessModel().removePackage(t.getName());
					((TreeParent) o).getParent().removeChild(tO);
				}
				// BusinessTable
				if (o instanceof TreeBusinessTable) {
					TreeParent tp = (TreeParent) o;
					TreeParent parent = tp.getParent().getParent();

					IBusinessTable t = ((TreeBusinessTable) o).getTable();

					// from Business Model
					if (parent instanceof TreeModel) {
						((BusinessModel) ((TreeModel) parent).getModel()).removeBusinessTable(t);
						((TreeParent) o).getParent().removeChild(tO);
						TreeModel tMod = (TreeModel) parent;

						// remove from Package that refered it
						for (IBusinessPackage p : tMod.getModel().getBusinessPackages("none")) { //$NON-NLS-1$
							BusinessPackage pack = (BusinessPackage) p;

							if (pack.getBusinessTables("none").contains(t)) { //$NON-NLS-1$
								TreeParent tPack = (TreeParent) ((TreeParent) tMod.getChildNamed("Business Packages")).getChildNamed(pack.getName()); //$NON-NLS-1$

								TreeObject tTable = ((TreeParent) tPack.getChildNamed("Business Tables")).getChildNamed(t.getName()); //$NON-NLS-1$
								tTable.getParent().removeChild(tTable);
								
								((TreePackage)tPack).getPackage().removeBusinessTable(((TreeBusinessTable)tTable).getTable());
							}
						}
					}

					// from BusinessPackage
					else if (parent instanceof TreePackage) {
						((BusinessPackage) ((TreePackage) parent).getPackage()).removeBusinessTable(t);
						((TreeParent) o).getParent().removeChild(tO);
					}

					// from BusinessTable
					else if (((TreeObject) o).getParent() instanceof TreeBusinessTable) {
						((SQLBusinessTable) ((TreeBusinessTable) ((TreeObject) o).getParent()).getTable()).removeChild(t);
						((TreeParent) o).getParent().removeChild(tO);
					}

				}
				// SimpleRelation
				if (o instanceof TreeRelation) {
					TreeRelation tr = (TreeRelation) o;
					TreeParent parent = tr.getParent().getParent();
					Relation t = ((TreeRelation) o).getRelation();

					if (parent instanceof TreeDataSource) {
						((TreeDataSource) parent).getDataSource().removeRelation(t);
						tr.getParent().removeChild(tr);

					} else if (parent instanceof TreeModel) {
						((TreeModel) parent).getModel().removeRelation(t);
						tr.getParent().removeChild(tr);
					}

				}
				if (o instanceof TreeMultiRelation) {
					TreeMultiRelation tr = (TreeMultiRelation) o;
					TreeParent parent = tr.getParent();
					MultiDSRelation t = ((TreeMultiRelation) o).getRelation();

				}

				if (o instanceof TreeSavedQuery) {
					TreeSavedQuery tr = (TreeSavedQuery) o;
					TreeParent parent = tr.getParent();
					parent.removeChild(tr);

					if (parent.getParent() instanceof TreePackage) {
						((TreePackage) parent.getParent()).getPackage().removeSavedQuery(tr.getQuery());
					}
				}

				// LFilter
				if (o instanceof TreeResource) {
					TreeResource tLov = (TreeResource) o;
					TreeParent ancestor = tLov.getParent().getParent();

					// from BusinessModel
					if (ancestor instanceof TreeModel) {
						TreeModel tModel = (TreeModel) ancestor;
						((BusinessModel) tModel.getModel()).removeResource(tLov.getResource());
						tLov.getParent().removeChild(tLov);

						// remove from BusinessPackage if it's present
						TreeParent root = (TreeParent) tModel.getChildNamed("Business Packages"); //$NON-NLS-1$
						for (IBusinessPackage p : Activator.getDefault().getModel().getPackagesContaining(tLov.getResource())) {
							if (root.getChildNamed(p.getName()) != null) {
								TreeParent tObj = (TreeParent) root.getChildNamed(p.getName());

								TreeObject found = ((TreeParent) (tObj.getChildNamed("Resources"))).getChildNamed(tLov.getName()); //$NON-NLS-1$

								found.getParent().removeChild(found);
								p.removeResource(tLov.getResource());
							}

						}

					}
					// from BusinessPackage
					else if (ancestor instanceof TreePackage) {
						TreePackage tPack = (TreePackage) ancestor;
						tPack.getPackage().removeResource(tLov.getResource());
						tLov.getParent().removeChild(tLov);
					} else if (tLov.getParent() instanceof TreeDataStream) {
						TreeDataStream tds = (TreeDataStream) tLov.getParent();
						tds.getDataStream().removeGenericFilter((IFilter) tLov.getResource());
						tLov.getParent().removeChild(tLov);
					} else {
						model.delResource(tLov.getResource());

						// remove in cascade from businesspakage and
						// businessmodels
						TreeParent root = tLov.getParent().getParent();
						tLov.getParent().removeChild(tLov);
						List<TreeObject> removeFromTheirParent = new ArrayList<TreeObject>();
						for (TreeObject m : ((TreeParent) root.getChildNamed("Business Models")).getChildren()) {

							for (TreeObject r : ((TreeParent) (((TreeParent) m).getChildNamed("Resources"))).getChildren()) {
								if (((TreeResource) r).getResource() == tLov.getResource()) {
									removeFromTheirParent.add(r);

								}
							}

							for (TreeObject p : ((TreeParent) (((TreeParent) m).getChildNamed("Business Packages"))).getChildren()) {
								for (TreeObject r : ((TreeParent) (((TreeParent) p).getChildNamed("Resources"))).getChildren()) {
									if (((TreeResource) r).getResource() == tLov.getResource()) {
										removeFromTheirParent.add(r);

									}
								}
							}
						}

						for (TreeObject x : removeFromTheirParent) {
							x.getParent().removeChild(x);
						}

					}
				}

				if (o instanceof TreeRelationStrategy) {
					((TreeModel) ((TreeRelationStrategy) o).getParent().getParent()).getModel().getRelationStrategies().remove(((TreeRelationStrategy) o).getStrategy());
					((TreeRelationStrategy) o).getParent().removeChild((TreeObject) o);
				}

				viewer.refresh();
				update();
			}
		};
		delete.setImageDescriptor(ImageDescriptor.createFromImage(Activator.getDefault().getImageRegistry().get("delete"))); //$NON-NLS-1$

		addRelation = new MtdAction(Messages.ViewTree_85) { //$NON-NLS-1$
			public void run() {
				IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();

				/*
				 * from a datasource
				 */

				if (ss.getFirstElement() instanceof TreeDataSource || (((TreeObject) ss.getFirstElement()).getName().equals(Messages.ViewTree_59) && ((TreeObject) ss.getFirstElement()).getParent() instanceof TreeDataSource)) {
					TreeDataSource tDS = null;

					if (ss.getFirstElement() instanceof TreeDataSource) {
						tDS = (TreeDataSource) ss.getFirstElement();
					} else {
						tDS = (TreeDataSource) ((TreeObject) ss.getFirstElement()).getParent();
					}

					DialogRelation dial = new DialogRelation(getSite().getShell(), tDS.getDataSource());
					if (dial.open() == DialogRelation.OK) {
						tDS.getDataSource().addRelation(dial.getRelation());
						((TreeParent) tDS.getChildNamed("Relations")).addChild(new TreeRelation(dial.getRelation())); //$NON-NLS-1$

						viewer.refresh();
						update();

					}
				} else if (ss.getFirstElement() instanceof TreeDataStream) {

					DialogRelation dial = new DialogRelation(getSite().getShell(), ((TreeDataStream) ss.getFirstElement()).getDataStream().getDataSource());
					if (dial.open() == DialogRelation.OK) {
						((TreeDataStream) ss.getFirstElement()).getDataStream().getDataSource().addRelation(dial.getRelation());

						((TreeParent) ((TreeParent) ((TreeDataStream) ss.getFirstElement()).getParent()).getChildNamed("Relations")).addChild(new TreeRelation(dial.getRelation())); //$NON-NLS-1$

						viewer.refresh();
						update();

					}
				} else if (ss.getFirstElement() instanceof TreeDataStreamElement) {
					DialogRelation dial = new DialogRelation(getSite().getShell(), ((TreeDataStreamElement) ss.getFirstElement()).getDataStreamElement().getDataStream().getDataSource());
					if (dial.open() == DialogRelation.OK) {
						((TreeDataStreamElement) ss.getFirstElement()).getDataStreamElement().getDataStream().getDataSource().addRelation(dial.getRelation());

						((TreeParent) ((TreeParent) ss.getFirstElement()).getParent().getParent().getChildNamed("Relations")).addChild(new TreeRelation(dial.getRelation())); //$NON-NLS-1$

						viewer.refresh();
						update();

					}
				} else if (ss.getFirstElement() instanceof TreeModel || (((TreeObject) ss.getFirstElement()).getName().equals(Messages.ViewTree_60) && ((TreeObject) ss.getFirstElement()).getParent() instanceof TreeModel)) {
					TreeModel tM = null;

					if (ss.getFirstElement() instanceof TreeModel) {
						tM = (TreeModel) ss.getFirstElement();
					} else {
						tM = (TreeModel) ((TreeObject) ss.getFirstElement()).getParent();
					}

					DialogSelectDataSource dDs = new DialogSelectDataSource(getSite().getShell());

					IDataSource ds = null;

					if (dDs.open() == Dialog.OK) {
						ds = dDs.getDataSource();
					}

					if (ds == null) {
						return;
					}

					DialogRelation dial = new DialogRelation(getSite().getShell(), ds);
					if (dial.open() == DialogRelation.OK) {
						((BusinessModel) tM.getModel()).addRelation(dial.getRelation());
						tM.refresh("none"); //$NON-NLS-1$

						viewer.refresh();
						update();

					}
				}

			}
		};
		addRelation.setImageDescriptor(ImageDescriptor.createFromImage(Activator.getDefault().getImageRegistry().get("relation"))); //$NON-NLS-1$
		addLogicalTable = new MtdAction(Messages.ViewTree_89) { //$NON-NLS-1$
			@Override
			public void run() {
				IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();
				if (ss.isEmpty() || !(ss.getFirstElement() instanceof TreeDataSource)) {
					return;
				}

				TreeDataSource tds = (TreeDataSource) ss.getFirstElement();

				TableWizard wizard = new TableWizard(tds.getDataSource());
				wizard.init(Activator.getDefault().getWorkbench(), (IStructuredSelection) Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection());

				WizardDialog dialog = new WizardDialog(getSite().getShell(), wizard);
				dialog.create();
				dialog.getShell().setSize(800, 600);
				dialog.getShell().setText(Messages.ViewTree_90); //$NON-NLS-1$

				if (dialog.open() == WizardDialog.OK) {
					IDataStream s = wizard.getTable();
					tds.addChild(new TreeDataStream(s));
					viewer.refresh();
					update();
				}
			}
		};
		addLogicalTable.setImageDescriptor(ImageDescriptor.createFromImage(Activator.getDefault().getImageRegistry().get("log_table"))); //$NON-NLS-1$

		addFormula = new MtdAction(Messages.ViewTree_91) { //$NON-NLS-1$
			public void run() {
				IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();
				if (ss.isEmpty() || !(ss.getFirstElement() instanceof TreeDataStream)) {
					return;
				}
//				IDataSource ds = ((TreeDataStream) ss.getFirstElement()).getDataStream().getDataSource();
//
//				IConnection con = ds.getConnections("none").get(0); //$NON-NLS-1$
				List<IDataStream> tables = new ArrayList<IDataStream>();
				try {
					tables.add(((TreeDataStream) ss.getFirstElement()).getDataStream());
					DialogFormula d = new DialogFormula(getSite().getShell(), tables);
					if (d.open() == DialogFormula.OK) {
						((TreeDataStream) ss.getFirstElement()).getDataStream().addCalculatedElement(d.getFormula());

						((TreeDataStream) ss.getFirstElement()).addChild(new TreeFormula(d.getFormula()));
//						for (ITable t : tables) {
//							if (d.getFormula().getFormula().contains(t.getName() + ".")) { //$NON-NLS-1$
//								d.getFormula().setFormula(d.getFormula().getFormula().replace(t.getName() + ".", "`" + d.getFormula().getDataStream().getName() + "`.")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//							}
//
//						}
						viewer.refresh();

					}

				} catch (Exception e1) {
					e1.printStackTrace();
					MessageDialog.openError(getSite().getShell(), Messages.ViewTree_14, e1.getMessage());
				}

			}
		};

		addAlternateConnection = new MtdAction(Messages.ViewTree_92) { //$NON-NLS-1$
			public void run() {
				IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();
				TreeDataSource tds = (TreeDataSource) ss.getFirstElement();

				DialogConnection con = new DialogConnection(getSite().getShell(), tds.getDataSource());
				if (con.open() == DialogConnection.OK) {
					tds.refresh();
					viewer.refresh();
					update();
				}
			}
		};
		addAlternateConnection.setImageDescriptor(ImageDescriptor.createFromImage(Activator.getDefault().getImageRegistry().get("object"))); //$NON-NLS-1$

		duplicateDataStream = new MtdAction(Messages.ViewTree_76) {
			public void run() {
				IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();
				TreeDataStream tds = (TreeDataStream) ss.getFirstElement();

				IDataStream ds = tds.getDataStream();

				if (ds instanceof SQLDataStream) {
					SQLDataStream copy = new SQLDataStream();
					copy.setDataSource(ds.getDataSource());
					copy.setName(Messages.ViewTree_77 + ds.getName());
					copy.setOrigin(ds.getOrigin());
					ds.setOutputLength(ds.getOutputLength());
					ds.setType(ds.getType());

					for (IDataStreamElement c : ds.getElements()) {
						IDataStreamElement nc = null;
						if (c instanceof ICalculatedElement) {
							nc = new ICalculatedElement(((ICalculatedElement) c).getFormula().replace(c.getDataStream().getName() + ".", copy.getName() + ".")); //$NON-NLS-1$ //$NON-NLS-2$
						} else {
							nc = new SQLDataStreamElement();
						}

						nc.setBackgroundColor(c.getBackgroundColor());
						nc.setDataStream(copy);
//						nc.setDefaultMeasureBehavior(c.getDefaultMeasureBehavior());
						nc.setName(c.getName().replace(ds.getName(), copy.getName()));
						nc.setOrigin(c.getOrigin());
						nc.setType(c.getType());

						for (String s : c.getVisibility().keySet()) {
							nc.setVisible(s, c.getVisibility().get(s));
						}

						for (String s : c.getGrants().keySet()) {
							nc.setGranted(s, c.getGrants().get(s));
						}
						copy.addColumn(nc);
					}

					ds.getDataSource().add(copy);
					tds.getParent().addChild(new TreeDataStream(copy));

				}
				viewer.refresh();

			}
		};

		importRelation = new MtdAction(Messages.ViewTree_80) {
			public void run() {
				IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();
				TreeModel tM = (TreeModel) ss.getFirstElement();

				DialogImportRelation dial = new DialogImportRelation(getSite().getShell(), ((BusinessModel) tM.getModel()).getRelations());
				if (dial.open() == Dialog.OK) {

					for (Relation r : dial.getRelations()) {
						((BusinessModel) tM.getModel()).addRelation(r);
					}
					tM.refresh("none"); //$NON-NLS-1$
					viewer.refresh();

				}

			}
		};
		importRelation.setImageDescriptor(ImageDescriptor.createFromImage(Activator.getDefault().getImageRegistry().get("relation"))); //$NON-NLS-1$
	}

	public void reInit() {
		createModel();
		viewer.setSelection(new StructuredSelection());
	}

	private void setDnd() {
		int ops = DND.DROP_COPY;
		Transfer[] transfers = new Transfer[] { TextTransfer.getInstance() };

		viewer.addDragSupport(ops, transfers, new DragSourceListener() {

			public void dragFinished(DragSourceEvent event) {

			}

			public void dragSetData(DragSourceEvent event) {
				IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();

				StringBuffer buf = new StringBuffer();
				boolean first = true;
				
				List listElements = new ArrayList();
				
				for (Object o : ss.toList()) {
					if (o instanceof TreeDataStream) {					
						listElements.add(o);				
					} else if (o instanceof TreeBusinessTable) {				
						listElements.add(o);
					} else if (o instanceof TreeResource) {
						listElements.add(o);
					}
					else if(o instanceof TreeRelation) {
						listElements.add(o);
					}

				}
				
				for(Object o : listElements) {
					if(first) {
						first = false;
					}
					else {
						buf.append("/");
					}
					if (o instanceof TreeDataStream) {
						buf.append("DS;" + ((TreeDataStream)o).getName());
					} else if (o instanceof TreeBusinessTable) {				
						buf.append("BT;" + ((TreeBusinessTable)o).getName());
					} else if (o instanceof TreeResource) {
						buf.append("TR;" + ((TreeResource)o).getName());
					}
					else if(o instanceof TreeRelation) {
						buf.append("REL;" + ((TreeRelation)o).getRelation().getRelationKey());
					}
				}
				
				event.data = buf.toString();
			}

			public void dragStart(DragSourceEvent event) {

			}

		});

		viewer.addDropSupport(ops, transfers, new DropTargetListener() {

			public void dragEnter(DropTargetEvent event) {
				event.detail = DND.DROP_COPY;

			}

			public void dragLeave(DropTargetEvent event) {

			}

			public void dragOperationChanged(DropTargetEvent event) {

			}

			public void dragOver(DropTargetEvent event) {
				event.feedback |= DND.FEEDBACK_SCROLL;
			}

			public void drop(DropTargetEvent event) {
				
				String data = (String) event.data;
				String[] dataPart = data.split("/");
				
//				String[] buf = ((String) event.data).split("/"); //$NON-NLS-1$
				Object o = ((TreeItem) event.item).getData();
				MetaData meta = Activator.getDefault().getModel();
				
				
				for(String e : dataPart) {
					
					String[] parts = e.split(";");
					if(parts[0].equals("BT")) {
						if (o instanceof TreePackage) {
							BusinessModel m = (BusinessModel) ((TreePackage) o).getPackage().getBusinessModel();
			
							IBusinessTable t = m.getBusinessTable(parts[1]);
							((TreePackage) o).getPackage().addBusinessTable(t);
							((TreePackage) o).refresh("none"); //$NON-NLS-1$
							viewer.refresh();
							
						}
						else if(o instanceof TreeParent && ((TreeParent)o).getParent() instanceof TreePackage) {
							BusinessModel m = (BusinessModel) ((TreePackage) ((TreeParent)o).getParent()).getPackage().getBusinessModel();
							
							IBusinessTable t = m.getBusinessTable(parts[1]);
							((TreePackage) ((TreeParent)o).getParent()).getPackage().addBusinessTable(t);
							((TreePackage) ((TreeParent)o).getParent()).refresh("none"); //$NON-NLS-1$
							viewer.refresh();
						}
					}
					else if(parts[0].equals("TR")) {
						IResource r = meta.getResource(parts[1]);
						if (o instanceof TreeModel && e != null) {
							((BusinessModel) ((TreeModel) o).getModel()).addResource(r);
							((TreeModel) o).refresh("none"); //$NON-NLS-1$
							viewer.refresh();
						} else if (o instanceof TreeParent && ((TreeParent) o).getParent() instanceof TreeModel) {
							((BusinessModel) ((TreeModel) ((TreeParent) o).getParent()).getModel()).addResource(r);
							((TreeModel) ((TreeParent) o).getParent()).refresh("none"); //$NON-NLS-1$
							viewer.refresh();
						} else if (o instanceof TreePackage && e != null) {
							((TreePackage) o).getPackage().addResource(r);
							((TreePackage) o).refresh("none"); //$NON-NLS-1$
							viewer.refresh();
						} else if (o instanceof TreeParent && ((TreeParent) o).getParent() instanceof TreePackage) {
							((TreePackage) ((TreeParent) o).getParent()).getPackage().addResource(r);
							((TreePackage) ((TreeParent) o).getParent()).refresh("none"); //$NON-NLS-1$
							viewer.refresh();
						}
					}
					else if(parts[0].equals("REL")) {
						if (o instanceof TreeParent && ((TreeParent)o).getName().equals("Relations") && e != null) {
							TreeModel tm = (TreeModel) ((TreeParent)o).getParent();
							for(Relation rel : tm.getModel().getRelations()) {
								if(rel.getRelationKey().equals(parts[1])) {
									return;
								}
							}
							for(Relation rel : meta.getDataSources().iterator().next().getRelations()) {
								if(rel.getRelationKey().equals(parts[1])) {
									tm.getModel().getRelations().add(rel);
									break;
								}
							}
							viewer.refresh();
						}
					}
				}
				
//				int i = 0;
//				if (buf[i].equals("DST")) { //$NON-NLS-1$
//
//				} else if (buf[i].equals("BUT")) { //$NON-NLS-1$
//					if (o instanceof TreePackage) {
//						BusinessModel m = (BusinessModel) ((TreePackage) o).getPackage().getBusinessModel();
//						IBusinessTable t = m.getBusinessTable(buf[i + 2]);
//						((TreePackage) o).getPackage().addBusinessTable(t);
//						((TreePackage) o).refresh("none"); //$NON-NLS-1$
//						viewer.refresh();
//					}
//				} else if (buf[i].equals("RES")) { //$NON-NLS-1$
//					IResource r = meta.getResource(buf[i + 1]);
//					if (o instanceof TreeModel && r != null) {
//						((BusinessModel) ((TreeModel) o).getModel()).addResource(r);
//						((TreeModel) o).refresh("none"); //$NON-NLS-1$
//						viewer.refresh();
//					} else if (o instanceof TreeParent && ((TreeParent) o).getParent() instanceof TreeModel) {
//						((BusinessModel) ((TreeModel) ((TreeParent) o).getParent()).getModel()).addResource(r);
//						((TreeModel) ((TreeParent) o).getParent()).refresh("none"); //$NON-NLS-1$
//						viewer.refresh();
//					} else if (o instanceof TreePackage && r != null) {
//						((TreePackage) o).getPackage().addResource(r);
//						((TreePackage) o).refresh("none"); //$NON-NLS-1$
//						viewer.refresh();
//					} else if (o instanceof TreeParent && ((TreeParent) o).getParent() instanceof TreePackage) {
//						((TreePackage) ((TreeParent) o).getParent()).getPackage().addResource(r);
//						((TreePackage) ((TreeParent) o).getParent()).refresh("none"); //$NON-NLS-1$
//						viewer.refresh();
//					}
//				}

			}

			public void dropAccept(DropTargetEvent event) {
				Object o = ((TreeItem) event.item).getData();
				if (!((o instanceof TreeModel) || (o instanceof TreePackage))) {
					event = null;
				}

			}

		});
	}

	public Viewer getViewer() {

		return viewer;
	}

	private ToolItem[] toolBarItems = new ToolItem[11];

	private static final int IT_DATASOURCE = 0;
	private static final int IT_MULTI_REL = 1;
	private static final int IT_MODEL = 2;
	private static final int IT_TABLE = 3;
	private static final int IT_LOV = 4;
	private static final int IT_FILTER = 5;
	private static final int IT_COMPLXEX_FILTER = 6;
	private static final int IT_PROMPT = 7;
	private static final int IT_PACKAGE = 8;
	private static final int IT_DELETE = 9;
	private static final int IT_SQL_FILTER = 10;

	private void createToolbar() {
		toolBarItems[IT_DATASOURCE] = new ToolItem(toolbar, SWT.PUSH);
		toolBarItems[IT_DATASOURCE].setToolTipText(addDataSource.getText());
		toolBarItems[IT_DATASOURCE].setImage(addDataSource.getImageDescriptor().createImage());
		toolBarItems[IT_DATASOURCE].addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				addDataSource.run();
			}
		});

		toolBarItems[IT_MULTI_REL] = new ToolItem(toolbar, SWT.PUSH);
		toolBarItems[IT_MULTI_REL].setToolTipText(addMultiRelation.getText());
		toolBarItems[IT_MULTI_REL].setImage(Activator.getDefault().getImageRegistry().get("relation")); //$NON-NLS-1$
		toolBarItems[IT_MULTI_REL].addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				addMultiRelation.run();
			}
		});

		toolBarItems[IT_MODEL] = new ToolItem(toolbar, SWT.PUSH);
		toolBarItems[IT_MODEL].setToolTipText(addModel.getText());
		toolBarItems[IT_MODEL].setImage(addModel.getImageDescriptor().createImage());
		toolBarItems[IT_MODEL].addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				addModel.run();
			}
		});

		toolBarItems[IT_TABLE] = new ToolItem(toolbar, SWT.PUSH);
		toolBarItems[IT_TABLE].setToolTipText(addBusinessTable.getText());
		toolBarItems[IT_TABLE].setImage(addBusinessTable.getImageDescriptor().createImage());
		toolBarItems[IT_TABLE].addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				addBusinessTable.run();
			}
		});

		toolBarItems[IT_LOV] = new ToolItem(toolbar, SWT.PUSH);
		toolBarItems[IT_LOV].setToolTipText(createLov.getText());
		toolBarItems[IT_LOV].setImage(createLov.getImageDescriptor().createImage());
		toolBarItems[IT_LOV].addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				createLov.run();
			}
		});

		toolBarItems[IT_FILTER] = new ToolItem(toolbar, SWT.PUSH);
		toolBarItems[IT_FILTER].setToolTipText(createFilter.getText());
		toolBarItems[IT_FILTER].setImage(createFilter.getImageDescriptor().createImage());
		toolBarItems[IT_FILTER].addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				createFilter.run();
			}
		});

		toolBarItems[IT_COMPLXEX_FILTER] = new ToolItem(toolbar, SWT.PUSH);
		toolBarItems[IT_COMPLXEX_FILTER].setToolTipText(createComplexFilter.getText());
		toolBarItems[IT_COMPLXEX_FILTER].setImage(createComplexFilter.getImageDescriptor().createImage());
		toolBarItems[IT_COMPLXEX_FILTER].addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				createComplexFilter.run();
			}
		});

		toolBarItems[IT_SQL_FILTER] = new ToolItem(toolbar, SWT.PUSH);
		toolBarItems[IT_SQL_FILTER].setToolTipText(createSqlFilter.getText());
		toolBarItems[IT_SQL_FILTER].setImage(createSqlFilter.getImageDescriptor().createImage());
		toolBarItems[IT_SQL_FILTER].addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				createSqlFilter.run();
			}
		});

		toolBarItems[IT_PROMPT] = new ToolItem(toolbar, SWT.PUSH);
		toolBarItems[IT_PROMPT].setToolTipText(createPrompt.getText());
		toolBarItems[IT_PROMPT].setImage(createPrompt.getImageDescriptor().createImage());
		toolBarItems[IT_PROMPT].addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				createPrompt.run();
			}
		});

		toolBarItems[IT_PACKAGE] = new ToolItem(toolbar, SWT.PUSH);
		toolBarItems[IT_PACKAGE].setToolTipText(addPackage.getText());
		toolBarItems[IT_PACKAGE].setImage(addPackage.getImageDescriptor().createImage());
		toolBarItems[IT_PACKAGE].addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				addPackage.run();
			}
		});

		toolBarItems[IT_DELETE] = new ToolItem(toolbar, SWT.PUSH);
		toolBarItems[IT_DELETE].setToolTipText(delete.getText());
		toolBarItems[IT_DELETE].setImage(delete.getImageDescriptor().createImage());
		toolBarItems[IT_DELETE].addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				delete.run();
			}
		});
	}

	private void setLoD() {
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();

				if (ss.isEmpty()) {
					return;
				}

			}

		});
	}

	protected class FMDTTreeLabelProvider extends TreeLabelProvider {
		@Override
		public String getText(Object obj) {
			if (obj instanceof Script) {
				return ((Script) obj).getName();
			} else if (obj instanceof Variable) {
				return ((Variable) obj).getName();
			}
			return super.getText(obj);
		}

		@Override
		public Image getImage(Object obj) {
			if (obj instanceof Script) {
				return Activator.getDefault().getImageRegistry().get("script"); //$NON-NLS-1$
			} else if (obj instanceof Variable) {
				return Activator.getDefault().getImageRegistry().get("variable"); //$NON-NLS-1$
			}
			return super.getImage(obj);
		}
	}

	// this decorator adds the row nomber on a DataStream
	private class DataStreamDecorator extends DecoratingLabelProvider {
		private final Color error = new Color(Display.getDefault(), 255, 0, 0);
		private final Color notUsedInRelation = new Color(Display.getDefault(), 0, 183, 0);
		private final Color warn = new Color(Display.getDefault(), 230, 127, 39);
		private HashMap<Object, String> map = new HashMap<Object, String>();
		private HashMap<IDataStream, Integer> counter = new HashMap<IDataStream, Integer>();

		public DataStreamDecorator(ILabelDecorator decorator) {
			super(new FMDTTreeLabelProvider(), decorator);
		}

		@Override
		public String getText(Object element) {
			if (showSize && element instanceof TreeDataStream) {
				return super.getText(element) + getRowCount(((TreeDataStream) element).getDataStream());
			}
			return super.getText(element);
		}

		@Override
		public Color getForeground(Object element) {
			if (decorationType == HIGHLIGHTTYPE.ERROR) {
				if (element instanceof TreeObject) {

					if (element instanceof TreeParent) {
						for (TreeObject o : ((TreeParent) element).getChildren()) {
							if (map.get(o.getContainedModelObject()) != null) {
								return warn;
							}
						}
					}

					if (map.get(((TreeObject) element).getContainedModelObject()) != null) {
						return error;
					}

				}
			} else if (decorationType == HIGHLIGHTTYPE.USED_RELATION) {
				if (element instanceof TreeDataStream) {
					IDataStream ds = ((TreeDataStream) element).getDataStream();
					for (Relation r : ds.getDataSource().getRelations()) {
						if (r.isUsingTable(ds)) {
							return super.getForeground(element);
						}
					}

					return notUsedInRelation;
				}
			}
			return super.getForeground(element);
		}

		private String getRowCount(IDataStream table) {
			Integer c = counter.get(table);

			if (c == null || c == -1) {
				try {
					c = FieldValuesHelper.countRows(table);
					counter.put(table, c);
				} catch (Exception ex) {
					ex.printStackTrace();
					counter.put(table, -1);
					c = -1;
				}

			}

			if (c != -1) {
				return "   [" + c + " rows]"; //$NON-NLS-1$ //$NON-NLS-2$
			}
			return ""; //$NON-NLS-1$

		}
	}
}
