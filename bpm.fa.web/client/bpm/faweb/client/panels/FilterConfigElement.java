package bpm.faweb.client.panels;

import bpm.faweb.client.images.FaWebImage;
import bpm.faweb.shared.FilterConfigDTO;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;

public class FilterConfigElement extends HTML {

	private FilterConfigDTO dto;
	private int index;
	
	public FilterConfigElement(FilterConfigDTO dto, int index) {
		this.dto = dto;
		this.index = index;
		
		Image img = new Image(FaWebImage.INSTANCE.filterconfig());
		
		this.setHTML(img + dto.getName() + " - " + dto.getComment());
		
		this.setWidth("100%");
		this.getElement().getStyle().setCursor(Cursor.POINTER);
		this.getElement().getStyle().setOverflowX(Overflow.HIDDEN);
		if(index % 2 != 0) {
			this.getElement().getStyle().setBackgroundColor("#DDDDDD");
		}
		else {
			this.getElement().getStyle().setBackgroundColor("#FFFFFF");
		}
	}
	
	public FilterConfigDTO getFilterDto() {
		return dto;
	}

	public void removeSelectedStyle() {
		if(index % 2 != 0) {
			this.getElement().getStyle().setBackgroundColor("#DDDDDD");
		}
		else {
			this.getElement().getStyle().setBackgroundColor("#FFFFFF");
		}
	}

	public void setSelectedStyle() {
		this.getElement().getStyle().setBackgroundColor("#92C1F0");
	}
}
