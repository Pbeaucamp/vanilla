package bpm.profiling.ui.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import bpm.profiling.database.bean.AnalysisContentBean;
import bpm.profiling.database.bean.AnalysisInfoBean;
import bpm.profiling.runtime.core.Column;
import bpm.profiling.ui.Activator;
import bpm.profiling.ui.composite.CompositeAnalysisContent;
import bpm.profiling.ui.composite.CompositeAnalysisInfo;

public class ViewDesign extends ViewPart implements ISelectionListener{

	public static final String ID = "bpm.profiling.ui.view.design";
	
	private CompositeAnalysisContent contentComposite;
	private CompositeAnalysisInfo infoComposite;
	
	
	private AnalysisInfoBean info;
	
	public ViewDesign() {
		
	}

	@Override
	public void createPartControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout());
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		createToolbar(main);
		
		TabFolder folder = new TabFolder(main, SWT.NONE);
		folder.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		
		TabItem infos = new TabItem(folder, SWT.NONE, 0);
		infos.setText("General Informations");
		infos.setControl(infoComposite = new CompositeAnalysisInfo(folder, SWT.NONE, true));
		
		
		TabItem content = new TabItem(folder, SWT.NONE, 1);
		content.setText("Analysis Content");
		content.setControl(contentComposite = new CompositeAnalysisContent(folder, SWT.NONE));

		getSite().getPage().addSelectionListener(this);

	}

	@Override
	public void setFocus() {
		

	}

	
	public void performChanges(){
		infoComposite.performChange();

	}
	
	
	private void setContent(AnalysisInfoBean info){
		infoComposite.fillContent(info);
		contentComposite.setContent(info);
		this.info = info;
	}

	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (selection.isEmpty()){
			
			info = null;
			return;
		}
		
		IStructuredSelection ss = (IStructuredSelection)selection;
		
		if (ss.getFirstElement() instanceof AnalysisInfoBean && info != ss.getFirstElement()){
			setContent((AnalysisInfoBean)ss.getFirstElement());
			info = (AnalysisInfoBean)ss.getFirstElement();
			
		}
		
	}
	
	private void createToolbar(Composite parent){
		
		ToolBar toolbar = new ToolBar(parent, SWT.NONE);
		toolbar.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		ToolItem applyChange = new ToolItem(toolbar, SWT.PUSH);
		applyChange.setImage(Activator.getDefault().getImageRegistry().get("save"));
		applyChange.setToolTipText("Save Change");
		applyChange.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				List<Column> c = contentComposite.getCheckedColumns();
			
				
				List<Column> alreadyPresent = new ArrayList<Column>();
				List<AnalysisContentBean> toDelete = new ArrayList<AnalysisContentBean>();
				
				for(AnalysisContentBean content : Activator.helper.getAnalysisManager().getAllAnalysisContentFor(info)){
					
					boolean present = false;
					for(Column col : c){
						if (col.getLabel().equals(content.getColumnName())
							&& col.getTable().getLabel().equals(content.getTableName())){
							alreadyPresent.add(col);
							present = true;
							break;
						}
					}
					
					//remove
					if (!present){
						toDelete.add(content);
					}
				}
				
				
				c.removeAll(alreadyPresent);
				
				//add new
				for(Column col : c){
					AnalysisContentBean a = new AnalysisContentBean();
					a.setAnalysisId(info.getId());
					a.setColumnName(col.getLabel());
					a.setTableName(col.getTable().getLabel());
					
					Activator.helper.getAnalysisManager().createAnalysisContent(a);
				}
				
				//remove 
				for(AnalysisContentBean r : toDelete){
					Activator.helper.getAnalysisManager().deleteAnalysisContent(r);
				}
				
				performChanges();
				info = infoComposite.getAnalysisInformation();
				Activator.helper.getAnalysisManager().updateAnalysis(info);
				
				
				
			}
			
		});
		
		
		
		
		ToolItem refresh = new ToolItem(toolbar, SWT.PUSH);
		refresh.setImage(Activator.getDefault().getImageRegistry().get("refresh"));
		refresh.setToolTipText("reinit analysis");
		refresh.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (info != info){
					setContent(info);
				}
				
			}
			
		});
	}
	
}
