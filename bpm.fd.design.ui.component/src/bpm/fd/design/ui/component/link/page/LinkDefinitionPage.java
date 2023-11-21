package bpm.fd.design.ui.component.link.page;


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

import bpm.fd.api.core.model.components.definition.IComponentOptions;
import bpm.fd.api.core.model.components.definition.link.LinkOptions;
import bpm.fd.design.ui.component.Messages;
import bpm.fd.design.ui.viewer.contentproviders.ListContentProvider;

public class LinkDefinitionPage extends WizardPage {

	public static final String PAGE_NAME = "bpm.fd.design.ui.component.link.page.LinkDefinitionPage"; //$NON-NLS-1$
	public static final String PAGE_TITLE = Messages.LinkDefinitionPage_1;
	public static final String PAGE_DESCRIPTION = Messages.LinkDefinitionPage_2;

	
	private Text baseUrl;
	private Text baseLabel;
	private ListViewer parameters;
	
	private ContentProposalAdapter proposalAdapter ;
	private ContentProposalAdapter proposalAdapter2 ;
	
	public LinkDefinitionPage(){
		this(PAGE_NAME);
		this.setTitle(PAGE_TITLE);
		this.setDescription(PAGE_DESCRIPTION);
	}
	
	protected LinkDefinitionPage(String pageName) {
		super(pageName);
	}
	
	public void createControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, false));
		
		
		
		Label l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 2, 1));
		l.setText(Messages.LinkDefinitionPage_3);
		
		parameters = new ListViewer(main, SWT.BORDER |  SWT.FULL_SELECTION |  SWT.V_SCROLL);
		parameters.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		parameters.setLabelProvider(new LabelProvider());
		parameters.setContentProvider(new ListContentProvider<String>());
		parameters.setInput(new ArrayList<String>());
		
		
		
		l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.FILL, false, true));
		l.setText(Messages.LinkDefinitionPage_4);
		
		baseUrl = new Text(main, SWT.BORDER | SWT.MULTI | SWT.WRAP);
		baseUrl.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		baseUrl.addModifyListener(new ModifyListener(){

			public void modifyText(ModifyEvent e) {
				getContainer().updateButtons();
				
			}
			
		});
		
		
		proposalAdapter = new ContentProposalAdapter(
				baseUrl, 
				new TextContentAdapter(), 
				new SimpleContentProposalProvider(new String[]{}),
				null, 
				null);;
				
		
		
		
		l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.FILL, false, true));
		l.setText(Messages.LinkDefinitionPage_5);
		
		baseLabel = new Text(main, SWT.BORDER | SWT.WRAP | SWT.MULTI);
		baseLabel.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		baseLabel.addModifyListener(new ModifyListener(){

			public void modifyText(ModifyEvent e) {
				getContainer().updateButtons();
				
			}
			
		});

		
		proposalAdapter2 = new ContentProposalAdapter(
				baseLabel, 
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
				InputDialog d = new InputDialog(getShell(), Messages.LinkDefinitionPage_6, Messages.LinkDefinitionPage_7, "parameter", null); //$NON-NLS-3$
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
		add.setText(Messages.LinkDefinitionPage_13);
		
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
		remove.setText(Messages.LinkDefinitionPage_16);
		
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
	
	@Override
	public boolean isPageComplete() {
		return ! "".equals(baseUrl.getText().trim()) &&  ! "".equals(baseLabel.getText().trim()); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	public List<String > getParameterNames(){
		return (List<String>)parameters.getInput();
	}
	public IComponentOptions getOptions(){
		LinkOptions o = new LinkOptions();
		o.setUrl(baseUrl.getText());
		o.setLabel(baseLabel.getText());
		return o;
	}

}
