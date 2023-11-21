package metadata.client.model.composites;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import metadata.client.helper.GroupHelper;
import metadata.client.i18n.Messages;
import metadataclient.Activator;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import bpm.metadata.layer.logical.ICalculatedElement;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.layer.logical.IDataStreamElement.SubType;
import bpm.metadata.layer.logical.IDataStreamElement.Type;
import bpm.metadata.layer.logical.olap.UnitedOlapDataStreamElement;
import bpm.metadata.layer.physical.olap.UnitedOlapLevelColumn;
import bpm.metadata.layer.physical.olap.UnitedOlapMeasureColumn;

public class CompositeDataStreamElement extends Composite {
	private static final Color RED = new Color(Display.getDefault(), 255, 0, 0);
	
	private Text name, desc, fontName, textColor, backGroundColor, origin;
	private Text dataStreamOrigin;
	private Text formula, type;
	private Button calculated;
	private Button isKpi; 
	private Button indexable;
	
	private IDataStreamElement data;
	private boolean containApply = true;
	private Viewer viewer;
	
	private CheckboxTreeViewer tableVis, tableSec;
	private ComboViewer locale;
	private Text outputName;
	
	private Combo colType, colSubType;
	
//	private Button dimensionType, measureType, propertyType, undefinedType;
//	private Combo defaultMeasureBehavior;
	
	private Label errorLabel;
	private Button ok, cancel;
	private boolean isFilling = false;
	
	private HashMap<Locale, String> modifiedLocales = new HashMap<Locale, String>();

//	private Button countryType;
//
//	private Button cityType;
	
	public CompositeDataStreamElement(Composite parent, int style, Viewer viewer, boolean containApply, IDataStreamElement data) {
		super(parent, style);
		this.data = data;
		this.containApply = containApply;
		this.viewer = viewer;
		buildContent();
		fillData();
	}
	
	private void buildContent(){
		this.setLayout(new GridLayout(3, false));
		
		
		TabFolder tabFolder = new TabFolder(this, SWT.NONE);
		tabFolder.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));

		errorLabel = new Label(this, SWT.NONE);
    	errorLabel.setLayoutData(new GridData(GridData.FILL, GridData.END, true, false, 2, 1));
    	errorLabel.setForeground(RED);
    	errorLabel.setVisible(false);
		
		TabItem itemGal = new TabItem(tabFolder, SWT.NONE, 0);
		itemGal.setText(Messages.CompositeDataStreamElement_0); //$NON-NLS-1$
		itemGal.setControl(createGeneral(tabFolder));
    	
    	TabItem itemVis = new TabItem(tabFolder, SWT.NONE, 1);
    	itemVis.setText(Messages.CompositeDataStreamElement_1); //$NON-NLS-1$
    	itemVis.setControl(createVisibility(tabFolder));

    	TabItem itemSec = new TabItem(tabFolder, SWT.NONE, 1);
    	itemSec.setText(Messages.CompositeDataStreamElement_2); //$NON-NLS-1$
    	itemSec.setControl(createSecurity(tabFolder));
		
    	fillData();
	}
	
	
	private Control createToolbar(Composite parent){
		ToolBar toolbar = new ToolBar(parent, SWT.NONE);
		toolbar.setLayoutData(new GridData());
		
		ToolItem checkAll = new ToolItem(toolbar, SWT.PUSH);
		checkAll.setToolTipText("Check All"); //$NON-NLS-1$
		checkAll.setImage(Activator.getDefault().getImageRegistry().get("check")); //$NON-NLS-1$
		checkAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				
				tableSec.setAllChecked(true);
				for(Object o : ((IStructuredContentProvider)tableSec.getContentProvider()).getElements(tableSec.getInput())){
					data.setGranted(((Secu)o).groupName, true);
				}
				notifyListeners(SWT.Selection, new Event());
			}
		});
		
		ToolItem uncheckAll = new ToolItem(toolbar, SWT.PUSH);
		uncheckAll.setToolTipText("Uncheck All"); //$NON-NLS-1$
		uncheckAll.setImage(Activator.getDefault().getImageRegistry().get("uncheck")); //$NON-NLS-1$
		uncheckAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				tableSec.setAllChecked(false);
				for(Object o : ((IStructuredContentProvider)tableSec.getContentProvider()).getElements(tableSec.getInput())){
					data.setGranted(((Secu)o).groupName, false);
				}
				notifyListeners(SWT.Selection, new Event());
			}
		});
		
		return toolbar;
	}
	
	private Control createSecurity(TabFolder folder){
		Composite parent = new Composite(folder, SWT.NONE);
		parent.setLayout(new GridLayout(2, false));
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label l0 = new Label(parent, SWT.NONE);
		l0.setLayoutData(new GridData());
		l0.setText(Messages.CompositeDataStreamElement_3); //$NON-NLS-1$
		
		createToolbar(parent);
		
		tableSec = new CheckboxTreeViewer(parent, SWT.V_SCROLL | SWT.VIRTUAL);
		tableSec.getTree().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		
		tableSec.setContentProvider(new ITreeContentProvider(){

			public Object[] getElements(Object inputElement) {
				List<Secu> list = (List<Secu>) inputElement;
				return list.toArray(new Secu[list.size()]);
			}

			public void dispose() {
				
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
			}
			
			@Override
			public Object[] getChildren(Object parentElement) {
				return null;
			}

			@Override
			public Object getParent(Object element) {
				return null;
			}

			@Override
			public boolean hasChildren(Object element) {
				return false;
			}
		});
		
		tableSec.setLabelProvider(new LabelProvider(){

			@Override
			public String getText(Object element) {
				return ((Secu)element).groupName;
			}
			
		});
		
		tableSec.addCheckStateListener(new ICheckStateListener(){

			public void checkStateChanged(CheckStateChangedEvent event) {
				Secu s = (Secu)event.getElement();
				
				s.visible = event.getChecked();
				notifyListeners(SWT.Selection, new Event());
			}
			
		});
		
		
		//setInput
		List<Secu> l = getGroups();
		
		for(Secu s : l){
			if (data.isGrantedFor(s.groupName)){
				s.visible = true;
			}
			else{
				s.visible = false;
			}
		}
		
		tableSec.setInput(l);
		
		for(Secu s : l){
			tableSec.setChecked(s, s.visible);
		}

		
		
		return parent;
	}
	
	
	private Control createVisibility(TabFolder folder){
		Composite parent = new Composite(folder, SWT.NONE);
		parent.setLayout(new GridLayout(1, true));
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label l0 = new Label(parent, SWT.NONE);
		l0.setLayoutData(new GridData());
		l0.setText(Messages.CompositeDataStreamElement_4); //$NON-NLS-1$
		
		tableVis = new CheckboxTreeViewer(parent, SWT.V_SCROLL | SWT.VIRTUAL);
		tableVis.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		
		tableVis.setContentProvider(new ITreeContentProvider(){

			public Object[] getElements(Object inputElement) {
				List<Secu> list = (List<Secu>) inputElement;
				return list.toArray(new Secu[list.size()]);
			}

			public void dispose() {
				
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
			}
			
			@Override
			public Object[] getChildren(Object parentElement) {
				return null;
			}

			@Override
			public Object getParent(Object element) {
				return null;
			}

			@Override
			public boolean hasChildren(Object element) {
				return false;
			}
			
		});
		
		tableVis.setLabelProvider(new LabelProvider(){

			@Override
			public String getText(Object element) {
				return ((Secu)element).groupName;
			}
			
		});
		
		tableVis.addCheckStateListener(new ICheckStateListener(){

			public void checkStateChanged(CheckStateChangedEvent event) {
				Secu s = (Secu)event.getElement();
				
				s.visible = event.getChecked();
				notifyListeners(SWT.Selection, new Event());
			}
			
		});
		
		
		//setInput
		List<Secu> l = getGroups();
		
		for(Secu s : l){
			if (data.isVisibleFor(s.groupName)){
				s.visible = true;
			}
			else{
				s.visible = false;
			}
		}
		
		tableVis.setInput(l);
		
		for(Secu s : l){
			tableVis.setChecked(s, s.visible);
		}

		
		
		return parent;
	}
	
	private Control createGeneral(TabFolder folder){
		
		ScrolledComposite scrolled = new ScrolledComposite(folder, SWT.V_SCROLL | SWT.H_SCROLL);
		scrolled.setAlwaysShowScrollBars(false);
		scrolled.setExpandHorizontal(true);
		scrolled.setExpandVertical(true);
		
		Composite parent = new Composite(scrolled, SWT.NONE);
		parent.setLayout(new GridLayout(3, true));
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label l = new Label(parent, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText(Messages.CompositeDataStreamElement_5); //$NON-NLS-1$
		
		name = new Text(parent, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		name.addModifyListener(new ModifyListener() {
			
			public void modifyText(ModifyEvent e) {
				try{
					for(IDataStreamElement s : data.getDataStream().getElements()){
						if ( s != null && s != data && s.getName().equals(name.getText())){
							setErrorMessage(Messages.CompositeDataStreamElement_14);
							break;
						}
						else{
							setErrorMessage(null);
						}
					}
					notifyListeners(SWT.Selection, new Event());
				}catch(Exception ex){
					ex.printStackTrace();
				}
				
				
				
				
			}
		});
		
		l = new Label(parent, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));
		l.setText(Messages.CompositeDataStreamElement_17); 

		dataStreamOrigin = new Text(parent, SWT.BORDER );
		dataStreamOrigin.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 2, 1));
		dataStreamOrigin.setEnabled(false);

		
		Label l2 = new Label(parent, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.FILL, false, true, 1, 2));
		l2.setText(Messages.CompositeDataStreamElement_6); //$NON-NLS-1$
		
		desc = new Text(parent, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		desc.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 2));
		desc.addModifyListener(new ModifyListener() {
			
			public void modifyText(ModifyEvent e) {
				notifyListeners(SWT.Selection, new Event());
				
			}
		});
				
//		SelectionListener radioListener = new SelectionAdapter(){
//
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				defaultMeasureBehavior.setEnabled(measureType.getSelection());
//				notifyListeners(SWT.Selection, new Event());
//			}
//			
//		};
		
		Label ltype = new Label(parent, SWT.NONE);
		ltype.setLayoutData(new GridData(GridData.BEGINNING, GridData.FILL, false, false, 1, 1));
		ltype.setText("Type"); //$NON-NLS-1$
		
		colType = new Combo(parent, SWT.READ_ONLY);
		colType.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 1));
		
		List<String> types = new ArrayList<>();
		types.add(Type.UNDEFINED.toString());
		types.add(Type.DIMENSION.toString());
		types.add(Type.GEO.toString());
		types.add(Type.DATE.toString());
		types.add(Type.MEASURE.toString());
		types.add(Type.PROPERTY.toString());
		colType.setItems(types.toArray(new String[types.size()]));
		
		colType.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				fillSubTypeList();
				notifyListeners(SWT.Selection, new Event());
			}
		});
		
		Label lstype = new Label(parent, SWT.NONE);
		lstype.setLayoutData(new GridData(GridData.BEGINNING, GridData.FILL, false, false, 1, 1));
		lstype.setText("SubType (parent column)"); //$NON-NLS-1$
		
		colSubType = new Combo(parent, SWT.READ_ONLY);
		colSubType.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 1));
		colSubType.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				notifyListeners(SWT.Selection, new Event());
			}
		});
//		undefinedType = new Button(parent, SWT.RADIO);
//		undefinedType.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 3, 1));
//		undefinedType.setText(IDataStreamElement.TYPE_NAME[IDataStreamElement.UNDEFINED_TYPE]);
//		undefinedType.setSelection(true);
//		undefinedType.addSelectionListener(radioListener);
//		
//		dimensionType = new Button(parent, SWT.RADIO);
//		dimensionType.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 3, 1));
//		dimensionType.setText(IDataStreamElement.TYPE_NAME[IDataStreamElement.DIMENSION_TYPE]);
//		dimensionType.addSelectionListener(radioListener);
//		
//		countryType = new Button(parent, SWT.RADIO);
//		countryType.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 3, 1));
//		countryType.setText(IDataStreamElement.TYPE_NAME[IDataStreamElement.COUNTRY_TYPE]);
//		countryType.addSelectionListener(radioListener);
//		
//		cityType = new Button(parent, SWT.RADIO);
//		cityType.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 3, 1));
//		cityType.setText(IDataStreamElement.TYPE_NAME[IDataStreamElement.CITY_TYPE]);
//		cityType.addSelectionListener(radioListener);
//		
//		measureType = new Button(parent, SWT.RADIO);
//		measureType.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 3, 1));
//		measureType.setText(IDataStreamElement.TYPE_NAME[IDataStreamElement.MEASURE_TYPE]);
//		measureType.addSelectionListener(radioListener);
//		
//		propertyType = new Button(parent, SWT.RADIO);
//		propertyType.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 3, 1));
//		propertyType.setText(IDataStreamElement.TYPE_NAME[IDataStreamElement.PROPERTY_TYPE]);
//		propertyType.addSelectionListener(radioListener);
//		
//		Label _l = new Label(parent, SWT.NONE);
//		_l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
//		_l.setText(Messages.CompositeDataStreamElement_27);
//		
//		defaultMeasureBehavior = new Combo(parent, SWT.READ_ONLY);
//		defaultMeasureBehavior.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
//		defaultMeasureBehavior.setItems(IDataStreamElement.MEASURE_DEFAULT_BEHAVIOR);
//		defaultMeasureBehavior.setEnabled(false);
//		defaultMeasureBehavior.addSelectionListener(new SelectionListener() {
//			
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				notifyListeners(SWT.Selection, new Event());
//			}
//			
//			@Override
//			public void widgetDefaultSelected(SelectionEvent e) {
//				
//			}
//		});
		
		Label l3 = new Label(parent, SWT.NONE);
		l3.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l3.setText(Messages.CompositeDataStreamElement_7); //$NON-NLS-1$
		
		locale = new ComboViewer(parent, SWT.READ_ONLY);
		locale.getCombo().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		locale.setContentProvider(new IStructuredContentProvider(){

			public Object[] getElements(Object inputElement) {
				List<Locale> l = (List<Locale>)inputElement;
				return l.toArray(new Locale[l.size()]);
			}

			public void dispose() {
				
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
			}
			
		});
		locale.setLabelProvider(new LabelProvider(){

			@Override
			public String getText(Object element) {
				return ((Locale)element).getDisplayName();
			}
			
		});
		locale.setInput(Activator.getDefault().getModel().getLocales());
		try{
			locale.setSelection(new StructuredSelection(Locale.getDefault()));
		}catch(Exception ex){
			
		}
		locale.addSelectionChangedListener(new ISelectionChangedListener(){

			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection ss = (IStructuredSelection)locale.getSelection();
				
				if (ss.isEmpty()){
					return;
				}
				
				String s = modifiedLocales.get((Locale)ss.getFirstElement());
				if (s == null){
					s = data.getOuputName((Locale)ss.getFirstElement());
				}
				if (s == null){
					s= ""; //$NON-NLS-1$
				}
				outputName.setText(s);
				
			};		
		});
		
		
		Label l4 = new Label(parent, SWT.NONE);
		l4.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l4.setText(Messages.CompositeDataStreamElement_9); //$NON-NLS-1$
		
		outputName = new Text(parent, SWT.BORDER);
		outputName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		outputName.addModifyListener(new ModifyListener(){

			public void modifyText(ModifyEvent e) {
				IStructuredSelection ss = (IStructuredSelection)locale.getSelection();
				

				if (ss.isEmpty()){
					modifiedLocales.put(Locale.getDefault(), outputName.getText());
				}
				else{
					modifiedLocales.put((Locale)ss.getFirstElement(), outputName.getText());
				}
				
				notifyListeners(SWT.Selection, new Event());

			}
			
		});
		
		Label l6 = new Label(parent, SWT.NONE);
		l6.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l6.setText(Messages.CompositeDataStreamElement_10); //$NON-NLS-1$
		
		type = new Text(parent, SWT.BORDER);
		type.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 1));
		type.setEnabled(false);
		
		isKpi = new Button(parent, SWT.CHECK);
		isKpi.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 3, 1));
		isKpi.setText(Messages.CompositeDataStreamElement_28);
		isKpi.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				notifyListeners(SWT.Selection, new Event());
			}
		});
		
		indexable = new Button(parent, SWT.CHECK);
		indexable.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 3, 1));
		indexable.setText(Messages.CompositeDataStreamElement_29);
		indexable.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				notifyListeners(SWT.Selection, new Event());
			}
		});
		
		Label l8 = new Label(parent, SWT.NONE);
		l8.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l8.setText(Messages.CompositeDataStreamElement_11); //$NON-NLS-1$
		
		fontName = new Text(parent, SWT.BORDER);
		fontName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		Button font = new Button(parent, SWT.PUSH);
		font.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		font.setText("..."); //$NON-NLS-1$
		font.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				FontDialog d = new FontDialog(getShell());
				FontData fontD = d.open();
				
				if (fontD != null){
					fontName.setText(fontD.getName());
				}
				notifyListeners(SWT.Selection, new Event());
			}
			
		});
		
		Label l9 = new Label(parent, SWT.NONE);
		l9.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l9.setText(Messages.CompositeDataStreamElement_13); //$NON-NLS-1$
		
		textColor = new Text(parent, SWT.BORDER);
		textColor.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		Button col = new Button(parent, SWT.PUSH);
		col.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		col.setText("..."); //$NON-NLS-1$
		col.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				ColorDialog d = new ColorDialog(getShell());
				try{
					
					d.setRGB(getColor(data.getTextColor()));
				}catch(Exception ex){
					Activator.getLogger().error(Messages.CompositeDataStreamElement_15, ex); //$NON-NLS-1$
				}
				
				RGB rgb = d.open();
				textColor.setBackground(new Color(Display.getCurrent(), rgb));
				
				notifyListeners(SWT.Selection, new Event());
			}
			
		});
		
		Label l5 = new Label(parent, SWT.NONE);
		l5.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l5.setText(Messages.CompositeDataStreamElement_16); //$NON-NLS-1$
		
		backGroundColor = new Text(parent, SWT.BORDER);
		backGroundColor.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		Button bg = new Button(parent, SWT.PUSH);
		bg.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		bg.setText("..."); //$NON-NLS-1$
		bg.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				ColorDialog d = new ColorDialog(getShell());
				
				try{
					d.setRGB(getColor(data.getBackgroundColor()));
				}catch(Exception ex){
					Activator.getLogger().error(Messages.CompositeDataStreamElement_18, ex); //$NON-NLS-1$
				}
				
				RGB rgb = d.open();
				backGroundColor.setBackground(new Color(Display.getCurrent(), rgb));
				notifyListeners(SWT.Selection, new Event());
				
			}
		});
		
		calculated = new Button(parent, SWT.CHECK);
		calculated.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 3, 1));
		calculated.setText(Messages.CompositeDataStreamElement_19); //$NON-NLS-1$
		calculated.setEnabled(false);
		
		if (data instanceof ICalculatedElement){
			Label l7 = new Label(parent, SWT.NONE);
			l7.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 2));
			l7.setText(Messages.CompositeDataStreamElement_20); //$NON-NLS-1$
			
			formula = new Text(parent, SWT.BORDER | SWT.MULTI | SWT.WRAP);
			formula.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 2));
			formula.setEnabled(false);
		}
		else{
			Label l7 = new Label(parent, SWT.NONE);
			l7.setLayoutData(new GridData());
			l7.setText(Messages.CompositeDataStreamElement_21); //$NON-NLS-1$
			
			origin = new Text(parent, SWT.BORDER);
			origin.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
			origin.setEnabled(false);
		}
		
		
		if (containApply){
			Composite bar = new Composite(this, SWT.NONE);
			bar.setLayout(new GridLayout(2, true));
			bar.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 3, 1));
			
			ok = new Button(bar, SWT.PUSH);
			ok.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
			ok.setText(Messages.CompositeDataStreamElement_22); //$NON-NLS-1$
			ok.setEnabled(false);
			ok.addSelectionListener(new SelectionAdapter(){

				@Override
				public void widgetSelected(SelectionEvent e) {
					setData();
					if (viewer!= null){
						viewer.refresh();
						Activator.getDefault().setChanged();
					}
					ok.setEnabled(false);
					cancel.setEnabled(false);
				}
				
			});
			
			cancel = new Button(bar, SWT.PUSH);
			cancel.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
			cancel.setText(Messages.CompositeDataStreamElement_23); //$NON-NLS-1$
			cancel.setEnabled(false);
			cancel.addSelectionListener(new SelectionAdapter(){

				@Override
				public void widgetSelected(SelectionEvent e) {
					fillData();
					ok.setEnabled(false);
					cancel.setEnabled(false);
				}
				
			});
			addListener(SWT.Selection, new Listener() {
				
				public void handleEvent(Event event) {
					if (!isFilling){
						ok.setEnabled(isFilled());
						cancel.setEnabled(true);
					}
					
				}

				
			});
		}
		

		
		scrolled.setContent(parent);
		scrolled.setMinSize(parent.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		return scrolled;
	}
	
	private void fillData(){
		boolean error = false;
		
		if (data instanceof UnitedOlapDataStreamElement){
			colSubType.setEnabled(false);
			colType.setEnabled(false);
		}
		
		if (data != null){
			isFilling = true;
			name.setText(data.getName());
			desc.setText(data.getDescription());
			
			fontName.setText(data.getFontName());
			locale.setSelection(new StructuredSelection(Locale.getDefault()));
			outputName.setText(data.getOuputName(Locale.getDefault()));
			
			isKpi.setSelection(data.isKpi());
			indexable.setSelection(data.isIndexable());
			try{
				dataStreamOrigin.setText(data.getDataStream().getName());
			}catch(Exception ex){
				ex.printStackTrace();
			}
			
			try {
				textColor.setBackground(new Color(Display.getCurrent(), getColor(data.getTextColor())));
			} catch (Exception e1) {
				Activator.getLogger().error(Messages.CompositeDataStreamElement_24, e1); //$NON-NLS-1$
				textColor.setBackground(new Color(Display.getCurrent(), new RGB(0, 0, 0)));
			}
			
			try {
				backGroundColor.setBackground(new Color(Display.getCurrent(), getColor(data.getBackgroundColor())));
			} catch (Exception e1) {
				Activator.getLogger().error(Messages.CompositeDataStreamElement_25, e1); //$NON-NLS-1$
				backGroundColor.setBackground(new Color(Display.getCurrent(), new RGB(255, 255, 255)));
			}
			//XXX
			switch(data.getType()){
				case ADRESSE:
				case COMMUNE:
				case COUNTRY:
				case POSTALCODE:
				case REGION:
				case ZONEID:
				case GEOLOCAL:
				case LATITUDE:
				case LONGITUDE:
					colType.setText(Type.GEO.toString());
					fillSubTypeList();
					colSubType.setText(data.getType().toString());
					break;
				case AVG:
				case COUNT:
				case DISTINCT:
				case MAX:
				case MIN:
				case SUM:
					colType.setText(Type.MEASURE.toString());
					fillSubTypeList();
					colSubType.setText(data.getType().toString());
					break;
				case COMPLETE:
				case DAY:
				case MONTH:
				case QUARTER:
				case WEEK:
				case YEAR:
					colType.setText(Type.DATE.toString());
					fillSubTypeList();
					colSubType.setText(data.getType().toString());
					break;
				case DIMENSION:
					colType.setText(Type.DIMENSION.toString());
					fillSubTypeList();
					colSubType.setText(data.getParentDimension());
					break;
				case PROPERTY:
					colType.setText(Type.PROPERTY.toString());
					fillSubTypeList();
					colSubType.setText(data.getParentDimension());
					break;
				case UNDEFINED:
					colType.setText(Type.UNDEFINED.toString());
					fillSubTypeList();
					colSubType.setText(SubType.UNDEFINED.toString());
					break;
			}
//			case IDataStreamElement.UNDEFINED_TYPE:
//				dimensionType.setSelection(false);
//				measureType.setSelection(false);
//				propertyType.setSelection(false);
//				undefinedType.setSelection(true);
//				countryType.setSelection(false);
//				cityType.setSelection(false);
//				break;
//			case IDataStreamElement.PROPERTY_TYPE:
//				dimensionType.setSelection(false);
//				measureType.setSelection(false);
//				propertyType.setSelection(true);
//				undefinedType.setSelection(false);
//				countryType.setSelection(false);
//				cityType.setSelection(false);
//				break;
//				
//			case IDataStreamElement.DIMENSION_TYPE:
//				dimensionType.setSelection(true);
//				measureType.setSelection(false);
//				propertyType.setSelection(false);
//				undefinedType.setSelection(false);
//				countryType.setSelection(false);
//				cityType.setSelection(false);
//				break;
//				
//			case IDataStreamElement.MEASURE_TYPE:
//				dimensionType.setSelection(false);
//				measureType.setSelection(true);
//				propertyType.setSelection(false);
//				undefinedType.setSelection(false);
//				defaultMeasureBehavior.setEnabled(true);
//				defaultMeasureBehavior.select(data.getDefaultMeasureBehavior());
//				countryType.setSelection(false);
//				cityType.setSelection(false);
//				break;
//			case IDataStreamElement.COUNTRY_TYPE:
//				dimensionType.setSelection(false);
//				measureType.setSelection(false);
//				propertyType.setSelection(false);
//				undefinedType.setSelection(false);
//				countryType.setSelection(true);
//				cityType.setSelection(false);
//				break;			
//			case IDataStreamElement.CITY_TYPE:
//					dimensionType.setSelection(false);
//					measureType.setSelection(false);
//					propertyType.setSelection(false);
//					undefinedType.setSelection(false);
//					countryType.setSelection(false);
//					cityType.setSelection(true);
//					break;
//			}
			
			if (origin != null){
				try{

					origin.setText(data.getOrigin().getName());

				}catch(Exception ex){
					error = true;
					setErrorMessage(Messages.CompositeDataStreamElement_30 + data.getOriginName());
				}
				
				
				calculated.setSelection(false);
				try {
					if (!(data.getOrigin() instanceof UnitedOlapLevelColumn) 
							&&
						!(data.getOrigin() instanceof UnitedOlapMeasureColumn)){
						
						type.setText(data.getOrigin().getJavaClass().getSimpleName());
						
						
					}
					
				} catch (Exception e) {
					Activator.getLogger().error(Messages.CompositeDataStreamElement_26, e); //$NON-NLS-1$
				}
			}
			else{
				formula.setText(((ICalculatedElement)data).getFormula());
				calculated.setSelection(true);
				try{
					type.setText(((ICalculatedElement)data).getJavaClassName().substring(((ICalculatedElement)data).getJavaClassName().lastIndexOf(".") + 1)); //$NON-NLS-1$
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
			for(Secu s : (List<Secu>)tableSec.getInput()){
				if (data.isGrantedFor(s.groupName)){
					s.visible = true;
					
					tableSec.setChecked(s, true);
				}
				else{
					s.visible = false;
					tableSec.setChecked(s, false);
				}
			}
			
			List<Secu> i =  (List<Secu>)tableSec.getInput();
			for(Secu s : i){
				tableSec.setChecked(s, s.visible);
				
			}
			viewer.refresh();

			Object[] c = tableSec.getCheckedElements();
			
			
			//fill Visibility
			for(Secu s : (List<Secu>)tableVis.getInput()){
				if (data.isVisibleFor(s.groupName)){
					s.visible = true;
					
					tableVis.setChecked(s, true);
					
				}
				else{
					s.visible = false;
					tableVis.setChecked(s, false);
					
				}
			}
			
			i =  (List<Secu>)tableVis.getInput();
			for(Secu s : i){
				tableVis.setChecked(s, s.visible);
				
			}
			viewer.refresh();

			c = tableVis.getCheckedElements();
			isFilling = false;
			
			if (!error){
				setErrorMessage(null);
			}
			
			modifiedLocales = new HashMap<Locale, String>();
		}
	}
	
	public void setData () {
		data.setName(name.getText());
		data.setDescription(desc.getText());
		data.setBackgroundColor(getColor(backGroundColor.getBackground().getRGB()));
		data.setTextColor(getColor(textColor.getBackground().getRGB()));
		data.setFontName(fontName.getText());
		data.setIsKpi(isKpi.getSelection());
		data.setIndexable(indexable.getSelection());
		
		for(Secu s : (List<Secu>)tableVis.getInput()){
			data.setVisible(s.groupName, tableVis.getChecked(s));
		}
		
		for(Secu s : (List<Secu>)tableSec.getInput()){
			data.setGranted(s.groupName, tableSec.getChecked(s));
		}

		String t = colType.getText();
		Type type = Type.valueOf(t);
		SubType subType = null;
		switch(type) {
			case DATE:
			case GEO:
			case MEASURE:
				subType = SubType.valueOf(colSubType.getText());
				break;
			case DIMENSION:
				subType = SubType.DIMENSION;
				data.setParentDimension(colSubType.getText());
				break;
			case PROPERTY:
				subType = SubType.PROPERTY;
				data.setParentDimension(colSubType.getText());
				break;
			case UNDEFINED:
				subType = SubType.UNDEFINED;
				break;
			default:
				break;
		}
		
		data.setType(subType);
		
		//XXX
//		if (dimensionType.getSelection()){
//			data.setType(IDataStreamElement.DIMENSION_TYPE);
//		}
//		else if (measureType.getSelection()){
//			data.setType(IDataStreamElement.MEASURE_TYPE);
//			data.setDefaultMeasureBehavior(defaultMeasureBehavior.getSelectionIndex());
//		}
//		else if (undefinedType.getSelection()){
//			data.setType(IDataStreamElement.UNDEFINED_TYPE);
//		}
//		else if (propertyType.getSelection()){
//			data.setType(IDataStreamElement.PROPERTY_TYPE);
//		}
//		else if (countryType.getSelection()){
//			data.setType(IDataStreamElement.COUNTRY_TYPE);
//		}
//		else if (cityType.getSelection()){
//			data.setType(IDataStreamElement.CITY_TYPE);
//		}
		
		if(modifiedLocales != null && !modifiedLocales.isEmpty()) {
			for(Locale loc : modifiedLocales.keySet()) {
				data.setOutputName(loc, modifiedLocales.get(loc));
			}
		}
	}
	
	private String getColor(RGB rgb){
		if (rgb != null){
			String r = Integer.toHexString(rgb.red);
			String g = Integer.toHexString(rgb.green);
			String b = Integer.toHexString(rgb.blue);
			
			return r + g + b;
		}
		else{
			return "000000"; //$NON-NLS-1$
		}
	}
	

	private RGB getColor(String color) throws Exception{
		int r = Integer.parseInt(color.substring(0, 2), 16);
		int g = Integer.parseInt(color.substring(2, 4), 16);
		int b = Integer.parseInt(color.substring(4), 16);
		
		RGB rgb = new RGB(r, g, b);
		
		return rgb;
	}
	
	private class Secu{
		public String groupName;
		Boolean visible;
	}
	
	
	private List<Secu> getGroups(){
		
		List<Secu> l = new ArrayList<Secu>();
		for(String o : GroupHelper.getGroups(0, 0)){ 
			Secu s = new Secu();
			s.visible = true;
			s.groupName = o; 
			l.add(s);
		}
		return l;
		
	}
	
	private void setErrorMessage(String message){
		if (message == null){
			errorLabel.setVisible(false);
		}
		else{
			errorLabel.setText(message);
			errorLabel.setVisible(true);
		}
		
	}
	private boolean isFilled() {
		return !errorLabel.isVisible();
	}
	
	private void fillSubTypeList() {
		Type selectedType = Type.valueOf(colType.getText());
		colSubType.removeAll();
		switch(selectedType) {
			case DATE:
			case GEO:
			case MEASURE:
			case UNDEFINED:
				for(SubType t : selectedType.getSubtypes()) {
					colSubType.add(t.toString());
				}
				break;
			case DIMENSION:
			case PROPERTY:
				for(IDataStreamElement e : data.getDataStream().getElements()) {
					if(!e.getName().equals(data.getName())) {
						colSubType.add(e.getOuputName());
					}
				}
			default:
				break;
		}
	}
}
