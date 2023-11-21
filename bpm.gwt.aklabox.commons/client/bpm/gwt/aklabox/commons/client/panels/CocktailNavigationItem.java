package bpm.gwt.aklabox.commons.client.panels;

import bpm.aklabox.workflow.core.model.resources.AkLadExportObject;
import bpm.document.management.core.model.AkladematAdminEntity;
import bpm.document.management.core.model.Chorus;
import bpm.document.management.core.model.Cocktail;
import bpm.gwt.aklabox.commons.client.images.CommonImages;
import bpm.gwt.aklabox.commons.client.utils.ThemeCSS;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class CocktailNavigationItem extends Composite implements HasClickHandlers, INavigationItem {
	
	private static final int MARGIN_LEFT = 10;
	private static final int IMG_PADDING = 5;
	private static final int IMG_WIDTH = 40;

	private static CocktailNavigationItemUiBinder uiBinder = GWT.create(CocktailNavigationItemUiBinder.class);

	interface CocktailNavigationItemUiBinder extends UiBinder<Widget, CocktailNavigationItem> {
	}
	
	interface MyStyle extends CssResource {
		String selected();
		String blue();
		String green();
	}
	
	@UiField
	MyStyle style;
	
	@UiField
	FocusPanel focus;
	
	@UiField
	HTMLPanel item, closePanel;
	
	@UiField
	Label lbl;
	
	@UiField
	Image img, imgClose;
	
	@UiField
	SimplePanel background;
	
	public enum Type{
		AKLAD, AKLADEMAT
	}
	
	private NavigationMenuPanel navParent;
	private CocktailValidationPanel vpanel;
	private AkLadExportObject aeo;
	private AkladematAdminEntity<Chorus> entity;
	private Type type;
	private boolean collapse;
	private double progress = 0;
	
	private CocktailNavigationItem(NavigationMenuPanel navParent, int index, CocktailValidationPanel vpanel) {
		initWidget(uiBinder.createAndBindUi(this));
		this.navParent = navParent;
		//this.aeo = aeo;
		this.vpanel = vpanel;
		
		
		this.img.setResource(CommonImages.INSTANCE.file_bill_100());
		
		this.lbl.getElement().getStyle().setMarginLeft(MARGIN_LEFT, Unit.PX);
		this.lbl.getElement().getStyle().setWidth(NavigationMenuPanel.DEFAULT_WIDTH - (MARGIN_LEFT + (2 * IMG_PADDING) + IMG_WIDTH), Unit.PX);
		
		collapse = true;
		
		focus.addDomHandler(new MouseOverHandler() {
			
			@Override
			public void onMouseOver(MouseOverEvent event) {
				closePanel.setVisible(true);
			}
		}, MouseOverEvent.getType());
		focus.addDomHandler(new MouseOutHandler() {
			
			@Override
			public void onMouseOut(MouseOutEvent event) {
				closePanel.setVisible(false);
			}
		}, MouseOutEvent.getType());
		
		closePanel.setVisible(false);
	}
	
	public CocktailNavigationItem(NavigationMenuPanel navParent, AkLadExportObject aeo, int index, CocktailValidationPanel vpanel) {
		this(navParent, index, vpanel);
		this.aeo = aeo;
		this.type = Type.AKLAD;
		this.lbl.setText(aeo.getDoc().getName());
	}
	
	public CocktailNavigationItem(NavigationMenuPanel navParent, AkladematAdminEntity<Chorus> entity, int index, CocktailValidationPanel vpanel) {
		this(navParent, index, vpanel);
		this.entity = entity;
		this.type = Type.AKLADEMAT;
		this.lbl.setText(entity.getName());
	}
	
	public AkLadExportObject getAkLadExportObject() {
		return aeo;
	}
	
	public AkladematAdminEntity<Chorus> getEntity() {
		return entity;
	}
	
	public Type getType() {
		return type;
	}

	public void fireSelection() {
		NativeEvent event = Document.get().createClickEvent(0, 0, 0, 0, 0, false, false, false, false);
		DomEvent.fireNativeEvent(event, focus);
	}
	
	@Override
	public HandlerRegistration addClickHandler(ClickHandler handler) {
		return focus.addClickHandler(handler);
	}
	
	@UiHandler("focus")
	public void onItemClick(ClickEvent event) {
		navParent.setSelectedItem(this);
	}
	
	@Override
	public void setSelected(boolean selected) {
		if (selected) {
			item.addStyleName(ThemeCSS.LEVEL_1);
			item.addStyleName(style.selected());
		}
		else {
			item.removeStyleName(ThemeCSS.LEVEL_1);
			item.removeStyleName(style.selected());
		}
	}

	@Override
	public void onAction(boolean collapse) { 
		this.collapse = collapse;
		updateUIProgress();
		
	}
	
	public void updateProgress(double percent){
		progress = percent;
		updateUIProgress();
		vpanel.updateProgress();
	}
	
	public void updateUIProgress(){
		double ratio = ((double)((2 * IMG_PADDING) + IMG_WIDTH) / NavigationMenuPanel.DEFAULT_WIDTH);
		if(collapse){
			background.getElement().getStyle().setLeft(NavigationMenuPanel.DEFAULT_WIDTH - ((2 * IMG_PADDING) + IMG_WIDTH), Unit.PX);
			background.getElement().getStyle().setWidth(getProgress()*ratio, Unit.PCT);
		} else {
			background.getElement().getStyle().setLeft(0, Unit.PX);
			background.getElement().getStyle().setWidth(getProgress(), Unit.PCT);
		}

		if(getProgress() == 100){
			background.removeStyleName(style.blue());
			background.addStyleName(style.green());
		} else {
			background.removeStyleName(style.green());
			background.addStyleName(style.blue());
		}
	}

	public double getProgress() {
		return progress;
	}
	
	@UiHandler("imgClose")
	public void onCloselick(ClickEvent event) {
		
		aeo.setCocktailMetadata(new Cocktail());
		if(aeo.getChorusMetadata() != null){
			aeo.getChorusMetadata().setCocktail(new Cocktail());
		}
		
		this.removeFromParent();
		
		vpanel.onCloseItem(this);
	}

	public AkLadExportObject getAeo() {
		return aeo;
	}

	public void setAeo(AkLadExportObject aeo) {
		this.aeo = aeo;
	}

	public void setChorusMetadata(Chorus chorus) {
		switch (type) {
		case AKLAD:
			aeo.setChorusMetadata(chorus);
			break;
		case AKLADEMAT:
			entity.setObject(chorus);
			break;
		}
	}
}
