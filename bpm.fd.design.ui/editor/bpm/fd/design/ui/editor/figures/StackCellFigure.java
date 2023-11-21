package bpm.fd.design.ui.editor.figures;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.StackLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;

import bpm.fd.api.core.model.structure.StackableCell;
import bpm.fd.design.ui.editor.editparts.StackableCellPart;


public class StackCellFigure extends Figure {
	
	private class ControlerListener implements MouseListener{
		private IFigure target;
		public ControlerListener(IFigure target){
			this.target = target;
		}
		@Override
		public void mouseDoubleClicked(MouseEvent me) {}

		@Override
		public void mousePressed(MouseEvent me) {
			int oldInex = currentIndex;
			currentIndex = componentFigure.getChildren().indexOf(target);
			componentFigure.remove(target);
			componentFigure.add(target);
			((IFigure)selectionBar.getChildren().get(currentIndex)).setBackgroundColor(ColorConstants.blue);
			StackCellFigure.this.repaint();
			firePropertyChange(StackableCellPart.selectedContentChanged, oldInex, currentIndex);
		}

		@Override
		public void mouseReleased(MouseEvent me) {}
	}
	
	private int currentLayoutType = 0;
	private GridLayout layout;
	private Figure selectionBar;
	private Figure componentFigure;
	//private Figure content;
	private StackLayout stackLayout;
	
	private int currentIndex;
	
	public StackCellFigure(){
		//set the main layout
		layout = new GridLayout(2, false);
		setLayoutManager(layout);
		
		setBorder(new LineBorder(ColorConstants.red, 2));
		
		selectionBar = new Figure();
		selectionBar.setLayoutManager(new FlowLayout(false));
		selectionBar.setBorder(new LineBorder(ColorConstants.black, 1));
		selectionBar.setMinimumSize(new Dimension(20, 10));
		add(selectionBar, new GridData(GridData.BEGINNING, GridData.FILL, false, true));
		
		componentFigure = new Figure();
		add(componentFigure, new GridData(GridData.FILL, GridData.FILL, true, true));
		
		stackLayout = new StackLayout();
		componentFigure.setLayoutManager(stackLayout);
	}
	
	public void addContent(final IFigure fig){
		
		fig.setOpaque(true);
		
		Figure it = new Figure();
		it.setBorder(new LineBorder(1));
		it.setSize(20, 20);

		selectionBar.add(it);
		componentFigure.add(fig);
		if (fig instanceof ComponentFigure){
			((ComponentFigure)fig).setLayout(componentFigure.getBounds());
		}
		stackLayout.layout(fig);

		it.addMouseListener(new ControlerListener(fig));
	
		
		
	}
	public void removeFromContent(IFigure fig){
		int index = componentFigure.getChildren().indexOf(fig);
		if (index == -1){
			return;
		}
		componentFigure.remove(fig);
		selectionBar.remove((IFigure)selectionBar.getChildren().get(index));
	
		
	}
	
	public void setLayout(Rectangle rect) {
        Rectangle r = new Rectangle(rect.x, rect.y, rect.width, rect.height);
        
        
        if (currentIndex < componentFigure.getChildren().size()){
        	IFigure current = (IFigure)componentFigure.getChildren().get(currentIndex);
        	componentFigure.remove(current);
        	componentFigure.add(current);
        	if (current instanceof ComponentFigure){
        		((ComponentFigure)current).setLayout(componentFigure.getBounds());
        	}
		}
        setBounds(r);
        layout();
        
	}
	
	@Override
	public void add(IFigure figure, Object constraint, int index) {
		if (figure instanceof ComponentFigure){
			if (componentFigure.getChildren().contains(figure)){
				return ;
			}
			addContent(figure);
			//repaint();
		}
		else{
			super.add(figure, constraint, index);
		}
		
	}
	
	@Override
	public void remove(IFigure figure) {
		removeFromContent(figure);
		if (getChildren().contains(figure)){
			super.remove(figure);
		}
		
	}
	
	public void setType(int type){
		if (type == currentLayoutType){
			return;
		}
		currentLayoutType = type;
		switch(type){
		case StackableCell.BUTTONS_BOTTOM:
			remove(componentFigure);
			remove(selectionBar);
			selectionBar.setLayoutManager(new FlowLayout(true));
			setLayoutManager(new GridLayout());
			add(componentFigure, new GridData(GridData.FILL, GridData.FILL, true, true));
			add(selectionBar, new GridData(GridData.FILL, GridData.END, true, false));
			break;
		case StackableCell.BUTTONS_TOP:
			remove(componentFigure);
			remove(selectionBar);
			selectionBar.setLayoutManager(new FlowLayout(true));
			setLayoutManager(new GridLayout());
			add(selectionBar, new GridData(GridData.FILL, GridData.END, true, false));
			add(componentFigure, new GridData(GridData.FILL, GridData.FILL, true, true));
			break;
		case StackableCell.BUTTONS_LEFT:
			remove(componentFigure);
			remove(selectionBar);
			selectionBar.setLayoutManager(new FlowLayout(false));
			setLayoutManager(new GridLayout(2, false));
			add(selectionBar, new GridData(GridData.BEGINNING, GridData.FILL, false, true));
			add(componentFigure, new GridData(GridData.FILL, GridData.FILL, true, true));
			break;
		case StackableCell.BUTTONS_RIGHT:
			remove(selectionBar);
			remove(componentFigure);
			setLayoutManager(new GridLayout(2, false));
			selectionBar.setLayoutManager(new FlowLayout(false));
			add(componentFigure, new GridData(GridData.FILL, GridData.FILL, true, true));
			add(selectionBar, new GridData(GridData.END, GridData.FILL, false, true));
			break;
		}
		repaint();
	}
	
	public int getCurrentIndex(){
		return currentIndex;
	}
	
	public void decorate(int index, boolean defined){
		if (selectionBar.getChildren().size() > index){
			((Figure)selectionBar.getChildren().get(index)).setBackgroundColor(defined ? null : ComponentFigure.errorColor);
//			if (componentFigure.getChildren().size() > index && ((Figure)componentFigure.getChildren().get(index)) instanceof ComponentFigure){
//				if (!defined){
//					((ComponentFigure)((Figure)componentFigure.getChildren().get(index))).decorate(Activator.getDefault().getImageRegistry().get(Icons.error_16));
//				}
//				else{
//					((ComponentFigure)((Figure)componentFigure.getChildren().get(index))).decorate(null);
//				}
//			}
		}
		
		
		
	}
}
