package bpm.gateway.ui.tools.dialogs;

import java.math.BigInteger;
import java.util.Date;

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

import bpm.gateway.core.StreamDescriptor;
import bpm.gateway.core.StreamElement;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.server.userdefined.Variable;
import bpm.gateway.core.transformations.calcul.Calculation;
import bpm.gateway.core.transformations.calcul.Script;
import bpm.gateway.core.transformations.utils.DefinitionXSD;
import bpm.gateway.ui.composites.CalculatorComposite;
import bpm.gateway.ui.i18n.Messages;

import com.ibm.icu.util.Calendar;

public class DialogCalculationEditor extends Dialog {

	
	private CalculatorComposite calcComposite;
	private Text name;
	private Combo type;
	
	private Script script;
	
	private Calculation calculation;
	
	private boolean isXSDCalculation;
	private Transformation transfo;
	private DefinitionXSD definitionXSD;
	
	/**
	 * Used for a Calculation Transformation
	 */
	public DialogCalculationEditor(Shell parentShell, Calculation calculation, Script script) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.calculation = calculation;
		this.transfo = calculation;
		this.script = script;
		this.isXSDCalculation = false;
	}
	
	/**
	 * Used for a XSD Definition
	 */
	public DialogCalculationEditor(Shell parentShell, Transformation transfo, DefinitionXSD definitionXSD, Script script) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.transfo = transfo;
		this.definitionXSD = definitionXSD;
		this.script = script;
		this.isXSDCalculation = true;
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
		type.setItems(Variable.VARIABLES_TYPES);
		
		
		calcComposite = new CalculatorComposite(main, SWT.NONE, CalculatorComposite.TYPE_CALC);
		calcComposite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		
		return main;
	}

	@Override
	protected void initializeBounds() {
		getShell().setText(Messages.DialogCalculationEditor_2);
		getShell().setSize(800, 600);
		
		if (transfo != null){
			fillContent();
		}
		
	}
	
	private void fillContent(){
		if (script != null){
			name.setText(script.getName());
			type.select(script.getType());
		}
		else {
			name.setText(Messages.CalculationSection_1);
		}
		
		if (transfo != null){
			calcComposite.setInput(script, transfo);
		}
	}

	@Override
	protected void okPressed() {
		if(script == null) {
			script = new Script();
		}
		else {
			if(!isXSDCalculation) {
				calculation.removeScript(script);
			}
		}
		
		script.setName(name.getText());
		script.setScriptFunction(calcComposite.getScript());
		script.setType(type.getSelectionIndex());
		
		try {
			if(transfo != null && transfo.getDescriptor(transfo) != null && transfo.getDescriptor(transfo).getStreamElements() != null) {
				for(StreamElement e : transfo.getDescriptor(transfo).getStreamElements()){
					if (e.name.equals(name)){
						e.type = type.getSelectionIndex();
						e.typeName = Variable.VARIABLES_TYPES[type.getSelectionIndex()];
					}
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		if(!isXSDCalculation) {
			if(!calculation.addScript(script)) {
				MessageDialog.openWarning(getShell(), Messages.DialogCalculationEditor_3, Messages.DialogCalculationEditor_4);
			}
			else {
				transfo.refreshDescriptor();
				super.okPressed();
			}
		}
		else {
			definitionXSD.setColumnId(script);
			transfo.refreshDescriptor();
			super.okPressed();
		}
	}
	
	
	private String validateScript() throws Exception{
		Context ctxt = ContextFactory.getGlobal().enterContext();
		
		String test = transfo.getDocument().getStringParser().getValue(transfo.getDocument(), calcComposite.getScript());
		String formula = Calculation.PREBUILT_FUNCTIONS + "\r\n" + test; //$NON-NLS-1$
		
		StreamDescriptor desc = transfo.getDescriptor(transfo);
		
		for(int i = 0; i < desc.getColumnCount(); i++){
			Class<?> colClass = Class.forName(desc.getStreamElements().get(i).className);
			if (Date.class.isAssignableFrom(colClass)){
				formula = formula.replace("{$" + desc.getStreamElements().get(i).name + "}", "new Date(" + Calendar.getInstance().getTimeInMillis() + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			}
			else if (String.class.isAssignableFrom(colClass)){
				formula = formula.replace("{$" + desc.getStreamElements().get(i).name + "}", "'string_" + i + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			}
			else{
				formula = formula.replace("{$" + desc.getStreamElements().get(i).name + "}", 10.1f + "");	 //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
		}
		ScriptableObject scope = ctxt.initStandardObjects();
		Object result = null;
		
		try{
			result = ctxt.evaluateString(scope, formula, "<cmd>", 1, null); //$NON-NLS-1$
		}catch(Exception ex){
			throw new Exception(Messages.DialogCalculationEditor_16 + ex.getMessage());
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
		String resultString = formula.substring((Calculation.PREBUILT_FUNCTIONS + "\r\n").length()) + " ----> " ; //$NON-NLS-1$ //$NON-NLS-2$
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
					throw new Exception(Messages.DialogCalculationEditor_19);
				}
			}catch(Exception ex2){
				throw new Exception(Messages.DialogCalculationEditor_20 + resultString, ex2);
			}
			
		}
		
		return resultString ;

	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		
		Button bt = createButton(parent, IDialogConstants.BACK_ID, Messages.DialogCalculationEditor_21,true);
		bt.addSelectionListener(new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			try{
				MessageDialog.openInformation(getShell(), Messages.DialogCalculationEditor_22, validateScript());
			}catch(Exception ex){
				MessageDialog.openError(getShell(), Messages.DialogCalculationEditor_23, ex.getMessage());
				return;
			}
		}});
		super.createButtonsForButtonBar(parent);
		
	}
	
	
}
