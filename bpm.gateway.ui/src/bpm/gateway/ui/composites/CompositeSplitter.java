package bpm.gateway.ui.composites;


import java.util.List;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import bpm.gateway.core.StreamElement;
import bpm.gateway.core.transformations.FieldSplitter;
import bpm.gateway.core.transformations.SplitedField;
import bpm.gateway.ui.i18n.Messages;

public class CompositeSplitter extends Composite {

	private TreeViewer viewer;
	private FieldSplitter transfo;
	
	
	public CompositeSplitter(Composite parent, int style) {
		super(parent, style);
		this.setLayout(new GridLayout());
		buildControl();
	}
	
	private void buildControl(){
		
		viewer = new TreeViewer(this, SWT.H_SCROLL | SWT.V_SCROLL | SWT.VIRTUAL | SWT.FULL_SELECTION);
		viewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.setContentProvider(new SpliterContentProvider());
		
		
		TreeViewerColumn fieldColumn = new TreeViewerColumn(viewer, SWT.CENTER);
		fieldColumn.setLabelProvider(new ColumnLabelProvider(){

			@Override
			public String getText(Object element) {
				if (element instanceof StreamElement && transfo.getNonSplitedStreamElements().contains(element)){
					
					return ((StreamElement)element).name;
				}
				return null;
			}
			
		});
		fieldColumn.getColumn().setText(Messages.CompositeSplitter_0);
		fieldColumn.getColumn().setWidth(200);
		
		TreeViewerColumn splitterColumn = new TreeViewerColumn(viewer, SWT.CENTER);
		splitterColumn.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				if (element instanceof SplitedField){
					return ((SplitedField)element).getSpliter();
				}
				return null;
				
			}
		});
		splitterColumn.getColumn().setText(Messages.CompositeSplitter_1);
		splitterColumn.getColumn().setWidth(200);
		
		TreeViewerColumn nameColumn = new TreeViewerColumn(viewer, SWT.CENTER);
		nameColumn.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				if (element instanceof StreamElement){
					if (!transfo.getNonSplitedStreamElements().contains(element)){
						return ((StreamElement)element).name;
					}
				}
				
				return null;
			}
		});
		nameColumn.getColumn().setText(Messages.CompositeSplitter_2);
		nameColumn.getColumn().setWidth(200);
		
		
		viewer.getTree().setHeaderVisible(true);
	}
	
	
	public void setInput(FieldSplitter transfo){
		this.transfo = transfo;
		viewer.setInput(transfo);
	}
	
	
	public void refresh(){
		viewer.refresh();
	}
	
	class SpliterContentProvider implements ITreeContentProvider{

		public Object[] getChildren(Object parentElement) {
			if (parentElement instanceof StreamElement){
				try{
					
					
					List l = transfo.getSpliterFor((StreamElement) parentElement);
					return l.toArray(new SplitedField[l.size()]);
				}catch(Exception e){
					
				}
				
			}
			else if (parentElement instanceof SplitedField){
				List l = ((SplitedField)parentElement).getStreamElements();
				return l.toArray(new StreamElement[l.size()]);
			}
			return null;
		}

		public Object getParent(Object element) {
			if (element instanceof StreamElement){
				/*
				 * splited field
				 */
				
				if (transfo.getNonSplitedStreamElements().contains((StreamElement)element)){
					return transfo;
				}
				else{
					return transfo.getSplitedFieldFor((StreamElement)element);
				}
				
			}
			else if (element instanceof SplitedField){
				return transfo;
			}
			return null;
		}

		public boolean hasChildren(Object element) {
			if (element instanceof StreamElement){
				/*
				 * splited field
				 */
				
//				if (transfo.getNonSplitedStreamElements().contains((StreamElement)element)){
//					return false;
//				}
//				else{
					return transfo.getSplitedFieldFor((StreamElement)element) != null;
//				}
				
			}
			else if (element instanceof SplitedField){
				return ((SplitedField)element).getStreamElements().size() > 0;
			}
						
			
			return false;
		}

		public Object[] getElements(Object inputElement) {
			List l = transfo.getNonSplitedStreamElements();
			return l.toArray(new StreamElement[l.size()]);
		}

		public void dispose() {
			
			
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			
			
		}
		
	}

	public Object getSelection() {
		IStructuredSelection ss = (IStructuredSelection)viewer.getSelection();
		if (ss.isEmpty() ){
			return null;
		}
		
		return ss.getFirstElement();
	}
	

}
