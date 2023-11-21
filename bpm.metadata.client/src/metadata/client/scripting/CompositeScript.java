package metadata.client.scripting;

import java.util.List;

import metadata.client.i18n.Messages;
import metadata.client.model.dialog.DialogConnection;
import metadataclient.Activator;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;




import bpm.metadata.scripting.PrebuiltFunction;
import bpm.metadata.scripting.Script;
import bpm.metadata.scripting.Variable;

public class CompositeScript extends Composite{
	private static Color red = new Color(Display.getDefault(), 255, 0, 0);
	
	private ListViewer prebuiltFunctions;
	private ListViewer variables;
	private Text name;
	
	private SourceViewer definition;
	private IDocument doc;
	
	private Label errorMessage;
	
	private Script script;
	
	private Viewer viewer;
	private Button ok, cancel;
	
	private boolean isFilling = false;
	
	public CompositeScript(Composite parent, int style) {
		super(parent, style);
		this.setLayout(new GridLayout(2, false));
//		script = new Script();
		createContent();
		fill();
	}
	public CompositeScript(Composite parent, int style, Script script) {
		this(parent, style);
		this.script = script;
		fill();
				
	}
	public CompositeScript(Composite parent, int style, Script script, Viewer viewer){
		this(parent, style, script);
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
		ok.setText(Messages.CompositeScript_0);
		ok.setEnabled(false);
		ok.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getScript();
				viewer.refresh();
				cancel.setEnabled(false);
				ok.setEnabled(false);
			}
		});
		
		cancel = new Button(c, SWT.PUSH);
		cancel.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		cancel.setText(Messages.CompositeScript_1);
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
		l.setText(Messages.CompositeScript_2);
		
		name = new Text(this, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		name.addModifyListener(new ModifyListener() {
			
			public void modifyText(ModifyEvent e) {
				notifyListeners(SWT.Selection, new Event());
				
			
				for(Script v : Activator.getDefault().getModel().getScripts()){
					if (v.getName().trim().equals(name.getText().trim()) && v != script){
						setErrorMessage(Messages.CompositeScript_3);
						return;
					}
				}
				
				if (name.getText().trim().equals("")){ //$NON-NLS-1$
					setErrorMessage(Messages.CompositeScript_5);
					return;
				}
				setErrorMessage(null);
			}
		});
		
		l = new Label(this, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText("Datasource");
		
		Button btnDs = new Button(this, SWT.PUSH);
		btnDs.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		btnDs.setText("Select datasource");
		btnDs.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DialogConnection dial = new DialogConnection(getShell(), script != null ? script.getConnection() : null);
				if(dial.open() == Dialog.OK) {
					script.setConnection(dial.getConnection());
				}
			}
		});
	
		l = new Label(this, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.CompositeScript_6);
		
		l = new Label(this, SWT.NONE);
		l.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		l.setText(Messages.CompositeScript_7);
		
		prebuiltFunctions = new ListViewer(this, SWT.BORDER);
		prebuiltFunctions.getControl().setLayoutData(new GridData(GridData.BEGINNING, GridData.FILL, false, true));
		prebuiltFunctions.setContentProvider(new IStructuredContentProvider() {
			
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
			
			public void dispose() {}
			
			public Object[] getElements(Object inputElement) {
				return (Object[])inputElement;
			}
		});
		prebuiltFunctions.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				return ((PrebuiltFunction)element).name();
			}
			
			
		});
		prebuiltFunctions.setInput(new PrebuiltFunction[]{PrebuiltFunction.Group_Name, PrebuiltFunction.Group_Value, PrebuiltFunction.User_Id, PrebuiltFunction.User_Function, PrebuiltFunction.User_SkypeName});
		prebuiltFunctions.addDoubleClickListener(new IDoubleClickListener() {
			
			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection ss = (IStructuredSelection)prebuiltFunctions.getSelection();
				PrebuiltFunction func = (PrebuiltFunction)ss.getFirstElement();
				
				Point p = definition.getSelectedRange();
				try {
					doc.replace(p.x, p.y, func.getTemplate());
				} catch (BadLocationException e) {
					
					e.printStackTrace();
				}
				
			}
		});
		
		
		createSourceViewer();
			
		l = new Label(this, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(Messages.CompositeScript_8);
		
		variables = new ListViewer(this, SWT.BORDER);
		variables.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, true));
		variables.setContentProvider(new IStructuredContentProvider() {
			
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
			
			public void dispose() {}
			
			public Object[] getElements(Object inputElement) {
				List l = (List)inputElement;
				return l.toArray(new Object[l.size()]);
			}
		});
		variables.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				return ((Variable)element).getName();
			}
			
			
		});
		
		variables.addDoubleClickListener(new IDoubleClickListener() {
			
			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection ss = (IStructuredSelection)variables.getSelection();
				Variable v = (Variable)ss.getFirstElement();
				
				Point p = definition.getSelectedRange();
				try {
					doc.replace(p.x, p.y, v.getSymbol());
				} catch (BadLocationException e) {
					
					e.printStackTrace();
				}
				
			}
		});
		variables.setInput(Activator.getDefault().getModel().getVariables());
		
		errorMessage = new Label(this, SWT.NONE);
		errorMessage.setLayoutData(new GridData(GridData.FILL, GridData.END, false, false, 2, 1));
		errorMessage.setForeground(red);
		
	}
	
	
	private void createSourceViewer(){
		definition = new SourceViewer(this, null, SWT.BORDER | SWT.WRAP | SWT.MULTI);
		definition.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 3));
		definition.configure(new SourceViewerConfiguration());
		
		
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

		if (script == null){
			script = new Script();
			doc = new Document();
			definition.setInput(doc);
			return;
		}
		isFilling = true;
		name.setText(script.getName());
	
		doc = new Document(script.getDefinition());
		definition.setInput(doc);
		definition.addTextListener(new ITextListener() {
			
			public void textChanged(TextEvent event) {
				notifyListeners(SWT.Selection, new Event());
				
			}
		});
		
		isFilling = false;
	}
	
	
	/**
	 * if Variable is null, create it
	 * init variable with the specified fields
	 * @return
	 */
	public Script getScript(){
		if (script == null){
			script =  new Script();
		}
		
		script.setName(name.getText());
		script.setDefinition(doc.get());
		
		return script;
	}
}
