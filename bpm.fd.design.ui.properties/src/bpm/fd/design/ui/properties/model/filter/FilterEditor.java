package bpm.fd.design.ui.properties.model.filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
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

import bpm.fd.api.core.model.components.definition.ComponentConfig;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.components.definition.OrderingType;
import bpm.fd.api.core.model.components.definition.filter.ComponentFilterDefinition;
import bpm.fd.api.core.model.components.definition.filter.FilterData;
import bpm.fd.api.core.model.components.definition.filter.FilterOptions;
import bpm.fd.api.core.model.components.definition.filter.FilterRenderer;
import bpm.fd.api.core.model.components.definition.filter.MenuOptions;
import bpm.fd.api.core.model.components.definition.filter.TypeRenderer;
import bpm.fd.api.core.model.datas.DataSet;
import bpm.fd.api.core.model.datas.DataSource;
import bpm.fd.design.ui.properties.i18n.Messages;
import bpm.fd.design.ui.properties.model.Property;
import bpm.fd.design.ui.properties.model.editors.ComponentEditor;
import bpm.fd.design.ui.properties.model.editors.IComponentEditor;
import bpm.fd.design.ui.properties.viewer.PropertiesContentProvider;
import bpm.fd.design.ui.viewer.labelprovider.DatasLabelProvider;

public class FilterEditor extends ComponentEditor implements IComponentEditor {
	private ComboBoxViewerCellEditor dataSourceCbo;
	private ComboBoxViewerCellEditor dataSetCbo;
	private ComboBoxViewerCellEditor fieldCbo;

	public FilterEditor(Composite parent) {
		super(parent);
		fillBar();
	}

	@Override
	protected void fillBar() {
		// createStyle(getControl());
		createFilter(getControl());
		createParameters();
		createDatas(getControl());
	}

	private void createDatas(ExpandBar parent) {
		final TreeViewer viewer = createViewer(parent);
		viewer.setContentProvider(new PropertiesContentProvider());
		TreeViewerColumn colValue = createValueViewerColum(viewer);

		ExpandItem item = new ExpandItem(parent, SWT.NONE);
		item.setControl(viewer.getTree());
		item.setText(Messages.FilterEditor_0);

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
				if(ss.isEmpty()) {
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

		final Property datasource = new Property(Messages.FilterEditor_1, dataSourceCbo);
		final Property dataset = new Property(Messages.FilterEditor_2, dataSetCbo);
		final Property categoryValue = new Property(Messages.FilterEditor_3, fieldCbo);
		final Property categoryLabel = new Property(Messages.FilterEditor_4, fieldCbo);
		final Property categoryOrdinal = new Property(Messages.FilterEditor_5, fieldCbo);
		
		ComboBoxViewerCellEditor orderTypeCbo = new ComboBoxViewerCellEditor(viewer.getTree(), SWT.READ_ONLY);
		orderTypeCbo.setContenProvider(new ArrayContentProvider());
		orderTypeCbo.setLabelProvider(new LabelProvider());
		orderTypeCbo.setInput(OrderingType.getOrderTypes());

		final Property orderType = new Property(Messages.DatasChartEditor_16, orderTypeCbo);

		colValue.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if(getComponentDefinition() == null) {
					return "";}; //$NON-NLS-1$

				// if (getComponentDefinition().getDatas().getDataSet() == null){return "";}
				if(element == datasource) {
					return dataSourceCbo.getViewer().getCCombo().getText();
				}

				if(element == dataset && getComponentDefinition().getDatas() != null) {
					if(getComponentDefinition().getDatas().getDataSet() == null) {
						return "";
					}
					return getComponentDefinition().getDatas().getDataSet().getName();
				} //$NON-NLS-1$

				try {
					if(element == categoryValue) {
						return getComponentDefinition().getDatas().getDataSet().getDataSetDescriptor().getColumnsDescriptors().get(((FilterData) getComponentDefinition().getDatas()).getColumnValueIndex() - 1).getColumnLabel();
					}
					if(element == categoryLabel) {
						return getComponentDefinition().getDatas().getDataSet().getDataSetDescriptor().getColumnsDescriptors().get(((FilterData) getComponentDefinition().getDatas()).getColumnLabelIndex() - 1).getColumnLabel();
					}
					if(element == categoryOrdinal) {
						return getComponentDefinition().getDatas().getDataSet().getDataSetDescriptor().getColumnsDescriptors().get(((FilterData) getComponentDefinition().getDatas()).getColumnOrderIndex() - 1).getColumnLabel();
					}
					if(element == orderType) {
						return ((FilterData)getComponentDefinition().getDatas()).getOrderType().name();
					}

				} catch(Exception ex) {

				}
				return ""; //$NON-NLS-1$
			}

		});

		colValue.setEditingSupport(new EditingSupport(viewer) {

			@Override
			protected void setValue(Object element, Object value) {
				if(getComponentDefinition() == null) {
					return;
				}
				FilterData data = (FilterData) getComponentDefinition().getDatas();
				if(element == dataset) {
					data.setDataSet((DataSet) value);
					fieldCbo.setInput(((DataSet) value).getDataSetDescriptor().getColumnsDescriptors());
					refreshParameters();
				}
				if(element == datasource) {
					dataSetCbo.setInput(getComponentDefinition().getDictionary().getDatasets());
				}

				if(element == categoryValue) {
					data.setColumnValueIndex(((ComboBoxViewerCellEditor) getCellEditor(element)).getViewer().getCCombo().getSelectionIndex() + 1);
				}
				if(element == categoryLabel) {
					data.setColumnLabelIndex(((ComboBoxViewerCellEditor) getCellEditor(element)).getViewer().getCCombo().getSelectionIndex() + 1);
				}
				if(element == categoryOrdinal) {
					data.setColumnOrderIndex(((ComboBoxViewerCellEditor) getCellEditor(element)).getViewer().getCCombo().getSelectionIndex() + 1);
				}
				if(element == orderType) {
					data.setOrderType((OrderingType) value);
				}
				resize();
				viewer.refresh();
				notifyChangeOccured();
			}

			@Override
			protected Object getValue(Object element) {
				if(getComponentDefinition() == null) {
					return null;
				}
				try {
					FilterData data = (FilterData) getComponentDefinition().getDatas();
					if(element == dataset) {
						return data.getDataSet();
					}
					if(element == datasource) {
						return ((IStructuredSelection) dataSourceCbo.getViewer().getSelection()).getFirstElement();
					}
					if(element == categoryValue) {
						return ((List) fieldCbo.getViewer().getInput()).get(data.getColumnValueIndex() - 1);
					}
					if(element == categoryLabel) {
						return ((List) fieldCbo.getViewer().getInput()).get(data.getColumnLabelIndex() - 1);
					}
					if(element == categoryOrdinal) {
						return ((List) fieldCbo.getViewer().getInput()).get(data.getColumnOrderIndex() - 1);
					}
					if(element == orderType) {
						return data.getOrderType();
					}
				} catch(Exception ex) {}

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
		input.add(categoryValue);
		input.add(categoryLabel);
		input.add(categoryOrdinal);
		input.add(orderType);
		viewer.setInput(input);
	}

	private void createFilter(ExpandBar parent) {
		final TreeViewer viewer = createViewer(parent);
		viewer.setContentProvider(new PropertiesContentProvider());
		TreeViewerColumn colValue = createValueViewerColum(viewer);

		ExpandItem item = new ExpandItem(parent, SWT.NONE);
		item.setControl(viewer.getTree());
		item.setText(Messages.FilterEditor_9);

		final Property renderer = new Property(Messages.FilterEditor_10, new ComboBoxCellEditor(viewer.getTree(), FilterRenderer.RENDERER_NAMES));
		final Property hidden = new Property(Messages.FilterEditor_11, new CheckboxCellEditor(viewer.getTree()));
		final Property selectFirstValue = new Property(Messages.FilterEditor_12, new CheckboxCellEditor(viewer.getTree()));
		final Property submit = new Property(Messages.FilterEditor_13, new CheckboxCellEditor(viewer.getTree()));
		final Property size = new Property(Messages.FilterEditor_14, new TextCellEditor(viewer.getTree()));
		final Property defaultValue = new Property(Messages.FilterEditor_15, new TextCellEditor(viewer.getTree()));
		final Property label = new Property(Messages.FilterEditor_16, new TextCellEditor(viewer.getTree()));
		final Property required = new Property(Messages.FilterEditor_17, new CheckboxCellEditor(viewer.getTree()));
		final Property type = new Property("type", new ComboBoxCellEditor(viewer.getTree(), TypeRenderer.RENDERER_NAMES));
		final Property orientation = new Property("IsVertical", new CheckboxCellEditor(viewer.getTree()));

		colValue.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if(getComponentDefinition() == null) {
					return "";
				}; //$NON-NLS-1$
				ComponentFilterDefinition f = (ComponentFilterDefinition) getComponentDefinition();
				FilterOptions opts = (FilterOptions) f.getOptions(FilterOptions.class);
				if(element == renderer) {
					return FilterRenderer.RENDERER_NAMES[f.getRenderer().getRendererStyle()];
				}
				if(element == hidden) {
					return opts.isHidden() + "";
				} //$NON-NLS-1$
				if(element == label) {
					return f.getLabel() == null ? "" : f.getLabel();
				} //$NON-NLS-1$
				if(element == selectFirstValue) {
					return opts.isSelectFirstValue() + "";
				} //$NON-NLS-1$
				if(element == submit) {
					return opts.isSubmitOnChange() + "";
				} //$NON-NLS-1$
				if(element == required) {
					return opts.isRequired() + "";
				}
				if(f.getRenderer().getRendererStyle() == FilterRenderer.MENU && element == size) {
					return ((MenuOptions) f.getOptions(MenuOptions.class)).getSize() + "";
				} //$NON-NLS-1$
				if(f.getRenderer().getRendererStyle() == FilterRenderer.MENU && element == orientation) {
					return ((MenuOptions) f.getOptions(MenuOptions.class)).getIsVertical() + "";
				} //$NON-NLS-1$

				return ""; //$NON-NLS-1$
			}

		});

		colValue.setEditingSupport(new EditingSupport(viewer) {

			@Override
			protected void setValue(Object element, Object value) {
				ComponentFilterDefinition f = (ComponentFilterDefinition) getComponentDefinition();
				FilterOptions opts = (FilterOptions) f.getOptions(FilterOptions.class);
				if(f == null) {
					return;
				}
				try {
					if(element == renderer) {
						f.setRenderer(FilterRenderer.getRenderer((Integer) value));
					}
				} catch(Exception ex) {}
				if(element == label) {
					f.setLabel((String) value);
				}
				if(element == hidden) {
					opts.setHidden((Boolean) value);
				}
				if(element == required) {
					opts.setRequired((Boolean) value);
				}
				if(element == selectFirstValue) {
					opts.setSelectFirstValue((Boolean) value);
				}
				if(element == submit) {
					opts.setSubmitOnChange((Boolean) value);
				}
				try {
					if(element == size) {
						((MenuOptions) f.getOptions(MenuOptions.class)).setSize(Integer.parseInt((String) value));
					}
					if(element == orientation) {
						((MenuOptions) f.getOptions(MenuOptions.class)).setIsVertical((Boolean) value);
					}
				} catch(Exception ex) {
					
				}
				if(element == defaultValue) {
					opts.setDefaultValue((String) value);
				}
				viewer.refresh();
				notifyChangeOccured();
			}

			@Override
			protected Object getValue(Object element) {
				ComponentFilterDefinition f = (ComponentFilterDefinition) getComponentDefinition();
				FilterOptions opts = (FilterOptions) f.getOptions(FilterOptions.class);
				if(f == null) {
					return null;
				}
				if(element == renderer) {
					return f.getRenderer().getRendererStyle();
				}
				if(element == hidden) {
					return opts.isHidden();
				}
				if(element == required) {
					return opts.isRequired();
				}
				if(element == selectFirstValue) {
					return opts.isSelectFirstValue();
				}
				if(element == submit) {
					return opts.isSubmitOnChange();
				}
				if(element == label) {
					return f.getLabel() == null ? "" : f.getLabel();} //$NON-NLS-1$
				try {
					if(element == size) {
						return ((MenuOptions) f.getOptions(MenuOptions.class)).getSize() + "";
					} //$NON-NLS-1$
					if(element == orientation) {
						return ((MenuOptions) f.getOptions(MenuOptions.class)).getIsVertical();
					} //$NON-NLS-1$
				} catch(Exception ex) {

				}
				if(element == defaultValue) {
					return opts.getDefaultValue();
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
		input.add(label);
		input.add(renderer);
		input.add(hidden);
		input.add(selectFirstValue);
		input.add(required);
		input.add(submit);
		input.add(size);
		input.add(orientation);
		input.add(defaultValue);
		viewer.setInput(input);
	}

	public void setInput(EditPart editPart, ComponentConfig conf, IComponentDefinition component) {

		dataSourceCbo.setInput(component.getDictionary().getDatasources());
		dataSetCbo.setInput(component.getDictionary().getDatasets());

		if(component.getDatas() != null && component.getDatas().getDataSet() != null) {

			dataSourceCbo.setValue(component.getDictionary().getDatasource(component.getDatas().getDataSet().getDataSourceName()));
			fieldCbo.setInput(component.getDatas().getDataSet().getDataSetDescriptor().getColumnsDescriptors());
		}
		else {
			dataSetCbo.setInput(Collections.EMPTY_LIST);
		}
		super.setInput(editPart, conf, component);
		// viewer.expandAll();

	}
}
