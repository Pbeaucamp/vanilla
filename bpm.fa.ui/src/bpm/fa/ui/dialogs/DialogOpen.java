package bpm.fa.ui.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.fasd.olap.ICubeView;

import bpm.fa.api.olap.OLAPStructure;
import bpm.fa.api.olap.unitedolap.UnitedOlapLoader;
import bpm.fa.ui.Messages;
import bpm.fa.ui.composite.CompositeOpen;
import bpm.united.olap.api.runtime.IRuntimeContext;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class DialogOpen extends Dialog{
	

	private CompositeOpen open;
	private OLAPStructure cube;
	private RepositoryItem cubeView;
	private UnitedOlapLoader loader;
	private IRuntimeContext runtimeContext;
	
	public DialogOpen(Shell parentShell,  UnitedOlapLoader loader, IRuntimeContext runtimeContext) {
		super(parentShell);
		this.runtimeContext = runtimeContext;
		this.loader = loader;
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		open  = new CompositeOpen(parent, SWT.NONE, loader, runtimeContext);
		open.setLayoutData(new GridData(GridData.FILL_BOTH));
		open.addListener(SWT.Selection, new Listener() {
			
			@Override
			public void handleEvent(Event event) {
				getButton(IDialogConstants.OK_ID).setEnabled(open.getSelectedCube() != null);				
			}
		});
		
		
		return open;
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		getButton(IDialogConstants.OK_ID).setEnabled(false);
	}
	
	@Override
	protected void initializeBounds() {
		getShell().setSize(400, 300);
		getShell().setText(Messages.DialogOpen_0);
	}
	
	@Override
	protected void okPressed() {
		cube = open.getSelectedCube();
		cubeView = open.getCubeView();
		super.okPressed();
	}
	
	public OLAPStructure getCube(){
		return cube;
	}
	
	public RepositoryItem getCubeView(){
		return cubeView;
	}
}
