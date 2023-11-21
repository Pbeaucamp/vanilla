package bpm.birep.admin.client.composites;

import java.text.SimpleDateFormat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import adminbirep.Messages;
import bpm.repository.api.model.IDocument;

public class CompositeDocument extends Composite {

	private IDocument doc;
	
	private Text name, format, creation;
	
	public CompositeDocument(Composite parent, int style, IDocument doc) {
		super(parent, style);
		this.doc = doc;
		
		buildContent();
		fillData();
	}
	
	private void buildContent(){
		this.setLayout(new GridLayout(2, false));
		
		Label l = new Label(this, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText(Messages.CompositeDocument_0);
		
		name = new Text(this, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false,1, 1));
		
		Label l1 = new Label(this, SWT.NONE);
		l1.setLayoutData(new GridData());
		l1.setText(Messages.CompositeDocument_1);
		
		format = new Text(this, SWT.BORDER);
		format.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false,1, 1));
		format.setEnabled(false);
		
		Label l2 = new Label(this, SWT.NONE);
		l2.setLayoutData(new GridData());
		l2.setText(Messages.CompositeDocument_2);
		
		creation = new Text(this, SWT.BORDER);
		creation.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false,1, 1));
		creation.setEnabled(false);

		
		
	}

	private void fillData(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); //$NON-NLS-1$
		
		name.setText(doc.getName());
		format.setText(doc.getFormat());
		if (doc.getCreation() != null){
			creation.setText(sdf.format(doc.getCreation()));
		}
		
		
	}
	
	
}
