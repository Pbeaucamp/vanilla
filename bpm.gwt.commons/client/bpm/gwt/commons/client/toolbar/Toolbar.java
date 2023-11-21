package bpm.gwt.commons.client.toolbar;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class Toolbar extends Composite {

	private static ToolbarUiBinder uiBinder = GWT.create(ToolbarUiBinder.class);

	interface ToolbarUiBinder extends UiBinder<Widget, Toolbar> {
	}

	interface MyStyle extends CssResource {
		String absoluteStyle();
		String pointer();
		String toolbarOpacity();
		String separator();
		String hidePanelToolbar();
	}

	@UiField
	MyStyle style;

	@UiField
	AbsolutePanel toolbarPanel;
	
	private SimplePanel panelHide;
	private Image imgExpend;
	private ImageResource resourceExpend, resourceCollapse;

	private int imageSizeV, imageSizeH, marginVertical, marginHorizontal;

	private int nbBtnLeft = 0;
	private int nbBtnRight = 0;
	private int imageSizeExpend = 0;
	
	private int leftHidePanel = 0;
	
	private boolean alreadyHasExpendButton;
	private boolean isOpenToolbar = false;

	public Toolbar(int imageSizeV, int imageSizeH, int marginVertical, int marginHorizontal) {
		initWidget(uiBinder.createAndBindUi(this));
		this.imageSizeV = imageSizeV;
		this.imageSizeH = imageSizeH;
		this.marginVertical = marginVertical;
		this.marginHorizontal = marginHorizontal;

		toolbarPanel.getElement().getStyle().setHeight(imageSizeV + (2 * marginVertical), Unit.PX);
	}

	public void addButton(Button btn) {
		applyOptionAndAddToToolbar(true, btn);
	}

	public void addButton(PushButton pushBtn) {
		applyOptionAndAddToToolbar(true, pushBtn);
	}

	public Image addButton(ImageResource resource, String title, ClickHandler... clickHandlers) {
		Image imgBtn = new Image(resource);
		imgBtn.setTitle(title);
		if (clickHandlers != null) {
			for (ClickHandler clickHandler : clickHandlers) {
				imgBtn.addClickHandler(clickHandler);
			}
		}
		applyOptionAndAddToToolbar(true, imgBtn);
		return imgBtn;
	}
	
	public void addButtonWithTwoState(ImageResource stateOne, String titleOne, ImageResource stateTwo, String titleTwo, ClickHandler clickHandlerOne, ClickHandler clickHandlerTwo){
		final Image imgBtnOne = new Image(stateOne);
		imgBtnOne.setTitle(titleOne);
		imgBtnOne.addClickHandler(clickHandlerOne);
		
		final Image imgBtnTwo = new Image(stateTwo);
		imgBtnTwo.setTitle(titleTwo);
		imgBtnTwo.addClickHandler(clickHandlerTwo);
		imgBtnTwo.setVisible(false);

		imgBtnOne.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				imgBtnOne.setVisible(false);
				imgBtnTwo.setVisible(true);
			}
		});
		imgBtnTwo.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				imgBtnOne.setVisible(true);
				imgBtnTwo.setVisible(false);
			}
		});
		
		applyOptionAndAddToToolbar(true, imgBtnOne, imgBtnTwo);
	}

	public void addButtonRight(PushButton pushBtn) {
		applyOptionAndAddToToolbar(false, pushBtn);
	}

	public void addButtonRight(Button btn) {
		applyOptionAndAddToToolbar(false, btn);
	}

	public Image addButtonRight(ImageResource resource, String title, ClickHandler... clickHandlers) {
		Image imgBtn = new Image(resource);
		imgBtn.setTitle(title);
		if (clickHandlers != null) {
			for (ClickHandler clickHandler : clickHandlers) {
				imgBtn.addClickHandler(clickHandler);
			}
		}
		applyOptionAndAddToToolbar(false, imgBtn);
		return imgBtn;
	}
	
	private void applyOptionAndAddToToolbar(boolean isLeft, Widget... widgets) {
		for(Widget widget : widgets){
			widget.addStyleName(style.absoluteStyle());
			widget.addStyleName(style.pointer());
			widget.addStyleName(style.toolbarOpacity());
			widget.getElement().getStyle().setMarginTop(marginVertical, Unit.PX);
			
			if(isLeft){
				widget.getElement().getStyle().setLeft(((imageSizeH + marginHorizontal) * nbBtnLeft) + (imageSizeExpend != 0 ? imageSizeExpend + marginHorizontal : 0), Unit.PX);
			}
			else {
				widget.getElement().getStyle().setRight((imageSizeH + marginHorizontal) * nbBtnRight, Unit.PX);
			}		
			toolbarPanel.add(widget);
		}
		

		if(isLeft){
			nbBtnLeft++;
		}
		else {
			nbBtnRight++;
			refreshRightHidPanel();
		}
	}

	protected void makeSeparator(boolean fromLeft) {
		SimplePanel separator = new SimplePanel();
		separator.addStyleName(style.separator());
		separator.setHeight(imageSizeV + "px");
		separator.getElement().getStyle().setTop(marginVertical, Unit.PX);
		if (fromLeft) {
			separator.getElement().getStyle().setLeft((((imageSizeH + marginHorizontal) * nbBtnLeft) - (marginHorizontal / 2)) + (imageSizeExpend != 0 ? imageSizeExpend + marginHorizontal : 0), Unit.PX);
		}
		else {
			separator.getElement().getStyle().setRight(((imageSizeH + marginHorizontal) * nbBtnRight) - (marginHorizontal / 2), Unit.PX);
		}
		toolbarPanel.add(separator);
	}

	public void addExpendButton(ImageResource resourceExpend, ImageResource resourceCollapse, String title, int imageSizeExpend) {
		if(!alreadyHasExpendButton){
			alreadyHasExpendButton = true;
			
			this.resourceExpend = resourceExpend;
			this.resourceCollapse = resourceCollapse;
			this.imageSizeExpend = imageSizeExpend;
			this.leftHidePanel = ((imageSizeH + marginHorizontal) * nbBtnLeft) + imageSizeExpend;
			
			imgExpend = new Image(resourceExpend);
			imgExpend.setTitle(title);
			imgExpend.addStyleName(style.absoluteStyle());
			imgExpend.addStyleName(style.pointer());
			imgExpend.addStyleName(style.toolbarOpacity());
			imgExpend.getElement().getStyle().setMarginTop(marginVertical, Unit.PX);
			imgExpend.getElement().getStyle().setLeft((imageSizeH + marginHorizontal) * nbBtnLeft, Unit.PX);
			imgExpend.addClickHandler(expendHandler);
			toolbarPanel.add(imgExpend);
			
			panelHide = new SimplePanel();
			panelHide.addStyleName(style.hidePanelToolbar());
			panelHide.setHeight(imageSizeV + (2 * marginVertical) + "px");
			panelHide.getElement().getStyle().setLeft(leftHidePanel, Unit.PX);
			refreshRightHidPanel();
			toolbarPanel.add(panelHide);
		}
	}
	
	private void refreshRightHidPanel(){
		panelHide.getElement().getStyle().setRight((imageSizeH + marginHorizontal) * nbBtnRight, Unit.PX);
	}

	private ClickHandler expendHandler = new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			
			Animation expend = new Animation() {
				
				@Override
				protected void onUpdate(double progress) {
					int sizeToolbar = (((imageSizeH + marginHorizontal) * nbBtnLeft) - (marginHorizontal / 2)) + (imageSizeExpend != 0 ? imageSizeExpend + marginHorizontal : 0);
					if(!isOpenToolbar){
						int progressValue = (int) (sizeToolbar*progress + leftHidePanel);
						panelHide.getElement().getStyle().setLeft(progressValue, Unit.PX);
					}
					else {
						int progressValue = (int)(sizeToolbar*(1 - progress) + leftHidePanel);
						panelHide.getElement().getStyle().setLeft(progressValue, Unit.PX);
					}
				}
				
				@Override
				protected void onComplete() {
					isOpenToolbar = !isOpenToolbar;
					if(isOpenToolbar){
						imgExpend.setResource(resourceCollapse);
					}
					else {
						imgExpend.setResource(resourceExpend);
					}
				};
				@Override
				protected void onCancel() {
					isOpenToolbar = !isOpenToolbar;
					if(isOpenToolbar){
						imgExpend.setResource(resourceCollapse);
					}
					else {
						imgExpend.setResource(resourceExpend);
					}
				}
			};
			expend.run(1000);
		}
	};
}
