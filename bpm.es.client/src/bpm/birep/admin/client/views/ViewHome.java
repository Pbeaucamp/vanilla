package bpm.birep.admin.client.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

import adminbirep.Activator;
import adminbirep.Messages;


public class ViewHome extends ViewPart {
	private Text newHtml;
	private String html = ""; //$NON-NLS-1$
	
	public static final String ID = "bpm.birep.admin.client.viewhome"; //$NON-NLS-1$

	public ViewHome() { }

	@Override
	public void createPartControl(Composite parent) {
		Composite c = new Composite(parent, SWT.NONE);
		c.setLayoutData(new GridData(GridData.FILL_BOTH));
		c.setLayout(new GridLayout());
		
		newHtml = new Text(c, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		newHtml.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		try {
			html = Activator.getDefault().getVanillaApi().getVanillaPreferencesManager().getCustomLogoUrl();
			if (!"".equalsIgnoreCase(html)) { //$NON-NLS-1$
				newHtml.setText(html);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		Composite cc = new Composite(c, SWT.NONE);
		cc.setLayout(new GridLayout(2, true));
		cc.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 1));
		
		Button ok = new Button(cc, SWT.PUSH);
		ok.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		ok.setText(Messages.ViewHome_3);
		ok.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				
			}
		});
		
		Button cancel = new Button(cc, SWT.PUSH);
		cancel.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		cancel.setText(Messages.ViewHome_4);
		cancel.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				newHtml.setText(html);
			}
		});
		

		
	}

	@Override
	public void setFocus() { }

}
