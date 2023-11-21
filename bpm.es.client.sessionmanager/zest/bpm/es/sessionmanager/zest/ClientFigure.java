package bpm.es.sessionmanager.zest;

import org.eclipse.draw2d.AbstractBorder;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.RoundedRectangle;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.text.BlockFlowLayout;
import org.eclipse.draw2d.text.FlowPage;
import org.eclipse.draw2d.text.InlineFlow;
import org.eclipse.draw2d.text.TextFlow;
import org.eclipse.draw2d.widgets.MultiLineLabel;
import org.eclipse.swt.graphics.LineAttributes;

import bpm.es.sessionmanager.Activator;
import bpm.es.sessionmanager.Messages;
import bpm.es.sessionmanager.api.UserWrapper;
import bpm.es.sessionmanager.icons.IconsName;

public class ClientFigure extends RoundedRectangle { 

	private Label username;
	private TextFlow connected;
	
	private UserWrapper user;
	
	public ClientFigure(UserWrapper user){
		this.user = user;
		
	    ToolbarLayout layout = new ToolbarLayout();
	    layout.setMinorAlignment(ToolbarLayout.ALIGN_TOPLEFT);
	    setLayoutManager(layout);	
	    setOpaque(true);

		this.setBackgroundColor(ColorConstants.tooltipBackground);
		this.setLineWidth(1);
		this.setLineStyle(Graphics.LINE_SOLID);
		this.setLayoutManager(new ToolbarLayout());
		//this.add(holder);
		
		//title
		username = new Label(user.getUser().getName());
		username.setLabelAlignment(PositionConstants.CENTER);
		username.setIconAlignment(PositionConstants.LEFT);
		if (user.isConnected()) {
			username.setIcon(Activator.getDefault().getImageRegistry().get(IconsName.ONLINE));
			this.add(username);
			
			FlowPage fp = new FlowPage();
//			BlockFlowLayout lay = new BlockFlowLayout(fp);
//			fp.setLayoutManager(lay);
			
			connected = new TextFlow(Messages.ClientFigure_0 + user.getConnectedSince().toString());
			//connected = new Label("Last action on " + user.getConnectedSince().toString());
			//connected.setLabelAlignment(PositionConstants.CENTER);
			fp.add(connected);
			this.add(fp);
		}
		else {
			username.setIcon(Activator.getDefault().getImageRegistry().get(IconsName.OFFLINE));
			this.add(username);
		}

	}
	
	public UserWrapper getUser() {
		return user;
	}
	
	@Override
	public Dimension getMinimumSize(int wHint, int hHint) {
		return super.getMinimumSize(wHint, hHint);
	}
}
