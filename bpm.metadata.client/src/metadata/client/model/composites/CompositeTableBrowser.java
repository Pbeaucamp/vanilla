package metadata.client.model.composites;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import metadata.client.viewer.TableContentProvider;
import metadata.client.viewer.TableLabelProvider;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;

public class CompositeTableBrowser extends Composite {

	private TableViewer table;
	private List<List<String>> model;
	private List<String> colNames; 
	private List data;
	
	public CompositeTableBrowser(Composite parent, int style, List<String> colNames, List<List<String>> model) {
		super(parent, style);
		this.setLayout(new GridLayout());
		this.colNames = colNames;
		this.model = model;
		buildContent();
	}
	
	private void buildContent(){
		table = new TableViewer(this, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.VIRTUAL | SWT.FULL_SELECTION | SWT.MULTI);
		table.getTable().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		table.setContentProvider(new TableContentProvider());
		table.setLabelProvider(new TableLabelProvider());
		
		if (colNames != null){
			for(String s : colNames){
				TableColumn c = new TableColumn(table.getTable(), SWT.NONE);
				c.setText(s);
				c.setWidth(100);
			}
		}
		else if (model.get(0) != null){
			for (int i = 0; i< model.get(0).size(); i++){
				TableColumn c = new TableColumn(table.getTable(), SWT.NONE);
				c.setWidth(100);
			}
		}
		
		
		if (model.size()!=0){
			
			table.setInput(model);
			table.getTable().setHeaderVisible(true);
			table.getTable().setLinesVisible(false);
		}
		
		
		
		
	}

	public void setData(){
		if (!table.getSelection().isEmpty()){
			data = new ArrayList();
			Iterator it = ((IStructuredSelection)table.getSelection()).iterator();
			while(it.hasNext()) {
				Object a = ((List)it.next()).get(0);
				data.add(a);
			}
		}
		
	}
	
	@Override
	public List getData(){
		return data;
	}
	
}
