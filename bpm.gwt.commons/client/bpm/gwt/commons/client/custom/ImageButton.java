package bpm.gwt.commons.client.custom;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ImageButton extends FocusPanel implements HasText {

	private static ImageButtonUiBinder uiBinder = GWT.create(ImageButtonUiBinder.class);

	interface ImageButtonUiBinder extends UiBinder<Widget, ImageButton> {
	}
	
	interface MyStyle extends CssResource {
		String button();
		String image();
	}
	
	@UiField
	MyStyle style;
	
	@UiField
	Image image;
	
	@UiField
	Label label;

	public ImageButton() {
		this(null);
	}

	public ImageButton(ImageResource resource) {
		setWidget(uiBinder.createAndBindUi(this));
		image.addStyleName(style.image());
		this.addStyleName(style.button());
		if (resource != null) {
			setResource(resource);
		}
	}
 
    @Override
    public String getText() {
        return label.getText();
    }
 
    @Override
    public void setText(String text) {
        label.setText(text);
    }
    
    public void setResource(ImageResource resource) {
		image.setResource(resource);
	}
}
