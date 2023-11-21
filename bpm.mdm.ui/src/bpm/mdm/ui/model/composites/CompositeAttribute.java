package bpm.mdm.ui.model.composites;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.databinding.EMFProperties;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.databinding.viewers.ViewerSupport;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
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
import bpm.mdm.model.MdmPackage;
import bpm.mdm.model.Rule;
import bpm.mdm.ui.Activator;
import bpm.mdm.ui.i18n.Messages;
import bpm.mdm.ui.icons.IconNames;
import bpm.mdm.ui.wizards.RuleWizard;

public class CompositeAttribute implements IDetailsPage {
	
	private ICheckStateListener checkListener = new ICheckStateListener() {
		
		@Override
		public void checkStateChanged(CheckStateChangedEvent event) {
			((Rule)event.getElement()).setActive(event.getChecked());
			
		}
	};
	
	
	private DataBindingContext bindingContext;
	private Attribute attribute;
	private Text textName;
	private Text textDesc;
	private Text textDefault;
	private ControlDecoration defaultDecoration;
	private static final Image image = Activator.getDefault().getImageRegistry().get(IconNames.ERROR);
	private static final Color WARN = new Color(Display.getDefault(), 255,128,0);
	private static final Color ERROR = new Color(Display.getDefault(), 255,0,0);
	

	
	private ComboViewer typeViewer;
	private CheckboxTableViewer rulesViewer;
	private Button nullity, idPart;
	
	private IManagedForm form;

	public CompositeAttribute() {
	}

	@Override
	public void createContents(Composite parent) {
		parent.setLayout(new GridLayout());
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));

		Composite main = form.getToolkit().createComposite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, false));
		main.setLayoutData(new GridData(GridData.FILL_BOTH));

		Label lblName = form.getToolkit().createLabel(main, Messages.CompositeAttribute_0, SWT.NONE);
		lblName.setBounds(0, 0, 55, 15);

		textName = form.getToolkit().createText(main, "", SWT.BORDER); //$NON-NLS-1$
		textName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));
		textName.setBounds(0, 0, 76, 21);

		Label lblDescription =form.getToolkit().createLabel(main, "",  SWT.NONE); //$NON-NLS-1$
		lblDescription.setBounds(0, 0, 55, 15);
		lblDescription.setText(Messages.CompositeAttribute_3);

		textDesc = form.getToolkit().createText(main, "", SWT.BORDER); //$NON-NLS-1$
		textDesc.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));
		textDesc.setBounds(0, 0, 76, 21);

		Label lblType = form.getToolkit().createLabel(main, "",SWT.NONE); //$NON-NLS-1$
		lblType.setBounds(0, 0, 55, 15);
		lblType.setText(Messages.CompositeAttribute_6);

		typeViewer = new ComboViewer(main, SWT.READ_ONLY);
		form.getToolkit().paintBordersFor(typeViewer.getCombo());
		typeViewer.setContentProvider(new ArrayContentProvider());
		typeViewer.setLabelProvider(new LabelProvider());
		typeViewer.setInput(DataType.VALUES);
		typeViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (event.getSelection().isEmpty()) {
					attribute.setDataType(null);
				} else {
					DataType type = (DataType) ((IStructuredSelection) event
							.getSelection()).getFirstElement();
					if (attribute.getDataType() != type){
						attribute.setDataType(type);
					}
					
				}

			}
		});

		Combo combo = typeViewer.getCombo();
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1,
				1));
		combo.setBounds(0, 0, 91, 23);

		nullity = form.getToolkit().createButton(main, "",SWT.CHECK); //$NON-NLS-1$
		nullity.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 2, 1));
		nullity.setText(Messages.CompositeAttribute_1);
		
		Label lblDefault = form.getToolkit().createLabel(main, "",SWT.NONE); //$NON-NLS-1$
		lblDefault.setBounds(0, 0, 55, 15);
		lblDefault.setText(Messages.CompositeAttribute_2);
		
		textDefault = form.getToolkit().createText(main,"", SWT.BORDER); //$NON-NLS-1$
		textDefault.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		textDefault.setBounds(0, 0, 76, 21);
		
		defaultDecoration = new ControlDecoration(textDefault, SWT.RIGHT);

		
		
		
		
		idPart = form.getToolkit().createButton(main, "",SWT.CHECK); //$NON-NLS-1$
		idPart.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		idPart.setText(Messages.CompositeAttribute_13);
		
		Label lblRules =form.getToolkit().createLabel(main, "",SWT.NONE); //$NON-NLS-1$
		lblRules.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false,
				2, 1));
		lblRules.setText(Messages.CompositeAttribute_15);
		
		Composite c = form.getToolkit().createComposite(main, SWT.NONE); 
		c.setLayout(new GridLayout(2, false));
		c.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));

		
		ToolBar toolBar = new ToolBar(c, SWT.FLAT | SWT.VERTICAL);
		toolBar.setLayoutData(new GridData(SWT.BEGINNING, SWT.TOP, false, true));
		toolBar.setBounds(0, 0, 89, 23);

		ToolItem tltmNewItem = new ToolItem(toolBar, SWT.NONE);
		tltmNewItem.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ADD));
		tltmNewItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				RuleWizard wiz = new RuleWizard(attribute);
				WizardDialog d = new WizardDialog(form.getForm().getShell(), wiz);
				if(d.open() == WizardDialog.OK){
					rulesViewer.refresh();
				}
				
				
				
			}
		});
		tltmNewItem.setToolTipText(Messages.CompositeAttribute_16);

		ToolItem tltmDel = new ToolItem(toolBar, SWT.NONE);
		tltmDel.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_ETOOL_DELETE));
		tltmDel.setToolTipText(Messages.CompositeAttribute_17);
		tltmDel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				if (rulesViewer.getSelection().isEmpty()){
					return;
				}
				for(Object o : ((IStructuredSelection)rulesViewer.getSelection()).toList()){
					attribute.getRules().remove(o);
				}
				rulesViewer.refresh();
				
				
			}
		});
		toolBar.setBackground(form.getToolkit().getColors().getBackground());
		form.getToolkit().paintBordersFor(toolBar);

		rulesViewer = CheckboxTableViewer.newCheckList(c, SWT.BORDER | SWT.V_SCROLL
				| SWT.FULL_SELECTION | SWT.MULTI);
		rulesViewer.getTable().setHeaderVisible(true);
		rulesViewer.getTable().setLinesVisible(true);
		rulesViewer.getTable().setLayoutData(
				new GridData(GridData.FILL, GridData.FILL, true, true));
		rulesViewer.setContentProvider(new ObservableListContentProvider());
		rulesViewer.addDoubleClickListener(new IDoubleClickListener() {
			
			@Override
			public void doubleClick(DoubleClickEvent event) {
				Rule rule = (Rule)((IStructuredSelection)event.getSelection()).getFirstElement();
				
				RuleWizard wiz = new RuleWizard(attribute, rule);
				WizardDialog d = new WizardDialog(rulesViewer.getTable().getShell(), wiz);
				d.open();
				rulesViewer.refresh();
				
			}
		});

		TableViewerColumn col = new TableViewerColumn(rulesViewer, SWT.NONE);
		col.getColumn().setText(Messages.CompositeAttribute_18);
		col.getColumn().setWidth(150);

		col = new TableViewerColumn(rulesViewer, SWT.NONE);
		col.getColumn().setText(Messages.CompositeAttribute_19);
		col.getColumn().setWidth(150);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {

				return ((Rule) element).getRuleTypeName();
			}
		});
		
		form.getToolkit().paintBordersFor(rulesViewer.getTable());
		
	}

	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue textNameObserveTextObserveWidget = SWTObservables
				.observeText(textName, SWT.Modify);

		bindingContext.addBinding(bindingContext.bindValue(
				textNameObserveTextObserveWidget,
				EMFProperties.value(MdmPackage.Literals.ATTRIBUTE__NAME)
						.observe(attribute), null, null));
		//
		IObservableValue textDescObserveTextObserveWidget = SWTObservables
				.observeText(textDesc, SWT.Modify);

		bindingContext.addBinding(bindingContext.bindValue(
				textDescObserveTextObserveWidget, EMFProperties.value(
						MdmPackage.Literals.ATTRIBUTE__DESCRIPTION).observe(
						attribute), null, null));
		
		
		
		bindingContext.addBinding(bindingContext.bindValue(
				SWTObservables.observeSelection(nullity), 
				EMFProperties.value(
						MdmPackage.Literals.ATTRIBUTE__NULL_ALLOWED).observe(
						attribute),null, null));
		
		bindingContext.addBinding(bindingContext.bindValue(
				SWTObservables.observeText(textDefault, SWT.Modify), 
				EMFProperties.value(
						MdmPackage.Literals.ATTRIBUTE__DEFAULT_VALUE).observe(
						attribute), 
						new UpdateValueStrategy(){
							@Override
							public IStatus validateAfterConvert(Object value) {
								IStatus s = null;  
								try{
									if (attribute.getDataType().convertFromString((String)value) != null){
										s = new Status(IStatus.OK, Activator.PLUGIN_ID, ""); //$NON-NLS-1$
										 defaultDecoration.setImage(null);
										 defaultDecoration.setDescriptionText(null);
										 textDefault.setBackground(null);
									}
									else{
										s = new Status(IStatus.ERROR, Activator.PLUGIN_ID, ""); //$NON-NLS-1$
										if (!attribute.isNullAllowed()){
											 defaultDecoration.setImage(image);
											 defaultDecoration.setDescriptionText(Messages.CompositeAttribute_7);
											 textDefault.setBackground(WARN);
										}
									}
									
								}catch(Exception ex){
									s = new Status(IStatus.ERROR, Activator.PLUGIN_ID, ex.getMessage());
									 defaultDecoration.setImage(image);
									 defaultDecoration.setDescriptionText(s.getMessage());
									 textDefault.setBackground(ERROR);
								}
								return s;
//								return super.validateAfterConvert(value);
							}
						}
						,null));
		
		
		bindingContext.addBinding(bindingContext.bindValue(
				SWTObservables.observeSelection(nullity), 
				EMFProperties.value(
						MdmPackage.Literals.ATTRIBUTE__NULL_ALLOWED).observe(
						attribute), null, null));
		
		
		bindingContext.addBinding(bindingContext.bindValue(
				SWTObservables.observeSelection(idPart), 
				EMFProperties.value(
						MdmPackage.Literals.ATTRIBUTE__ID).observe(
						attribute), null, null));
		//

		/*
		 * bind viewer
		 */
		WritableList input = new WritableList(attribute.getRules(),
				Attribute.class);

		ViewerSupport.bind(rulesViewer, input, EMFProperties
				.values(MdmPackage.Literals.RULE__NAME,
						MdmPackage.Literals.RULE__DESCRIPTION));
		

		
		return bindingContext;
	}

	public void bind(Attribute attribute) {
		rulesViewer.removeCheckStateListener(checkListener);
		if (bindingContext != null) {
			bindingContext.dispose();
		}

		this.attribute = attribute;
		bindingContext = initDataBindings();

		typeViewer
				.setSelection(new StructuredSelection(attribute.getDataType()));
		
		for(Rule r : attribute.getRules()){
			rulesViewer.setChecked(r, r.isActive());
		}
		
		rulesViewer.addCheckStateListener(checkListener);
	}

	public Attribute getModel() {
		return attribute;
	}

	@Override
	public void commit(boolean onSave) {
		

	}

	@Override
	public void dispose() {
		

	}
	private static final String RED = "error"; //$NON-NLS-1$
	@Override
	public void initialize(IManagedForm form) {
		this.form = form;
		form.getToolkit().getColors().createColor("", 255, 0, 0); //$NON-NLS-1$
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
		if (!selection.isEmpty()) {
			Object o = ((IStructuredSelection) selection).getFirstElement();
			bind((Attribute) o);
		}

	}
}
