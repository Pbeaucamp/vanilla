package bpm.smart.web.client.dialogs;

import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.gwt.commons.shared.utils.CommonConstants;
import bpm.smart.web.client.I18N.LabelsConstants;
import bpm.smart.web.client.services.SmartAirService;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class GraphDialog extends AbstractDialogBox {
	private static  GraphDialogUiBinder uiBinder = GWT
			.create( GraphDialogUiBinder.class);

	interface  GraphDialogUiBinder extends
			UiBinder<Widget, GraphDialog> {
	}

	interface MyStyle extends CssResource {
		String panelContent();
		
		String panelContentFull();
	}
	@UiField
	HTMLPanel panel;
	@UiField
	Frame frame;
	
	@UiField
	TextArea text;
	
	@UiField
	Image image;
	
	@UiField
	SimplePanel svgField;
	
	@UiField
	MyStyle style;

	public static LabelsConstants lblCnst = (LabelsConstants) GWT.create(LabelsConstants.class);
	
	public GraphDialog(byte[] stream, String title, String type) {
		super(title, true, true);
		setWidget(uiBinder.createAndBindUi(this));
		this.text.setVisible(false);
		this.image.setVisible(false);
		this.svgField.setVisible(false);
		SmartAirService.Connect.getInstance().addSessionStream(stream, type, new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();

				ExceptionManager.getInstance().handleException(caught, lblCnst.UnableToExecuteScript());
				
			}

			@Override
			public void onSuccess(String result) {
				  String fullUrl = GWT.getHostPageBaseURL() + CommonConstants.VIEW_STREAM_SERVLET + "?" + CommonConstants.STREAM_HASHMAP_NAME + "=" + result + "&" + CommonConstants.STREAM_HASHMAP_FORMAT + "=" + CommonConstants.FORMAT_HTML;
			//	  ToolsGWT.doRedirect(fullUrl);
				  frame.setUrl(fullUrl);
			}
		});
		
		
	}
	
	public GraphDialog(String stream, String title, String type) {
		super(title, true, true);
		setWidget(uiBinder.createAndBindUi(this));
		this.frame.setVisible(false);
		if(type.equals(CommonConstants.FORMAT_SVG)){
			this.image.setVisible(false);	
			this.svgField.setVisible(true);
			this.text.setVisible(false);
//			this.image.setUrl(stream);
			HTML htm = new HTML(stream);
			htm.setHeight("100%");
			Element el = (Element)(htm.getElement().getChild(0));
			el.setAttribute("height", "100%");
			el.setAttribute("width", "100%");
//			el.setAttribute("preserveAspectRatio", "none");
			this.svgField.add(htm);
		} else {
			this.image.setVisible(false);
			this.svgField.setVisible(false);
			this.text.setVisible(true);
			this.text.setText(stream);
//			SmartAirService.Connect.getInstance().getTempText(url, new AsyncCallback<String>() {
//
//				@Override
//				public void onSuccess(String result) {
//					text.setText(result);
//				}
//
//				@Override
//				public void onFailure(Throwable caught) {
//				
//				}
//			});
			
		}
	}
	
	@Override
	public void maximize(boolean maximize) {
		if(maximize){
			panel.setStyleName(style.panelContentFull());
		} else {
			panel.setStyleName(style.panelContent());
		}
	}
	
}
