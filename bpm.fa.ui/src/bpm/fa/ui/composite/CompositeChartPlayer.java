package bpm.fa.ui.composite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.Range;
import org.jfree.data.category.CategoryDataset;
import org.jfree.experimental.chart.swt.ChartComposite;

import bpm.fa.api.olap.Dimension;
import bpm.fa.api.olap.Hierarchy;
import bpm.fa.api.olap.Level;
import bpm.fa.api.olap.Measure;
import bpm.fa.api.olap.MeasureGroup;
import bpm.fa.api.olap.OLAPMember;
import bpm.fa.api.olap.OLAPQuery;
import bpm.fa.ui.Messages;
import bpm.fa.ui.composite.chart.ResultDataExtractor;
import bpm.fa.ui.composite.viewers.StructureLabelProvider;
import bpm.fa.ui.ktable.CubeModel;


public class CompositeChartPlayer extends Composite{
	
	private class Animator extends Thread{
		LinkedHashMap<String, JFreeChart> charts;
		Iterator<String> it;
		
		Animator(Animator animator){
			this.charts = animator.charts;
			this.it = animator.it;
		}
		Animator(LinkedHashMap<String, JFreeChart> charts){
			this.charts = charts;
			Double min = null;
			Double max = null;
			for(JFreeChart c : charts.values()){
				ValueAxis va = c.getCategoryPlot().getRangeAxis();
				if (min == null){
					min = va.getRange().getLowerBound();
				}
				
				if (max == null){
					max = va.getRange().getUpperBound();
				}
			}
			
			for(JFreeChart c : charts.values()){
				ValueAxis va = c.getCategoryPlot().getRangeAxis();
				va.setRange(new Range(min, max));
			}
			
			it = this.charts.keySet().iterator();
			next(true);
			
		}
		
		public void next(boolean loop){
			if (!it.hasNext()){
				if (loop){
					it = this.charts.keySet().iterator();
				}
				else{
					return;
				}
				
				
			}
			
			Display.getDefault().asyncExec(new Runnable() {
				
				@Override
				public void run() {
					String current = it.next();
					chart.setChart(charts.get(current));
					chart.getChart().fireChartChanged();
					chart.setVisible(true);
					
				}
			});
			
//			scrollValue.setText(current);
			
		}
		
		
		public void run(){
			next(true);
			
			while(it.hasNext()){
				
				try{
					Thread.sleep(2000);
				}catch(Exception ex){
					
				}
				next(false);
			}
			Display.getDefault().asyncExec(new Runnable() {
			
				@Override
				public void run() {
					play.setSelection(false);
				}
			});
			next(true);
		}
	}
	
	
	
	private class HieraListener implements ISelectionChangedListener{
		Viewer levelViewer;
		HieraListener(Viewer levelViewer){this.levelViewer = levelViewer;}
		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			if (event.getSelection().isEmpty()){
				levelViewer.setInput(Collections.EMPTY_LIST);
			}
			else{
				levelViewer.setInput(((Hierarchy)((IStructuredSelection)event.getSelection()).getFirstElement()).getLevel());
			}
			
			try{
				levelViewer.setSelection(new StructuredSelection(((List)levelViewer.getInput()).get(0)));
			}catch(Exception ex){
				
			}
			
//			if (xHieraViewer.getSelection().isEmpty() || xLvlViewer.getSelection().isEmpty() ||
//				yHieraViewer.getSelection().isEmpty() || yLvlViewer.getSelection().isEmpty()){
//				draw.setEnabled(false);
//			}
//			else{
//				draw.setEnabled(true);
//			}
		}
		
	}
	
	
	private class Filter extends ViewerFilter{

		@Override
		public boolean select(Viewer viewer, Object parentElement, Object element) {
			
			if (element instanceof Hierarchy){
				if (!usedHierarchy.contains(element)){
					return false;
				}
			}
			
			else if (element instanceof Level){
				OLAPQuery q = model.getOLAPCube().getMdx();
				List<String> lst = new ArrayList<String>();
				lst.addAll(q.getRows());
				lst.addAll(q.getCols());
				
				for(Hierarchy h : usedHierarchy){
					
					for(Level l : h.getLevel()){
						if (l.getUniqueName().equals(((Level)element).getUniqueName())){
							for(OLAPMember m : model.getOLAPCube().getLastResult().getLevelMembers(h, l)){
								for(String s : lst){
									if (s.equals( m.getUniqueName())){
										return true;
									}
									
									else if (s.endsWith(".children")){ //$NON-NLS-1$
										if (s.replace(".children", "").split("\\]|\\]\\.\\[").length + 1 == m.getUniqueName().split("\\]|\\]\\.\\[").length){ //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
											return true;
										}
									}
									else{
										if (s.split("\\]|\\]\\.\\[").length == m.getUniqueName().split("\\]|\\]\\.\\[").length ){ //$NON-NLS-1$ //$NON-NLS-2$
											return true;
										}
									}
									
								}
							}
							
						}
						
					}
				}
				
				return false;
			}
			return true;
		}
		
	}
	
	private static final String[] CHARTS = new String[]{
		"AreaChart", "BarChart", "BarChart3D","LineChart", "LineChart3D", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		"StackedArea", "StackedBar", "StackedBar3D","WaterFall" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	};
	
	private final FormToolkit formToolkit = new FormToolkit(Display.getDefault());
//	private Text scrollSpeed;
//	private Text scrollValue;
	private Combo chartType;
	private ComboViewer xHieraViewer, xLvlViewer, yHieraViewer, yLvlViewer, scrollHieraViewer,scrollLvlViewer, measureViewer;
	private Button play, draw, next;


	private ChartComposite chart;
	
	private Animator animator = null;
	
	private CubeModel model;
	private List<Hierarchy> usedHierarchy;
	
	public CompositeChartPlayer(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(3, false));
		
		Label lblAxis = new Label(this, SWT.NONE);
		lblAxis.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lblAxis.setText(Messages.CompositeChartPlayer_0);
		
		Label lblHierarchy = new Label(this, SWT.NONE);
		lblHierarchy.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lblHierarchy.setText(Messages.CompositeChartPlayer_17);
		
		Label lblLevel = new Label(this, SWT.NONE);
		lblLevel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lblLevel.setText(Messages.CompositeChartPlayer_18);
		
		Label lblXaxis = new Label(this, SWT.NONE);
		lblXaxis.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lblXaxis.setText(Messages.CompositeChartPlayer_19);
		
		xHieraViewer = new ComboViewer(this, SWT.READ_ONLY);
		Combo combo_1 = xHieraViewer.getCombo();
		combo_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		xHieraViewer.addFilter(new Filter());
		
		xLvlViewer = new ComboViewer(this, SWT.READ_ONLY);
		xLvlViewer.addFilter(new Filter());
		Combo combo = xLvlViewer.getCombo();
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblYaxis = new Label(this, SWT.NONE);
		lblYaxis.setText(Messages.CompositeChartPlayer_20);
		lblYaxis.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		
		yHieraViewer = new ComboViewer(this, SWT.READ_ONLY);
		yHieraViewer.addFilter(new Filter());
		Combo combo_2 = yHieraViewer.getCombo();
		combo_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		yLvlViewer = new ComboViewer(this, SWT.READ_ONLY);
		yLvlViewer.addFilter(new Filter());
		Combo combo_3 = yLvlViewer.getCombo();
		combo_3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblScrollaxis = new Label(this, SWT.NONE);
		lblScrollaxis.setText(Messages.CompositeChartPlayer_21);
		
		scrollHieraViewer = new ComboViewer(this, SWT.READ_ONLY);
		scrollHieraViewer.addFilter(new Filter());
		Combo combo_4 = scrollHieraViewer.getCombo();
		combo_4.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		scrollLvlViewer = new ComboViewer(this, SWT.READ_ONLY);
		scrollLvlViewer.addFilter(new Filter());
		Combo combo_5 = scrollLvlViewer.getCombo();
		combo_5.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		
		Label lblMeasure = new Label(this, SWT.NONE);
		lblMeasure.setText(Messages.CompositeChartPlayer_22);
		lblMeasure.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		
		measureViewer = new ComboViewer(this, SWT.READ_ONLY);
		measureViewer.addFilter(new Filter());
		Combo combo_6 = measureViewer.getCombo();
		combo_6.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		
		
		
//		Label lblSpeed = new Label(this, SWT.NONE);
//		formToolkit.adapt(lblSpeed, true, true);
//		lblSpeed.setText("Speed");
//		
//		scrollSpeed = new Text(this, SWT.BORDER);
//		scrollSpeed.setText("10");
//		scrollSpeed.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
//		formToolkit.adapt(scrollSpeed, true, true);
		
		
		Label l = new Label(this, SWT.NONE);
		l.setText(Messages.CompositeChartPlayer_23);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		chartType = new Combo(this, SWT.READ_ONLY);
		chartType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		chartType.setItems(CHARTS);
		chartType.select(2);
		
		Composite composite_1 = new Composite(this, SWT.NONE);
		composite_1.setLayout(new GridLayout(3, true));
		composite_1.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false, 3, 1));
		
		draw = formToolkit.createButton(composite_1, Messages.CompositeChartPlayer_24, SWT.NONE);
		draw.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		
		
		play = formToolkit.createButton(composite_1, Messages.CompositeChartPlayer_25, SWT.TOGGLE);
		play.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		play.setEnabled(false);
		

		next = formToolkit.createButton(composite_1, Messages.CompositeChartPlayer_26, SWT.NONE);
		next.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		next.setEnabled(false);
		
		Composite composite = new Composite(this, SWT.BORDER);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
		composite.setLayout(new FillLayout());
		
		
		chart = new ChartComposite(composite, SWT.BORDER);
		chart.setVisible(false);
		
	
		configureViewers();
	}
	
	private void configureViewers(){
		xHieraViewer.setContentProvider(new ArrayContentProvider());
		xHieraViewer.setLabelProvider(new StructureLabelProvider());
		xHieraViewer.addSelectionChangedListener(new HieraListener(xLvlViewer));
		
		xLvlViewer.setContentProvider(new ArrayContentProvider());
		xLvlViewer.setLabelProvider(new StructureLabelProvider());
		
		yHieraViewer.setContentProvider(new ArrayContentProvider());;
		yHieraViewer.setLabelProvider(new StructureLabelProvider());
		yHieraViewer.addSelectionChangedListener(new HieraListener(yLvlViewer));
		
		yLvlViewer.setContentProvider(new ArrayContentProvider());
		yLvlViewer.setLabelProvider(new StructureLabelProvider());
		
		
		scrollHieraViewer.setContentProvider(new ArrayContentProvider());
		scrollHieraViewer.setLabelProvider(new StructureLabelProvider());
		scrollHieraViewer.addSelectionChangedListener(new HieraListener(scrollLvlViewer));
		
		scrollLvlViewer.setContentProvider(new ArrayContentProvider());
		scrollLvlViewer.setLabelProvider(new StructureLabelProvider());
		
		measureViewer.setContentProvider(new ArrayContentProvider());
		measureViewer.setLabelProvider(new StructureLabelProvider());
		
		draw.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				createChart();
			}			
		});
		
		
		
		next.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (animator != null){
					animator.next(true);
				}
			}			
		});
		
		play.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (animator != null){
					if( animator.isAlive()){
						animator.interrupt();
					}
					animator = new Animator(animator);		
				}
				
				if (play.getSelection()){
					animator.start();
					
				}
				

			}			
		});
	}


	
	private JFreeChart createJFree(String title, Hierarchy dX, Measure measure, CategoryDataset ds){
		JFreeChart c = null;
		
		
		switch(chartType.getSelectionIndex()){
		case 0:
			c = ChartFactory.createAreaChart(title,dX.getCaption(), measure.getCaption(), ds, PlotOrientation.VERTICAL, true, false, false);
			break;
		case 1:
			c = ChartFactory.createBarChart(title, dX.getCaption(), measure.getCaption(), ds, PlotOrientation.VERTICAL, true, false, false);
			break;
		case 2:
			c = ChartFactory.createBarChart3D(title, dX.getCaption(), measure.getCaption(), ds, PlotOrientation.VERTICAL, true, false, false);
			break;
		case 3:
			c = ChartFactory.createLineChart(title, dX.getCaption(), measure.getCaption(), ds, PlotOrientation.VERTICAL, true, false, false);
			break;
		case 4:
			c = ChartFactory.createLineChart3D(title, dX.getCaption(), measure.getCaption(), ds, PlotOrientation.VERTICAL, true, false, false);
			break;
		case 5:
			c = ChartFactory.createStackedAreaChart(title, dX.getCaption(), measure.getCaption(), ds, PlotOrientation.VERTICAL, true, false, false);
			break;
		case 6:
			c = ChartFactory.createStackedBarChart(title, dX.getCaption(), measure.getCaption(), ds, PlotOrientation.VERTICAL, true, false, false);
			break;
		case 7:
			c = ChartFactory.createStackedBarChart3D(title, dX.getCaption(), measure.getCaption(), ds, PlotOrientation.VERTICAL, true, false, false);
			break;
		case 8:
			c = ChartFactory.createWaterfallChart(title, dX.getCaption(), measure.getCaption(), ds, PlotOrientation.VERTICAL, true, false, false);
			break;
		case 9:
		}
		
		return c;
	}
	
	
	private void createChart(){
		play.setSelection(false);
		if (scrollLvlViewer.getSelection().isEmpty()){
			
			if (animator != null && animator.isAlive()){
				animator.interrupt();
				
			}
			animator = null;
			play.setEnabled(false);
			next.setEnabled(false);
			ResultDataExtractor ex = new ResultDataExtractor(model.getOLAPCube().getMdx(), model.getOLAPResult());

			
			
			CategoryDataset ds = ex.extractDatas(
					model.getOLAPResult().getLevelMembers((bpm.fa.api.olap.Hierarchy)((IStructuredSelection)xHieraViewer.getSelection()).getFirstElement(),
							(Level)((IStructuredSelection)xLvlViewer.getSelection()).getFirstElement()), 
					model.getOLAPResult().getLevelMembers((bpm.fa.api.olap.Hierarchy)((IStructuredSelection)yHieraViewer.getSelection()).getFirstElement(),
							(Level)((IStructuredSelection)yLvlViewer.getSelection()).getFirstElement()),
							((List)measureViewer.getInput()).size() > 1 ? (Measure)((IStructuredSelection)measureViewer.getSelection()).getFirstElement() : null);
			
			
			
			
			
			chart.setChart(createJFree("", (bpm.fa.api.olap.Hierarchy)((IStructuredSelection)xHieraViewer.getSelection()).getFirstElement(), (Measure)((IStructuredSelection)measureViewer.getSelection()).getFirstElement(), ds)); //$NON-NLS-1$
			chart.getChart().fireChartChanged();
			chart.setVisible(true);
		}
		else{
			
			ResultDataExtractor ex = new ResultDataExtractor(model.getOLAPCube().getMdx(), model.getOLAPResult());
			
			
			LinkedHashMap<String, CategoryDataset> ds = ex.extractDatas(
					model.getOLAPResult().getLevelMembers((bpm.fa.api.olap.Hierarchy)((IStructuredSelection)xHieraViewer.getSelection()).getFirstElement(),
							(Level)((IStructuredSelection)xLvlViewer.getSelection()).getFirstElement()), 
					model.getOLAPResult().getLevelMembers((bpm.fa.api.olap.Hierarchy)((IStructuredSelection)yHieraViewer.getSelection()).getFirstElement(),
							(Level)((IStructuredSelection)yLvlViewer.getSelection()).getFirstElement()), 
					model.getOLAPResult().getLevelMembers((bpm.fa.api.olap.Hierarchy)((IStructuredSelection)scrollHieraViewer.getSelection()).getFirstElement(),
							(Level)((IStructuredSelection)scrollLvlViewer.getSelection()).getFirstElement()),
							((List)measureViewer.getInput()).size() > 1 ? (Measure)((IStructuredSelection)measureViewer.getSelection()).getFirstElement() : null);
			
			if (animator != null && animator.isAlive()){
				animator.interrupt();
				animator = null;
				play.setEnabled(false);
				next.setEnabled(false);
			}
			
			List<String> keys = new ArrayList<String>(ds.keySet());
			Collections.sort(keys);
			
			
			LinkedHashMap<String, JFreeChart> charts = new LinkedHashMap<String, JFreeChart>();
			
			for(String s : keys){
				charts.put(s, createJFree(s, (bpm.fa.api.olap.Hierarchy)((IStructuredSelection)xHieraViewer.getSelection()).getFirstElement(), 
							(Measure)((IStructuredSelection)measureViewer.getSelection()).getFirstElement(), ds.get(s)));// createJFree(ds.get(s)));
			}
			animator = new Animator(charts);
			play.setEnabled(true);
			next.setEnabled(true);
		}
		
		
		
		
		
	}
	
	
	
	
	
	public void setInput(CubeModel model){
		this.model = model;
		
		
	
		usedHierarchy = new ArrayList<Hierarchy>();
		List<Measure> measures = new ArrayList<Measure>();
		
		for(Dimension d : model.getOLAPCube().getDimensions()){
			for(bpm.fa.api.olap.Hierarchy h : d.getHierarchies()){
				
				for(String s : model.getOLAPCube().getMdx().getCols()){
					if (s.startsWith(h.getUniqueName()) && !usedHierarchy.contains(h)){
						usedHierarchy.add(h);
					}
				}
				
				for(String s : model.getOLAPCube().getMdx().getRows()){
					if (s.startsWith(h.getUniqueName()) && !usedHierarchy.contains(h)){
						usedHierarchy.add(h);
					}
				}
			}
		}
		
		
		
		for(MeasureGroup g : model.getOLAPCube().getMeasures()){
			for(Measure m : g.getMeasures()){
				for(String s : model.getOLAPCube().getMdx().getRows()){
					if (s.startsWith(m.getUniqueName()) && !measures.contains(m)){
						measures.add(m);
					}
				}
			}
		}
		
		xHieraViewer.setInput(usedHierarchy);
		xHieraViewer.setSelection(new StructuredSelection(usedHierarchy.get(0)));
		
		yHieraViewer.setInput(usedHierarchy);
		yHieraViewer.setSelection(new StructuredSelection(usedHierarchy.get(0)));
		scrollHieraViewer.setInput(usedHierarchy);
		measureViewer.setInput(measures);
		
		measureViewer.setSelection(new StructuredSelection(measures.get(0)));
	}
}
