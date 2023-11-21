package bpm.vanilla.map.design.ui.dialogs;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import bpm.norparena.mapmanager.Activator;
import bpm.norparena.mapmanager.Messages;
import bpm.vanilla.map.core.design.openlayers.IOpenLayersMapObject;
import bpm.vanilla.map.core.design.openlayers.IOpenLayersMapSpecificationEntity;
import bpm.vanilla.map.design.ui.composite.WMSMapComposite;
import bpm.vanilla.map.model.openlayers.impl.OpenLayersEntityGenerator;

public class DialogAddOpenLayersMap extends Dialog {
	
	private Text txtName;
	private ComboViewer comboLayerType;
	private Text txtXml;
	private Button btnImport;
	
	private Composite typeComposite;
	private Composite mainComposite;
	
	private FileDialog fileDialog;
	
	private List<IOpenLayersMapSpecificationEntity> entities;
	
	private IOpenLayersMapObject map;
	
	public DialogAddOpenLayersMap(Shell parentShell, IOpenLayersMapObject map) {
		super(parentShell);
		this.setShellStyle(SWT.SHELL_TRIM);
		this.map = map;
		if(map.getEntities() != null) {
			entities = map.getEntities();
		}
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		
		newShell.setText(Messages.DialogAddOpenLayersMap_0);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		
		mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayout(new GridLayout(3, false));
		mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Label lblName = new Label(mainComposite, SWT.NONE);
		lblName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lblName.setText(Messages.DialogAddOpenLayersMap_1);
		
		txtName = new Text(mainComposite, SWT.BORDER);
		txtName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
		
		Label lblXml = new Label(mainComposite, SWT.NONE);
		lblXml.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lblXml.setText(Messages.DialogAddOpenLayersMap_2);
		
		txtXml = new Text(mainComposite, SWT.READ_ONLY|SWT.BORDER);
		txtXml.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		btnImport = new Button(mainComposite, SWT.PUSH);
		btnImport.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		btnImport.setText(Messages.DialogAddOpenLayersMap_3);
		btnImport.addSelectionListener(new ImportSelectionListener());
		
		Label lblType = new Label(mainComposite, SWT.NONE);
		lblType.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lblType.setText(Messages.DialogAddOpenLayersMap_4);
		
		comboLayerType = new ComboViewer(mainComposite, SWT.DROP_DOWN|SWT.PUSH);
		comboLayerType.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
		comboLayerType.setLabelProvider(new LabelProvider());
		comboLayerType.setContentProvider(new ArrayContentProvider());
		String[] input = new String[]{IOpenLayersMapObject.TYPE_WMS};
		comboLayerType.setInput(input);
		comboLayerType.addSelectionChangedListener(new TypeChangeListener());
		
		fillData(map);
		
		return mainComposite;
	}
	
	

	private void fillData(IOpenLayersMapObject mapOl) {
		if(mapOl.getId() != null) {
			txtName.setText(mapOl.getName());
			txtXml.setText(mapOl.getXml());
			
			comboLayerType.setSelection(new StructuredSelection(mapOl.getType()));
		}
	}

	@Override
	protected void okPressed() {
		
		map.setName(txtName.getText());
		map.setType((String) ((IStructuredSelection)comboLayerType.getSelection()).getFirstElement());
		map.setXml(txtXml.getText());
		
		if(typeComposite instanceof WMSMapComposite) {
			map.setProperties(((WMSMapComposite)typeComposite).getProperties());
		}
		
		if(entities != null && entities.size() > 0) {
			map.setEntities(entities);
		}
		
		try {
			Activator.getDefault().getDefinitionService().saveOpenLayersMapObject(map);
			super.okPressed();
		} catch (Exception e) {
			MessageDialog.openError(getShell(), Messages.DialogAddOpenLayersMap_5, Messages.DialogAddOpenLayersMap_6 + e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	protected void initializeBounds() {
		getShell().setSize(600, 400);
	}

	private class ImportSelectionListener implements SelectionListener {
		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			
		}

		@Override
		public void widgetSelected(SelectionEvent event) {
			fileDialog = new FileDialog(getShell());
			String filename = fileDialog.open();
			int mid= filename.lastIndexOf("."); //$NON-NLS-1$
			String ext=filename.substring(mid+1,filename.length());
			if(ext != null && ext.equalsIgnoreCase("kml")) { //$NON-NLS-1$
				try {
					FileInputStream input = new FileInputStream(new File(filename));
					String xml = IOUtils.toString(input, "UTF-8"); //$NON-NLS-1$
					
					OpenLayersEntityGenerator generator = new OpenLayersEntityGenerator(xml, true);
					entities = generator.createEntities();
					
					txtXml.setText(generator.getNewXml());
				} catch (Exception e) {
					MessageDialog.openError(getShell(), Messages.DialogAddOpenLayersMap_10, Messages.DialogAddOpenLayersMap_11 + e.getMessage());
				}
			}
			else {
				try {
					FileInputStream input = new FileInputStream(new File(filename));
					String xml = IOUtils.toString(input, "UTF-8"); //$NON-NLS-1$
					
					OpenLayersEntityGenerator generator = new OpenLayersEntityGenerator(xml, false);
					entities = generator.createEntities();
					
					txtXml.setText(xml);
				} catch (Exception e) {
					MessageDialog.openError(getShell(), Messages.DialogAddOpenLayersMap_13, Messages.DialogAddOpenLayersMap_14 + e.getMessage());
				}
			}
		}		
	}
	
	private class TypeChangeListener implements ISelectionChangedListener {
		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			String type = (String) ((IStructuredSelection)event.getSelection()).getFirstElement();
			if(type.equals(IOpenLayersMapObject.TYPE_WMS)) {
				typeComposite = new WMSMapComposite(mainComposite, SWT.NONE);
				typeComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
				
				((WMSMapComposite)typeComposite).fillData(map);
				
				mainComposite.layout();
//				DialogAddOpenLayersMap.this.getContents().redraw();
			}
		}
	}
}
