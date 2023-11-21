package bpm.gwt.aklabox.commons.client.viewers;

import java.util.ArrayList;
import java.util.List;

import bpm.document.management.core.model.DocPages;
import bpm.gwt.aklabox.commons.client.utils.ToolsGWT;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class PagesViewer extends Composite {

	private static PagesViewerUiBinder uiBinder = GWT.create(PagesViewerUiBinder.class);

	interface PagesViewerUiBinder extends UiBinder<Widget, PagesViewer> {
	}

	interface MyStyle extends CssResource {
		String hpanel();
		String vpanel();
	}

	@UiField
	HTMLPanel pagePreview, panel;

	@UiField
	MyStyle style;

	public enum Orientation {
		HORIZONTAL, VERTICAL
	}

	public PagesViewer(DocumentViewer docViewer, List<DocPages> pages, Orientation orientation) {
		initWidget(uiBinder.createAndBindUi(this));

		if (orientation == Orientation.HORIZONTAL) {
			panel.addStyleName(style.hpanel());
		}
		else {
			panel.addStyleName(style.vpanel());
		}

		int pageNumber = 1;
		if (pages != null && !pages.isEmpty()) {
			for (DocPages page : pages) {
				pagePreview.add(new PageResult(docViewer, ToolsGWT.getRightPath(page.getImagePath()), pageNumber, "office"));
				if (pageNumber == 1) {
					docViewer.loadPage(page.getImagePath(), false);
				}
				pageNumber++;
			}
		}
		else {
			docViewer.loadPage("", false);
		}
	}
	
	public List<PageResult> getPages(){
		List<PageResult> res = new ArrayList<>();
		for(Widget w : pagePreview){
			if(w instanceof PageResult){
				res.add((PageResult) w);
			}
		}
		return res;
	}

}
