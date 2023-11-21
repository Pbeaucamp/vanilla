package bpm.fa.ui.composite;

import java.util.ArrayList;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.experimental.chart.swt.ChartComposite;

import bpm.fa.api.item.Item;
import bpm.fa.api.item.ItemValue;
import bpm.fa.api.olap.OLAPResult;
import bpm.fa.ui.Messages;

public class CompositeChart {
	
	private OLAPResult result;
	private ComboViewer group;
	private ChartComposite chart;
		
	
	public Control createComposite(Composite parent){
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, false));
		
		Label l = new Label(main, SWT.NONE);
		l.setText(Messages.CompositeChart_0);
		l.setLayoutData(new GridData());
		
		group = new ComboViewer(main, SWT.READ_ONLY);
		group.getControl().setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		group.setContentProvider(new ArrayContentProvider());
		group.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				return ((Item)element).getLabel();
			}
		});
		group.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (group.getSelection().isEmpty()){
					chart.setVisible(false);
					return;
				}
				createChart();
				chart.setVisible(true);
			}
		});
		
		
		
		chart = new ChartComposite(main, SWT.NONE);
		chart.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		chart.setVisible(false);
//		chart.setRangeZoomable(true);
//		chart.setDomainZoomable(true);
		
		
		
		return main;
		
	}
	
	public void setOlapResult(OLAPResult result){
		this.result = result;
		group.setInput(new ArrayList(result.getRaw().get(0).subList(0, result.getRaw().get(0).size() - 1)));
	}
	
	private void createChart(){
//		chart.setRangeZoomable(true);
		Item i = (Item)((IStructuredSelection)group.getSelection()).getFirstElement();
		JFreeChart chart = ChartFactory.createPieChart3D(i.getLabel(), createPieDataSet(i), true, true, false);
		
		this.chart.setChart(chart);
		this.chart.getChart().fireChartChanged();
	}
	
	private PieDataset createPieDataSet(Item groupItem){
		
		int groupIndex = result.getRaw().get(0).indexOf(groupItem);
		DefaultPieDataset ds = new DefaultPieDataset();
		
		boolean first = true;
		for(ArrayList<Item> row : result.getRaw()){
			if (first){first = false; continue;}
			
			
			Double val = null;
			
			try{
				val = (Double)ds.getValue(row.get(groupIndex).getLabel());
				ds.setValue(row.get(groupIndex).getLabel(), Double.parseDouble(((ItemValue)row.get(row.size() - 1)).getLabel()) + val);
			}catch(Exception ex){
				try{
					ds.setValue(row.get(groupIndex).getLabel(), Double.parseDouble(((ItemValue)row.get(row.size() - 1)).getLabel()));
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		}
		
		return ds;
		
	}
	
}
