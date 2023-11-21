package bpm.gateway.ui.resource.variables.wizard;

import java.util.Properties;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.gateway.core.manager.ResourceManager;
import bpm.gateway.core.server.userdefined.Variable;
import bpm.gateway.ui.Activator;
import bpm.gateway.ui.editors.GatewayEditorPart;
import bpm.gateway.ui.i18n.Messages;
import bpm.gateway.ui.tools.dialogs.DialogVanillaVariable;

public class VariablePage extends WizardPage {


	private Button bLocale, bEnvironment, bVanilla, vanillaVariable;
	
	private Text name, value;
	private Combo type;
	
	
	protected VariablePage(String pageName) {
		super(pageName);
		
	}

	public void createControl(Composite parent) {
		
		Composite mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayout(new GridLayout());
		mainComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		// create contents
		createPageContent(mainComposite);

		// page setting
		setControl( mainComposite );
		setPageComplete(true);
		
		
		
	}

	
	private Control createPageContent(Composite parent){
		Composite c = new Composite(parent, SWT.NONE);
		c.setLayout(new GridLayout());
		c.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		
		Group g = new Group(c, SWT.NONE);
		g.setLayout(new GridLayout());
		g.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		g.setText(Messages.VariablePage_0);
		
		RadioListener buttonListener = new RadioListener();
		
		bLocale = new Button(g, SWT.RADIO);
		bLocale.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		bLocale.setText(Messages.VariablePage_1);
		bLocale.addSelectionListener(buttonListener);
		bLocale.setEnabled(false);
		if (Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor() instanceof GatewayEditorPart){
			bLocale.setEnabled(true);
		}
		
		
		bEnvironment= new Button(g, SWT.RADIO);
		bEnvironment.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		bEnvironment.setText(Messages.VariablePage_2);
		bEnvironment.addSelectionListener(buttonListener);
		bEnvironment.addFocusListener(new FocusListener(){
			public void focusGained(FocusEvent e) {
				VariablePage.this.setMessage(Messages.VariablePage_3);
				
			}

			public void focusLost(FocusEvent e) {
				VariablePage.this.setMessage(""); //$NON-NLS-1$
				
			}
		});
		bEnvironment.setSelection(true);
		
		bVanilla= new Button(g, SWT.RADIO);
		bVanilla.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		bVanilla.setText(Messages.VariablePage_5);
		bVanilla.addSelectionListener(buttonListener);

		bVanilla.setEnabled(true);
		
		
		Group general = new Group(c, SWT.NONE);
		general.setLayout(new GridLayout(2, false));
		general.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		general.setText(Messages.VariablePage_6);
		
		
		vanillaVariable = new Button(c, SWT.PUSH);
		vanillaVariable.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, true, false, 2, 1));
		vanillaVariable.setText(Messages.VariablePage_7);
		vanillaVariable.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				DialogVanillaVariable d = new DialogVanillaVariable(getShell());
				
				if(d.open() == DialogVanillaVariable.OK){
					bpm.vanilla.platform.core.beans.Variable v = d.getVariable();
					name.setText(v.getName());
					
					switch(v.getType()){
					case bpm.vanilla.platform.core.beans.Variable.TYPE_BOOLEAN:
						type.select(Variable.BOOLEAN);
					case bpm.vanilla.platform.core.beans.Variable.TYPE_DOUBLE:
						type.select(Variable.FLOAT);
					case bpm.vanilla.platform.core.beans.Variable.TYPE_FLOAT:
						type.select(Variable.FLOAT);
					case bpm.vanilla.platform.core.beans.Variable.TYPE_INT:
						type.select(Variable.INTEGER);
					case bpm.vanilla.platform.core.beans.Variable.TYPE_STRING:
						type.select(Variable.STRING);
						
						
					}
					
					value.setText(v.getValue());
					
					
				}
			}
			
		});
		vanillaVariable.setEnabled(false);
		
		
		Label l = new Label(general, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.VariablePage_8);
		
		name = new Text(general, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		name.addFocusListener(new FocusListener(){

			public void focusGained(FocusEvent e) {
				VariablePage.this.setMessage(Messages.VariablePage_9);
				
			}

			public void focusLost(FocusEvent e) {
				VariablePage.this.setMessage(""); //$NON-NLS-1$
				
			}
			
		});
		name.addModifyListener(new ModifyListener(){

			public void modifyText(ModifyEvent e) {
				getContainer().updateButtons();
				
				for(Variable s : ResourceManager.getInstance().getVariables()){
					if (name.getText().equals(s.getName())){
						setPageComplete(false);
						setErrorMessage(Messages.VariablePage_11);
						break;
					}
					else{
						setErrorMessage(null);
					}
				}
			}
			
		});
		
		
		Label l3 = new Label(general, SWT.NONE);
		l3.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l3.setText(Messages.VariablePage_12);
		
		type = new Combo(general, SWT.READ_ONLY);
		type.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		type.setItems(Variable.VARIABLES_TYPES);
		type.select(0);
		
		Label l2 = new Label(general, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.FILL, false, true));
		l2.setText(Messages.VariablePage_13);
		
		value = new Text(general, SWT.BORDER | SWT.WRAP | SWT.MULTI);
		value.addFocusListener(new FocusListener(){

			public void focusGained(FocusEvent e) {
				VariablePage.this.setMessage(Messages.VariablePage_14);
				
			}

			public void focusLost(FocusEvent e) {
				VariablePage.this.setMessage(""); //$NON-NLS-1$
				
			}
			
		});
		value.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
		
		
				
		
		return c;
	}
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.WizardPage#isPageComplete()
	 */
	@Override
	public boolean isPageComplete() {
		return !name.getText().equals("") && (bLocale.getSelection() || bVanilla.getSelection() || bEnvironment.getSelection()); //$NON-NLS-1$
	}


	/**
	 * 
	 * @return a properties object containing the values filled is that page
	 * the Keys are : 
	 *  - name
	 *  - description
	 *  - type
	 */
	protected Properties getValues(){
		Properties p =  new Properties ();
		
		p.setProperty("name", name.getText()); //$NON-NLS-1$
		p.setProperty("value", value.getText()); //$NON-NLS-1$
		p.put("dataType", type.getSelectionIndex()); //$NON-NLS-1$

		p.put("type", getType()); //$NON-NLS-1$

		
		//TODO: take care of the other Server type
		
		
		return p;
	}


	
	
	
	


	protected Integer getType(){
		if (bEnvironment.getSelection()){
			return Variable.ENVIRONMENT_VARIABLE;
		}
		if (bVanilla.getSelection()){
			return Variable.VANILLA_VARIABLE;
		}
		if (bLocale.getSelection()){
			return Variable.LOCAL_VARIABLE;
		}
//		if ((bVanilla.getSelection())){
//			return Server.REPOSITORY_TYPE;
//		}
		
		return null;
	}


	private class RadioListener extends SelectionAdapter{

		/* (non-Javadoc)
		 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
		 */
		@Override
		public void widgetSelected(SelectionEvent e) {

			name.setEnabled(!bVanilla.getSelection());
			type.setEnabled(!bVanilla.getSelection());
			value.setEnabled(!bVanilla.getSelection());
			vanillaVariable.setEnabled(bVanilla.getSelection());
			
			if (bVanilla.getSelection()){
				name.setText(""); //$NON-NLS-1$
				value.setText(""); //$NON-NLS-1$
			}
			
			VariablePage.this.getContainer().updateButtons();
		}
		
	}
	
	
	
}
