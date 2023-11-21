package bpm.fd.repository.ui.composites;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.fd.repository.ui.Messages;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;

public class CompositeDirectory extends Composite {

	private RepositoryDirectory directory;
	private Text name, comment, creation;
	
	public CompositeDirectory(Composite parent, int style, RepositoryDirectory di) {
		super(parent, style);
		directory = di;
		
		this.setLayout(new GridLayout(2, false));
		
		Label l1 = new Label(this, SWT.NONE);
		l1.setLayoutData(new GridData());
		l1.setText(Messages.CompositeDirectory_0);
		
		name = new Text(this, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		//name.setEnabled(false);
		

		
//		Label l3 = new Label(this, SWT.NONE);
//		l3.setLayoutData(new GridData());
//		l3.setText("Creator");
//		
//		creator = new Text(this, SWT.BORDER);
//		creator.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
//		creator.setEnabled(false);
		
		Label l4 = new Label(this, SWT.NONE);
		l4.setLayoutData(new GridData());
		l4.setText(Messages.CompositeDirectory_2);
		
		creation = new Text(this, SWT.BORDER);
		creation.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		creation.setEnabled(false);
		
		Label l5 = new Label(this, SWT.NONE);
		l5.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 4));
		l5.setText(Messages.CompositeDirectory_3);
		
		comment = new Text(this, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		comment.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 4));
		
		
//		Composite buttons = new Composite(this, SWT.NONE);
//		buttons.setLayout(new GridLayout(2, true));
//		buttons.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 1));
//		
//		Button ok = new Button(buttons, SWT.PUSH);
//		ok.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
//		ok.setText("Apply");
//		
//		Button cancel = new Button(buttons, SWT.PUSH);
//		cancel.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
//		cancel.setText("Cancel");
		
	}
	
	public void fillData(){
		name.setText(directory.getName());
		comment.setText(directory.getComment());
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"); //$NON-NLS-1$
		creation.setText(sdf.format(directory.getDateCreation()));
		//creator.setText(directory.getCreator());
	}
	
	public void setDirectory(){
		directory.setName(name.getText());
		directory.setComment(comment.getText());
		directory.setDateCreation(directory.getDateCreation()!=null?directory.getDateCreation():Calendar.getInstance().getTime());
		//directory.setCreator(creator.getText());
		
	}

}
