package bpm.google.spreadsheet.oda.driver.ui.impl.dataset;

import java.util.ArrayList;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;


public class FilterTableViewer extends CheckboxTableViewer {

	private ArrayList<FilterDescription> listInput;
	private ArrayList<FilterDescription> listFilterToRemoved;


	public static CheckboxTableViewer newCheckList(Composite parent, int style) {
		Table table = new Table(parent, SWT.CHECK | style);
		return new FilterTableViewer(table);
	}

	@SuppressWarnings("unchecked")
	public FilterTableViewer(Table pTable) {


		super(pTable);


	//Label Provider - Content provider - Input	
		this.setContentProvider(new MyStructuredContentProvider());		
		this.setLabelProvider(new MyTableLabelProvider());

		if(this.getInput() == null){
			listInput = new ArrayList<FilterDescription>();
		}
		else{
			listInput = new ArrayList<FilterDescription>();
			listInput = (ArrayList<FilterDescription>) this.getInput();
		}


	// Check listenner to Remove a filter.
		
		listFilterToRemoved = new ArrayList<FilterDescription>();
		
		this.addCheckStateListener(new ICheckStateListener() {
			
			public void checkStateChanged(CheckStateChangedEvent event) {
				
				FilterDescription currentFilter = (FilterDescription)event.getElement();
				
				if(event.getChecked()){
					listFilterToRemoved.add(currentFilter);
				}
				
				else{
					listFilterToRemoved.remove(currentFilter);
				}
				
				
			}			
		});
		
		
		this.setInput(listInput);
		this.refresh();



	}


	static class MyStructuredContentProvider implements IStructuredContentProvider {
		@SuppressWarnings("unchecked")
		public Object[] getElements(Object inputElement) {
			ArrayList<String> localInputElement = (ArrayList<String>)inputElement;				
			return localInputElement.toArray();
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput,
				Object newInput) {
		}	
	}



	static class MyTableLabelProvider implements ITableLabelProvider {
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			FilterDescription currentFilter = (FilterDescription)element;

			String strReturned = "";

			switch (columnIndex) {
			case 0:
				strReturned =  currentFilter.getFilterName();
				break;

			case 1:
				strReturned =  currentFilter.getFilterOperator();
				break;

			case 2:
				strReturned =  currentFilter.getFilterValue1();
				break;

			case 3:
				strReturned =  currentFilter.getFilterValue2();
				break;

			case 4:
				strReturned =  currentFilter.getFilterLogicalOperator();
				break;

			default:
				strReturned =  "";
			break;
			}

			return strReturned;
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
	}



	
	
	
	public ArrayList<FilterDescription> getListFilterToRemoved() {
		return listFilterToRemoved;
	}








}
