package bpm.gateway.ui.composites;

import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import bpm.gateway.core.server.userdefined.Parameter;
import bpm.gateway.ui.Activator;
import bpm.gateway.ui.ApplicationWorkbenchWindowAdvisor;
import bpm.gateway.ui.i18n.Messages;
import bpm.gateway.ui.views.ModelViewPart;

public class CompositeParameter extends Composite {

	private Text name;
	private Text defaultValue;
	private CCombo type;
	
	private Parameter parameter;
	
	private SelectionListener comboLst = new SelectionAdapter(){

		@Override
		public void widgetSelected(SelectionEvent e) {
			parameter.setType(type.getSelectionIndex());
			try{
				parameter.getValue();
				defaultValue.setBackground(null);
				
				
			}catch(Exception es){
				es.printStackTrace();
				ColorRegistry reg = PlatformUI.getWorkbench().getThemeManager().getCurrentTheme().getColorRegistry();
				Color color = reg.get(ApplicationWorkbenchWindowAdvisor.ERROR_COLOR_KEY);
				
				defaultValue.setBackground(color);
			}
		}
		
	};
	
	private ModifyListener listener = new ModifyListener(){

		public void modifyText(ModifyEvent e) {
			if (defaultValue.getText().equals("") ||  //$NON-NLS-1$
				name.getText().equals("") || //$NON-NLS-1$
				type.getSelectionIndex() == -1){
				
			}
			else{
				parameter.setName(name.getText());
				parameter.setType(type.getSelectionIndex());
				parameter.setDefaultValue(defaultValue.getText());
				
				/*
				 * test to get the value from the defaultValue string
				 * if it failed, the default is not correct
				 */
				try{
					parameter.getValue();
					defaultValue.setBackground(null);
					
					
				}catch(Exception es){
					es.printStackTrace();
					ColorRegistry reg = PlatformUI.getWorkbench().getThemeManager().getCurrentTheme().getColorRegistry();
					Color color = reg.get(ApplicationWorkbenchWindowAdvisor.ERROR_COLOR_KEY);
					
					defaultValue.setBackground(color);
				}
				
				
			}
			
			if (e.widget == name){
				ModelViewPart v = (ModelViewPart)Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ModelViewPart.ID);
				if (v != null){
					v.refresh();
				}
				
			}
			
		}
		
	};
	
	public CompositeParameter(Composite parent, int style) {
		super(parent, style);
		this.setLayout(new GridLayout(2, false));
		
		buildContent();
		setColor();
	}
	
	private void setColor(){
		this.setBackground(getParent().getBackground());
		for(Control  c : this.getChildren()){
			c.setBackground(getParent().getBackground());
		}
	}
	
	private void buildContent(){
		Label l = new Label(this, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.CompositeParameter_2);
		
		name = new Text(this, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
	
		
		Label l2 = new Label(this, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l2.setText(Messages.CompositeParameter_3);
		
		type = new CCombo(this, SWT.READ_ONLY);
		type.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		type.setItems(Parameter.PARAMETER_TYPES);
		
		Label l3 = new Label(this, SWT.NONE);
		l3.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l3.setText(Messages.CompositeParameter_4);
		
		defaultValue = new Text(this, SWT.BORDER);
		defaultValue.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

	}

	
	public void fillDatas(Parameter p){
		releaseListeners();
		parameter = p;
		if (p != null){
			
			name.setText(p.getName());
			type.select(p.getType());
			defaultValue.setText(p.getDefaultValue());
			
			attachListeners();
		}
		
	}
	
	public void attachListeners(){
		name.addModifyListener(listener);
		defaultValue.addModifyListener(listener);
		type.addSelectionListener(comboLst);
	}
	
	public void releaseListeners(){
		name.removeModifyListener(listener);
		defaultValue.removeModifyListener(listener);
		type.removeSelectionListener(comboLst);
	}
}
