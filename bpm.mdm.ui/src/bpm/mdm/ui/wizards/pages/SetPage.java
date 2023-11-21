package bpm.mdm.ui.wizards.pages;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import bpm.mdm.model.Rule;
import bpm.mdm.model.rules.SetValueRule;
import bpm.mdm.ui.i18n.Messages;

public class SetPage extends WizardPage implements IRulePage{
	


	private ListViewer datas;
	private List<String> input = new ArrayList<String>();
	private Rule editedRule;
	public SetPage(String pageName) {
		super(pageName);
		
	}
	
	public SetPage(String pageName, Rule editedrule) {
		this(pageName);
		this.editedRule= editedrule;
	}
	@Override
	public void createControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, true));
		setControl(main);
		
		
		Button add = new Button(main, SWT.PUSH);
		add.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		add.setText(Messages.SetPage_0);
		add.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				InputDialog d = new InputDialog(getShell(), Messages.SetPage_1, Messages.SetPage_2, Messages.SetPage_3, null);
				if (d.open() == InputDialog.OK){
					input.add(d.getValue());
					datas.refresh();
				}
			}
		});
		
		Button del = new Button(main, SWT.PUSH);
		del.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		del.setText(Messages.SetPage_4);
		
		Label l  = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, true, false, 2, 1));
		l.setText(Messages.SetPage_5);
		
		
		datas = new ListViewer(main, SWT.BORDER | SWT.V_SCROLL);
		datas.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		datas.setContentProvider(new ArrayContentProvider());
		datas.setLabelProvider(new LabelProvider());
		
		datas.setInput(new WritableList(input, String.class));
		fill();
	}

	@Override
	public void setRule(Rule rule) {
		((SetValueRule)rule).getAuthorizedValues().clear();
		((SetValueRule)rule).getAuthorizedValues().addAll(input);
		
	}
	private void fill(){
		if (editedRule != null){
			input.addAll((List)((SetValueRule)editedRule).getAuthorizedValues());
			datas.refresh();
		}
		
	}
}
