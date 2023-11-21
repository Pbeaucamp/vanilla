package bpm.profiling.ui.composite;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import bpm.profiling.database.bean.AnalysisInfoBean;
import bpm.profiling.runtime.core.Connection;
import bpm.profiling.ui.Activator;

public class CompositeAnalysisInfo extends Composite {

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
	
	private Text name;
	private Text desc;
	private Text creator;
	private Text modifier;
	private Text creationDate;
	private Text lastModification;
	
	private ComboViewer connections;
	
	private AnalysisInfoBean infosModel;
	private boolean editing;
	
	
	public CompositeAnalysisInfo(Composite parent, int style, boolean editing) {
		super(parent, style);
		
		this.editing = editing;
		setLayout(new GridLayout(2, false));
		buildContent();
	}
	
	private void buildContent(){
		Label l = new Label(this, SWT.NONE);
		l.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l.setText("Analysis Name");
		
		name = new Text(this, SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		
		Label l8 = new Label(this, SWT.NONE);
		l8.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l8.setText("Connection");
		
		
		connections = new ComboViewer(this, SWT.READ_ONLY);
		connections.getCombo().setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		connections.setContentProvider(new IStructuredContentProvider(){

			public Object[] getElements(Object inputElement) {
				List<Connection> l = (List<Connection>)inputElement;
				return l.toArray(new Connection[l.size()]);
			}

			public void dispose() {
				
				
			}

			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
				
				
			}
			
		});
		connections.setLabelProvider(new LabelProvider(){

			@Override
			public String getText(Object element) {
				
				return ((Connection)element).getName();
			}
			
		});
		
		
		Label l2 = new Label(this, SWT.NONE);
		l2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l2.setText("Creation Date");
		
		creationDate = new Text(this, SWT.BORDER);
		creationDate.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		creationDate.setEnabled(false);
		
		
		Label l4 = new Label(this, SWT.NONE);
		l4.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l4.setText("Creator");
		
		creator = new Text(this, SWT.BORDER);
		creator.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		
		Label l5 = new Label(this, SWT.NONE);
		l5.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l5.setText("Last Modification Date");
		
		lastModification = new Text(this, SWT.BORDER);
		lastModification.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		lastModification.setEnabled(false);
		
		
		Label l6 = new Label(this, SWT.NONE);
		l6.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		l6.setText("Last Modifier");
		
		modifier = new Text(this, SWT.BORDER);
		modifier.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		
		Label l7 = new Label(this, SWT.NONE);
		l7.setLayoutData(new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));
		l7.setText("Description");
		
		desc = new Text(this, SWT.BORDER | SWT.WRAP | SWT.MULTI);
		desc.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
		
		
		

		connections.setInput(Activator.getDefault().getConnections());

		
		if (editing == true){
			name.setEnabled(false);
			connections.getCombo().setEnabled(false);
			creator.setEnabled(false);
		}
	}
	
	
	public void fillContent(AnalysisInfoBean infosModel){
		this.infosModel = infosModel;
		if (infosModel == null){
			creationDate.setText(sdf.format(Calendar.getInstance().getTime()));
			lastModification.setText(sdf.format(Calendar.getInstance().getTime()));
		}
		
		else{
			name.setText(infosModel.getName());
			creator.setText(infosModel.getCreator());
			desc.setText(infosModel.getDescription());
			try {
				creationDate.setText(sdf.format(infosModel.getCreation()));
				lastModification.setText(sdf.format(infosModel.getModification()));
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			Connection c = Activator.helper.getConnectionManager().getConnection(infosModel.getConnectionId());
			
			if (c == null){
				return;
			}
			
			for(Connection con : (List<Connection>)connections.getInput()){
				if (con.getId() == c.getId()){
					connections.setSelection(new StructuredSelection(con));
					break;
				}
			}

		}
		
	}

	
	public void performChange(){
		if (infosModel == null){
			infosModel = new AnalysisInfoBean();
		}
		
		infosModel.setName(name.getText());
		infosModel.setDescription(desc.getText());
		infosModel.setCreator(creator.getText());
		
		if (!connections.getSelection().isEmpty()){
			IStructuredSelection ss = (IStructuredSelection)connections.getSelection();
			infosModel.setConnectionId(((Connection)ss.getFirstElement()).getId());
		}
		
		
		

	}

	public AnalysisInfoBean getAnalysisInformation(){
		return infosModel;
	}
	
	

}
