package bpm.fd.repository.ui.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import bpm.fd.repository.ui.Messages;
import bpm.fd.repository.ui.composites.CompositeItem;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.repository.RepositoryItem;


public class DialogItem extends Dialog {

	private RepositoryItem item;
	private CompositeItem composite;
	private CheckboxTableViewer table;
	private List<Group> groupNames;
	
	private List<Group> groups;
	
	public DialogItem(Shell parentShell, List<Group> groups) {
		super(parentShell);
		this.setShellStyle(getShellStyle() | SWT.RESIZE);
		this.groups = groups.subList(0, groups.size() > 20 ? 20 : groups.size() - 1);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		parent.setLayout(new GridLayout(2, false));
		composite =new CompositeItem(parent, SWT.NONE, item);
		if (item != null){
			composite.fillData();
		}
		composite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true , 2, 1));
		composite.addListener(SWT.Modify, new Listener(){

			@Override
			public void handleEvent(Event event) {
				updateButton();				
			}
			
		});
		
		Label l = new Label(parent, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 2, 1));
		l.setText(Messages.DialogItem_0);
		
		table = new CheckboxTableViewer(parent, SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL);
		table.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		table.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				return ((Group)element).getName();
			}
		});
		table.setContentProvider(new ArrayContentProvider());
		table.addCheckStateListener(new ICheckStateListener() {
			
			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {
				updateButton();
			}
		});
		
		table.setInput(groups.toArray(new Group[groups.size()]));
		
		
		return parent;
	}

	public RepositoryItem getItem(){
		return item;
	}

	@Override
	protected void okPressed() {
		item = composite.setObjects();
		
		Object[] checked = table.getCheckedElements();;
		groupNames = new ArrayList<Group>();
		for(int i = 0; i < checked.length; i++){
			groupNames.add((Group)checked[i]);
		}
		
		
		super.okPressed();
	}

	@Override
	protected void initializeBounds() {
		getShell().setText(Messages.DialogItem_1);
		getShell().setSize(400, 500);
		
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		getButton(IDialogConstants.OK_ID).setEnabled(false);
	}
	
	public List<Group> getGroupName(){
		return groupNames;
	}
	
	private void updateButton(){
		boolean enabled = table.getCheckedElements().length > 0;
		enabled = enabled && composite.isFilled();
		
		getButton(IDialogConstants.OK_ID).setEnabled(enabled);
	}
}
