package bpm.gateway.ui.gef.part;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.editpolicies.DirectEditPolicy;
import org.eclipse.gef.requests.DirectEditRequest;
import org.eclipse.gef.requests.GroupRequest;
import org.eclipse.gef.tools.CellEditorLocator;
import org.eclipse.gef.tools.DirectEditManager;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import bpm.gateway.core.Comment;
import bpm.gateway.ui.ApplicationWorkbenchWindowAdvisor;
import bpm.gateway.ui.gef.commands.DeleteCommand;
import bpm.gateway.ui.gef.commands.DirectEditCommand;
import bpm.gateway.ui.gef.editpolicies.NodeEditPolicy;
import bpm.gateway.ui.gef.figure.FigureAnnote;
import bpm.gateway.ui.gef.model.Node;

public class CommentPart extends AbstractGraphicalEditPart implements PropertyChangeListener{


	
	
	@Override
	protected IFigure createFigure() {
		FigureAnnote figure = null;
		figure = new FigureAnnote();
		
		if (((Comment)getModel()).getTypeNote() == Comment.WARNING_NOTE){
			ColorRegistry reg = PlatformUI.getWorkbench().getThemeManager().getCurrentTheme().getColorRegistry();
			figure.setBackgroundColor(reg.get(ApplicationWorkbenchWindowAdvisor.ERROR_COLOR_KEY));
		}
		
		return figure;
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new NodeEditPolicy(){
			
		});
		
		installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new DirectEditPolicy(){

			@Override
			protected Command getDirectEditCommand(DirectEditRequest request) {
				String value = (String)request.getCellEditor().getValue();
				CommentPart editPart = (CommentPart)getHost();
				
				return new DirectEditCommand((Comment)editPart.getModel(), value);
			}

			@Override
			protected void showCurrentEditValue(DirectEditRequest request) {
				String value = (String)request.getCellEditor().getValue();
				((FigureAnnote)getHostFigure()).setName(value);
				//hack to prevent async layout from placing the cell editor twice.
				getHostFigure().getUpdateManager().performUpdate();
				
			}
			
		});
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new ComponentEditPolicy(){
			protected Command createDeleteCommand(GroupRequest deleteRequest){
				DeleteCommand command = new DeleteCommand();
				command.setModel(getHost().getModel());
				command.setParentModel(getHost().getParent().getModel());
				return command;
			}

			
		});
	}

	@Override
	protected void refreshVisuals() {

		Comment model = (Comment)getModel();
		FigureAnnote figure = (FigureAnnote)getFigure();
		figure.setName(model.getContent());
		figure.setLayout(new Rectangle(model.getX(), model.getY(), model.getWidth(), model.getHeight()));
		
		ColorRegistry reg = PlatformUI.getWorkbench().getThemeManager().getCurrentTheme().getColorRegistry();
		switch(((Comment)getModel()).getTypeNote()){
		case Comment.WARNING_NOTE:
			figure.setBackgroundColor(reg.get(ApplicationWorkbenchWindowAdvisor.WARN_COLOR_KEY));
			break;
		case Comment.NORMAL_NOTE:
			figure.setBackgroundColor(ColorConstants.tooltipBackground);
			break;
		}
		figure.setName(model.getContent());
		
	}


	
	
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(Node.PROPERTY_LAYOUT)) {
			refreshVisuals();
		}

		if (evt.getPropertyName().equals(Node.PROPERTY_RENAME)) {
			refreshVisuals();
		}
		
		if (evt.getPropertyName().equals(Comment.PROPERTY_TEXT)){
			refreshVisuals();
		}
		
		if (evt.getPropertyName().equals(Comment.PROPERTY_TYPE)){
			refreshVisuals();
		}
		
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#activate()
	 */
	@Override
	public void activate() {
		super.activate();
		((Comment)getModel()).addPropertyChangeListener(this);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#deactivate()
	 */
	@Override
	public void deactivate() {
		super.deactivate();
		((Comment)getModel()).removePropertyChangeListener(this);
	}

	@Override
	public void performRequest(Request req) {

		if (req.getType() == RequestConstants.REQ_DIRECT_EDIT){
			DirectEditManager mgr = new DirectEditManager(this, TextCellEditor.class, new LocalLocator((FigureAnnote)getFigure())){

				@Override
				protected void initCellEditor() {
					FigureAnnote fig = (FigureAnnote)getEditPart().getFigure();
					getCellEditor().setValue(fig.getName());
					
				}
				
			};
			mgr.show();
		}
		super.performRequest(req);
	}
@Override
public Object getModel() {
	
	return super.getModel();
}
	
	class LocalLocator implements CellEditorLocator{
		private FigureAnnote fig;
		public LocalLocator(FigureAnnote fig){
			this.fig = fig;
		}
		
		public void relocate(CellEditor celleditor) {
			Text text = (Text)celleditor.getControl();
			Rectangle rect = fig.getClientArea();
			fig.translateToAbsolute(rect);
			org.eclipse.swt.graphics.Rectangle trim = text.computeTrim(0, 0, 0, 0);
			rect.translate(trim.x, trim.y);
			rect.width += trim.width;
			rect.height += trim.height;
			text.setBounds(rect.x, rect.y, rect.width, rect.height);
			
		}
		
	}
}
