package bpm.fd.design.ui.properties.model.chart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;

import bpm.fd.api.core.model.components.definition.ComponentConfig;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.components.definition.chart.ChartData;
import bpm.fd.api.core.model.components.definition.chart.DataAggregation;
import bpm.fd.api.core.model.components.definition.chart.DatasLimit;
import bpm.fd.api.core.model.components.definition.chart.IChartData;
import bpm.fd.api.core.model.components.definition.chart.MultiSerieChartData;
import bpm.fd.api.core.model.components.definition.chart.ChartOrderingType;
import bpm.fd.api.core.model.datas.DataSet;
import bpm.fd.api.core.model.datas.DataSource;
import bpm.fd.api.core.model.resources.Palette;
import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.properties.i18n.Messages;
import bpm.fd.design.ui.properties.model.Property;
import bpm.fd.design.ui.properties.model.PropertyGroup;
import bpm.fd.design.ui.properties.viewer.PropertiesContentProvider;
import bpm.fd.design.ui.viewer.labelprovider.DatasLabelProvider;

public class DatasChartEditor extends ChartEditor {
	// private static final String noLevel = "--- NONE ---";
	private class ContentProvider extends PropertiesContentProvider {

		@Override
		public Object[] getChildren(Object parentElement) {
			if(def == null) {
				return null;
			}
			if(parentElement == series) {
				List l = new ArrayList();
				for(DataAggregation agg : ((IChartData) def.getDatas()).getAggregation()) {
					l.add(new SerieProperty(def.getNature().isMultiSerie(), agg, (IChartData) def.getDatas(), fieldCbo));
				}

				return l.toArray(new Object[l.size()]);
			}
			return super.getChildren(parentElement);
		}

		@Override
		public boolean hasChildren(Object element) {
			if(def == null) {
				return false;
			}
			if(element == series) {
				IChartData dt = (IChartData) def.getDatas();
				if(dt == null || dt.getAggregation() == null) {
					return false;
				}
				return ((IChartData) def.getDatas()).getAggregation().size() > 0;
			}
			return super.hasChildren(element);
		}
	}

	private ComboBoxViewerCellEditor dataSourceCbo;
	private ComboBoxViewerCellEditor dataSetCbo;
	private ComboBoxViewerCellEditor fieldCbo;
	private ComboBoxViewerCellEditor fieldCbo2;
	private ComboBoxViewerCellEditor paletteEditor;

	private PropertyGroup series;
	private TreeViewer viewer;

	private PropertyGroup dimension;

	public DatasChartEditor(Composite parent) {
		super(parent);
	}

	protected void fillBar() {
		createStyle(getControl());
		createChart(getControl());
		createColors(getControl());
//		createParameters();
		createDrillDown(getControl());
		createDimension(getControl());
		createDatas(getControl());
		createNumberFormat(getControl());
	}

	public void setInput(EditPart editPart, ComponentConfig config, IComponentDefinition component) {
		//Trick to remove the images from the colorEditor
		try {
			for(CellEditor editor : PropertyColor.colorPickers) {
				editor.dispose();
			}
			PropertyColor.colorPickers.clear();
//			for(Property p : ((SerieProperty)series).getColorProperties()) {
//				PropertyColor color = (PropertyColor) p;
//				color.getCellEditor().dispose();
//			}
		} catch(Exception e) {

		}
		
		dimension.clear();

		for(int i = 0; i < ((IChartData) component.getDatas()).getLevelCategoriesIndex().size(); i++) {
			dimension.add(new Property(Messages.DatasChartEditor_0 + (i + 1), fieldCbo2));
		}

		super.setInput(editPart, config, component);
		dataSourceCbo.setInput(component.getDictionary().getDatasources());
		dataSetCbo.setInput(component.getDictionary().getDatasets());

		if(component.getDatas() != null && component.getDatas().getDataSet() != null) {
			dataSourceCbo.setValue(component.getDictionary().getDatasource(component.getDatas().getDataSet().getDataSourceName()));
			fieldCbo.setInput(component.getDatas().getDataSet().getDataSetDescriptor().getColumnsDescriptors());
			fieldCbo2.setInput(component.getDatas().getDataSet().getDataSetDescriptor().getColumnsDescriptors());
		}
		else {
			dataSetCbo.setInput(Collections.EMPTY_LIST);
		}

		List palettes = new ArrayList();
		palettes.add(Messages.DatasChartEditor_1);
		palettes.addAll(Activator.getDefault().getProject().getDictionary().getPalettes());
		paletteEditor.setInput(palettes);

		// viewer.expandAll();

	}

	private void createDimension(ExpandBar parent) {
		final TreeViewer viewer = createViewer(parent);
		viewer.setContentProvider(new ContentProvider());
		TreeViewerColumn colValue = createValueViewerColum(viewer);

		final ExpandItem item = new ExpandItem(parent, SWT.NONE);
		item.setControl(viewer.getTree());
		item.setText(Messages.DatasChartEditor_2);

		fieldCbo2 = new ComboBoxViewerCellEditor(viewer.getTree(), SWT.READ_ONLY);
		fieldCbo2.setContenProvider(new ArrayContentProvider());
		fieldCbo2.setLabelProvider(new DatasLabelProvider());

		dimension = new PropertyGroup(Messages.DatasChartEditor_3, new DialogCellEditor(viewer.getTree()) {

			@Override
			protected Object openDialogBox(Control cellEditorWindow) {
				Property p = new Property(Messages.DatasChartEditor_4 + (dimension.getProperties().size() + 1), fieldCbo2);
				dimension.add(p);
				((IChartData) def.getDatas()).addLevel();
				viewer.expandAll();
				viewer.refresh();
				resizeAllItems();
				return null;
			}
		});

		colValue.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if(def == null) {
					return "";}; //$NON-NLS-1$
				if(element == dimension) {
					return "";} //$NON-NLS-1$
				int pos = dimension.getProperties().indexOf(element);
				if(pos == -1) {
					return ""; //$NON-NLS-1$
				}
				Integer index = ((IChartData) def.getDatas()).getLevelCategoriesIndex().get(pos);
				if(index == null) {
					return ""; //$NON-NLS-1$
				}
				return def.getDatas().getDataSet().getDataSetDescriptor().getColumnsDescriptors().get(index).getColumnLabel();
			}

		});

		colValue.setEditingSupport(new EditingSupport(viewer) {

			@Override
			protected void setValue(Object element, Object value) {
				if(def == null) {
					return;
				}
				int pos = dimension.getProperties().indexOf(element);
				if(pos == -1) {
					return;
				}
				Integer index = def.getDatas().getDataSet().getDataSetDescriptor().getColumnsDescriptors().indexOf(value);
				if(index == -1) {
					((IChartData) def.getDatas()).setLevelIndex(pos, null);
				}
				else {
					((IChartData) def.getDatas()).setLevelIndex(pos, index);
				}

				notifyChangeOccured();
				viewer.refresh();
				viewer.expandAll();
				resizeItem(item);
			}

			@Override
			protected Object getValue(Object element) {
				if(def == null) {
					return null;
				}
				try {
					int pos = dimension.getProperties().indexOf(element);
					Integer index = ((IChartData) def.getDatas()).getLevelCategoriesIndex().get(pos);
					if(index == null) {
						return null;
					}
					return def.getDatas().getDataSet().getDataSetDescriptor().getColumnsDescriptors().get(index);

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

		MenuManager mgr = new MenuManager();
		final Action removeLevel = new Action(Messages.DatasChartEditor_9) {
			public void run() {
				Property p = (Property) ((IStructuredSelection) viewer.getSelection()).getFirstElement();
				int index = ((PropertyGroup) p.getParent()).getProperties().indexOf(p);
				((IChartData) def.getDatas()).removeLevel(index);
				notifyChangeOccured();
				// refresh the viewer's model
				dimension.clear();
				for(int i = 0; i < ((IChartData) def.getDatas()).getLevelCategoriesIndex().size(); i++) {
					dimension.add(new Property(Messages.DatasChartEditor_10 + (i + 1), fieldCbo2));
				}
				viewer.refresh();
				viewer.expandAll();
				resizeItem(item);
			}
		};
		mgr.add(removeLevel);
		mgr.addMenuListener(new IMenuListener() {

			@Override
			public void menuAboutToShow(IMenuManager manager) {
				IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();
				if(ss.isEmpty() || ss.getFirstElement() instanceof PropertyGroup) {
					removeLevel.setEnabled(false);
				}
				else {
					removeLevel.setEnabled(true);
				}

			}
		});

		viewer.getTree().setMenu(mgr.createContextMenu(viewer.getControl()));
		List input = new ArrayList();
		input.add(dimension);
		viewer.setInput(input);
	}

	private void createDatas(ExpandBar parent) {
		viewer = createViewer(parent);
		viewer.setContentProvider(new ContentProvider());
		TreeViewerColumn colValue = createValueViewerColum(viewer);

		final ExpandItem item = new ExpandItem(parent, SWT.NONE);
		item.setControl(viewer.getTree());
		item.setText(Messages.DatasChartEditor_11);

		dataSourceCbo = new ComboBoxViewerCellEditor(viewer.getTree());
		dataSourceCbo.setContenProvider(new ArrayContentProvider());
		dataSourceCbo.setLabelProvider(new DatasLabelProvider());

		dataSetCbo = new ComboBoxViewerCellEditor(viewer.getTree());
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

		paletteEditor = new ComboBoxViewerCellEditor(viewer.getTree(), SWT.READ_ONLY);
		paletteEditor.setContenProvider(new ArrayContentProvider());
		paletteEditor.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if(element instanceof Palette) {
					return ((Palette) element).getName();
				}
				return super.getText(element);
			}
		});

		final Property datasource = new Property(Messages.DatasChartEditor_12, dataSourceCbo);
		final Property dataset = new Property(Messages.DatasChartEditor_13, dataSetCbo);
		final Property categoryValue = new Property(Messages.DatasChartEditor_14, fieldCbo);
		final Property categoryLabel = new Property(Messages.DatasChartEditor_15, fieldCbo);

		ComboBoxViewerCellEditor orderTypeCbo = new ComboBoxViewerCellEditor(viewer.getTree(), SWT.READ_ONLY);
		orderTypeCbo.setContenProvider(new ArrayContentProvider());
		orderTypeCbo.setLabelProvider(new LabelProvider());
		orderTypeCbo.setInput(ChartOrderingType.values());

		final Property categoryOrdinal = new Property(Messages.DatasChartEditor_16, orderTypeCbo);

		final Property subcategory = new Property(Messages.DatasChartEditor_17, fieldCbo);
		final PropertyGroup limit = new PropertyGroup(Messages.DatasChartEditor_18, new ComboBoxCellEditor(viewer.getTree(), DatasLimit.LIMIT_TYPES, SWT.READ_ONLY));
		final Property limitSize = new Property("N", new TextCellEditor(viewer.getTree())); //$NON-NLS-1$
		final Property colorPalette = new Property(Messages.DatasChartEditor_20, paletteEditor);
		limit.add(limitSize);

		series = new PropertyGroup(Messages.DatasChartEditor_21);

		colValue.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if(def == null) {
					return "";}; //$NON-NLS-1$
				if(element instanceof Property && ((Property) element).getParent() instanceof SerieProperty) {
					return ((SerieProperty) ((Property) element).getParent()).getPropertyValueString(element);
				}
				if(element instanceof FilterProperty) {
					return ((FilterProperty) element).getValue();
				}

				if(element == datasource) {
					return dataSourceCbo.getViewer().getCCombo().getText();
				}
				if(element == colorPalette) {
					return ((IChartData) def.getDatas()).getColorPalette() != null ? ((IChartData) def.getDatas()).getColorPalette().getName() : Messages.DatasChartEditor_23;
				}

				try {
					if(element == dataset) {
						if(getComponentDefinition().getDatas().getDataSet() == null) {
							return "";}return getComponentDefinition().getDatas().getDataSet().getName();} //$NON-NLS-1$
					if(element == categoryValue) {
						return def.getDatas().getDataSet().getDataSetDescriptor().getColumnsDescriptors().get(((IChartData) def.getDatas()).getCategorieFieldIndex() - 1).getColumnLabel();
					}
					if(element == categoryLabel) {
						return def.getDatas().getDataSet().getDataSetDescriptor().getColumnsDescriptors().get(((IChartData) def.getDatas()).getCategorieFieldLabelIndex() - 1).getColumnLabel();
					}
					if(element == categoryOrdinal) {
						return ((IChartData) def.getDatas()).getOrderType().name();
					}
					if(element == subcategory && def.getDatas() instanceof MultiSerieChartData) {
						return def.getDatas().getDataSet().getDataSetDescriptor().getColumnsDescriptors().get(((MultiSerieChartData) def.getDatas()).getSerieFieldIndex() - 1).getColumnLabel();
					}
					if(element == limit) {
						return DatasLimit.LIMIT_TYPES[((IChartData) def.getDatas()).getLimit().getType()];
					}
					if(element == limitSize) {
						return ((IChartData) def.getDatas()).getLimit().getSize() + "";} //$NON-NLS-1$
				} catch(Exception ex) {

				}

				return ""; //$NON-NLS-1$
			}

			@Override
			public Color getBackground(Object element) {
				if(element instanceof PropertyColor) {
					return getColor(((PropertyColor) element).getColor());
				}
				return super.getBackground(element);
			}
		});

		colValue.setEditingSupport(new EditingSupport(viewer) {

			@Override
			protected void setValue(Object element, Object value) {
				if(def == null) {
					return;
				}
				IChartData data = ((IChartData) def.getDatas());
				if(element == colorPalette) {
					if(value instanceof Palette) {
						data.setColorPalette((Palette) value);
					}
					else {
						data.setColorPalette(null);
					}
				}

				if(element == datasource) {
					dataSetCbo.setInput(getComponentDefinition().getDictionary().getDatasets());
				}

				if(element instanceof Property && ((Property) element).getParent() instanceof SerieProperty) {
					((SerieProperty) ((Property) element).getParent()).setPropertyValue(element, value);
				}
				if(element instanceof PropertyColor) {
					((PropertyColor) element).setColor((RGB) value);
				}

				try {
					if(element == dataset) {
						data.setDataSet((DataSet) value);
						fieldCbo.setInput(((DataSet) value).getDataSetDescriptor().getColumnsDescriptors());
						refreshParameters();
					}
					if(element == categoryValue) {
						try {
							((ChartData) def.getDatas()).setGroupFieldIndex(((ComboBoxViewerCellEditor) getCellEditor(element)).getViewer().getCCombo().getSelectionIndex() + 1);
						} catch(Exception e) {
							((MultiSerieChartData) def.getDatas()).setCategorieFieldIndex(((ComboBoxViewerCellEditor) getCellEditor(element)).getViewer().getCCombo().getSelectionIndex() + 1);
						}
					}
					if(element == categoryLabel) {
						try {
							((ChartData) def.getDatas()).setCategorieFieldLabelIndex(((ComboBoxViewerCellEditor) getCellEditor(element)).getViewer().getCCombo().getSelectionIndex() + 1);
						} catch(Exception e) {
							((MultiSerieChartData) def.getDatas()).setCategorieFieldLabelIndex(((ComboBoxViewerCellEditor) getCellEditor(element)).getViewer().getCCombo().getSelectionIndex() + 1);
						}
					}
					if(element == categoryOrdinal) {
						try {
							((ChartData) def.getDatas()).setOrderType((ChartOrderingType) value);
						} catch(Exception e) {
							((MultiSerieChartData) def.getDatas()).setOrderType((ChartOrderingType) value);
						}
					}
					if(element == subcategory) {
						((MultiSerieChartData) def.getDatas()).setSerieFieldIndex(((ComboBoxViewerCellEditor) getCellEditor(element)).getViewer().getCCombo().getSelectionIndex() + 1);
					}
					if(element == limit) {
						((IChartData) def.getDatas()).getLimit().setType((Integer) value);
					}

					if(element == limitSize) {
						((IChartData) def.getDatas()).getLimit().setSize(Integer.parseInt((String) value));
					}
				} catch(Exception ex) {
					ex.printStackTrace();
				}
				notifyChangeOccured();
				viewer.refresh();
				viewer.expandAll();
				resizeItem(item);
			}

			@Override
			protected Object getValue(Object element) {
				if(def == null) {
					return null;
				}
				try {
					if(element instanceof Property && ((Property) element).getParent() instanceof SerieProperty) {
						return ((SerieProperty) ((Property) element).getParent()).getPropertyValue(element);
					}
					if(element == colorPalette) {
						return ((IChartData) def.getDatas()).getColorPalette();
					}
					if(element == dataset) {
						return def.getDatas().getDataSet();
					}
					if(element == datasource) {
						return def.getDictionary().getDatasource(def.getDatas().getDataSet().getDataSourceName());
					}
					if(element == categoryValue) {
						return ((List) fieldCbo.getViewer().getInput()).get(((ChartData) def.getDatas()).getCategorieFieldIndex() -1);
					}
					if(element == categoryLabel) {
						return ((List) fieldCbo.getViewer().getInput()).get(((ChartData) def.getDatas()).getCategorieFieldLabelIndex() -1);
					}
					if(element == categoryOrdinal) {
						return ((IChartData) def.getDatas()).getOrderType();
					}
					if(element == subcategory) {
						return ((List) fieldCbo.getViewer().getInput()).get(((MultiSerieChartData) def.getDatas()).getSerieFieldIndex() -1);
					}
					if(element == limit) {
						return ((IChartData) def.getDatas()).getLimit().getType();
					}
					if(element == limitSize) {
						return ((IChartData) def.getDatas()).getLimit().getSize() + "";} //$NON-NLS-1$
				} catch(Exception ex) {
					ex.printStackTrace();
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
		input.add(categoryValue);
		input.add(categoryLabel);
		input.add(subcategory);
		input.add(categoryOrdinal);
		input.add(limit);
		input.add(colorPalette);
		input.add(series);
		viewer.setInput(input);

		createMenu();
	}

	private void expandTree(Property p) {
		viewer.expandToLevel(p, AbstractTreeViewer.ALL_LEVELS);
		if(p instanceof PropertyGroup) {
			for(Property c : ((PropertyGroup) p).getProperties()) {
				expandTree(c);
			}
		}

	}

	private void createMenu() {
		MenuManager mgr = new MenuManager();

		final Action addSerie = new Action(Messages.DatasChartEditor_28) {
			public void run() {
				DataAggregation da = new DataAggregation();
				da.setMeasureName(Messages.DatasChartEditor_29);
				da.setValueFieldIndex(0);
				((MultiSerieChartData) def.getDatas()).addAggregation(da);
				notifyChangeOccured();
				resize();
			}
		};

		final Action deleteSerie = new Action(Messages.DatasChartEditor_30) {
			public void run() {
				IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();
				SerieProperty p = (SerieProperty) ss.getFirstElement();
				((MultiSerieChartData) def.getDatas()).removeAggregation(p.getDataAggeregation());
				notifyChangeOccured();
				resize();
				expandTree(p);
			}

		};

		final Action addColor = new Action(Messages.DatasChartEditor_31) {
			public void run() {
				IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();
				Property p = (Property) ss.getFirstElement();
				if(p instanceof SerieProperty) {
					((SerieProperty) p).getDataAggeregation().addColorCode("ffffff"); //$NON-NLS-1$
					((SerieProperty) p).refresh();
				}
				else {
					try {
						((SerieProperty) ((PropertyGroup) p).getParent()).getDataAggeregation().addColorCode("ffffff"); //$NON-NLS-1$
						((SerieProperty) ((PropertyGroup) p).getParent()).refresh();
					} catch(Exception e) {

					}
				}
				notifyChangeOccured();
				viewer.refresh();

				viewer.expandToLevel(4);

				// expandTree(p);
			}
		};

		final Action deleteColor = new Action(Messages.DatasChartEditor_33) {
			public void run() {
				IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();
				PropertyColor c = (PropertyColor) ss.getFirstElement();
				c.getDataAggregation().getColorsCode().remove(c.getIndex());
				notifyChangeOccured();
				resize();
			}
		};

		mgr.add(addSerie);
		mgr.add(deleteSerie);
		mgr.add(new Separator());
		mgr.add(addColor);
		mgr.add(deleteColor);
		mgr.addMenuListener(new IMenuListener() {

			@Override
			public void menuAboutToShow(IMenuManager manager) {
				if(def == null || !def.getNature().isMultiSerie()) {
					addSerie.setEnabled(false);
					deleteSerie.setEnabled(false);
					// return;
				}
				IStructuredSelection ss = (IStructuredSelection) viewer.getSelection();
				if(ss.isEmpty()) {
					addSerie.setEnabled(false);
					deleteSerie.setEnabled(false);
					addColor.setEnabled(false);
					deleteColor.setEnabled(false);
					return;
				}
				deleteSerie.setEnabled(ss.getFirstElement() instanceof SerieProperty);
				deleteColor.setEnabled(ss.getFirstElement() instanceof PropertyColor);
				addSerie.setEnabled(def != null && def.getNature().isMultiSerie());
				addColor.setEnabled(ss.getFirstElement() instanceof SerieProperty || ss.getFirstElement() instanceof PropertyGroup);
			}
		});

		viewer.getTree().setMenu(mgr.createContextMenu(viewer.getTree()));
	}
}
