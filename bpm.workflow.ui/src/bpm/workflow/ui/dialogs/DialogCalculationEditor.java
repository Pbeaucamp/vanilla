package bpm.workflow.ui.dialogs;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.JavaAdapter;
import org.mozilla.javascript.ScriptableObject;

import bpm.workflow.runtime.model.WorkflowModel;
import bpm.workflow.runtime.model.activities.CalculationActivity;
import bpm.workflow.runtime.resources.Script;
import bpm.workflow.runtime.resources.variables.ListVariable;
import bpm.workflow.runtime.resources.variables.Variable;
import bpm.workflow.ui.Activator;
import bpm.workflow.ui.Messages;
import bpm.workflow.ui.composites.CalculatorComposite;
import bpm.workflow.ui.views.ResourceViewPart;

/**
 * Create a dialog for the calculation activity
 * @author CAMUS, MARTIN
 *
 */
public class DialogCalculationEditor extends Dialog {

	
	private CalculatorComposite calcComposite;
	private Text name;
	private Combo type;
	private String oldName;
	private CalculationActivity calculationActivity;
	private Script script;
	
	public DialogCalculationEditor(Shell parentShell, CalculationActivity transfo, Script script) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.calculationActivity = transfo;
		this.script = script;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, false));
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label l = new Label(main, SWT.NONE);
		l.setText(Messages.DialogCalculationEditor_0);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		
		name = new Text(main, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		
		Label l2 = new Label(main, SWT.NONE);
		l2.setText(Messages.DialogCalculationEditor_1);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		
		type = new Combo(main, SWT.READ_ONLY);
		type.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		type.setItems(Variable.TYPES_NAMES); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		
		
		calcComposite = new CalculatorComposite(main, SWT.NONE);
		calcComposite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		
		return main;
	}

	@Override
	protected void initializeBounds() {
		getShell().setText(Messages.DialogCalculationEditor_7);
		getShell().setSize(800, 600);
		
		if (calculationActivity != null){
			fillContent();
		}
		
	}
	
	private void fillContent(){
		if (script != null){
			oldName = script.getName();
			name.setText(script.getName());
			type.select(script.getType());
		}
		
		if (calculationActivity != null){
			calcComposite.setInput(script, calculationActivity);
		}
	}

	@Override
	protected void okPressed() {
		script.setName(name.getText());
		script.setScriptFunction(calcComposite.getScript());
		script.setType(type.getSelectionIndex());
		
		((WorkflowModel)Activator.getDefault().getCurrentModel()).removeResource(ListVariable.getInstance().getVariable(oldName).getId());
		ListVariable.getInstance().removeVariable(oldName);
		
		Properties prop = new Properties();
		prop.setProperty("name", name.getText()); //$NON-NLS-1$
		prop.setProperty("default","calculated"); //$NON-NLS-1$ //$NON-NLS-2$
		prop.setProperty("type", type.getSelectionIndex() + ""); //$NON-NLS-1$ //$NON-NLS-2$
		
		try{
			Variable v = new Variable(prop);
			ListVariable.getInstance().addVariable(v);
			((WorkflowModel)Activator.getDefault().getCurrentModel()).addResource(ListVariable.getInstance().getVariable(v.getName()));
			ResourceViewPart view = (ResourceViewPart)Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ResourceViewPart.ID);

			if (view != null){
				view.refresh();
			}
		}
		catch(Exception e){

		}

		super.okPressed();
	}
	
	private String validateScript() throws Exception{
		Context ctxt = ContextFactory.getGlobal().enterContext();
		String formula = calcComposite.getScript();
		
		
		
		for(String variableName : ListVariable.getInstance().getArray((WorkflowModel) Activator.getDefault().getCurrentModel())){
			if (formula.contains("{$" + variableName + "}")){ //$NON-NLS-1$ //$NON-NLS-2$
				Variable v = ListVariable.getInstance().getVariable(variableName);
				switch(v.getType()){
				case Variable.DATE:
					formula = formula.replace("{$" + variableName+ "}", "new Date(" + Calendar.getInstance().getTimeInMillis() + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
					break;
				case Variable.STRING:
				case Variable.ENUMERATION:
					formula = formula.replace("{$" + variableName + "}", "'string_'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					break;
				case Variable.INTEGER:
				case Variable.FLOAT:
					formula.replace("{$" + variableName + "}", 10.1f + ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					break;
				case Variable.BOOLEAN:
					formula.replace("{$" + variableName + "}", "true"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					break;
				}
			}
			
			
		}
		
		
		ScriptableObject scope = ctxt.initStandardObjects();
		Object result = null;
		
		try{
			ctxt.evaluateString(scope, formula, "<cmd>", 1, null); //$NON-NLS-1$
		}catch(Exception ex){
			throw new Exception(Messages.DialogCalculationEditor_29 + ex.getMessage());
		}
		
		Object o = null;
		Class<?> resultClass = null;
		
		switch(type.getSelectionIndex()){
		case Variable.BOOLEAN:
			resultClass = Boolean.class;
			break;
		case Variable.DATE:
			resultClass = Date.class;
			break;
		case Variable.FLOAT:
			resultClass = Double.class;
			break;
		case Variable.INTEGER:
			resultClass = BigInteger.class;
			break;
		case Variable.STRING:
			resultClass = String.class;
			break;
		default:
			resultClass = Object.class;
			
		}
		String resultString = formula + " ----> " ; //$NON-NLS-1$
		try{
			o = JavaAdapter.convertResult(result, resultClass);
			resultString = resultString + o;
		}catch(EvaluatorException ex){
			try{
				if (Number.class.isAssignableFrom(resultClass)){
					o = JavaAdapter.convertResult(result, Date.class);
					o = ((Date)o).getTime();
					resultString = resultString + o;
				}
				else{
					throw new Exception(Messages.DialogCalculationEditor_31);
				}
			}catch(Exception ex2){
				throw new Exception(Messages.DialogCalculationEditor_32 + resultString, ex2);
			}
			
		}
		
		return resultString ;
//		return "";

	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		
		
		Button bt = createButton(parent, IDialogConstants.BACK_ID, Messages.DialogCalculationEditor_33,true);
		bt.addSelectionListener(new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			try{
				MessageDialog.openInformation(getShell(), Messages.DialogCalculationEditor_34, validateScript());
			}catch(Exception ex){
				MessageDialog.openError(getShell(), Messages.DialogCalculationEditor_35, ex.getMessage());
				return;
			}
		}});
		super.createButtonsForButtonBar(parent);
		
	}
	
}
