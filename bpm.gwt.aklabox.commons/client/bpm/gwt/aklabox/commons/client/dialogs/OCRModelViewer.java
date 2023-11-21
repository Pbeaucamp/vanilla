package bpm.gwt.aklabox.commons.client.dialogs;

import java.util.List;

import bpm.aklabox.workflow.core.model.resources.FormCell;
import bpm.aklabox.workflow.core.model.resources.StandardForm;
import bpm.gwt.aklabox.commons.client.services.AklaCommonService;
import bpm.gwt.aklabox.commons.client.utils.ChildDialogComposite;
import bpm.gwt.aklabox.commons.client.utils.CropCellLayer;
import bpm.gwt.aklabox.commons.client.utils.DefaultDialog;
import bpm.gwt.aklabox.commons.client.utils.PathHelper;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class OCRModelViewer extends ChildDialogComposite {

	private static OCRModelViewerUiBinder uiBinder = GWT.create(OCRModelViewerUiBinder.class);

	interface OCRModelViewerUiBinder extends UiBinder<Widget, OCRModelViewer> {
	}

	@UiField
	HTMLPanel hoverPanel;

	@UiField
	Image imgPreview;
	
	private StandardForm form;
	private List<FormCell> cells;
	
	private int[] taille;

	public OCRModelViewer(StandardForm form) {
		initWidget(uiBinder.createAndBindUi(this));

		this.form = form;
//		imgPreview.setUrl("https://aide.kiubi.com/media/524");
		imgPreview.setUrl(PathHelper.getRightPath( form.getBaseImage()));
		
		hoverPanel.addDomHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				DefaultDialog dial = new DefaultDialog(OCRModelViewer.this.form.getName(), new OCRModelViewer(OCRModelViewer.this.form), 600, 0, 10);
				dial.show();
			}
		}, ClickEvent.getType());
	}
	
	public OCRModelViewer(StandardForm form, List<FormCell> cells) {
		initWidget(uiBinder.createAndBindUi(this));
		
		this.form = form;
		this.cells = cells;

//		imgPreview.setUrl("https://aide.kiubi.com/media/524");
		imgPreview.setUrl(PathHelper.getRightPath( form.getBaseImage()));
		
		
		if(cells != null && !cells.isEmpty()){
			AklaCommonService.Connect.getService().getImageSize(form.getBaseImage(), new AsyncCallback<int[]>() {
				
				@Override
				public void onSuccess(int[] result) {
					taille = result;
					loadCells();
				}
				
				@Override
				public void onFailure(Throwable caught) {
					caught.printStackTrace();
				}
			});
		}
		
		hoverPanel.addDomHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				DefaultDialog dial = new DefaultDialog(OCRModelViewer.this.form.getName(), new OCRModelViewer(OCRModelViewer.this.form, OCRModelViewer.this.cells), 600, 0, 10);
				dial.show();
			}
		}, ClickEvent.getType());
		
	}
	

	public void previewPage(String pageImagePath) {
		imgPreview.getElement().getStyle().setMarginTop(50, Unit.PX);
		imgPreview.setUrl(PathHelper.getRightPath(pageImagePath));
	}


	public void loadCells() {
		hoverPanel.clear();
		for(FormCell cell : cells){
			hoverPanel.add(new CropCellLayer(cell, taille));
		}
	}

	public void updateCells(List<FormCell> cells){
		this.cells = cells;
		loadCells();
	}
	
}