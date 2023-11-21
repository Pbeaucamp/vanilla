package bpm.fd.design.ui.component.gauge.pages;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.fd.api.core.model.components.definition.IComponentDatas;
import bpm.fd.api.core.model.components.definition.gauge.ComplexGaugeDatas;
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

public class CompositeFmGaugeDatas extends AbstractCompositeGaugeDatas{

	private ComboViewer  dataSource;
	private ComboViewer  dataSet;
	
	
	private ComboViewer  value;
	
	private Button bMinMax;
	private ComboViewer  min;
	private Text minTxt;
	private ComboViewer  max;
	private Text maxTxt;
	
	
	private Button bTarget;
	private Text  target;
	private Text  tolerancePerc;
	
	private Button bTolerance;
	private ComboViewer minTolerated;
	private ComboViewer maxTolerated;
	
	
	private ComplexGaugeDatas datas;
	
	private ISelectionChangedListener lst = new ISelectionChangedListener() {
		
		public void selectionChanged(SelectionChangedEvent event) {
			notifyListeners(SWT.Modify, new Event());
			
		}
	};
	
	private class BtnListener extends SelectionAdapter{
		private Control[] controled;
		public BtnListener(Control[] controled){
			this.controled = controled;
		}
		@Override
		public void widgetSelected(SelectionEvent e) {
			for(Control c : controled){
				c.setEnabled(!c.getEnabled());
			}
			notifyListeners(SWT.Modify, new Event());
		}
	}
	
	private class TxtListener implements ModifyListener{
		
		@Override
		public void modifyText(ModifyEvent e) {
			notifyListeners(SWT.Modify, new Event());
			
		}
	}
	
	
	public CompositeFmGaugeDatas(Composite parent, int style) {
		super(parent, style);
		buildContent();
	}

	@Override
	public IComponentDatas getDatas() throws Exception {
		if (datas == null){
			datas = new ComplexGaugeDatas();
		}
		
		datas.setDataSet((DataSet)((IStructuredSelection)dataSet.getSelection()).getFirstElement());
		datas.setManualRange(bMinMax.getSelection());
		if (!max.getSelection().isEmpty()){
			datas.setIndexMax(((ColumnDescriptor)((IStructuredSelection)max.getSelection()).getFirstElement()).getColumnIndex());
		}
		if (!min.getSelection().isEmpty()){
			datas.setIndexMin(((ColumnDescriptor)((IStructuredSelection)min.getSelection()).getFirstElement()).getColumnIndex());
		}
		
		try{
			datas.setMaxValue(Float.valueOf(maxTxt.getText()));
		}catch(Exception ex){
			
		}
		try{
			datas.setMinValue(Float.valueOf(minTxt.getText()));
		}catch(Exception ex){
			
		}
		
		datas.setUseTarget(bTarget.getSelection());
		
		try{
			datas.setTargetValue(Float.valueOf(target.getText()));
		}catch(Exception ex){
			datas.setTargetValue(null);
		}
		
		try{
			datas.setIndexTolerance(Integer.parseInt(tolerancePerc.getText()));
		}catch(Exception ex){
			datas.setIndexTolerance(0);
		}
		
		
		
		datas.setUseExpected(bTolerance.getSelection());
		if (!maxTolerated.getSelection().isEmpty()){
			datas.setIndexMaxSeuil(((ColumnDescriptor)((IStructuredSelection)maxTolerated.getSelection()).getFirstElement()).getColumnIndex());
			datas.setIndexMinSeuil(((ColumnDescriptor)((IStructuredSelection)minTolerated.getSelection()).getFirstElement()).getColumnIndex());
		}
		if (!minTolerated.getSelection().isEmpty()){
			datas.setIndexMinSeuil(((ColumnDescriptor)((IStructuredSelection)minTolerated.getSelection()).getFirstElement()).getColumnIndex());
		}
		
		if (!value.getSelection().isEmpty()){
			datas.setIndexValue(((ColumnDescriptor)((IStructuredSelection)value.getSelection()).getFirstElement()).getColumnIndex());
		}
		
		return datas;

	}

	@Override
	public boolean isComplete() {
		boolean b = true;
		
		if (bTarget.getSelection()){
			b = b && !tolerancePerc.getText().isEmpty();
			b = b && !target.getText().isEmpty();
		}
		if (bTolerance.getSelection()){
			b = b && !maxTolerated.getSelection().isEmpty();
			b = b && !minTolerated.getSelection().isEmpty();
		}
		
		if (bMinMax.getSelection()){
			try{
				Float.parseFloat(maxTxt.getText());
			}catch(Exception ex){
				b = false;
			}
			
			try{
				Float.parseFloat(minTxt.getText());
			}catch(Exception ex){
				b = false;
			}
			
		}
		else{
			b = b && !max.getSelection().isEmpty();
			b = b && !min.getSelection().isEmpty();
		}
	
	
		
		
		
		b = b && !value.getSelection().isEmpty();
		return b;
	}
	
	private void buildContent(){
		setLayout(new GridLayout(3, false));
		
		Label l = new Label(this, SWT.NONE);
		l.setText(Messages.CompositeFmGaugeDatas_0);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		dataSource = new ComboViewer(this, SWT.READ_ONLY);
		dataSource.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		dataSource.setContentProvider(new ListContentProvider<DataSource>());
		dataSource.setLabelProvider(new DatasLabelProvider());
		dataSource.setComparator(new LabelableComparator());
		dataSource.addSelectionChangedListener(new ISelectionChangedListener(){

			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection ss = (IStructuredSelection)dataSource.getSelection();
				DataSource ds = (DataSource)ss.getFirstElement();
				
				dataSet.setInput(ds.getDataSet());
				
			}
			
		});
		dataSource.setInput(Activator.getDefault().getProject().getDictionary().getDatasources());

		
		Button b = new Button(this, SWT.PUSH);
		b.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		b.setText("..."); //$NON-NLS-1$
		b.setToolTipText(Messages.CompositeFmGaugeDatas_2);
		b.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try{
					OdaDataSourceWizard wiz = new OdaDataSourceWizard();
					
					WizardDialog dial = new WizardDialog(getShell(), wiz);
					if (dial.open() == Dialog.OK){
						dataSource.setInput(Activator.getDefault().getProject().getDictionary().getDatasources());
					}
				}catch(Exception ex){
					ex.printStackTrace();
				}
			
			}
		});
		
		l = new Label(this, SWT.NONE);
		l.setText(Messages.CompositeFmGaugeDatas_3);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		dataSet = new ComboViewer(this, SWT.READ_ONLY);
		dataSet.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		dataSet.setLabelProvider(new DatasLabelProvider());
		dataSet.setContentProvider(new ListContentProvider<DataSet>());
		dataSet.setComparator(new LabelableComparator());
		dataSet.addSelectionChangedListener(new ISelectionChangedListener() {
			
			public void selectionChanged(SelectionChangedEvent event) {
				DataSet ds = (DataSet)((IStructuredSelection)event.getSelection()).getFirstElement();
				List<ColumnDescriptor> l = ds.getDataSetDescriptor().getColumnsDescriptors();
				
				min.setInput(l);
				minTolerated.setInput(l);
				maxTolerated.setInput(l);
				max.setInput(l);
				value.setInput(l);
			
			}
		});

		b = new Button(this, SWT.PUSH);
		b.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		b.setText("..."); //$NON-NLS-1$
		b.setToolTipText(Messages.CompositeFmGaugeDatas_5);
		b.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try{
					OdaDataSetWizard wiz = new OdaDataSetWizard(null);
					
					WizardDialog dial = new WizardDialog(getShell(), wiz);
					if (dial.open() == Dialog.OK){
						dataSource.setSelection(dataSource.getSelection());
					}
				}catch(Exception ex){
					ex.printStackTrace();
				}
			
			}
		});
		
		/*
		 * minmax
		 */
		bMinMax = new Button(this, SWT.CHECK);
		bMinMax.setText("User Defined Minimum/Maximum");
		bMinMax.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 3, 1));
		
		l = new Label(this, SWT.NONE);
		l.setText(Messages.CompositeFmGaugeDatas_6);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 2));

		minTxt = new Text(this, SWT.BORDER);
		minTxt.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1));
		minTxt.setEnabled(false);
		minTxt.addModifyListener(new TxtListener());
		
		min = new ComboViewer(this, SWT.READ_ONLY);
		min.getControl().setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1));
		min.addSelectionChangedListener(lst);
		min.setLabelProvider(new DatasLabelProvider());
		min.setContentProvider(new ListContentProvider<ColumnDescriptor>());
		min.setComparator(new LabelableComparator());
		

		l = new Label(this, SWT.NONE);
		l.setText(Messages.CompositeFmGaugeDatas_10);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 1, 2 ));

			
		maxTxt = new Text(this, SWT.BORDER);
		maxTxt.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1));
		maxTxt.setEnabled(false);
		maxTxt.addModifyListener(new TxtListener());

		max = new ComboViewer(this, SWT.READ_ONLY);
		max.getControl().setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1));
		max.addSelectionChangedListener(lst);
		max.setLabelProvider(new DatasLabelProvider());
		max.setContentProvider(new ListContentProvider<ColumnDescriptor>());
		max.setComparator(new LabelableComparator());
		
		bMinMax.addSelectionListener(new BtnListener(new Control[]{min.getControl(), minTxt, max.getControl(), maxTxt}));
		
		/*
		 * expectation
		 */
		bTolerance = new Button(this, SWT.CHECK);
		bTolerance.setText("Use Expectation Fields");
		bTolerance.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 3, 1));
		bTolerance.setSelection(true);
		
		l = new Label(this, SWT.NONE);
		l.setText(Messages.CompositeFmGaugeDatas_7);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));

		
		minTolerated = new ComboViewer(this, SWT.READ_ONLY);
		minTolerated.getControl().setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1));
		minTolerated.addSelectionChangedListener(lst);
		minTolerated.setLabelProvider(new DatasLabelProvider());
		minTolerated.setContentProvider(new ListContentProvider<ColumnDescriptor>());
		minTolerated.setComparator(new LabelableComparator());
		
		
		l = new Label(this, SWT.NONE);
		l.setText(Messages.CompositeFmGaugeDatas_9);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		maxTolerated = new ComboViewer(this, SWT.READ_ONLY);
		maxTolerated.getControl().setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1));
		maxTolerated.addSelectionChangedListener(lst);
		maxTolerated.setLabelProvider(new DatasLabelProvider());
		maxTolerated.setContentProvider(new ListContentProvider<ColumnDescriptor>());
		maxTolerated.setComparator(new LabelableComparator());
		
		
		bTolerance.addSelectionListener(new BtnListener(new Control[]{maxTolerated.getControl(), minTolerated.getControl()}));
		
		/*
		 * target
		 */
		bTarget = new Button(this, SWT.CHECK);
		bTarget.setText("Use Target");
		bTarget.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 3, 1));
		bTarget.setSelection(true);
		
		l = new Label(this, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.CompositeFmGaugeDatas_8);
		
		
		target = new Text(this, SWT.BORDER);
		target.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1));


		l = new Label(this, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.CompositeFmGaugeDatas_11);
		
		
		tolerancePerc = new Text(this, SWT.BORDER);
		tolerancePerc.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1));
		tolerancePerc.addModifyListener(new TxtListener());


		
		bTarget.addSelectionListener(new BtnListener(new Control[]{tolerancePerc, target}));
		

		
		
		/*
		 * value
		 */
		l = new Label(this, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.CompositeFmGaugeDatas_12);

		value = new ComboViewer(this, SWT.READ_ONLY);
		value.getControl().setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1));
		value.addSelectionChangedListener(lst);
		value.setLabelProvider(new DatasLabelProvider());
		value.setContentProvider(new ListContentProvider<ColumnDescriptor>());
		value.setComparator(new LabelableComparator());
	
	}

	@Override
	public void fill(IComponentDatas data) {
		 Assert.isTrue(data instanceof ComplexGaugeDatas);
		 datas = (ComplexGaugeDatas)data;
		 
		 dataSource.setSelection(new StructuredSelection(Activator.getDefault().getProject().getDictionary().getDatasource(data.getDataSet().getDataSourceName())));
		 dataSet.setSelection(new StructuredSelection(datas.getDataSet()));
		 
		 
		 /*
		  * expectations
		  */
		 bTolerance.setSelection(datas.isExpectedFieldsUsed());
		 maxTolerated.getCombo().setEnabled(datas.isExpectedFieldsUsed());
		 minTolerated.getCombo().setEnabled(datas.isExpectedFieldsUsed());
			
		 for(ColumnDescriptor c : (List<ColumnDescriptor>)minTolerated.getInput()){
			 if (datas.getIndexMaxSeuil() !=null && c.getColumnIndex() == datas.getIndexMaxSeuil()){
				 maxTolerated.setSelection(new StructuredSelection(c));
			 }
			 if (datas.getIndexMinSeuil() != null && c.getColumnIndex() == datas.getIndexMinSeuil()){
				 minTolerated.setSelection(new StructuredSelection(c));
			 }
			 
			 if (datas.getIndexMin() != null && c.getColumnIndex() == datas.getIndexMin()){
				 min.setSelection(new StructuredSelection(c));
			 }
			 
			 
			 if (datas.getIndexValue() != null && c.getColumnIndex() == datas.getIndexValue()){
				 value.setSelection(new StructuredSelection(c));
			 }
		 }
		 

		 /*
		  * target
		  */
		 if (datas.getTargetValue() != null){
			 target.setText("" + datas.getTargetValue());
		 }
		 bTarget.setSelection(datas.isTargetNeeded());
		 target.setEnabled(datas.isTargetNeeded());
		 tolerancePerc.setEnabled(datas.isTargetNeeded());


		 if (datas.getIndexTolerance() != null){
			 tolerancePerc.setText(datas.getIndexTolerance() + ""); 
		 }
		 
		 
		 /*
		  * minmax
		  */
		 
		 bMinMax.setSelection(datas.isRangeManual());
		 minTxt.setEnabled(datas.isRangeManual());
		 maxTxt.setEnabled(datas.isRangeManual());
		 min.getControl().setEnabled(!datas.isRangeManual());
		 max.getControl().setEnabled(!datas.isRangeManual());
		 if (datas.getMinValue() != null){
			 minTxt.setText(datas.getMinValue() + "");
			
		 }
		 if (datas.getMaxValue() != null){
			 maxTxt.setText(datas.getMaxValue() + ""); 
		 }
		 
		 
	}

}
