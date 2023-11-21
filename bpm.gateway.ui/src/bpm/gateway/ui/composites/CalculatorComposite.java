package bpm.gateway.ui.composites;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;

import bpm.gateway.core.StreamElement;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.server.userdefined.Parameter;
import bpm.gateway.core.transformations.calcul.Script;
import bpm.gateway.core.transformations.calcul.Scripting;
import bpm.gateway.ui.Activator;
import bpm.gateway.ui.editors.GatewayEditorInput;
import bpm.gateway.ui.i18n.Messages;
import bpm.vanilla.platform.core.beans.service.IService;

public class CalculatorComposite extends Composite {
	public static final int TYPE_CALC = 0;
	public static final int TYPE_WEB = 1;
	
	private static final String[] TYPES = new String[]{"Fields", "Functions", "Parameters"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	
	private org.eclipse.swt.widgets.List typeList, categorieList, functionList; 
	private Text scriptText;
	private Label lblMessage;
	
	private String scriptName;
	
	private List<IService> inputs;
	private Transformation transfo;
	private int typeCalc;
	
	public CalculatorComposite(Composite parent, int style, int typeCalc) {
		super(parent, style);
		this.typeCalc = typeCalc;
		setLayout(new GridLayout(3, true));
		buildContent();
	}

	
	private void buildContent(){
		Label l0 = new Label(this, SWT.NONE);
		l0.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false, 3, 1));
		l0.setText(Messages.CalculatorComposite_0);
		
		scriptText = new Text(this, SWT.BORDER | SWT.WRAP | SWT.MULTI | SWT.V_SCROLL);
		scriptText.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 3, 1));
		scriptText.addModifyListener(new ModifyListener(){

			public void modifyText(ModifyEvent e) {
				notifyListeners(SWT.SELECTED, new Event());
			}
		});
		
		Label l = new Label(this, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.CalculatorComposite_4);
		
		Label l2 = new Label(this, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l2.setText(Messages.CalculatorComposite_5);
		
		Label l3 = new Label(this, SWT.NONE);
		l3.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l3.setText(Messages.CalculatorComposite_6);
		
		
		typeList = new org.eclipse.swt.widgets.List(this, SWT.V_SCROLL | SWT.BORDER);
		typeList.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		typeList.setItems(TYPES);
		typeList.addSelectionListener(new SelectionAdapter(){

			public void widgetSelected(SelectionEvent e) {
				int i = typeList.getSelectionIndex();
				
				if (i == 0){
					lblMessage.setText(""); //$NON-NLS-1$
					
					if(typeCalc == TYPE_CALC){
						categorieList.setItems(getFields());
					}
					else if(typeCalc == TYPE_WEB){
						categorieList.setItems(getServiceInputs());
					}
					functionList.setItems(new String[]{});
				}
				else if (i == 1){
					lblMessage.setText(""); //$NON-NLS-1$
					
					categorieList.setItems(Scripting.CATEGORIES);
					functionList.setItems(new String[]{});
				}
				else if (i == 2){
					lblMessage.setText(Messages.CalculatorComposite_3);
					
					if(typeCalc == TYPE_CALC){
						categorieList.setItems(getParameters());
					}
					functionList.setItems(new String[]{});
				}	
			}
		});
		
		
		
		
		
		categorieList = new org.eclipse.swt.widgets.List(this, SWT.V_SCROLL | SWT.BORDER);
		categorieList.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		categorieList.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {

				int i = categorieList.getSelectionIndex();
				if (typeList.getSelectionIndex() == 0 || typeList.getSelectionIndex() == 2){
					return;
				}
				switch(i){
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
		categorieList.addMouseListener(new MouseListener(){

			public void mouseDoubleClick(MouseEvent e) {
				
				if (typeList.getSelectionIndex() == 0){
					scriptText.setText(scriptText.getText() +  "{$" + categorieList.getSelection()[0] + "}"); //$NON-NLS-1$ //$NON-NLS-2$
					notifyListeners(SWT.SELECTED, new Event());
				}
				else if (typeList.getSelectionIndex() == 2){
					scriptText.setText(scriptText.getText() +  "{$P_" + categorieList.getSelection()[0] + "}"); //$NON-NLS-1$ //$NON-NLS-2$
					notifyListeners(SWT.SELECTED, new Event());
				}
			}

			public void mouseDown(MouseEvent e) { }

			public void mouseUp(MouseEvent e) { }
			
		});
		
		
		functionList = new org.eclipse.swt.widgets.List(this, SWT.V_SCROLL | SWT.BORDER);
		functionList.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		functionList.addMouseListener(new MouseListener(){

			public void mouseDoubleClick(MouseEvent e) {
				scriptText.setText(scriptText.getText() + functionList.getSelection()[0]);
				notifyListeners(SWT.SELECTED, new Event());
			}

			public void mouseDown(MouseEvent e) { }

			public void mouseUp(MouseEvent e) { }
			
		});
		
		lblMessage = new Label(this, SWT.NONE);
		lblMessage.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 3, 1));
	}
	
	public void setInput(Script script, Transformation transfo){
		this.transfo = transfo;
		if (script != null) {
			scriptText.setText(script.getScriptFunction());
			this.scriptName = new String(script.getName());
		}
	}
	
	public void setInput(List<IService> inputs, IService output){
		this.inputs = inputs;
		if (output != null) {
			scriptText.setText(output.getValue() != null ? output.getValue() : ""); //$NON-NLS-1$
			this.scriptName = new String(output.getValue() != null ? output.getValue() : ""); //$NON-NLS-1$
		}
	}
	
	
	private String[] getParameters() {
		List<String> l = new ArrayList<String>();
		
		
		IEditorPart e = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		
		if (e != null){
			for(Parameter p : ((GatewayEditorInput)e.getEditorInput()).getDocumentGateway().getParameters()){
				l.add(p.getName());
			}
		}
		
		return l.toArray(new String[l.size()]);
	}
	
	
	private String[] getFields(){
		List<String> l = new ArrayList<String>();
		if(transfo != null){
			try {
				for(StreamElement e : transfo.getDescriptor(transfo).getStreamElements()){
					if (scriptName == null || (scriptName != null && !e.name.equals(scriptName))){
						l.add(e.name);
					}
					
				}
			} catch (ServerException e) {
				e.printStackTrace();
			}
		}
		return l.toArray(new String[l.size()]);
	}
	
	
	private String[] getServiceInputs(){
		List<String> l = new ArrayList<String>();
		if(inputs != null){
			for(IService serv : inputs){
				l.add(serv.getName());
			}
		}
		return l.toArray(new String[l.size()]);
	}


	public String getScript() {
		return scriptText.getText();
	}
}
