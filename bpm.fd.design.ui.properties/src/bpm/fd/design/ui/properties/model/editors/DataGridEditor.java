package bpm.fd.design.ui.properties.model.editors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;

import bpm.fd.api.core.model.FdModel;
import bpm.fd.api.core.model.MultiPageFdProject;
import bpm.fd.api.core.model.components.definition.ComponentConfig;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.components.definition.OrderingType;
import bpm.fd.api.core.model.components.definition.datagrid.ComponentDataGrid;
import bpm.fd.api.core.model.components.definition.datagrid.DataGridColumnOption;
import bpm.fd.api.core.model.components.definition.datagrid.DataGridColumnOption.DataGridCellType;
import bpm.fd.api.core.model.components.definition.datagrid.DataGridData;
import bpm.fd.api.core.model.components.definition.datagrid.DataGridDrill;
import bpm.fd.api.core.model.components.definition.datagrid.DataGridDrill.DrillTarget;
import bpm.fd.api.core.model.components.definition.datagrid.DataGridLayout;
import bpm.fd.api.core.model.components.definition.datagrid.DataGridOptions;
import bpm.fd.api.core.model.datas.ColumnDescriptor;
import bpm.fd.api.core.model.datas.DataSet;
import bpm.fd.api.core.model.datas.DataSource;
import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.properties.i18n.Messages;
import bpm.fd.design.ui.properties.model.Property;
import bpm.fd.design.ui.properties.model.PropertyGroup;
import bpm.fd.design.ui.properties.model.datagrid.PropertyGridLayout;
import bpm.fd.design.ui.properties.viewer.PropertiesContentProvider;
import bpm.fd.design.ui.viewer.labelprovider.DatasLabelProvider;

public class DataGridEditor extends ComponentEditor implements IComponentEditor {
	private ComboBoxViewerCellEditor dataSourceCbo;
	private ComboBoxViewerCellEditor dataSetCbo;
	private ComboBoxViewerCellEditor fieldCbo, fieldCbo2;
	private ComboBoxViewerCellEditor modelPageCbo;
	private ComboBoxViewerCellEditor layoutTypeCbo;

	private PropertyGroup columnsLayout;
	private TreeViewer layoutTree;

	public DataGridEditor(Composite parent) {
		super(parent);
		fillBar();
	}

	@Override
	protected void fillBar() {
		// createStyle(getControl());

		createParameters();
		createDatas(getControl());
		createLayout(getControl());
		createDrillDown(getControl());
	}

	private void createDatas(ExpandBar parent) {
		TreeViewer viewer = createViewer(parent);
		viewer.setContentProvider(new PropertiesContentProvider());
		TreeViewerColumn colValue = createValueViewerColum(viewer);

		ExpandItem item = new ExpandItem(parent, SWT.NONE);
		item.setControl(viewer.getTree());
		item.setText(Messages.DataGridEditor_0);

		dataSourceCbo = new ComboBoxViewerCellEditor(viewer.getTree(), SWT.READ_ONLY);
		dataSourceCbo.setContenProvider(new ArrayContentProvider());
		dataSourceCbo.setLabelProvider(new DatasLabelProvider());

		dataSetCbo = new ComboBoxViewerCellEditor(viewer.getTree(), SWT.READ_ONLY);
		dataSetCbo.setContenProvider(new ArrayContentProvider());
		dataSetCbo.setLabelProvider(new DatasLabelProvider());
		dataSetCbo.getViewer().addFilter(new ViewerFilter() {

			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				IStructuredSelection ss = (IStructuredSelection) dataSourceCbo.getViewer().getSelection();
				if (ss.isEmpty()) {
					return true;
				}
				else {
					String dsName = ((DataSource) ss.getFirstElement()).getName();
					return ((DataSet) element).getDataSourceName().equals(dsName);
				}
			}
		});

		fieldCbo = new ComboBoxViewerCellEditor(viewer.getTree(), SWT.READ_ONLY);
		fieldCbo.setContenProvider(new ArrayContentProvider());
		fieldCbo.setLabelProvider(new DatasLabelProvider());

		final Property datasource = new Property(Messages.DataGridEditor_1, dataSourceCbo);
		final Property dataset = new Property(Messages.DataGridEditor_2, dataSetCbo);
		final Property categoryOrdinal = new Property(Messages.DataGridEditor_3, fieldCbo);
		final PropertyGroup useorder = new PropertyGroup(Messages.DataGridEditor_4, new CheckboxCellEditor(viewer.getTree()));
		useorder.add(categoryOrdinal);

		colValue.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (getComponentDefinition() == null) {
					return "";}; //$NON-NLS-1$

				if (element == datasource) {
					return dataSourceCbo.getViewer().getCCombo().getText();
				}
				if (element == dataset) {
					if (getComponentDefinition().getDatas().getDataSet() == null) {
						return ""; //$NON-NLS-1$
					}
					return getComponentDefinition().getDatas().getDataSet().getName();
				}
				if (element == useorder) {
					return ((DataGridData) getComponentDefinition().getDatas()).getOrderType() != OrderingType.NONE ? "true" : "false"; //$NON-NLS-1$
				}
				try {
					if (element == categoryOrdinal) {
						return getComponentDefinition().getDatas().getDataSet().getDataSetDescriptor().getColumnsDescriptors().get(((DataGridData) getComponentDefinition().getDatas()).getOrderFieldPosition() - 1).getColumnLabel();
					}

				} catch (Exception ex) {

				}
				return ""; //$NON-NLS-1$
			}

		});

		colValue.setEditingSupport(new EditingSupport(viewer) {

			@Override
			protected void setValue(Object element, Object value) {
				if (getComponentDefinition() == null) {
					return;
				}
				DataGridData data = (DataGridData) getComponentDefinition().getDatas();
				if (element == dataset) {
					data.setDataSet((DataSet) value);
					fieldCbo.setInput(((DataSet) value).getDataSetDescriptor().getColumnsDescriptors());
					refreshParameters();
					refreshColumnLayout();
				}
				if (element == datasource) {
					dataSetCbo.setInput(getComponentDefinition().getDictionary().getDatasets());
				}
				if (element == useorder) {
					data.setOrderType((Boolean) value ? OrderingType.ASC : OrderingType.NONE);
				}
				if (data.getDataSet() == null) {
					return;
				}
				if (element == categoryOrdinal) {
					data.setOrderFieldPosition(((ComboBoxViewerCellEditor) getCellEditor(element)).getViewer().getCCombo().getSelectionIndex() + 1);
				}
				notifyChangeOccured();
				resize();
			}

			@Override
			protected Object getValue(Object element) {
				if (getComponentDefinition() == null) {
					return null;
				}
				try {
					DataGridData data = (DataGridData) getComponentDefinition().getDatas();
					if (element == useorder) {
						return data.getOrderType() != OrderingType.NONE ? true : false;
					}
					if (element == dataset) {
						return data.getDataSet();
					}
					if (element == datasource) {
						return ((IStructuredSelection) dataSourceCbo.getViewer().getSelection()).getFirstElement();
					}
					if (element == categoryOrdinal) {
						((List) fieldCbo.getViewer().getInput()).get(data.getOrderFieldPosition());
					}
				} catch (Exception ex) {
				}

				return null;
			}

			@Override
			protected CellEditor getCellEditor(Object element) {
				return ((Property) element).getCellEditor();
			}

			@Override
			protected boolean canEdit(Object element) {
				return element instanceof Property && ((Property) element).getCellEditor() != null;
			}
		});

		List input = new ArrayList();
		input.add(datasource);
		input.add(dataset);
		input.add(useorder);
		viewer.setInput(input);
	}

	protected void createColors(ExpandBar parent) {
		TreeViewer viewer = createViewer(parent);
		TreeViewerColumn valueCol = createValueViewerColum(layoutTree);

		ExpandItem item = new ExpandItem(parent, SWT.NONE);
		item.setControl(layoutTree.getTree());
		item.setText(Messages.DataGridEditor_9);

		final PropertyGroup usecolor = new PropertyGroup(Messages.DataGridEditor_10, new CheckboxCellEditor(viewer.getTree()));
		final Property colorfield = new Property(Messages.DataGridEditor_11, fieldCbo);

		valueCol.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				ComponentDataGrid c = (ComponentDataGrid) getComponentDefinition();
				if (c == null) {
					return "";} //$NON-NLS-1$
				DataGridLayout l = c.getLayout();
				try {
					if (element == usecolor) {
						return l.isUseColor() + "";} //$NON-NLS-1$
					else if (element == colorfield) {
						return fieldCbo.getViewer().getCombo().getItem(l.getColorFieldIndex() - 1) + "";} //$NON-NLS-1$
				} catch (Exception ex) {
				}

				return ""; //$NON-NLS-1$
			}
		});

		valueCol.setEditingSupport(new EditingSupport(layoutTree) {

			@Override
			protected void setValue(Object element, Object value) {
				ComponentDataGrid c = (ComponentDataGrid) getComponentDefinition();
				if (c == null) {
					return;
				}
				DataGridLayout l = c.getLayout();
				if (element == usecolor) {
					l.setUseColors((Boolean) value);
				}
				else if (element == colorfield) {
					l.setColorFieldIndex(((List) fieldCbo.getViewer().getInput()).indexOf(value));
				}
				else {
					// color
				}

				notifyChangeOccured();
				layoutTree.refresh();

			}

			@Override
			protected Object getValue(Object element) {
				ComponentDataGrid c = (ComponentDataGrid) getComponentDefinition();
				if (c == null) {
					return null;
				}
				DataGridLayout l = c.getLayout();
				if (element == usecolor) {
					return l.isUseColor();
				}
				else if (element == colorfield) {
					return ((List) fieldCbo.getViewer().getInput()).get(l.getColorFieldIndex());
				}
				else {
					// color
				}
				return null;
			}

			@Override
			protected CellEditor getCellEditor(Object element) {
				return ((Property) element).getCellEditor();
			}

			@Override
			protected boolean canEdit(Object element) {
				return ((Property) element).getCellEditor() != null;
			}
		});
	}

	protected void createLayout(ExpandBar parent) {
		layoutTree = createViewer(parent);
		TreeViewerColumn valueCol = createValueViewerColum(layoutTree);

		ExpandItem item = new ExpandItem(parent, SWT.NONE);
		item.setControl(layoutTree.getTree());
		item.setText(Messages.DataGridEditor_16);

		layoutTypeCbo = new ComboBoxViewerCellEditor(layoutTree.getTree(), SWT.READ_ONLY);
		layoutTypeCbo.setContenProvider(new ArrayContentProvider());
		layoutTypeCbo.setLabelProvider(new LabelProvider());
		layoutTypeCbo.setInput(DataGridCellType.values());

		final Property headers = new Property(Messages.DataGridEditor_17, new CheckboxCellEditor(layoutTree.getTree()));
		final Property totals = new Property(Messages.DataGridEditor_18, new CheckboxCellEditor(layoutTree.getTree()));
		final Property insert = new Property(Messages.DataGridEditor_19, new CheckboxCellEditor(layoutTree.getTree()));

		columnsLayout = new PropertyGroup(Messages.DataGridEditor_20);
		valueCol.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				ComponentDataGrid c = (ComponentDataGrid) getComponentDefinition();
				if (c == null) {
					return "";} //$NON-NLS-1$
				DataGridOptions opts = (DataGridOptions) c.getOptions(DataGridOptions.class);
				if (element instanceof PropertyGridLayout) {
					return ((PropertyGridLayout) element).getPropertyValueString();
				}
				if (element == headers) {
					return opts.isHeadersVisible() + "";} //$NON-NLS-1$
				if (element == totals) {
					return opts.isIncludeTotal() + "";} //$NON-NLS-1$
				if (element == insert) {
					return opts.isRowsCanBeAdded() + "";} //$NON-NLS-1$

				return ""; //$NON-NLS-1$
			}
		});

		valueCol.setEditingSupport(new EditingSupport(layoutTree) {

			@Override
			protected void setValue(Object element, Object value) {
				ComponentDataGrid c = (ComponentDataGrid) getComponentDefinition();
				if (c == null) {
					return;
				}
				DataGridOptions opts = (DataGridOptions) c.getOptions(DataGridOptions.class);
				if (element == headers) {
					opts.setHeadersVisible((Boolean) value);
				}
				if (element == totals) {
					opts.setIncludeTotal((Boolean) value);
				}
				if (element == insert) {
					opts.setRowsCanBeAdded((Boolean) value);
				}
				if (element instanceof PropertyGridLayout) {
					((PropertyGridLayout) element).setPropertyValue(value);
				}
				notifyChangeOccured();
				layoutTree.refresh();

			}

			@Override
			protected Object getValue(Object element) {
				ComponentDataGrid c = (ComponentDataGrid) getComponentDefinition();
				if (c == null) {
					return null;
				}
				DataGridOptions opts = (DataGridOptions) c.getOptions(DataGridOptions.class);
				if (element == headers) {
					return opts.isHeadersVisible();
				}
				if (element == totals) {
					return opts.isIncludeTotal();
				}
				if (element == insert) {
					opts.isRowsCanBeAdded();
				}
				if (element instanceof PropertyGridLayout) {
					return ((PropertyGridLayout) element).getPropertyValue();
				}
				return null;
			}

			@Override
			protected CellEditor getCellEditor(Object element) {
				return ((Property) element).getCellEditor();
			}

			@Override
			protected boolean canEdit(Object element) {
				return ((Property) element).getCellEditor() != null;
			}
		});

		List input = new ArrayList();
		input.add(headers);
		input.add(totals);
		input.add(insert);
		input.add(columnsLayout);
		layoutTree.setInput(input);
	}

	protected void createDrillDown(ExpandBar parent) {
		final TreeViewer viewer = createViewer(parent);
		TreeViewerColumn valueCol = createValueViewerColum(viewer);

		ExpandItem item = new ExpandItem(parent, SWT.NONE);
		item.setControl(viewer.getTree());
		item.setText(Messages.DataGridEditor_26);

		fieldCbo2 = new ComboBoxViewerCellEditor(viewer.getTree(), SWT.READ_ONLY);
		fieldCbo2.setContenProvider(new ArrayContentProvider());
		fieldCbo2.setLabelProvider(new DatasLabelProvider());

		modelPageCbo = new ComboBoxViewerCellEditor(viewer.getTree(), SWT.READ_ONLY);
		modelPageCbo.setContenProvider(new ArrayContentProvider());
		modelPageCbo.setLabelProvider(new DatasLabelProvider());

		ComboBoxViewerCellEditor typeCbo = new ComboBoxViewerCellEditor(viewer.getTree(), SWT.READ_ONLY);
		typeCbo.setContenProvider(new ArrayContentProvider());
		typeCbo.setLabelProvider(new LabelProvider());
		typeCbo.setInput(DataGridDrill.DrillTarget.values());

		final PropertyGroup drillType = new PropertyGroup(Messages.DataGridEditor_27, typeCbo);
		final PropertyGroup drillField = new PropertyGroup(Messages.DataGridEditor_28, fieldCbo2);
		final PropertyGroup targetPage = new PropertyGroup(Messages.DataGridEditor_29, modelPageCbo);
		final PropertyGroup popupsize = new PropertyGroup(Messages.DataGridEditor_30);
		final Property width = new Property(Messages.DataGridEditor_31, new TextCellEditor(viewer.getTree()));
		final Property height = new Property(Messages.DataGridEditor_32, new TextCellEditor(viewer.getTree()));
		popupsize.add(width);
		popupsize.add(height);

		valueCol.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				ComponentDataGrid c = (ComponentDataGrid) getComponentDefinition();
				if (c == null) {
					return "";} //$NON-NLS-1$
				DataGridDrill drill = c.getDrillInfo();
				try {
					if (element == drillField) {
						return (((ColumnDescriptor) ((List) fieldCbo2.getViewer().getInput()).get(drill.getDrillableFieldIndex())).getLabel());
					}
				} catch (Exception ex) {
				}
				if (element == drillType) {
					return drill.getType().name();
				}
				if (element == width) {
					return drill.getPopupWidth() + "";} //$NON-NLS-1$
				if (element == height) {
					return drill.getPopupHeight() + "";} //$NON-NLS-1$
				if (element == targetPage && drill.getModelPage() != null) {
					return drill.getModelPageName() + "";} //$NON-NLS-1$

				return ""; //$NON-NLS-1$
			}
		});
		valueCol.setEditingSupport(new EditingSupport(viewer) {

			@Override
			protected void setValue(Object element, Object value) {
				ComponentDataGrid c = (ComponentDataGrid) getComponentDefinition();
				if (c == null) {
					return;
				}
				DataGridDrill drill = c.getDrillInfo();
				try {
					if (element == width) {
						drill.setPopupWidth(Integer.valueOf((String) value));
					}
					if (element == height) {
						drill.setPopupHeight(Integer.valueOf((String) value));
					}
					if (element == drillType) {
						drill.setType((DrillTarget) value);
					}
					if (element == drillField) {
						drill.setDrillableFieldIndex(((List) fieldCbo2.getViewer().getInput()).indexOf(value));
					}
				} catch (Exception ex) {
				}

				if (element == targetPage) {
					drill.setModelPage((FdModel) value);
				}

				notifyChangeOccured();
				viewer.refresh();

			}

			@Override
			protected Object getValue(Object element) {
				ComponentDataGrid c = (ComponentDataGrid) getComponentDefinition();
				if (c == null) {
					return null;
				}
				DataGridDrill drill = c.getDrillInfo();
				if (element == width) {
					return drill.getPopupWidth() + "";} //$NON-NLS-1$
				if (element == height) {
					return drill.getPopupHeight() + "";} //$NON-NLS-1$
				if (element == targetPage) {
					return drill.getModelPage();
				}
				if (element == drillType) {
					return drill.getType();
				}
				;
				try {
					if (element == drillField) {
						return ((List) fieldCbo2.getViewer().getInput()).get(drill.getDrillableFieldIndex());
					}
				} catch (Exception ex) {
				}
				return null;
			}

			@Override
			protected CellEditor getCellEditor(Object element) {
				return ((Property) element).getCellEditor();
			}

			@Override
			protected boolean canEdit(Object element) {
				if (getComponentDefinition() == null) {
					return false;
				}
				if (element == targetPage && !(Activator.getDefault().getProject() instanceof MultiPageFdProject))
					return false;
				return ((Property) element).getCellEditor() != null;
			}
		});

		List input = new ArrayList();
		input.add(drillType);
		input.add(drillField);
		input.add(targetPage);
		input.add(popupsize);
		viewer.setInput(input);
	}

	public void setInput(EditPart editPart, ComponentConfig config, IComponentDefinition component) {

		dataSourceCbo.setInput(component.getDictionary().getDatasources());
		dataSetCbo.setInput(component.getDictionary().getDatasets());
		if (component.getDatas().getDataSet() != null) {
			dataSourceCbo.setValue(component.getDictionary().getDatasource(component.getDatas().getDataSet().getDataSourceName()));
			fieldCbo.setInput(component.getDatas().getDataSet().getDataSetDescriptor().getColumnsDescriptors());
			fieldCbo2.setInput(component.getDatas().getDataSet().getDataSetDescriptor().getColumnsDescriptors());
		}
		else {
			dataSetCbo.setInput(Collections.EMPTY_LIST);
		}

		if (modelPageCbo != null) {
			if (Activator.getDefault().getProject() instanceof MultiPageFdProject) {
				modelPageCbo.setInput(((MultiPageFdProject) Activator.getDefault().getProject()).getPagesModels());
			}
			else {
				modelPageCbo.setInput(Collections.EMPTY_LIST);
			}
		}

		super.setInput(editPart, config, component);
		refreshColumnLayout();
		// viewer.expandAll();

	}

	private void refreshColumnLayout() {
		columnsLayout.clear();
		ComponentDataGrid c = (ComponentDataGrid) getComponentDefinition();
		if (c == null) {
			return;
		}
		
		List<ColumnDescriptor> columns = getComponentDefinition().getDatas().getDataSet().getDataSetDescriptor().getColumnsDescriptors();
		
		if (c.getLayout().getColumnOptions() == null || c.getLayout().getColumnOptions().isEmpty() && columns != null) {
			List<DataGridColumnOption> columnOptions = new ArrayList<DataGridColumnOption>();
			for (ColumnDescriptor column : columns) {
				columnOptions.add(new DataGridColumnOption(column.getName(),  column.getColumnLabel(), DataGridCellType.Visible));
			}
			c.getLayout().setColumnOptions(columnOptions);
		}
		
		for (int i = 0; i < columns.size(); i++) {
			PropertyGroup g = new PropertyGroup(Messages.DataGridEditor_40 + columns.get(i).getColumnLabel());
			PropertyGridLayout p = new PropertyGridLayout(PropertyGridLayout.HEADER_LABEL, c.getLayout(), i, Messages.DataGridEditor_41, new TextCellEditor(layoutTree.getTree()));
			g.add(p);
			PropertyGridLayout t = new PropertyGridLayout(PropertyGridLayout.HEADER_TYPE, c.getLayout(), i, Messages.DataGridEditor_42, layoutTypeCbo);
			g.add(t);

			columnsLayout.add(g);
		}
		layoutTree.refresh();
	}
}