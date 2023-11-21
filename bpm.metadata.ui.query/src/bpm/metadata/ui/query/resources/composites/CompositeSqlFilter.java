package bpm.metadata.ui.query.resources.composites;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.metadata.resource.IResource;
import bpm.metadata.resource.SqlQueryFilter;
import bpm.metadata.ui.query.i18n.Messages;

public class CompositeSqlFilter extends Composite implements ResourceBuilder {
private static final Color RED = new Color(Display.getDefault(), 255, 0, 0);

	
	private SqlQueryFilter filter;
	private Text name;

	private Label errorLabel;
	private Text query;
	
	public CompositeSqlFilter(Composite parent, int style, SqlQueryFilter filter) {
		super(parent, style);
		this.setLayout(new GridLayout());
		buildContent();
		this.filter = filter;
	
		fillDatas();
	}
	


	private void buildContent(){
		createGeneral(this);

		errorLabel = new Label(this, SWT.NONE);
    	errorLabel.setLayoutData(new GridData(GridData.FILL, GridData.END, true, false, 2, 1));
    	errorLabel.setForeground(RED);
    	errorLabel.setVisible(false);
		
	}
	
	
	private Control createGeneral(Composite root){
		Composite parent = new Composite(root, SWT.NONE);
		parent.setLayout(new GridLayout(2, false));
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		
		Label l = new Label(parent, SWT.NONE);
		l.setLayoutData(new GridData());
		l.setText(Messages.CompositeSqlFilter_0);
		
		name = new Text(parent, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
			
			
		
		l = new Label(parent, SWT.NONE);
		l.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));
		l.setText(Messages.CompositeSqlFilter_1);	
		
		query = new Text(parent,SWT.BORDER | SWT.WRAP | SWT.MULTI);
		query.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		query.addModifyListener(new ModifyListener() {
			
			public void modifyText(ModifyEvent e) {
				
				notifyListeners(SWT.Selection, new Event());
				
			}
		});

		return parent;
	}

	private void fillDatas(){
		name.setText(filter.getName());
		if (filter.getQuery() != null && !filter.getQuery().equals(" null")){ //$NON-NLS-1$
			query.setText(filter.getQuery());
		}
		
	}
	
	public IResource getResource(){
		filter.setName(name.getText());
		filter.setQuery(query.getText());
		
		return filter;
	}
	

	/**
	 * 
	 * @return true if the composite is rightly filled
	 */
	public boolean isFilled(){
		return !errorLabel.isVisible() && !name.getText().trim().equals("") &&!query.getText().trim().equals("") ; //$NON-NLS-1$ //$NON-NLS-2$
	}

}
