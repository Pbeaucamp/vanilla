package bpm.workflow.ui.views.property.sections;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.SimpleContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import bpm.workflow.runtime.model.IServer;
import bpm.workflow.runtime.model.WorkflowModel;
import bpm.workflow.runtime.model.activities.CheckColumnActivity;
import bpm.workflow.runtime.resources.servers.DataBaseServer;
import bpm.workflow.runtime.resources.servers.ListServer;
import bpm.workflow.runtime.resources.servers.Server;
import bpm.workflow.runtime.resources.variables.ListVariable;
import bpm.workflow.runtime.resources.variables.Variable;
import bpm.workflow.ui.Activator;
import bpm.workflow.ui.Messages;
import bpm.workflow.ui.gef.model.Node;
import bpm.workflow.ui.gef.part.NodePart;

/**
 * Section for the checkcolumn activity
 * @author Charles MARTIN
 *
 */
public class CheckColumnSection extends AbstractPropertySection {
	public static Color mainBrown = new Color(Display.getDefault(), 209,177, 129);
	public static Color secondBrown = new Color(Display.getDefault(), 238,226, 208);

	
	private Text select,selectColumn,selectType;

	private Node node;
	private Button validPath;
	private Combo server;


	
	
	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage tabbedPropertySheetPage) {
		
		super.createControls(parent, tabbedPropertySheetPage);

		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		parent.setLayout(new GridLayout());
		
		org.eclipse.swt.widgets.Group general = new org.eclipse.swt.widgets.Group(parent, SWT.NONE);
		general.setLayout(new GridLayout(3, false));
		general.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		general.setText(Messages.CheckColumnSection_0);
		
		
		CLabel labelLabel = getWidgetFactory().createCLabel(general, Messages.CheckColumnSection_1);
		labelLabel.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		server = new Combo(general, SWT.READ_ONLY);
		server.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false,2,1));
		server.addSelectionListener(adaptder);

		
		CLabel labelcom = getWidgetFactory().createCLabel(general, Messages.CheckColumnSection_3);
		labelcom.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		
		select = getWidgetFactory().createText(general, ""); //$NON-NLS-1$
		select.setLayoutData(new GridData(GridData.FILL,GridData.CENTER,true, false));
		

		validPath = new Button(general, SWT.PUSH);
		validPath.setLayoutData(new GridData(GridData.END, GridData.CENTER, false, false,1,3));
		validPath.setText(Messages.CheckColumnSection_2);
		
		
		
		
		CLabel labelcom1 = getWidgetFactory().createCLabel(general, Messages.CheckColumnSection_4);
		labelcom1.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		
		selectColumn = getWidgetFactory().createText(general, ""); //$NON-NLS-1$
		selectColumn.setLayoutData(new GridData(GridData.FILL,GridData.CENTER,true, false));
		
		CLabel labelcom2 = getWidgetFactory().createCLabel(general, Messages.CheckColumnSection_5);
		labelcom2.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		
		
		selectType = getWidgetFactory().createText(general, ""); //$NON-NLS-1$
		selectType.setLayoutData(new GridData(GridData.FILL,GridData.CENTER,true, false));
		
		validPath.addSelectionListener(new SelectionAdapter(){
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				if(select.getText() != null && !select.getText().equalsIgnoreCase("")){ //$NON-NLS-1$
				((CheckColumnActivity)node.getWorkflowObject()).setTable(select.getText());
				if(select.getText().contains("{$")){ //$NON-NLS-1$
					String finalstring = new String(select.getText());
					
					List<String> varsString = new ArrayList<String>();
					for(Variable variable : ListVariable.getInstance().getVariables()){
						varsString.add(variable.getName());
					}
					for(String nomvar : varsString){
						String toto = finalstring.replace("{$"+nomvar+"}", ListVariable.getInstance().getVariable(nomvar).getValues().get(0)); //$NON-NLS-1$ //$NON-NLS-2$
						if(!toto.equalsIgnoreCase(finalstring)){
							((WorkflowModel)Activator.getDefault().getCurrentModel()).addResource(ListVariable.getInstance().getVariable(nomvar));
							
						}
						
						finalstring = toto;
					}

				}
				}
				
				if(selectColumn.getText() != null && !selectColumn.getText().equalsIgnoreCase("")){ //$NON-NLS-1$
					((CheckColumnActivity)node.getWorkflowObject()).setColumnName(selectColumn.getText());
					if(selectColumn.getText().contains("{$")){ //$NON-NLS-1$
						String finalstring = new String(selectColumn.getText());
						
						List<String> varsString = new ArrayList<String>();
						for(Variable variable : ListVariable.getInstance().getVariables()){
							varsString.add(variable.getName());
						}
						for(String nomvar : varsString){
							String toto = finalstring.replace("{$"+nomvar+"}", ListVariable.getInstance().getVariable(nomvar).getValues().get(0)); //$NON-NLS-1$ //$NON-NLS-2$
							if(!toto.equalsIgnoreCase(finalstring)){
								((WorkflowModel)Activator.getDefault().getCurrentModel()).addResource(ListVariable.getInstance().getVariable(nomvar));
								
							}
							
							finalstring = toto;
						}

					}
					}
				
				if(selectType.getText() != null && !selectType.getText().equalsIgnoreCase("")){ //$NON-NLS-1$
					((CheckColumnActivity)node.getWorkflowObject()).setTypeOfColumn(selectType.getText());
					if(selectType.getText().contains("{$")){ //$NON-NLS-1$
						String finalstring = new String(selectType.getText());
						
						List<String> varsString = new ArrayList<String>();
						for(Variable variable : ListVariable.getInstance().getVariables()){
							varsString.add(variable.getName());
						}
						for(String nomvar : varsString){
							String toto = finalstring.replace("{$"+nomvar+"}", ListVariable.getInstance().getVariable(nomvar).getValues().get(0)); //$NON-NLS-1$ //$NON-NLS-2$
							if(!toto.equalsIgnoreCase(finalstring)){
								((WorkflowModel)Activator.getDefault().getCurrentModel()).addResource(ListVariable.getInstance().getVariable(nomvar));
								
							}
							
							finalstring = toto;
						}

					}
					}
				else if(selectType.getText() == null || selectType.getText().equalsIgnoreCase("")){ //$NON-NLS-1$
					((CheckColumnActivity)node.getWorkflowObject()).setTypeOfColumn("nonremplie"); //$NON-NLS-1$
					
				}
				
			}
			
			});
		
		
	}
	@Override
	public void refresh() {
		
		if(((CheckColumnActivity)node.getWorkflowObject()).getTable() != null){
		
		select.setText(((CheckColumnActivity)node.getWorkflowObject()).getTable());
		}
		if(((CheckColumnActivity)node.getWorkflowObject()).getColumnName() != null){
			
			selectColumn.setText(((CheckColumnActivity)node.getWorkflowObject()).getColumnName());
			}
		if(((CheckColumnActivity)node.getWorkflowObject()).getTypeOfColumn() != null){
			
			selectType.setText(((CheckColumnActivity)node.getWorkflowObject()).getTypeOfColumn());
			}
		
		String[] listeVar = new String[ListVariable.getInstance().getArray((WorkflowModel) Activator.getDefault().getCurrentModel()).length-2];
		int i = 0;
		for(String string : ListVariable.getInstance().getArray((WorkflowModel) Activator.getDefault().getCurrentModel())){
			if(!string.equalsIgnoreCase("{$VANILLA_FILES}") && !string.equalsIgnoreCase("{$VANILLA_HOME}")){ //$NON-NLS-1$ //$NON-NLS-2$
				listeVar[i] = string;
				i++;
			}
			
		}
		new ContentProposalAdapter(
				select, 
	    	       new TextContentAdapter(), 
	    	       new SimpleContentProposalProvider(listeVar),
	    	       Activator.getDefault().getKeyStroke(), 
	    	       Activator.getDefault().getAutoActivationCharacters());
		
		new ContentProposalAdapter(
				selectColumn, 
	    	       new TextContentAdapter(), 
	    	       new SimpleContentProposalProvider(listeVar),
	    	       Activator.getDefault().getKeyStroke(), 
	    	       Activator.getDefault().getAutoActivationCharacters());
		
		new ContentProposalAdapter(
				selectType, 
	    	       new TextContentAdapter(), 
	    	       new SimpleContentProposalProvider(listeVar),
	    	       Activator.getDefault().getKeyStroke(), 
	    	       Activator.getDefault().getAutoActivationCharacters());
		
		List<Server> servers = ListServer.getInstance().getServers();
		List<Server> temp = new ArrayList<Server>();
		for (Server s : servers) {
			if (s.getClass() == ((IServer) node.getWorkflowObject()).getServerClass()) {
				
				temp.add(s);
			}
		}
	
		int selectTo = -1;
		String[] items = new String[temp.size()];
		int index = 0;
		for (Server s : temp) {
			items[index] = s.getName();
			if (s.getClass() == ((IServer) node.getWorkflowObject()).getServerClass() 
					&& ((IServer) node.getWorkflowObject()).getServer() != null 
					&& s.getName().equalsIgnoreCase(((IServer) node.getWorkflowObject()).getServer().getName())) {
				selectTo = index;
			}
			index++;
		}
		
		server.setItems(items);
		server.select(selectTo);
		
	}
	
	
	@Override
	public void aboutToBeShown() {
		
		

		super.aboutToBeShown();
	
	}



	public void aboutToBeHidden() {
		
		

		super.aboutToBeHidden();
	}

	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
        Assert.isTrue(selection instanceof IStructuredSelection);
        Object input = ((IStructuredSelection) selection).getFirstElement();
        Assert.isTrue(input instanceof NodePart);
        this.node = (Node)((NodePart) input).getModel();
       
	}
	SelectionAdapter adaptder = new SelectionAdapter() {


		@Override
		public void widgetSelected(SelectionEvent e) {
			Server s = null;
			if (server.getText() != null) {
				s = ListServer.getInstance().getServer(server.getText());
				if (s!=null && s instanceof DataBaseServer) {
					((CheckColumnActivity) node.getWorkflowObject()).setServer(s);
					((WorkflowModel)Activator.getDefault().getCurrentModel()).addResource(s);
				}
			}
			

			
		}
	
	};

	
	
	
}
