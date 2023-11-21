package metadata.client.model.dialog;

import java.util.List;

import metadata.client.model.composites.CompositeFormula;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import bpm.metadata.layer.logical.ICalculatedElement;
import bpm.metadata.layer.logical.IDataStream;

public class DialogFormula extends Dialog {
	private CompositeFormula composite;
	private List<IDataStream> tables;
	private ICalculatedElement formula;
	
	public DialogFormula(Shell parentShell, List<IDataStream> tables) {
		super(parentShell);
		this.tables = tables;
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}
	
	public DialogFormula(Shell parentShell, List<IDataStream> tables, ICalculatedElement formula) {
		super(parentShell);
		this.tables = tables;
		this.formula = formula;
		setShellStyle(getShellStyle() | SWT.RESIZE);
		
		
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		parent.setLayout(new GridLayout());
		
		
		composite = new CompositeFormula(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.createModel(tables);
		composite.setFormula(formula);
		return parent;
	}

	@Override
	protected void okPressed() {
		composite.setFormula();
		formula = composite.getFormula();
		//to avoid some errors in teh query
		//we replace the OriginTable name
		//by the DataStreapm name for aliasing purpose
//		if (formula.getDataStream() != null){
//			for(IDataStream t : tables){
//				if (formula.getFormula().contains(t.getName() + ".")){
//					formula.setFormula(formula.getFormula().replace(t.getName() + ".", "`" + formula.getDataStream().getName() + "`."));
//				}
//				
//				
//			}
//		}
		
		
		super.okPressed();
	}

	public ICalculatedElement getFormula(){
		return formula;
	}

	@Override
	protected void initializeBounds() {
		getShell().setSize(600, 400);
	}
	
}
