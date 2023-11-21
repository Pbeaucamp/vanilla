package bpm.fd.design.ui.component.gauge.pages;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;

import bpm.fd.api.core.model.components.definition.IComponentDatas;
import bpm.fd.design.ui.component.Messages;

public class GaugeDatasPage extends WizardPage{
	public static final String PAGE_NAME = "bpm.fd.design.ui.component.gauge.pages.GaugeDatasPage"; //$NON-NLS-1$
	public static final String PAGE_TITLE = Messages.GaugeDatasPage_1;
	public static final String PAGE_DESCRIPTION = Messages.GaugeDatasPage_2;

	
	private AbstractCompositeGaugeDatas compositeDatas;
	private Button typeStatic;
	private Button typeFm;
		
	private Listener lst = new Listener() {

		public void handleEvent(Event event) {
			getContainer().updateButtons();
			
		}
	};
	
	public GaugeDatasPage(){
		this(PAGE_NAME);
		this.setTitle(PAGE_TITLE);
		this.setDescription(PAGE_DESCRIPTION);
	}
	
	protected GaugeDatasPage(String pageName) {
		super(pageName);
	}
	
	
	
	
	public void createControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout(2, false));
		
		Group g = new Group(main, SWT.NONE);
		g.setLayout(new GridLayout());
		g.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		
		typeStatic = new Button(g, SWT.RADIO);
		typeStatic.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		typeStatic.setText(Messages.GaugeDatasPage_3);
		typeStatic.setSelection(true);
		typeStatic.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent e) {
				
				
				if (!compositeDatas.isDisposed()){
					compositeDatas.removeListener(SWT.Modify, lst);
					compositeDatas.dispose();
				}
				
				if (typeStatic.getSelection()){
					compositeDatas = new CompositeStaticGaugeDatas((Composite)getControl(), SWT.NONE);
					compositeDatas.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
					
				}
				else{
					compositeDatas = new CompositeFmGaugeDatas((Composite)getControl(), SWT.NONE);
					compositeDatas.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));

				}
				compositeDatas.addListener(SWT.Modify, lst);
				((Composite)getControl()).layout();
				getContainer().updateButtons();
			}
			
			
		});
		
		typeFm = new Button(g, SWT.RADIO);
		typeFm.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		typeFm.setText(Messages.GaugeDatasPage_4);
		typeFm.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent e) {
				
				if (!compositeDatas.isDisposed()){
					compositeDatas.removeListener(SWT.Modify, lst);
					compositeDatas.dispose();
					
				}
				
				if (typeStatic.getSelection()){
					compositeDatas = new CompositeStaticGaugeDatas((Composite)getControl(), SWT.NONE);
					compositeDatas.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
					
				}
				else{
					compositeDatas = new CompositeFmGaugeDatas((Composite)getControl(), SWT.NONE);
					compositeDatas.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));

				}
				compositeDatas.addListener(SWT.Modify, lst);
				((Composite)getControl()).layout();
				getContainer().updateButtons();
			}
			
			
		});
		
		compositeDatas = new CompositeStaticGaugeDatas(main, SWT.NONE);
		compositeDatas.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		compositeDatas.addListener(SWT.Modify, lst);
		
		setControl(main);
		
	}
	
	public IComponentDatas getDatas() throws Exception{
		return compositeDatas.getDatas();
	}
	
	

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.WizardPage#isPageComplete()
	 */
	@Override
	public boolean isPageComplete() {
		return compositeDatas.isComplete();
	}
	
	
	
	
}
