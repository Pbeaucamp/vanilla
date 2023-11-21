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
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import bpm.fd.api.core.model.components.definition.chart.ChartData;
import bpm.fd.api.core.model.components.definition.chart.DataAggregation;
import bpm.fd.api.core.model.components.definition.chart.IChartData;
import bpm.fd.api.core.model.components.definition.chart.ChartOrderingType;
import bpm.fd.api.core.model.datas.ColumnDescriptor;
import bpm.fd.api.core.model.datas.DataSet;
import bpm.fd.api.core.model.datas.DataSource;
import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.component.Messages;
import bpm.fd.design.ui.viewer.comparators.LabelableComparator;
import bpm.fd.design.ui.viewer.contentproviders.ListContentProvider;
import bpm.fd.design.ui.viewer.labelprovider.DatasLabelProvider;
import bpm.fd.design.ui.wizard.OdaDataSetWizard;
import bpm.fd.design.ui.wizard.OdaDataSourceWizard;

public class ChartDatasPage extends WizardPage{
	public static final String PAGE_NAME = "bpm.fd.design.ui.component.chart.pages.ChartDatasPage"; //$NON-NLS-1$
	public static final String PAGE_TITLE = Messages.ChartDatasPage_1;
	public static final String PAGE_DESCRIPTION = Messages.ChartDatasPage_2;

	
	
	protected ComboViewer dataSourceViewer;
	protected ComboViewer dataSetViewer;
	protected ComboViewer categorieCol, categorieLabelCol, orderCol;
	private ComboViewer  valueCol;
	protected Combo aggregation;

	
	public ChartDatasPage(){
		this(PAGE_NAME);
		this.setTitle(PAGE_TITLE);
		this.setDescription(PAGE_DESCRIPTION);
	}
	
	protected ChartDatasPage(String pageName) {
		super(pageName);
	}
	
	
	
	
	public void createControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(3, false));

		Label l = new Label(main, SWT.NONE);
		l.setText(Messages.ChartDatasPage_7);
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
					getContainer().updateButtons();
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
		});
		
		Label l2 = new Label(main, SWT.NONE);
		l2.setText(Messages.ChartDatasPage_10);
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
		l.setText(Messages.ChartDatasPage_13);
		
		
		valueCol = new ComboViewer(main, SWT.READ_ONLY);
		valueCol.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		valueCol.setLabelProvider(new DatasLabelProvider());
		valueCol.setContentProvider(new ListContentProvider<ColumnDescriptor>());
		valueCol.setComparator(new LabelableComparator());
		valueCol.addSelectionChangedListener(new SelectionChangedListener());

		valueCol.getControl().setEnabled(false);
		
		l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.ChartDatasPage_14);
		
		
		categorieCol = new ComboViewer(main, SWT.READ_ONLY);
		categorieCol.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		categorieCol.setLabelProvider(new DatasLabelProvider());
		categorieCol.setContentProvider(new ListContentProvider<ColumnDescriptor>());
		categorieCol.setComparator(new LabelableComparator());
		categorieCol.getControl().setEnabled(false);
		categorieCol.addSelectionChangedListener(new SelectionChangedListener());
		
		l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.ChartDatasPage_15);
		
		
		categorieLabelCol = new ComboViewer(main, SWT.READ_ONLY);
		categorieLabelCol.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		categorieLabelCol.setLabelProvider(new DatasLabelProvider());
		categorieLabelCol.setContentProvider(new ListContentProvider<ColumnDescriptor>());
		categorieLabelCol.setComparator(new LabelableComparator());
		categorieLabelCol.getControl().setEnabled(false);
		categorieLabelCol.addSelectionChangedListener(new SelectionChangedListener());
		
		l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.ChartDatasPage_16);
		
		
		orderCol = new ComboViewer(main, SWT.READ_ONLY);
		orderCol.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		orderCol.setLabelProvider(new LabelProvider());
		orderCol.setContentProvider(new ArrayContentProvider());
		orderCol.setInput(ChartOrderingType.values());
		orderCol.addSelectionChangedListener(new SelectionChangedListener());
		
		
		l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.ChartDatasPage_17);
		
		aggregation = new Combo(main, SWT.READ_ONLY);
		aggregation.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		aggregation.setItems(DataAggregation.AGGREGATORS_NAME);
		aggregation.addSelectionListener(new SelectionAdapter(){

			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				getContainer().updateButtons();
			}
			
		});
		
		
		
		
		setControl(main);
		
	}
	
	public IChartData getChartDatas() throws Exception{
		ChartData datas = null;
	
		datas = new ChartData();
		DataSet ds = (DataSet) ((IStructuredSelection)dataSetViewer.getSelection()).getFirstElement();
		datas.setDataSet(ds);
		datas.setAggregator(aggregation.getSelectionIndex());
		
		datas.setGroupFieldIndex(((ColumnDescriptor)((IStructuredSelection)categorieCol.getSelection()).getFirstElement()).getColumnIndex());
		datas.setCategorieFieldLabelIndex(((ColumnDescriptor)((IStructuredSelection)categorieLabelCol.getSelection()).getFirstElement()).getColumnIndex());
		datas.setValueFieldIndex(((ColumnDescriptor)((IStructuredSelection)valueCol.getSelection()).getFirstElement()).getColumnIndex());
		datas.setOrderType((ChartOrderingType)((IStructuredSelection)orderCol.getSelection()).getFirstElement());

	
		
		
		return datas;
	}
	
	public class SelectionChangedListener implements ISelectionChangedListener{

		public void selectionChanged(SelectionChangedEvent event) {
			if (event.getSource() == dataSourceViewer){
				IStructuredSelection ss = (IStructuredSelection)dataSourceViewer.getSelection();
				DataSource ds = (DataSource)ss.getFirstElement();

				dataSetViewer.setInput(ds.getDataSet());
				valueCol.setInput(new ArrayList<ColumnDescriptor>());
				valueCol.getControl().setEnabled(false);
				
				categorieCol.setInput(new ArrayList<ColumnDescriptor>());
				categorieCol.getControl().setEnabled(false);
				
				categorieLabelCol.setInput(new ArrayList<ColumnDescriptor>());
				categorieLabelCol.getControl().setEnabled(false);
			}
			else if (event.getSource() == dataSetViewer){
				IStructuredSelection ss = (IStructuredSelection)dataSetViewer.getSelection();
				
				if (ss.isEmpty()){
					List<ColumnDescriptor> l = new ArrayList<ColumnDescriptor>();
					valueCol.setInput(l);
					categorieCol.setInput(l);
					categorieLabelCol.setInput(l);
					return;

				}
				DataSet ds = (DataSet)ss.getFirstElement();

				valueCol.setInput(ds.getDataSetDescriptor().getColumnsDescriptors());
				categorieCol.setInput(ds.getDataSetDescriptor().getColumnsDescriptors());
				categorieLabelCol.setInput(ds.getDataSetDescriptor().getColumnsDescriptors());
				valueCol.getControl().setEnabled(true);
				categorieCol.getControl().setEnabled(true);
				categorieLabelCol.getControl().setEnabled(true);
			
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
			 return !(dataSourceViewer.getSelection().isEmpty() || valueCol.getSelection().isEmpty() || categorieCol.getSelection().isEmpty() || orderCol.getSelection().isEmpty()); 
		 }catch(NullPointerException ex){
			 return false;
		 }
		
	}
	
	
}
