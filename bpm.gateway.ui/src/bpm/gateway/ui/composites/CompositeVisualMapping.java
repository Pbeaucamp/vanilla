package bpm.gateway.ui.composites;

import java.util.HashMap;

import org.eclipse.gef.EditDomain;
import org.eclipse.gef.editparts.FreeformGraphicalRootEditPart;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.tools.ConnectionCreationTool;
import org.eclipse.gef.tools.SelectionTool;
import org.eclipse.gef.ui.parts.GraphicalViewerImpl;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import bpm.gateway.core.DefaultStreamDescriptor;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.exception.ServerException;
import bpm.gateway.core.transformations.outputs.DataBaseOutputStream;
import bpm.gateway.core.transformations.outputs.IOutput;
import bpm.gateway.ui.Activator;
import bpm.gateway.ui.gef.mapping.MapingEditPartFactory;
import bpm.gateway.ui.gef.mapping.editparts.RelationEditPart;
import bpm.gateway.ui.gef.mapping.model.MappingModel;
import bpm.gateway.ui.gef.mapping.model.Relation;
import bpm.gateway.ui.i18n.Messages;
import bpm.gateway.ui.icons.IconsNames;

public class CompositeVisualMapping extends Composite {
	

	private GraphicalViewerImpl viewer;
	private EditDomain editDomain = new EditDomain();
	private Combo input;
	private Transformation gmodel;
	private Transformation selectedTransfo;
	private int selectedInput;
	
	public CompositeVisualMapping(Composite parent, int style) {
		super(parent, style);
		this.setLayout(new GridLayout());
		
		createToolbar();
		buildContent(parent);
	}
	
	private void createToolbar(){
		ToolBar toolbar = new ToolBar(this, SWT.NONE);
		toolbar.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
		
		final  ToolItem link = new ToolItem(toolbar, SWT.PUSH);
		link.setText(Messages.CompositeVisualMapping_0);
		link.setToolTipText(Messages.CompositeVisualMapping_1);
		
		
		final ToolItem select = new ToolItem(toolbar, SWT.PUSH);
		select.setText(Messages.CompositeVisualMapping_2);
		select.setToolTipText(Messages.CompositeVisualMapping_3);

		input = new Combo(this, SWT.NONE);
		input.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		input.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				super.widgetSelected(e);
				selectedInput = input.getSelectionIndex();
				int index = 0;
				for (Transformation t : gmodel.getInputs()){
					
					if(t.getName().equals(input.getItem(input.getSelectionIndex()))){
						selectedTransfo = t;
						break;
					}
					index++;
				}
				
				try {
					setInput((DefaultStreamDescriptor)((IOutput)gmodel).getInputs().get(index).getDescriptor(gmodel), (DefaultStreamDescriptor)((IOutput)gmodel).getDescriptor(gmodel), ((IOutput)gmodel).getMappingsFor(selectedTransfo), gmodel);
				} catch (ServerException e1) {
					
					e1.printStackTrace();
				}
			}
		});
		input.getSelection();
		final ConnectionCreationTool toolLink = new ConnectionCreationTool();
		final SelectionTool toolSelect = new SelectionTool();
		
		link.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				editDomain.setActiveTool(toolLink);
				link.setSelection(true);
				select.setSelection(false);
				
			}
		});
		
		select.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				editDomain.setActiveTool(toolSelect);
				link.setSelection(false);
				select.setSelection(true);
			}
		});
		

	}
	private void buildContent(Composite parent){		
		
		viewer = new GraphicalViewerImpl();
		Control viewerComposite = viewer.createControl(this);
		viewerComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		viewer.setEditDomain(editDomain);
		editDomain.addViewer(viewer);
//		viewer.setRootEditPart(new FreeformGraphicalRootEditPart());
		viewer.setEditPartFactory(new MapingEditPartFactory(parent));
		
		MenuManager del = new MenuManager();
		Action act = new Action(Messages.CompositeVisualMapping_4) {
			@Override
			public void run() {

				IStructuredSelection ss =(IStructuredSelection)viewer.getSelection();
				
				RelationEditPart editPart = (RelationEditPart) ss.getFirstElement();
				
				Relation relation = (Relation)editPart.getModel();
				relation.getSource().removeRelation(relation);
				relation.getTarget().removeRelation(relation);
				relation.disconnect();
				
				Transformation inputs = null;
				for(Transformation t : ((DataBaseOutputStream)relation.getSource().getTransfo()).getInputs() ){
					if(relation.getSource().getParent().getName().equals(t.getName())){
						inputs = t;
						break;
					}
				}
				
				IOutput output =(IOutput) relation.getSource().getTransfo();
				output.deleteMapping(inputs, relation.getSource().getParent().getFields().indexOf(relation.getSource()), relation.getTarget().getParent().getFields().indexOf(relation.getTarget()));

			}
		};
		act.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(IconsNames.del_16));
		del.add(act);
		viewer.setContextMenu(del);	
	}
		
	public void setInput(DefaultStreamDescriptor left, DefaultStreamDescriptor right, HashMap<String, String> map, Transformation gatewayModel){
		MappingModel model = new MappingModel(left, right, map, gatewayModel);
		
		String[] names = new String[gatewayModel.getInputs().size()];
		
		int i =0;
	
		gmodel = gatewayModel;
		for(Transformation t : gatewayModel.getInputs()){
			names[i++] = t.getName();
		}
		
		input.setItems(names);
		input.select(selectedInput);
		viewer.setContents(model);
	}

	public String getInput() {
		return input.getText();
	}	
}
