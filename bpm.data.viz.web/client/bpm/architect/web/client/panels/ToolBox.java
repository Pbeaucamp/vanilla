package bpm.architect.web.client.panels;

import bpm.architect.web.client.dialogs.dataviz.BaseRuleDialog;
import bpm.architect.web.client.utils.ToolHelper;
import bpm.data.viz.core.preparation.PreparationRule.RuleType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class ToolBox extends Composite {

	private static ToolBoxUiBinder uiBinder = GWT.create(ToolBoxUiBinder.class);

	interface ToolBoxUiBinder extends UiBinder<Widget, ToolBox> {
	}

	interface MyStyle extends CssResource  {
		String imgTool();
	}

	@UiField
	MyStyle style;

	@UiField
	FocusPanel tool;

	@UiField
	Image imgTool;

	@UiField
	Label lblTool;

	private RuleType type;

	private DataVizDesignPanel parent;

	public ToolBox(RuleType type, DataVizDesignPanel parent) {
		initWidget(uiBinder.createAndBindUi(this));
		this.type = type;
		this.parent = parent;
		//		imgTool.setResource(ToolHelper.getImage(type));
		imgTool.setVisible(false);
		imgTool.addStyleName(style.imgTool());
		lblTool.setText(ToolHelper.getLabel(type));

		tool.getElement().setDraggable(Element.DRAGGABLE_TRUE);
		tool.addDoubleClickHandler(doubleClickHandler);
	}

	private DoubleClickHandler doubleClickHandler = new DoubleClickHandler() {
		@Override
		public void onDoubleClick(DoubleClickEvent event) {
			final BaseRuleDialog dial = new BaseRuleDialog(parent.getDataPreparation(), type, parent.getDataPanel());
			dial.addCloseHandler(new CloseHandler<PopupPanel>() {
				@Override
				public void onClose(CloseEvent<PopupPanel> event) {
					if(dial.isConfirm()) {
						parent.getDataPanel().refresh(parent.getDataPreparation());
						if(parent.getDataPanel().getVisualPanel().isVisible()) {
							parent.getDataPanel().getVisualPanel().refreshVisualPanel();
						}
					}
				}
			});
			dial.center();
		}
	};
}
