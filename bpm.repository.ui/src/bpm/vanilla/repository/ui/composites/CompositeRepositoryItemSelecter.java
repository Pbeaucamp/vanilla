package bpm.vanilla.repository.ui.composites;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import bpm.vanilla.platform.core.repository.Repository;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.repository.ui.viewers.RepositoryTreeViewer;

public class CompositeRepositoryItemSelecter extends Composite{

	
	private RepositoryTreeViewer repositoryViewer;
	
	/**
	 * 
	 * @param parent
	 * @param style : SWT.NONE, SWT.MULTI
	 */
	public CompositeRepositoryItemSelecter(Composite parent, int style) {
		super(parent, style);
		this.setLayout(new GridLayout(2, false));
		buildContent();
	}
	
	
	public StructuredViewer getViewer(){
		return repositoryViewer;
	}
	
	private void buildContent(){
		repositoryViewer = new RepositoryTreeViewer(this, SWT.BORDER | getStyle());
		repositoryViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		
	}
	
	public void addViewerFilter(ViewerFilter filter){
		repositoryViewer.addFilter(filter);
	}
	
	public void removeViewerFilter(ViewerFilter filter){
		repositoryViewer.removeFilter(filter);
	}
	

	public void addSelectionChangedListener(ISelectionChangedListener listener){
		repositoryViewer.addSelectionChangedListener(listener);
	}
	
	public void setInput(Object repositoryContent){
		repositoryViewer.setInput(repositoryContent);
	}
	
	public void selectDirectoryItem(Integer directoryItemId){
		if (repositoryViewer.getInput() != null){
			
			try{
				if (directoryItemId != null){
					RepositoryItem it = getInput().getItem(directoryItemId);
					
					
					
					
					if (it != null){
						
						RepositoryDirectory d = getInput().getDirectory(it.getDirectoryId());
						while(d != null){
							repositoryViewer.setExpandedState(d, true);
							d = getInput().getDirectory(d.getParentId());
						}
						repositoryViewer.setSelection(new StructuredSelection(it));
						
					}
					else{
						repositoryViewer.setSelection(StructuredSelection.EMPTY);
					}
				}
				else{
					repositoryViewer.setSelection(StructuredSelection.EMPTY);
				}
			}catch(Exception ex){
				ex.printStackTrace();
			}
			
		}
		
	}
	
	public Repository getInput(){
		return (Repository)repositoryViewer.getInput();
	}
	
	public List<Object> getSelectedItems(){
		List<Object> l = new ArrayList<Object>();
		
		l.addAll(((IStructuredSelection)repositoryViewer.getSelection()).toList());
		return l;
	}
	
	
	
	public void refresh(){
		repositoryViewer.refresh();
	}


	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		repositoryViewer.removeSelectionChangedListener(listener);
		
	}
}
