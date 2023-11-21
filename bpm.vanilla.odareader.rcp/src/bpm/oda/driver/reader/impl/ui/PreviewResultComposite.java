package bpm.oda.driver.reader.impl.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import bpm.oda.driver.reader.Activator;
import bpm.oda.driver.reader.icons.IconsName;
import bpm.oda.driver.reader.model.Snapshot;
import bpm.oda.driver.reader.model.dataset.DataSet;

public class PreviewResultComposite extends Composite{
	
	private Button btnRefresh;
	private TableViewer viewerResults;
	private Table tableResuts;
	private Label labelRecords;
	
	private DataSet dataSet;
	private Snapshot snapShot;
	
	
/*
 * Constructor: Preview results for a Data set
 */
	public PreviewResultComposite(Composite parent, int style, DataSet pDataSet) {
		super(parent, style);
		
		this.setLayout(new GridLayout(2, false));
		this.dataSet = pDataSet;
		
		createControl();
	}
	
	
/*
 * Constructor: Preview results for a Snapshot
 */
	public PreviewResultComposite(Composite parent, int style, Snapshot snapSHot) {
		super(parent, style);
		
		this.setLayout(new GridLayout(2, false));
		
		createControl();
		
		this.snapShot = snapSHot;
		
		fillSnapshot();
	}

	private void createControl() {
		
		btnRefresh = new Button(this, SWT.PUSH);
		btnRefresh.setImage(Activator.getDefault().getImageRegistry().get(IconsName.ICO_REFRESH));
		btnRefresh.setLayoutData(new GridData(GridData.FILL, GridData.FILL,false,false,1,1));
		
		Label l = new Label(this, SWT.NONE);
		l.setText("Preview Results:");
		l.setLayoutData(new GridData(GridData.FILL, GridData.CENTER,true,false,1,1));
		l.setFont(new Font (Display.getCurrent(), "Arial", 10, SWT.BOLD));
		
		
		labelRecords = new Label(this, SWT.NONE);
		labelRecords.setText("  Total: 0 record(s) shown.");
		labelRecords.setLayoutData(new GridData(GridData.FILL, GridData.FILL,true,false,2,1));
		
		
		viewerResults = new TableViewer(this, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER | SWT.FULL_SELECTION | SWT.READ_ONLY);
		
		tableResuts = viewerResults.getTable();
		tableResuts.setLayoutData(new GridData(GridData.FILL, GridData.FILL,true,true,2,1));
		
		tableResuts.setLinesVisible(true);
		tableResuts.setHeaderVisible(true);
		
		
		viewerResults.refresh();
	}
	
/*
 * Fill data from the snapshot
 */
	private void fillSnapshot(){
		
	//Content provider
		viewerResults.setContentProvider(new IStructuredContentProvider(){

			@SuppressWarnings("unchecked")
			public Object[] getElements(Object inputElement) {
				java.util.List<List> currentElement = (ArrayList)inputElement;
				return currentElement.toArray();
			}
		
			public void dispose() {
			}
	
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}
		});	
		
	//Label provider
		
		 viewerResults.setLabelProvider(new ITableLabelProvider(){

			 public Image getColumnImage(Object element, int columnIndex) {
				 return null;
			 }

			 public String getColumnText(Object element, int columnIndex) {
				 
				 return ((List)element).get(columnIndex) + "";
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
		 
	//Columns
		 
		 viewerResults.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL,true,true,2,1));

		 for(String nameCol : snapShot.getListColumns()){
			 TableColumn c = new TableColumn(viewerResults.getTable(), SWT.NONE);
			 c.setText(nameCol);
			 c.setWidth(150);
		 }
		 
	//Input
		 viewerResults.setInput(snapShot.getListValues());
		 
		 labelRecords.setText("  Total: " + ((List)viewerResults.getInput()).size() + " record(s) shown.");
		 
		 viewerResults.refresh();
		
	}

	public Button getBtnRefresh() {
		return btnRefresh;
	}

	public TableViewer getViewerResults() {
		return viewerResults;
	}

	public Table getTableResuts() {
		return tableResuts;
	}

	public Label getLabelRecords() {
		return labelRecords;
	}

	public DataSet getDataSet() {
		return dataSet;
	}

	public Snapshot getSnapShot() {
		return snapShot;
	}

	public void setSnapShot(Snapshot snapShot) {
		this.snapShot = snapShot;
	}


}
