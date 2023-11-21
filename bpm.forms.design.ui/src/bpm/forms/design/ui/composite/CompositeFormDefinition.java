package bpm.forms.design.ui.composite;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.databinding.observable.ChangeEvent;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import bpm.forms.core.design.IFormDefinition;
import bpm.forms.core.design.IFormFieldMapping;
import bpm.forms.core.design.IFormUi;
import bpm.forms.core.design.ITargetTable;
import bpm.forms.core.design.ui.VanillaFdProperties;
import bpm.forms.design.ui.Activator;
import bpm.forms.design.ui.Messages;
import bpm.forms.design.ui.composite.tools.ExpantionListener;
import bpm.forms.design.ui.composite.viewer.CompositoryItemSelectionSelectionListener;
import bpm.forms.design.ui.dialogs.DialogTargetTable;
import bpm.forms.design.ui.tools.converters.BooleanToInt;
import bpm.forms.design.ui.tools.converters.BooleanToNumber;
import bpm.forms.design.ui.tools.converters.DateToTextConverter;
import bpm.forms.design.ui.tools.converters.IntToBoolean;
import bpm.forms.design.ui.tools.converters.TextToDateConverter;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.repository.Repository;
import bpm.vanilla.repository.ui.composites.CompositeRepositoryItemSelecter;
import bpm.vanilla.repository.ui.composites.CompositeRepositorySelecter;

public class CompositeFormDefinition {
	private Composite client;
	
	private Text description;
	private Text creationDate;
//	private Text id;
	private Text startDate;
	private Text stopDate;
	private Text version;
	private Button active;
	private Button fullyDesigner;
	
	private TableViewer availableTargetTables;
	private TableViewer targetTables;
//	private CheckboxTableViewer validatorViewer;
	
	private CompositeRepositoryItemSelecter itemSelecterComposite;
	private CompositoryItemSelectionSelectionListener itemSelecterListener; 
	
	
	
	
	
	private CompositeRepositorySelecter repositorySelecterComposite;
	private FormToolkit toolkit;
	
	private DataBindingContext bindingCtx;
	
	private IFormDefinition formDef;
	
	private CompositeFieldMapping compositeFieldMapping;
	
	private IChangeListener changeListener = new IChangeListener() {
		
		@Override
		public void handleChange(ChangeEvent event) {
			if (getClient() != null && !getClient().isDisposed()){
				getClient().notifyListeners(SWT.Modify, new Event());
			}
			
			
		}
	};
	
	private Listener repositorySelecterListener = new Listener() {
		
		@Override
		public void handleEvent(Event event) {
			IRepositoryApi sock = repositorySelecterComposite.getRepositorySocket();
			bpm.vanilla.platform.core.beans.Repository repDef = repositorySelecterComposite.getSelectedRepositoryDefinition();
			try{
				itemSelecterComposite.setInput(new Repository(sock, IRepositoryApi.FD_TYPE));
			}catch(Exception ex){
				ex.printStackTrace();
				MessageDialog.openError(getClient().getShell(), Messages.CompositeFormDefinition_0, Messages.CompositeFormDefinition_1 + ex.getMessage());
			}
			
			IFormUi  ui = (IFormUi)formDef.getFormUI();
			if (ui == null){
				ui = Activator.getDefault().getFactoryModel().createFormUi();
//				ui.setProperty(VanillaFdProperties.);
				ui.setFormDefinitionId(formDef.getId());
				formDef.setFormUI(ui);
			}
			
						
			if (repDef != null){
				ui.setProperty(VanillaFdProperties.PROP_FD_REPOSITORY_ID, repDef.getId() + ""); //$NON-NLS-1$
				
			}
			else{
				ui.setProperty(VanillaFdProperties.PROP_FD_REPOSITORY_ID, "0"); //$NON-NLS-1$
			}
			
			if (getClient() != null && !getClient().isDisposed()){
				getClient().notifyListeners(SWT.Modify, new Event());
			}
			
		}
	};
	private List<IObservableValue> listened = new ArrayList<IObservableValue>();
	
	private ExpantionListener expansionListener = new ExpantionListener();
	
	private Listener fieldMappingListener = new Listener(){

		@Override
		public void handleEvent(Event event) {
			if (getClient() != null && !getClient().isDisposed()){
				getClient().notifyListeners(SWT.Modify, new Event());
			}
			
			
		}
		
	};
	
	
	
	public CompositeFormDefinition(FormToolkit toolkit){
		this.toolkit = toolkit;
	
	}

	
	public Composite getClient(){
		return client;
	}
	
	public void createContent(Composite parent){
		
		if (toolkit == null){
			this.toolkit = new FormToolkit(Display.getDefault());
		}
		
		
		client = toolkit.createComposite(parent, SWT.NONE);
		client.setLayout(new GridLayout());
		client.addDisposeListener(new DisposeListener() {
			
			@Override
			public void widgetDisposed(DisposeEvent e) {
				toolkit.dispose();
				toolkit = null;
				
			}
		});
			
		
		/*
		 * Section General Infos
		 */
		createGeneralSection();

		
		/*
		 * Activations Datas
		 */
		createActivationSection();
		
		/*
		 * FdForm Attachment
		 */
		createFdFormAttachment();
		//TODO
		
		/*
		 * Table Links
		 */
		createTableSection();
		
		/*
		 * Field Mapping
		 */
		createFieldsMapping();
		
		/*
		 * Validators
		 */
//		createValidators();
		
	}
	
	
//	private void createValidators(){
//		Section section = toolkit.createSection(client, Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED);
//		section.setText("Validators");
//		section.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
//		section.setExpanded(false);
//		
//		validatorViewer = new CheckboxTableViewer(toolkit.createTable(section, SWT.CHECK | SWT.V_SCROLL | SWT.H_SCROLL));
//		validatorViewer.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
//		validatorViewer.setContentProvider(new ArrayContentProvider());
//		validatorViewer.setLabelProvider(new LabelProvider(){
//			@Override
//			public String getText(Object element) {
//				return ((Group)element).getName();
//			}
//		});
//		validatorViewer.getTable().setLinesVisible(true);
//		validatorViewer.getTable().setHeaderVisible(true);
////		validatorViewer.getTable().getColumn(0).setText("Validator Group Name");
//		
//		
//		section.setClient(validatorViewer.getControl());
//		section.addExpansionListener(expansionListener);
//		expansionListener.registerSection(section);
//	}
	
	private void createFieldsMapping(){
		Section section = toolkit.createSection(client, Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED);
		section.setText(Messages.CompositeFormDefinition_4);
		section.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		section.setExpanded(false);
		
		compositeFieldMapping = new CompositeFieldMapping(toolkit);
		compositeFieldMapping.createContent(section);
		compositeFieldMapping.getClient().setLayoutData(new GridData(GridData.FILL_BOTH));
		
		section.setClient(compositeFieldMapping.getClient());
		
		section.addExpansionListener(expansionListener);
		
		expansionListener.registerSection(section);

	}
	
	
		
	
	private void createFdFormAttachment(){
		Section section = toolkit.createSection(client, Section.TITLE_BAR | Section.TWISTIE);
		section.setText(Messages.CompositeFormDefinition_5);
		section.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		section.setExpanded(false);
		
		Composite main = toolkit.createComposite(section);
		main.setLayout(new GridLayout());
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		repositorySelecterComposite = new CompositeRepositorySelecter(main, SWT.NONE);
		repositorySelecterComposite.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		
		
		
		itemSelecterComposite = new CompositeRepositoryItemSelecter(main, SWT.NONE);
		itemSelecterComposite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
//		itemSelecterComposite.addViewerFilter(new ViewerFilter() {
//			
//			@Override
//			public boolean select(Viewer viewer, Object parentElement, Object element) {
//				if (element instanceof AxisDirectoryItem){
//					
//					return ((AxisDirectoryItem)element).getRepositoryItem().getModelType().equals("bpm.fd.api.core.model.FdVanillaFormModel");
//				}
//				else{
//					return false;
//				}
//				
//			}
//		});
		
		
		itemSelecterListener = new CompositoryItemSelectionSelectionListener(itemSelecterComposite, getClient());

		
		
		
		section.setClient(main);
		section.addExpansionListener(expansionListener);
		
		expansionListener.registerSection(section);
	}
	
	
	
	private void createGeneralSection(){
		Section section = toolkit.createSection(client, Section.TITLE_BAR | Section.TWISTIE);
		section.setText(Messages.CompositeFormDefinition_6);
		section.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		section.setExpanded(false);
		
		
		Composite generalComposite = toolkit.createComposite(section);
		generalComposite.setLayout(new GridLayout(2, false));
		generalComposite.setLayoutData(new GridData());
		
		Label  l = toolkit.createLabel(generalComposite, Messages.CompositeFormDefinition_7);
		l.setLayoutData(new GridData());
		
		creationDate = toolkit.createText(generalComposite, ""); //$NON-NLS-1$
		creationDate.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		creationDate.setEnabled(false);
		
		l = toolkit.createLabel(generalComposite, Messages.CompositeFormDefinition_9);
		l.setLayoutData(new GridData());
		
		
		version = toolkit.createText(generalComposite, ""); //$NON-NLS-1$
		version.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		version.setEnabled(false);
		
		
		
		l = toolkit.createLabel(generalComposite, Messages.CompositeFormDefinition_11);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, true));
		
		description = toolkit.createText(generalComposite, ""); //$NON-NLS-1$
		description.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
		section.setClient(generalComposite);
		section.addExpansionListener(expansionListener);
		
		expansionListener.registerSection(section);
	}
	
	private void createActivationSection(){
		Section section = toolkit.createSection(client, Section.TITLE_BAR | Section.TWISTIE);
		section.setText(Messages.CompositeFormDefinition_13);
		section.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		section.setExpanded(false);
		
		Composite activationComposite = toolkit.createComposite(section);
		activationComposite.setLayout(new GridLayout(2, false));
		activationComposite.setLayoutData(new GridData());
		
		Label l = toolkit.createLabel(activationComposite, Messages.CompositeFormDefinition_14 + " (YYYY-MM-DD)");
		l.setLayoutData(new GridData());
		
		startDate = toolkit.createText(activationComposite, ""); //$NON-NLS-1$
		startDate.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		l = toolkit.createLabel(activationComposite, Messages.CompositeFormDefinition_16 + " (YYYY-MM-DD)");
		l.setLayoutData(new GridData());
		
		stopDate = toolkit.createText(activationComposite, ""); //$NON-NLS-1$
		stopDate.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		active = toolkit.createButton(activationComposite, "IsActive", SWT.CHECK); //$NON-NLS-1$
		active.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		
		
		fullyDesigner = toolkit.createButton(activationComposite, Messages.CompositeFormDefinition_19, SWT.CHECK);
		fullyDesigner.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		
		section.setClient(activationComposite);
		section.addExpansionListener(expansionListener);
		
		expansionListener.registerSection(section);
	}
	
	private void createTableSection(){
		
		/*
		 * Table Links
		 */
		Section section = toolkit.createSection(client, Section.TITLE_BAR | Section.TWISTIE);
		section.setText(Messages.CompositeFormDefinition_20);
		section.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		section.setExpanded(false);
		
		Composite targetTableComposite = toolkit.createComposite(section);
		targetTableComposite.setLayout(new GridLayout(3, false));
		targetTableComposite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));

		
		Label l = toolkit.createLabel(targetTableComposite, Messages.CompositeFormDefinition_21);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 2, 1));
		
		l = toolkit.createLabel(targetTableComposite, Messages.CompositeFormDefinition_22);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		
		availableTargetTables = new TableViewer(toolkit.createTable(targetTableComposite, SWT.FULL_SELECTION | SWT.VIRTUAL | SWT.H_SCROLL | SWT.V_SCROLL));
		availableTargetTables.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 3));
		availableTargetTables.setContentProvider(new ObservableListContentProvider());
		
		configureTable(availableTargetTables);
		createContextMenu();
		
		
		
		
		Button add = toolkit.createButton(targetTableComposite, ">>", SWT.PUSH); //$NON-NLS-1$
		add.setLayoutData(new GridData(GridData.CENTER, GridData.END, false, true, 1, 2));
		add.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean notify = false;
				for(Object o : ((IStructuredSelection)availableTargetTables.getSelection()).toList()){

					((WritableList)targetTables.getInput()).add(o);
					((IFormDefinition)formDef).addTargetTable((ITargetTable)o);
					((WritableList)availableTargetTables.getInput()).remove(o);
					notify = true;	
				}
				if (notify){
					if (getClient() != null && !getClient().isDisposed()){
						getClient().notifyListeners(SWT.Modify, new Event());
					}
				}
			}
		});
		
		targetTables = new TableViewer(toolkit.createTable(targetTableComposite, SWT.FULL_SELECTION | SWT.VIRTUAL | SWT.H_SCROLL | SWT.V_SCROLL));
		targetTables.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 3));
		targetTables.setContentProvider(new ObservableListContentProvider());
		configureTable(targetTables);
		
		
		Button rem = toolkit.createButton(targetTableComposite, "<<", SWT.PUSH); //$NON-NLS-1$
		rem.setLayoutData(new GridData(GridData.CENTER, GridData.BEGINNING, false, true));
		rem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean notify = false;
				for(Object o : ((IStructuredSelection)targetTables.getSelection()).toList()){

					
					boolean remove = false;
					
					for(IFormFieldMapping m : formDef.getIFormFieldMappings()){
						if (!remove && m.getTargetTableId() == ((ITargetTable)o).getId()){
							remove = MessageDialog.openQuestion(getClient().getShell(), Messages.CompositeFormDefinition_25, Messages.CompositeFormDefinition_26);
						}
						if (!remove){
							return;
						}
						else{
							((IFormFieldMapping)m).setTargetTableId(null);
							((IFormFieldMapping)m).setDatabasePhysicalName(null);
							
						}
					}
					if (remove){
						compositeFieldMapping.refresh();
					}
					
					((WritableList)targetTables.getInput()).remove(o);
					((IFormDefinition)formDef).removeTargetTable((ITargetTable)o);
					((WritableList)availableTargetTables.getInput()).add(o);
					notify = true;	
				}
				if (notify){
					if (getClient() != null && !getClient().isDisposed()){
						getClient().notifyListeners(SWT.Modify, new Event());
					}
				}
			}
		});
		section.setClient(targetTableComposite);
		section.addExpansionListener(expansionListener);
		
		expansionListener.registerSection(section);
	}
	
	
	private void configureTable(TableViewer viewer){
		
		viewer.getTable().setHeaderVisible(true);
		viewer.getTable().setLinesVisible(true);
		
		TableViewerColumn col = new TableViewerColumn(viewer, SWT.LEFT);
		col.getColumn().setText(Messages.CompositeFormDefinition_27);
		col.getColumn().setWidth(100);
		col.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				return ((ITargetTable)element).getName();
			}
		});
		
		col = new TableViewerColumn(viewer, SWT.LEFT);
		col.getColumn().setText(Messages.CompositeFormDefinition_28);
		col.getColumn().setWidth(100);
		col.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				return ((ITargetTable)element).getDescription();
			}
		});
		
		col = new TableViewerColumn(viewer, SWT.LEFT);
		col.getColumn().setText(Messages.CompositeFormDefinition_29);
		col.getColumn().setWidth(100);
		col.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				return ((ITargetTable)element).getDatabasePhysicalName();
			}
		});
		
		
	}
	
	
	public void setInput(IFormDefinition formdef){
		this.formDef = formdef;
		itemSelecterComposite.removeSelectionChangedListener(itemSelecterListener);
		repositorySelecterComposite.removeListener(SWT.Selection, repositorySelecterListener);
//		validatorViewer.removeCheckStateListener(checkListener);
		
		
		if (bindingCtx != null){
			bindingCtx.dispose();
			bindingCtx = null;
		}
		
		//XXX : should be replaced by XmlForm once DAO is removed from dependancies 
		bindValues();
		
		
		/*
		 * fill Tables
		 */
		
		
		List<ITargetTable> allTables = Activator.getDefault().getServiceProvider().getDefinitionService().getTargetTables();
		List<ITargetTable> usedTables = formdef.getITargetTables();

		List toRemove = new ArrayList();
		for(ITargetTable t : allTables){
			for(ITargetTable _t : usedTables){
			
				if (t.getId() == _t.getId()){
					toRemove.add(t);
				}
			}
		}
		allTables.removeAll(toRemove);
		
		WritableList availableTables = new WritableList(allTables, ITargetTable.class);
		availableTargetTables.setInput(availableTables);
		
		
		WritableList usedtables = new WritableList(usedTables, ITargetTable.class);
		targetTables.setInput(usedtables);
		
		
		/*
		 * fill FdAttachmentPart
		 */
		//TODO
		if (formDef.getFormUI() != null){
			
			//init RepDef
			Integer repId = null;
			
			try{
				repId = Integer.parseInt(formdef.getFormUI().getPropertyValue(VanillaFdProperties.PROP_FD_REPOSITORY_ID));
			}catch(Exception ex){
				
			}
			
			IVanillaContext vCtx = Activator.getDefault().getVanillaContext().getVanillaContext();
			repositorySelecterComposite.fill(vCtx.getLogin(), vCtx.getPassword(), vCtx.getVanillaUrl(), repId);
			repositorySelecterComposite.enableConnectionFields(false);

			//init itemDef
			Integer dirItId = null;
			try{
				
				itemSelecterComposite.setInput(new Repository(repositorySelecterComposite.getRepositorySocket(), IRepositoryApi.FD_TYPE));
				dirItId = Integer.parseInt(formdef.getFormUI().getPropertyValue(VanillaFdProperties.PROP_FD_DIRECTORY_ITEM_ID));
				
				
				itemSelecterComposite.selectDirectoryItem(dirItId);
			}catch(Exception ex){
//				itemSelecterComposite.setInput(new Repository());
			}
			
		}
		else{
			itemSelecterComposite.setInput(new Repository(repositorySelecterComposite.getRepositorySocket()));
			IVanillaContext vCtx = Activator.getDefault().getVanillaContext().getVanillaContext();
			repositorySelecterComposite.fill(vCtx.getLogin(), vCtx.getPassword(), vCtx.getVanillaUrl(), null);
			repositorySelecterComposite.enableConnectionFields(false);

		}
		
//		/*
//		 * fill Validators
//		 */
//		AdminAccess aa = new AdminAccess(Activator.getDefault().getVanillaContext().getVanillaUrl());
//		try{
//			validatorViewer.setInput(aa.getGroups());
//		}catch(Exception ex){
//			ex.printStackTrace();
//			MessageDialog.openError(getClient().getShell(), "Loading Groups", "Unable to load Groups from Vanilla Server : " + ex.getMessage());
//		}
		
		
		
		itemSelecterListener.setFormDef(formdef);
		itemSelecterComposite.addSelectionChangedListener(itemSelecterListener);
		repositorySelecterComposite.addListener(SWT.Selection, repositorySelecterListener);
		
		compositeFieldMapping.getClient().removeListener(SWT.Modify, fieldMappingListener);
		compositeFieldMapping.setInput(formdef);
		compositeFieldMapping.getClient().addListener(SWT.Modify, fieldMappingListener);
//		validatorViewer.addCheckStateListener(checkListener);
		
				
	}
	
	private void bindValues(){
		if (bindingCtx != null){
			bindingCtx.dispose();
		}
		
		for(IObservableValue o : listened){
			o.removeChangeListener(changeListener);
			if (!o.isDisposed()){
				o.dispose();
			}
		}
		listened.clear();
		bindingCtx = new DataBindingContext();
		
		
		
		
		UpdateValueStrategy booleanToText = new UpdateValueStrategy();
		booleanToText.setConverter(new BooleanToNumber());
		
		UpdateValueStrategy booleanToInt = new UpdateValueStrategy();
		booleanToInt.setConverter(new BooleanToInt());
		
		UpdateValueStrategy intToBoolean = new UpdateValueStrategy();
		intToBoolean.setConverter(new IntToBoolean());
		
		
		
			/*
			 * startDate
			 */
			IObservableValue myModel = PojoProperties.value(IFormDefinition.class, "startDate").observe(formDef); //$NON-NLS-1$
			IObservableValue myWidget = WidgetProperties.text(SWT.Modify).observe(startDate);
			try{
				UpdateValueStrategy textToDate = new UpdateValueStrategy();
				textToDate.setConverter(new TextToDateConverter("yyyy-MM-dd")); //$NON-NLS-1$
				
				UpdateValueStrategy dateToText = new UpdateValueStrategy();
				dateToText.setConverter(new DateToTextConverter("yyyy-MM-dd")); //$NON-NLS-1$
				bindingCtx.bindValue(myWidget, myModel, textToDate, dateToText);
				
			}catch(Exception ex){
				ex.printStackTrace();
			}
			myWidget.addChangeListener(changeListener);
			listened.add(myWidget);
			
			/*
			 * stopDate
			 */
			myModel = PojoProperties.value(IFormDefinition.class, "stopDate").observe(formDef); //$NON-NLS-1$
			myWidget = WidgetProperties.text(SWT.Modify).observe(stopDate);
			try{
				UpdateValueStrategy textToDate = new UpdateValueStrategy();
				textToDate.setConverter(new TextToDateConverter("yyyy-MM-dd")); //$NON-NLS-1$
				
				UpdateValueStrategy dateToText = new UpdateValueStrategy();
				dateToText.setConverter(new DateToTextConverter("yyyy-MM-dd")); //$NON-NLS-1$
				bindingCtx.bindValue(myWidget, myModel, textToDate, dateToText);
			}catch(Exception ex){
				ex.printStackTrace();
			}
			
			myWidget.addChangeListener(changeListener);
			listened.add(myWidget);
			
			/*
			 * creationDate
			 */
			myModel = PojoProperties.value(IFormDefinition.class, "creationDate").observe(formDef); //$NON-NLS-1$
			myWidget = WidgetProperties.text(SWT.Modify).observe(creationDate);
			try{
				UpdateValueStrategy textToDate = new UpdateValueStrategy();
				textToDate.setConverter(new TextToDateConverter("yyyy-MM-dd")); //$NON-NLS-1$
				
				UpdateValueStrategy dateToText = new UpdateValueStrategy();
				dateToText.setConverter(new DateToTextConverter("yyyy-MM-dd")); //$NON-NLS-1$
				bindingCtx.bindValue(myWidget, myModel, textToDate, dateToText);
			}catch(Exception ex){
				ex.printStackTrace();
			}
			
			myWidget.addChangeListener(changeListener);
			listened.add(myWidget);

			
			/*
			 * active
			 */
			myModel = PojoProperties.value(IFormDefinition.class, "activated").observe(formDef); //$NON-NLS-1$
			myWidget = WidgetProperties.selection().observe(active);
			bindingCtx.bindValue(myWidget, myModel);
			myWidget.addChangeListener(changeListener);
			listened.add(myWidget);

			
			/*
			 * fullyDesigned
			 */
			myModel = PojoProperties.value(IFormDefinition.class, "designed").observe(formDef); //$NON-NLS-1$
			myWidget = WidgetProperties.selection().observe(fullyDesigner);
			bindingCtx.bindValue(myWidget, myModel);//,booleanToInt, intToBoolean);
			myWidget.addChangeListener(changeListener);
			listened.add(myWidget);

			/*
			 * description
			 */
			myModel = PojoProperties.value(IFormDefinition.class, "description").observe(formDef); //$NON-NLS-1$
			myWidget = WidgetProperties.text(SWT.Modify).observe(description);
			bindingCtx.bindValue(myWidget, myModel);
			myWidget.addChangeListener(changeListener);
			listened.add(myWidget);
			
			
			/*
			 * version
			 */
			myModel = PojoProperties.value(IFormDefinition.class, "version").observe(formDef); //$NON-NLS-1$
			myWidget = WidgetProperties.text(SWT.Modify).observe(version);
			bindingCtx.bindValue(myWidget, myModel);
			myWidget.addChangeListener(changeListener);
			listened.add(myWidget);
		
		
		
	}

	public IFormDefinition getInput() {
		return formDef;
	}
	
	
	private void createContextMenu(){
		MenuManager mgr = new MenuManager();
		
		final Action addTable = new Action(Messages.CompositeFormDefinition_46){
			public void run(){
				DialogTargetTable  d = new DialogTargetTable(getClient().getShell());
				
				if (d.open() == DialogTargetTable.OK){
					((WritableList)availableTargetTables.getInput()).add(d.getTargetTable());
					
				}
			}
		};
		
		final Action delTable = new Action(Messages.CompositeFormDefinition_47){
			public void run(){
				
			}
		};
		
		mgr.add(addTable);
		mgr.add(delTable);
		
		mgr.addMenuListener(new IMenuListener() {
			
			@Override
			public void menuAboutToShow(IMenuManager manager) {
				delTable.setEnabled(!availableTargetTables.getSelection().isEmpty());
			}
		});
		
		availableTargetTables.getTable().setMenu(mgr.createContextMenu(availableTargetTables.getTable()));
	}
	
	

	public void setEnabled(boolean b) {
		availableTargetTables.getControl().setEnabled(b);
		targetTables.getControl().setEnabled(b);
		creationDate.setEnabled(b);
		compositeFieldMapping.getClient().setEnabled(b);
		description.setEnabled(b);
		fullyDesigner.setEnabled(b);
		itemSelecterComposite.setEnabled(b);
		repositorySelecterComposite.setEnabled(b);
		startDate.setEnabled(b);
		stopDate.setEnabled(b);
		version.setEnabled(b);
		
		
	}
}
