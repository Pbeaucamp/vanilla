package bpm.metadata.ui.query.resources.composites;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.metadata.resource.IResource;
import bpm.metadata.resource.Prompt;
import bpm.metadata.ui.query.i18n.Messages;

public class CompositePrompt extends Composite implements ResourceBuilder{
	private static final Color RED = new Color(Display.getDefault(), 255, 0, 0);
	private Prompt prompt;
	
	private Text destination, name;
	private Combo operator;
	
	private Label errorLabel;

	public CompositePrompt(Composite parent, int style, Prompt prompt) {
		super(parent, style);
		this.setLayout(new GridLayout());
		buildContent();
		this.prompt = prompt;
		fillDatas();
	}
	
	private void buildContent(){
		createGeneral(this);
    	errorLabel = new Label(this, SWT.NONE);
    	errorLabel.setText(" xx"); //$NON-NLS-1$
    	errorLabel.setLayoutData(new GridData(GridData.FILL, GridData.END, true, false, 2, 1));
    	errorLabel.setForeground(RED);
    	errorLabel.setVisible(false);
	}
	
	
	private void setErrorMessage(String message){
		if (message == null){
			errorLabel.setVisible(false);
		}
		else{
			errorLabel.setText(message);
			errorLabel.setVisible(true);
		}
		
	}

	private Control createGeneral(Composite root){
		Composite parent = new Composite(root, SWT.NONE);
		parent.setLayout(new GridLayout(2, false));
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		
		Label l = new Label(parent, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText(Messages.CompositePrompt_0);
		
		name = new Text(parent, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
				
		
		Label l3 = new Label(parent, SWT.NONE);
		l3.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER,false, false));
		l3.setText(Messages.CompositePrompt_1); 
		
		destination = new Text(parent, SWT.BORDER);
		destination.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		destination.setEnabled(false);
	
		
		
		Label l4 = new Label(parent, SWT.NONE);
		l4.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER,false, false));
		l4.setText(Messages.CompositePrompt_2);
		
		operator = new Combo(parent, SWT.READ_ONLY);
		operator.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		operator.setItems(new String[]{"=", "<", "<=", ">", ">=", "!=", "<>", "IN", "LIKE", "BETWEEN"}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$
		operator.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				notifyListeners(SWT.Selection, new Event());
			}
		});
		
		
		return parent;
	}

	private void fillDatas(){
		name.setText(prompt.getName());
		if(prompt.getGotoDataStreamElement() != null) {
			destination.setText(prompt.getGotoDataStreamElement().getName());
		}
		else {
			destination.setText(prompt.getGotoSql());
		}
		operator.setText(prompt.getOperator());
	}
	
	public IResource getResource(){
		prompt.setName(name.getText());
		prompt.setOperator(operator.getText());
		
		return prompt;
	}
	
	public boolean isFilled(){
		return !errorLabel.isVisible() && !"".equals(name.getText().trim()) && !"".equals(operator.getText()); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
}
