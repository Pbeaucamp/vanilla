package bpm.map.viewer.web.client.utils;

import bpm.fm.api.model.ComplexMapLevel;
import bpm.map.viewer.web.client.UserSession;
import bpm.map.viewer.web.client.images.Images;
import bpm.map.viewer.web.client.panel.viewer.MapViewer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TreeItem;

public class LevelTreeItem extends TreeItem{

	public static Images images = (Images) GWT
			.create(Images.class);
	
	private CheckBox checkbox = new CheckBox();
	private MapViewer parent;
	private ComplexMapLevel level;
	private Label name = new Label("");
	private Image customIcon;
	private Label filtered = new Label();
	private HorizontalPanel color = new HorizontalPanel();
	private HorizontalPanel treePanel = new HorizontalPanel();

	private String webappUrl;
	
	public LevelTreeItem(MapViewer parent, ComplexMapLevel level, boolean check, boolean filtered) {
		super();
		this.parent = parent;
		this.level = level;
		this.webappUrl = UserSession.getInstance().getWebappUrl();
				
		this.color.getElement().getStyle().setBackgroundColor('#'+level.getColor());
		this.color.getElement().getStyle().setWidth(30, Unit.PX);
		this.color.getElement().getStyle().setHeight(18, Unit.PX);
		this.color.getElement().getStyle().setMarginRight(50, Unit.PX);
		this.color.getElement().getStyle().setMarginLeft(-20, Unit.PX);
		
		this.checkbox.setValue(check);
		this.checkbox.getElement().getStyle().setMarginRight(15, Unit.PX);
		
		String url = level.getIconUrl();
		if (url.contains("webapps")) {
			url = url.substring(url.indexOf("webapps") + "webapps".length(), url.length());
		}
		url = webappUrl + url.replace("\\", "/");
		//this.customIcon = new Image(GWT.getHostPageBaseURL() + "icons-set/" + level.getIconUrl());
		this.customIcon = new Image(url);
		this.customIcon.getElement().getStyle().setPaddingRight(15, Unit.PX);
		this.customIcon.getElement().getStyle().setWidth(18, Unit.PX);
		this.customIcon.getElement().getStyle().setHeight(18, Unit.PX);
		this.customIcon.getElement().getStyle().setMarginRight(35, Unit.PX);
		
		this.name.setText(level.getLevel().getName());
		this.name.getElement().getStyle().setPaddingRight(30, Unit.PX);
		
		if(filtered)
			this.filtered.setText("filtered");
		this.filtered.getElement().getStyle().setColor("green");
		this.filtered.getElement().getStyle().setFontSize(12, Unit.PX);
		
		this.treePanel.add(color);
		this.treePanel.add(checkbox);
		this.treePanel.add(customIcon);
		this.treePanel.add(name);
		this.treePanel.add(this.filtered);
		

		this.getElement().getStyle().setLineHeight(18, Unit.PX);
		this.setWidget(treePanel);
		
		this.checkbox.addClickHandler(new ClickHandler() {
		      @Override
		      public void onClick(ClickEvent event) {
		        boolean checked = ((CheckBox) event.getSource()).getValue();
		        checkBoxChange(checked);
		      }
		    });
		
		
		
	}

	private void checkBoxChange(boolean checked){
		this.parent.onLevelSelection(checked, level);
	}

	
	
	public ComplexMapLevel getLevel() {
		return level;
	}

	public void setLevel(ComplexMapLevel level) {
		this.level = level;
	}

	public boolean getCheckBoxState(){
		return checkbox.getValue();
	}
	
	public void setCheckBoxState(boolean state){
		checkbox.setValue(state);
	}
}
