package bpm.workflow.ui.composites;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.workflow.runtime.model.WorkflowModel;
import bpm.workflow.runtime.model.activities.CalculationActivity;
import bpm.workflow.runtime.resources.Script;
import bpm.workflow.runtime.resources.Scripting;
import bpm.workflow.runtime.resources.variables.ListVariable;
import bpm.workflow.ui.Activator;
import bpm.workflow.ui.Messages;

/**
 * Composite for the calculation activity
 * 
 * @author CAMUS, MARTIN
 * 
 */
public class CalculatorComposite extends Composite {
	private static final String[] TYPES = new String[] { "Variables", "Functions" }; //$NON-NLS-1$ //$NON-NLS-2$

	private org.eclipse.swt.widgets.List typeList, categorieList, functionList;
	private Text scriptText;

	/**
	 * Create a calculator composite
	 * 
	 * @param parent
	 *            : the parent composite
	 * @param style
	 */
	public CalculatorComposite(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(3, true));
		buildContent();
	}

	private void buildContent() {
		Label l0 = new Label(this, SWT.NONE);
		l0.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 3, 1));
		l0.setText(Messages.CalculatorComposite_0);

		scriptText = new Text(this, SWT.BORDER);
		scriptText.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 3, 1));
		scriptText.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				notifyListeners(SWT.SELECTED, new Event());

			}

		});

		Label l = new Label(this, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.CalculatorComposite_3);

		Label l2 = new Label(this, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l2.setText(Messages.CalculatorComposite_4);

		Label l3 = new Label(this, SWT.NONE);
		l3.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l3.setText(Messages.CalculatorComposite_5);

		typeList = new org.eclipse.swt.widgets.List(this, SWT.V_SCROLL | SWT.BORDER);
		typeList.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		typeList.setItems(TYPES);
		typeList.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {

			}

			public void widgetSelected(SelectionEvent e) {
				int i = typeList.getSelectionIndex();

				if(i == 0) {
					categorieList.setItems(getFields());
					functionList.setItems(new String[] {});
				}
				else if(i == 1) {
					categorieList.setItems(Scripting.CATEGORIES);
					functionList.setItems(new String[] {});
				}

			}

		});

		categorieList = new org.eclipse.swt.widgets.List(this, SWT.V_SCROLL | SWT.BORDER);
		categorieList.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		categorieList.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {

				int i = categorieList.getSelectionIndex();
				if(typeList.getSelectionIndex() == 0) {
					return;
				}
				switch(i) {
					case Scripting.CAT_MATH:
						functionList.setItems(Scripting.MATH_FUNCTIONS);
						break;
					case Scripting.CAT_OPERATORS:
						functionList.setItems(Scripting.OPERATOR_FUNCTIONS);
						break;
					case Scripting.CAT_LOGICAL:
						functionList.setItems(Scripting.LOGICAL_FUNCTIONS);
						break;
					case Scripting.CAT_TRIGO:
						functionList.setItems(Scripting.TRIGO_FUNCTIONS);
						break;
					case Scripting.CAT_STRINGS:
						functionList.setItems(Scripting.STRING_FUNCTIONS);
						break;
					case Scripting.CAT_DATE:
						functionList.setItems(Scripting.DATE_FUNCTIONS);
						break;
					case Scripting.CAT_PREBUILT:
						functionList.setItems(Scripting.PREBUILT_FUNCTIONS);
						break;
				}
			}
		});
		categorieList.addMouseListener(new MouseListener() {

			public void mouseDoubleClick(MouseEvent e) {

				if(typeList.getSelectionIndex() == 0) {
					scriptText.setText(scriptText.getText() + categorieList.getSelection()[0]);
					notifyListeners(SWT.SELECTED, new Event());
				}

			}

			public void mouseDown(MouseEvent e) {

			}

			public void mouseUp(MouseEvent e) {

			}

		});

		functionList = new org.eclipse.swt.widgets.List(this, SWT.V_SCROLL | SWT.BORDER);
		functionList.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		functionList.addMouseListener(new MouseListener() {

			public void mouseDoubleClick(MouseEvent e) {
				scriptText.setText(scriptText.getText() + functionList.getSelection()[0]);
				notifyListeners(SWT.SELECTED, new Event());

			}

			public void mouseDown(MouseEvent e) {

			}

			public void mouseUp(MouseEvent e) {

			}

		});

	}

	/**
	 * Set the current script
	 * 
	 * @param script
	 * @param transfo
	 *            : calculation activity
	 */
	public void setInput(Script script, CalculationActivity transfo) {
		if(script != null) {
			scriptText.setText(script.getScriptFunction());
		}

	}

	/**
	 * 
	 * @return the variables names which are in the workspace
	 */
	private String[] getFields() {
		List<String> l = new ArrayList<String>();

		for(String variableName : ListVariable.getInstance().getArray((WorkflowModel) Activator.getDefault().getCurrentModel())) {
			if(variableName.equalsIgnoreCase("{$VANILLA_HOME}") || variableName.equalsIgnoreCase("{$VANILLA_FILES}")) { //$NON-NLS-1$ //$NON-NLS-2$

			}

			else {
				l.add(variableName);
			}

		}

		return l.toArray(new String[l.size()]);
	}

	/**
	 * 
	 * @return the current script text
	 */
	public String getScript() {
		return scriptText.getText();
	}
}
