package bpm.fd.design.ui.component.chart.pages;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import bpm.fd.api.core.model.components.definition.chart.DataAggregation;
import bpm.fd.api.core.model.components.definition.chart.IChartData;
import bpm.fd.api.core.model.components.definition.chart.MultiSerieChartData;
import bpm.fd.api.core.model.components.definition.chart.ChartOrderingType;
import bpm.fd.api.core.model.datas.ColumnDescriptor;
import bpm.fd.api.core.model.datas.DataSet;
import bpm.fd.api.core.model.datas.DataSource;
import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.component.Messages;
import bpm.fd.design.ui.component.chart.MultiSeriesTableViewer;
import bpm.fd.design.ui.viewer.comparators.LabelableComparator;
import bpm.fd.design.ui.viewer.contentproviders.ListContentProvider;
import bpm.fd.design.ui.viewer.labelprovider.DatasLabelProvider;
import bpm.fd.design.ui.wizard.OdaDataSetWizard;
import bpm.fd.design.ui.wizard.OdaDataSourceWizard;

public class MultiSeriesChartDatasPage extends ChartDatasPage{

	private ComboViewer serieCol;
	private MultiSeriesTableViewer aggregation;
	
	
	public MultiSeriesChartDatasPage(){
		this(PAGE_NAME);
		this.setTitle(PAGE_TITLE);
		this.setDescription(PAGE_DESCRIPTION);
	}
	
	protected MultiSeriesChartDatasPage(String pageName) {
		super(pageName);
	}
	
	
	
	private void createAggregationTable(Composite main){
		Label l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		
		aggregation = new MultiSeriesTableViewer(main, SWT.BORDER | SWT.FULL_SELECTION);
		aggregation.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 3));
		aggregation.getTable().setLinesVisible(true);
		aggregation.getTable().setHeaderVisible(true);
		
		
	}
	
	
	
	
	
	
	
	public void createControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(3, false));

		Label l = new Label(main, SWT.NONE);
		l.setText(Messages.MultiSeriesChartDatasPage_0);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		dataSourceViewer = new ComboViewer(main, SWT.READ_ONLY);
		dataSourceViewer.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		dataSourceViewer.setContentProvider(new ListContentProvider<DataSource>());
		dataSourceViewer.setLabelProvider(new DatasLabelProvider());
		dataSourceViewer.setComparator(new LabelableComparator());
		dataSourceViewer.addSelectionChangedListener(new SelectionChangedListener());
		dataSourceViewer.setInput(Activator.getDefault().getProject().getDictionary().getDatasources());
		
		
		Button createDataSource = new Button(main, SWT.PUSH);
		createDataSource.setText("..."); //$NON-NLS-1$
		createDataSource.setToolTipText(Messages.ChartDatasPage_9);
		createDataSource.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		createDataSource.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				try{
					OdaDataSourceWizard wiz = new OdaDataSourceWizard();
					
					WizardDialog dial = new WizardDialog(getShell(), wiz);
					if (dial.open() == Dialog.OK){
						dataSourceViewer.setInput(Activator.getDefault().getProject().getDictionary().getDatasources());
					}
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
		});
		
		Label l2 = new Label(main, SWT.NONE);
		l2.setText(Messages.MultiSeriesChartDatasPage_1);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		dataSetViewer = new ComboViewer(main, SWT.READ_ONLY);
		dataSetViewer.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		dataSetViewer.setContentProvider(new ListContentProvider<DataSet>());
		dataSetViewer.setLabelProvider(new DatasLabelProvider());
		dataSetViewer.setComparator(new LabelableComparator());
		dataSetViewer.addSelectionChangedListener(new SelectionChangedListener());

		Button createDataSet = new Button(main, SWT.PUSH);
		createDataSet.setText("..."); //$NON-NLS-1$
		createDataSet.setToolTipText(Messages.ChartDatasPage_12);
		createDataSet.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		createDataSet.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				try{
					OdaDataSetWizard wiz = new OdaDataSetWizard(null);
					
					WizardDialog dial = new WizardDialog(getShell(), wiz);
					if (dial.open() == Dialog.OK){
						dataSourceViewer.setSelection(dataSourceViewer.getSelection());
					}
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
		});
		
		
		l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.MultiSeriesChartDatasPage_2);
		
		
		categorieCol = new ComboViewer(main, SWT.READ_ONLY);
		categorieCol.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		categorieCol.setLabelProvider(new DatasLabelProvider());
		categorieCol.setContentProvider(new ListContentProvider<ColumnDescriptor>());
		categorieCol.setComparator(new LabelableComparator());
		categorieCol.getControl().setEnabled(false);
		categorieCol.addSelectionChangedListener(new SelectionChangedListener());
		
		l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.MultiSeriesChartDatasPage_3);
		
		
		categorieLabelCol = new ComboViewer(main, SWT.READ_ONLY);
		categorieLabelCol.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		categorieLabelCol.setLabelProvider(new DatasLabelProvider());
		categorieLabelCol.setContentProvider(new ListContentProvider<ColumnDescriptor>());
		categorieLabelCol.setComparator(new LabelableComparator());
		categorieLabelCol.getControl().setEnabled(false);
		categorieLabelCol.addSelectionChangedListener(new SelectionChangedListener());

		
		l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.MultiSeriesChartDatasPage_4);
		
		
		serieCol = new ComboViewer(main, SWT.READ_ONLY);
		serieCol.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		serieCol.setLabelProvider(new DatasLabelProvider(){

			@Override
			public String getText(Object element) {
				if (element instanceof String){
					return ((String)element);
				}
				return super.getText(element);
			}
			
		});
		serieCol.setContentProvider(new ListContentProvider<Object>());
		serieCol.setComparator(new LabelableComparator());
		serieCol.getControl().setEnabled(false);
		serieCol.addSelectionChangedListener(new SelectionChangedListener());
		
		l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.MultiSeriesChartDatasPage_5);
		
		
		orderCol = new ComboViewer(main, SWT.READ_ONLY);
		orderCol.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		orderCol.setLabelProvider(new LabelProvider());
		orderCol.setContentProvider(new ArrayContentProvider());
		orderCol.setInput(ChartOrderingType.values());
		orderCol.addSelectionChangedListener(new SelectionChangedListener());
		
		
		createAggregationTable(main);
		
			
		
		
		setControl(main);
		
	}
	
	public IChartData getChartDatas() throws Exception{
		MultiSerieChartData datas = null;

		datas = new MultiSerieChartData();
		DataSet ds = (DataSet) ((IStructuredSelection)dataSetViewer.getSelection()).getFirstElement();
		datas.setDataSet(ds);
		for(DataAggregation a : (List<DataAggregation>)aggregation.getInput()){
			datas.addAggregation(a);
		}
		
		
		datas.setCategorieFieldIndex(((ColumnDescriptor)((IStructuredSelection)categorieCol.getSelection()).getFirstElement()).getColumnIndex());
		datas.setCategorieFieldLabelIndex(((ColumnDescriptor)((IStructuredSelection)categorieLabelCol.getSelection()).getFirstElement()).getColumnIndex());
		
		if (((IStructuredSelection)serieCol.getSelection()).getFirstElement() instanceof ColumnDescriptor){
			datas.setSerieFieldIndex(((ColumnDescriptor)((IStructuredSelection)serieCol.getSelection()).getFirstElement()).getColumnIndex());
		}
		else{
			datas.setSerieFieldIndex(null);
		}
		
		datas.setOrderType((ChartOrderingType)((IStructuredSelection)orderCol.getSelection()).getFirstElement());


		
		
		return datas;
	}
	
	public class SelectionChangedListener implements ISelectionChangedListener{

		public void selectionChanged(SelectionChangedEvent event) {
			List<Object> serieInput = new ArrayList<Object>();
			serieInput.add("--- None ---"); //$NON-NLS-1$
			if (event.getSource() == dataSourceViewer){
				IStructuredSelection ss = (IStructuredSelection)dataSourceViewer.getSelection();
				DataSource ds = (DataSource)ss.getFirstElement();

				dataSetViewer.setInput(ds.getDataSet());
				
				
				categorieCol.setInput(new ArrayList<ColumnDescriptor>());
				categorieCol.getControl().setEnabled(false);
				categorieLabelCol.setInput(new ArrayList<ColumnDescriptor>());
				categorieLabelCol.getControl().setEnabled(false);
				
				
				serieCol.setInput(serieInput);
				serieCol.getControl().setEnabled(false);
				
				aggregation.setInput(new ArrayList<DataAggregation>());
			}
			else if (event.getSource() == dataSetViewer){
				IStructuredSelection ss = (IStructuredSelection)dataSetViewer.getSelection();
				DataSet ds = (DataSet)ss.getFirstElement();

			
				categorieCol.setInput(ds.getDataSetDescriptor().getColumnsDescriptors());
				categorieCol.getControl().setEnabled(true);
				
				categorieLabelCol.setInput(ds.getDataSetDescriptor().getColumnsDescriptors());
				categorieLabelCol.getControl().setEnabled(true);

				
				serieInput.addAll(ds.getDataSetDescriptor().getColumnsDescriptors());
				serieCol.setInput(serieInput);
				serieCol.getControl().setEnabled(true);
				
				aggregation.setInput(new ArrayList<DataAggregation>());
				aggregation.setDataSet(ds);
			}
			getContainer().updateButtons();
		}
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.WizardPage#isPageComplete()
	 */
	@Override
	public boolean isPageComplete() {
		try{
			boolean b = !(dataSourceViewer.getSelection().isEmpty()  || categorieCol.getSelection().isEmpty() || orderCol.getSelection().isEmpty()); 
			return b;	
		}catch(NullPointerException ex){
			return false;
		}
		 
	}
	
	
}
