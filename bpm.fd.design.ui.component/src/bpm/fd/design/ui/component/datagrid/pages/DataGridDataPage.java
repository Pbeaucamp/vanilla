package bpm.fd.design.ui.component.datagrid.pages;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
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

import bpm.fd.api.core.model.components.definition.datagrid.DataGridData;
import bpm.fd.api.core.model.datas.DataSet;
import bpm.fd.api.core.model.datas.DataSource;
import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.component.Messages;
import bpm.fd.design.ui.viewer.comparators.LabelableComparator;
import bpm.fd.design.ui.viewer.contentproviders.ListContentProvider;
import bpm.fd.design.ui.viewer.labelprovider.DatasLabelProvider;
import bpm.fd.design.ui.wizard.OdaDataSetWizard;
import bpm.fd.design.ui.wizard.OdaDataSourceWizard;

public class DataGridDataPage extends WizardPage{

	public static final String PAGE_NAME = "bpm.fd.design.ui.component.datagrid.pages.DataGridDataPage"; //$NON-NLS-1$
	public static final String PAGE_TITLE = Messages.DataGridDataPage_1;
	public static final String PAGE_DESCRIPTION = Messages.DataGridDataPage_2;

	
	private ComboViewer dataSourceViewer;
	private ComboViewer dataSetViewer;
	
	public DataGridDataPage(){
		this(PAGE_NAME);
		this.setTitle(PAGE_TITLE);
		this.setDescription(PAGE_DESCRIPTION);
	}
	
	protected DataGridDataPage(String pageName) {
		super(pageName);
	}
	
	public void createControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(3, false));
		
		
		Label l = new Label(main, SWT.NONE);
		l.setText(Messages.DataGridDataPage_3);
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
		createDataSource.setToolTipText(Messages.DataGridDataPage_5);
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
		l2.setText(Messages.DataGridDataPage_6);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		dataSetViewer = new ComboViewer(main, SWT.READ_ONLY);
		dataSetViewer.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		dataSetViewer.setContentProvider(new ListContentProvider<DataSet>());
		dataSetViewer.setLabelProvider(new DatasLabelProvider());
		dataSetViewer.setComparator(new LabelableComparator());
		dataSetViewer.addSelectionChangedListener(new SelectionChangedListener());
		
		Button createDataSet = new Button(main, SWT.PUSH);
		createDataSet.setText("..."); //$NON-NLS-1$
		createDataSet.setToolTipText(Messages.DataGridDataPage_8);
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
		
		setControl(main);
	}
	
	
	public DataGridData getDataGridDatas() {
		if (dataSetViewer.getSelection().isEmpty()){
			return null;
		}
		
		DataGridData datas = new DataGridData();
		datas.setDataSet((DataSet)((IStructuredSelection)dataSetViewer.getSelection()).getFirstElement());
		return datas;
	}
	
	public class SelectionChangedListener implements ISelectionChangedListener{

		public void selectionChanged(SelectionChangedEvent event) {
			if (event.getSource() == dataSourceViewer){
				IStructuredSelection ss = (IStructuredSelection)dataSourceViewer.getSelection();
				DataSource ds = (DataSource)ss.getFirstElement();
				
				dataSetViewer.setInput(ds.getDataSet());
				
			}
			getContainer().updateButtons();
			
		}
		
	}


	
}
