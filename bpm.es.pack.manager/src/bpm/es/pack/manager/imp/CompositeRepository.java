package bpm.es.pack.manager.imp;

import java.util.Properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import adminbirep.Messages;

public class CompositeRepository extends CompositeInformation {

	private Text url;
	
	public CompositeRepository(Composite parent, int style) {
		super(parent, style);
		this.setLayout(new GridLayout(2,false));
		
		buildContent();		
	}
	
	protected void buildContent(){
		Label l = new Label(this, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText(bpm.es.pack.manager.I18N.Messages.CompositeRepository_0);
		
		url = new Text(this, SWT.BORDER);
		url.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

		super.buildContent();
	}

	@Override
	public Properties getProperties() {
		Properties p = new Properties();
		p.put("repositoryUrl", url.getText()); //$NON-NLS-1$
		p.put("type", "repository"); //$NON-NLS-1$ //$NON-NLS-2$
		return p;
	}

	@Override
	public void setProperties(Properties prop) {
		url.setText(prop.getProperty("repositoryUrl")); //$NON-NLS-1$

	}

}
