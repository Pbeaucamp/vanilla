package bpm.mdm.ui.model.composites;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.emf.databinding.EMFProperties;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.databinding.viewers.ViewerSupport;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;

import bpm.mdm.model.Attribute;
import bpm.mdm.model.DataType;
import bpm.mdm.model.Entity;
import bpm.mdm.model.MdmFactory;
import bpm.mdm.model.MdmPackage;
import bpm.mdm.ui.i18n.Messages;

public class CompositeEntity implements IDetailsPage{
	private DataBindingContext bindingContext;
	private TableViewer attributesViewer;
	private Text textName;
	private Text textDesc;
	private IManagedForm form;
	private Entity entity;
	
	
	public CompositeEntity(){
	}
	
	protected DataBindingContext initDataBindings() {
		
		DataBindingContext bindingContext = new DataBindingContext();
		//UpdateValueStrategy
		
		IObservableValue textObserveTextObserveWidget = SWTObservables.observeText(textName, SWT.Modify);
		bindingContext.bindValue(textObserveTextObserveWidget, 
				EMFProperties.value(MdmPackage.Literals.ENTITY__NAME).observe(entity), 
				null, 
				null);
		//
		
		
		IObservableValue text_1ObserveTextObserveWidget = SWTObservables.observeText(textDesc, SWT.Modify);
		
		bindingContext.bindValue(text_1ObserveTextObserveWidget,
				EMFProperties.value(MdmPackage.Literals.ENTITY__DESCRIPTION).observe(entity), null, null);
		//
		
		/*
		 * bind viewer
		 */
		WritableList input = new WritableList(entity.getAttributes(), Attribute.class);
		
		ViewerSupport.bind(
				attributesViewer,
				input,
				EMFProperties.values(
						MdmPackage.Literals.ATTRIBUTE__NAME,
						MdmPackage.Literals.ATTRIBUTE__DATA_TYPE
						));
		
		return bindingContext;
	}


	public void fill(Entity entity){
		if (bindingContext != null ){
			bindingContext.dispose();
		}

		this.entity = entity;
		bindingContext = initDataBindings();
	}
	public Entity getModel() {
		return entity;
	}
	@Override
	public void createContents(Composite parent) {
		parent.setLayout(new GridLayout());
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Composite main = form.getToolkit().createComposite(parent, SWT.NONE); 
		main.setLayout(new GridLayout(2, false));
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label lblName = form.getToolkit().createLabel(main, Messages.CompositeEntity_0, SWT.NONE);
		
		textName =form.getToolkit().createText(main, "", SWT.BORDER); //$NON-NLS-1$
		textName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textName.setBounds(0, 0, 76, 21);
		
		Label lblDescription = form.getToolkit().createLabel(main, "", SWT.NONE); //$NON-NLS-1$
		lblDescription.setBounds(0, 0, 55, 15);
		lblDescription.setText(Messages.CompositeEntity_3);
		
		textDesc = form.getToolkit().createText(main, "", SWT.BORDER); //$NON-NLS-1$
		textDesc.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textDesc.setBounds(0, 0, 76, 21);
		
		
		Label lblAttributes = form.getToolkit().createLabel(main, "", SWT.NONE); //$NON-NLS-1$
		lblAttributes.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		lblAttributes.setBounds(0, 0, 55, 15);
		lblAttributes.setText(Messages.CompositeEntity_6);
		
		Composite c = form.getToolkit().createComposite(main, SWT.NONE); 
		c.setLayout(new GridLayout(2, false));
		c.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		
		ToolBar toolBar = new ToolBar(c, SWT.FLAT | SWT.VERTICAL);
		toolBar.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, true));
		toolBar.setBounds(0, 0, 89, 23);
		
		ToolItem tltmNewItem = new ToolItem(toolBar, SWT.NONE);
		tltmNewItem.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ADD));
		tltmNewItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Attribute att = MdmFactory.eINSTANCE.createAttribute();
				att.setDataType(DataType.STRING);
				att.setName(Messages.CompositeEntity_7);
				entity.getAttributes().add(att);
				attributesViewer.refresh();
				
				form.fireSelectionChanged(CompositeEntity.this, new StructuredSelection(entity));
				//TODO : find something better, we loose the viewer expension state
//				viewer.setInput(Activator.getDefault().getMdmModel());
			}
		});
		tltmNewItem.setToolTipText(Messages.CompositeEntity_8);
		
		ToolItem tltmDel = new ToolItem(toolBar, SWT.NONE);
		tltmDel.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_ETOOL_DELETE));
		tltmDel.setToolTipText(Messages.CompositeEntity_9);
		tltmDel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				for(Object o : ((IStructuredSelection)attributesViewer.getSelection()).toList()){
					EcoreUtil.delete((EObject) o);
//					entity.getAttributes().remove(o);
				}
				attributesViewer.refresh();
				
				form.fireSelectionChanged(CompositeEntity.this, new StructuredSelection(entity));

			}
		});
		toolBar.setBackground(form.getToolkit().getColors().getBackground());
		form.getToolkit().paintBordersFor(toolBar);
		
		
		attributesViewer = new TableViewer(c, SWT.BORDER | SWT.FULL_SELECTION);
		attributesViewer.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		attributesViewer.getTable().setBounds(0, 0, 85, 85);
		attributesViewer.getTable().setHeaderVisible(true);
		attributesViewer.getTable().setLinesVisible(true);
		
		attributesViewer.setContentProvider(new ObservableListContentProvider());

		TableViewerColumn col = new TableViewerColumn(attributesViewer, SWT.NONE);
		col.getColumn().setText(Messages.CompositeEntity_10);
		col.getColumn().setWidth(150);
		
		col = new TableViewerColumn(attributesViewer, SWT.NONE);
		col.getColumn().setText(Messages.CompositeEntity_11);
		col.getColumn().setWidth(150);
		form.getToolkit().paintBordersFor(attributesViewer.getTable());
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
			fill((Entity)o);
		}
	}
}
