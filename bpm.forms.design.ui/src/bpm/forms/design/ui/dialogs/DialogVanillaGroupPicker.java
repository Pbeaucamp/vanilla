package bpm.forms.design.ui.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import bpm.forms.design.ui.Activator;
import bpm.forms.design.ui.Messages;
import bpm.vanilla.platform.core.beans.Group;

public class DialogVanillaGroupPicker extends Dialog{
	public static final int MULTIPLE_GROUPS = 0;
	public static final int MONO_GROUPS = 1;

	private StructuredViewer viewer;
	
	private List<Group> group = new ArrayList<Group>();
	private int dialogType = MONO_GROUPS;
	
	public DialogVanillaGroupPicker(Shell parentShell, int dialogType) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.dialogType = dialogType;
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, false));
		main.setLayoutData(new GridData());
		
		
		Label l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText(Messages.DialogVanillaGroupPicker_0);
		
		
		switch (dialogType) {
		case MULTIPLE_GROUPS:
			viewer = CheckboxTableViewer.newCheckList(parent, SWT.NONE);
			((CheckboxTableViewer)viewer).addCheckStateListener(new ICheckStateListener(){

				@Override
				public void checkStateChanged(CheckStateChangedEvent event) {
					getButton(IDialogConstants.OK_ID).setEnabled(((CheckboxTableViewer)viewer).getCheckedElements().length > 0);
					
				}
				
			});
			break;

		case MONO_GROUPS:
			viewer = new ComboViewer(parent, SWT.NONE);
			viewer.addSelectionChangedListener(new ISelectionChangedListener() {
				
				@Override
				public void selectionChanged(SelectionChangedEvent event) {
					getButton(IDialogConstants.OK_ID).setEnabled(!viewer.getSelection().isEmpty());
					
				}
			});
			break;
		}
		
		viewer.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		viewer.setContentProvider(new ObservableListContentProvider());
		viewer.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				return ((Group)element).getName();
			}
		});
		
		return main;
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
			super.createButtonsForButtonBar(parent);
			getButton(IDialogConstants.OK_ID).setEnabled(false);
	}
	
	@Override
	protected void okPressed() {
		group.clear();
		if (viewer instanceof CheckboxTableViewer){
			for(Object o : ((CheckboxTableViewer)viewer).getCheckedElements()){
				group.add((Group)o);
			}
		}
		else{
			group.add((Group)((IStructuredSelection)viewer.getSelection()).getFirstElement());
		}
		super.okPressed();
	}
	
	public List<Group> getGroup(){
		return group;
	}
	
	@Override
	protected void initializeBounds() {
		getShell().setText(Messages.DialogVanillaGroupPicker_1);
		getShell().setSize(400, 200);
		
		
		
		
		try{
			WritableList l = new WritableList(Activator.getDefault().getVanillaContext().getVanillaApi().getVanillaSecurityManager().getGroups(), Group.class);
			viewer.setInput(l);
		}catch(Exception ex){
			ex.printStackTrace();
			MessageDialog.openError(getShell(), Messages.DialogVanillaGroupPicker_2, Messages.DialogVanillaGroupPicker_3  + ".\n" + ex.getMessage()); //$NON-NLS-3$
		}
		
	}
}
