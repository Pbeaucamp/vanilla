package bpm.profiling.ui.composite;

import java.util.Calendar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.profiling.database.bean.AnalysisInfoBean;

public class CompositeQuery extends Composite {

	private Text name, description;
	
	public CompositeQuery(Composite parent, int style) {
		super(parent, style);
		this.setLayout(new GridLayout());
		buildContent();
	}
	
	private void buildContent(){
		Label l = new Label(this, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText("Name");
		
		name = new Text(this, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		
		Label l2 = new Label(this, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l2.setText("Description");
		
		description = new Text(this, SWT.BORDER);
		description.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
	}

	
	public AnalysisInfoBean getQueryInfo(){
		AnalysisInfoBean info = new AnalysisInfoBean();
		info.setCreation(Calendar.getInstance().getTime());
		info.setName(name.getText());
		info.setDescription(description.getText());
		
		return info;
	}
}
