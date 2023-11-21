package bpm.fa.ui.dialogs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import bpm.fa.api.olap.Hierarchy;
import bpm.fa.api.olap.Level;
import bpm.fa.api.olap.OLAPMember;
import bpm.fa.ui.Messages;
import bpm.fa.ui.composite.viewers.StructureLabelProvider;
import bpm.fa.ui.ktable.CubeModel;

public class DialogLevelValues extends Dialog{

	private Hierarchy hiera;
	private Level lvl;
	private CubeModel cubeModel;
	
	private CheckboxTreeViewer viewer;
	
	public DialogLevelValues(Shell parentShell, CubeModel cubeModel, Hierarchy h, Level lvl) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.cubeModel = cubeModel;
		this.hiera = h;
		this.lvl = lvl;
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		ToolBar bar = new ToolBar(parent, SWT.HORIZONTAL);
		bar.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		
		ToolItem checkAll = new ToolItem(bar, SWT.PUSH);
		checkAll.setText(Messages.DialogLevelValues_0);
		checkAll.setToolTipText(Messages.DialogLevelValues_1);
		checkAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				viewer.setAllChecked(true);
			}
		});
		
		ToolItem uncheckAll = new ToolItem(bar, SWT.PUSH);
		uncheckAll.setText(Messages.DialogLevelValues_2);
		uncheckAll.setToolTipText(Messages.DialogLevelValues_3);
		uncheckAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				viewer.setAllChecked(false);
			}
		});
		
		viewer = new CheckboxTreeViewer(parent, SWT.BORDER | SWT.V_SCROLL | SWT.VIRTUAL);
		viewer.setContentProvider(new ITreeContentProvider(){

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

			@Override
			public Object[] getElements(Object inputElement) {
				Collection c = (Collection)inputElement;
				return c.toArray(new Object[c.size()]);
			}

			@Override
			public void dispose() {
				
				
			}

			@Override
			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
				
				
			}
			
		});
		viewer.setLabelProvider(new StructureLabelProvider());
		viewer.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 3, 1));
		
		
		
		return viewer.getControl();
	}
	
	@Override
	protected void okPressed() {
		List<OLAPMember> checked = (List)Arrays.asList(viewer.getCheckedElements());
		for(Object o : checked){
			((OLAPMember)o).setLevel(lvl);
			
		}
		cubeModel.getFilterManager().setLevelFilters(hiera, lvl, checked);
		
//		for(OLAPMember m : cubeModel.getFilterManager().getFiltered(hiera, lvl)){
//			if (!checked.contains(m)){
//				cubeModel.getFilterManager().removeFilter(m);
//			}
//		}
		
		super.okPressed();
	}
	
	@Override
	protected void initializeBounds() {
		getShell().setText(Messages.DialogLevelValues_4 + lvl.getName());
		getShell().setSize(400, 300);
		
		List<OLAPMember> l = cubeModel.getFilterManager().getOLAPMembers(hiera, lvl);
		
		
		/*
		 * filter on double Values from different members
		 */
		List<OLAPMember> uniqueValues = new ArrayList<OLAPMember>();
		
		for(OLAPMember m : l){
			boolean found = false;
			for(OLAPMember _m : uniqueValues){
				String last = m.getUniqueName().substring(m.getUniqueName().lastIndexOf("].[")); //$NON-NLS-1$
				String _last = _m.getUniqueName().substring(_m.getUniqueName().lastIndexOf("].[")); //$NON-NLS-1$
				if (last.equals(_last)){
					found = true;
				}
			}
			if (!found){
				uniqueValues.add(m);
			}
		}
		
		
		viewer.setInput(uniqueValues);
		for (OLAPMember o : cubeModel.getFilterManager().getFiltered()){
			
			String[] p = o.getUniqueName().split("\\]\\."); //$NON-NLS-1$
			for(OLAPMember m : uniqueValues){
				String[] _p = m.getUniqueName().split("\\]\\."); //$NON-NLS-1$
				if (p[p.length - 1].equals(_p[_p.length - 1])){
					viewer.setChecked(m, true);
				}
			}

		}

	}
}
