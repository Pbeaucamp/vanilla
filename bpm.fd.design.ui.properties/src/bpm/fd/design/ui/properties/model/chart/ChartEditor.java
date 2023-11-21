package bpm.fd.design.ui.properties.model.chart;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColorCellEditor;
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
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;

import bpm.fd.api.core.model.FdModel;
import bpm.fd.api.core.model.MultiPageFdProject;
import bpm.fd.api.core.model.components.definition.ComponentConfig;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.components.definition.chart.ChartNature;
import bpm.fd.api.core.model.components.definition.chart.ComponentChartDefinition;
import bpm.fd.api.core.model.components.definition.chart.DataAggregation;
import bpm.fd.api.core.model.components.definition.chart.IChartData;
import bpm.fd.api.core.model.components.definition.chart.MultiSerieChartData;
import bpm.fd.api.core.model.components.definition.chart.fusion.options.GenericNonPieOptions;
import bpm.fd.api.core.model.components.definition.chart.fusion.options.GenericOptions;
import bpm.fd.api.core.model.components.definition.chart.fusion.options.LineCombinationOption;
import bpm.fd.api.core.model.components.definition.chart.fusion.options.NumberFormatOptions;
import bpm.fd.api.core.model.components.definition.chart.fusion.options.PieGenericOptions;
import bpm.fd.api.core.model.components.definition.chart.fusion.options.TypeTarget;
import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.properties.i18n.Messages;
import bpm.fd.design.ui.properties.model.Property;
import bpm.fd.design.ui.properties.model.PropertyGroup;
import bpm.fd.design.ui.properties.model.editors.ComponentEditor;
import bpm.fd.design.ui.properties.model.editors.IComponentEditor;
import bpm.fd.design.ui.properties.model.map.PropertyMapColor;
import bpm.fd.design.ui.tools.ColorManager;
import bpm.fd.design.ui.viewer.labelprovider.DatasLabelProvider;
import bpm.vanilla.map.core.design.fusionmap.ColorRange;

public abstract class ChartEditor extends ComponentEditor implements IComponentEditor{
	protected ComponentChartDefinition def;
	
	private ComboBoxViewerCellEditor lvlCbo;
	private ComboBoxViewerCellEditor natureCbo;
	private ComboBoxViewerCellEditor lineSerieCbo;
	private ComboBoxViewerCellEditor modelPageCbo;
	
	private class ChartPropertyFilter extends ViewerFilter{
		private List<Property> lineCombiProps = new ArrayList<Property>();
		private List<Property> pieProps = new ArrayList<Property>();
		private List<Property> nonPieProps = new ArrayList<Property>();
		
		@Override
		public boolean select(Viewer viewer, Object parentElement,
				Object element) {
			if (def == null){
				return false;
			}
			if (def.getNature().getNature() == ChartNature.PIE || def.getNature().getNature() == ChartNature.PIE_3D){
				if (lineCombiProps.contains(element)){
					return false;
				}
				if (nonPieProps.contains(element)){
					return false;
				}
			}
			else if (def.getNature().isLineCombnation()){
				if (pieProps.contains(element)){
					return false;
				}
			}
			else{
				if (pieProps.contains(element)){
					return false;
				}
//				if (lineCombiProps.contains(element)){
//					return false;
//				}
			}
			return true;
		}
		
		public void addPieProp(Property p){pieProps.add(p);}
		public void addLineProp(Property p){lineCombiProps.add(p);}
		public void addNonPieProp(Property p){nonPieProps.add(p);}
	}
	
	private ChartPropertyFilter filter = new ChartPropertyFilter();

	private TreeViewer colorViewer;
	
	protected void createStyle(ExpandBar parent){
		final TreeViewer viewer = createViewer(parent);

		TreeViewerColumn valueCol = createValueViewerColum(viewer);
		
		ExpandItem item = new ExpandItem(parent, SWT.NONE);
		item.setText(Messages.ChartEditor_0);
		item.setControl(viewer.getTree());
		
		final Property foreground = new Property(Messages.ChartEditor_1, new ColorCellEditor(viewer.getTree()));
		final Property background = new Property(Messages.ChartEditor_2, new ColorCellEditor(viewer.getTree()));
		
		PropertyGroup groupBorder = new PropertyGroup(Messages.ChartEditor_3);
		final Property bordercolor = new Property(Messages.ChartEditor_4, new ColorCellEditor(viewer.getTree()));
		final Property borderwidth = new Property(Messages.ChartEditor_5, new TextCellEditor(viewer.getTree()));
		final Property showborder = new Property(Messages.ChartEditor_6, new CheckboxCellEditor(viewer.getTree()));
		groupBorder.add(borderwidth);
		groupBorder.add(bordercolor);
		groupBorder.add(showborder);
		
		List<Property> input = new ArrayList<Property>();
		input.add(foreground);
		input.add(background);
		input.add(groupBorder);
		
		
		valueCol.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				GenericOptions opts = getGenericOptions();
				if (opts == null){
					return ""; //$NON-NLS-1$
				}
				if (element == borderwidth){
					return "" + opts.getBorderThickness(); //$NON-NLS-1$
				}
				if (element == showborder){
					return opts.isShowBorder() + ""; //$NON-NLS-1$
				}
				return ""; //$NON-NLS-1$
			}
			@Override
			public org.eclipse.swt.graphics.Color getBackground(Object element) {
				GenericOptions opts = getGenericOptions();
				if (opts == null){
					return null;
				}
				if (element == bordercolor){
					Color c = opts.getBorderColor();
					return getColor(c);
				}
				if (element == foreground){
					Color c = opts.getBaseFontColor();
					return getColor(c);

				}
				if (element == background){
					Color c = opts.getBgColor();
					return getColor(c);
				}
				return super.getBackground(element);
			}
			
		});
		
		
		valueCol.setEditingSupport(new EditingSupport(viewer) {
			
			@Override
			protected void setValue(Object element, Object value) {
				GenericOptions opts = getGenericOptions();
				if (value == null){
					return;
				}
				if (element == foreground){
					RGB rgb = (RGB)value;
					opts.setBaseFontColor(new Color(rgb.red, rgb.green, rgb.blue));
				}
				if (element == background){
					RGB rgb = (RGB)value;
					opts.setBgColor(new Color(rgb.red, rgb.green, rgb.blue));
				}
				if (element == bordercolor){
					RGB rgb = (RGB)value;
					opts.setBorderColor(new Color(rgb.red, rgb.green, rgb.blue));
				}
				if (element == borderwidth){
					opts.setBorderThickness(Integer.parseInt((String)value));
				}
				if (element == showborder){
					opts.setShowBorder((Boolean)value);
				}
				notifyChangeOccured();
				viewer.refresh();
			}
			
			@Override
			protected Object getValue(Object element) {
				GenericOptions opts = getGenericOptions();
				if (element == foreground){
					Color c = opts.getBaseFontColor();
					if (c == null){
						return null;
					}
					return new RGB(c.getRed(), c.getGreen(), c.getBlue());
				}
				if (element == background){
					Color c = opts.getBgColor();
					if (c == null){
						return null;
					}
					return new RGB(c.getRed(), c.getGreen(), c.getBlue());
				}
				if (element == bordercolor){
					Color c = opts.getBorderColor();
					if (c == null){
						return null;
					}
					return new RGB(c.getRed(), c.getGreen(), c.getBlue());
				}
				if (element == borderwidth){
					return "" + opts.getBorderThickness(); //$NON-NLS-1$
				}
				if (element == showborder){
					return opts.isShowBorder();
				}
				return null;
			}
			
			@Override
			protected CellEditor getCellEditor(Object element) {
				return ((Property)element).getCellEditor();
			}
			
			@Override
			protected boolean canEdit(Object element) {
				return element instanceof Property;
			}
		});
		viewer.setInput(input);
	}
	
	protected void createDrillDown(ExpandBar parent){
		final TreeViewer viewer = createViewer(parent);
		TreeViewerColumn valueCol = createValueViewerColum(viewer);
		
		final ExpandItem item = new ExpandItem(parent, SWT.NONE);
		item.setControl(viewer.getTree());
		item.setText(Messages.ChartEditor_12);
		
		final Property enable = new Property(Messages.ChartEditor_13, new CheckboxCellEditor(viewer.getTree()));
		ComboBoxViewerCellEditor typeCelled = new ComboBoxViewerCellEditor(viewer.getTree(), SWT.READ_ONLY);
		typeCelled.setLabelProvider(new LabelProvider());
		typeCelled.setContenProvider(new ArrayContentProvider());
		typeCelled.setInput(TypeTarget.values());
		
		final Property drillType = new Property(Messages.ChartEditor_14, typeCelled);
		final Property categoryAsValue = new Property(Messages.ChartEditor_15, new CheckboxCellEditor(viewer.getTree()));
		final Property keepColor = new Property("Keep color", new CheckboxCellEditor(viewer.getTree()));
		
		modelPageCbo = new ComboBoxViewerCellEditor(viewer.getTree(), SWT.READ_ONLY);
		modelPageCbo.setContenProvider(new ArrayContentProvider());
		modelPageCbo.setLabelProvider(new DatasLabelProvider());
		
		final Property targetPage = new Property(Messages.ChartEditor_16, modelPageCbo);

		
		final PropertyGroup zoomProps = new PropertyGroup(Messages.ChartEditor_17);
		final Property width = new Property(Messages.ChartEditor_18, new TextCellEditor(viewer.getTree()));
		final Property height = new Property(Messages.ChartEditor_19, new TextCellEditor(viewer.getTree()));
		zoomProps.add(width); zoomProps.add(height);
		
		lvlCbo = new ComboBoxViewerCellEditor(viewer.getTree(), SWT.READ_ONLY);
		lvlCbo.setContenProvider(new ArrayContentProvider());
		lvlCbo.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				return Messages.ChartEditor_20 + ((Integer)element + 1);
			}
		});
		
		final Property levelProvider = new Property(Messages.ChartEditor_21, lvlCbo);
		//drillType.add(url);
		
		
		valueCol.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				if (def == null){
					return ""; //$NON-NLS-1$
				}
				if (element == enable){	return def.getDrillDatas().isDrillable()+ "";} //$NON-NLS-1$
				if (element == keepColor){	return def.getDrillDatas().isKeepColor()+ "";} //$NON-NLS-1$
				if (element == drillType){
					return def.getDrillDatas().getTypeTarget().name();
				}
				if (element == categoryAsValue){return def.getDrillDatas().isCategorieAsParameterValue() + "";} //$NON-NLS-1$
				if (element == width){return def.getDrillDatas().getZoomWidth() + "";} //$NON-NLS-1$
				if (element == height){	return def.getDrillDatas().getZoomHeight() + "";} //$NON-NLS-1$
				if (element == levelProvider){return Messages.ChartEditor_27 + (def.getDrillDatas().getDrillLevelProvider() + 1);}
				if (element ==targetPage && def.getDrillDatas().getTargetModelPage() != null){return def.getDrillDatas().getTargetModelPage().getName();}
				return ""; //$NON-NLS-1$
			}
		});
		valueCol.setEditingSupport(new EditingSupport(viewer) {
			
			@Override
			protected void setValue(Object element, Object value) {
				if (def == null){return;}
				if (element == enable){def.getDrillDatas().setDrillable((Boolean)value);}
				if (element == keepColor){def.getDrillDatas().setKeepColor((Boolean)value);}
				if (element == drillType){
					def.getDrillDatas().setTypeTarget((TypeTarget)value);
				}
				if (element == categoryAsValue){def.getDrillDatas().setCategorieAsParameterValue((Boolean)value);}
				try{
					if (element == width){def.getDrillDatas().setZoomWidth(Integer.valueOf((String)value));}
					if (element == height){def.getDrillDatas().setZoomHeight(Integer.valueOf((String)value));}
				}catch(Exception ex){}
				if (element == targetPage){def.getDrillDatas().setTargetModelPage((FdModel)value);}
				if (element == levelProvider){def.getDrillDatas().setDrillLevelProvider((Integer)value);}
				
				notifyChangeOccured();
				viewer.refresh();
				viewer.expandAll();
				resizeItem(item);
				

			}
			
			@Override
			protected Object getValue(Object element) {
				if (def == null){return null;}
				if (element == enable){
					return def.getDrillDatas().isDrillable();
				}
				if (element == keepColor){
					return def.getDrillDatas().isKeepColor();
				}
				if (element == drillType){
					return def.getDrillDatas().getTypeTarget();
				}
				if (element == categoryAsValue){
					return def.getDrillDatas().isCategorieAsParameterValue();
				}
				if (element == width){
					return def.getDrillDatas().getZoomWidth() + ""; //$NON-NLS-1$
				}
				if (element == height){
					return def.getDrillDatas().getZoomHeight() + ""; //$NON-NLS-1$
				}
				if (element == targetPage){return def.getDrillDatas().getTargetModelPage();}
				if (element == levelProvider){return def.getDrillDatas().getDrillLevelProvider();}
				return null;
			}
			
			@Override
			protected CellEditor getCellEditor(Object element) {
				if (element == targetPage){
					if (Activator.getDefault().getProject() instanceof MultiPageFdProject){
						modelPageCbo.setInput(((MultiPageFdProject)Activator.getDefault().getProject()).getPagesModels());
					}
					else{
						modelPageCbo.setInput(Collections.EMPTY_LIST);
					}
				}
				
				return ((Property)element).getCellEditor();
			}
			
			@Override
			protected boolean canEdit(Object element) {
				if (def == null){return false;}
				return ((Property)element).getCellEditor() != null;
			}
		});
		
	
		List<Property> input = new ArrayList<Property>();
		input.add(enable);
		input.add(drillType);
		input.add(categoryAsValue);
		input.add(keepColor);
		input.add(zoomProps);
		input.add(targetPage);
		input.add(levelProvider);
		viewer.setInput(input);
	}
	
	
	protected void createChart(ExpandBar parent){
		final TreeViewer viewer = createViewer(parent);
		TreeViewerColumn valueCol = createValueViewerColum(viewer);
		
		ExpandItem item = new ExpandItem(parent, SWT.NONE);
		item.setControl(viewer.getTree());
		item.setText(Messages.ChartEditor_31);
		
		final Property caption = new Property(Messages.ChartEditor_32, new TextCellEditor(viewer.getTree()), true);
		final Property subcaption = new Property(Messages.ChartEditor_33, new TextCellEditor(viewer.getTree()), true);		
		natureCbo = new ComboBoxViewerCellEditor(viewer.getTree());
		natureCbo.setContenProvider(new ArrayContentProvider());
		natureCbo.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				return ChartNature.NATURE_NAMES[((ChartNature)element).getNature()];
			}
		});
		
		lineSerieCbo = new ComboBoxViewerCellEditor(viewer.getTree());
		lineSerieCbo.setContenProvider(new ArrayContentProvider());
		lineSerieCbo.setLabelProvider(new LabelProvider(){
				@Override
				public String getText(Object element) {
					return ((DataAggregation)element).getMeasureName();
				}
			
		});
		
		final Property nature = new Property(Messages.ChartEditor_34, natureCbo);
		
		final Property showLabels = new Property(Messages.ChartEditor_35, new CheckboxCellEditor(viewer.getTree()));
		final Property showValues = new Property(Messages.ChartEditor_36, new CheckboxCellEditor(viewer.getTree()));
		
		final Property rotateValues = new Property(Messages.ChartEditor_37, new CheckboxCellEditor(viewer.getTree()));
		filter.addNonPieProp(rotateValues);
		final Property rotateLabels = new Property(Messages.ChartEditor_38, new CheckboxCellEditor(viewer.getTree()));
		filter.addNonPieProp(rotateLabels);
		final Property slantLabels = new Property(Messages.ChartEditor_39, new CheckboxCellEditor(viewer.getTree()));
		filter.addNonPieProp(slantLabels);
		final Property rotateYAxisNams = new Property(Messages.ChartEditor_40, new CheckboxCellEditor(viewer.getTree()));
		filter.addNonPieProp(rotateYAxisNams);
		
		
		//pie
		final Property pieRadius = new Property(Messages.ChartEditor_41, new TextCellEditor(viewer.getTree()));
		filter.addPieProp(pieRadius);
		final Property sliceDepth = new Property(Messages.ChartEditor_42, new TextCellEditor(viewer.getTree()));
		filter.addPieProp(sliceDepth);
		final Property sliceDistance = new Property(Messages.ChartEditor_43, new TextCellEditor(viewer.getTree()));
		filter.addPieProp(sliceDistance);
		
		//line
		final Property lineSerie = new Property(Messages.ChartEditor_44, lineSerieCbo);
		filter.addLineProp(lineSerie);
		final Property xAxisName = new Property("X Axis name", new TextCellEditor(viewer.getTree()));
		filter.addLineProp(xAxisName);
		final Property pyAxisName = new Property(Messages.ChartEditor_45, new TextCellEditor(viewer.getTree()));
		filter.addLineProp(pyAxisName);
		final Property syAxisName = new Property(Messages.ChartEditor_46, new TextCellEditor(viewer.getTree()));
		filter.addLineProp(syAxisName);
		
		//
		final Property dynamicLegend = new Property("DynamicLegend",  new CheckboxCellEditor(viewer.getTree()));
		filter.addNonPieProp(dynamicLegend);
		final Property labelSize = new Property("LabelSize",  new TextCellEditor(viewer.getTree()));
		
		final Property exportEnable = new Property("exportEnable",  new CheckboxCellEditor(viewer.getTree()));
		
		List<Property> input = new ArrayList<Property>();
		input.add(caption);
		input.add(subcaption);
		input.add(nature);
		input.add(showLabels);
		input.add(showValues);
		input.add(rotateValues);
		input.add(rotateLabels);
		input.add(slantLabels);
		input.add(rotateYAxisNams);
		input.add(pieRadius);
		input.add(sliceDepth);
		input.add(sliceDistance);
		input.add(lineSerie);
		input.add(xAxisName);
		input.add(pyAxisName);
		input.add(syAxisName);
		input.add(dynamicLegend);
		input.add(labelSize);
		input.add(exportEnable);
		
		valueCol.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				GenericOptions opts = getGenericOptions();
				if (opts == null){
					return ""; //$NON-NLS-1$
				}
				if (element == caption){
					return opts.getCaption() + ""; //$NON-NLS-1$
				}
				if (element == subcaption){
					return opts.getSubCaption()+ ""; //$NON-NLS-1$
				}
				if (element == nature){
					return ChartNature.NATURE_NAMES[def.getNature().getNature()];
				}
				if (element == showLabels){
					return opts.isShowLabel() + ""; //$NON-NLS-1$
				}
				if (element == showValues){
					return opts.isShowValues() + ""; //$NON-NLS-1$
				}
				if (element == pieRadius){
					return ((PieGenericOptions)opts).getPieRadius() + ""; //$NON-NLS-1$
				}
				if (element == sliceDistance){
					return((PieGenericOptions)opts).getSlicingDistance()+ ""; //$NON-NLS-1$
				}
				if (element == sliceDepth){
					return ((PieGenericOptions)opts).getPieSliceDepth() + ""; //$NON-NLS-1$
				}
				if (getNonPieOptions() != null && element == rotateLabels){
					return getNonPieOptions().isRotateLabels() + "";	 //$NON-NLS-1$
				}
				if (getNonPieOptions() != null &&element == rotateValues){
					return getNonPieOptions().isRotateValues()+ ""; //$NON-NLS-1$
				}
				if (getNonPieOptions() != null &&element == rotateYAxisNams){
					return getNonPieOptions().isRotateYAxisName() + "";	 //$NON-NLS-1$
				}
				if (getNonPieOptions() != null &&element == slantLabels){
					return getNonPieOptions().isSlantLabels()+ ""; //$NON-NLS-1$
				}
				if (getNonPieOptions() != null && element == pyAxisName){
					return getNonPieOptions().getPYAxisName()  +""; //$NON-NLS-1$
				}
				if (getNonPieOptions() != null && element == syAxisName){
					return getNonPieOptions().getSYAxisName()  +""; //$NON-NLS-1$
				}
				if (getNonPieOptions() != null && element == xAxisName){
					return getNonPieOptions().getxAxisName()  +""; //$NON-NLS-1$
				}
				if (getLineCombiOptions() != null && element == lineSerie){
					if (getLineCombiOptions() == null){
						return ""; //$NON-NLS-1$
					}
					return getLineCombiOptions().getLineSerieName();
				}
				if(element == dynamicLegend) {
					return opts.isDynamicLegend() + ""; //$NON-NLS-1$
				}
				if(element == exportEnable) {
					return opts.isExportEnable() + ""; //$NON-NLS-1$
				}
				if(element == labelSize) {
					return opts.getLabelSize() + "";
				}
				
				return ""; //$NON-NLS-1$
			}
		});
		
		valueCol.setEditingSupport(new EditingSupport(viewer) {
			@Override
			protected void setValue(Object element, Object value) {
				GenericOptions opts = getGenericOptions();
				if (element == caption){
					opts.setCaption((String)value);
				}
				if (element == nature){
					def.setNature((ChartNature)value);
				}
				if (element == subcaption){
					opts.setSubCaption((String)value);
				}
				if (element == showLabels){
					opts.setShowLabel((Boolean)value);
				}
				if (element == showValues){
					opts.setShowValues((Boolean)value);
				}
				try{
					if (element == pieRadius){
						((PieGenericOptions)opts).setPieRadius(Integer.valueOf((String)value));
					}
					if (element == sliceDistance){
						((PieGenericOptions)opts).setSlicingDistance((Integer.valueOf((String)value)));
					}
					if (element == sliceDepth){
						((PieGenericOptions)opts).setPieSliceDepth((Integer.valueOf((String)value)));
					}
				}catch(NumberFormatException ex){
					
				}
				
				if (getNonPieOptions() != null && element == rotateLabels){
					getNonPieOptions().setRotateLabels((Boolean)value);	
				}
				if (getNonPieOptions() != null &&element == rotateValues){
					getNonPieOptions().setRotateValues((Boolean)value);
				}
				if (getNonPieOptions() != null &&element == rotateYAxisNams){
					getNonPieOptions().setRotateYAxisName((Boolean)value);	
				}
				if (getNonPieOptions() != null &&element == slantLabels){
					getNonPieOptions().setSlantLabels((Boolean)value);
				}
				if (getNonPieOptions() != null && element == pyAxisName){
					getNonPieOptions().setPYAxisName((String)value);
				}
				if (getNonPieOptions() != null && element == syAxisName){
					getNonPieOptions().setSYAxisName((String)value);
				}
				if (getNonPieOptions() != null && element == xAxisName){
					getNonPieOptions().setxAxisName((String)value);
				}
				if (getLineCombiOptions() != null && element == lineSerie){
					getLineCombiOptions().setLineSerieName(((DataAggregation)value).getMeasureName());
				}
				
				if(element == dynamicLegend) {
					opts.setDynamicLegend((Boolean)value); //$NON-NLS-1$
				}
				if(element == exportEnable) {
					opts.setExportEnable((Boolean)value); //$NON-NLS-1$
				}
				if(element == labelSize) {
					opts.setLabelSize(Integer.valueOf((String)value));
				}
				
				notifyChangeOccured();
				viewer.refresh();
			}
			
			@Override
			protected Object getValue(Object element) {
				GenericOptions opts = getGenericOptions();
				if (element == caption){
					return opts.getCaption();
				}
				if (element == subcaption){
					return opts.getSubCaption();
				}
				if (element == nature){
					return def.getNature();
				}
				if (element == showLabels){
					return opts.isShowLabel();
				}
				if (element == showValues){
					return opts.isShowValues();
				}
				if (element == pieRadius){
					return ((PieGenericOptions)opts).getPieRadius() + ""; //$NON-NLS-1$
				}
				if (element == sliceDistance){
					return((PieGenericOptions)opts).getSlicingDistance() + ""; //$NON-NLS-1$
				}
				if (element == sliceDepth){
					return ((PieGenericOptions)opts).getPieSliceDepth()  + ""; //$NON-NLS-1$
				}
				if (getNonPieOptions() != null && element == rotateLabels){
					return getNonPieOptions().isRotateLabels();	
				}
				if (getNonPieOptions() != null &&element == rotateValues){
					return getNonPieOptions().isRotateValues();
				}
				if (getNonPieOptions() != null &&element == rotateYAxisNams){
					return getNonPieOptions().isRotateYAxisName();	
				}
				if (getNonPieOptions() != null &&element == slantLabels){
					return getNonPieOptions().isSlantLabels();
				}
				if (getNonPieOptions() != null && element == pyAxisName){
					return getNonPieOptions().getPYAxisName()  +""; //$NON-NLS-1$
				}
				if (getNonPieOptions() != null && element == syAxisName){
					return getNonPieOptions().getSYAxisName()  +""; //$NON-NLS-1$
				}
				if (getNonPieOptions() != null && element == xAxisName){
					return getNonPieOptions().getxAxisName()  +""; //$NON-NLS-1$
				}
				if (getLineCombiOptions() != null && element == lineSerie){
					if (getLineCombiOptions() == null){
						return null;
					}
					for(DataAggregation a : ((MultiSerieChartData)def.getDatas()).getAggregation()){
						if (a.getMeasureName().equals(getLineCombiOptions().getLineSerieName())){
							return a;
						}
					}
				}
				if(element == dynamicLegend) {
					return opts.isDynamicLegend(); //$NON-NLS-1$
				}
				if(element == exportEnable) {
					return opts.isExportEnable(); //$NON-NLS-1$
				}
				if(element == labelSize) {
					return opts.getLabelSize() + "";
				}
				return null;
			}
			
			@Override
			protected CellEditor getCellEditor(Object element) {
				((Property)element).updateContentAssist(getComponentParameterNames());
				return ((Property)element).getCellEditor();
			}
			
			@Override
			protected boolean canEdit(Object element) {
				return element instanceof Property;
			}
		});
		viewer.setFilters(new ViewerFilter[]{filter});
		viewer.setInput(input);
		
	}
	
	protected void createNumberFormat(ExpandBar parent){
		final TreeViewer viewer = createViewer(parent);
		TreeViewerColumn valueCol = createValueViewerColum(viewer);
		
		ExpandItem item = new ExpandItem(parent, SWT.NONE);
		item.setControl(viewer.getTree());
		item.setText(Messages.ChartEditor_68);
		
		final Property decSeparator = new Property(Messages.ChartEditor_69, new TextCellEditor(viewer.getTree()), 	NumberFormatOptions.standardKeys[NumberFormatOptions.KEY_DECIMAL_SEPARATOR]);
		final Property thSeparator = new Property(Messages.ChartEditor_70, new TextCellEditor(viewer.getTree()), 	NumberFormatOptions.standardKeys[NumberFormatOptions.KEY_THOUSAND_SEPARATOR]);
		final Property decNumber = new Property(Messages.ChartEditor_71, new TextCellEditor(viewer.getTree()), 	NumberFormatOptions.standardKeys[NumberFormatOptions.KEY_DECIMALS]);
		final Property forceDecimal = new Property(Messages.ChartEditor_72, new CheckboxCellEditor(viewer.getTree()), 	NumberFormatOptions.standardKeys[NumberFormatOptions.KEY_FORCE_DECIMALS]);
		final Property prefix = new Property(Messages.ChartEditor_73, new TextCellEditor(viewer.getTree()), 	NumberFormatOptions.standardKeys[NumberFormatOptions.KEY_NUMBER_PREFIX]);
		final Property suffix = new Property(Messages.ChartEditor_74, new TextCellEditor(viewer.getTree()), 	NumberFormatOptions.standardKeys[NumberFormatOptions.KEY_NUMBER_SUFFIX]);
		final Property applyformat = new Property(Messages.ChartEditor_75, new CheckboxCellEditor(viewer.getTree()), 	NumberFormatOptions.standardKeys[NumberFormatOptions.KEY_FORMAT_NUMBER]);
		final Property applyScaleFormat = new Property(Messages.ChartEditor_76, new CheckboxCellEditor(viewer.getTree()), 	NumberFormatOptions.standardKeys[NumberFormatOptions.KEY_FORMAT_NUMBER_SCALE]);
		
		List<Property> input = new ArrayList<Property>();
		input.add(applyformat);
		input.add(decSeparator);
		input.add(thSeparator);
		input.add(decNumber);
		input.add(forceDecimal);
		input.add(prefix);
		input.add(suffix);
		input.add(applyScaleFormat);
		
		valueCol.setLabelProvider(new ColumnLabelProvider(){
			public String getText(Object element) {
				if (def == null){return "";} //$NON-NLS-1$
				if (element == forceDecimal){
					return getNumberFormatOptions().isForceDecimal() + ""; //$NON-NLS-1$
				}
				if (element == applyScaleFormat){
					return "" + getNumberFormatOptions().isFormatNumberScale(); //$NON-NLS-1$
				}
				return "" + getNumberFormatOptions().getValue(((Property)element).getKeyProperty()); //$NON-NLS-1$
			}
		});
		
		valueCol.setEditingSupport(new EditingSupport(viewer) {
			@Override
			protected void setValue(Object element, Object value) {
				if (def == null){return;}
				NumberFormatOptions opts = getNumberFormatOptions();
				if (element == decSeparator){
					opts.setDecimalSeparator((String)value);
				}
				if (element == thSeparator){
					opts.setThousandSeparator((String)value);
				}
				if (element == decNumber){
					opts.setDecimals(Integer.valueOf((String)value));
				}
				if (element == forceDecimal){
					opts.setForceDecimal((Boolean)value);
				}
				if (element == prefix){
					opts.setNumberPrefix((String)value);
				}
				if (element == suffix){
					opts.setNumberSuffix((String)value);
				}
				if (element == applyformat){
					opts.setFormatNumber((Boolean)value);
				}
				if (element == applyScaleFormat){
					opts.setFormatNumberScale((Boolean)value);
				}
			
				notifyChangeOccured();
				viewer.refresh();
			}
			
			@Override
			protected Object getValue(Object element) {
				if (def == null){return "";} //$NON-NLS-1$
				NumberFormatOptions opts = getNumberFormatOptions();
				if (element == forceDecimal){
					return opts.isForceDecimal();
				}
				else if (element == applyformat){
					return opts.isFormatNumber();
				}
				else if (element == applyScaleFormat){
					return opts.isFormatNumberScale();
				}
				else{
					return opts.getValue(((Property)element).getKeyProperty());
				}
				
			}
			
			@Override
			protected CellEditor getCellEditor(Object element) {
				return ((Property)element).getCellEditor();
			}
			
			@Override
			protected boolean canEdit(Object element) {
				return element instanceof Property;
			}
		});
		viewer.setInput(input);
	}
	
	
	public ChartEditor(Composite parent) {
		super(parent);
		fillBar();
	}
	
	
	public void setInput(EditPart editPart, ComponentConfig config, IComponentDefinition component){
		this.def = (ComponentChartDefinition)component;
		if (Activator.getDefault().getProject() instanceof MultiPageFdProject){
			modelPageCbo.setInput(((MultiPageFdProject)Activator.getDefault().getProject()).getPagesModels());
		}
		else{
			modelPageCbo.setInput(Collections.EMPTY_LIST);
		}
		if (def.getNature().isMonoSerie()){
			natureCbo.setInput(ChartNature.getMonoSeries());
		}
		else{
			natureCbo.setInput(ChartNature.getMultiSeries());
		}
		
		if (this.def.getDatas() instanceof MultiSerieChartData){
			lineSerieCbo.setInput(((MultiSerieChartData)this.def.getDatas()).getAggregation());
		}
		
		List<Integer> availableLevels = new ArrayList<Integer>();
		for(int i = 0; i < ((IChartData)def.getDatas()).getLevelCategoriesIndex().size(); i++){
			availableLevels.add(i);
		}
		lvlCbo.setInput(availableLevels);
		
		List colors = new ArrayList();
		for(ColorRange r : ((ComponentChartDefinition)component).getColorRanges()){
			colors.add(new PropertyMapColor(r, colorViewer.getTree()));
		}
		colorViewer.setInput(colors);
		
		super.setInput(editPart, config, component);
		
		
		
	}
	
	private LineCombinationOption getLineCombiOptions(){
		if (def == null){
			return null;
		}
		LineCombinationOption opts = (LineCombinationOption)def.getOptions(LineCombinationOption.class);
		return opts;
	}
	
	private GenericOptions getGenericOptions(){
		if (def == null){
			return null;
		}
		GenericOptions opts = (GenericOptions)def.getOptions(GenericOptions.class);
		if (opts == null){
			opts = (GenericOptions)def.getOptions(PieGenericOptions.class);
		}
		if (opts == null){
			opts = (GenericOptions)def.getOptions(GenericNonPieOptions.class);
		}
		return opts;
	}
	
	public static org.eclipse.swt.graphics.Color getColor(Color col) {
		if (col == null){
			return null;
		}
		String c = "#"; //$NON-NLS-1$
		String t = Integer.toHexString(col.getRed());
		if (t.length() == 1){
			t = "0" + t; //$NON-NLS-1$
		}
		c = c + t;
		
		t = Integer.toHexString(col.getGreen());
		if (t.length() == 1){
			t = "0" + t; //$NON-NLS-1$
		}
		c = c + t;
		
		t = Integer.toHexString(col.getBlue()) ;
		if (t.length() == 1){
			t = "0" + t; //$NON-NLS-1$
		}
		c = c + t;
		
		
		if (ColorManager.getColorRegistry().get(c) == null){
			ColorManager.getColorRegistry().put(c, new RGB(col.getRed(), col.getGreen(), col.getBlue()));
		}
		return ColorManager.getColorRegistry().get(c);
	}
	
	private GenericNonPieOptions getNonPieOptions(){
		return (GenericNonPieOptions)def.getOptions(GenericNonPieOptions.class);
	}
	
	private NumberFormatOptions getNumberFormatOptions(){
		return (NumberFormatOptions)def.getOptions(NumberFormatOptions.class);
	}
	
	protected void createColors(ExpandBar parent){
		colorViewer = createViewer(parent);
		TreeViewerColumn valueCol = createValueViewerColum(colorViewer);
		
		ExpandItem item = new ExpandItem(parent, SWT.NONE);
		item.setControl(colorViewer.getTree());
		item.setText(Messages.MapEditor_24);

		
		valueCol.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				if (element instanceof PropertyMapColor){
					return ((PropertyMapColor)element).getRange().getName();
				}
				PropertyMapColor parent = (PropertyMapColor)((Property)element).getParent();
				return parent.getValueString((Property)element);
			}
			@Override
			public org.eclipse.swt.graphics.Color getBackground(Object element) {
				if (element instanceof PropertyMapColor){
					return null;
				}
				PropertyMapColor parent = (PropertyMapColor)((Property)element).getParent();
				
				return parent.getColor((Property)element);
			}
		});
	
		valueCol.setEditingSupport(new EditingSupport(colorViewer) {
			
			@Override
			protected void setValue(Object element, Object value) {
				if (element instanceof PropertyMapColor){
					((PropertyMapColor)element).setRangeName((String)value);
				}
				else{
					PropertyMapColor parent = (PropertyMapColor)((Property)element).getParent();
					parent.setValue((Property)element, value);
				}
				notifyChangeOccured();
				colorViewer.refresh();
			}
			
			@Override
			protected Object getValue(Object element) {
				if (element instanceof PropertyMapColor){
					return ((PropertyMapColor)element).getRange().getName();
				}
				else{
					PropertyMapColor parent = (PropertyMapColor)((Property)element).getParent();
					return parent.getValue((Property)element);
				}
			}
			
			@Override
			protected CellEditor getCellEditor(Object element) {
				return ((Property)element).getCellEditor() ;
			}
			
			@Override
			protected boolean canEdit(Object element) {
				return ((Property)element).getCellEditor() != null;
			}
		});
	
	
		createColorMenu();
	}
	
	
	private void createColorMenu(){
		MenuManager mgr = new MenuManager();
		Action addRange = new Action(Messages.MapEditor_25){
			public void run(){
				ColorRange range = new ColorRange(Messages.MapEditor_26, "ffffff", 0, Integer.MAX_VALUE); //$NON-NLS-2$
				((ComponentChartDefinition)getComponentDefinition()).addColorRange(range);
				((List)colorViewer.getInput()).add(new PropertyMapColor(range, colorViewer.getTree()));
				colorViewer.refresh();
			}
		};
		
		final Action removeRange = new Action(Messages.MapEditor_28){
			public void run(){
				PropertyMapColor range = (PropertyMapColor)((IStructuredSelection)colorViewer.getSelection()).getFirstElement();
				((ComponentChartDefinition)getComponentDefinition()).removeColorRange(range.getRange());
				((List)colorViewer.getInput()).remove(range);
				colorViewer.refresh();
			}
		};
		mgr.add(addRange);mgr.add(removeRange);
		mgr.addMenuListener(new IMenuListener() {
			
			@Override
			public void menuAboutToShow(IMenuManager manager) {
				if (colorViewer.getSelection().isEmpty()){
					removeRange.setEnabled(false);return;
				}
				if (! (((IStructuredSelection)colorViewer.getSelection()).getFirstElement() instanceof PropertyMapColor)){
					removeRange.setEnabled(false);return;
				}
				removeRange.setEnabled(true);
				
				
			}
		});
	
		colorViewer.getTree().setMenu(mgr.createContextMenu(colorViewer.getTree()));
	}
}

