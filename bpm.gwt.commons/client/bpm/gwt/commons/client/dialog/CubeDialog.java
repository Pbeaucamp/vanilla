package bpm.gwt.commons.client.dialog;

import bpm.gwt.commons.client.ExceptionManager;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.services.CommonService;
import bpm.gwt.commons.client.utils.MessageHelper;
import bpm.smart.core.model.AirCube;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class CubeDialog extends AbstractDialogBox {
	private static CubeDialogUiBinder uiBinder = GWT.create(CubeDialogUiBinder.class);

	interface CubeDialogUiBinder extends UiBinder<Widget, CubeDialog> {
	}
	
	interface MyStyle extends CssResource {
		String frame();
	}

	@UiField
	SimplePanel panelFrame;
	
	@UiField
	Image btnSave;
	
	@UiField
	MyStyle style;
	
	private String url;
	private String xmlModel;
	private String name;
	private int idDataset;
	private int idCube;

	public CubeDialog(String url, String name, String xmlModel, int idDataset, int idCube) {
		super(name, false, true);
		setWidget(uiBinder.createAndBindUi(this));
		this.url = url;
		this.xmlModel = xmlModel;
		this.name = name;
		this.idDataset= idDataset;
		this.idCube= idCube;
		
		panelFrame.clear();
		Frame frame = new Frame(this.url);
		frame.setStyleName(style.frame());
		panelFrame.add(frame);
		
	}
	
	@UiHandler("btnSave")
	public void onSaveClick(ClickEvent event){
		AirCube airCube = new AirCube(name, idDataset, xmlModel);
		airCube.setId(idCube);
		CommonService.Connect.getInstance().saveAirCube(airCube, new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {
				MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Information(), LabelsConstants.lblCnst.SaveCubeSuccessfull());
			}
			
			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
				ExceptionManager.getInstance().handleException(caught, LabelsConstants.lblCnst.Error());
				MessageHelper.openMessageDialog(LabelsConstants.lblCnst.Error(), caught.getMessage());
			}
		});
	}
}
