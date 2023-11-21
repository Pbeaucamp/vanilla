package bpm.es.datasource.analyzer.ui.gef.parts;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.ConnectionLayer;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ManhattanConnectionRouter;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.Viewport;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import bpm.es.datasource.analyzer.ui.gef.model.Diagram;

public class DiagramPart extends AbstractGraphicalEditPart{

	private Figure dataSourceContainer, itemsContainer;
	
	@Override
	protected IFigure createFigure() {
		Viewport v = new Viewport();
	
		v.setBorder(new MarginBorder(3));
		v.setLayoutManager(new GridLayout());
		
		dataSourceContainer = new Figure();
		dataSourceContainer.setLayoutManager(new ToolbarLayout(true));
		v.add(dataSourceContainer);
		v.setConstraint(dataSourceContainer, new GridData(GridData.CENTER, GridData.FILL, true, true));
		
		
		itemsContainer = new Figure();
		itemsContainer.setLayoutManager(new ToolbarLayout(true));
		
		v.add(itemsContainer);
		v.setConstraint(itemsContainer, new GridData(GridData.FILL, GridData.FILL, true, true));
		
		ConnectionLayer connLayer = (ConnectionLayer)getLayer(LayerConstants.CONNECTION_LAYER);
		connLayer.setConnectionRouter(new ManhattanConnectionRouter());
		
		return v;
	}

	
	
	@Override
	protected void createEditPolicies() {
		
		
	}
	
	@Override
	protected void addChildVisual(EditPart childEditPart, int index) {
		
		if (childEditPart instanceof DataSourcePart){
			dataSourceContainer.add(((GraphicalEditPart)childEditPart).getFigure());
		}
		else if (childEditPart instanceof ItemPart){
			itemsContainer.add(((GraphicalEditPart)childEditPart).getFigure());
		}
		
	}
	
	@Override
	protected void removeChildVisual(EditPart childEditPart) {
		if (childEditPart instanceof DataSourcePart){
			dataSourceContainer.remove(((GraphicalEditPart)childEditPart).getFigure());
		}
		else if (childEditPart instanceof ItemPart){
			itemsContainer.remove(((GraphicalEditPart)childEditPart).getFigure());
		}
	}

	@Override
	protected List getModelChildren() {
		List<Object> l  = new ArrayList<Object>();
		l.add(((Diagram)getModel()).getDataSource());
		l.addAll(((Diagram)getModel()).getItems());
		return l;
	}
}
