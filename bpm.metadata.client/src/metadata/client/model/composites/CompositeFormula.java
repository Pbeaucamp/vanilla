package metadata.client.model.composites;

import java.util.List;

import metadata.client.i18n.Messages;
import metadata.client.trees.TreeDataStream;
import metadata.client.trees.TreeDataStreamElement;
import metadata.client.trees.TreeObject;
import metadata.client.trees.TreeParent;
import metadata.client.viewer.TreeContentProvider;
import metadata.client.viewer.TreeLabelProvider;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.metadata.layer.logical.ICalculatedElement;
import bpm.metadata.layer.logical.IDataStream;
import bpm.metadata.layer.logical.IDataStreamElement;

public class CompositeFormula extends Composite {

	private TreeViewer viewer;
	private Text formula, name;
	private Combo className;
	private Button isKpi;
	
	private ICalculatedElement calc;
	
	public CompositeFormula(Composite parent, int style) {
		super(parent, style);
		this.setLayout(new GridLayout(2, true));
		createContent(this);
	}
	
	
	
	private void createContent(Composite parent){
		
		Label l = new Label(parent, SWT.NONE);
		l.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		l.setText(Messages.CompositeFormula_0); //$NON-NLS-1$
		
		name = new Text(parent, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		

		Label l1 = new Label(parent, SWT.NONE);
		l1.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l1.setText(Messages.CompositeFormula_1);
		
		className = new Combo(parent, SWT.READ_ONLY);
		className.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		className.setItems(ICalculatedElement.TYPES_NAMES);
		
		isKpi = new Button(parent, SWT.CHECK);
		isKpi.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 1));
		isKpi.setText(Messages.CompositeFormula_2);		
		
		
		viewer = new TreeViewer(parent, SWT.NONE);
		viewer.getTree().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		viewer.setContentProvider(new TreeContentProvider());
		viewer.setLabelProvider(new TreeLabelProvider());
		viewer.addDoubleClickListener(new IDoubleClickListener(){


			public void doubleClick(DoubleClickEvent event) {
				if (viewer.getSelection().isEmpty()){
					return;
				}
				
				Object o = ((IStructuredSelection)viewer.getSelection()).getFirstElement();
				if (o instanceof TreeDataStreamElement){
					TreeDataStreamElement col = (TreeDataStreamElement)o;
					if(col.getDataStreamElement() instanceof ICalculatedElement) {
						formula.append(((ICalculatedElement)col.getDataStreamElement()).getFormula());
					}
					else {
						formula.append("`" + col.getDataStreamElement().getDataStream().getName() + "`" + "." + col.getDataStreamElement().getOrigin().getShortName());
					}
				}
				else{
					formula.append(((TreeObject)o).getName());
				}
			}
			
		});
//		viewer.addSelectionChangedListener(new ISelectionChangedListener(){
//
//			public void selectionChanged(SelectionChangedEvent event) {
//				final IStructuredSelection ss =  (IStructuredSelection)viewer.getSelection();
//				if (ss.isEmpty() || !(ss.getFirstElement() instanceof TreeTable)){
//					return;
//				}
//				
//				BusyIndicator.showWhile(Display.getDefault(), new Runnable(){
//					public void run(){
//						for(Object o : ss.toList()){
//							if (!(o instanceof TreeTable)){
//								continue;
//							}
//							TreeTable tt = (TreeTable)o;
//							
//							
//							
//							if (tt.getTable() instanceof SQLTable){
//								SQLTable t = (SQLTable)tt.getTable();
//								
//								if (!t.hasColumns()){
//									for(IColumn c : t.getColumns()){
//										TreeColumn tc = new TreeColumn(c);
//										tt.addChild(tc);
//									}
//								}
//								
//							}
//						}
//						
//						
//						
//						viewer.refresh();
//					}
//				});
//				
//			}
//			
//		});
		
		formula = new Text(parent, SWT.BORDER | SWT.MULTI | SWT.WRAP);
		formula.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
	}
	
	
	public void createModel(List<IDataStream> list){
		TreeParent root = new TreeParent(""); //$NON-NLS-1$
		
		//add columns
		for( IDataStream t : list){
			TreeDataStream tt = new TreeDataStream(t);
			for(IDataStreamElement elem : t.getElements()) {
				TreeDataStreamElement e = new TreeDataStreamElement(elem);
				tt.addChild(e);
			}
//			tt.buildChild();
			root.addChild(tt);
		}
		
		//add operators
		root.addChild(new TreeParent("+")); //$NON-NLS-1$
		root.addChild(new TreeParent("-")); //$NON-NLS-1$
		root.addChild(new TreeParent("*")); //$NON-NLS-1$
		root.addChild(new TreeParent("/")); //$NON-NLS-1$
		root.addChild(new TreeParent("(")); //$NON-NLS-1$
		root.addChild(new TreeParent(")")); //$NON-NLS-1$
		
		viewer.setInput(root);
		
		
	}

	
	public void setFormula(){
		if (calc == null){
			calc = new ICalculatedElement();
		}
		calc.setName(name.getText());
		calc.setFormula(formula.getText());
		calc.setClassType(className.getSelectionIndex());
		calc.setIsKpi(isKpi.getSelection());
	}
	
	public ICalculatedElement getFormula(){
		return calc;
	}



	public void setFormula(ICalculatedElement formula2) {
		if (formula2 == null){
			return;
		}
		calc = formula2;
		formula.setText(calc.getFormula());
		name.setText(calc.getName());
		
		String typeName = ""; //$NON-NLS-1$
		
		try{
			typeName = calc.getJavaClassName();
			if(typeName == null) {
				typeName = "";
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		if (typeName.contains(".")){ //$NON-NLS-1$
			
			typeName = typeName.substring(typeName.lastIndexOf(".") + 1); //$NON-NLS-1$
		}
		
		for(int i = 0; i < className.getItemCount(); i++){
			
			try{
				if (className.getItem(i).equals(typeName)){
					className.select(i);
				}
				
				if (className.getSelectionIndex() == -1){
					className.setText(typeName);
				}
				
			}catch(Exception ex){
				
			}
				
		}
		
		
		isKpi.setSelection(calc.isKpi());
	}
}
