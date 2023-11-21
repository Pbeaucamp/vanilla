package bpm.profiling.ui.composite;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;

import bpm.profiling.database.bean.AnalysisContentBean;
import bpm.profiling.database.bean.RuleSetBean;
import bpm.profiling.database.bean.TagBean;
import bpm.profiling.ui.Activator;

public class CompositeListTag extends Composite{

	private TableViewer viewer;
	
	public CompositeListTag(Composite parent, int style) {
		super(parent, style);
		buildContent();
	}

	private void buildContent(){
				
		setLayout(new GridLayout());
		viewer = new TableViewer(this, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);
		viewer.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		viewer.setContentProvider(new IStructuredContentProvider(){

			public Object[] getElements(Object inputElement) {
				List<TagBean> l = (List<TagBean>)inputElement;
				return l.toArray(new TagBean[l.size()]);
			}

			public void dispose() {
				
				
			}

			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
				
				
			}
			
		});
		viewer.setLabelProvider(new ITableLabelProvider(){

			public Image getColumnImage(Object element, int columnIndex) {
				
				return null;
			}

			public String getColumnText(Object element, int columnIndex) {
				switch(columnIndex){
				
				case 0:
					return ((TagBean)element).getCreator();
				case 1:
					return ((TagBean)element).getContent();
				}
				return "";
			}

			public void addListener(ILabelProviderListener listener) {
				
				
			}

			public void dispose() {
				
				
			}

			public boolean isLabelProperty(Object element, String property) {
				
				return false;
			}

			public void removeListener(ILabelProviderListener listener) {
				
				
			}
			
		});	
		
		
		TableColumn creator = new TableColumn(viewer.getTable(), SWT.NONE);
		creator.setText("Creator");
		creator.setWidth(200);
		
		
		TableColumn content = new TableColumn(viewer.getTable(), SWT.NONE);
		content.setText("Content");
		content.setWidth(200);
		
		viewer.getTable().setLinesVisible(true);
		viewer.getTable().setHeaderVisible(true);
		
		
		/*
		 * create menu
		 */
		final MenuManager menuMgr = new MenuManager();
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {
            public void menuAboutToShow(IMenuManager mgr) {
            	      
            	if (viewer.getSelection().isEmpty()){
            		return;
            	}
            	
            	
            	Action ac = new Action("delete"){
            		public void run(){
            			IStructuredSelection ss = (IStructuredSelection)viewer.getSelection();
                    	TagBean e = (TagBean)ss.getFirstElement();

            			((List)viewer.getInput()).remove(e);
            			viewer.refresh();
            			
            			if (getParent().getParent() instanceof CompositeRuleSetResult){
            				((CompositeRuleSetResult)getParent().getParent()).getTags().remove(e);
            			}
            			else if (getParent().getParent() instanceof CompositeAnalysisResult){
            				((CompositeAnalysisResult)getParent().getParent()).getTags().remove(e);
            			}
            		}
            	};
            	menuMgr.add(ac);
            	menuMgr.update();
            	
            	
            }
        });   
		
		viewer.getTable().setMenu(menuMgr.createContextMenu(viewer.getTable()));
//		this.setContent(viewer.getTable());
//		this.setMinSize(500, 350);
//		setExpandHorizontal(true);
//		setExpandVertical(true);
	}
	
	public void setInput(List<TagBean> tags){
	
		viewer.setInput(tags);
	}


	
}
