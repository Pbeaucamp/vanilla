package bpm.fd.design.ui.gef.figures;

import java.util.List;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.ToolbarLayout;

import bpm.fd.design.ui.Activator;
import bpm.fd.design.ui.icons.Icons;

public class FolderFigure extends Figure{

	private Figure labels;
	private Figure body;
	private ImageFigure events;
	
	public FolderFigure(){
		setBorder(new LineBorder(1));
		setForegroundColor(ColorConstants.orange);
		
		setLayoutManager(new GridLayout(2, false));
		
		labels = new Figure();
		labels.setLayoutManager(new ToolbarLayout(false));
		add(labels, new GridData(GridData.FILL, GridData.BEGINNING, true, false));
		labels.setLayoutManager(new GridLayout());
		
		events = new ImageFigure(Activator.getDefault().getImageRegistry().get(Icons.events));
		add(events, new GridData(GridData.END, GridData.BEGINNING, false, false));
		events.setVisible(false);
		
		body = new Figure();
		add(body, new GridData(GridData.FILL_BOTH));
		
		//setMinimumSize(new Dimension(10, 30));
		
	}
	
	public void buildTitles(List<String> titles){
		labels.removeAll();
		labels.setLayoutManager(new GridLayout(titles.size(), false));
		for(String s : titles){
			Label l = new Label();
			l.setText(s);
			l.setBorder(new LineBorder(1));
			l.setForegroundColor(ColorConstants.orange);
			
			labels.add(l, new GridData(GridData.BEGINNING, GridData.CENTER, false, false));
		}
		this.layout();
	}

	public void hasEvents(boolean hasEvents) {
		events.setVisible(hasEvents);
		
	}
}
