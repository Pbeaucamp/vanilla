package bpm.inlinedatas.oda.driver.ui.tableViewer;

import java.util.ArrayList;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import bpm.inlinedatas.oda.driver.ui.Activator;
import bpm.inlinedatas.oda.driver.ui.icons.IconNames;
import bpm.inlinedatas.oda.driver.ui.model.ColumnsDescription;

public final class StructureTableViewer  extends TableViewer{
	
	private ArrayList<ColumnsDescription> listeColViewer;
	protected ArrayList<String> listInputStr;
	
	public StructureTableViewer(Composite pParent, ArrayList<ColumnsDescription> listCol){
		
		super(pParent, SWT.FULL_SELECTION | SWT.MULTI | SWT.BORDER);
		
		listeColViewer = new ArrayList<ColumnsDescription>();
		listeColViewer.addAll(listCol);

		listInputStr = new ArrayList<String>();
		
			for (ColumnsDescription currentCol: listeColViewer){	
				viewerAddCol(currentCol);		
			}
		
		this.setContentProvider(new MyContentProvider());	
	
		this.setInput(listInputStr);	
	}
	
	
//****************  Method to add a new columninto the viewer
	
	public void viewerAddCol(final ColumnsDescription pCol){
		
		TableViewerColumn viewerCol = new TableViewerColumn(this,SWT.NONE);
	

	// LabelProvider
		viewerCol.setLabelProvider(new ColumnLabelProvider(){
			
			public String getText(Object element) {
			
				return "";
				
				}

		});
		
	
	//Columns caracteristics	
		viewerCol.getColumn().setWidth(100);
		viewerCol.getColumn().setText(pCol.getColName());
		viewerCol.getColumn().setToolTipText(pCol.getColType().getSimpleName());
		viewerCol.getColumn().setMoveable(false);
		viewerCol.getColumn().setImage(Activator.getDefault().getImageRegistry().get(IconNames.COLUMN));
			
	}
	
	
//**************** Methode to add a new column into the arrayList
	
public void arrayAddCol(ColumnsDescription pCol){
	
	listeColViewer.add(pCol);
}


//**************** Methode to Delete a  column into the arrayList
	
public void arrayDelCol(String pColName){
	
	for(ColumnsDescription current: listeColViewer){
		
		if(current.getColName().equals(pColName)){
			
			listeColViewer.remove(current);
		}
		
	
		
	}

	
	
}


	
//****************  definition of content Provider	
	
	protected static class MyContentProvider implements IStructuredContentProvider{
		
		@SuppressWarnings("unchecked")
		public Object[] getElements(Object inputElement) {
			
			ArrayList<String> localInputElement = (ArrayList<String>)inputElement;				
			return localInputElement.toArray();
		}

		public void dispose() {
			
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			
		}	
	}



	public ArrayList<ColumnsDescription> getListeColViewer() {
		return listeColViewer;
	}


	public void setListeColViewer(ArrayList<ColumnsDescription> listeColViewer) {
		this.listeColViewer = listeColViewer;
	}
	

}
