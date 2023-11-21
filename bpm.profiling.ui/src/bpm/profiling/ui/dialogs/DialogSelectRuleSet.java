package bpm.profiling.ui.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import bpm.profiling.database.bean.AnalysisContentBean;
import bpm.profiling.database.bean.AnalysisInfoBean;
import bpm.profiling.database.bean.RuleSetBean;
import bpm.profiling.ui.Activator;

public class DialogSelectRuleSet extends Dialog {

	private CheckboxTableViewer viewer;
	
	private List<RuleSetBean> ruleSets;
	private AnalysisInfoBean infos;
	
	public DialogSelectRuleSet(Shell parentShell, AnalysisInfoBean infos) {
		super(parentShell);
		this.infos = infos;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite c = new Composite(parent, SWT.NONE);
		c.setLayout(new GridLayout());
		c.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		viewer = new CheckboxTableViewer(c, SWT.NONE | SWT.H_SCROLL | SWT.V_SCROLL );
		viewer.setLabelProvider(new LabelProvider(){

			@Override
			public String getText(Object element) {
				return ((RuleSetBean)element).getName();
			}
			
		});
		
		viewer.setContentProvider(new IStructuredContentProvider(){

			public Object[] getElements(Object inputElement) {
				List<RuleSetBean> l = (List<RuleSetBean>)inputElement; 
				return l.toArray(new RuleSetBean[l.size()]);
			}

			public void dispose() {
				
				
			}

			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
				
				
			}
			
		});
		viewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		
		return c;
	}

	@Override
	protected void okPressed() {
		ruleSets = new ArrayList<RuleSetBean>();
		
		for(Object rs : viewer.getCheckedElements()){
			ruleSets.add((RuleSetBean)rs);
		}
		
		super.okPressed();
	}
	
	public List<RuleSetBean> getRuleSets(){
		return ruleSets;
	}

	@Override
	protected void initializeBounds() {
		getShell().setText("Select RuleSets to save");
		
		List<RuleSetBean> l = new ArrayList<RuleSetBean>();
		for(AnalysisContentBean c : Activator.helper.getAnalysisManager().getAllAnalysisContentFor(infos)){
			l.addAll(Activator.helper.getAnalysisManager().getRuleSetsFor(c));
		}
		
		viewer.setInput(l);
		
		setShellStyle(SWT.RESIZE);
		getShell().setSize(600, 400);
		
	}

}
