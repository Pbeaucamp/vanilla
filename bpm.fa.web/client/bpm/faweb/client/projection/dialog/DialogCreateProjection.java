package bpm.faweb.client.projection.dialog;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import bpm.faweb.client.FreeAnalysisWeb;
import bpm.faweb.client.MainPanel;
import bpm.faweb.client.projection.Projection;
import bpm.faweb.client.projection.ProjectionMeasure;
import bpm.faweb.client.projection.panel.ProjectionExtrapolationFormulaPanel;
import bpm.faweb.client.projection.panel.ProjectionMeasureFormulaPanel;
import bpm.faweb.shared.infoscube.ItemDim;
import bpm.faweb.shared.infoscube.ItemMes;
import bpm.gwt.commons.client.I18N.LabelsConstants;
import bpm.gwt.commons.client.dialog.AbstractDialogBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;

/**
 * A dialog to create a new projection
 * 
 * @author Marc Lanquetin
 * 
 */
public class DialogCreateProjection extends AbstractDialogBox {

	private static DialogCreateProjectionUiBinder uiBinder = GWT.create(DialogCreateProjectionUiBinder.class);

	interface DialogCreateProjectionUiBinder extends UiBinder<Widget, DialogCreateProjection> {
	}

	@UiField
	HTMLPanel contentPanel;

	@UiField
	Label lblName, lblType, lblDate, lblStartDate, lblLevel, lblComment;

	@UiField
	TextBox txtName, txtComment;

	@UiField
	ListBox lstType, lstLevel;

	@UiField
	DateBox txtDate, txtStartDate;

	@UiField
	VerticalPanel typePanel;

	private MainPanel mainPanel;

	private HashMap<String, Widget> extrapolationFormulaMap;
	private HashMap<String, Widget> whatIfFormulaMap;

	private ListBox lstMeasure;

	public DialogCreateProjection(MainPanel mainPanel) {
		super(FreeAnalysisWeb.LBL.ProjectionCreate(), false, true);
		this.mainPanel = mainPanel;

		setWidget(uiBinder.createAndBindUi(this));

		createButtonBar(FreeAnalysisWeb.LBL.Apply(), okHandler, LabelsConstants.lblCnst.Cancel(), cancelHandler);

		lblName.setText(FreeAnalysisWeb.LBL.ProjectionName() + " : ");
		lblComment.setText(FreeAnalysisWeb.LBL.ProjectionComment() + " : ");
		lblStartDate.setText(FreeAnalysisWeb.LBL.ProjectionStartDate() + " : ");
		lblDate.setText(FreeAnalysisWeb.LBL.ProjectionEndDate() + " : ");
		lblType.setText(FreeAnalysisWeb.LBL.ProjectionType() + " : ");
		lblLevel.setText(FreeAnalysisWeb.LBL.ProjectionLevel() + " : ");

		DateTimeFormat format = DateTimeFormat.getFormat("dd/MM/yyyy");
		txtStartDate.setValue(new Date());
		txtStartDate.setFormat(new DateBox.DefaultFormat(format));

		txtDate.setValue(new Date());
		txtDate.setFormat(new DateBox.DefaultFormat(format));

		Projection.init();

		fillTypePart(true);
		fillTypeList(lstType);
		fillLevelList();
	}

	private void fillLevelList() {
		for (ItemDim dim : mainPanel.getInfosReport().getDims()) {
			if (dim.isDate()) {
				for (int i = 0; i < dim.getChilds().get(0).getChilds().size() - 1; i++) {
					lstLevel.addItem(dim.getChilds().get(0).getChilds().get(i).getUname(), dim.getChilds().get(0).getChilds().get(i).getUname());
				}
				break;
			}
		}

	}

	public DialogCreateProjection(MainPanel mainPanel, Projection actualProjection) {
		this(mainPanel);
		setTitle(FreeAnalysisWeb.LBL.ProjectionEdit());
		initWithProjectionInformations(actualProjection);
	}

	private void initWithProjectionInformations(Projection actualProjection) {
		txtName.setText(actualProjection.getName());
		if (actualProjection.getType().equals(Projection.TYPE_WHATIF)) {
			lstType.setItemSelected(1, true);
			DomEvent.fireNativeEvent(Document.get().createChangeEvent(), lstType);
			for (int i = 0; i < typePanel.getWidgetCount(); i++) {
				if (typePanel.getWidget(i) instanceof ProjectionMeasureFormulaPanel) {
					ProjectionMeasureFormulaPanel pan = (ProjectionMeasureFormulaPanel) typePanel.getWidget(i);
					pan.initProjectionMeasureInformations(actualProjection);
				}
			}
		}
		else {
			lstType.setItemSelected(0, true);
			DomEvent.fireNativeEvent(Document.get().createChangeEvent(), lstType);
			for (int i = 0; i < lstLevel.getItemCount(); i++) {
				String lvl = lstLevel.getValue(i);
				if (lvl.equals(actualProjection.getProjectionLevel())) {
					lstLevel.setSelectedIndex(i);
					break;
				}
			}
			txtDate.setValue(actualProjection.getEndDate());
			txtStartDate.setValue(actualProjection.getStartDate());

			for (int i = 0; i < typePanel.getWidgetCount(); i++) {
				if (typePanel.getWidget(i) instanceof ProjectionExtrapolationFormulaPanel) {
					ProjectionExtrapolationFormulaPanel pan = (ProjectionExtrapolationFormulaPanel) typePanel.getWidget(i);
					pan.initProjectionMeasureInformations(actualProjection);
				}
			}
		}
	}

	private void fillTypePart(boolean isExtrapolation) {

		typePanel.getElement().getStyle().setMarginLeft(10, Unit.PX);

		List<ItemMes> measures = mainPanel.getInfosReport().getMeasures();
		if (typePanel.getWidgetCount() > 1) {
			typePanel.remove(1);
		}
		else if (typePanel.getWidgetCount() == 0) {
			lstMeasure = new ListBox();
			lstMeasure.getElement().getStyle().setWidth(373, Unit.PX);
			for (ItemMes mes : measures) {
				lstMeasure.addItem(mes.getName(), mes.getUname());
			}
			typePanel.add(lstMeasure);
			lstMeasure.setSelectedIndex(0);
			lstMeasure.addChangeHandler(new ChangeHandler() {
				@Override
				public void onChange(ChangeEvent event) {
					if (typePanel.getWidgetCount() > 1) {
						typePanel.remove(1);
					}
					if (lstType.getValue(lstType.getSelectedIndex()).equals(Projection.TYPE_EXTRAPOLATION)) {
						typePanel.add(extrapolationFormulaMap.get(lstMeasure.getValue(lstMeasure.getSelectedIndex())));
					}
					else {
						typePanel.add(whatIfFormulaMap.get(lstMeasure.getValue(lstMeasure.getSelectedIndex())));
					}
				}
			});
		}

		if (isExtrapolation) {
			if (extrapolationFormulaMap == null) {
				extrapolationFormulaMap = new HashMap<String, Widget>();
				for (ItemMes mes : measures) {
					extrapolationFormulaMap.put(mes.getUname(), new ProjectionExtrapolationFormulaPanel(mainPanel, mes));
					if (typePanel.getWidgetCount() == 1) {
						typePanel.add(extrapolationFormulaMap.get(mes.getUname()));
					}
				}
			}
		}

		else {
			if (whatIfFormulaMap == null) {
				whatIfFormulaMap = new HashMap<String, Widget>();
				for (ItemMes mes : measures) {
					whatIfFormulaMap.put(mes.getUname(), new ProjectionMeasureFormulaPanel(mainPanel, mes));
					if (typePanel.getWidgetCount() == 1) {
						typePanel.add(whatIfFormulaMap.get(mes.getUname()));
					}
				}
			}
		}

		if (typePanel.getWidgetCount() < 2) {
			String uname = lstMeasure.getValue(0);
			if (isExtrapolation) {
				typePanel.add(extrapolationFormulaMap.get(uname));
			}
			else {
				typePanel.add(whatIfFormulaMap.get(uname));
			}
		}
	}

	private void fillTypeList(final ListBox lstType) {
		for (String type : Projection.TYPES) {
			lstType.addItem(type, type);
		}

		lstType.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				String type = lstType.getValue(lstType.getSelectedIndex());
				if (type.equals(Projection.TYPE_WHATIF)) {
					txtDate.setEnabled(false);
					txtStartDate.setEnabled(false);
					lstLevel.setEnabled(false);
					fillTypePart(false);
				}
				else {
					txtDate.setEnabled(true);
					txtStartDate.setEnabled(true);
					lstLevel.setEnabled(true);
					fillTypePart(true);
				}
			}
		});
	}

	private ClickHandler okHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			Projection proj = new Projection();

			if (lstType.getValue(lstType.getSelectedIndex()).equals(Projection.TYPE_WHATIF)) {
				List<ProjectionMeasure> formulas = new ArrayList<ProjectionMeasure>();
				for (String uname : whatIfFormulaMap.keySet()) {
					ProjectionMeasureFormulaPanel pan = (ProjectionMeasureFormulaPanel) whatIfFormulaMap.get(uname);
					ProjectionMeasure mes = pan.getProjectionMeasure();
					if (mes != null) {
						formulas.add(mes);
					}
				}
				proj.setMeasureFormulas(formulas);
			}

			else {
				proj.setStartDate(txtStartDate.getValue());
				proj.setEndDate(txtDate.getValue());
				proj.setProjectionLevel(lstLevel.getValue(lstLevel.getSelectedIndex()));
				List<ProjectionMeasure> formulas = new ArrayList<ProjectionMeasure>();
				for (String uname : extrapolationFormulaMap.keySet()) {
					ProjectionExtrapolationFormulaPanel pan = (ProjectionExtrapolationFormulaPanel) extrapolationFormulaMap.get(uname);
					ProjectionMeasure mes = pan.getProjectionMeasure();
					if (mes != null) {
						formulas.add(mes);
					}
				}
				proj.setMeasureFormulas(formulas);
			}

			proj.setName(txtName.getText());
			proj.setType(lstType.getValue(lstType.getSelectedIndex()));
			proj.setComment(txtComment.getText());

			mainPanel.loadProjectionGrid(proj);

			DialogCreateProjection.this.hide();
		}
	};

	private ClickHandler cancelHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			DialogCreateProjection.this.hide();
		}
	};
}
