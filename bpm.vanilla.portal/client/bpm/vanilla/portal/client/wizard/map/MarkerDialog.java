package bpm.vanilla.portal.client.wizard.map;

import java.util.List;

import bpm.gwt.commons.client.dialog.AbstractDialogBox;
import bpm.vanilla.portal.client.biPortal;
import bpm.vanilla.portal.client.utils.ToolsGWT;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class MarkerDialog extends AbstractDialogBox {

	private static MarkerDialogUiBinder uiBinder = GWT.create(MarkerDialogUiBinder.class);

	interface MarkerDialogUiBinder extends UiBinder<Widget, MarkerDialog> {}

	interface MyStyle extends CssResource {
		String selectedWidget();
	}

	@UiField
	MyStyle style;

	@UiField
	HTMLPanel addIconPanel;

	@UiField
	TextBox txtMinSize, txtMaxSize;

	@UiField
	FlexTable availableIcons;

	private FileUpload addIcon = new FileUpload();
	private FormPanel addIconForm;

	private String selectedMarker;
	private int minSize;
	private int maxSize;

	private boolean confirm;

	public MarkerDialog(String selectedMarker, int selectedMin, int selectedMax) {
		super("Marker", false, true);
		setWidget(uiBinder.createAndBindUi(this));

		createButton(ToolsGWT.lblCnst.Cancel(), cancelHandler);
		createButton(ToolsGWT.lblCnst.Ok(), okHandler);

		this.selectedMarker = selectedMarker;
		this.minSize = selectedMin;
		this.maxSize = selectedMax;
		
		txtMaxSize.setText(maxSize + "");
		txtMinSize.setText(minSize + "");

		addIconForm = new FormPanel();
		addIconForm.setAction(GWT.getHostPageBaseURL() + "VanillaPortail/UploadIconServlet");
		addIconForm.setEncoding(FormPanel.ENCODING_MULTIPART);
		addIconForm.setMethod(FormPanel.METHOD_POST);
		addIconForm.addSubmitCompleteHandler(submitCompleteHandler);
		addIconForm.setWidget(addIcon);
		addIcon.setName("file");
		
		addIcon.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				addIcon.getFilename();
				addIconForm.submit();
			}
		});
		
		addIconPanel.add(addIconForm);

		fillIconsTable();

	}

	private void fillIconsTable() {
		List<String> imgTab = biPortal.get().getIcons();
		availableIcons.clear();
		int i = 0;
		for(String res : imgTab) {

			if(res.contains("webapps")) {
				res = res.substring(res.indexOf("webapps") + "webapps".length(), res.length());
			}
			String url = GWT.getHostPageBaseURL() + ".." + res.replace("\\", "/");

			Image img = new Image(url);
			img.setHeight("18px");
			img.setWidth("18px");
			img.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					selectedMarker = "/vanilla_files/KpiMap_Icons/" + ((Image) event.getSource()).getUrl().split("KpiMap_Icons/")[1];
					for(int i = 0; i < availableIcons.getRowCount(); i++) {
						for(int j = 0; j < availableIcons.getCellCount(i); j++) {
							availableIcons.getWidget(i, j).removeStyleName(style.selectedWidget());
						}
					}
					((Image) event.getSource()).addStyleName(style.selectedWidget());
				}
			});
			int line = (int) (i / 15);
			availableIcons.setWidget(line, i - line * 15, img);

			if(selectedMarker != null && !selectedMarker.isEmpty() && url.contains(selectedMarker)) {
				img.addStyleName(style.selectedWidget());
			}

			i++;
		}

	}
	
	private SubmitCompleteHandler submitCompleteHandler = new SubmitCompleteHandler() {
		public void onSubmitComplete(SubmitCompleteEvent event) {
			String res = event.getResults();
			res = res.subSequence(5, res.length()-6).toString();
			
			if(res != ""){
				biPortal.get().getIcons().add(res);
			}
			fillIconsTable();
		}
	};

	private ClickHandler cancelHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			MarkerDialog.this.hide();
		}
	};

	private ClickHandler okHandler = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			confirm = true;
			MarkerDialog.this.hide();
		}
	};

	public boolean isConfirm() {
		return confirm;
	}

	public String getMarkerUrl() {
		return selectedMarker;
	}

	public int getMinSize() {
		return Integer.parseInt(txtMinSize.getText());
	}

	public int getMaxSize() {
		return Integer.parseInt(txtMaxSize.getText());
	}
}
