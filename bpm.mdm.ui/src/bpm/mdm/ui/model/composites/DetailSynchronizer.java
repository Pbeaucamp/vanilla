package bpm.mdm.ui.model.composites;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.datatools.connectivity.oda.design.DataSetDesign;
import org.eclipse.emf.databinding.EMFProperties;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.viewers.ViewerSupport;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;

import bpm.mdm.model.Entity;
import bpm.mdm.model.MdmPackage;
import bpm.mdm.model.Model;
import bpm.mdm.model.Synchronizer;
import bpm.mdm.ui.Activator;
import bpm.mdm.ui.i18n.Messages;

public class DetailSynchronizer implements IDetailsPage{
	
	private ComboViewer dataSources, entities;
	private Text name, description;
	private CompositeMapper mapper;
	private DataBindingContext bindingContext;
	private IManagedForm form;
	
	private Synchronizer sync;
	@Override
	public void createContents(Composite parent) {
		parent.setLayout(new GridLayout());
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Composite main = form.getToolkit().createComposite(parent, SWT.NONE); 
		main.setLayout(new GridLayout(2, false));
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label l = form.getToolkit().createLabel(main,"",  SWT.NONE); //$NON-NLS-1$
		l.setLayoutData(new GridData());
		l.setText(Messages.DetailSynchronizer_1);
		
		name = form.getToolkit().createText(main, "", SWT.BORDER); //$NON-NLS-1$
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		l = form.getToolkit().createLabel(main,"",  SWT.NONE); //$NON-NLS-1$
		l.setLayoutData(new GridData());
		l.setText(Messages.DetailSynchronizer_4);
		
		description = form.getToolkit().createText(main, "", SWT.BORDER); //$NON-NLS-1$
		description.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		
		l = form.getToolkit().createLabel(main,"",  SWT.NONE); //$NON-NLS-1$
		l.setLayoutData(new GridData());
		l.setText(Messages.DetailSynchronizer_7);
		
		entities = new ComboViewer(main, SWT.READ_ONLY);
		entities.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		entities.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
//				sync.setEntity((Entity)((IStructuredSelection)event.getSelection()).getFirstElement());
//				mapper.bind(sync);
				
			}
		});
		form.getToolkit().paintBordersFor(entities.getCombo());
		
		
		l = form.getToolkit().createLabel(main,"",  SWT.NONE); //$NON-NLS-1$
		l.setLayoutData(new GridData());
		l.setText(Messages.DetailSynchronizer_9);
		
		dataSources = new ComboViewer(main, SWT.READ_ONLY);
		dataSources.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		dataSources.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (sync != null){
					if (dataSources.getSelection().isEmpty()){
						sync.setDataSourceName(null);
					}
					else{
						
						sync.setDataSourceName(((DataSetDesign)((IStructuredSelection)dataSources.getSelection()).getFirstElement()).getName());
					}
					
				}
				mapper.bind(sync);
			}
		});

		l = form.getToolkit().createLabel(main, "", SWT.NONE); //$NON-NLS-1$
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, true, false, 2, 1));
		l.setText(Messages.DetailSynchronizer_11);
		
		mapper = new CompositeMapper(main,SWT.NONE);
		mapper.setLayoutData(new GridData(GridData.FILL, GridData.FILL,true, true, 2, 1));
		mapper.setBackground(form.getToolkit().getColors().getBackground());
		form.getToolkit().paintBordersFor(mapper);
	}

	@Override
	public void commit(boolean onSave) {
		
		
	}

	@Override
	public void dispose() {
		
		
	}

	@Override
	public void initialize(IManagedForm form) {
		this.form = form;
		
	}

	@Override
	public boolean isDirty() {
		
		return false;
	}

	@Override
	public boolean isStale() {
		
		return false;
	}

	@Override
	public void refresh() {
		
		
	}

	@Override
	public void setFocus() {
		
		
	}

	@Override
	public boolean setFormInput(Object input) {
		
		return false;
	}

	@Override
	public void selectionChanged(IFormPart part, ISelection selection) {
		if (!selection.isEmpty()){
			Object o = ((IStructuredSelection)selection).getFirstElement();
			fill((Synchronizer)o);
		}
		
	}
	
	
	private void fill(Synchronizer sync){
		
		Model model = null;
		
		try{
			model = Activator.getDefault().getMdmProvider().getModel(); 
		}catch(Exception ex){
			ex.printStackTrace();
			MessageDialog.openError(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.DetailSynchronizer_0, Messages.DetailSynchronizer_2 + ex.getMessage());
		}
		
		WritableList input = new WritableList(model.getEntities(), Entity.class);
		
		ViewerSupport.bind(
				entities,
				input,
				EMFProperties.value(
						MdmPackage.Literals.ENTITY__NAME
						));
		
		
		input = new WritableList(model.getDataSources(), DataSetDesign.class);
		
		ViewerSupport.bind(
				dataSources,
				input,
				EMFProperties.value(
						org.eclipse.datatools.connectivity.oda.design.DesignPackage.eINSTANCE.getDataSetDesign_Name()
						));
		
		this.sync = sync;
		
		
		if (bindingContext != null ){
			bindingContext.dispose();
		}

		this.sync = sync;
		bindingContext = initDataBindings();
		
		mapper.bind(sync);
		
//		entities.setSelection(new StructuredSelection(sync.getEntity()));
//		dataSources.setSelection(new StructuredSelection(sync.getDataSource()));
	}
	
	private DataBindingContext initDataBindings(){
		DataBindingContext bindingContext = new DataBindingContext();
		
		

		bindingContext.bindValue(
				SWTObservables.observeText(name, SWT.Modify),
				EMFProperties.value(MdmPackage.Literals.SYNCHRONIZER__NAME).observe(sync), null, null);

		bindingContext.bindValue(
				SWTObservables.observeText(description, SWT.Modify),
				EMFProperties.value(MdmPackage.Literals.SYNCHRONIZER__DESCRIPTION).observe(sync), null, null);

		
		bindingContext.bindValue(
				ViewersObservables.observeSingleSelection(entities),
				EMFProperties.value(MdmPackage.Literals.SYNCHRONIZER__ENTITY).observe(sync), 
				new UpdateValueStrategy(){
					@Override
					protected IStatus doSet(IObservableValue observableValue,
							Object value) {
						IStatus res=  super.doSet(observableValue, value);
						mapper.bind(sync);
						return res;
					}
				}, null);

//		bindingContext.bindValue(
//				ViewersObservables.observeInput(entities),
//				EMFProperties.value(MdmPackage.Literals.MODEL__ENTITIES).observe(Activator.getDefault().getMdmModel()), null, null);

//		IObservableValue selection = ViewersObservables.observeSingleSelection(dataSources);
//		
//		IObservableValue val = EMFProperties.value(MdmPackage.Literals.SYNCHRONIZER__ENTITY).o
//		
//		bindingContext.bindValue(
//				//ViewersObservables.observeSingleSelection(dataSources),
//				selection,
//				EMFObservables.observeDetailValue(selection.getRealm(), selection, MdmPackage.Literals.SYNCHRONIZER__DATA_SOURCE_NAME));
//				//EMFProperties.value(MdmPackage.Literals.SYNCHRONIZER__DATA_SOURCE_NAME).observe(sync), null, null);

		
		try{
			dataSources.setSelection(new StructuredSelection(Activator.getDefault().getMdmProvider().getModel().getDataSource(sync.getDataSourceName())));
		}catch(Exception ex){
			dataSources.setSelection(StructuredSelection.EMPTY);
		}
		
		return bindingContext;
	}

}
