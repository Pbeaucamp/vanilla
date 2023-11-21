package bpm.vanilla.server.ui.views;


import java.util.List;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import bpm.vanilla.server.client.communicators.TaskInfo;

public abstract class ServerContent extends ViewPart{

	private TableViewer tableViewer;
		
	
	
	
	
	@Override
	public void createPartControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout());
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		createToolBar(main);
		tableViewer = new TableViewer(main, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		tableViewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		tableViewer.setContentProvider(new IStructuredContentProvider() {
			
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				
				
			}
			
			public void dispose() {
				
				
			}
			
			public Object[] getElements(Object inputElement) {
				List<TaskInfo> l = (List<TaskInfo>)inputElement;
				return l.toArray(new TaskInfo[l.size()]);
			}
		});
		
		createColumns(tableViewer);
	}

	protected TableViewer getTableViewer(){
		return tableViewer;
	}
	
	abstract protected void createToolBar(Composite parent);
	
	protected void createColumns(TableViewer viewer){
		TableViewerColumn taskId = new TableViewerColumn(tableViewer, SWT.NONE);
		taskId.getColumn().setText("TaskId");
		taskId.getColumn().setWidth(100);
		taskId.setLabelProvider(new ColumnLabelProvider(){

			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
			 */
			@Override
			public String getText(Object element) {
				
				return ((TaskInfo)element).getId();
			}
			
		});
		
		TableViewerColumn taskClass = new TableViewerColumn(tableViewer, SWT.NONE);
		taskClass.getColumn().setText("Class");
		taskClass.getColumn().setWidth(400);
		taskClass.setLabelProvider(new ColumnLabelProvider(){

			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
			 */
			@Override
			public String getText(Object element) {
				
				return ((TaskInfo)element).getClassName();
			}
			
		});
		
	}
	
	@Override
	public void setFocus() {
		
		
	}

	
}
