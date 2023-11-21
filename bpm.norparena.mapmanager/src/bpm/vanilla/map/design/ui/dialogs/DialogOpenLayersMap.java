package bpm.vanilla.map.design.ui.dialogs;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import bpm.norparena.mapmanager.Activator;
import bpm.norparena.mapmanager.Messages;
import bpm.vanilla.map.core.design.IMapDefinition;
import bpm.vanilla.map.core.design.openlayers.IOpenLayersMapObject;
import bpm.vanilla.map.model.openlayers.impl.OpenLayersMapObject;

public class DialogOpenLayersMap extends Dialog {

	private IMapDefinition mapDefinition;
	private boolean update;
	
	private Text txtName;
	private Text txtDesc;
	private ComboViewer comboOlMap;
	private Button btnAddOlMap;
	private ComboViewer comboMapType;
	
	protected DialogOpenLayersMap(Shell parentShell) {
		super(parentShell);
		this.setShellStyle(SWT.SHELL_TRIM);
	}

	public DialogOpenLayersMap(Shell shell, IMapDefinition mapDef, boolean update) {
		super(shell);
		this.mapDefinition = mapDef;
		this.update = update;
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		
		newShell.setText(Messages.DialogOpenLayersMap_0);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		
		Composite mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayout(new GridLayout(3, false));
		mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Label lblName = new Label(mainComposite, SWT.NONE);
		lblName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lblName.setText(Messages.DialogOpenLayersMap_1);
		
		txtName = new Text(mainComposite, SWT.BORDER);
		txtName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
		
		Label lblDesc = new Label(mainComposite, SWT.NONE);
		lblDesc.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lblDesc.setText(Messages.DialogOpenLayersMap_2);
		
		txtDesc = new Text(mainComposite, SWT.MULTI|SWT.BORDER);
		txtDesc.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
		
		Label lblOlMap = new Label(mainComposite, SWT.NONE);
		lblOlMap.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lblOlMap.setText(Messages.DialogOpenLayersMap_3);
		
		comboOlMap = new ComboViewer(mainComposite, SWT.DROP_DOWN|SWT.PUSH);
		comboOlMap.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		comboOlMap.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				return ((IOpenLayersMapObject)element).getName();
			}
		});
		comboOlMap.setContentProvider(new ArrayContentProvider());
		comboOlMap.setInput(getMaps());
		
		btnAddOlMap = new Button(mainComposite, SWT.PUSH);
		btnAddOlMap.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		btnAddOlMap.setText(Messages.DialogOpenLayersMap_4);
		btnAddOlMap.addSelectionListener(new AddOpenLayersMapListener());
		
		Label lblMapType = new Label(mainComposite, SWT.NONE);
		lblMapType.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		lblMapType.setText(Messages.DialogOpenLayersMap_5);
		
		this.comboMapType = new ComboViewer(mainComposite, SWT.DROP_DOWN|SWT.PUSH);
		this.comboMapType.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		this.comboMapType.setContentProvider(new ArrayContentProvider());
		this.comboMapType.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				return (String) element;
			}
		});
		this.comboMapType.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (mapDefinition != null){
					
					Object o = ((IStructuredSelection)DialogOpenLayersMap.this.comboMapType.getSelection()).getFirstElement();
					
					mapDefinition.setMapType((String) o);
				}
			}
		});
		
		String[] types = new String[]{IMapDefinition.MAP_TYPE_CLASSIC, IMapDefinition.MAP_TYPE_FM};
		
		this.comboMapType.setInput(types);
		if(mapDefinition != null) {
			if(mapDefinition.getMapType() != null && mapDefinition.getMapType().equals(IMapDefinition.MAP_TYPE_FM)) {
				this.comboMapType.getCombo().select(1);
			}
			else {
				this.comboMapType.getCombo().select(0);
			}
		}
		
		if(update) {
			initData();
		}
		
		return mainComposite;
	}

	private void initData() {
		txtDesc.setText(mapDefinition.getDescription());
		txtName.setText(mapDefinition.getLabel());
		List<IOpenLayersMapObject> maps = (List<IOpenLayersMapObject>) comboOlMap.getInput();
		for(int i = 0 ; i < maps.size() ; i++) {
			if(mapDefinition.getOpenLayersObjectId().equals(maps.get(i).getId())) {
				comboOlMap.getCombo().select(i);
				break;
			}
		}
	}

	private Object getMaps() {
		List<IOpenLayersMapObject> maps = null;
		try {
			maps = Activator.getDefault().getDefinitionService().getOpenLayersMapObjects();
		} catch (Exception e) {
			MessageDialog.openError(getShell(), Messages.DialogOpenLayersMap_6, Messages.DialogOpenLayersMap_7 + e.getMessage());
			e.printStackTrace();
		}
		return maps;
	}

	@Override
	protected void okPressed() {
		
		String name = txtName.getText();
		String desc = txtDesc.getText();
		int olmoId = ((IOpenLayersMapObject) ((IStructuredSelection)comboOlMap.getSelection()).getFirstElement()).getId();
		
		mapDefinition.setDescription(desc);
		mapDefinition.setLabel(name);
		mapDefinition.setOpenLayersObjectId(olmoId);
		
		try {
			Activator.getDefault().getDefinitionService().saveMapDefinition(mapDefinition);
			super.okPressed();
		} catch (Exception e) {
			MessageDialog.openError(getShell(), Messages.DialogOpenLayersMap_8, Messages.DialogOpenLayersMap_9 + e.getMessage());
			e.printStackTrace();
		}
	}
	
	private class AddOpenLayersMapListener implements SelectionListener {
		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			
		}

		@Override
		public void widgetSelected(SelectionEvent e) {
			DialogAddOpenLayersMap dial = new DialogAddOpenLayersMap(getShell(), new OpenLayersMapObject());
			if(dial.open() == Dialog.OK) {
				comboOlMap.setInput(getMaps());
			}
		}
	}

	@Override
	protected void initializeBounds() {
		getShell().setSize(600, 400);
	}
	
	
}
