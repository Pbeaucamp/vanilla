package bpm.profiling.ui.views;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.part.ViewPart;

import bpm.profiling.database.bean.AnalysisInfoBean;
import bpm.profiling.database.bean.TagBean;
import bpm.profiling.ui.Activator;
import bpm.profiling.ui.composite.CompositeAnalysisResult;
import bpm.profiling.ui.composite.CompositeRuleSetResult;
import bpm.profiling.ui.dialogs.DialogAnalysis;

public class ViewAnalysis extends ViewPart {
	
	private class Runned{
		AnalysisInfoBean parent;
		Date date;
		
	}
	
	public static final String ID = "bpm.profiling.ui.view.analysis";

	private TreeViewer viewer;
	private SelectionListener listener;
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	
	public ViewAnalysis() {
		
	}

	@Override
	public void createPartControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout());
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		createToolbar(main);
		
		viewer = new TreeViewer(main, SWT.V_SCROLL | SWT.H_SCROLL | SWT.VIRTUAL);
		viewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.setContentProvider(new ITreeContentProvider(){

			public Object[] getElements(Object inputElement) {
				List<AnalysisInfoBean> l = (List<AnalysisInfoBean>)inputElement;
				
				return l.toArray(new AnalysisInfoBean[l.size()]);
			}

			public void dispose() {
				
				
			}

			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
				
				
			}

			public Object[] getChildren(Object parentElement) {
				if (!(parentElement instanceof AnalysisInfoBean)){
					return null;
				}
				List<Runned> l  = new ArrayList<Runned>();
				for(Date d : Activator.helper.getAnalysisManager().getAnalysisDatesFor((AnalysisInfoBean)parentElement)){
					Runned r = new Runned();
					r.parent = (AnalysisInfoBean)parentElement;
					r.date = d;
					l.add(r);
				}
				
				return l.toArray(new Runned[l.size()]);
			}

			public Object getParent(Object element) {
				if (element instanceof Runned){
					return ((Runned)element).parent;
				}
				return null;
			}

			public boolean hasChildren(Object element) {
				return true;
			}

			
		});
		viewer.setLabelProvider(new LabelProvider(){

			@Override
			public String getText(Object element) {
				if (element instanceof AnalysisInfoBean){
					return ((AnalysisInfoBean)element).getName();
				}else{
					return sdf.format(((Runned)element).date);
				}
				
			}
			
			@Override
			public Image getImage(Object element) {
				return Activator.getDefault().getImageRegistry().get("analysis");
			}
			
		});
		
		getSite().setSelectionProvider(viewer);
		
		
		try {
			viewer.setInput(Activator.helper.getAnalysisManager().getAllAnalysis());
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		
		
		
		final MenuManager menuMgr = new MenuManager();
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {
            public void menuAboutToShow(IMenuManager mgr) {
            	      
            	if (viewer.getSelection().isEmpty()){
            		return;
            	}
            	if ((((IStructuredSelection)viewer.getSelection()).getFirstElement() instanceof AnalysisInfoBean)){
            		Action edit = new Action("Edit Analysis Info"){
                		public void run(){
                			IStructuredSelection ss = (IStructuredSelection)viewer.getSelection();
                			AnalysisInfoBean e = (AnalysisInfoBean)ss.getFirstElement();

                			DialogAnalysis d = new DialogAnalysis(getSite().getShell(),e);
                			if (d.open() == DialogAnalysis.OK){
                				Activator.helper.getAnalysisManager().updateAnalysis(d.getInfos());
                				viewer.refresh();
                			}
                		}
                	};
                	menuMgr.add(edit);

            	}
            	
            	            	
            	if (!(((IStructuredSelection)viewer.getSelection()).getFirstElement() instanceof Runned)){
            		return;
            	}
            	
            	Action ac = new Action("View Result Analysis"){
            		public void run(){
            			IStructuredSelection ss = (IStructuredSelection)viewer.getSelection();
                    	Runned e = (Runned)ss.getFirstElement();

            			ViewResult v = (ViewResult)getSite().getWorkbenchWindow().getActivePage().findView(ViewResult.ID);
            			if (v != null){
            				v.loadFromHistoric(e.parent, e.date);
            			}
            		}
            	};
            	menuMgr.add(ac);
            	
            	

            	
            	menuMgr.update();
            	
            	
            	
            }
        });   
		
		viewer.getTree().setMenu(menuMgr.createContextMenu(viewer.getTree()));
//	
		
	}

	@Override
	public void setFocus() {
		

	}
	
	public void setInput(){
		viewer.setInput(Activator.helper.getAnalysisManager().getAllAnalysis());
	}
	
	private void createToolbar(Composite parent){
		ToolBar toolbar = new ToolBar(parent, SWT.NONE);
		toolbar.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		ToolItem add = new ToolItem(toolbar, SWT.PUSH);
		add.setImage(Activator.getDefault().getImageRegistry().get("analysis"));
		add.setToolTipText("create analysis");
		add.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				DialogAnalysis dial = new DialogAnalysis(getSite().getShell());
				
				if (dial.open() == DialogAnalysis.OK){
					AnalysisInfoBean bean = dial.getInfos();
					
					Activator.helper.getAnalysisManager().createAnalysis(bean);
					viewer.setInput(Activator.helper.getAnalysisManager().getAllAnalysis());
					
				}
				
			}
			
		});
		
		
			
		ToolItem delete = new ToolItem(toolbar, SWT.PUSH);
		delete.setToolTipText("delete analysis");
		delete.setImage(Activator.getDefault().getImageRegistry().get("delete"));
		delete.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection ss = (IStructuredSelection)viewer.getSelection();
				
				if (ss.isEmpty()){
					return;
				}
				
				Activator.helper.getAnalysisManager().deleteAnalysis(((AnalysisInfoBean)ss.getFirstElement()));
				viewer.setInput(Activator.helper.getAnalysisManager().getAllAnalysis());
				
			}
			
		});
		
		
		ToolItem refresh = new ToolItem(toolbar, SWT.PUSH);
		refresh.setToolTipText("refresh analysis");
		refresh.setImage(Activator.getDefault().getImageRegistry().get("refresh"));
		refresh.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				viewer.setInput(Activator.helper.getAnalysisManager().getAllAnalysis());
				
			}
			
		});
	}

	public AnalysisInfoBean getSelection() {
		if (viewer.getSelection().isEmpty()){
			return null;
		}
		return (AnalysisInfoBean)((IStructuredSelection)viewer.getSelection()).getFirstElement();
	}

}
