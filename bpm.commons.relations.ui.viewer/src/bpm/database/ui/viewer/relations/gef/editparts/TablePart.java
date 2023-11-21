package bpm.database.ui.viewer.relations.gef.editparts;

import java.beans.PropertyChangeEvent;
import java.util.List;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editparts.AbstractEditPart;

import bpm.database.ui.viewer.relations.gef.figures.ColumnFigure;
import bpm.database.ui.viewer.relations.gef.figures.TableFigure;
import bpm.database.ui.viewer.relations.gef.policies.AppEditLayoutPolicy;
import bpm.database.ui.viewer.relations.model.Column;
import bpm.database.ui.viewer.relations.model.JoinConnection;
import bpm.database.ui.viewer.relations.model.Node;
import bpm.database.ui.viewer.relations.model.Table;

public class TablePart extends AppAbstractEditPart {

	@Override
	protected IFigure createFigure() {
		IFigure figure = new TableFigure();
		return figure;
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new AppEditLayoutPolicy());
		
	}

	protected void refreshVisuals() {
		TableFigure figure = (TableFigure) getFigure();
	/*	
		List<ColumnFigure> childs= figure.getColumnsCompartment().getChildren();
		for (ColumnFigure column: childs){
			ColumnPart columnPart= column.getPart();
			columnPart.refreshAnchor();
		}
		*/
		/*
		List<ColumnPart> columns= (List<ColumnPart>)getChildren();
		for (ColumnPart column: columns){
			column.refreshAnchor();
			column.refreshVisuals();
		}
		*/
	//	figure.getColumnsCompartment().repaint();
		
		Table model = (Table) getModel();
		
		figure.setModel(model);
		figure.setName(model.getName());
		figure.setLayout(model.getLayout());
		

		//figure.getColumnsCompartment().removeAll();
		
		/*
		 for(Node node : model.getChildren()){
			 ColumnPart column=new ColumnPart((Column)node);
			for (JoinConnection connectionSource : node.getSourceConnections())
				column.
			 column.
			 figure.getColumnsCompartment().add(column.createFigure());
		 }
		 */
		 
	}

	@Override
	public IFigure getContentPane() {
		TableFigure figure = (TableFigure) getFigure();

		return figure.getColumnsCompartment();
	}

	public List<Node> getModelChildren() {
		return ((Table) getModel()).getChildrenArray();
	}

	
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(Node.PROPERTY_LAYOUT)){
			refreshVisuals();
			
//			for(Object n : getChildren()){
//				if (n instanceof ColumnPart){
//					((ColumnPart)n).refreshVisuals();
//				}
//			}
		}
			
	
	}

//	@Override
//	protected void addChildVisual(EditPart childEditPart, int index) {
//		super.addChildVisual(childEditPart, index);
//		Figure f = ((TableFigure) getFigure()).getRightAnchors().addAnchor(
//				index);
//		((ColumnPart) childEditPart).setAnchorFigure(f);
//	}

}