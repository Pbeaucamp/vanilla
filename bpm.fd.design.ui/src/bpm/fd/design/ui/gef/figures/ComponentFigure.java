package bpm.fd.design.ui.gef.figures;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.swt.graphics.Image;

import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.Messages;
import bpm.fd.design.ui.icons.Icons;
import bpm.fd.design.ui.tools.ColorManager;

public class ComponentFigure extends Figure{

	
	private ImageFigure pict, events;
	private Label text;
	private Label errorText;
	private Label errorParameterNames;
	
	public ComponentFigure(Image picture, String name) {
		GridLayout layout = new GridLayout(2, false);
//		ImageFigure p = new ImageFigure(new Image(Display.getDefault()," out.jpg"));
//		add(p);
//		setConstraint(p, new GridData(GridData.FILL, GridData.FILL, false, false));

		//Image dd = new Image(Display.getDefault(), "out.jpg");
		
//		SvgUtility svg = new SvgUtility();
//		svg.CreateImage(picture);
//		pict = new ImageFigure(dd);
		pict = new ImageFigure(picture);
		add(pict, new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));
		
		events = new ImageFigure(Activator.getDefault().getImageRegistry().get(Icons.events));
		add(events, new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));
		events.setVisible(false);

		errorText = new Label();
		errorText.setText(Messages.ComponentFigure_0);
		errorText.setForegroundColor(ColorManager.getColorRegistry().get(ColorManager.COLOR_WRONG_VALUE_FIELD));
		errorText.setVisible(false);
		
		errorParameterNames = new Label();
		errorParameterNames.setText(Messages.ComponentFigure_1);
		errorParameterNames.setForegroundColor(ColorManager.getColorRegistry().get(ColorManager.COLOR_WRONG_VALUE_FIELD));
		errorParameterNames.setVisible(false);
		
		add(errorText, new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false, 2, 1));
		add(errorParameterNames, new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false, 2, 1));
		text = new Label();
		text.setText(name);
		
		add(text, new GridData(GridData.BEGINNING, GridData.END, false, false, 2, 1));
		this.setBorder(new LineBorder(1));
		setLayoutManager(layout);
		
	}

	
	


	/* (non-Javadoc)
	 * @see org.eclipse.draw2d.Figure#getPreferredSize(int, int)
	 */
	@Override
	public Dimension getPreferredSize(int hint, int hint2) {
		
		return super.getPreferredSize(hint, hint2);
	}





	public void setText(String name){
		text.setText(name);
	}


	public void setPicture(Image fullSizePicture) {
		remove(pict);
		pict = new ImageFigure(fullSizePicture);
		add(pict, new GridData(GridData.BEGINNING, GridData.BEGINNING, false, false));
	}





	public void setName(String name) {
		text.setText(name);
		
	}





	public boolean hasErrorText() {
		return errorText.isVisible() || errorParameterNames.isVisible();
	}

	public void hideErrorText() {
		errorText.setVisible(false);
		
	}
	
	public void showErrorText() {
		errorText.setVisible(true);
		
	}
	
	public void hideParameterErrorText() {
		errorParameterNames.setVisible(false);
		
	}
	
	public void showParameterErrorText() {
		errorParameterNames.setVisible(true);
		
	}





	public void hasEvents(boolean hasEvents) {
		events.setVisible(hasEvents);
	}

	


}
