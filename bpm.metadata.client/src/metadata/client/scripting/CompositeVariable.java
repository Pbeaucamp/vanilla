package metadata.client.scripting;

import metadata.client.i18n.Messages;
import metadataclient.Activator;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import bpm.metadata.scripting.Variable;
import bpm.metadata.scripting.VariableType;

public class CompositeVariable extends Composite{
	private static Color red = new Color(Display.getDefault(), 255, 0, 0);
	
	private ComboViewer type;
	private Text name;
	private Text symbol;
	private Label errorMessage;
	
	private Variable variable;
	
	private Viewer viewer;
	private Button ok, cancel;
	
	private boolean isFilling = false;
	
	public CompositeVariable(Composite parent, int style) {
		super(parent, style);
		this.setLayout(new GridLayout(2, false));
		createContent();
	}
	public CompositeVariable(Composite parent, int style, Variable variable) {
		this(parent, style);
		this.variable = variable;
		if (variable != null){
			fill();
		}
		
	}
	public CompositeVariable(Composite parent, int style, Variable variable, Viewer viewer){
		this(parent, style, variable);
		this.viewer = viewer;
		createButtons();
		addListener(SWT.Selection, new Listener() {
			
			public void handleEvent(Event event) {
				if (isFilling){
					return;
				}
				ok.setEnabled(isFilled());
				cancel.setEnabled(true);
			}
		});
	}
	
	
	private void createButtons(){
		Composite c = new Composite(this, SWT.NONE);
		c.setLayout(new GridLayout(2, false));
		c.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		
		ok = new Button(c, SWT.PUSH);
		ok.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		ok.setText(Messages.CompositeVariable_0);
		ok.setEnabled(false);
		ok.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getVariable();
				viewer.refresh();
				cancel.setEnabled(false);
				ok.setEnabled(false);
			}
		});
		
		cancel = new Button(c, SWT.PUSH);
		cancel.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		cancel.setText(Messages.CompositeVariable_1);
		cancel.setEnabled(false);
		cancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				fill();
				cancel.setEnabled(false);
				ok.setEnabled(false);
			}
		});
	}
	
	private void createContent(){
		Label l = new Label(this, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText(Messages.CompositeVariable_2);
		
		name = new Text(this, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		name.addModifyListener(new ModifyListener() {
			
			public void modifyText(ModifyEvent e) {
				notifyListeners(SWT.Selection, new Event());
				
			
				for(Variable v : Activator.getDefault().getModel().getVariables()){
					if (v.getName().trim().equals(name.getText().trim()) && v != variable){
						setErrorMessage(Messages.CompositeVariable_3);
						symbol.setText(""); //$NON-NLS-1$
						return;
					}
				}
				
				if (name.getText().trim().equals("")){ //$NON-NLS-1$
					setErrorMessage(Messages.CompositeVariable_6);
					symbol.setText(""); //$NON-NLS-1$
					return;
				}
				setErrorMessage(null);
				symbol.setText("{$" + name.getText() + "}"); //$NON-NLS-1$ //$NON-NLS-2$
			}
		});
	
		l = new Label(this, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText(Messages.CompositeVariable_10);
		
		type = new ComboViewer(this, SWT.READ_ONLY);
		type.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		type.setContentProvider(new IStructuredContentProvider() {
			
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
			
			public void dispose() {}
			
			public Object[] getElements(Object inputElement) {
				return (Object[])inputElement;
			}
		});
		type.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				return ((VariableType)element).name();
			}
		});
		type.setInput(new VariableType[]{VariableType.String, VariableType.Integer, VariableType.Float});
		type.setSelection(new StructuredSelection(VariableType.String));
		type.addSelectionChangedListener(new ISelectionChangedListener() {
			
			public void selectionChanged(SelectionChangedEvent event) {
				notifyListeners(SWT.Selection, new Event());
			}
		});
		
		l = new Label(this, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText(Messages.CompositeVariable_11);
		
		symbol = new Text(this, SWT.BORDER);
		symbol.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		symbol.setEnabled(false);
		
		errorMessage = new Label(this, SWT.NONE);
		errorMessage.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false, 2, 1));
		errorMessage.setForeground(red);
		
	}
	
	private void setErrorMessage(String value){
		if (value == null){
			errorMessage.setText(""); //$NON-NLS-1$
			errorMessage.setVisible(false);
			return;
		}
		
		errorMessage.setText(value);
		errorMessage.setVisible(true);
		
	}

	public boolean isFilled(){
		return !errorMessage.isVisible() && !name.getText().trim().equals(""); //$NON-NLS-1$
	}
	
	/**
	 * fill the composite with the Variable 
	 */
	private void fill(){
		
		if (variable == null){
			return;
		}
		isFilling = true;
		name.setText(variable.getName());
		type.setSelection(new StructuredSelection(variable.getType()));
		isFilling = false;
	}
	
	
	/**
	 * if Variable is null, create it
	 * init variable with the specified fields
	 * @return
	 */
	public Variable getVariable(){
		if (variable == null){
			variable =  new Variable();
		}
		
		variable.setName(name.getText());
		variable.setType((VariableType)((IStructuredSelection)type.getSelection()).getFirstElement());
		
		return variable;
	}
}
