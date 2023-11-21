package bpm.metadata.ui.query.resources.dialogs;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import bpm.metadata.layer.business.IBusinessPackage;
import bpm.metadata.layer.business.IBusinessTable;
import bpm.metadata.layer.business.SQLBusinessTable;
import bpm.metadata.layer.logical.ICalculatedElement;
import bpm.metadata.layer.logical.IDataStream;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.query.Formula;
import bpm.metadata.ui.query.i18n.Messages;

public class DialogComputedColumn extends Dialog{

	private TreeViewer viewer;
	private Text formula, name;
	
	private Formula formulaObject;
	
	private final String NODE_OPERATOR = "Operators"; //$NON-NLS-1$
	private final String[] NODES = new String[]{"+", "-", "*", "/"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	
	private IBusinessPackage pack;
	private String groupName;
	
	
	public DialogComputedColumn(Shell parentShell, IBusinessPackage pack, String groupName) {
		super(parentShell);
		this.pack = pack;
		this.groupName = groupName;
		this.setShellStyle(getShellStyle() | SWT.RESIZE);
	}
	
	public DialogComputedColumn(Shell shell, IBusinessPackage pack, String groupName,	Formula formula) {
		this(shell, pack, groupName);
		initFormula(formula);
	}
	
	private void initFormula(Formula formula){
		this.formulaObject = formula;
	}

	
	
	
	private void createContent(Composite parent){
		
		Label l = new Label(parent, SWT.NONE);
		l.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		l.setText(Messages.DialogComputedColumn_5);
		
		name = new Text(parent, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		viewer = new TreeViewer(parent, SWT.NONE);
		viewer.getTree().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		viewer.setContentProvider(new ITreeContentProvider(){

			public Object[] getChildren(Object parentElement) {
				List<Object> l = new ArrayList<Object>();
				if (parentElement == NODE_OPERATOR){
					return NODES;
				}
				else if (parentElement instanceof IBusinessTable){
					l.addAll(((IBusinessTable)parentElement).getChilds(groupName));
					l.addAll(((IBusinessTable)parentElement).getColumns(groupName));
				}
				
				else if (parentElement instanceof String){
					l.add(NODE_OPERATOR);
				}
				return l.toArray(new Object[l.size()]);
			}

			public Object getParent(Object element) {
				if (element == NODE_OPERATOR){
					return null;
				}
				else if (element instanceof IBusinessTable){
					return ((IBusinessTable)element).getParent();
				}
				else if (element instanceof String){
					return NODE_OPERATOR;
				}
				return null;
			}

			public boolean hasChildren(Object element) {
				if (element == NODE_OPERATOR){
					return true;
				}
				if (element instanceof IBusinessTable){
					
					return ((IBusinessTable)element).getColumns(groupName).size() +((IBusinessTable)element).getChilds(groupName).size() > 0; 
					
				}
				return false;
			}

			public Object[] getElements(Object inputElement) {
				IBusinessPackage p = (IBusinessPackage) inputElement;
				
				List<Object> l = new ArrayList<Object>();
				l.add(NODE_OPERATOR);
				
				l.addAll(p.getBusinessTables(groupName));
				
				return l.toArray(new Object[l.size()]);
			}

			public void dispose() {}

			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {}
			
		});
		viewer.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				if (element instanceof IBusinessTable){
					return ((SQLBusinessTable)element).getOuputName(Locale.getDefault());
				}
				else if (element instanceof IDataStreamElement){
					return ((IDataStreamElement)element).getOuputName(Locale.getDefault());
				}
				return super.getText(element);
			}
		});
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			
			public void doubleClick(DoubleClickEvent event) {
				if (viewer.getSelection().isEmpty()){
					return;
				}
				
				Object o = ((IStructuredSelection)viewer.getSelection()).getFirstElement();
				if (o instanceof IDataStreamElement){
					IDataStreamElement col = (IDataStreamElement)o;
					try {
//						if (col.getOriginName().contains(".")){ //$NON-NLS-1$
//							formula.append(col.getOriginName());
//						}
//						else{
//							formula.append(col.getDataStream().getName() + "." + col.getOriginName()); //$NON-NLS-1$
//						}
						formula.append("`" + col.getDataStream().getName() + "`." + col.getOrigin().getShortName()); //$NON-NLS-1$
					} catch (Exception e) {
						if(col instanceof ICalculatedElement) {
							formula.append(((ICalculatedElement) col).getFormula());
						}
					}
					
				}
				else if (o instanceof String){
					formula.append((String)o);
				}
				
			}
		});
		
		formula = new Text(parent, SWT.BORDER | SWT.MULTI | SWT.WRAP);
		formula.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		fillDatas();
	}
	
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, false));
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		createContent(main);
		viewer.setInput(pack);
		return main;
	}
	
	private void fillDatas(){
		if (formulaObject != null){
			this.formula.setText(formulaObject.getFormula());
			this.name.setText(formulaObject.getName());
		}
		
	}
	
	@Override
	protected void initializeBounds() {
		getShell().setSize(600, 400);
		getShell().setText(Messages.DialogComputedColumn_8);
	}
	
	public Formula getFormula(){
		return formulaObject;
		
	}
	
	@Override
	protected void okPressed() {
		if (formulaObject == null){
			List<String> streams = new ArrayList<String>();
			for(IDataStream s : pack.getDataSources(groupName).get(0).getDataStreams()){
				if (formula.getText().contains(s.getOriginName() + ".")){ //$NON-NLS-1$
					streams.add(s.getName());
				}
			}
			formulaObject = new Formula(name.getText(), formula.getText(), streams);
		}
		else{
			formulaObject.setName(name.getText());
			formulaObject.setFormula(formula.getText());
		}
		
		super.okPressed();
	}
}
