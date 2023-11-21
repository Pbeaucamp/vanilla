package bpm.fa.ui.composite.viewers;

import java.util.ArrayList;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TableColumn;

import bpm.fa.api.item.Item;
import bpm.fa.api.olap.OLAPResult;

public class OlapResultViewer {
	private TableViewer viewer;
	private static class Orderer extends ViewerComparator{
		
		private int colNumber;
		private boolean asc = true;
		
		public void setCols(int indexColumn){
			if (colNumber == indexColumn){
				asc = ! asc;
			}
			else{
				asc = true;
				colNumber = indexColumn;
			}
		}
		
		@Override
		public int compare(Viewer viewer, Object e1, Object e2) {
			if (asc){
				return super.compare(viewer, ((ArrayList<Item>)e1).get(colNumber).getLabel(), ((ArrayList<Item>)e2).get(colNumber).getLabel());
			}
			else{
				return super.compare(viewer, ((ArrayList<Item>)e2).get(colNumber).getLabel(), ((ArrayList<Item>)e1).get(colNumber).getLabel());
			}
			
		}
	}
	
	private Orderer orderer = new Orderer();
	
	public Control createControl(Composite parent){
		viewer = new TableViewer(parent, SWT.FULL_SELECTION | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new ArrayContentProvider());
		viewer.setLabelProvider(new ITableLabelProvider(){

			@Override
			public Image getColumnImage(Object element, int columnIndex) {
				
				return null;
			}

			@Override
			public String getColumnText(Object element, int columnIndex) {
				return ((ArrayList<Item>)element).get(columnIndex).getLabel();
			}

			@Override
			public void addListener(ILabelProviderListener listener) {
				
				
			}

			@Override
			public void dispose() {
				
				
			}

			@Override
			public boolean isLabelProperty(Object element, String property) {
				
				return false;
			}

			@Override
			public void removeListener(ILabelProviderListener listener) {
				
				
			}
			
		});
		viewer.setComparator(orderer);
		viewer.getTable().setLinesVisible(true);
		viewer.getTable().setHeaderVisible(true);
		
		
		
		return viewer.getTable();
		
	}
	
	public void setInput(OLAPResult result){
		ArrayList<ArrayList<Item>> l = new ArrayList<ArrayList<Item>>(result.getRaw());
		ArrayList<Item> rowTitle = l.remove(0);
		
		for(int i = 0; i < rowTitle.size(); i++){
			TableColumn col = new TableColumn(viewer.getTable(), SWT.LEFT);
			col.setText(rowTitle.get(i).getLabel());
			col.setWidth(150);
			final int j = i;
			col.addSelectionListener(new SelectionAdapter(){
				@Override
				public void widgetSelected(SelectionEvent e) {
					orderer.setCols(j);
					viewer.refresh();
				}
			});
		}
		
		
		
		viewer.setInput(l);
	}
}
