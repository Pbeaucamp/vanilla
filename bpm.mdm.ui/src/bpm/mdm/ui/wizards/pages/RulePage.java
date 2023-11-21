package bpm.mdm.ui.wizards.pages;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.mdm.model.DataType;
import bpm.mdm.model.Rule;
import bpm.mdm.model.rules.RulesPackage;
import bpm.mdm.ui.i18n.Messages;

public class RulePage extends WizardPage implements IRulePage{

	private Text name, description;
	private ComboViewer type;
	
	private Rule editedRule;
	private DataType attributeType;
	
	public RulePage(String pageName, DataType attributeType) {
		super(pageName);
		this.attributeType = attributeType;
	}

	public RulePage(String pageName, Rule editedrule, DataType attributeType) {
		this(pageName, attributeType);
		this.editedRule= editedrule;
		this.attributeType = attributeType;
	}

	@Override
	public void createControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, false));
		
		Label l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText(Messages.RulePage_0);
		
		type = new ComboViewer(main, SWT.READ_ONLY);
		type.getControl().setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		type.setContentProvider(new ArrayContentProvider());
		type.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				
				return ((EClass)element).getName();
			}
		});
//		type.setInput(AttributeRulesProvider.getAuthorizedRuleClasses(attribute));
		type.setInput(RulesPackage.eINSTANCE.getEClassifiers());
		type.addFilter(new ViewerFilter() {
			
			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				
				return (element instanceof EClass) && !((EClass)element).isAbstract();
			}
		});
		
		
		
		l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText(Messages.RulePage_1);
		
		name = new Text(main, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		
		
		
		l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText(Messages.RulePage_2);
		
		description = new Text(main, SWT.BORDER);
		description.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));

		setControl(main);
		fill();
	}
	
	private void fill(){
		if (editedRule != null){
			type.getControl().setEnabled(false);
			type.setSelection(new StructuredSelection(editedRule.eClass()));
			name.setText(editedRule.getName());
			
			description.setText(editedRule.getDescription());
		}
		
		type.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				getContainer().updateButtons();
				
			}
		});
		name.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				getContainer().updateButtons();
				
			}
		});
	}
	
	@Override
	public boolean isPageComplete() {
		return !type.getSelection().isEmpty();
	}
	
	public  EClass getRuleClass(){
		return (EClass)((IStructuredSelection)type.getSelection()).getFirstElement();
	}
	@Override
	public boolean canFlipToNextPage() {
		return isPageComplete();
	}

	@Override
	public void setRule(Rule rule) {
		rule.setName(name.getText());
		rule.setDescription(description.getText());
		
	}

}
