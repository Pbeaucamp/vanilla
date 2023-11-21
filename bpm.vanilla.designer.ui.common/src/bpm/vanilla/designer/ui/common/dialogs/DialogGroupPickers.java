package bpm.vanilla.designer.ui.common.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import bpm.vanilla.platform.core.IVanillaAPI;
import bpm.vanilla.platform.core.beans.Group;

public class DialogGroupPickers extends Dialog{

	private IVanillaAPI vanillaApi;
	private CheckboxTableViewer viewer;
	private List<Group> groups = new ArrayList<Group>();
	
	public DialogGroupPickers(Shell parentShell, IVanillaAPI vanillaApi) {
		super(parentShell);
		this.vanillaApi = vanillaApi;
	}
	public DialogGroupPickers(Shell parentShell, IVanillaAPI vanillaApi, List<Group> checkedGroups){
		super(parentShell);
		this.vanillaApi = vanillaApi;
		if (groups != null){
			this.groups = checkedGroups;
		}
		
	}
	

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, true));
		main.setLayoutData(new GridData(GridData.FILL_BOTH));

		
		Button checkAll = new Button(parent, SWT.PUSH);
		checkAll.setText("Check All");
		checkAll.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		checkAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				viewer.setAllChecked(true);
				viewer.refresh();
			}
		});
		
		Button uncheckAll = new Button(parent, SWT.PUSH);
		uncheckAll.setText("Uncheck All");
		uncheckAll.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		uncheckAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				viewer.setAllChecked(false);
				viewer.refresh();
			}
		});
		
		Label l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 2, 1));
		l.setText("Select Groups");
		
		
		
		
		viewer = CheckboxTableViewer.newCheckList(main, SWT.BORDER);
		viewer.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				return ((Group)element).getName();
			}
		});
		viewer.setContentProvider(new ArrayContentProvider());
		viewer.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));

		return main;
	}

	public List<Group> getGroups(){
		return groups;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	@Override
	protected void okPressed() {
		for(Object o : viewer.getCheckedElements()){
			groups.add((Group)o);
		}
		super.okPressed();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#initializeBounds()
	 */
	@Override
	protected void initializeBounds() {
		getShell().setSize(600, 400);
		getShell().setText("Select Groups");
		try {
			viewer.setInput(vanillaApi.getVanillaSecurityManager().getGroups());
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		for(Group g : (List<Group>)viewer.getInput()){
			for(Group gg : groups){
				if (g.getId().intValue() == gg.getId().intValue()){
					viewer.setChecked(g, true);
				}
			}
		}
		groups.clear();
	}
	
	
}
