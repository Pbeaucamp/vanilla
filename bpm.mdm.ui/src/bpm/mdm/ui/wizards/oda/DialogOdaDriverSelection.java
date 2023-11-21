package bpm.mdm.ui.wizards.oda;

import org.eclipse.datatools.connectivity.oda.util.manifest.DataSetType;
import org.eclipse.datatools.connectivity.oda.util.manifest.ExtensionManifest;
import org.eclipse.datatools.connectivity.oda.util.manifest.ManifestExplorer;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import bpm.mdm.ui.i18n.Messages;

public class DialogOdaDriverSelection extends Dialog{
	private String extensionId;
	private String dataSetTypeId;
	private String dataSetName;
	
	public DialogOdaDriverSelection(Shell parentShell) {
		super(parentShell);
		
	}

	private ComboViewer drivers;
	private ComboViewer types;
	private Text name;
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, false));
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		
		Label l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText(Messages.DialogOdaDriverSelection_0);
		
		drivers = new ComboViewer(main, SWT.READ_ONLY);
		drivers.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		drivers.setContentProvider(new ArrayContentProvider());
		drivers.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				
				return((ExtensionManifest)element).getDataSourceDisplayName();
			}
		});

		drivers.setInput(ManifestExplorer.getInstance().getExtensionManifests());
		drivers.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				ExtensionManifest mf = ((ExtensionManifest)((IStructuredSelection)drivers.getSelection()).getFirstElement());

				extensionId = mf.getExtensionID();
				types.setInput(mf.getDataSetTypes());
				types.setSelection(StructuredSelection.EMPTY);
			}
		});
		
		l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText(Messages.DialogOdaDriverSelection_1);
		
		types = new ComboViewer(main, SWT.READ_ONLY);
		types.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		types.setContentProvider(new ArrayContentProvider());
		types.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				
				return((DataSetType)element).getDisplayName();
			}
		});

		
		types.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				updateButton();
				
			}
		});

		
		l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText(Messages.DialogOdaDriverSelection_2);
		
		name = new Text(main, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		name.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				updateButton();
				
			}
		});
		
		return main;
	}
	private void updateButton(){
		boolean ok  = !(types.getSelection().isEmpty() && name.getText().isEmpty());
		getButton(IDialogConstants.OK_ID).setEnabled(ok);
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);getButton(IDialogConstants.OK_ID).setEnabled(false);
	}
	
	public String getExtensionId(){
		return extensionId;
	}
	public String getDataSetTypeId(){
		return dataSetTypeId;
	}
	public String getName(){
		return dataSetName;
	}
	
	@Override
	protected void okPressed() {
		dataSetName = name.getText();
		dataSetTypeId = ((DataSetType)((IStructuredSelection)types.getSelection()).getFirstElement()).getID();
		super.okPressed();
	}
}
