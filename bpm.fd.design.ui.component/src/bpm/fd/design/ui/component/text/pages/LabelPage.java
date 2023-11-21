package bpm.fd.design.ui.component.text.pages;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.SimpleContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.fd.api.core.model.components.definition.text.LabelOptions;
import bpm.fd.design.ui.component.Messages;
import bpm.fd.design.ui.viewer.contentproviders.ListContentProvider;

public class LabelPage extends WizardPage{
	public static final String PAGE_NAME = "bpm.fd.design.ui.component.text.pages.LabelPage"; //$NON-NLS-1$
	public static final String PAGE_TITLE = Messages.LabelPage_1;
	public static final String PAGE_DESCRIPTION = Messages.LabelPage_2;

	
	
	private Text labelContent;
	private ListViewer parameters;
	
	private ContentProposalAdapter proposalAdapter ;
	private ContentProposalAdapter proposalAdapter2 ;
	
	public LabelPage(){
		this(PAGE_NAME);
		this.setTitle(PAGE_TITLE);
		this.setDescription(PAGE_DESCRIPTION);
	}
	
	protected LabelPage(String pageName) {
		super(pageName);
	}
	public void createControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, false));
		
		
		Label l2 = new Label(main, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 2, 1));
		l2.setText(Messages.LabelPage_3);
		
		parameters = new ListViewer(main, SWT.BORDER |  SWT.FULL_SELECTION |  SWT.V_SCROLL);
		parameters.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		parameters.setLabelProvider(new LabelProvider());
		parameters.setContentProvider(new ListContentProvider<String>());
		parameters.setInput(new ArrayList<String>());
		
		

		
		Label l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.FILL, false, true));
		l.setText(Messages.LabelPage_4);
		
		labelContent = new Text(main, SWT.BORDER | SWT.MULTI | SWT.WRAP);
		labelContent.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		labelContent.addModifyListener(new ModifyListener(){

			public void modifyText(ModifyEvent e) {
				getContainer().updateButtons();
				
			}
			
		});
		
		proposalAdapter = new ContentProposalAdapter(
				labelContent, 
				new TextContentAdapter(), 
				new SimpleContentProposalProvider(new String[]{}),
				null, 
				null);;
		
		createContextMenu();
		setControl(main);
	}

	private void createContextMenu(){
		MenuManager menu = new MenuManager();
		
		final Action add = new Action(){
			public void run(){
				InputDialog d = new InputDialog(getShell(), Messages.LabelPage_5, Messages.LabelPage_6, "parameter", null); //$NON-NLS-3$
				if (d.open() == InputDialog.OK){
					
					List<String> l = new ArrayList<String>();
					for(String s : (List<String>)parameters.getInput()){
						if (s.equals(d.getValue())){
							return;
						}
						l.add("{$" + s + "}"); //$NON-NLS-1$ //$NON-NLS-2$
					}
					
					
					l.add("{$" + d.getValue() + "}"); //$NON-NLS-1$ //$NON-NLS-2$
					((List<String>)parameters.getInput()).add(d.getValue());
					parameters.refresh();
					
					
					
				
					proposalAdapter.setContentProposalProvider(new SimpleContentProposalProvider(l.toArray(new String[l.size()])));
					proposalAdapter2.setContentProposalProvider(new SimpleContentProposalProvider(l.toArray(new String[l.size()])));
				}
			}
		};
		add.setText(Messages.LabelPage_12);
		
		final Action remove = new Action(){
			public void run(){
				for(Object o : ((IStructuredSelection)parameters.getSelection()).toList()){
					((List<String>)parameters.getInput()).remove(o);
				}
				List<String> l = new ArrayList<String>();
				
				for(String s : (List<String>)parameters.getInput()){
					l.add("{$" + s + "}"); //$NON-NLS-1$ //$NON-NLS-2$
				}
				parameters.refresh();
				proposalAdapter.setContentProposalProvider(new SimpleContentProposalProvider(l.toArray(new String[l.size()])));
				proposalAdapter2.setContentProposalProvider(new SimpleContentProposalProvider(l.toArray(new String[l.size()])));			}
		};
		remove.setText(Messages.LabelPage_15);
		
		menu.add(add);
		menu.add(remove);
		
		menu.addMenuListener(new IMenuListener(){

			public void menuAboutToShow(IMenuManager manager) {
				if (parameters.getSelection().isEmpty()){
					remove.setEnabled(false);
				}
				else {
					remove.setEnabled(true);
				}
				
			}
			
		});

		parameters.getControl().setMenu(menu.createContextMenu(parameters.getControl()));
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.WizardPage#isPageComplete()
	 */
	@Override
	public boolean isPageComplete() {
		return !labelContent.getText().equals(""); //$NON-NLS-1$
	}

	public LabelOptions getOptions() {
		LabelOptions opt = new LabelOptions();
		opt.setText(labelContent.getText());
		return opt;
	}
	public List<String > getParameterNames(){
		return (List<String>)parameters.getInput();
	}
	
}
