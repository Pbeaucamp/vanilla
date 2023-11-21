package bpm.norparena.ui.menu.client.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import bpm.norparena.ui.menu.Activator;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class DialogXmlViewer extends Dialog {

	private RepositoryItem item;
	private Text xml;
	private boolean isModfied = false;
	private String content;
	
	public DialogXmlViewer(Shell parentShell, RepositoryItem item) {
		super(parentShell);
		this.item = item;
		
	}

	
	public DialogXmlViewer(Shell parentShell, String content) {
		super(parentShell);
		this.content = content;
		
	}

	
	
	private void createToolbar(Composite parent){
		ToolBar toolbar = new ToolBar(parent, SWT.NONE);
		toolbar.setLayoutData(new GridData());
		
		
		ToolItem it = new ToolItem(toolbar, SWT.NONE);
		
	}
	
	
	private void fillData(){
		try {
			String xml = Activator.getDefault().getSocket().getRepositoryService().loadModel( item);
			this.xml.setText(xml);
		} catch (Exception e) {
			this.xml.setText(e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite c = new Composite(parent, SWT.NONE);
		c.setLayoutData(new GridData(GridData.FILL_BOTH));
		c.setLayout(new GridLayout());
		//createToolbar(c);
		
		
		xml = new Text(c, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		xml.setLayoutData(new GridData(GridData.FILL_BOTH));
		xml.addModifyListener(new ModifyListener(){

			public void modifyText(ModifyEvent e) {
				isModfied = true;
				content = xml.getText();
			}
			
		});

		if (item != null){
			fillData();
		}
		else if (content != null){
			xml.setText(content);
		}
		
		
		return c;
	}

	public boolean isModified(){
		return isModfied;
	}
	
	public String getXml(){
		return content;
	}
}
