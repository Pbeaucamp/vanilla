package bpm.gateway.ui.mdm.actions;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.gateway.ui.i18n.Messages;
import bpm.mdm.model.Synchronizer;
import bpm.mdm.model.provider.FileSystemModelProvider;

public class SynchronizerSelectionPage extends WizardPage{
	
	protected static FileSystemModelProvider helper ;
	
	static{
		try {
			//XXX
			//helper = new FileSystemModelProvider("data/test1.xml");
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}
	private ComboViewer sync;
	private Text file;
	
	protected SynchronizerSelectionPage(String pageName) {
		super(pageName);
		
	}
	@Override
	public void createControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(3, false));
		
		
		Label l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText(Messages.SynchronizerSelectionPage_0);
		
		file = new Text(main, SWT.BORDER);
		file.setEnabled(false);
		file.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		
		Button b = new Button(main, SWT.PUSH);
		b.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false));
		b.setText("..."); //$NON-NLS-1$
		b.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog d = new FileDialog(getShell(), SWT.SAVE);
				d.setFilterExtensions(new String[]{"*.gateway", "*.*"}); //$NON-NLS-1$ //$NON-NLS-2$
				String s = d.open();
				
				if (s != null){
					file.setText(s);
				}
				else{
					file.setText(""); //$NON-NLS-1$
				}
				getContainer().updateButtons();
			}
		});
		
		l = new Label(main, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText(Messages.SynchronizerSelectionPage_5);
		
		sync = new ComboViewer(main, SWT.READ_ONLY);
		sync.getControl().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		sync.setContentProvider(new ArrayContentProvider());
		sync.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				
				return ((Synchronizer)element).getName();
			}
		});
		sync.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				getContainer().updateButtons();
				
			}
		});
		//XXX
		//sync.setInput(helper.getModel().getSynchronizers());
		
		setControl(main);
		
	}
	
	@Override
	public boolean canFlipToNextPage() {
		return false;
	}
	@Override
	public boolean isPageComplete() {
		return !sync.getSelection().isEmpty() && !file.getText().isEmpty();
	}
	
	public Synchronizer getSynchronizer(){
		return (Synchronizer)((IStructuredSelection)sync.getSelection()).getFirstElement();
	}
	
	public String getFileName(){
		return file.getText();
	}
}
