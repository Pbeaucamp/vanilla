package bpm.norparena.mapmanager.fusionmap.wizard;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.norparena.mapmanager.Activator;
import bpm.norparena.mapmanager.Messages;
import bpm.norparena.mapmanager.fusionmap.viewers.FusionMapEntityLabelProvider;
import bpm.norparena.mapmanager.fusionmap.viewers.FusionMapEntityLabelProvider.FusionMapEntityAttribute;
import bpm.norparena.mapmanager.icons.Icons;
import bpm.vanilla.map.core.design.fusionmap.IFusionMapObject;
import bpm.vanilla.map.core.design.fusionmap.IFusionMapSpecificationEntity;

public class FusionMapObjectPage extends WizardPage{

	private ComboViewer comboSwfType;
	
	private static class MyCellEditor extends EditingSupport{
		static final int TYPE_INTERNAL_ID = 0;
		static final int TYPE_SHORT_NAME = 1;
		static final int TYPE_LONG_NAME = 2;
		
		
		
		private TextCellEditor editor;
		private int type;
		public MyCellEditor(ColumnViewer viewer, int type) {
			super(viewer);
			editor = new TextCellEditor((Composite)viewer.getControl());
			this.type = type;
		}

		
		@Override
		protected void setValue(Object element, Object value) {
			IFusionMapSpecificationEntity entity = (IFusionMapSpecificationEntity)element;
			switch (type) {
			case TYPE_INTERNAL_ID:
				entity.setFusionMapInternalId(value + ""); //$NON-NLS-1$
				break;

			case TYPE_LONG_NAME:
				entity.setFusionMapLongName(value + ""); //$NON-NLS-1$
				break;
			case TYPE_SHORT_NAME:
				entity.setFusionMapShortName(value + ""); //$NON-NLS-1$
				break;
			}
			
			getViewer().refresh();
		}
		
		@Override
		protected Object getValue(Object element) {
			
			IFusionMapSpecificationEntity entity = (IFusionMapSpecificationEntity)element;
			
			switch (type) {
			case TYPE_INTERNAL_ID:
				return entity.getFusionMapInternalId();

			case TYPE_LONG_NAME:
				return entity.getFusionMapLongName();
			case TYPE_SHORT_NAME:
				return entity.getFusionMapShortName();
			}
			return null;
		}
		
		@Override
		protected CellEditor getCellEditor(Object element) {
			return editor;
		}
		
		@Override
		protected boolean canEdit(Object element) {
			return true;
		}
	}
	
	
	private Text name;
	private Text description;
	private Text swfFileName;
	
	private TableViewer viewer;
	
	private ModifyListener textListener = new ModifyListener() {
		
		@Override
		public void modifyText(ModifyEvent e) {
			getContainer().updateButtons();
			
		}
	};
	
	private List<IFusionMapSpecificationEntity> entities = new ArrayList<IFusionMapSpecificationEntity>(); 
	
	protected FusionMapObjectPage(String pageName) {
		super(pageName);
		
	}

	@Override
	public void createControl(Composite parent) {
		setControl(createContent(parent));
	}
	
	private Composite createContent(Composite parent){
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(3, false));
		
		Label l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText(Messages.FusionMapObjectPage_3);
		
		name = new Text(main, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		name.addModifyListener(textListener);
		
		l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText(Messages.FusionMapObjectPage_4);
		
		description = new Text(main, SWT.BORDER | SWT.MULTI | SWT.WRAP);
		description.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 1));
		
		l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText(Messages.FusionMapObjectPage_5);
		
		swfFileName = new Text(main, SWT.BORDER);
		swfFileName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		swfFileName.setEnabled(false);
		swfFileName.addModifyListener(textListener);
		
		Button b = new Button(main, SWT.PUSH);
		b.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		b.setText("..."); //$NON-NLS-1$
		b.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(getShell(), SWT.OPEN);
				fd.setFilterExtensions(new String[]{"*.swf"}); //$NON-NLS-1$
				
				String fName = fd.open();
				if (fName == null){
					return;
				}
				swfFileName.setText(fName);
			}
		});
		
		Label lblSwfType = new Label(main, SWT.NONE);
		lblSwfType.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		lblSwfType.setText(Messages.FusionMapObjectPage_8);
		
		this.comboSwfType = new ComboViewer(main, SWT.BORDER);
		this.comboSwfType.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		this.comboSwfType.setContentProvider(new ArrayContentProvider());
		this.comboSwfType.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				return (String) element;
			}
		});
		this.comboSwfType.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
//				if (mapDefinition != null){
//					
//					Object o = ((IStructuredSelection)DialogMapDefinition.this.comboSwfType.getSelection()).getFirstElement();
//					
//					mapDefinition.setMapType((String) o);
//				}
			}
		});
		
		String[] swftypes = new String[]{IFusionMapObject.FUSIONMAP_TYPE, IFusionMapObject.VANILLAMAP_TYPE};
		
		this.comboSwfType.setInput(swftypes);
		
		Composite t = new Composite(main, SWT.NONE);
		t.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 3, 1));
		t.setLayout(new GridLayout(2, false));
		
		
		Button add = new Button(t, SWT.PUSH);
		add.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));
		add.setImage(Activator.getDefault().getImageRegistry().get(Icons.ADD));
		add.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				try{
					IFusionMapSpecificationEntity entity = Activator.getDefault().getFactoryFusionMap().createFusionMapSpecificationEntity();
					entity.setFusionMapInternalId("--- Internal Id ---"); //$NON-NLS-1$
					entity.setFusionMapLongName("--- Long Name --- "); //$NON-NLS-1$
					entity.setFusionMapShortName("--- Short Name --- "); //$NON-NLS-1$
					
					entities.add(entity);
					viewer.refresh();
				}catch(Exception ex){
					ex.printStackTrace();
					MessageDialog.openError(getShell(), Messages.FusionMapObjectPage_12, ex.getMessage());
				}
				getContainer().updateButtons();
			}
		});
		
		
		viewer = new TableViewer(t, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);
		viewer.setContentProvider(new ArrayContentProvider());
		viewer.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 3));
		viewer.getTable().setHeaderVisible(true);
		viewer.getTable().setLinesVisible(true);
		viewer.setInput(entities);
		
		
		Button loadCsv = new Button(t, SWT.PUSH);
		loadCsv.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));
//		loadCsv.setImage(Activator.getDefault().getImageRegistry().get(Icons.DEL));
		loadCsv.setText(Messages.FusionMapObjectPage_13);
		loadCsv.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				DialogCsvLoader dial = new DialogCsvLoader(getShell());
				
				if (dial.open() == DialogCsvLoader.OK){
					for(IFusionMapSpecificationEntity entity : dial.getResult()){
						entities.add(entity);
					}
				}
				viewer.refresh();
			}
		});
		
		
		Button del = new Button(t, SWT.PUSH);
		del.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));
		del.setImage(Activator.getDefault().getImageRegistry().get(Icons.DEL));

		del.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				for(Object o : ((IStructuredSelection)viewer.getSelection()).toList()){
					entities.remove(o);
				}
				viewer.refresh();
				getContainer().updateButtons();
			}
			
		});

		//TODO add controls to load files to easily create the mappings
		
		
		
		TableViewerColumn col = new TableViewerColumn(viewer, SWT.LEFT);
		col.getColumn().setText(Messages.FusionMapObjectPage_14);
		col.getColumn().setWidth(200);
		col.setLabelProvider(new FusionMapEntityLabelProvider(FusionMapEntityAttribute.InternalId));
		col.setEditingSupport(new MyCellEditor(viewer, MyCellEditor.TYPE_INTERNAL_ID));
		
		
		col = new TableViewerColumn(viewer, SWT.LEFT);
		col.getColumn().setText(Messages.FusionMapObjectPage_15);
		col.getColumn().setWidth(200);
		col.setLabelProvider(new FusionMapEntityLabelProvider(FusionMapEntityAttribute.LongName));
		col.setEditingSupport(new MyCellEditor(viewer, MyCellEditor.TYPE_LONG_NAME));
		
		
		col = new TableViewerColumn(viewer, SWT.LEFT);
		col.getColumn().setText(Messages.FusionMapObjectPage_16);
		col.getColumn().setWidth(200);
		col.setLabelProvider(new FusionMapEntityLabelProvider(FusionMapEntityAttribute.ShortName));
		col.setEditingSupport(new MyCellEditor(viewer, MyCellEditor.TYPE_SHORT_NAME));

		return main;
		
	}

	
	@Override
	public boolean isPageComplete() {
		if (!name.getText().trim().isEmpty() && !swfFileName.getText().isEmpty()&& !entities.isEmpty()){
			return true;
		}
		return false;
	}


	public IFusionMapObject getFusionMap()throws Exception{
		IFusionMapObject fusionMap = Activator.getDefault().getFactoryFusionMap().createFusionMapObject();
		
		for(IFusionMapSpecificationEntity e : entities){
			fusionMap.addSpecificationEntity(e);
		}
		
		fusionMap.setDescription(description.getText());
		fusionMap.setName(name.getText());
		fusionMap.setSwfFileName(swfFileName.getText());
		if(!comboSwfType.getSelection().isEmpty()) {
			
			String t = (String) ((IStructuredSelection)comboSwfType.getSelection()).getFirstElement();
			fusionMap.setType(t);
			
		}
		else {
			fusionMap.setType(IFusionMapObject.FUSIONMAP_TYPE);
		}
		
		return fusionMap;
		
	}
}
