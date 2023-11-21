package bpm.fd.design.ui.component.jsp.pages;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.InputDialog;
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

import bpm.fd.design.ui.component.Messages;
import bpm.fd.design.ui.viewer.contentproviders.ListContentProvider;

public class UrlPage extends WizardPage{
	public static final String PAGE_NAME = "bpm.fd.design.ui.component.text.pages.LabelPage"; //$NON-NLS-1$
	public static final String PAGE_TITLE = Messages.UrlPage_1;
	public static final String PAGE_DESCRIPTION = Messages.UrlPage_2;

	
	
	private Text url;
	private ListViewer parameters;
	
	
	public UrlPage(){
		this(PAGE_NAME);
		this.setTitle(PAGE_TITLE);
		this.setDescription(PAGE_DESCRIPTION);
	}
	
	protected UrlPage(String pageName) {
		super(pageName);
	}
	public void createControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, false));
		
		
		Label l2 = new Label(main, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 2, 1));
		l2.setText(Messages.UrlPage_3);
		
		parameters = new ListViewer(main, SWT.BORDER |  SWT.FULL_SELECTION |  SWT.V_SCROLL);
		parameters.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		parameters.setLabelProvider(new LabelProvider());
		parameters.setContentProvider(new ListContentProvider<String>());
		parameters.setInput(new ArrayList<String>());
		
		

		
		Label l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.FILL, false, true));
		l.setText(Messages.UrlPage_4);
		
		url = new Text(main, SWT.BORDER | SWT.MULTI | SWT.WRAP);
		url.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		url.addModifyListener(new ModifyListener(){

			public void modifyText(ModifyEvent e) {
				getContainer().updateButtons();
				
			}
			
		});
		
	
		
		createContextMenu();
		setControl(main);
	}

	private void createContextMenu(){
		MenuManager menu = new MenuManager();
		
		final Action add = new Action(){
			public void run(){
				InputDialog d = new InputDialog(getShell(), Messages.UrlPage_5, Messages.UrlPage_6, Messages.UrlPage_7, null);
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
					
				}
			}
		};
		add.setText(Messages.UrlPage_12);
		
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
				
			}
		};
		
		remove.setText(Messages.UrlPage_15);
		
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
		return !url.getText().equals(""); //$NON-NLS-1$
	}

	
	public List<String > getParameterNames(){
		return (List<String>)parameters.getInput();
	}
	
	public String getUrl(){
		return url.getText();
	}
}
