package bpm.sqldesigner.ui.editpart;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;

import bpm.sqldesigner.api.document.DocumentSnapshot;
import bpm.sqldesigner.api.model.Column;
import bpm.sqldesigner.api.model.LinkForeignKey;
import bpm.sqldesigner.api.model.Node;
import bpm.sqldesigner.ui.figure.ColumnFigure;

public class ColumnPart extends AppAbstractEditPart {

	private ConnectionAnchor anchorLeft;
	private Figure anchorFigureRight;
	private ConnectionAnchor anchorRight;

	@Override
	protected IFigure createFigure() {
		IFigure figure = new ColumnFigure();
		figure.addPropertyChangeListener(this);
		return figure;
	}

	@Override
	protected void createEditPolicies() {
//		installEditPolicy(EditPolicy.LAYOUT_ROLE, new FlowLayoutPolicy());

	}

	@Override
	protected void refreshVisuals() {
		ColumnFigure figure = (ColumnFigure) getFigure();
		Column model = (Column) getModel();

		figure.setLabelColumn(model.getName(), model.getType().getName(), model
				.isPrimaryKey());
		
		int[] layout = model.getLayout();

		figure.setLayout(new Rectangle(layout[0], layout[1], layout[2],
				layout[3]));

		refreshSourceConnections();
		refreshTargetConnections();
	}

	@Override
	protected void refreshSourceConnections() {
		super.refreshSourceConnections();
		Iterator<?> it = getSourceConnections().iterator();
		while (it.hasNext()) {
			ConnectionPart link = (ConnectionPart) it.next();
			link.refresh();
		}
	}

	@Override
	protected void refreshTargetConnections() {
		super.refreshTargetConnections();

		Iterator<?> it = getTargetConnections().iterator();
		while (it.hasNext()) {
			ConnectionPart link = (ConnectionPart) it.next();
			link.refresh();
		}
	}

	@Override
	public List getChildren() {
		return super.getChildren();
	}

	@Override
	public List<Node> getModelChildren() {
		return new ArrayList<Node>();
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(Node.PROPERTY_LAYOUT))
			refreshVisuals();
		if (evt.getPropertyName().equals(Node.SOURCE_CONNECTIONS_PROP)) {
			refreshSourceConnections();
//			refreshVisuals();
			fireSelectionChanged();
		}
		if (evt.getPropertyName().equals(Node.TARGET_CONNECTIONS_PROP)) {
			refreshTargetConnections();
//			refreshVisuals();
			fireSelectionChanged();
		}
	}

	protected ConnectionAnchor getConnectionAnchor(int i) {

		int j = ((TablePart) getParent()).getFigure().getBounds().x;

		if (i > j) {
			if (anchorRight == null) {
				anchorRight = new ChopboxAnchor(anchorFigureRight);
			}
			return anchorRight;
		} else {
			if (anchorLeft == null) {
				anchorLeft = new ChopboxAnchor(((ColumnFigure) getFigure())
						.getAnchorLeft());
			}

			return anchorLeft;
		}

	}

	@Override
	protected List getModelSourceConnections() {
		List<LinkForeignKey> l = new ArrayList<LinkForeignKey>(((Column) getModel()).getSourceForeignKeys());
		List<LinkForeignKey> rem = new ArrayList<LinkForeignKey>();
		
		if (getParent().getParent() instanceof SnapshotPart){
			DocumentSnapshot snap = (DocumentSnapshot)getParent().getParent().getModel();
			for(LinkForeignKey fk : l){
				
				
				if (!snap.getTables().contains(fk.getTarget().getParent()) || !snap.getTables().contains(fk.getSource().getParent())){
					rem.add(fk);
				}
			}
			l.removeAll(rem);
			
			
		}
		
		
		
		return l;
	}

	@Override
	protected List getModelTargetConnections() {
		List<LinkForeignKey> list = new ArrayList<LinkForeignKey>();
		if (((Column) getModel()).isForeignKey()){
			list.add(((Column) getModel()).getTargetPrimaryKey());
		}
		
		List<LinkForeignKey> rem = new ArrayList<LinkForeignKey>();
		
		if (getParent().getParent() instanceof SnapshotPart){
			DocumentSnapshot snap = (DocumentSnapshot)getParent().getParent().getModel();
			for(LinkForeignKey fk : list){
				
				
				if (!snap.getTables().contains(fk.getSource().getParent()) || !snap.getTables().contains(fk.getTarget().getParent())){
					rem.add(fk);
				}
			}
			list.removeAll(rem);
		}
		return list;
	}

	public void setAnchorFigure(Figure anchorFigureRight) {
		this.anchorFigureRight = anchorFigureRight;
	}

}