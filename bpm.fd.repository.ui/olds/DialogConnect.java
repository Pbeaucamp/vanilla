package bpm.fd.repository.ui.dialogs;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import bpm.fd.repository.ui.Activator;


/**
 * @deprecated
 * @author ludo
 *
 */
public abstract class DialogConnect extends Dialog{
	private Text host, login, password;
	private static Font font = new Font(Display.getCurrent(), "Times New Roman", 12, SWT.ITALIC); //$NON-NLS-1$
	private ComboViewer combo;
	
	private HashMap<String, RepositoryConnection> map = new HashMap<String, RepositoryConnection>();
	
	protected void initializeBounds() {
		
		this.getShell().setText("Default repository Connection"); //$NON-NLS-1$
		super.initializeBounds();
		this.getShell().setSize(400, 200);
		
	}
	
	public DialogConnect(Shell parentShell) {
		super(parentShell);
		
	}
//	protected Control createDialogArea(Composite parent) {
//		Composite container = new Composite(parent, SWT.NONE);
//		container.setLayout(new GridLayout(2, false));
//		container.setLayoutData(new GridData(GridData.FILL_BOTH));
//		
//		Label l = new Label(container, SWT.NONE);
//		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
//		l.setText("Select a Connection"); //$NON-NLS-1$
//		l.setFont(font);
//		
//		combo = new ComboViewer(container, SWT.READ_ONLY);
//		combo.getCombo().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true , false));
//		combo.setLabelProvider(new LabelProvider(){
//
//			@Override
//			public String getText(Object element) {
//				return ((RepositoryConnection)element).getName();
//			}
//			
//		});
//		
//		combo.setContentProvider(new IStructuredContentProvider(){
//
//			public void dispose() {
//				
//				
//			}
//
//			public void inputChanged(Viewer viewer, Object oldInput,
//					Object newInput) {
//				
//				
//			}
//
//			public Object[] getElements(Object inputElement) {
//				Collection<RepositoryConnection> l = (Collection<RepositoryConnection>)inputElement;
//				return l.toArray(new RepositoryConnection[l.size()]);
//			}
//			
//		});
//		combo.addSelectionChangedListener(new ISelectionChangedListener(){
//
//			public void selectionChanged(SelectionChangedEvent event) {
//				if (combo.getSelection().isEmpty()){
//					return;
//				}
//				
//				RepositoryConnection r = (RepositoryConnection)((IStructuredSelection)combo.getSelection()).getFirstElement();
//				
//				
//				
//				login.setText(r.getUsername());
//				password.setText(r.getPassword());
//				host.setText(r.getHost());
//			}
//			
//		});
//
//		
//		Label l3 = new Label(container, SWT.NONE);
//		l3.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
//		l3.setText("Repository Url"); 
//		l3.setFont(font);
//		
//		host = new Text(container, SWT.BORDER);
//		host.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
//
//		
//		Label l6 = new Label(container, SWT.NONE);
//		l6.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
//		l6.setText("Login");
//		l6.setFont(font);
//		
//		login= new Text(container, SWT.BORDER);
//		login.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
//				
//		Label l7 = new Label(container, SWT.NONE);
//		l7.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
//		l7.setText("Password"); 
//		l7.setFont(font);
//		
//		password = new Text(container, SWT.BORDER | SWT.PASSWORD);
//		password.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
//			
//		setDatas();
//		combo.setInput(map.values());
//		if (map.get("Default") != null){ //$NON-NLS-1$
//			combo.setSelection(new StructuredSelection(map.get("Default"))); //$NON-NLS-1$
//		}
//		
//		return container;
//
//	}
//
//	
//	private void setDatas(){
//		try {
//			List<RepositoryConnection> list = new ConnectionDigester("resources/connections.xml").getConnections(); //$NON-NLS-1$
//			for(RepositoryConnection r: list){
//				map.put(r.getName(), r);
//			}
//			
//			if (Activator.getDefault().getRepositorySocket() != null){
//				((RepositoryConnection)Activator.getDefault().getRepositorySocket()).setName("Default"); //$NON-NLS-1$
//				map.put("Default", (RepositoryConnection)Activator.getDefault().getRepositorySocket()); //$NON-NLS-1$
//			}
//			
//			
//		} catch (Exception e) {
//			MessageDialog.openError(getShell(), "Error when parsing connection file", e.getMessage()); 
//			e.printStackTrace();
//		}
//		
//		
//		
//	}
//	
//	@Override
//	protected void okPressed() {
//		try{
//			IRepositoryConnection r = FactoryRepository.getInstance().getConnection(FactoryRepository.STANDARD_CLIENT, host.getText(), login.getText(), password.getText());
//			r.test();
//			Activator.getDefault().setRepositorySocket((RepositoryConnection)r);
//			MessageDialog.openInformation(getShell(), "Connection", "Connected to Repository"); 
//
//			
//		}catch(Exception ex){
//			MessageDialog.openError(getShell(), "Error When connecting to Repository", ex.getMessage()); 
//			
//			ex.printStackTrace();
//		}
//		super.okPressed();
//	}
//
//	@Override
//	protected void createButtonsForButtonBar(Composite parent) {
//		createButton(parent,IDialogConstants.OK_ID , "Connect", true); 
//		createButton(parent,IDialogConstants.CANCEL_ID, "Cancel", false); 
//	}

}
