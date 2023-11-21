package bpm.fasd.ui.measure.definition.dialog;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.fasd.olap.FAModel;
import org.fasd.olap.OLAPDimension;
import org.fasd.olap.OLAPHierarchy;
import org.fasd.olap.OLAPLevel;
import org.fasd.olap.OLAPSchema;
import org.fasd.olap.OlapDynamicMeasure;
import org.fasd.olap.aggregation.CalculatedAggregation;
import org.fasd.olap.aggregation.ClassicAggregation;
import org.fasd.olap.aggregation.IMeasureAggregation;
import org.fasd.olap.aggregation.LastAggregation;

import bpm.fasd.ui.measure.definition.Messages;

public class DynamicMeasureDialog extends Dialog {

	private OlapDynamicMeasure measure;
	private FAModel model;
	
	public DynamicMeasureDialog(Shell parentShell, OlapDynamicMeasure mes, FAModel model) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.measure = mes;
		this.model = model;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		
		Composite mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayout(new GridLayout());
		mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Label lblTitle = new Label(mainComposite, SWT.NONE);
		lblTitle.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		lblTitle.setText(Messages.DynamicMeasureDialog_0);
		
		TableViewer viewer = new TableViewer(mainComposite);
		viewer.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		viewer.getTable().setHeaderVisible(true);
		
		String[] columns = createColumnsNames();
		
		for(String element : columns) {
	        TableColumn col = new TableColumn(viewer.getTable(), SWT.NONE);
	        col.setText(element);
	    }
		
		TableLayout tlayout = new TableLayout();
		
		tlayout.addColumnData(new ColumnPixelData(200, true));
		tlayout.addColumnData(new ColumnPixelData(100, true));
		tlayout.addColumnData(new ColumnPixelData(100, true));
		tlayout.addColumnData(new ColumnPixelData(200, true));
		tlayout.addColumnData(new ColumnPixelData(100, true));
		tlayout.addColumnData(new ColumnPixelData(200, true));
		viewer.getTable().setLayout(tlayout);
		
		viewer.setContentProvider(new ArrayContentProvider());
		viewer.setLabelProvider(new ITableLabelProvider() {
			@Override
			public void removeListener(ILabelProviderListener listener) {
				
				
			}
			
			@Override
			public boolean isLabelProperty(Object element, String property) {
				
				return false;
			}
			
			@Override
			public void dispose() {
				
				
			}
			
			@Override
			public void addListener(ILabelProviderListener listener) {
				
				
			}
			
			@Override
			public String getColumnText(Object element, int columnIndex) {
				String result = null;
				IMeasureAggregation aggreg = (IMeasureAggregation) element;
				result = findColumnText(aggreg, columnIndex);
				return result;
			}
			
			@Override
			public Image getColumnImage(Object element, int columnIndex) {
				
				return null;
			}
		});
		
		IMeasureAggregation[] aggregations = createAggregations();
		
		viewer.setInput(aggregations);
		viewer.refresh();
		
		return mainComposite;
	}

	protected String findColumnText(IMeasureAggregation aggreg, int columnIndex) {
		String res = ""; //$NON-NLS-1$
		switch(columnIndex) {
			case 0:
				return aggreg.getLevel();
			case 1:
				if(aggreg instanceof ClassicAggregation) {
					if(((ClassicAggregation)aggreg).getAggregator().equals(Messages.DynamicMeasureDialog_1)) {
						return Messages.DynamicMeasureDialog_2;
					}
				}
				return aggreg.getClass().getSimpleName().replace("Aggregation", ""); //$NON-NLS-1$ //$NON-NLS-2$
			case 2:
				if(aggreg instanceof ClassicAggregation) {
					if(((ClassicAggregation)aggreg).getAggregator().equals(Messages.DynamicMeasureDialog_6)) {
						return ""; //$NON-NLS-1$
					}
					return ((ClassicAggregation)aggreg).getAggregator();
				}
				else if(aggreg instanceof LastAggregation) {
					return ((LastAggregation)aggreg).getAggregator();
				}
				break;
			case 3:
				if(aggreg instanceof ClassicAggregation) {
					if(((ClassicAggregation)aggreg).getAggregator().equals(Messages.DynamicMeasureDialog_8)) {
						return ""; //$NON-NLS-1$
					}
					return ((ClassicAggregation)aggreg).getOrigin().getFullName();
				}
				else if(aggreg instanceof LastAggregation) {
					return ((LastAggregation)aggreg).getOrigin().getFullName();
				}
				break;
			case 4:
				if(aggreg instanceof LastAggregation) {
					return ((LastAggregation)aggreg).getRelatedDimension();
				}
				break;
			case 5:
				if(aggreg instanceof CalculatedAggregation) {
					return ((CalculatedAggregation)aggreg).getFormula();
				}
				break;
		}
		return res;
	}

	private IMeasureAggregation[] createAggregations() {
		OLAPSchema schema = model.getOLAPSchema();
		List<IMeasureAggregation> aggs = new ArrayList<IMeasureAggregation>();
		
		if(measure.getType().equals("calculated")) { //$NON-NLS-1$
			CalculatedAggregation agg = new CalculatedAggregation();
			agg.setFormula(measure.getFormula());
			agg.setLevel("Default"); //$NON-NLS-1$
			aggs.add(agg);
		}
		else {
			if(measure.getLastTimeDimensionName() != null && !measure.getLastTimeDimensionName().equals("")) { //$NON-NLS-1$
				LastAggregation agg = new LastAggregation();
				agg.setAggregator(measure.getAggregator());
				agg.setLevel("Default"); //$NON-NLS-1$
				agg.setRelatedDimension(measure.getLastTimeDimensionName());
				agg.setOrigin(measure.getOrigin());
				aggs.add(agg);
			}
			else {
				ClassicAggregation agg = new ClassicAggregation();
				agg.setAggregator(measure.getAggregator());
				agg.setLevel("Default"); //$NON-NLS-1$
				agg.setOrigin(measure.getOrigin());
				aggs.add(agg);
			}
		}
		
		for(OLAPDimension dim : schema.getDimensions()) {
			for(OLAPHierarchy hiera : dim.getHierarchies()) {
				boolean finded = false;
				String lvlUname = ""; //$NON-NLS-1$
				for(OLAPLevel lvl : hiera.getLevels()) {
					
					lvlUname = "["+lvl.getParent().getParent().getName()+"."+lvl.getParent().getName()+"].[" + lvl.getName() +"]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
					
					if(measure.getAggregations() != null) {
						for(IMeasureAggregation aggreg : measure.getAggregations()) {
							if(aggreg.getLevel().equals(lvlUname)) {
								aggs.add(aggreg);
								finded = true;
							}
						}
					}
					
				}
				if(!finded) {
					IMeasureAggregation agg = new ClassicAggregation();
					agg.setLevel(lvlUname);
					((ClassicAggregation)agg).setAggregator(Messages.DynamicMeasureDialog_20);
					aggs.add(agg);
				}
			}
		}
		
		IMeasureAggregation[] aggss = new IMeasureAggregation[aggs.size()];
		
		for(int i = 0; i < aggs.size() ; i++) {
			aggss[i] = aggs.get(i);
		}
		
		return aggss;
	}

	private String[] createColumnsNames() {
		return new String[]{Messages.DynamicMeasureDialog_21,Messages.DynamicMeasureDialog_22,Messages.DynamicMeasureDialog_23,Messages.DynamicMeasureDialog_24,Messages.DynamicMeasureDialog_25,Messages.DynamicMeasureDialog_26};
	}

	@Override
	protected void initializeBounds() {
		getShell().setSize(950, 500);
		super.initializeBounds();
	}
	
}
