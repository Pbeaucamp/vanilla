package bpm.gwt.aklabox.commons.client.customs;

import java.util.HashMap;

import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * This CustomDialog represent a Dialog which can have multiple Button
 * 
 * If you want the basic Ok and Cancel button you just need to call 
 * {@link #createButtonBar(int, String, ClickHandler, int, String, ClickHandler)}
 * 
 * If you want custom Button you can call as many time as you want
 * {@link #createButton(int, String, ClickHandler)}
 * 
 * @author SVI
 *
 */
public class CustomDialog extends DialogBox {
	
	private SimplePanel contentPanel;
	private HTMLPanel panelButton;
	
	private HashMap<Integer, Button> buttons = new HashMap<Integer, Button>();
	
	public CustomDialog(String title) {
		this.setText(title);
		
		contentPanel = new SimplePanel();
		
		panelButton = new HTMLPanel("");
		panelButton.getElement().getStyle().setPosition(Position.ABSOLUTE);
		panelButton.getElement().getStyle().setRight(5, Unit.PX);
		panelButton.getElement().getStyle().setMarginTop(10, Unit.PX);
		panelButton.getElement().getStyle().setMarginBottom(5, Unit.PX);
		
		AbsolutePanel panel = new AbsolutePanel();
		panel.setHeight("45px");
		panel.setWidth("100%");
		panel.add(panelButton);
		
		HTMLPanel mainPanel = new HTMLPanel("");
		mainPanel.add(contentPanel);
		mainPanel.add(panel);
		
		this.setWidget(mainPanel);
		
		this.addStyleName("zIndex");
	}
	
	public void createContent(Widget widget){
		contentPanel.setWidget(widget);
	}
	
	public void createButtonBar(int okId, String okLabel, ClickHandler okHandler, int cancelId, 
			String cancelLabel, ClickHandler cancelHandler){
		createButton(okId, okLabel, okHandler);
		createButton(cancelId, cancelLabel, cancelHandler);
	}
	
	public void createButton(int id, String label, ClickHandler clickHandler){
		Button btn = new Button(label);
		btn.addClickHandler(clickHandler);
		btn.getElement().getStyle().setMarginLeft(5, Unit.PX);
		
		if(buttons.get(id) == null){
			buttons.put(id, btn);
			panelButton.add(btn);
		}
		else {
			Window.alert("You are trying to put a button with an already used ID = " + id);
		}
	}
	
	public Button getButton(int id){
		return buttons.get(id);
	}
	
	public void setTheme(Widget header, Widget dialog, int color){
		switch(color){
		case 0 : dialog.getElement().setAttribute("style", "border: 2px solid #555555;");
		header.getElement().setAttribute("style", "background: #555555;");break;
		case 1 : dialog.getElement().setAttribute("style", "border: 2px solid #8CC474;");
		header.getElement().setAttribute("style", "background: #8CC474;");break;
		case 2 : dialog.getElement().setAttribute("style", "border: 2px solid #e46f61;");
		header.getElement().setAttribute("style", "background: #e46f61;");break;
		case 3 : dialog.getElement().setAttribute("style", "border: 2px solid #4dbfd9;");
		header.getElement().setAttribute("style", "background: #4dbfd9;");break;
		case 4 : dialog.getElement().setAttribute("style", "border: 2px solid #FBCB43;");
		header.getElement().setAttribute("style", "background: #FBCB43;");break;
		default : dialog.getElement().setAttribute("style", "border: 2px solid #555555;");
		header.getElement().setAttribute("style", "background: #555555;");
		}
	}
	
}
