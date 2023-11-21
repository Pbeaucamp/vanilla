package bpm.fd.design.ui.component.filter.pages;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import bpm.fd.api.core.model.components.definition.OrderingType;
import bpm.fd.api.core.model.components.definition.filter.FilterData;
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

public class FilterDatasPage extends WizardPage{
	public static final String PAGE_NAME = "bpm.fd.design.ui.component.filter.pages.FilterDatasPage"; //$NON-NLS-1$
	public static final String PAGE_TITLE = Messages.FilterDatasPage_1;
	public static final String PAGE_DESCRIPTION = Messages.FilterDatasPage_2;

	
	
	private ComboViewer dataSourceViewer;
	private ComboViewer dataSetViewer;
	private ComboViewer labelViewer;
	private ComboViewer valueViewer;
	private ComboViewer orderViewer, orderType;
	
	public FilterDatasPage(){
		this(PAGE_NAME);
		this.setTitle(PAGE_TITLE);
		this.setDescription(PAGE_DESCRIPTION);
	}
	
	protected FilterDatasPage(String pageName) {
		super(pageName);
	}

	public void createControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(3, false));
		
		Label l = new Label(main, SWT.NONE);
		l.setText(Messages.FilterDatasPage_3);
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
		createDataSource.setToolTipText(Messages.FilterDatasPage_5);
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
		l2.setText(Messages.FilterDatasPage_6);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		dataSetViewer = new ComboViewer(main, SWT.READ_ONLY);
		dataSetViewer.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		dataSetViewer.setContentProvider(new ListContentProvider<DataSet>());
		dataSetViewer.setLabelProvider(new DatasLabelProvider());
		dataSetViewer.setComparator(new LabelableComparator());
		dataSetViewer.addSelectionChangedListener(new SelectionChangedListener());
		
		Button createDataSet = new Button(main, SWT.PUSH);
		createDataSet.setText("..."); //$NON-NLS-1$
		createDataSet.setToolTipText(Messages.FilterDatasPage_8);
		createDataSet.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		createDataSet.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				try{
					OdaDataSetWizard wiz = new OdaDataSetWizard(null);
					
					WizardDialog dial = new WizardDialog(getShell(), wiz);
					if (dial.open() == Dialog.OK){
						dataSetViewer.setSelection(StructuredSelection.EMPTY);
					}
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
		});
		
		Label l3 = new Label(main, SWT.NONE);
		l3.setText(Messages.FilterDatasPage_9);
		l3.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		labelViewer = new ComboViewer(main, SWT.READ_ONLY);
		labelViewer.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		labelViewer.setContentProvider(new ListContentProvider<ColumnDescriptor>());
		labelViewer.setLabelProvider(new DatasLabelProvider());
		labelViewer.setComparator(new LabelableComparator());
		labelViewer.addSelectionChangedListener(new SelectionChangedListener());
		
		Label l4 = new Label(main, SWT.NONE);
		l4.setText(Messages.FilterDatasPage_10);
		l4.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		valueViewer = new ComboViewer(main, SWT.READ_ONLY);
		valueViewer.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		valueViewer.setContentProvider(new ListContentProvider<ColumnDescriptor>());
		valueViewer.setLabelProvider(new DatasLabelProvider());
		valueViewer.setComparator(new LabelableComparator());
		valueViewer.addSelectionChangedListener(new SelectionChangedListener());
		
		
		l4 = new Label(main, SWT.NONE);
		l4.setText(Messages.FilterDatasPage_11);
		l4.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		orderViewer = new ComboViewer(main, SWT.READ_ONLY);
		orderViewer.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		orderViewer.setContentProvider(new ListContentProvider<ColumnDescriptor>());
		orderViewer.setLabelProvider(new DatasLabelProvider());
		orderViewer.setComparator(new LabelableComparator());
		orderViewer.addSelectionChangedListener(new SelectionChangedListener());

		l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.ChartDatasPage_16);
		
		
		orderType = new ComboViewer(main, SWT.READ_ONLY);
		orderType.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		orderType.setLabelProvider(new LabelProvider());
		orderType.setContentProvider(new ArrayContentProvider());
		orderType.setInput(OrderingType.values());
		orderType.addSelectionChangedListener(new SelectionChangedListener());
		
		setControl(main);
	}
	
	

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.WizardPage#isPageComplete()
	 */
	@Override
	public boolean isPageComplete() {
		return ! (valueViewer.getSelection().isEmpty() && labelViewer.getSelection().isEmpty());
	}



	public class SelectionChangedListener implements ISelectionChangedListener{

		public void selectionChanged(SelectionChangedEvent event) {
			if (event.getSource() == dataSourceViewer){
				IStructuredSelection ss = (IStructuredSelection)dataSourceViewer.getSelection();
				DataSource ds = (DataSource)ss.getFirstElement();
				
				dataSetViewer.setInput(ds.getDataSet());
				labelViewer.setInput(new ArrayList<ColumnDescriptor>());
				labelViewer.getControl().setEnabled(false);
				valueViewer.setInput(new ArrayList<ColumnDescriptor>());
				valueViewer.getControl().setEnabled(false);
				orderViewer.setInput(new ArrayList<ColumnDescriptor>());
				orderViewer.getControl().setEnabled(false);
			}
			else if (event.getSource() == dataSetViewer){
				IStructuredSelection ss = (IStructuredSelection)dataSetViewer.getSelection();
				if (ss.isEmpty()){
					List<ColumnDescriptor> l = new ArrayList<ColumnDescriptor>();
					labelViewer.setInput(l);
					valueViewer.setInput(l);
					orderViewer.setInput(l);
					return;

				}
				
				DataSet ds = (DataSet)ss.getFirstElement();

				labelViewer.setInput(ds.getDataSetDescriptor().getColumnsDescriptors());
				valueViewer.setInput(ds.getDataSetDescriptor().getColumnsDescriptors());
				orderViewer.setInput(ds.getDataSetDescriptor().getColumnsDescriptors());
				labelViewer.getControl().setEnabled(true);
				valueViewer.getControl().setEnabled(true);
				orderViewer.getControl().setEnabled(true);
			}
			else{
				getContainer().updateButtons();
			}
			
			
		}
		
	}



	public FilterData getFilterDatas() {
		FilterData datas = new FilterData();
		datas.setDataSet((DataSet)((IStructuredSelection)dataSetViewer.getSelection()).getFirstElement());
		datas.setColumnLabelIndex((((ColumnDescriptor)((IStructuredSelection)labelViewer.getSelection()).getFirstElement()).getColumnIndex()));
		datas.setColumnValueIndex((((ColumnDescriptor)((IStructuredSelection)valueViewer.getSelection()).getFirstElement()).getColumnIndex()));
		datas.setColumnOrderIndex((((ColumnDescriptor)((IStructuredSelection)orderViewer.getSelection()).getFirstElement()).getColumnIndex()));
		datas.setOrderType((OrderingType)((IStructuredSelection)orderType.getSelection()).getFirstElement());
		return datas;
	}
	
}
