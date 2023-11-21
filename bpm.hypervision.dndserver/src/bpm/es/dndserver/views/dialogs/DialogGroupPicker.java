package bpm.es.dndserver.views.dialogs;



import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import bpm.es.dndserver.Messages;
import bpm.vanilla.platform.core.beans.Group;

public class DialogGroupPicker extends Dialog{
	

	private CheckboxTableViewer groups;
	
	private List<Integer> selectedGroup = new ArrayList<Integer>();
	public DialogGroupPicker(Shell parentShell) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout());
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		
		Label l = new Label(main, SWT.NONE);
		l.setText(Messages.DialogGroupPicker_0);
		l.setLayoutData(new GridData());
		
		groups = CheckboxTableViewer.newCheckList(main, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		
		groups.getTable().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		groups.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				updateOk();
				
			}
		});
		groups.setContentProvider(new ArrayContentProvider());
		groups.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				return ((Group)element).getName();
			}
		});
		groups.addCheckStateListener(new ICheckStateListener() {
			
			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {
				if (event.getChecked()){
					selectedGroup.add(((Group)event.getElement()).getId());
				}
				else{
					for(Integer i : selectedGroup){
						if (i.intValue() == ((Group)event.getElement()).getId()){
							selectedGroup.remove(i);
							break;
						}
					}
				}
				updateOk();
			}
		});
		
		fillGroups();
		
		return main;
	}
	
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		getButton(IDialogConstants.OK_ID).setEnabled(false);
	}
	
	
	private void fillGroups(){
		bpm.vanilla.server.client.ui.clustering.menu.Activator activator = bpm.vanilla.server.client.ui.clustering.menu.Activator.getDefault();
	
		try{
			groups.setInput(activator.getVanillaApi().getVanillaSecurityManager().getGroups());
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		
		
	}
	
	private void updateOk(){
		getButton(IDialogConstants.OK_ID).setEnabled(!selectedGroup.isEmpty());
	}
	
	public List<Integer> getGroups(){
		return selectedGroup;
	}
	
	@Override
	protected void initializeBounds() {
		getShell().setSize(400, 300);
		fillGroups();
		getShell().setText(Messages.DialogGroupPicker_1);
	}
	
}
