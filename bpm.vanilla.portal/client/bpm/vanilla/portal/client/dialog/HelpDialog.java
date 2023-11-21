package bpm.vanilla.portal.client.dialog;

import java.util.HashMap;
import java.util.Map.Entry;

import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.vanilla.portal.client.utils.ToolsGWT;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class HelpDialog extends AbstractDialogBox {

	private static HelpDialogUiBinder uiBinder = GWT.create(HelpDialogUiBinder.class);

	interface HelpDialogUiBinder extends UiBinder<Widget, HelpDialog> {}

	interface MyStyle extends CssResource {
		String imgDoc();
		String lblDoc();
		String link();
	}
	
	@UiField
	MyStyle style;
	
	@UiField
	CaptionPanel panelPlateforme, panelAdmin, panelDesigner;
	
	public HelpDialog(HashMap<String, HashMap<String, HashMap<String, String>>> docPaths) {
		super(ToolsGWT.lblCnst.Help(), false, false);	
		setWidget(uiBinder.createAndBindUi(this));
		
		panelPlateforme.setCaptionHTML(ToolsGWT.lblCnst.DocumentationPlateforme());
		panelPlateforme.setContentWidget(createPlateformePart(docPaths));
		
		panelAdmin.setCaptionHTML(ToolsGWT.lblCnst.DocumentationAdmin());
		panelAdmin.setContentWidget(createAdminPart(docPaths));
		
		panelDesigner.setCaptionHTML(ToolsGWT.lblCnst.DocumentationDesigner());
		panelDesigner.setContentWidget(createDesignerPart(docPaths));
		
		createButton(ToolsGWT.lblCnst.Close(), new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});
	}

	private HTMLPanel createPlateformePart(HashMap<String, HashMap<String, HashMap<String, String>>> docPaths){
		HTMLPanel mainPanel = new HTMLPanel("");
		for(String labelDoc : docPaths.keySet()){
			if(!labelDoc.contains("admin") 
					&& !labelDoc.contains("designer")){
				buildContent(mainPanel, docPaths, labelDoc);
			}
		}
		
		return mainPanel;
	}
	
	private void buildContent(HTMLPanel mainPanel, HashMap<String, HashMap<String, HashMap<String, String>>> docPaths, String labelDoc){
		for(Entry<String, HashMap<String, String>> name : docPaths.get(labelDoc).entrySet()){
			String lblSoft = name.getKey().replace("_", " ");

			String imgPath = "";
			StringBuffer linkHTML = new StringBuffer();

			boolean first = true;
			for(Entry<String, String> loc : name.getValue().entrySet()){
				imgPath = GWT.getHostPageBaseURL() + loc.getValue() + ".png";
				
				String locale = loc.getKey();
				String linkDoc = GWT.getHostPageBaseURL() + loc.getValue() + locale + ".pdf";

				if(first){
					linkHTML.append("<a href='" + linkDoc + "' target='_blank'>" + locale + "</a>");
					first = false;
				}
				else {
					linkHTML.append(" | <a href='" + linkDoc + "' target='_blank'>" + locale + "</a>");
				}
			}
			
			Image img = new Image(imgPath);
			img.addStyleName(style.imgDoc());
			
			Label lbl = new Label(lblSoft + " : ");
			lbl.addStyleName(style.lblDoc());

			HTML link = new HTML(linkHTML.toString());
			link.addStyleName(style.link());
			
			HTMLPanel panelSoft = new HTMLPanel("");
			panelSoft.setHeight("45px");
			panelSoft.add(img);
			panelSoft.add(lbl);
			panelSoft.add(link);
			
			mainPanel.add(panelSoft);
		}
	}

	private HTMLPanel createAdminPart(HashMap<String, HashMap<String, HashMap<String, String>>> docPaths){
		HTMLPanel mainPanel = new HTMLPanel("");
		for(String labelDoc : docPaths.keySet()){
			if(labelDoc.contains("admin")){
				buildContent(mainPanel, docPaths, labelDoc);
			}
		}
		
		return mainPanel;
	}

	private HTMLPanel createDesignerPart(HashMap<String, HashMap<String, HashMap<String, String>>> docPaths){
		HTMLPanel mainPanel = new HTMLPanel("");
		for(String labelDoc : docPaths.keySet()){
			if(labelDoc.contains("designer")){
				buildContent(mainPanel, docPaths, labelDoc);
			}
		}
		
		return mainPanel;
	}
}
